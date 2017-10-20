package com.supermap.desktop.ui.controls.prjcoordsys;

import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.properties.CommonProperties;
import com.supermap.desktop.ui.SMFormattedTextField;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.ui.controls.SmComboBox;
import com.supermap.desktop.ui.controls.SmDialog;
import com.supermap.desktop.ui.controls.SmFileChoose;
import com.supermap.desktop.ui.controls.button.SmButton;
import com.supermap.desktop.utilities.CoordSysTransMethodUtilities;
import com.supermap.desktop.utilities.FileUtilities;
import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.text.NumberFormat;

/**
 * 原先的投影转换JDialog，现在充当投影转换参数设置面板
 * 仿照.net进行修改
 * yuanR2017.9.25
 */
public class JDialogPrjCoordSysTranslatorSettings extends SmDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private JLabel labelMethod;
	private SmComboBox<String> comboBoxMethod;
	//private SmButton buttonSetPrj;
	private JLabel labelScaleDifference;
	private SMFormattedTextField textFieldScaleDifference;
	private JLabel labelRotationX;
	private SMFormattedTextField textFieldRotationX;
	private JLabel labelRotationY;
	private SMFormattedTextField textFieldRotationY;
	private JLabel labelRotationZ;
	private SMFormattedTextField textFieldRotationZ;
	private JLabel labelTranslateX;
	private SMFormattedTextField textFieldTranslateX;
	private JLabel labelTranslateY;
	private SMFormattedTextField textFieldTranslateY;
	private JLabel labelTranslateZ;
	private SMFormattedTextField textFieldTranslateZ;
	private SmButton buttonImport;
	private SmButton buttonExport;
	private SmButton buttonOk;
	private SmButton buttonCancel;

	private CoordSysTransMethod method = CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION;
	private CoordSysTransParameter parameter = new CoordSysTransParameter();

	private transient ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == buttonImport) {
				buttonImportClicked();
			} else if (e.getSource() == buttonExport) {
				buttonExportClicked();
			} else if (e.getSource() == buttonOk) {
				buttonOKClicked();
			} else if (e.getSource() == buttonCancel) {
				buttonCancelClicked();
			}
		}
	};
	private transient ItemListener itemListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				comboBoxMethodSelectedChange();
			}
		}
	};

	/**
	 * 投影转换参数设置窗口
	 *
	 * @param
	 */
	public JDialogPrjCoordSysTranslatorSettings() {
		initializeComponents();
		initLayout();
		initializeResources();
		fillCoordSysTransParameter(this.parameter);
		setComponentsEnabled();
		registerEvents();
		setSize(600, 315);
		setLocationRelativeTo(null);
		this.componentList.add(buttonImport);
		this.componentList.add(buttonExport);
		this.componentList.add(buttonOk);
		this.componentList.add(buttonCancel);
		this.setFocusTraversalPolicy(policy);
		this.getRootPane().setDefaultButton(this.buttonCancel);
	}

	public CoordSysTransMethod getMethod() {
		return this.method;
	}

	public CoordSysTransParameter getParameter() {
		return this.parameter;
	}

	private void initializeComponents() {
		// 基本参数
		this.labelMethod = new JLabel("Method");
		this.comboBoxMethod = new SmComboBox();
		this.comboBoxMethod.addItem(CoordSysTransMethodUtilities.toString(CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION));
		this.comboBoxMethod.addItem(CoordSysTransMethodUtilities.toString(CoordSysTransMethod.MTH_MOLODENSKY));
		this.comboBoxMethod.addItem(CoordSysTransMethodUtilities.toString(CoordSysTransMethod.MTH_MOLODENSKY_ABRIDGED));
		this.comboBoxMethod.addItem(CoordSysTransMethodUtilities.toString(CoordSysTransMethod.MTH_POSITION_VECTOR));
		this.comboBoxMethod.addItem(CoordSysTransMethodUtilities.toString(CoordSysTransMethod.MTH_COORDINATE_FRAME));
		this.comboBoxMethod.addItem(CoordSysTransMethodUtilities.toString(CoordSysTransMethod.MTH_BURSA_WOLF));
		this.comboBoxMethod.setPreferredSize(new Dimension(80, 23));
		this.labelScaleDifference = new JLabel("ScaleDiff");
		this.textFieldScaleDifference = new SMFormattedTextField(NumberFormat.getInstance());
	}

	private void initLayout() {

		JPanel panelBase = new JPanel();
		panelBase.setBorder(BorderFactory.createTitledBorder(ControlsProperties.getString("String_BasicParameters")));

		GroupLayout gl_panelBase = new GroupLayout(panelBase);
		gl_panelBase.setAutoCreateContainerGaps(true);
		gl_panelBase.setAutoCreateGaps(true);
		panelBase.setLayout(gl_panelBase);

		// @formatter:off
		gl_panelBase.setHorizontalGroup(gl_panelBase.createSequentialGroup()
				.addGroup(gl_panelBase.createParallelGroup(Alignment.LEADING)
						.addComponent(this.labelMethod)
						.addComponent(this.labelScaleDifference))
				.addGroup(gl_panelBase.createParallelGroup(Alignment.LEADING)
						.addComponent(this.comboBoxMethod)
						.addComponent(this.textFieldScaleDifference)));


		gl_panelBase.setVerticalGroup(gl_panelBase.createSequentialGroup()
				.addGroup(gl_panelBase.createParallelGroup(Alignment.CENTER)
						.addComponent(this.labelMethod)
						.addComponent(this.comboBoxMethod, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_panelBase.createParallelGroup(Alignment.CENTER)
						.addComponent(this.labelScaleDifference)
						.addComponent(this.textFieldScaleDifference, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(5, 5, Short.MAX_VALUE));


		// @formatter:on

		// 旋转角度
		this.labelRotationX = new JLabel("X:");
		this.textFieldRotationX = new SMFormattedTextField(NumberFormat.getInstance());
		this.labelRotationY = new JLabel("Y:");
		this.textFieldRotationY = new SMFormattedTextField(NumberFormat.getInstance());
		this.labelRotationZ = new JLabel("Z:");
		this.textFieldRotationZ = new SMFormattedTextField(NumberFormat.getInstance());

		JPanel panelRotation = new JPanel();
		panelRotation.setBorder(BorderFactory.createTitledBorder(ControlsProperties.getString("String_Rotation")));
		GroupLayout gl_panelRotation = new GroupLayout(panelRotation);
		gl_panelRotation.setAutoCreateContainerGaps(true);
		gl_panelRotation.setAutoCreateGaps(true);
		panelRotation.setLayout(gl_panelRotation);

		// @formatter:off
		gl_panelRotation.setHorizontalGroup(gl_panelRotation.createSequentialGroup()
				.addGroup(gl_panelRotation.createParallelGroup(Alignment.LEADING)
						.addComponent(this.labelRotationX)
						.addComponent(this.labelRotationY)
						.addComponent(this.labelRotationZ))
				.addGroup(gl_panelRotation.createParallelGroup(Alignment.LEADING)
						.addComponent(this.textFieldRotationX)
						.addComponent(this.textFieldRotationY)
						.addComponent(this.textFieldRotationZ)));

		gl_panelRotation.setVerticalGroup(gl_panelRotation.createSequentialGroup()
				.addGroup(gl_panelRotation.createParallelGroup(Alignment.CENTER)
						.addComponent(this.labelRotationX)
						.addComponent(this.textFieldRotationX, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_panelRotation.createParallelGroup(Alignment.CENTER)
						.addComponent(this.labelRotationY)
						.addComponent(this.textFieldRotationY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_panelRotation.createParallelGroup(Alignment.CENTER)
						.addComponent(this.labelRotationZ)
						.addComponent(this.textFieldRotationZ, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(5, 5, Short.MAX_VALUE));
		// @formatter:on

		// 偏移量
		this.labelTranslateX = new JLabel("X:");
		this.textFieldTranslateX = new SMFormattedTextField(NumberFormat.getInstance());
		this.labelTranslateY = new JLabel("Y:");
		this.textFieldTranslateY = new SMFormattedTextField(NumberFormat.getInstance());
		this.labelTranslateZ = new JLabel("Z:");
		this.textFieldTranslateZ = new SMFormattedTextField(NumberFormat.getInstance());

		JPanel panelOffset = new JPanel();
		panelOffset.setBorder(BorderFactory.createTitledBorder(ControlsProperties.getString("String_Offset")));
		GroupLayout gl_panelOffset = new GroupLayout(panelOffset);
		gl_panelOffset.setAutoCreateContainerGaps(true);
		gl_panelOffset.setAutoCreateGaps(true);
		panelOffset.setLayout(gl_panelOffset);

		// @formatter:off
		gl_panelOffset.setHorizontalGroup(gl_panelOffset.createSequentialGroup()
				.addGroup(gl_panelOffset.createParallelGroup(Alignment.LEADING)
						.addComponent(this.labelTranslateX)
						.addComponent(this.labelTranslateY)
						.addComponent(this.labelTranslateZ))
				.addGroup(gl_panelOffset.createParallelGroup(Alignment.LEADING)
						.addComponent(this.textFieldTranslateX)
						.addComponent(this.textFieldTranslateY)
						.addComponent(this.textFieldTranslateZ)));

		gl_panelOffset.setVerticalGroup(gl_panelOffset.createSequentialGroup()
				.addGroup(gl_panelOffset.createParallelGroup(Alignment.CENTER)
						.addComponent(this.labelTranslateX)
						.addComponent(this.textFieldTranslateX, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_panelOffset.createParallelGroup(Alignment.CENTER)
						.addComponent(this.labelTranslateY)
						.addComponent(this.textFieldTranslateY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_panelOffset.createParallelGroup(Alignment.CENTER)
						.addComponent(this.labelTranslateZ)
						.addComponent(this.textFieldTranslateZ, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(5, 5, Short.MAX_VALUE));
		// @formatter:on

		// 主界面
		this.buttonImport = new SmButton(ControlsProperties.getString("String_Button_Import"));
		this.buttonExport = new SmButton(ControlsProperties.getString("String_Button_Export"));
		this.buttonOk = new SmButton("OK");
		this.buttonCancel = new SmButton("Cancel");
		this.getRootPane().setDefaultButton(this.buttonOk);

		GroupLayout groupLayout = new GroupLayout(this.getContentPane());
		groupLayout.setAutoCreateContainerGaps(true);
		groupLayout.setAutoCreateGaps(true);
		this.getContentPane().setLayout(groupLayout);

		// @formatter:off
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.CENTER)
				.addComponent(panelBase)
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(panelRotation)
						.addComponent(panelOffset))
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(this.buttonImport)
						.addComponent(this.buttonExport)
						.addGap(10, 10, Short.MAX_VALUE)
						.addComponent(this.buttonOk)
						.addComponent(this.buttonCancel)));

		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addComponent(panelBase)
				.addGroup(groupLayout.createParallelGroup(Alignment.CENTER)
						.addComponent(panelRotation)
						.addComponent(panelOffset))
				.addGroup(groupLayout.createParallelGroup(Alignment.CENTER)
						.addComponent(this.buttonImport)
						.addComponent(this.buttonExport)
						.addComponent(this.buttonOk)
						.addComponent(this.buttonCancel)));
		// @formatter:on
	}

	private void initializeResources() {
		setTitle(ControlsProperties.getString("String_TransParamsSetting"));
		this.labelMethod.setText(ControlsProperties.getString("String_TransMethod"));
		this.labelScaleDifference.setText(ControlsProperties.getString("String_ScaleDifference"));
		//this.buttonSetPrj.setText(ControlsProperties.getString("String_SetDesPrjCoordSys"));
		this.buttonImport.setText(ControlsProperties.getString("String_Button_Import"));
		this.buttonExport.setText(ControlsProperties.getString("String_Button_Export"));
		this.buttonOk.setText(CommonProperties.getString(CommonProperties.OK));
		this.buttonCancel.setText(CommonProperties.getString(CommonProperties.Cancel));
	}

	private void registerEvents() {
		unregisterEvents();
		this.comboBoxMethod.addItemListener(this.itemListener);
		//this.buttonSetPrj.addActionListener(this.actionListener);
		this.buttonImport.addActionListener(this.actionListener);
		this.buttonExport.addActionListener(this.actionListener);
		this.buttonOk.addActionListener(this.actionListener);
		this.buttonCancel.addActionListener(this.actionListener);
	}

	private void unregisterEvents() {
		this.comboBoxMethod.removeItemListener(this.itemListener);
		//this.buttonSetPrj.removeActionListener(this.actionListener);
		this.buttonImport.removeActionListener(this.actionListener);
		this.buttonExport.removeActionListener(this.actionListener);
		this.buttonOk.removeActionListener(this.actionListener);
		this.buttonCancel.removeActionListener(this.actionListener);
	}

	/**
	 * 初始化投影参数的值
	 * yuanR2017.9.25
	 */
	public void fillCoordSysTransParameter(CoordSysTransParameter coordSysTransParameter) {
		this.textFieldScaleDifference.setValue(coordSysTransParameter.getScaleDifference());
		// 旋转角度单位与组件保持一致，需要是弧度，这里做一下转换。弧度转为秒
		this.textFieldRotationX.setValue(coordSysTransParameter.getRotateX() / Math.PI * 180 * 60 * 60);
		this.textFieldRotationY.setValue(coordSysTransParameter.getRotateY() / Math.PI * 180 * 60 * 60);
		this.textFieldRotationZ.setValue(coordSysTransParameter.getRotateZ() / Math.PI * 180 * 60 * 60);

		this.textFieldTranslateX.setValue(coordSysTransParameter.getTranslateX());
		this.textFieldTranslateY.setValue(coordSysTransParameter.getTranslateY());
		this.textFieldTranslateZ.setValue(coordSysTransParameter.getTranslateZ());
	}

	public void fillCoordSysTransMethodValue(CoordSysTransMethod method) {
		this.comboBoxMethod.setSelectedItem(CoordSysTransMethodUtilities.toString(method));
	}

	private void buttonSetPrjClicked() {
		//JDialogPrjCoordSysSettings prjSettings = new JDialogPrjCoordSysSettings();
		//prjSettings.setPrjCoordSys(this.getTargetPrj() == null ? this.beforePrj : this.getTargetPrj());
		//if (prjSettings.showDialog() == DialogResult.OK) {
		//	this.targetPrj = prjSettings.getPrjCoordSys();
		//}
		//setComponentsEnabled();
	}

	private void comboBoxMethodSelectedChange() {
		this.method = CoordSysTransMethodUtilities.valueOf(this.comboBoxMethod.getSelectedItem().toString());
		setComponentsEnabled();
	}

	private void setComponentsEnabled() {
		if (this.method == CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION || this.method == CoordSysTransMethod.MTH_MOLODENSKY
				|| this.method == CoordSysTransMethod.MTH_MOLODENSKY_ABRIDGED) {
			this.textFieldScaleDifference.setEditable(false);
			this.textFieldRotationX.setEditable(false);
			this.textFieldRotationY.setEditable(false);
			this.textFieldRotationZ.setEditable(false);
		} else {
			this.textFieldScaleDifference.setEditable(true);
			this.textFieldRotationX.setEditable(true);
			this.textFieldRotationY.setEditable(true);
			this.textFieldRotationZ.setEditable(true);
		}
		//this.buttonOk.setEnabled(this.targetPrj != null);
	}


	/**
	 * 导入.ctp文件，
	 */
	private void buttonImportClicked() {
		String moduleName = "ImportPrjCoordSysTranslatorFile";
		if (!SmFileChoose.isModuleExist(moduleName)) {
			SmFileChoose.addNewNode(SmFileChoose.createFileFilter(ControlsProperties.getString("String_TransParamFile"), "ctp"),
					CommonProperties.getString("String_DefaultFilePath"), ControlsProperties.getString("String_OpenRasterAlgebraExpressionFile"),
					moduleName, "OpenOne");
		}
		SmFileChoose fileChooseImport = new SmFileChoose(moduleName);
		if (fileChooseImport.showDefaultDialog() == JFileChooser.APPROVE_OPTION) {
			String filePath = fileChooseImport.getFilePath();
			if (!StringUtilities.isNullOrEmptyString(filePath)) {
				try {
					String strXML = FileUtilities.getFileValue(filePath);
					CoordSysTransParameter coordSysTransParameter = new CoordSysTransParameter();
					coordSysTransParameter.fromXML(strXML);
					this.parameter = coordSysTransParameter;
					fillCoordSysTransParameter(this.parameter);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 导出.ctp文件，
	 */
	private void buttonExportClicked() {
		String moduleName = "ExportPrjCoordSysTranslatorFile";
		if (!SmFileChoose.isModuleExist(moduleName)) {
			SmFileChoose.addNewNode(SmFileChoose.createFileFilter(ControlsProperties.getString("String_TransParamFile"), "ctp"),
					CommonProperties.getString("String_DefaultFilePath"), ControlsProperties.getString("String_SaveAsFile"),
					moduleName, "SaveOne");
		}
		SmFileChoose fileChooseExport = new SmFileChoose(moduleName);
		fileChooseExport.setSelectedFile(new File("CoordSysTransPatameter.ctp"));
		if (fileChooseExport.showDefaultDialog() == JFileChooser.APPROVE_OPTION) {
			// 获得当前设置的参数值，赋值给默认参数：parameter
			setCoordSysTransParameterValue();
			String filePath = fileChooseExport.getFilePath();
			if (!StringUtilities.isNullOrEmptyString(filePath)) {
				try {
					File file = new File(filePath);
					FileUtilities.createFile(file);
					FileUtilities.writeToFile(file, parameter.toXML());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void buttonOKClicked() {
		this.method = CoordSysTransMethodUtilities.valueOf(this.comboBoxMethod.getSelectedItem().toString());
		setCoordSysTransParameterValue();
		this.dialogResult = DialogResult.OK;
		setVisible(false);
	}

	/**
	 *
	 */
	private void buttonCancelClicked() {
		// 点击取消按钮时，恢复textField的默认显示
		this.comboBoxMethod.setSelectedIndex(0);
		this.textFieldScaleDifference.setValue(0);
		this.textFieldRotationX.setValue(0);
		this.textFieldRotationY.setValue(0);
		this.textFieldRotationZ.setValue(0);
		this.textFieldTranslateX.setValue(0);
		this.textFieldTranslateY.setValue(0);
		this.textFieldTranslateZ.setValue(0);
		this.dialogResult = DialogResult.CANCEL;
		setVisible(false);
	}

	private void setCoordSysTransParameterValue() {
		this.parameter.setScaleDifference(Double.valueOf(this.textFieldScaleDifference.getValue().toString()));

		// 旋转角度单位与组件保持一致，需要是弧度，这里做一下转换。秒转为弧度
		this.parameter.setRotateX(Double.valueOf(this.textFieldRotationX.getValue().toString()) / 60 / 60 / 180 * Math.PI);
		this.parameter.setRotateY(Double.valueOf(this.textFieldRotationY.getValue().toString()) / 60 / 60 / 180 * Math.PI);
		this.parameter.setRotateZ(Double.valueOf(this.textFieldRotationZ.getValue().toString()) / 60 / 60 / 180 * Math.PI);

		this.parameter.setTranslateX(Double.valueOf(this.textFieldTranslateX.getValue().toString()));
		this.parameter.setTranslateY(Double.valueOf(this.textFieldTranslateY.getValue().toString()));
		this.parameter.setTranslateZ(Double.valueOf(this.textFieldTranslateZ.getValue().toString()));
	}
}
