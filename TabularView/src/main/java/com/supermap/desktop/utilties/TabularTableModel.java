package com.supermap.desktop.utilties;

import com.supermap.data.*;
import com.supermap.desktop.enums.TabularChangedType;
import com.supermap.desktop.event.TabularChangedEvent;
import com.supermap.desktop.event.TabularValueChangedListener;
import com.supermap.desktop.tabularview.TabularViewProperties;
import com.supermap.desktop.utilities.Convert;
import com.supermap.desktop.utilities.DoubleUtilities;

import javax.swing.table.AbstractTableModel;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 属性表的TableModel
 *
 * @author XiaJT
 */
public class TabularTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private transient Recordset recordset;
    private transient FieldInfos fieldInfos;
    private transient FieldInfos fieldInfosDataset;
    private int currentRow = 0;
    // private TabularCache tabularCache = new TabularCache();
    private SimpleDateFormat resultFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.US);
    private List<TabularValueChangedListener> tabularValueChangedListeners = new ArrayList<>();
    private boolean isHiddenSystemField;

    public TabularTableModel(Recordset recordset) {
        setRecordset(recordset);
    }

    private void init() {
        if (this.recordset != null) {
            this.recordset.moveFirst();
            this.currentRow = 0;
            this.fieldInfos = recordset.getFieldInfos();
            this.fieldInfosDataset = recordset.getDataset().getFieldInfos();
        }
    }

    /**
     * 得到表头名字
     */
    @Override
    public String getColumnName(int column) {
        if (this.recordset == null) {
            return null;
        }
        return fieldInfos.get(getFieldIndex(column)).getCaption();
    }

    private int getFieldIndex(int column) {
        if (isHiddenSystemField) {
            int index = -1;
            for (int i = 0; i < fieldInfos.getCount(); i++) {
                if (!fieldInfos.get(i).isSystemField()) {
                    index++;
                    if (index == column) {
                        return i;
                    }
                }
            }
        }
        return column;
    }

    @Override
    public int getRowCount() {
        if (recordset == null || recordset.isClosed()) {
            return 0;
        } else {
            return recordset.getRecordCount();
        }
    }

    @Override
    public int getColumnCount() {
        if (recordset == null || recordset.isClosed()) {
            return 0;
        } else {
            if (!isHiddenSystemField) {
                return recordset.getFieldCount();
            } else {
                FieldInfos fieldInfos = recordset.getFieldInfos();
                int count = 0;
                for (int i = fieldInfos.getCount() - 1; i >= 0; i--) {
                    if (!fieldInfos.get(i).isSystemField()) {
                        count++;
                    }
                }
                return count;
            }
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        // return 0;
        columnIndex = getFieldIndex(columnIndex);
        if (recordset == null || recordset.isClosed()) {
            return null;
        }
        // 缓存用处效果不明显
        // int key = getKey(rowIndex, columnIndex);
        // Object value = tabularCache.getValue(key);
        // if (null == value) {
        Object value;
        // 每次赋值的时候，都需要不断移动记录集的位置
        moveToRow(rowIndex);
        value = recordset.getFieldValue(columnIndex);
        if (value instanceof Double && Double.isInfinite((double) value)) {
            value = TabularViewProperties.getString("String_Infinite");
        } else if (value instanceof Float && Float.isInfinite((float) value)) {
            value = TabularViewProperties.getString("String_Infinite");
        } else if (recordset.getFieldInfos().get(columnIndex).getType().equals(FieldType.LONGBINARY) && null != value) {
            value = "BinaryData";
        }
        return value;
    }

    private int getKey(int rowIndex, int columnIndex) {
        // 暂定最多一百列
        return rowIndex * 100 + columnIndex;
    }

    /**
     * 从当前位置移动到行
     *
     * @param rowIndex rowIndex意义是只当前需要赋值的行，currentRow是指当前记录集所在的行，当进行删除操作后，需要重新对应起来-yuanR
     */
    public void moveToRow(int rowIndex) {
        if (recordset == null || recordset.isClosed()) {
            return;
        }
        while (rowIndex != this.currentRow) {
            if (rowIndex > this.currentRow) {
                this.currentRow++;
                this.recordset.moveNext();
            } else {
                this.currentRow--;
                this.recordset.movePrev();
            }
        }
    }

    private void moveToRowWithoutCurrentIndex(int rowIndex) {
        if (recordset == null || recordset.isClosed()) {
            return;
        }
        int nowRow = recordset.getID();
        while (rowIndex != nowRow) {
            if (rowIndex > nowRow) {
                nowRow++;
                recordset.moveNext();
            } else {
                nowRow--;
                recordset.movePrev();
            }
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        column = getFieldIndex(column);
        if (recordset == null || recordset.getDataset().isReadOnly() || recordset.isClosed()) {
            return false;
        }
        if (fieldInfosDataset.get(column) == null) {
            return false;
        }
        boolean flag = true;
        if (column == 0 || fieldInfos.get(column).isSystemField()) {
            flag = false;
        }
        return flag;
    }

    public void setRecordset(Recordset recordset) {
        if (this.recordset != null && !this.recordset.isClosed()) {
            this.recordset.dispose();
            this.recordset = null;
        }
        if (recordset == null) {
            return;
        }
        this.recordset = recordset;
        recordset.moveFirst();
        int count = 0;
        init();
        fireTableStructureChanged();
    }

    public Recordset getRecordset() {
        return this.recordset;
    }

    // public void updateData(int row, int column, Object data) {
    // if (recordset == null) {
    // return;
    // }
    //
    // moveToRow(row);
    // try {
    // // 判断Data是否为空
    // boolean isDataNull = false;
    // if (data == null)
    // isDataNull = true;
    // else if (data instanceof String && ((String) data).length() <= 0) {
    // isDataNull = true;
    // }
    //
    // if ((!isDataNull || recordset.getFieldInfos().get(column).isRequired()) && (recordset.getFieldValue(column) == null
    // || !recordset.getFieldValue(column).equals(data))) {
    //
    // recordset.edit();
    // if (recordset.setFieldValue(column, data)) {
    // recordset.update();
    // }
    // }
    // } catch (Exception e) {
    // deadException(e);
    // // everything will be fine
    // }
    // }


    @Override
    public Class getColumnClass(int c) {
        c = getFieldIndex(c);
        if (recordset == null) {
            return Object.class;
        }

        Class result;
        if (recordset.getRecordCount() > 0) {
            FieldType fieldType = fieldInfos.get(c).getType();
            if (fieldType == FieldType.BOOLEAN) {
                result = Boolean.class;
            } else if (fieldType == FieldType.DATETIME) {
                return Time.class;
            } else if (fieldType == FieldType.DOUBLE) {
                result = Double.class;
            } else if (fieldType == FieldType.INT16) {
                result = Short.class;
            } else if (fieldType == FieldType.INT32) {
                result = Integer.class;
            } else if (fieldType == FieldType.INT64) {
                result = Long.class;
            } else if (fieldType == FieldType.SINGLE) {
                result = Float.class;
            } else {
                result = String.class;
            }
        } else {
            result = String.class;
        }

        return result;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        columnIndex = getFieldIndex(columnIndex);
        if (recordset == null) {
            return;
        }

        String columnName = getColumnName(columnIndex);
        String fieldName = "";
        for (int i = 0; i < fieldInfos.getCount(); i++) {
            if (fieldInfos.get(i).getCaption().equals(columnName)) {
                fieldName = fieldInfos.get(i).getName();
                break;
            }
        }
        if (fieldName.equals("")) {
            return;
        }
        moveToRow(rowIndex);
        TabularChangedEvent tabularChangedEvent = new TabularChangedEvent();
        try {
            // 判断Data是否为空
            boolean isDataNull = false;
            if (aValue == null) {
                isDataNull = true;
            } else if (aValue instanceof String && ((String) aValue).length() <= 0) {
                isDataNull = true;
            }
            Object oldFieldValue = recordset.getFieldValue(fieldName);
            if ((!isDataNull || !fieldInfos.get(fieldName).isRequired())
                    && (oldFieldValue == null || !oldFieldValue.equals(aValue))) {
                recordset.edit();
                // bool类型先处理
                Object value = aValue;
                FieldType fieldType = fieldInfos.get(fieldName).getType();
                if (FieldType.BOOLEAN == fieldType) {
                    if (!isDataNull) {
                        value = "True".equals(aValue);
                    } else {
                        value = null;
                    }
                }
                if (isDataNull) {
                    recordset.setFieldValueNull(fieldName);
                } else if (FieldType.BYTE == fieldType) {
                    recordset.setByte(fieldName, Short.parseShort(String.valueOf(value)));
                } else if (FieldType.DATETIME == fieldType) {
                    Date date = Convert.toDateTime(aValue);
                    recordset.setFieldValue(fieldName, date);
                } else if (FieldType.INT16 == fieldType || FieldType.INT32 == fieldType || FieldType.INT64 == fieldType) {
                    int intValue = DoubleUtilities.intValue(DoubleUtilities.stringToValue(String.valueOf(value)));
                    recordset.setFieldValue(fieldName, intValue);
                } else if (FieldType.SINGLE == fieldType || FieldType.DOUBLE == fieldType) {
                    recordset.setFieldValue(fieldName, value);
                } else {
                    recordset.setFieldValue(fieldName, value);
                }
                recordset.update();
                tabularChangedEvent.setTabularChangedType(TabularChangedType.UPDATED);
                tabularChangedEvent.setSmId(recordset.getID());
                tabularChangedEvent.setBeforeValue(oldFieldValue);
                tabularChangedEvent.setAfterValue(value);
                tabularChangedEvent.setFieldName(fieldName);
                fireTabularValueChangedListener(tabularChangedEvent);
                // tabularCache.updateValue(getKey(rowIndex, columnIndex), value);
            }
        } catch (Exception e) {
            deadException(e);
            // everything will be fine
        }
    }

    public void dispose() {
        if (this.recordset != null) {
            this.recordset.dispose();
            this.recordset = null;
        }
    }

    /**
     * 数据集排序
     */
    public Recordset sortRecordset(String sortKind, int... selectedColumns) {
        DatasetVector datasetVector = recordset.getDataset();
        boolean isFirst = true;
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < selectedColumns.length; i++) {
            if (!isFirst) {
                buffer.append("#");
            }
            buffer.append(fieldInfos.get(getFieldIndex(selectedColumns[i])).getName());
            buffer.append(" ");
            buffer.append(sortKind);
            isFirst = false;
        }

        try {
            QueryParameter queryParameter = recordset.getQueryParameter();
            queryParameter.setOrderBy(buffer.toString().split("#"));
            queryParameter.setCursorType(CursorType.DYNAMIC);

            this.setRecordset(datasetVector.query(queryParameter));
        } catch (Exception e) {
            deadException(e);
        }
        return recordset;
    }

    public void deadException(Exception e) {
        // do nothing
    }

    public void addValueChangedListener(TabularValueChangedListener tabularValueChangedListener) {
        tabularValueChangedListeners.add(tabularValueChangedListener);
    }

    public void removeValueChangedListener(TabularValueChangedListener tabularValueChangedListener) {
        tabularValueChangedListeners.remove(tabularValueChangedListener);
    }

    protected void fireTabularValueChangedListener(TabularChangedEvent tabularChangedEvent) {
        for (int i = tabularValueChangedListeners.size() - 1; i >= 0; i--) {
            tabularValueChangedListeners.get(i).valueChanged(tabularChangedEvent);
        }
    }

    public int getFieldColumn(String fieldName) {
        int hiddenFieldIndex = -1;
        for (int i = 0; i < fieldInfos.getCount(); i++) {
            if (!fieldInfos.get(i).isSystemField()) {
                hiddenFieldIndex++;
            }
            if (fieldInfos.get(i).getName().equalsIgnoreCase(fieldName)) {
                return isHiddenSystemField ? hiddenFieldIndex : i;
            }
        }
        return -1;
    }

    public int getRowBySmId(int smId) {
        for (int i = 0; i < getRowCount(); i++) {
            moveToRow(i);
            if (recordset.getID() == smId) {
                return i;
            }
        }
        return -1;
    }

    public boolean getHiddenSystemField() {
        return this.isHiddenSystemField;
    }

    public void setHiddenSystemField(boolean isHiddenSystemField) {
        this.isHiddenSystemField = isHiddenSystemField;
        fireTableStructureChanged();
    }

    public int getModelColumn(int columnIndex) {
        return getFieldIndex(columnIndex);
    }

    private int getSmIdByRow(int viewRow) {
        moveToRow(viewRow);
        return (int) recordset.getFieldValue("smId");
    }

    public boolean addRow(Geometry geometry) {
//		int id = recordset.getID();
        boolean result = recordset.addNew(geometry);
        recordset.update();
//		moveToRowWithoutCurrentIndex(id)
        if (result) {
            // 删除操作完成后，为确保下一次getValue赋值正确，调整记录集所在的记录数
            this.currentRow = 0;
            this.recordset.moveFirst();
            this.recordset.refresh();
            fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
        }
        return result;
    }

    /**
     * @param viewRows
     */
    public void deleteRows(int[] viewRows) {

        int[] smIds = new int[viewRows.length];
        for (int i = 0; i < viewRows.length; i++) {
            int viewRow = viewRows[i];
            smIds[i] = getSmIdByRow(viewRow);
        }
        // 通过smid值进行记录集的查找
        Recordset query = this.recordset.getDataset().query(smIds, CursorType.DYNAMIC);
        if (query != null && !query.isEmpty()) {
            try {
                query.deleteAll();
            } finally {
                query.close();
                query.dispose();
            }
            // 删除操作完成后，为确保下一次getValue赋值正确，调整记录集所在的记录数
            this.currentRow = 0;
            this.recordset.moveFirst();
            this.recordset.refresh();
            fireTableDataChanged();
        }
    }

    public void refresh() {
        this.recordset.refresh();
        fireTableDataChanged();
    }
}