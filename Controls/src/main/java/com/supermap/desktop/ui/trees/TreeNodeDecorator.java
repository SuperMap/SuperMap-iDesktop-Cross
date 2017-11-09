package com.supermap.desktop.ui.trees;

import javax.swing.*;

/**
 * 节点渲染装饰接口
 * @author xuzw
 *
 */
public interface TreeNodeDecorator {
	public void decorate(JLabel label, TreeNodeData data);
	public static final int IMAGEICON_WIDTH = 16;
	public static final int IMAGEICON_HEIGHT = 16;
}
