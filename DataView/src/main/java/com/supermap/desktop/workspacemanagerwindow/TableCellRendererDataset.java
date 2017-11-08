package com.supermap.desktop.workspacemanagerwindow;

import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.desktop.CommonToolkit;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.dataview.DataViewResources;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

import static com.supermap.desktop.workspacemanagerwindow.WorkspaceManagerWindowResources.*;


/**
 * @author YuanR
 *         添加数据集图标,类型列用中文显示
 */

public class TableCellRendererDataset extends DefaultTableCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object value,
	                                               boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);

		//根据数据集类型添加图标
		if (column == COLUMN_NAME) {
			String dataTypeString = table.getValueAt(row, COLUMN_TYPE) + ControlsProperties.getString("String_Dataset");
			DatasetType datasetType = CommonToolkit.DatasetTypeWrap.findType(dataTypeString);
			this.setIcon(DataViewResources.getIcon(DATAVIEW_ICON_ROOTPATH + datasetType + ".png"));
			// 设置其显示为数据集名称
			this.setText(((Dataset) value).getName());
		}


		if (column == COLUMN_NUMBER) {
			if (table.getValueAt(row, COLUMN_TYPE).equals(ControlsProperties.getString("String_Grid"))) {
				String widthGrid = String.valueOf(table.getValueAt(row, COLUMN_NULL));
				String heightGrid = String.valueOf((Integer) value / (Integer) (table.getValueAt(row, COLUMN_NULL)));
				this.setText(heightGrid + "*" + widthGrid);
			}
			if (table.getValueAt(row, COLUMN_TYPE).equals(ControlsProperties.getString("String_ImageProperty"))) {
				String widthImage = String.valueOf(table.getValueAt(row, COLUMN_NULL));
				String heightImage = String.valueOf((Integer) value / (Integer) (table.getValueAt(row, COLUMN_NULL)));
				this.setText(heightImage + "*" + widthImage);
			}
			this.setHorizontalAlignment(LEFT);
		}
		//第五列存入栅格，影像数据的一个像素值，但不做显示
		if (column == COLUMN_NULL) {
			this.setText("");
		}
		return this;
	}
}
