package com.supermap.desktop.WorkflowView.graphics.storage;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Administrator on 2017/10/13.
 */
public class LabelTextField extends JComponent {
	private JTextField editor;
	private JLabel labelText;

	public LabelTextField() {
		this("1111", "米");
	}

	public LabelTextField(String editorStr, String labelStr) {
		this.editor = new JTextField(editorStr);
		this.labelText = new JLabel(labelStr);
		setUI(new LabelUI());
	}

	public JTextField getEditor() {
		return editor;
	}

	public JLabel getLabelText() {
		return labelText;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(new Dimension(200, 50));
		frame.setPreferredSize(new Dimension(200, 50));
		LabelTextField textField = new LabelTextField();
		textField.setSize(new Dimension(100, 23));
		frame.add(textField, BorderLayout.NORTH);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
