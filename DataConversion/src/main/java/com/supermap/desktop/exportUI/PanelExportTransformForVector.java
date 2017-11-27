package com.supermap.desktop.exportUI;

import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.conversion.*;
import com.supermap.desktop.baseUI.PanelExportTransform;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.dataconversion.DataConversionProperties;
import com.supermap.desktop.iml.ExportFileInfo;
import com.supermap.desktop.implement.UserDefineType.ExportSettingExcel;
import com.supermap.desktop.implement.UserDefineType.ExportSettingGPX;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.StateChangeEvent;
import com.supermap.desktop.ui.StateChangeListener;
import com.supermap.desktop.ui.TristateCheckBox;
import com.supermap.desktop.ui.controls.CharsetComboBox;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.ui.controls.SQLExpressionDialog;
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
 * Created by xie on 2016/10/27.
 * 导出矢量数据集矢量参数设置
 */
public class PanelExportTransformForVector extends PanelExportTransform {
	private TristateCheckBox checkBoxExportExternalData;
	private TristateCheckBox checkBoxExportExternalRecord;
	private TristateCheckBox checkBoxExportPointAsWKT;
	private TristateCheckBox checkBoxExportFieldName;

	private JLabel labelCharset;
	private CharsetComboBox charsetComboBox;
	private JLabel labelCADVersion;
	private JComboBox<String> comboBoxCADVersion;
	private JLabel labelExpression;
	private JScrollPane scrollPaneExpression;
	private JTextArea textAreaExpression;
	private JButton buttonExpression;
	private StateChangeListener externalDataListener = new StateChangeListener() {

		@Override
		public void stateChange(StateChangeEvent e) {
			if (null != panels) {
				for (PanelExportTransform tempPanel : panels) {
					((PanelExportTransformForVector) tempPanel).getCheckBoxExportExternalData().setSelected(checkBoxExportExternalData.isSelected());
				}
			} else {
				ExportSetting exportSetting = exportsFileInfo.getExportSetting();
				if (exportSetting instanceof ExportSettingDWG) {
					((ExportSettingDWG) exportSetting).setExportingExternalData(checkBoxExportExternalData.isSelected());
				} else if (exportSetting instanceof ExportSettingDXF) {
					((ExportSettingDXF) exportSetting).setExportingExternalData(checkBoxExportExternalData.isSelected());
				}
			}
		}
	};
	private StateChangeListener externalRecordListener = new StateChangeListener() {

		@Override
		public void stateChange(StateChangeEvent e) {
			if (null != panels) {
				for (PanelExportTransform tempPanel : panels) {
					((PanelExportTransformForVector) tempPanel).getCheckBoxExportExternalRecord().setSelected(checkBoxExportExternalRecord.isSelected());
				}
			} else {
				ExportSetting exportSetting = exportsFileInfo.getExportSetting();
				if (exportSetting instanceof ExportSettingDWG) {
					((ExportSettingDWG) exportSetting).setExportingXRecord(checkBoxExportExternalRecord.isSelected());
				} else if (exportSetting instanceof ExportSettingDXF) {
					((ExportSettingDXF) exportSetting).setExportingXRecord(checkBoxExportExternalRecord.isSelected());
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
						if (tempPanel instanceof PanelExportTransformForVector) {
							((PanelExportTransformForVector) tempPanel).getCharsetComboBox().setSelectedItem(charsetComboBox.getSelectedItem());
						}
					}
				} else {
					exportsFileInfo.getExportSetting().setTargetFileCharset(CharsetUtilities.valueOf(charsetComboBox.getSelectedItem().toString()));
				}
			}
		}
	};
	private ItemListener cadVersionListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				if (null != panels) {
					for (PanelExportTransform tempPanel : panels) {
						((PanelExportTransformForVector) tempPanel).getComboBoxCADVersion().setSelectedItem(comboBoxCADVersion.getSelectedItem());
					}
				} else {
					String cadVersion = comboBoxCADVersion.getSelectedItem().toString();
					ExportSetting exportSetting = exportsFileInfo.getExportSetting();
					if (exportSetting instanceof ExportSettingDWG) {
						((ExportSettingDWG) exportSetting).setVersion(getCADVersion(cadVersion));
					} else if (exportSetting instanceof ExportSettingDXF) {
						((ExportSettingDXF) exportSetting).setVersion(getCADVersion(cadVersion));
					}
				}
			}
		}

		private CADVersion getCADVersion(String item) {
			CADVersion version = null;
			if ("CAD2007".equalsIgnoreCase(item)) {
				version = CADVersion.CAD2007;
			}
			if ("CAD2004".equalsIgnoreCase(item)) {
				version = CADVersion.CAD2004;
			}
			if ("CAD2000".equalsIgnoreCase(item)) {
				version = CADVersion.CAD2000;
			}
			if ("CAD12".equalsIgnoreCase(item)) {
				version = CADVersion.CAD12;
			}
			if ("CAD14".equalsIgnoreCase(item)) {
				version = CADVersion.CAD14;
			}
			if ("CAD13".equalsIgnoreCase(item)) {
				version = CADVersion.CAD13;
			}
			return version;
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
	private StateChangeListener exportPointAsWKTListener = new StateChangeListener() {
		@Override
		public void stateChange(StateChangeEvent e) {
			if (null != panels) {
				for (PanelExportTransform tempPanel : panels) {
					((PanelExportTransformForVector) tempPanel).getCheckBoxExportPointAsWKT().setSelected(checkBoxExportPointAsWKT.isSelected());
				}
			} else {
				ExportSetting exportSetting = exportsFileInfo.getExportSetting();
				((ExportSettingCSV) exportSetting).setIsExportPointAsWKT(checkBoxExportPointAsWKT.isSelected());
			}
		}
	};
	private StateChangeListener exportFieldNameListener = new StateChangeListener() {
		@Override
		public void stateChange(StateChangeEvent e) {
			if (null != panels) {
				for (PanelExportTransform tempPanel : panels) {
					((PanelExportTransformForVector) tempPanel).getCheckBoxExportFieldName().setSelected(checkBoxExportFieldName.isSelected());
				}
			} else {
				ExportSetting exportSetting = exportsFileInfo.getExportSetting();
				((ExportSettingCSV) exportSetting).setIsExportFieldName(checkBoxExportFieldName.isSelected());
			}
		}
	};

	public PanelExportTransformForVector(ExportFileInfo exportsFileInfo) {
		super(exportsFileInfo);
		registEvents();
	}

	public PanelExportTransformForVector(ArrayList<PanelExportTransform> panelExports, int layoutType) {
		super(panelExports, layoutType);
		registEvents();
	}

	@Override
	public void initComponents() {
		this.checkBoxExportExternalData = new TristateCheckBox();
		this.checkBoxExportExternalRecord = new TristateCheckBox();
		this.checkBoxExportPointAsWKT = new TristateCheckBox();
		this.checkBoxExportFieldName = new TristateCheckBox();
		this.labelCharset = new JLabel();
		this.charsetComboBox = new CharsetComboBox();
		this.labelCADVersion = new JLabel();
		this.comboBoxCADVersion = new JComboBox<>();
		this.labelExpression = new JLabel();

		this.scrollPaneExpression = new JScrollPane();

		this.textAreaExpression = new JTextArea();
		this.textAreaExpression.setBorder(new LineBorder(Color.gray));
		this.textAreaExpression.setLineWrap(true);
		this.textAreaExpression.setColumns(5);
		this.buttonExpression = new JButton();
		initComboboxCADVersion();
		setUnEnabled();
		if (null != exportsFileInfo && null != exportsFileInfo.getExportSetting()) {
			initComponentsState(exportsFileInfo.getExportSetting());
			this.charsetComboBox.setSelectCharset(exportsFileInfo.getExportSetting().getTargetFileCharset().name());
		} else if (null != panels) {
			initComponentsState(panels);
		}
	}

	@Override
	public void setComponentName() {
		ComponentUIUtilities.setName(this.checkBoxExportExternalData, "checkBoxExportExternalData");
		ComponentUIUtilities.setName(this.checkBoxExportExternalRecord, "checkBoxExportExternalRecord");
		ComponentUIUtilities.setName(this.checkBoxExportPointAsWKT, "checkBoxExportPointAsWKT");
		ComponentUIUtilities.setName(this.checkBoxExportFieldName, "checkBoxExportFieldName");
		ComponentUIUtilities.setName(this.labelCharset, "labelCharset");
		ComponentUIUtilities.setName(this.charsetComboBox, "charsetComboBox");
		ComponentUIUtilities.setName(this.labelCADVersion, "labelCADVersion");
		ComponentUIUtilities.setName(this.comboBoxCADVersion, "comboBoxCADVersion");
		ComponentUIUtilities.setName(this.labelExpression, "labelExpression");
		ComponentUIUtilities.setName(this.scrollPaneExpression, "scrollPaneExpression");
		ComponentUIUtilities.setName(this.textAreaExpression, "textAreaExpression");
		ComponentUIUtilities.setName(this.buttonExpression, "buttonExpression");
	}

	private void initComponentsState(ArrayList<PanelExportTransform> panels) {
		this.charsetComboBox.setEnabled(true);
		this.charsetComboBox.setSelectedItem(selectSameCharsetItem(panels));
		if (isDtype(panels)) {
			//相同类型
			this.checkBoxExportExternalData.setEnabled(true);
			this.checkBoxExportExternalRecord.setEnabled(true);
			this.comboBoxCADVersion.setEnabled(true);
			this.checkBoxExportExternalData.setSelectedEx(externalDataSelectAll(panels));
			this.checkBoxExportExternalRecord.setSelectedEx(externalRecordSelectAll(panels));
			this.comboBoxCADVersion.setSelectedItem(selectSameItem(panels));
		}
		Boolean exportPointAsWKTSelectAll = exportPointAsWKTSelectAll(panels);
		Boolean exportFieldNameSelectAll = exportFieldNameSelectAll(panels);
		this.checkBoxExportFieldName.setEnabled(fieladNameEnabledAll(panels));
		this.checkBoxExportPointAsWKT.setEnabled(pointAsWKTEnabledAll(panels));
		//this.buttonExpression.setEnabled(sqlExpressionEnabledAll(panels));
		this.checkBoxExportPointAsWKT.setSelectedEx(exportPointAsWKTSelectAll);
		this.checkBoxExportFieldName.setSelectedEx(exportFieldNameSelectAll);
	}

	private boolean pointAsWKTEnabledAll(ArrayList<PanelExportTransform> panels) {
		boolean result = false;
		int selectCount = 0;
		for (PanelExportTransform tempPanel : panels) {
			if (((PanelExportTransformForVector) tempPanel).getCheckBoxExportPointAsWKT().isEnabled()) {
				selectCount++;
			}
		}
		if (selectCount == panels.size()) {
			result = true;
		}
		return result;
	}

	private boolean fieladNameEnabledAll(ArrayList<PanelExportTransform> panels) {
		boolean result = false;
		int selectCount = 0;
		for (PanelExportTransform tempPanel : panels) {
			if (((PanelExportTransformForVector) tempPanel).getCheckBoxExportFieldName().isEnabled()) {
				selectCount++;
			}
		}
		if (selectCount == panels.size()) {
			result = true;
		}
		return result;
	}

	/**
	 * 为什么要判断所有的表达式设置按妞是否可用，当多选时，表达式设置按妞也不可用才对-yuanR2017.11.27
	 *
	 * @param panels
	 * @return
	 */
	//private boolean sqlExpressionEnabledAll(ArrayList<PanelExportTransform> panels) {
	//	boolean result = false;
	//	int selectCount = 0;
	//	for (PanelExportTransform tempPanel : panels) {
	//		if (((PanelExportTransformForVector) tempPanel).getButtonExpression().isEnabled()) {
	//			selectCount++;
	//		}
	//	}
	//	if (selectCount == panels.size()) {
	//		result = true;
	//	}
	//	return result;
	//}

	private Boolean exportFieldNameSelectAll(ArrayList<PanelExportTransform> panels) {
		Boolean result = null;
		int selectCount = 0;
		int unSelectCount = 0;
		for (PanelExportTransform tempPanel : panels) {
			if (((PanelExportTransformForVector) tempPanel).getCheckBoxExportFieldName().isSelected()) {
				selectCount++;
			} else if (!((PanelExportTransformForVector) tempPanel).getCheckBoxExportFieldName().isSelected()) {
				unSelectCount++;
			}
		}
		if (selectCount == panels.size()) {
			result = true;
		} else if (unSelectCount == panels.size()) {
			result = false;
		}
		return result;
	}

	private Boolean exportPointAsWKTSelectAll(ArrayList<PanelExportTransform> panels) {
		Boolean result = null;
		int selectCount = 0;
		int unSelectCount = 0;
		for (PanelExportTransform tempPanel : panels) {
			if (((PanelExportTransformForVector) tempPanel).getCheckBoxExportPointAsWKT().isSelected()) {
				selectCount++;
			} else if (!((PanelExportTransformForVector) tempPanel).getCheckBoxExportPointAsWKT().isSelected()) {
				unSelectCount++;
			}
		}
		if (selectCount == panels.size()) {
			result = true;
		} else if (unSelectCount == panels.size()) {
			result = false;
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
		if (panels.get(0) instanceof PanelExportTransformForVector) {
			temp = ((PanelExportTransformForVector) panels.get(0)).getCharsetComboBox().getSelectedItem().toString();
		}
		boolean isSame = true;
		for (PanelExportTransform tempPanel : panels) {
			if (tempPanel instanceof PanelExportTransformForVector) {
				String tempObject = ((PanelExportTransformForVector) tempPanel).getCharsetComboBox().getSelectedItem().toString();
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

	private Object selectSameItem(ArrayList<PanelExportTransform> panels) {
		Object result = "";
		String temp = "";
		if (panels.get(0) instanceof PanelExportTransformForVector) {
			temp = ((PanelExportTransformForVector) panels.get(0)).getComboBoxCADVersion().getSelectedItem().toString();
		}
		boolean isSame = true;
		for (PanelExportTransform tempPanel : panels) {
			if (tempPanel instanceof PanelExportTransformForVector) {
				String tempObject = ((PanelExportTransformForVector) tempPanel).getComboBoxCADVersion().getSelectedItem().toString();
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

	private Boolean externalRecordSelectAll(ArrayList<PanelExportTransform> panels) {
		Boolean result = null;
		int selectCount = 0;
		int unSelectCount = 0;
		for (PanelExportTransform tempPanel : panels) {
			if (((PanelExportTransformForVector) tempPanel).getCheckBoxExportExternalRecord().isSelected()) {
				selectCount++;
			} else if (!((PanelExportTransformForVector) tempPanel).getCheckBoxExportExternalRecord().isSelected()) {
				unSelectCount++;
			}
		}
		if (selectCount == panels.size()) {
			result = true;
		} else if (unSelectCount == panels.size()) {
			result = false;
		}
		return result;
	}

	private Boolean externalDataSelectAll(ArrayList<PanelExportTransform> panels) {
		Boolean result = null;
		int selectCount = 0;
		int unSelectCount = 0;
		for (PanelExportTransform tempPanel : panels) {
			if (tempPanel instanceof PanelExportTransformForVector) {
				if (((PanelExportTransformForVector) tempPanel).getCheckBoxExportExternalData().isSelected()) {
					selectCount++;
				} else if (!((PanelExportTransformForVector) tempPanel).getCheckBoxExportExternalData().isSelected()) {
					unSelectCount++;
				}
			}
		}
		if (selectCount == panels.size()) {
			result = true;
		} else if (unSelectCount == panels.size()) {
			result = false;
		}
		return result;
	}

	private boolean isDtype(ArrayList<PanelExportTransform> panels) {
		int count = 0;
		for (PanelExportTransform tempPanel : panels) {
			if (tempPanel.getExportsFileInfo().getExportSetting() instanceof ExportSettingDXF || tempPanel.getExportsFileInfo().getExportSetting() instanceof ExportSettingDWG)
				count++;
		}
		return count == panels.size();
	}


	private void initComponentsState(ExportSetting tempExportSetting) {
		if (tempExportSetting instanceof ExportSettingDWG || tempExportSetting instanceof ExportSettingDXF) {
			this.checkBoxExportExternalData.setEnabled(true);
			this.checkBoxExportExternalRecord.setEnabled(true);
			this.comboBoxCADVersion.setEnabled(true);
			if (tempExportSetting instanceof ExportSettingDWG) {
				this.checkBoxExportExternalData.setSelected(((ExportSettingDWG) tempExportSetting).isExportingExternalData());
				this.checkBoxExportExternalRecord.setSelected(((ExportSettingDWG) tempExportSetting).isExportingXRecord());
			} else {
				this.checkBoxExportExternalData.setSelected(((ExportSettingDXF) tempExportSetting).isExportingExternalData());
				this.checkBoxExportExternalRecord.setSelected(((ExportSettingDXF) tempExportSetting).isExportingXRecord());
			}
			this.comboBoxCADVersion.setSelectedItem(selectItem(tempExportSetting));
		}
		if (tempExportSetting instanceof ExportSettingCSV && !(tempExportSetting instanceof ExportSettingExcel)) {
			this.checkBoxExportFieldName.setEnabled(true);
			//默认设置为导出表头
			this.checkBoxExportFieldName.setSelected(true);
			((ExportSettingCSV) tempExportSetting).setIsExportFieldName(true);
			if (((Dataset) tempExportSetting.getSourceData()).getType().equals(DatasetType.POINT)
					|| ((Dataset) tempExportSetting.getSourceData()).getType().equals(DatasetType.POINT3D)) {
				this.checkBoxExportPointAsWKT.setEnabled(!(tempExportSetting instanceof ExportSettingExcel));
				this.checkBoxExportPointAsWKT.setSelected(((ExportSettingCSV) tempExportSetting).GetIsExportPointAsWKT());
			}
		}

		if (tempExportSetting instanceof ExportSettingGPX) {
			this.charsetComboBox.setEnabled(false);
			this.textAreaExpression.setEnabled(false);
			this.buttonExpression.setEnabled(false);
		} else {
			this.charsetComboBox.setEnabled(true);
			this.textAreaExpression.setEnabled(true);
			this.buttonExpression.setEnabled(true);
			this.textAreaExpression.setText(tempExportSetting.getFilter());
		}
	}

	private String selectItem(ExportSetting tempExportSetting) {
		CADVersion cadVersion = null;
		if (tempExportSetting instanceof ExportSettingDWG) {
			cadVersion = ((ExportSettingDWG) tempExportSetting).getVersion();
		} else {
			cadVersion = ((ExportSettingDXF) tempExportSetting).getVersion();
		}
		return cadVersion.toString();
	}


	private void initComboboxCADVersion() {
		this.comboBoxCADVersion.setModel(new DefaultComboBoxModel<>(new String[]{CADVersion.CAD12.toString(), CADVersion.CAD13.toString(),
				CADVersion.CAD14.toString(), CADVersion.CAD2000.toString(), CADVersion.CAD2004.toString(), CADVersion.CAD2007.toString()}));
		this.charsetComboBox.setEditable(true);
		((JTextField) this.charsetComboBox.getEditor().getEditorComponent()).setEditable(false);
		this.comboBoxCADVersion.setEditable(true);
		((JTextField) this.comboBoxCADVersion.getEditor().getEditorComponent()).setEditable(false);
	}

	@Override
	public void initLayerout() {
		JPanel panelContent = new JPanel();
		panelContent.setLayout(new GridBagLayout());
		this.setLayout(new GridBagLayout());
		this.add(panelContent, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.NORTH).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 1));

		panelContent.add(this.checkBoxExportExternalData, new GridBagConstraintsHelper(0, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(10, 0, 0, 0).setFill(GridBagConstraints.HORIZONTAL).setWeight(0, 0));
		panelContent.add(this.checkBoxExportExternalRecord, new GridBagConstraintsHelper(0, 1, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0).setFill(GridBagConstraints.HORIZONTAL).setWeight(0, 0));
		panelContent.add(this.checkBoxExportPointAsWKT, new GridBagConstraintsHelper(0, 2, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0).setFill(GridBagConstraints.HORIZONTAL).setWeight(0, 0));
		panelContent.add(this.checkBoxExportFieldName, new GridBagConstraintsHelper(0, 3, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0).setFill(GridBagConstraints.HORIZONTAL).setWeight(0, 0));
		panelContent.add(this.labelCharset, new GridBagConstraintsHelper(0, 4, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 20).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		panelContent.add(this.charsetComboBox, new GridBagConstraintsHelper(1, 4, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 5).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
		panelContent.add(this.labelCADVersion, new GridBagConstraintsHelper(0, 5, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 20).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		panelContent.add(this.comboBoxCADVersion, new GridBagConstraintsHelper(1, 5, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 5).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
		panelContent.add(this.labelExpression, new GridBagConstraintsHelper(0, 6, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 0).setFill(GridBagConstraints.HORIZONTAL).setWeight(0, 0));

		//添加了JScrollPane用来装JTextArea
		panelContent.add(this.scrollPaneExpression, new GridBagConstraintsHelper(0, 7, 2, 3).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 5).setFill(GridBagConstraints.BOTH).setWeight(1, 0).setIpad(0, 80));
		scrollPaneExpression.setViewportView(textAreaExpression);

		panelContent.add(this.buttonExpression, new GridBagConstraintsHelper(1, 10, 1, 1).setAnchor(GridBagConstraints.EAST).setInsets(0, 5, 5, 5).setFill(GridBagConstraints.NONE).setWeight(0, 0));
	}

	@Override
	public void initResources() {
		this.setBorder(new TitledBorder(null, DataConversionProperties.getString("string_border_panelproperty")));
		this.checkBoxExportExternalData.setText(DataConversionProperties.getString("string_chcekbox_extends"));
		this.checkBoxExportExternalRecord.setText(ControlsProperties.getString("String_ExportExternalRecord"));
		this.checkBoxExportPointAsWKT.setText(CoreProperties.getString("String_ExportPointAsWKT"));
		this.checkBoxExportFieldName.setText(CoreProperties.getString("String_ExportFieldName"));
		this.labelCharset.setText(ControlsProperties.getString("String_LabelCharset"));
		this.labelCADVersion.setText(ControlsProperties.getString("string_label_lblCAD"));
		this.labelExpression.setText(ControlsProperties.getString("String_LabelFilter"));
		this.buttonExpression.setText(ControlsProperties.getString("String_SQLExpression") + "...");
	}

	public void setUnEnabled() {
		this.checkBoxExportExternalData.setSelected(false);
		this.checkBoxExportExternalRecord.setSelected(false);
		this.checkBoxExportPointAsWKT.setSelected(false);
		this.checkBoxExportFieldName.setSelected(false);

		this.checkBoxExportExternalData.setEnabled(false);
		this.checkBoxExportExternalRecord.setEnabled(false);
		this.checkBoxExportPointAsWKT.setEnabled(false);
		this.checkBoxExportFieldName.setEnabled(false);
		this.charsetComboBox.setEnabled(false);
		this.comboBoxCADVersion.setEnabled(false);
		this.textAreaExpression.setEnabled(false);
		this.buttonExpression.setEnabled(false);
	}

	@Override
	public void registEvents() {
		removeEvents();
		this.checkBoxExportExternalData.addStateChangeListener(this.externalDataListener);
		this.checkBoxExportExternalRecord.addStateChangeListener(this.externalRecordListener);
		this.checkBoxExportPointAsWKT.addStateChangeListener(this.exportPointAsWKTListener);
		this.checkBoxExportFieldName.addStateChangeListener(this.exportFieldNameListener);
		this.charsetComboBox.addItemListener(this.charsetListener);
		this.comboBoxCADVersion.addItemListener(this.cadVersionListener);
		this.textAreaExpression.getDocument().addDocumentListener(this.expressionListener);
		this.buttonExpression.addActionListener(this.buttonExpressionListener);
	}

	@Override
	public void removeEvents() {
		this.checkBoxExportExternalData.removeStateChangeListener(this.externalDataListener);
		this.checkBoxExportExternalRecord.removeStateChangeListener(this.externalRecordListener);
		this.checkBoxExportPointAsWKT.removeStateChangeListener(this.exportPointAsWKTListener);
		this.checkBoxExportFieldName.removeStateChangeListener(this.exportFieldNameListener);
		this.charsetComboBox.removeItemListener(this.charsetListener);
		this.comboBoxCADVersion.removeItemListener(this.cadVersionListener);
		this.textAreaExpression.getDocument().removeDocumentListener(this.expressionListener);
		this.buttonExpression.removeActionListener(this.buttonExpressionListener);
	}

	public TristateCheckBox getCheckBoxExportExternalData() {
		return checkBoxExportExternalData;
	}

	public TristateCheckBox getCheckBoxExportExternalRecord() {
		return checkBoxExportExternalRecord;
	}

	public CharsetComboBox getCharsetComboBox() {
		return charsetComboBox;
	}

	public JComboBox<String> getComboBoxCADVersion() {
		return comboBoxCADVersion;
	}

	public TristateCheckBox getCheckBoxExportPointAsWKT() {
		return checkBoxExportPointAsWKT;
	}

	public TristateCheckBox getCheckBoxExportFieldName() {
		return checkBoxExportFieldName;
	}

	public JButton getButtonExpression() {
		return buttonExpression;
	}
}
