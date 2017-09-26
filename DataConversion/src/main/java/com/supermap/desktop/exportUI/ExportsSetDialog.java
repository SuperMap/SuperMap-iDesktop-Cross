package com.supermap.desktop.exportUI;

import com.supermap.data.conversion.ExportSetting;
import com.supermap.desktop.Interface.IExportSettingFactory;
import com.supermap.desktop.Interface.IPanelModel;
import com.supermap.desktop.baseUI.PanelExportTransform;
import com.supermap.desktop.controls.utilities.ComponentFactory;
import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.dataconversion.DataConversionProperties;
import com.supermap.desktop.iml.ExportFileInfo;
import com.supermap.desktop.iml.ExportSettingFactory;
import com.supermap.desktop.localUtilities.CommonUtilities;
import com.supermap.desktop.localUtilities.FiletypeUtilities;
import com.supermap.desktop.localUtilities.LocalFileUtilities;
import com.supermap.desktop.properties.CommonProperties;
import com.supermap.desktop.ui.controls.*;
import com.supermap.desktop.utilities.FileUtilities;
import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by xie on 2016/11/17.
 * 导出统一设置界面
 */
public class ExportsSetDialog extends SmDialog implements IPanelModel {
	private JCheckBox checkBoxFileType;
	private JComboBox comboBoxFileType;
	private JCheckBox checkBoxOverwirte;
	private JRadioButton radioButtonOK;
	private JRadioButton radioButtonNO;
	private JCheckBox checkBoxExportPath;
	private JFileChooserControl fileChooserControlExportPath;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JPanel panelContent;
	private DataExportDialog owner;

