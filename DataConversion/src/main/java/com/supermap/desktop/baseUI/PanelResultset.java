package com.supermap.desktop.baseUI;

import com.supermap.data.Datasource;
import com.supermap.data.Datasources;
import com.supermap.data.EncodeType;
import com.supermap.data.PixelFormat;
import com.supermap.data.conversion.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IImportSettingResultset;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.controls.utilities.ControlsResources;
import com.supermap.desktop.dataconversion.DataConversionProperties;
import com.supermap.desktop.iml.ImportInfo;
import com.supermap.desktop.implement.UserDefineType.ImportSettingGPX;
import com.supermap.desktop.importUI.PanelImport;
import com.supermap.desktop.importUI.PanelTransformForImage;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.StateChangeEvent;
import com.supermap.desktop.ui.StateChangeListener;
import com.supermap.desktop.ui.TristateCheckBox;
import com.supermap.desktop.ui.controls.DataCell;
import com.supermap.desktop.ui.controls.DatasetTypeComboBox;
import com.supermap.desktop.ui.controls.DatasourceComboBox;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.utilities.EncodeTypeUtilities;
import com.supermap.desktop.utilities.FileUtilities;
import com.supermap.desktop.utilities.PixelFormatUtilities;
import com.supermap.desktop.utilities.StringUtilities;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * Created by xie on 2016/9/29.
 * 结果设置界面
 */
public class PanelResultset extends JPanel implements IImportSettingResultset {

	private PanelImport owner;
	private JLabel labelDatasource;
	private DatasourceComboBox comboBoxDatasource;
	private JLabel labelDatasetName;
	private JTextField textFieldDatasetName;
	private JLabel labelEncodeType;
	private JComboBox comboBoxEncodeType;
	private JLabel labelImportMode;
	private JComboBox comboBoxImportMode;
	private JLabel labelDatasetType;
	private JComboBox comboBoxDatasetType;
	private TristateCheckBox checkBoxFieldIndex;
	private TristateCheckBox checkBoxSpatialIndex;
	private ImportInfo importInfo;
	private ImportSetting importSetting;
	private ArrayList<PanelImport> panelImports;
	private int layeroutType;
	//    private final static int DATASOURCE_TYPE = 0;
	private final static int DATASET_TYPE = 1;
	private final static int IMPORTMODE_TYPE = 2;
	private final static int ENCODE_TYPE = 3;
	private final static int SPATIALINDEX_TYPE = 4;
	private final static int FIELDINDEX_TYPE = 5;
	private final static int IMPORTEMPTYDATASET_TYPE = 6;

	//Modify by xie 2017.11.24 新增空数据集判断
	private TristateCheckBox checkBoxImportEmptyDataset;

