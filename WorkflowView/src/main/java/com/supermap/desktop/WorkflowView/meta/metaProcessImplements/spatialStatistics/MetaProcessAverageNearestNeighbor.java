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
import com.supermap.desktop.process.parameter.definedClass.SpatialStatisticsCollection;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.process.types.CommonTypes;
import com.supermap.desktop.process.types.DatasetTypes;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.OutputFrame;
import com.supermap.desktop.utilities.DatasetUtilities;

import java.text.DecimalFormat;
import java.text.MessageFormat;

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
		this.parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_Result_SpatialStatistics"),  CommonTypes.STATISTICS, parameterCombineResult);

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
				String resultDescription = MessageFormat.format(ProcessProperties.getString("String_AnalyzingPatterns_Description"), confidenceCoefficient);

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
				result += resultDescription + "\n";
				result += ProcessProperties.getString("String_AverageNearestNeighbor_ResultDescription") + "\n";
				result += "-----------------------------------------" + "\n"
						+ ProcessProperties.getString("String_Label_DetailedUse")
						+ "http://supermap-idesktop.github.io/SuperMap-iDesktop-Cross/docs/SpatialStatisticalAnalysis/AverageNearestNeighbor.html?SpatialStatisticalAnalysis,AnalyzingPatterns1";

				// 不显示时间-yuanR2017.9.6
				((OutputFrame) Application.getActiveApplication().getOutput()).setShowTime(false);
				Application.getActiveApplication().getOutput().output(result);
				((OutputFrame) Application.getActiveApplication().getOutput()).setShowTime(true);

				SpatialStatisticsCollection spatialStatisticsCollection = new SpatialStatisticsCollection(
						analyzingPatternsResult.getIndex(),
						analyzingPatternsResult.getExpectation(),
						analyzingPatternsResult.getVariance(),
						analyzingPatternsResult.getZScore(),
						analyzingPatternsResult.getPValue());
				this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(spatialStatisticsCollection);

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
