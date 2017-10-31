package com.supermap.desktop.ui.controls.prjcoordsys;

import com.supermap.data.*;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.ui.controls.SmDialog;
import com.supermap.desktop.ui.controls.borderPanel.PanelButton;
import com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels.DoSome;
import com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels.PanelPointCoordSysTranslator;
import com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels.PanelReferSysTransSettings;
import com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels.PanelTargetCoordSys;
import com.supermap.desktop.utilities.DoubleUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by yuanR on 2017/10/12 0012.
 * 坐标点转换功能
 */
public class JDialogPointPrjTranslator extends SmDialog {
	// 源坐标点/结果坐标点
	private PanelPointCoordSysTranslator panelPointCoordSysTranslatorSource = new PanelPointCoordSysTranslator();
	private PanelPointCoordSysTranslator panelPointCoordSysTranslatorResult = new PanelPointCoordSysTranslator();
	// 源坐标系/结果坐标系
	private PanelTargetCoordSys panelTargetCoordSysSource;
	private PanelTargetCoordSys panelTargetCoordSysResult;
	// 参照系转换设置
	private PanelReferSysTransSettings panelReferSysTransSettings;

	private PanelButton panelButton = new PanelButton();

	private Boolean isSourcePrjHas = false;
	private Boolean isResultPrjHas = false;

	private PrjCoordSys prjCoordSysSource = null;
	private PrjCoordSys prjCoordSysResult = null;

	/**
	 * 源坐标系面板响应
	 */
	private DoSome doSomeSource = new DoSome() {
		@Override
		public void setTargetPrjCoordSys(PrjCoordSys targetPrjCoordSys) {
			if (targetPrjCoordSys != null) {
				prjCoordSysSource = targetPrjCoordSys;

				Unit selectedUnit = prjCoordSysSource.getCoordUnit();
				if (selectedUnit.equals(Unit.DEGREE)) {
					panelPointCoordSysTranslatorSource.setCurrentModel(2);
				} else {
					panelPointCoordSysTranslatorSource.setCurrentModel(1);
				}
			}

		}

		@Override
		public void setOKButtonEnabled(boolean isEnabled) {
			isSourcePrjHas = isEnabled;
			panelButton.getButtonOk().setEnabled(isEnabled && isResultPrjHas);
		}
	};


	/**
	 * 目标坐标系面板响应
	 */
	private DoSome doSomeResult = new DoSome() {
		@Override
		public void setTargetPrjCoordSys(PrjCoordSys targetPrjCoordSys) {
			if (targetPrjCoordSys != null) {
				prjCoordSysResult = targetPrjCoordSys;
				Unit selectedUnit = prjCoordSysResult.getCoordUnit();
				if (selectedUnit.equals(Unit.DEGREE)) {
					panelPointCoordSysTranslatorResult.setCurrentModel(2);
				} else {
					panelPointCoordSysTranslatorResult.setCurrentModel(1);
				}
			}
		}

		@Override
		public void setOKButtonEnabled(boolean isEnabled) {
			isResultPrjHas = isEnabled;
			panelButton.getButtonOk().setEnabled(isEnabled && isSourcePrjHas);
		}
	};

