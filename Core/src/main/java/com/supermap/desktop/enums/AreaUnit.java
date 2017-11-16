package com.supermap.desktop.enums;

import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.Unit;
import com.supermap.desktop.Application;
import com.supermap.desktop.properties.CoreProperties;

public enum AreaUnit {
	// @formatter:off
    MILLIMETER(UnitValue.MILLIMETER * UnitValue.MILLIMETER),
    CENTIMETER(UnitValue.CENTIMETER * UnitValue.CENTIMETER),
    DECIMETER(UnitValue.DECIMETER * UnitValue.DECIMETER),
	METER(UnitValue.METER * UnitValue.METER),
	//改为long型，因为10000000的平方使用int装不下会产生溢出的错误
	KILOMETER((long) UnitValue.KILOMETER * UnitValue.KILOMETER),
	MILE((long)UnitValue.MILE * UnitValue.MILE),
	INCH(UnitValue.INCH * UnitValue.INCH),
	FOOT(UnitValue.FOOT * UnitValue.FOOT),
	YARD(UnitValue.YARD * UnitValue.YARD),
	MU(66666666667L),
	ACRE(404685642240L);
	// @formatter:on

	private long value = 0;

	AreaUnit(long value) {
		this.value = value;
	}

	public static AreaUnit convertFrom(Unit unit) {
		AreaUnit areaUnit = AreaUnit.METER;

		if (unit == Unit.CENTIMETER) {
			areaUnit = AreaUnit.CENTIMETER;
		} else if (unit == Unit.DECIMETER) {
			areaUnit = AreaUnit.DECIMETER;
		} else if (unit == Unit.FOOT) {
			areaUnit = AreaUnit.FOOT;
		} else if (unit == Unit.INCH) {
			areaUnit = AreaUnit.INCH;
		} else if (unit == Unit.KILOMETER) {
			areaUnit = AreaUnit.KILOMETER;
		} else if (unit == Unit.METER) {
			areaUnit = AreaUnit.METER;
		} else if (unit == Unit.MILE) {
			areaUnit = AreaUnit.MILE;
		} else if (unit == Unit.MILIMETER) {
            areaUnit = AreaUnit.MILLIMETER;
        } else if (unit == Unit.YARD) {
            areaUnit = AreaUnit.YARD;
		} else {
			areaUnit = AreaUnit.METER;
		}
		return areaUnit;
	}

	public Unit getUnit() {
		Unit unit = Unit.METER;

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

	public long getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		String result = "";

        if (this == AreaUnit.MILLIMETER) {
            result = CoreProperties.getString("String_AreaUnit_Millimeter");
        } else if (this == AreaUnit.CENTIMETER) {
			result = CoreProperties.getString("String_AreaUnit_Centimeter");
		} else if (this == AreaUnit.DECIMETER) {
			result = CoreProperties.getString("String_AreaUnit_Decimeter");
		} else if (this == METER) {
			result = CoreProperties.getString("String_AreaUnit_Meter");
		} else if (this == KILOMETER) {
			result = CoreProperties.getString("String_AreaUnit_Kilometer");
		} else if (this == AreaUnit.MILE) {
			result = CoreProperties.getString("String_AreaUnit_Mile");
		} else if (this == AreaUnit.INCH) {
			result = CoreProperties.getString("String_AreaUnit_Inch");
		} else if (this == AreaUnit.FOOT) {
			result = CoreProperties.getString("String_AreaUnit_Foot");
		} else if (this == AreaUnit.YARD) {
			result = CoreProperties.getString("String_AreaUnit_Yard");
		} else if (this == AreaUnit.ACRE) {
			result = CoreProperties.getString("String_AreaUnit_Acre");
		} else if (this == AreaUnit.MU) {
			result = CoreProperties.getString("String_AreaUnit_Mu");
		}
		return result;
	}

	public static AreaUnit getValueOf(String name) {
		AreaUnit result = AreaUnit.METER;
		if (name.equals(CoreProperties.getString("String_AreaUnit_Millimeter"))) {
            result = AreaUnit.MILLIMETER;
        } else if (name.equals(CoreProperties.getString("String_AreaUnit_Centimeter"))) {
            result = AreaUnit.CENTIMETER;
		} else if (name.equals(CoreProperties.getString("String_AreaUnit_Decimeter"))) {
			result = AreaUnit.DECIMETER;
		} else if (name.equals(CoreProperties.getString("String_AreaUnit_Meter"))) {
			result = AreaUnit.METER;
		} else if (name.equals(CoreProperties.getString("String_AreaUnit_Kilometer"))) {
			result = AreaUnit.KILOMETER;
		} else if (name.equals(CoreProperties.getString("String_AreaUnit_Mile"))) {
			result = AreaUnit.MILE;
		} else if (name.equals(CoreProperties.getString("String_AreaUnit_Inch"))) {
			result = AreaUnit.INCH;
		} else if (name.equals(CoreProperties.getString("String_AreaUnit_Foot"))) {
			result = AreaUnit.FOOT;
		} else if (name.equals(CoreProperties.getString("String_AreaUnit_Yard"))) {
			result = AreaUnit.YARD;
		} else if (name.equals(CoreProperties.getString("String_AreaUnit_Acre"))) {
			result = AreaUnit.ACRE;
		} else if (name.equals(CoreProperties.getString("String_AreaUnit_Mu"))) {
			result = AreaUnit.MU;
		}

		return result;
	}

	public static double convertArea(PrjCoordSys prjCoordSys, Unit curUnit, Double area) {
		double resultArea = area;
		try {
			Unit unit = Unit.METER;
			if (prjCoordSys.getType() == PrjCoordSysType.PCS_NON_EARTH) {
				unit = prjCoordSys.getCoordUnit();
			}
			resultArea = ConvertArea(unit, curUnit, area);
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
		return resultArea;
	}


	public static Double ConvertArea(Unit befUnit, Unit curUnit, Double area) {
		Double resultArea = area;
		try {
			if (befUnit != curUnit) {
				// 两种单位不相同时才进行转换
				resultArea = area * Math.pow(((double) befUnit.value() / (double) curUnit.value()), 2);
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
		return resultArea;
	}
}
