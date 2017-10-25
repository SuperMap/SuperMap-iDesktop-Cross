package com.supermap.desktop.ui.controls.prjcoordsys.prjCoordSysSettingPanels;

/**
 * Created by yuanR on 2017/10/24 0024.
 */

import com.supermap.desktop.Application;

/**
 * 选中节点之后，在 Table 上展示对应数据的 Model
 *
 * @author highsad
 */
public class PrjCoordSysTableModel extends AbstractPrjTableModel {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private transient CoordSysDefine define;

	@Override
	public CoordSysDefine getDefine() {
		return this.define;
	}

	public PrjCoordSysTableModel() {
		// do nothing
	}

	@Override
	public void setDefine(CoordSysDefine define) {
		this.define = define;
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		if (this.define == null) {
			return 0;
		}

		// 如果没有子项，则表明是坐标系定义，在列表中展示它本身
		if (this.define.size() == 0) {
			return 1;
		} else {
			return this.define.size();
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (this.define == null) {
			return null;
		}

		CoordSysDefine item = this.define;
		if (this.define.size() > 0) {
			item = this.define.get(rowIndex);
		}

		if (columnIndex == CAPTION) {
			return item.getCaption();
		} else if (columnIndex == TYPE) {
			return item.getCoordSysTypeDescription();
		} else if (columnIndex == GROUP) {
			return item.getParent().getCaption();
		} else {
			return null;
		}
	}

	@Override
	public CoordSysDefine getRowData(int row) {
		CoordSysDefine result = null;

		try {
			if (this.define == null) {
				return null;
			}

			if (this.define.size() == 0 && row == 0) {
				result = this.define;
			} else if (this.define.size() > 0 && 0 <= row && row < this.define.size()) {
				result = this.define.get(row);
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
		return result;
	}
}
