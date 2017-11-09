package com.supermap.desktop.ui.trees;

/*
 * ArrayListTransferHandler.java is used by the 1.4
 * DragListDemo.java example.
 */

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class WorkspaceTreeTransferHandler extends TransferHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTree source = null;

	public WorkspaceTreeTransferHandler() {
		// 默认实现，后续进行初始化操作
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		if (c instanceof WorkspaceTree) {
			source = (WorkspaceTree) c;
			if (null != source.getLastSelectedPathComponent()) {
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) source.getLastSelectedPathComponent();
				if (null != treeNode && null != treeNode.getUserObject()) {
					TreeNodeData data = (TreeNodeData) treeNode.getUserObject();
					return new TransferableTreeNode(data);
				}
			}
		}
		return null;
	}

	@Override
	public int getSourceActions(JComponent c) {
		return COPY_OR_MOVE;
	}

	class TransferableTreeNode implements Transferable {
		private DataFlavor TREE_NODE_FLAVOR = new DataFlavor(TreeNodeData.class, "TreeNodeData");

		DataFlavor flavors[] = { TREE_NODE_FLAVOR };

		TreeNodeData data;

		public TransferableTreeNode(TreeNodeData dt) {
			data = dt;
		}

		@Override
		public synchronized DataFlavor[] getTransferDataFlavors() {
			return flavors;
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return flavor.getRepresentationClass() == TreeNodeData.class;
		}

		@Override
		public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if (isDataFlavorSupported(flavor)) {
				return (Object) data;
			} else {
				throw new UnsupportedFlavorException(flavor);
			}
		}
	}
}
