package com.supermap.desktop.utilities;

import com.supermap.analyst.spatialanalyst.GridStatisticsMode;
import com.supermap.desktop.Application;
import com.supermap.desktop.properties.CoreProperties;

/**
 * Created By Chens on 2017/8/15 0015
 */
public class GridStatisticsModeUtilities {
	public GridStatisticsModeUtilities() {
	}

	public static String getGridStatisticsModeName(GridStatisticsMode mode) {
		String name = "";
		try {
			if (mode == GridStatisticsMode.MAX) {
				name = CoreProperties.getString("String_StatisticsType_MAX");
			} else if (mode == GridStatisticsMode.MIN) {
				name = CoreProperties.getString("String_StatisticsType_MIN");
			} else if (mode == GridStatisticsMode.MEAN) {
				name = CoreProperties.getString("String_StatisticsType_MEAN");
			} else if (mode == GridStatisticsMode.MEDIAN) {
				name = CoreProperties.getString("String_StatisticsType_MEDIAN");
			} else if (mode == GridStatisticsMode.SUM) {
				name = CoreProperties.getString("String_GridStatisticsMode_Sum");
			} else if (mode == GridStatisticsMode.MAJORITY) {
				name = CoreProperties.getString("String_TerrainStatisticType_Majority");
			} else if (mode == GridStatisticsMode.MINORITY) {
				name = CoreProperties.getString("String_GridStatisticsMode_Minority");
			} else if (mode == GridStatisticsMode.VARIETY) {
				name = CoreProperties.getString("String_GridStatisticsMode_Variety");
			} else if (mode == GridStatisticsMode.STDEV) {
				name = CoreProperties.getString("String_GridStatisticsMode_Stdev");
			} else if (mode == GridStatisticsMode.RANGE) {
				name = CoreProperties.getString("String_GridStatisticsMode_Range");
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
		return name;
	}

	public static GridStatisticsMode getGridStatisticsMode(String name) {
		GridStatisticsMode type = GridStatisticsMode.MAX;
		try {
			if (name.equalsIgnoreCase(CoreProperties.getString("String_StatisticsType_MAX"))) {
				type = GridStatisticsMode.MAX;
			} else if (name.equalsIgnoreCase(CoreProperties.getString("String_StatisticsType_MIN"))) {
				type = GridStatisticsMode.MIN;
			} else if (name.equalsIgnoreCase(CoreProperties.getString("String_StatisticsType_MEAN"))) {
				type = GridStatisticsMode.MEAN;
			} else if (name.equalsIgnoreCase(CoreProperties.getString("String_StatisticsType_MEDIAN"))) {
				type = GridStatisticsMode.MEDIAN;
			} else if (name.equalsIgnoreCase(CoreProperties.getString("String_GridStatisticsMode_Sum"))) {
				type = GridStatisticsMode.SUM;
			} else if (name.equalsIgnoreCase(CoreProperties.getString("String_TerrainStatisticType_Majority"))) {
				type = GridStatisticsMode.MAJORITY;
			} else if (name.equalsIgnoreCase(CoreProperties.getString("String_StatisticsType_Minority"))) {
				type = GridStatisticsMode.MINORITY;
			} else if (name.equalsIgnoreCase(CoreProperties.getString("String_StatisticsType_Variety"))) {
				type = GridStatisticsMode.VARIETY;
			} else if (name.equalsIgnoreCase(CoreProperties.getString("String_StatisticsType_Stdev"))) {
				type = GridStatisticsMode.STDEV;
			} else if (name.equalsIgnoreCase(CoreProperties.getString("String_StatisticsType_Range"))) {
				type = GridStatisticsMode.RANGE;
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
		return type;
	}
}
