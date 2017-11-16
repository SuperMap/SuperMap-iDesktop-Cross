package com.supermap.desktop.CtrlAction.Map.MapMeasure.Measure;

import com.supermap.analyst.spatialanalyst.CalculationTerrain;
import com.supermap.data.DatasetGrid;
import com.supermap.data.GeoRegion;
import com.supermap.desktop.enums.AreaUnit;
import com.supermap.desktop.enums.MeasureType;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.ui.TrackedEvent;
import com.supermap.ui.TrackingEvent;

/**
 * Created by ChenS on 2017/11/10 0010.
 */
public class MeasureSurfaceArea extends MeasureArea {
    protected DatasetGrid datasetGrid;

    public MeasureSurfaceArea(DatasetGrid datasetGrid) {
        this.datasetGrid = datasetGrid;
    }

    @Override
    protected String drawTextBox(TrackingEvent event) {
        return "";
    }

    @Override
    protected void getResult(TrackedEvent event) {
        GeoRegion region = (GeoRegion) event.getGeometry();
        double totalArea = CalculationTerrain.computeSurfaceArea(datasetGrid, region);
        totalArea = AreaUnit.convertArea(mapControl.getMap().getPrjCoordSys(), getAreaUnit().getUnit(), totalArea);
        showResult(CoreProperties.getString("String_Map_MeasureSurfaceArea"), totalArea, event.getGeometry().getInnerPoint(), getAreaUnit().toString());
    }


    @Override
    public MeasureType getMeasureType() {
        return MeasureType.Area_Surface;
    }
}
