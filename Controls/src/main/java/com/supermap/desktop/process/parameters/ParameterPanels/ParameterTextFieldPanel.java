package com.supermap.desktop.process.parameters.ParameterPanels;

import com.supermap.desktop.Interface.ISmTextFieldLegit;
import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.events.ParameterUpdateValueEvent;
import com.supermap.desktop.process.parameter.events.UpdateValueListener;
import com.supermap.desktop.process.parameter.interfaces.AbstractParameter;
import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.interfaces.IParameterPanel;
import com.supermap.desktop.process.parameter.interfaces.ParameterPanelDescribe;
import com.supermap.desktop.process.parameter.ipls.ParameterTextField;
import com.supermap.desktop.process.util.ParameterUtil;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.ui.controls.ProviderLabel.NewHelpProvider;
import com.supermap.desktop.ui.controls.TextFields.SmTextFieldLegit;
import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;

/**
 * @author XiaJT
 */
@ParameterPanelDescribe(parameterPanelType = ParameterType.TEXTFIELD)
public class ParameterTextFieldPanel extends SwingPanel implements IParameterPanel {
	private ISmTextFieldLegit smTextFieldLegit;
	protected ParameterTextField parameterTextField;
	protected JLabel label = new JLabel();

	protected JLabel labelUnit = new JLabel();
	protected SmTextFieldLegit textField = new SmTextFieldLegit();
	protected boolean isSelectingItem = false;

	public ParameterTextFieldPanel(final IParameter parameterTextField) {
		super(parameterTextField);
		this.parameterTextField = (ParameterTextField) parameterTextField;
		label.setText(getDescribe());
		label.setToolTipText(this.parameterTextField.getDescription());
		label.setVisible(this.parameterTextField.isDescriptionVisible());
		textField.setText(String.valueOf(this.parameterTextField.getSelectedItem()));
		textField.setToolTipText(this.parameterTextField.getToolTip());
		this.smTextFieldLegit = ((ParameterTextField) parameterTextField).getSmTextFieldLegit();
		ComponentUIUtilities.setName(this.textField, parameter.getDescription());

		initLayout();
		initListeners();
	}

	private void initLayout() {
		label.setPreferredSize(ParameterUtil.LABEL_DEFAULT_SIZE);
		textField.setPreferredSize(new Dimension(20, 23));
		panel.setLayout(new GridBagLayout());
		// 需要用提示icon来显示提示信息
		if (!StringUtilities.isNullOrEmpty(parameterTextField.getTipButtonMessage())) {
			NewHelpProvider newHelpProvider = new NewHelpProvider(getDescribe(), parameterTextField.getTipButtonMessage());
			newHelpProvider.setPreferredSize(ParameterUtil.LABEL_DEFAULT_SIZE);
			panel.add(newHelpProvider, new GridBagConstraintsHelper(0, 0, 1, 1).setWeight(0, 1));
			panel.add(textField, new GridBagConstraintsHelper(1, 0, 1, 1).setWeight(1, 1).setAnchor(GridBagConstraints.CENTER).setFill(GridBagConstraints.HORIZONTAL).setInsets(0, 5, 0, 0));
		} else {
			panel.add(label, new GridBagConstraintsHelper(0, 0, 1, 1).setWeight(0, 1));
			panel.add(textField, new GridBagConstraintsHelper(1, 0, 1, 1).setWeight(1, 1).setAnchor(GridBagConstraints.CENTER).setFill(GridBagConstraints.HORIZONTAL).setInsets(0, 5, 0, 0));
		}


		// 判断是否添加提示按钮-yuanR2017.9.6

		if (!StringUtilities.isNullOrEmpty(parameterTextField.getUnit())) {
			labelUnit.setText(parameterTextField.getUnit());
			panel.add(labelUnit, new GridBagConstraintsHelper(2, 0, 1, 1).setInsets(3, 3, 3, 3));
		}
	}

	private void initListeners() {
		parameterTextField.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (!isSelectingItem && evt.getPropertyName().equals(AbstractParameter.PROPERTY_VALE)) {
					try {
						isSelectingItem = true;
						ParameterTextFieldPanel.this.textField.setText(evt.getNewValue() == null ? "" : evt.getNewValue().toString());
						// 当值改变时，同时改变其值得单位-yuanR
						if (!StringUtilities.isNullOrEmpty(parameterTextField.getUnit())) {
							labelUnit.setText(parameterTextField.getUnit());
						}
					} finally {
						isSelectingItem = false;
					}
				}
			}
		});
		parameterTextField.addUpdateValueListener(new UpdateValueListener() {
			@Override
			public void fireUpdateValue(ParameterUpdateValueEvent event) {
				if (event.getFieldName().equals(AbstractParameter.PROPERTY_VALE)) {
					isSelectingItem = true;
					parameterTextField.setSelectedItem(ParameterTextFieldPanel.this.textField.getBackUpValue());
					isSelectingItem = false;
				}
			}
		});
		textField.setSmTextFieldLegit(new ISmTextFieldLegit() {
			@Override
			public boolean isTextFieldValueLegit(String textFieldValue) {
				if (smTextFieldLegit == null || smTextFieldLegit.isTextFieldValueLegit(textFieldValue)) {
					if (!isSelectingItem) {
						isSelectingItem = true;
						parameterTextField.setSelectedItem(textFieldValue);
						isSelectingItem = false;
					}
					return true;
				}
				return false;
			}

			@Override
			public String getLegitValue(String currentValue, String backUpValue) {
				return smTextFieldLegit == null ? currentValue : smTextFieldLegit.getLegitValue(currentValue, backUpValue);
			}
		});
	}

	@Override
	protected void descriptionVisibleChanged(boolean newValue) {
		label.setVisible(newValue);
	}

	/**
	 * @return
	 */
	protected String getDescribe() {
		String describe = parameterTextField.getDescription();
		if (parameterTextField.isRequisite()) {
			return MessageFormat.format(CoreProperties.getString("String_IsRequiredLable"), describe);
		} else {
			return describe;
		}
	}
}
