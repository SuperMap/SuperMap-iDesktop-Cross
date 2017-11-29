package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.spatialStatistics;

import com.supermap.analyst.spatialstatistics.AnalyzingPatterns;
import com.supermap.analyst.spatialstatistics.AnalyzingPatternsResult;
import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.parameter.definedClass.SpatialStatisticsCollection;
import com.supermap.desktop.process.parameter.ipls.ParameterCombine;
import com.supermap.desktop.process.parameter.ipls.ParameterSaveDataset;
import com.supermap.desktop.process.types.BasicTypes;
import com.supermap.desktop.process.types.CommonTypes;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.OutputFrame;

import java.text.DecimalFormat;
import java.text.MessageFormat;

/**
 * @author XiaJT
 * 空间自相关分析
 */
public class MetaProcessAutoCorrelation extends MetaProcessAnalyzingPatterns {

	//	private ParameterTextArea parameterResult;
	public MetaProcessAutoCorrelation() {
		setTitle(ProcessProperties.getString("String_AutoCorrelation"));
	}

	private final static String OUTPUT_DATA = "AutoCorrelationResult";
	private ParameterSaveDataset parameterSaveDataset;

	@Override
	protected void initHook() {
		parameterSaveDataset = new ParameterSaveDataset();
		parameterSaveDataset.setDefaultDatasetName("result_autoCorrelation");
		ParameterCombine parameterCombine = new ParameterCombine();
		parameterCombine.addParameters(parameterSaveDataset);
		parameterCombine.setDescribe(CoreProperties.getString("String_ResultSet"));
		DatasourceConstraint.getInstance().constrained(parameterSaveDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);
		parameters.addParameters(parameterCombine);
		parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_Result_SpatialStatistics"), CommonTypes.STATISTICS, parameterCombine);
		// 支持将空间权重矩阵文件当做导入数据-yuanR
		parameters.addInputParameters(INPUT_SPATIALWEIGHTMATRIXFILE, BasicTypes.STRING, parameterPatternsParameter.getParameterFile());
	}

	protected boolean doWork(DatasetVector datasetVector) {

		AnalyzingPatterns.addSteppedListener(steppedListener);
		AnalyzingPatternsResult analyzingPatternsResult = null;
		try {
			analyzingPatternsResult = AnalyzingPatterns.autoCorrelation(datasetVector, parameterPatternsParameter.getPatternParameter());
			if (analyzingPatternsResult != null) {

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
				result += ProcessProperties.getString("String_Label_Morans") + " "
						+ decimalFormat.format(analyzingPatternsResult.getIndex()) + "\n";
				result += ProcessProperties.getString("String_Label_Expectation") + " "
						+ decimalFormat.format(analyzingPatternsResult.getExpectation()) + "\n";
				result += ProcessProperties.getString("String_Label_Variance") + " "
						+ decimalFormat.format(analyzingPatternsResult.getVariance()) + "\n";
				result += ProcessProperties.getString("String_Label_ZScor") + " "
						+ decimalFormat.format(analyzingPatternsResult.getZScore()) + "\n";
				result += ProcessProperties.getString("String_Label_PValue") + " "
						+ decimalFormat.format(analyzingPatternsResult.getPValue()) + "\n";
				result += resultDescription + "\n";

				result += "-----------------------------------------" + "\n"
						+ ProcessProperties.getString("String_Label_DetailedUse")
						+ "http://supermap-idesktop.github.io/SuperMap-iDesktop-Cross/docs/SpatialStatisticalAnalysis/SpatialAutocorrelation.html?SpatialStatisticalAnalysis,AnalyzingPatterns1";

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
		return analyzingPatternsResult != null;
	}

	@Override
	public String getKey() {
		return MetaKeys.AUTO_CORRELATION;
	}
}
