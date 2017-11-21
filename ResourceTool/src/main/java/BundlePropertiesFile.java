package main.java;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Created by lixiaoyao on 2017/11/10.
 */
public class BundlePropertiesFile {

	private String defaultPropertiesFileName = "";
	private String allPropertiesFileNames = "";
	private ArrayList<File> allPropertiesFile = new ArrayList<>();
	private LinkedHashMap<Integer,FileState> fileStateLinkedHashMap=new LinkedHashMap<>();

	public BundlePropertiesFile(String defaultPropertiesFileName) {
		this.defaultPropertiesFileName = defaultPropertiesFileName;
	}

	public void addPropertiesFile(File file) {
		this.allPropertiesFileNames = this.allPropertiesFileNames + file.getName();
		this.allPropertiesFile.add(file);
	}

	public boolean isIllegalBundlePropertiesFile() {
		boolean result = true;
		if (this.allPropertiesFile.size() != PropertiesUtilites.getFileType().size() + 1) {
			result = false;
			return result;
		} else {
			for (int j = 0; j < PropertiesUtilites.getFileType().size(); j++) {
				int index = this.allPropertiesFileNames.indexOf(this.defaultPropertiesFileName + "_" + PropertiesUtilites.getFileType().get(j) +
						PropertiesUtilites.getSuffixName());
				if (index == -1) {
					result = false;
					return result;
				}
			}
			if (this.allPropertiesFileNames.indexOf(this.defaultPropertiesFileName + PropertiesUtilites.getSuffixName()) == -1) {
				result = false;
				return result;
			}
		}
		initFileState();
		return result;
	}

	public void initFileState(){
		for (int i = 0; i <this.allPropertiesFile.size() ; i++) {
			FileState fileState=new FileState();
			fileState.setFileName(this.allPropertiesFile.get(i).getName());
			fileState.setFilePath(this.allPropertiesFile.get(i).getAbsolutePath());
			fileState.setContent(FileUtilites.readFileContent(this.allPropertiesFile.get(i)));
			this.fileStateLinkedHashMap.put(PropertiesFileType.getFileType(fileState.getFileName()),fileState);
		}
	}

	public String getDefaultPropertiesFileName() {
		return defaultPropertiesFileName;
	}

	public Collection<FileState> values(){
		return this.fileStateLinkedHashMap.values();
	}

	public FileState getFileState(Integer propertiesFileType){
		return this.fileStateLinkedHashMap.get(propertiesFileType);
	}
}
