package com.supermap.desktop.CtrlAction.Map.MapMeasure.Measure;

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
public class MeasureGeodesic extends MeasureDistance {
    private double totalLength;

    public MeasureGeodesic() {
        this.totalLength = 0;
    }

    @Override
    protected double computeDistance(Point2D pntA, Point2D pntB, Unit unit) {
        GeoSpheroid geoSpheroid = mapControl.getMap().getPrjCoordSys().getGeoCoordSys().getGeoDatum().getGeoSpheroid();
        double distance = Geometrist.computeGeodesicDistance(new Point2Ds(new Point2D[]{pntA, pntB}), geoSpheroid.getAxis(),
                geoSpheroid.getFlatten());
        distance = LengthUnit.ConvertDistance(mapControl.getMap().getPrjCoordSys(), unit, distance);
        totalLength += distance;
        return distance;
    }

    @Override
    protected void outputMeasure(double length) {
        Application.getActiveApplication().getOutput().output(MessageFormat.format(CoreProperties.getString("String_Map_MeasureGeodesicDistance"),
                decimalFormat.format(totalLength), getLengthUnit().toString()));
        totalLength = 0;
    }

    @Override
    protected double computeDistance(TrackingEvent event, Unit unit, boolean isTotal) {
        GeoSpheroid geoSpheroid = mapControl.getMap().getPrjCoordSys().getGeoCoordSys().getGeoDatum().getGeoSpheroid();
        GeoLine line = (GeoLine) event.getGeometry();
        double distance = Geometrist.computeGeodesicDistance(line.getPart(0), geoSpheroid.getAxis(), geoSpheroid.getFlatten());
        return LengthUnit.ConvertDistance(mapControl.getMap().getPrjCoordSys(), unit, isTotal ? totalLength + distance : distance);
    }

    @Override
    public MeasureType getMeasureType() {
        return MeasureType.Geodesic;
    }
}
