package com.supermap.desktop.process.parameter.ipls;

import com.supermap.desktop.process.constraint.annotation.ParameterField;
import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.interfaces.AbstractParameter;
import com.supermap.desktop.process.parameter.interfaces.ISelectionParameter;

import java.beans.PropertyChangeEvent;

/**
 * @author XiaJT
 */
public class ParameterLabel extends AbstractParameter implements ISelectionParameter {
	private String describe;
	@ParameterField(name = PROPERTY_VALE)
	private String value = "";

	@Override
	public void setSelectedItem(Object value) {
		Object oldValue = this.value;
		this.value = String.valueOf(value);
		firePropertyChangeListener(new PropertyChangeEvent(this, "value", oldValue, value));
	}

	@Override
	public Object getSelectedItem() {
		fireUpdateValue(PROPERTY_VALE);
		return value;
	}

	@Override
	public String getType() {
		return ParameterType.LABEL;
	}

	public ParameterLabel setDescribe(String describe) {
		this.describe = describe;
		return this;
	}

	@Override
	public String getDescription() {
		return describe;
	}
}
