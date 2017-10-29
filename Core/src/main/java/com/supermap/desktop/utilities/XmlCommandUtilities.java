package com.supermap.desktop.utilities;

import com.supermap.desktop.ui.XMLCommand;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import sun.awt.image.ToolkitImage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author XiaJT
 */
public class XmlCommandUtilities {
	private static final Dimension DEFAULT_ICON_SIZE = new Dimension(23, 23);

	public static Image getXmlCommandImage(XMLCommand xmlCommand) {
		String[] pathPrams = new String[]{PathUtilities.getRootPathName(), xmlCommand.getImageFile()};
		String path = PathUtilities.combinePath(pathPrams, false);
		File file = new File(path);
		Image image = null;
		if (file.exists()) {
			image = new ImageIcon(path).getImage();
		}
		return image;
	}

	public static Dimension getDefaultIconSize() {
		return DEFAULT_ICON_SIZE;
	}

	public static ImageWrapperResizableIcon getICon(Image image) {
		Dimension dimension = getDefaultIconSize();
		if (image instanceof BufferedImage) {
			dimension = new Dimension(((BufferedImage) image).getWidth(), ((BufferedImage) image).getHeight());
		} else if (image instanceof ToolkitImage) {
			dimension = new Dimension(((ToolkitImage) image).getWidth(), ((ToolkitImage) image).getHeight());
		}
		return image == null ? null : ImageWrapperResizableIcon.getIcon(image, dimension);

	}
}
