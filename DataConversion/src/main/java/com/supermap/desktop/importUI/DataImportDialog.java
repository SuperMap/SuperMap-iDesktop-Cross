package com.supermap.desktop.importUI;

import com.supermap.data.conversion.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IImportPanelFactory;
import com.supermap.desktop.Interface.IImportSettingFactory;
import com.supermap.desktop.Interface.IPanelImport;
import com.supermap.desktop.Interface.IPanelModel;
import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.dataconversion.DataConversionProperties;
import com.supermap.desktop.iml.*;
import com.supermap.desktop.implement.UserDefineType.ImportSettingExcel;
import com.supermap.desktop.implement.UserDefineType.ImportSettingGPX;
import com.supermap.desktop.localUtilities.CommonUtilities;
import com.supermap.desktop.localUtilities.FiletypeUtilities;
import com.supermap.desktop.localUtilities.LocalFileUtilities;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.tableModel.ImportTableModel;
import com.supermap.desktop.ui.UICommonToolkit;
import com.supermap.desktop.ui.controls.*;
import com.supermap.desktop.ui.controls.button.SmButton;
import com.supermap.desktop.ui.controls.progress.FormProgressTotal;
import com.supermap.desktop.utilities.CoreResources;
import com.supermap.desktop.utilities.FileUtilities;
import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * Created by xie on 2016/10/12.
 * 导入展示框
 */
public class DataImportDialog extends SmDialog implements IPanelModel {
	private JSplitPane splitPane;
	private JPanel contentPane;
	private JTable table;
	private ImportTableModel model;
	private JButton buttonAddFile;
	private JButton buttonAddDir;
	private JButton buttonDelete;
	private JButton buttonSelectAll;
	private JButton buttonInvertSelect;
	private SmButton buttonImport;
	private SmButton buttonClose;
	private JPanel panelFiles;
	private JLabel labelTitle;
	private JLabel labelRemind;
	private JPanel panelParams;
	private JPanel panelImportInfo;
	private JCheckBox checkBoxAutoClose;
	private JToolBar toolBar;
	private JScrollPane scrollPane;
	private ArrayList<PanelImport> panelImports;
	private SteppedComboBox steppedComboBox;
	private DropTarget dropTargetTemper;
	private PanelImport panelImportTemp;

	private final int GRID_TYPE = 1;
	private final int VERTICAL_TYPE = 2;
	private final int SAME_TYPE = 4;

	private static final int IMPORT_TEMSVECTORS = 0;
	private static final int IMPORT_TEMSBUILDINGVECTORS = 1;
	private static final int IMPORT_FILEGDBVECTORS = 2;
	private static final int IMPORT_GJB = 3;
	private static final int IMPORT_SIMPLEJSON = 4;

