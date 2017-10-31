package com.supermap.desktop.WorkflowView.graphics.storage;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Administrator on 2017/10/13.
 */
public class ComplexTextField extends JComponent {
	private LabelTextField dTextField;
	private LabelTextField fTextField;
	private LabelTextField mTextField;
	public ComplexTextField(){
		this.dTextField = new LabelTextField("","度");
		this.fTextField = new LabelTextField("","分");
		this.mTextField = new LabelTextField("","秒");
		setUI(new ComplexLabelUI());
	}

	public LabelTextField getdTextField() {
		return dTextField;
	}

	public LabelTextField getfTextField() {
		return fTextField;
	}

	public LabelTextField getmTextField() {
		return mTextField;
	}
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(new Dimension(200, 50));
		frame.setPreferredSize(new Dimension(200, 50));
		ComplexTextField textField = new ComplexTextField();
		textField.setSize(new Dimension(100, 23));
		frame.add(textField, BorderLayout.NORTH);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
