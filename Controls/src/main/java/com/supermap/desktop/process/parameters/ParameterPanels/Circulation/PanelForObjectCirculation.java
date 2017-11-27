package com.supermap.desktop.process.parameters.ParameterPanels.Circulation;

import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.ControlsResources;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.ui.controls.SmFileChoose;
import com.supermap.desktop.ui.controls.TableTooltipCellRenderer;
import com.supermap.desktop.ui.controls.button.SmButton;
import com.supermap.desktop.utilities.CoreResources;
import com.supermap.desktop.utilities.TableUtilities;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * Created by xie on 2017/11/6.
 */
public class PanelForObjectCirculation extends JPanel {

	private static final long serialVersionUID = 1L;
	private final int COLUMN_INDEX_INDEX = 0;
	private final int COLUMN_INDEX_CONTENT = 1;

	private JToolBar toolBar;
	private SmButton buttonAddObject;
	private SmButton buttonSelectAll;
	private SmButton buttonSelectInvert;
	private SmButton buttonDelete;
	private SmButton buttonMoveUp;
	private SmButton buttonMoveDown;
	private SmButton buttonMoveTop;
	private SmButton buttonMoveBottom;

	private boolean isShowAddButton;
	protected JTable tableForObjectCirculation;
	private ExchangeTableModel exchangeTableModel;

	protected ArrayList<String> pathList = new ArrayList<>();
	private String fileType;
	private final String ISDIR = "Directory";

	public PanelForObjectCirculation(boolean isShowAddButton) {
		this.isShowAddButton = isShowAddButton;
		initComponents();
		initLayout();
		initResources();
		registerEvent();
	}

	protected void initComponents() {
		this.toolBar = new JToolBar();
		this.toolBar.setFloatable(false);

		if (isShowAddButton) {
			this.buttonAddObject = new SmButton();
			this.buttonAddObject.setIcon(ControlsResources.getIcon("/controlsresources/ToolBar/ColorScheme/add.png"));
		}

		this.buttonSelectAll = new SmButton();
		this.buttonSelectAll.setIcon(CoreResources.getIcon("/coreresources/ToolBar/Image_ToolButton_SelectAll.png"));

		this.buttonSelectInvert = new SmButton();
		this.buttonSelectInvert.setIcon(CoreResources.getIcon("/coreresources/ToolBar/Image_ToolButton_SelectInverse.png"));

		this.buttonDelete = new SmButton();
		this.buttonDelete.setIcon(CoreResources.getIcon("/coreresources/ToolBar/Image_ToolButton_Delete.png"));

		this.buttonMoveUp = new SmButton();
		this.buttonMoveUp.setIcon(ControlsResources.getIcon("/controlsresources/ToolBar/ColorScheme/moveUp.png"));

		this.buttonMoveDown = new SmButton();
		this.buttonMoveDown.setIcon(ControlsResources.getIcon("/controlsresources/ToolBar/ColorScheme/moveDown.png"));

		this.buttonMoveTop = new SmButton();
		this.buttonMoveTop.setIcon(ControlsResources.getIcon("/controlsresources/ToolBar/ColorScheme/moveToTop.png"));

		this.buttonMoveBottom = new SmButton();
		this.buttonMoveBottom.setIcon(ControlsResources.getIcon("/controlsresources/ToolBar/ColorScheme/moveBottom.png"));

		this.tableForObjectCirculation = new JTable();
		this.exchangeTableModel = new ExchangeTableModel();
		this.exchangeTableModel.setEditable(new boolean[]{false, false});
		this.exchangeTableModel.setInfo(this.pathList);
		this.exchangeTableModel.setTitle(new String[]{CoreProperties.getString("String_ColumnHeader_Index"),
				CoreProperties.getString("String_Content")});
		this.tableForObjectCirculation.setModel(this.exchangeTableModel);
		// 设置列不可移动
		this.tableForObjectCirculation.getTableHeader().setReorderingAllowed(false);
		this.tableForObjectCirculation.setRowHeight(23);
		// 设置列宽
		TableColumn indexColumn = this.tableForObjectCirculation.getColumnModel().getColumn(COLUMN_INDEX_INDEX);
		indexColumn.setMinWidth(80);
		indexColumn.setPreferredWidth(80);
		indexColumn.setMaxWidth(150);
		this.tableForObjectCirculation.getColumnModel().getColumn(COLUMN_INDEX_CONTENT).setCellRenderer(TableTooltipCellRenderer.getInstance());
		this.setPreferredSize(new Dimension(this.getWidth(), 200));
		this.setMinimumSize(new Dimension(this.getWidth(), 200));
		checkButtonStates();
	}