	private ItemListener checkBoxListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getSource().equals(checkBoxFileType)) {
				comboBoxFileType.setEnabled(checkBoxFileType.isSelected());
			} else if (e.getSource().equals(checkBoxOverwirte)) {
				radioButtonOK.setEnabled(checkBoxOverwirte.isSelected());
				radioButtonNO.setEnabled(checkBoxOverwirte.isSelected());
			} else if (e.getSource().equals(checkBoxExportPath)) {
				fileChooserControlExportPath.setEnabled(checkBoxExportPath.isSelected());
			}
		}
	};

	private ActionListener exportsSetListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String targetFileType = null;
			Boolean targetOverWrite = null;
			String targetFilePath = null;
			if (checkBoxFileType.isSelected()) {
				targetFileType = comboBoxFileType.getSelectedItem().toString();
			}
			if (checkBoxOverwirte.isSelected()) {
				if (radioButtonOK.isSelected()) {
					targetOverWrite = true;
				} else if (radioButtonNO.isSelected()) {
					targetOverWrite = false;
				}
			}
			if (checkBoxExportPath.isSelected()) {
				targetFilePath = fileChooserControlExportPath.getPath();
			}

			if (null != targetFileType) {
				resetTableAndFileInfos(owner.COLUMN_EXPORTTYPE, targetFileType);
			}
			if (null != targetOverWrite) {
				resetTableAndFileInfos(owner.COLUMN_ISOVERWRITE, targetOverWrite);
			}
			if (null != targetFilePath) {
				resetTableAndFileInfos(owner.COLUMN_FILEPATH, targetFilePath);
			}

			ExportsSetDialog.this.dispose();
		}
	};
	private FileChooserPathChangedListener setExportPathListener = new FileChooserPathChangedListener() {
		@Override
		public void pathChanged() {
			String directories = fileChooserControlExportPath.getPath();
			if (FileUtilities.isFilePath(directories)) {
				fileChooserControlExportPath.setPath(directories);
			}
		}
	};

	private void resetTableAndFileInfos(int columnExporttype, Object targetFileType) {
		ArrayList<PanelExportTransform> fileInfos = owner.getPanelExports();
		JTable tableExports = owner.getTableExport();
		int[] selectrows = tableExports.getSelectedRows();
		int size = selectrows.length;
		for (int i = 0; i < size; i++) {
			tableExports.setValueAt(targetFileType, selectrows[i], columnExporttype);
			if (columnExporttype == owner.COLUMN_EXPORTTYPE) {
				//替换修改的行所在的界面
				Object fileType = FiletypeUtilities.getFileType(targetFileType.toString());
				ExportFileInfo fileInfo = fileInfos.get(selectrows[i]).getExportsFileInfo();
				fileInfo.setFileType(fileType);
				IExportSettingFactory exportSettingFactory = new ExportSettingFactory();
				ExportSetting newExportsetting = exportSettingFactory.createExportSetting(fileType);
				owner.replaceExportPanelForFileType(newExportsetting, fileInfo.getExportSetting(), fileInfo, selectrows[i]);
			}
		}
	}

	private ActionListener cancelListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			removeEvents();
			ExportsSetDialog.this.dispose();
		}
	};
	private ActionListener radioListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(radioButtonOK)) {
				radioButtonNO.setSelected(!radioButtonOK.isSelected());
			} else {
				radioButtonOK.setSelected(!radioButtonNO.isSelected());
			}
		}
	};

	public ExportsSetDialog(DataExportDialog owner) {
		this.owner = owner;
		init(owner);
		setComponentName();
	}

	private void init(DataExportDialog owner) {
		initComponents();
		initLayerout();
		initResources();
		registEvents();
		this.getRootPane().setDefaultButton(this.buttonOK);
		this.setSize(new Dimension(400, 180));
		this.setLocationRelativeTo(owner);
		this.setVisible(true);
	}

	public void setComponentName() {
		ComponentUIUtilities.setName(this.checkBoxFileType, "ExportsSetDialog_checkBoxFileType");
		ComponentUIUtilities.setName(this.comboBoxFileType, "ExportsSetDialog_comboBoxFileType");
		ComponentUIUtilities.setName(this.checkBoxOverwirte, "ExportsSetDialog_checkBoxOverwirte");
		ComponentUIUtilities.setName(this.radioButtonOK, "ExportsSetDialog_radioButtonOK");
		ComponentUIUtilities.setName(this.radioButtonNO, "ExportsSetDialog_radioButtonNO");
		ComponentUIUtilities.setName(this.checkBoxExportPath, "ExportsSetDialog_checkBoxExportPath");
		ComponentUIUtilities.setName(this.fileChooserControlExportPath, "ExportsSetDialog_fileChooserControlExportPath");
		ComponentUIUtilities.setName(this.buttonOK, "ExportsSetDialog_buttonOK");
		ComponentUIUtilities.setName(this.buttonCancel, "ExportsSetDialog_buttonCancel");
		ComponentUIUtilities.setName(this.panelContent, "ExportsSetDialog_panelContent");
		ComponentUIUtilities.setName(this.owner, "ExportsSetDialog_owner");
	}

	@Override
	public void initComponents() {
		this.checkBoxFileType = new JCheckBox();
		this.comboBoxFileType = new JComboBox();
		this.checkBoxOverwirte = new JCheckBox();
		this.radioButtonOK = new JRadioButton();
		this.radioButtonNO = new JRadioButton();
		this.checkBoxExportPath = new JCheckBox();
		this.fileChooserControlExportPath = new JFileChooserControl();
		SmFileChoose tempfileChooser = LocalFileUtilities.createExportFileChooser(fileChooserControlExportPath.getPath());
		fileChooserControlExportPath.setFileChooser(tempfileChooser);
		this.buttonOK = ComponentFactory.createButtonOK();
		this.buttonCancel = ComponentFactory.createButtonCancel();
		CommonUtilities.setComboBoxTheme(comboBoxFileType);
		setDefaultState();
		fillComponentsInfo();
	}

	private void fillComponentsInfo() {
		int[] selectRows = owner.getTableExport().getSelectedRows();
		ArrayList<PanelExportTransform> fileInfos = owner.getPanelExports();
		ArrayList<ExportFileInfo> selectFileInfos = new ArrayList<>();
		int size = selectRows.length;
		for (int i = 0; i < size; i++) {
			selectFileInfos.add(fileInfos.get(selectRows[i]).getExportsFileInfo());
		}
		addSameItems(selectFileInfos);
		Boolean sameResult = getSameResult(selectFileInfos);
		if (null != sameResult) {
			if (true == sameResult) {
				radioButtonOK.setSelected(true);
				radioButtonNO.setSelected(false);
			} else {
				radioButtonNO.setSelected(true);
				radioButtonOK.setSelected(false);
			}
		}
		addSamePath(selectFileInfos);
	}

	private void addSamePath(ArrayList<ExportFileInfo> selectFileInfos) {
		int size = selectFileInfos.size();
		if (size > 0) {
			if (1 == size) {
				fileChooserControlExportPath.setPath(selectFileInfos.get(0).getFilePath());
				return;
			}
			String filePath = selectFileInfos.get(0).getFilePath();
			boolean hasSamePath = true;
			for (int i = 1; i < size; i++) {
				String temp = selectFileInfos.get(i).getFilePath();
				if (!filePath.equals(temp)) {
					hasSamePath = false;
					break;
				}
			}
			if (hasSamePath) {
				fileChooserControlExportPath.setPath(filePath);
			}
		}
	}

	private void addSameItems(ArrayList<ExportFileInfo> selectFileInfos) {
		ArrayList<String> sameFileType = CommonUtilities.getSameFileTypes(selectFileInfos);
		int fileTypeSize = sameFileType.size();
		if (0 == fileTypeSize) {
			this.checkBoxFileType.setEnabled(false);
		} else {
			this.checkBoxFileType.setEnabled(true);
			for (int i = 0; i < fileTypeSize; i++) {
				String datasetName = CommonUtilities.getDatasetName(sameFileType.get(i));
				if (!StringUtilities.isNullOrEmpty(datasetName)) {
					this.comboBoxFileType.addItem(datasetName);
				}
			}
		}
	}

	private Boolean getSameResult(ArrayList<ExportFileInfo> selectFileInfos) {
		Boolean result = null;
		int overwriteCount = 0;
		int disOverwriteCount = 0;
		int size = selectFileInfos.size();
		if (1 == size) {
			return selectFileInfos.get(0).getExportSetting().isOverwrite();
		}
		for (int i = 0; i < size; i++) {
			ExportSetting temp = selectFileInfos.get(i).getExportSetting();
			if (temp.isOverwrite()) {
				overwriteCount++;
			} else {
				disOverwriteCount++;
			}
		}
		if (overwriteCount == size) {
			result = true;
		} else if (disOverwriteCount == size) {
			result = false;
		}
		return result;
	}

	private void setDefaultState() {
		this.comboBoxFileType.setEnabled(false);
		this.radioButtonNO.setEnabled(false);
		this.radioButtonOK.setEnabled(false);
		this.fileChooserControlExportPath.setEnabled(false);
	}

	@Override
	public void initLayerout() {
		JPanel panelButton = new JPanel();
		panelButton.setLayout(new GridBagLayout());
		panelButton.add(this.buttonOK, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.EAST).setWeight(0, 0).setInsets(5, 0, 0, 10));
		panelButton.add(this.buttonCancel, new GridBagConstraintsHelper(1, 0, 1, 1).setAnchor(GridBagConstraints.EAST).setWeight(0, 0).setInsets(5, 0, 0, 10));
		panelContent = new JPanel();
		this.setContentPane(panelContent);
		panelContent.setLayout(new GridBagLayout());
		panelContent.add(this.checkBoxFileType, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 10, 5, 10).setFill(GridBagConstraints.NONE).setWeight(1, 0));
		panelContent.add(this.comboBoxFileType, new GridBagConstraintsHelper(1, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 10).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
		panelContent.add(this.checkBoxOverwirte, new GridBagConstraintsHelper(0, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 10, 5, 10).setFill(GridBagConstraints.NONE).setWeight(1, 0));
		panelContent.add(this.radioButtonOK, new GridBagConstraintsHelper(1, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 10).setFill(GridBagConstraints.NONE).setWeight(1, 0));
		panelContent.add(this.radioButtonNO, new GridBagConstraintsHelper(2, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 10).setFill(GridBagConstraints.NONE).setWeight(1, 0));
		panelContent.add(this.checkBoxExportPath, new GridBagConstraintsHelper(0, 2, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 10, 5, 10).setFill(GridBagConstraints.NONE).setWeight(1, 0));
		panelContent.add(this.fileChooserControlExportPath, new GridBagConstraintsHelper(1, 2, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 10).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
		panelContent.add(panelButton, new GridBagConstraintsHelper(0, 3, 3, 1).setAnchor(GridBagConstraints.EAST).setWeight(0, 0));
	}

	private void initResources() {
		this.setTitle(CommonProperties.getString("String_ToolBar_SetBatch"));
		this.checkBoxFileType.setText(DataConversionProperties.getString("String_ExportType"));
		this.checkBoxOverwirte.setText(DataConversionProperties.getString("String_OverWrite"));
		this.radioButtonOK.setText(CommonProperties.getString(CommonProperties.yes));
		this.radioButtonNO.setText(CommonProperties.getString(CommonProperties.no));
		this.checkBoxExportPath.setText(DataConversionProperties.getString("String_ExportPath"));
	}

	@Override
	public void registEvents() {
		removeEvents();
		this.checkBoxFileType.addItemListener(this.checkBoxListener);
		this.checkBoxOverwirte.addItemListener(this.checkBoxListener);
		this.checkBoxExportPath.addItemListener(this.checkBoxListener);
		this.radioButtonOK.addActionListener(this.radioListener);
		this.radioButtonNO.addActionListener(this.radioListener);
		this.fileChooserControlExportPath.addFileChangedListener(this.setExportPathListener);
		this.buttonOK.addActionListener(this.exportsSetListener);
		this.buttonCancel.addActionListener(this.cancelListener);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				removeEvents();
			}
		});
	}

	@Override
	public void removeEvents() {
		this.checkBoxFileType.removeItemListener(this.checkBoxListener);
		this.checkBoxOverwirte.removeItemListener(this.checkBoxListener);
		this.checkBoxExportPath.removeItemListener(this.checkBoxListener);
		this.fileChooserControlExportPath.removePathChangedListener(this.setExportPathListener);
		this.buttonOK.removeActionListener(this.exportsSetListener);
		this.buttonCancel.removeActionListener(this.cancelListener);
	}
}