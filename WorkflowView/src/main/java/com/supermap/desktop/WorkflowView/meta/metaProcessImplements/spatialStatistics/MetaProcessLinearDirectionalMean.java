package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.spatialStatistics;

import com.supermap.analyst.spatialstatistics.SpatialMeasure;
import com.supermap.data.DatasetVector;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.controls.ControlsProperties;

/**
 * Created by YuanR on 2017/7/6
 * 空间统计分析-线性方向平均值
 */

public class MetaProcessLinearDirectionalMean extends MetaProcessSpatialMeasure {

	public MetaProcessLinearDirectionalMean() {
		setTitle(ControlsProperties.getString("String_LinearDirectionalMean"));
	}

	protected void initHook() {
		OUTPUT_DATASET = "LinearDirectionalMeanResult";
		resultName = "result_linearDirectionalMean";
		outputName = ControlsProperties.getString("String_LinearDirectionalMean");
	}

	@Override
	protected boolean doWork(DatasetVector datasetVector) {
		boolean isSuccessful = false;

		try {
			SpatialMeasure.addSteppedListener(steppedListener);
			// 调用线性方向平均值方法，并获取结果矢量数据集
			DatasetVector result = SpatialMeasure.measureLinearDirectionalMean(
					datasetVector,
					parameterSaveDataset.getResultDatasource(),
					parameterSaveDataset.getResultDatasource().getDatasets().getAvailableDatasetName(parameterSaveDataset.getDatasetName()),
					measureParameter.getMeasureParameter());
			this.getParameters().getOutputs().getData(OUTPUT_DATASET).setValue(result);
			isSuccessful = result != null;
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
		return MetaKeys.LINEAR_DIRECTIONAL_MEAN;
	}
}


