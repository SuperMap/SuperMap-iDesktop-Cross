package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.spatialStatistics;

import com.supermap.analyst.spatialstatistics.AnalyzingPatterns;
import com.supermap.analyst.spatialstatistics.AnalyzingPatternsResult;
import com.supermap.data.DatasetVector;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.parameter.interfaces.datas.types.BasicTypes;
import com.supermap.desktop.ui.OutputFrame;

import java.text.DecimalFormat;

/**
 * @author XiaJT
 * 空间自相关分析
 */
public class MetaProcessAutoCorrelation extends MetaProcessAnalyzingPatterns {

//	private ParameterTextArea parameterResult;

	public MetaProcessAutoCorrelation() {
		setTitle(ProcessProperties.getString("String_AutoCorrelation"));
	}


	@Override
	protected void initHook() {
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

				DecimalFormat decimalFormat = new DecimalFormat("0.0000");
				String result = "";
				result += ProcessProperties.getString("String_Label_Morans") + " "
						+ decimalFormat.format(analyzingPatternsResult.getIndex()) + "\n";
				result += ProcessProperties.getString("String_Label_Expectation") + " "
						+ decimalFormat.format(analyzingPatternsResult.getExpectation()) + "\n";
				result += ControlsProperties.getString("String_Label_Variance") + " "
						+ decimalFormat.format(analyzingPatternsResult.getVariance()) + "\n";
				result += ProcessProperties.getString("String_Label_ZScor") + " "
						+ decimalFormat.format(analyzingPatternsResult.getZScore()) + "\n";
				result += ProcessProperties.getString("String_Label_PValue") + " "
						+ decimalFormat.format(analyzingPatternsResult.getPValue()) + "\n";
				result += ProcessProperties.getString("String_Label_ConfidenceCoefficient") + " "
						+ confidenceCoefficient + "\n";


				// 不显示时间-yuanR2017.9.6
				((OutputFrame) Application.getActiveApplication().getOutput()).setShowTime(false);
				Application.getActiveApplication().getOutput().output(result);
				((OutputFrame) Application.getActiveApplication().getOutput()).setShowTime(true);
				//parameterResult.setSelectedItem(result);
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
