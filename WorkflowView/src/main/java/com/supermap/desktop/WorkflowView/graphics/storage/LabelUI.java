package com.supermap.desktop.WorkflowView.graphics.storage;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

/**
 * Created by Administrator on 2017/10/13.
 */
public class LabelUI extends ComponentUI {
	private LabelTextField textField;

	@Override
	public void installUI(JComponent c) {
		textField = ((LabelTextField) c);
		textField.getEditor().setBorder(null);
		c.setLayout(new BorderLayout());
		c.add(textField.getEditor(), BorderLayout.CENTER);
		c.add(textField.getLabelText(), BorderLayout.EAST);
		c.setBorder(new JTextField().getBorder());
	}

	@Override
	public void uninstallUI(JComponent c) {
		c.removeAll();
	}
}
