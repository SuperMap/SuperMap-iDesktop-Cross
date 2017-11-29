package com.supermap.desktop.dialog;

import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.layoutview.LayoutViewProperties;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.CellRenders.ListStatisticsTypeCellRender;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.ui.controls.SmDialog;
import com.supermap.desktop.utilities.CoreResources;
import com.supermap.layout.RulerLine;
import com.supermap.layout.RulerLineType;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/29
 * Time: 9:03
 * Description:Ruler line management form
 */
public class DialogRulerLinesManager extends SmDialog {

	private JToolBar toolBar;
	private JButton buttonAdd;
	private JButton buttonSelectAll;
	private JButton buttonSelectInverse;
	private JButton buttonDelete;
	private JScrollPane scrollPane;
	private JTable table;
	private RulerTableModel rulerTableModel;
	private JButton buttonOK;
	private JButton buttonCancel;

	private static final String URL_STR = "/coreresources/ToolBar/";
	private RulerLine[] rulerLines;

	public DialogRulerLinesManager(RulerLine[] rulerLines) {
		super();
		this.rulerLines = rulerLines;
		setSize(620, 420);
		setLocationRelativeTo(null);
		initComponents();
		initLayout();
		initResources();
		unRegisterEvents();
		registerEvents();
	}

	private void initComponents() {
		this.toolBar = new JToolBar();
		this.buttonAdd = new JButton();
		this.buttonSelectAll = new JButton();
		this.buttonSelectInverse = new JButton();
		this.buttonDelete = new JButton();
		this.scrollPane = new JScrollPane();
		this.table = new JTable();
		this.rulerTableModel = new RulerTableModel();
		this.buttonOK = new JButton();
		this.buttonCancel = new JButton();

		this.toolBar.add(this.buttonAdd);
		this.toolBar.add(this.buttonSelectAll);
		this.toolBar.add(this.buttonSelectInverse);
		this.toolBar.add(this.buttonDelete);
		this.toolBar.setFloatable(false);

		this.table.setModel(this.rulerTableModel);
		this.rulerTableModel.initData(this.rulerLines);
		this.table.getColumnModel().getColumn(0).setMaxWidth(30);
		TableColumn columnType = table.getColumnModel().getColumn(2);
		DefaultCellEditor cellEditorRulerLineType = new RulerLineTypeCellEditor();
		cellEditorRulerLineType.setClickCountToStart(2);
		columnType.setCellEditor(cellEditorRulerLineType);
		columnType.setCellRenderer(rulerLineTypeCellRenderer);

		this.scrollPane.setViewportView(this.table);
		getRootPane().setDefaultButton(buttonOK);
		this.componentList.add(this.buttonOK);
		this.componentList.add(this.buttonCancel);
		this.setFocusTraversalPolicy(policy);
	}

