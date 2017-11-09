package com.supermap.desktop.ui.trees;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IFormMap;
import com.supermap.desktop.ui.controls.InternalImageIconFactory;
import com.supermap.desktop.ui.trees.LayersTreeUtilties;
import com.supermap.desktop.ui.trees.TreeNodeData;
import com.supermap.desktop.ui.trees.TreeNodeDecorator;
import com.supermap.mapping.Layer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 图层是否可见节点装饰器
 * fix by lixiaoyao
 * @author hmily
 */
class VisibleDecorator implements TreeNodeDecorator {

	private static final int LEFT_X = 1;
	private static final int RIGHT_X = 15;
	private static final int TOP_Y = 1;
	private static final int BOTTOM_Y = 14;
	private static final int SHORT_LINE_LENGTH = 3;
	private static final Color WARNING_COLOR = Color.RED;
	private static final Color NO_WARNING_COLOR = (new JTable()).getSelectionBackground();
	private static final Color UNAVAILABLE_COLOR = Color.GRAY;
	private Graphics graphics = null;
	private boolean isVisible = false;

	@Override
	public void decorate(JLabel label, TreeNodeData data) {
		ImageIcon icon = (ImageIcon) label.getIcon();
		BufferedImage bufferedImage = new BufferedImage(IMAGEICON_WIDTH, IMAGEICON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		this.graphics = bufferedImage.getGraphics();
		this.isVisible = LayersTreeUtilties.isTreeNodeDataVisible(data.getData());

		if (this.isVisible) {
			this.graphics.drawImage(InternalImageIconFactory.VISIBLE.getImage(), 0, 0, label);
		} else {
			this.graphics.drawImage(InternalImageIconFactory.INVISIBLE.getImage(), 0, 0, label);
		}
		setState(data.getData());
		icon.setImage(bufferedImage);
	}

	private void drawLine(boolean isTopLine, Color color) {
		if (this.isVisible) {
			this.graphics.setColor(color);
		} else {
			this.graphics.setColor(UNAVAILABLE_COLOR);
		}
		if (isTopLine) {
			this.graphics.drawLine(LEFT_X, TOP_Y, RIGHT_X, TOP_Y);
			this.graphics.drawLine(LEFT_X, TOP_Y, LEFT_X, TOP_Y + SHORT_LINE_LENGTH);
			this.graphics.drawLine(RIGHT_X, TOP_Y, RIGHT_X, TOP_Y + SHORT_LINE_LENGTH);
		} else {
			this.graphics.drawLine(LEFT_X, BOTTOM_Y, RIGHT_X, BOTTOM_Y);
			this.graphics.drawLine(LEFT_X, BOTTOM_Y, LEFT_X, BOTTOM_Y - SHORT_LINE_LENGTH);
			this.graphics.drawLine(RIGHT_X, BOTTOM_Y, RIGHT_X, BOTTOM_Y - SHORT_LINE_LENGTH);
		}
	}

	private void setState(Object data) {
		try {
			if (data instanceof Layer) {
				Layer layer = (Layer) data;
				if (Double.compare(layer.getMaxVisibleScale(), 0) != 0 || Double.compare(layer.getMinVisibleScale(), 0) != 0) {
					IFormMap formMap = (IFormMap) Application.getActiveApplication().getActiveForm();
					double currentScale = formMap.getMapControl().getMap().getScale();
					if (Double.compare(layer.getMaxVisibleScale(), 0) != 0 && Double.compare(layer.getMinVisibleScale(), 0) != 0) {
						if (Double.compare(currentScale, layer.getMaxVisibleScale()) != -1) {
							if (Double.compare(currentScale, layer.getMinVisibleScale()) != -1) {
								drawLine(true, UNAVAILABLE_COLOR);
								drawLine(false, WARNING_COLOR);
							}
//					else {
//						No greater than or equal to the MaxVisibleScale and smaller than the MinVisibleScale
//					}
						} else {
							if (Double.compare(currentScale, layer.getMinVisibleScale()) != -1) {
								drawLine(true, NO_WARNING_COLOR);
								drawLine(false, NO_WARNING_COLOR);
							} else {
								drawLine(true, WARNING_COLOR);
								drawLine(false, UNAVAILABLE_COLOR);
							}
						}
					} else if (Double.compare(layer.getMaxVisibleScale(), 0) != 0 && Double.compare(layer.getMinVisibleScale(), 0) == 0) {
						if (Double.compare(currentScale, layer.getMaxVisibleScale()) != -1) {
							drawLine(false, WARNING_COLOR);
						} else {
							drawLine(false, NO_WARNING_COLOR);
						}
					} else if (Double.compare(layer.getMaxVisibleScale(), 0) == 0 && Double.compare(layer.getMinVisibleScale(), 0) != 0) {
						if (Double.compare(currentScale, layer.getMinVisibleScale()) != -1) {
							drawLine(true, NO_WARNING_COLOR);
						} else {
							drawLine(true, WARNING_COLOR);
						}
					}
				}
			}
		}catch (Exception e){
			// do nothing
		}
	}
}
