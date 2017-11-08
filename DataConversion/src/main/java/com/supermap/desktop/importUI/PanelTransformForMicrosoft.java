package com.supermap.desktop.importUI;

import com.supermap.data.*;
import com.supermap.data.conversion.ImportSetting;
import com.supermap.data.conversion.ImportSettingCSV;
import com.supermap.desktop.baseUI.PanelTransform;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.dataconversion.DataConversionProperties;
import com.supermap.desktop.implement.UserDefineType.ImportSettingExcel;
import com.supermap.desktop.implement.UserDefineType.ImportSettingGPX;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.TristateCheckBox;
import com.supermap.desktop.ui.controls.ComponentBorderPanel.CompTitledPane;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.ui.controls.SteppedComboBox;
import com.supermap.desktop.utilities.StringUtilities;
import com.supermap.desktop.utilities.XlsUtilities;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

/**
 * Created by xie on 2016/10/11.
 * 导入csv,xlsx
 */
public class PanelTransformForMicrosoft extends PanelTransform {
	private ArrayList<PanelImport> panelImports;
	private JLabel labelSeparator;
	private JTextField textFieldSeparator;
	private TristateCheckBox checkBoxFirstRowAsField;
	private JLabel labelEmpty;
	private JCheckBox checkBoxImportIndexData;
	private JRadioButton radioButtonIndex;
	private JRadioButton radioButtonIndexWKT;
	private JLabel labelX;
	private JLabel labelY;
	private JLabel labelZ;

	private SteppedComboBox comboBoxWKT;
	private SteppedComboBox comboBoxX;
	private SteppedComboBox comboBoxY;
	private SteppedComboBox comboBoxZ;
	private JLabel labelDataPreview;
	private JTable tablePreviewCSV;
	private JScrollPane scrollPanePreviewCSV;

	private CompTitledPane paneForIndexAsPoint;

