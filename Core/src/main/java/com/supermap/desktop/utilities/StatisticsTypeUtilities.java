package com.supermap.desktop.utilities;

import com.supermap.analyst.spatialanalyst.StatisticsFieldType;
import com.supermap.analyst.spatialstatistics.StatisticsType;
import com.supermap.desktop.Application;
import com.supermap.desktop.properties.CoreProperties;

/**
 * Created by hanyz on 2017/5/4.
 */
public class StatisticsTypeUtilities {
	private StatisticsTypeUtilities() {
	}

	public static String getStatisticsTypeName(StatisticsType type) {
		String name = "";
		try {
			if (type == StatisticsType.MAX) {
				name = CoreProperties.getString("String_StatisticsType_MAX");
			} else if (type == StatisticsType.MIN) {
				name = CoreProperties.getString("String_StatisticsType_MIN");
			} else if (type == StatisticsType.MEAN) {
				name = CoreProperties.getString("String_StatisticsType_MEAN");
			} else if (type == StatisticsType.MEDIAN) {
				name = CoreProperties.getString("String_StatisticsType_MEDIAN");
			} else if (type == StatisticsType.SUM) {
				name = CoreProperties.getString("String_StatisticsType_SUM");
			} else if (type == StatisticsType.FIRST) {
				name = CoreProperties.getString("String_StatisticsType_FIRST");
			} else if (type == StatisticsType.LAST) {
				name = CoreProperties.getString("String_StatisticsType_LAST");
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
		return name;
	}

	public static String getStatisticsTypeNameForDatasetDissloveType(com.supermap.analyst.spatialanalyst.StatisticsType type) {
		String name = "";
		try {
			if (type == com.supermap.analyst.spatialanalyst.StatisticsType.MAX) {
				name = CoreProperties.getString("String_StatisticsType_MAX");
			} else if (type == com.supermap.analyst.spatialanalyst.StatisticsType.MIN) {
				name = CoreProperties.getString("String_StatisticsType_MIN");
			} else if (type == com.supermap.analyst.spatialanalyst.StatisticsType.MEAN) {
				name = CoreProperties.getString("String_StatisticsType_MEAN");
			}  else if (type == com.supermap.analyst.spatialanalyst.StatisticsType.SUM) {
				name = CoreProperties.getString("String_StatisticsType_SUM");
			} else if (type == com.supermap.analyst.spatialanalyst.StatisticsType.FIRST) {
				name = CoreProperties.getString("String_StatisticsType_FIRST");
			} else if (type == com.supermap.analyst.spatialanalyst.StatisticsType.LAST) {
				name = CoreProperties.getString("String_StatisticsType_LAST");
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
		return name;
	}

	public static String getStatisticsTypeNameForDatasetRarefyPointsType(com.supermap.analyst.spatialanalyst.StatisticsFieldType type) {
		String name = "";
		try {
			if (type == StatisticsFieldType.AVERAGE ){
				name = CoreProperties.getString("String_StatisticsType_MEAN");
			} else if (type == StatisticsFieldType.MAXVALUE) {
				name = CoreProperties.getString("String_StatisticsType_MAX");
			} else if (type == StatisticsFieldType.MINVALUE) {
				name = CoreProperties.getString("String_StatisticsType_MIN");
			}  else if (type == StatisticsFieldType.SAMPLESTDDEV) {
				name = CoreProperties.getString("String_SampleStddev");
			} else if (type == StatisticsFieldType.SAMPLEVARIANCE) {
				name = CoreProperties.getString("String_SampleEvariance");
			} else if (type == StatisticsFieldType.STDDEVIATION) {
				name = CoreProperties.getString("String_GridStatisticsMode_Stdev");
			}else if (type == StatisticsFieldType.SUM) {
				name = CoreProperties.getString("String_StatisticsType_SUM");
			}else if (type == StatisticsFieldType.VARIANCE) {
				name = CoreProperties.getString("String_Evariance");
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
		return name;
	}

	public static StatisticsType getStatisticsType(String statisticsTypeName) {
		StatisticsType type = StatisticsType.FIRST;
		try {
			if (statisticsTypeName.equalsIgnoreCase(CoreProperties.getString("String_StatisticsType_MAX"))) {
				type = StatisticsType.MAX;
			} else if (statisticsTypeName.equalsIgnoreCase(CoreProperties.getString("String_StatisticsType_MIN"))) {
				type = StatisticsType.MIN;
			} else if (statisticsTypeName.equalsIgnoreCase(CoreProperties.getString("String_StatisticsType_MEAN"))) {
				type = StatisticsType.MEAN;
			} else if (statisticsTypeName.equalsIgnoreCase(CoreProperties.getString("String_StatisticsType_MEDIAN"))) {
				type = StatisticsType.MEDIAN;
			} else if (statisticsTypeName.equalsIgnoreCase(CoreProperties.getString("String_StatisticsType_SUM"))) {
				type = StatisticsType.SUM;
			} else if (statisticsTypeName.equalsIgnoreCase(CoreProperties.getString("String_StatisticsType_FIRST"))) {
				type = StatisticsType.FIRST;
			} else if (statisticsTypeName.equalsIgnoreCase(CoreProperties.getString("String_StatisticsType_LAST"))) {
				type = StatisticsType.LAST;
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
		return type;
	}
}

