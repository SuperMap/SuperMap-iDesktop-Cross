package com.supermap.desktop.ui.trees;

import com.supermap.desktop.ui.controls.DecoratorUnities;
import com.supermap.desktop.ui.controls.InternalImageIconFactory;
import com.supermap.desktop.ui.trees.LayersTreeUtilties;
import com.supermap.desktop.ui.trees.TreeNodeData;
import com.supermap.desktop.ui.trees.TreeNodeDecorator;
import com.supermap.mapping.Layer;
import com.supermap.realspace.Layer3D;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 图层是否可编辑装饰器
 * 
 * @author xuzw
 *
 */
class EditableDecorator implements TreeNodeDecorator {

	public void decorate(JLabel label, TreeNodeData data) {
		if (DecoratorUnities.isDecoratorShow(data.getData())) {
			return;
		}
		boolean isEditable = false;
		ImageIcon icon = (ImageIcon) label.getIcon();
		BufferedImage bufferedImage = new BufferedImage(IMAGEICON_WIDTH, IMAGEICON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = bufferedImage.getGraphics();
		isEditable = getState(data.getData());
		boolean isVisible = LayersTreeUtilties.isTreeNodeDataVisible(data.getData());

		if (isEditable && isVisible) {
			graphics.drawImage(InternalImageIconFactory.EDITABLE.getImage(), 0, 0, label);
		} else {
			graphics.drawImage(InternalImageIconFactory.UNEDITABLE.getImage(), 0, 0, label);
		}
		icon.setImage(bufferedImage);
	}

	public boolean getState(Object data) {
		boolean isEditable = false;
		if (data instanceof Layer) {
			Layer layer = (Layer) data;
			isEditable = layer.isEditable();
		} else if (data instanceof Layer3D) {
			Layer3D layer3D = (Layer3D) data;
			isEditable = layer3D.isEditable();
		}
		return isEditable;
	}

}
