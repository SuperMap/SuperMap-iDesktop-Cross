package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.typeConversion;

import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.geometry.Abstract.IGeometry;
import com.supermap.desktop.geometry.Implements.DGeometryFactory;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.ipls.ParameterCombine;
import com.supermap.desktop.process.parameter.ipls.ParameterDatasourceConstrained;
import com.supermap.desktop.process.parameter.ipls.ParameterSaveDataset;
import com.supermap.desktop.process.parameter.ipls.ParameterSingleDataset;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;
import com.supermap.desktop.utilities.RecordsetUtilities;

import java.util.Map;

/**
 * 当只有源数据及结果数据，且输入输出数据集类型都只有一种时，可用此模板
 * by Chens
 */
public abstract class MetaProcessPointLineRegion extends MetaProcessTypeConversion {
	protected static String OUTPUT_DATA = "OutputData";

	private DatasetType inputType;
	private DatasetType outputType;

	public MetaProcessPointLineRegion(DatasetType inputType, DatasetType outputType) {
		this.inputType = inputType;
		this.outputType = outputType;

		initHook();
		initParameters();
		initParameterConstraint();
	}

	protected abstract void initHook();

	protected abstract String getOutputName();

	protected abstract String getOutputResultName();

	private void initParameters() {
		inputDatasource = new ParameterDatasourceConstrained();
		inputDataset = new ParameterSingleDataset(inputType);
		outputData = new ParameterSaveDataset();
		outputData.setDefaultDatasetName(getOutputName());

		Dataset dataset = DatasetUtilities.getDefaultDataset(inputType);
		if (dataset != null) {
			inputDatasource.setSelectedItem(dataset.getDatasource());
			inputDataset.setSelectedItem(dataset);
		}

		ParameterCombine inputCombine = new ParameterCombine();
		inputCombine.setDescribe(CoreProperties.getString("String_GroupBox_SourceData"));
		inputCombine.addParameters(inputDatasource, inputDataset);
		ParameterCombine outputCombine = new ParameterCombine();
		outputCombine.setDescribe(CoreProperties.getString("String_GroupBox_ResultData"));
		outputCombine.addParameters(outputData);

		parameters.setParameters(inputCombine, outputCombine);
		parameters.addInputParameters(INPUT_DATA, datasetTypeToTypes(inputType), inputCombine);
		if (outputType.equals(DatasetType.REGION)) {
			parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_Result_Region_Dataset"), datasetTypeToTypes(outputType), outputCombine);
		} else if (outputType.equals(DatasetType.LINE)) {
			parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_Result_Line_Dataset"), datasetTypeToTypes(outputType), outputCombine);
		} else if (outputType.equals(DatasetType.POINT)) {
			parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_Result_Point_Dataset"), datasetTypeToTypes(outputType), outputCombine);
		}
	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(inputDatasource, ParameterDatasourceConstrained.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(inputDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		DatasourceConstraint.getInstance().constrained(outputData, ParameterSaveDataset.DATASOURCE_FIELD_NAME);
	}

	@Override
	public IParameters getParameters() {
		return parameters;
	}

	/**
	 * 点线面互转执行优化，当生成失败时，确保删除新创建的数据集-yuanR2017.9.19
	 *
	 * @return
	 */
	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		Recordset recordsetResult = null;
		Recordset recordsetInput = null;


		DatasetVector src = null;
		if (parameters.getInputs().getData(INPUT_DATA).getValue() != null) {
			src = (DatasetVector) parameters.getInputs().getData(INPUT_DATA).getValue();
		} else {
			src = (DatasetVector) inputDataset.getSelectedDataset();
		}

		DatasetVectorInfo datasetVectorInfo = new DatasetVectorInfo();
		datasetVectorInfo.setName(outputData.getResultDatasource().getDatasets().getAvailableDatasetName(outputData.getDatasetName()));
		datasetVectorInfo.setType(outputType);
		DatasetVector resultDataset = outputData.getResultDatasource().getDatasets().create(datasetVectorInfo);
		resultDataset.setPrjCoordSys(src.getPrjCoordSys());

		for (int i = 0; i < src.getFieldInfos().getCount(); i++) {
			FieldInfo fieldInfo = src.getFieldInfos().get(i);
			if (!fieldInfo.isSystemField() && !fieldInfo.getName().toLowerCase().equals("smuserid")) {
				resultDataset.getFieldInfos().add(fieldInfo);
			}
		}
		try {
			recordsetResult = resultDataset.getRecordset(false, CursorType.DYNAMIC);
			recordsetInput = src.getRecordset(false, CursorType.DYNAMIC);
			recordsetResult.addSteppedListener(steppedListener);

			recordsetResult.getBatch().setMaxRecordCount(2000);
			recordsetResult.getBatch().begin();

			while (!recordsetInput.isEOF()) {
				IGeometry geometry = null;
				try {
					geometry = DGeometryFactory.create(recordsetInput.getGeometry());
					Map<String, Object> value = mergePropertyData(resultDataset, recordsetInput.getFieldInfos(), RecordsetUtilities.getFieldValuesIgnoreCase(recordsetInput));
					isSuccessful = convert(recordsetResult, geometry, value);

				} finally {
					if (geometry != null) {
						geometry.dispose();
					}
					if (!isSuccessful) {
						break;
					}
				}
				recordsetInput.moveNext();
			}
			if (isSuccessful) {
				// 转换成功时，在进行批量更新-yuanR
				recordsetResult.getBatch().update();
				this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(resultDataset);
			} else {
				outputData.getResultDatasource().getDatasets().delete(resultDataset.getName());
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			if (recordsetInput != null) {
				recordsetInput.close();
				recordsetInput.dispose();
			}
			if (recordsetResult != null) {
				recordsetResult.removeSteppedListener(steppedListener);
				recordsetResult.close();
				recordsetResult.dispose();
			}
		}
		return isSuccessful;
	}

	protected abstract boolean convert(Recordset recordset, IGeometry geometry, Map<String, Object> value);
}
