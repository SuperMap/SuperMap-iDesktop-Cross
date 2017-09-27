package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.typeConversion;

import com.supermap.data.DatasetType;
import com.supermap.data.GeoRegion;
import com.supermap.data.Recordset;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.geometry.Abstract.IGeometry;
import com.supermap.desktop.geometry.Abstract.ILineFeature;
import com.supermap.desktop.geometry.Abstract.IRegionConvertor;
import com.supermap.desktop.process.ProcessProperties;

import java.util.Map;

public class MetaProcessLineToRegion extends MetaProcessPointLineRegion {
	public MetaProcessLineToRegion() {
		super(DatasetType.LINE, DatasetType.REGION);
		setTitle(ProcessProperties.getString("String_Title_LineToRegion"));
	}

	@Override
	protected void initHook() {
		OUTPUT_DATA = "LineToRegionResult";
	}

	@Override
	protected String getOutputName() {
		return "result_lineToRegion";
	}

	@Override
	protected String getOutputResultName() {
		return ProcessOutputResultProperties.getString("String_LineToRegionResult");
	}

	@Override
	protected boolean convert(Recordset recordset, IGeometry geometry, Map<String, Object> value) {

		if (geometry instanceof ILineFeature && geometry instanceof IRegionConvertor) {
			GeoRegion geoRegion = null;
			try {
				geoRegion = ((IRegionConvertor) geometry).convertToRegion(120);
				if (!recordset.addNew(geoRegion, value)) {
					return false;
				} else {
					return true;
				}
			} catch (UnsupportedOperationException e) {
				// 此时返回false-yuanR2017.9.19
				return false;
			} finally {
				if (geoRegion != null) {
					geoRegion.dispose();
				}
			}
		} else {
			return false;
		}
	}

	@Override
	public String getKey() {
		return MetaKeys.CONVERSION_LINE_TO_REGION;
	}
}
