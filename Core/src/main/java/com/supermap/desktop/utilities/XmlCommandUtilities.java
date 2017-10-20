package com.supermap.desktop.utilities;

import com.supermap.desktop.ui.XMLCommand;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * @author XiaJT
 */
public class XmlCommandUtilities {
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
}
