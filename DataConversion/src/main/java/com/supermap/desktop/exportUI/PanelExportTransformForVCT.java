package com.supermap.desktop.exportUI;

import com.supermap.data.Dataset;
import com.supermap.data.conversion.ExportSettingVCT;
import com.supermap.data.conversion.VCTVersion;
import com.supermap.desktop.baseUI.PanelExportTransform;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.dataconversion.DataConversionProperties;
import com.supermap.desktop.iml.ExportFileInfo;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.*;
import com.supermap.desktop.utilities.CharsetUtilities;
import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

/**
 * Created by yuanR on 2017/11/27 0027.
 * VCT专属参数设置面板
 */
public class PanelExportTransformForVCT extends PanelExportTransform {

	private JLabel labelConfigFilePath;
	private JFileChooserControl configFileChooserControl;
	private JLabel labelVCTVersion;
	private JComboBox<VCTVersion> comboBoxVCTVersion;
	private JLabel labelSetDatasets;
	private JButton buttonSetDatasets;
	private JLabel labelCharset;
	private CharsetComboBox charsetComboBox;
	private JLabel labelExpression;
	private JScrollPane scrollPaneExpression;
	private JTextArea textAreaExpression;
	private JButton buttonExpression;


	private FileChooserPathChangedListener configFileChangedListener = new FileChooserPathChangedListener() {
		@Override
		public void pathChanged() {

			if (null != panels) {
				for (PanelExportTransform tempPanel : panels) {
					if (tempPanel instanceof PanelExportTransformForVCT) {
						((PanelExportTransformForVCT) tempPanel).getConfigFileChooserControl().setPath(configFileChooserControl.getPath());
					}
				}
			} else {
				((ExportSettingVCT) exportsFileInfo.getExportSetting()).setConfigFilePath(configFileChooserControl.getPath());
			}
		}
	};

