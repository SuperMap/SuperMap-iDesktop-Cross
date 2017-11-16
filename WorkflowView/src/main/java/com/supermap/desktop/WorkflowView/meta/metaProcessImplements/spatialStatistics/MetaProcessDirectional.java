package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.spatialStatistics;

import com.supermap.analyst.spatialstatistics.SpatialMeasure;
import com.supermap.data.DatasetVector;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.ui.OutputFrame;

/**
 * @author XiaJT
 */
public class MetaProcessDirectional extends MetaProcessSpatialMeasure {

	public MetaProcessDirectional() {
		setTitle(ControlsProperties.getString("String_Directional"));
	}

	protected void initHook() {
		OUTPUT_DATASET = "DirectionalResult";
		resultName = "result_directional";
		outputName = ControlsProperties.getString("String_Directional");
	}

	@Override
	protected boolean doWork(DatasetVector datasetVector) {
		boolean isSuccessful = false;

		try {
			SpatialMeasure.addSteppedListener(steppedListener);
			// 调用方向分布方法，并获取结果矢量数据集
			DatasetVector result = SpatialMeasure.measureDirectional(
					datasetVector,
					parameterSaveDataset.getResultDatasource(),
					parameterSaveDataset.getResultDatasource().getDatasets().getAvailableDatasetName(parameterSaveDataset.getDatasetName()),
					measureParameter.getMeasureParameter());
			this.getParameters().getOutputs().getData(OUTPUT_DATASET).setValue(result);
			isSuccessful = result != null;
			if (isSuccessful) {
				String message = "How to Use?" + "\n"
						+ "http://supermap-idesktop.github.io/SuperMap-iDesktop-Cross/docs/SpatialStatisticalAnalysis/MeasureDirection.html?SpatialStatisticalAnalysis,MeasureGeographicDistr,Clusters1,SpatialRelationshipModeling1,AnalyzingPatterns1";
				((OutputFrame) Application.getActiveApplication().getOutput()).setShowTime(false);
				Application.getActiveApplication().getOutput().output(message);
				((OutputFrame) Application.getActiveApplication().getOutput()).setShowTime(true);
			}

		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			SpatialMeasure.removeSteppedListener(steppedListener);
		}
		return isSuccessful;
	}

	@Override
	public String getKey() {
		return MetaKeys.DIRECTIONAL;
	}
}
