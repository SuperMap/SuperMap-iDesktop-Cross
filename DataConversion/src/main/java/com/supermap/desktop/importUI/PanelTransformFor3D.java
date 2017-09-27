package com.supermap.desktop.importUI;

import com.supermap.data.Point3D;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjFileType;
import com.supermap.data.conversion.*;
import com.supermap.desktop.baseUI.PanelTransform;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.dataconversion.DataConversionProperties;
import com.supermap.desktop.iml.FileTypeLocale;
import com.supermap.desktop.properties.CommonProperties;
import com.supermap.desktop.ui.TristateCheckBox;
import com.supermap.desktop.ui.controls.*;
import com.supermap.desktop.ui.controls.prjcoordsys.JDialogPrjCoordSysSettings;
import com.supermap.desktop.utilities.FileUtilities;
import com.supermap.desktop.utilities.PrjCoordSysUtilities;
import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.*;
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
 * Created by xie on 2016/10/11.
 * 三维模型(osgb,osg,3ds,.x,.dxf,.obj,.ifc,.fbx,.dae)导入参数设置界面
 */
public class PanelTransformFor3D extends PanelTransform {
	private ArrayList<PanelImport> panelImports;
	private JLabel labelRotationType;
	private JComboBox comboBoxRotationType;//旋转模式
	private TristateCheckBox checkBoxSplitForMore;//拆分为多个子对象
	private JRadioButton radioButtonPrjSet;//投影设置
	private JButton buttonPrjSet;
	private JRadioButton radioButtonImportPrjFile;//导入投影文件
	private JFileChooserControl fileChooserControlImportPrjFile;
	private JLabel labelPositionX;
	private JTextField textFieldPositionX;//x坐标
	private JLabel labelPositionY;
	private JTextField textFieldPositionY;//y坐标
	private JLabel labelPositionZ;
	private JTextField textFieldPositionZ;//z坐标
	private JTextArea textAreaPrjInfo;//投影信息显示
	private final int POSITIONX = 0;
	private final int POSITIONY = 1;
	private final int POSITIONZ = 2;
	private final int PRJINFO = 3;
	private final int PRJFILEPATH = 4;

