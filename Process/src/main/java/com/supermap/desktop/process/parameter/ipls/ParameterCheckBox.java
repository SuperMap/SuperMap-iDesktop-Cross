package com.supermap.desktop.process.parameter.ipls;

import com.supermap.desktop.process.constraint.annotation.ParameterField;
import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.interfaces.AbstractParameter;
import com.supermap.desktop.process.parameter.interfaces.ISelectionParameter;

import java.beans.PropertyChangeEvent;

/**
 * @author XiaJT
 * 增加tip提示-yuanR
 */
public class ParameterCheckBox extends AbstractParameter implements ISelectionParameter {

	public static final String PARAMETER_CHECK_BOX_VALUE = "PARAMETER_CHECK_BOX_VALUE";
	@ParameterField(name = PARAMETER_CHECK_BOX_VALUE)
	private String value = "false";
	private String describe;
	private String tip;

	public ParameterCheckBox() {
		this("");
	}

	public ParameterCheckBox(String describe) {
		this.describe = describe;
	}

	@Override
	public String getType() {
		return ParameterType.CHECKBOX;
	}

	@Override
	public void setSelectedItem(Object value) {
		Object oldValue = this.value;
		if (value == null) {
			this.value = null;
		} else {
			this.value = String.valueOf(value);
		}
		firePropertyChangeListener(new PropertyChangeEvent(this, PARAMETER_CHECK_BOX_VALUE, oldValue, value));
	}

	@Override
	public String getSelectedItem() {
		return value;
	}

	public ParameterCheckBox setDescribe(String describe) {
		this.describe = describe;
		return this;
	}


	public void setTip(String tip) {
		this.tip = tip;
	}

	public String getTip() {
		return tip;
	}


	@Override
	public void dispose() {

	}

	@Override
	public String getDescription() {
		return describe;
	}
}
