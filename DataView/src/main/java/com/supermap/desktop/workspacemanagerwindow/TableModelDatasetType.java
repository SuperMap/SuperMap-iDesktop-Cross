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
public class TableModelDatasetType extends AbstractTableModel {
	private Datasource datasource;
	private DatasetType datasetType;
	private DatasetVector datasetVector;
	private DatasetGrid datasetGrid;
	private DatasetImage datasetImage;
	private DatasetGridCollection datasetGridCollection;
	private DatasetImageCollection datasetImageCollection;

	private int sum = 0;
	private Dataset[] aimDataset;

	//获得工作空间以及列名
	public TableModelDatasetType(Datasource datasource, DatasetType dataType) {
		this.datasetType = dataType;
		this.datasource = datasource;
		//预处理
		for (int i = 0; i < this.datasource.getDatasets().getCount(); i++) {
			if ((this.datasource.getDatasets().get(i).getType()).equals(this.datasetType)) {
				sum++;
			}
		}
		this.aimDataset = new Dataset[sum];
		int m = 0;
		for (int i = 0; i < this.datasource.getDatasets().getCount(); i++) {
			if ((this.datasource.getDatasets().get(i).getType()).equals(this.datasetType)) {
				this.aimDataset[m] = this.datasource.getDatasets().get(i);
				m++;
			}
		}
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
		return sum;
	}

	public int getColumnCount() {
		return 5;
	}

	@Override
	public Object getValueAt(int row, int col) {
		//增加">0"判断，防止索引越界
		if (sum > 0) {
			if (col == COLUMN_NAME) {
				return this.aimDataset[row];
			}
			if (col == COLUMN_TYPE) {
				String replaceString = DataViewProperties.getString("String_Dataset_T");
				String datasetTypeName = CommonToolkit.DatasetTypeWrap.findName(this.datasetType);
				String newDatasetTypeName = datasetTypeName.replace(replaceString, "");
				return newDatasetTypeName;
			}
			if (col == COLUMN_NUMBER) {
				if (this.aimDataset[row] instanceof DatasetVector) {
					this.datasetVector = (DatasetVector) this.aimDataset[row];
					return this.datasetVector.getRecordCount();
				} else if (this.aimDataset[row] instanceof DatasetGrid) {
					this.datasetGrid = (DatasetGrid) this.aimDataset[row];
					return this.datasetGrid.getWidth() * this.datasetGrid.getHeight();
				} else if (this.aimDataset[row] instanceof DatasetImage) {
					this.datasetImage = (DatasetImage) this.aimDataset[row];
					return this.datasetImage.getWidth() * this.datasetImage.getHeight();
				} else if (this.aimDataset[row] instanceof DatasetGridCollection) {
					this.datasetGridCollection = (DatasetGridCollection) this.aimDataset[row];
					return this.datasetGridCollection.getCount();
				} else if (this.aimDataset[row] instanceof DatasetImageCollection) {
					this.datasetImageCollection = (DatasetImageCollection) this.aimDataset[row];
					return this.datasetImageCollection.getCount();
				} else {
					return 0;
				}
			}
			if (col == COLUMN_PRJCOORDSYS) {
				return this.aimDataset[row].getPrjCoordSys().getName();
			}
			//取巧，
			//将栅格/图片的像素数存在第五列，并隐藏，
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
