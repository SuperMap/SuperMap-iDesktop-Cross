package com.supermap.desktop.process.parameters.ParameterPanels;

import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.interfaces.IParameterPanel;
import com.supermap.desktop.process.parameter.interfaces.ParameterPanelDescribe;
import com.supermap.desktop.process.parameter.ipls.ParameterSpinner;

import javax.swing.*;

/**
 * @author XiaJT
 */
@ParameterPanelDescribe(parameterPanelType = ParameterType.SPINNER)
public class ParameterSpinnerPanel extends SwingPanel implements IParameterPanel {
	private ParameterSpinner parameterSpinner;
	private JLabel label = new JLabel();
	private JSpinner spinner;

	public ParameterSpinnerPanel(IParameter parameterSpinner) {
		super(parameterSpinner);
		this.parameterSpinner = (ParameterSpinner) parameterSpinner;
		spinner = new JSpinner();
		ComponentUIUtilities.setName(this.spinner, parameter.getDescription());
	}
}
