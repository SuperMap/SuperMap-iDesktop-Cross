package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.spatialStatistics;

import com.supermap.analyst.spatialstatistics.AnalyzingPatterns;
import com.supermap.analyst.spatialstatistics.DistanceMethod;
import com.supermap.analyst.spatialstatistics.IncrementalParameter;
import com.supermap.analyst.spatialstatistics.IncrementalResult;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.data.FieldType;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasetConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.OutputFrame;
import com.supermap.desktop.utilities.DatasetUtilities;

import java.text.DecimalFormat;

/**
 * @author XiaJT
 *         增量空间自相关
 */
public class MetaProcessIncrementalAutoCorrelation extends MetaProcess {
	// TODO: 2017/4/27
	private final static String INPUT_SOURCE_DATASET = CoreProperties.getString("String_GroupBox_SourceData");
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

	public MetaProcessIncrementalAutoCorrelation() {
		setTitle(ProcessProperties.getString("String_incrementalAutoCorrelation"));
		initParameters();
		initParameterState();
		initParameterConstraint();
	}

	private void initParameters() {
		parameterDistanceMethod.addItem(new ParameterDataNode(ProcessProperties.getString("String_EUCLIDEAN"), DistanceMethod.EUCLIDEAN));

		parameterFieldComboBox.setDescribe(ProcessProperties.getString("String_AssessmentField"));
		FieldType[] fieldType = {FieldType.INT16, FieldType.INT32, FieldType.INT64, FieldType.SINGLE, FieldType.DOUBLE};
		parameterFieldComboBox.setFieldType(fieldType);
		parameterCheckBox.setDescribe(ProcessProperties.getString("String_Standardization"));
		parameterTextFieldBeginDistance.setDescribe(ProcessProperties.getString("String_BeginDistance"));
		parameterTextFieldBeginDistance.setUnit(ProcessProperties.getString("String_Label_meter"));
		parameterTextFieldIncrementalDistance.setDescribe(ProcessProperties.getString("String_IncrementalDistance"));
		parameterTextFieldIncrementalDistance.setUnit(ProcessProperties.getString("String_Label_meter"));
		parameterTextFieldIncrementalNumber.setDescribe(ProcessProperties.getString("String_IncrementalNumber"));
		parameterDistanceMethod.setDescribe(ProcessProperties.getString("String_DistanceMethod"));
		// 数据源
		ParameterCombine parameterCombine = new ParameterCombine();
		parameterCombine.addParameters(datasource, dataset);
		parameterCombine.setDescribe(ControlsProperties.getString("String_GroupBox_SourceDataset"));
		// 参数设置
		ParameterCombine parameterCombineSetting = new ParameterCombine();
		parameterCombineSetting.addParameters(parameterFieldComboBox, parameterTextFieldBeginDistance, parameterTextFieldIncrementalDistance,
				parameterTextFieldIncrementalNumber, parameterDistanceMethod, parameterCheckBox);
		parameterCombineSetting.setDescribe(CoreProperties.getString("String_GroupBox_ParamSetting"));
		// 结果展示
//		ParameterCombine parameterCombineResult = new ParameterCombine();
//		parameterCombineResult.addParameters(parameterResult);
//		parameterCombineResult.setDescribe(ProcessProperties.getString("String_result"));

		parameters.setParameters(parameterCombine, parameterCombineSetting);
		parameters.addInputParameters(INPUT_SOURCE_DATASET, DatasetTypes.VECTOR, parameterCombine);
//		parameters.addParameters(parameterCombineResult);
	}

	private void initParameterState() {
		DatasetVector defaultDatasetVector = DatasetUtilities.getDefaultDatasetVector();
		if (defaultDatasetVector != null) {
			datasource.setSelectedItem(defaultDatasetVector.getDatasource());
			dataset.setSelectedItem(defaultDatasetVector);
			parameterFieldComboBox.setFieldName(defaultDatasetVector);
		}
		parameterTextFieldBeginDistance.setSelectedItem("0.0");
		parameterTextFieldBeginDistance.setMinValue(0);
		parameterTextFieldIncrementalDistance.setSelectedItem("0.0");
		parameterTextFieldIncrementalDistance.setMinValue(0);
		parameterTextFieldIncrementalNumber.setSelectedItem("10");
		parameterTextFieldIncrementalNumber.setMinValue(2);
		parameterTextFieldIncrementalNumber.setMaxBit(0);
		parameterTextFieldIncrementalNumber.setMaxValue(30);
	}

