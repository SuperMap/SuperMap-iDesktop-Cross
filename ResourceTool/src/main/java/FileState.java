package main.java;

import java.util.ArrayList;

/**
 * Created by lixiaoyao on 2017/11/8.
 */
public class FileState {

	private boolean isChange = false;
	private ArrayList<String> content = new ArrayList<>();
	private String fileName = "";
	private String filePath = "";

	public FileState() { }

	public boolean isChange() {
		return isChange;
	}

	public void setChange(boolean change) {
		isChange = change;
	}

	public ArrayList<String> getContent() {
		return content;
	}

	public void setContent(ArrayList<String> content) {
		this.content = content;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}

