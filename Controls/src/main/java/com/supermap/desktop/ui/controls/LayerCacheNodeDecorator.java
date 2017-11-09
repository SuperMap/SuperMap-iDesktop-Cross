package com.supermap.desktop.ui.controls;

import com.supermap.mapping.LayerCache;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by ChenS on 2017/11/7 0007.
 */
public class LayerCacheNodeDecorator implements TreeNodeDecorator {
    public void decorate(JLabel label, TreeNodeData data) {
        if (data.getType().equals(NodeDataType.LAYER_CACHE)) {
            LayerCache layerCache = (LayerCache) data.getData();
            label.setText(layerCache.getCaption());

            ImageIcon icon = (ImageIcon) label.getIcon();
            BufferedImage bufferedImage = new BufferedImage(
                    IMAGEICON_WIDTH, IMAGEICON_HEIGHT,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = bufferedImage.getGraphics();
            graphics.drawImage(
                    InternalImageIconFactory.LAYER_CACHE.getImage(), 0,
                    0, label);
            icon.setImage(bufferedImage);
        }
    }
}
