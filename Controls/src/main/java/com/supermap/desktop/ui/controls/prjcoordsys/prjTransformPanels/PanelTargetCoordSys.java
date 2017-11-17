package com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels;

import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.*;
import com.supermap.desktop.ui.controls.button.SmButton;
import com.supermap.desktop.ui.controls.prjcoordsys.JDialogPrjCoordSysSettings;
import com.supermap.desktop.utilities.FileUtilities;
import com.supermap.desktop.utilities.PrjCoordSysUtilities;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by yuanR on 2017/9/25 0025.
 * 投影转换.目标坐标系面板
 * 提供三种方式选择投影坐标系：来自数据源、选择、导入文件
 * 面板主要提供设置好的坐标系
 * <p>
 * 数据源屏蔽平面坐标系
 * 增加来自数据集投影选择-2017.10.23yuanR
 */
public class PanelTargetCoordSys extends JPanel {
	/**
	 *
	 */
	private JRadioButton radioButtonFromDatasource;
	private JRadioButton radioButtonFromDataset;
	private JRadioButton radioButtonPrjSetting;
	private JRadioButton radioButtonImportPrjFile;
	protected DatasourceComboBox datasource;
	protected DatasetComboBox datasetComboBox;
	private SmButton buttonPrjSetting;
	private JFileChooserControl fileChooser;
	private PanelCoordSysInfo panelCoordSysInfo;
	protected PrjCoordSys targetPrjCoordSys = null;
	private PrjCoordSys buttonSetPrjCoordSys = null;
	private PrjCoordSys importFilePrjCoordSys = null;
	protected DoSome doSome;


