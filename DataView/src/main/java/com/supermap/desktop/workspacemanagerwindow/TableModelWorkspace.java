package com.supermap.desktop.workspacemanagerwindow;

import com.supermap.data.Workspace;
import com.supermap.desktop.dataview.DataViewProperties;
import com.supermap.desktop.properties.CoreProperties;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import static com.supermap.desktop.workspacemanagerwindow.WorkspaceManagerWindowResources.*;

/**
 * @author YuanR
 */
public class TableModelWorkspace extends AbstractTableModel {
	private Workspace workspace;

	//获得工作空间以及列名
	public TableModelWorkspace(Workspace workspace) {
		this.workspace = workspace;
	}

	@Override
	public String getColumnName(int column) {
		if (column == COLUMN_NAME) {
			return CoreProperties.getString("String_Name");
		} else if (column == COLUMN_TYPE) {
			return CoreProperties.getString("String_Type");
		} else if (column == COLUMN_NUMBER) {
			return DataViewProperties.getString("String_ObjectCount");
		}
		return "";
	}

	@Override
	public int getRowCount() {
		//工作空间行数暂定为5（数据源、地图、场景、资源）
		return 4;
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (row == 0) {
			if (col == COLUMN_NAME) {
				return this.workspace.getDatasources();
			} else if (col == COLUMN_TYPE) {
				return DataViewProperties.getString("String_DatasourceType");
			} else if (col == COLUMN_NUMBER) {
				return this.workspace.getDatasources().getCount();
			}
		}
		if (row == 1) {
			if (col == COLUMN_NAME) {
				return this.workspace.getMaps();
			} else if (col == COLUMN_TYPE) {
				return DataViewProperties.getString("String_MapType");
			} else if (col == COLUMN_NUMBER) {
				return this.workspace.getMaps().getCount();
			}
		}
		if (row == 2) {
			if (col == COLUMN_NAME) {
				return this.workspace.getScenes();
			} else if (col == COLUMN_TYPE) {
				return DataViewProperties.getString("String_SceneType");
			} else if (col == COLUMN_NUMBER) {
				return this.workspace.getScenes().getCount();
			}
		}
		if (row == 3) {
			if (col == COLUMN_NAME) {
				return this.workspace.getResources();
			} else if (col == COLUMN_TYPE) {
				return DataViewProperties.getString("String_ResourceType");
			} else if (col == COLUMN_NUMBER) {
				return 3;
			}
		}
		return "";
	}

	public Class getColumnClass(int col) {
		if (col == COLUMN_NAME) {
			return Icon.class;
		} else {
			return getValueAt(0, col).getClass();
		}
	}
}

