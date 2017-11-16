package com.supermap.desktop.enums;

import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.Unit;
import com.supermap.desktop.Application;
import com.supermap.desktop.properties.CoreProperties;

/**
 * Created by ChenS on 2017/11/16 0016.
 */
public enum VolumeUnit {
    MILLIMETER(Unit.MILIMETER),
    CENTIMETER(Unit.CENTIMETER),
    DECIMETER(Unit.DECIMETER),
    METER(Unit.METER),
    KILOMETER(Unit.KILOMETER),
    MILE(Unit.MILE),
    INCH(Unit.INCH),
    FOOT(Unit.FOOT),
    YARD(Unit.YARD);

    private Unit unit;

    VolumeUnit(Unit unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        String result = "";

        if (this == VolumeUnit.MILLIMETER) {
            result = CoreProperties.getString("String_VolumeUnit_Millimeter");
        } else if (this == VolumeUnit.CENTIMETER) {
            result = CoreProperties.getString("String_VolumeUnit_Centimeter");
        } else if (this == VolumeUnit.DECIMETER) {
            result = CoreProperties.getString("String_VolumeUnit_Decimeter");
        } else if (this == METER) {
            result = CoreProperties.getString("String_VolumeUnit_Meter");
        } else if (this == KILOMETER) {
            result = CoreProperties.getString("String_VolumeUnit_Kilometer");
        } else if (this == VolumeUnit.MILE) {
            result = CoreProperties.getString("String_VolumeUnit_Mile");
        } else if (this == VolumeUnit.INCH) {
            result = CoreProperties.getString("String_VolumeUnit_Inch");
        } else if (this == VolumeUnit.FOOT) {
            result = CoreProperties.getString("String_VolumeUnit_Foot");
        } else if (this == VolumeUnit.YARD) {
            result = CoreProperties.getString("String_VolumeUnit_Yard");
        }
        return result;
    }

    public Unit getUnit() {
        if (this == CENTIMETER) {
            unit = Unit.CENTIMETER;
        } else if (this == DECIMETER) {
            unit = Unit.DECIMETER;
        } else if (this == FOOT) {
            unit = Unit.FOOT;
        } else if (this == INCH) {
            unit = Unit.INCH;
        } else if (this == KILOMETER) {
            unit = Unit.KILOMETER;
        } else if (this == METER) {
            unit = Unit.METER;
        } else if (this == MILE) {
            unit = Unit.MILE;
        } else if (this == MILLIMETER) {
            unit = Unit.MILIMETER;
        } else if (this == YARD) {
            unit = Unit.YARD;
        } else {
            unit = Unit.METER;
        }
        return unit;
    }

    public static VolumeUnit getValueOf(String name) {
        VolumeUnit result;
        if (name.equals(CoreProperties.getString("String_VolumeUnit_Millimeter"))) {
            result = VolumeUnit.MILLIMETER;
        } else if (name.equals(CoreProperties.getString("String_VolumeUnit_Centimeter"))) {
            result = VolumeUnit.CENTIMETER;
        } else if (name.equals(CoreProperties.getString("String_VolumeUnit_Decimeter"))) {
            result = VolumeUnit.DECIMETER;
        } else if (name.equals(CoreProperties.getString("String_VolumeUnit_Meter"))) {
            result = VolumeUnit.METER;
        } else if (name.equals(CoreProperties.getString("String_VolumeUnit_Kilometer"))) {
            result = VolumeUnit.KILOMETER;
        } else if (name.equals(CoreProperties.getString("String_VolumeUnit_Mile"))) {
            result = VolumeUnit.MILE;
        } else if (name.equals(CoreProperties.getString("String_VolumeUnit_Inch"))) {
            result = VolumeUnit.INCH;
        } else if (name.equals(CoreProperties.getString("String_VolumeUnit_Foot"))) {
            result = VolumeUnit.FOOT;
        } else {
            result = VolumeUnit.YARD;
        }

        return result;
    }

    public static double convertVolume(PrjCoordSys prjCoordSys, Unit curUnit, Double volume) {
        double resultArea = volume;
        try {
            Unit unit = Unit.METER;
            if (prjCoordSys.getType() == PrjCoordSysType.PCS_NON_EARTH) {
                unit = prjCoordSys.getCoordUnit();
            }
            resultArea = ConvertVolume(unit, curUnit, volume);
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        }
        return resultArea;
    }

    public static Double ConvertVolume(Unit befUnit, Unit curUnit, Double volume) {
        Double resultArea = volume;
        try {
            if (befUnit != curUnit) {
                // 两种单位不相同时才进行转换
                resultArea = volume * Math.pow(((double) befUnit.value() / (double) curUnit.value()), 3);
            }
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        }
        return resultArea;
    }
}
