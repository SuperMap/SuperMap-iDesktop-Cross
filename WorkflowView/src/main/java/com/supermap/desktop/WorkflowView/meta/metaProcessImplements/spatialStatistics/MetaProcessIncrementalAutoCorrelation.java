package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.spatialStatistics;

import com.supermap.analyst.spatialstatistics.AnalyzingPatterns;
import com.supermap.analyst.spatialstatistics.DistanceMethod;
import com.supermap.analyst.spatialstatistics.IncrementalParameter;
import com.supermap.analyst.spatialstatistics.IncrementalResult;
import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasetConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.process.types.DatasetTypes;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.OutputFrame;
import com.supermap.desktop.utilities.DatasetUtilities;

import java.text.DecimalFormat;
import java.text.MessageFormat;

/**
 * @author XiaJT
 * 增量空间自相关
 */
public class MetaProcessIncrementalAutoCorrelation extends MetaProcess {

	private final static String INPUT_SOURCE_DATASET = CoreProperties.getString("String_GroupBox_SourceData");
	private final static String OUTPUT_DATA = "IncrementalAutoCorrelationResult";

	private ParameterDatasourceConstrained datasource = new ParameterDatasourceConstrained();
	private ParameterSingleDataset dataset = new ParameterSingleDataset(DatasetType.POINT, DatasetType.LINE, DatasetType.REGION);

	private ParameterFieldComboBox parameterFieldComboBox = new ParameterFieldComboBox();
	private ParameterCheckBox parameterCheckBox = new ParameterCheckBox();
	private ParameterNumber parameterTextFieldBeginDistance = new ParameterNumber();
	private ParameterNumber parameterTextFieldIncrementalDistance = new ParameterNumber();
	private ParameterNumber parameterTextFieldIncrementalNumber = new ParameterNumber();
	private ParameterComboBox parameterDistanceMethod = new ParameterComboBox();
	// 添加展示结果的textArea--yuanR
//	private ParameterTextArea parameterResult = new ParameterTextArea();
	// 结果追加到一个属性表中
	private ParameterSaveDataset parameterSaveDataset = new ParameterSaveDataset();

	public MetaProcessIncrementalAutoCorrelation() {
		setTitle(ProcessProperties.getString("String_incrementalAutoCorrelation"));
		initParameters();
		initParameterState();
		initParameterConstraint();
	}

	private void initParameters() {
		this.parameterDistanceMethod.addItem(new ParameterDataNode(ProcessProperties.getString("String_EUCLIDEAN"), DistanceMethod.EUCLIDEAN));

		this.parameterFieldComboBox.setDescribe(ProcessProperties.getString("String_AssessmentField"));
		FieldType[] fieldType = {FieldType.INT16, FieldType.INT32, FieldType.INT64, FieldType.SINGLE, FieldType.DOUBLE};
		this.parameterFieldComboBox.setFieldType(fieldType);
		this.parameterCheckBox.setDescribe(ProcessProperties.getString("String_Standardization"));
		this.parameterTextFieldBeginDistance.setDescribe(ProcessProperties.getString("String_BeginDistance"));
		this.parameterTextFieldBeginDistance.setUnit(CoreProperties.getString("String_DistanceUnit_Meter"));
		this.parameterTextFieldIncrementalDistance.setDescribe(ProcessProperties.getString("String_IncrementalDistance"));
		this.parameterTextFieldIncrementalDistance.setUnit(CoreProperties.getString("String_DistanceUnit_Meter"));
		this.parameterTextFieldIncrementalNumber.setDescribe(ProcessProperties.getString("String_IncrementalNumber"));
		this.parameterDistanceMethod.setDescribe(ProcessProperties.getString("String_DistanceMethod"));
		// 数据源
		ParameterCombine parameterCombine = new ParameterCombine();
		parameterCombine.addParameters(this.datasource, this.dataset);
		parameterCombine.setDescribe(ControlsProperties.getString("String_GroupBox_SourceDataset"));
		// 参数设置
		ParameterCombine parameterCombineSetting = new ParameterCombine();
		parameterCombineSetting.addParameters(this.parameterFieldComboBox, this.parameterTextFieldBeginDistance, this.parameterTextFieldIncrementalDistance,
				this.parameterTextFieldIncrementalNumber, this.parameterDistanceMethod, this.parameterCheckBox);
		parameterCombineSetting.setDescribe(CoreProperties.getString("String_GroupBox_ParamSetting"));
		// 结果展示
//		ParameterCombine parameterCombineResult = new ParameterCombine();
//		parameterCombineResult.addParameters(parameterResult);
//		parameterCombineResult.setDescribe(ProcessProperties.getString("String_result"));
		ParameterCombine parameterCombineResult = new ParameterCombine();
		parameterCombineResult.addParameters(this.parameterSaveDataset);
		parameterCombineResult.setDescribe(CoreProperties.getString("String_GroupBox_ResultData"));

		this.parameters.setParameters(parameterCombine, parameterCombineSetting, parameterCombineResult);
		this.parameters.addInputParameters(INPUT_SOURCE_DATASET, DatasetTypes.SIMPLE_VECTOR, parameterCombine);
		this.parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_Result_Tabular"), DatasetTypes.TABULAR, parameterCombineResult);

		//		parameters.addParameters(parameterCombineResult);
	}

