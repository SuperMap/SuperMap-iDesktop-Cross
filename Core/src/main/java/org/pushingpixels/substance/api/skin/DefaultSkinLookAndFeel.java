package org.pushingpixels.substance.api.skin;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;

/**
 * @author XiaJT
 */
public class DefaultSkinLookAndFeel extends SubstanceLookAndFeel {

	/**
	 * Creates a new skin-based Substance look-and-feel. This is the only way to
	 * create an instance of {@link SubstanceLookAndFeel} class.
	 */
	public DefaultSkinLookAndFeel() {
		super(new DefaultSkin());
	}
}
