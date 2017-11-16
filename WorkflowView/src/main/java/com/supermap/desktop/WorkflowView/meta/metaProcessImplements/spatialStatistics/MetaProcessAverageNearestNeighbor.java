package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.spatialStatistics;

import com.supermap.analyst.spatialstatistics.AnalyzingPatterns;
import com.supermap.analyst.spatialstatistics.AnalyzingPatternsResult;
import com.supermap.analyst.spatialstatistics.DistanceMethod;
import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.process.types.DatasetTypes;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.OutputFrame;
import com.supermap.desktop.utilities.DatasetUtilities;

import java.text.DecimalFormat;

/**
 * @author XiaJT
 * 平均最近邻分析
 */
public class MetaProcessAverageNearestNeighbor extends MetaProcess {
	private final static String INPUT_SOURCE_DATASET = "SourceDataset";
	private final static String OUTPUT_DATA = "AverageNearestNeighborResult";

	private ParameterDatasourceConstrained parameterDatasourceConstrained = new ParameterDatasourceConstrained();
	private ParameterSingleDataset parameterSingleDataset = new ParameterSingleDataset(DatasetType.POINT, DatasetType.LINE, DatasetType.REGION);

	private ParameterNumber parameterTextFieldArea = new ParameterNumber();
	private ParameterComboBox parameterComboBox = new ParameterComboBox();
	// 添加展示结果的textArea--yuanR
	//去除展示textArea，分析执行过后会关闭窗口，将分析结果展示在output中-yuanR2017.9.6
//	private ParameterTextArea parameterResult = new ParameterTextArea();
	private ParameterSaveDataset parameterSaveDataset = new ParameterSaveDataset();


	public MetaProcessAverageNearestNeighbor() {
		setTitle(ProcessProperties.getString("String_AverageNearestNeighbor"));
		initParameters();
		initParameterStates();
		initParameterConstraint();
	}

	private void initParameters() {
		parameterTextFieldArea.setDescribe(ProcessProperties.getString("String_SearchArea"));
		parameterTextFieldArea.setUnit(CoreProperties.getString("String_AreaUnit_Meter"));
		parameterComboBox.setDescribe(ProcessProperties.getString("String_DistanceMethod"));
		parameterComboBox.setItems(new ParameterDataNode(ProcessProperties.getString("String_EUCLIDEAN"), DistanceMethod.EUCLIDEAN),
				new ParameterDataNode(ProcessProperties.getString("String_MANHATTAN"), DistanceMethod.MANHATTAN));
		// 源数据
		ParameterCombine parameterCombine = new ParameterCombine();
		parameterCombine.addParameters(parameterDatasourceConstrained, parameterSingleDataset);
		parameterCombine.setDescribe(ControlsProperties.getString("String_GroupBox_SourceDataset"));
		// 参数设置
		ParameterCombine parameterCombineSetting = new ParameterCombine();
		parameterCombineSetting.addParameters(parameterTextFieldArea, parameterComboBox);
		parameterCombineSetting.setDescribe(CoreProperties.getString("String_GroupBox_ParamSetting"));

		ParameterCombine parameterCombineResult = new ParameterCombine();
		parameterCombineResult.addParameters(this.parameterSaveDataset);
		parameterCombineResult.setDescribe(CoreProperties.getString("String_GroupBox_ResultData"));

		parameters.addParameters(parameterCombine, parameterCombineSetting, parameterCombineResult);
		parameters.addInputParameters(INPUT_SOURCE_DATASET, DatasetTypes.SIMPLE_VECTOR, parameterCombine);
		this.parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_Result_Tabular"), DatasetTypes.TABULAR, parameterCombineResult);

	}

