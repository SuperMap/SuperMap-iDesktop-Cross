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
		firePropertyChangeListener(new PropertyChangeEvent(this, "radioLists", oldValue, this.infoList));
	}


	@Override
	public ArrayList<String> getSelectedItem() {
		if (this.infoList.size() <= 0) {
			return null;
		}
		return this.infoList;
	}

	@Override
	public String getType() {
		return ParameterType.CIRCULATION_FOR_OBJECT;
	}
}
