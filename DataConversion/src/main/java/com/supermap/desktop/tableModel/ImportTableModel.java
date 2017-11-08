package com.supermap.desktop.tableModel;

import com.supermap.desktop.dataconversion.DataConversionProperties;
import com.supermap.desktop.iml.ImportInfo;
import com.supermap.desktop.properties.CoreProperties;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xie on 2016/10/13.
 * 导入界面中的table模型
 */
public class ImportTableModel extends AbstractTableModel {
    /**
     *
     */
    public static final int COLUMN_FILENAME = 0;
    public static final int COLUMN_FILETYPE = 1;
    public static final int COLUMN_STATE = 2;

    private String[] title = {DataConversionProperties.getString("string_tabletitle_data"),
            DataConversionProperties.getString("string_tabletitle_filetype"),
            CoreProperties.getString("String_State")};
    private ArrayList<ImportInfo> importInfos = new ArrayList<ImportInfo>();

    public ImportTableModel() {
        super();
    }

    @Override
    public int getRowCount() {
        return importInfos.size();
    }

    @Override
    public int getColumnCount() {
        return title.length;
    }

    public void addRow(ImportInfo fileInfo) {
        this.importInfos.add(fileInfo);
        fireTableRowsInserted(COLUMN_FILENAME, getRowCount());
    }

    public void removeRow(int i) {
        importInfos.remove(i);
        fireTableRowsDeleted(COLUMN_FILENAME, getRowCount());
    }

    public void removeRows(int[] rows) {
        ArrayList<ImportInfo> removeInfo = new ArrayList<ImportInfo>();
        if (rows.length > 0) {
            for (int i = 0; i < rows.length; i++) {
                removeInfo.add(importInfos.get(rows[i]));
            }
            importInfos.removeAll(removeInfo);
            fireTableRowsDeleted(COLUMN_FILENAME, getRowCount());
        }
    }

    public void updateRows(List<ImportInfo> tempFileInfos) {
        this.importInfos = (ArrayList<ImportInfo>) tempFileInfos;
        fireTableRowsUpdated(COLUMN_FILENAME, getRowCount());
    }

    @Override
    public String getColumnName(int columnIndex) {
        return title[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == COLUMN_FILETYPE) {
            return true;
        }
        return false;
    }

    // 得到某行的数据
    public ImportInfo getTagValueAt(int tag) {
        return importInfos.get(tag);
    }

    // 得到选中的所有行的数据
    public List<ImportInfo> getTagValueAt(int[] tag) {
        ArrayList<ImportInfo> result = new ArrayList<ImportInfo>();
        for (int i = 0; i < tag.length; i++) {
            result.add(importInfos.get(i));
        }
        return result;
    }

    // 在表格中填充数据
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ImportInfo importInfo = importInfos.get(rowIndex);
        if (columnIndex == COLUMN_FILENAME) {
            return importInfo.getFileName();
        }
        if (columnIndex == COLUMN_FILETYPE) {
            return importInfo.getFileType();
        }
        if (columnIndex == COLUMN_STATE) {
            return importInfo.getState();
        }
        return "";
    }

    public ArrayList<ImportInfo> getImportInfos() {
        return importInfos;
    }
}
