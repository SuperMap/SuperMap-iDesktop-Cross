package com.supermap.desktop.controls.utilties;

import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import com.supermap.desktop.properties.CommonProperties;
import com.supermap.desktop.ui.SMFormattedTextField;
import com.supermap.desktop.ui.controls.TextFields.ISmTextFieldLegit;
import com.supermap.desktop.ui.controls.TextFields.SmTextFieldLegit;
import com.supermap.desktop.utilties.NumberUtilties;
import com.supermap.desktop.utilties.StringUtilties;

/**
 * 控件构造工厂
 * 
 * @author highsad
 *
 */
public class ComponentFactory {

	/**
	 * 生产一个OK按钮
	 * 
	 * @return
	 */
	public static JButton createButtonOK() {
		JButton buttonOK = new JButton();
		buttonOK.setText(CommonProperties.getString(CommonProperties.OK));
		return buttonOK;
	}

	/**
	 * 生产一个Cancel按钮
	 * 
	 * @return
	 */
	public static JButton createButtonCancel() {
		JButton buttonCancel = new JButton();
		buttonCancel.setText(CommonProperties.getString(CommonProperties.Cancel));
		return buttonCancel;
	}

	/**
	 * 生产一个整型限制的输入控件
	 * 
	 * @param defaultValue
	 * @param minValue
	 * @param maxValue
	 * @return
	 */
	public static SmTextFieldLegit createIntegerTextField(int defaultValue, final int minValue, final int maxValue) {
		if (minValue > maxValue) {
			return null;
		}

		SmTextFieldLegit textField = new SmTextFieldLegit();
		textField.setSmTextFieldLegit(new ISmTextFieldLegit() {

			@Override
			public boolean isTextFieldValueLegit(String textFieldValue) {
				boolean result = false;

				if (StringUtilties.isPositiveInteger(textFieldValue)) {
					Integer value = Integer.valueOf(textFieldValue);

					if (value >= minValue && value <= maxValue) {
						result = true;
					}
				}
				return result;
			}

			@Override
			public String getLegitValue(String currentValue, String backUpValue) {
				return backUpValue;
			}
		});
		textField.setText(String.valueOf(defaultValue));

		return textField;
	}
}