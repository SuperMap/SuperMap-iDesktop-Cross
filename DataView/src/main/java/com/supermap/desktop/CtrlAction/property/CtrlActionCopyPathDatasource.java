package com.supermap.desktop.CtrlAction.property;

import com.supermap.data.Datasource;
import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.dataview.DataViewProperties;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.ui.UICommonToolkit;
import com.supermap.desktop.ui.trees.NodeDataType;
import com.supermap.desktop.ui.trees.TreeNodeData;
import com.supermap.desktop.ui.trees.WorkspaceTree;
import com.supermap.desktop.utilities.ClipBoardUtilties;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Created by lixiaoyao on 2017/9/11.
 */
public class CtrlActionCopyPathDatasource extends CtrlAction {
	public CtrlActionCopyPathDatasource(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public void run() {
		WorkspaceTree workspaceTree = UICommonToolkit.getWorkspaceManager().getWorkspaceTree();
		TreePath selectedPath = workspaceTree.getSelectionPath();
		try {
			if (selectedPath != null && selectedPath.getLastPathComponent() instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
				TreeNodeData nodeData = (TreeNodeData) selectedNode.getUserObject();
				if (nodeData.getType() == NodeDataType.DATASOURCE) {
					Datasource tempDatasource = (Datasource) nodeData.getData();
					ClipBoardUtilties.setSysClipboardText(tempDatasource.getConnectionInfo().getServer());
				}
				Application.getActiveApplication().getOutput().output(DataViewProperties.getString("String_CopyFilePath"));
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

}
