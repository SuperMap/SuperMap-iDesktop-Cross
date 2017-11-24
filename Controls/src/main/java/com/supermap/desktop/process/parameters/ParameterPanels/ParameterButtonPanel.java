package com.supermap.desktop.process.parameters.ParameterPanels;

import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.interfaces.IParameterPanel;
import com.supermap.desktop.process.parameter.interfaces.ParameterPanelDescribe;
import com.supermap.desktop.process.parameter.ipls.ParameterButton;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * Created by caolp on 2017-04-26.
 */
@ParameterPanelDescribe(parameterPanelType = ParameterType.BUTTON)
public class ParameterButtonPanel extends SwingPanel implements IParameterPanel {

	private ParameterButton parameterButton;
	private JButton jButton = new JButton();
	private boolean isSelectedItem = false;

	public ParameterButtonPanel(IParameter parameterButton) {
		super(parameterButton);
		this.parameterButton = (ParameterButton) parameterButton;
		jButton.setText(this.parameterButton.getDescription());
		jButton.setEnabled(((ParameterButton) parameterButton).isEnabled());
		ComponentUIUtilities.setName(this.jButton, parameter.getDescription());
		initLayout();
		initListeners();
	}

	private void initListeners() {
		this.parameterButton.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (!isSelectedItem) {
					isSelectedItem = true;
					if (evt.getNewValue() instanceof Boolean) {
						jButton.setEnabled((Boolean) evt.getNewValue());
					} else {

					}
					isSelectedItem = false;
				}
			}
		});
	}

	private void initLayout() {
		panel.setLayout(new GridBagLayout());
		// 拉升方式自行设定
		panel.add(jButton, new GridBagConstraintsHelper(0, 0, 1, 1).setWeight(1, 0).setAnchor(GridBagConstraints.CENTER).setFill(parameterButton.getFill()).setInsets(0, 0, 0, 5));
		jButton.addActionListener(parameterButton.getActionListener());
	}


}