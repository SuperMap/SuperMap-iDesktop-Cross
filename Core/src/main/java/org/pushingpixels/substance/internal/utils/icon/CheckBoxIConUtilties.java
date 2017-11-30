package org.pushingpixels.substance.internal.utils.icon;

import org.pushingpixels.substance.internal.ui.SubstanceCheckBoxUI;

import javax.swing.*;

/**
 * @author XiaJT
 */
public class CheckBoxIConUtilties {

	private static JCheckBox jCheckBox;

	public static final Icon getCheckBoxSelectedICon() {
		JCheckBox jCheckBox = getCheckBox();
		return ((SubstanceCheckBoxUI) jCheckBox.getUI()).getDefaultIcon();
	}

	private static JCheckBox getCheckBox() {
		if (jCheckBox == null) {
			jCheckBox = new JCheckBox();
			jCheckBox.setSelected(true);
		}
		return jCheckBox;
	}
}
