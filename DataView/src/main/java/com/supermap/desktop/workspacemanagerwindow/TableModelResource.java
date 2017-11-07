package com.supermap.desktop.workspacemanagerwindow;

import com.supermap.desktop.Application;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.properties.CoreProperties;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import static com.supermap.desktop.workspacemanagerwindow.WorkspaceManagerWindowResources.COLUMN_NAME;
import static com.supermap.desktop.workspacemanagerwindow.WorkspaceManagerWindowResources.COLUMN_TYPE;

/**
 * @author YuanR
 */
public class TableModelResource extends AbstractTableModel {
	@Override
	public String getColumnName(int column) {
		if (column == COLUMN_NAME) {
			return CoreProperties.getString("String_Name");
		} else if (column == COLUMN_TYPE) {
			return CoreProperties.getString("String_Type");
		}
		return "";
	}

	@Override
	public int getRowCount() {
		//资源行数暂定为3
		return 3;
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (row == 0 && col == COLUMN_TYPE) {
			return ControlsProperties.getString("SymbolMarkerLibNodeName");
		} else if (row == 1 && col == COLUMN_TYPE) {
			return ControlsProperties.getString("SymbolLineLibNodeName");
		} else if (row == 2 &&  col == COLUMN_TYPE) {
			return ControlsProperties.getString("SymbolFillLibNodeName");
		}

		if (row == 0 && col == COLUMN_NAME ) {
			return Application.getActiveApplication().getWorkspace().getResources().getMarkerLibrary();
		} else if (row == 1 && col == COLUMN_NAME ) {
			return Application.getActiveApplication().getWorkspace().getResources().getLineLibrary();
		} else if (row == 2 && col == COLUMN_NAME ) {
			return Application.getActiveApplication().getWorkspace().getResources().getFillLibrary();
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


