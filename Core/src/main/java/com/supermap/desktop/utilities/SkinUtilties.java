package com.supermap.desktop.utilities;


import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;

/**
 * @author XiaJT
 */
public class SkinUtilties {
	private SkinUtilties() {

	}

	public static void setSkin(SubstanceSkin skin) {
		SubstanceLookAndFeel.setSkin(skin);
	}

	public static void setSkin(String skin) {
		SubstanceLookAndFeel.setSkin(skin);
	}
}
