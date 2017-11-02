package com.supermap.desktop.CtrlAction.LayerSetting;

import com.supermap.desktop.Application;
import com.supermap.desktop.FormMap;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.ui.UICommonToolkit;
import com.supermap.desktop.ui.controls.LayersTree;
import com.supermap.desktop.ui.controls.NodeDataType;
import com.supermap.desktop.ui.controls.TreeNodeData;
import com.supermap.mapping.Layer;
import com.supermap.mapping.LayerGroup;
import com.supermap.mapping.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Created by lixiaoyao on 2017/10/19.
 */
public class CtrlActionUnLayerGroup extends CtrlAction {
//	private TreeNodeData selectedNodeData = null;
	private TreeNodeData selectedNodeDatas[] =null;

	public CtrlActionUnLayerGroup(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public void run() {
		IForm iForm = Application.getActiveApplication().getActiveForm();
		if (this.selectedNodeDatas != null && iForm != null && iForm instanceof FormMap) {
//			LayersTree layersTree = UICommonToolkit.getLayersManager().getLayersTree();
//			int originSelectedRowIndex = layersTree.getRowForPath(layersTree.getSelectionPath());
			FormMap formMap = (FormMap) iForm;
			for (int i=0;i<this.selectedNodeDatas.length;i++) {
				LayerGroup layerGroup = (LayerGroup) this.selectedNodeDatas[i].getData();
				layerGroup.ungroup();
				formMap.getMapControl().getMap().refresh();
			}
			System.out.println(formMap.getMapControl().getMap().getLayers().getCount());
			Map map = formMap.getMapControl().getMap();
			try {
				for (int i = 0; i < map.getLayers().getCount(); i++) {
					Layer layer = map.getLayers().get(i);
				}
			} catch (Exception ex) {
				System.out.println("Current count is:");
				System.out.println(map.getLayers().getCount());
				Application.getActiveApplication().getOutput().output(ex);
			}
		}
	}

	@Override
	public boolean enable() {
		boolean enable = false;
		LayersTree layersTree = UICommonToolkit.getLayersManager().getLayersTree();
		if (layersTree != null &&layersTree.getSelectionPaths()!=null) {
			TreePath treePath[]= layersTree.getSelectionPaths();
			this.selectedNodeDatas=new TreeNodeData[treePath.length];

			for (int i=0;i<treePath.length;i++){
				DefaultMutableTreeNode selectedTreeNode = (DefaultMutableTreeNode)treePath[i].getLastPathComponent();
				TreeNodeData selectedTreeNodeData=(TreeNodeData) selectedTreeNode.getUserObject();
				if (selectedTreeNodeData.getType() != NodeDataType.LAYER_GROUP && selectedTreeNodeData.getType()!=NodeDataType.LAYER_SNAPSHOT) {
					break;
				}else{
					this.selectedNodeDatas[i]=selectedTreeNodeData;
					if (i==treePath.length-1){
						enable=true;
					}
				}
			}
//			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) layersTree.getLastSelectedPathComponent();
//			this.selectedNodeData = (TreeNodeData) selectedNode.getUserObject();

		}
		if (!enable) {
			this.selectedNodeDatas = null;
		}
		return enable;
	}
}
