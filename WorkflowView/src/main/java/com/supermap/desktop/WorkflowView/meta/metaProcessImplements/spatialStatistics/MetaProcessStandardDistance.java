package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.spatialStatistics;

import com.supermap.analyst.spatialstatistics.SpatialMeasure;
import com.supermap.data.DatasetVector;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.ui.OutputFrame;

/**
 * Created by Chen on 2017/7/5 0005.
 */
public class MetaProcessStandardDistance extends MetaProcessSpatialMeasure {
	public MetaProcessStandardDistance() {
		setTitle(ControlsProperties.getString("String_StandardDistance"));
	}

	protected void initHook() {
		resultName = "result_standardDistance";
		OUTPUT_DATASET = "StandardDistanceResult";
		outputName = ControlsProperties.getString("String_StandardDistance");
	}

	@Override
	public String getKey() {
		return MetaKeys.STANDARD_DISTANCE;
	}

	@Override
	protected boolean doWork(DatasetVector datasetVector) {
		boolean isSuccessful = false;

		try {
			SpatialMeasure.addSteppedListener(steppedListener);
			// 调用中心要素方法，并获取结果矢量数据集
			DatasetVector result = SpatialMeasure.measureStandardDistance(
					datasetVector,
					parameterSaveDataset.getResultDatasource(),
					parameterSaveDataset.getResultDatasource().getDatasets().getAvailableDatasetName(parameterSaveDataset.getDatasetName()),
					measureParameter.getMeasureParameter());
			this.getParameters().getOutputs().getData(OUTPUT_DATASET).setValue(result);
			isSuccessful = result != null;
			if (isSuccessful) {
				String message = "-----------------------------------------" + "\n"
						+ "How to Use?" + "\n"
						+ "http://supermap-idesktop.github.io/SuperMap-iDesktop-Cross/docs/SpatialStatisticalAnalysis/MeasureStandardDistance.html?SpatialStatisticalAnalysis,MeasureGeographicDistr,Clusters1,SpatialRelationshipModeling1,AnalyzingPatterns1";
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
}
