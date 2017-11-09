package com.supermap.desktop.ui.trees;

import com.supermap.desktop.controls.utilities.ControlsResources;
import com.supermap.desktop.controls.utilities.WorkspaceTreeManagerUIUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 三维场景名节点装饰器
 * @author xuzw
 *
 */
class SceneNameNodeDecorator implements TreeNodeDecorator {

	public void decorate(JLabel label, TreeNodeData data) {
		if(data.getType().equals(NodeDataType.SCENE_NAME)){
			String name = (String) data.getData();
			label.setText(name);
			ImageIcon icon = (ImageIcon) label.getIcon();
			BufferedImage bufferedImage = new BufferedImage(IMAGEICON_WIDTH,
					IMAGEICON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
			Graphics graphics = bufferedImage.getGraphics();
			graphics.drawImage(
					new ImageIcon(ControlsResources.getResourceURL(WorkspaceTreeManagerUIUtilities.SceneIconPath)).getImage(), 0, 0, label);
			icon.setImage(bufferedImage);
		}
	}

}