	private void initParameterState() {
		DatasetVector defaultDatasetVector = DatasetUtilities.getDefaultDatasetVector();
		if (defaultDatasetVector != null) {
			this.datasource.setSelectedItem(defaultDatasetVector.getDatasource());
			this.dataset.setSelectedItem(defaultDatasetVector);
			this.parameterFieldComboBox.setFieldName(defaultDatasetVector);
		}
		this.parameterTextFieldBeginDistance.setSelectedItem("0.0");
		this.parameterTextFieldBeginDistance.setMinValue(0);
		this.parameterTextFieldIncrementalDistance.setSelectedItem("0.0");
		this.parameterTextFieldIncrementalDistance.setMinValue(0);
		this.parameterTextFieldIncrementalNumber.setSelectedItem("10");
		this.parameterTextFieldIncrementalNumber.setMinValue(2);
		this.parameterTextFieldIncrementalNumber.setMaxBit(0);
		this.parameterTextFieldIncrementalNumber.setMaxValue(30);
		this.parameterSaveDataset.setDefaultDatasetName("result_incrementalAutoCorrelation");
	}

	private void initParameterConstraint() {
		DatasourceConstraint.getInstance().constrained(this.dataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(this.datasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(this.dataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasetConstraint equalDatasetConstraint = new EqualDatasetConstraint();
		equalDatasetConstraint.constrained(this.dataset, ParameterSingleDataset.DATASET_FIELD_NAME);
		equalDatasetConstraint.constrained(this.parameterFieldComboBox, ParameterFieldComboBox.DATASET_FIELD_NAME);

		DatasourceConstraint.getInstance().constrained(this.parameterSaveDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);

	}

	@Override
	public String getKey() {
		return MetaKeys.INCREMENTAL_AUTO_CORRELATION;
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;

		DatasetVector datasetVector;
		Object value = this.parameters.getInputs().getData(INPUT_SOURCE_DATASET).getValue();
		if (value != null && value instanceof DatasetVector) {
			datasetVector = (DatasetVector) value;
		} else {
			datasetVector = (DatasetVector) this.dataset.getSelectedItem();
		}

		IncrementalParameter incrementalParameter = new IncrementalParameter();
		incrementalParameter.setAssessmentFieldName(this.parameterFieldComboBox.getFieldName());
		incrementalParameter.setStandardization(Boolean.valueOf(this.parameterCheckBox.getSelectedItem()));
		incrementalParameter.setBeginDistance(Double.valueOf(this.parameterTextFieldBeginDistance.getSelectedItem()));
		incrementalParameter.setIncrementalNumber(Integer.valueOf(this.parameterTextFieldIncrementalNumber.getSelectedItem()));
		incrementalParameter.setIncrementalDistance(Double.valueOf(this.parameterTextFieldIncrementalDistance.getSelectedItem()));
		incrementalParameter.setDistanceMethod((DistanceMethod) ((ParameterDataNode) this.parameterDistanceMethod.getSelectedItem()).getData());
		// 当选择的字段值全相等时，会抛异常-yuanR2017.9.5
		// 当字段值较少时，会抛异常 -yuanR2017.9.5
		try {
			AnalyzingPatterns.addSteppedListener(this.steppedListener);
			IncrementalResult[] incrementalResults = AnalyzingPatterns.incrementalAutoCorrelation(datasetVector, incrementalParameter);
			isSuccessful = incrementalResults != null && incrementalResults.length > 0;
			// 当分析过程无误时，对分析结果进行输出-yuanR
			if (isSuccessful) {
				String[] fieldName = {
						ProcessProperties.getString("String_Column_IncrementalDistance"),
						ProcessProperties.getString("String_Morans"),
						ProcessProperties.getString("String_Expectation"),
						CoreProperties.getString("String_Evariance"),
						ProcessProperties.getString("String_ZScor"),
						ProcessProperties.getString("String_PValue")};

				DecimalFormat dcmFmtDistance = new DecimalFormat("0.00");
				DecimalFormat dcmFmtOthers = new DecimalFormat("0.000000");
				// 记录每条记录的z值和增量距离
				double z = 0.0;
				double distance = 0.0;
				String result = "";
				for (int i = 0; i < fieldName.length; i++) {
					result += fieldName[i] + "         ";
				}
				result += "\n";
				for (int i = 0; i < incrementalResults.length; i++) {
					// 在循环输出值的时候，筛选出最大峰值
					result += dcmFmtDistance.format(incrementalResults[i].getDistance()) + "     "
							+ dcmFmtOthers.format(incrementalResults[i].getIndex()) + "     "
							+ dcmFmtOthers.format(incrementalResults[i].getExpectation()) + "     "
							+ dcmFmtOthers.format(incrementalResults[i].getVariance()) + "     "
							+ dcmFmtOthers.format(incrementalResults[i].getZScore()) + "     "
							+ dcmFmtOthers.format(incrementalResults[i].getPValue()) + "\n";
					if (incrementalResults[i].getZScore() > z) {
						z = incrementalResults[i].getZScore();
						distance = incrementalResults[i].getDistance();
					}
				}
				result += MessageFormat.format(ProcessProperties.getString("String_IncrementalAutoCorrelation_ResultDescription"), dcmFmtOthers.format(z), dcmFmtDistance.format(distance)) + "\n";
				result += "-----------------------------------------" + "\n"
						+ ProcessProperties.getString("String_Label_DetailedUse")
						+ "http://supermap-idesktop.github.io/SuperMap-iDesktop-Cross/docs/SpatialStatisticalAnalysis/IncrementalSpatialAutocorrelation.html?SpatialStatisticalAnalysis,MeasureGeographicDistr,Clusters1,AnalyzingPatterns1";


				// 不显示时间-yuanR2017.9.6
				((OutputFrame) Application.getActiveApplication().getOutput()).setShowTime(false);
				Application.getActiveApplication().getOutput().output(result);
				((OutputFrame) Application.getActiveApplication().getOutput()).setShowTime(true);
				//  parameterResult.setSelectedItem(result);

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
					for (int i = 0; i < incrementalResults.length; i++) {
						recordset.addNew(null);
						recordset.update();
					}
					recordset.getBatch().setMaxRecordCount(2000);
					recordset.getBatch().begin();
					recordset.moveFirst();
					int n = 0;
					while (!recordset.isEOF()) {
						recordset.setFieldValue(fieldName[0], incrementalResults[n].getDistance());
						recordset.setFieldValue(fieldName[1], incrementalResults[n].getIndex());
						recordset.setFieldValue(fieldName[2], incrementalResults[n].getExpectation());
						recordset.setFieldValue(fieldName[3], incrementalResults[n].getVariance());
						recordset.setFieldValue(fieldName[4], incrementalResults[n].getZScore());
						recordset.setFieldValue(fieldName[5], incrementalResults[n].getPValue());
						n++;
						recordset.moveNext();
					}
					recordset.getBatch().update();
				}
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			AnalyzingPatterns.removeSteppedListener(this.steppedListener);
		}
		return isSuccessful;
	}
}
