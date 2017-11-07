package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.gridAnalyst.iso;

import com.supermap.analyst.spatialanalyst.SurfaceAnalyst;
import com.supermap.analyst.spatialanalyst.SurfaceExtractParameter;
import com.supermap.data.DatasetGrid;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.process.parameter.interfaces.IParameterPanel;
import com.supermap.desktop.properties.CoreProperties;

/**
 * 提取等值线
 * Created by xie on 2017/3/6.
 */
public class MetaProcessISOLine extends MetaProcessISO {
	@Override
	protected void initHook() {
		OUTPUT_DATA = "ISOLineResult";
	}

	public MetaProcessISOLine() {
		setTitle(CoreProperties.getString("String_SurfaceISOLine"));
		OUTPUT_DATA_TYPE = ProcessOutputResultProperties.getString("String_SurfaceAnalyst_ISOLineResult");
		super.initParameters();
		super.initParameterConstraint();
		super.initParametersState();
		super.initParametersListener();
	}

	@Override
	protected String getDefaultResultName() {
		return "result_ISOLine";
	}

	public DatasetVector subExecute(SurfaceExtractParameter surfaceExtractParameter, DatasetGrid dataset, Datasource datasource, String datasetName) {
		return SurfaceAnalyst.extractIsoline(surfaceExtractParameter, dataset, datasource, datasetName);
	}

	@Override
	public String getKey() {
		return MetaKeys.ISOLINE;
	}

	@Override
	public IParameterPanel getComponent() {
		return parameters.getPanel();
	}

}
