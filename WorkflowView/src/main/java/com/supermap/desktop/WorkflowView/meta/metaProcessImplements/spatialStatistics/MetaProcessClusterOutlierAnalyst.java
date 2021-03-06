package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.spatialStatistics;

import com.supermap.analyst.spatialstatistics.ClusteringDistributions;
import com.supermap.data.DatasetVector;
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

/**
 * @author XiaJT
 * 聚类和异常值分析
 */
public class MetaProcessClusterOutlierAnalyst extends MetaProcessAnalyzingPatterns {

	private final static String OUTPUT_DATASET = "ClusterOutlierResult";
	private ParameterSaveDataset parameterSaveDataset;

	public MetaProcessClusterOutlierAnalyst() {
		setTitle(ProcessProperties.getString("String_clusterOutlierAnalyst"));
	}

	@Override
	protected void initHook() {
		parameterSaveDataset = new ParameterSaveDataset();
		parameterSaveDataset.setDefaultDatasetName("result_clusterOutlier");
		ParameterCombine parameterCombine = new ParameterCombine();
		parameterCombine.addParameters(parameterSaveDataset);
		parameterCombine.setDescribe(CoreProperties.getString("String_ResultSet"));
		DatasourceConstraint.getInstance().constrained(parameterSaveDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);
		parameters.addParameters(parameterCombine);
		parameters.addOutputParameters(OUTPUT_DATASET, ProcessOutputResultProperties.getString("String_Result_Analyst"), DatasetTypes.VECTOR, parameterCombine);
		// 支持将空间权重矩阵文件当做导入数据-yuanR
		parameters.addInputParameters(INPUT_SPATIALWEIGHTMATRIXFILE, BasicTypes.STRING, parameterPatternsParameter.getParameterFile());
	}

	@Override
	public String getKey() {
		return MetaKeys.CLUSTER_OUTLIER_ANALYST;
	}

	@Override
	protected boolean doWork(DatasetVector datasetVector) {
		boolean isSuccessful = false;

		try {
			ClusteringDistributions.addSteppedListener(steppedListener);
			DatasetVector result = ClusteringDistributions.clusterOutlierAnalyst(datasetVector, parameterSaveDataset.getResultDatasource(), parameterSaveDataset.getResultDatasource().getDatasets().getAvailableDatasetName(parameterSaveDataset.getDatasetName()), parameterPatternsParameter.getPatternParameter());
			this.getParameters().getOutputs().getData(OUTPUT_DATASET).setValue(result);
			isSuccessful = result != null;
			if (isSuccessful) {
				String message = ProcessProperties.getString("String_Label_DetailedUse")
						+ "http://supermap-idesktop.github.io/SuperMap-iDesktop-Cross/docs/SpatialStatisticalAnalysis/ClusterOutlierAnalyst.html?SpatialStatisticalAnalysis,Clusters1,SpatialRelationshipModeling1,AnalyzingPatterns1";
				((OutputFrame) Application.getActiveApplication().getOutput()).setShowTime(false);
				Application.getActiveApplication().getOutput().output(message);
				((OutputFrame) Application.getActiveApplication().getOutput()).setShowTime(true);
			}

		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			ClusteringDistributions.removeSteppedListener(steppedListener);
		}
		return isSuccessful;
	}
}
