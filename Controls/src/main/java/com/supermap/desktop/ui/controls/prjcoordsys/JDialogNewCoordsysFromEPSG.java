package com.supermap.desktop.ui.controls.prjcoordsys;

import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.ui.controls.SmDialog;
import com.supermap.desktop.ui.controls.TextFields.WaringTextField;
import com.supermap.desktop.ui.controls.borderPanel.PanelButton;
import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Created by yuanR on 2017/11/2 0002.
 * 通过EPSG创建坐标系面板
 */
public class JDialogNewCoordsysFromEPSG extends SmDialog {

	private JLabel coordsysNameLabel;
	private JLabel codeLabel;

	private JTextField nameTextField;

	private WaringTextField codeTextField;

	private JCheckBox useDefaultNameCheck;
	private PanelButton panelButton;

	// 默认epsg值
	private int code = 3857;
	private ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(panelButton.getButtonOk())) {
				setCode(Integer.valueOf(codeTextField.getText()));
				dialogResult = DialogResult.OK;
				dispose();
			} else if (e.getSource().equals(panelButton.getButtonCancel())) {
				dialogResult = DialogResult.CANCEL;
				dispose();
			} else if (e.getSource().equals(useDefaultNameCheck)) {
				nameTextField.setEnabled(!useDefaultNameCheck.isSelected());
			}
		}
	};

	public JDialogNewCoordsysFromEPSG() {
		initializeComponents();
		initializeResources();
		initializeLayout();
		initStates();
		initListener();

		setSize(300, 165);
		setLocationRelativeTo(null);
	}

	private void initializeComponents() {
		this.coordsysNameLabel = new JLabel();
		this.codeLabel = new JLabel();
		this.nameTextField = new JTextField();
		this.codeTextField = new WaringTextField(String.valueOf(getCode()));

		this.codeTextField.setInitInfo(1, 999999999, WaringTextField.INTEGER_TYPE, "null");
		this.useDefaultNameCheck = new JCheckBox();

		this.panelButton = new PanelButton();
	}

	private void initializeResources() {
		this.setTitle(ControlsProperties.getString("String_Button_NewCoordSysFormEPSG"));
		this.coordsysNameLabel.setText(ControlsProperties.getString("String_Message_CoordSysName"));
		this.codeLabel.setText(ControlsProperties.getString("String_Label_EPSG_Code"));
		this.useDefaultNameCheck.setText(ControlsProperties.getString("String_UseCoordsysDefaultName"));
	}

	private void initializeLayout() {

		JPanel panel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(panel);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);
		panel.setLayout(groupLayout);

		//@formatter:off
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(this.coordsysNameLabel)
						.addGap(27)
						.addComponent(this.nameTextField))
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(this.codeLabel)
						.addComponent(this.codeTextField))
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(this.useDefaultNameCheck)));
		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.coordsysNameLabel)
						.addComponent(this.nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.codeLabel)
						.addComponent(this.codeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.useDefaultNameCheck)));
		//@formatter:on

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(panel, BorderLayout.CENTER);
		this.getContentPane().add(this.panelButton, BorderLayout.SOUTH);

	}

	private void initStates() {
		this.nameTextField.setText(ControlsProperties.getString("String_NewCoorSys"));
	}

	private void initListener() {
		this.nameTextField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {

			}

			@Override
			public void focusLost(FocusEvent e) {
				if (StringUtilities.isNullOrEmpty(nameTextField.getText())) {
					nameTextField.setText(ControlsProperties.getString("String_NewCoorSys"));
				}
			}
		});
		this.codeTextField.registEvents();
		this.codeTextField.getTextField().addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				try {
					Integer value = Integer.valueOf(codeTextField.getText());
					if (value < 1 || value > 999999999) {
						panelButton.getButtonOk().setEnabled(false);
					} else {

						panelButton.getButtonOk().setEnabled(true);
					}
				} catch (Exception ex) {
					panelButton.getButtonOk().setEnabled(false);
				}
			}
		});

		this.panelButton.getButtonOk().addActionListener(this.actionListener);
		this.panelButton.getButtonCancel().addActionListener(this.actionListener);
		this.useDefaultNameCheck.addActionListener(this.actionListener);
	}

	public JTextField getNameTextField() {
		return nameTextField;
	}

	public JCheckBox getUseDefaultNameCheck() {
		return this.useDefaultNameCheck;
	}

	public int getCode() {
		return this.code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public WaringTextField getCodeTextField() {
		return codeTextField;
	}
}
