package com.supermap.desktop.process.meta.metaProcessImplements.spatialStatistics.StatisticsField;

import com.supermap.analyst.spatialstatistics.StatisticsType;
import com.supermap.data.FieldType;
import com.supermap.desktop.controls.ControlsProperties;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

/**
 * Created by hanyz on 2017/5/3.
 */
public class StatisticsFieldTableModel extends DefaultTableModel {
	private ArrayList<StatisticsFieldInfo> statisticsFieldInfos = new ArrayList<>();

	private final String[] columnNames = new String[]{
			ControlsProperties.getString("String_FieldInfoName"),
			ControlsProperties.getString("String_FieldInfoType"),
			ControlsProperties.getString("String_StatisticsType")
	};
	public static final int COLUMN_FIELDNAME = 0;
	public static final int COLUMN_FIELDTYPE = 1;
	public static final int COLUMN_STATISTICSTYPE = 2;
	private boolean isColumnStatisticsTypeEditable = true;

	public ArrayList<StatisticsFieldInfo> getStatisticsFieldInfos() {
		return statisticsFieldInfos;
	}

	public StatisticsFieldTableModel(ArrayList<StatisticsFieldInfo> statisticsFieldInfos) {
		super();
		this.statisticsFieldInfos = statisticsFieldInfos;
	}


	@Override
	public int getRowCount() {
		if (statisticsFieldInfos == null) {
			return 0;
		}
		return statisticsFieldInfos.size();
	}

	public StatisticsFieldInfo getRow(int row) {
		return statisticsFieldInfos.get(row);
	}

	public void addRow(StatisticsFieldInfo rowData) {
		statisticsFieldInfos.add(rowData);
		fireTableRowsInserted(statisticsFieldInfos.size() - 1, statisticsFieldInfos.size() - 1);
	}

	@Override
	public void removeRow(int row) {
		statisticsFieldInfos.remove(row);
		fireTableRowsDeleted(row, row);
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if (column == COLUMN_STATISTICSTYPE) {
			return isColumnStatisticsTypeEditable;
		}
		return false;
	}

	@Override
	public Object getValueAt(int row, int column) {
		if (column == COLUMN_FIELDNAME) {
			return statisticsFieldInfos.get(row).getFieldInfo().getName();
		}
		if (column == COLUMN_FIELDTYPE) {
			return statisticsFieldInfos.get(row).getFieldInfo().getType();
		}
		if (column == COLUMN_STATISTICSTYPE) {
			return statisticsFieldInfos.get(row).getStatisticsType();
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		if (column == COLUMN_STATISTICSTYPE) {
			statisticsFieldInfos.get(row).setStatisticsType((StatisticsType) aValue);
			fireTableCellUpdated(row, column);
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == COLUMN_FIELDNAME) {
			return String.class;
		}
		if (columnIndex == COLUMN_FIELDTYPE) {
			return FieldType.class;
		}
		if (columnIndex == COLUMN_STATISTICSTYPE) {
			return StatisticsType.class;
		}
		return super.getColumnClass(columnIndex);
	}

	public boolean isColumnStatisticsTypeEditable() {
		return isColumnStatisticsTypeEditable;
	}

	public void setColumnStatisticsTypeEditable(boolean columnStatisticsTypeEditable) {
		isColumnStatisticsTypeEditable = columnStatisticsTypeEditable;
	}
}
