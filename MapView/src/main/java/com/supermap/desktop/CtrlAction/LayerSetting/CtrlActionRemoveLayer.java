package com.supermap.desktop.CtrlAction.LayerSetting;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.Interface.IFormMap;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.ui.UICommonToolkit;
import com.supermap.desktop.ui.trees.LayersTree;
import com.supermap.desktop.ui.trees.NodeDataType;
import com.supermap.desktop.ui.trees.TreeNodeData;
import com.supermap.mapping.Layer;

import javax.swing.tree.DefaultMutableTreeNode;

public class CtrlActionRemoveLayer extends CtrlAction {

	public CtrlActionRemoveLayer(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public void run() {
		try {
			IFormMap formMap = (IFormMap) Application.getActiveApplication().getActiveForm();
			formMap.removeLayers(formMap.getActiveLayers());
			LayersTree tree = UICommonToolkit.getLayersManager().getLayersTree();
			tree.setSelectionRow(0);
			tree.firePropertyChangeWithLayerSelect();
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	@Override
	public boolean enable() {
		boolean enable = false;
		if (Application.getActiveApplication().getActiveForm() instanceof IFormMap) {
			IFormMap formMap = (IFormMap) Application.getActiveApplication().getActiveForm();
			if (formMap != null) {
				LayersTree layersTree = UICommonToolkit.getLayersManager().getLayersTree();
				if (layersTree != null) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) layersTree.getSelectionPaths()[0].getLastPathComponent();
					TreeNodeData selectedNodeData = (TreeNodeData) selectedNode.getUserObject();
					Layer layer = (Layer) selectedNodeData.getData();
					if (layer != null && selectedNodeData.getType() != NodeDataType.WMSSUB_LAYER && selectedNodeData.getType() != NodeDataType.THEME_GRAPH_ITEM) {
						enable = true;
					}
				}
			}
		}
		return enable;
	}

}