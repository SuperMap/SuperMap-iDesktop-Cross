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
import com.supermap.mapping.LayerGroup;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by lixiaoyao on 2017/10/19.
 */
public class CtrlActionUnLayerGroup extends CtrlAction {
	private TreeNodeData selectedNodeData = null;

	public CtrlActionUnLayerGroup(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public void run() {
		IForm iForm = Application.getActiveApplication().getActiveForm();
		if (this.selectedNodeData != null && this.selectedNodeData.getData() instanceof LayerGroup &&
				iForm != null && iForm instanceof FormMap) {
			LayersTree layersTree = UICommonToolkit.getLayersManager().getLayersTree();
			int originSelectedRowIndex=layersTree.getRowForPath(layersTree.getSelectionPath());
			FormMap formMap = (FormMap) iForm;
			LayerGroup layerGroup = (LayerGroup) this.selectedNodeData.getData();
			layerGroup.ungroup();
			formMap.getMapControl().getMap().refresh();

//			layersTree.setSelectionRow(0);
//			layersTree.firePropertyChangeWithLayerSelect();
//			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) layersTree.getLastSelectedPathComponent();
//			layersTree.expandRow(layersTree.getMaxSelectionRow());
//			layersTree.setSelectionPath(layersTree.getSelectionPath().pathByAddingChild(selectedNode.getLastChild()));
//			layersTree.startEditingAtPath(layersTree.getSelectionPath().pathByAddingChild(selectedNode.getLastChild()));
		}
	}

	@Override
	public boolean enable() {
		boolean enable = false;
		LayersTree layersTree = UICommonToolkit.getLayersManager().getLayersTree();
		if (layersTree != null && layersTree.getSelectionCount() == 1) {
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) layersTree.getLastSelectedPathComponent();
			this.selectedNodeData = (TreeNodeData) selectedNode.getUserObject();
			if (this.selectedNodeData.getType() == NodeDataType.LAYER_GROUP) {
				enable = true;
			}
		}
		if (!enable) {
			this.selectedNodeData = null;
		}
		return enable;
	}
}