	private void initLayout() {
		if (isShowAddButton) {
			this.toolBar.add(this.buttonAddObject);
			this.toolBar.addSeparator();
		}
		this.toolBar.add(this.buttonSelectAll);
		this.toolBar.add(this.buttonSelectInvert);
		this.toolBar.addSeparator();
		this.toolBar.add(this.buttonDelete);
		this.toolBar.addSeparator();
		this.toolBar.add(this.buttonMoveTop);
		this.toolBar.add(this.buttonMoveUp);
		this.toolBar.add(this.buttonMoveDown);
		this.toolBar.add(this.buttonMoveBottom);

		this.setLayout(new GridBagLayout());
		this.add(this.toolBar, new GridBagConstraintsHelper(0, 0, 1, 1).setWeight(1, 0).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE));
		this.add(new JScrollPane(this.tableForObjectCirculation), new GridBagConstraintsHelper(0, 1, 1, 1).setWeight(1, 1).setAnchor(GridBagConstraints.CENTER).setFill(GridBagConstraints.BOTH).setInsets(5, 0, 0, 0));
	}

	private void initResources() {
		if (isShowAddButton) {
			this.buttonAddObject.setToolTipText(ControlsProperties.getString("String_Add"));
		}
		this.buttonSelectAll.setToolTipText(ControlsProperties.getString("String_SelectAll"));
		this.buttonSelectInvert.setToolTipText(ControlsProperties.getString("String_SelectReverse"));
		this.buttonDelete.setToolTipText(CoreProperties.getString("String_Delete"));
		this.buttonMoveTop.setToolTipText(ControlsProperties.getString("String_MoveFirst"));
		this.buttonMoveUp.setToolTipText(ControlsProperties.getString("String_MoveUp"));
		this.buttonMoveDown.setToolTipText(ControlsProperties.getString("String_MoveDown"));
		this.buttonMoveBottom.setToolTipText(ControlsProperties.getString("String_MoveLast"));
	}

	private void registerEvent() {
		if (isShowAddButton) {
			this.buttonAddObject.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String modelName = "CirculationForObjectModel";
					String fileFilters = "";
					String modelType = "OpenMany";
					if (null != fileType) {
						modelName += fileType;
						if (!fileType.equals(ISDIR)) {
							fileFilters = SmFileChoose.buildFileFilters(SmFileChoose.createFileFilter(MessageFormat.format(ProcessProperties.getString("String_ImportFileType"), fileType.toUpperCase(), fileType.toLowerCase()), fileType.toLowerCase()));
						} else if (fileType.equals(ISDIR)) {
							modelType = "GetDirectories";
						}
						if (!SmFileChoose.isModuleExist(modelName)) {
							SmFileChoose.addNewNode(fileFilters, CoreProperties.getString("String_DefaultFilePath"), CoreProperties.getString("String_SelectFile"),
									modelName, modelType);
						}
					}

					SmFileChoose smFileChoose = new SmFileChoose(modelName);
					smFileChoose.setAcceptAllFileFilterUsed(true);
					int state = smFileChoose.showDefaultDialog();
					if (state == JFileChooser.APPROVE_OPTION) {
						if (null != fileType && fileType.equals(ISDIR)) {
							exchangeTableModel.addRow(smFileChoose.getSelectedFile().getAbsolutePath());
						} else {
							File[] files = smFileChoose.getSelectFiles();
							for (int i = 0, length = files.length; i < length; i++) {
								exchangeTableModel.addRow(files[i].getAbsolutePath());
							}
						}
						tableForObjectCirculation.setRowSelectionInterval(tableForObjectCirculation.getRowCount() - 1, tableForObjectCirculation.getRowCount() - 1);
					}
				}
			});
		}
		this.buttonSelectAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableForObjectCirculation.selectAll();
			}
		});

		this.buttonSelectInvert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selectedRows = tableForObjectCirculation.getSelectedRows();
				if (selectedRows.length > 0) {
					TableUtilities.stopEditing(tableForObjectCirculation);
					TableUtilities.invertSelection(tableForObjectCirculation);
				}
			}
		});

		this.buttonDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selectedRows = tableForObjectCirculation.getSelectedRows();
				if (selectedRows.length > 0) {
					// 多选删除时，从后往前删除
					for (int i = selectedRows.length - 1; i > -1; i--) {
						exchangeTableModel.removeRow(selectedRows[i]);
					}
					int selectedLastRow = selectedRows[selectedRows.length - 1];
					if (selectedLastRow <= tableForObjectCirculation.getRowCount() - 1) {
						tableForObjectCirculation.addRowSelectionInterval(selectedLastRow, selectedLastRow);
					} else {
						if (tableForObjectCirculation.getRowCount() != 0) {
							tableForObjectCirculation.addRowSelectionInterval(tableForObjectCirculation.getRowCount() - 1, tableForObjectCirculation.getRowCount() - 1);
						}
					}
				}
			}
		});
		this.buttonMoveTop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selectedRows = tableForObjectCirculation.getSelectedRows();

				if (selectedRows.length > 0 && selectedRows[0] > 0) {
					exchangeTableModel.moveTop(selectedRows);
					tableForObjectCirculation.clearSelection();
					int index = selectedRows[0];
					for (int selectedRow : selectedRows) {
						tableForObjectCirculation.addRowSelectionInterval(selectedRow - index, selectedRow - index);
					}
					selectedRows = tableForObjectCirculation.getSelectedRows();
					Rectangle cellRect = tableForObjectCirculation.getCellRect(selectedRows[0], 0, true);
					tableForObjectCirculation.scrollRectToVisible(cellRect);
				}
			}
		});
		this.buttonMoveUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selectedRows = tableForObjectCirculation.getSelectedRows();

				if (selectedRows.length > 0 && selectedRows[0] > 0) {
					exchangeTableModel.moveUp(selectedRows);
					tableForObjectCirculation.clearSelection();
					for (int selectedRow : selectedRows) {
						tableForObjectCirculation.addRowSelectionInterval(selectedRow - 1, selectedRow - 1);
					}
					selectedRows = tableForObjectCirculation.getSelectedRows();
					Rectangle cellRect = tableForObjectCirculation.getCellRect(selectedRows[0], 0, true);
					tableForObjectCirculation.scrollRectToVisible(cellRect);
				}
			}
		});

		this.buttonMoveDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selectedRows = tableForObjectCirculation.getSelectedRows();
				if (selectedRows.length > 0 && selectedRows[selectedRows.length - 1] < tableForObjectCirculation.getRowCount() - 1) {
					exchangeTableModel.moveDown(selectedRows);
					tableForObjectCirculation.clearSelection();
					for (int selectedRow : selectedRows) {
						tableForObjectCirculation.addRowSelectionInterval(selectedRow + 1, selectedRow + 1);
					}
					selectedRows = tableForObjectCirculation.getSelectedRows();
					Rectangle cellRect = tableForObjectCirculation.getCellRect(selectedRows[selectedRows.length - 1], 0, true);
					tableForObjectCirculation.scrollRectToVisible(cellRect);
				}
			}
		});

		this.buttonMoveBottom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selectedRows = tableForObjectCirculation.getSelectedRows();
				if (selectedRows.length > 0 && selectedRows[selectedRows.length - 1] < tableForObjectCirculation.getRowCount() - 1) {
					exchangeTableModel.moveBottom(selectedRows);
					int index = tableForObjectCirculation.getRowCount() - selectedRows[selectedRows.length - 1] - 1;
					tableForObjectCirculation.clearSelection();
					for (int selectedRow : selectedRows) {
						tableForObjectCirculation.addRowSelectionInterval(selectedRow + index, selectedRow + index);
					}
					selectedRows = tableForObjectCirculation.getSelectedRows();
					Rectangle cellRect = tableForObjectCirculation.getCellRect(selectedRows[selectedRows.length - 1], 0, true);
					tableForObjectCirculation.scrollRectToVisible(cellRect);
				}

			}
		});

		this.tableForObjectCirculation.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				checkButtonStates();
			}
		});
	}

	/**
	 * 设置各按钮是否可用
	 */
	private void checkButtonStates() {
		int rowCount = this.tableForObjectCirculation.getRowCount();
		int selectedRowCount = this.tableForObjectCirculation.getSelectedRowCount();
		this.buttonDelete.setEnabled(selectedRowCount > 0);
		this.buttonSelectAll.setEnabled(rowCount > 0);
		this.buttonSelectInvert.setEnabled(rowCount > 0);
		this.buttonMoveUp.setEnabled(selectedRowCount > 0 && !this.tableForObjectCirculation.isRowSelected(0));
		this.buttonMoveDown.setEnabled(selectedRowCount > 0 && !this.tableForObjectCirculation.isRowSelected(rowCount - 1));
		this.buttonMoveTop.setEnabled(selectedRowCount > 0 && !this.tableForObjectCirculation.isRowSelected(0));
		this.buttonMoveBottom.setEnabled(selectedRowCount > 0 && !this.tableForObjectCirculation.isRowSelected(rowCount - 1));
	}

	public JTable getTableForObjectCirculation() {
		return tableForObjectCirculation;
	}

	public ExchangeTableModel getExchangeTableModel() {
		return exchangeTableModel;
	}

	public ArrayList<String> getPathList() {
		return pathList;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public void addRow(Object rowInfo) {
		this.exchangeTableModel.addRow(rowInfo);
	}
}
