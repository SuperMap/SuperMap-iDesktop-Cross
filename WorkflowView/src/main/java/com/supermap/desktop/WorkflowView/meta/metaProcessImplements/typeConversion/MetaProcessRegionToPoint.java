package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.typeConversion;

import com.supermap.data.*;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.geometry.Abstract.IGeometry;
import com.supermap.desktop.geometry.Abstract.IRegionFeature;
import com.supermap.desktop.process.ProcessProperties;

import java.util.Map;

public class MetaProcessRegionToPoint extends MetaProcessPointLineRegion {
	public MetaProcessRegionToPoint() {
		super(DatasetType.REGION, DatasetType.POINT);
		setTitle(ProcessProperties.getString("String_Title_RegionToPoint"));
	}

	@Override
	protected void initHook() {
		OUTPUT_DATA = "RegionToPointResult";
	}

	@Override
	protected String getOutputName() {
		return "result_regionToPoint";
	}

	@Override
	protected String getOutputResultName() {
		return ProcessOutputResultProperties.getString("String_RegionToPointResult");
	}

	@Override
	protected boolean convert(Recordset recordset, IGeometry geometry, Map<String, Object> value) {
		if (geometry instanceof IRegionFeature) {
			GeoRegion geoRegion = null;
			try {
				geoRegion = ((IRegionFeature) geometry).convertToRegion(120);
				for (int i = 0; i < geoRegion.getPartCount(); i++) {
					Point2Ds points = geoRegion.getPart(i);
					double x = 0;
					double y = 0;
					for (int j = 0; j < points.getCount(); j++) {
						x += points.getItem(j).getX();
						y += points.getItem(j).getY();
					}
					x = x / points.getCount();
					y = y / points.getCount();
					GeoPoint geoPoint = new GeoPoint(x, y);
					if (!recordset.addNew(geoPoint, value)) {
						return false;
					}
					geoPoint.dispose();
				}
				return true;
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
		return MetaKeys.CONVERSION_REGION_TO_POINT;
	}
}
