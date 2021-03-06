package com.supermap.desktop.ui.controls.prjcoordsys;

import com.supermap.data.Enum;
import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.ui.controls.comboBox.SearchItemValueGetter;
import com.supermap.desktop.ui.controls.prjcoordsys.prjCoordSysSettingPanels.CoordSysDefine;
import com.supermap.desktop.utilities.PrjCoordSysTypeUtilities;
import com.supermap.desktop.utilities.PrjCoordSysUtilities;

import javax.swing.*;
import java.text.MessageFormat;

public class PrjCoordSysSettingsUtilties {
	private static ListCellRenderer<Enum> enumComboBoxItemRender = null;
	private static SearchItemValueGetter<Enum> searchItemValueGetter = null;

	/**
	 * 根据投影定义获取对应的投影对象
	 *
	 * @return
	 */
	private PrjCoordSysSettingsUtilties() {
		// 工具类不提供构造函数
	}

	public static PrjCoordSys getPrjCoordSys(CoordSysDefine define) {
		PrjCoordSys result = null;
		// 没有子项，则为具体的投影定义
		if (define.size() == 0 && define.getCoordSysType() == CoordSysDefine.PROJECTION_SYSTEM) {
			if (define.getCoordSysCode() != -1) {
				try {
					PrjCoordSysType type = (PrjCoordSysType) Enum.parse(PrjCoordSysType.class, define.getCoordSysCode());
					if (type != null) {
						result = new PrjCoordSys(type);
					}
				} catch (Exception e) {
					//Enum值错误 不抛异常
					Application.getActiveApplication().getOutput().output(e.getMessage());
					e.printStackTrace();
				}
			} else {
				result = define.getPrjCoordSys();
			}
		}
		return result;
	}

	/**
	 * 根据投影定义获取对应的地理坐标系对象
	 *
	 * @param define
	 * @return
	 */
	public static GeoCoordSys getGeoCoordSys(CoordSysDefine define) {
		GeoCoordSys result = null;
		if (define.size() == 0 && define.getCoordSysType() == CoordSysDefine.GEOGRAPHY_COORDINATE) {
			if (define.getCoordSysCode() != -1) {
				try {
					GeoCoordSysType type = (GeoCoordSysType) Enum.parse(GeoCoordSysType.class, define.getCoordSysCode());
					if (type != null) {
						result = new GeoCoordSys(type, GeoSpatialRefType.SPATIALREF_EARTH_LONGITUDE_LATITUDE);
					}
				} catch (Exception e) {
					Application.getActiveApplication().getOutput().output(e);
				}
			} else {
				result = define.getGeoCoordSys();
			}
		}
		return result;
	}

	/**
	 * 获取指定投影定义的详细描述信息
	 *
	 * @param define
	 * @return
	 */

	public static String getDescription(CoordSysDefine define) {
		String description = "";

		try {
			if (define != null) {
				if (define.size() == 0) {
					// 如果当前选中的投影定义没有子项，那么就是具体的投影
					PrjCoordSys prjCoordSys = null;
					if (define.getCoordSysType() == CoordSysDefine.NONE_EARTH) {
						prjCoordSys = new PrjCoordSys(PrjCoordSysType.PCS_NON_EARTH);
						prjCoordSys.setCoordUnit((Unit) Enum.parse(Unit.class, define.getCoordSysCode()));
					} else if (define.getCoordSysType() == CoordSysDefine.PROJECTION_SYSTEM) {
						prjCoordSys = PrjCoordSysSettingsUtilties.getPrjCoordSys(define);
						// 确保描述中坐标系名称和JTable中显示一致
						prjCoordSys.setName(define.getCaption());
					} else if (define.getCoordSysType() == CoordSysDefine.GEOGRAPHY_COORDINATE) {
						GeoCoordSys geoCoordSys = PrjCoordSysSettingsUtilties.getGeoCoordSys(define);
						prjCoordSys = new PrjCoordSys(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
						prjCoordSys.setGeoCoordSys(geoCoordSys);
						// 确保描述中坐标系名称和JTable中显示一致
						prjCoordSys.setName(define.getCaption());
					}

					if (prjCoordSys != null) {
						description = PrjCoordSysUtilities.getDescription(prjCoordSys);
					}
				} else {
					// 如果当前选中的投影定义有子项，那么就是某一类投影的集合
					description = define.getCaption() + System.lineSeparator()
							+ MessageFormat.format(ControlsProperties.getString("String_ObjectsCount"), define.size());
				}
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
		return description;
	}


	public static SearchItemValueGetter<Enum> getSearchItemValueGetter() {
		if (searchItemValueGetter == null) {
			searchItemValueGetter = new SearchItemValueGetter<Enum>() {
				@Override
				public String getSearchString(Enum item) {
					return PrjCoordSysTypeUtilities.getDescribe(item.name());
				}
			};
		}
		return searchItemValueGetter;
	}
}
