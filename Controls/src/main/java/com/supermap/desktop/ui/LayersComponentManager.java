package com.supermap.desktop.ui;

import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IContextMenuManager;
import com.supermap.desktop.Interface.IFormMap;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.ControlsResources;
import com.supermap.desktop.controls.utilities.MapViewUIUtilities;
import com.supermap.desktop.ui.controls.ComponentDropDown;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.ui.controls.datasetChoose.DatasetChooser;
import com.supermap.desktop.ui.trees.Layer3DsTree;
import com.supermap.desktop.ui.trees.LayersTree;
import com.supermap.desktop.ui.trees.NodeDataType;
import com.supermap.desktop.ui.trees.TreeNodeData;
import com.supermap.desktop.utilities.CoreResources;
import com.supermap.mapping.*;
import com.supermap.realspace.Scene;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;

public class LayersComponentManager extends JComponent {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private javax.swing.JScrollPane jScrollPane = null;
	private transient LayersTree layersTree = null;
	private transient Layer3DsTree layer3DsTree = null;
	private JToolBar toolBar;
	private final String urlStr = "/coreresources/ToolBar/";
	private ComponentDropDown addLayerGroup;
    private ComponentDropDown addData;
    private JMenuItem jMenuItemAddLayerRootGroup;
	private JMenuItem jMenuItemAddLayerGroup;
	private JMenuItem jMenuItemAddLayerSnapshot;
    private JMenuItem jMenuItemAddData;
    private JMenuItem jMenuItemMongoDB;
    // 临时的变量，现在还没有自动加载Dockbar，所以暂时用这个变量测试
	private Boolean isContextMenuBuilded = false;
	private JPopupMenu layerWMSPopupMenu;
	private ArrayList<TreePath> legalPaths;
	private String currentMapName=""; //  用来判断进行了切换地图窗口的操作
	private ArrayList<TreeNode> allTreeNode=new ArrayList<>();

	/**
	 * Create the panel.
	 */
	public LayersComponentManager() {

		initializeComponent();
		initializeResources();
		initialize();
	}

	private void initializeComponent() {

		this.jScrollPane = new javax.swing.JScrollPane();
		this.layersTree = new LayersTree();
		this.layer3DsTree = new Layer3DsTree();
		this.toolBar = new JToolBar();


		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(this.toolBar, 100, 100, 100)
                .addComponent(this.jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.toolBar,23, 23, 23)
				.addComponent(this.jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE));

