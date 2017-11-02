package com.supermap.desktop.WorkflowView.graphics.storage;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

/**
 * Created by Administrator on 2017/10/13.
 */
public class ComplexLabelUI extends ComponentUI{
	ComplexTextField textField;
	@Override
	public void installUI(JComponent c) {
		textField = ((ComplexTextField) c);
		textField.getdTextField().setBorder(null);
		textField.getfTextField().setBorder(null);
		textField.getmTextField().setBorder(null);
		c.setLayout(new GridBagLayout());
		c.add(textField.getdTextField(),new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		c.add(textField.getfTextField(),new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		c.add(textField.getmTextField(),new GridBagConstraints(2,0,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		c.setBorder(new JTextField().getBorder());
	}
}
