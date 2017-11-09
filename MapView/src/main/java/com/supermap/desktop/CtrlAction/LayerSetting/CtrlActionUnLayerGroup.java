package com.supermap.desktop.CtrlAction.LayerSetting;

import com.supermap.desktop.Application;
import com.supermap.desktop.FormMap;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.ui.UICommonToolkit;
import com.supermap.desktop.ui.trees.LayersTree;
import com.supermap.desktop.ui.trees.NodeDataType;
import com.supermap.desktop.ui.trees.TreeNodeData;
import com.supermap.mapping.LayerGroup;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Created by lixiaoyao on 2017/10/19.
 */
public class CtrlActionUnLayerGroup extends CtrlAction {
//	private TreeNodeData selectedNodeData = null;
	private LayerGroup selectedLayerGroup[] =null;

	public CtrlActionUnLayerGroup(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public void run() {
		IForm iForm = Application.getActiveApplication().getActiveForm();
		if (this.selectedLayerGroup != null && iForm != null && iForm instanceof FormMap) {
//			LayersTree layersTree = UICommonToolkit.getLayersManager().getLayersTree();
//			int originSelectedRowIndex = layersTree.getRowForPath(layersTree.getSelectionPath());
			FormMap formMap = (FormMap) iForm;
			for (int i = 0; i<this.selectedLayerGroup.length; i++) {
				LayerGroup layerGroup = this.selectedLayerGroup[i];
				layerGroup.ungroup();
			}
			formMap.getMapControl().getMap().refresh();
//			System.out.println(formMap.getMapControl().getMap().getLayers().getCount());
//			Map map = formMap.getMapControl().getMap();
//			try {
//				for (int i = 0; i < map.getLayers().getCount(); i++) {
//					Layer layer = map.getLayers().get(i);
//				}
//			} catch (Exception ex) {
//				System.out.println("Current count is:");
//				System.out.println(map.getLayers().getCount());
//				Application.getActiveApplication().getOutput().output(ex);
//			}
		}
	}

	@Override
	public boolean enable() {
		boolean enable = false;
		LayersTree layersTree = UICommonToolkit.getLayersManager().getLayersTree();
		if (layersTree != null &&layersTree.getSelectionPaths()!=null) {
			TreePath treePath[]= layersTree.getSelectionPaths();
			this.selectedLayerGroup =new LayerGroup[treePath.length];

			for (int i=0;i<treePath.length;i++){
				DefaultMutableTreeNode selectedTreeNode = (DefaultMutableTreeNode)treePath[i].getLastPathComponent();
				TreeNodeData selectedTreeNodeData=(TreeNodeData) selectedTreeNode.getUserObject();
				if (selectedTreeNodeData.getType() != NodeDataType.LAYER_GROUP && selectedTreeNodeData.getType()!=NodeDataType.LAYER_SNAPSHOT) {
					break;
				}else{
					this.selectedLayerGroup[i]=(LayerGroup)selectedTreeNodeData.getData();
					if (i==treePath.length-1){
						enable=true;
					}
				}
			}
//			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) layersTree.getLastSelectedPathComponent();
//			this.selectedNodeData = (TreeNodeData) selectedNode.getUserObject();

		}
		if (!enable) {
			this.selectedLayerGroup = null;
		}
		return enable;
	}
}
