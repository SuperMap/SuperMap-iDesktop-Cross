package com.supermap.desktop.ui.controls;

import com.supermap.data.TextStyle;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.enums.TextStyleType;
import com.supermap.desktop.ui.controls.button.SmButton;
import com.supermap.desktop.ui.controls.textStyle.ITextStyle;
import com.supermap.desktop.ui.controls.textStyle.ResetTextStyleUtil;
import com.supermap.desktop.ui.controls.textStyle.TextBasicPanel;
import com.supermap.desktop.ui.controls.textStyle.TextStyleChangeListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created By Chens on 2017/7/24 0024
 */
public class LabelSettingDialog extends SmDialog {
	private ITextStyle textStylePanel;
	private TextStyle textStyle;
	private SmButton okSmButton = new SmButton();
	private SmButton cancelSmButton = new SmButton();

	public LabelSettingDialog(TextStyle textStyle) {
		this.textStyle=textStyle;
		initComponents();
		removeEvents();
		registerEvents();
	}

	private void initComponents(){
		this.setTitle(ControlsProperties.getString("String_Form_SetTextStyle"));
		this.setSize(new Dimension(326, 472));
		setLocationRelativeTo(null);
		getRootPane().setDefaultButton(okSmButton);
		this.textStylePanel = new TextBasicPanel();
		this.textStylePanel.setTextStyle(this.textStyle);
		this.textStylePanel.setOutLineWidth(true);
		this.textStylePanel.setProperty(false);
		this.textStylePanel.setUnityVisible(true);
		this.textStylePanel.initTextBasicPanel();
		this.textStylePanel.initCheckBoxState();
		this.textStylePanel.setAlign(false);
		this.textStylePanel.setFontHeight(false);
		this.textStylePanel.setRotationAngl(false);
		this.textStylePanel.setFixedSize(false);
		this.okSmButton.setText(ControlsProperties.getString("String_Ok"));
		this.cancelSmButton.setText(ControlsProperties.getString("String_Cancel"));
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setAutoCreateContainerGaps(true);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.textStylePanel.getBasicsetPanel())
						.addComponent(this.textStylePanel.getEffectPanel())
						.addGroup(groupLayout.createSequentialGroup()
								.addGap(5, 5, Integer.MAX_VALUE)
								.addComponent(okSmButton)
								.addComponent(cancelSmButton)))
		);

		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addComponent(this.textStylePanel.getBasicsetPanel())
				.addComponent(this.textStylePanel.getEffectPanel())
				.addContainerGap(0, Short.MAX_VALUE)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(okSmButton)
						.addComponent(cancelSmButton))
		);
		getContentPane().setLayout(groupLayout);
	}

	private void registerEvents() {
		this.okSmButton.addActionListener(this.buttonOKListener);
		this.cancelSmButton.addActionListener(this.buttonCancelListener);
		this.textStylePanel.addTextStyleChangeListener(this.textStyleChangeListener);
	}

	private void removeEvents() {
		this.okSmButton.removeActionListener(this.buttonOKListener);
		this.cancelSmButton.removeActionListener(this.buttonCancelListener);
		this.textStylePanel.removeTextStyleChangeListener(this.textStyleChangeListener);
	}

	private void resetTextStyle(TextStyleType newValue) {
		ResetTextStyleUtil.resetTextStyle(newValue, textStyle, textStylePanel.getResultMap().get(newValue));
	}

	public TextStyle getTextStyle() {
		return textStyle;
	}

	private ActionListener buttonOKListener=new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			LabelSettingDialog.this.setDialogResult(DialogResult.OK);
			LabelSettingDialog.this.dispose();
		}
	};

	private ActionListener buttonCancelListener=new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			LabelSettingDialog.this.dispose();
		}
	};

	private TextStyleChangeListener textStyleChangeListener=new TextStyleChangeListener() {
		@Override
		public void modify(TextStyleType newValue) {
			resetTextStyle(newValue);
		}
	};

}

