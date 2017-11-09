package com.supermap.desktop.ui.trees;

import com.supermap.desktop.ui.controls.InternalImageIconFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 栅格数据集集合子项节点装饰器
 * 
 * @author gouyu
 *
 */
class DatasetGridCollectionItemNodeDecorator implements TreeNodeDecorator {
	@Override
	public void decorate(JLabel label, TreeNodeData data) {
		if (data.getType().equals(NodeDataType.DATASET_GRID_COLLECTION_ITEM)) {
			label.setText((String) data.getData());
			ImageIcon icon = (ImageIcon) label.getIcon();
			BufferedImage bufferedImage = new BufferedImage(IMAGEICON_WIDTH, IMAGEICON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
			Graphics graphics = bufferedImage.getGraphics();
			graphics.drawImage(InternalImageIconFactory.DT_GRID.getImage(), 0, 0, label);
			icon.setImage(bufferedImage);
		}
	}

}
