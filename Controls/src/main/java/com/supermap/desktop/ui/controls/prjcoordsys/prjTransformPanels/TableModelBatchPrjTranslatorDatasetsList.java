package com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels;

import com.supermap.data.Dataset;
import com.supermap.data.Datasource;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.DataCell;
import com.supermap.desktop.ui.controls.smTables.IModel;
import com.supermap.desktop.ui.controls.smTables.IModelController;
import com.supermap.desktop.ui.controls.smTables.ModelControllerAdapter;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

/**
 * Created by yuanR on 2017/10/11 0011.
 * 批量投影转换数据集列表table'modle
 */
public class TableModelBatchPrjTranslatorDatasetsList extends DefaultTableModel implements IModel {

	public ArrayList<TableData> getDataList() {
		return dataList;
	}

	private ArrayList<TableData> dataList = new ArrayList<>();
	private String[] columnNames = new String[]{
			"",
			CoreProperties.getString(CoreProperties.SourceDataset),
			CoreProperties.getString(CoreProperties.TargetDataset),
	};


	private static final int TABLE_COLUMN_ISSELECTED = 0;
	private static final int TABLE_COLUMN_SOURCEDATASET = 1;
	private static final int TABLE_COLUMN_TARGETDATASETNAME = 2;

	@Override
	public int getRowCount() {
		return dataList == null ? 0 : dataList.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if (column == TABLE_COLUMN_ISSELECTED || column == TABLE_COLUMN_TARGETDATASETNAME) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Object getValueAt(int row, int column) {
		switch (column) {
			case TABLE_COLUMN_ISSELECTED:
				return dataList.get(row).isSelected();
			case TABLE_COLUMN_SOURCEDATASET:
				return dataList.get(row).getDataset();
			case TABLE_COLUMN_TARGETDATASETNAME:
				return dataList.get(row).getResultDatasetName();
		}
		return super.getValueAt(row, column);
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		if (column == TABLE_COLUMN_ISSELECTED) {
			dataList.get(row).setSelected((Boolean) aValue);
		} else if (column == TABLE_COLUMN_TARGETDATASETNAME) {
			if (null != dataList.get(row).getTargetDatasource()) {
				dataList.get(row).setResultDatasetName(dataList.get(row).getTargetDatasource().getDatasets().getAvailableDatasetName(aValue.toString()));
			}
		}
		fireTableDataChanged();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
			case TABLE_COLUMN_ISSELECTED:
				return Boolean.class;
			case TABLE_COLUMN_SOURCEDATASET:
				return DataCell.class;
			case TABLE_COLUMN_TARGETDATASETNAME:
				return String.class;
		}
		return super.getColumnClass(columnIndex);
	}

	public void setDataList(ArrayList<Dataset> datasets, Datasource targetDatasource) {
		this.dataList.clear();
		if (datasets != null && datasets.size() > 0) {
			if (targetDatasource != null) {
				for (int i = 0; i < datasets.size(); i++) {
					dataList.add(new TableData(true, datasets.get(i), targetDatasource.getDatasets().getAvailableDatasetName(datasets.get(i).getName()), targetDatasource));
				}
			} else {
				for (int i = 0; i < datasets.size(); i++) {
					dataList.add(new TableData(true, datasets.get(i), datasets.get(i).getName(), null));
				}
			}

		}
		fireTableDataChanged();
	}

	public void updataDataList(Datasource targetDatasource) {
		if (targetDatasource != null) {
			for (int i = 0; i < dataList.size(); i++) {
				dataList.get(i).setResultDatasetName(targetDatasource.getDatasets().getAvailableDatasetName(dataList.get(i).getDataset().getName()));
				dataList.get(i).setTargetDatasource(targetDatasource);
			}
		}
		fireTableDataChanged();
	}


	@Override
	public IModelController getModelController() {
		return this.modelController;
	}


	private IModelController modelController = new ModelControllerAdapter() {
		@Override
		public void selectAllOrNull(boolean value) {
			for (int i = 0; i < getRowCount(); i++) {
				setValueAt(value, i, 0);
			}
		}
	};

	public class TableData {
		public boolean isSelected() {
			return isSelected;
		}

		public void setSelected(boolean selected) {
			isSelected = selected;
		}

		public Dataset getDataset() {
			return dataset;
		}

		public String getResultDatasetName() {
			return resultDatasetName;
		}

		public void setResultDatasetName(String resultDatasetName) {
			this.resultDatasetName = resultDatasetName;
		}

		public Datasource getTargetDatasource() {
			return targetDatasource;
		}

		public void setTargetDatasource(Datasource targetDatasource) {
			this.targetDatasource = targetDatasource;
		}

		boolean isSelected = true;
		Dataset dataset;
		String resultDatasetName;
		Datasource targetDatasource;

		TableData(Boolean isSelected, Dataset dataset, String resultDatasetName, Datasource targetDatasource) {
			this.isSelected = isSelected;
			this.dataset = dataset;
			this.resultDatasetName = resultDatasetName;
			this.targetDatasource = targetDatasource;
		}
	}
}

