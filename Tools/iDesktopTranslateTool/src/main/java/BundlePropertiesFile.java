import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/23
 * Time: 10:53
 * Description:
 */
public class BundlePropertiesFile {

	private String defaultPropertiesFileName = "";
	private String allPropertiesFileNames = "";
	private ArrayList<File> allPropertiesFile = new ArrayList<File>();
	private LinkedHashMap<Integer,FileState> fileStateLinkedHashMap=new LinkedHashMap<Integer,FileState>();

	public BundlePropertiesFile(String defaultPropertiesFileName) {
		this.defaultPropertiesFileName = defaultPropertiesFileName;
	}

	public void addPropertiesFile(File file) {
		this.allPropertiesFileNames = this.allPropertiesFileNames + file.getName();
		this.allPropertiesFile.add(file);
	}

	public boolean isIllegalBundlePropertiesFile() {
		boolean result = true;
		if (this.allPropertiesFile.size() != SystemFileUtilities.getPropertiesType().size() + 1) {
			result = false;
			return result;
		} else {
			for (int j = 0; j < SystemFileUtilities.getPropertiesType().size(); j++) {
				int index = this.allPropertiesFileNames.indexOf(this.defaultPropertiesFileName + "_" + SystemFileUtilities.getPropertiesType().get(j) +
						TranslateManager.SUFFIX_NAME);
				if (index == -1) {
					result = false;
					return result;
				}
			}
			if (this.allPropertiesFileNames.indexOf(this.defaultPropertiesFileName +TranslateManager.SUFFIX_NAME) == -1) {
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
