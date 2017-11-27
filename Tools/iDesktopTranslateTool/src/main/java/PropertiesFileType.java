/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/23
 * Time: 11:34
 * Description:
 */
public class PropertiesFileType {

	public static final int DEFAULT=0;
	public static final int CHINESE = 1;
	public static final int ENGLISH=2;
	private static String[] chineseRule=new String[]{"zh_CN","zh_cn"};
	private static String[] englishRule=new String[]{"en_US","en_us"};

	public static int getFileType(String fileName){
		int result=DEFAULT;
		if (isChineseFile(fileName)){
			result=CHINESE;
		}else if (isEnglishFile(fileName)){
			result=ENGLISH;
		}
		return result;
	}

	private static boolean isChineseFile(String fileName){
		boolean result=false;
		for (int i = 0; i <chineseRule.length; i++) {
			if (fileName.indexOf(chineseRule[i])!=-1){
				result=true;
				break;
			}
		}
		return result;
	}

	private static boolean isEnglishFile(String fileName){
		boolean result=false;
		for (int i = 0; i <englishRule.length; i++) {
			if (fileName.indexOf(englishRule[i])!=-1){
				result=true;
				break;
			}
		}
		return result;
	}
}
