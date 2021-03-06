package com.supermap.desktop.ui.trees;

import com.supermap.data.DatasetImage;
import com.supermap.data.DatasetType;
import com.supermap.desktop.CommonToolkit;
import com.supermap.desktop.ui.controls.InternalImageIconFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

/**
 * 影像数据集节点装饰器
 * @author xuzw
 *
 */
class DatasetImageNodeDecorator implements TreeNodeDecorator {

	public void decorate(JLabel label, TreeNodeData data) {
		if(data.getType().equals(NodeDataType.DATASET_IMAGE)){
			DatasetImage datasetImage = (DatasetImage) data.getData();
			DatasetType type = datasetImage.getType();
			label.setText(datasetImage.getName());
			ImageIcon icon = (ImageIcon) label.getIcon();
			BufferedImage bufferedImage = new BufferedImage(IMAGEICON_WIDTH,
					IMAGEICON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
			Graphics graphics = bufferedImage.getGraphics();
			if(type.equals(DatasetType.IMAGE)){
				String path = CommonToolkit.DatasetImageWrap.getImageIconPath(type);
				URL url = DatasetImageNodeDecorator.class.getResource(path);
				graphics.drawImage(
						new ImageIcon(url).getImage(), 0, 0, label);
			}else if(type.equals(DatasetType.WCS)){
				graphics.drawImage(
						InternalImageIconFactory.DT_WCS.getImage(), 0, 0, label);
			}else if(type.equals(DatasetType.WMS)){
				graphics.drawImage(
						InternalImageIconFactory.DT_WMS.getImage(), 0, 0, label);
			}
			icon.setImage(bufferedImage);
		}
	}

}
