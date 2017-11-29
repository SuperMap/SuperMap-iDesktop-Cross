import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/24
 * Time: 11:18
 * Description:
 */
public class ConfigsTranslate implements ITranslateFile {

	private static LinkedHashMap<String, ConfigFile> newLatestPropertiesFile = new LinkedHashMap<String, ConfigFile>();

	@Override
	public void readFileDirectory(File file) {
		File[] files = file.listFiles();
		for (File a : files) {
			if (a.isFile() && !a.isDirectory()) {
				String absolutePath = a.getAbsolutePath();
				String fileType = absolutePath.substring(absolutePath.lastIndexOf(".") + 1);
				fileType = fileType.toLowerCase();
				if (fileType.equals(TranslateManager.CONFIG_SUFFIX_NAME)) {
					try {
						ConfigFile configFile=new ConfigFile();
						configFile.setChineseFilePath(absolutePath);
						configFile.setChineseDocument(ConfigFileUtilities.readConfigFile(absolutePath));
						String englishPath=absolutePath.replace("Default",SystemFileUtilities.getEnglishConfigFilename());
						configFile.setChineseDocument(ConfigFileUtilities.readConfigFile(absolutePath));
						configFile.setEnglishDocument(ConfigFileUtilities.readConfigFile(englishPath));
						newLatestPropertiesFile.put(a.getName(),configFile);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (a.isDirectory()) {
				readFileDirectory(a);
			}
		}
	}

	@Override
	public void removeIllegalFiles() {
		// ignore
	}

	@Override
	public void initKeyValues() {
		//ignore
	}

	@Override
	public void findUnTranslatedKey() {
		try {
			for (String bundleName : newLatestPropertiesFile.keySet()) {
				ArrayList<Element> allNodes = newLatestPropertiesFile.get(bundleName).getChineseAllNodes();
				for (Element node : allNodes) {
					List<Attribute> listAttr = node.attributes();
					for (Attribute attr : listAttr) {
						if (SystemFileUtilities.getAttributesType().contains(attr.getName()) &&
								hasFullChar(attr.getValue().trim())) {
							if (attr.getValue().trim().equals("")){
								continue;
							}
							KeyValue keyValue = new KeyValue();
							keyValue.setKey(attr.getValue().trim());
							keyValue.setModify(false);
							keyValue.setEnglishValue("");
							SystemFileUtilities.addKeyValue(keyValue);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isNeedAutoTranslated() {
		boolean result = true;
//		try {
//			needAutoTranslateFiles.clear();
//			for (String configName : newLatestPropertiesFile.keySet()) {
//				ArrayList<Element> chineseNodes=newLatestPropertiesFile.get(configName).getChineseAllNodes();
//				ArrayList<Element> englishNodes=newLatestPropertiesFile.get(configName).getEnglishAllNodes();
//
//				if (chineseNodes.size() != englishNodes.size()) {
//					needAutoTranslateFiles.add(configName);
//					continue;
//				}
//				for (Element node : chineseNodes) {
//					List<Attribute> listAttr = node.attributes();
//					for (Attribute attr : listAttr) {
//						if (SystemFileUtilities.getAttributesType().contains(attr.getName()) &&
//								hasFullChar(attr.getValue().trim())) {
//
//						}
//					}
//				}
//				for (String commonKey : latestChineseContent.keySet()) {
//					String chineseValue = unicodeToString(latestChineseContent.get(commonKey));
//					if (latestEnglishContent.containsKey(commonKey)) {
//						if (SystemFileUtilities.getAllKeyValue().get(chineseValue).isModify()) {
//							if (!SystemFileUtilities.getAllKeyValue().get(chineseValue).getEnglishValue().equals(latestEnglishContent.get(commonKey))) {
//								needAutoTranslateFiles.add(configName);
//								break;
//							}
//						}
//					} else {
//						needAutoTranslateFiles.add(configName);
//						break;
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		if (needAutoTranslateFiles.size() > 0) {
//			result = true;
//		}
		return result;
	}

	@Override
	public boolean autoTranslated() {
		boolean result = true;
		try {
			for (String bundleName : newLatestPropertiesFile.keySet()) {
				ArrayList<Element> allNodes = newLatestPropertiesFile.get(bundleName).getChineseAllNodes();
				for (Element node : allNodes) {
					List<Attribute> listAttr = node.attributes();
					for (Attribute attr : listAttr) {
						String chineseValue=attr.getValue().trim();
						if (SystemFileUtilities.getAttributesType().contains(attr.getName()) &&
								hasFullChar(chineseValue) && SystemFileUtilities.getAllKeyValue().get(chineseValue).isModify()) {
							attr.setValue(SystemFileUtilities.getAllKeyValue().get(chineseValue).getEnglishValue());
						}
					}
				}
				String englishPath=newLatestPropertiesFile.get(bundleName).getChineseFilePath().replace("Default",SystemFileUtilities.getEnglishConfigFilename());
				ConfigFileUtilities.saveDocument(newLatestPropertiesFile.get(bundleName).getChineseDocument(),englishPath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean hasFullChar(String str) {
		if (str.getBytes().length == str.length()) {
			return false;
		}
		return true;
	}

	private class ConfigFile {
		private Document chineseDocument = null;
		private ArrayList<Element> chineseAllNodes = null;
		private Document englishDocument = null;
		private ArrayList<Element> englishAllNodes = null;
		private String chineseFilePath="";

		public ConfigFile() {
			chineseAllNodes = new ArrayList<Element>();
			englishAllNodes=new ArrayList<Element>();
		}

		private void getChineseNodes(Element node) {
			try {
				chineseAllNodes.add(node);
				//递归遍历当前节点所有的子节点
				List<Element> listElement = node.elements();//所有一级子节点的list
				for (Element e : listElement) {//遍历所有一级子节点
					getChineseNodes(e);//递归
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void getEnglishNodes(Element node) {
			try {
				englishAllNodes.add(node);
				//递归遍历当前节点所有的子节点
				List<Element> listElement = node.elements();//所有一级子节点的list
				for (Element e : listElement) {//遍历所有一级子节点
					getEnglishNodes(e);//递归
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public Document getChineseDocument() {
			return chineseDocument;
		}

		public void setChineseDocument(Document chineseDocument) {
			this.chineseDocument = chineseDocument;
			getChineseNodes(this.chineseDocument.getRootElement());

		}

		public ArrayList<Element> getChineseAllNodes() {
			return chineseAllNodes;
		}

		public void setChineseAllNodes(ArrayList<Element> chineseAllNodes) {
			this.chineseAllNodes = chineseAllNodes;
		}

		public Document getEnglishDocument() {
			return englishDocument;
		}

		public void setEnglishDocument(Document englishDocument) {
			this.englishDocument = englishDocument;
			getEnglishNodes(this.englishDocument.getRootElement());
		}

		public ArrayList<Element> getEnglishAllNodes() {
			return englishAllNodes;
		}

		public void setEnglishAllNodes(ArrayList<Element> englishAllNodes) {
			this.englishAllNodes = englishAllNodes;
		}

		public String getChineseFilePath() {
			return chineseFilePath;
		}

		public void setChineseFilePath(String chineseFilePath) {
			this.chineseFilePath = chineseFilePath;
		}
	}

}
