package main.java;

/**
 * Created by lixiaoyao on 2017/11/10.
 */
public class KeyValue {

	private String fileName = ""; //文件名
	private String key = "";//词条key
	private String modifyType = ModifyType.UNTRANSLATED;//词条修改类型
	private boolean isModify = false; //修条是否被翻译或修改
	private String preValue = "";     //针对词条修改，上一个版本的值，如果词条没被修改，则是为""
	private String currentValue = "";//当前版本的值
	private String afterModifyValue="";//翻译后的值

	public KeyValue() {}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getModifyType() {
		return modifyType;
	}

	public void setModifyType(String modifyType) {
		this.modifyType = modifyType;
	}

	public boolean isModify() {
		return isModify;
	}

	public void setModify(boolean modify) {
		isModify = modify;
	}

	public String getPreValue() {
		return preValue;
	}

	public void setPreValue(String preValue) {
		this.preValue = preValue;
	}

	public String getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}

	public String getAfterModifyValue() {
		return afterModifyValue;
	}

	public void setAfterModifyValue(String afterModifyValue) {
		this.afterModifyValue = afterModifyValue;
		if (!this.isModify){
			this.isModify=true;
		}
	}

}
