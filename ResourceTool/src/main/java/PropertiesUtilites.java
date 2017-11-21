package main.java;

import com.sun.org.apache.bcel.internal.generic.NEW;

import javax.jnlp.FileSaveService;
import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by lixiaoyao on 2017/11/9.
 */

public class PropertiesUtilites {

	private static String preVersionPath = "ResourceTool/src/main/properties";
	public static String processResultPath = "ResourceTool/src/main/result";
	private static boolean isUpdate = false;//判断更新文件以及读取上个版本文件，比较词条是否处理完毕，
	private static ArrayList<String> filesType = new ArrayList<>();
	private static LinkedHashMap<String, BundlePropertiesFile> newLatestPropertiesFile = new LinkedHashMap<>();
	private static LinkedHashMap<String, BundlePropertiesFile> oldPropertiesFile = new LinkedHashMap<>();
	private static final String SUFFIX_NAME = ".properties";
	public static ArrayList<KeyValue> allKeyValue = new ArrayList<>();
	private static final String UNPROCESS_FILE_PATH = "ResourceTool/src/main/system/unprocess.txt";
	private static final String ALL_FILES_PATH = "ResourceTool/src/main/system/filePath.txt";
	public static final LinkedHashMap<String, ArrayList<String>> modifyTypeAndFiles = new LinkedHashMap<>();
	private static LinkedHashMap<String, String> filesPath = new LinkedHashMap<>();

	public static boolean isUpdate() {
		return isUpdate;
	}

	public static void clearParameter() {
		newLatestPropertiesFile.clear();
		filesType.clear();
	}

