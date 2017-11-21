package main.java;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lixiaoyao on 2017/11/11.
 */
public class ModelKeyValue extends DefaultTableModel {

    private String[] columnHeaders = new String[]{
            ResourceToolProperties.getString("String_TableColumnIndex"),
            ResourceToolProperties.getString("String_TableColumnKey"),
            ResourceToolProperties.getString("String_TableColumnState")
    };
    private ArrayList<KeyValue> tableDatas = new ArrayList<>();
    public static final int COLUMN_INDEX = 0;
    public static final int COLUMN_KEY = 1;
    public static final int COLUMN_STATE = 2;
    private String currentModifyType = ModifyType.UNTRANSLATED;
    private String currentFileName = "";
    private int preKeyValueIndex = -1;

    public ModelKeyValue() {
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public int getRowCount() {
        return tableDatas == null ? 0 : tableDatas.size();
    }

    @Override
    public int getColumnCount() {
        return columnHeaders == null ? 0 : columnHeaders.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnHeaders[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case COLUMN_INDEX:
            case COLUMN_KEY:
            case COLUMN_STATE:
                return String.class;
        }
        return super.getColumnClass(columnIndex);
    }

    @Override
    public Object getValueAt(int row, int column) {
        switch (column) {
            case 0:
                return row;
            case 1:
                return tableDatas.get(row).getKey();
            case 2:
                if (tableDatas.get(row).isModify()) {
                    return ResourceToolProperties.getString("String_Processed");
                } else {
                    return ResourceToolProperties.getString("String_UnProcess");
                }
        }
        return super.getValueAt(row, column);
    }

    public void initParameter(){
        this.currentModifyType = ModifyType.UNTRANSLATED;
        this.currentFileName = "";
        this.preKeyValueIndex = -1;
    }

    public void setParameter(String latestNewModifyType, String latestNewFileName) {
        if (!this.currentModifyType.equals(latestNewModifyType) ||
                (this.currentFileName!=null && !this.currentFileName.equals(latestNewFileName)) ) {
            this.currentModifyType = latestNewModifyType;
            this.currentFileName = latestNewFileName;
            resetTableData();
            this.preKeyValueIndex = -1;
        }
    }

    public void resetTableData() {
        this.tableDatas.clear();
        for (int i = 0; i < PropertiesUtilites.allKeyValue.size(); i++) {
            KeyValue temp = PropertiesUtilites.allKeyValue.get(i);
            if (this.currentFileName!=null && temp.getFileName().equals(this.currentFileName) && temp.getModifyType().equals(this.currentModifyType)) {
                this.tableDatas.add(temp);
            }
        }

        fireTableDataChanged();
    }

    public String getChineseValue(int row, String preEnglishValue) {
        if (!preEnglishValue.equals("") && this.preKeyValueIndex !=-1){
            this.tableDatas.get(this.preKeyValueIndex).setAfterModifyValue(preEnglishValue);
            fireTableCellUpdated(this.preKeyValueIndex,COLUMN_STATE);
        }
        this.preKeyValueIndex=row;
        String result = "";
        if (this.currentModifyType.equals(ModifyType.MODIFY)) {
            result = this.tableDatas.get(row).getPreValue() + "¡ú" + this.tableDatas.get(row).getCurrentValue();
        } else {
            result = this.tableDatas.get(row).getCurrentValue();
        }
        if (result != "") {
            try {
                result = unicodeToString(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public String getEnglishValue(int row){
        return this.tableDatas.get(row).getAfterModifyValue();
    }

    private String unicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }

}
