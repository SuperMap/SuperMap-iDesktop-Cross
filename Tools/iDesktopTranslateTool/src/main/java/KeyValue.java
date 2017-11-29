/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/22
 * Time: 10:29
 * Description:
 */
public class KeyValue {
	private String key = "";                         //词条key
	private String englishValue = "";                //翻译后的值
	private boolean isModify = true;                //词条是否被翻译
	//	private String fileType="";                      //文件类型，即是资源文件还是配置文件
	private double similarityDegree = 0;              //相似度
	private String similarityString = "";              //相似词条

	public KeyValue() {
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isModify() {
		return isModify;
	}

	public void setModify(boolean modify) {
		isModify = modify;
	}

	public String getEnglishValue() {
		return englishValue;
	}

	public void setEnglishValue(String englishValue) {
		if (!englishValue.equals("")) {
			this.englishValue = englishValue;
			if (!this.isModify) {
				this.isModify = true;
			}
		}
	}

	public double getSimilarityDegree() {
		return similarityDegree;
	}

	public void setSimilarityDegree(double similarityDegree) {
		this.similarityDegree = similarityDegree;
	}

	public String getSimilarityString() {
		return similarityString;
	}

	public void setSimilarityString(String similarityString) {
		this.similarityString = similarityString;
	}

}
