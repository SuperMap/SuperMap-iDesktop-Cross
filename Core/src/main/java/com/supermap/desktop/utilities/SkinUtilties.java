package com.supermap.desktop.utilities;


import com.supermap.desktop.GlobalParameters;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;

/**
 * @author XiaJT
 */
public class SkinUtilties {
	private SkinUtilties() {

	}

	public static void setSkin(SubstanceSkin skin) {
		setSkin(skin.getClass().getName());
	}

	public static void setSkin(String skin) {
		if (StringUtilities.isNullOrEmpty(skin)) {
			return;
		}
		SubstanceLookAndFeel.setSkin(skin);
		if (!skin.equals(GlobalParameters.getSkin())) {
			GlobalParameters.setSkin(skin);
			GlobalParameters.save();
		}
	}
}
