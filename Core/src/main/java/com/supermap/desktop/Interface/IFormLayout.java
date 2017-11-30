package com.supermap.desktop.Interface;

import com.supermap.data.Geometry;
import com.supermap.ui.MapLayoutControl;

public interface IFormLayout extends IForm {
	
	/**
	 * 获取布局窗口中的 MapLayoutControl 控件。
	 * @return
	 */
    MapLayoutControl getMapLayoutControl() ;

	/**
	 * Get the first selected object in the layout window selection object
	 * @return
	 */
	Geometry getFirstSelectedGeometry();
}
