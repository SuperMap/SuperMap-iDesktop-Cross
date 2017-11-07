package com.supermap.desktop.netservices.iserver;

import com.supermap.desktop.Application;
import com.supermap.desktop.core.FileSize;
import com.supermap.desktop.core.FileSizeType;
import com.supermap.desktop.event.TableCellValueChangeEvent;
import com.supermap.desktop.event.TableCellValueChangeListener;
import com.supermap.desktop.netservices.NetServicesProperties;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.TristateCheckBox;
import com.supermap.desktop.utilities.FileUtilities;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PanelFolderSelector extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final int CONTENT_FILE = 1;
	private static final int CONTENT_DIRECTORY = 2;
	private static final int CONTENT_FILE_AND_DIRECTORY = 3;

	private int displayContent = CONTENT_FILE_AND_DIRECTORY;
	/**
	 * 显示隐藏文件。true，在列表中列出，并以 是/否 显示；false，过滤掉，并且不在列表中显示。
	 */
	private boolean isShowHidden = true;
	private JTable table;
	private EventListenerList listenerList = new EventListenerList();
	private TristateCheckBox checkBoxSelectedAll;
	private boolean isIgnore = false;

	private PanelFolderSelector() {
		initializeComponent();
	}

	public PanelFolderSelector(ArrayList<SelectableFile> files) {
		this();
		updateTableModel(files);
	}

	public boolean isShowHidden() {
		return isShowHidden;
	}

	public void setShowHidden(boolean isShowHidden) {
		this.isShowHidden = isShowHidden;
		((FolderSelectorTableModel) this.table.getModel()).setShowHidden(isShowHidden);
	}

	public void addFileSelectedChangeListener(FileSelectedChangeListener listener) {
		this.listenerList.add(FileSelectedChangeListener.class, listener);
	}

	public void removeFileSelectedChangeListener(FileSelectedChangeListener listener) {
		this.listenerList.remove(FileSelectedChangeListener.class, listener);
	}

	private void fireFileSelectedChange(FileSelectedChangeEvent e) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == FileSelectedChangeListener.class) {
				((FileSelectedChangeListener) listeners[i + 1]).FileSelectedChange(e);
			}
		}
	}

	private void initializeComponent() {
		this.checkBoxSelectedAll = new TristateCheckBox();
		this.table = new JTable();

		table.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (table.getTableHeader().columnAtPoint(e.getPoint()) == 0) {
					checkBoxSelectedAll.setSelected(checkBoxSelectedAll.isSelectedEx() == null || !checkBoxSelectedAll.isSelectedEx());
					table.getTableHeader().repaint();
					isIgnore = true;
					boolean isSelected = checkBoxSelectedAll.isSelected();
					for (int i = 0; i < table.getRowCount(); i++) {
						table.setValueAt(isSelected, i, 0);
					}
					isIgnore = false;

				}
			}
		});
		final FolderSelectorTableModel tableModel = new FolderSelectorTableModel();
		tableModel.addTableCellValueChangeListener(new TableCellValueChangeListener() {

			@Override
			public void tableCellValueChange(TableCellValueChangeEvent e) {
				PanelFolderSelector.this.tableCellValueChange(tableModel.getFile(e.getRow()));
				if (!isIgnore) {
					isIgnore = true;
					checkSelectedAllState();
					isIgnore = false;
				}
			}
		});
		this.table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setMaxWidth(30);
		table.getColumnModel().getColumn(0).setMinWidth(30);
		table.getColumnModel().getColumn(0).setHeaderRenderer(new TableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				JPanel jPanel = new JPanel();
				jPanel.add(checkBoxSelectedAll);
				return jPanel;
			}
		});
		this.table.getColumnModel().getColumn(FolderSelectorTableModel.SELECTED).setPreferredWidth(15);
		this.table.getColumnModel().getColumn(FolderSelectorTableModel.TYPE).setPreferredWidth(15);
		this.table.getColumnModel().getColumn(FolderSelectorTableModel.SIZE).setPreferredWidth(30);
		this.table.getColumnModel().getColumn(FolderSelectorTableModel.HIDEN).setPreferredWidth(15);

		JScrollPane scrollTable = new JScrollPane(this.table);
		this.setLayout(new BorderLayout());
		this.add(scrollTable, BorderLayout.CENTER);
	}

	private void checkSelectedAllState() {
		TableModel tableModel = table.getModel();
		Boolean isSelected = (Boolean) tableModel.getValueAt(0, 0);
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			if (tableModel.getValueAt(i, 0) != isSelected) {
				checkBoxSelectedAll.setSelectedEx(null);
				break;
			}
			if (i == tableModel.getRowCount() - 1) {
				checkBoxSelectedAll.setSelectedEx(isSelected);
			}
		}
		table.getTableHeader().repaint();
	}

	private void tableCellValueChange(SelectableFile file) {
		fireFileSelectedChange(new FileSelectedChangeEvent(this, file));
	}

	/**
	 * 更新 TableModel 的数据
	 *
	 */
	private void updateTableModel(ArrayList<SelectableFile> files) {
		if (files == null || files.size() == 0) {
			return;
		}

		ArrayList<SelectableFile> modelData = new ArrayList<>(); // 用来初始化 Model 的集合

		if (this.displayContent == CONTENT_FILE) {
			modelData.addAll(getSingleFiles(files));
		} else if (this.displayContent == CONTENT_DIRECTORY) {
			modelData.addAll(getDirectories(files));
		} else if (this.displayContent == CONTENT_FILE_AND_DIRECTORY) {
			modelData.addAll(getSingleFiles(files));
			modelData.addAll(getDirectories(files));
		}
		((FolderSelectorTableModel) this.table.getModel()).setFiles(modelData);
		checkSelectedAllState();
	}

	/**
	 * 获取文件集合
	 *
	 * @return
	 */
	private ArrayList<SelectableFile> getSingleFiles(ArrayList<SelectableFile> files) {
		ArrayList<SelectableFile> singleFiles = new ArrayList<>();

		for (int i = 0; i < files.size(); i++) {
			if (!files.get(i).isDirectory()) {
				singleFiles.add(files.get(i));
			}
		}
		return singleFiles;
	}

	/**
	 * 获取目录集合
	 *
	 * @return
	 */
	private ArrayList<SelectableFile> getDirectories(ArrayList<SelectableFile> files) {
		ArrayList<SelectableFile> directories = new ArrayList<>();

		for (int i = 0; i < files.size(); i++) {
			if (files.get(i).isDirectory()) {
				directories.add(files.get(i));
			}
		}
		return directories;
	}

	private class FolderSelectorTableModel extends AbstractTableModel {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public static final int SELECTED = 0;
		public static final int NAME = 1; // 名称列
		public static final int TYPE = 2; // 类型
		public static final int SIZE = 3; // 文件大小列
		public static final int LASTMODIFIED = 4; // 最后修改时间列
		public static final int HIDEN = 5; // 是否隐藏

		private boolean isShowHidden = true;// 是否显示隐藏列
		private ArrayList<SelectableFile> files;

		public FolderSelectorTableModel() {
			this.files = new ArrayList<>();
		}

		@Override
		public int getRowCount() {
			return this.files.size();
		}

		@Override
		public int getColumnCount() {
			return this.isShowHidden ? 6 : 5;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Object result = null;

			try {
				if (rowIndex >= 0 && rowIndex < files.size()) {
					SelectableFile file = this.files.get(rowIndex);

					if (columnIndex == SELECTED) {
						result = file.isSelected();
					} else if (columnIndex == NAME) {
						result = file.getName();
					} else if (columnIndex == TYPE) {
						result = file.isDirectory() ? CoreProperties.getString(CoreProperties.Directory) : CoreProperties
								.getString(CoreProperties.File);
					} else if (columnIndex == SIZE) {
						FileSize fileSize = new FileSize(FileUtilities.getFileSize(file), FileSizeType.BYTE);
						result = fileSize.ToStringClever();
					} else if (columnIndex == LASTMODIFIED) {
						result = DateFormat.getDateTimeInstance().format(new Date(file.lastModified()));
						new Date(file.lastModified());
					} else if (columnIndex == HIDEN && this.isShowHidden) {
						if (file.isHidden()) {
							result = CoreProperties.getString(CoreProperties.True);
						} else {
							result = CoreProperties.getString(CoreProperties.False);
						}
					}
				}
			} catch (Exception e) {
				Application.getActiveApplication().getOutput().output(e);
			}
			return result;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			try {
				if (columnIndex == SELECTED) {
					SelectableFile file = this.files.get(rowIndex);

					if (aValue == null) {
						file.setIsSelected(false);
					} else {
						file.setIsSelected(Boolean.valueOf(aValue.toString()));
					}
					fireTableCellValueChange(new TableCellValueChangeEvent(this, rowIndex, columnIndex));
				}
			} catch (Exception e) {
				Application.getActiveApplication().getOutput().output(e);
			}
		}

		/**
		 * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
		 *
		 * @param columnIndex the column being queried
		 * @return the Object.class
		 */
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == SELECTED) {
				return Boolean.class;
			} else {
				return Object.class;
			}
		}

		@Override
		public String getColumnName(int column) {
			if (column == NAME) {
				return CoreProperties.getString(CoreProperties.Name);
			} else if (column == TYPE) {
				return CoreProperties.getString(CoreProperties.Type);
			} else if (column == SIZE) {
				return CoreProperties.getString(CoreProperties.Size);
			} else if (column == LASTMODIFIED) {
				return NetServicesProperties.getString("String_LastModified");
			} else if (column == HIDEN) {
				return NetServicesProperties.getString("String_Hidden");
			} else {
				return "";
			}
		}

		/**
		 * Returns false. This is the default implementation for all cells.
		 *
		 * @param rowIndex    the row being queried
		 * @param columnIndex the column being queried
		 * @return false
		 */
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == SELECTED ? true : false;
		}

		public boolean isShowHidden() {
			return isShowHidden;
		}

		public void setShowHidden(boolean isShowHidden) {
			this.isShowHidden = isShowHidden;
			fireTableStructureChanged();
		}

		public void clear() {
			this.files.clear();
			fireTableDataChanged();
		}

		/**
		 * 使用文件集合填充 Model
		 *
		 * @param files
		 */
		public void setFiles(ArrayList<SelectableFile> files) {
			this.files.addAll(files);
			fireTableDataChanged();
		}

		public SelectableFile getFile(int rowIndex) {
			SelectableFile file = null;

			if (rowIndex < this.files.size()) {
				file = this.files.get(rowIndex);
			}
			return file;
		}

		public void addTableCellValueChangeListener(TableCellValueChangeListener listener) {
			this.listenerList.add(TableCellValueChangeListener.class, listener);
		}

		public void removeTableCellValueChangeListener(TableCellValueChangeListener listener) {
			this.listenerList.remove(TableCellValueChangeListener.class, listener);
		}

		private void fireTableCellValueChange(TableCellValueChangeEvent e) {
			Object[] listeners = listenerList.getListenerList();

			for (int i = listeners.length - 2; i >= 0; i -= 2) {
				if (listeners[i] == TableCellValueChangeListener.class) {
					((TableCellValueChangeListener) listeners[i + 1]).tableCellValueChange(e);
				}
			}
		}
	}
}
