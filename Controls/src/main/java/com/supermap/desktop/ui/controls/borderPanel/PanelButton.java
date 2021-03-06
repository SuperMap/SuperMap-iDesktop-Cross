package com.supermap.desktop.ui.controls.borderPanel;

import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.button.SmButton;

import javax.swing.*;
import java.awt.*;

public class PanelButton extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JButton buttonOk;
	private JButton buttonCancel;

	public JButton getButtonOk() {
		return buttonOk;
	}

	public void setButtonOk(JButton buttonOk) {
		this.buttonOk = buttonOk;
	}

	public JButton getButtonCancel() {
		return buttonCancel;
	}

	public void setButtonCancel(JButton buttonCancel) {
		this.buttonCancel = buttonCancel;
	}

	public PanelButton() {
		initComponent();
		initResources();
		FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT, 10, 10);
		this.setLayout(flowLayout);
		this.add(buttonOk);
		this.add(buttonCancel);
	}

	private void initComponent() {
		this.buttonOk = new SmButton("Ok");
		this.buttonCancel = new SmButton("Cancel");
		ComponentUIUtilities.setName(this.buttonOk,"PanelButton_buttonOk");
		ComponentUIUtilities.setName(this.buttonCancel,"PanelButton_buttonCancel");
	}

	private void initResources() {
		this.buttonOk.setText(CoreProperties.getString("String_OK"));
		this.buttonCancel.setText(CoreProperties.getString("String_Cancel"));
	}
}
