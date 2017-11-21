package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.typeConversion;

import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasetConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.process.types.DatasetTypes;
import com.supermap.desktop.process.types.Type;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;
import com.supermap.desktop.utilities.FieldTypeUtilities;
import com.supermap.desktop.utilities.RecordsetUtilities;

import java.util.Map;

public class MetaProcessTabularToPoint extends MetaProcessTypeConversion {

	private ParameterFieldComboBox comboBoxX;
	private ParameterFieldComboBox comboBoxY;

	public MetaProcessTabularToPoint() {
		setTitle(ProcessProperties.getString("String_Title_TabularToPoint"));
		initParameters();
		initParameterConstraint();
	}

	private void initParameters() {
		OUTPUT_DATA = "TabularToPointResult";
		inputDatasource = new ParameterDatasourceConstrained();
		inputDataset = new ParameterSingleDataset(DatasetType.POINT, DatasetType.LINE, DatasetType.REGION,
				DatasetType.TEXT, DatasetType.TABULAR, DatasetType.POINT3D, DatasetType.LINE3D, DatasetType.REGION3D, DatasetType.CAD);
		outputData = new ParameterSaveDataset();
		comboBoxX = new ParameterFieldComboBox(ProcessProperties.getString("String_Xcoordinate"));
		comboBoxY = new ParameterFieldComboBox(ControlsProperties.getString("String_MapClip_Y"));

		DatasetVector datasetVector = DatasetUtilities.getDefaultDatasetVector();
		if (datasetVector != null) {
			inputDatasource.setSelectedItem(datasetVector.getDatasource());
			inputDataset.setSelectedItem(datasetVector);
			comboBoxY.setFieldName(datasetVector);
			comboBoxX.setFieldName(datasetVector);
		}
        comboBoxX.setFieldType(FieldTypeUtilities.getNumericFieldType());
        comboBoxY.setFieldType(FieldTypeUtilities.getNumericFieldType());
        outputData.setDefaultDatasetName("result_tabularToPoint");

		ParameterCombine inputCombine = new ParameterCombine();
		inputCombine.setDescribe(CoreProperties.getString("String_GroupBox_SourceData"));
		inputCombine.addParameters(inputDatasource, inputDataset);
		ParameterCombine settingCombine = new ParameterCombine();
		settingCombine.setDescribe(CoreProperties.getString("String_GroupBox_ParamSetting"));
		settingCombine.addParameters(comboBoxX, comboBoxY);
		ParameterCombine outputCombine = new ParameterCombine();
		outputCombine.setDescribe(CoreProperties.getString("String_GroupBox_ResultData"));
		outputCombine.addParameters(outputData);

		Type datasetTypes = Type.instance("").and(DatasetTypes.POINT).and(DatasetTypes.LINE).and(DatasetTypes.REGION).and(DatasetTypes.TEXT)
				.and(DatasetTypes.TABULAR).and(DatasetTypes.POINT3D).and(DatasetTypes.LINE3D).and(DatasetTypes.REGION3D).and(DatasetTypes.CAD);
		parameters.setParameters(inputCombine, settingCombine, outputCombine);
		parameters.addInputParameters(INPUT_DATA, datasetTypes, inputCombine);
		parameters.addOutputParameters(OUTPUT_DATA, CoreProperties.getString("String_DatasetType_Point"), DatasetTypes.POINT, outputCombine);
	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(inputDatasource, ParameterDatasourceConstrained.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(inputDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasetConstraint equalDatasetConstraint = new EqualDatasetConstraint();
		equalDatasetConstraint.constrained(inputDataset, ParameterSingleDataset.DATASET_FIELD_NAME);
		equalDatasetConstraint.constrained(comboBoxX, ParameterFieldComboBox.DATASET_FIELD_NAME);
		equalDatasetConstraint.constrained(comboBoxY, ParameterFieldComboBox.DATASET_FIELD_NAME);

		DatasourceConstraint.getInstance().constrained(outputData, ParameterSaveDataset.DATASOURCE_FIELD_NAME);
	}

	@Override
	public IParameters getParameters() {
		return parameters;
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		Recordset recordsetResult = null;
		try {
			String filedX = null;
			String filedY = null;
			if (comboBoxX.getSelectedItem() != null && comboBoxY.getSelectedItem() != null) {
				filedX = comboBoxX.getFieldName();
				filedY = comboBoxY.getFieldName();
			} else {
				Application.getActiveApplication().getOutput().output("Coordinate is null");
				return false;
			}
			DatasetVector src = null;
			if (parameters.getInputs().getData(INPUT_DATA).getValue() != null) {
				src = (DatasetVector) parameters.getInputs().getData(INPUT_DATA).getValue();
			} else {
				src = (DatasetVector) inputDataset.getSelectedDataset();
			}
			DatasetVectorInfo datasetVectorInfo = new DatasetVectorInfo();
			datasetVectorInfo.setName(outputData.getResultDatasource().getDatasets().getAvailableDatasetName(outputData.getDatasetName()));
			datasetVectorInfo.setType(DatasetType.POINT);
			DatasetVector resultDataset = outputData.getResultDatasource().getDatasets().create(datasetVectorInfo);

			resultDataset.setPrjCoordSys(src.getPrjCoordSys());
			for (int i = 0; i < src.getFieldInfos().getCount(); i++) {
				FieldInfo fieldInfo = src.getFieldInfos().get(i);
				if (!fieldInfo.isSystemField() && !fieldInfo.getName().toLowerCase().equals("smuserid")) {
					resultDataset.getFieldInfos().add(fieldInfo);
				}
			}
			recordsetResult = resultDataset.getRecordset(false, CursorType.DYNAMIC);
			recordsetResult.addSteppedListener(steppedListener);
			recordsetResult.getBatch().setMaxRecordCount(2000);
			recordsetResult.getBatch().begin();

			Recordset recordsetInput = src.getRecordset(false, CursorType.DYNAMIC);
			while (!recordsetInput.isEOF()) {
				Map<String, Object> value = mergePropertyData(resultDataset, recordsetInput.getFieldInfos(), RecordsetUtilities.getFieldValuesIgnoreCase(recordsetInput));
				Point2D point2D = new Point2D(Double.valueOf(recordsetInput.getFieldValue(filedX).toString()), Double.valueOf(recordsetInput.getFieldValue(filedY).toString()));
				GeoPoint geoPoint = new GeoPoint(point2D);
				recordsetResult.addNew(geoPoint, value);
				geoPoint.dispose();
				recordsetInput.moveNext();
			}
			recordsetResult.getBatch().update();
			recordsetInput.close();
			recordsetInput.dispose();
			isSuccessful = recordsetResult != null;
			if (isSuccessful) {
				this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(resultDataset);
			} else {
				outputData.getResultDatasource().getDatasets().delete(resultDataset.getName());
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			if (recordsetResult != null) {
				recordsetResult.removeSteppedListener(steppedListener);
				recordsetResult.close();
				recordsetResult.dispose();
			}
		}

		return isSuccessful;
	}

	@Override
	public String getKey() {
		return MetaKeys.CONVERSION_TABULAR_TO_POINT;
	}
}