	private ItemListener radioListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getSource().equals(radioButtonPrjSet)) {
				buttonPrjSet.setEnabled(radioButtonPrjSet.isSelected());
				radioButtonImportPrjFile.setSelected(!radioButtonPrjSet.isSelected());
				fileChooserControlImportPrjFile.setEnabled(!radioButtonPrjSet.isEnabled());
			} else {
				buttonPrjSet.setEnabled(!radioButtonImportPrjFile.isSelected());
				radioButtonPrjSet.setSelected(!radioButtonImportPrjFile.isSelected());
				fileChooserControlImportPrjFile.setEnabled(radioButtonPrjSet.isEnabled());
			}
		}
	};
	private DocumentListener documentListener = new DocumentListener() {
		@Override
		public void insertUpdate(DocumentEvent e) {
			updatePosition(e);
		}

		private void updatePosition(DocumentEvent e) {
			String point3DX = textFieldPositionX.getText();
			String point3DY = textFieldPositionY.getText();
			String point3dZ = textFieldPositionZ.getText();
			if (null != panelImports && e.getDocument().equals(textFieldPositionX.getDocument())) {
				for (PanelImport tempPanelImport : panelImports) {
					((PanelTransformFor3D) tempPanelImport.getTransform()).getTextFieldPositionX().setText(textFieldPositionX.getText());
				}
			} else if (null != panelImports && e.getDocument().equals(textFieldPositionY.getDocument())) {
				for (PanelImport tempPanelImport : panelImports) {
					((PanelTransformFor3D) tempPanelImport.getTransform()).getTextFieldPositionY().setText(textFieldPositionY.getText());
				}
			} else if (null != panelImports && e.getDocument().equals(textFieldPositionZ.getDocument())) {
				for (PanelImport tempPanelImport : panelImports) {
					((PanelTransformFor3D) tempPanelImport.getTransform()).getTextFieldPositionZ().setText(textFieldPositionZ.getText());
				}
			} else {
				if (!StringUtilities.isNullOrEmpty(point3DX) && StringUtilities.isNumber(point3DX) &&
						!StringUtilities.isNullOrEmpty(point3DY) && StringUtilities.isNumber(point3DY) &&
						!StringUtilities.isNullOrEmpty(point3dZ) && StringUtilities.isNumber(point3dZ)) {
					Point3D newPoint3D = new Point3D(Double.parseDouble(point3DX), Double.parseDouble(point3DY), Double.parseDouble(point3dZ));

					if (importSetting instanceof ImportSettingModelOSG) {
						((ImportSettingModelOSG) importSetting).setPosition(newPoint3D);
					}
					if (importSetting instanceof ImportSettingModelX) {
						((ImportSettingModelX) importSetting).setPosition(newPoint3D);
					}
					if (importSetting instanceof ImportSettingModel3DS) {
						((ImportSettingModel3DS) importSetting).setPosition(newPoint3D);
					}
					if (importSetting instanceof ImportSettingModelDXF) {
						((ImportSettingModelDXF) importSetting).setPosition(newPoint3D);
					}
					if (importSetting instanceof ImportSettingModelFBX) {
						((ImportSettingModelFBX) importSetting).setPosition(newPoint3D);
					}
					if (importSetting instanceof ImportSettingModelFLT) {
						((ImportSettingModelFLT) importSetting).setPosition(newPoint3D);
					}
				}
			}
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			updatePosition(e);
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			updatePosition(e);
		}
	};
	private ActionListener prjsetListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			JDialogPrjCoordSysSettings dialogPrjCoordSysSettings = new JDialogPrjCoordSysSettings();
			PrjCoordSys prjCoordSys = importSetting.getSourcePrjCoordSys();
			if (prjCoordSys != null) {
				dialogPrjCoordSysSettings.setPrjCoordSys(prjCoordSys);
			}

			if (dialogPrjCoordSysSettings.showDialog() == DialogResult.OK) {
				// 修改
				PrjCoordSys newPrjCoordSys = dialogPrjCoordSysSettings.getPrjCoordSys();
				String prjCoorSysInfo = PrjCoordSysUtilities.getDescription(newPrjCoordSys);
				textAreaPrjInfo.setText(prjCoorSysInfo);
				if (null != panelImports) {
					for (PanelImport tempPanelImport : panelImports) {
						tempPanelImport.getImportInfo().getImportSetting().setTargetPrjCoordSys(newPrjCoordSys);
						((PanelTransformFor3D) tempPanelImport.getTransform()).getTextAreaPrjInfo().setText(prjCoorSysInfo);
					}
				} else {
					importSetting.setTargetPrjCoordSys(newPrjCoordSys);
				}

			}
		}
	};
	private FileChooserPathChangedListener importPrjFileListener = new FileChooserPathChangedListener() {


		@Override
		public void pathChanged() {
			String filePath = fileChooserControlImportPrjFile.getPath();
			// 设置投影信息
			if (!StringUtilities.isNullOrEmpty(filePath)) {
				setPrjCoordSys(filePath);
			}
		}
	};

	private void setPrjCoordSys(String filePath) {
		PrjCoordSys newPrjCoorSys = new PrjCoordSys();
		String fileType = FileUtilities.getFileType(filePath);
		boolean isPrjFile;
		if (fileType.equalsIgnoreCase(FileTypeLocale.PRJ_STRING)) {
			isPrjFile = newPrjCoorSys.fromFile(filePath, PrjFileType.ESRI);
		} else {
			isPrjFile = newPrjCoorSys.fromFile(filePath, PrjFileType.SUPERMAP);
		}
		if (isPrjFile) {
			String prjCoorSysInfo = PrjCoordSysUtilities.getDescription(newPrjCoorSys);
			fileChooserControlImportPrjFile.setPath(filePath);
			textAreaPrjInfo.setText(prjCoorSysInfo);
			if (null != panelImports) {
				for (PanelImport panelImport : panelImports) {
					panelImport.getImportInfo().getImportSetting().setTargetPrjCoordSys(newPrjCoorSys);
					((PanelTransformFor3D) panelImport.getTransform()).getFileChooserControlImportPrjFile().setPath(filePath);
					((PanelTransformFor3D) panelImport.getTransform()).getTextAreaPrjInfo().setText(prjCoorSysInfo);
				}
			} else {
				importSetting.setTargetPrjCoordSys(newPrjCoorSys);
			}
		}
	}


	public PanelTransformFor3D(ImportSetting importSetting) {
		super(importSetting);
		registEvents();
	}

	public PanelTransformFor3D(ArrayList<PanelImport> panelImports, int layoutType) {
		super(panelImports, layoutType);
		this.panelImports = panelImports;
		initLayerout();
		registEvents();
	}

	@Override
	public void initComponents() {
		this.labelRotationType = new JLabel();
		this.comboBoxRotationType = new JComboBox();
		this.checkBoxSplitForMore = new TristateCheckBox();
		this.checkBoxSplitForMore.setSelected(false);
		this.radioButtonPrjSet = new JRadioButton();
		this.buttonPrjSet = new JButton();
		this.radioButtonImportPrjFile = new JRadioButton();
		this.fileChooserControlImportPrjFile = new JFileChooserControl();
		if (!SmFileChoose.isModuleExist("ImportPrjFile")) {
			String fileFilters = SmFileChoose.buildFileFilters(
					SmFileChoose.createFileFilter(ControlsProperties.getString("String_ImportPrjFiles"), "prj", "xml"),
					SmFileChoose.createFileFilter(ControlsProperties.getString("String_ImportPrjFileShape"), "prj"),
					SmFileChoose.createFileFilter(ControlsProperties.getString("String_ImportPrjFileXml"), "xml"));
			SmFileChoose.addNewNode(fileFilters, CommonProperties.getString("String_DefaultFilePath"),
					ControlsProperties.getString("string_importPrjFile"), "ImportPrjFile", "OpenMany");
		}
		SmFileChoose fileChooser = new SmFileChoose("ImportPrjFile");
		this.fileChooserControlImportPrjFile.setFileChooser(fileChooser);
		this.labelPositionX = new JLabel();
		this.textFieldPositionX = new JTextField();
		this.labelPositionY = new JLabel();
		this.textFieldPositionY = new JTextField();
		this.labelPositionZ = new JLabel();
		this.textFieldPositionZ = new JTextField();
		this.textAreaPrjInfo = new JTextArea();
		this.textAreaPrjInfo.setEditable(false);
		initTextFiledPosition();
	}

	@Override
	public void setComponentName() {
		super.setComponentName();
		ComponentUIUtilities.setName(this.labelRotationType, "PanelTransformFor3D_labelRotationType");
		ComponentUIUtilities.setName(this.comboBoxRotationType, "PanelTransformFor3D_comboBoxRotationType");
		ComponentUIUtilities.setName(this.checkBoxSplitForMore, "PanelTransformFor3D_checkBoxSplitForMore");
		ComponentUIUtilities.setName(this.radioButtonPrjSet, "PanelTransformFor3D_radioButtonPrjSet");
		ComponentUIUtilities.setName(this.buttonPrjSet, "PanelTransformFor3D_buttonPrjSet");
		ComponentUIUtilities.setName(this.radioButtonImportPrjFile, "PanelTransformFor3D_radioButtonImportPrjFile");
		ComponentUIUtilities.setName(this.fileChooserControlImportPrjFile, "PanelTransformFor3D_radioButtonImportPrjFile");
		ComponentUIUtilities.setName(this.labelPositionX, "PanelTransformFor3D_labelPositionX");
		ComponentUIUtilities.setName(this.textFieldPositionX, "PanelTransformFor3D_textFieldPositionX");
		ComponentUIUtilities.setName(this.labelPositionY, "PanelTransformFor3D_labelPositionY");
		ComponentUIUtilities.setName(this.textFieldPositionY, "PanelTransformFor3D_textFieldPositionY");
		ComponentUIUtilities.setName(this.labelPositionZ, "PanelTransformFor3D_labelPositionZ");
		ComponentUIUtilities.setName(this.textFieldPositionZ, "PanelTransformFor3D_textFieldPositionZ");
		ComponentUIUtilities.setName(this.textAreaPrjInfo, "PanelTransformFor3D_textAreaPrjInfo");
	}

	private void initTextFiledPosition() {
		if (importSetting instanceof ImportSettingModelOSG) {
			Point3D point = ((ImportSettingModelOSG) importSetting).getPosition();
			initTextFileds(point);
		} else if (importSetting instanceof ImportSettingModelX) {
			Point3D point = ((ImportSettingModelX) importSetting).getPosition();
			initTextFileds(point);
		} else if (importSetting instanceof ImportSettingModel3DS) {
			Point3D point = ((ImportSettingModel3DS) importSetting).getPosition();
			initTextFileds(point);
		} else if (importSetting instanceof ImportSettingModelDXF) {
			Point3D point = ((ImportSettingModelDXF) importSetting).getPosition();
			initTextFileds(point);
		} else if (importSetting instanceof ImportSettingModelFBX) {
			Point3D point = ((ImportSettingModelFBX) importSetting).getPosition();
			initTextFileds(point);
		} else if (importSetting instanceof ImportSettingModelFLT) {
			Point3D point = ((ImportSettingModelFLT) importSetting).getPosition();
			initTextFileds(point);
		}
	}

	private void initTextFileds(Point3D point) {
		this.textFieldPositionX.setText(String.valueOf(point.getX()));
		this.textFieldPositionY.setText(String.valueOf(point.getY()));
		this.textFieldPositionZ.setText(String.valueOf(point.getZ()));
	}

	@Override
	public void initLayerout() {
		JScrollPane scrollPane = new JScrollPane();
		this.setLayout(new GridBagLayout());
		this.add(this.labelRotationType, new GridBagConstraintsHelper(0, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 5, 5, 10).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		this.add(this.comboBoxRotationType, new GridBagConstraintsHelper(2, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 30).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
		this.add(this.radioButtonPrjSet, new GridBagConstraintsHelper(4, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 10).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		this.add(this.buttonPrjSet, new GridBagConstraintsHelper(6, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 10).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));

		this.add(this.checkBoxSplitForMore, new GridBagConstraintsHelper(0, 1, 4, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 20).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		this.add(this.radioButtonImportPrjFile, new GridBagConstraintsHelper(4, 1, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 10).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		this.add(this.fileChooserControlImportPrjFile, new GridBagConstraintsHelper(6, 1, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 10).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));

		JPanel panelModel = new JPanel();
		panelModel.setLayout(new GridBagLayout());
		panelModel.setBorder(new TitledBorder(DataConversionProperties.getString("string_modelPoint")));
		panelModel.add(this.labelPositionX, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 5, 5, 20).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		panelModel.add(this.textFieldPositionX, new GridBagConstraintsHelper(1, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 5, 10).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
		panelModel.add(this.labelPositionY, new GridBagConstraintsHelper(0, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 20).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		panelModel.add(this.textFieldPositionY, new GridBagConstraintsHelper(1, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 10).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));
		panelModel.add(this.labelPositionZ, new GridBagConstraintsHelper(0, 2, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 5, 5, 20).setFill(GridBagConstraints.NONE).setWeight(0, 0));
		panelModel.add(this.textFieldPositionZ, new GridBagConstraintsHelper(1, 2, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 5, 10).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0));

		this.add(panelModel, new GridBagConstraintsHelper(0, 2, 4, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 0, 20).setFill(GridBagConstraints.BOTH).setWeight(0, 0));
		this.add(scrollPane, new GridBagConstraintsHelper(4, 2, 4, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 0, 10).setFill(GridBagConstraints.BOTH).setWeight(0, 0));
		scrollPane.setViewportView(this.textAreaPrjInfo);
		this.comboBoxRotationType.setEnabled(false);
		this.radioButtonPrjSet.setSelected(true);
		this.fileChooserControlImportPrjFile.setEnabled(false);
		setSplitForMore();
		setImportPrjFilePath();
		setPositionInfo();
	}

	private void setPositionInfo() {
		if (null != panelImports) {
			this.textFieldPositionX.setText(getText(POSITIONX));
			this.textFieldPositionY.setText(getText(POSITIONY));
			this.textFieldPositionZ.setText(getText(POSITIONZ));
			this.textAreaPrjInfo.setText(getText(PRJINFO));
		}
	}

	private void setSplitForMore() {
		if (null != panelImports) {
			this.checkBoxSplitForMore.setSelectedEx(externalDataSelectAll());
		}
	}

	private Boolean externalDataSelectAll() {
		Boolean result = null;
		int selectCount = 0;
		int unSelectCount = 0;
		for (PanelImport tempPanel : panelImports) {
			boolean select = ((PanelTransformFor3D) tempPanel.getTransform()).getCheckBoxSplitForMore().isSelected();
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

	private void setImportPrjFilePath() {
		if (null != panelImports) {
			this.fileChooserControlImportPrjFile.setPath(getText(PRJFILEPATH));
		}
	}

	public String getText(int type) {
		String result = "";
		String temp = getInfo(panelImports.get(0), type);
		boolean isSame = true;
		for (PanelImport tempPanel : panelImports) {
			String tempObject = getInfo(tempPanel, type);
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

	@Override
	public void registEvents() {
		removeEvents();
		this.radioButtonPrjSet.addItemListener(this.radioListener);
		this.radioButtonImportPrjFile.addItemListener(this.radioListener);
		this.textFieldPositionX.getDocument().addDocumentListener(this.documentListener);
		this.textFieldPositionY.getDocument().addDocumentListener(this.documentListener);
		this.textFieldPositionZ.getDocument().addDocumentListener(this.documentListener);
		this.buttonPrjSet.addActionListener(this.prjsetListener);
		this.fileChooserControlImportPrjFile.addFileChangedListener(this.importPrjFileListener);
//        this.fileChooserControlImportPrjFile.getButton().addActionListener(this.importPrjFileListener);
	}

	@Override
	public void removeEvents() {
		this.radioButtonPrjSet.removeItemListener(this.radioListener);
		this.radioButtonImportPrjFile.removeItemListener(this.radioListener);
		this.textFieldPositionX.getDocument().removeDocumentListener(this.documentListener);
		this.textFieldPositionY.getDocument().removeDocumentListener(this.documentListener);
		this.textFieldPositionZ.getDocument().removeDocumentListener(this.documentListener);
		this.buttonPrjSet.removeActionListener(this.prjsetListener);
		this.fileChooserControlImportPrjFile.addFileChangedListener(this.importPrjFileListener);
	}

	@Override
	public void initResources() {
		this.setBorder(new TitledBorder(DataConversionProperties.getString("string_border_panelTransform")));
		this.labelRotationType.setText(DataConversionProperties.getString("string_rotationType"));
		this.labelPositionX.setText(DataConversionProperties.getString("string_label_lblx"));
		this.labelPositionY.setText(DataConversionProperties.getString("string_label_lbly"));
		this.labelPositionZ.setText(DataConversionProperties.getString("string_label_lblz"));
		this.checkBoxSplitForMore.setText(DataConversionProperties.getString("string_splitForMore"));
		this.radioButtonImportPrjFile.setText(ControlsProperties.getString("String_ImportPrjFile"));
		this.radioButtonPrjSet.setText(ControlsProperties.getString("String_SetProjection_Caption"));
		this.buttonPrjSet.setText(ControlsProperties.getString("String_Button_Setting"));
	}

	public JTextField getTextFieldPositionX() {
		return textFieldPositionX;
	}

	public JTextField getTextFieldPositionY() {
		return textFieldPositionY;
	}

	public JTextField getTextFieldPositionZ() {
		return textFieldPositionZ;
	}

	public JTextArea getTextAreaPrjInfo() {
		return textAreaPrjInfo;
	}

	public TristateCheckBox getCheckBoxSplitForMore() {
		return checkBoxSplitForMore;
	}

	public JFileChooserControl getFileChooserControlImportPrjFile() {
		return fileChooserControlImportPrjFile;
	}

	public String getInfo(PanelImport tempPanel, int type) {
		String result = "";
		switch (type) {
			case POSITIONX:
				result = ((PanelTransformFor3D) tempPanel.getTransform()).getTextFieldPositionX().getText();
				break;
			case POSITIONY:
				result = ((PanelTransformFor3D) tempPanel.getTransform()).getTextFieldPositionY().getText();
				break;
			case POSITIONZ:
				result = ((PanelTransformFor3D) tempPanel.getTransform()).getTextFieldPositionZ().getText();
				break;
			case PRJINFO:
				result = ((PanelTransformFor3D) tempPanel.getTransform()).getTextAreaPrjInfo().getText();
				break;
			case PRJFILEPATH:
				result = ((PanelTransformFor3D) tempPanel.getTransform()).getFileChooserControlImportPrjFile().getPath();
				break;
			default:
				break;
		}
		return result;
	}
}
