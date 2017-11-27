
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/21
 * Time: 15:14
 * Description:Common properties
 */
public class Properties {
	private static Locale locale = Locale.getDefault();

	public static final Locale getLocale() {
		return locale;
	}

	public static void setLocale(String language, String country) {
		locale = new Locale(language, country);
	}
}
