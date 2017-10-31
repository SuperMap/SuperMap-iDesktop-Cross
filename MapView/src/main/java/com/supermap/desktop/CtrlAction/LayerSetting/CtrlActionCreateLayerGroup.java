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
import javax.swing.tree.TreeNode;
import java.util.Enumeration;

/**
 * Created by lixiaoyao on 2017/10/11.
 */
public class CtrlActionCreateLayerGroup extends CtrlAction {

	private TreeNodeData selectedNodeData = null;

	public CtrlActionCreateLayerGroup(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public void run() {
		IForm iForm = Application.getActiveApplication().getActiveForm();
		if (this.selectedNodeData != null && this.selectedNodeData.getData() instanceof LayerGroup &&
				iForm != null && iForm instanceof FormMap) {
			FormMap formMap = (FormMap) iForm;
			String layerGroupName = formMap.getMapControl().getMap().getLayers().getAvailableCaption("LayerGroup");
			LayerGroup layerGroup = (LayerGroup) this.selectedNodeData.getData();
			layerGroup.addGroup(layerGroupName);
			LayersTree layersTree = UICommonToolkit.getLayersManager().getLayersTree();
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) layersTree.getLastSelectedPathComponent();
//			layersTree.expandRow(layersTree.getMaxSelectionRow());
//			layersTree.clearSelection();
			layersTree.setSelectionPath(layersTree.getSelectionPath().pathByAddingChild(selectedNode.getLastChild()));
			layersTree.startEditingAtPath(layersTree.getSelectionPath().pathByAddingChild(selectedNode.getLastChild()));
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

	// 获取节点下面的所有节点，包括子节点和子节点的子节点
	public void visitAllNodes(TreeNode node) {
		if (node.getChildCount() >= 0) {//判断是否有子节点
			for (Enumeration e = node.children(); e.hasMoreElements(); ) {
				TreeNode n = (TreeNode) e.nextElement();
				visitAllNodes(n);//若有子节点则再次查找
			}
		}
	}
}