	private void initParameterStates() {
		DatasetVector defaultDatasetVector = DatasetUtilities.getDefaultDatasetVector();
		if (defaultDatasetVector != null) {
			parameterDatasourceConstrained.setSelectedItem(defaultDatasetVector.getDatasource());
			parameterSingleDataset.setSelectedItem(defaultDatasetVector);
		}
		parameterTextFieldArea.setSelectedItem("0.0");
		parameterTextFieldArea.setMinValue(0);
		this.parameterSaveDataset.setDefaultDatasetName("result_averageNearestNeighbor");

	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(parameterDatasourceConstrained, ParameterDatasource.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(parameterSingleDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		DatasourceConstraint.getInstance().constrained(this.parameterSaveDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		DatasetVector datasetVector;

		Object value = parameters.getInputs().getData(INPUT_SOURCE_DATASET).getValue();
		if (value != null && value instanceof DatasetVector) {
			datasetVector = (DatasetVector) value;
		} else {
			datasetVector = (DatasetVector) parameterSingleDataset.getSelectedItem();
		}
		try {
			AnalyzingPatterns.addSteppedListener(steppedListener);
			AnalyzingPatternsResult analyzingPatternsResult = AnalyzingPatterns.averageNearestNeighbor(datasetVector, Double.valueOf((String) parameterTextFieldArea.getSelectedItem()), (DistanceMethod) ((ParameterDataNode) parameterComboBox.getSelectedItem()).getData());
			isSuccessful = analyzingPatternsResult != null;
			// 如果分析成功，进行结果数据的展示--yuanR
			if (isSuccessful) {
				// 计算置信度
				double z = analyzingPatternsResult.getZScore();
				double p = analyzingPatternsResult.getPValue();
				String confidenceCoefficient = "99%";
				if (p < 0.05 && (-2.58 > z || 2.58 < z)) {
					confidenceCoefficient = "99%";
				} else if (p < 0.05 && (-1.96 > z || 1.96 < z)) {
					confidenceCoefficient = "95%";
				} else if (p < 0.1 && (-1.65 > z || 1.65 < z)) {
					confidenceCoefficient = "90%";
				}

				//对分析结果的描述
				double morans = analyzingPatternsResult.getIndex();
				String resultDescription = "";
				if (morans < 1) {
					//数据呈正相关性
					resultDescription = ProcessProperties.getString("String_AverageNearestNeighbor_PositiveDescription");
				} else if (morans > 1) {
					//数据呈负相关性
					resultDescription = ProcessProperties.getString("String_AverageNearestNeighbor_NegativeDescription");
				} else {
					//数据呈随机性
					resultDescription = ProcessProperties.getString("String_AverageNearestNeighbor_RandomnessDescription");
				}

				DecimalFormat decimalFormat = new DecimalFormat("0.0000");
				String result = "";
				result += ProcessProperties.getString("String_Label_Nearest_Neighbor_Ratio") + " "
						+ decimalFormat.format(analyzingPatternsResult.getIndex()) + "\n";
				result += ProcessProperties.getString("String_Label_Expected_Mean_Distance") + " "
						+ decimalFormat.format(analyzingPatternsResult.getExpectation()) + "\n";
				result += ProcessProperties.getString("String_Label_Observed_Mean_Distance") + " "
						+ decimalFormat.format(analyzingPatternsResult.getVariance()) + "\n";
				result += ProcessProperties.getString("String_Label_ZScor") + " "
						+ decimalFormat.format(analyzingPatternsResult.getZScore()) + "\n";
				result += ProcessProperties.getString("String_Label_PValue") + " "
						+ decimalFormat.format(analyzingPatternsResult.getPValue()) + "\n";
				result += ProcessProperties.getString("String_Label_ConfidenceCoefficient") + " "
						+ confidenceCoefficient + "\n";
				result += resultDescription + "\n";

				// 不显示时间-yuanR2017.9.6
				((OutputFrame) Application.getActiveApplication().getOutput()).setShowTime(false);
				Application.getActiveApplication().getOutput().output(result);
				((OutputFrame) Application.getActiveApplication().getOutput()).setShowTime(true);
				//  parameterResult.setSelectedItem(result);

				// 将结果追加到属性表中
				String[] fieldName = {
						ProcessProperties.getString("String_Nearest_Neighbor_Ratio"),
						ProcessProperties.getString("String_Expected_Mean_Distance"),
						ProcessProperties.getString("String_Observed_Mean_Distance"),
						ProcessProperties.getString("String_ZScor"),
						ProcessProperties.getString("String_PValue"),
				};

				//生成一个新的属性表，将结果数据追加其中
				Datasource resultDatasource = this.parameterSaveDataset.getResultDatasource();
				String resultName = this.parameterSaveDataset.getDatasetName();
				DatasetVectorInfo info = new DatasetVectorInfo(resultName, DatasetType.TABULAR);
				info.setEncodeType(EncodeType.NONE);
				DatasetVector dataset = resultDatasource.getDatasets().create(info);
				this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(dataset);
				// 设置新建数据集投影坐标系和数据源保持一致
				if (dataset != null) {
					FieldInfos fieldInfos = dataset.getFieldInfos();
					for (int i = 0; i < fieldName.length; i++) {
						FieldInfo fieldInfo = new FieldInfo();
						fieldInfo.setName(fieldName[i]);
						fieldInfo.setCaption(fieldName[i]);
						fieldInfo.setType(FieldType.DOUBLE);
						fieldInfos.add(fieldInfo);
					}
					Recordset recordset = dataset.getRecordset(false, CursorType.DYNAMIC);
					recordset.addNew(null);
					recordset.update();
					recordset.getBatch().setMaxRecordCount(2000);
					recordset.getBatch().begin();
					recordset.moveFirst();
					while (!recordset.isEOF()) {
						recordset.setFieldValue(fieldName[0], analyzingPatternsResult.getIndex());
						recordset.setFieldValue(fieldName[1], analyzingPatternsResult.getExpectation());
						recordset.setFieldValue(fieldName[2], analyzingPatternsResult.getVariance());
						recordset.setFieldValue(fieldName[3], analyzingPatternsResult.getZScore());
						recordset.setFieldValue(fieldName[4], analyzingPatternsResult.getPValue());
						recordset.moveNext();
					}
					recordset.getBatch().update();
				}
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			AnalyzingPatterns.removeSteppedListener(steppedListener);
		}
		return isSuccessful;
	}

	@Override
	public String getKey() {
		return MetaKeys.AVERAGE_NEAREST_NEIGHBOR;
	}
}