	public static void addAllPropertiesFile(File file) {
		String currentFileType = file.getName();
		for (int i = 0; i < filesType.size(); i++) {
			if (currentFileType.indexOf(filesType.get(i)) != -1) {
				currentFileType = file.getName().replaceAll("_" + filesType.get(i), "");
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

	private static void removeIllegalPropertiesFile() {
		ArrayList<String> removeKeys = new ArrayList<>();
		for (BundlePropertiesFile bundlePropertiesFile : newLatestPropertiesFile.values()) {
			if (!bundlePropertiesFile.isIllegalBundlePropertiesFile()) {
				removeKeys.add(bundlePropertiesFile.getDefaultPropertiesFileName());
			}
		}
		for (int j = 0; j < removeKeys.size(); j++) {
			newLatestPropertiesFile.remove(removeKeys.get(j));
		}
	}

	public static void addFileType(String fileType) {
		filesType.add(fileType);
	}

	public static ArrayList<String> getFileType() {
		return filesType;
	}

	/*
	读取原来的文件时，只考虑文件名称跟最新的文件名称相同的文件，如果最新的某些资源文件被删除，那么就不需要加载进来
	，只需要加载同名的文件，至于被删除的文件下次更新时会全部删除的，不用处理
	 */
	public static void readOldPropertiesFile() {
		oldPropertiesFile.clear();
		for (String bundlePropertiesName : newLatestPropertiesFile.keySet()) {
			BundlePropertiesFile bundlePropertiesFile = new BundlePropertiesFile(bundlePropertiesName);
			oldPropertiesFile.put(bundlePropertiesName, bundlePropertiesFile);
		}
		File file = new File(preVersionPath);
		File[] files = file.listFiles();
		for (File tempFile : files) {
			String currentFileType = tempFile.getName();
			for (int i = 0; i < filesType.size(); i++) {
				if (currentFileType.indexOf(filesType.get(i)) != -1) {
					currentFileType = currentFileType.replaceAll("_" + filesType.get(i), "");
					break;
				}
			}
			currentFileType = currentFileType.substring(0, currentFileType.lastIndexOf("."));
			if (oldPropertiesFile.containsKey(currentFileType)) {
				oldPropertiesFile.get(currentFileType).addPropertiesFile(tempFile);
			}
		}
		for (BundlePropertiesFile bundlePropertiesFile : oldPropertiesFile.values()) {
			bundlePropertiesFile.initFileState();
		}
		compareFile();
		saveUnProcessKeyValue();
	}

	/*
	将properties文件夹下的所有文本拷贝到result文件夹，之后再从工程中拷贝文件到properties文件夹。
	如果第一次运行即，properties文件夹为空，那么就先从工程中拷贝文件到properties文件夹，再把properties文件夹
	中所有内容复制到result文件夹
	 */
	public static void moveFile() {
		removeIllegalPropertiesFile();
		filesPath.clear();
		try {
			File file = new File(processResultPath);
			if (file.listFiles().length != 0) {
				FileUtilites.deleteDir(new File(preVersionPath));
				FileUtilites.createDir(preVersionPath);
				FileUtilites.IOCopy(processResultPath, preVersionPath);
				FileUtilites.deleteDir(file);
				FileUtilites.createDir(processResultPath);
				for (BundlePropertiesFile bundlePropertiesFile : newLatestPropertiesFile.values()) {
					for (FileState fileState : bundlePropertiesFile.values()) {
						filesPath.put(fileState.getFileName(), fileState.getFilePath());
						FileUtilites.outPutToFile(fileState.getContent(), processResultPath + "/" + fileState.getFileName());
					}
				}
			} else {
				for (BundlePropertiesFile bundlePropertiesFile : newLatestPropertiesFile.values()) {
					for (FileState fileState : bundlePropertiesFile.values()) {
						filesPath.put(fileState.getFileName(), fileState.getFilePath());
						FileUtilites.outPutToFile(fileState.getContent(), processResultPath + "/" + fileState.getFileName());
					}
				}
				FileUtilites.IOCopy(processResultPath, preVersionPath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		readOldPropertiesFile();
		saveFilePath();
		isUpdate = true;
	}

	public static String getSuffixName() {
		return SUFFIX_NAME;
	}

	public static void initFilePath() {
		filesPath.clear();
		File file = new File(ALL_FILES_PATH);
		ArrayList<String> result = FileUtilites.readFileContent(file);
		for (int i = 0; i < result.size(); i++) {
			String[] content = result.get(i).split("\t");
			if (content.length < 2) {
				continue;
			}
			filesPath.put(content[0], content[1]);
		}
	}

	private static void compareFile() {
		initModifyTypeAndFiles();
		allKeyValue.clear();
		for (String name : newLatestPropertiesFile.keySet()) {
			findNewAdd(newLatestPropertiesFile.get(name), oldPropertiesFile.get(name));
			findUntranslated(newLatestPropertiesFile.get(name));
			findModify(newLatestPropertiesFile.get(name), oldPropertiesFile.get(name));
		}
	}

	private static void findNewAdd(BundlePropertiesFile latestBPF, BundlePropertiesFile oldBPF) {
		boolean isFind = false;
		String currentFileName = latestBPF.getFileState(PropertiesFileType.CHINESE).getFileName();
		LinkedHashMap<String, String> latestContent = arrayListToMap(latestBPF.getFileState(PropertiesFileType.CHINESE).getContent());
		LinkedHashMap<String, String> oldContent = arrayListToMap(oldBPF.getFileState(PropertiesFileType.CHINESE).getContent());
		for (String sourceKey : latestContent.keySet()) {
			if (!oldContent.containsKey(sourceKey)) {
				if (!isFind) {
					modifyTypeAndFiles.get(ModifyType.NEW_ADD).add(currentFileName);
					isFind = true;
				}
				KeyValue keyValue = new KeyValue();
				keyValue.setFileName(currentFileName);
				keyValue.setKey(sourceKey);
				keyValue.setCurrentValue(latestContent.get(sourceKey));
				keyValue.setModifyType(ModifyType.NEW_ADD);
				keyValue.setPreValue("");
				allKeyValue.add(keyValue);
			}
		}
	}

	private static void findUntranslated(BundlePropertiesFile latestBPF) {
		boolean isFind = false;
		String currentFileName = latestBPF.getFileState(PropertiesFileType.CHINESE).getFileName();
		LinkedHashMap<String, String> latestChineseContent = arrayListToMap(latestBPF.getFileState(PropertiesFileType.CHINESE).getContent());
		LinkedHashMap<String, String> latestEnglishContent = arrayListToMap(latestBPF.getFileState(PropertiesFileType.ENGLISH).getContent());
		for (String chineseKey : latestChineseContent.keySet()) {
			if (!latestEnglishContent.containsKey(chineseKey)) {
				boolean isOfUntranslated = true;
				for (int i = 0; i < allKeyValue.size(); i++) {
					if (allKeyValue.get(i).getModifyType().equals(ModifyType.NEW_ADD)
							&& allKeyValue.get(i).getFileName().equals(currentFileName) && allKeyValue.get(i).getKey().equals(chineseKey)) {
						isOfUntranslated = false;
						break;
					}
				}
				if (isOfUntranslated) {
					if (!isFind) {
						modifyTypeAndFiles.get(ModifyType.UNTRANSLATED).add(currentFileName);
						isFind = true;
					}
					KeyValue keyValue = new KeyValue();
					keyValue.setFileName(currentFileName);
					keyValue.setKey(chineseKey);
					keyValue.setCurrentValue(latestChineseContent.get(chineseKey));
					keyValue.setModifyType(ModifyType.UNTRANSLATED);
					keyValue.setPreValue("");
					allKeyValue.add(keyValue);
				}
			}
		}
	}

	private static void findModify(BundlePropertiesFile latestBPF, BundlePropertiesFile oldBPF) {
		boolean isFind = false;
		String currentFileName = latestBPF.getFileState(PropertiesFileType.CHINESE).getFileName();
		LinkedHashMap<String, String> latestContent = arrayListToMap(latestBPF.getFileState(PropertiesFileType.CHINESE).getContent());
		LinkedHashMap<String, String> oldContent = arrayListToMap(oldBPF.getFileState(PropertiesFileType.CHINESE).getContent());
		LinkedHashMap<String, String> latestEnglishContent = arrayListToMap(latestBPF.getFileState(PropertiesFileType.ENGLISH).getContent());
		for (String sourceKey : latestContent.keySet()) {
			if (oldContent.containsKey(sourceKey) && !latestContent.get(sourceKey).equals(oldContent.get(sourceKey)) && latestEnglishContent.containsKey(sourceKey)) {
				if (!isFind) {
					modifyTypeAndFiles.get(ModifyType.MODIFY).add(currentFileName);
					isFind = true;
				}
				KeyValue keyValue = new KeyValue();
				keyValue.setFileName(currentFileName);
				keyValue.setKey(sourceKey);
				keyValue.setCurrentValue(latestContent.get(sourceKey));
				keyValue.setModifyType(ModifyType.MODIFY);
				keyValue.setPreValue(oldContent.get(sourceKey));
				allKeyValue.add(keyValue);
			}
		}
	}

	public static void saveUnProcessKeyValue() {
		try {
			File file = new File(UNPROCESS_FILE_PATH);
			file.createNewFile();
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			BufferedWriter writer = new BufferedWriter(outputStreamWriter);
			for (int i = 0; i < allKeyValue.size(); i++) {
				KeyValue temp = allKeyValue.get(i);
				if (!temp.isModify()) {
					writer.write(temp.getFileName() + "\t" + temp.getKey() + "\t" +
							temp.getModifyType() + "\t" + temp.getCurrentValue() + "\t" + temp.getPreValue());
					writer.write("\r\n");
				}
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean readUnProcessKeyValue() {
		boolean isShowWarning = false;
		initModifyTypeAndFiles();
		allKeyValue.clear();
		try {
			File file = new File(UNPROCESS_FILE_PATH);
			ArrayList<String> result = FileUtilites.readFileContent(file);
			for (int i = 0; i < result.size(); i++) {
				String[] content = result.get(i).split("\t");
				if (content.length < 4) {
					continue;
				}
				KeyValue keyValue = new KeyValue();
				keyValue.setFileName(content[0]);
				keyValue.setKey(content[1]);
				keyValue.setModifyType(content[2]);
				keyValue.setCurrentValue(content[3]);
				if (keyValue.getModifyType().equals(ModifyType.MODIFY)) {
					keyValue.setPreValue(content[4]);
				}
				if (!modifyTypeAndFiles.get(keyValue.getModifyType()).contains(keyValue.getFileName())) {
					modifyTypeAndFiles.get(keyValue.getModifyType()).add(keyValue.getFileName());
				}
				allKeyValue.add(keyValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (allKeyValue.size() == 0) {
			isShowWarning = true;
		}
		return isShowWarning;
	}

	public static boolean commitLocal() {
		boolean result = true;
		LinkedHashMap<String, ArrayList<String>> changedFileAndContent = new LinkedHashMap<>();
		try {
			for (int i = 0; i < allKeyValue.size(); i++) {
				KeyValue temp = allKeyValue.get(i);
				String changeFileName = temp.getFileName().replace("zh_CN", "en_US");
				if (temp.isModify()) {
					if (!changedFileAndContent.containsKey(changeFileName)) {
						changedFileAndContent.put(changeFileName, FileUtilites.readFileContent(new File(processResultPath + "/" + changeFileName)));
					}
					ArrayList<String> tempContent = changedFileAndContent.get(changeFileName);
					if (temp.getModifyType().equals(ModifyType.NEW_ADD) || temp.getModifyType().equals(ModifyType.UNTRANSLATED)) {
						tempContent.add(temp.getKey() + "=" + temp.getAfterModifyValue());
					} else if (temp.getModifyType().equals(ModifyType.MODIFY)) {
						for (int j = 0; j < tempContent.size(); j++) {
							if (tempContent.get(j) != "" && tempContent.get(j).indexOf("=") != -1) {
								String tempKey = tempContent.get(j).substring(0, tempContent.get(j).indexOf("="));
								if (tempKey.equals(temp.getKey())) {
									tempContent.set(j, tempKey + "=" + temp.getAfterModifyValue());
									break;
								}
							}
						}
					}
				}
			}

			for (String fileName : changedFileAndContent.keySet()) {
				File resultFile = new File(processResultPath + "/" + fileName);
				resultFile.createNewFile();
				FileUtilites.outPutToFile(changedFileAndContent.get(fileName), resultFile);
				File projectFile = new File(filesPath.get(fileName));
				projectFile.createNewFile();
				FileUtilites.outPutToFile(changedFileAndContent.get(fileName), projectFile);
			}
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
		saveUnProcessKeyValue();
		readUnProcessKeyValue();
		return result;
	}

	private static void saveFilePath() {
		try {
			File file = new File(ALL_FILES_PATH);
			file.createNewFile();
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			BufferedWriter writer = new BufferedWriter(outputStreamWriter);
			for (String fileName : filesPath.keySet()) {
				writer.write(fileName + "\t" + filesPath.get(fileName));
				writer.write("\r\n");
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static LinkedHashMap<String, String> arrayListToMap(ArrayList<String> content) {
		LinkedHashMap<String, String> result = new LinkedHashMap<>();
		for (int i = 0; i < content.size(); i++) {
			if (content.get(i) != "" && content.get(i).indexOf("=") != -1) {
				String tempKey = content.get(i).substring(0, content.get(i).indexOf("="));
				String tempValue = content.get(i).substring(content.get(i).indexOf("=") + 1);
				result.put(tempKey, tempValue);
			}
		}
		return result;
	}

	private static void initModifyTypeAndFiles() {
		modifyTypeAndFiles.clear();
		modifyTypeAndFiles.put(ModifyType.MODIFY, new ArrayList<String>());
		modifyTypeAndFiles.put(ModifyType.NEW_ADD, new ArrayList<String>());
		modifyTypeAndFiles.put(ModifyType.UNTRANSLATED, new ArrayList<String>());
	}

}