	private ActionListener addFileListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			addImportInfo();
		}
	};
	private KeyListener keyListener = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			if ((e.isControlDown() == true) && (e.getKeyCode() == KeyEvent.VK_A) && (1 <= table.getRowCount())) {
				// 键盘点击ctrl+A,全选
				replaceImportInfos(panelImports);
				return;
			}
			if (e.getKeyCode() == KeyEvent.VK_DELETE) {
				// 键盘点击delete,删除
				relesePanelImportTemp();
				deleteImportInfo();
				return;
			}

		}

		@Override
		public void keyReleased(KeyEvent e) {
			if ((e.isShiftDown() == true) && (e.getKeyCode() == KeyEvent.VK_UP) || ((e.isShiftDown() == true) && (e.getKeyCode() == KeyEvent.VK_DOWN))) {
				// 键盘点击shift+up/down刷新右边界面
				replaceImportInfos();
				return;
			} else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
				// 键盘点击up/down刷新右边界面
				relesePanelImportTemp();
				replaceImportInfo(table.getSelectedRow());
				return;
			}
		}
	};

	private ActionListener closeListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			removeEvents();
			panelImportsDispose();
			dispose();
		}
	};
	private ActionListener addDirListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// 添加文件夹
			try {
				int x = (int) buttonAddDir.getLocation().getX() - buttonAddDir.getWidth();
				int y = buttonAddDir.getHeight();
				AddDirDialog addDirDialog = new AddDirDialog();
				addDirDialog.show(buttonAddDir, x, y);
			} catch (Exception ex) {
				Application.getActiveApplication().getOutput().output(ex);
			}
			return;
		}
	};
	private ItemListener aListener;

	private void addImportInfo() {
		//获取并创建需要的导入信息类及导入界面信息类
		SmFileChoose fileChoose = LocalFileUtilities.createImportFileChooser();
		int state = fileChoose.showDefaultDialog();
		LocalFileUtilities.LastFileFilter = fileChoose.getFileFilter().getDescription();
		File[] files = fileChoose.getSelectFiles();
		String fileFilter = fileChoose.getFileFilter().getDescription();
		if (null != files && state == JFileChooser.APPROVE_OPTION) {
			initImportInfo(files, fileFilter);
		}
	}

	private void initImportInfo(File[] files, String fileFilter) {
		for (File file : files) {
			ImportInfo importInfo = new ImportInfo();
			String fileType = FileUtilities.getFileType(file.getPath());
			if (!StringUtilities.isNullOrEmpty(fileType)) {
				//导入信息参数设置
				importInfo.setFileName(file.getName());
				String filetype = FiletypeUtilities.getParseFile(file.getPath(), fileFilter);
				importInfo.setFiletype(filetype);
				importInfo.setState(DataConversionProperties.getString("string_change"));

				IImportSettingFactory importSettingFactory = new ImportSettingFactory();
				ImportSetting importSetting = importSettingFactory.createImportSetting(file.getPath(), fileFilter);
				if (null != importSetting) {
					importInfo.setImportSetting(importSetting);
					IImportPanelFactory importPanelFactory = new ImportPanelFactory();
					PanelImport panelImport = (PanelImport) importPanelFactory.createPanelImport(DataImportDialog.this, importInfo);
					panelImports.add(panelImport);
					model.addRow(importInfo);
				}
			}
		}
		if (panelImports.size() > 0) {
			table.setRowSelectionInterval(table.getRowCount() - 1, table.getRowCount() - 1);
			initComboBoxColumns();
			replaceImportInfo(table.getRowCount() - 1);
		}
	}

	private MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mouseReleased(MouseEvent e) {
			if (2 == e.getClickCount() && table.getSelectedColumn() != ImportTableModel.COLUMN_FILETYPE) {
				addImportInfo();
				return;
			} else {
				replaceImportPanel();
			}
		}
	};

	private void replaceImportPanel() {
		if (table.getSelectedRows().length == 1) {
			relesePanelImportTemp();
			replaceImportInfo(table.getSelectedRow());
		} else if (table.getSelectedRows().length > 1) {
			replaceImportInfos();
		}
	}

	private void relesePanelImportTemp() {
		if (null != panelImportTemp) {
			panelImportTemp.dispose();
			panelImportTemp = null;
		}
	}

	private void replaceImportInfos() {
		ArrayList<PanelImport> tempPanelImports = new ArrayList<PanelImport>();
		int[] selectRows = table.getSelectedRows();
		int size = selectRows.length;
		for (int i = 0; i < size; i++) {
			tempPanelImports.add(panelImports.get(selectRows[i]));
		}
		replaceImportInfos(tempPanelImports);
	}

	private void replaceImportInfos(ArrayList<PanelImport> panelImports) {
		ArrayList<PanelImport> tempPanelImports = panelImports;
		IImportPanelFactory importPanelFactory = new ImportPanelFactory();
		panelImportTemp = (PanelImport) importPanelFactory.createPanelImport(tempPanelImports);
		int panelImportType = panelImportTemp.getPanelImportType();
		if (panelImportType == SAME_TYPE) {
			if (panelImports.get(0).getImportInfo().getImportSetting() instanceof ImportSettingModel3DS) {
				labelTitle.setText(MessageFormat.format(DataConversionProperties.getString("String_ImportFill"), "Model3DS"));
			} else {
				String fileType = panelImports.get(0).getImportInfo().getImportSetting().getSourceFileType().toString();
				labelTitle.setText(MessageFormat.format(DataConversionProperties.getString("String_ImportFill"), fileType));
			}
		} else if (panelImportType == VERTICAL_TYPE) {
			labelTitle.setText(DataConversionProperties.getString("String_FormImportVector_Text"));
		} else if (panelImportType == GRID_TYPE) {
			labelTitle.setText(DataConversionProperties.getString("String_FormImportRaster_Text"));
		} else {
			labelTitle.setText(DataConversionProperties.getString("String_FormImportMix_Text"));
		}
		CommonUtilities.replace(panelImportInfo, panelImportTemp);
		importPanelFactory = null;
		tempPanelImports = null;
	}

	private void replaceImportInfo(int selectRow) {
		IPanelImport panelImport = panelImports.get(selectRow);
		if (null != panelImport.getImportInfo().getImportSetting()) {
			setImportTitle(panelImport.getImportInfo().getImportSetting());
			initComboBoxColumns();
			CommonUtilities.replace(panelImportInfo, (JPanel) panelImport);
			setButtonState();
		}
	}

	private ActionListener deleteListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			deleteImportInfo();
		}
	};

	private void deleteImportInfo() {
		try {
			if (null != table.getSelectedRows() && table.getSelectedRows().length != 0) {
				// 执行删除
				int[] selectedRow = table.getSelectedRows();
				if (!panelImports.isEmpty()) {
					ArrayList<JPanel> removePanel = new ArrayList<JPanel>();
					if (selectedRow.length < table.getRowCount()) {
						for (int i = 0; i < selectedRow.length; i++) {
							PanelImport panelImport = panelImports.get(selectedRow[i]);
							panelImport.removeEvents();
							removePanel.add(panelImport);
						}
						model.removeRows(selectedRow);
						panelImports.removeAll(removePanel);
					} else {
						int[] tempRow = new int[table.getRowCount()];
						for (int i = 0; i < table.getRowCount(); i++) {
							PanelImport panelImport = panelImports.get(i);
							panelImport.removeEvents();
							tempRow[i] = i;
						}
						model.removeRows(tempRow);
						panelImports.removeAll(panelImports);
						table.clearSelection();
					}
				}
				// 如果表中没有数据，右边部分显示为默认界面。
				if (panelImports.isEmpty()) {
					labelTitle.setText(DataConversionProperties.getString("string_label_importData"));
					CommonUtilities.replace(panelImportInfo, panelParams);
					setButtonState();
				} else {
					if (selectedRow[0] != table.getRowCount()) {
						table.setRowSelectionInterval(selectedRow[0], selectedRow[0]);
					} else {
						table.setRowSelectionInterval(selectedRow[0] - 1, selectedRow[0] - 1);
					}
					replaceImportInfo(table.getSelectedRow());
				}
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	private ActionListener selectAllListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				CommonUtilities.selectAll(table);
				setButtonState();
				replaceImportPanel();
			} catch (Exception ex) {
				Application.getActiveApplication().getOutput().output(ex);
			}
		}
	};
	private ActionListener invertSelectListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				CommonUtilities.invertSelect(table);
				if (table.getRowCount() == 0) {
					//没有要导入的项时
					labelTitle.setText(DataConversionProperties.getString("string_label_importData"));
					CommonUtilities.replace(panelImportInfo, panelParams);
				} else {
					replaceImportPanel();
				}
				setButtonState();
			} catch (Exception ex) {
				Application.getActiveApplication().getOutput().output(ex);
			}
		}
	};
	private ActionListener importListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				// 导入表中数据
				if (table.getRowCount() > 0) {
					final FormProgressTotal formProgress = new FormProgressTotal();
					formProgress.setTitle(DataConversionProperties.getString("String_FormImport_FormText"));
					ArrayList<ImportInfo> fileInfos = new ArrayList<ImportInfo>();
					for (IPanelImport panelImport : panelImports) {
						fileInfos.add(panelImport.getImportInfo());
					}
					if (null != formProgress) {
						formProgress.doWork(new ImportCallable(fileInfos, DataImportDialog.this));
					}
				} else {
					UICommonToolkit.showMessageDialog(DataConversionProperties.getString("String_ImportSettingPanel_Cue_AddFiles"));
				}
			} catch (Exception ex) {
				Application.getActiveApplication().getOutput().output(ex);
			} finally {
				if (checkBoxAutoClose.isSelected()) {
					panelImportsDispose();
					dispose();
				}
			}
		}
	};

	private void panelImportsDispose() {
		for (IPanelImport panelImportTemp : panelImports) {
			panelImportTemp = null;
		}

		panelImports = null;
	}

	public DataImportDialog() {
		super();
		initComponents();
		initLayerout();
		registEvents();
		initResources();
		initDrag();
		this.buttonAddFile.setSelected(false);
		this.componentList.add(this.buttonImport);
		this.componentList.add(this.buttonClose);
		this.setFocusTraversalPolicy(this.policy);
		this.getRootPane().setDefaultButton(this.buttonImport);
		this.setLocationRelativeTo(null);
		this.setSize(new Dimension(960, 540));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(Math.abs(screenSize.width - 864) / 2, Math.abs(screenSize.height - 486) / 2);
		setComponentName();
	}

	private void initDrag() {

		dropTargetTemper = new DropTarget(this, new TableDropTargetAdapter(this));

	}

	@Override
	public void initComponents() {
		this.panelImports = new ArrayList<>();
		this.splitPane = new JSplitPane();
		this.contentPane = new JPanel();
		this.table = new JTable();
		this.model = new ImportTableModel();
		this.table.getTableHeader().setReorderingAllowed(false);
		this.table.setModel(this.model);
		this.table.setRowHeight(23);
		this.buttonAddFile = new JButton();
		this.buttonAddDir = new JButton();
		this.buttonDelete = new JButton();
		this.buttonSelectAll = new JButton();
		this.buttonInvertSelect = new JButton();
		this.buttonImport = new SmButton("import");
		this.buttonClose = new SmButton("close");
		this.panelFiles = new JPanel();
		this.labelTitle = new JLabel();
		this.labelRemind = new JLabel();
		this.panelParams = new JPanel();
		this.panelImportInfo = new JPanel();
		this.checkBoxAutoClose = new JCheckBox();
		this.toolBar = new JToolBar();
		initToolbar();
		initTableTheme();
		this.scrollPane = new JScrollPane();
		this.buttonInvertSelect.setEnabled(false);
		this.checkBoxAutoClose.setSelected(true);
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(contentPane);
		this.labelTitle.setBackground(Color.LIGHT_GRAY);
		this.labelTitle.setHorizontalAlignment(SwingConstants.CENTER);
		this.labelTitle.setOpaque(true);
	}

	public void setComponentName() {
		ComponentUIUtilities.setName(this.splitPane, "DataImportDialog_splitPane");
		ComponentUIUtilities.setName(this.contentPane, "DataImportDialog_contentPane");
		ComponentUIUtilities.setName(this.table, "DataImportDialog_table");
		ComponentUIUtilities.setName(this.buttonAddFile, "DataImportDialog_buttonAddFile");
		ComponentUIUtilities.setName(this.buttonAddDir, "DataImportDialog_buttonAddDir");
		ComponentUIUtilities.setName(this.buttonDelete, "DataImportDialog_buttonDelete");
		ComponentUIUtilities.setName(this.buttonSelectAll, "DataImportDialog_buttonSelectAll");
		ComponentUIUtilities.setName(this.buttonInvertSelect, "DataImportDialog_buttonInvertSelect");
		ComponentUIUtilities.setName(this.buttonImport, "DataImportDialog_buttonImport");
		ComponentUIUtilities.setName(this.buttonClose, "DataImportDialog_buttonClose");
		ComponentUIUtilities.setName(this.panelFiles, "DataImportDialog_panelFiles");
		ComponentUIUtilities.setName(this.labelTitle, "DataImportDialog_labelTitle");
		ComponentUIUtilities.setName(this.labelRemind, "DataImportDialog_labelRemind");
		ComponentUIUtilities.setName(this.panelParams, "DataImportDialog_panelParams");
		ComponentUIUtilities.setName(this.panelImportInfo, "DataImportDialog_panelImportInfo");
		ComponentUIUtilities.setName(this.checkBoxAutoClose, "DataImportDialog_checkBoxAutoClose");
		ComponentUIUtilities.setName(this.toolBar, "DataImportDialog_toolBar");
		ComponentUIUtilities.setName(this.scrollPane, "DataImportDialog_scrollPane");
	}

	private void initTableTheme() {
		TableColumn fileNameColumn = table.getColumn(table.getModel().getColumnName(ImportTableModel.COLUMN_FILENAME));
		TableColumn fileTypeColumn = table.getColumn(table.getModel().getColumnName(ImportTableModel.COLUMN_FILETYPE));
		TableColumn stateColumn = table.getColumn(table.getModel().getColumnName(ImportTableModel.COLUMN_STATE));
		fileNameColumn.setCellRenderer(TableTooltipCellRenderer.getInstance());
		fileTypeColumn.setCellRenderer(TableTooltipCellRenderer.getInstance());
		fileTypeColumn.setMinWidth(120);
		stateColumn.setMaxWidth(50);
		stateColumn.setCellRenderer(TableTooltipCellRenderer.getInstance());
	}

	private void initToolbar() {
		this.toolBar.setFloatable(false);
		this.toolBar.add(this.buttonAddFile);
		this.toolBar.add(this.buttonAddDir);
		this.buttonDelete.setEnabled(false);
		this.toolBar.addSeparator();
		this.toolBar.add(this.buttonDelete);
		this.toolBar.addSeparator();
		this.buttonSelectAll.setEnabled(false);
		this.toolBar.add(this.buttonSelectAll);
		this.toolBar.add(this.buttonInvertSelect);
	}

	@Override
	public void initLayerout() {
		initPanelFilesLayerout();
		initPanelImportInfoLayerout();
		initPanelContentLayerout();
	}

	private void initPanelContentLayerout() {
		this.splitPane = new JSplitPane();
		this.splitPane.setContinuousLayout(true);
		this.splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		this.splitPane.setLeftComponent(this.panelFiles);
		this.splitPane.setRightComponent(this.panelImportInfo);
		this.splitPane.setBorder(null);
		this.contentPane.setLayout(new BorderLayout());
		this.contentPane.add(this.splitPane, BorderLayout.CENTER);
	}

	private void initPanelImportInfoLayerout() {
		this.panelImportInfo.setLayout(new GridBagLayout());
		JPanel pane = new JPanel();
		pane.setLayout(new GridBagLayout());
		this.panelImportInfo.add(this.labelTitle, new GridBagConstraintsHelper(0, 0, 2, 1).setAnchor(GridBagConstraints.CENTER).setFill(GridBagConstraints.HORIZONTAL).setInsets(5).setWeight(1, 0));
		this.panelImportInfo.add(pane, new GridBagConstraintsHelper(0, 1, 2, 1).setAnchor(GridBagConstraints.NORTH).setFill(GridBagConstraintsHelper.BOTH).setWeight(1, 1));
		this.panelImportInfo.add(this.buttonImport, new GridBagConstraintsHelper(0, 2, 1, 1).setAnchor(GridBagConstraints.EAST).setInsets(5, 0, 5, 10).setWeight(1, 0));
		this.panelImportInfo.add(this.buttonClose, new GridBagConstraintsHelper(1, 2, 1, 1).setAnchor(GridBagConstraints.EAST).setInsets(5, 0, 5, 5).setWeight(0, 0));
		this.labelTitle.setPreferredSize(new Dimension(200, 30));
		this.labelTitle.setMinimumSize(new Dimension(200, 30));
		pane.setBorder(null);
		pane.add(panelParams, new GridBagConstraintsHelper(0, 0).setAnchor(GridBagConstraints.CENTER).setWeight(1, 1).setFill(GridBagConstraints.BOTH));
		this.panelParams.setLayout(new GridBagLayout());
		this.panelParams.add(this.labelRemind, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.CENTER));
		this.buttonImport.setEnabled(false);
	}

	private void initPanelFilesLayerout() {
		this.panelFiles.setLayout(new GridBagLayout());
		this.panelFiles.add(this.toolBar, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(5).setWeight(0, 0));
		this.panelFiles.add(this.scrollPane, new GridBagConstraintsHelper(0, 1, 1, 1).setAnchor(GridBagConstraints.CENTER).setInsets(0, 5, 5, 5).setWeight(3, 3).setFill(GridBagConstraints.BOTH));
		this.panelFiles.add(this.checkBoxAutoClose, new GridBagConstraintsHelper(0, 2, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 5).setWeight(0, 0));
		this.scrollPane.setViewportView(this.table);
	}

	@Override
	public void registEvents() {
		removeEvents();
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				splitPane.setDividerLocation(0.35);
				splitPane.setResizeWeight(0.35);
			}

			@Override
			public void windowClosing(WindowEvent e) {
				panelImportsDispose();
				removeEvents();
			}
		});
		this.buttonAddFile.addActionListener(this.addFileListener);
		this.buttonDelete.addActionListener(this.deleteListener);
		this.buttonSelectAll.addActionListener(this.selectAllListener);
		this.buttonInvertSelect.addActionListener(this.invertSelectListener);
		this.buttonImport.addActionListener(this.importListener);
		this.table.addMouseListener(this.mouseListener);
		this.table.addKeyListener(this.keyListener);
		this.scrollPane.addMouseListener(this.mouseListener);
		this.buttonClose.addActionListener(this.closeListener);
		this.buttonAddDir.addActionListener(this.addDirListener);
	}

	@Override
	public void removeEvents() {
		this.buttonAddFile.removeActionListener(this.addFileListener);
		this.buttonDelete.removeActionListener(this.deleteListener);
		this.buttonSelectAll.removeActionListener(this.selectAllListener);
		this.buttonInvertSelect.removeActionListener(this.invertSelectListener);
		this.buttonImport.removeActionListener(this.importListener);
		this.table.removeMouseListener(this.mouseListener);
		this.table.removeKeyListener(this.keyListener);
		this.scrollPane.removeMouseListener(this.mouseListener);
	}

	private void initResources() {
		this.setTitle(DataConversionProperties.getString("String_FormImport_FormText"));
		this.buttonAddDir.setIcon(CoreResources.getIcon("/coreresources/ToolBar/Image_ToolButton_Add.png"));
		this.buttonAddFile.setIcon(CoreResources.getIcon("/coreresources/ToolBar/Image_ToolButton_AddItem.png"));
		this.buttonSelectAll.setIcon(CoreResources.getIcon("/coreresources/ToolBar/Image_ToolButton_SelectAll.png"));
		this.buttonInvertSelect.setIcon(CoreResources.getIcon("/coreresources/ToolBar/Image_ToolButton_SelectInverse.png"));
		this.buttonDelete.setIcon(CoreResources.getIcon("/coreresources/ToolBar/Image_ToolButton_Delete.png"));
		this.buttonImport.setText(CoreProperties.getString("String_Button_Import"));
		this.buttonClose.setText(CoreProperties.getString("String_Button_Close"));
		this.checkBoxAutoClose.setText(DataConversionProperties.getString("string_chcekbox_autoCloseIn"));
		this.labelTitle.setText(DataConversionProperties.getString("string_label_importData"));
		this.labelRemind.setText(DataConversionProperties.getString("string_label_addFileRemind"));
		this.buttonAddDir.setToolTipText(DataConversionProperties.getString("String_button_addDir"));
		this.buttonAddFile.setToolTipText(DataConversionProperties.getString("String_button_addFile"));
		this.buttonDelete.setToolTipText(DataConversionProperties.getString("string_button_delete"));
		this.buttonSelectAll.setToolTipText(DataConversionProperties.getString("string_button_selectAll"));
		this.buttonInvertSelect.setToolTipText(DataConversionProperties.getString("string_button_invertSelect"));
		this.labelRemind.setForeground(new Color(32, 178, 170));
	}

	/**
	 * 设置按键状态
	 */
	private void setButtonState() {
		if (0 < table.getRowCount()) {
			this.buttonImport.setEnabled(true);
			this.buttonSelectAll.setEnabled(true);
			this.buttonInvertSelect.setEnabled(true);
		} else {
			this.buttonImport.setEnabled(false);
			this.buttonSelectAll.setEnabled(false);
			this.buttonInvertSelect.setEnabled(false);
		}
		if (table.getSelectedRows().length > 0) {
			this.buttonDelete.setEnabled(true);
		} else {
			this.buttonDelete.setEnabled(false);
		}
	}

	public JTable getTable() {
		return table;
	}

	public JCheckBox getCheckBoxAutoClose() {
		return checkBoxAutoClose;
	}

	/**
	 * 为table添加下拉列表
	 */
	@SuppressWarnings("unchecked")
	public void initComboBoxColumns() {
		TableRowCellEditor rowEditor = new TableRowCellEditor(this.table);
		for (int i = 0; i < this.panelImports.size(); i++) {
			final ImportInfo tempFileInfo = this.panelImports.get(i).getImportInfo();
			if (null == tempFileInfo.getImportSetting()) {
				return;
			}
			this.steppedComboBox = new SteppedComboBox(new String[]{});
			CommonUtilities.setComboBoxTheme(this.steppedComboBox);
			this.steppedComboBox.removeAllItems();
			String filePath = tempFileInfo.getImportSetting().getSourceFilePath();
			String fileType = FileUtilities.getFileType(filePath);
			if (fileType.equalsIgnoreCase(FileTypeLocale.DXF_STRING)) {
				this.steppedComboBox.addItem(DataConversionProperties.getString("String_FormImport_CAD"));
				this.steppedComboBox.addItem(DataConversionProperties.getString("String_FormImport_FilterModel"));
			} else if (fileType.equals(FileTypeLocale.BIL_STRING)) {
				this.steppedComboBox.addItem(DataConversionProperties.getString("String_FormImport_GRID"));
				this.steppedComboBox.addItem(DataConversionProperties.getString("String_FormImport_BIL"));
			} else if (fileType.equalsIgnoreCase(FileTypeLocale.DEM_STRING)) {
				steppedComboBox.addItem(DataConversionProperties.getString("String_FormImport_ArcGIS"));
				steppedComboBox.addItem(DataConversionProperties.getString("String_FormImport_DEM"));
				steppedComboBox.addItem(DataConversionProperties.getString("String_FormImport_GBDEM"));
			} else if (fileType.equalsIgnoreCase(FileTypeLocale.TXT_STRING)) {
				this.steppedComboBox.addItem(DataConversionProperties.getString("String_FormImport_ArcGIS"));
				this.steppedComboBox.addItem(DataConversionProperties.getString("String_FormImport_FilterLIDAR"));
			} else {
				this.steppedComboBox.addItem(tempFileInfo.getFileType());
			}
			Dimension d = this.steppedComboBox.getPreferredSize();
			this.steppedComboBox.setPreferredSize(new Dimension(d.width, d.height));
			this.steppedComboBox.setPopupWidth(d.width);

			rowEditor.setEditorAt(i, new DefaultCellEditor(steppedComboBox));
			this.table.getColumn(DataConversionProperties.getString("string_tabletitle_filetype")).setCellEditor(rowEditor);
			this.aListener = new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						String nowType = e.getItem().toString();
						tempFileInfo.setFiletype(nowType);
						ImportSetting newImportSetting = null;
						if (nowType.equalsIgnoreCase(DataConversionProperties.getString("String_FormImport_BIL"))) {
							// bil 导入为BIL文件
							newImportSetting = new ImportSettingBIL();
							replaceImportPanel(newImportSetting);
						} else if (nowType.equalsIgnoreCase(DataConversionProperties.getString("String_FormImport_GRID"))) {
							// 导入为电信栅格文件
							newImportSetting = new ImportSettingTEMSClutter();
							replaceImportPanel(newImportSetting);
						} else if (nowType.equalsIgnoreCase(DataConversionProperties.getString("String_FormImport_CAD"))) {
							// DXF 导入为CAD格式文件
							newImportSetting = new ImportSettingDXF();
							replaceImportPanel(newImportSetting);
						} else if (nowType.equalsIgnoreCase(DataConversionProperties.getString("String_FormImport_FilterModel"))) {
							// DXF 导入为三维模型文件
							newImportSetting = new ImportSettingModelDXF();
							replaceImportPanel(newImportSetting);
						} else if (nowType.equalsIgnoreCase(DataConversionProperties.getString("String_FormImport_ArcGIS"))) {
							// txt,dem 导入为ArcGIS类型文件
							newImportSetting = new ImportSettingGRD();
							replaceImportPanel(newImportSetting);
						} else if (nowType.equalsIgnoreCase(DataConversionProperties.getString("String_FormImport_FilterLIDAR"))) {
							// txt 导入为LIDAR类型文件
							newImportSetting = new ImportSettingLIDAR();
							replaceImportPanel(newImportSetting);
						} else if (nowType.equalsIgnoreCase(DataConversionProperties.getString("String_FormImport_DEM"))) {
							// dem 导入为美国标准DEM文件
							newImportSetting = new ImportSettingUSGSDEM();
							replaceImportPanel(newImportSetting);
						} else if (nowType.equalsIgnoreCase(DataConversionProperties.getString("String_FormImport_GBDEM"))) {
							// dem 导入为中国标准DEM文件
							newImportSetting = new ImportSettingGBDEM();
							replaceImportPanel(newImportSetting);
						}
					}
				}

				private void replaceImportPanel(ImportSetting newImportSetting) {
					IImportPanelFactory importPanelFactory = new ImportPanelFactory();
					ImportSetting oldImportSetting = tempFileInfo.getImportSetting();
					String filePath = oldImportSetting.getSourceFilePath();
					newImportSetting.setTargetDatasource(oldImportSetting.getTargetDatasource());
					newImportSetting.setSourceFilePath(filePath);
					newImportSetting.setTargetDatasetName(oldImportSetting.getTargetDatasetName());
					tempFileInfo.setImportSetting(newImportSetting);
					PanelImport panelImport = (PanelImport) importPanelFactory.createPanelImport(DataImportDialog.this, tempFileInfo);
					setImportTitle(newImportSetting);
					int selectRow = table.getSelectedRow();
					panelImports.remove(selectRow);
					panelImports.add(selectRow, panelImport);
					CommonUtilities.replace(panelImportInfo,panelImport);
				}
			};
			this.steppedComboBox.addItemListener(this.aListener);
		}
	}

	private void setImportTitle(ImportSetting importSetting) {
		if (importSetting instanceof ImportSettingExcel) {
			labelTitle.setText(MessageFormat.format(DataConversionProperties.getString("String_ImportFill"), "EXCEL"));
		} else if (importSetting instanceof ImportSettingModel3DS) {
			labelTitle.setText(MessageFormat.format(DataConversionProperties.getString("String_ImportFill"), "Model3DS"));
		} else if (importSetting instanceof ImportSettingGPX) {
			//If you want to replace your title for import file,change the message
			labelTitle.setText(MessageFormat.format(DataConversionProperties.getString("String_ImportFill"), "GPS"));
		} else {
			labelTitle.setText(MessageFormat.format(DataConversionProperties.getString("String_ImportFill"), importSetting.getSourceFileType().toString()));
		}
	}

	class TableDropTargetAdapter extends DropTargetAdapter {
		private DataImportDialog dataImportDialog;

		public TableDropTargetAdapter(DataImportDialog dataImportDialog) {
			this.dataImportDialog = dataImportDialog;
		}

		@Override
		public void drop(DropTargetDropEvent dtde) {
			try {
				if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					dtde.acceptDrop(DnDConstants.ACTION_REFERENCE);// 接收拖拽来的数据
					// 将文件添加到导入界面
					@SuppressWarnings("unchecked")
					java.util.List<File> files = (java.util.List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					initImportInfo(files.toArray(new File[files.size()]), "");
				}
			} catch (Exception e) {
				Application.getActiveApplication().getOutput().output(e);
			}
		}
	}

	class AddDirDialog extends JPopupMenu {

		JList<String> list = new JList<String>();

		public AddDirDialog() {
			initComponent();
		}

		private void initComponent() {
			this.setPreferredSize(new Dimension(240, 160));
			JScrollPane pane = new JScrollPane();
			this.setLayout(new GridBagLayout());
			this.add(pane, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.CENTER).setFill(GridBagConstraints.BOTH).setWeight(1, 1));
			pane.setViewportView(list);
			pane.setBorder(null);
			DefaultListModel<String> listModel = new DefaultListModel<String>();
			listModel.addElement(DataConversionProperties.getString("String_ImportTEMSVectors"));
			listModel.addElement(DataConversionProperties.getString("String_ImportTEMSBuildingVectors"));
			listModel.addElement(DataConversionProperties.getString("String_ImportFileGDBVectors"));
			listModel.addElement(DataConversionProperties.getString("String_ImportGJB"));
			listModel.addElement(DataConversionProperties.getString("String_ImportSimpleJson"));
			list.setModel(listModel);
			list.addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {
					int selectIndex = list.getSelectedIndex();
					initImportDirectoryInfo(selectIndex);
				}
			});

		}
	}

	private void initImportDirectoryInfo(int type) {
		if (!SmFileChoose.isModuleExist("DataImportFrame_ImportDirectories")) {
			SmFileChoose.addNewNode("", "", DataConversionProperties.getString("String_ScanDir"),
					"DataImportFrame_ImportDirectories", "GetDirectories");
		}
		SmFileChoose tempfileChooser = new SmFileChoose("DataImportFrame_ImportDirectories");
		tempfileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int state = tempfileChooser.showDefaultDialog();
		if (state == JFileChooser.APPROVE_OPTION) {
			String directories = tempfileChooser.getFilePath();
			ImportInfo importInfo = new ImportInfo();
			ImportSetting importSetting = createImportSetting(importInfo, type);
			importSetting.setSourceFilePath(directories);
			importInfo.setImportSetting(importSetting);
			importInfo.setFileName(directories);
			importInfo.setState(DataConversionProperties.getString("string_change"));
			IImportPanelFactory importPanelFactory = new ImportPanelFactory();
			PanelImport panelImport = (PanelImport) importPanelFactory.createPanelImport(DataImportDialog.this, importInfo);
			panelImports.add(panelImport);
			model.addRow(importInfo);
			if (panelImports.size() > 0) {
				table.setRowSelectionInterval(table.getRowCount() - 1, table.getRowCount() - 1);
				setImportTitle(importSetting);
				initComboBoxColumns();
				CommonUtilities.replace(panelImportInfo, panelImport);
				setButtonState();
			}
		}
	}

	private ImportSetting createImportSetting(ImportInfo importInfo, int type) {
		ImportSetting result = null;
		switch (type) {
			case IMPORT_TEMSVECTORS:
				result = new ImportSettingTEMSVector();
				((ImportSettingTEMSVector) result).setImportEmptyDataset(true);
				importInfo.setFiletype(DataConversionProperties.getString("String_ImportTEMSVector"));
				break;
			case IMPORT_TEMSBUILDINGVECTORS:
				result = new ImportSettingTEMSBuildingVector();
				((ImportSettingTEMSBuildingVector) result).setImportEmptyDataset(true);
				importInfo.setFiletype(DataConversionProperties.getString("String_ImportTEMSBuildingVector"));
				break;
			case IMPORT_FILEGDBVECTORS:
				result = new ImportSettingFileGDBVector();
				((ImportSettingFileGDBVector) result).setImportEmptyDataset(true);
				importInfo.setFiletype(DataConversionProperties.getString("String_ImportFileGDBVector"));
				break;
			case IMPORT_GJB:
				result = new ImportSettingGJB();
				((ImportSettingGJB) result).setImportEmptyDataset(true);
				importInfo.setFiletype("GJB5068");
				break;
			case IMPORT_SIMPLEJSON:
				result = new ImportSettingSimpleJson();
				((ImportSettingSimpleJson) result).setImportEmptyDataset(true);
				importInfo.setFiletype(DataConversionProperties.getString("String_ImportSimpleJson"));
				break;
			default:
				break;
		}
		if (null != Application.getActiveApplication().getActiveDatasources() && Application.getActiveApplication().getActiveDatasources().length >= 0) {
			result.setTargetDatasource(Application.getActiveApplication().getActiveDatasources()[0]);
		}
		return result;
	}

}
