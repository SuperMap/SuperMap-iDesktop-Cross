package com.supermap.desktop.ui.controls.prjcoordsys.prjCoordSysSettingPanels;

/**
 * Created by yuanR on 2017/10/24 0024.
 */

import com.supermap.data.GeoCoordSys;
import com.supermap.data.PrjCoordSys;
import com.supermap.desktop.Application;
import com.supermap.desktop.controls.ControlsProperties;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 描述一个坐标系
 *
 * @author highsad
 * 坐标系定义类
 */
public class CoordSysDefine {

	public static final int USER_DEFINED = -1; // 用户自定义
	public static final int PROJECTION_SYSTEM = -2; // 投影坐标系统
	public static final int NONE_ERRTH = -3; // 平面坐标系
	public static final int GEOGRAPHY_COORDINATE = -4; // 地理坐标系
	public static final int FAVORITE_COORDINATE = -5; // 收藏夹
	public static final int CUSTOM_COORDINATE = -6; // 自定义

	private CoordSysDefine parent;
	private String caption = "";

	public void setCoordSysType(int coordSysType) {
		this.coordSysType = coordSysType;
	}

	private int coordSysType = PROJECTION_SYSTEM; // 坐标系类型
	private int coordSysCode = -1; // 默认坐标系代码
	private ArrayList<CoordSysDefine> children = new ArrayList<>();

	private PrjCoordSys prjCoordSys = null;
	private GeoCoordSys geoCoordSys = null;

	public CoordSysDefine(int coordSysType) {
		this.coordSysType = coordSysType;
	}

	public CoordSysDefine(int coordSysType, CoordSysDefine parent) {
		this(coordSysType);
		if (parent != null) {
			parent.add(this);
		}
	}

	public CoordSysDefine(int coordSysType, CoordSysDefine parent, String caption) {
		this(coordSysType, parent);
		this.caption = caption;
	}

	public CoordSysDefine getParent() {
		return this.parent;
	}

	private void setParent(CoordSysDefine parent) {
		this.parent = parent;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public int getCoordSysType() {
		return coordSysType;
	}

	public int getCoordSysCode() {
		return coordSysCode;
	}

	public void setCoordSysCode(int coordSysCode) {
		this.coordSysCode = coordSysCode;
	}

	public CoordSysDefine get(int index) {
		return this.children.get(index);
	}

	public boolean add(CoordSysDefine child) {
		boolean result = false;

		try {
			if (!this.children.contains(child)) {
				result = this.children.add(child);
				child.setParent(this);
			}
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	public boolean remove(CoordSysDefine child) {
		boolean result = false;

		try {
			if (this.children.contains(child)) {
				result = this.children.remove(child);
				child.setParent(null);
			}
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	public int size() {
		return this.children.size();
	}

	public CoordSysDefine getChildByCaption(String caption) {
		CoordSysDefine result = null;

		try {
			for (CoordSysDefine coordSysDefine : children) {
				if (coordSysDefine.getCaption().equals(caption)) {
					result = coordSysDefine;
				} else {
					result = coordSysDefine.getChildByCaption(caption);
				}

				if (result != null) {
					break;
				}
			}
		} catch (Exception e) {
			result = null;
		}
		return result;
	}

	public CoordSysDefine getChildByCoordSysCode(int coordSysCode) {
		CoordSysDefine result = null;

		try {
			for (CoordSysDefine coordSysDefine : children) {
				if (coordSysDefine.getCoordSysCode() == coordSysCode) {
					result = coordSysDefine;
				} else {
					result = coordSysDefine.getChildByCoordSysCode(coordSysCode);
				}

				if (result != null) {
					break;
				}
			}
		} catch (Exception e) {
			result = null;
		}
		return result;
	}

	public String getCoordSysTypeDescription() {
		String result = "";
		if (this.coordSysType == PROJECTION_SYSTEM) {
			result = ControlsProperties.getString("String_PrjCoorSys");
		} else if (this.coordSysType == NONE_ERRTH) {
			result = ControlsProperties.getString("String_NoneEarth");
		} else if (this.coordSysType == GEOGRAPHY_COORDINATE) {
			result = ControlsProperties.getString("String_GeoCoordSys");
		}
		return result;
	}

	public GeoCoordSys getGeoCoordSys() {
		return geoCoordSys;
	}

	public void setGeoCoordSys(GeoCoordSys geoCoordSys) {
		this.geoCoordSys = geoCoordSys;
	}

	public PrjCoordSys getPrjCoordSys() {
		return prjCoordSys;
	}

	public void setPrjCoordSys(PrjCoordSys prjCoordSys) {
		this.prjCoordSys = prjCoordSys;
	}

	/**
	 * 获取该投影定义下所有的叶子节点（也就是所有的投影）
	 *
	 * @return
	 */
	public CoordSysDefine[] getAllLeaves() {
		ArrayList<CoordSysDefine> list = new ArrayList<>();

		try {
			if (this.children.isEmpty()) {
				list.add(this);
			} else {
				for (CoordSysDefine aChildren : this.children) {
					CoordSysDefine[] leaves = aChildren.getAllLeaves();
					Collections.addAll(list, leaves);
				}
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
		return list.toArray(new CoordSysDefine[list.size()]);
	}

	@Override
	public String toString() {
		return this.caption;
	}

	public void dispose() {
		for (CoordSysDefine child : children) {
			child.dispose();
		}
		if (this.geoCoordSys != null) {
			this.geoCoordSys.dispose();
		}
		if (this.prjCoordSys != null) {
			this.prjCoordSys.dispose();
		}
	}
}
