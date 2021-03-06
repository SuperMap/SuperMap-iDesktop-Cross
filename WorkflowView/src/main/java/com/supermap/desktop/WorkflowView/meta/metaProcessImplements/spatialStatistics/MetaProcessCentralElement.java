package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.spatialStatistics;

import com.supermap.analyst.spatialstatistics.SpatialMeasure;
import com.supermap.data.DatasetVector;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.ui.OutputFrame;
import com.supermap.desktop.utilities.DatasetUtilities;

/**
 * @author XiaJT
 */
public class MetaProcessCentralElement extends MetaProcessSpatialMeasure {

	public MetaProcessCentralElement() {
		setTitle(ControlsProperties.getString("String_CentralElement"));
	}

	protected void initHook() {
		OUTPUT_DATASET = "CentralElementResult";
		resultName = "result_centralElement";
		outputName = ControlsProperties.getString("String_CentralElement");
	}

	@Override
	protected boolean doWork(DatasetVector datasetVector) {
		boolean isSuccessful = false;

		try {
			SpatialMeasure.addSteppedListener(steppedListener);
			// 调用中心要素方法，并获取结果矢量数据集
			DatasetVector result = SpatialMeasure.measureCentralElement(
					datasetVector,
					parameterSaveDataset.getResultDatasource(),
					DatasetUtilities.getAvailableDatasetName(parameterSaveDataset.getResultDatasource(), parameterSaveDataset.getDatasetName(), null)
					,
					measureParameter.getMeasureParameter());
			this.getParameters().getOutputs().getData(OUTPUT_DATASET).setValue(result);
			isSuccessful = result != null;
			if (isSuccessful) {
				String message = ProcessProperties.getString("String_Label_DetailedUse")
						+ "http://supermap-idesktop.github.io/SuperMap-iDesktop-Cross/docs/SpatialStatisticalAnalysis/CentralFeature.html?SpatialStatisticalAnalysis,MeasureGeographicDistr,Clusters1,SpatialRelationshipModeling1,AnalyzingPatterns1";
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
		return MetaKeys.CENTRAL_ELEMENT;
	}
}
