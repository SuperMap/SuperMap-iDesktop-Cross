package com.supermap.desktop.ui.controls.prjcoordsys;

import com.supermap.data.Enum;
import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.ControlsResources;
import com.supermap.desktop.controls.utilities.JTreeUIUtilities;
import com.supermap.desktop.properties.CommonProperties;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.UICommonToolkit;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.ui.controls.SmDialog;
import com.supermap.desktop.ui.controls.SmFileChoose;
import com.supermap.desktop.ui.controls.TextFieldSearch;
import com.supermap.desktop.ui.controls.button.SmButton;
import com.supermap.desktop.ui.controls.prjcoordsys.prjCoordSysSettingPanels.AbstractPrjTableModel;
import com.supermap.desktop.ui.controls.prjcoordsys.prjCoordSysSettingPanels.CoordSysDefine;
import com.supermap.desktop.ui.controls.prjcoordsys.prjCoordSysSettingPanels.PrjCoordSysTableModel;
import com.supermap.desktop.utilities.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static com.supermap.desktop.ui.controls.prjcoordsys.XMLProjectionTag.GEOCOORDSYS_CAPTION;

// @formatter:off

/**
 * 先不做自定义投影 投影描述的文件是从 iDesktop .NET 迁移过来的，而 Java版的实现与投影描述文件的结构略有不同。
 * 在配置文件中，分组信息是以 GroupCaption 子节点的形式写在了每一个定义里，它们是同级平行关系，
 * 而在本类的实现中， 分组与子项是上下层级关系。
 *
 * @author highsad
 * 优化：支持树节点的定制-yuanR2017.10.18
 * 功能丰富：依照.net，对投影设置面板进行重构-yuanR2017.10.24
 */
