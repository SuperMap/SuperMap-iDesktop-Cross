package main.java;

/**
 * Created by lixiaoyao on 2017/11/10.
 */
public class KeyValue {

	private String fileName = ""; //�ļ���
	private String key = "";//����key
	private String modifyType = ModifyType.UNTRANSLATED;//�����޸�����
	private boolean isModify = false; //�����Ƿ񱻷�����޸�
	private String preValue = "";     //��Դ����޸ģ���һ���汾��ֵ���������û���޸ģ�����Ϊ""
	private String currentValue = "";//��ǰ�汾��ֵ
	private String afterModifyValue="";//������ֵ

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
