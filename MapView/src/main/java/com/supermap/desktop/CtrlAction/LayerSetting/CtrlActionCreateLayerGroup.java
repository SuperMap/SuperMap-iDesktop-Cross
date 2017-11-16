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
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by lixiaoyao on 2017/10/11.
 */
public class CtrlActionCreateLayerGroup extends CtrlAction {

	private TreeNodeData selectedNodeData = null;
	private ArrayList<TreeNode> allTreeNode=new ArrayList<>();

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
			LayerGroup[] oldExpandLayerGroup =formMap.getExpandLayerGroup();
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
//			formMap.getMapControl().getMap().refresh();
			LayersTree layersTree = UICommonToolkit.getLayersManager().getLayersTree();
			if (!isNeedAddExpandLayerGroup) {
				layersTree.reload();
			}else{
				layersTree.reload(newExpandLayerGroup);
			}
			this.allTreeNode.clear();
			initAllNodes((TreeNode) layersTree.getModel().getRoot());
			TreePath newLayerGroupTreePath=null;
			for (int j=0;j<this.allTreeNode.size();j++){
				DefaultMutableTreeNode node=(DefaultMutableTreeNode)this.allTreeNode.get(j);
				TreeNodeData nodeData = (TreeNodeData) node.getUserObject();

				if (nodeData.getData() == layerGroup.get(layerGroup.getCount()-1)) {
					newLayerGroupTreePath=new TreePath(node.getPath());
					break;
				}
			}
//			layersTree.clearSelection();
			layersTree.startEditingAtPath(newLayerGroupTreePath);
		}
	}

	@Override
	public boolean enable() {
		boolean enable = false;
		LayersTree layersTree = UICommonToolkit.getLayersManager().getLayersTree();
		if (layersTree != null && layersTree.getSelectionCount() == 1) {
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) layersTree.getLastSelectedPathComponent();
			this.selectedNodeData = (TreeNodeData) selectedNode.getUserObject();
			if (this.selectedNodeData.getType() == NodeDataType.LAYER_GROUP ||
					this.selectedNodeData.getType() == NodeDataType.LAYER_SNAPSHOT) {
				enable = true;
			}
		}
		if (!enable) {
			this.selectedNodeData = null;
		}
		return enable;
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
