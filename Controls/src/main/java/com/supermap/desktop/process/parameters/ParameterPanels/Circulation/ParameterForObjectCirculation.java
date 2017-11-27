package com.supermap.desktop.process.parameters.ParameterPanels.Circulation;

import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.interfaces.AbstractParameter;
import com.supermap.desktop.process.parameter.interfaces.ISelectionParameter;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

/**
 * Created by xie on 2017/11/6.
 */
public class ParameterForObjectCirculation extends AbstractParameter implements ISelectionParameter {
	private ArrayList<String> infoList = new ArrayList<>();
	private String fileType;
	private String nowInfo;
	private boolean isShowAddButton = true;
	public final String FILE_TYPE_CHANGED = "fileTypeChanged";
	private final String LIST_CHANGED="infoListChanged";
	public final String NEW_INFO_ADDED="newInfoAdded";

	public ParameterForObjectCirculation() {

	}

	@Override
	public boolean isRequired() {
		return true;
	}

	@Override
	public void setSelectedItem(Object item) {
		ArrayList<String> oldValue = null;
		if (item == null) {
			oldValue = this.infoList;
			this.infoList = null;
		} else if (item instanceof ArrayList) {
			oldValue = this.infoList;
			this.infoList = (ArrayList) item;
		}
		firePropertyChangeListener(new PropertyChangeEvent(this, LIST_CHANGED, oldValue, this.infoList));
	}


	@Override
	public ArrayList<String> getSelectedItem() {
		if (this.infoList.size() <= 0) {
			return null;
		}
		return this.infoList;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		String oldValue = this.fileType;
		this.fileType = fileType;
		firePropertyChangeListener(new PropertyChangeEvent(this, FILE_TYPE_CHANGED, oldValue, this.fileType));
	}

	@Override
	public String getType() {
		return ParameterType.CIRCULATION_FOR_OBJECT;
	}

	public boolean isShowAddButton() {
		return isShowAddButton;
	}

	public void setShowAddButton(boolean showAddButton) {
		isShowAddButton = showAddButton;
	}

	public void addRow(Object newInfo) {
		String oldValue = this.nowInfo;
		this.nowInfo = (String) newInfo;
		firePropertyChangeListener(new PropertyChangeEvent(this, NEW_INFO_ADDED, oldValue, this.nowInfo));
	}
}
