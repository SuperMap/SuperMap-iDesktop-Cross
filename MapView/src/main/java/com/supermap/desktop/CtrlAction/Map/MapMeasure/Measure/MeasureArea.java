package com.supermap.desktop.CtrlAction.Map.MapMeasure.Measure;

import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.enums.AreaUnit;
import com.supermap.desktop.enums.MeasureType;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.FontUtilities;
import com.supermap.desktop.utilities.SystemPropertyUtilities;
import com.supermap.ui.*;

import java.awt.*;
import java.text.MessageFormat;

/**
 * Created by Administrator on 2016/1/28.
 */
public class MeasureArea extends Measure {
    private static final String measureAreaTag = TRACKING_OBJECT_NAME + "measureAreaTag";

    public MeasureArea() {
        textTagTitle += "AreaText";
        trackingListener = new TrackingListener() {
            @Override
            public void tracking(TrackingEvent e) {
                try {
                    if (currentGeometry != null) {
                        currentGeometry.dispose();
                        currentGeometry = null;
                    }
                    if (e.getGeometry() != null) {
                        currentGeometry = e.getGeometry().clone();
                        GeoRegion geoRegion = ((GeoRegion) e.getGeometry());
                        Point2Ds pnts = new Point2Ds(geoRegion.getPart(0));
                        if (pnts.getCount() > 2) {
                            pnts.add(pnts.getItem(0));
                            GeoRegion region = new GeoRegion(pnts);
                            Point pointInnerPixel = mapControl.getMap().mapToPixel(region.getInnerPoint());

                            String text = drawTextBox(e);
                            labelTextBoxCurrent.setText(text);
                            labelTextBoxCurrent.setSize((int) (((labelTextBoxCurrent.getText().length() << 3) + 12 + getSystemLength()) * SystemPropertyUtilities.getSystemSizeRate()), 23);
                            labelTextBoxCurrent.setLocation(pointInnerPixel);
                            mapControl.add(labelTextBoxCurrent);
                            labelTextBoxCurrent.setVisible(true);
                        } else {
                            labelTextBoxCurrent.setVisible(false);
                        }
                    } else {
                        labelTextBoxCurrent.setVisible(false);
                    }

                } catch (Exception ex) {
                    Application.getActiveApplication().getOutput().output(ex);
                }
            }
        };
        trackedListener = new TrackedListener() {
            @Override
            public void tracked(TrackedEvent trackedEvent) {
                Geometry geometry = trackedEvent.getGeometry();
                if (geometry != null) {
                    if (geometry.getStyle() == null) {
                        geometry.setStyle(new GeoStyle());
                    }
                    geometry.getStyle().setLineWidth(0.1);
                    geometry.getStyle().setFillSymbolID(1);
                    geometry.getStyle().setLineColor(Color.BLUE);
                    mapControl.getMap().getTrackingLayer().add(geometry, measureAreaTag);

                    getResult(trackedEvent);
                }
                cancelEdit();
                refreshTrackingLayer();
            }
        };
    }

    protected String drawTextBox(TrackingEvent event) {
        double area = AreaUnit.convertArea(mapControl.getMap().getPrjCoordSys(), getAreaUnit().getUnit(), event.getArea());
        return MessageFormat.format(CoreProperties.getString("String_Map_MeasureArea"), decimalFormat.format(area), getAreaUnit().toString());
    }

    protected void getResult(TrackedEvent event) {
        double totalArea = AreaUnit.convertArea(mapControl.getMap().getPrjCoordSys(), getAreaUnit().getUnit(), event.getArea());
        GeoRegion geoRegion = ((GeoRegion) event.getGeometry());
        showResult(CoreProperties.getString("String_Map_MeasureTotalArea"), totalArea, event.getGeometry().getInnerPoint(), getAreaUnit().toString());
    }

    protected void showResult(String s, double result, Point2D point2D, String unitString) {
        String info = MessageFormat.format(s, decimalFormat.format(result), unitString);

        TextPart part = new TextPart(info, point2D);
        GeoText geotext = new GeoText(part);

        TextStyle textStyle = geotext.getTextStyle();
        textStyle.setFontHeight(FontUtilities.fontSizeToMapHeight(textFontHeight,
                mapControl.getMap(), textStyle.isSizeFixed()));
        mapControl.getMap().getTrackingLayer().add(geotext, textTagTitle + "FinishedMeasure");
        Application.getActiveApplication().getOutput().output(MessageFormat.format(s, decimalFormat.format(result), unitString));
    }

    @Override
    protected Action getMeasureAction() {
        return Action.CREATEPOLYGON;
    }

    @Override
    protected void removeLastAdded() {
        // 面积不需要
    }

    @Override
    public MeasureType getMeasureType() {
        return MeasureType.Area;
    }
}
