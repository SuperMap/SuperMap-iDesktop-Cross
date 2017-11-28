package com.supermap.desktop.ui.controls;

import com.supermap.data.Dataset;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.parameters.ParameterPanels.JPanelDatasetChooseForParameter;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.borderPanel.PanelButton;
import com.supermap.desktop.utilities.DatasetTypeUtilities;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by yuanR on 2017/11/28 0028.
 * 存放数据集集合的面板
 */
public class DatasetChooseDialog extends SmDialog {

	private final String[] columnNames = {"", CoreProperties.getString("String_Dataset"), CoreProperties.getString("String_Datasource")};
	private final boolean[] enables = {false, false, false};
	ArrayList<Dataset> datasets;
	private JPanelDatasetChooseForParameter panelDatasetChooseForParameter;
	private PanelButton panelButton;
	private ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(panelButton.getButtonOk())) {
				setDialogResult(DialogResult.OK);
				dispose();
			} else if (e.getSource().equals(panelButton.getButtonCancel())) {
				setDialogResult(DialogResult.CANCEL);
				dispose();
			}
		}
	};

	public DatasetChooseDialog(ArrayList<Dataset> datasets) {
		this.datasets = datasets;
		initComponents();
		initLayout();
		intListener();
		initResource();

		setSize(460, 320);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);

		this.componentList.add(this.panelButton.getButtonOk());
		this.componentList.add(this.panelButton.getButtonCancel());
		this.setFocusTraversalPolicy(policy);
		this.getRootPane().setDefaultButton(this.panelButton.getButtonCancel());
	}


	private void initComponents() {
		this.panelDatasetChooseForParameter = new JPanelDatasetChooseForParameter(this.datasets, this.columnNames, this.enables);
		this.panelDatasetChooseForParameter.setSupportDatasetTypes(DatasetTypeUtilities.getDatasetTypeVector());
		this.panelButton = new PanelButton();
	}

	private void initLayout() {
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(this.panelDatasetChooseForParameter, BorderLayout.CENTER);
		this.getContentPane().add(this.panelButton, BorderLayout.SOUTH);
	}

	private void intListener() {
		removeListener();
		panelButton.getButtonOk().addActionListener(this.actionListener);
		panelButton.getButtonCancel().addActionListener(this.actionListener);
	}

	private void removeListener() {
		panelButton.getButtonOk().removeActionListener(this.actionListener);
		panelButton.getButtonCancel().removeActionListener(this.actionListener);
	}

	private void initResource() {
		this.setTitle(ControlsProperties.getString("String_SetDatasets"));
	}

	public ArrayList<Dataset> getDatasets() {
		return this.panelDatasetChooseForParameter.getDatasets();
	}
}
