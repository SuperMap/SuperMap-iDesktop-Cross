package com.supermap.desktop.importUI;

import com.supermap.data.conversion.ImportSetting;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.dataconversion.DataConversionProperties;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.ui.controls.SmDialog;
import com.supermap.desktop.ui.controls.button.SmButton;
import com.supermap.desktop.utilities.FileUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Created by xie on 2016/10/14.
 * 自定义的导入文件属性信息显示界面
 */
public class ImportFilePropertiesDialog extends SmDialog {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JLabel labelFileType = new JLabel();
	private JLabel labelFileLocation = new JLabel();
	private JLabel labelFileSize = new JLabel();
	private JLabel labelFileModify = new JLabel();
	private JLabel labelProperty = new JLabel();
	private JCheckBox checkboxHidden = new JCheckBox();
	private JLabel labelDate = new JLabel("date");
	private JLabel labelSize = new JLabel("size");
	private JLabel labelPath = new JLabel("location");
	private JLabel labelType = new JLabel("type");
	private SmButton buttonSure = new SmButton();
	private SmButton buttonQuit = new SmButton();
	private ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	};

	public ImportFilePropertiesDialog(ImportSetting importSetting) {
		initLayout();
		setFileInfo(importSetting);
		setLocationRelativeTo(null);
		this.componentList.add(this.buttonSure);
		this.componentList.add(this.buttonQuit);
		this.setFocusTraversalPolicy(policy);
		initResource();
		registEvents();
		setComponentName();
	}

	private void registEvents() {
		this.buttonSure.addActionListener(this.actionListener);
		this.buttonQuit.addActionListener(this.actionListener);
	}

	private void setFileInfo(ImportSetting importSetting) {
		if (null != importSetting) {
			String filePath = importSetting.getSourceFilePath();
			this.labelPath.setText(filePath);
			File file = new File(filePath);
			if (file.isDirectory()) {
				this.labelType.setText(CoreProperties.getString("String_Directory"));
			} else {
				this.labelType.setText(FileUtilities.getFileType(filePath));
			}
			this.labelSize.setText(parseFileSize(file.length()));
			String dateStr = new String(DataConversionProperties.getString("string_dataFormat_ch"));
			SimpleDateFormat sdf = new SimpleDateFormat(dateStr);
			this.labelDate.setText(sdf.format(file.lastModified()));
			this.checkboxHidden.setSelected(file.isHidden());
			this.checkboxHidden.setEnabled(false);
		}
	}

	private String parseFileSize(long size) {
		long kb = 1024;
		long mb = kb * 1024;
		long gb = mb * 1024;
		if (size >= gb) {
			return String.format("%.1f GB", (float) size / gb);
		} else if (size >= mb) {
			float f = (float) size / mb;
			return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
		} else if (size > kb) {
			float f = (float) size / kb;
			return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
		} else {
			return String.format("%d B", size);
		}
	}

	private void initLayout() {
		setBounds(100, 100, 600, 250);
		JPanel panelContent = (JPanel) this.getContentPane();
		panelContent.setLayout(new GridBagLayout());
		JPanel panelButton = new JPanel();
		panelButton.setLayout(new GridBagLayout());
		panelButton.add(this.buttonSure, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.EAST).setWeight(0, 0).setInsets(5, 0, 10, 5));
		panelButton.add(this.buttonQuit, new GridBagConstraintsHelper(1, 0, 1, 1).setAnchor(GridBagConstraints.EAST).setWeight(0, 0).setInsets(5, 0, 10, 10));
		panelContent.add(this.labelFileType, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(10, 20, 5, 0));
		panelContent.add(this.labelType, new GridBagConstraintsHelper(1, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 5, 10).setWeight(1, 0));
		panelContent.add(this.labelFileLocation, new GridBagConstraintsHelper(0, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(0, 20, 5, 0));
		panelContent.add(this.labelPath, new GridBagConstraintsHelper(1, 1, 2, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(0, 10, 5, 10).setWeight(1, 0));
		panelContent.add(this.labelFileSize, new GridBagConstraintsHelper(0, 2, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(0, 20, 5, 0));
		panelContent.add(this.labelSize, new GridBagConstraintsHelper(1, 2, 2, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(0, 10, 5, 10).setWeight(1, 0));

		panelContent.add(this.labelFileModify, new GridBagConstraintsHelper(0, 3, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(0, 20, 5, 0));
		panelContent.add(this.labelDate, new GridBagConstraintsHelper(1, 3, 2, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(0, 10, 5, 10).setWeight(1, 0));

		panelContent.add(this.labelProperty, new GridBagConstraintsHelper(0, 4, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(0, 20, 0, 10));
		panelContent.add(this.checkboxHidden, new GridBagConstraintsHelper(1, 4, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(0, 10, 0, 10));
		panelContent.add(new JPanel(), new GridBagConstraintsHelper(0, 5, 3, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.BOTH).setWeight(1, 1));

		panelContent.add(panelButton, new GridBagConstraintsHelper(0, 6, 3, 1).setAnchor(GridBagConstraints.EAST).setWeight(0, 0));
	}

	public void setComponentName() {
		ComponentUIUtilities.setName(this.labelFileType, "ImportFilePropertiesDialog_labelFileType");
		ComponentUIUtilities.setName(this.labelFileLocation, "ImportFilePropertiesDialog_labelFileLocation");
		ComponentUIUtilities.setName(this.labelFileSize, "ImportFilePropertiesDialog_labelFileSize");
		ComponentUIUtilities.setName(this.labelFileModify, "ImportFilePropertiesDialog_labelFileModify");
		ComponentUIUtilities.setName(this.labelProperty, "ImportFilePropertiesDialog_labelProperty");
		ComponentUIUtilities.setName(this.checkboxHidden, "ImportFilePropertiesDialog_checkboxHidden");
		ComponentUIUtilities.setName(this.labelDate, "ImportFilePropertiesDialog_labelDate");
		ComponentUIUtilities.setName(this.labelSize, "ImportFilePropertiesDialog_labelSize");
		ComponentUIUtilities.setName(this.labelPath, "ImportFilePropertiesDialog_labelPath");
		ComponentUIUtilities.setName(this.labelType, "ImportFilePropertiesDialog_labelType");
		ComponentUIUtilities.setName(this.buttonSure, "ImportFilePropertiesDialog_buttonSure");
		ComponentUIUtilities.setName(this.buttonQuit, "ImportFilePropertiesDialog_buttonQuit");
	}

	private void initResource() {
		setTitle(DataConversionProperties.getString("string_fileProperty"));
		this.labelFileLocation.setText(DataConversionProperties.getString("string_label_lblFileLocation"));
		this.labelFileType.setText(ControlsProperties.getString("String_LabelFileType"));
		this.labelFileModify.setText(DataConversionProperties.getString("string_label_lblFileLastModify"));
		this.labelFileSize.setText(DataConversionProperties.getString("string_label_lblFileSize"));
		this.labelProperty.setText(DataConversionProperties.getString("string_label_lblFileProperty"));
		this.checkboxHidden.setText(DataConversionProperties.getString("string_chcekbox_hidden"));
		this.buttonSure.setText(ControlsProperties.getString("String_Ok"));
		this.buttonQuit.setText(DataConversionProperties.getString("string_button_quit"));
	}
}
