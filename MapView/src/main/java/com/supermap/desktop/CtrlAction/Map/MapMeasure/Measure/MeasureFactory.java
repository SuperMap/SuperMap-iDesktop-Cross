package com.supermap.desktop.CtrlAction.Map.MapMeasure.Measure;

import com.supermap.data.DatasetGrid;
import com.supermap.desktop.enums.MeasureType;
import com.supermap.desktop.mapview.MapViewProperties;
import com.supermap.desktop.utilities.LogUtilities;

/**
 * @author XiaJt
 */
public class MeasureFactory {

	public static IMeasureAble getMeasureInstance(MeasureType measureType) {
		if (measureType == MeasureType.Distance) {
			return new MeasureDistance();
		} else if (measureType == MeasureType.Area) {
			return new MeasureArea();
        } else if (measureType == MeasureType.Angle) {
            return new MeasureAngle();
		} else if (measureType == MeasureType.Geodesic) {
			return new MeasureGeodesic();
		}
		LogUtilities.debug(MapViewProperties.getString("Log_MeasureTypeUnSupport") + measureType);
		return null;
	}

	public static IMeasureAble getMeasureInstance(MeasureType measureType, DatasetGrid datasetGrid) {
		if (measureType == MeasureType.Distance_Surface) {
			return new MeasureSurfaceDistance(datasetGrid);
		} else if (measureType == MeasureType.Area_Surface) {
			return new MeasureSurfaceArea(datasetGrid);
		} else if (measureType == MeasureType.Volume_Surface) {
			return new MeasureSurfaceVolume(datasetGrid);
		}
		LogUtilities.debug(MapViewProperties.getString("Log_MeasureTypeUnSupport") + measureType);
		return null;
	}
}
