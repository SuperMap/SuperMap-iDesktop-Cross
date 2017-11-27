import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/22
 * Time: 10:49
 * Description:
 */
public class SystemFileUtilities {

	private static String systemFilePath = "";
	private static ArrayList<String> propertiesType = new ArrayList<String>();
	private static ArrayList<String> attributesType = new ArrayList<String>();
	private static LinkedHashMap<String,KeyValue> allKeyValue = new LinkedHashMap<String, KeyValue>();
	private static final String ALL_KEY_VALUE_PATH = "Tools/iDesktopTranslateTool/src/main/system/keyValue.txt";
	private static final String SYSTEM_FILE_PATH = "Tools/iDesktopTranslateTool/src/main/system/system.txt";
	private static final String CHINESE_CONFIG_PATH="/WorkEnvironment/Default";
	private static final String ENGLISH_CONFIG_FILENAME="Default_EN_US";
	private static final int ROOT_PATH_INDEX = 0;
	private static final int PROPERTIES_TYPE_INDEX = 1;
	private static final int ATTRIBUTES_TYPE_INDEX = 2;
	private static String currentSimilarityText = "null";
	private static double currentSimilarityDegree = 0;
	private static MyAipNlp myAipNlp=null;

	private static int unTranslateKeyCount=0;

	public static boolean isFirstRun() {
		boolean isFirst = true;
		allKeyValue.clear();
		try {
			ArrayList<String> result = FileUtilites.readFileContent(new File(ALL_KEY_VALUE_PATH));
			for (int i = 0; i < result.size(); i++) {
				String[] content = result.get(i).split("\t");
				if (content.length<3){
					continue;
				}
				KeyValue keyValue = new KeyValue();
				keyValue.setKey(content[0].trim());
				keyValue.setEnglishValue(content[1]);
				keyValue.setModify(true);
//				keyValue.setFileType(content[3]);
				keyValue.setSimilarityDegree(0);
				keyValue.setSimilarityString(content[4]);
				allKeyValue.put(keyValue.getKey(),keyValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (allKeyValue.size() == 0) {
			isFirst = true;
		}else{
			isFirst=false;
		}
		return isFirst;
	}

	public static void initSystemSetting() {
		try {
			File file = new File(SYSTEM_FILE_PATH);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));//构造一个BufferedReader类来读取文件
			String readStr = "";
			ArrayList<String> resultStr = new ArrayList<String>();
			while ((readStr = br.readLine()) != null) {
				readStr = readStr.split("=")[1]; //为什么直接add split之后的是单个字符
				resultStr.add(readStr);
				readStr = "";
			}
			systemFilePath=resultStr.get(ROOT_PATH_INDEX);
			strToArrayList(propertiesType,resultStr.get(PROPERTIES_TYPE_INDEX));
			strToArrayList(attributesType,resultStr.get(ATTRIBUTES_TYPE_INDEX));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void strToArrayList(ArrayList<String> arrayList,String str){
		arrayList.clear();
		String[] result=str.split(",");
		for (int i=0;i<result.length;i++){
			arrayList.add(result[i]);
		}
	}

	public static String getSystemFilePath() {
		return systemFilePath;
	}

	public static ArrayList<String> getPropertiesType() {
		return propertiesType;
	}

	public static ArrayList<String> getAttributesType() {
		return attributesType;
	}

	public static LinkedHashMap<String, KeyValue> getAllKeyValue() {
		return allKeyValue;
	}

	public static void addKeyValue(KeyValue keyValue){
		if (!allKeyValue.containsKey(keyValue.getKey())){
			if (!keyValue.isModify()){
				unTranslateKeyCount++;
			}
			runSimilarity(keyValue.getKey());
			keyValue.setSimilarityDegree(currentSimilarityDegree);
			keyValue.setSimilarityString(currentSimilarityText);
			if (allKeyValue.containsKey(currentSimilarityText)) {
				allKeyValue.get(currentSimilarityText).setSimilarityString(keyValue.getKey());
				allKeyValue.get(currentSimilarityText).setSimilarityDegree(currentSimilarityDegree);
			}
//			System.out.println(keyValue.getKey());
			allKeyValue.put(keyValue.getKey(),keyValue);
		}
	}

	private static void runSimilarity(String str) {
		currentSimilarityDegree = 0;
		currentSimilarityText = "null";
		if (allKeyValue.size() == 0) {
			return;
		}
		if (myAipNlp==null){
			myAipNlp=new MyAipNlp();
		}
		for (String key : allKeyValue.keySet()) {
			if (!allKeyValue.get(key).isModify()){
				continue;
			}
			double similarityDegreeTemp = TextSimilarity.sim(str, key);
			if (Double.compare(similarityDegreeTemp, currentSimilarityDegree) == 1) {
				currentSimilarityDegree = similarityDegreeTemp;
				currentSimilarityText = key;
			}
		}
	}

	public static int getUnTranslateKeyCount() {
		return unTranslateKeyCount;
	}

	public static void saveKeyValue(){
		try {
			File file = new File(ALL_KEY_VALUE_PATH);
			file.createNewFile();
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			BufferedWriter writer = new BufferedWriter(outputStreamWriter);
			for (KeyValue temp:allKeyValue.values()) {
				if (temp.isModify()) {
					writer.write(temp.getKey() + "\t" + temp.getEnglishValue() + "\t" +
							temp.isModify() + "\t" + temp.getSimilarityDegree() + "\t" + temp.getSimilarityString());
					writer.write("\r\n");
				}
			}
			writer.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static String getChineseConfigPath() {
		return CHINESE_CONFIG_PATH;
	}

	public static String getEnglishConfigFilename() {
		return ENGLISH_CONFIG_FILENAME;
	}
}
