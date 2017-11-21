package main.java;

import java.util.ResourceBundle;

/**
 * Created by lixiaoyao on 2017/11/10.
 */
public class ResourceToolProperties extends main.java.Properties {
	public static final String RESOURCE_TOOL = "main/ResourceTool";

	public static final String getString(String key) {
		return getString(RESOURCE_TOOL, key);
	}

	public static final String getString(String baseName, String key) {
		String result = "";

		ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, getLocale());
		if (resourceBundle != null) {
			try {
				result = new String(resourceBundle.getString(key).getBytes("ISO8859-1"), "GBK");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