	private ItemListener datasourceListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				Datasource tempDatasource = comboBoxDatasource.getSelectedDatasource();
				if (null != panelImports) {
					for (PanelImport tempPanelImport : panelImports) {
						tempPanelImport.getResultset().getComboBoxDatasource().setSelectedDatasource(tempDatasource);
					}
				} else {
					importSetting.setTargetDatasource(tempDatasource);
					textFieldDatasetName.setText(tempDatasource.getDatasets().getAvailableDatasetName(textFieldDatasetName.getText()));
				}
			}
		}
	};

	private ItemListener encodeTypeListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				if (null != panelImports) {
					for (PanelImport tempPanelImport : panelImports) {
						tempPanelImport.getResultset().getComboBoxEncodeType().setSelectedItem(comboBoxEncodeType.getSelectedItem());
					}
				} else {
					String encodeType = comboBoxEncodeType.getSelectedItem().toString();
					if (importSetting instanceof ImportSettingGRD && encodeType.equals("SGL")) {
						Application.getActiveApplication().getOutput().output(MessageFormat.format(DataConversionProperties.getString("String_EncodingError"), importSetting.getSourceFilePath(),
								encodeType, "LZW"));
						importSetting.setTargetEncodeType(EncodeType.LZW);
					} else if (importSetting instanceof ImportSettingSHP && encodeType.equals(CoreProperties.getString("String_EncodeType_Int32"))) {
						Application.getActiveApplication().getOutput().output(MessageFormat.format(DataConversionProperties.getString("String_EncodingError"), importSetting.getSourceFilePath(),
								encodeType, CoreProperties.getString("String_EncodeType_None")));
						importSetting.setTargetEncodeType(EncodeType.NONE);
					} else {
						importSetting.setTargetEncodeType(EncodeTypeUtilities.valueOf(encodeType));
					}
				}
			}
		}
	};
	private ItemListener importModeListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				if (null != panelImports) {
					for (PanelImport tempPanelImport : panelImports) {
						tempPanelImport.getResultset().getComboBoxImportMode().setSelectedItem(comboBoxImportMode.getSelectedItem());
					}
				} else {
					int importModel = comboBoxImportMode.getSelectedIndex();
					switch (importModel) {
						case 0:
							importSetting.setImportMode(ImportMode.NONE);
							break;
						case 1:
							importSetting.setImportMode(ImportMode.APPEND);
							break;
						case 2:
							importSetting.setImportMode(ImportMode.OVERWRITE);
						default:
							break;
					}
				}
			}
		}
	};
	private ItemListener datasetTypeListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				String datasetType;
				if (comboBoxDatasetType instanceof DatasetTypeComboBox) {
					datasetType = ((DatasetTypeComboBox) comboBoxDatasetType).getSelectedDatasetTypeName();
				} else {
					datasetType = comboBoxDatasetType.getSelectedItem().toString();
				}
				if (null != panelImports) {
					for (PanelImport tempPanelImport : panelImports) {
						if (datasetType.equalsIgnoreCase(CoreProperties.getString("String_DatasetType_CAD"))) {
							tempPanelImport.getResultset().getComboBoxDatasetType().setSelectedIndex(0);
						} else if (datasetType.equalsIgnoreCase(ControlsProperties.getString("string_comboboxitem_sample"))) {
							tempPanelImport.getResultset().getComboBoxDatasetType().setSelectedIndex(1);
						} else if (datasetType.equalsIgnoreCase(CoreProperties.getString("String_DatasetType_Image"))) {
							tempPanelImport.getResultset().getComboBoxDatasetType().setSelectedIndex(0);
						} else if (datasetType.equalsIgnoreCase(CoreProperties.getString("String_DatasetType_Grid"))) {
							tempPanelImport.getResultset().getComboBoxDatasetType().setSelectedIndex(1);
						} else if (datasetType.equals(ControlsProperties.getString("String_datasetType2D"))) {
							tempPanelImport.getResultset().getComboBoxDatasetType().setSelectedIndex(0);
						} else if (datasetType.equals(ControlsProperties.getString("String_datasetType3D"))) {
							tempPanelImport.getResultset().getComboBoxDatasetType().setSelectedIndex(1);
						}
					}
				} else {
					if (datasetType.equals(CoreProperties.getString("String_DatasetType_CAD"))) {
						if (importSetting instanceof ImportSettingTAB) {
							((ImportSettingTAB) importSetting).setImportingAsCAD(true);
						} else if (importSetting instanceof ImportSettingMIF) {
							((ImportSettingMIF) importSetting).setImportingAsCAD(true);
						} else if (importSetting instanceof ImportSettingDWG) {
							((ImportSettingDWG) importSetting).setImportingAsCAD(true);
						} else if (importSetting instanceof ImportSettingDXF) {
							((ImportSettingDXF) importSetting).setImportingAsCAD(true);
						} else if (importSetting instanceof ImportSettingKML) {
							((ImportSettingKML) importSetting).setImportingAsCAD(true);
						} else if (importSetting instanceof ImportSettingKMZ) {
							((ImportSettingKMZ) importSetting).setImportingAsCAD(true);
						} else if (importSetting instanceof ImportSettingMAPGIS) {
							((ImportSettingMAPGIS) importSetting).setImportingAsCAD(true);
						} else if (importSetting instanceof ImportSettingDGN) {
							((ImportSettingDGN) importSetting).setImportingAsCAD(true);
						} else if (importSetting instanceof ImportSettingGeoJson) {
							((ImportSettingGeoJson) importSetting).setImportingAsCAD(true);
						}
					} else if (datasetType.equals(ControlsProperties.getString("string_comboboxitem_sample"))) {
						if (importSetting instanceof ImportSettingTAB) {
							((ImportSettingTAB) importSetting).setImportingAsCAD(false);
						} else if (importSetting instanceof ImportSettingMIF) {
							((ImportSettingMIF) importSetting).setImportingAsCAD(false);
						} else if (importSetting instanceof ImportSettingDWG) {
							((ImportSettingDWG) importSetting).setImportingAsCAD(false);
						} else if (importSetting instanceof ImportSettingDXF) {
							((ImportSettingDXF) importSetting).setImportingAsCAD(false);
						} else if (importSetting instanceof ImportSettingKML) {
							((ImportSettingKML) importSetting).setImportingAsCAD(false);
						} else if (importSetting instanceof ImportSettingKMZ) {
							((ImportSettingKMZ) importSetting).setImportingAsCAD(false);
						} else if (importSetting instanceof ImportSettingMAPGIS) {
							((ImportSettingMAPGIS) importSetting).setImportingAsCAD(false);
						} else if (importSetting instanceof ImportSettingDGN) {
							((ImportSettingDGN) importSetting).setImportingAsCAD(false);
						} else if (importSetting instanceof ImportSettingGeoJson) {
							((ImportSettingGeoJson) importSetting).setImportingAsCAD(false);
						}
					} else if (datasetType.equals(CoreProperties.getString("String_DatasetType_Image"))) {
						if (importSetting instanceof ImportSettingJPG) {
							((ImportSettingJPG) importSetting).setImportingAsGrid(false);
						} else if (importSetting instanceof ImportSettingJP2) {
							((ImportSettingJP2) importSetting).setImportingAsGrid(false);
						} else if (importSetting instanceof ImportSettingPNG) {
							((ImportSettingPNG) importSetting).setImportingAsGrid(false);
						} else if (importSetting instanceof ImportSettingBMP) {
							((ImportSettingBMP) importSetting).setImportingAsGrid(false);
						} else if (importSetting instanceof ImportSettingIMG) {
							((ImportSettingIMG) importSetting).setImportingAsGrid(false);
						} else if (importSetting instanceof ImportSettingTIF) {
							((ImportSettingTIF) importSetting).setImportingAsGrid(false);
						} else if (importSetting instanceof ImportSettingGIF) {
							((ImportSettingGIF) importSetting).setImportingAsGrid(false);
						} else if (importSetting instanceof ImportSettingMrSID) {
							((ImportSettingMrSID) importSetting).setImportingAsGrid(false);
						} else if (importSetting instanceof ImportSettingECW) {
							((ImportSettingECW) importSetting).setImportingAsGrid(false);
						}
					} else if (datasetType.equals(CoreProperties.getString("String_DatasetType_Grid"))) {
						if (importSetting instanceof ImportSettingJPG) {
							((ImportSettingJPG) importSetting).setImportingAsGrid(true);
						} else if (importSetting instanceof ImportSettingJP2) {
							((ImportSettingJP2) importSetting).setImportingAsGrid(true);
						} else if (importSetting instanceof ImportSettingPNG) {
							((ImportSettingPNG) importSetting).setImportingAsGrid(true);
						} else if (importSetting instanceof ImportSettingBMP) {
							((ImportSettingBMP) importSetting).setImportingAsGrid(true);
						} else if (importSetting instanceof ImportSettingIMG) {
							((ImportSettingIMG) importSetting).setImportingAsGrid(true);
						} else if (importSetting instanceof ImportSettingTIF) {
							((ImportSettingTIF) importSetting).setImportingAsGrid(true);
						} else if (importSetting instanceof ImportSettingGIF) {
							((ImportSettingGIF) importSetting).setImportingAsGrid(true);
						} else if (importSetting instanceof ImportSettingMrSID) {
							((ImportSettingMrSID) importSetting).setImportingAsGrid(true);
						} else if (importSetting instanceof ImportSettingECW) {
							((ImportSettingECW) importSetting).setImportingAsGrid(true);
						}
					} else if (datasetType.equals(ControlsProperties.getString("String_datasetType2D"))) {
						((ImportSettingLIDAR) importSetting).setImportingAs3D(false);
					} else if (datasetType.equals(ControlsProperties.getString("String_datasetType3D"))) {
						((ImportSettingLIDAR) importSetting).setImportingAs3D(true);
					}
				}
				resetImportModel();
			}
		}
	};

	private void resetImportModel() {
		String newdatasetType;
		if (comboBoxDatasetType instanceof DatasetTypeComboBox) {
			newdatasetType = ((DatasetTypeComboBox) comboBoxDatasetType).getSelectedDatasetTypeName();
		} else {
			newdatasetType = comboBoxDatasetType.getSelectedItem().toString();
		}
		if (newdatasetType.equalsIgnoreCase(CoreProperties.getString("String_DatasetType_Image"))) {
			if (owner.getTransform() instanceof PanelTransformForImage) {
				((PanelTransformForImage) owner.getTransform()).getComboBoxBandImportModel().setModel(new DefaultComboBoxModel(new String[]{CoreProperties.getString("String_MultiBand_SingleBand"),
						CoreProperties.getString("String_MultiBand_MultiBand"), CoreProperties.getString("String_MultiBand_Composite")}));
			}
		} else if (newdatasetType.equalsIgnoreCase(CoreProperties.getString("String_DatasetType_Grid"))) {
			if (owner.getTransform() instanceof PanelTransformForImage) {
				((PanelTransformForImage) owner.getTransform()).getComboBoxBandImportModel().setModel(new DefaultComboBoxModel(new String[]{CoreProperties.getString("String_MultiBand_SingleBand"),
						CoreProperties.getString("String_MultiBand_MultiBand")}));
			}
			setImgImportModel();
		}
	}

	private void setImgImportModel() {
		if (containsFileType(FileType.TIF) || containsFileType(FileType.IMG)) {
			((PanelTransformForImage) owner.getTransform()).getComboBoxBandImportModel().setModel(new DefaultComboBoxModel(new String[]{CoreProperties.getString("String_MultiBand_SingleBand")}));
		}
	}

	private StateChangeListener fieldIndexListener = new StateChangeListener() {

		@Override
		public void stateChange(StateChangeEvent e) {
			if (null != panelImports) {
				for (PanelImport tempPanelImport : panelImports) {
					if (tempPanelImport.getResultset().getCheckBoxFieldIndex().isVisible()) {
						tempPanelImport.getResultset().getCheckBoxFieldIndex().setSelected(checkBoxFieldIndex.isSelected());
					}
				}
			} else {
				importInfo.setFieldIndex(checkBoxFieldIndex.isSelected());
			}
		}
	};
	private StateChangeListener spatialIndexListener = new StateChangeListener() {

		@Override
		public void stateChange(StateChangeEvent e) {
			if (null != panelImports) {
				for (PanelImport tempPanelImport : panelImports) {
					if (tempPanelImport.getResultset().getCheckBoxSpatialIndex().isVisible()) {
						tempPanelImport.getResultset().getCheckBoxSpatialIndex().setSelected(checkBoxSpatialIndex.isSelected());
					}
				}
			} else {
				importInfo.setSpatialIndex(checkBoxSpatialIndex.isSelected());
			}
		}
	};
	private StateChangeListener importEmptyDatasetListener = new StateChangeListener() {
		@Override
		public void stateChange(StateChangeEvent e) {
			if (null != panelImports) {
				for (PanelImport tempPanelImport : panelImports) {
					if (tempPanelImport.getResultset().getCheckBoxImportEmptyDataset().isVisible()) {
						tempPanelImport.getResultset().getCheckBoxImportEmptyDataset().setSelected(checkBoxImportEmptyDataset.isSelected());
					}
				}
			} else {
				importEmptyDataset(checkBoxImportEmptyDataset.isSelected());
			}
		}
	};
	private DocumentListener documentListener = new DocumentListener() {
		@Override
		public void insertUpdate(DocumentEvent e) {
			setTargetDatasetName();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			setTargetDatasetName();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			setTargetDatasetName();
		}

		private void setTargetDatasetName() {
			Datasource targetDatasource = comboBoxDatasource.getSelectedDatasource();
			if (!StringUtilities.isNullOrEmpty(textFieldDatasetName.getText()) && !StringUtilities.isNullOrEmpty(targetDatasource.getDatasets().getAvailableDatasetName(textFieldDatasetName.getText()))) {
				String targetDatasetName = targetDatasource.getDatasets().getAvailableDatasetName(textFieldDatasetName.getText());
				if (null != panelImports) {
					for (PanelImport tempPanelImport : panelImports) {
						tempPanelImport.getResultset().getTextFieldDatasetName().setText(textFieldDatasetName.getText());
					}
				} else {
					importSetting.setTargetDatasetName(targetDatasetName);
				}
			}
		}
	};
	private JPanel panelCheckBox;

	public PanelResultset(PanelImport owner, ImportInfo importInfo) {
		this.owner = owner;
		this.importInfo = importInfo;
		this.importSetting = importInfo.getImportSetting();
		initComponents();
		initLayerout();
		initResources();
		registEvents();
		setComponentName();
	}

	public PanelResultset(PanelImport owner, ArrayList<PanelImport> panelImports, int layeroutType) {
		this.owner = owner;
		this.panelImports = panelImports;
		this.importSetting = panelImports.get(panelImports.size() - 1).getImportInfo().getImportSetting();
		this.layeroutType = layeroutType;
		initComponents();
		if (this.layeroutType == PackageInfo.SAME_TYPE) {
			initLayerout();
		} else {
			resetLayout(layeroutType);
		}
		initResources();
		registEvents();
		setComponentName();
	}

	private void setDefaultImportSettingEncode() {
		importSetting.setTargetEncodeType(EncodeTypeUtilities.valueOf(comboBoxEncodeType.getSelectedItem().toString()));
	}

	private void resetLayout(int layeroutType) {
		this.setLayout(new GridBagLayout());
		if (layeroutType == PackageInfo.VERTICAL_TYPE) {
			initComboboxEncodeType(false);
			setDefaultImportSettingEncode();
			initDefaultLayout();
			this.add(this.checkBoxFieldIndex, new GridBagConstraintsHelper(0, 2, 4, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 10).setFill(GridBagConstraints.NONE).setWeight(0, 0));
			this.add(this.checkBoxSpatialIndex, new GridBagConstraintsHelper(4, 2, 4, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 10).setFill(GridBagConstraints.NONE).setWeight(0, 0));
			this.checkBoxFieldIndex.setEnabled(isVisible(true));
			this.checkBoxSpatialIndex.setEnabled(isVisible(false));
		} else if (layeroutType == PackageInfo.GRID_TYPE) {
			initDefaultLayout();
			this.remove(labelDatasetName);
			this.remove(textFieldDatasetName);
			this.comboBoxDatasetType = new DatasetTypeComboBox(new String[]{CoreProperties.getString("String_DatasetType_Image"), CoreProperties.getString("String_DatasetType_Grid")});
			this.add(this.labelDatasetType, new GridBagConstraintsHelper(4, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 10).setFill(GridBagConstraints.NONE).setWeight(0, 0));
			this.add(this.comboBoxDatasetType, new GridBagConstraintsHelper(6, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 10).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
			this.comboBoxDatasource.setPreferredSize(PackageInfo.defaultSize);
			this.comboBoxEncodeType.setPreferredSize(PackageInfo.defaultSize);
			setGridEncodeTypeModel();
		} else if (layeroutType == PackageInfo.GRID_AND_VERTICAL_TYPE) {
			this.add(this.labelDatasource, new GridBagConstraintsHelper(0, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 5, 5, 20).setFill(GridBagConstraints.NONE).setWeight(0, 0));
			this.add(this.comboBoxDatasource, new GridBagConstraintsHelper(2, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 20).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
			this.add(this.labelImportMode, new GridBagConstraintsHelper(4, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 10).setFill(GridBagConstraints.NONE).setWeight(0, 0));
			this.add(this.comboBoxImportMode, new GridBagConstraintsHelper(6, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 10).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
		}
		setDefaultSize();
		initDatasetType();
		initEncodeType();
		initCheckboxState();
	}

	private boolean isVisible(boolean isFieldType) {
		boolean isVisible = true;
		for (PanelImport tempImport : panelImports) {
			if (isFieldType && !(tempImport.getResultset()).getCheckBoxFieldIndex().isVisible()) {
				isVisible = false;
				break;
			} else if (!isFieldType && !(tempImport.getResultset()).getCheckBoxSpatialIndex().isVisible()) {
				isVisible = false;
				break;
			}

		}
		return isVisible;
	}

	@Override
	public void initComponents() {
		this.labelDatasource = new JLabel();
		this.comboBoxDatasource = new DatasourceComboBox();
		removeDatasource();
		initDatasource();
		this.labelDatasetName = new JLabel();
		this.textFieldDatasetName = new JTextField();
		initDatasetName();
		this.labelEncodeType = new JLabel();
		this.comboBoxEncodeType = new JComboBox();
		this.labelImportMode = new JLabel();
		this.comboBoxImportMode = new JComboBox();
		initImportMode();
		this.labelDatasetType = new JLabel();
		this.checkBoxFieldIndex = new TristateCheckBox();
		this.checkBoxSpatialIndex = new TristateCheckBox();
		this.checkBoxImportEmptyDataset = new TristateCheckBox();
		this.checkBoxImportEmptyDataset.setSelected(false);
		this.checkBoxSpatialIndex.setSelected(false);
		this.checkBoxFieldIndex.setSelected(false);
		this.comboBoxEncodeType.setEditable(true);
		((JTextField) this.comboBoxEncodeType.getEditor().getEditorComponent()).setEditable(false);
		this.comboBoxImportMode.setEditable(true);
		((JTextField) this.comboBoxImportMode.getEditor().getEditorComponent()).setEditable(false);
	}

	private void importEmptyDataset(boolean selected) {
		if (importSetting instanceof ImportSettingDWG) {
			((ImportSettingDWG) importSetting).setImportEmptyDataset(selected);
		}
		if (importSetting instanceof ImportSettingDXF) {
			((ImportSettingDXF) importSetting).setImportEmptyDataset(selected);
		}
		if (importSetting instanceof ImportSettingSHP) {
			((ImportSettingSHP) importSetting).setImportEmptyDataset(selected);
		}
		if (importSetting instanceof ImportSettingGeoJson) {
			((ImportSettingGeoJson) importSetting).setImportEmptyDataset(selected);
		}
		if (importSetting instanceof ImportSettingSimpleJson) {
			((ImportSettingSimpleJson) importSetting).setImportEmptyDataset(selected);
		}
	}

	public void setComponentName() {
		ComponentUIUtilities.setName(this.owner, "PanelResultset_owner");
		ComponentUIUtilities.setName(this.labelDatasource, "PanelResultset_labelDatasource");
		ComponentUIUtilities.setName(this.labelDatasetName, "PanelResultset_labelDatasetName");
		ComponentUIUtilities.setName(this.labelEncodeType, "PanelResultset_labelEncodeType");
		ComponentUIUtilities.setName(this.labelImportMode, "PanelResultset_labelImportMode");
		ComponentUIUtilities.setName(this.labelDatasetType, "PanelResultset_labelDatasetType");
		ComponentUIUtilities.setName(this.comboBoxDatasetType, "PanelResultset_comboBoxDatasetType");
		ComponentUIUtilities.setName(this.comboBoxDatasource, "PanelResultset_comboBoxDatasource");
		ComponentUIUtilities.setName(this.comboBoxEncodeType, "PanelResultset_comboBoxEncodeType");
		ComponentUIUtilities.setName(this.comboBoxImportMode, "PanelResultset_comboBoxImportMode");
		ComponentUIUtilities.setName(this.checkBoxFieldIndex, "PanelResultset_checkBoxFieldIndex");
		ComponentUIUtilities.setName(this.checkBoxSpatialIndex, "PanelResultset_checkBoxSpatialIndex");
		ComponentUIUtilities.setName(this.textFieldDatasetName, "PanelResultset_textFieldDatasetName");
		ComponentUIUtilities.setName(this.checkBoxImportEmptyDataset, "PanelResultset_checkBoxImportEmptyDataset");
	}

	private void removeDatasource() {
		//删除只读数据源
		Datasources datasources = Application.getActiveApplication().getWorkspace().getDatasources();
		int size = datasources.getCount();
		for (int i = 0; i < size; i++) {
			if (datasources.get(i).isReadOnly()) {
				this.comboBoxDatasource.removeDataSource(datasources.get(i));
			}
		}
	}

	private void initEncodeType() {
		if (null != panelImports) {
			this.comboBoxEncodeType.setSelectedItem(selectedItem(ENCODE_TYPE));
		}
	}


	private void setGridEncodeTypeModel() {
		ArrayList<String> listModel = new ArrayList();
		JComboBox tempEncodeComboBox = panelImports.get(0).getResultset().getComboBoxEncodeType();
		int size = tempEncodeComboBox.getItemCount();
		for (int i = 0; i < size; i++) {
			listModel.add(tempEncodeComboBox.getItemAt(i).toString());
		}
		for (int i = 0; i < panelImports.size(); i++) {
			ArrayList<String> tempFileTypes = new ArrayList();
			JComboBox compare = panelImports.get(i).getResultset().getComboBoxEncodeType();
			int compareItemCount = compare.getItemCount();
			for (int j = 0; j < compareItemCount; j++) {
				tempFileTypes.add(compare.getItemAt(j).toString());
			}
			listModel.retainAll(tempFileTypes);
		}
		this.comboBoxEncodeType.setModel(new DefaultComboBoxModel(listModel.toArray(new String[listModel.size()])));
	}

	private boolean containsFileType(FileType fileType) {
		boolean result = false;
		if (null == panelImports) {
			result = importSetting.getSourceFileType() == fileType;
		} else {
			int size = panelImports.size();
			for (int i = 0; i < size; i++) {
				if (panelImports.get(i).getImportInfo().getImportSetting().getSourceFileType() == fileType) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	private void initImportMode() {
		this.comboBoxImportMode.setModel(new DefaultComboBoxModel(new String[]{
				CoreProperties.getString("String_None"),
				ControlsProperties.getString("String_FormImport_Append"),
				ControlsProperties.getString("String_FormImport_OverWrite")
		}));
		if (null != panelImports) {
			this.comboBoxImportMode.setSelectedItem(selectedItem(IMPORTMODE_TYPE));
			return;
		} else if (null != importSetting.getImportMode()) {
			ImportMode mode = importSetting.getImportMode();
			if (mode.equals(ImportMode.NONE)) {
				this.comboBoxImportMode.setSelectedIndex(0);
			} else if (mode.equals(ImportMode.APPEND)) {
				this.comboBoxImportMode.setSelectedIndex(1);
			} else {
				this.comboBoxImportMode.setSelectedIndex(2);
			}
			return;
		} else {
			this.importSetting.setImportMode(ImportMode.NONE);
			return;
		}
	}

	private void initDatasetName() {
		if (layeroutType == 4 && hasSameName()) {
			this.textFieldDatasetName.setText(importSetting.getTargetDatasetName());
		} else if (!StringUtilities.isNullOrEmpty(importSetting.getTargetDatasetName()) && panelImports == null) {
			String availableName = this.comboBoxDatasource.getSelectedDatasource().getDatasets().getAvailableDatasetName(importSetting.getTargetDatasetName());
			this.textFieldDatasetName.setText(availableName);
			this.importSetting.setTargetDatasetName(availableName);
		} else if (StringUtilities.isNullOrEmpty(importSetting.getTargetDatasetName()) && panelImports == null) {
			String textInfo = FileUtilities.getFileAlias(this.importSetting.getSourceFilePath());
			if (null != textInfo) {
				String availableName = this.comboBoxDatasource.getSelectedDatasource().getDatasets().getAvailableDatasetName(textInfo);
				this.textFieldDatasetName.setText(availableName);
				this.importSetting.setTargetDatasetName(availableName);
			}
		}
	}

	private boolean hasSameName() {
		String temp = panelImports.get(0).getImportInfo().getImportSetting().getTargetDatasetName();
		boolean isSame = true;
		for (PanelImport tempPanel : panelImports) {
			String tempObject = tempPanel.getImportInfo().getImportSetting().getTargetDatasetName();
			if (!temp.equals(tempObject)) {
				isSame = false;
				break;
			}
		}
		return isSame;
	}

	private void initDatasource() {
		if (null == panelImports) {
			this.comboBoxDatasource.setSelectedDatasource(importSetting.getTargetDatasource());
		} else {
			this.comboBoxDatasource.setSelectedDatasource(selectedDatasource());
		}
	}

	private Datasource selectedDatasource() {
		Datasource result = null;
		Datasource temp = null;
		Datasource info = panelImports.get(0).getImportInfo().getImportSetting().getTargetDatasource();
		if (null != info) {
			temp = info;
		} else {
			result = temp;
			return result;
		}
		boolean isSame = true;
		for (PanelImport tempPanel : panelImports) {
			String tempObject = tempPanel.getResultset().getComboBoxDatasource().getSelectedDatasource().getAlias();
			if (!temp.getAlias().equals(tempObject)) {
				isSame = false;
				break;
			}
		}
		if (isSame) {
			result = info;
		}
		return result;
	}

	private Boolean externalDataSelectAll(int type) {
		Boolean result = null;
		int selectCount = 0;
		int unSelectCount = 0;
		for (PanelImport tempPanel : panelImports) {
			boolean select = getCheckbox(tempPanel, type).isSelected();
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

	private JCheckBox getCheckbox(PanelImport panelImport, int type) {
		JCheckBox result = null;
		if (type == SPATIALINDEX_TYPE) {
			result = panelImport.getResultset().getCheckBoxSpatialIndex();
		} else if (type == FIELDINDEX_TYPE) {
			result = panelImport.getResultset().getCheckBoxFieldIndex();
		} else if (type == IMPORTEMPTYDATASET_TYPE) {
			result = panelImport.getResultset().getCheckBoxImportEmptyDataset();
		}
		return result;
	}

	private Object selectedItem(int type) {
		Object result = null;
		Object temp = "";
		Object info = getResult(panelImports.get(0).getResultset(), type).toString();
		if (null != info) {
			temp = info;
		} else {
			result = temp;
			return result;
		}
		boolean isSame = true;
		for (PanelImport tempPanel : panelImports) {
			String tempObject = getResult(tempPanel.getResultset(), type).toString();
			if (!temp.equals(tempObject)) {
				isSame = false;
				break;
			}
		}
		if (isSame) {
			result = info;
		}
		return result;
	}

	private Object getResult(IImportSettingResultset tempPanel, int type) {
		Object result = "";
		if (type == DATASET_TYPE && null != tempPanel.getComboBoxDatasetType() && null != tempPanel.getComboBoxDatasetType().getSelectedItem()) {
			result = tempPanel.getComboBoxDatasetType().getSelectedItem();
		} else if (type == ENCODE_TYPE && null != tempPanel.getComboBoxEncodeType().getSelectedItem()) {
			result = tempPanel.getComboBoxEncodeType().getSelectedItem();
		} else if (type == IMPORTMODE_TYPE && null != tempPanel.getComboBoxImportMode().getSelectedItem()) {
			result = tempPanel.getComboBoxImportMode().getSelectedItem();
		}
		return result;
	}

	@Override
	public void initLayerout() {
		this.setLayout(new GridBagLayout());
		if (importSetting instanceof ImportSettingCSV || importSetting instanceof ImportSettingGPX) {
			this.removeAll();
			this.add(this.labelDatasource, new GridBagConstraintsHelper(0, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 5, 5, 20).setFill(GridBagConstraints.NONE).setWeight(0, 0));
			this.add(this.comboBoxDatasource, new GridBagConstraintsHelper(2, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 20).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
			this.add(this.labelDatasetName, new GridBagConstraintsHelper(4, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 10).setFill(GridBagConstraints.NONE).setWeight(0, 0));
			this.add(this.textFieldDatasetName, new GridBagConstraintsHelper(6, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 10).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
			setDefaultSize();
			this.checkBoxFieldIndex.setVisible(false);
			this.checkBoxSpatialIndex.setVisible(false);
		} else if (importSetting instanceof ImportSettingWOR) {
			initComboboxEncodeType(false);
			setDefaultImportSettingEncode();
			this.removeAll();
			this.add(this.labelDatasource, new GridBagConstraintsHelper(0, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 5, 5, 20).setFill(GridBagConstraints.NONE).setWeight(0, 0));
			this.add(this.comboBoxDatasource, new GridBagConstraintsHelper(2, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 20).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
			this.add(this.labelEncodeType, new GridBagConstraintsHelper(4, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 10).setFill(GridBagConstraints.NONE).setWeight(0, 0));
			this.add(this.comboBoxEncodeType, new GridBagConstraintsHelper(6, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 10).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));

			this.add(this.labelImportMode, new GridBagConstraintsHelper(0, 1, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 20).setFill(GridBagConstraints.NONE).setWeight(0, 0));
			this.add(this.comboBoxImportMode, new GridBagConstraintsHelper(2, 1, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 20).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
			setDefaultSize();
			this.checkBoxFieldIndex.setVisible(false);
			this.checkBoxSpatialIndex.setVisible(false);
		} else if (importSetting instanceof ImportSettingModel3DS || importSetting instanceof ImportSettingModelDXF
				|| importSetting instanceof ImportSettingModelFBX || importSetting instanceof ImportSettingModelOSG
				|| importSetting instanceof ImportSettingModelX) {
			this.removeAll();
			this.comboBoxDatasetType = new DatasetTypeComboBox(new String[]{CoreProperties.getString("String_DatasetType_Model")});
			this.add(this.labelDatasource, new GridBagConstraintsHelper(0, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 5, 5, 20).setFill(GridBagConstraints.NONE).setWeight(0, 0));
			this.add(this.comboBoxDatasource, new GridBagConstraintsHelper(2, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 20).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
			this.add(this.labelDatasetName, new GridBagConstraintsHelper(4, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 10).setFill(GridBagConstraints.NONE).setWeight(0, 0));
			this.add(this.textFieldDatasetName, new GridBagConstraintsHelper(6, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 10).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));

			this.add(this.labelDatasetType, new GridBagConstraintsHelper(0, 1, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 20).setFill(GridBagConstraints.NONE).setWeight(0, 0));
			this.add(this.comboBoxDatasetType, new GridBagConstraintsHelper(2, 1, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 20).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
			this.add(this.labelImportMode, new GridBagConstraintsHelper(4, 1, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 10).setFill(GridBagConstraints.NONE).setWeight(0, 0));
			this.add(this.comboBoxImportMode, new GridBagConstraintsHelper(6, 1, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 10).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
			setDefaultSize();
			this.checkBoxFieldIndex.setVisible(false);
			this.checkBoxSpatialIndex.setVisible(false);
		} else if (importSetting instanceof ImportSettingTAB || importSetting instanceof ImportSettingMIF
				|| importSetting instanceof ImportSettingDWG || importSetting instanceof ImportSettingDXF
				|| importSetting instanceof ImportSettingKML || importSetting instanceof ImportSettingKMZ
				|| importSetting instanceof ImportSettingMAPGIS || importSetting instanceof ImportSettingDGN
				|| importSetting instanceof ImportSettingGeoJson || importSetting instanceof ImportSettingSimpleJson) {
			initComboboxEncodeType(false);
			setDefaultImportSettingEncode();

			this.comboBoxDatasetType = new DatasetTypeComboBox(new String[]{CoreProperties.getString("String_DatasetType_CAD")});
			// 增加一个item为“简单数据集”--yuanR 17.2.14
			String fileParentPath = "/controlsresources/WorkspaceManager/Dataset/Image_SimpleDataset_Normal.png";
			URL url = ControlsResources.getResourceURL(fileParentPath);
			ImageIcon simpleDatasetIcon = new ImageIcon(url);
			this.comboBoxDatasetType.addItem(new DataCell(simpleDatasetIcon, ControlsProperties.getString("string_comboboxitem_sample")));

			setDefaultLayout();
			initTargetDatasetTypeForVector();
			setFullsize();
			if (importSetting instanceof ImportSettingDXF || importSetting instanceof ImportSettingDWG) {
				this.add(this.checkBoxImportEmptyDataset, new GridBagConstraintsHelper(0, 3, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 20).setFill(GridBagConstraints.NONE).setWeight(0, 0));
			}
			if (importSetting instanceof ImportSettingGeoJson) {
				this.panelCheckBox.removeAll();
				this.panelCheckBox.add(this.checkBoxImportEmptyDataset, new GridBagConstraintsHelper(0, 0, 4, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 10).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
			}
			// SimpleJson数据类型无法设置导入的数据集类型，数据集类型控件不做显示-yuanR2017.9.4
			if (importSetting instanceof ImportSettingSimpleJson) {
				this.remove(this.labelDatasetType);
				this.remove(this.comboBoxDatasetType);
				this.panelCheckBox.removeAll();
				this.add(this.checkBoxImportEmptyDataset, new GridBagConstraintsHelper(0, 2, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 20).setFill(GridBagConstraints.NONE).setWeight(0, 0));
			}
		} else if (importSetting instanceof ImportSettingJPG || importSetting instanceof ImportSettingJP2 ||
				importSetting instanceof ImportSettingPNG || importSetting instanceof ImportSettingBMP ||
				importSetting instanceof ImportSettingIMG || importSetting instanceof ImportSettingTIF ||
				importSetting instanceof ImportSettingGIF || importSetting instanceof ImportSettingMrSID
				|| importSetting instanceof ImportSettingECW) {
			if (importSetting instanceof ImportSettingECW) {
				this.comboBoxEncodeType.setModel(new DefaultComboBoxModel(new String[]{CoreProperties.getString("String_EncodeType_None")}));
			} else {
				initComboboxEncodeType(true);
			}
			if (importSetting instanceof ImportSettingJP2) {
				this.comboBoxEncodeType.insertItemAt("SGL", 2);
			}

			// 导入如果是rgb和rgba的image，默认就是EncodeType设置为png-yuanR2017.11.24
			try {
				BufferedImage bufferedImage = ImageIO.read(new File(importInfo.getFilePath()));
				if (bufferedImage != null && bufferedImage.getColorModel() != null) {
					if (PixelFormat.RGB.equals(PixelFormatUtilities.valueOf(bufferedImage.getColorModel().getPixelSize()))
							|| PixelFormat.RGBA.equals(PixelFormatUtilities.valueOf(bufferedImage.getColorModel().getPixelSize()))) {
						this.comboBoxEncodeType.setSelectedItem("PNG");
					}
				}
			} catch (IOException e) {
				//e.printStackTrace();
			}
			setDefaultImportSettingEncode();
			this.comboBoxDatasetType = new DatasetTypeComboBox(new String[]{CoreProperties.getString("String_DatasetType_Image"), CoreProperties.getString("String_DatasetType_Grid")});
			setDefaultLayout();
			this.panelCheckBox.removeAll();
			initTargetDatasetTypeForImage();
			setDefaultSize();

		} else if (importSetting instanceof ImportSettingSIT || importSetting instanceof ImportSettingGRD ||
				importSetting instanceof ImportSettingGBDEM || importSetting instanceof ImportSettingUSGSDEM ||
				importSetting instanceof ImportSettingSHP || importSetting instanceof ImportSettingE00 ||
				importSetting instanceof ImportSettingDBF || importSetting instanceof ImportSettingBIL ||
				importSetting instanceof ImportSettingBSQ || importSetting instanceof ImportSettingBIP ||
				importSetting instanceof ImportSettingTEMSClutter || importSetting instanceof ImportSettingVCT ||
				importSetting instanceof ImportSettingRAW || importSetting instanceof ImportSettingGJB ||
				importSetting instanceof ImportSettingTEMSVector || importSetting instanceof ImportSettingTEMSBuildingVector
				|| importSetting instanceof ImportSettingFileGDBVector) {
			this.comboBoxDatasetType = new DatasetTypeComboBox();
			if (importSetting instanceof ImportSettingGRD || importSetting instanceof ImportSettingGBDEM
					|| importSetting instanceof ImportSettingUSGSDEM) {
				this.comboBoxEncodeType.setModel(new DefaultComboBoxModel(new String[]{CoreProperties.getString("String_EncodeType_None"), "SGL", "LZW"}));
			} else if (importSetting instanceof ImportSettingSIT) {
				initComboboxEncodeType(true);
			} else {
				initComboboxEncodeType(false);
			}
			setDefaultImportSettingEncode();
			initDefaultLayout();
			if (importSetting instanceof ImportSettingSHP) {
				this.add(this.checkBoxFieldIndex, new GridBagConstraintsHelper(0, 2, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 10).setFill(GridBagConstraints.NONE).setWeight(0, 0));
				this.add(this.checkBoxSpatialIndex, new GridBagConstraintsHelper(2, 2, 4, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 10).setFill(GridBagConstraints.NONE).setWeight(0, 0));
				this.add(this.checkBoxImportEmptyDataset, new GridBagConstraintsHelper(4, 2, 4, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 10).setFill(GridBagConstraints.NONE).setWeight(0, 0));
			} else if (importSetting instanceof ImportSettingE00 || importSetting instanceof ImportSettingGJB
					|| importSetting instanceof ImportSettingTEMSVector || importSetting instanceof ImportSettingTEMSBuildingVector
					|| importSetting instanceof ImportSettingFileGDBVector) {
				this.add(this.checkBoxSpatialIndex, new GridBagConstraintsHelper(0, 2, 4, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 10).setFill(GridBagConstraints.NONE).setWeight(0, 0));
				if (importSetting instanceof ImportSettingGJB || importSetting instanceof ImportSettingTEMSVector
						|| importSetting instanceof ImportSettingTEMSBuildingVector || importSetting instanceof ImportSettingFileGDBVector) {
					this.labelDatasetName.setEnabled(true);
					this.textFieldDatasetName.setEnabled(true);
				}
				this.checkBoxFieldIndex.setVisible(false);
			} else {
				this.checkBoxFieldIndex.setVisible(false);
				this.checkBoxSpatialIndex.setVisible(false);
			}
			setDefaultSize();
		} else if (importSetting instanceof ImportSettingLIDAR) {
			initComboboxEncodeType(false);
			setDefaultImportSettingEncode();
			this.comboBoxDatasetType = new JComboBox(new String[]{ControlsProperties.getString("String_datasetType2D"), ControlsProperties.getString("String_datasetType3D")});
			this.comboBoxDatasetType.setEditable(true);
			((JTextField) this.comboBoxDatasetType.getEditor().getEditorComponent()).setEditable(false);
			initDefaultLayout();
			if (((ImportSettingLIDAR) importSetting).isImportingAs3D()) {
				this.comboBoxDatasetType.setSelectedIndex(1);
			} else {
				this.comboBoxDatasetType.setSelectedIndex(0);
			}
			this.add(this.labelDatasetType, new GridBagConstraintsHelper(0, 2, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 20).setFill(GridBagConstraints.NONE).setWeight(0, 0));
			this.add(this.comboBoxDatasetType, new GridBagConstraintsHelper(2, 2, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 20).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
			this.add(this.checkBoxSpatialIndex, new GridBagConstraintsHelper(4, 2, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 10).setFill(GridBagConstraints.NONE).setWeight(0, 0));
			this.checkBoxFieldIndex.setVisible(false);
			setDefaultSize();
		}
		initDatasetType();
		initEncodeType();
		initCheckboxState();

	}

	private void setFullsize() {
		setDefaultSize();
		this.checkBoxSpatialIndex.setPreferredSize(PackageInfo.defaultSize);
		this.checkBoxFieldIndex.setPreferredSize(PackageInfo.defaultSize);
	}

	private void initCheckboxState() {
		if (null != panelImports) {
			this.checkBoxSpatialIndex.setSelectedEx(externalDataSelectAll(SPATIALINDEX_TYPE));
			this.checkBoxFieldIndex.setSelectedEx(externalDataSelectAll(FIELDINDEX_TYPE));
			this.checkBoxImportEmptyDataset.setSelectedEx(externalDataSelectAll(IMPORTEMPTYDATASET_TYPE));
		}
	}

	private void initDatasetType() {
		if (null != panelImports && null != this.comboBoxDatasetType) {
			this.comboBoxDatasetType.setSelectedItem(selectedItem(DATASET_TYPE));
			String newdatasetType;
			if (comboBoxDatasetType instanceof DatasetTypeComboBox) {
				newdatasetType = ((DatasetTypeComboBox) comboBoxDatasetType).getSelectedDatasetTypeName();
			} else {
				newdatasetType = comboBoxDatasetType.getSelectedItem().toString();
			}
			if (null == newdatasetType && owner.getTransform() instanceof PanelTransformForImage) {
				//选择集不同且导入类型为img或者tiff时导入波段模式设置为多个单波段
				setImgImportModel();
			}
		}
	}

	private void setDefaultSize() {
		this.textFieldDatasetName.setPreferredSize(PackageInfo.defaultSize);
		this.comboBoxDatasource.setPreferredSize(PackageInfo.defaultSize);
		if (null != comboBoxDatasetType) {
			this.comboBoxDatasetType.setPreferredSize(PackageInfo.defaultSize);
		}
		this.comboBoxImportMode.setPreferredSize(PackageInfo.defaultSize);
		this.comboBoxEncodeType.setPreferredSize(PackageInfo.defaultSize);
	}

	private void initTargetDatasetTypeForVector() {
		this.comboBoxDatasetType.setSelectedIndex(1);
		if (importSetting instanceof ImportSettingTAB && ((ImportSettingTAB) importSetting).isImportingAsCAD()) {
			this.comboBoxDatasetType.setSelectedIndex(0);
		} else if (importSetting instanceof ImportSettingMIF && ((ImportSettingMIF) importSetting).isImportingAsCAD()) {
			this.comboBoxDatasetType.setSelectedIndex(0);
		} else if (importSetting instanceof ImportSettingDWG && ((ImportSettingDWG) importSetting).isImportingAsCAD()) {
			this.comboBoxDatasetType.setSelectedIndex(0);
		} else if (importSetting instanceof ImportSettingDXF && ((ImportSettingDXF) importSetting).isImportingAsCAD()) {
			this.comboBoxDatasetType.setSelectedIndex(0);
		} else if (importSetting instanceof ImportSettingKML && ((ImportSettingKML) importSetting).isImportingAsCAD()) {
			this.comboBoxDatasetType.setSelectedIndex(0);
		} else if (importSetting instanceof ImportSettingKMZ && ((ImportSettingKMZ) importSetting).isImportingAsCAD()) {
			this.comboBoxDatasetType.setSelectedIndex(0);
		} else if (importSetting instanceof ImportSettingMAPGIS && ((ImportSettingMAPGIS) importSetting).isImportingAsCAD()) {
			this.comboBoxDatasetType.setSelectedIndex(0);
		} else if (importSetting instanceof ImportSettingDGN && ((ImportSettingDGN) importSetting).isImportingAsCAD()) {
			this.comboBoxDatasetType.setSelectedIndex(0);
		} else if (importSetting instanceof ImportSettingGeoJson && ((ImportSettingGeoJson) importSetting).isImportingAsCAD()) {
			this.comboBoxDatasetType.setSelectedIndex(0);
		}
	}

	private void initTargetDatasetTypeForImage() {
		this.comboBoxDatasetType.setSelectedIndex(0);
		if (importSetting instanceof ImportSettingJPG && ((ImportSettingJPG) importSetting).isImportingAsGrid()) {
			this.comboBoxDatasetType.setSelectedIndex(1);
		} else if (importSetting instanceof ImportSettingJP2 && ((ImportSettingJP2) importSetting).isImportingAsGrid()) {
			this.comboBoxDatasetType.setSelectedIndex(1);
		} else if (importSetting instanceof ImportSettingPNG && ((ImportSettingPNG) importSetting).isImportingAsGrid()) {
			this.comboBoxDatasetType.setSelectedIndex(1);
		} else if (importSetting instanceof ImportSettingBMP && ((ImportSettingBMP) importSetting).isImportingAsGrid()) {
			this.comboBoxDatasetType.setSelectedIndex(1);
		} else if (importSetting instanceof ImportSettingIMG && ((ImportSettingIMG) importSetting).isImportingAsGrid()) {
			this.comboBoxDatasetType.setSelectedIndex(1);
		} else if (importSetting instanceof ImportSettingTIF && ((ImportSettingTIF) importSetting).isImportingAsGrid()) {
			this.comboBoxDatasetType.setSelectedIndex(1);
		} else if (importSetting instanceof ImportSettingGIF && ((ImportSettingGIF) importSetting).isImportingAsGrid()) {
			this.comboBoxDatasetType.setSelectedIndex(1);
		} else if (importSetting instanceof ImportSettingMrSID && ((ImportSettingMrSID) importSetting).isImportingAsGrid()) {
			this.comboBoxDatasetType.setSelectedIndex(1);
		} else if (importSetting instanceof ImportSettingECW && ((ImportSettingECW) importSetting).isImportingAsGrid()) {
			this.comboBoxDatasetType.setSelectedIndex(1);
		}
	}

	private void setDefaultLayout() {
		initDefaultLayout();
		this.panelCheckBox = new JPanel();
		this.panelCheckBox.setLayout(new GridBagLayout());
		this.panelCheckBox.add(this.checkBoxFieldIndex, new GridBagConstraintsHelper(0, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 10).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
		this.panelCheckBox.add(this.checkBoxSpatialIndex, new GridBagConstraintsHelper(2, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 10).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));

		this.add(this.labelDatasetType, new GridBagConstraintsHelper(0, 2, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 20).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		this.add(this.comboBoxDatasetType, new GridBagConstraintsHelper(2, 2, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 20).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
		this.add(this.panelCheckBox, new GridBagConstraintsHelper(4, 2, 4, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
	}

	private void initDefaultLayout() {
		this.removeAll();
		this.add(this.labelDatasource, new GridBagConstraintsHelper(0, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 5, 5, 20).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		this.add(this.comboBoxDatasource, new GridBagConstraintsHelper(2, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 20).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
		this.add(this.labelDatasetName, new GridBagConstraintsHelper(4, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 10).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		this.add(this.textFieldDatasetName, new GridBagConstraintsHelper(6, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 10).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));

		this.add(this.labelEncodeType, new GridBagConstraintsHelper(0, 1, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 20).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		this.add(this.comboBoxEncodeType, new GridBagConstraintsHelper(2, 1, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 20).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
		this.add(this.labelImportMode, new GridBagConstraintsHelper(4, 1, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 10).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		this.add(this.comboBoxImportMode, new GridBagConstraintsHelper(6, 1, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 10).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
	}

	private void initComboboxEncodeType(boolean isGrid) {
		if (isGrid) {
			this.comboBoxEncodeType.setModel(new DefaultComboBoxModel(new String[]{CoreProperties.getString("String_EncodeType_None"), "DCT",
					"PNG", "LZW"}));
			this.comboBoxEncodeType.setSelectedItem("DCT");
		} else {
			this.comboBoxEncodeType.setModel(new DefaultComboBoxModel(new String[]{CoreProperties.getString("String_EncodeType_None"), CoreProperties.getString("String_EncodeType_Byte"),
					CoreProperties.getString("String_EncodeType_Int16"), CoreProperties.getString("String_EncodeType_Int24"), CoreProperties.getString("String_EncodeType_Int32")}));
		}
	}

	@Override
	public void registEvents() {
		removeEvents();
		this.comboBoxDatasource.addItemListener(this.datasourceListener);
		this.textFieldDatasetName.getDocument().addDocumentListener(this.documentListener);
		this.comboBoxEncodeType.addItemListener(this.encodeTypeListener);
		this.comboBoxImportMode.addItemListener(this.importModeListener);
		if (null != this.comboBoxDatasetType) {
			this.comboBoxDatasetType.addItemListener(this.datasetTypeListener);
		}
		this.checkBoxFieldIndex.addStateChangeListener(this.fieldIndexListener);
		this.checkBoxSpatialIndex.addStateChangeListener(this.spatialIndexListener);
		this.checkBoxImportEmptyDataset.addStateChangeListener(this.importEmptyDatasetListener);
	}

	@Override
	public void removeEvents() {
		this.comboBoxDatasource.removeItemListener(this.datasourceListener);
		this.textFieldDatasetName.getDocument().removeDocumentListener(this.documentListener);
		this.comboBoxEncodeType.removeItemListener(this.encodeTypeListener);
		this.comboBoxImportMode.removeItemListener(this.importModeListener);
		if (null != this.comboBoxDatasetType) {
			this.comboBoxDatasetType.removeItemListener(this.datasetTypeListener);
		}
		this.checkBoxFieldIndex.removeStateChangeListener(this.fieldIndexListener);
		this.checkBoxSpatialIndex.removeStateChangeListener(this.spatialIndexListener);
	}

	private void initResources() {
		this.labelImportMode.setText(ControlsProperties.getString("string_label_lblImportType"));
		this.labelDatasource.setText(ControlsProperties.getString("String_Label_TargetDatasource"));
		this.labelDatasetName.setText(ControlsProperties.getString("String_Label_TargetDataset"));
		this.labelEncodeType.setText(CoreProperties.getString("String_Label_EncodeType"));
		this.labelDatasetType.setText(DataConversionProperties.getString("string_label_lblDatasetType"));
		this.checkBoxFieldIndex.setText(ControlsProperties.getString("string_checkbox_chckbxFieldIndex"));
		this.checkBoxSpatialIndex.setText(ControlsProperties.getString("string_checkbox_chckbxSpatialIndex"));
		this.checkBoxImportEmptyDataset.setText(ControlsProperties.getString("String_checkbox_chckbxImportEmptyDataset"));
		this.setBorder(new TitledBorder(CoreProperties.getString("String_ResultSet")));
	}

	public DatasourceComboBox getComboBoxDatasource() {
		return comboBoxDatasource;
	}

	public JTextField getTextFieldDatasetName() {
		return textFieldDatasetName;
	}

	public JComboBox getComboBoxEncodeType() {
		return comboBoxEncodeType;
	}

	public JComboBox getComboBoxImportMode() {
		return comboBoxImportMode;
	}

	public JComboBox getComboBoxDatasetType() {
		return comboBoxDatasetType;
	}

	public JCheckBox getCheckBoxFieldIndex() {
		return checkBoxFieldIndex;
	}

	public JCheckBox getCheckBoxSpatialIndex() {
		return checkBoxSpatialIndex;
	}

	public TristateCheckBox getCheckBoxImportEmptyDataset() {
		return checkBoxImportEmptyDataset;
	}
}
