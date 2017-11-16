package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.spatialStatistics;

import com.supermap.analyst.spatialstatistics.AnalyzingPatterns;
import com.supermap.analyst.spatialstatistics.AnalyzingPatternsResult;
import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.parameter.ipls.ParameterCombine;
import com.supermap.desktop.process.parameter.ipls.ParameterSaveDataset;
import com.supermap.desktop.process.types.BasicTypes;
import com.supermap.desktop.process.types.DatasetTypes;
import com.supermap.desktop.properties.CoreProperties;
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
		parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_Result_Tabular"), DatasetTypes.TABULAR, parameterCombine);
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

				//对分析结果的描述
				double morans = analyzingPatternsResult.getIndex();
				String resultDescription = "";
				if (morans > 0) {
					//数据呈正相关性
					resultDescription = ProcessProperties.getString("String_AnalyzingPatterns_PositiveDescription");
				} else if (morans < 0) {
					//数据呈负相关性
					resultDescription = ProcessProperties.getString("String_AnalyzingPatterns_NegativeDescription");
				} else {
					//数据呈随机性
					resultDescription = ProcessProperties.getString("String_AnalyzingPatterns_RandomnessDescription");
				}

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
				result += ProcessProperties.getString("String_Label_ConfidenceCoefficient") + " "
						+ confidenceCoefficient + "\n";
				result += resultDescription + "\n";


				// 不显示时间-yuanR2017.9.6
				((OutputFrame) Application.getActiveApplication().getOutput()).setShowTime(false);
				Application.getActiveApplication().getOutput().output(result);
				((OutputFrame) Application.getActiveApplication().getOutput()).setShowTime(true);
				//parameterResult.setSelectedItem(result);
				// 将分析结果输出到属性表
				String[] fieldName = {
						ProcessProperties.getString("String_Morans"),
						ProcessProperties.getString("String_Expectation"),
						ProcessProperties.getString("String_Variance"),
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
		return analyzingPatternsResult != null;
	}

	@Override
	public String getKey() {
		return MetaKeys.AUTO_CORRELATION;
	}
}