	private DocumentListener separatorListener = new DocumentListener() {
		@Override
		public void insertUpdate(DocumentEvent e) {
			updateSeparator();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			updateSeparator();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			updateSeparator();
		}

		private void updateSeparator() {
			if (!StringUtilities.isNullOrEmpty(textFieldSeparator.getText())) {
				if (null != panelImports) {
					for (PanelImport tempPanelImport : panelImports) {
						((PanelTransformForMicrosoft) tempPanelImport.getTransform()).getTextFieldSeparator().setText(textFieldSeparator.getText());
					}
				} else {
					((ImportSettingCSV) importSetting).setSeparator(textFieldSeparator.getText());
				}
			}
		}
	};
	private ItemListener itemListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (null != panelImports) {
				for (PanelImport tempPanelImport : panelImports) {
					((PanelTransformForMicrosoft) tempPanelImport.getTransform()).getCheckBoxFirstRowAsField().setSelected(checkBoxFirstRowAsField.isSelected());
				}
			} else {
				((ImportSettingCSV) importSetting).setFirstRowIsField(checkBoxFirstRowAsField.isSelected());
			}
		}
	};
	private ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			setIndexPanelEnabled();
		}
	};
	private ItemListener wktItemListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (comboBoxWKT.isEnabled() && e.getStateChange() == ItemEvent.SELECTED) {
				if (null != panelImports) {
					for (PanelImport tempPanelImport : panelImports) {
						((PanelTransformForMicrosoft) tempPanelImport.getTransform()).getComboBoxWKT().setSelectedItem(comboBoxWKT.getSelectedItem());
					}
				} else {
					setImportAsPointWKT();
				}
			}
		}
	};

	private void setImportAsPointWKT() {
		//todo 设置后有崩溃问题，暂时屏蔽
		if (null != comboBoxWKT.getSelectedItem()) {
			((ImportSettingCSV) importSetting).setIndexAsGeometry((Integer) ((ItemNode) comboBoxWKT.getSelectedItem()).getNodeInfo());
		}
	}

	private ItemListener commonItemListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (((JComboBox) e.getSource()).isEnabled() && e.getStateChange() == ItemEvent.SELECTED) {
				if (null != panelImports) {
					for (PanelImport tempPanelImport : panelImports) {
						if (e.getSource() == getComboBoxX()) {
							((PanelTransformForMicrosoft) tempPanelImport.getTransform()).getComboBoxX().setSelectedItem(getComboBoxX().getSelectedItem());
						}
						if (e.getSource() == getComboBoxY()) {
							((PanelTransformForMicrosoft) tempPanelImport.getTransform()).getComboBoxY().setSelectedItem(getComboBoxY().getSelectedItem());
						}
						if (e.getSource() == getComboBoxZ()) {
							((PanelTransformForMicrosoft) tempPanelImport.getTransform()).getComboBoxZ().setSelectedItem(getComboBoxZ().getSelectedItem());
						}
					}
				} else {
					setImportAsPoint();
				}
			}
		}
	};

	private void setImportAsPoint() {
		//用非法经纬度处理异常数据
		ArrayList<String> fileds = new ArrayList<>();
		String tempX = comboBoxX.getSelectedItem().toString();
		if (!StringUtilities.isNullOrEmptyString(tempX)) {
			fileds.add(tempX);
		}
		String tempY = comboBoxY.getSelectedItem().toString();
		if (!StringUtilities.isNullOrEmptyString(tempY)) {
			fileds.add(tempY);
		}
		String tempZ = comboBoxZ.getSelectedItem().toString();
		if (!StringUtilities.isNullOrEmptyString(tempZ)) {
			fileds.add(tempZ);
		}
		((ImportSettingCSV) importSetting).setFieldsAsPoint(fileds.toArray(new String[fileds.size()]));
		fileds.clear();
		fileds = null;
		tempX = null;
		tempY = null;
		tempZ = null;
	}

	private ItemListener radioButtonListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				if (radioButtonIndexWKT.isSelected()) {
					setImportAsPointWKT();
				} else if (radioButtonIndex.isSelected()) {
					setImportAsPoint();
				}
				setIndexPanelEnabled();
			}
		}
	};

	public PanelTransformForMicrosoft(ImportSetting importSetting) {
		super(importSetting);
		registEvents();
	}

	public PanelTransformForMicrosoft(ArrayList<PanelImport> panelImports, int layoutType) {
		super(panelImports, layoutType);
		this.panelImports = panelImports;
		initLayerout();
		registEvents();
	}

	@Override
	public void initComponents() {
		GeoCoordSys geoCoordSys = new GeoCoordSys(GeoCoordSysType.GCS_WGS_1984, GeoSpatialRefType.SPATIALREF_EARTH_LONGITUDE_LATITUDE);
		PrjCoordSys prjCoordSys = new PrjCoordSys(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
		prjCoordSys.setGeoCoordSys(geoCoordSys);
		this.importSetting.setTargetPrjCoordSys(prjCoordSys);
		this.checkBoxFirstRowAsField = new TristateCheckBox();
		this.checkBoxFirstRowAsField.setSelected(true);
		((ImportSettingCSV) this.importSetting).setFirstRowIsField(true);
		this.labelSeparator = new JLabel();
		this.textFieldSeparator = new JTextField();
		this.checkBoxImportIndexData = new JCheckBox();
		this.labelEmpty = new JLabel();
		this.labelX = new JLabel();
		this.labelY = new JLabel();
		this.labelZ = new JLabel();
		this.radioButtonIndexWKT = new JRadioButton();
		this.radioButtonIndex = new JRadioButton();
		ButtonGroup group = new ButtonGroup();
		group.add(this.radioButtonIndexWKT);
		group.add(this.radioButtonIndex);
		this.labelDataPreview = new JLabel();
		this.scrollPanePreviewCSV = new JScrollPane();
		this.tablePreviewCSV = new JTable();
		String[] temp = new String[]{};
		this.comboBoxWKT = new SteppedComboBox(temp);
		this.comboBoxX = new SteppedComboBox(temp);
		this.comboBoxY = new SteppedComboBox(temp);
		this.comboBoxZ = new SteppedComboBox(temp);
		temp = null;
		if (!(this.importSetting instanceof ImportSettingExcel)) {
			String[][] data = XlsUtilities.getData(importSetting.getSourceFilePath());
			if (null != data) {
				String[] tempValues = data[0];
				for (int i = 0, tempLength = tempValues.length; i < tempLength; i++) {
					tempValues[i] = tempValues[i].replace("\"", "");
				}
				String[] indexX = tempValues;
				int length = XlsUtilities.getData(importSetting.getSourceFilePath()).length;
				String[][] tableValues = new String[length - 1][];
				for (int i = 1; i < length; i++) {
					tableValues[i - 1] = data[i];
				}
				DefaultTableModel model = new DefaultTableModel(tableValues, indexX);
				this.tablePreviewCSV.setModel(model);
				this.tablePreviewCSV.setRowHeight(23);
				//设置表头宽度
				this.tablePreviewCSV.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				this.tablePreviewCSV.getTableHeader().setPreferredSize(
						new Dimension(this.tablePreviewCSV.getTableHeader().getPreferredSize().width, 30));
				this.scrollPanePreviewCSV.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				boolean hasGeometry = false;
				ItemNode itemNode = new ItemNode();
				for (int i = 0, size = indexX.length; i < size; i++) {
					if ("Geometry".equals(indexX[i])) {
						hasGeometry = true;
						itemNode.setItemInfo("Geometry");
						itemNode.setNodeInfo(i);
						break;
					}
				}

				if (hasGeometry) {
					this.comboBoxWKT.addItem(itemNode);
				}
				DefaultComboBoxModel comboBoxModelX = new DefaultComboBoxModel(indexX);
				DefaultComboBoxModel comboBoxModelY = new DefaultComboBoxModel(indexX);
				this.comboBoxWKT.setRenderer(new ItemNodeComboBoxRender());
				this.comboBoxX.setModel(comboBoxModelX);
				this.comboBoxY.setModel(comboBoxModelY);
				String[] indexZ = new String[indexX.length + 1];
				for (int i = 0, lengthZ = indexZ.length; i < lengthZ; i++) {
					indexZ[i] = i == 0 ? "" : indexX[i - 1];
				}
				this.comboBoxZ.setModel(new DefaultComboBoxModel(indexZ));
				setComboboxStepSize(comboBoxWKT, comboBoxX, comboBoxY, comboBoxZ);
				data = null;
				tempValues = null;
				tableValues = null;
				indexX = null;
				indexZ = null;
				comboBoxModelX = null;
				comboBoxModelY = null;
			}
		}
	}

	private void setComboboxStepSize(SteppedComboBox... comboBoxes) {
		for (SteppedComboBox steppedComboBox : comboBoxes) {
			Dimension d = steppedComboBox.getPreferredSize();
			steppedComboBox.setPreferredSize(new Dimension(d.width, d.height));
			steppedComboBox.setPopupWidth(d.width);
		}

	}

	@Override
	public void setComponentName() {
		super.setComponentName();
		ComponentUIUtilities.setName(this.labelSeparator, "PanelTransformForMicrosoft_labelSeparator");
		ComponentUIUtilities.setName(this.textFieldSeparator, "PanelTransformForMicrosoft_textFieldSeparator");
		ComponentUIUtilities.setName(this.checkBoxFirstRowAsField, "PanelTransformForMicrosoft_checkBoxFirstRowAsField");
		ComponentUIUtilities.setName(this.labelEmpty, "PanelTransformForMicrosoft_labelEmpty");
	}

	@Override
	public void initLayerout() {

		JPanel panelTemp = new JPanel();
		// @formatter:off
		panelTemp.setLayout(new GridBagLayout());
		panelTemp.add(this.radioButtonIndexWKT, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(5).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		panelTemp.add(this.comboBoxWKT, new GridBagConstraintsHelper(1, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 5).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		panelTemp.add(this.labelEmpty, new GridBagConstraintsHelper(3, 0, 4, 1).setAnchor(GridBagConstraints.WEST).setInsets(5).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));

		panelTemp.add(this.radioButtonIndex, new GridBagConstraintsHelper(0, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 0, 0).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		panelTemp.add(this.labelX, new GridBagConstraintsHelper(1, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		panelTemp.add(this.comboBoxX, new GridBagConstraintsHelper(2, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(5).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
		panelTemp.add(this.labelY, new GridBagConstraintsHelper(3, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(5).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		panelTemp.add(this.comboBoxY, new GridBagConstraintsHelper(4, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(5).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
		panelTemp.add(this.labelZ, new GridBagConstraintsHelper(5, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(5).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		panelTemp.add(this.comboBoxZ, new GridBagConstraintsHelper(6, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 5, 5, 10).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
		this.paneForIndexAsPoint = new CompTitledPane(this.checkBoxImportIndexData, panelTemp);

		JPanel panelDefault = new JPanel();
		panelDefault.setLayout(new GridBagLayout());
		panelDefault.add(this.labelSeparator, new GridBagConstraintsHelper(0, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 5, 5, 45).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		panelDefault.add(this.textFieldSeparator, new GridBagConstraintsHelper(2, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 20).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
		panelDefault.add(this.checkBoxFirstRowAsField, new GridBagConstraintsHelper(4, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 10).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		panelDefault.add(this.labelEmpty, new GridBagConstraintsHelper(6, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 10).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
		panelDefault.add(this.labelDataPreview, new GridBagConstraintsHelper(0, 1, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 5, 5, 45).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		panelDefault.add(this.scrollPanePreviewCSV, new GridBagConstraintsHelper(2, 1, 6, 6).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 10).setFill(GridBagConstraints.BOTH).setWeight(1, 1).setIpad(0, 80));
		panelDefault.setBorder(new TitledBorder(ControlsProperties.getString("string_border_panelTransform")));
		this.setLayout(new GridBagLayout());
		this.add(panelDefault, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.BOTH).setWeight(1, 0));
		this.add(this.paneForIndexAsPoint, new GridBagConstraintsHelper(0, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.BOTH).setWeight(1, 0));
		this.textFieldSeparator.setPreferredSize(new Dimension(18, 23));
		this.comboBoxWKT.setPreferredSize(new Dimension(100,20));
		if (null != panelImports) {
			this.scrollPanePreviewCSV.setViewportView(null);
		} else {
			this.scrollPanePreviewCSV.setViewportView(this.tablePreviewCSV);
		}
		this.radioButtonIndex.setSelected(true);
		setFirstRowAsField();
		setSeparator();
		if (null != panelImports) {
			checkBoxImportIndexData.setEnabled(false);
			this.radioButtonIndexWKT.setEnabled(false);
			this.radioButtonIndex.setEnabled(false);
			this.labelX.setEnabled(false);
			this.labelY.setEnabled(false);
			this.labelZ.setEnabled(false);
			comboBoxX.setEnabled(false);
			comboBoxY.setEnabled(false);
			comboBoxZ.setEnabled(false);
			comboBoxWKT.setEnabled(false);
		} else {
			setIndexPanelEnabled();
		}
		if (importSetting instanceof ImportSettingExcel || importSetting instanceof ImportSettingGPX) {
			this.labelDataPreview.setVisible(false);
			this.paneForIndexAsPoint.setVisible(false);
			this.labelSeparator.setVisible(false);
			this.textFieldSeparator.setVisible(false);
			this.scrollPanePreviewCSV.setVisible(false);
		}
	}

	private void setSeparator() {
		if (null != panelImports) {
			this.textFieldSeparator.setText(getSameSeparator());
		}
	}

	private void setFirstRowAsField() {
		if (null != panelImports) {
			this.checkBoxFirstRowAsField.setSelectedEx(externalDataSelectAll());
		}
	}

	private Boolean externalDataSelectAll() {
		Boolean result = null;
		int selectCount = 0;
		int unSelectCount = 0;
		for (PanelImport tempPanel : panelImports) {
			boolean select = ((PanelTransformForMicrosoft) tempPanel.getTransform()).getCheckBoxFirstRowAsField().isSelected();
			if (select) {
				selectCount++;
			} else if (!select) {
				unSelectCount++;
			}
		}
		if (selectCount == panelImports.size()) {
			result = true;
		} else if (unSelectCount == panelImports.size()) {
			result = false;
		}
		return result;
	}

	@Override
	public void registEvents() {
		removeEvents();
		this.textFieldSeparator.getDocument().addDocumentListener(this.separatorListener);
		this.checkBoxFirstRowAsField.addItemListener(this.itemListener);
		this.checkBoxImportIndexData.addActionListener(this.actionListener);
		this.comboBoxWKT.addItemListener(wktItemListener);
		this.comboBoxX.addItemListener(commonItemListener);
		this.comboBoxY.addItemListener(commonItemListener);
		this.comboBoxZ.addItemListener(commonItemListener);
		this.radioButtonIndex.addItemListener(radioButtonListener);
		this.radioButtonIndexWKT.addItemListener(radioButtonListener);
	}

	@Override
	public void removeEvents() {
		this.textFieldSeparator.getDocument().removeDocumentListener(this.separatorListener);
		this.checkBoxFirstRowAsField.removeItemListener(this.itemListener);
		this.checkBoxImportIndexData.removeActionListener(this.actionListener);
		this.comboBoxWKT.removeItemListener(wktItemListener);
		this.comboBoxX.removeItemListener(commonItemListener);
		this.radioButtonIndex.removeItemListener(radioButtonListener);
		this.radioButtonIndexWKT.removeItemListener(radioButtonListener);
	}

	@Override
	public void initResources() {
		this.labelDataPreview.setText(CoreProperties.getString("String_DataPreview"));
		this.labelSeparator.setText(DataConversionProperties.getString("String_Label_Separator"));
		this.textFieldSeparator.setText(",");
		this.checkBoxFirstRowAsField.setText(CoreProperties.getString("String_FirstRowisField"));
		this.radioButtonIndexWKT.setText(CoreProperties.getString("String_WKTIndex"));
		this.radioButtonIndex.setText(CoreProperties.getString("String_XYField"));
		this.labelX.setText(CoreProperties.getString("string_longitude"));
		this.labelY.setText(CoreProperties.getString("string_latitude"));
		this.labelZ.setText(CoreProperties.getString("string_elevation"));
		this.checkBoxImportIndexData.setText(CoreProperties.getString("String_ImportIndexData"));
	}

	public JTextField getTextFieldSeparator() {
		return textFieldSeparator;
	}

	public JCheckBox getCheckBoxFirstRowAsField() {
		return checkBoxFirstRowAsField;
	}

	public JComboBox getComboBoxWKT() {
		return comboBoxWKT;
	}

	public JComboBox getComboBoxX() {
		return comboBoxX;
	}

	public JComboBox getComboBoxY() {
		return comboBoxY;
	}

	public JComboBox getComboBoxZ() {
		return comboBoxZ;
	}

	public String getSameSeparator() {
		String result = "";
		String temp = ((PanelTransformForMicrosoft) panelImports.get(0).getTransform()).getTextFieldSeparator().getText();
		boolean isSame = true;
		for (PanelImport tempPanel : panelImports) {
			String tempObject = ((PanelTransformForMicrosoft) tempPanel.getTransform()).getTextFieldSeparator().getText();
			if (!temp.equals(tempObject)) {
				isSame = false;
				break;
			}
		}
		if (isSame) {
			result = temp;
		}
		return result;
	}

	public void setIndexPanelEnabled() {
		boolean indexPanelEnabled = checkBoxImportIndexData.isSelected();
		this.radioButtonIndexWKT.setEnabled(indexPanelEnabled);
		this.radioButtonIndex.setEnabled(indexPanelEnabled);
		this.labelX.setEnabled(indexPanelEnabled);
		this.labelY.setEnabled(indexPanelEnabled);
		this.labelZ.setEnabled(indexPanelEnabled);
		boolean enabled = radioButtonIndex.isSelected();
		comboBoxX.setEnabled(enabled && indexPanelEnabled);
		comboBoxY.setEnabled(enabled && indexPanelEnabled);
		comboBoxZ.setEnabled(enabled && indexPanelEnabled);
		boolean wktEnabled = radioButtonIndexWKT.isSelected();
		comboBoxWKT.setEnabled(wktEnabled && indexPanelEnabled);
	}
	class ItemNode{
		Object itemInfo;
		Object nodeInfo;

		public Object getItemInfo() {
			return itemInfo;
		}

		public void setItemInfo(Object itemInfo) {
			this.itemInfo = itemInfo;
		}

		public Object getNodeInfo() {
			return nodeInfo;
		}

		public void setNodeInfo(Object nodeInfo) {
			this.nodeInfo = nodeInfo;
		}
	}
	class ItemNodeComboBoxRender implements ListCellRenderer<ItemNode>{
		@Override
		public Component getListCellRendererComponent(JList<? extends ItemNode> list, ItemNode value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel jLabel = new JLabel();
			if (value != null) {
				jLabel.setText((value).getItemInfo().toString());
			}
			return jLabel;
		}
	}
}
