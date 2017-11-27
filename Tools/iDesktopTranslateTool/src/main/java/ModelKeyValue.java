import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/22
 * Time: 10:28
 * Description:
 */
public class ModelKeyValue extends DefaultTableModel {

	private String[] columnHeaders = new String[]{
			ConfigToolProperties.getString("String_TableColumnIndex"),
			ConfigToolProperties.getString("String_TableColumnKey"),
			ConfigToolProperties.getString("String_TableColumnState")
	};
	private ArrayList<KeyValue> tableDatas = new ArrayList<KeyValue>();
	public static final int COLUMN_INDEX = 0;
	public static final int COLUMN_KEY = 1;
	public static final int COLUMN_STATE = 2;
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
				return row + 1;
			case 1:
				return tableDatas.get(row).getKey();
			case 2:
				if (tableDatas.get(row).isModify()) {
					return ConfigToolProperties.getString("String_Processed");
				} else {
					return ConfigToolProperties.getString("String_UnProcess");
				}
		}
		return super.getValueAt(row, column);
	}


	public void resetTableData() {
		this.tableDatas.clear();
		for (KeyValue temp:SystemFileUtilities.getAllKeyValue().values()) {
			if (!temp.isModify()) {
				this.tableDatas.add(temp);
			}
		}

		fireTableDataChanged();
	}

	public String getChineseValue(int row, String englishValue){
		if (!englishValue.equals("") && this.preKeyValueIndex !=-1){
			englishValue=PunctuationUtilities.ToDBC(englishValue);
			this.tableDatas.get(this.preKeyValueIndex).setEnglishValue(englishValue);
			fireTableCellUpdated(this.preKeyValueIndex,COLUMN_STATE);
		}
		this.preKeyValueIndex=row;
		return this.tableDatas.get(row).getKey();
	}

	public String getEnglishValue(int row) {
		return this.tableDatas.get(row).getEnglishValue();
	}

	public String getRecommendValue(int row){
		return SystemFileUtilities.getAllKeyValue().get(this.tableDatas.get(row).getSimilarityString()).getEnglishValue();
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

