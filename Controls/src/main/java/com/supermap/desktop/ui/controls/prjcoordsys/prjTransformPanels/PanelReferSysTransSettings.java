package com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels;

import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.ui.controls.SmComboBox;
import com.supermap.desktop.ui.controls.button.SmButton;
import com.supermap.desktop.ui.controls.prjcoordsys.JDialogPrjCoordSysTranslatorSettings;
import com.supermap.desktop.utilities.CoordSysTransMethodUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by yuanR on 2017/9/25 0025.
 * 参照系转换参数设置面板
 */
public class PanelReferSysTransSettings extends JPanel {
	private JLabel labelMethod;
	private SmComboBox comboBoxMethod;
	private JLabel labelPrjTransParameterset;
	private SmButton buttonSet;
	private CoordSysTransMethod method = CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION;
	private CoordSysTransParameter parameter = new CoordSysTransParameter();

	private JDialogPrjCoordSysTranslatorSettings dialogPrjCoordSysTranslatorSettings;

	public CoordSysTransMethod getMethod() {
		return method;
	}

	public CoordSysTransParameter getParameter() {
		return parameter;
	}

	private static final String LAYOUTMODEL = "HORIZONTAL";

	public PanelReferSysTransSettings(String layoutModel) {
		initComponents();
		initializeResources();
		initLayout(layoutModel);
		initListener();
	}

	/**
	 * 投影转化参数设置按钮响应事件
	 */
	private ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (dialogPrjCoordSysTranslatorSettings == null) {
				dialogPrjCoordSysTranslatorSettings = new JDialogPrjCoordSysTranslatorSettings();
				dialogPrjCoordSysTranslatorSettings.fillCoordSysTransMethodValue(CoordSysTransMethodUtilities.valueOf(comboBoxMethod.getSelectedItem().toString()));
				dialogPrjCoordSysTranslatorSettings.fillCoordSysTransParameter(parameter);
				dialogPrjCoordSysTranslatorSettings.setVisible(true);
			} else {
				dialogPrjCoordSysTranslatorSettings.fillCoordSysTransMethodValue(CoordSysTransMethodUtilities.valueOf(comboBoxMethod.getSelectedItem().toString()));
				dialogPrjCoordSysTranslatorSettings.fillCoordSysTransParameter(parameter);
				dialogPrjCoordSysTranslatorSettings.setVisible(true);
			}
			if (dialogPrjCoordSysTranslatorSettings.getDialogResult() == DialogResult.OK) {
				// 当设置结束后，根据设置改变父级面板转换模式的值
				method = dialogPrjCoordSysTranslatorSettings.getMethod();
				parameter = dialogPrjCoordSysTranslatorSettings.getParameter();
				comboBoxMethod.setSelectedItem(CoordSysTransMethodUtilities.toString(method));
			}
		}
	};

	private transient ItemListener itemListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				method = CoordSysTransMethodUtilities.valueOf(comboBoxMethod.getSelectedItem().toString());
			}
		}
	};


	private void initComponents() {
		this.labelMethod = new JLabel("Method");
		this.labelPrjTransParameterset = new JLabel("PrjTransParameterset");

		this.comboBoxMethod = new SmComboBox();
		this.comboBoxMethod.addItem(CoordSysTransMethodUtilities.toString(CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION));
		this.comboBoxMethod.addItem(CoordSysTransMethodUtilities.toString(CoordSysTransMethod.MTH_MOLODENSKY));
		this.comboBoxMethod.addItem(CoordSysTransMethodUtilities.toString(CoordSysTransMethod.MTH_MOLODENSKY_ABRIDGED));
		this.comboBoxMethod.addItem(CoordSysTransMethodUtilities.toString(CoordSysTransMethod.MTH_POSITION_VECTOR));
		this.comboBoxMethod.addItem(CoordSysTransMethodUtilities.toString(CoordSysTransMethod.MTH_COORDINATE_FRAME));
		this.comboBoxMethod.addItem(CoordSysTransMethodUtilities.toString(CoordSysTransMethod.MTH_BURSA_WOLF));

		this.buttonSet = new SmButton();
	}

	private void initializeResources() {
		this.labelMethod.setText(ControlsProperties.getString("String_TransMethod"));
		this.labelPrjTransParameterset.setText(ControlsProperties.getString("String_Label_ProjectionTranslatorParameter"));
		this.buttonSet.setText(ControlsProperties.getString("String_Button_Setting"));
	}

	private void initLayout(String layoutModel) {
		if (layoutModel.equals(LAYOUTMODEL)) {
			this.comboBoxMethod.setPreferredSize(new Dimension(50, 23));
			this.buttonSet.setPreferredSize(new Dimension(50, 23));
			GroupLayout groupLayout = new GroupLayout(this);
			groupLayout.setAutoCreateContainerGaps(true);
			groupLayout.setAutoCreateGaps(true);
			this.setLayout(groupLayout);

			// @formatter:off
			groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
					.addComponent(this.labelMethod)
					.addComponent(this.comboBoxMethod, 0, 0, Short.MAX_VALUE)
					.addGap(40)
					.addComponent(this.labelPrjTransParameterset)
					.addComponent(this.buttonSet, 0, 0, Short.MAX_VALUE));
			groupLayout.setVerticalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(this.labelMethod)
					.addComponent(this.comboBoxMethod, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(this.labelPrjTransParameterset)
					.addComponent(this.buttonSet, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE));
			// @formatter:on
		} else {
			GroupLayout groupLayout = new GroupLayout(this);
			groupLayout.setAutoCreateContainerGaps(true);
			groupLayout.setAutoCreateGaps(true);
			this.setLayout(groupLayout);

			// @formatter:off
			groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(this.labelMethod)
							.addComponent(this.labelPrjTransParameterset))
					.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(this.comboBoxMethod)
							.addComponent(this.buttonSet, 0, 0, Short.MAX_VALUE)));

			groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(this.labelMethod)
							.addComponent(this.comboBoxMethod, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
							.addComponent(this.labelPrjTransParameterset)
							.addComponent(this.buttonSet, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)));
			// @formatter:on
		}
	}

	private void initListener() {
		removeListener();
		this.comboBoxMethod.addItemListener(itemListener);
		this.buttonSet.addActionListener(actionListener);
	}

	private void removeListener() {
		this.comboBoxMethod.removeItemListener(itemListener);
		this.buttonSet.removeActionListener(actionListener);
	}

	/**
	 * 设置面板是否可用
	 *
	 * @param isEnable
	 */
	public void setPanelEnabled(Boolean isEnable) {
		this.comboBoxMethod.setEnabled(isEnable);
		this.buttonSet.setEnabled(isEnable);
	}
}
