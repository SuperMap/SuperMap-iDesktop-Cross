package com.supermap.desktop.ui.trees;

import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.ui.controls.InternalImageIconFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 地形图层集合节点装饰器
 * 
 * @author xuzw
 *
 */
class TerrainLayersNodeDecorator implements TreeNodeDecorator {

	public void decorate(JLabel label, TreeNodeData data) {
		if (data.getType().equals(NodeDataType.TERRAIN_LAYERS)) {
			label.setText(ControlsProperties.getString(ControlsProperties.TerrainLayersNodeName));
			ImageIcon icon = (ImageIcon) label.getIcon();
			BufferedImage bufferedImage = new BufferedImage(IMAGEICON_WIDTH, IMAGEICON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
			Graphics graphics = bufferedImage.getGraphics();
			graphics.drawImage(InternalImageIconFactory.TERRAIN_LAYERS.getImage(), 0, 0, label);
			icon.setImage(bufferedImage);
		}
	}

}
