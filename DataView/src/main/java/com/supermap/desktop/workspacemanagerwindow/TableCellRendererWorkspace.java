package com.supermap.desktop.workspacemanagerwindow;

import com.supermap.data.*;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.dataview.DataViewResources;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

import static com.supermap.desktop.workspacemanagerwindow.WorkspaceManagerWindowResources.*;

/**
 * @author YuanR
 */
public class TableCellRendererWorkspace extends DefaultTableCellRenderer {
	public Component getTableCellRendererComponent(JTable table, Object value,
	                                               boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		//给第一列设置图标/显示字符串
		if (value instanceof Datasources) {
			this.setIcon(DataViewResources.getIcon(DATASOURCES_ICON_PATH));
			this.setText(ControlsProperties.getString("String_Datasource"));
		}
		if (value instanceof Maps) {
			this.setIcon(DataViewResources.getIcon(MAPS_ICON_PATH));
			this.setText(ControlsProperties.getString("String_Maps"));
		}
		if (value instanceof Scenes) {
			this.setIcon(DataViewResources.getIcon(SCENES_ICON_PATH));
			this.setText(ControlsProperties.getString("String_Scenes"));
		}
		if (value instanceof Layouts) {
			this.setIcon(DataViewResources.getIcon(LAYOUTS_ICON_PATH));
			this.setText(ControlsProperties.getString("String_Layouts"));
		}
		if (value instanceof Resources) {
			this.setIcon(DataViewResources.getIcon(RESOURCES_ICON_PATH));
			this.setText(ControlsProperties.getString("String_Resources"));

		}
		if (column == COLUMN_NUMBER) {
			//靠左对齐
			this.setHorizontalAlignment(LEFT);
		}
		return this;
	}
}