	private ItemListener radioButtonItemListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getSource().equals(radioButtonFromDatasource) || e.getSource().equals(radioButtonFromDataset)
					|| e.getSource().equals(radioButtonPrjSetting) || e.getSource().equals(radioButtonImportPrjFile)) {
				datasource.setEnabled(radioButtonFromDatasource.isSelected() || radioButtonFromDataset.isSelected());
				datasetComboBox.setEnabled(radioButtonFromDataset.isSelected());
				buttonPrjSetting.setEnabled(radioButtonPrjSetting.isSelected());
				fileChooser.setEnabled(radioButtonImportPrjFile.isSelected());
				// 坐标系来自数据源
				if (e.getSource().equals(radioButtonFromDatasource)) {
					resetDatasourceComboBox(Application.getActiveApplication().getWorkspace().getDatasources(), null);
					if (datasource.getSelectedDatasource() != null) {
						targetPrjCoordSys = datasource.getSelectedDatasource().getPrjCoordSys();
					} else {
						targetPrjCoordSys = null;
					}
				} else if (e.getSource().equals(radioButtonFromDataset)) {
					// 当选择了来源于数据集，此时对数据源不做限制
					if (datasource.getSelectedDatasource() != null) {
						datasource.resetComboBox(Application.getActiveApplication().getWorkspace().getDatasources(), datasource.getSelectedDatasource());
					} else {
						datasource.resetComboBox(Application.getActiveApplication().getWorkspace().getDatasources(), null);
					}
					if (datasetComboBox.getSelectedDataset() != null) {
						targetPrjCoordSys = datasetComboBox.getSelectedDataset().getPrjCoordSys();
					} else {
						targetPrjCoordSys = null;
					}
				} else if (e.getSource().equals(radioButtonPrjSetting)) {
					// 坐标系来自设置，当设置过一次后用buttonSetprjCoordSys记录上次设置的坐标系
					targetPrjCoordSys = buttonSetPrjCoordSys;
				} else if (e.getSource().equals(radioButtonImportPrjFile)) {
					// 坐标系来自导入的文件
					targetPrjCoordSys = importFilePrjCoordSys;
				}
				// 点击单选框，
				setPrjCoordSysInfo(targetPrjCoordSys);
			}
		}
	};
	private ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {

			// 当点击了坐标系设置，并且设置了坐标系
			JDialogPrjCoordSysSettings dialogPrjCoordSysSettings = new JDialogPrjCoordSysSettings();
			dialogPrjCoordSysSettings.removeNONERRTHRoot();
			if (dialogPrjCoordSysSettings.showDialog() == DialogResult.OK) {
				if (dialogPrjCoordSysSettings.getPrjCoordSys().getType() != PrjCoordSysType.PCS_NON_EARTH) {
					buttonSetPrjCoordSys = dialogPrjCoordSysSettings.getPrjCoordSys();
					setPrjCoordSysInfo(buttonSetPrjCoordSys);
				}
			}
		}
	};

	private ItemListener comboBoxItemListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getSource() == datasource) {

				if (datasource.getSelectedDatasource() != null) {
					datasetComboBox.removeItemListener(comboBoxItemListener);
					resetDatasetComboBox(datasource.getSelectedDatasource(), null);
					datasetComboBox.addItemListener(comboBoxItemListener);
					if (radioButtonFromDatasource.isSelected()) {
						targetPrjCoordSys = datasource.getSelectedDatasource().getPrjCoordSys();
					} else {
						if (datasetComboBox.getSelectedDataset() != null) {
							targetPrjCoordSys = datasetComboBox.getSelectedDataset().getPrjCoordSys();
						} else {
							targetPrjCoordSys = null;
						}
					}
				} else {
					datasetComboBox.removeAllItems();
					datasetComboBox.setSelectedIndex(-1);
				}
			} else if (e.getSource() == datasetComboBox) {

				if (datasetComboBox.getSelectedDataset() != null) {
					targetPrjCoordSys = datasetComboBox.getSelectedDataset().getPrjCoordSys();
				} else {
					targetPrjCoordSys = null;
				}
			}
			setPrjCoordSysInfo(targetPrjCoordSys);
		}
	};


	/**
	 * 当导入文件路径改变时触发监听事件，此时重新获得路径下文件的坐标系
	 */
	private FileChooserPathChangedListener exportPathDocumentListener = new FileChooserPathChangedListener() {
		@Override
		public void pathChanged() {
			importFilePrjCoordSys = getPrjCoordSysFromImportFile();
			setPrjCoordSysInfo(importFilePrjCoordSys);
		}
	};

	public PanelTargetCoordSys(DoSome doSome) {
		this.doSome = doSome;
		initializeComponents();
		initializeResources();
		initializeLayout();
		initListener();
		initStates();
	}


	private void initializeComponents() {
		this.radioButtonFromDatasource = new JRadioButton();
		this.radioButtonFromDataset = new JRadioButton();
		this.radioButtonPrjSetting = new JRadioButton();
		this.radioButtonImportPrjFile = new JRadioButton();

		ButtonGroup bufferTypeButtonGroup = new ButtonGroup();
		bufferTypeButtonGroup.add(this.radioButtonFromDatasource);
		bufferTypeButtonGroup.add(this.radioButtonFromDataset);
		bufferTypeButtonGroup.add(this.radioButtonPrjSetting);
		bufferTypeButtonGroup.add(this.radioButtonImportPrjFile);

		this.datasource = new DatasourceComboBox();
		this.datasetComboBox = new DatasetComboBox();
		// 获得有投影坐标系的数据源
		resetDatasourceComboBox(Application.getActiveApplication().getWorkspace().getDatasources(), null);
		resetDatasetComboBox(this.datasource.getSelectedDatasource(), null);

		this.buttonPrjSetting = new SmButton();

		String moduleName = "ImportPrjFile";
		if (!SmFileChoose.isModuleExist(moduleName)) {
			String fileFilters = SmFileChoose.buildFileFilters(
					SmFileChoose.createFileFilter(ControlsProperties.getString("String_ImportPrjFiles"), "prj", "xml"),
					SmFileChoose.createFileFilter(ControlsProperties.getString("String_ImportPrjFileShape"), "prj"),
					SmFileChoose.createFileFilter(ControlsProperties.getString("String_ImportPrjFileXml"), "xml"));
			SmFileChoose.addNewNode(fileFilters, CoreProperties.getString("String_DefaultFilePath"),
					ControlsProperties.getString("String_ImportPrjFile"), moduleName, "OpenMany");
		}
		SmFileChoose smFileChoose = new SmFileChoose(moduleName);
		this.fileChooser = new JFileChooserControl();
		this.fileChooser.setEnabled(false);
		this.fileChooser.setFileChooser(smFileChoose);

		this.panelCoordSysInfo = new PanelCoordSysInfo("", true);
	}

	private void initializeResources() {
		this.radioButtonFromDatasource.setText(ControlsProperties.getString("String_Label_FromDatasource"));
		this.radioButtonFromDataset.setText(ControlsProperties.getString("String_Label_FromDataset"));
		this.radioButtonPrjSetting.setText(ControlsProperties.getString("String_Label_ProjectionSetting"));
		this.radioButtonImportPrjFile.setText(ControlsProperties.getString("String_Label_ImportPrjCoordSysFile"));
		this.buttonPrjSetting.setText(ControlsProperties.getString("String_Button_Setting"));
	}

	private void initializeLayout() {
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setAutoCreateContainerGaps(true);
		groupLayout.setAutoCreateGaps(true);
		this.setLayout(groupLayout);
		// @formatter:off
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
				.addGroup(groupLayout.createSequentialGroup()
						.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(this.radioButtonFromDatasource)
								.addComponent(this.radioButtonFromDataset)
								.addComponent(this.radioButtonPrjSetting)
								.addComponent(this.radioButtonImportPrjFile))
						.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(this.datasource)
								.addComponent(this.datasetComboBox)
								.addComponent(this.buttonPrjSetting, 0, 0, Short.MAX_VALUE)
								.addComponent(this.fileChooser)))
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(this.panelCoordSysInfo)));
		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.radioButtonFromDatasource)
						.addComponent(this.datasource, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.radioButtonFromDataset)
						.addComponent(this.datasetComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.radioButtonPrjSetting)
						.addComponent(this.buttonPrjSetting, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.radioButtonImportPrjFile)
						.addComponent(this.fileChooser, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(this.panelCoordSysInfo));
		// @formatter:on
	}

	private void initListener() {
		removeListener();
		this.datasource.addItemListener(this.comboBoxItemListener);
		this.datasetComboBox.addItemListener(this.comboBoxItemListener);
		this.radioButtonFromDatasource.addItemListener(this.radioButtonItemListener);
		this.radioButtonFromDataset.addItemListener(this.radioButtonItemListener);
		this.radioButtonPrjSetting.addItemListener(this.radioButtonItemListener);
		this.radioButtonImportPrjFile.addItemListener(this.radioButtonItemListener);
		this.buttonPrjSetting.addActionListener(this.actionListener);
		this.fileChooser.addFileChangedListener(this.exportPathDocumentListener);
	}

	private void removeListener() {
		this.datasource.removeItemListener(this.comboBoxItemListener);
		this.datasetComboBox.removeItemListener(this.comboBoxItemListener);
		this.radioButtonFromDatasource.removeItemListener(this.radioButtonItemListener);
		this.radioButtonFromDataset.removeItemListener(this.radioButtonItemListener);
		this.radioButtonPrjSetting.removeItemListener(this.radioButtonItemListener);
		this.radioButtonImportPrjFile.removeItemListener(this.radioButtonItemListener);
		this.buttonPrjSetting.removeActionListener(this.actionListener);
		this.fileChooser.removePathChangedListener(this.exportPathDocumentListener);

	}


	/**
	 * 初始值设置
	 * 默认使用数据源的坐标系，当打开的数据源没有坐标系时，坐标系获取方式为设置坐标系
	 */
	private void initStates() {
		this.datasetComboBox.setEnabled(false);
		if (this.datasource.getSelectedDatasource() != null) {
			this.radioButtonFromDatasource.setSelected(true);
			this.buttonPrjSetting.setEnabled(false);
			setPrjCoordSysInfo(this.datasource.getSelectedDatasource().getPrjCoordSys());
		} else {
			this.datasource.setEnabled(false);
			this.radioButtonPrjSetting.setSelected(true);
			setPrjCoordSysInfo(null);
		}
	}

	/**
	 * 设置投影信息
	 *
	 * @param prjCoordSysInfo
	 */
	private void setPrjCoordSysInfo(PrjCoordSys prjCoordSysInfo) {
		this.targetPrjCoordSys = prjCoordSysInfo;
		this.panelCoordSysInfo.setCoordInfo(PrjCoordSysUtilities.getDescription(prjCoordSysInfo));
		if (this.doSome != null) {
			// 通过doSome，将改变后的targetPrjCoordSys值传给主面板
			this.doSome.setTargetPrjCoordSys(this.targetPrjCoordSys);
			// 如果值为空，控制主面板确定按钮不可用
			this.doSome.setOKButtonEnabled(this.targetPrjCoordSys != null);
		}
	}

	/**
	 * 通过导入的投影文件获得文件投影信息
	 *
	 * @return
	 */
	private PrjCoordSys getPrjCoordSysFromImportFile() {

		if (!new File(this.fileChooser.getPath()).exists()) {
			return null;
		} else {
			PrjCoordSys newPrjCoorSys = new PrjCoordSys();
			String fileType = FileUtilities.getFileType(this.fileChooser.getPath());
			boolean isPrjFile;
			if (fileType.equalsIgnoreCase(".prj")) {
				isPrjFile = newPrjCoorSys.fromFile(this.fileChooser.getPath(), PrjFileType.ESRI);
			} else {
				isPrjFile = newPrjCoorSys.fromFile(this.fileChooser.getPath(), PrjFileType.SUPERMAP);
			}
			// 去除导入平面无投影坐标系
			if (isPrjFile && newPrjCoorSys.getType() != PrjCoordSysType.PCS_NON_EARTH) {
				return newPrjCoorSys;
			} else {
				return null;
			}
		}
	}

	/**
	 * 当tree中数据改变时，当前面板comboBox中的项也发生改变
	 * comboBox中的项过滤掉平面坐标系的数据源
	 *
	 * @param datasources
	 * @param selectedDatasource
	 */
	protected void resetDatasourceComboBox(Datasources datasources, Datasource selectedDatasource) {
		// 获得有投影坐标系的数据源
		ArrayList<Datasource> datasourceArray = new ArrayList<>();
		if (datasources.getCount() > 0) {
			for (int i = 0; i < datasources.getCount(); i++) {
				if (!datasources.get(i).getPrjCoordSys().getType().equals(PrjCoordSysType.PCS_NON_EARTH)) {
					datasourceArray.add(datasources.get(i));
				}
			}
		}
		if (null != datasource && datasourceArray.size() > 0) {
			datasource.resetComboBox(datasourceArray, selectedDatasource);
		} else {
			datasource.removeAllItems();
			datasource.setSelectedIndex(-1);
		}
	}

	/**
	 * 当tree中数据改变时，当前面板comboBox中的项也发生改变
	 * comboBox中的项过滤掉平面坐标系的数据源
	 *
	 * @param datasource
	 * @param selectedDatasource
	 */
	protected void resetDatasetComboBox(Datasource datasource, Datasource selectedDatasource) {
		// 获得有投影坐标系的数据源
		if (null != datasource) {
			datasetComboBox.setDatasets(datasource.getDatasets());
			for (int i = 0; i < datasource.getDatasets().getCount(); i++) {
				if (datasetComboBox.getDatasets().get(i).getPrjCoordSys().getType() == PrjCoordSysType.PCS_NON_EARTH) {
					datasetComboBox.removeItem(datasource.getDatasets().get(i));
				}
			}
		}
	}
}
