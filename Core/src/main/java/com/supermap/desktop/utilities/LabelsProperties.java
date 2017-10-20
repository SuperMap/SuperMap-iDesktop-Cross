package com.supermap.desktop.utilities;

import com.supermap.desktop.properties.Properties;

import java.util.ResourceBundle;

/**
 * @author XiaJT
 */
public class LabelsProperties extends Properties {
	public static final String LABELS = "Labels";

	public static final String getString(String key) {
		return getString(LABELS, key);
	}

	public static final String getString(String baseName, String key) {
		String result = "";

		ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, getLocale());
		if (resourceBundle != null) {
			result = resourceBundle.getString(key);
		}
		return result;
	}
}
