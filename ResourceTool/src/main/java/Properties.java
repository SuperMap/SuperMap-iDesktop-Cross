package main.java;

import java.util.Locale;

/**
 * Created by lixiaoyao on 2017/11/10.
 */
public abstract class Properties {
	private static Locale locale = Locale.getDefault();

	public static final Locale getLocale() {
		return locale;
	}

	public static void setLocale(String language, String country) {
		locale = new Locale(language, country);
	}
}
