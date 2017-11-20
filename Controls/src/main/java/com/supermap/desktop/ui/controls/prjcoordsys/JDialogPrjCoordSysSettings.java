package com.supermap.desktop.ui.controls.prjcoordsys;

import com.supermap.data.Enum;
import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.ControlsResources;
import com.supermap.desktop.controls.utilities.JTreeUIUtilities;
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
import com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels.DefaultCoordsysTreeCellRenderer;
import com.supermap.desktop.utilities.*;
import org.w3c.dom.Document;
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


/**
 * @author yuanR2017.11.17
 * 坐标系设置
 **/
public class JDialogPrjCoordSysSettings extends SmDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_PROJECTION_CONFIG_PATH = "/controlsresources/Projection.xml";

	private JButton buttonImport;
	private JButton buttonExport;
	private JButton buttonFavorites;
	private JButton buttonNewCoordSys;
	private JButton buttonNewGroup;
	private JButton buttonDelete;
	private JPopupMenu popupMenuNewCoordSys;
	private JMenuItem menuItemNewPrjCoordSysClone;
	private JMenuItem menuItemNewGeoCoordSysClone;
	private JMenuItem menuItemNewFormEPSGClone;
	private TextFieldSearch textFieldSearch;

	// table和tree的右键菜单
	private JPopupMenu popupmenu;
	private JMenu menuNewCoordsys;
	private JMenuItem menuItemNewPrjCoordSys;
	private JMenuItem menuItemNewGeoCoordSys;
	private JMenuItem menuItemNewFormEPSG;
	private JMenuItem menuItemNewGroup;
	private JMenuItem menuItemImportCoordSys;
	private JMenuItem menuItemExportCoordSys;
	private JMenuItem menuItemAddFavorites;
	private JMenuItem menuItemDelete;

	private JSplitPane splitPaneMain; // 整个投影选择区域的主面板
	private JTree treePrjCoordSys; // 读取加载投影信息的树，主面板左边区域
	private JSplitPane splitPaneDetails; // 读取加载选中树节点的具体内容，以及选定投影详细信息的面板，主面板右边区域
	private JTable tablePrjCoordSys; // 读取加载选中树节点的具体内容的 Table，主面板右边区域上半区域
	private JTextArea textAreaDetail; // 显示选定投影详细信息，主面板右边区域下半区域

	private SmButton buttonApply;
	private SmButton buttonClose;

	// 平面坐标系定义集合
	private transient CoordSysDefine noneEarth = new CoordSysDefine(CoordSysDefine.NONE_EARTH, null,
			ControlsProperties.getString("String_NoneEarth")).setFolderNode(true);
	// 投影坐标系统定义集合
	private transient CoordSysDefine projectionSystem = new CoordSysDefine(CoordSysDefine.PROJECTION_SYSTEM, null,
			ControlsProperties.getString("String_PrjCoorSys")).setFolderNode(true);
	// 地理坐标系定义集合
	private transient CoordSysDefine geographyCoordinate = new CoordSysDefine(CoordSysDefine.GEOGRAPHY_COORDINATE, null,
			ControlsProperties.getString("String_GeoCoordSys")).setFolderNode(true);
	// 自定义坐标系集合
	private transient CoordSysDefine customizeCoordinate = new CoordSysDefine(CoordSysDefine.CUSTOM_COORDINATE, null,
			ControlsProperties.getString("String_Customize")).setFolderNode(true);
	// 收藏夹坐标系集合
	private transient CoordSysDefine favoriteCoordinate = new CoordSysDefine(CoordSysDefine.FAVORITE_COORDINATE, null,
			ControlsProperties.getString("String_Favorite")).setFolderNode(true);
	private transient CoordSysDefine rootDefine;

	// 当前选中的坐标系
	private transient CoordSysDefine currentDefine = null;
	private transient PrjCoordSys prjCoordSys = null;

	private String projectionConfigPath = "";
	//private String userGeoCoordsysFolderPath = "";
	//private String userPrjCoordsysFolderPath = "";
	//private String userCoordsysFromEPSGFolderPath = "";
	//private String userImportCoordsysFolderPath = "";
	private String favoriteProjectionConfigPath = "";
	private String customizeProjectionConfigPath = "";
	//private String userDefineGeoParentName = "UserGeoCoordsys";
	//private String userDefinePrjParentName = "UserPrjCoordsys";
	//private String userCoordsysFromEPSGParentName = "UserCoordsysFromEPSG";
	//private String userImportCoordsysParentName = "UserImportCoordsys";

	private transient Document projectionDoc = null;
	private PrjCoordSysTableModel prjModel = new PrjCoordSysTableModel();

	private int successedExportNum = 0;

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
			if ((e.getSource() == treePrjCoordSys.getParent() || e.getSource() == treePrjCoordSys) && e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 1) {
				treeMouseRightClicked(e);
			}
			if ((e.getSource() == treePrjCoordSys.getParent() || e.getSource() == treePrjCoordSys) && e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
				treeMouseLeftClicked(e);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {

			if (e.getButton() != MouseEvent.BUTTON1 || e.getClickCount() != 2) {
				return;
			}

			if (e.getSource() == tablePrjCoordSys) {
				tableMouseDoubleClick(e);
			}
		}
	};

	private transient ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(buttonApply)) {
				buttonApplyClicked();
			} else if (e.getSource().equals(buttonClose)) {
				buttonCloseClicked();
			} else if (e.getSource().equals(menuItemNewPrjCoordSys) || e.getSource().equals(menuItemNewPrjCoordSysClone)) {
				newPrjCoordsys();
			} else if (e.getSource().equals(menuItemNewGeoCoordSys) || e.getSource().equals(menuItemNewGeoCoordSysClone)) {
				newGeoCoordsys();
			} else if (e.getSource().equals(menuItemNewFormEPSG) || e.getSource().equals(menuItemNewFormEPSGClone)) {
				newCoordsysFromEPSG();
			} else if (e.getSource().equals(buttonImport) || e.getSource().equals(menuItemImportCoordSys)) {
				importActive();
			} else if (e.getSource().equals(buttonExport) || e.getSource().equals(menuItemExportCoordSys)) {
				exportActive();
			} else if (e.getSource().equals(buttonNewCoordSys)) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						popupMenuNewCoordSys.show(buttonNewCoordSys, 0, buttonNewCoordSys.getHeight());
					}
				});
			} else if (e.getSource().equals(buttonFavorites) || e.getSource().equals(menuItemAddFavorites)) {
				// 将选中的投影添加到收藏夹当中
				addCoordsysToFavorites();
			} else if (e.getSource().equals(menuItemDelete) || e.getSource().equals(buttonDelete)) {
				deleteCoordsysFormFolder();
			} else if (e.getSource().equals(buttonNewGroup) || e.getSource().equals(menuItemNewGroup)) {
				// todo 新建组
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

	public JDialogPrjCoordSysSettings(String targetTitle) {
		this();
		// 坐标系设置title根据选中数据进行展开
		this.setTitle(ControlsProperties.getString("String_SetCoordsys") + "-[" + targetTitle + "]");
	}

	/**
	 * Create the dialog.
	 */
	public JDialogPrjCoordSysSettings() {
		try {
			initializeComponents();
			initializeResources();
			// 加载默认投影配置文件
			loadProjectionConfig();
			// 构建平面坐标系定义数据
			buildNoneEarthDefines();
			// 构建投影坐标系统定义数据
			buildProjectionSystemDefines();
			// 构建地理坐标系定义数据
			buildGeographyCoordinateDefines();
			// 构建自定义节点数据
			buildCustomCoordinateDefines();
			// 构建收藏夹节点数据
			buildFavoriteCoordinateDefines();
			bulidRootDefine();
			// 构造显示投影系统结构的树
			initializeTreePrjCoordSys();
			registerEvents();
			setControlsEnabled();
			setSize(new Dimension(1100, 600));
			setLocationRelativeTo(null);
			selectRootNode();
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}


	/**
	 * 初始化根节点
	 */
	private void bulidRootDefine() {
		rootDefine = new CoordSysDefine(CoordSysDefine.USER_DEFINED);
		rootDefine.add(noneEarth);
		rootDefine.add(projectionSystem);
		rootDefine.add(geographyCoordinate);
		rootDefine.add(customizeCoordinate);
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

		// 弹菜单
		if (currentDefine != null) {
			getPopupmenu().show(tablePrjCoordSys, e.getX(), e.getY());
		}
	}

	/**
	 * tree的鼠标右键响应事件
	 * 每次点击tree时，不论选择的节点是否改变，都需要重置选中，目的是为了重置currentDefine
	 *
	 * @param e
	 */
	private void treeMouseRightClicked(MouseEvent e) {
		this.treePrjCoordSys.setSelectionPath(null);
		// 确保当点击tree之前的展开按钮时也可以获得选中的位置
		TreePath path = this.treePrjCoordSys.getPathForLocation(e.getX() + 20, e.getY());
		this.treePrjCoordSys.setSelectionPath(path);
		if (currentDefine != null) {
			getPopupmenu().show(treePrjCoordSys, e.getX(), e.getY());
		}
	}

	/**
	 * tree的鼠标左键响应事件
	 * 每次点击tree时，不论选择的节点是否改变，都需要重置选中，目的是为了重置currentDefine
	 *
	 * @param e
	 */
	private void treeMouseLeftClicked(MouseEvent e) {
		this.treePrjCoordSys.setSelectionPath(null);
		// 确保当点击tree之前的展开按钮时也可以获得选中的位置
		TreePath path = this.treePrjCoordSys.getPathForLocation(e.getX() + 20, e.getY());
		this.treePrjCoordSys.setSelectionPath(path);
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
		this.currentDefine = null;
		if (this.prjCoordSys != null) {
			// 按照 收藏夹>自定义>平面>地理>投影顺序进行查找
			if (this.prjCoordSys.getType() == PrjCoordSysType.PCS_NON_EARTH) { // 平面坐标系
				this.currentDefine = this.noneEarth.getChildByCoordSysCode(this.prjCoordSys.getCoordUnit().value());
			} else if (this.prjCoordSys.getType() == PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) { // 地理坐标系
				GeoCoordSys geoCoordSys = this.prjCoordSys.getGeoCoordSys();
				if (this.currentDefine == null) {
					this.currentDefine = this.favoriteCoordinate.getChildByCoordSysCode(geoCoordSys.getType().value());
				}
				if (this.currentDefine == null) {
					this.currentDefine = this.customizeCoordinate.getChildByCoordSysCode(geoCoordSys.getType().value());
				}
				if (this.currentDefine == null && geoCoordSys.getType() != GeoCoordSysType.GCS_USER_DEFINE) {
					this.currentDefine = this.geographyCoordinate.getChildByCoordSysCode(geoCoordSys.getType().value());
				}
			} else { // 投影坐标系统
				if (this.currentDefine == null) {
					this.currentDefine = this.favoriteCoordinate.getChildByCoordSysCode(this.prjCoordSys.getType().value());
				}
				if (this.currentDefine == null) {
					this.currentDefine = this.customizeCoordinate.getChildByCoordSysCode(this.prjCoordSys.getType().value());
				}
				if (this.currentDefine == null && this.prjCoordSys.getType() != PrjCoordSysType.PCS_USER_DEFINED) {
					this.currentDefine = this.projectionSystem.getChildByCoordSysCode(this.prjCoordSys.getType().value());
				}
			}
		}
		if (this.currentDefine == null) {
			this.treePrjCoordSys.setSelectionRow(4);
		} else {
			CoordSysDefine targetDefine = this.currentDefine;
			// 获取根节点下与 currentDefine 绑定的子节点
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) getNodeByDefine((DefaultMutableTreeNode) this.treePrjCoordSys.getModel().getRoot(),
					this.currentDefine.getParent());
			if (node != null) {
				TreePath path = new TreePath(node.getPath());
				// 选中
				this.treePrjCoordSys.setSelectionPath(path);
				// 展开
				this.treePrjCoordSys.expandPath(path);
				// 滚动到可见
				this.treePrjCoordSys.scrollPathToVisible(path);

				//tree已定位到选中数据的父节点上，再定位table
				AbstractPrjTableModel model = (AbstractPrjTableModel) this.tablePrjCoordSys.getModel();
				for (int i = 0; i < model.getRowCount(); i++) {
					if (targetDefine.equals(model.getRowData(i))) {
						tablePrjCoordSys.setRowSelectionInterval(i, i);
						tablePrjCoordSys.scrollRectToVisible(tablePrjCoordSys.getCellRect(i, 0, true));
					}
				}
			}
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
		JToolBar toolBarButton = createToolBarButton();
		JPanel centerPanel = createCenterPanel();

		// @formatter:off
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.CENTER)
				.addComponent(toolBarButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addComponent(centerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE));

		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addComponent(toolBarButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(centerPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE));
		// @formatter:on

		getPopupmenu();
	}

	private void initializeResources() {
		// 坐标系设置title根据选中数据进行展开
		this.setTitle(ControlsProperties.getString("String_SetCoordsys"));
		this.buttonApply.setText(CoreProperties.getString(CoreProperties.Apply));
		this.buttonClose.setText(CoreProperties.getString(CoreProperties.Close));
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
		this.menuItemNewPrjCoordSysClone.addActionListener(this.actionListener);
		this.menuItemNewGeoCoordSys.addActionListener(this.actionListener);
		this.menuItemNewGeoCoordSysClone.addActionListener(this.actionListener);
		this.menuItemNewFormEPSG.addActionListener(this.actionListener);
		this.menuItemNewFormEPSGClone.addActionListener(this.actionListener);
		this.buttonNewGroup.addActionListener(this.actionListener);
		this.buttonDelete.addActionListener(this.actionListener);
		this.buttonApply.addActionListener(this.actionListener);
		this.buttonClose.addActionListener(this.actionListener);
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
		this.menuItemNewPrjCoordSysClone.removeActionListener(this.actionListener);
		this.menuItemNewGeoCoordSys.removeActionListener(this.actionListener);
		this.menuItemNewGeoCoordSysClone.removeActionListener(this.actionListener);
		this.menuItemNewFormEPSG.removeActionListener(this.actionListener);
		this.menuItemNewFormEPSGClone.removeActionListener(this.actionListener);
		this.buttonNewGroup.removeActionListener(this.actionListener);
		this.buttonDelete.removeActionListener(this.actionListener);
		this.buttonApply.removeActionListener(this.actionListener);
		this.buttonClose.removeActionListener(this.actionListener);
		this.textFieldSearch.getDocument().removeDocumentListener(this.documentListener);
	}


	/**
	 * 创建按钮工具条
	 * yuanR
	 *
	 * @return
	 */
	private JToolBar createToolBarButton() {
		JToolBar toolBarButton = new JToolBar();
		toolBarButton.setFloatable(false);

		this.buttonImport = new JButton(ControlsProperties.getString("String_Import"), CoreResources.getIcon("/coreresources/ToolBar/Image_ToolButton_Import.png"));
		this.buttonExport = new JButton(ControlsProperties.getString("String_Export"), CoreResources.getIcon("/coreresources/ToolBar/Image_ToolButton_Export.png"));
		this.buttonFavorites = new JButton(CoreProperties.getString("String_Collent"), CoreResources.getIcon("/coreresources/ToolBar/Image_Favorite.png"));
		this.buttonNewCoordSys = new JButton(ControlsProperties.getString("String_NewCoorSys"), CoreResources.getIcon("/coreresources/ToolBar/Image_NewCoordsys.png"));
		this.buttonNewGroup = new JButton(ControlsProperties.getString("String_NewGroup"), ControlsResources.getIcon("/controlsresources/SortType/Image_NewGroup.png"));
		this.buttonDelete = new JButton(CoreProperties.getString(CoreProperties.Delete), CoreResources.getIcon("/coreresources/ToolBar/Image_ToolButton_Delete.png"));
		this.textFieldSearch = new TextFieldSearch();
		this.textFieldSearch.setPreferredSize(new Dimension(150, 30));

		this.popupMenuNewCoordSys = new JPopupMenu();
		this.menuItemNewPrjCoordSysClone = new JMenuItem(ControlsProperties.getString("String_PrjCoorSys"));
		this.menuItemNewGeoCoordSysClone = new JMenuItem(ControlsProperties.getString("String_GeoCoordSys"));
		this.menuItemNewFormEPSGClone = new JMenuItem(ControlsProperties.getString("String_Button_NewCoordSysFormEPSG"));

		this.popupMenuNewCoordSys.add(this.menuItemNewPrjCoordSysClone);
		this.popupMenuNewCoordSys.add(this.menuItemNewGeoCoordSysClone);
		this.popupMenuNewCoordSys.add(this.menuItemNewFormEPSGClone);

		toolBarButton.add(this.buttonImport);
		toolBarButton.add(this.buttonExport);
		toolBarButton.addSeparator();
		toolBarButton.add(this.buttonFavorites);
		toolBarButton.add(this.buttonNewCoordSys);
		toolBarButton.add(this.buttonNewGroup);
		toolBarButton.addSeparator();
		toolBarButton.add(this.buttonDelete);
		toolBarButton.addSeparator();
		toolBarButton.add(this.textFieldSearch);

		return toolBarButton;
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
		createtreePrjCoordSys();
		this.treePrjCoordSys = createtreePrjCoordSys();
		this.treePrjCoordSys.setCellRenderer(new DefaultCoordsysTreeCellRenderer());
		scrollPane.setViewportView(this.treePrjCoordSys);
		this.splitPaneMain.setLeftComponent(scrollPane);
		this.splitPaneMain.setRightComponent(createSplitPaneDetails());
		return this.splitPaneMain;
	}


	private JTree createtreePrjCoordSys() {
		JTree tree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode(ControlsProperties.getString("String_CoordSystem"))));
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		// 设置树节点前展开小按钮可见-yuanR2017.11.13
		tree.setShowsRootHandles(true);
		tree.setRootVisible(false);
		return tree;
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
	private void buildCustomCoordinateDefines() {

		//// 加载自定义地理坐标系文件
		//if (StringUtilities.isNullOrEmpty(this.userGeoCoordsysFolderPath)) {
		//	this.userGeoCoordsysFolderPath = PathUtilities.getFullPathName(XMLProjectionTag.CUSTOMPROJECTION_FOLDER + "\\" + "UserGeoCoordsys", true);
		//}
		//CoordSysDefine userGeoParentDefine = customizeCoordinate.getChildByCaption(userDefineGeoParentName);
		//if (userGeoParentDefine == null) {
		//	userGeoParentDefine = new CoordSysDefine(CoordSysDefine.CUSTOM_COORDINATE, customizeCoordinate, userDefineGeoParentName).setFolderNode(true);
		//}
		//reloadCoordSysFromFolderPath(userGeoCoordsysFolderPath, userGeoParentDefine);
		//
		//// 加载自定义投影坐标系文件
		//if (StringUtilities.isNullOrEmpty(this.userPrjCoordsysFolderPath)) {
		//	this.userPrjCoordsysFolderPath = PathUtilities.getFullPathName(XMLProjectionTag.CUSTOMPROJECTION_FOLDER + "\\" + "UserPrjCoordsys", true);
		//}
		//CoordSysDefine userPrjParentDefine = customizeCoordinate.getChildByCaption(userDefinePrjParentName);
		//if (userPrjParentDefine == null) {
		//	userPrjParentDefine = new CoordSysDefine(CoordSysDefine.CUSTOM_COORDINATE, customizeCoordinate, userDefinePrjParentName).setFolderNode(true);
		//}
		//reloadCoordSysFromFolderPath(userPrjCoordsysFolderPath, userPrjParentDefine);
		//
		//// 加载自定义来自EPSG投影文件
		//if (StringUtilities.isNullOrEmpty(this.userCoordsysFromEPSGFolderPath)) {
		//	this.userCoordsysFromEPSGFolderPath = PathUtilities.getFullPathName(XMLProjectionTag.CUSTOMPROJECTION_FOLDER + "\\" + "UserCoordsysFromEPSG", true);
		//}
		//CoordSysDefine userCoordsysFromEPSGParentDefine = customizeCoordinate.getChildByCaption(userCoordsysFromEPSGParentName);
		//if (userCoordsysFromEPSGParentDefine == null) {
		//	userCoordsysFromEPSGParentDefine = new CoordSysDefine(CoordSysDefine.CUSTOM_COORDINATE, customizeCoordinate, userCoordsysFromEPSGParentName).setFolderNode(true);
		//}
		//reloadCoordSysFromFolderPath(userCoordsysFromEPSGFolderPath, userCoordsysFromEPSGParentDefine);
		//
		//// 加载自定义导入投影文件
		//if (StringUtilities.isNullOrEmpty(this.userImportCoordsysFolderPath)) {
		//	this.userImportCoordsysFolderPath = PathUtilities.getFullPathName(XMLProjectionTag.CUSTOMPROJECTION_FOLDER + "\\" + "UserImportCoordsys", true);
		//}
		//CoordSysDefine userImportCoordsysParentDefine = customizeCoordinate.getChildByCaption(userImportCoordsysParentName);
		//if (userImportCoordsysParentDefine == null) {
		//	userImportCoordsysParentDefine = new CoordSysDefine(CoordSysDefine.CUSTOM_COORDINATE, customizeCoordinate, userImportCoordsysParentName).setFolderNode(true);
		//}
		//reloadCoordSysFromFolderPath(userImportCoordsysFolderPath, userImportCoordsysParentDefine);
		if (StringUtilities.isNullOrEmpty(this.customizeProjectionConfigPath)) {
			this.customizeProjectionConfigPath = PathUtilities.getFullPathName(XMLProjectionTag.CUSTOMPROJECTION_FOLDER, true);
		}
		reloadCoordSysFromFolderPath(customizeProjectionConfigPath, customizeCoordinate);
	}

	/**
	 * 加载收藏的投影config
	 */
	private void buildFavoriteCoordinateDefines() {
		if (StringUtilities.isNullOrEmpty(this.favoriteProjectionConfigPath)) {
			this.favoriteProjectionConfigPath = PathUtilities.getFullPathName(XMLProjectionTag.FAVORITEPROJECTION_FOLDER, true);
		}
		reloadCoordSysFromFolderPath(favoriteProjectionConfigPath, favoriteCoordinate);
	}


	/**
	 * 加载文件夹下的坐标系文件
	 */
	private void reloadCoordSysFromFolderPath(String path, CoordSysDefine coordSysDefine) {
		try {
			if (!FileUtilities.exists(path)) {
				//当文件夹不存在时， 新建文件夹
				File file = new File(path);
				file.mkdirs();
			}
			ArrayList<String> fileList;
			fileList = getFileDirectory(new File(path));
			// 先不考虑层级问题，默认收藏夹中不存在文件夹只有投影文件
			for (String aFileList : fileList) {
				addToCoordSysDefine(getPrjCoordSysFromImportFile(aFileList), coordSysDefine);
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	/**
	 * 获得文件文件夹下所有文件夹
	 *
	 * @param file
	 */
	private void getFolderDirectory(File file, ArrayList<String> folderList) {
		File flist[] = file.listFiles();
		for (File f : flist) {
			if (f.isDirectory()) {
				//这里将列出所有的文件夹
				folderList.add(f.getAbsolutePath());
				getFolderDirectory(f, folderList);
			}
		}
	}


	/**
	 * 获得文件夹下所有xml文件
	 *
	 * @param file
	 * @return
	 */
	private ArrayList<String> getFileDirectory(File file) {
		ArrayList<String> filelist = new ArrayList<>();
		File flist[] = file.listFiles();
		for (File f : flist) {
			if (!f.isDirectory()) {
				//这里将列出所有的xml文件
				if ((f.getAbsolutePath().endsWith(".xml"))) {
					filelist.add(f.getAbsolutePath());
				}
			}
		}
		return filelist;
	}


	/**
	 * 构建平面坐标系定义集合
	 */
	private void buildNoneEarthDefines() {
		Enum[] units = Enum.getEnums(Unit.class);

		for (Enum unit1 : units) {
			Unit unit = (Unit) unit1;
			CoordSysDefine coordSysDefine = new CoordSysDefine(CoordSysDefine.NONE_EARTH, this.noneEarth, unit.toString()).setFolderNode(false);
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

		if (result.getParent() == null) {
			coordSysDefine.add(result);
		}
		return result;
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
		return result;
	}

	private void initializeTreePrjCoordSys() {
		DefaultTreeModel treeModel = (DefaultTreeModel) this.treePrjCoordSys.getModel();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
		rootNode.add(createNode(this.noneEarth));
		rootNode.add(createNode(this.projectionSystem));
		rootNode.add(createNode(this.geographyCoordinate));
		rootNode.add(createNode(this.customizeCoordinate));
		rootNode.add(createNode(this.favoriteCoordinate));
		this.treePrjCoordSys.expandPath(new TreePath(rootNode.getPath()));
	}

	/**
	 * 创建tree节点
	 *
	 * @param define
	 * @return
	 */
	private DefaultMutableTreeNode createNode(CoordSysDefine define) {
		DefaultMutableTreeNode result = new DefaultMutableTreeNode(define);
		for (int i = 0; i < define.size(); i++) {
			// tree节点中只显示文件夹，不显示末层数据
			if (define.get(i).getIsFolderNode()) {
				result.add(createNode(define.get(i)));
			}
		}
		return result;
	}

	private void treeSelectionChange() {
		try {
			// 点击 Expand / Collapse 也会触发 SelectionChange，然而此时 SelectionPath 为空
			if (this.treePrjCoordSys.getSelectionPath() != null) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) this.treePrjCoordSys.getSelectionPath().getLastPathComponent();

				//if (selectedNode == treePrjCoordSys.getModel().getRoot()) {
				// 当去除了根节点的显示，此判断无效
				//this.prjModel.setDefine(rootDefine);
				//this.currentDefine = null;
				//} else
				if (selectedNode.getUserObject() instanceof CoordSysDefine && ((CoordSysDefine) selectedNode.getUserObject()).size() > 0) {
					this.prjModel.setDefine((CoordSysDefine) selectedNode.getUserObject());
					this.currentDefine = (CoordSysDefine) selectedNode.getUserObject();
				} else {
					this.prjModel.setDefine(null);
					this.currentDefine = null;
				}

				// Table 上有可能是搜索结果的 Model，这时候就要重新设置一下 Model
				if (this.tablePrjCoordSys.getModel() != this.prjModel) {
					this.tablePrjCoordSys.setModel(this.prjModel);
				}
				clearSearchText();
				refreshTextAreaDetails();
				setControlsEnabled();
			}

		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	private void tableSelectionChange() {
		int row = this.tablePrjCoordSys.getSelectedRow();
		if (row >= 0) {
			AbstractPrjTableModel model = (AbstractPrjTableModel) this.tablePrjCoordSys.getModel();
			this.currentDefine = model.getRowData(row);
		} else {
			this.currentDefine = null;
		}
		refreshTextAreaDetails();
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
				JTreeUIUtilities.locateNode(this.treePrjCoordSys, toSelectedNode);
				this.treePrjCoordSys.scrollRectToVisible(new Rectangle(0, treePrjCoordSys.getVisibleRect().y, treePrjCoordSys.getVisibleRect().width, treePrjCoordSys.getVisibleRect().height));
			}
		} else { // 如果双击的项没有子项，那么就是具体的投影定义，提示选择应用
			if (clickedPrjDefine.getCoordSysType() != CoordSysDefine.CUSTOM_COORDINATE
					&& clickedPrjDefine.getCoordSysType() != CoordSysDefine.FAVORITE_COORDINATE) {
				confirmSelected();
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
		if (this.currentDefine != null && this.currentDefine.size() == 0) {
			if (showConfirmMessage() == JOptionPane.YES_OPTION) {
				applyPrjCoordSys();
			} else {
				this.prjCoordSys = null;
			}
		}
	}

	/**
	 * @return JOptionPane.YES_OPTION / JOptionPane.NO_OPTION / JOptionPane.CLOSED_OPTION
	 */
	private int showConfirmMessage() {
		return UICommonToolkit.showConfirmDialogYesNo(ControlsProperties.getString("String_message_Apply"));
	}


	/**
	 * 应用选中的投影
	 */
	private void applyPrjCoordSys() {
		if (this.currentDefine.getCoordSysType() == CoordSysDefine.NONE_EARTH) {
			this.prjCoordSys = new PrjCoordSys(PrjCoordSysType.PCS_NON_EARTH);
			this.prjCoordSys.setCoordUnit((Unit) Enum.parse(Unit.class, this.currentDefine.getCoordSysCode()));
		} else if (this.currentDefine.getCoordSysType() == CoordSysDefine.PROJECTION_SYSTEM) {
			this.prjCoordSys = PrjCoordSysSettingsUtilties.getPrjCoordSys(this.currentDefine);
		} else if (this.currentDefine.getCoordSysType() == CoordSysDefine.GEOGRAPHY_COORDINATE) {
			GeoCoordSys geoCoordSys = PrjCoordSysSettingsUtilties.getGeoCoordSys(this.currentDefine);
			this.prjCoordSys = new PrjCoordSys(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
			this.prjCoordSys.setGeoCoordSys(geoCoordSys);
		} else {
			this.prjCoordSys = null;
		}

		this.dialogResult = DialogResult.OK;
		setVisible(false);
	}


	/**
	 * 清除搜索栏
	 */
	private void clearSearchText() {
		this.textFieldSearch.setText("");
	}


	/**
	 * 当前选中的投影信息发生改变时，刷新投影信息详情
	 */
	private void refreshTextAreaDetails() {
		this.textAreaDetail.setText(PrjCoordSysSettingsUtilties.getDescription(this.currentDefine));
	}

	private void setControlsEnabled() {
		this.buttonImport.setEnabled(isImportButtonEnable());
		this.buttonExport.setEnabled(isExportEnable());
		this.buttonFavorites.setEnabled(isAddFavoritesEnable());
		this.buttonNewGroup.setEnabled(isNewGroupEnable());
		this.menuItemNewGeoCoordSysClone.setEnabled(isNewGeoCoordsysEnable());
		this.menuItemNewPrjCoordSysClone.setEnabled(isNewPrjCoordsysEnable());
		this.menuItemNewFormEPSGClone.setEnabled(isNewCoordsysFromEPSGEnable());
		this.buttonDelete.setEnabled(isDeleteEnable());
		this.buttonApply.setEnabled(isButtonApplyEnable());
	}

	private void search(String pattern) {
		SearchResultModel searchModel = new SearchResultModel();
		// 如果当前选中的投影不为空，就搜索当前选中的投影，否则就搜索所有
		if (this.currentDefine != null) {
			searchDefine(pattern, this.currentDefine, searchModel);
		}
		if (searchModel.getRowCount() <= 0) {
			searchDefine(pattern, this.noneEarth, searchModel);
			searchDefine(pattern, this.geographyCoordinate, searchModel);
			searchDefine(pattern, this.projectionSystem, searchModel);
			searchDefine(pattern, this.customizeCoordinate, searchModel);
			searchDefine(pattern, this.favoriteCoordinate, searchModel);
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
		CoordSysDefine[] allLeafDefines;

		// 如果选中的是最后一级子节点，那么就选择该节点的父节点进行搜索
		if (define.size() == 0) {
			allLeafDefines = define.getParent().getAllLeaves();
		} else {
			allLeafDefines = define.getAllLeaves();
		}

		for (CoordSysDefine allLeafDefine : allLeafDefines) {
			String caption = allLeafDefine.getCaption();
			if (caption.toLowerCase().contains(pattern.toLowerCase())) {
				searchModel.add(allLeafDefine);
			}
		}
	}

	public JPopupMenu getPopupmenu() {
		if (popupmenu == null) {
			this.popupmenu = new JPopupMenu();

			this.menuItemNewGroup = new JMenuItem(ControlsProperties.getString("String_NewGroup"));
			this.menuItemImportCoordSys = new JMenuItem(ControlsProperties.getString("String_Import"));
			this.menuItemExportCoordSys = new JMenuItem(ControlsProperties.getString("String_Export"));
			this.menuItemAddFavorites = new JMenuItem(CoreProperties.getString("String_Collent"));
			this.menuItemDelete = new JMenuItem(CoreProperties.getString(CoreProperties.Delete));

			this.menuNewCoordsys = new JMenu(ControlsProperties.getString("String_NewCoorSys"));
			this.menuItemNewPrjCoordSys = new JMenuItem(ControlsProperties.getString("String_PrjCoorSys"));
			this.menuItemNewGeoCoordSys = new JMenuItem(ControlsProperties.getString("String_GeoCoordSys"));
			this.menuItemNewFormEPSG = new JMenuItem(ControlsProperties.getString("String_Button_NewCoordSysFormEPSG"));
			this.menuNewCoordsys.add(this.menuItemNewGeoCoordSys);
			this.menuNewCoordsys.add(this.menuItemNewPrjCoordSys);
			this.menuNewCoordsys.add(this.menuItemNewFormEPSG);

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

			this.popupmenu.addPopupMenuListener(new PopupMenuListener() {
				@Override
				public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
					menuItemNewGeoCoordSys.setEnabled(isNewGeoCoordsysEnable());
					menuItemNewPrjCoordSys.setEnabled(isNewPrjCoordsysEnable());
					menuItemNewFormEPSG.setEnabled(isNewCoordsysFromEPSGEnable());
					menuItemNewGroup.setEnabled(isNewGroupEnable());
					menuItemImportCoordSys.setEnabled(isImportMenuItemEnable());
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
		return this.popupmenu;
	}


	/**
	 * @return
	 */
	private boolean isDeleteEnable() {
		return this.currentDefine != null && !this.currentDefine.getIsFolderNode()
				&& (currentDefine.getParent().getCoordSysType() == CoordSysDefine.CUSTOM_COORDINATE ||
				currentDefine.getParent().getCoordSysType() == CoordSysDefine.FAVORITE_COORDINATE);

	}

	private boolean isNewGroupEnable() {
		return false;
	}

	private boolean isNewGeoCoordsysEnable() {
		return true;
	}

	private boolean isNewPrjCoordsysEnable() {
		return true;
	}

	private boolean isNewCoordsysFromEPSGEnable() {
		return true;
	}

	private boolean isImportButtonEnable() {
		return true;
	}

	/**
	 * 右键菜单的导入按钮，只在自定义文件夹下才可用
	 *
	 * @return
	 */
	private boolean isImportMenuItemEnable() {
		return this.currentDefine != null && (this.currentDefine.getCoordSysType() == CoordSysDefine.CUSTOM_COORDINATE || this.currentDefine.getParent().getCoordSysType() == CoordSysDefine.CUSTOM_COORDINATE);
	}


	private boolean isExportEnable() {
		return this.currentDefine != null && this.currentDefine.getCoordSysType() != CoordSysDefine.NONE_EARTH &&
				(!this.currentDefine.getIsFolderNode() || this.currentDefine.size() > 0);
	}

	private boolean isAddFavoritesEnable() {
		return this.currentDefine != null &&
				!this.currentDefine.getIsFolderNode() &&
				this.currentDefine.getParent().getCoordSysType() != CoordSysDefine.FAVORITE_COORDINATE &&
				this.currentDefine.getParent().getCoordSysType() != CoordSysDefine.NONE_EARTH;
	}

	private boolean isButtonApplyEnable() {
		return this.currentDefine != null && !currentDefine.getIsFolderNode();
	}


	/**
	 * 导出按钮响应事件
	 */
	private void exportActive() {
		// 设置导出功能文本对话框，文件名称，根据是否为多选进行设置
		SmFileChoose prjFileExportFileChoose;
		if (tablePrjCoordSys.getSelectedRowCount() > 1 || currentDefine.getIsFolderNode()) {
			String moduleName = "ExportPrjFolder";
			if (!SmFileChoose.isModuleExist(moduleName)) {
				// 为确保导出文件名称不可修改，筛选的后缀名称为不存在-yuanR2017.11.1
				String fileFilters = SmFileChoose.createFileFilter(ControlsProperties.getString("String_PrjFile"), "NOEXIST");
				SmFileChoose.addNewNode(fileFilters, CoreProperties.getString("String_DefaultFilePath"),
						ControlsProperties.getString("String_ExportPrjFile"), moduleName, "SaveOne");
			}
			prjFileExportFileChoose = new SmFileChoose(moduleName);
			if (currentDefine.getIsFolderNode() && tablePrjCoordSys.getSelectedRowCount() <= 1) {
				prjFileExportFileChoose.setSelectedFile(new File(currentDefine.getCaption()));
			} else {
				prjFileExportFileChoose.setSelectedFile(new File(currentDefine.getParent().getCaption()));
			}
		} else {
			String moduleName = "ExportPrjFile";
			if (!SmFileChoose.isModuleExist(moduleName)) {
				// 为确保导出文件名称不可修改，筛选的后缀名称为不存在-yuanR2017.11.1
				String fileFilters = SmFileChoose.createFileFilter(ControlsProperties.getString("String_ImportPrjFileXml"), "NOEXIST");
				SmFileChoose.addNewNode(fileFilters, CoreProperties.getString("String_DefaultFilePath"),
						ControlsProperties.getString("String_ExportPrjFile"), moduleName, "SaveOne");
			}
			prjFileExportFileChoose = new SmFileChoose(moduleName);
			prjFileExportFileChoose.setSelectedFile(new File(currentDefine.getCaption()));
		}


		if (prjFileExportFileChoose.getTextField() != null) {
			prjFileExportFileChoose.getTextField().setEnabled(false);
		}

		if (prjFileExportFileChoose.showDefaultDialog() == JFileChooser.APPROVE_OPTION) {
			ArrayList<CoordSysDefine> coordSysDefineExportList = new ArrayList<>();
			int[] selectedRows = tablePrjCoordSys.getSelectedRows();
			AbstractPrjTableModel model = (AbstractPrjTableModel) tablePrjCoordSys.getModel();
			for (int selectedRow : selectedRows) {
				coordSysDefineExportList.add(model.getRowData(selectedRow));
			}
			// 当table中没有选中任何对象，此时焦点估计在tree中，并且currentDefine已设置为tree中选择的对象
			if (coordSysDefineExportList.size() <= 0 && currentDefine != null && currentDefine.getIsFolderNode()) {
				for (int i = 0; i < currentDefine.size(); i++) {
					coordSysDefineExportList.add(currentDefine.get(i));
				}
			}
			successedExportNum = 0;
			try {
				// 改变鼠标状态
				CursorUtilities.setWaitCursor(textAreaDetail);
				CursorUtilities.setWaitCursor(tablePrjCoordSys);
				CursorUtilities.setWaitCursor(treePrjCoordSys);
				if (coordSysDefineExportList.size() == 1 && !coordSysDefineExportList.get(0).getIsFolderNode()) {
					exportPrjCoordSys(coordSysDefineExportList.get(0), prjFileExportFileChoose.getFilePath().replace("\\" + prjFileExportFileChoose.getFileName(), ""));
				} else {
					buildExportRootFile(coordSysDefineExportList, prjFileExportFileChoose.getFilePath());
				}
			} finally {
				if (successedExportNum > 1) {
					Application.getActiveApplication().getOutput().output(MessageFormat.format(ControlsProperties.getString("String_ExportPrjFileSuccess"), successedExportNum, prjFileExportFileChoose.getFilePath().replace(".NOEXIST", "")));
				} else if (successedExportNum == 1) {
					Application.getActiveApplication().getOutput().output(MessageFormat.format(ControlsProperties.getString("String_ExportPrjFileSuccess"), successedExportNum, (prjFileExportFileChoose.getFilePath().replace(".NOEXIST", "")) + ".xml"));
				} else {
					Application.getActiveApplication().getOutput().output(ControlsProperties.getString("String_ExportPrjFileFailed"));
				}
				CursorUtilities.setDefaultCursor(textAreaDetail);
				CursorUtilities.setDefaultCursor(tablePrjCoordSys);
				CursorUtilities.setDefaultCursor(treePrjCoordSys);
			}

		}
	}

	/**
	 * 导入按钮响应事件
	 */
	private void importActive() {
		String moduleName = "ImportPrjFile";
		if (!SmFileChoose.isModuleExist(moduleName)) {
			String fileFilters = SmFileChoose.buildFileFilters(
					SmFileChoose.createFileFilter(ControlsProperties.getString("String_ImportPrjFiles"), "prj", "xml"),
					SmFileChoose.createFileFilter(ControlsProperties.getString("String_ImportPrjFileShape"), "prj"),
					SmFileChoose.createFileFilter(ControlsProperties.getString("String_ImportPrjFileXml"), "xml"));
			SmFileChoose.addNewNode(fileFilters, CoreProperties.getString("String_DefaultFilePath"),
					ControlsProperties.getString("String_ImportPrjFile"), moduleName, "OpenMany");
		}
		SmFileChoose prjFileImportFileChoose = new SmFileChoose(moduleName);
		if (prjFileImportFileChoose.showDefaultDialog() == JFileChooser.APPROVE_OPTION) {
			// 导入文件需要增加到自定义节点下userImportCoordsysParentName目录当中
			File file = prjFileImportFileChoose.getSelectedFile();
			//CoordSysDefine userDefine = customizeCoordinate.getChildByCaption(userImportCoordsysParentName);
			//if (userDefine == null) {
			//	userDefine = new CoordSysDefine(CoordSysDefine.CUSTOM_COORDINATE, customizeCoordinate, userImportCoordsysParentName).setFolderNode(true);
			//}
			CoordSysDefine result = addToCoordSysDefine(getPrjCoordSysFromImportFile(file.getPath()), customizeCoordinate);
			if (result != null && exportPrjCoordSys(result, customizeProjectionConfigPath)) {
				// 当增加成功，在tree中显示
				addToTree(result, customizeCoordinate.getCaption(), customizeCoordinate, customizeCoordinate.getCaption());
			}
		}
	}

	/**
	 * 将坐标系添加到收藏夹中
	 * yuanR2017.10.25
	 */
	private void addCoordsysToFavorites() {
		CoordSysDefine result = null;
		if (this.currentDefine.getCoordSysType() == CoordSysDefine.GEOGRAPHY_COORDINATE) {
			result = new CoordSysDefine(CoordSysDefine.GEOGRAPHY_COORDINATE);
			result.setGeoCoordSys(this.currentDefine.getGeoCoordSys());
		} else if (this.currentDefine.getCoordSysType() == CoordSysDefine.PROJECTION_SYSTEM) {
			result = new CoordSysDefine(CoordSysDefine.PROJECTION_SYSTEM);
			result.setPrjCoordSys(this.currentDefine.getPrjCoordSys());
		}
		result.setCoordSysCode(this.currentDefine.getCoordSysCode());
		// 对名字进行去重处理
		List<String> hasNames = new ArrayList<>();
		for (int i = 0; i < favoriteCoordinate.getAllLeaves().length; i++) {
			if (!(favoriteCoordinate.getAllLeaves().length == 1 && favoriteCoordinate.getAllLeaves()[0].equals(favoriteCoordinate))) {
				hasNames.add(favoriteCoordinate.get(i).getCaption());
			}
		}
		result.setCaption(getSingletonName(currentDefine.getCaption(), hasNames));
		if (this.favoriteCoordinate.add(result)) {
			exportPrjCoordSys(result, this.favoriteProjectionConfigPath);
			addToTree(result, this.favoriteCoordinate.getCaption(), this.favoriteCoordinate, this.favoriteCoordinate.getCaption());
		}
	}

	/**
	 * 新建地理坐标系
	 */
	private void newGeoCoordsys() {
		JDialogUserDefinePrjGeography geography = new JDialogUserDefinePrjGeography();
		if (this.currentDefine != null && !this.currentDefine.getIsFolderNode() && this.currentDefine.getCoordSysType() == CoordSysDefine.GEOGRAPHY_COORDINATE) {
			geography.setGeOCoordSys(PrjCoordSysSettingsUtilties.getGeoCoordSys(this.currentDefine));
		}
		if (geography.showDialog() == DialogResult.OK) {
			GeoCoordSys geoCoordSys = geography.getGeoCoordSys();
			CoordSysDefine result = new CoordSysDefine(CoordSysDefine.GEOGRAPHY_COORDINATE);
			result.setCoordSysCode(-1);
			result.setGeoCoordSys(geoCoordSys);
			//CoordSysDefine userDefine = customizeCoordinate.getChildByCaption(userDefineGeoParentName);
			//if (userDefine == null) {
			//	userDefine = new CoordSysDefine(CoordSysDefine.CUSTOM_COORDINATE, customizeCoordinate, userDefineGeoParentName).setFolderNode(true);
			//}
			// 对名字进行去重处理
			List<String> hasNames = new ArrayList<>();
			for (int i = 0; i < customizeCoordinate.getAllLeaves().length; i++) {
				if (!(customizeCoordinate.getAllLeaves().length == 1 && customizeCoordinate.getAllLeaves()[0].equals(customizeCoordinate))) {
					hasNames.add(customizeCoordinate.get(i).getCaption());
				}
			}
			result.setCaption(getSingletonName(geoCoordSys.getName(), hasNames));
			if (customizeCoordinate.add(result)) {
				//String grantParentName = ControlsProperties.getString("String_Customize");
				if (exportPrjCoordSys(result, this.customizeProjectionConfigPath)) {
					addToTree(result, customizeCoordinate.getCaption(), customizeCoordinate, customizeCoordinate.getCaption());
				}

			}
		}
		geography.clean();
	}


	/**
	 * 新建投影坐标系
	 */
	private void newPrjCoordsys() {
		JDialogUserDefinePrjProjection dialogUserDefinePrjProjection = new JDialogUserDefinePrjProjection();
		if (this.currentDefine != null && !this.currentDefine.getIsFolderNode() && this.currentDefine.getCoordSysType() == CoordSysDefine.PROJECTION_SYSTEM) {
			dialogUserDefinePrjProjection.setPrjCoordSys(PrjCoordSysSettingsUtilties.getPrjCoordSys(this.currentDefine));
		}
		if (dialogUserDefinePrjProjection.showDialog() == DialogResult.OK) {
			PrjCoordSys prjCoordSys = dialogUserDefinePrjProjection.getPrjCoordSys();
			CoordSysDefine result = new CoordSysDefine(CoordSysDefine.PROJECTION_SYSTEM);
			result.setCoordSysCode(-1);
			result.setPrjCoordSys(prjCoordSys);

			// 对名字进行去重处理
			List<String> hasNames = new ArrayList<>();
			for (int i = 0; i < customizeCoordinate.getAllLeaves().length; i++) {
				if (!(customizeCoordinate.getAllLeaves().length == 1 && customizeCoordinate.getAllLeaves()[0].equals(customizeCoordinate))) {
					hasNames.add(customizeCoordinate.get(i).getCaption());
				}
			}
			result.setCaption(getSingletonName(prjCoordSys.getName(), hasNames));
			if (customizeCoordinate.add(result)) {
				//String grantParentName = ControlsProperties.getString("String_Customize");
				if (exportPrjCoordSys(result, this.customizeProjectionConfigPath)) {
					addToTree(result, customizeCoordinate.getCaption(), customizeCoordinate, customizeCoordinate.getCaption());
				}

			}
		}
		dialogUserDefinePrjProjection.clean();
	}

	/**
	 * 通过EPSG新建坐标系
	 */
	private void newCoordsysFromEPSG() {

		JDialogNewCoordsysFromEPSG dialogNewCoordsysFromEPSG = new JDialogNewCoordsysFromEPSG();
		if (this.currentDefine != null && !this.currentDefine.getIsFolderNode() && (this.currentDefine.getCoordSysType() == CoordSysDefine.GEOGRAPHY_COORDINATE || this.currentDefine.getCoordSysType() == CoordSysDefine.PROJECTION_SYSTEM)) {

			PrjCoordSys prjCoordSys = null;
			if (this.currentDefine.getCoordSysType() == CoordSysDefine.GEOGRAPHY_COORDINATE) {
				GeoCoordSys geoCoordSys = PrjCoordSysSettingsUtilties.getGeoCoordSys(this.currentDefine);
				prjCoordSys = new PrjCoordSys(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
				prjCoordSys.setGeoCoordSys(geoCoordSys);
			} else if (this.currentDefine.getCoordSysType() == CoordSysDefine.PROJECTION_SYSTEM) {
				prjCoordSys = PrjCoordSysSettingsUtilties.getPrjCoordSys(this.currentDefine);
			}

			if (prjCoordSys != null) {
				int code;
				code = prjCoordSys.getEPSGCode();
				if (code <= 0) {
					code = prjCoordSys.toEPSGCode();
				}
				if (code <= 0) {
					code = 3857;
				}
				dialogNewCoordsysFromEPSG.setCode(code);
				dialogNewCoordsysFromEPSG.getCodeTextField().setText(String.valueOf(code));
			}
		}

		if (dialogNewCoordsysFromEPSG.showDialog() == DialogResult.OK) {
			try {
				PrjCoordSys prjCoordSys = new PrjCoordSys();
				prjCoordSys.fromEPSGCode(dialogNewCoordsysFromEPSG.getCode());
				CoordSysDefine result;
				if (prjCoordSys.getType().equals(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE)) {
					result = new CoordSysDefine(CoordSysDefine.GEOGRAPHY_COORDINATE);
					result.setGeoCoordSys(prjCoordSys.getGeoCoordSys());
				} else {
					result = new CoordSysDefine(CoordSysDefine.PROJECTION_SYSTEM);
					result.setPrjCoordSys(prjCoordSys);
				}
				result.setCoordSysCode(-1);
				// 对名字进行去重处理
				List<String> hasNames = new ArrayList<>();
				for (int i = 0; i < customizeCoordinate.getAllLeaves().length; i++) {
					if (!(customizeCoordinate.getAllLeaves().length == 1 && customizeCoordinate.getAllLeaves()[0].equals(customizeCoordinate))) {
						hasNames.add(customizeCoordinate.get(i).getCaption());
					}
				}
				result.setCaption(getSingletonName(dialogNewCoordsysFromEPSG.getNameTextField().getText(), hasNames));
				if (customizeCoordinate.add(result)) {
					if (exportPrjCoordSys(result, this.customizeProjectionConfigPath)) {
						addToTree(result, this.customizeCoordinate.getCaption(), customizeCoordinate, this.customizeCoordinate.getCaption());
					}
				}
			} catch (Exception ignored) {
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

	/**
	 * 从文件夹中删除坐标系
	 */
	private void deleteCoordsysFormFolder() {
		if (isDeleteEnable()) {
			if (UICommonToolkit.showConfirmDialog(ControlsProperties.getString("String_DelSelectedItem_Warning")) == 0) {
				int[] selectedRows = tablePrjCoordSys.getSelectedRows();
				for (int i = selectedRows.length - 1; i >= 0; i--) {
					CoordSysDefine rowData = prjModel.getRowData(selectedRows[i]);
					if (rowData == null && tablePrjCoordSys.getModel() instanceof SearchResultModel) {
						rowData = ((SearchResultModel) tablePrjCoordSys.getModel()).getRowData(selectedRows[i]);
					}
					if (rowData != null) {
						String deletePath = "";
						if (rowData.getParent().getCoordSysType() == CoordSysDefine.FAVORITE_COORDINATE) {
							deletePath = this.favoriteProjectionConfigPath + rowData.getCaption() + ".xml";
						} else if (rowData.getParent().getCoordSysType() == CoordSysDefine.CUSTOM_COORDINATE) {
							deletePath = this.customizeProjectionConfigPath + rowData.getCaption() + ".xml";
							//if (rowData.getParent().getCaption().equals(userDefineGeoParentName)) {
							//	deletePath = this.userGeoCoordsysFolderPath + rowData.getCaption() + ".xml";
							//} else if (rowData.getParent().getCaption().equals(userDefinePrjParentName)) {
							//	deletePath = this.userPrjCoordsysFolderPath + rowData.getCaption() + ".xml";
							//} else if (rowData.getParent().getCaption().equals(userCoordsysFromEPSGParentName)) {
							//	deletePath = this.userCoordsysFromEPSGFolderPath + rowData.getCaption() + ".xml";
							//} else if (rowData.getParent().getCaption().equals(userImportCoordsysParentName)) {
							//	deletePath = this.userImportCoordsysFolderPath + rowData.getCaption() + ".xml";
							//}
						}
						removeCoordSysDefineFormFolder(rowData, deletePath);
						rowData.getParent().remove(rowData);
					}
				}
			}
		}
	}

	/**
	 * 将投影文件增加到CoordSysDefine节点中
	 *
	 * @param prjCoordSys
	 * @param targetDefine
	 * @return
	 */
	private CoordSysDefine addToCoordSysDefine(PrjCoordSys prjCoordSys, CoordSysDefine targetDefine) {
		if (prjCoordSys != null && !prjCoordSys.getType().equals(PrjCoordSysType.PCS_NON_EARTH)) {
			CoordSysDefine result;
			if (prjCoordSys.getType().equals(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE)) {
				result = new CoordSysDefine(CoordSysDefine.GEOGRAPHY_COORDINATE);
				result.setGeoCoordSys(prjCoordSys.getGeoCoordSys());
			} else {
				result = new CoordSysDefine(CoordSysDefine.PROJECTION_SYSTEM);
				result.setPrjCoordSys(prjCoordSys);
			}
			result.setCoordSysCode(prjCoordSys.getEPSGCode());
			// 对名字进行去重处理
			List<String> hasNames = new ArrayList<>();
			for (int i = 0; i < targetDefine.getAllLeaves().length; i++) {
				if (!(targetDefine.getAllLeaves().length == 1 && targetDefine.getAllLeaves()[0].equals(targetDefine))) {
					hasNames.add(targetDefine.get(i).getCaption());
				}
			}
			result.setCaption(getSingletonName(prjCoordSys.getName(), hasNames));
			targetDefine.add(result);
			return result;
		}
		return null;
	}


	/**
	 * 当批量导出时创建根级目录
	 *
	 * @param coordSysDefineExportList
	 * @param path
	 */
	private void buildExportRootFile(ArrayList<CoordSysDefine> coordSysDefineExportList, String path) {
		String parentFolderName = path.replace(".NOEXIST", "");
		if (!FileUtilities.exists(parentFolderName)) {
			File file = new File(parentFolderName);
			file.mkdir();
		}
		for (CoordSysDefine aCoordSysDefineExportList : coordSysDefineExportList) {
			exportPrjCoordSys(aCoordSysDefineExportList, parentFolderName);
		}
	}

	/**
	 * 导出投影文件到指定目录
	 *
	 * @param coordSysDefine
	 * @param path
	 * @return
	 */
	private Boolean exportPrjCoordSys(CoordSysDefine coordSysDefine, String path) {
		// 开始进行投影导出
		if (coordSysDefine.getIsFolderNode()) {
			CoordSysDefine[] allCoordSysDefine = coordSysDefine.getAllLeaves().clone();
			String folderName = path + "\\" + coordSysDefine.getCaption();
			if (!FileUtilities.exists(folderName)) {
				File file = new File(folderName);
				file.mkdir();
			}
			for (CoordSysDefine anAllCoordSysDefine : allCoordSysDefine) {
				if (!anAllCoordSysDefine.getIsFolderNode()) {
					PrjCoordSys exportPrjCoordSys = new PrjCoordSys();
					if (anAllCoordSysDefine.getCoordSysType() == CoordSysDefine.GEOGRAPHY_COORDINATE) {
						GeoCoordSys exportGeoCoordSys = PrjCoordSysSettingsUtilties.getGeoCoordSys(anAllCoordSysDefine).clone();
						exportPrjCoordSys.setGeoCoordSys(exportGeoCoordSys);
						exportPrjCoordSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
						exportPrjCoordSys.setName(exportGeoCoordSys.getName());
						exportPrjCoordSys.setEPSGCode(anAllCoordSysDefine.getCoordSysCode());
					} else if (anAllCoordSysDefine.getCoordSysType() == CoordSysDefine.PROJECTION_SYSTEM) {
						try {
							exportPrjCoordSys = PrjCoordSysSettingsUtilties.getPrjCoordSys(anAllCoordSysDefine).clone();
							exportPrjCoordSys.setEPSGCode(anAllCoordSysDefine.getCoordSysCode());
						} catch (Exception ex) {
							continue;
						}
					}
					if (export(exportPrjCoordSys, folderName + "\\" + exportPrjCoordSys.getName() + ".xml")) {
						this.successedExportNum++;
						return true;
					} else {
						return false;
					}
				}
			}
		} else {
			// 选中非文件夹节点，直接导出即可
			PrjCoordSys exportPrjCoordSys = new PrjCoordSys();
			if (coordSysDefine.getCoordSysType() == CoordSysDefine.GEOGRAPHY_COORDINATE) {
				GeoCoordSys exportGeoCoordSys = PrjCoordSysSettingsUtilties.getGeoCoordSys(coordSysDefine).clone();
				exportPrjCoordSys.setGeoCoordSys(exportGeoCoordSys);
				exportPrjCoordSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
				exportPrjCoordSys.setName(coordSysDefine.getCaption());
				exportPrjCoordSys.setEPSGCode(coordSysDefine.getCoordSysCode());
			} else if (coordSysDefine.getCoordSysType() == CoordSysDefine.PROJECTION_SYSTEM) {
				exportPrjCoordSys = PrjCoordSysSettingsUtilties.getPrjCoordSys(coordSysDefine).clone();
				exportPrjCoordSys.setName(coordSysDefine.getCaption());
				exportPrjCoordSys.setEPSGCode(coordSysDefine.getCoordSysCode());
			}
			if (export(exportPrjCoordSys, path + "//" + exportPrjCoordSys.getName() + ".xml")) {
				this.successedExportNum++;
				return true;
			} else {
				return false;
			}
		}
		return false;
	}


	/**
	 * 导出通用方法
	 */
	public Boolean export(PrjCoordSys prjCoordSys, String path) {
		Boolean isSuccess = false;
		try {
			isSuccess = prjCoordSys.toFile(path, PrjFileVersion.UGC60);
		} catch (Exception ignored) {
			isSuccess = false;
		} finally {
			return isSuccess;
		}
	}


	/**
	 * 从根据所给的文件目录，删除文件
	 *
	 * @param coordSysDefine
	 */
	private void removeCoordSysDefineFormFolder(CoordSysDefine coordSysDefine, String deletePath) {

		File file = new File(deletePath);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				removeFormTree(coordSysDefine);
				CoordSysDefine define = prjModel.getDefine();
				if (define != coordSysDefine) {
					if (define.size() > 1) {
						prjModel.setDefine(define);
					} else {
						//removeFormTree(define);
						prjModel.setDefine(null);
					}
				} else {
					prjModel.setDefine(null);
				}
			}
		}
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
					// 当父节点和爷爷节点相同时，即表示没有爷爷节点
					if (parentName.equals(grantParentName)) {
						parentNode = grandParentNode;
					} else {
						for (int j = 0; j < treeNode.getChildCount(); j++) {
							DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeNode.getChildAt(j);
							if (node != null && ((CoordSysDefine) node.getUserObject()).getCaption().equals(parentName)) {
								parentNode = node;
								break;
							}
						}
					}
				}
			}
			if (parentNode == null && !parentName.equals(grantParentName)) {
				parentNode = createNode(parentValue);
				((DefaultTreeModel) treePrjCoordSys.getModel()).insertNodeInto(parentNode, grandParentNode, grandParentNode.getChildCount());
			}
			this.treePrjCoordSys.setSelectionPath(null);
			JTreeUIUtilities.locateNode(treePrjCoordSys, parentNode);
			this.treePrjCoordSys.scrollRectToVisible(new Rectangle(0, treePrjCoordSys.getVisibleRect().y, treePrjCoordSys.getVisibleRect().width, treePrjCoordSys.getVisibleRect().height));
			//tree已定位到选中数据的父节点上，再定位table
			// 防止此时的model是搜索的model
			treeSelectionChange();
			AbstractPrjTableModel model = (AbstractPrjTableModel) this.tablePrjCoordSys.getModel();
			for (int i = 0; i < model.getRowCount(); i++) {
				if (value.equals(model.getRowData(i))) {
					tablePrjCoordSys.setRowSelectionInterval(i, i);
					tablePrjCoordSys.scrollRectToVisible(tablePrjCoordSys.getCellRect(i, 0, true));
				}
			}
		}
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

			CoordSysDefine item;
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
	 * 通过文件地址获得其投影文件
	 *
	 * @param path
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

	private void selectRootNode() {
		if (this.treePrjCoordSys.getRowCount() > 0) {
			treePrjCoordSys.setSelectionRow(0);
		}
	}


	/**
	 * 移除平面坐标系文件节点
	 */
	public void removeNONEARTHRoot() {
		removeFormTree(this.noneEarth);
		selectRootNode();
	}
}
