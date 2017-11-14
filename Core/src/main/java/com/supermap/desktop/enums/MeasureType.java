package com.supermap.desktop.enums;

import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.LogUtilities;

import java.text.MessageFormat;

/**
 * Created by Administrator on 2016/3/31.
 */
public enum MeasureType {
	Distance,
	Area,
    Angle,
    Distance_Surface,
    Area_Surface,
    Volume_Surface,
    Geodesic;

	@Override
	public String toString() {
		if (this == Distance) {
			return CoreProperties.getString("String_DistanceMeasure");
		} else if (this == Area) {
			return CoreProperties.getString("String_AreaMeasure");
        } else if (this == Angle) {
            return CoreProperties.getString("String_AngleMeasure");
        } else if (this == Distance_Surface) {
            return CoreProperties.getString("String_SurfaceDistanceMeasure");
        } else if (this == Area_Surface) {
            return CoreProperties.getString("String_SurfaceAreaMeasure");
        } else if (this == Volume_Surface) {
            return CoreProperties.getString("String_SurfaceVolumeMeasure");
        } else if (this == Geodesic) {
            return CoreProperties.getString("String_GeodesicMeasure");
        }
		LogUtilities.debug(MessageFormat.format(CoreProperties.getString("Log_AddedMeasureNeedAddToString"), this));
		return super.toString();
	}
}
