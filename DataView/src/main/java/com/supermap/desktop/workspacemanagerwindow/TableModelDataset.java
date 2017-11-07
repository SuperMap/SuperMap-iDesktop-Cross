package com.supermap.desktop.workspacemanagerwindow;

import com.supermap.data.*;
import com.supermap.desktop.CommonToolkit;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.dataview.DataViewProperties;
import com.supermap.desktop.properties.CoreProperties;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import static com.supermap.desktop.workspacemanagerwindow.WorkspaceManagerWindowResources.*;

/**
 * @author YuanR
 */
public class TableModelDataset extends AbstractTableModel {
	private Datasource datasource;
	private DatasetVector datasetVector;
	private DatasetGrid datasetGrid;
	private DatasetImage datasetImage;
	private DatasetGridCollection datasetGridCollection;
	private DatasetImageCollection datasetImageCollection;


	//获得工作空间以及列名
	public TableModelDataset(Datasource datasource) {
		this.datasource = datasource;
	}

	@Override
	public String getColumnName(int column) {
		if (column == COLUMN_NAME) {
			return CoreProperties.getString("String_Name");
		} else if (column == COLUMN_TYPE) {
			return CoreProperties.getString("String_Type");
		} else if (column == COLUMN_NUMBER) {
			return DataViewProperties.getString("String_ObjectCount");
		} else if (column == COLUMN_PRJCOORDSYS) {
			return ControlsProperties.getString("String_PrjCoorSys");
		}
		return "";
	}

	@Override
	public int getRowCount() {
		return this.datasource.getDatasets().getCount();
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public Object getValueAt(int row, int col) {
		//增加">0"判断，防止索引越界
		if (this.datasource.getDatasets().getCount() > 0) {
			if (col == COLUMN_NAME) {
				return this.datasource.getDatasets().get(row);
			}
			if (col == COLUMN_TYPE) {
				String replaceString = DataViewProperties.getString("String_Dataset_T");
				String datasetTypeName = CommonToolkit.DatasetTypeWrap.findName(this.datasource.getDatasets().get(row).getType());
				String newDatasetTypeName = datasetTypeName.replace(replaceString, "");
				return newDatasetTypeName;
			}
			if (col == COLUMN_NUMBER) {
				if (this.datasource.getDatasets().get(row) instanceof DatasetVector) {
					this.datasetVector = (DatasetVector) this.datasource.getDatasets().get(row);
					return this.datasetVector.getRecordCount();
				} else if (this.datasource.getDatasets().get(row) instanceof DatasetGrid) {
					this.datasetGrid = (DatasetGrid) this.datasource.getDatasets().get(row);
					return this.datasetGrid.getWidth() * this.datasetGrid.getHeight();
				} else if (this.datasource.getDatasets().get(row) instanceof DatasetImage) {
					this.datasetImage = (DatasetImage) this.datasource.getDatasets().get(row);
					return this.datasetImage.getWidth() * this.datasetImage.getHeight();
				} else if (this.datasource.getDatasets().get(row) instanceof DatasetGridCollection) {
					this.datasetGridCollection = (DatasetGridCollection) this.datasource.getDatasets().get(row);
					return this.datasetGridCollection.getCount();
				} else if (this.datasource.getDatasets().get(row) instanceof DatasetImageCollection) {
					this.datasetImageCollection = (DatasetImageCollection) this.datasource.getDatasets().get(row);
					return this.datasetImageCollection.getCount();
				} else {
					return 0;
				}
			}
			if (col == COLUMN_PRJCOORDSYS) {
				return this.datasource.getDatasets().get(row).getPrjCoordSys().getName();
			}
			//将栅格/图片的像素数存在第五列
			if (col == COLUMN_NULL) {
				if (this.datasource.getDatasets().get(row) instanceof DatasetGrid) {
					this.datasetGrid = (DatasetGrid) this.datasource.getDatasets().get(row);
					return this.datasetGrid.getWidth();
				} else if (this.datasource.getDatasets().get(row) instanceof DatasetImage) {
					this.datasetImage = (DatasetImage) this.datasource.getDatasets().get(row);
					return this.datasetImage.getWidth();
				} else {
					return 0;
				}
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