	private void initParameterConstraint() {
		DatasourceConstraint.getInstance().constrained(dataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(datasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(dataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasetConstraint equalDatasetConstraint = new EqualDatasetConstraint();
		equalDatasetConstraint.constrained(dataset, ParameterSingleDataset.DATASET_FIELD_NAME);
		equalDatasetConstraint.constrained(parameterFieldComboBox, ParameterFieldComboBox.DATASET_FIELD_NAME);

	}

	@Override
	public String getKey() {
		return MetaKeys.INCREMENTAL_AUTO_CORRELATION;
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;

		DatasetVector datasetVector;
		Object value = parameters.getInputs().getData(INPUT_SOURCE_DATASET).getValue();
		if (value != null && value instanceof DatasetVector) {
			datasetVector = (DatasetVector) value;
		} else {
			datasetVector = (DatasetVector) dataset.getSelectedItem();
		}

		IncrementalParameter incrementalParameter = new IncrementalParameter();
		incrementalParameter.setAssessmentFieldName(parameterFieldComboBox.getFieldName());
		incrementalParameter.setStandardization(Boolean.valueOf(parameterCheckBox.getSelectedItem()));
		incrementalParameter.setBeginDistance(Double.valueOf(parameterTextFieldBeginDistance.getSelectedItem()));
		incrementalParameter.setIncrementalNumber(Integer.valueOf(parameterTextFieldIncrementalNumber.getSelectedItem()));
		incrementalParameter.setIncrementalDistance(Double.valueOf(parameterTextFieldIncrementalDistance.getSelectedItem()));
		incrementalParameter.setDistanceMethod((DistanceMethod) ((ParameterDataNode) parameterDistanceMethod.getSelectedItem()).getData());
		// 当选择的字段值全相等时，会抛异常-yuanR2017.9.5
		// 当字段值较少时，会抛异常 -yuanR2017.9.5
		try {
			AnalyzingPatterns.addSteppedListener(steppedListener);
			IncrementalResult[] incrementalResults = AnalyzingPatterns.incrementalAutoCorrelation(datasetVector, incrementalParameter);
			isSuccessful = incrementalResults != null && incrementalResults.length > 0;
			// 当分析过程无误时，对分析结果进行输出-yuanR
			if (isSuccessful) {
				DecimalFormat dcmFmtDistance = new DecimalFormat("0.00");
				DecimalFormat dcmFmtOthers = new DecimalFormat("0.000000");
				// 记录每条记录的z值和增量距离
				double z = 0.0;
				double distance = 0.0;

				String result = "";
				result += ProcessProperties.getString("String_Column_IncrementalDistance") + "    "
						+ ProcessProperties.getString("String_Column_Morans") + "       "
						+ ProcessProperties.getString("String_Column_Expectation") + "       "
						+ ProcessProperties.getString("String_Column_Variance") + "        "
						+ ProcessProperties.getString("String_Column_ZScor") + "        "
						+ ProcessProperties.getString("String_Column_PValue") + "\n";
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
				result += ProcessProperties.getString("String_Max_Peak") + dcmFmtDistance.format(distance) + "," + dcmFmtOthers.format(z) + "\n";
				// 不显示时间-yuanR2017.9.6
				((OutputFrame) Application.getActiveApplication().getOutput()).setShowTime(false);
				Application.getActiveApplication().getOutput().output(result);
				((OutputFrame) Application.getActiveApplication().getOutput()).setShowTime(true);
				//  parameterResult.setSelectedItem(result);
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			AnalyzingPatterns.removeSteppedListener(steppedListener);
		}
		return isSuccessful;
	}
}