	/**
	 *
	 */
	private ActionListener OKAndCancelActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(panelButton.getButtonOk())) {
				Translator();
				dialogResult = DialogResult.OK;
			} else {
				dialogResult = DialogResult.CANCEL;
				JDialogPointPrjTranslator.this.dispose();
			}
		}
	};

	/**
	 * 点坐标转换功能实现
	 */
	private Boolean Translator() {
		Boolean result = false;
		try {
			if (prjCoordSysSource != null && prjCoordSysResult != null) {
				double xValue = panelPointCoordSysTranslatorSource.getXValue();
				double yValue = panelPointCoordSysTranslatorSource.getYValue();
				Point2Ds point2Ds = new Point2Ds();
				point2Ds.add(new Point2D(xValue, yValue));
				result = CoordSysTranslator.convert(point2Ds, prjCoordSysSource, prjCoordSysResult, panelReferSysTransSettings.getParameter(), panelReferSysTransSettings.getMethod());

				if (result) {
					panelPointCoordSysTranslatorResult.setXValue(DoubleUtilities.toString(point2Ds.getItem(0).getX(), 15));
					panelPointCoordSysTranslatorResult.setYValue(DoubleUtilities.toString(point2Ds.getItem(0).getY(), 15));
				}
			}
		} catch (Exception e) {

		}
		return result;
	}

	public JDialogPointPrjTranslator() {
		initializeComponents();
		initializeResources();
		initializeLayout();
		initStates();
		initListener();

		setSize(800, 600);
		setLocationRelativeTo(null);
	}


	private void initializeComponents() {
		this.panelTargetCoordSysSource = new PanelTargetCoordSys(this.doSomeSource);
		this.panelTargetCoordSysResult = new PanelTargetCoordSys(this.doSomeResult);
		this.panelReferSysTransSettings = new PanelReferSysTransSettings("HORIZONTAL");
	}

	private void initializeResources() {
		this.setTitle(ControlsProperties.getString("String_Title_PointCoordSysTranslator"));
		this.panelPointCoordSysTranslatorSource.setBorder(BorderFactory.createTitledBorder(ControlsProperties.getString("String_GroupBox_SrcPoint")));
		this.panelPointCoordSysTranslatorResult.setBorder(BorderFactory.createTitledBorder(ControlsProperties.getString("String_GroupBox_TarPoint")));
		this.panelTargetCoordSysSource.setBorder(BorderFactory.createTitledBorder(ControlsProperties.getString("String_GroupBox_SrcCoordSys")));
		this.panelTargetCoordSysResult.setBorder(BorderFactory.createTitledBorder(ControlsProperties.getString("String_GroupBox_TarCoorSys")));
		this.panelReferSysTransSettings.setBorder(BorderFactory.createTitledBorder(ControlsProperties.getString("String_GroupBox_CoordSysTranslatorSetting")));
		this.panelButton.getButtonOk().setText(ControlsProperties.getString("String_Button_Conversion"));
	}

	private void initializeLayout() {
		JPanel mianPanel = new JPanel();
		mianPanel.setLayout(new GridBagLayout());
		mianPanel.add(this.panelPointCoordSysTranslatorSource, new GridBagConstraintsHelper(0, 0, 1, 1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setInsets(10, 5, 0, 0).setWeight(1, 0));
		mianPanel.add(this.panelPointCoordSysTranslatorResult, new GridBagConstraintsHelper(1, 0, 1, 1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setInsets(10, 0, 0, 5).setWeight(1, 0));
		mianPanel.add(this.panelTargetCoordSysSource, new GridBagConstraintsHelper(0, 1, 1, 1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setInsets(0, 5, 0, 0).setWeight(1, 1));
		mianPanel.add(this.panelTargetCoordSysResult, new GridBagConstraintsHelper(1, 1, 1, 1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setInsets(0, 0, 0, 5).setWeight(1, 1));
		mianPanel.add(this.panelReferSysTransSettings, new GridBagConstraintsHelper(0, 2, 2, 1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setInsets(0, 5, 10, 5).setWeight(1, 0));

		this.setLayout(new GridBagLayout());
		this.add(mianPanel, new GridBagConstraintsHelper(0, 0, 1, 1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setWeight(1, 1));
		this.add(this.panelButton, new GridBagConstraintsHelper(0, 1, 1, 1).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.EAST).setWeight(1, 0));
	}

	private void initStates() {
		// 结果坐标点面板不可用，仅作显示
		this.panelPointCoordSysTranslatorResult.setComponentEditable(false);
	}

	private void initListener() {
		removeListener();
		this.panelButton.getButtonOk().addActionListener(OKAndCancelActionListener);
		this.panelButton.getButtonCancel().addActionListener(OKAndCancelActionListener);

	}

	private void removeListener() {
		this.panelButton.getButtonOk().removeActionListener(OKAndCancelActionListener);
		this.panelButton.getButtonCancel().removeActionListener(OKAndCancelActionListener);
	}
}
