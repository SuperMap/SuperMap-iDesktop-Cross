package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.dataRun;

import com.supermap.data.DatasetType;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.metaProcessImplements.MetaProcessCenterLine;
import com.supermap.desktop.properties.CommonProperties;

/**
 * Created by lixiaoyao on 2017/7/22.
 */
public class MetaProcessRegionToCenterLine extends MetaProcessCenterLine {
	public DatasetType getSonDatasetType() {
		return DatasetType.REGION;
	}

	public String getResultDatasetName(){
		return "result_RegionToCenterLine";
	}

	@Override
	public String getKey() {
		return MetaKeys.REGION_TO_CENTERLINE;
	}

	@Override
	public String getTitle() {
		return  CommonProperties.getString("String_RegionToCenterLine");
	}
}
