import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/23
 * Time: 10:50
 * Description:
 */
public class TranslateManager {
	public static final String SUFFIX_NAME = ".properties";
	public static final String CONFIG_SUFFIX_NAME = "config";
	private static ITranslateFile propertiesManager = new PropertiesTranslate();
	private static ITranslateFile configManager = new ConfigsTranslate();

	public static void run() {
		SystemFileUtilities.initSystemSetting();
		String rootPath = SystemFileUtilities.getSystemFilePath().replace("\\", "/");
		propertiesManager.readFileDirectory(new File(rootPath));
		configManager.readFileDirectory(new File(rootPath + SystemFileUtilities.getChineseConfigPath()));
		propertiesManager.removeIllegalFiles();
		configManager.removeIllegalFiles();
		if (SystemFileUtilities.isFirstRun()) {
			propertiesManager.initKeyValues();
			configManager.initKeyValues();
			SystemFileUtilities.saveKeyValue();
		}

		propertiesManager.findUnTranslatedKey();
		configManager.findUnTranslatedKey();
	}

	public static boolean isNeedAutoTranslated() {
		return propertiesManager.isNeedAutoTranslated() && configManager.isNeedAutoTranslated();
	}

	public static boolean autoTranslate() {
		if (SystemFileUtilities.getUnTranslateKeyCount() != 0) {
			propertiesManager.isNeedAutoTranslated();
			configManager.isNeedAutoTranslated();
			SystemFileUtilities.saveKeyValue();
		}
		return propertiesManager.autoTranslated() && configManager.autoTranslated();
	}

}
