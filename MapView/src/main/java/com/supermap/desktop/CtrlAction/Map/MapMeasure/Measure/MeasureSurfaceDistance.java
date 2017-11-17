package com.supermap.desktop.CtrlAction.Map.MapMeasure.Measure;

import com.supermap.analyst.spatialanalyst.CalculationTerrain;
import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.enums.LengthUnit;
import com.supermap.desktop.enums.MeasureType;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.ui.TrackingEvent;

import java.text.MessageFormat;

/**
 * Created by ChenS on 2017/11/10 0010.
 */
public class MeasureSurfaceDistance extends MeasureDistance {
    private DatasetGrid datasetGrid;
    private double totalLength;

    public MeasureSurfaceDistance(DatasetGrid datasetGrid) {
        this.datasetGrid = datasetGrid;
        this.totalLength = 0;
    }

    @Override
    protected double computeDistance(Point2D pntA, Point2D pntB, Unit unit) {
        double distance;
        Point2Ds point2Ds = new Point2Ds();
        point2Ds.add(pntA);
        point2Ds.add(pntB);
        distance = CalculationTerrain.computeSurfaceDistance(datasetGrid, new GeoLine(point2Ds));
        totalLength += distance;
        distance = LengthUnit.ConvertDistance(mapControl.getMap().getPrjCoordSys(), unit, distance);

        return distance;
    }

    @Override
    protected double computeDistance(TrackingEvent event, Unit unit, boolean isTotal) {
        GeoLine line = (GeoLine) event.getGeometry();
        double distance = CalculationTerrain.computeSurfaceDistance(datasetGrid, line);
        return LengthUnit.ConvertDistance(mapControl.getMap().getPrjCoordSys(), unit, isTotal ? distance : distance - totalLength);
    }

    @Override
    protected void outputMeasure(double length) {
        totalLength = LengthUnit.ConvertDistance(mapControl.getMap().getPrjCoordSys(), getLengthUnit().getUnit(), totalLength);
        Application.getActiveApplication().getOutput().output(MessageFormat.format(CoreProperties.getString("String_Map_MeasureSurfaceDistance"),
                decimalFormat.format(totalLength), getLengthUnit().toString()));
        totalLength = 0;
    }

    @Override
    public MeasureType getMeasureType() {
        return MeasureType.Distance_Surface;
    }
}