		this.layersTree.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				layersTreeSelectDataChange(evt);
				layersTreeMousePressed(evt);
			}
		});
		this.layersTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				layersTreeSelectedLegalPath(e);
			}
		});

		this.layer3DsTree.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				layer3DsTreeMousePressed(evt);
			}
		});
	}

	private void layersTreeSelectedLegalPath(TreeSelectionEvent e) {
		TreePath[] selectionPaths = layersTree.getSelectionPaths();
		if (selectionPaths != null) {
			if (this.currentMapName.equals("") ||this.currentMapName.equals(layersTree.getMap().getName())) {
				if (selectionPaths.length > 1 &&legalPaths.size()>0) {
					layersTree.setSelectionPaths(getLegalPaths());
				} else {
					legalPaths = new ArrayList<>();
					legalPaths.add(selectionPaths[0]);
				}
				if (this.currentMapName.equals("")){
					this.currentMapName=layersTree.getMap().getName();
				}
			}else {
				legalPaths = new ArrayList<>();
				this.currentMapName=layersTree.getMap().getName();
			}
		}
	}

	private TreePath[] getLegalPaths() {
		TreePath[] selectionPaths = layersTree.getSelectionPaths();
		TreePath parentPath = legalPaths.get(0).getParentPath();
		for (TreePath selectionPath : selectionPaths) {
			if (selectionPath.getParentPath().equals(parentPath)) {
				legalPaths.add(selectionPath);
			}
		}
		TreePath[] result = new TreePath[legalPaths.size()];

		return legalPaths.toArray(result);
	}

	private void initialize() {
		try {
			initializeToolBar();
			buildContextMenu();

		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	private void initializeToolBar() {
		this.jMenuItemAddLayerRootGroup = new JMenuItem(ControlsProperties.getString("String_Button_NewRootGroup"), ControlsResources.getIcon("/controlsresources/ToolBar/Image_NewRootGroup.png"));
		this.jMenuItemAddLayerGroup = new JMenuItem(ControlsProperties.getString("String_Button_NewGroup"), ControlsResources.getIcon("/controlsresources/ToolBar/Image_NewGroup.png"));
		this.jMenuItemAddLayerSnapshot=new JMenuItem(ControlsProperties.getString("String_CreateLayerSnapshot"),ControlsResources.getIcon("/controlsresources/controlsImage/Image_Layer_LayerSnapshot.png"));
        this.jMenuItemAddData = new JMenuItem(ControlsProperties.getString("String_AddData"), ControlsResources.getIcon("controlsresources/ToolBar/Image_ToolButton_AddItem_16.png"));
        this.jMenuItemMongoDB = new JMenuItem(ControlsProperties.getString("String_LoadMongoDB"), null);
        this.addLayerGroup = new ComponentDropDown(ComponentDropDown.IMAGE_TYPE);
        this.addData = new ComponentDropDown(ComponentDropDown.IMAGE_TYPE);
        JPopupMenu popupMenuLayerGroup = new JPopupMenu();
		popupMenuLayerGroup.add(this.jMenuItemAddLayerRootGroup);
		popupMenuLayerGroup.add(this.jMenuItemAddLayerGroup);
		popupMenuLayerGroup.add(this.jMenuItemAddLayerSnapshot);
        JPopupMenu popupMenuAddData = new JPopupMenu();
        popupMenuAddData.add(this.jMenuItemAddData);
        popupMenuAddData.add(this.jMenuItemMongoDB);
        this.addLayerGroup.setPopupMenu(popupMenuLayerGroup);
		this.addLayerGroup.setToolTip(ControlsProperties.getString("String_Button_NewRootGroup"));
		this.addLayerGroup.setIcon(ControlsResources.getIcon("/controlsresources/ToolBar/Image_NewRootGroup.png"));
        this.addData.setPopupMenu(popupMenuAddData);
        this.addData.setToolTip(ControlsProperties.getString("String_AddData"));
        this.addData.setIcon(CoreResources.getIcon("coreresources/ThemeToolBar/Image_ToolButton_AddItem_16.png"));
        this.toolBar.add(this.addLayerGroup);
        this.toolBar.add(this.addData);
        this.toolBar.setFloatable(false);
		this.toolBar.setVisible(false);
		registerEvents();
	}

	private void registerEvents(){
		this.addLayerGroup.getDisplayButton().removeActionListener(this.addLayerRootGroupListener);
		this.addLayerGroup.getArrowButton().removeActionListener(this.arrawButtonListener);
		this.jMenuItemAddLayerRootGroup.removeActionListener(this.addLayerRootGroupListener);
		this.jMenuItemAddLayerGroup.removeActionListener(this.addLayerGroupListener);
		this.jMenuItemAddLayerSnapshot.removeActionListener(this.addLayerSnapshotListener);
		this.addLayerGroup.getDisplayButton().addActionListener(this.addLayerRootGroupListener);
		this.addLayerGroup.getArrowButton().addActionListener(this.arrawButtonListener);
		this.jMenuItemAddLayerRootGroup.addActionListener(this.addLayerRootGroupListener);
		this.jMenuItemAddLayerGroup.addActionListener(this.addLayerGroupListener);
		this.jMenuItemAddLayerSnapshot.addActionListener(this.addLayerSnapshotListener);

        this.addData.getDisplayButton().removeActionListener(addDataListener);
        this.jMenuItemAddData.removeActionListener(addDataListener);
        this.jMenuItemMongoDB.removeActionListener(loadMongoDBListener);
        this.addData.getDisplayButton().addActionListener(addDataListener);
        this.jMenuItemAddData.addActionListener(addDataListener);
        this.jMenuItemMongoDB.addActionListener(loadMongoDBListener);

    }

    //region Listener
    private ActionListener addLayerRootGroupListener=new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (layersTree!=null && layersTree.getMap()!= null) {
                String layerGroupName = layersTree.getMap().getLayers().getAvailableCaption("LayerGroup");
                layersTree.getMap().getLayers().addGroup(layerGroupName);
                int selectRow = layersTree.getRowCount() - 1;
                if (selectRow<0){
                    selectRow=0;
                    layersTree.reload();
                }
                layersTree.setSelectionRow(selectRow);
                layersTree.startEditingAtPath(layersTree.getPathForRow(selectRow));
            }
        }
    };

	private ActionListener addLayerGroupListener=new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			TreeNodeData selectedNodeData = null;
			if (layersTree != null && layersTree.getMap()!= null && layersTree.getSelectionCount() == 1) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) layersTree.getLastSelectedPathComponent();
				selectedNodeData = (TreeNodeData) selectedNode.getUserObject();
				if (selectedNodeData.getType() == NodeDataType.LAYER_GROUP || selectedNodeData.getType()==NodeDataType.LAYER_SNAPSHOT) {
					IFormMap formMap=(IFormMap)Application.getActiveApplication().getActiveForm();
					LayersTree layersTree = UICommonToolkit.getLayersManager().getLayersTree();
					String layerGroupName = formMap.getMapControl().getMap().getLayers().getAvailableCaption("LayerGroup");
					LayerGroup layerGroup = (LayerGroup) selectedNodeData.getData();
					LayerGroup[] oldExpandLayerGroup =layersTree.getExpandLayerGroup(formMap);
					LayerGroup[] newExpandLayerGroup=new LayerGroup[1];
					boolean isNeedAddExpandLayerGroup =true;
					if (oldExpandLayerGroup.length!=0){
						for (LayerGroup layerGroup1: oldExpandLayerGroup){
							if (layerGroup1.equals(layerGroup)){
								isNeedAddExpandLayerGroup =false;
								break;
							}
						}
					}
					if (isNeedAddExpandLayerGroup){
						if (oldExpandLayerGroup.length!=0){
							newExpandLayerGroup=new LayerGroup[oldExpandLayerGroup.length+1];
							for (int i = 0; i <oldExpandLayerGroup.length ; i++) {
								newExpandLayerGroup[i]=oldExpandLayerGroup[i];
							}
							newExpandLayerGroup[oldExpandLayerGroup.length]=layerGroup;
						}else {
							newExpandLayerGroup[0] = layerGroup;
						}
					}
					layerGroup.insertGroup(layerGroup.getCount(),layerGroupName);
					if (!isNeedAddExpandLayerGroup) {
						layersTree.reload();
					}else{
						layersTree.reload(newExpandLayerGroup);
					}
					allTreeNode.clear();
					initAllNodes((TreeNode) layersTree.getModel().getRoot());
					TreePath newLayerGroupTreePath=null;
					for (int j=0;j<allTreeNode.size();j++){
						DefaultMutableTreeNode node=(DefaultMutableTreeNode)allTreeNode.get(j);
						TreeNodeData nodeData = (TreeNodeData) node.getUserObject();

						if (nodeData.getData() == layerGroup.get(layerGroup.getCount()-1)) {
							newLayerGroupTreePath=new TreePath(node.getPath());
							break;
						}
					}
					formMap.setActiveLayers(layerGroup.get(layerGroup.getCount()-1));
					layersTree.startEditingAtPath(newLayerGroupTreePath);
				}
			}
		}
	};

    private ActionListener addLayerSnapshotListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (layersTree != null && layersTree.getMap() != null) {
                String layerGroupName = layersTree.getMap().getLayers().getAvailableCaption("SnapshotLayer");
                layersTree.getMap().getLayers().insertLayerSnapshot(layersTree.getMap().getLayers().getCount(), layerGroupName);
                int selectRow = layersTree.getRowCount() - 1;
                if (selectRow<0){
                    selectRow=0;
                    layersTree.reload();
                }
                layersTree.clearSelection();
                layersTree.setSelectionRow(selectRow);
                layersTree.startEditingAtPath(layersTree.getPathForRow(selectRow));
            }
        }
    };

    private ActionListener arrawButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            TreeNodeData selectedNodeData = null;
            if (layersTree != null && layersTree.getMap() != null && layersTree.getSelectionCount() == 1) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) layersTree.getLastSelectedPathComponent();
                selectedNodeData = (TreeNodeData) selectedNode.getUserObject();
                if (selectedNodeData.getType() == NodeDataType.LAYER_GROUP || selectedNodeData.getType() == NodeDataType.LAYER_SNAPSHOT) {
                    jMenuItemAddLayerGroup.setEnabled(true);
                } else {
                    jMenuItemAddLayerGroup.setEnabled(false);
                }
            } else {
                jMenuItemAddLayerGroup.setEnabled(false);
            }
