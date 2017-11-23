package com.supermap.desktop.ui.controls.prjcoordsys;

import com.supermap.data.*;
import com.supermap.data.Enum;
import com.supermap.desktop.Interface.ISmTextFieldLegit;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.ui.controls.SmDialog;
import com.supermap.desktop.ui.controls.TextFields.SmTextFieldLegit;
import com.supermap.desktop.ui.controls.button.SmButton;
import com.supermap.desktop.ui.controls.comboBox.JSearchComboBox;
import com.supermap.desktop.ui.controls.comboBox.SearchItemValueGetter;
import com.supermap.desktop.utilities.EnumComparator;
import com.supermap.desktop.utilities.PrjCoordSysTypeUtilities;
import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * @author XiaJT
 */
public class JDialogUserDefinePrjProjection extends SmDialog {
	private final ActionListener buttonOkListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			dialogResult = DialogResult.OK;
			dispose();
		}
	};
	private final ActionListener buttonCancelListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			dialogResult = DialogResult.CANCEL;
			dispose();
		}
	};
	private JTabbedPane tabbedPane = new JTabbedPane();
	private JLabel labelName = new JLabel();
	private final static String DEFAULT_NAME = "New_Projected_Coordinate_System";
	//private JSearchComboBox<PrjCoordSysType> comboBoxName = new JSearchComboBox<>();

	// 投影坐标系
	private JPanel panelPrjCoordSys = new JPanel();
	private JLabel labelCoordType = new JLabel();
	private JSearchComboBox<ProjectionType> comboBoxCoordType = new JSearchComboBox<>();

	private JLabel labelCoordSysUnit = new JLabel();
	private JSearchComboBox<Unit> comboBoxCoordSysUnit = new JSearchComboBox<>();
	private Unit[] units = new Unit[]{Unit.KILOMETER, Unit.METER, Unit.DECIMETER, Unit.CENTIMETER, Unit.MILIMETER, Unit.MILE, Unit.YARD, Unit.FOOT, Unit.INCH};
	private DecimalFormat df = new DecimalFormat("0.######################");

	// 投影参数
	private JPanel panelPrjCoordSysParameters = new JPanel();

	// 按钮
	private JPanel panelButtons = new JPanel();
	private SmButton buttonOK = new SmButton();
	private SmButton buttonCancel = new SmButton();

	private JPanelGeoCoordSys panelGeoCoordSys = new JPanelGeoCoordSys();

	private JLabel labelParameterFormat = new JLabel();
	private ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton radioButtonAngle = new JRadioButton();
	private JRadioButton radioButtonAMS = new JRadioButton();

	// 中央经线
	private JLabel labelCentralMeridian = new JLabel();
	private JPanelFormat panelCentralMeridian = new JPanelFormat();
	// 水平偏移
	private JLabel labelFalseEasting = new JLabel();
	private SmTextFieldLegit textFieldFalseEasting = new SmTextFieldLegit();

	// 原点纬线
	private JLabel labelCentralParallel = new JLabel();
	private JPanelFormat panelCentralParallel = new JPanelFormat();
	// 垂直偏移
	private JLabel labelFalseNorthing = new JLabel();
	private SmTextFieldLegit textFieldFalseNorthing = new SmTextFieldLegit();

	// 比例因子
	private JLabel labelScaleFactor = new JLabel();
	private SmTextFieldLegit textFieldScaleFactor = new SmTextFieldLegit();

	// 第一标准纬线
	private JLabel labelStandardParallel1 = new JLabel();
	private JPanelFormat panelStandardParallel1 = new JPanelFormat();
	// 第二标准纬线
	private JLabel labelStandardParallel2 = new JLabel();
	private JPanelFormat panelStandardParallel2 = new JPanelFormat();

	// 第一点的经度
	private JLabel labelFirstPointLongitude = new JLabel();
	private JPanelFormat panelFirstPointLongitude = new JPanelFormat();

	// 第2点的经度
	private JLabel labelSecondPointLongitude = new JLabel();
	private JPanelFormat panelSecondPointLongitude = new JPanelFormat();

	// 方位角
	private JLabel labelAzimuth = new JLabel();
	private JPanelFormat panelAzimuth = new JPanelFormat();
	private final ItemListener radioAMSListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			boolean mode = JPanelFormat.angle;
			if (e.getStateChange() == ItemEvent.SELECTED) {
				mode = JPanelFormat.ANGLE_M_S;
			}
			panelCentralMeridian.setMode(mode);
			panelCentralParallel.setMode(mode);
			panelStandardParallel1.setMode(mode);
			panelStandardParallel2.setMode(mode);
			panelFirstPointLongitude.setMode(mode);
			panelSecondPointLongitude.setMode(mode);
			panelAzimuth.setMode(mode);
			repaint();
		}
	};

	private PrjCoordSys prjCoordSys = new PrjCoordSys();
	private JTextField textFieldName = new JTextField();

	private ISmTextFieldLegit fieldLegit;
	private Boolean lockUnit = false;
	private ItemListener comboBoxUnitListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (lockUnit) {
				return;
			}
			if (e.getStateChange() == ItemEvent.SELECTED) {
				if (prjCoordSys.getType() != PrjCoordSysType.PCS_USER_DEFINED) {
					prjCoordSys.setType(PrjCoordSysType.PCS_USER_DEFINED);
					if (!StringUtilities.isNullOrEmpty(textFieldName.getText())) {
						prjCoordSys.setName(textFieldName.getText());
					} else {
						prjCoordSys.setName(DEFAULT_NAME);
					}
				}
				prjCoordSys.setCoordUnit((Unit) comboBoxCoordSysUnit.getSelectedItem());
			}
		}
	};
	//private ItemListener comboBoxNameListener = new ItemListener() {
	//	@Override
	//	public void itemStateChanged(ItemEvent e) {
	//		if (lock) {
	//			return;
	//		}
	//		if (e.getStateChange() == ItemEvent.SELECTED) {
	//			Object selectedItem = comboBoxName.getSelectedItem();
	//			if (selectedItem instanceof PrjCoordSysType && selectedItem != PrjCoordSysType.PCS_USER_DEFINED) {
	//				prjCoordSys.setType((PrjCoordSysType) selectedItem);
	//				lock = true;
	//				prjCoordSys.setType(PrjCoordSysType.PCS_USER_DEFINED);
	//				prjCoordSys.setName(PrjCoordSysTypeUtilities.getDescribe(((PrjCoordSysType) selectedItem).name()));
	//				panelGeoCoordSys.setGeoCoordSys(prjCoordSys.getGeoCoordSys());
	//				comboBoxName.setSelectedItem(PrjCoordSysTypeUtilities.getDescribe(((PrjCoordSysType) selectedItem).name()));
	//				resetProjectionTypeValues();
	//				lock = false;
	//			} else {
	//				if (StringUtilities.isNullOrEmptyString(selectedItem)) {
	//					return;
	//				}
	//				if (selectedItem instanceof String) {
	//					prjCoordSys.setType(PrjCoordSysType.PCS_USER_DEFINED);
	//					prjCoordSys.setName((String) selectedItem);
	//				}
	//				//prjCoordSys.setName(selectedItem instanceof String ? (String) selectedItem : ((PrjCoordSysType) selectedItem).name());
	//			}
	//		}
	//	}
	//};

	private boolean lock = false;
	private final DocumentListener textFieldNameListener = new DocumentListener() {
		@Override
		public void insertUpdate(DocumentEvent e) {
			textFieldNameChanged(e);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			textFieldNameChanged(e);
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			textFieldNameChanged(e);
		}
	};
	private Boolean lockType = false;
	private ItemListener comboBoxCoordTypeListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (lockType) {
				return;
			}
			if (e.getStateChange() == ItemEvent.SELECTED) {
				Object selectedItem = comboBoxCoordType.getSelectedItem();
				if (selectedItem instanceof ProjectionType) {
					// 当改变
					if (prjCoordSys.getType() != PrjCoordSysType.PCS_USER_DEFINED) {
						prjCoordSys.setType(PrjCoordSysType.PCS_USER_DEFINED);
						if (!StringUtilities.isNullOrEmpty(textFieldName.getText())) {
							prjCoordSys.setName(textFieldName.getText());
						} else {
							prjCoordSys.setName(DEFAULT_NAME);
						}
					}
					prjCoordSys.getProjection().setType((ProjectionType) selectedItem);
					lockType = true;
					comboBoxCoordType.setSelectedItem(PrjCoordSysTypeUtilities.getDescribe(((ProjectionType) selectedItem).name()));
					lockType = false;

				}
			}
		}
	};

	public JDialogUserDefinePrjProjection() {
		super();
		this.setTitle(ControlsProperties.getString("String_UserDefined_PrjCoordSys"));
		this.componentList.add(this.buttonOK);
		this.componentList.add(this.buttonCancel);
		init();
	}

	private void textFieldNameChanged(DocumentEvent e) {
		if (this.lock) {
			return;
		}
		if (StringUtilities.isNullOrEmptyString(this.textFieldName.getText())) {
			return;
		}
		this.prjCoordSys.setName(this.textFieldName.getText());
	}

	private void init() {
		initComponents();
		initLayout();
		initResources();
		addListeners();
		initComponentState();
	}

	private void initComponents() {

		setSize(550, 460);
		setLocationRelativeTo(null);
		this.textFieldScaleFactor.setPreferredSize(new Dimension(50, 23));
		this.textFieldFalseNorthing.setPreferredSize(new Dimension(50, 23));
		this.textFieldFalseEasting.setPreferredSize(new Dimension(50, 23));
		this.buttonGroup.add(this.radioButtonAMS);
		this.buttonGroup.add(this.radioButtonAngle);

		// region 名称
		//SearchItemValueGetter<Enum> searchItemValueGetter = PrjCoordSysSettingsUtilties.getSearchItemValueGetter();
		//comboBoxName.setSearchItemValueGetter(searchItemValueGetter);
		//Enum[] enums = Enum.getEnums(PrjCoordSysType.class);
		//Arrays.sort(enums, 0, enums.length, new EnumComparator());
		//for (Enum anEnum : enums) {
		//	if (anEnum instanceof PrjCoordSysType && anEnum != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE && anEnum != PrjCoordSysType.PCS_NON_EARTH && anEnum != PrjCoordSysType.PCS_USER_DEFINED) {
		//		comboBoxName.addItem((PrjCoordSysType) anEnum);
		//	}
		//}
		//comboBoxName.setRenderer(new MyEnumCellRender(comboBoxName));
		// endregion

		// region 投影方式
		SearchItemValueGetter<Enum> searchItemValueGetter = PrjCoordSysSettingsUtilties.getSearchItemValueGetter();
		this.comboBoxCoordType.setSearchItemValueGetter(searchItemValueGetter);
		Enum[] enums1 = Enum.getEnums(ProjectionType.class);
		Arrays.sort(enums1, 0, enums1.length, new EnumComparator());
		for (Enum anEnum : enums1) {
			if (anEnum instanceof ProjectionType) {
				this.comboBoxCoordType.addItem((ProjectionType) anEnum);
			}
		}
		this.comboBoxCoordType.setRenderer(new MyEnumCellRender(this.comboBoxCoordType));
		this.comboBoxCoordType.setSelectedItem(PrjCoordSysTypeUtilities.getDescribe(ProjectionType.PRJ_NONPROJECTION.name()));
		// endregion

		// region 单位
		this.comboBoxCoordSysUnit.setSearchItemValueGetter(searchItemValueGetter);
		for (Unit unit : this.units) {
			this.comboBoxCoordSysUnit.addItem(unit);
		}
		this.comboBoxCoordSysUnit.setMaximumRowCount(this.units.length);
		this.comboBoxCoordSysUnit.setSelectedItem(Unit.METER);
		// endregion

		// region 垂直偏移\水平偏移\比例因子
		this.fieldLegit = new ISmTextFieldLegit() {
			@Override
			public boolean isTextFieldValueLegit(String textFieldValue) {
				if (StringUtilities.isNullOrEmpty(textFieldValue) || textFieldValue.contains("d")) {
					return false;
				}
				try {
					Double aDouble = Double.valueOf(textFieldValue);
					if (aDouble < -100000000000d || aDouble > 100000000000d) {
						return false;
					}
				} catch (Exception e) {
					return false;
				}
				return true;
			}

			@Override
			public String getLegitValue(String currentValue, String backUpValue) {
				return backUpValue;
			}
		};
		this.textFieldFalseNorthing.setSmTextFieldLegit(this.fieldLegit);
		this.textFieldFalseEasting.setSmTextFieldLegit(this.fieldLegit);
		this.textFieldScaleFactor.setSmTextFieldLegit(new ISmTextFieldLegit() {
			@Override
			public boolean isTextFieldValueLegit(String textFieldValue) {
				if (StringUtilities.isNullOrEmpty(textFieldValue) || textFieldValue.contains("d")) {
					return false;
				}
				try {
					Double aDouble = Double.valueOf(textFieldValue);
					if (aDouble < 0d || aDouble > 1d) {
						return false;
					}
				} catch (Exception e) {
					return false;
				}
				return true;
			}

			@Override
			public String getLegitValue(String currentValue, String backUpValue) {
				return backUpValue;
			}
		});
		this.textFieldFalseEasting.setText("0");
		this.textFieldFalseNorthing.setText("0");
		this.textFieldScaleFactor.setText("0");
		// endregion

		this.prjCoordSys.setType(PrjCoordSysType.PCS_USER_DEFINED);
		this.prjCoordSys.setName(DEFAULT_NAME);

		// 设置控件不可编辑
		setComponentEditable(false);
	}

	// region 初始化布局
	private void initLayout() {
		initTabbedPane();
		initPanelButtons();
		this.setLayout(new GridBagLayout());
		this.add(this.labelName, new GridBagConstraintsHelper(0, 0, 1, 1).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.WEST).setInsets(10, 5, 0, 0).setWeight(0, 0));
		this.add(this.textFieldName, new GridBagConstraintsHelper(1, 0, 1, 1).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.CENTER).setInsets(10, 5, 0, 10).setWeight(1, 0));
		this.add(this.tabbedPane, new GridBagConstraintsHelper(0, 1, 2, 1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setInsets(5, 10, 0, 10).setWeight(1, 1));
		this.add(this.panelButtons, new GridBagConstraintsHelper(0, 2, 2, 1).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.CENTER).setInsets(5, 10, 10, 10).setWeight(1, 0));
	}

	private void initTabbedPane() {
		initPanelPrjcoordSys();
		initPanelPrjCoordSysParameters();
		this.tabbedPane.add(ControlsProperties.getString("String_GeoCoordSys"), this.panelPrjCoordSys);
		this.tabbedPane.add(ControlsProperties.getString("String_PrjCoordSysParameters"), this.panelPrjCoordSysParameters);
	}

	/**
	 * 初始化投影坐标系系统
	 */
	private void initPanelPrjcoordSys() {
		//panelGeoCoordSys.setBorder(BorderFactory.createTitledBorder(ControlsProperties.getString("String_GeoCoordSys")));
		this.panelPrjCoordSys.setLayout(new GridBagLayout());
		this.panelPrjCoordSys.add(this.panelGeoCoordSys, new GridBagConstraintsHelper(0, 0, 1, 1).setWeight(1, 0).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.NORTH).setInsets(5, 10, 0, 10));
		this.panelPrjCoordSys.add(new JPanel(), new GridBagConstraintsHelper(0, 1, 1, 1).setWeight(1, 1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setInsets(5, 10, 10, 10));
	}

	private void initPanelPrjCoordSysParameters() {
		this.panelPrjCoordSysParameters.setLayout(new GridBagLayout());

		this.panelPrjCoordSysParameters.add(this.labelCoordType, new GridBagConstraintsHelper(0, 0, 1, 1).setWeight(0, 0).setAnchor(GridBagConstraints.WEST).setInsets(6, 15, 0, 0));
		this.panelPrjCoordSysParameters.add(this.comboBoxCoordType, new GridBagConstraintsHelper(1, 0, 1, 1).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.CENTER).setInsets(6, 5, 0, 6));
		this.panelPrjCoordSysParameters.add(this.labelCoordSysUnit, new GridBagConstraintsHelper(2, 0, 1, 1).setWeight(0, 0).setAnchor(GridBagConstraints.WEST).setInsets(6, 5, 0, 0));
		this.panelPrjCoordSysParameters.add(this.comboBoxCoordSysUnit, new GridBagConstraintsHelper(3, 0, 1, 1).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.CENTER).setInsets(6, 5, 0, 12));

		this.panelPrjCoordSysParameters.add(this.labelCentralMeridian, new GridBagConstraintsHelper(0, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 15, 0, 0));
		this.panelPrjCoordSysParameters.add(this.panelCentralMeridian, new GridBagConstraintsHelper(1, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 5, 0, 0).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL));
		this.panelPrjCoordSysParameters.add(this.labelFalseEasting, new GridBagConstraintsHelper(2, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 5, 0, 0));
		this.panelPrjCoordSysParameters.add(this.textFieldFalseEasting, new GridBagConstraintsHelper(3, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 5, 0, 12).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL));

		this.panelPrjCoordSysParameters.add(this.labelCentralParallel, new GridBagConstraintsHelper(0, 2, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 15, 0, 0));
		this.panelPrjCoordSysParameters.add(this.panelCentralParallel, new GridBagConstraintsHelper(1, 2, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 5, 0, 0).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL));
		this.panelPrjCoordSysParameters.add(this.labelFalseNorthing, new GridBagConstraintsHelper(2, 2, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 5, 0, 0));
		this.panelPrjCoordSysParameters.add(this.textFieldFalseNorthing, new GridBagConstraintsHelper(3, 2, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 5, 0, 12).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL));

		this.panelPrjCoordSysParameters.add(this.labelStandardParallel1, new GridBagConstraintsHelper(0, 3, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 15, 0, 0));
		this.panelPrjCoordSysParameters.add(this.panelStandardParallel1, new GridBagConstraintsHelper(1, 3, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 5, 0, 0).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL));
		this.panelPrjCoordSysParameters.add(this.labelScaleFactor, new GridBagConstraintsHelper(2, 3, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 5, 0, 0));
		this.panelPrjCoordSysParameters.add(this.textFieldScaleFactor, new GridBagConstraintsHelper(3, 3, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 5, 0, 12).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL));

		this.panelPrjCoordSysParameters.add(this.labelStandardParallel2, new GridBagConstraintsHelper(0, 4, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 15, 0, 0));
		this.panelPrjCoordSysParameters.add(this.panelStandardParallel2, new GridBagConstraintsHelper(1, 4, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 5, 0, 0).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL));
		this.panelPrjCoordSysParameters.add(new Panel(), new GridBagConstraintsHelper(3, 4, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 5, 0, 10).setWeight(1, 0));

		this.panelPrjCoordSysParameters.add(this.labelFirstPointLongitude, new GridBagConstraintsHelper(0, 5, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 15, 0, 0));
		this.panelPrjCoordSysParameters.add(this.panelFirstPointLongitude, new GridBagConstraintsHelper(1, 5, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 5, 0, 0).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL));
		this.panelPrjCoordSysParameters.add(new Panel(), new GridBagConstraintsHelper(3, 5, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 5, 0, 10).setWeight(1, 0));

		this.panelPrjCoordSysParameters.add(this.labelSecondPointLongitude, new GridBagConstraintsHelper(0, 6, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 15, 0, 0));
		this.panelPrjCoordSysParameters.add(this.panelSecondPointLongitude, new GridBagConstraintsHelper(1, 6, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 5, 0, 0).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL));
		this.panelPrjCoordSysParameters.add(new Panel(), new GridBagConstraintsHelper(3, 6, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 5, 0, 10).setWeight(1, 0));

		this.panelPrjCoordSysParameters.add(this.labelAzimuth, new GridBagConstraintsHelper(0, 7, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 15, 0, 0));
		this.panelPrjCoordSysParameters.add(this.panelAzimuth, new GridBagConstraintsHelper(1, 7, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 5, 0, 0).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL));
		this.panelPrjCoordSysParameters.add(new Panel(), new GridBagConstraintsHelper(3, 7, 2, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 5, 0, 10).setWeight(1, 0));

		this.panelPrjCoordSysParameters.add(this.labelParameterFormat, new GridBagConstraintsHelper(0, 8, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 15, 0, 0));
		this.panelPrjCoordSysParameters.add(this.radioButtonAngle, new GridBagConstraintsHelper(1, 8, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 5, 0, 0).setWeight(1, 0));
		this.panelPrjCoordSysParameters.add(this.radioButtonAMS, new GridBagConstraintsHelper(2, 8, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 5, 0, 0));
		this.panelPrjCoordSysParameters.add(new Panel(), new GridBagConstraintsHelper(3, 8, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(6, 5, 0, 10).setWeight(1, 0));

		this.panelPrjCoordSysParameters.add(new JPanel(), new GridBagConstraintsHelper(0, 9, 4, 1).setAnchor(GridBagConstraints.CENTER).setFill(GridBagConstraints.BOTH).setWeight(2, 1));


	}

	private void initPanelButtons() {
		this.panelButtons.setLayout(new GridBagLayout());
		this.panelButtons.add(this.buttonOK, new GridBagConstraintsHelper(0, 0, 1, 1).setWeight(1, 1).setAnchor(GridBagConstraints.EAST).setInsets(0, 0, 0, 5));
		this.panelButtons.add(this.buttonCancel, new GridBagConstraintsHelper(1, 0, 1, 1).setWeight(0, 1).setAnchor(GridBagConstraints.EAST));
	}

	// endregion

	private void initResources() {
		this.labelName.setText(ControlsProperties.getString("String_Label_Name"));
		this.labelCoordType.setText(ControlsProperties.getString("String_ProjectionType"));
		this.labelCoordSysUnit.setText(ControlsProperties.getString("String_CoorSysUnit"));

		this.labelParameterFormat.setText(ControlsProperties.getString("String_ParameterFormat"));
		this.radioButtonAngle.setText(ControlsProperties.getString("String_angle"));
		this.radioButtonAMS.setText(ControlsProperties.getString("String_DMSFormat"));
		this.labelCentralMeridian.setText(ControlsProperties.getString("String_Label_CentralMeridian"));
		this.labelFalseEasting.setText(ControlsProperties.getString("String_FalseEasting"));
		this.labelCentralParallel.setText(ControlsProperties.getString("String_Label_CentralParallel"));
		this.labelFalseNorthing.setText(ControlsProperties.getString("String_FalseNorthing"));

		this.labelScaleFactor.setText(ControlsProperties.getString("String_ScaleFactor"));
		this.labelStandardParallel1.setText(ControlsProperties.getString("String_StandardParallel1"));
		this.labelStandardParallel2.setText(ControlsProperties.getString("String_StandardParallel2"));
		this.labelFirstPointLongitude.setText(ControlsProperties.getString("String_FirstPointLongitude"));
		this.labelSecondPointLongitude.setText(ControlsProperties.getString("String_SecondPointLongitude"));
		this.labelAzimuth.setText(ControlsProperties.getString("String_Label_Azimuth"));

		this.buttonOK.setText(CoreProperties.getString(CoreProperties.OK));
		this.buttonCancel.setText(CoreProperties.getString(CoreProperties.Cancel));
	}

	private void addListeners() {
		this.buttonOK.addActionListener(this.buttonOkListener);
		this.buttonCancel.addActionListener(this.buttonCancelListener);
		this.radioButtonAMS.addItemListener(this.radioAMSListener);
		this.textFieldName.getDocument().addDocumentListener(this.textFieldNameListener);
		//comboBoxName.addItemListener(comboBoxNameListener);
		this.comboBoxCoordType.addItemListener(this.comboBoxCoordTypeListener);
		this.comboBoxCoordSysUnit.addItemListener(this.comboBoxUnitListener);
	}

	private void removeListeners() {
		this.buttonOK.removeActionListener(this.buttonOkListener);
		this.buttonCancel.removeActionListener(this.buttonCancelListener);
		this.radioButtonAMS.removeItemListener(this.radioAMSListener);
		this.textFieldName.getDocument().addDocumentListener(this.textFieldNameListener);
		//comboBoxName.removeItemListener(comboBoxNameListener);
		this.comboBoxCoordType.removeItemListener(this.comboBoxCoordTypeListener);
		this.comboBoxCoordSysUnit.removeItemListener(this.comboBoxUnitListener);
	}

	private void resetProjectionTypeValues() {
		this.panelCentralMeridian.setValue(this.prjCoordSys.getPrjParameter().getCentralMeridian());
		this.panelCentralParallel.setValue(this.prjCoordSys.getPrjParameter().getCentralParallel());
		this.panelStandardParallel1.setValue(this.prjCoordSys.getPrjParameter().getStandardParallel1());
		this.panelStandardParallel2.setValue(this.prjCoordSys.getPrjParameter().getStandardParallel2());
		this.panelFirstPointLongitude.setValue(this.prjCoordSys.getPrjParameter().getFirstPointLongitude());
		this.panelSecondPointLongitude.setValue(this.prjCoordSys.getPrjParameter().getSecondPointLongitude());
		this.panelAzimuth.setValue(this.prjCoordSys.getPrjParameter().getAzimuth());
		this.textFieldFalseEasting.setText(df.format(this.prjCoordSys.getPrjParameter().getFalseEasting()));
		this.textFieldFalseNorthing.setText(df.format(this.prjCoordSys.getPrjParameter().getFalseNorthing()));
		this.textFieldScaleFactor.setText(df.format(this.prjCoordSys.getPrjParameter().getScaleFactor()));
	}

	/**
	 *
	 */
	private void initComponentState() {
		this.lock = true;
		this.textFieldName.setText(this.prjCoordSys.getName());
		this.lock = false;
		this.panelGeoCoordSys.setGeoCoordSys(this.prjCoordSys.getGeoCoordSys());
		this.radioButtonAngle.setSelected(true);
	}

	public PrjCoordSys getPrjCoordSys() {
		if (this.panelGeoCoordSys.getGeoCoordSys().getType() == GeoCoordSysType.GCS_USER_DEFINE) {
			this.prjCoordSys.setType(PrjCoordSysType.PCS_USER_DEFINED);
			if (!StringUtilities.isNullOrEmpty(this.textFieldName.getText())) {
				this.prjCoordSys.setName(this.textFieldName.getText());
			} else {
				this.prjCoordSys.setName(DEFAULT_NAME);
			}
		}
		if (this.prjCoordSys.getType() == PrjCoordSysType.PCS_USER_DEFINED) {
			this.prjCoordSys.setGeoCoordSys(this.panelGeoCoordSys.getGeoCoordSys());
			this.prjCoordSys.getPrjParameter().setCentralMeridian(this.panelCentralMeridian.getValue());
			this.prjCoordSys.getPrjParameter().setCentralParallel(this.panelCentralParallel.getValue());
			this.prjCoordSys.getPrjParameter().setStandardParallel1(this.panelStandardParallel1.getValue());
			this.prjCoordSys.getPrjParameter().setStandardParallel2(this.panelStandardParallel2.getValue());
			this.prjCoordSys.getPrjParameter().setFirstPointLongitude(this.panelFirstPointLongitude.getValue());
			this.prjCoordSys.getPrjParameter().setSecondPointLongitude(this.panelSecondPointLongitude.getValue());
			this.prjCoordSys.getPrjParameter().setAzimuth(this.panelAzimuth.getValue());
			this.prjCoordSys.getPrjParameter().setFalseEasting(Double.valueOf(this.textFieldFalseEasting.getText()));
			this.prjCoordSys.getPrjParameter().setFalseNorthing(Double.valueOf(this.textFieldFalseNorthing.getText()));
			this.prjCoordSys.getPrjParameter().setScaleFactor(Double.valueOf(this.textFieldScaleFactor.getText()));
		}
		return this.prjCoordSys.clone();
	}

	public void setPrjCoordSys(PrjCoordSys prjCoordSys) {
		if (this.prjCoordSys != null) {
			this.prjCoordSys.dispose();
		}
		this.prjCoordSys = prjCoordSys.clone();

		this.lock = true;
		this.lockType = true;
		this.lockUnit = true;
		this.textFieldName.setText(this.prjCoordSys.getName());
		this.comboBoxCoordType.setSelectedItem(PrjCoordSysTypeUtilities.getDescribe(this.prjCoordSys.getProjection().getType().name()));
		this.comboBoxCoordSysUnit.setSelectedItem(this.prjCoordSys.getCoordUnit());
		this.lock = false;
		this.lockType = false;
		this.lockUnit = false;

		this.panelGeoCoordSys.setGeoCoordSys(this.prjCoordSys.getGeoCoordSys());
		resetProjectionTypeValues();
	}

	public void clean() {
		this.panelGeoCoordSys.dispose();
		removeListeners();
		if (this.prjCoordSys != null) {
			this.prjCoordSys.dispose();
		}
	}

	/**
	 * @param isEditable
	 */
	public void setComponentEditable(Boolean isEditable) {
		//comboBoxCoordSysUnit.setEnabled(isEditable);
		this.panelCentralMeridian.setTextFieldEditable(isEditable);
		this.textFieldFalseEasting.setEditable(isEditable);
		this.panelCentralParallel.setTextFieldEditable(isEditable);
		this.textFieldFalseNorthing.setEditable(isEditable);
		this.textFieldScaleFactor.setEditable(isEditable);
		this.panelStandardParallel1.setTextFieldEditable(isEditable);
		this.panelStandardParallel2.setTextFieldEditable(isEditable);
		this.panelFirstPointLongitude.setTextFieldEditable(isEditable);
		this.panelSecondPointLongitude.setTextFieldEditable(isEditable);
		this.panelAzimuth.setTextFieldEditable(isEditable);

	}

}