	private ActionListener setDatasetsListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(buttonSetDatasets) && null == panels) {
				ArrayList<Dataset> datasets = new ArrayList<>();
				for (int i = 0; i < ((ExportSettingVCT) exportsFileInfo.getExportSetting()).getSourceDatas().length; i++) {
					datasets.add((Dataset) ((ExportSettingVCT) exportsFileInfo.getExportSetting()).getSourceDatas()[i]);
				}
				DatasetChooseDialog datasetChooseDialog = new DatasetChooseDialog(datasets);

				if (datasetChooseDialog.showDialog() == DialogResult.OK) {
					// 只有设置的数据集集合数量大于1时才进行设置
					if (datasetChooseDialog.getDatasets().size() > 1) {
						// 数据集集合窗口可以任意设置，当输出结果必须包含SourceData，才能setSourceDatas（）成功
						if (datasetChooseDialog.getDatasets().contains((exportsFileInfo.getExportSetting()).getSourceData())) {
							((ExportSettingVCT) exportsFileInfo.getExportSetting()).setSourceDatas(datasetChooseDialog.getDatasets().toArray());
						}
					}
				}
			}
		}
	};

	private ItemListener VCTVersionListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED && comboBoxVCTVersion.getSelectedItem() instanceof VCTVersion) {
				if (null != panels) {
					for (PanelExportTransform tempPanel : panels) {
						if (tempPanel instanceof PanelExportTransformForVCT) {
							((PanelExportTransformForVCT) tempPanel).getComboBoxVCTVersion().setSelectedItem(comboBoxVCTVersion.getSelectedItem());
						}
					}
				} else {
					((ExportSettingVCT) exportsFileInfo.getExportSetting()).setVersion((VCTVersion) comboBoxVCTVersion.getSelectedItem());
				}
			}
		}
	};

	private ItemListener charsetListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				if (null != panels) {
					for (PanelExportTransform tempPanel : panels) {
						if (tempPanel instanceof PanelExportTransformForVCT) {
							((PanelExportTransformForVCT) tempPanel).getCharsetComboBox().setSelectedItem(charsetComboBox.getSelectedItem());
						}
					}
				} else {
					exportsFileInfo.getExportSetting().setTargetFileCharset(CharsetUtilities.valueOf(charsetComboBox.getSelectedItem().toString()));
				}
			}
		}
	};


	private DocumentListener expressionListener = new DocumentListener() {
		@Override
		public void insertUpdate(DocumentEvent e) {
			setExpression();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			setExpression();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			setExpression();
		}

		private void setExpression() {
			exportsFileInfo.getExportSetting().setFilter(textAreaExpression.getText());
		}
	};


	private ActionListener buttonExpressionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			SQLExpressionDialog dialog = new SQLExpressionDialog();
			DialogResult result = dialog.showDialog(textAreaExpression.getText(), (Dataset) exportsFileInfo.getExportSetting().getSourceData());
			if (result == DialogResult.OK) {
				String filter = dialog.getQueryParameter().getAttributeFilter();
				if (!StringUtilities.isNullOrEmpty(filter)) {
					textAreaExpression.setText(filter);
				}
			}
		}
	};


	public PanelExportTransformForVCT(ExportFileInfo exportsFileInfo) {
		super(exportsFileInfo);
		registEvents();
	}

	public PanelExportTransformForVCT(ArrayList<PanelExportTransform> panelExports, int layoutType) {
		super(panelExports, layoutType);
		registEvents();
	}

	@Override
	public void initComponents() {
		// 配置文件路径
		this.labelConfigFilePath = new JLabel();
		this.configFileChooserControl = new JFileChooserControl();
		String moduleName = "InputVCTConfigFile";
		if (!SmFileChoose.isModuleExist(moduleName)) {
			SmFileChoose.addNewNode("", CoreProperties.getString("String_DefaultFilePath"), ControlsProperties.getString("String_Import")
					, moduleName, "OpenOne");
		}
		SmFileChoose fileChoose = new SmFileChoose(moduleName);
		fileChoose.setAcceptAllFileFilterUsed(true);
		this.configFileChooserControl.setFileChooser(fileChoose);

		// vct版本
		this.labelVCTVersion = new JLabel();
		this.comboBoxVCTVersion = new JComboBox<>();
		this.comboBoxVCTVersion.setModel(new DefaultComboBoxModel<>(new VCTVersion[]{VCTVersion.CNSDTF_VCT, VCTVersion.LANDUSE_VCT}));

		// 设置数据集集合
		this.labelSetDatasets = new JLabel();
		this.buttonSetDatasets = new JButton();

		// 字符集
		this.labelCharset = new JLabel();
		this.charsetComboBox = new CharsetComboBox();

		// 表达式块
		this.labelExpression = new JLabel();
		this.scrollPaneExpression = new JScrollPane();
		this.textAreaExpression = new JTextArea();
		this.textAreaExpression.setBorder(new LineBorder(Color.gray));
		this.textAreaExpression.setLineWrap(true);
		this.textAreaExpression.setColumns(5);
		this.buttonExpression = new JButton();

		if (null != exportsFileInfo && null != exportsFileInfo.getExportSetting()) {
			this.textAreaExpression.setText(exportsFileInfo.getExportSetting().getFilter());
			this.charsetComboBox.setSelectCharset(exportsFileInfo.getExportSetting().getTargetFileCharset().name());
			this.comboBoxVCTVersion.setSelectedItem(((ExportSettingVCT) exportsFileInfo.getExportSetting()).getVersion());
		} else if (null != panels) {
			// 当以多个面板进行初始化时，设置表达式块和设置数据集集合不可用
			this.buttonSetDatasets.setEnabled(false);
			this.textAreaExpression.setEnabled(false);
			this.buttonExpression.setEnabled(false);
			// 当以多个面板进行初始化时，当各面板字符集选中不同时，设置项为空
			this.configFileChooserControl.setPath(selectSameConfigFilePath(panels).toString());
			this.charsetComboBox.setSelectedItem(selectSameCharsetItem(panels));
			this.comboBoxVCTVersion.setSelectedItem(selectSameVCTVersionItem(panels));
		}
	}

	@Override
	public void setComponentName() {
		ComponentUIUtilities.setName(this.labelConfigFilePath, "labelConfigFilePath");
		ComponentUIUtilities.setName(this.configFileChooserControl, "configFileChooserControl");
		ComponentUIUtilities.setName(this.labelVCTVersion, "labelVCTVersion");
		ComponentUIUtilities.setName(this.comboBoxVCTVersion, "comboBoxVCTVersion");
		ComponentUIUtilities.setName(this.labelSetDatasets, "labelSetDatasets");
		ComponentUIUtilities.setName(this.buttonSetDatasets, "buttonSetDatasets");
		ComponentUIUtilities.setName(this.labelCharset, "labelCharset");
		ComponentUIUtilities.setName(this.charsetComboBox, "charsetComboBox");
		ComponentUIUtilities.setName(this.labelExpression, "labelExpression");
		ComponentUIUtilities.setName(this.scrollPaneExpression, "scrollPaneExpression");
		ComponentUIUtilities.setName(this.textAreaExpression, "textAreaExpression");
		ComponentUIUtilities.setName(this.buttonExpression, "buttonExpression");
	}

	/**
	 * 统一所有面板Config文件路径 ，当有差异时返回空
	 *
	 * @param panels
	 * @return
	 */
	private Object selectSameConfigFilePath(ArrayList<PanelExportTransform> panels) {
		Object result = "";
		String temp = "";
		if (panels.get(0) instanceof PanelExportTransformForVCT) {
			temp = ((PanelExportTransformForVCT) panels.get(0)).getConfigFileChooserControl().getPath();
		}
		boolean isSame = true;
		for (PanelExportTransform tempPanel : panels) {
			if (tempPanel instanceof PanelExportTransformForVCT) {
				String tempObject = ((PanelExportTransformForVCT) tempPanel).getConfigFileChooserControl().getPath();
				if (!temp.equals(tempObject)) {
					isSame = false;
					break;
				}
			}
		}
		if (isSame) {
			result = temp;
		}
		return result;
	}

	/**
	 * 统一所有面板字符集选中，当有差异时返回空
	 *
	 * @param panels
	 * @return
	 */
	private Object selectSameCharsetItem(ArrayList<PanelExportTransform> panels) {
		Object result = "";
		String temp = "";
		if (panels.get(0) instanceof PanelExportTransformForVCT) {
			temp = ((PanelExportTransformForVCT) panels.get(0)).getCharsetComboBox().getSelectedItem().toString();
		}
		boolean isSame = true;
		for (PanelExportTransform tempPanel : panels) {
			if (tempPanel instanceof PanelExportTransformForVCT) {
				String tempObject = ((PanelExportTransformForVCT) tempPanel).getCharsetComboBox().getSelectedItem().toString();
				if (!temp.equals(tempObject)) {
					isSame = false;
					break;
				}
			}
		}
		if (isSame) {
			result = temp;
		}
		return result;
	}

	/**
	 * 统一所有面板VCT模式选中，当有差异时返回空
	 *
	 * @param panels
	 * @return
	 */
	private Object selectSameVCTVersionItem(ArrayList<PanelExportTransform> panels) {
		Object result = null;
		Object temp = null;
		if (panels.get(0) instanceof PanelExportTransformForVCT) {
			temp = ((PanelExportTransformForVCT) panels.get(0)).getComboBoxVCTVersion().getSelectedItem();
		}
		boolean isSame = true;
		for (PanelExportTransform tempPanel : panels) {
			if (tempPanel instanceof PanelExportTransformForVCT) {
				Object tempObject = ((PanelExportTransformForVCT) tempPanel).getComboBoxVCTVersion().getSelectedItem();
				if (!temp.equals(tempObject)) {
					isSame = false;
					break;
				}
			}
		}
		if (isSame) {
			result = temp;
		}
		return result;
	}


	@Override
	public void initLayerout() {
		JPanel panelContent = new JPanel();
		panelContent.setLayout(new GridBagLayout());
		this.setLayout(new GridBagLayout());
		this.add(panelContent, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.NORTH).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 1));

		panelContent.add(this.labelConfigFilePath, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(10, 5, 5, 20).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		panelContent.add(this.configFileChooserControl, new GridBagConstraintsHelper(1, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(10, 5, 5, 5).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
		panelContent.add(this.labelVCTVersion, new GridBagConstraintsHelper(0, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 20).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		panelContent.add(this.comboBoxVCTVersion, new GridBagConstraintsHelper(1, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 5).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
		panelContent.add(this.labelSetDatasets, new GridBagConstraintsHelper(0, 2, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 20).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		panelContent.add(this.buttonSetDatasets, new GridBagConstraintsHelper(1, 2, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 5).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
		panelContent.add(this.labelCharset, new GridBagConstraintsHelper(0, 3, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 20).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		panelContent.add(this.charsetComboBox, new GridBagConstraintsHelper(1, 3, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 5).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));

		panelContent.add(this.labelExpression, new GridBagConstraintsHelper(0, 4, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 0).setFill(GridBagConstraints.HORIZONTAL).setWeight(0, 0));
		//添加了JScrollPane用来装JTextArea
		panelContent.add(this.scrollPaneExpression, new GridBagConstraintsHelper(0, 5, 2, 3).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 5).setFill(GridBagConstraints.BOTH).setWeight(1, 0).setIpad(0, 80));
		scrollPaneExpression.setViewportView(textAreaExpression);

		panelContent.add(this.buttonExpression, new GridBagConstraintsHelper(1, 8, 1, 1).setAnchor(GridBagConstraints.EAST).setInsets(0, 5, 5, 5).setFill(GridBagConstraints.NONE).setWeight(0, 0));
	}

	@Override
	public void initResources() {
		this.setBorder(new TitledBorder(null, DataConversionProperties.getString("string_border_panelproperty")));
		labelConfigFilePath.setText(ControlsProperties.getString("String_Label_ReferenceFile"));
		labelVCTVersion.setText(ControlsProperties.getString("String_Label_VCTVersion"));
		labelSetDatasets.setText(ControlsProperties.getString("String_Label_SetDatasets"));
		buttonSetDatasets.setText("...");
		this.labelCharset.setText(ControlsProperties.getString("String_LabelCharset"));
		this.labelExpression.setText(ControlsProperties.getString("String_LabelFilter"));
		this.buttonExpression.setText(ControlsProperties.getString("String_SQLExpression") + "...");
	}

	@Override
	public void registEvents() {
		removeEvents();
		this.configFileChooserControl.addFileChangedListener(this.configFileChangedListener);
		this.comboBoxVCTVersion.addItemListener(this.VCTVersionListener);
		this.buttonSetDatasets.addActionListener(this.setDatasetsListener);
		this.charsetComboBox.addItemListener(this.charsetListener);
		this.textAreaExpression.getDocument().addDocumentListener(this.expressionListener);
		this.buttonExpression.addActionListener(this.buttonExpressionListener);
	}

	@Override
	public void removeEvents() {
		this.configFileChooserControl.removePathChangedListener(this.configFileChangedListener);
		this.comboBoxVCTVersion.removeItemListener(this.VCTVersionListener);
		this.buttonSetDatasets.removeActionListener(this.setDatasetsListener);
		this.charsetComboBox.removeItemListener(this.charsetListener);
		this.textAreaExpression.getDocument().removeDocumentListener(this.expressionListener);
		this.buttonExpression.removeActionListener(this.buttonExpressionListener);
	}

	public JFileChooserControl getConfigFileChooserControl() {
		return configFileChooserControl;
	}

	public JComboBox<VCTVersion> getComboBoxVCTVersion() {
		return comboBoxVCTVersion;
	}

	public CharsetComboBox getCharsetComboBox() {
		return charsetComboBox;
	}

}
