package com.supermap.desktop.ui.controls.prjcoordsys.prjCoordSysSettingPanels;

/**
 * Created by yuanR on 2017/10/24 0024.
 */

import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.properties.CoreProperties;

import javax.swing.table.AbstractTableModel;

/**
 * 投影信息 TableModel 的抽象基类，搜索结果的 Model 和 正常展示的 Model 各自有不同的实现。
 *
 * @author yuanR2017.10.24
 */
public abstract class AbstractPrjTableModel extends AbstractTableModel {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	protected static final int CAPTION = 0;
	protected static final int TYPE = 1;
	protected static final int GROUP = 2;

	private transient CoordSysDefine define;

	public AbstractPrjTableModel() {

	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public String getColumnName(int column) {
		if (column == CAPTION) {
			return CoreProperties.getString(CoreProperties.Name);
		} else if (column == TYPE) {
			return CoreProperties.getString(CoreProperties.Type);
		} else if (column == GROUP) {
			return ControlsProperties.getString("String_BelongToGroup");
		} else {
			return null;
		}
	}

	public abstract CoordSysDefine getRowData(int row);

	public CoordSysDefine getDefine() {
		return define;
	}

	public void setDefine(CoordSysDefine define) {
		this.define = define;
	}
}

