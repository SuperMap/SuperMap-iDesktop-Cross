import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/23
 * Time: 11:31
 * Description:
 */

public class PropertiesTranslate implements ITranslateFile {
	private static LinkedHashMap<String, BundlePropertiesFile> newLatestPropertiesFile = new LinkedHashMap<String, BundlePropertiesFile>();
	private static ArrayList<String> needAutoTranslateFiles = new ArrayList<String>();

	@Override
	public void readFileDirectory(File file) {
		File[] files = file.listFiles();
		for (File a : files) {
			if (a.isFile() && !a.isDirectory()) {
				String absolutePath = a.getAbsolutePath();
				String fileType = absolutePath.substring(absolutePath.lastIndexOf(".") + 1);
				fileType = fileType.toLowerCase();
				if (fileType.equals("properties") && a.getAbsolutePath().indexOf("target") == -1) {
					addAllPropertiesFile(a);
				}
			}
			if (a.isDirectory() && !a.getName().equals("Tools")) {
				readFileDirectory(a);
			}
		}
	}

	@Override
	public void removeIllegalFiles() {
		ArrayList<String> removeKeys = new ArrayList<String>();
		for (BundlePropertiesFile bundlePropertiesFile : newLatestPropertiesFile.values()) {
			if (!bundlePropertiesFile.isIllegalBundlePropertiesFile()) {
				removeKeys.add(bundlePropertiesFile.getDefaultPropertiesFileName());
			}
		}
		for (int j = 0; j < removeKeys.size(); j++) {
			newLatestPropertiesFile.remove(removeKeys.get(j));
		}
	}

	@Override
	public void initKeyValues() {
		for (String bundleName : newLatestPropertiesFile.keySet()) {
			findTranslated(newLatestPropertiesFile.get(bundleName));
		}
	}

	@Override
	public void findUnTranslatedKey() {
		try {
			for (String bundleName : newLatestPropertiesFile.keySet()) {
				LinkedHashMap<String, String> latestChineseContent = arrayListToMap(newLatestPropertiesFile.get(bundleName).getFileState(PropertiesFileType.CHINESE).getContent());
				for (String commonKey : latestChineseContent.keySet()) {
					String chineseValue = unicodeToString(latestChineseContent.get(commonKey));
					if (!SystemFileUtilities.getAllKeyValue().containsKey(chineseValue)) {
						KeyValue keyValue = new KeyValue();
						keyValue.setKey(chineseValue);
						keyValue.setModify(false);
						keyValue.setEnglishValue("");
						SystemFileUtilities.addKeyValue(keyValue);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isNeedAutoTranslated() {
		boolean result = false;
		try {
			needAutoTranslateFiles.clear();
			for (String bundleName : newLatestPropertiesFile.keySet()) {
				LinkedHashMap<String, String> latestChineseContent = arrayListToMap(newLatestPropertiesFile.get(bundleName).getFileState(PropertiesFileType.CHINESE).getContent());
				LinkedHashMap<String, String> latestEnglishContent = arrayListToMap(newLatestPropertiesFile.get(bundleName).getFileState(PropertiesFileType.ENGLISH).getContent());
				if (latestChineseContent.size() != latestEnglishContent.size()) {
					needAutoTranslateFiles.add(bundleName);
					continue;
				}
				for (String commonKey : latestChineseContent.keySet()) {
					String chineseValue = unicodeToString(latestChineseContent.get(commonKey));
					if (latestEnglishContent.containsKey(commonKey)) {
						if (SystemFileUtilities.getAllKeyValue().get(chineseValue).isModify()) {
							if (!SystemFileUtilities.getAllKeyValue().get(chineseValue).getEnglishValue().equals(latestEnglishContent.get(commonKey))) {
								needAutoTranslateFiles.add(bundleName);
								break;
							}
						}
					} else {
						needAutoTranslateFiles.add(bundleName);
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (needAutoTranslateFiles.size() > 0) {
			result = true;
		}
		return result;
	}

	@Override
	public boolean autoTranslated() {
		boolean result = true;
		try {
			for (String bundleName : needAutoTranslateFiles) {
				LinkedHashMap<String, String> latestChineseContent = arrayListToMap(newLatestPropertiesFile.get(bundleName).getFileState(PropertiesFileType.CHINESE).getContent());
				String englishFilePath = newLatestPropertiesFile.get(bundleName).getFileState(PropertiesFileType.ENGLISH).getFilePath();
				ArrayList<String> englishContent = new ArrayList<String>();
				for (String commonKey : latestChineseContent.keySet()) {
					String chineseValue = unicodeToString(latestChineseContent.get(commonKey));
					String englishValue = "";
					if ( !SystemFileUtilities.getAllKeyValue().get(chineseValue).isModify()){
						continue;
					}
					try {
						englishValue = SystemFileUtilities.getAllKeyValue().get(chineseValue).getEnglishValue();
					} catch (Exception e) {
						e.printStackTrace();
					}
					englishContent.add(commonKey + "=" + englishValue);
				}
				FileUtilites.outPutToFile(englishContent, englishFilePath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private void addAllPropertiesFile(File file) {
		String currentFileType = file.getName();
		for (int i = 0; i < SystemFileUtilities.getPropertiesType().size(); i++) {
			if (currentFileType.indexOf(SystemFileUtilities.getPropertiesType().get(i)) != -1) {
				currentFileType = file.getName().replaceAll("_" + SystemFileUtilities.getPropertiesType().get(i), "");
				break;
			}
		}
		currentFileType = currentFileType.substring(0, currentFileType.lastIndexOf("."));
		if (newLatestPropertiesFile.containsKey(currentFileType)) {
			newLatestPropertiesFile.get(currentFileType).addPropertiesFile(file);
		} else {
			BundlePropertiesFile bundlePropertiesFile = new BundlePropertiesFile(currentFileType);
			bundlePropertiesFile.addPropertiesFile(file);
			newLatestPropertiesFile.put(currentFileType, bundlePropertiesFile);
		}
	}

	private void findTranslated(BundlePropertiesFile latestBPF) {
		try {
			LinkedHashMap<String, String> latestChineseContent = arrayListToMap(latestBPF.getFileState(PropertiesFileType.CHINESE).getContent());
			LinkedHashMap<String, String> latestEnglishContent = arrayListToMap(latestBPF.getFileState(PropertiesFileType.ENGLISH).getContent());
			for (String commonKey : latestChineseContent.keySet()) {
				String chineseValue = unicodeToString(latestChineseContent.get(commonKey));
				if (latestEnglishContent.containsKey(commonKey) && !SystemFileUtilities.getAllKeyValue().containsKey(chineseValue)) {
					KeyValue keyValue = new KeyValue();
					keyValue.setKey(chineseValue);
					keyValue.setModify(true);
					keyValue.setEnglishValue(latestEnglishContent.get(commonKey));
					SystemFileUtilities.addKeyValue(keyValue);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private LinkedHashMap<String, String> arrayListToMap(ArrayList<String> content) {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		for (int i = 0; i < content.size(); i++) {
			if (content.get(i) != "" && content.get(i).indexOf("=") != -1) {
				String tempKey = content.get(i).substring(0, content.get(i).indexOf("="));
				String tempValue = content.get(i).substring(content.get(i).indexOf("=") + 1);
				result.put(tempKey, tempValue);
			}
		}
		return result;
	}

	private String unicodeToString(String str) {
		Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
		Matcher matcher = pattern.matcher(str);
		char ch;
		while (matcher.find()) {
			ch = (char) Integer.parseInt(matcher.group(2), 16);
			str = str.replace(matcher.group(1), ch + "");
		}
		return str.trim();
	}

}
