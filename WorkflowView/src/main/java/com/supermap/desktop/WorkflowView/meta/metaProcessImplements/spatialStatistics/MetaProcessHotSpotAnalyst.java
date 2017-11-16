package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.spatialStatistics;

import com.supermap.analyst.spatialstatistics.ClusteringDistributions;
import com.supermap.data.DatasetType;
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
 */
public class MetaProcessHotSpotAnalyst extends MetaProcessAnalyzingPatterns {
	private final static String OUTPUT_DATASET = "HotSpotResult";
	private ParameterSaveDataset parameterSaveDataset;

	public MetaProcessHotSpotAnalyst() {
		setTitle(ProcessProperties.getString("String_hotSpotAnalyst"));
	}

	@Override
	protected void initHook() {
		dataset.setDatasetTypes(DatasetType.REGION, DatasetType.POINT, DatasetType.LINE);
		parameterSaveDataset = new ParameterSaveDataset();
		parameterSaveDataset.setDefaultDatasetName("result_hotSpot");
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
		return MetaKeys.HOT_SPOT_ANALYST;
	}

	@Override
	protected boolean doWork(DatasetVector datasetVector) {
		boolean isSuccessful = false;

		try {
			ClusteringDistributions.addSteppedListener(steppedListener);
			DatasetVector result = ClusteringDistributions.hotSpotAnalyst(datasetVector, parameterSaveDataset.getResultDatasource(), parameterSaveDataset.getResultDatasource().getDatasets().getAvailableDatasetName(parameterSaveDataset.getDatasetName()), parameterPatternsParameter.getPatternParameter());
			this.getParameters().getOutputs().getData(OUTPUT_DATASET).setValue(result);
			isSuccessful = result != null;
			if (isSuccessful) {
				String message = "-----------------------------------------" + "\n"
						+ "How to Use?" + "\n"
						+ "http://supermap-idesktop.github.io/SuperMap-iDesktop-Cross/docs/SpatialStatisticalAnalysis/HotSpotAnalyst.html?SpatialStatisticalAnalysis,Clusters1,SpatialRelationshipModeling1,AnalyzingPatterns1";
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
