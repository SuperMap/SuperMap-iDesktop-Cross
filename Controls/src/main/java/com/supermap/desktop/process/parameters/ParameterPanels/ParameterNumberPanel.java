package com.supermap.desktop.process.parameters.ParameterPanels;

import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.interfaces.ParameterPanelDescribe;
import com.supermap.desktop.process.parameter.ipls.ParameterNumber;

/**
 * @author XiaJT
 */
@ParameterPanelDescribe(parameterPanelType = ParameterType.NUMBER)
public class ParameterNumberPanel extends ParameterTextFieldPanel {

	private ParameterNumber parameterNumber;

	public ParameterNumberPanel(IParameter parameterNumber) {
		super(parameterNumber);
		this.parameterNumber = (ParameterNumber) parameterNumber;
		label.setText(getDescribe());
		labelUnit.setText(this.parameterNumber.getUnit());
	}

	/**
	 * @return
	 */
//	private String getDescription() {
//		String describe = parameterNumber.getDescription();
//		if (parameterNumber.isRequired()) {
//			return MessageFormat.format(CommonProperties.getString("String_IsRequiredLable"), describe);
//		} else {
//			return describe;
//		}
//	}

}
