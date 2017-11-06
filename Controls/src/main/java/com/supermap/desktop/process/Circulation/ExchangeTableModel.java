package com.supermap.desktop.process.Circulation;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

/**
 * Created by xie on 2017/11/3.
 */
public class ExchangeTableModel<T extends Object> extends DefaultTableModel {
	private String[] title;
	private boolean[] editable;
	private ArrayList<T> info;

	public ExchangeTableModel() {

	}

	@Override
	public int getRowCount() {
		return info == null ? 0 : info.size();
	}

	@Override
	public int getColumnCount() {
		return title.length;
	}

	@Override
	public Object getValueAt(int row, int column) {
		if (column == 0) {
			return row + 1;
		} else {
			return info.get(row);
		}
	}

	public void addRow(T t) {
		info.add(t);
		fireTableDataChanged();
	}

	public void moveTop(int... rows) {
		int index = rows[0];
		for (int row : rows) {
			moveTo(row, row - index);
		}
		fireTableDataChanged();
	}

	public void moveBottom(int... rows) {
		int index = getRowCount() - rows[rows.length - 1];
		for (int i = rows.length - 1; i >= 0; i--) {
			moveTo(rows[i], rows[i] + index);
		}
		fireTableDataChanged();
	}

	public void moveUp(int... rows) {
		for (int row : rows) {
			moveTo(row, row - 1);
		}
		fireTableDataChanged();
	}

	public void moveDown(int... rows) {
		for (int i = rows.length - 1; i >= 0; i--) {
			moveTo(rows[i], rows[i] + 1);
		}
		fireTableDataChanged();
	}

	private void moveTo(int srcRow, int targetRow) {
		if (srcRow > targetRow) {
			for (int i = srcRow; i > targetRow; i--) {
				swap(i, i - 1);
			}
		} else if (srcRow < targetRow) {
			for (int i = srcRow; i < targetRow; i++) {
				swap(i, i + 1);
			}
		}
	}

	private void swap(int i, int i1) {
		T value = info.get(i);
		info.set(i, info.get(i1));
		info.set(i1, value);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return editable[column];
	}

	public String[] getTitle() {
		return title;
	}

	public void setTitle(String[] title) {
		this.title = title;
	}

	public boolean[] getEditable() {
		return editable;
	}

	public void setEditable(boolean[] editable) {
		this.editable = editable;
	}

	public ArrayList<T> getInfo() {
		return info;
	}

	public void setInfo(ArrayList<T> info) {
		this.info = info;
	}
}
