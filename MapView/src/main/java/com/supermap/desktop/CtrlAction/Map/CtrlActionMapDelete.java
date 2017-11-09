package com.supermap.desktop.CtrlAction.Map;

import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.controls.utilities.ToolbarUIUtilities;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.ui.UICommonToolkit;
import com.supermap.desktop.ui.trees.NodeDataType;
import com.supermap.desktop.ui.trees.TreeNodeData;
import com.supermap.desktop.ui.trees.WorkspaceTree;
import com.supermap.desktop.utilities.MapUtilities;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;

public class CtrlActionMapDelete extends CtrlAction {

	public CtrlActionMapDelete(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		WorkspaceTree workspaceTree = UICommonToolkit.getWorkspaceManager().getWorkspaceTree();
		ArrayList<String> mapNames = new ArrayList<String>();
		for (TreePath treePath : workspaceTree.getSelectionPaths()) {
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
			TreeNodeData selectedNodeData = (TreeNodeData) treeNode.getUserObject();
			mapNames.add(selectedNodeData.getData().toString());
		}
		MapUtilities.deleteMaps(mapNames.toArray(new String[mapNames.size()]));
		ToolbarUIUtilities.updataToolbarsState();
	}

	@Override
	public boolean enable() {
		boolean enable = false;
		WorkspaceTree workspaceTree = UICommonToolkit.getWorkspaceManager().getWorkspaceTree();
		if (workspaceTree != null && workspaceTree.getSelectionCount() > 0) {
			TreePath treeSelectionPath = workspaceTree.getSelectionPath();
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treeSelectionPath.getLastPathComponent();
			TreeNodeData selectedNodeData = (TreeNodeData) treeNode.getUserObject();
			if (selectedNodeData != null && selectedNodeData.getType() == NodeDataType.MAP_NAME) {
				enable = true;
			}
		}

		return enable;
	}

}