//			if (layersTree != null && layersTree.getMap()!= null && layersTree.getMap().getLayers().getCount() > 0){
//				jMenuItemAddLayerRootGroup.setEnabled(true);
//				jMenuItemAddLayerSnapshot.setEnabled(true);
//			}else{
//				jMenuItemAddLayerRootGroup.setEnabled(false);
//				jMenuItemAddLayerSnapshot.setEnabled(false);
//			}
        }
    };

    private ActionListener addDataListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                IFormMap formMap = (IFormMap) Application.getActiveApplication().getActiveForm();
                if (formMap != null) {
                    DatasetType[] datasetTypes = new DatasetType[]{
                            DatasetType.POINT, DatasetType.LINE, DatasetType.REGION, DatasetType.TEXT, DatasetType.CAD, DatasetType.NETWORK,
                            DatasetType.LINEM, DatasetType.GRID, DatasetType.IMAGE, DatasetType.POINT3D, DatasetType.LINE3D, DatasetType.REGION3D,
                            DatasetType.GRIDCOLLECTION, DatasetType.IMAGECOLLECTION, DatasetType.PARAMETRICLINE, DatasetType.PARAMETRICREGION,
                            DatasetType.NETWORK3D
                    };
                    DatasetChooser datasetChooser = new DatasetChooser();
                    datasetChooser.setSupportDatasetTypes(datasetTypes);
                    if (datasetChooser.showDialog() == DialogResult.OK) {
                        java.util.List<Dataset> datasetsToMap = datasetChooser.getSelectedDatasets();
                        Layer[] addedLayers = MapViewUIUtilities.addDatasetsToMap(formMap.getMapControl().getMap(), datasetsToMap.toArray(new Dataset[datasetsToMap.size()]), true);

                        if (addedLayers != null && addedLayers.length > 0) {
                            formMap.setActiveLayers(addedLayers);
                        }
                    }
                }
            } catch (Exception ex) {
                Application.getActiveApplication().getOutput().output(ex);
            }
        }
    };

    private ActionListener loadMongoDBListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };
    //endregion

    private void initializeResources() {
        // 默认实现，后续进行初始化操作
    }

    //  Create by lixiaoyao 2017/10/10
    // 当点击鼠标右键时，当坐标超出图层树当前显示的高度时那么不需要改变图层树当前选择的对象，
    // 如果没超过那么就需要改变图层树中当前选择的对象，效果类似于工作空间树
    private void layersTreeSelectDataChange(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 1) {
            TreePath closestPathForLocation = this.layersTree.getClosestPathForLocation(e.getX(), e.getY());
            if (closestPathForLocation != null) {
                Rectangle pathBounds = layersTree.getPathBounds(closestPathForLocation);
                if (pathBounds != null && e.getY() >= pathBounds.y && e.getY() < (pathBounds.y + pathBounds.height)
                        && closestPathForLocation.getPath().length > 0 && !this.layersTree.isPathSelected(closestPathForLocation)) {
                    this.layersTree.setSelectionPath(closestPathForLocation);
                }
            }
        }
