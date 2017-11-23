package com.supermap.desktop.process.parameters.ParameterPanels.MultiBufferRadioList;

import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.interfaces.AbstractParameter;
import com.supermap.desktop.process.parameter.interfaces.ISelectionParameter;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

/**
 * Created by yuanR on 2017/8/22 0022.
 */
public class ParameterMultiBufferRadioList extends AbstractParameter implements ISelectionParameter {
	private ArrayList<Double> radioLists = new ArrayList<>();

	public ParameterMultiBufferRadioList() {
	}

	@Override
	public boolean isRequired() {
		return true;
	}

	@Override
	public void setSelectedItem(Object item) {
		ArrayList<Double> oldValue = null;
		if (item == null) {
			oldValue = this.radioLists;
			this.radioLists = null;
		} else if (item instanceof ArrayList) {
			oldValue = this.radioLists;
			this.radioLists = (ArrayList) item;
		}
		firePropertyChangeListener(new PropertyChangeEvent(this, "radioLists", oldValue, this.radioLists));
	}


	@Override
	public ArrayList<Double> getSelectedItem() {
		if (this.radioLists.size() <= 0) {
			return null;
		}
		return this.radioLists;
	}

	@Override
	public String getType() {
		return ParameterType.MULTI_BUFFER_RADIOLIST;
	}
}