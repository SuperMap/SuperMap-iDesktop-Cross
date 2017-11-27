
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/21
 * Time: 15:14
 * Description:ConfigTool Bundle's properties
 */
public class ConfigToolProperties extends Properties {
	public static final String RESOURCE_TOOL = "ConfigTool";

	public static final String getString(String key) {
		return getString(RESOURCE_TOOL, key);
	}

	public static final String getString(String baseName, String key) {
		String result = "";

		ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, getLocale());
		if (resourceBundle != null) {
			try {
				result=resourceBundle.getString(key);
//				result = new String(resourceBundle.getString(key).getBytes("ISO8859-1"), "GBK");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