//		else if (e.getButton() == MouseEvent.BUTTON1) {
//			if (this.layersTree.getPathForLocation(e.getX(), e.getY()) == null) {
//				layersTree.clearSelection();
//			}
//		}
    }

    private void layersTreeMousePressed(java.awt.event.MouseEvent evt) {
        try {
            int buttonType = evt.getButton();
            int clickCount = evt.getClickCount();
//			if (selectedNode != null) {
            if (buttonType == MouseEvent.BUTTON3 && clickCount == 1) {

                if (!this.isContextMenuBuilded) {
                    this.buildContextMenu();
                }
                JPopupMenu popupMenu = null;
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) this.layersTree.getLastSelectedPathComponent();
                if (selectedNode != null && evt.getY() <= this.layersTree.getRowCount() * this.layersTree.getRowHeight()) {
                    TreeNodeData selectedNodeData = (TreeNodeData) selectedNode.getUserObject();
                    popupMenu = this.getPopupMenu(selectedNodeData);
                } else if (evt.getY() > this.layersTree.getRowCount() * this.layersTree.getRowHeight()) {// fix by lixiaoyao 2017/10/11 当点击的鼠标Y大于树当前显示的高度时，显示图层树的右键菜单
                    popupMenu = this.layersTreeToolBarPopupMenu;
                }
                if (popupMenu != null) {
                    popupMenu.show(this.layersTree, evt.getX(), evt.getY());
                }
            }
//			}
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        }
    }

    private void layer3DsTreeMousePressed(java.awt.event.MouseEvent evt) {
        try {
            int buttonType = evt.getButton();
            int clickCount = evt.getClickCount();
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) this.layer3DsTree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                TreeNodeData selectedNodeData = (TreeNodeData) selectedNode.getUserObject();
                if (buttonType == MouseEvent.BUTTON3 && clickCount == 1 && this.layer3DsTree.getLastSelectedPathComponent() != null) {

                    if (!this.isContextMenuBuilded) {
                        this.buildContextMenu();
                    }

                    JPopupMenu popupMenu = this.getPopupMenu(selectedNodeData);
                    if (popupMenu != null) {
                        popupMenu.show(this.layer3DsTree, evt.getX(), evt.getY());
                    }
                }
            }
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        }
    }

    private JPopupMenu getPopupMenu(TreeNodeData nodeData) {
        JPopupMenu popupMenu = null;
        try {
            NodeDataType type = nodeData.getType();

            if (type == NodeDataType.LAYER) {
                popupMenu = this.layerPopupMenu;
            } else if (type == NodeDataType.FEATURE3D) {
                popupMenu = this.feature3DPopupMenu;
            } else if (type == NodeDataType.FEATURE3DS) {
                popupMenu = this.feature3DsPopupMenu;
            } else if (type == NodeDataType.LAYER3DS) {
                popupMenu = this.generalLayersPopupMenu;
            } else if (type == NodeDataType.LAYER3D_DATASET) {
                popupMenu = this.layer3DDatasetPopupMenu;
            } else if (type == NodeDataType.LAYER3D_KML) {
                popupMenu = this.layer3DKMLPopupMenu;
            } else if (type == NodeDataType.LAYER_IMAGE || type == NodeDataType.LAYER_GRID) {
                popupMenu = this.layer3DImagePopupMenu;
            } else if (type == NodeDataType.SCREENLAYER3D_GEOMETRY_TAG) {
                popupMenu = this.screenLayer3DPopupMenu;
            } else if (type == NodeDataType.TERRAIN_LAYER) {
                popupMenu = this.terrainLayerPopupMenu;
            } else if (type == NodeDataType.TERRAIN_LAYERS) {
                popupMenu = this.terrainLayersPopupMenu;
            } else if (type == NodeDataType.LAYER3D_MAP) {
                popupMenu = this.layer3DMapPopupMenu;
            }

            if (nodeData.getData() != null && nodeData.getData() instanceof Layer) {
                Layer layer = (Layer) nodeData.getData();

                if (layer.getTheme() != null) {
                    if ((layer.getTheme() instanceof ThemeUnique) || (layer.getTheme() instanceof ThemeRange) || (layer.getTheme() instanceof ThemeCustom)) {
                        popupMenu = this.layerVectorThemeUniqueAndRangePopupMenu;
                    } else if ((layer.getTheme() instanceof ThemeGridRange) || (layer.getTheme() instanceof ThemeGridUnique)) {
                        popupMenu = this.layerGridThemePopupMenu;
                    } else {
                        popupMenu = this.layerVectorThemeOtherPopupMenu;
                    }
                } else if (layer.getDataset() != null) {
                    if (layer instanceof LayerHeatmap || layer instanceof LayerGridAggregation) {
                        popupMenu = this.layerHeatmapAndAggregationPopupMenu;
                    } else if (layer.getDataset().getType() == DatasetType.CAD) {
                        popupMenu = this.layerVectorCADPopupMenu;
                    } else if (layer.getDataset().getType() == DatasetType.TEXT) {
                        popupMenu = this.layerTextPopupMenu;
                    } else if (layer.getDataset() instanceof DatasetVector) {
                        popupMenu = this.layerVectorPopupMenu;
                    } else if (layer.getDataset().getType() == DatasetType.IMAGE || layer.getDataset().getType() == DatasetType.IMAGECOLLECTION) {
                        popupMenu = this.layerImagePopupMenu;
                    } else if (layer.getDataset().getType() == DatasetType.GRID || layer.getDataset().getType() == DatasetType.GRIDCOLLECTION) {
                        popupMenu = this.layerGridPopupMenu;
                    } else if (layer.getDataset().getType() == DatasetType.WMS) {
                        popupMenu = layerWMSPopupMenu;
                    } else {
                        popupMenu = this.layerPopupMenu;
                    }
                } else if (layer instanceof LayerGroup) {
                    popupMenu = this.layerGroupPopupMenu;
                } else if (layer instanceof LayerCache) {
                    popupMenu = this.layerCachePopupMenu;
                } else {
                    popupMenu = this.layerPopupMenu;
                }

				if (popupMenu != null) {
					// 默认实现，后续进行初始化操作
				}
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}

		return popupMenu;
	}

	public LayersTree getLayersTree() {
		if (this.layersTree == null) {
			this.layersTree = new LayersTree();
		}
		return this.layersTree;
	}

	public Layer3DsTree getLayer3DsTree() {
		if (this.layer3DsTree == null) {
			this.layer3DsTree = new Layer3DsTree();
		}
		return this.layer3DsTree;
	}

	public Map getMap() {
		return this.layersTree.getMap();
	}

    public void setMap(Map map) {
        this.jScrollPane.setViewportView(this.layersTree);

        // 这里先这么绕一下，保证每次设置 map 都会生效，绕过相同地图不处理的问题。
        if (map != null) {
            this.layersTree.setMap(null);
        }
        this.layersTree.setMap(map);
        if (map != null && map.getLayers() != null && map.getLayers().getCount() > 0) {
            layersTree.setSelectionRow(0);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                layersTree.updateUI();
            }
        });

	}

	/**
	 * When you close all the formmap, hide the layer tree toolbar automatically
	 * @param isVisible
	 */
	public void setToolBarVisible(boolean isVisible){
		this.toolBar.setVisible(isVisible);
	}

	public Scene getScene() {
		return this.layer3DsTree.getScene();
	}

	public void setScene(Scene scene) {
		if (!this.toolBar.isVisible()){
			this.toolBar.setVisible(true);
		}
		this.jScrollPane.setViewportView(this.layer3DsTree);

		// 这里先这么绕一下，保证每次设置 map 都会生效，绕过相同地图不处理的问题。
		if (scene != null) {
			this.layer3DsTree.setScene(null);
		}
		this.layer3DsTree.setScene(scene);
		this.layer3DsTree.updateUI();
	}

	private JPopupMenu layerVectorPopupMenu = null;

	/**
	 * 获取二维图层管理器中矢量图层的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getLayerVectorPopupMenu() {
		return this.layerVectorPopupMenu;
	}

	private JPopupMenu layerVectorCADPopupMenu = null;

	/**
	 * 获取二维图层管理器中CAD图层的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getLayerVectorCADPopupMenu() {
		return this.layerVectorCADPopupMenu;
	}

	private JPopupMenu layerTextPopupMenu = null;

	/**
	 * 获取二维图层管理器中文本图层的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getLayerTextPopupMenu() {
		return this.layerTextPopupMenu;
	}

	private JPopupMenu layerGridPopupMenu = null;

	/**
	 * 获取二维图层管理器中栅格图层的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getLayerGridPopupMenu() {
		return this.layerGridPopupMenu;
	}

	private JPopupMenu layerImagePopupMenu = null;

	/**
	 * 获取二维图层管理器中矢量图层的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getLayerImagePopupMenu() {
		return this.layerImagePopupMenu;
	}

	private JPopupMenu layerVectorThemeUniqueAndRangePopupMenu = null;

	/**
	 * 获取二维图层管理器中矢量单值、分段专题图图层的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getLayerVectorThemeUniqueAndRangePopupMenu() {
		return this.layerVectorThemeUniqueAndRangePopupMenu;
	}

	private JPopupMenu layerGridThemePopupMenu = null;

	/**
	 * 获取二维图层管理器中栅格单值、分段专题图图层的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getLayerGridThemePopupMenu() {
		return this.layerGridThemePopupMenu;
	}

	private JPopupMenu layerVectorThemeOtherPopupMenu = null;

	/**
	 * 获取二维图层管理器中矢量点密度、统计、等级符号、标签、自定义专题图图层的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getLayerVectorThemeOtherPopupMenu() {
		return this.layerVectorThemeOtherPopupMenu;
	}

	private JPopupMenu layerPopupMenu = null;

	/**
	 * 获取选中二维图层管理器中多个图层的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getLayerPopupMenu() {
		return this.layerPopupMenu;
	}

	private JPopupMenu layerGroupPopupMenu = null;

	/**
	 * 获取图层分组右键菜单
	 *
	 * @return
	 */
	public JPopupMenu getLayerGroupPopupMenu() {
		return this.layerGroupPopupMenu;
	}

	private JPopupMenu layerCachePopupMenu = null;

	/**
	 * 获取图层分组右键菜单
	 *
	 * @return
	 */
	public JPopupMenu getLayerCachePopupMenu() {
		return this.layerCachePopupMenu;
	}

	private JPopupMenu themeItemUniqueAndRangePopupMenu = null;

	/**
	 * 获取二维矢量、栅格，单值、分段专题图子项右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getThemeItemUniqueAndRangePopupMenu() {
		return this.themeItemUniqueAndRangePopupMenu;
	}

	private JPopupMenu themeItemVectorStatPopupMenu = null;

	/**
	 * 获取二维矢量统计专题图子项右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getThemeItemVectorStatPopupMenu() {
		return this.themeItemVectorStatPopupMenu;
	}

	private JPopupMenu feature3DPopupMenu = null;

	/**
	 * 获取三维图层管理器中三维要素对象的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getFeature3DPopupMenu() {
		return this.feature3DPopupMenu;
	}

	private JPopupMenu feature3DsPopupMenu = null;

	/**
	 * 获取三维图层管理器中三维要素集合的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getFeature3DsPopupMenu() {
		return this.feature3DsPopupMenu;
	}

	private JPopupMenu generalLayersPopupMenu = null;

	/**
	 * 获取三维图层管理器中普通图层的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getGeneralLayersPopupMenu() {
		return this.generalLayersPopupMenu;
	}

	private JPopupMenu geometryPopupMenu = null;

	/**
	 * 获取选中对象的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getGeometryPopupMenu() {
		return this.geometryPopupMenu;
	}

	private JPopupMenu layer3DDatasetPopupMenu = null;

	/**
	 * 获取三维图层管理器中三维数据集图层的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getLayer3DDatasetPopupMenu() {
		return this.layer3DDatasetPopupMenu;
	}

	private JPopupMenu layer3DKMLPopupMenu = null;

	/**
	 * 获取三维图层管理器中KML图层的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getLayer3DKMLPopupMenu() {
		return this.layer3DKMLPopupMenu;
	}

	private JPopupMenu layer3DImagePopupMenu = null;

	/**
	 * 获取三维图层管理器中三维影像图层的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getLayer3DImagePopupMenu() {
		return this.layer3DImagePopupMenu;
	}

	private JPopupMenu layer3DSCMPopupMenu = null;

	/**
	 * 获取三维图层管理器中三维SCM模型缓存的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getLayer3DSCMPopupMenu() {
		return this.layer3DSCMPopupMenu;
	}

	private JPopupMenu layer3DVectorCachePopupMenu = null;

	/**
	 * 获取三维图层管理器中三维矢量缓存的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getLayer3DVectorCachePopupMenu() {
		return this.layer3DVectorCachePopupMenu;
	}

	private JPopupMenu screenLayer3DPopupMenu = null;

	/**
	 * 获取三维图层管理器中屏幕图层的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getScreenLayer3DPopupMenu() {
		return this.screenLayer3DPopupMenu;
	}

	private JPopupMenu terrainLayerPopupMenu = null;

	/**
	 * 获取三维图层管理器中地形图层的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getTerrainLayerPopupMenu() {
		return this.terrainLayerPopupMenu;
	}

	private JPopupMenu terrainLayersPopupMenu = null;

	/**
	 * 获取三维图层管理器中地形图层集合的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getTerrainLayersPopupMenu() {
		return this.terrainLayersPopupMenu;
	}

	private JPopupMenu layer3DThemePopupMenu = null;

	/**
	 * 获取三维图层管理器中三维专题图图层的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getLayer3DThemePopupMenu() {
		return this.layer3DThemePopupMenu;
	}

	private JPopupMenu trackingLayerPopupMenu = null;

	/**
	 * 获取三维图层管理器中跟踪图层的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getTrackingLayerPopupMenu() {
		return this.trackingLayerPopupMenu;
	}

	private JPopupMenu layer3DPopupMenu = null;

	/**
	 * 获取三维图层管理器中三维图层的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getLayer3DPopupMenu() {
		return this.layer3DPopupMenu;
	}

	private JPopupMenu layer3DMapPopupMenu = null;

	/**
	 * 获取三维图层管理器中三维地图图层的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getLayer3DMapPopupMenu() {
		return this.layer3DMapPopupMenu;
	}

	private JPopupMenu layerHeatmapAndAggregationPopupMenu = null;

	/**
	 * 获取热力图或网格图的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getLayerHeatmapAndAggregationPopupMenu() {
		return this.layerHeatmapAndAggregationPopupMenu;
	}

	private JPopupMenu layersTreeToolBarPopupMenu = null;

	/**
	 * 获取图层管理器工具条对应的右键菜单。
	 *
	 * @return
	 */
	public JPopupMenu getLayersTreeToolBarPopupMenu() {
		return this.layersTreeToolBarPopupMenu;
	}

	public JPopupMenu getLayerWMSPopupMenu() {
		return layerWMSPopupMenu;
	}

	/**
	 * 创建右键菜单对象
	 */
	private void buildContextMenu() {
		try {

			if (Application.getActiveApplication().getMainFrame() != null) {
				IContextMenuManager manager = Application.getActiveApplication().getMainFrame().getContextMenuManager();

				this.layerVectorPopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.LayerVectorContextMenu");
				this.layerVectorCADPopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.LayerVectorCADContextMenu");
				this.layerCachePopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.LayerCacheContextMenu");
				this.layerTextPopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.LayerTextContextMenu");
				this.layerGridPopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.LayerGridContextMenu");
				this.layerImagePopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.LayerImageContextMenu");
				this.layerVectorThemeUniqueAndRangePopupMenu = (JPopupMenu) manager
						.get("SuperMap.Desktop.UI.LayersControlManager.LayerVectorThemeUniqueAndRangeContextMenu");
				this.layerGridThemePopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.LayerGridThemeContextMenu");
				this.layerVectorThemeOtherPopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.LayerVectorThemeOtherContextMenu");
				this.layerPopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.LayerContextMenu");
				this.layerGroupPopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.LayerFolderContextMenu");
				this.themeItemUniqueAndRangePopupMenu = (JPopupMenu) manager
						.get("SuperMap.Desktop.UI.LayersControlManager.ContextMenuPlanarUniqueAndRangeTheme");
				this.themeItemVectorStatPopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.ContextMenuPlanarVectorStatTheme");
				this.feature3DPopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.ContextMenuFeature3D");
				this.geometryPopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.ContextMenuGeometry");
				this.layer3DDatasetPopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.ContextMenuLayer3DDataset");
				this.layer3DKMLPopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.ContextMenuLayer3DKML");
				this.layer3DImagePopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.ContextMenuLayer3DImage");
				this.layer3DSCMPopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.ContextMenuLayer3DSCM");
				this.layer3DVectorCachePopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.ContextMenuLayer3DVectorCache");
				this.screenLayer3DPopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.ContextMenuScreenLayer3D");
				this.terrainLayerPopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.ContextMenuTerrainLayer");
				this.layer3DThemePopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.ContextMenuLayer3DTheme");
				this.trackingLayerPopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.ContextMenuTrackingLayer");
				this.layer3DPopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.ContextMenuLayer3D");
				this.layer3DMapPopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.ContextMenuLayer3DMap");
				this.layerWMSPopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.ContextMenuLayerWMS");
				this.layerHeatmapAndAggregationPopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.ContextMenuHeatmapAndAggregation");
				this.layersTreeToolBarPopupMenu = (JPopupMenu) manager.get("SuperMap.Desktop.UI.LayersControlManager.ContextMenuLayersTreeToolBar");
				this.isContextMenuBuilded = true;
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	// 获取节点下面的所有节点，包括子节点和子节点的子节点
	private void initAllNodes(TreeNode node) {
		if (node.getChildCount() >= 0) {//判断是否有子节点
			for (Enumeration e = node.children(); e.hasMoreElements(); ) {
				TreeNode n = (TreeNode) e.nextElement();
				this.allTreeNode.add(n);
				initAllNodes(n);//若有子节点则再次查找
			}
		}
	}
}
