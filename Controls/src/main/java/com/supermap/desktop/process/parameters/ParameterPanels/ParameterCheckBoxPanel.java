package com.supermap.desktop.process.parameters.ParameterPanels;

import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.interfaces.IParameterPanel;
import com.supermap.desktop.process.parameter.interfaces.ParameterPanelDescribe;
import com.supermap.desktop.process.parameter.ipls.ParameterCheckBox;
import com.supermap.desktop.properties.CommonProperties;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.ui.controls.ProviderLabel.WarningOrHelpProvider;
import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;

/**
 * @author XiaJT
 */
@ParameterPanelDescribe(parameterPanelType = ParameterType.CHECKBOX)
public class ParameterCheckBoxPanel extends SwingPanel implements IParameterPanel {

	private ParameterCheckBox parameterCheckBox;

	private JCheckBox checkBox = new JCheckBox();
	private boolean isSelectingItem = false;

	public ParameterCheckBoxPanel(IParameter parameterCheckBox) {
		super(parameterCheckBox);
		this.parameterCheckBox = (ParameterCheckBox) parameterCheckBox;
		checkBox.setText(getDescribe());
		ComponentUIUtilities.setName(this.checkBox, parameter.getDescribe());
		checkBox.setSelected(Boolean.valueOf(String.valueOf(this.parameterCheckBox.getSelectedItem())));
		initLayout();
		initListeners();
	}

	private void initLayout() {
		panel.setLayout(new GridBagLayout());
		if (!StringUtilities.isNullOrEmpty(parameterCheckBox.getTip())) {
			panel.add(checkBox, new GridBagConstraintsHelper(0, 0, 1, 1).setWeight(0, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL));
			panel.add(new WarningOrHelpProvider(parameterCheckBox.getTip(), false), new GridBagConstraintsHelper(1, 0, 1, 1).setWeight(1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE));
		} else {
			panel.add(checkBox, new GridBagConstraintsHelper(0, 0, 1, 1).setWeight(1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL));
		}
	}

	private void initListeners() {
		parameterCheckBox.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// 对判断条件进行修改-yuanR
				//  if (!isSelectingItem && evt.getPropertyName().equals(AbstractParameter.PROPERTY_VALE)) {
				if (!isSelectingItem && evt.getPropertyName().equals(parameterCheckBox.PARAMETER_CHECK_BOX_VALUE)) {
					isSelectingItem = true;
					checkBox.setSelected(Boolean.valueOf(String.valueOf(parameterCheckBox.getSelectedItem())));
					isSelectingItem = false;
				}
			}
		});
		checkBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (!isSelectingItem) {
					isSelectingItem = true;
					parameterCheckBox.setSelectedItem(checkBox.isSelected());
					isSelectingItem = false;
				}
			}
		});
	}

	/**
	 * @return
	 */
	private String getDescribe() {
		String describe = parameterCheckBox.getDescribe();
		if (parameterCheckBox.isRequisite()) {
			return MessageFormat.format(CommonProperties.getString("String_IsRequiredLable"), describe);
		} else {
			return describe;
		}
	}
}