// @formatter:on
public class JDialogPrjCoordSysSettings extends SmDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_PROJECTION_CONFIG_PATH = "/controlsresources/Projection.xml";
	private static final String DEFAULT_GROUPCAPTION = "Default";

	private JToolBar toolBar;
	private JLabel labelPath;
	private JTextField textFieldPath;
	private TextFieldSearch textFieldSearch;

	private JToolBar toolBarButton;
	private JButton buttonImport;
	private JButton buttonExport;
	private JButton buttonFavorites;
	private JButton buttonNewCoordSys;
	private JPopupMenu popupMenuNewCoordSys;
	private JMenuItem menuItemNewPrjCoordSys;
	private JMenuItem menuItemNewGeoCoordSys;
	private JMenuItem menuItemNewFormEPSG;
	private JButton buttonNewGroup;
	private JButton buttonDelete;

	private JSplitPane splitPaneMain; // 整个投影选择区域的主面板
	private JTree treePrjCoordSys; // 读取加载投影信息的树，主面板左边区域
	private JSplitPane splitPaneDetails; // 读取加载选中树节点的具体内容，以及选定投影详细信息的面板，主面板右边区域
	private JTable tablePrjCoordSys; // 读取加载选中树节点的具体内容的 Table，主面板右边区域上半区域
	private JTextArea textAreaDetail; // 显示选定投影详细信息，主面板右边区域下半区域

	private SmButton buttonApply;
	private SmButton buttonClose;

	// 平面坐标系定义集合
	private transient CoordSysDefine noneEarth = new CoordSysDefine(CoordSysDefine.NONE_ERRTH, null,
			ControlsProperties.getString("String_NoneEarth")).setFolderNode(true);
	// 投影坐标系统定义集合
	private transient CoordSysDefine projectionSystem = new CoordSysDefine(CoordSysDefine.PROJECTION_SYSTEM, null,
			ControlsProperties.getString("String_PrjCoorSys")).setFolderNode(true);
	// 地理坐标系定义集合
	private transient CoordSysDefine geographyCoordinate = new CoordSysDefine(CoordSysDefine.GEOGRAPHY_COORDINATE, null,
			ControlsProperties.getString("String_GeoCoordSys")).setFolderNode(true);
	// 自定义坐标系集合
	private transient CoordSysDefine customCoordinate = new CoordSysDefine(CoordSysDefine.CUSTOM_COORDINATE, null,
			ControlsProperties.getString("String_Customize")).setFolderNode(true);
	// 收藏夹坐标系集合
	private transient CoordSysDefine favoriteCoordinate = new CoordSysDefine(CoordSysDefine.FAVORITE_COORDINATE, null,
			ControlsProperties.getString("String_MyFavorite")).setFolderNode(true);

	// 当前选中的坐标系
	private transient CoordSysDefine currentPrjDefine = null;
	private transient PrjCoordSys prjCoordSys = null;

	private String projectionConfigPath = "";
	private String customProjectionConfigPath = "";
	private String favoriteProjectionConfigPath = "";
	private transient Document projectionDoc = null;
	private transient Document customProjectionDoc = null;
	private transient Document favoriteProjectionDoc = null;

	private PrjCoordSysTableModel prjModel = new PrjCoordSysTableModel();

	// table和tree的右键菜单
	private JPopupMenu popupmenu;
	private JMenu menuNewCoordsys;
	private JMenuItem menuItemNewGroup;
	private JMenuItem menuItemImportCoordSys;
	private JMenuItem menuItemExportCoordSys;
	private JMenuItem menuItemAddFavorites;
	private JMenuItem menuItemDelete;

	// 这个意义何在？
	private CoordSysDefine currentRowData;
	private CoordSysDefine rootDefine;
	private String userDefineGeoParentName = "UserGeoCoordsys";
	private String userDefinePrjParentName = "UserPrjCoordsys";
	private String userImportCoordsysParentName = "UserImportCoordsys";

	private transient TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			if (e.getSource() == treePrjCoordSys) {
				treeSelectionChange();
			}
		}
	};
	private transient ListSelectionListener listSelectionListener = new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				tableSelectionChange();
			}
		}
	};
	private transient MouseAdapter mouseAdapter = new MouseAdapter() {

		@Override
		public void mousePressed(MouseEvent e) {
			if ((e.getSource() == tablePrjCoordSys.getParent() || e.getSource() == tablePrjCoordSys) && e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 1) {
				tableMouseRightClicked(e);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {

			if (e.getButton() != MouseEvent.BUTTON1 || e.getClickCount() != 2) {
				return;
			}

			if (e.getSource() == tablePrjCoordSys) {
				tableMouseDoubleClick(e);
			} else if (e.getSource() == treePrjCoordSys) {
				treeMouseDoubleClick(e);
			}
		}
	};

	private transient ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == buttonApply) {
				buttonApplyClicked();
			} else if (e.getSource() == buttonClose) {
				buttonCloseClicked();
			} else if (e.getSource() == buttonImport || e.getSource() == menuItemImportCoordSys) {
				if (isImportEnable()) {
					String moduleName = "ImportPrjFile";
					if (!SmFileChoose.isModuleExist(moduleName)) {
						String fileFilters = SmFileChoose.buildFileFilters(
								SmFileChoose.createFileFilter(ControlsProperties.getString("String_ImportPrjFiles"), "prj", "xml"),
								SmFileChoose.createFileFilter(ControlsProperties.getString("String_ImportPrjFileShape"), "prj"),
								SmFileChoose.createFileFilter(ControlsProperties.getString("String_ImportPrjFileXml"), "xml"));
						SmFileChoose.addNewNode(fileFilters, CommonProperties.getString("String_DefaultFilePath"),
								ControlsProperties.getString("String_ImportPrjFile"), moduleName, "OpenMany");
					}
					SmFileChoose prjFileImportFileChoose = new SmFileChoose(moduleName);
					if (prjFileImportFileChoose.showDefaultDialog() == JFileChooser.APPROVE_OPTION) {
						File file = prjFileImportFileChoose.getSelectedFile();
						importCoordsys(getPrjCoordSysFromImportFile(file.getPath()));
					}
				}
			} else if (e.getSource() == buttonExport || e.getSource() == menuItemExportCoordSys) {
				if (isExportEnable()) {
					exportCoordsys();
				}
			} else if (e.getSource() == buttonNewCoordSys) {
				popupMenuNewCoordSys.show(buttonNewCoordSys, 0, buttonNewCoordSys.getHeight());
			} else if (e.getSource() == menuItemNewPrjCoordSys) {
				newPrjCoordsys();
			} else if (e.getSource() == menuItemNewGeoCoordSys) {
				newGeoCoordsys();
			} else if (e.getSource() == menuItemNewFormEPSG) {
				System.out.println("menuItemNewFormEPSG");

			} else if (e.getSource() == buttonFavorites || e.getSource() == menuItemAddFavorites) {
				// 将选中的投影添加到收藏夹当中
				addCoordsysToFavorites();
			} else if (e.getSource() == menuItemDelete || e.getSource() == buttonDelete) {
				deleteCoordsys();
			} else if (e.getSource() == buttonNewGroup || e.getSource() == menuItemNewGroup) {

			} else if (e.getSource() == textFieldSearch) {
				textFieldSearchAction();
			}
		}
	};


	private transient DocumentListener documentListener = new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent e) {
			textFieldSearchAction();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			textFieldSearchAction();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			textFieldSearchAction();
		}
	};

	/**
	 * Create the dialog.
	 */
	public JDialogPrjCoordSysSettings() {
		try {
			initializeComponents();
			initializeResources();
			// 加载默认投影配置文件
			loadProjectionConfig();
			// 加载自定义投影配置文件
			loadCustomProjectionConfig();
			// 加载收藏投影配置文件
			loadFavoriteProjectionConfig();
			// 构建平面坐标系定义数据
			buildNoneEarthDefines();
			// 构建投影坐标系统定义数据
			buildProjectionSystemDefines();
			// 构建地理坐标系定义数据
			buildGeographyCoordinateDefines();
			// 构建自定义中的坐标系定义数据
			buildCustomCoordinateDefines();
			// 构建收藏夹中的坐标系定义数据
			buildFavoriteCoordinateDefines();
			bulidRootDefine();
			// 构造显示投影系统结构的树
			initializeTreePrjCoordSys();
			registerEvents();
			setControlsEnabled();
			setSize(new Dimension(1100, 600));
			setLocationRelativeTo(null);
			selectRootNode();
			this.componentList.add(buttonApply);
			this.componentList.add(buttonClose);
			this.setFocusTraversalPolicy(policy);
			this.getRootPane().setDefaultButton(buttonClose);
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	/**
	 * 初始化根节点
	 */
	private void bulidRootDefine() {
		rootDefine = new CoordSysDefine(CoordSysDefine.USER_DEFINED);
		rootDefine.setCaption(ControlsProperties.getString("String_CoordSystem"));
		rootDefine.add(noneEarth);
		rootDefine.add(projectionSystem);
		rootDefine.add(geographyCoordinate);
		rootDefine.add(customCoordinate);
		rootDefine.add(favoriteCoordinate);
	}

	private void tableMouseRightClicked(MouseEvent e) {
		if (tablePrjCoordSys.getRowCount() <= 0) {
			return;
		}
		int i = tablePrjCoordSys.rowAtPoint(e.getPoint());
		int[] selectedRows = tablePrjCoordSys.getSelectedRows();

		if (i != -1) {
			// 判断是否已有选中行
			boolean isExist = false;
			for (int selectedRow : selectedRows) {
				if (selectedRow == i) {
					isExist = true;
				}
			}
			if (!isExist) {
				tablePrjCoordSys.setRowSelectionInterval(i, i);
			}
		} else {
			tablePrjCoordSys.setRowSelectionInterval(tablePrjCoordSys.getRowCount() - 1, tablePrjCoordSys.getRowCount() - 1);
		}

		if (tablePrjCoordSys.getModel() instanceof SearchResultModel) {
			currentRowData = ((SearchResultModel) tablePrjCoordSys.getModel()).getRowData(tablePrjCoordSys.getSelectedRow());
		} else {
			currentRowData = prjModel.getRowData(tablePrjCoordSys.getSelectedRow());
		}
		// 弹菜单
		if (currentRowData != null) {
			getPopupmenu().show(tablePrjCoordSys, e.getX(), e.getY());
		}
	}

	// 获取选定的投影
	public PrjCoordSys getPrjCoordSys() {
		return this.prjCoordSys;
	}

	/**
	 * 设置选定的投影（目前不支持自定义投影，所以如果是自定义投影，那么就不选择，是默认投影，则选中默认投影）
	 *
	 * @param prj
	 */
	public void setPrjCoordSys(PrjCoordSys prj) {
		this.prjCoordSys = prj;

		if (this.prjCoordSys != null) {
			if (this.prjCoordSys.getType() == PrjCoordSysType.PCS_NON_EARTH) { // 平面坐标系
				this.currentPrjDefine = this.noneEarth.getChildByCoordSysCode(this.prjCoordSys.getCoordUnit().value());
			} else if (this.prjCoordSys.getType() == PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) { // 地理坐标系
				GeoCoordSys geoCoordSys = this.prjCoordSys.getGeoCoordSys();
				if (geoCoordSys.getType() != GeoCoordSysType.GCS_USER_DEFINE) {
					this.currentPrjDefine = this.geographyCoordinate.getChildByCoordSysCode(geoCoordSys.getType().value());
				}
			} else { // 投影坐标系统
				if (this.prjCoordSys.getType() != PrjCoordSysType.PCS_USER_DEFINED) {
					this.currentPrjDefine = this.projectionSystem.getChildByCoordSysCode(this.prjCoordSys.getType().value());
				}
			}
		}

		// 获取根节点下与 currentPrjDefine 绑定的子节点
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) getNodeByDefine((DefaultMutableTreeNode) this.treePrjCoordSys.getModel().getRoot(),
				this.currentPrjDefine);
		if (node != null) {
			TreePath path = new TreePath(node.getPath());
			// 选中
			this.treePrjCoordSys.setSelectionPath(path);
			// 展开
			this.treePrjCoordSys.expandPath(path);
			// 滚动到可见
			this.treePrjCoordSys.scrollPathToVisible(path);
		}
		this.buttonApply.setEnabled(false);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		this.splitPaneMain.setDividerLocation(0.3);
		this.splitPaneDetails.setDividerLocation(0.7);
	}

	private void initializeComponents() {
		GroupLayout groupLayout = new GroupLayout(this.getContentPane());
		groupLayout.setAutoCreateContainerGaps(true);
		this.getContentPane().setLayout(groupLayout);
		JToolBar toolBarTemp = createToolBar();
		JToolBar toolBarButton = createToolBarButton();
		JPanel centerPanel = createCenterPanel();

		// @formatter:off
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.CENTER)
				.addComponent(toolBarTemp, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addComponent(toolBarButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addComponent(centerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE));

		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addComponent(toolBarTemp, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(toolBarButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(centerPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE));
		// @formatter:on
	}

	private void initializeResources() {
		this.setTitle(ControlsProperties.getString("String_SetProjection_Caption"));
		this.labelPath.setText(ControlsProperties.getString("String_CoordSys_PathName"));
		this.buttonApply.setText(CommonProperties.getString(CommonProperties.Apply));
		this.buttonClose.setText(CommonProperties.getString(CommonProperties.Close));
	}

	private void registerEvents() {
		unregisterEvents();
		this.treePrjCoordSys.addTreeSelectionListener(this.treeSelectionListener);
		this.tablePrjCoordSys.getSelectionModel().addListSelectionListener(listSelectionListener);
		this.treePrjCoordSys.addMouseListener(this.mouseAdapter);
		this.tablePrjCoordSys.addMouseListener(this.mouseAdapter);
		this.tablePrjCoordSys.getParent().addMouseListener(this.mouseAdapter);
		this.buttonImport.addActionListener(this.actionListener);
		this.buttonExport.addActionListener(this.actionListener);
		this.buttonFavorites.addActionListener(this.actionListener);
		this.buttonNewCoordSys.addActionListener(this.actionListener);
		this.menuItemNewPrjCoordSys.addActionListener(this.actionListener);
		this.menuItemNewGeoCoordSys.addActionListener(this.actionListener);
		this.menuItemNewFormEPSG.addActionListener(this.actionListener);
		this.buttonNewGroup.addActionListener(this.actionListener);
		this.buttonDelete.addActionListener(this.actionListener);
		this.buttonApply.addActionListener(this.actionListener);
		this.buttonClose.addActionListener(this.actionListener);
		this.textFieldSearch.addActionListener(this.actionListener);
		this.textFieldSearch.getDocument().addDocumentListener(this.documentListener);
	}

	private void unregisterEvents() {
		this.treePrjCoordSys.removeTreeSelectionListener(this.treeSelectionListener);
		this.tablePrjCoordSys.getSelectionModel().removeListSelectionListener(listSelectionListener);
		this.treePrjCoordSys.removeMouseListener(this.mouseAdapter);
		this.tablePrjCoordSys.removeMouseListener(this.mouseAdapter);
		this.tablePrjCoordSys.getParent().removeMouseListener(this.mouseAdapter);
		this.buttonImport.removeActionListener(this.actionListener);
		this.buttonExport.removeActionListener(this.actionListener);
		this.buttonFavorites.removeActionListener(this.actionListener);
		this.buttonNewCoordSys.removeActionListener(this.actionListener);
		this.menuItemNewPrjCoordSys.removeActionListener(this.actionListener);
		this.menuItemNewGeoCoordSys.removeActionListener(this.actionListener);
		this.menuItemNewFormEPSG.removeActionListener(this.actionListener);
		this.buttonNewGroup.removeActionListener(this.actionListener);
		this.buttonDelete.removeActionListener(this.actionListener);
		this.buttonApply.removeActionListener(this.actionListener);
		this.buttonClose.removeActionListener(this.actionListener);
		this.textFieldSearch.removeActionListener(this.actionListener);
		this.textFieldSearch.getDocument().removeDocumentListener(this.documentListener);
	}

	/**
	 * 创建工具条
	 *
	 * @return
	 */
	private JToolBar createToolBar() {
		this.toolBar = new JToolBar();
		this.toolBar.setFloatable(false);
		this.labelPath = new JLabel("ProjectionPath:");
		this.textFieldPath = new JTextField();
		this.textFieldPath.setEditable(false);
		this.textFieldSearch = new TextFieldSearch();
		textFieldSearch.setPreferredSize(new Dimension(150, 30));
		this.toolBar.add(this.labelPath);
		this.toolBar.addSeparator(new Dimension(5, 5));
		this.toolBar.add(this.textFieldPath);
		this.toolBar.addSeparator(new Dimension(5, 5));
		this.toolBar.add(this.textFieldSearch);
		return this.toolBar;
	}

	/**
	 * 创建按钮工具条
	 * yuanR
	 *
	 * @return
	 */
	private JToolBar createToolBarButton() {
		this.toolBarButton = new JToolBar();
		this.toolBarButton.setFloatable(false);

		this.buttonImport = new JButton(ControlsProperties.getString("String_Button_ImportCoordsys"), CoreResources.getIcon("/coreresources/ToolBar/Image_ToolButton_Import.png"));
		this.buttonExport = new JButton(ControlsProperties.getString("String_Button_ExportCoordsys"), CoreResources.getIcon("/coreresources/ToolBar/Image_ToolButton_Export.png"));
		this.buttonFavorites = new JButton(CoreProperties.getString("String_Favorite"), CoreResources.getIcon("/coreresources/ToolBar/Image_Favorite.png"));
		this.buttonNewCoordSys = new JButton(ControlsProperties.getString("String_NewCoorSys"), CoreResources.getIcon("/coreresources/ToolBar/Image_NewCoordsys.png"));
		this.buttonNewGroup = new JButton(ControlsProperties.getString("String_NewGroup"), ControlsResources.getIcon("/controlsresources/SortType/Image_NewGroup.png"));
		this.buttonDelete = new JButton(CommonProperties.getString(CommonProperties.Delete), CoreResources.getIcon("/coreresources/ToolBar/Image_ToolButton_Delete.png"));

		this.popupMenuNewCoordSys = new JPopupMenu();
		this.menuItemNewPrjCoordSys = new JMenuItem(ControlsProperties.getString("String_PrjCoorSys"));
		this.menuItemNewGeoCoordSys = new JMenuItem(ControlsProperties.getString("String_GeoCoordSys"));
		this.menuItemNewFormEPSG = new JMenuItem(ControlsProperties.getString("String_Button_NewCoordSysFormEPSG"));
		this.popupMenuNewCoordSys.add(this.menuItemNewPrjCoordSys);
		this.popupMenuNewCoordSys.add(this.menuItemNewGeoCoordSys);
		this.popupMenuNewCoordSys.add(this.menuItemNewFormEPSG);

		this.toolBarButton.add(this.buttonImport);
		this.toolBarButton.add(this.buttonExport);
		this.toolBarButton.addSeparator();
		this.toolBarButton.add(this.buttonFavorites);
		this.toolBarButton.add(this.buttonNewCoordSys);
		this.toolBarButton.add(this.buttonNewGroup);
		this.toolBarButton.addSeparator();
		this.toolBarButton.add(this.buttonDelete);

		return this.toolBarButton;
	}


	/**
	 * 创建工具条之下的内容面板
	 *
	 * @return
	 */
	private JPanel createCenterPanel() {
		JPanel centerPanel = new JPanel();
		createSplitPaneMain();
		this.buttonApply = new SmButton("Apply");
		this.buttonClose = new SmButton("Close");

		GroupLayout groupLayout = new GroupLayout(centerPanel);
		groupLayout.setAutoCreateGaps(true);
		centerPanel.setLayout(groupLayout);

		// @formatter:off
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.CENTER)
				.addComponent(this.splitPaneMain, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addGroup(groupLayout.createSequentialGroup()
						.addGap(10, 10, Short.MAX_VALUE)
						.addComponent(this.buttonApply)
						.addComponent(this.buttonClose)));

		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addComponent(this.splitPaneMain, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addGroup(groupLayout.createParallelGroup(Alignment.CENTER)
						.addComponent(this.buttonApply)
						.addComponent(this.buttonClose)));
		// @formatter:on
		this.getRootPane().setDefaultButton(this.buttonApply);
		return centerPanel;
	}

	/**
	 * 创建容纳 Tree 和 Table 的分割面板
	 *
	 * @return
	 */
	private JSplitPane createSplitPaneMain() {
		this.splitPaneMain = new JSplitPane();
		this.splitPaneMain.setContinuousLayout(true);

		JScrollPane scrollPane = new JScrollPane();
		this.treePrjCoordSys = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode(ControlsProperties.getString("String_CoordSystem"))));
		this.treePrjCoordSys.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		scrollPane.setViewportView(this.treePrjCoordSys);
		this.splitPaneMain.setLeftComponent(scrollPane);
		this.splitPaneMain.setRightComponent(createSplitPaneDetails());
		return this.splitPaneMain;
	}

	/**
	 * 创建容纳 Table 和投影详细信息展示的分割面板
	 *
	 * @return
	 */
	private JSplitPane createSplitPaneDetails() {
		this.splitPaneDetails = new JSplitPane();
		this.splitPaneDetails.setContinuousLayout(true);
		this.splitPaneDetails.setOrientation(JSplitPane.VERTICAL_SPLIT);

		JScrollPane scrollPane = new JScrollPane();
		this.tablePrjCoordSys = new JTable();
		this.tablePrjCoordSys.setRowHeight(this.tablePrjCoordSys.getRowHeight() + 4);
		this.tablePrjCoordSys.setModel(this.prjModel);
		this.tablePrjCoordSys.getTableHeader().setReorderingAllowed(false);
		scrollPane.setViewportView(this.tablePrjCoordSys);

		JScrollPane scrollPaneDetail = new JScrollPane();
		this.textAreaDetail = new JTextArea();
		this.textAreaDetail.setEditable(false);
		scrollPaneDetail.setViewportView(this.textAreaDetail);

		this.splitPaneDetails.setTopComponent(scrollPane);
		this.splitPaneDetails.setBottomComponent(scrollPaneDetail);
		return this.splitPaneDetails;
	}

	/**
	 * 加载默认投影配置文件
	 *
	 * @throws Exception
	 */
	private void loadProjectionConfig() {
		try {
			String startupXml = PathUtilities.getFullPathName(XMLProjectionTag.FILE_STARTUP_XML, false);
			Document startupDoc = XmlUtilities.getDocument(startupXml);
			if (startupDoc != null) {
				NodeList nodeList = startupDoc.getElementsByTagName(XMLProjectionTag.PROJECTION);
				if (nodeList.getLength() > 0) {
					this.projectionConfigPath = nodeList.item(0).getAttributes().getNamedItem(XMLProjectionTag.DEFAULT).getNodeValue();
				}
			}

			if (StringUtilities.isNullOrEmpty(this.projectionConfigPath)) {
				this.projectionConfigPath = PathUtilities.getFullPathName(XMLProjectionTag.PROJECTION_XML, false);
			}
			this.projectionDoc = loadProjectionConfig(this.projectionConfigPath);
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	/**
	 * 根据指定路径加载投影配置文件，返回 xml 文档对象
	 *
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	private Document loadProjectionConfig(String filePath) {
		Document document = null;
		File file = new File(filePath);
		try {
			if (!file.exists()) {
				InputStream stream = getClass().getResourceAsStream(DEFAULT_PROJECTION_CONFIG_PATH);
				if (stream != null) {
					document = XmlUtilities.getDocument(stream);
				} else {
					throw new Exception("Default ProjectionConfig does not exists.");
				}
			} else {
				document = XmlUtilities.getDocument(filePath);
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
		return document;
	}

	/**
	 * 加载自定义的投影config
	 */
	private void loadCustomProjectionConfig() {
		try {
			if (StringUtilities.isNullOrEmpty(this.customProjectionConfigPath)) {
				this.customProjectionConfigPath = PathUtilities.getFullPathName(XMLProjectionTag.CUSTOMPROJECTION_XML, false);
			}
			this.customProjectionDoc = loadProjectionConfig(this.customProjectionConfigPath);
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	/**
	 * 加载收藏的投影config
	 */
	private void loadFavoriteProjectionConfig() {
		try {
			if (StringUtilities.isNullOrEmpty(this.favoriteProjectionConfigPath)) {
				this.favoriteProjectionConfigPath = PathUtilities.getFullPathName(XMLProjectionTag.FAVORITEPROJECTION_XML, false);
			}
			this.favoriteProjectionDoc = loadProjectionConfig(this.favoriteProjectionConfigPath);
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}


	/**
	 * 构建平面坐标系定义集合
	 */
	private void buildNoneEarthDefines() {
		Enum[] units = Enum.getEnums(Unit.class);

		for (int i = 0; i < units.length; i++) {
			Unit unit = (Unit) units[i];
			CoordSysDefine coordSysDefine = new CoordSysDefine(CoordSysDefine.NONE_ERRTH, this.noneEarth, unit.toString()).setFolderNode(false);
			coordSysDefine.setCoordSysCode(unit.value());
		}
	}

	/**
	 * 构建投影坐标系统定义集合
	 */
	private void buildProjectionSystemDefines() {
		NodeList nodes = this.projectionDoc.getElementsByTagName(XMLProjectionTag.PRJCOORDSYS_DEFINE);

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
				createPrjCoordSysDefine(node, this.projectionSystem, this.projectionDoc);
			}
		}
	}

	/**
	 * 创建投影坐标系节点数据
	 *
	 * @param prjCoordSysNode
	 * @return
	 */
	private CoordSysDefine createPrjCoordSysDefine(Node prjCoordSysNode, CoordSysDefine coordSysDefine, Document doc) {
		CoordSysDefine result = new CoordSysDefine(CoordSysDefine.PROJECTION_SYSTEM).setFolderNode(false);

		NodeList nodes = prjCoordSysNode.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);

			if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getNodeName().equalsIgnoreCase(XMLProjectionTag.PRJGROUP_CAPTION)) {
					String groupCaption = node.getTextContent();
					CoordSysDefine parent = coordSysDefine.getChildByCaption(groupCaption);
					if (parent == null) {
						parent = new CoordSysDefine(coordSysDefine.getCoordSysType(), coordSysDefine, groupCaption).setFolderNode(true);
					}
					parent.add(result);
				} else if (node.getNodeName().equalsIgnoreCase(XMLProjectionTag.PRJCOORDSYS_CAPTION)) {
					result.setCaption(node.getTextContent());
				} else if (node.getNodeName().equalsIgnoreCase(XMLProjectionTag.PRJCOORDSYS_TYPE)) {
					String prjType = node.getTextContent();
					if (!StringUtilities.isNullOrEmpty(prjType)) {
						result.setCoordSysCode(Integer.valueOf(prjType));
					} else {
						result.setCoordSysCode(-1);
					}
				} else if (node.getNodeName().equalsIgnoreCase(XMLProjectionTag.COORDINATE_REFERENCE_SYSTEM)) {
					PrjCoordSys prjCoordSys = new PrjCoordSys();
					try {
						prjCoordSys.fromXML(XmlUtilities.nodeToString(node, doc.getXmlEncoding()));
					} catch (Exception e) {
						Application.getActiveApplication().getOutput().output(e);
					}
					result.setPrjCoordSys(prjCoordSys);
				}
			}
		}
		return result;
	}

	/**
	 * 构建地理坐标系定义集合
	 */
	private void buildGeographyCoordinateDefines() {
		NodeList nodes = this.projectionDoc.getElementsByTagName(XMLProjectionTag.GEOCOORDSYS_DEFINE);

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);

			if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
				createGeoCoordSysDefine(node, this.geographyCoordinate, this.projectionDoc);
			}
		}
	}

	/**
	 * 创建地理坐标系节点数据
	 *
	 * @param geoCoordSysNode
	 * @return
	 */
	private CoordSysDefine createGeoCoordSysDefine(Node geoCoordSysNode, CoordSysDefine coordSysDefine, Document doc) {
		CoordSysDefine result = new CoordSysDefine(CoordSysDefine.GEOGRAPHY_COORDINATE).setFolderNode(false);

		NodeList nodes = geoCoordSysNode.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getNodeName().equalsIgnoreCase(XMLProjectionTag.GEOGROUP_CATION)) {
					String groupCaption = node.getTextContent();
					CoordSysDefine parent = coordSysDefine.getChildByCaption(groupCaption);
					if (parent == null) {
						parent = new CoordSysDefine(coordSysDefine.getCoordSysType(), coordSysDefine, groupCaption).setFolderNode(true);
					}
					parent.add(result);
				} else if (node.getNodeName().equalsIgnoreCase(GEOCOORDSYS_CAPTION)) {
					result.setCaption(node.getTextContent());
				} else if (node.getNodeName().equalsIgnoreCase(XMLProjectionTag.GEOCOORDSYS_TYPE)) {
					String geoType = node.getTextContent();
					if (!StringUtilities.isNullOrEmpty(geoType)) {
						result.setCoordSysCode(Integer.valueOf(geoType));
					} else {
						result.setCoordSysCode(-1);
					}
				} else if (node.getNodeName().equalsIgnoreCase(XMLProjectionTag.GEOGRAPHIC_COORDINATE_SYSTEM)) {
					GeoCoordSys geoCoordSys = new GeoCoordSys();
					try {
						geoCoordSys.fromXML(XmlUtilities.nodeToString(node, doc.getXmlEncoding()));
					} catch (Exception e) {
						Application.getActiveApplication().getOutput().output(e);
					}
					result.setGeoCoordSys(geoCoordSys);
				}
			}
		}

		if (result.getParent() == null) {
			coordSysDefine.add(result);
		}
		// 地理坐标系节点下内容不会被修改，因此不需要设置“default”组
		//// 到这一步没有 Parent，说明配置文件中没有 GorupCaption 节点，那么就取 Default 节点
		//if (result.getParent() == null) {
		//	CoordSysDefine parent = this.geographyCoordinate.getChildByCaption(DEFAULT_GROUPCAPTION);
		//	if (parent == null) {
		//		parent = new CoordSysDefine(CoordSysDefine.GEOGRAPHY_COORDINATE, this.geographyCoordinate, DEFAULT_GROUPCAPTION);
		//	}
		//	parent.add(result);
		//}
		return result;
	}


	/**
	 * 构建自定义和收藏夹坐标系定义集合
	 */
	private void buildCustomCoordinateDefines() {
		NodeList GEONodes = this.customProjectionDoc.getElementsByTagName(XMLProjectionTag.GEOCOORDSYS_DEFINE);
		for (int i = 0; i < GEONodes.getLength(); i++) {
			Node node = GEONodes.item(i);
			if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
				createGeoCoordSysDefine(node, this.customCoordinate, this.customProjectionDoc);
			}
		}

		NodeList PRJNodes = this.customProjectionDoc.getElementsByTagName(XMLProjectionTag.PRJCOORDSYS_DEFINE);
		for (int i = 0; i < PRJNodes.getLength(); i++) {
			Node node = PRJNodes.item(i);
			if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
				createPrjCoordSysDefine(node, this.customCoordinate, this.customProjectionDoc);
			}
		}
	}


	/**
	 * 构建收藏夹坐标系定义集合
	 */
	private void buildFavoriteCoordinateDefines() {
		NodeList GEONodes = this.favoriteProjectionDoc.getElementsByTagName(XMLProjectionTag.GEOCOORDSYS_DEFINE);
		for (int i = 0; i < GEONodes.getLength(); i++) {
			Node node = GEONodes.item(i);
			if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
				createGeoCoordSysDefine(node, this.favoriteCoordinate, this.favoriteProjectionDoc);
			}
		}

		NodeList PRJNodes = this.favoriteProjectionDoc.getElementsByTagName(XMLProjectionTag.PRJCOORDSYS_DEFINE);
		for (int i = 0; i < PRJNodes.getLength(); i++) {
			Node node = PRJNodes.item(i);
			if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
				createPrjCoordSysDefine(node, this.favoriteCoordinate, this.favoriteProjectionDoc);
			}
		}
	}

	private void initializeTreePrjCoordSys() {
		DefaultTreeModel treeModel = (DefaultTreeModel) this.treePrjCoordSys.getModel();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
		rootNode.add(createNode(this.noneEarth));
		rootNode.add(createNode(this.projectionSystem));
		rootNode.add(createNode(this.geographyCoordinate));
		rootNode.add(createNode(this.customCoordinate));
		rootNode.add(createNode(this.favoriteCoordinate));
		this.treePrjCoordSys.expandPath(new TreePath(rootNode.getPath()));
	}

	private DefaultMutableTreeNode createNode(CoordSysDefine define) {
		DefaultMutableTreeNode result = new DefaultMutableTreeNode(define);
		for (int i = 0; i < define.size(); i++) {
			result.add(createNode(define.get(i)));
		}
		return result;
	}

	private void treeSelectionChange() {
		try {
			// 点击 Expand / Collapse 也会触发 SelectionChange，然而此时 SelectionPath 为空
			if (this.treePrjCoordSys.getSelectionPath() != null) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) this.treePrjCoordSys.getSelectionPath().getLastPathComponent();

				if (selectedNode == treePrjCoordSys.getModel().getRoot()) {
					this.prjModel.setDefine(rootDefine);
					this.currentPrjDefine = null;
				} else if (selectedNode.getUserObject() instanceof CoordSysDefine) {
					this.prjModel.setDefine((CoordSysDefine) selectedNode.getUserObject());
					this.currentPrjDefine = (CoordSysDefine) selectedNode.getUserObject();
				} else {
					this.prjModel.setDefine(null);
					this.currentPrjDefine = null;
				}

				// Table 上有可能是搜索结果的 Model，这时候就要重新设置一下 Model
				if (this.tablePrjCoordSys.getModel() != this.prjModel) {
					this.tablePrjCoordSys.setModel(this.prjModel);
				}
				if (tablePrjCoordSys.getRowCount() > 0) {
					tablePrjCoordSys.setRowSelectionInterval(0, 0);
				} else {
					refreshStates();
					setControlsEnabled();
				}
			}

		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	private void tableSelectionChange() {
		int row = this.tablePrjCoordSys.getSelectedRow();
		if (row >= 0) {
			AbstractPrjTableModel model = (AbstractPrjTableModel) this.tablePrjCoordSys.getModel();
			this.currentPrjDefine = model.getRowData(row);
		} else {
			this.currentPrjDefine = null;
		}
		refreshStates();
		setControlsEnabled();
	}

	private void tableMouseDoubleClick(MouseEvent e) {
		// 获取鼠标双击位置处的选中行
		int row = this.tablePrjCoordSys.rowAtPoint(e.getPoint());
		if (row < 0 || row != this.tablePrjCoordSys.getSelectedRow()) {
			return;
		}

		AbstractPrjTableModel model = (AbstractPrjTableModel) this.tablePrjCoordSys.getModel();
		CoordSysDefine clickedPrjDefine = model.getRowData(row);
		if (clickedPrjDefine.size() > 0) { // 如果双击的项是集合，那么进入下一层，并设置树的节点选中，触发 TreeSlection
			// 获取树上当前选中的节点（即将设置的节点的父节点）
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) this.treePrjCoordSys.getSelectionPath().getLastPathComponent();
			// 即将设置选中的节点
			DefaultMutableTreeNode toSelectedNode = (DefaultMutableTreeNode) getNodeByDefine(selectedNode, clickedPrjDefine);
			if (toSelectedNode != null) {
				TreePath nodePath = new TreePath(toSelectedNode.getPath());
				this.treePrjCoordSys.setSelectionPath(nodePath);
				this.treePrjCoordSys.expandPath(nodePath);
			}
		} else { // 如果双击的项没有子项，那么就是具体的投影定义，提示选择应用
			if (clickedPrjDefine.getCoordSysType() != CoordSysDefine.CUSTOM_COORDINATE
					&& clickedPrjDefine.getCoordSysType() != CoordSysDefine.FAVORITE_COORDINATE) {
				confirmSelected();
			}
		}
	}

	/**
	 * // 如果双击的是叶子节点，那么提示应用，如果双击的是其他节点，没有任何效果
	 *
	 * @param e
	 */
	private void treeMouseDoubleClick(MouseEvent e) {
		// 获取双击位置处的 TreePath
		TreePath clickedPath = this.treePrjCoordSys.getPathForLocation(e.getX(), e.getY());
		// 判断双击的是否是节点
		if (clickedPath != null) {
			DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode) clickedPath.getLastPathComponent();
			// 双击的是节点，并且和当前选中的 Define 是同一个节点，就提示是否应用
			if (clickedNode != null && clickedNode.getUserObject() == this.currentPrjDefine) {
				if (currentPrjDefine.getCoordSysType() != CoordSysDefine.CUSTOM_COORDINATE
						&& currentPrjDefine.getCoordSysType() != CoordSysDefine.FAVORITE_COORDINATE) {
					confirmSelected();
				}
			}
		}
	}

	private void textFieldSearchAction() {
		search(this.textFieldSearch.getText());
	}

	private void buttonApplyClicked() {
		applyPrjCoordSys();
	}

	private void buttonCloseClicked() {
		this.dialogResult = DialogResult.CANCEL;
		setVisible(false);
	}

	/**
	 * 获取指定父节点下，与指定 UserData 匹配的子节点
	 *
	 * @param node
	 * @param define
	 * @return
	 */
	private TreeNode getNodeByDefine(DefaultMutableTreeNode node, CoordSysDefine define) {
		TreeNode result = null;

		try {
			if (node != null && define != null) {
				if (node.getUserObject() == define) {
					result = node;
				} else {
					if (node.getChildCount() == 0) {
						result = null;
					} else {
						for (int i = 0; i < node.getChildCount(); i++) {
							DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
							result = getNodeByDefine(childNode, define);
							if (result != null) {
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
		return result;
	}

	/**
	 * 确认并选择指定的投影。只有当前投影是叶子节点也就是具体的投影时，才提示是否应用。
	 *
	 * @param
	 */
	private void confirmSelected() {
		if (this.currentPrjDefine != null && this.currentPrjDefine.size() == 0) {
			if (showConfirmMessage() == JOptionPane.YES_OPTION) {
				applyPrjCoordSys();
			} else {
				this.prjCoordSys = null;
			}
		}
	}

	/**
	 * 应用选中的投影
	 */
	private void applyPrjCoordSys() {
		if (this.currentPrjDefine.getCoordSysType() == CoordSysDefine.NONE_ERRTH) {
			this.prjCoordSys = new PrjCoordSys(PrjCoordSysType.PCS_NON_EARTH);
			this.prjCoordSys.setCoordUnit((Unit) Enum.parse(Unit.class, this.currentPrjDefine.getCoordSysCode()));
		} else if (this.currentPrjDefine.getCoordSysType() == CoordSysDefine.PROJECTION_SYSTEM) {
			this.prjCoordSys = PrjCoordSysSettingsUtilties.getPrjCoordSys(this.currentPrjDefine);
		} else if (this.currentPrjDefine.getCoordSysType() == CoordSysDefine.GEOGRAPHY_COORDINATE) {
			GeoCoordSys geoCoordSys = PrjCoordSysSettingsUtilties.getGeoCoordSys(this.currentPrjDefine);
			this.prjCoordSys = new PrjCoordSys(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
			this.prjCoordSys.setGeoCoordSys(geoCoordSys);
		} else {
			this.prjCoordSys = null;
		}

		this.dialogResult = DialogResult.OK;
		setVisible(false);
	}

	/**
	 * @return JOptionPane.YES_OPTION / JOptionPane.NO_OPTION / JOptionPane.CLOSED_OPTION
	 */
	private int showConfirmMessage() {
		return UICommonToolkit.showConfirmDialogYesNo(ControlsProperties.getString("String_message_Apply"));
	}

	/**
	 * 当前选中的投影信息发生改变时，刷新一些控件的内容展示
	 */
	private void refreshStates() {
		refreshTextAreaDetails();
		refreshPath();
	}

	/**
	 * 当前选中的投影信息发生改变时，刷新投影信息详情
	 */
	private void refreshTextAreaDetails() {
		this.textAreaDetail.setText(PrjCoordSysSettingsUtilties.getDescription(this.currentPrjDefine));
	}

	private void refreshPath() {
		TreePath path = this.treePrjCoordSys.getSelectionPath();
		if (path != null) {
			this.textFieldPath.setText(path.toString());
		}
	}

	private void setControlsEnabled() {
		this.buttonImport.setEnabled(isImportEnable());
		this.buttonExport.setEnabled(isExportEnable());
		this.buttonFavorites.setEnabled(isAddFavoritesEnable());
		this.menuItemNewGeoCoordSys.setEnabled(isNewGeoCoordsysEnable());
		this.menuItemNewPrjCoordSys.setEnabled(isNewPrjCoordsysEnable());
		this.menuItemNewFormEPSG.setEnabled(isNewCoordsysFromEPSGEnable());
		this.buttonDelete.setEnabled(isDeleteEnable());
		this.buttonApply.setEnabled(isButtonApplyEnable());
	}

	private void search(String pattern) {
		SearchResultModel searchModel = new SearchResultModel();
		// 如果当前选中的投影不为空，就搜索当前选中的投影，否则就搜索所有
		if (this.currentPrjDefine != null) {
			searchDefine(pattern, this.currentPrjDefine, searchModel);
		} else {
			searchDefine(pattern, this.noneEarth, searchModel);
			searchDefine(pattern, this.geographyCoordinate, searchModel);
			searchDefine(pattern, this.projectionSystem, searchModel);
		}

		this.tablePrjCoordSys.setModel(searchModel);
	}

	/**
	 * 根据关键字搜索指定的 Define，并将所有结果添加到 SearchResultModel 中
	 *
	 * @param pattern
	 * @param define
	 * @param searchModel
	 */
	private void searchDefine(String pattern, CoordSysDefine define, SearchResultModel searchModel) {
		CoordSysDefine[] allLeafDefines = null;

		// 如果选中的是最后一级子节点，那么就选择改节点的父节点进行搜索
		if (define.size() == 0) {
			allLeafDefines = define.getParent().getAllLeaves();
		} else {
			allLeafDefines = define.getAllLeaves();
		}

		for (int i = 0; i < allLeafDefines.length; i++) {
			String caption = allLeafDefines[i].getCaption();
			if (caption.toLowerCase().contains(pattern.toLowerCase())) {
				searchModel.add(allLeafDefines[i]);
			}
		}
	}

	public JPopupMenu getPopupmenu() {
		if (popupmenu == null) {
			this.popupmenu = new JPopupMenu();
			this.menuNewCoordsys = new JMenu(ControlsProperties.getString("String_NewCoorSys"));
			this.menuNewCoordsys.add(this.menuItemNewGeoCoordSys);
			this.menuNewCoordsys.add(this.menuItemNewPrjCoordSys);
			this.menuNewCoordsys.add(this.menuItemNewFormEPSG);
			this.menuItemNewGroup = new JMenuItem(ControlsProperties.getString("String_NewGroup"));
			this.menuItemImportCoordSys = new JMenuItem(ControlsProperties.getString("String_Button_ImportCoordsys"));
			this.menuItemExportCoordSys = new JMenuItem(ControlsProperties.getString("String_Button_ExportCoordsys"));
			this.menuItemAddFavorites = new JMenuItem(CoreProperties.getString("String_Favorite"));
			this.menuItemDelete = new JMenuItem(CommonProperties.getString(CommonProperties.Delete));

			this.popupmenu.add(this.menuItemNewGroup);
			this.popupmenu.add(menuNewCoordsys);
			this.popupmenu.addSeparator();
			this.popupmenu.add(menuItemDelete);
			this.popupmenu.addSeparator();
			this.popupmenu.add(this.menuItemImportCoordSys);
			this.popupmenu.add(this.menuItemExportCoordSys);
			this.popupmenu.add(this.menuItemAddFavorites);

			this.menuItemDelete.addActionListener(this.actionListener);
			this.menuItemImportCoordSys.addActionListener(this.actionListener);
			this.menuItemExportCoordSys.addActionListener(this.actionListener);
			this.menuItemAddFavorites.addActionListener(this.actionListener);
			this.menuItemNewGroup.addActionListener(this.actionListener);
			//menuItemUserDefine = new JMenuItem(ControlsProperties.getString("String_Button_NewCoordSys"));
			//menuItemNewGroup.addActionListener(new ActionListener() {
			//	@Override
			//	public void actionPerformed(ActionEvent e) {
			//		if (currentRowData != null) {
			//			if (currentRowData.getCoordSysType() == CoordSysDefine.GEOGRAPHY_COORDINATE) {
			//				JDialogUserDefinePrjGeography geography = new JDialogUserDefinePrjGeography();
			//				geography.setGeOCoordSys(PrjCoordSysSettingsUtilties.getGeoCoordSys(currentPrjDefine));
			//				if (geography.showDialog() == DialogResult.OK) {
			//					GeoCoordSys geoCoordSys = geography.getGeoCoordSys();
			//					CoordSysDefine result = new CoordSysDefine(CoordSysDefine.GEOGRAPHY_COORDINATE);
			//					result.setCoordSysCode(CoordSysDefine.USER_DEFINED);
			//					result.setGeoCoordSys(geoCoordSys);
			//					result.setCaption(geoCoordSys.getName());
			//					CoordSysDefine userDefine = geographyCoordinate.getChildByCaption(userDefineParentName);
			//					if (userDefine == null) {
			//						userDefine = new CoordSysDefine(CoordSysDefine.GEOGRAPHY_COORDINATE, geographyCoordinate, userDefineParentName);
			//					}
			//					if (userDefine.add(result)) {
			//						String geoCoorSys = ControlsProperties.getString("String_GeoCoordSys");
			//						addToTree(result, userDefineParentName, userDefine, geoCoorSys);
			//						addGeoCoorSysToDocument(result, customProjectionDoc, customProjectionConfigPath);
			//					}
			//				}
			//			} else if (currentRowData.getCoordSysType() == CoordSysDefine.PROJECTION_SYSTEM) {
			//				JDialogUserDefinePrjProjection dialogUserDefinePrjProjection = new JDialogUserDefinePrjProjection();
			//				dialogUserDefinePrjProjection.setPrjCoordSys(PrjCoordSysSettingsUtilties.getPrjCoordSys(currentPrjDefine));
			//				if (dialogUserDefinePrjProjection.showDialog() == DialogResult.OK) {
			//					PrjCoordSys prjCoordSys = dialogUserDefinePrjProjection.getPrjCoordSys();
			//					CoordSysDefine result = new CoordSysDefine(CoordSysDefine.PROJECTION_SYSTEM);
			//					result.setCoordSysCode(CoordSysDefine.USER_DEFINED);
			//					result.setPrjCoordSys(prjCoordSys);
			//					result.setCaption(prjCoordSys.getName());
			//					CoordSysDefine userDefine = projectionSystem.getChildByCaption(userDefineParentName);
			//					if (userDefine == null) {
			//						userDefine = new CoordSysDefine(CoordSysDefine.PROJECTION_SYSTEM, projectionSystem, userDefineParentName);
			//					}
			//					if (userDefine.add(result)) {
			//						String geoCoorSys = ControlsProperties.getString("String_PrjCoorSys");
			//						addToTree(result, userDefineParentName, userDefine, geoCoorSys);
			//						addProjToDocument(result, projectionDoc, projectionConfigPath);
			//					}
			//				}
			//				dialogUserDefinePrjProjection.clean();
			//			}
			//		}
			//	}
			//});


			popupmenu.addPopupMenuListener(new PopupMenuListener() {
				@Override
				public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
					menuItemNewGeoCoordSys.setEnabled(isNewGeoCoordsysEnable());
					menuItemNewPrjCoordSys.setEnabled(isNewPrjCoordsysEnable());
					menuItemNewFormEPSG.setEnabled(isNewCoordsysFromEPSGEnable());
					menuItemNewGroup.setEnabled(isNewGroupEnable());
					menuItemImportCoordSys.setEnabled(isImportEnable());
					menuItemExportCoordSys.setEnabled(isExportEnable());
					menuItemAddFavorites.setEnabled(isAddFavoritesEnable());
					menuItemDelete.setEnabled(isDeleteEnable());
				}

				@Override
				public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

				}

				@Override
				public void popupMenuCanceled(PopupMenuEvent e) {

				}
			});
		}

		return popupmenu;
	}


	/**
	 * @return
	 */
	private boolean isDeleteEnable() {
		return this.currentPrjDefine != null && !this.currentPrjDefine.getIsFolderNode()
				&& (currentPrjDefine.getParent().getCoordSysType() == CoordSysDefine.CUSTOM_COORDINATE ||
				currentPrjDefine.getParent().getCoordSysType() == CoordSysDefine.FAVORITE_COORDINATE);

	}

	private boolean isNewGroupEnable() {
		return true;
	}

	private boolean isNewGeoCoordsysEnable() {
		return this.currentPrjDefine != null &&
				!this.currentPrjDefine.getIsFolderNode() &&
				this.currentPrjDefine.getCoordSysType() == CoordSysDefine.GEOGRAPHY_COORDINATE;

	}

	private boolean isNewPrjCoordsysEnable() {
		return this.currentPrjDefine != null &&
				!this.currentPrjDefine.getIsFolderNode() &&
				this.currentPrjDefine.getCoordSysType() == CoordSysDefine.PROJECTION_SYSTEM;

	}

	private boolean isNewCoordsysFromEPSGEnable() {
		return false;
	}

	private boolean isImportEnable() {
		return true;
	}

	private boolean isExportEnable() {
		return this.currentPrjDefine != null && this.currentPrjDefine.getCoordSysType() != CoordSysDefine.NONE_ERRTH &&
				(!this.currentPrjDefine.getIsFolderNode() || this.currentPrjDefine.size() > 0);
	}

	private boolean isAddFavoritesEnable() {
		return this.currentPrjDefine != null &&
				!this.currentPrjDefine.getIsFolderNode() &&
				this.currentPrjDefine.getParent().getCoordSysType() != CoordSysDefine.FAVORITE_COORDINATE &&
				!this.currentPrjDefine.getCoordSysTypeDescription().equals(ControlsProperties.getString("String_NoneEarth"));
	}

	private boolean isButtonApplyEnable() {
		return this.currentPrjDefine != null && this.currentPrjDefine.size() == 0
				&& currentPrjDefine.getCoordSysType() != CoordSysDefine.CUSTOM_COORDINATE
				&& currentPrjDefine.getCoordSysType() != CoordSysDefine.FAVORITE_COORDINATE;
	}

	/**
	 * 将坐标系添加到收藏夹中
	 * yuanR2017.10.25
	 */
	private void addCoordsysToFavorites() {
		if (this.currentPrjDefine != null) {
			String grantParentName = ControlsProperties.getString("String_MyFavorite");
			if (this.currentPrjDefine.getCoordSysType() == CoordSysDefine.GEOGRAPHY_COORDINATE) {
				CoordSysDefine result = new CoordSysDefine(CoordSysDefine.GEOGRAPHY_COORDINATE);
				result.setCoordSysCode(this.currentPrjDefine.getCoordSysCode());
				result.setGeoCoordSys(this.currentPrjDefine.getGeoCoordSys());
				result.setCaption(this.currentPrjDefine.getCaption());
				CoordSysDefine userDefine = this.favoriteCoordinate.getChildByCaption(DEFAULT_GROUPCAPTION).setFolderNode(true);
				if (userDefine == null) {
					userDefine = new CoordSysDefine(CoordSysDefine.FAVORITE_COORDINATE, this.favoriteCoordinate, DEFAULT_GROUPCAPTION);
				}
				if (userDefine.add(result)) {
					addToTree(result, DEFAULT_GROUPCAPTION, userDefine, grantParentName);
					addGeoCoorSysToDocument(result, this.favoriteProjectionDoc, this.favoriteProjectionConfigPath);
				}
			} else if (this.currentPrjDefine.getCoordSysType() == CoordSysDefine.PROJECTION_SYSTEM) {
				//JDialogUserDefinePrjProjection dialogUserDefinePrjProjection = new JDialogUserDefinePrjProjection();
				//dialogUserDefinePrjProjection.setPrjCoordSys(PrjCoordSysSettingsUtilties.getPrjCoordSys(currentPrjDefine));

				CoordSysDefine result = new CoordSysDefine(CoordSysDefine.PROJECTION_SYSTEM);
				result.setCoordSysCode(this.currentPrjDefine.getCoordSysCode());
				result.setPrjCoordSys(this.currentPrjDefine.getPrjCoordSys());
				result.setCaption(this.currentPrjDefine.getCaption());
				CoordSysDefine userDefine = this.favoriteCoordinate.getChildByCaption(DEFAULT_GROUPCAPTION);
				if (userDefine == null) {
					userDefine = new CoordSysDefine(CoordSysDefine.FAVORITE_COORDINATE, this.favoriteCoordinate, DEFAULT_GROUPCAPTION).setFolderNode(true);
				}
				if (userDefine.add(result)) {
					addToTree(result, DEFAULT_GROUPCAPTION, userDefine, grantParentName);
					addProjToDocument(result, this.favoriteProjectionDoc, this.favoriteProjectionConfigPath);
				}
			}
		}
	}

	/**
	 * 新建地理坐标系
	 */
	private void newGeoCoordsys() {
		JDialogUserDefinePrjGeography geography = new JDialogUserDefinePrjGeography();
		geography.setGeOCoordSys(PrjCoordSysSettingsUtilties.getGeoCoordSys(this.currentPrjDefine));
		if (geography.showDialog() == DialogResult.OK) {
			GeoCoordSys geoCoordSys = geography.getGeoCoordSys();
			CoordSysDefine result = new CoordSysDefine(CoordSysDefine.GEOGRAPHY_COORDINATE);
			result.setCoordSysCode(-1);
			result.setGeoCoordSys(geoCoordSys);
			result.setCaption(geoCoordSys.getName());
			CoordSysDefine userDefine = customCoordinate.getChildByCaption(userDefineGeoParentName);
			if (userDefine == null) {
				userDefine = new CoordSysDefine(CoordSysDefine.CUSTOM_COORDINATE, customCoordinate, userDefineGeoParentName).setFolderNode(true);
			}
			if (userDefine.add(result)) {
				String grantParentName = ControlsProperties.getString("String_Customize");
				addToTree(result, userDefineGeoParentName, userDefine, grantParentName);
				addGeoCoorSysToDocument(result, customProjectionDoc, customProjectionConfigPath);
			}
		}
		geography.clean();

	}


	/**
	 * 新建投影坐标系
	 */
	private void newPrjCoordsys() {
		JDialogUserDefinePrjProjection dialogUserDefinePrjProjection = new JDialogUserDefinePrjProjection();
		dialogUserDefinePrjProjection.setPrjCoordSys(PrjCoordSysSettingsUtilties.getPrjCoordSys(this.currentPrjDefine));
		if (dialogUserDefinePrjProjection.showDialog() == DialogResult.OK) {
			PrjCoordSys prjCoordSys = dialogUserDefinePrjProjection.getPrjCoordSys();
			CoordSysDefine result = new CoordSysDefine(CoordSysDefine.PROJECTION_SYSTEM);
			result.setCoordSysCode(CoordSysDefine.USER_DEFINED);
			result.setPrjCoordSys(prjCoordSys);
			result.setCaption(prjCoordSys.getName());
			CoordSysDefine userDefine = customCoordinate.getChildByCaption(userDefinePrjParentName);
			if (userDefine == null) {
				userDefine = new CoordSysDefine(CoordSysDefine.CUSTOM_COORDINATE, customCoordinate, userDefinePrjParentName).setFolderNode(true);
			}
			if (userDefine.add(result)) {
				String grantParentName = ControlsProperties.getString("String_Customize");
				addToTree(result, userDefinePrjParentName, userDefine, grantParentName);
				addProjToDocument(result, customProjectionDoc, customProjectionConfigPath);
			}
		}
		dialogUserDefinePrjProjection.clean();
	}

	/**
	 * 删除坐标系
	 */
	private void deleteCoordsys() {
		if (isDeleteEnable()) {
			if (UICommonToolkit.showConfirmDialog(ControlsProperties.getString("String_DelSelectedItem_Warning")) == 0) {
				int[] selectedRows = tablePrjCoordSys.getSelectedRows();
				for (int i = selectedRows.length - 1; i >= 0; i--) {
					CoordSysDefine rowData = prjModel.getRowData(selectedRows[i]);
					if (rowData == null && tablePrjCoordSys.getModel() instanceof SearchResultModel) {
						rowData = ((SearchResultModel) tablePrjCoordSys.getModel()).getRowData(selectedRows[i]);
					}
					if (rowData != null) {
						removeCoordSysDefineFormDoc(rowData);
						//if (rowData.getParent().size() <= 1) {
						//	rowData.getParent().getParent().remove(rowData.getParent());
						//}
						rowData.getParent().remove(rowData);
					}
				}
			}
		}
	}

	/**
	 * 导入坐标系到自定义
	 */
	private void importCoordsys(PrjCoordSys prjCoordSys) {
		if (prjCoordSys != null && !prjCoordSys.getType().equals(PrjCoordSysType.PCS_NON_EARTH)) {
			CoordSysDefine result;
			if (prjCoordSys.getType().equals(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE)) {
				result = new CoordSysDefine(CoordSysDefine.GEOGRAPHY_COORDINATE);
				result.setGeoCoordSys(prjCoordSys.getGeoCoordSys());
			} else {
				result = new CoordSysDefine(CoordSysDefine.PROJECTION_SYSTEM);
				result.setPrjCoordSys(prjCoordSys);
			}
			result.setCoordSysCode(CoordSysDefine.USER_DEFINED);
			result.setCaption(prjCoordSys.getName());
			CoordSysDefine userDefine = customCoordinate.getChildByCaption(userImportCoordsysParentName).setFolderNode(true);
			if (userDefine == null) {
				userDefine = new CoordSysDefine(CoordSysDefine.CUSTOM_COORDINATE, customCoordinate, userImportCoordsysParentName);
			}
			if (userDefine.add(result)) {
				String grantParentName = ControlsProperties.getString("String_Customize");
				addToTree(result, userImportCoordsysParentName, userDefine, grantParentName);
				if (result.getCoordSysType() == CoordSysDefine.GEOGRAPHY_COORDINATE) {
					addGeoCoorSysToDocument(result, customProjectionDoc, customProjectionConfigPath);
				} else if (result.getCoordSysType() == CoordSysDefine.PROJECTION_SYSTEM) {
					addProjToDocument(result, customProjectionDoc, customProjectionConfigPath);
				}
			}
		}
	}

	/**
	 * 导出投影到xxx
	 * todo 优化导出方法，满足除去平面坐标系无法导出，单个文件及文件夹，以及文件夹包含文件夹都可以进行导出
	 */
	private void exportCoordsys() {
		int successedNum = 0;
		String moduleName = "ExportPrjFile";
		if (!SmFileChoose.isModuleExist(moduleName)) {
			// 为确保导出文件名称不可修改，筛选的后缀名称为不存在-yuanR2017.11.1
			String fileFilters = SmFileChoose.createFileFilter(ControlsProperties.getString("String_ImportPrjFileXml"), "noExist");
			SmFileChoose.addNewNode(fileFilters, CommonProperties.getString("String_DefaultFilePath"),
					ControlsProperties.getString("String_ExportPrjFile"), moduleName, "SaveOne");
		}
		SmFileChoose prjFileExportFileChoose = new SmFileChoose(moduleName);
		prjFileExportFileChoose.setSelectedFile(new File(currentPrjDefine.getCaption()));

		if (prjFileExportFileChoose.getTextField() != null) {
			prjFileExportFileChoose.getTextField().setEnabled(false);
		}
		if (prjFileExportFileChoose.showDefaultDialog() == JFileChooser.APPROVE_OPTION) {
			// 开始进行投影导出
			if (this.currentPrjDefine.getIsFolderNode()) {
				CoordSysDefine[] allCoordSysDefine = this.currentPrjDefine.getAllLeaves().clone();
				String folderName = prjFileExportFileChoose.getFilePath().replace(".noExist", "");
				if (!FileUtilities.exists(folderName)) {
					File file = new File(folderName);
					file.mkdir();
				}
				for (int i = 0; i < allCoordSysDefine.length; i++) {
					if (!allCoordSysDefine[i].getIsFolderNode()) {
						PrjCoordSys exportPrjCoordSys = new PrjCoordSys();
						if (allCoordSysDefine[i].getCoordSysType() == CoordSysDefine.GEOGRAPHY_COORDINATE) {
							GeoCoordSys exportGeoCoordSys = PrjCoordSysSettingsUtilties.getGeoCoordSys(allCoordSysDefine[i]).clone();
							exportPrjCoordSys.setGeoCoordSys(exportGeoCoordSys);
							exportPrjCoordSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
							exportPrjCoordSys.setName(exportGeoCoordSys.getName());
						} else if (allCoordSysDefine[i].getCoordSysType() == CoordSysDefine.PROJECTION_SYSTEM) {
							try {
								exportPrjCoordSys = PrjCoordSysSettingsUtilties.getPrjCoordSys(allCoordSysDefine[i]).clone();
							} catch (Exception ex) {
								continue;
							}
						}
						if (export(exportPrjCoordSys, folderName + "//" + exportPrjCoordSys.getName() + ".xml")) {
							successedNum++;
						}
					}
				}
			} else {
				// 选中非文件夹节点，直接导出即可
				PrjCoordSys exportPrjCoordSys = new PrjCoordSys();
				if (this.currentPrjDefine.getCoordSysType() == CoordSysDefine.GEOGRAPHY_COORDINATE) {
					GeoCoordSys exportGeoCoordSys = PrjCoordSysSettingsUtilties.getGeoCoordSys(this.currentPrjDefine).clone();
					exportPrjCoordSys.setGeoCoordSys(exportGeoCoordSys);
					exportPrjCoordSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
					exportPrjCoordSys.setName(exportGeoCoordSys.getName());
				} else if (this.currentPrjDefine.getCoordSysType() == CoordSysDefine.PROJECTION_SYSTEM) {
					exportPrjCoordSys = PrjCoordSysSettingsUtilties.getPrjCoordSys(this.currentPrjDefine).clone();
				}
				if (export(exportPrjCoordSys, prjFileExportFileChoose.getFilePath().replace("noExist", "xml"))) {
					successedNum++;
				}
			}

			if (successedNum > 0) {
				Application.getActiveApplication().getOutput().output(MessageFormat.format(ControlsProperties.getString("String_ExportPrjFileSuccess"), successedNum));
			} else {
				Application.getActiveApplication().getOutput().output(ControlsProperties.getString("String_ExportPrjFileFailed"));
			}
		}
	}

	/**
	 * 导出通用方法
	 */
	public Boolean export(PrjCoordSys prjCoordSys, String path) {
		Boolean isSuccess = false;
		isSuccess = prjCoordSys.toFile(path, PrjFileVersion.UGC60);
		return isSuccess;
	}

	private void addGeoCoorSysToDocument(CoordSysDefine result, Document targetDocument, String path) {
		Element defines = (Element) targetDocument.getElementsByTagName(XMLProjectionTag.GEOCOORDSYS_DEFINES).item(0);
		Element define = targetDocument.createElement(XMLProjectionTag.GEOCOORDSYS_DEFINE);

		Element parent = targetDocument.createElement(XMLProjectionTag.GEOGROUP_CATION);
		parent.appendChild(targetDocument.createTextNode(result.getParent().getCaption()));
		define.appendChild(parent);

		Element caption = targetDocument.createElement(XMLProjectionTag.GEOCOORDSYS_CAPTION);
		caption.appendChild(targetDocument.createTextNode(result.getCaption()));
		define.appendChild(caption);

		Element type = targetDocument.createElement(XMLProjectionTag.GEOCOORDSYS_TYPE);
		type.appendChild(targetDocument.createTextNode(String.valueOf(result.getCoordSysCode())));
		define.appendChild(type);

		define.appendChild(targetDocument.createCDATASection(result.getGeoCoordSys().toXML()));
		defines.appendChild(define);
		try {
			String string = XmlUtilities.nodeToString(targetDocument, targetDocument.getXmlEncoding());
			string = string.replaceAll("<!\\[CDATA\\[", "");
			string = string.replaceAll("\\]\\]>", "");
			FileUtilities.writeToFile(path, string);
//			XmlUtilties.saveXml(projectionConfigPath, projectionDoc, projectionDoc.getXmlEncoding());
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}

	}

	private void addProjToDocument(CoordSysDefine result, Document targetDocument, String path) {
		Element defines = (Element) targetDocument.getElementsByTagName(XMLProjectionTag.PRJCOORDSYS_DEFINES).item(0);
		Element define = targetDocument.createElement(XMLProjectionTag.PRJCOORDSYS_DEFINE);

		Element parentCaption = targetDocument.createElement(XMLProjectionTag.PRJGROUP_CAPTION);
		parentCaption.appendChild(targetDocument.createTextNode(result.getParent().getCaption()));
		define.appendChild(parentCaption);

		Element caption = targetDocument.createElement(XMLProjectionTag.PRJCOORDSYS_CAPTION);
		caption.appendChild(targetDocument.createTextNode(result.getCaption()));
		define.appendChild(caption);

		Element type = targetDocument.createElement(XMLProjectionTag.PRJCOORDSYS_TYPE);
		type.appendChild(targetDocument.createTextNode(String.valueOf(result.getCoordSysCode())));
		define.appendChild(type);

		String s = result.getPrjCoordSys().toXML();
		define.appendChild(targetDocument.createCDATASection(s));
		defines.appendChild(define);
		try {
			String string = XmlUtilities.nodeToString(targetDocument, targetDocument.getXmlEncoding());
			string = string.replaceAll("<!\\[CDATA\\[", "");
			string = string.replaceAll("\\]\\]>", "");
			FileUtilities.writeToFile(path, string);
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	/**
	 * @param coordSysDefine
	 */
	private void removeCoordSysDefineFormDoc(CoordSysDefine coordSysDefine) {
		boolean isDel = false;
		if (coordSysDefine.getCoordSysType() == CoordSysDefine.PROJECTION_SYSTEM) {
			if (coordSysDefine.getParent().getCoordSysType() == CoordSysDefine.CUSTOM_COORDINATE) {
				isDel = removeProjFormDoc(coordSysDefine, customProjectionDoc);
			} else if (coordSysDefine.getParent().getCoordSysType() == CoordSysDefine.FAVORITE_COORDINATE) {
				isDel = removeProjFormDoc(coordSysDefine, favoriteProjectionDoc);
			}
		} else if (coordSysDefine.getCoordSysType() == CoordSysDefine.GEOGRAPHY_COORDINATE) {
			if (coordSysDefine.getParent().getCoordSysType() == CoordSysDefine.CUSTOM_COORDINATE) {
				isDel = removeGeoFormDoc(coordSysDefine, customProjectionDoc);
			} else if (coordSysDefine.getParent().getCoordSysType() == CoordSysDefine.FAVORITE_COORDINATE) {
				isDel = removeGeoFormDoc(coordSysDefine, favoriteProjectionDoc);
			}
		}
		if (isDel) {
			if (coordSysDefine.getParent().getCoordSysType() == CoordSysDefine.CUSTOM_COORDINATE) {
				XmlUtilities.saveXml(customProjectionConfigPath, customProjectionDoc, "UTF-8");
			} else if (coordSysDefine.getParent().getCoordSysType() == CoordSysDefine.FAVORITE_COORDINATE) {
				XmlUtilities.saveXml(favoriteProjectionConfigPath, favoriteProjectionDoc, "UTF-8");
			}
			removeFormTree(coordSysDefine);
			CoordSysDefine define = prjModel.getDefine();
			if (define != coordSysDefine) {
				if (define.size() > 0) {
					prjModel.setDefine(define);
				} else {
					removeFormTree(define);
					prjModel.setDefine(null);
				}
			} else {
				prjModel.setDefine(null);
			}
			if (coordSysDefine.getParent() != null && coordSysDefine.getParent().size() <= 1) {
				removeFormTree(coordSysDefine.getParent());
				prjModel.setDefine(null);
			}
		}
	}

	/**
	 * 重构删除实现，无论是否有组，只要是自定义和收藏夹下的数据都可以删除
	 * yuanR
	 *
	 * @param coordSysDefine
	 * @param targetDoc
	 * @return
	 */
	private boolean removeProjFormDoc(CoordSysDefine coordSysDefine, Document targetDoc) {
		NodeList parentNodes = targetDoc.getElementsByTagName(XMLProjectionTag.PRJCOORDSYS_DEFINE);
		//boolean isGroup = false;
		boolean isName = false;
		for (int i = 0; i < parentNodes.getLength(); i++) {
			Node parentNode = parentNodes.item(i);
			if (parentNode != null && parentNode.getNodeType() == Node.ELEMENT_NODE) {
				NodeList nodes = parentNode.getChildNodes();
				for (int j = 0; j < nodes.getLength(); j++) {
					Node node = nodes.item(j);
					if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
						//if (node.getNodeName().equalsIgnoreCase(XMLProjectionTag.PRJGROUP_CAPTION)) {
						//	//isGroup = node.getTextContent().equals(coordSysDefine.getParent().getCaption());
						//} else
						if (node.getNodeName().equalsIgnoreCase(XMLProjectionTag.PRJCOORDSYS_CAPTION)) {
							isName = node.getTextContent().equals(coordSysDefine.getCaption());
						}
					}
				}
			}
			if (isName) {
				parentNode.getParentNode().removeChild(parentNode);
				return true;
			}
			//isGroup = false;
			isName = false;
		}
		return false;
	}

	private void removeFormTree(CoordSysDefine coordSysDefine) {
		CoordSysDefine parent = getMiddleParent(coordSysDefine, null);
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePrjCoordSys.getModel().getRoot();
		while (parent != coordSysDefine) {
			parent = getMiddleParent(coordSysDefine, parent);
			node = getNode(parent, node);
		}
		if (node != null) {
			((DefaultMutableTreeNode) node.getParent()).remove(node);
			treePrjCoordSys.updateUI();
		}

	}

	private CoordSysDefine getMiddleParent(CoordSysDefine coordSysDefine, CoordSysDefine parent) {
		while (coordSysDefine.getParent() != parent) {
			coordSysDefine = coordSysDefine.getParent();
		}
		return coordSysDefine;
	}

	private DefaultMutableTreeNode getNode(CoordSysDefine parent, DefaultMutableTreeNode root) {
		if (root.getUserObject() == parent) {
			return root;
		}
		for (int k = 0; k < root.getChildCount(); k++) {
			DefaultMutableTreeNode childAt = (DefaultMutableTreeNode) root.getChildAt(k);
			CoordSysDefine userObject = (CoordSysDefine) childAt.getUserObject();
			if (userObject == parent) {
				return childAt;
			}
		}
		return null;
	}

	private void save() {
		XmlUtilities.saveXml(projectionConfigPath, projectionDoc, "UTF-8");
	}

	private boolean removeGeoFormDoc(CoordSysDefine coordSysDefine, Document targetDoc) {
		NodeList parentNodes = targetDoc.getElementsByTagName(XMLProjectionTag.GEOCOORDSYS_DEFINE);
		//boolean isGroup = false;
		boolean isName = false;
		for (int i = 0; i < parentNodes.getLength(); i++) {
			Node parentNode = parentNodes.item(i);
			if (parentNode != null && parentNode.getNodeType() == Node.ELEMENT_NODE) {
				NodeList nodes = parentNode.getChildNodes();
				for (int j = 0; j < nodes.getLength(); j++) {
					Node node = nodes.item(j);
					if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
						//if (node.getNodeName().equalsIgnoreCase(XMLProjectionTag.GEOGROUP_CATION)) {
						//	isGroup = node.getTextContent().equals(coordSysDefine.getParent().getCaption());
						//} else
						if (node.getNodeName().equalsIgnoreCase(XMLProjectionTag.GEOCOORDSYS_CAPTION)) {
							isName = node.getTextContent().equals(coordSysDefine.getCaption());
						}
					}
				}
			}
			if (isName) {
				parentNode.getParentNode().removeChild(parentNode);
				return true;
			}
			//isGroup = false;
			isName = false;
		}
		return false;
	}

	/**
	 * 添加到树上
	 *
	 * @param value           结果节点
	 * @param parentName      父节点名称
	 * @param parentValue     父节点值
	 * @param grantParentName 爷爷节点的值
	 */
	private void addToTree(CoordSysDefine value, String parentName, CoordSysDefine parentValue, String grantParentName) {
		Object root = treePrjCoordSys.getModel().getRoot();
		if (root != null && root instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode grandParentNode = null;
			DefaultMutableTreeNode parentNode = null;
			for (int i = 0; i < ((DefaultMutableTreeNode) root).getChildCount(); i++) {
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) root).getChildAt(i);

				if (((CoordSysDefine) treeNode.getUserObject()).getCaption().equals(grantParentName)) {
					grandParentNode = treeNode;
					for (int j = 0; j < treeNode.getChildCount(); j++) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeNode.getChildAt(j);
						if (node != null && ((CoordSysDefine) node.getUserObject()).getCaption().equals(parentName)) {
							parentNode = node;
							break;
						}
					}
				}
			}
			if (parentNode == null) {
				parentNode = createNode(parentValue);
				((DefaultTreeModel) treePrjCoordSys.getModel()).insertNodeInto(parentNode, grandParentNode, grandParentNode.getChildCount());
				JTreeUIUtilities.locateNode(treePrjCoordSys, (DefaultMutableTreeNode) parentNode.getChildAt(0));
			} else {
				java.util.List<String> names = new ArrayList<>();
				for (int i = 0; i < parentNode.getChildCount(); i++) {
					CoordSysDefine userObject = (CoordSysDefine) ((DefaultMutableTreeNode) parentNode.getChildAt(i)).getUserObject();
					names.add(userObject.getCaption());
				}
				value.setCaption(getSingletonName(value.getCaption(), names));
				DefaultMutableTreeNode node = createNode(value);
				((DefaultTreeModel) treePrjCoordSys.getModel()).insertNodeInto(node, parentNode, parentNode.getChildCount());
				JTreeUIUtilities.locateNode(treePrjCoordSys, node);
			}
		}
	}

	private String getSingletonName(String caption, List<String> names) {
		for (int i = 0; true; i++) {
			if (!names.contains(getName(caption, i))) {
				return getName(caption, i);
			}
		}
	}

	private String getName(String caption, int i) {
		if (i == 0) {
			return caption;
		}
		return caption + "_" + i;
	}

	public void setPopupmenu(JPopupMenu popupmenu) {
		this.popupmenu = popupmenu;
	}

	/**
	 * 在 Table 上展示搜索结果的 Model
	 *
	 * @author highsad
	 */
	private class SearchResultModel extends AbstractPrjTableModel {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		private ArrayList<CoordSysDefine> defines = new ArrayList<>();

		public void add(CoordSysDefine define) {
			if (!this.defines.contains(define)) {
				this.defines.add(define);
			}
		}

		@Override
		public int getRowCount() {
			return this.defines.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (this.defines == null) {
				return null;
			}

			CoordSysDefine item = null;
			if (!this.defines.isEmpty()) {
				item = this.defines.get(rowIndex);
				if (columnIndex == CAPTION) {
					return item.getCaption();
				} else if (columnIndex == TYPE) {
					return item.getCoordSysTypeDescription();
				} else if (columnIndex == GROUP) {
					return item.getParent().getCaption();
				} else {
					return null;
				}
			}
			return null;
		}

		@Override
		public CoordSysDefine getRowData(int row) {
			CoordSysDefine result = null;

			try {
				if (!this.defines.isEmpty() && 0 <= row && row < this.defines.size()) {
					result = this.defines.get(row);
				}
			} catch (Exception e) {
				Application.getActiveApplication().getOutput().output(e);
			}
			return result;
		}

	}

	/**
	 * 通过导入的投影文件获得文件投影信息
	 *
	 * @return
	 */
	private PrjCoordSys getPrjCoordSysFromImportFile(String path) {

		if (!new File(path).exists()) {
			return null;
		} else {
			PrjCoordSys newPrjCoorSys = new PrjCoordSys();
			String fileType = FileUtilities.getFileType(path);
			boolean isPrjFile;
			if (fileType.equalsIgnoreCase(".prj")) {
				isPrjFile = newPrjCoorSys.fromFile(path, PrjFileType.ESRI);
			} else {
				isPrjFile = newPrjCoorSys.fromFile(path, PrjFileType.SUPERMAP);
			}
			// 去除导入平面无投影坐标系
			if (isPrjFile && newPrjCoorSys.getType() != PrjCoordSysType.PCS_NON_EARTH) {
				return newPrjCoorSys;
			} else {
				return null;
			}
		}
	}

	public void selectRootNode() {
		if (this.treePrjCoordSys.getRowCount() > 0) {
			treePrjCoordSys.setSelectionRow(0);
		}
	}

	/**
	 * 外部去除节点，定制
	 * 未完成
	 * yuanR2017.10.18
	 *
	 * @param num
	 */
	public void removeRoot(int[] num) {
		for (int aNum : num) {
			switch (aNum) {
				case CoordSysDefine.NONE_ERRTH:
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) getNodeByDefine((DefaultMutableTreeNode) this.treePrjCoordSys.getModel().getRoot(), currentPrjDefine.getParent());
					// HOW TO DO 隐藏节点？
				case CoordSysDefine.PROJECTION_SYSTEM:

				case CoordSysDefine.GEOGRAPHY_COORDINATE:

			}
		}
	}
}
