package com.supermap.desktop.CtrlAction.Layout;

import com.supermap.desktop.Application;
import com.supermap.desktop.CommonToolkit;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.Interface.IFormLayout;
import com.supermap.desktop.enums.WindowType;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.ui.UICommonToolkit;
import com.supermap.desktop.ui.trees.NodeDataType;
import com.supermap.desktop.ui.trees.TreeNodeData;
import com.supermap.desktop.ui.trees.WorkspaceTree;
import com.supermap.layout.MapLayout;

import javax.swing.tree.DefaultMutableTreeNode;

public class CtrlActionBrowserLayout extends CtrlAction {

	public CtrlActionBrowserLayout(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public void run() {
		try {
			WorkspaceTree workspaceTree = UICommonToolkit.getWorkspaceManager().getWorkspaceTree();
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) workspaceTree.getSelectionPath().getLastPathComponent();
			TreeNodeData selectedNodeData = (TreeNodeData) selectedNode.getUserObject();
			String layoutName = (String) selectedNodeData.getData();

			IFormLayout formLayout = (IFormLayout) CommonToolkit.FormWrap.fireNewWindowEvent(WindowType.LAYOUT, layoutName);
			if (formLayout != null) {
				MapLayout mapLayout = formLayout.getMapLayoutControl().getMapLayout();
				mapLayout.open(layoutName);
				mapLayout.refresh();
				UICommonToolkit.getLayersManager().setMap(null);
				UICommonToolkit.getLayersManager().setScene(null);
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	@Override
	public boolean enable() {
		boolean enable = false;
		WorkspaceTree workspaceTree = UICommonToolkit.getWorkspaceManager().getWorkspaceTree();
		if (workspaceTree.getSelectionCount() == 1) {
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) workspaceTree.getSelectionPath().getLastPathComponent();
			TreeNodeData selectedNodeData = (TreeNodeData) selectedNode.getUserObject();
			if (selectedNodeData != null && selectedNodeData.getType() == NodeDataType.LAYOUT_NAME) {
				enable = true;
			}
		}
		return enable;
	}

}
