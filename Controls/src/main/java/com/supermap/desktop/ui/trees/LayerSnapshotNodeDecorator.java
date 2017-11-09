package com.supermap.desktop.ui.trees;

import com.supermap.desktop.ui.controls.InternalImageIconFactory;
import com.supermap.mapping.LayerSnapshot;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by lixiaoyao on 2017/10/31.
 */
public class LayerSnapshotNodeDecorator implements TreeNodeDecorator {

	public void decorate(JLabel label, TreeNodeData data) {
		if (data.getType().equals(NodeDataType.LAYER_SNAPSHOT)) {
			LayerSnapshot layerSnapshot = (LayerSnapshot)data.getData();
			label.setText(layerSnapshot.getCaption());

			ImageIcon icon = (ImageIcon) label.getIcon();
			BufferedImage bufferedImage = new BufferedImage(
					IMAGEICON_WIDTH, IMAGEICON_HEIGHT,
					BufferedImage.TYPE_INT_ARGB);
			Graphics graphics = bufferedImage.getGraphics();
			graphics.drawImage(
					InternalImageIconFactory.LAYER_SNAPSHOT.getImage(), 0,
					0, label);
			icon.setImage(bufferedImage);

		}
	}
}