	private void initLayout() {
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);

		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.toolBar)
						.addComponent(this.scrollPane)
						.addGroup(groupLayout.createSequentialGroup()
								.addGap(320, 320, Short.MAX_VALUE)
								.addComponent(this.buttonOK)
								.addComponent(this.buttonCancel)
						)
				)
		);

		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addComponent(this.toolBar, 30, 30, 30)
				.addComponent(this.scrollPane)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.buttonOK)
						.addComponent(this.buttonCancel)
				)
		);
		getContentPane().setLayout(groupLayout);
	}

	private void initResources() {
		setTitle(LayoutViewProperties.getString("String_RulerLinesManagerFormTitle"));
		this.buttonAdd.setIcon(CoreResources.getIcon(URL_STR + "Image_ToolButton_AddItem.png"));
		this.buttonAdd.setToolTipText(CoreProperties.getString("String_Add"));
		this.buttonSelectAll.setIcon(CoreResources.getIcon(URL_STR + "Image_ToolButton_SelectAll.png"));
		this.buttonSelectAll.setToolTipText(CoreProperties.getString("String_ToolBar_SelectAll"));
		this.buttonSelectInverse.setIcon(CoreResources.getIcon(URL_STR + "Image_ToolButton_SelectInverse.png"));
		this.buttonSelectInverse.setToolTipText(CoreProperties.getString("String_ToolBar_SelectInverse"));
		this.buttonDelete.setIcon(CoreResources.getIcon(URL_STR + "Image_ToolButton_Delete.png"));
		this.buttonDelete.setToolTipText(CoreProperties.getString("String_Delete"));
		this.buttonOK.setText(CoreProperties.getString("String_OK"));
		this.buttonCancel.setText(CoreProperties.getString("String_Cancel"));
	}

	private void registerEvents() {
		this.buttonAdd.addActionListener(this.actionListener);
		this.buttonSelectAll.addActionListener(this.actionListener);
		this.buttonSelectInverse.addActionListener(this.actionListener);
		this.buttonDelete.addActionListener(this.actionListener);
		this.buttonOK.addActionListener(this.actionListener);
		this.buttonCancel.addActionListener(this.actionListener);
	}

	private void unRegisterEvents() {
		this.buttonAdd.removeActionListener(this.actionListener);
		this.buttonSelectAll.removeActionListener(this.actionListener);
		this.buttonSelectInverse.removeActionListener(this.actionListener);
		this.buttonDelete.removeActionListener(this.actionListener);
		this.buttonOK.removeActionListener(this.actionListener);
		this.buttonCancel.removeActionListener(this.actionListener);
	}

	private ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == buttonAdd) {
				rulerTableModel.addData();
				table.setRowSelectionInterval(table.getRowCount() - 1, table.getRowCount() - 1);
			} else if (e.getSource() == buttonSelectAll) {
				table.setRowSelectionInterval(0, table.getRowCount() - 1);
			} else if (e.getSource() == buttonSelectInverse) {
				selectInverse();
			} else if (e.getSource() == buttonDelete) {
				rulerTableModel.removeData(table.getSelectedRows());
				if (table.getRowCount() - 1 >= 0) {
					table.setRowSelectionInterval(table.getRowCount() - 1, table.getRowCount() - 1);
				}
			} else if (e.getSource() == buttonOK) {
				rulerLines = rulerTableModel.getData();
				DialogRulerLinesManager.this.setDialogResult(DialogResult.OK);
				DialogRulerLinesManager.this.dispose();
			} else if (e.getSource() == buttonCancel) {
				DialogRulerLinesManager.this.dispose();
			}
		}
	};

	private void selectInverse() {
		int[] temp = this.table.getSelectedRows();
		ListSelectionModel selectionModel = this.table.getSelectionModel();
		int allRowCount = this.table.getRowCount();
		ArrayList<Integer> selectedRows = new ArrayList<Integer>();
		for (int index = 0; index < temp.length; index++) {
			selectedRows.add(temp[index]);
		}

		selectionModel.clearSelection();
		for (int index = 0; index < allRowCount; index++) {
			if (!selectedRows.contains(index)) {
				selectionModel.addSelectionInterval(index, index);
			}
		}
	}

	DefaultTableCellRenderer rulerLineTypeCellRenderer = new DefaultTableCellRenderer() {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (component instanceof JLabel && value instanceof RulerLineType) {
				((JLabel) component).setText(getTypeName((RulerLineType) value));
			}
			return component;
		}
	};

	private String getTypeName(RulerLineType rulerLineType) {
		String type = CoreProperties.getString("String_FieldType_Unknown");
		if (rulerLineType == RulerLineType.HORIZONTAL) {
			type = LayoutViewProperties.getString("String_Horizontal");
		} else if (rulerLineType == RulerLineType.VERTICAL) {
			type = LayoutViewProperties.getString("String_Vertical");
		}
		return type;
	}

	private class RulerTableModel extends DefaultTableModel {

		private static final double DEFAULT_POSITION = 100;
		private String[] columnHeaders = new String[]{
				"",
				LayoutViewProperties.getString("String_Position"),
				CoreProperties.getString("String_Type")};

		private ArrayList<TableData> tableDatas = new ArrayList<>();
		public static final int COLUMN_INDEX = 0;
		public static final int COLUMN_POSITION = 1;
		public static final int COLUMN_TYPE = 2;

		public RulerTableModel() {
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			if (column == COLUMN_INDEX) {
				return false;
			} else {
				return true;
			}
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
					return String.class;
				case COLUMN_POSITION:
					return String.class;
				case COLUMN_TYPE:
					return RulerLineType.class;
			}
			return super.getColumnClass(columnIndex);
		}

		@Override
		public Object getValueAt(int row, int column) {
			switch (column) {
				case 0:
					return row + 1;
				case 1:
					return tableDatas.get(row).rulerLine.getPosition();
				case 2:
					return tableDatas.get(row).rulerLine.getType();
			}
			return super.getValueAt(row, column);
		}

		@Override
		public void setValueAt(Object aValue, int row, int column) {
			if (column == COLUMN_POSITION) {
				try {
					tableDatas.get(row).rulerLine.setPosition(Double.valueOf(aValue.toString()));
					fireTableCellUpdated(row, column);
				} catch (Exception e) {
					fireTableCellUpdated(row, column);
				}
			} else if (column == COLUMN_TYPE) {
				tableDatas.get(row).rulerLine.setType((RulerLineType) aValue);
				fireTableCellUpdated(row, column);
			}
		}

		public void initData(RulerLine[] rulerLines) {
			this.tableDatas.clear();
			if (rulerLines != null && rulerLines.length > 0) {
				for (RulerLine rulerLine : rulerLines) {
					this.tableDatas.add(new TableData(rulerLine));
				}
			}
			fireTableDataChanged();
		}

		public RulerLine[] getData() {
			RulerLine[] rulerLines = new RulerLine[this.tableDatas.size()];
			for (int i = 0; i < rulerLines.length; i++) {
				rulerLines[i] = this.tableDatas.get(i).rulerLine;
			}
			return rulerLines;
		}

		public void addData() {
			RulerLine rulerLine = new RulerLine();
			rulerLine.setPosition(DEFAULT_POSITION);
			rulerLine.setType(RulerLineType.HORIZONTAL);
			this.tableDatas.add(new TableData(rulerLine));
			fireTableDataChanged();
		}

		public void removeData(int[] selectRows) {
			for (int i = selectRows.length - 1; i >= 0; i--) {
				this.tableDatas.remove(selectRows[i]);
			}
			fireTableDataChanged();
		}
	}

	private class TableData {
		RulerLine rulerLine;

		TableData(RulerLine rulerLine) {
			this.rulerLine = rulerLine;
		}
	}

	private class RulerLineTypeCellEditor extends DefaultCellEditor {
		private JComboBox comboBox;
		private ArrayList<RulerLineType> rulerLineTypes = new ArrayList<>();

		public RulerLineTypeCellEditor() {
			super(new JComboBox());
			rulerLineTypes.add(RulerLineType.HORIZONTAL);
			rulerLineTypes.add(RulerLineType.VERTICAL);
		}

		@Override
		public Object getCellEditorValue() {
			if (comboBox != null) {
				return comboBox.getSelectedItem();
			}
			return null;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			comboBox = (JComboBox) super.getTableCellEditorComponent(table, value, isSelected, row, column);
			Object item = comboBox.getSelectedItem();
			comboBox.setRenderer(new ListStatisticsTypeCellRender());
			comboBox.removeAllItems();
			for (RulerLineType rulerLineType : rulerLineTypes) {
				comboBox.addItem(rulerLineType);
			}
			if (item != null) {
				comboBox.setSelectedItem(item);
			}
			return comboBox;
		}
	}

	class ListStatisticsTypeCellRender extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel component = new JLabel();
			if (value != null && value instanceof RulerLineType) {
				component.setText(getTypeName((RulerLineType) value));
			}
			component.setOpaque(true);
			if (isSelected) {
				component.setBackground(list.getSelectionBackground());
				component.setForeground(list.getSelectionForeground());
			} else {
				component.setBackground(list.getBackground());
				component.setForeground(list.getForeground());
			}
			return component;
		}
	}

	public RulerLine[] getRulerLines() {
		return rulerLines;
	}

}
