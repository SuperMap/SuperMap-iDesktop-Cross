package com.supermap.desktop.ui.controls.prjcoordsys;

import com.supermap.data.Enum;
import com.supermap.data.*;
import com.supermap.desktop.Interface.ISmTextFieldLegit;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.TextFields.SmTextFieldLegit;
import com.supermap.desktop.ui.controls.comboBox.JSearchComboBox;
import com.supermap.desktop.ui.controls.comboBox.SearchItemValueGetter;
import com.supermap.desktop.utilities.PrjCoordSysTypeUtilities;
import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author XiaJT
 * 重构界面-yuanR
 */
public class JPanelGeoCoordSys extends JPanel {

	// 名称
	private JLabel labelName = new JLabel();
	private final ItemListener comboBoxGeoSpheroidTypeListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (lockAxis) {
				return;
			}
			if (e.getStateChange() == ItemEvent.SELECTED) {
				Object selectedItem = comboBoxReferenceSpheroid.getSelectedItem();
				if (selectedItem instanceof GeoSpheroidType) {
					if (!selectedItem.equals(GeoSpheroidType.SPHEROID_USER_DEFINED)) {
						textFieldGeoSpheroidAxis.setText(String.valueOf(geoCoordSys.getGeoDatum().getGeoSpheroid().getAxis()));
						textFieldGeoSpheroidFlatten.setText(String.valueOf(geoCoordSys.getGeoDatum().getGeoSpheroid().getFlatten()));
						textFieldGeoSpheroidAxis.setEditable(false);
						textFieldGeoSpheroidFlatten.setEditable(false);
					} else {
						textFieldGeoSpheroidAxis.setEditable(true);
						textFieldGeoSpheroidFlatten.setEditable(true);
					}

					geoCoordSys.getGeoDatum().getGeoSpheroid().setType((GeoSpheroidType) selectedItem);
					geoCoordSys.getGeoDatum().getGeoSpheroid().setName(PrjCoordSysTypeUtilities.getDescribe(((GeoSpheroidType) selectedItem).name()));
					//geoCoordSys.getGeoDatum().getGeoSpheroid().setType(GeoSpheroidType.SPHEROID_USER_DEFINED);
					lockAxis = true;
					comboBoxReferenceSpheroid.setSelectedItem(PrjCoordSysTypeUtilities.getDescribe(((GeoSpheroidType) selectedItem).name()));
					lockAxis = false;
				} else {
					textFieldGeoSpheroidAxis.setEditable(true);
					textFieldGeoSpheroidFlatten.setEditable(true);
					if (StringUtilities.isNullOrEmptyString(selectedItem)) {
						return;
					}
					geoCoordSys.getGeoDatum().getGeoSpheroid().setType(GeoSpheroidType.SPHEROID_USER_DEFINED);
					geoCoordSys.getGeoDatum().getGeoSpheroid().setName((String) selectedItem);
				}
			}
		}
	};
	private JTextField textFieldName = new JTextField();
	private JSearchComboBox<GeoDatumType> comboBoxGeoDatumPlane = new JSearchComboBox<>();
	// 参考椭球体
	private JLabel labelReferenceSpheroid = new JLabel();
	private JSearchComboBox<GeoSpheroidType> comboBoxReferenceSpheroid = new JSearchComboBox<>();
	// 椭球长半轴
	private JLabel labelGeoSpheroidAxis = new JLabel();
	private SmTextFieldLegit textFieldGeoSpheroidAxis = new SmTextFieldLegit();
	//椭球扁率
	private JLabel labelGeoSpheroidFlatten = new JLabel();
	private SmTextFieldLegit textFieldGeoSpheroidFlatten = new SmTextFieldLegit();

	//中央子午线类型
	private JLabel labelCentralMeridianType = new JLabel();
	private JSearchComboBox<GeoPrimeMeridianType> comboBoxCentralMeridianType = new JSearchComboBox<>();
	//中央子午线
	private JLabel labelCentralBasisMeridian = new JLabel();
	// 用度分秒控件替换
	JPanelFormat panelCentralBasisMeridian = new JPanelFormat();
	//private SmTextFieldLegit textFieldCentralMeridian = new SmTextFieldLegit();

	private GeoCoordSys geoCoordSys = new GeoCoordSys();
	// 加锁防止事件循环触发

	private final static String DEFAULT_NAME = "New_Geographic_Coordinate_System";

	//private final ItemListener comboBoxNameListener = new ItemListener() {
	//	@Override
	//	public void itemStateChanged(ItemEvent e) {
	//		if (e.getStateChange() == ItemEvent.SELECTED && comboBoxName.getSelectedItem() != null) {
	//			if (lock) {
	//				return;
	//			}
	//			Object selectedItem = comboBoxName.getSelectedItem();
	//			if (selectedItem instanceof GeoCoordSysType && selectedItem != GeoCoordSysType.GCS_USER_DEFINE) {
	//				geoCoordSys.setType((GeoCoordSysType) selectedItem);
	//				lock = true;
	//				geoCoordSys.setType((GeoCoordSysType) selectedItem);
	//				geoCoordSys.setName(PrjCoordSysTypeUtilities.getDescribe(((GeoCoordSysType) selectedItem).name()));
	//				//comboBoxGeoDatumPlane.setSelectedItem(PrjCoordSysTypeUtilities.getDescribe(geoCoordSys.getGeoDatum().getType().name()));
	//				//comboBoxCentralMeridianType.setSelectedItem(PrjCoordSysTypeUtilities.getDescribe(geoCoordSys.getGeoPrimeMeridian().getType().name()));
	//				comboBoxName.setSelectedItem(PrjCoordSysTypeUtilities.getDescribe(((GeoCoordSysType) selectedItem).name()));
	//				lock = false;
	//			} else {
	//				if (StringUtilities.isNullOrEmptyString(selectedItem)) {
	//					return;
	//				}
	//				if (selectedItem instanceof String) {
	//					geoCoordSys.setType(GeoCoordSysType.GCS_USER_DEFINE);
	//					geoCoordSys.setName((String) selectedItem);
	//				}
	//			}
	//			//firePropertyChange("GeoCoordSysType", "", "");
	//		}
	//	}
	//};
	private final ItemListener comboBoxGeoDatumTypeListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (lockGeo) {
				return;
			}
			if (e.getStateChange() == ItemEvent.SELECTED) {
				Object selectedItem = comboBoxGeoDatumPlane.getSelectedItem();
				if (selectedItem instanceof GeoDatumType) {
					geoCoordSys.getGeoDatum().setType((GeoDatumType) selectedItem);
					geoCoordSys.getGeoDatum().setName(PrjCoordSysTypeUtilities.getDescribe(((GeoDatumType) selectedItem).name()));
					lockGeo = true;
					//geoCoordSys.getGeoDatum().setType(GeoDatumType.DATUM_USER_DEFINED);
					comboBoxGeoDatumPlane.setSelectedItem(PrjCoordSysTypeUtilities.getDescribe(((GeoDatumType) selectedItem).name()));
					lockGeo = false;
					// 当选择为defined时，支持设置：参考椭球体
					if (selectedItem.equals(GeoDatumType.DATUM_USER_DEFINED)) {
						comboBoxReferenceSpheroid.setEnabled(true);
					} else {
						comboBoxReferenceSpheroid.setEnabled(false);
						comboBoxReferenceSpheroid.setSelectedItem(PrjCoordSysTypeUtilities.getDescribe((geoCoordSys.getGeoDatum().getGeoSpheroid().getType().name())));
					}

					if (geoCoordSys.getType() != GeoCoordSysType.GCS_USER_DEFINE) {
						geoCoordSys.setType(GeoCoordSysType.GCS_USER_DEFINE);
						if (!StringUtilities.isNullOrEmpty(textFieldName.getText())) {
							geoCoordSys.setName(textFieldName.getText());
						} else {
							geoCoordSys.setName(DEFAULT_NAME);
						}
					}
				} else {
					comboBoxReferenceSpheroid.setEnabled(true);
					if (StringUtilities.isNullOrEmptyString(selectedItem)) {
						return;
					}
					geoCoordSys.getGeoDatum().setType(GeoDatumType.DATUM_USER_DEFINED);
					geoCoordSys.getGeoDatum().setName((String) selectedItem);
				}
			}
		}
	};
	private final ItemListener comboBoxCentralMeridianTypeListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (lockCenter) {
				return;
			}
			if (e.getStateChange() == ItemEvent.SELECTED) {
				Object selectedItem = comboBoxCentralMeridianType.getSelectedItem();
				if (selectedItem instanceof GeoPrimeMeridianType) {
					geoCoordSys.getGeoPrimeMeridian().setType((GeoPrimeMeridianType) selectedItem);
					geoCoordSys.getGeoPrimeMeridian().setName(PrjCoordSysTypeUtilities.getDescribe(((GeoPrimeMeridianType) selectedItem).name()));
					lockCenter = true;
					panelCentralBasisMeridian.setValue(geoCoordSys.getGeoPrimeMeridian().getLongitudeValue());
					comboBoxCentralMeridianType.setSelectedItem(PrjCoordSysTypeUtilities.getDescribe(((GeoPrimeMeridianType) selectedItem).name()));
					//geoCoordSys.getGeoPrimeMeridian().setType(GeoPrimeMeridianType.PRIMEMERIDIAN_USER_DEFINED);
					lockCenter = false;

					if (geoCoordSys.getType() != GeoCoordSysType.GCS_USER_DEFINE) {
						geoCoordSys.setType(GeoCoordSysType.GCS_USER_DEFINE);
						if (!StringUtilities.isNullOrEmpty(textFieldName.getText())) {
							geoCoordSys.setName(textFieldName.getText());
						} else {
							geoCoordSys.setName(DEFAULT_NAME);
						}
					}
				} else {
					if (StringUtilities.isNullOrEmptyString(selectedItem)) {
						return;
					}
					geoCoordSys.getGeoPrimeMeridian().setType(GeoPrimeMeridianType.PRIMEMERIDIAN_USER_DEFINED);
					geoCoordSys.getGeoPrimeMeridian().setName((String) selectedItem);
				}

			}
		}
	};
	//private JSearchComboBox<GeoCoordSysType> comboBoxName = new JSearchComboBox<>();
	// EPSG Code
	//private JLabel labelEPSG = new JLabel();
	//private SmTextFieldLegit textFieldEPSG = new SmTextFieldLegit();
	// 大地基准面
	private JLabel labelGeoDatumPlane = new JLabel();


	private boolean lockGeo = false;
	private boolean lock = false;
	private boolean lockAxis = false;
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
	private boolean lockCenter = false;

	private void textFieldNameChanged(DocumentEvent e) {
		if (this.lock) {
			return;
		}
		this.lock = true;
		if (StringUtilities.isNullOrEmptyString(this.textFieldName.getText())) {
			return;
		}
		this.geoCoordSys.setName(this.textFieldName.getText());
		this.lock = false;
	}

	public JPanelGeoCoordSys() {
		initComponents();
		addListeners();
		initLayout();
		initResources();
		initComponentStates();
		//setPanelEditable(false);
	}

	private void initComponents() {
		// region 类型
		//SearchItemValueGetter<Enum> searchItemValueGetter = PrjCoordSysSettingsUtilties.getSearchItemValueGetter();
		//comboBoxName.setSearchItemValueGetter(searchItemValueGetter);
		//Enum[] enums = Enum.getEnums(GeoCoordSysType.class);
		//
		//Arrays.sort(enums, 0, enums.length, new EnumComparator());
		//for (Enum anEnum : enums) {
		//	if (anEnum instanceof GeoCoordSysType && anEnum != GeoCoordSysType.GCS_USER_DEFINE) {
		//		comboBoxName.addItem((GeoCoordSysType) anEnum);
		//	}
		//}
		//comboBoxName.setRenderer(new MyEnumCellRender(comboBoxName));

		// region 大地参考系类型
		SearchItemValueGetter<Enum> searchItemValueGetter = PrjCoordSysSettingsUtilties.getSearchItemValueGetter();
		this.comboBoxGeoDatumPlane.setSearchItemValueGetter(searchItemValueGetter);
		Enum[] enumsGeoDatum = Enum.getEnums(GeoDatumType.class);
		//Arrays.sort(enumsGeoDatum, 0, enumsGeoDatum.length, new EnumComparator());
		for (Enum anEnum : enumsGeoDatum) {
			if (anEnum instanceof GeoDatumType) {
				this.comboBoxGeoDatumPlane.addItem((GeoDatumType) anEnum);
			}
		}
		this.comboBoxGeoDatumPlane.setRenderer(new MyEnumCellRender(this.comboBoxGeoDatumPlane));
		// endregion

		// region 椭球参数类型
		this.comboBoxReferenceSpheroid.setSearchItemValueGetter(searchItemValueGetter);
		Enum[] enumsGeoSpheroid = Enum.getEnums(GeoSpheroidType.class);
		//Arrays.sort(enumsGeoSpheroid, 0, enumsGeoSpheroid.length, new EnumComparator());
		for (Enum anEnum : enumsGeoSpheroid) {
			if (anEnum instanceof GeoSpheroidType) {
				this.comboBoxReferenceSpheroid.addItem((GeoSpheroidType) anEnum);
			}
		}
		this.comboBoxReferenceSpheroid.setRenderer(new MyEnumCellRender(this.comboBoxReferenceSpheroid));
		this.comboBoxReferenceSpheroid.setEnabled(false);
		// endregion

		// region 赤道半径
		this.textFieldGeoSpheroidAxis.setSmTextFieldLegit(new ISmTextFieldLegit() {
			@Override
			public boolean isTextFieldValueLegit(String textFieldValue) {
				if (StringUtilities.isNullOrEmpty(textFieldValue) || textFieldValue.contains("d")) {
					return false;
				}
				try {
					double value = Double.valueOf(textFieldValue);
					return axisValueChanged(value);
				} catch (Exception e) {
					return false;
				}
			}

			@Override
			public String getLegitValue(String currentValue, String backUpValue) {
				return backUpValue;
			}
		});
		this.textFieldGeoSpheroidAxis.setEditable(false);
		// endregion

		// region 扁率
		this.textFieldGeoSpheroidFlatten.setSmTextFieldLegit(new ISmTextFieldLegit() {
			@Override
			public boolean isTextFieldValueLegit(String textFieldValue) {
				if (StringUtilities.isNullOrEmpty(textFieldValue) || textFieldValue.contains("d")) {
					return false;
				}
				try {
					double value = Double.valueOf(textFieldValue);
					return flattenValueChanged(value);
				} catch (Exception e) {
					return false;
				}
			}

			@Override
			public String getLegitValue(String currentValue, String backUpValue) {
				return backUpValue;
			}
		});
		this.textFieldGeoSpheroidFlatten.setEditable(false);
		// endregion

		// region 中央经线
		this.comboBoxCentralMeridianType.setSearchItemValueGetter(searchItemValueGetter);
		Enum[] enumsCenter = Enum.getEnums(GeoPrimeMeridianType.class);
		//Arrays.sort(enumsCenter, 0, enumsCenter.length, new EnumComparator());
		for (Enum anEnum : enumsCenter) {
			if (anEnum instanceof GeoPrimeMeridianType) {
				this.comboBoxCentralMeridianType.addItem((GeoPrimeMeridianType) anEnum);
			}
		}
		this.comboBoxCentralMeridianType.setRenderer(new MyEnumCellRender(this.comboBoxCentralMeridianType));
		// endregion

		// region 其他初始化
		this.panelCentralBasisMeridian.setTextFieldEditable(false);
		// 默认初始化geoCoordSys
		this.geoCoordSys.setType(GeoCoordSysType.GCS_USER_DEFINE);
		this.geoCoordSys.setName(DEFAULT_NAME);
		// endregion
	}

	private boolean flattenValueChanged(double value) {
		if (value < 0 || value > 1) {
			return false;
		}
		if (!this.textFieldGeoSpheroidFlatten.getText().equals(this.textFieldGeoSpheroidFlatten.getBackUpValue())) {
			if (this.geoCoordSys.getGeoDatum().getGeoSpheroid().getType() != GeoSpheroidType.SPHEROID_USER_DEFINED) {
				String name = this.geoCoordSys.getGeoDatum().getGeoSpheroid().getName();
				this.geoCoordSys.getGeoDatum().getGeoSpheroid().setType(GeoSpheroidType.SPHEROID_USER_DEFINED);
				this.geoCoordSys.getGeoDatum().getGeoSpheroid().setName(name);
			}
			this.geoCoordSys.getGeoDatum().getGeoSpheroid().setFlatten(value);
		}
		return true;
	}

	private boolean axisValueChanged(double value) {
		if (value < 5000000 || value > 10000000) {
			return false;
		}
		if (!this.textFieldGeoSpheroidAxis.getText().equals(this.textFieldGeoSpheroidAxis.getBackUpValue())) {
			// if (!lockAxis) {
			// comboBoxReferenceSpheroid.setSelectedItem(GeoSpheroidType.SPHEROID_USER_DEFINED);
			// }
			if (this.geoCoordSys.getGeoDatum().getGeoSpheroid().getType() != GeoSpheroidType.SPHEROID_USER_DEFINED) {
				String name = this.geoCoordSys.getGeoDatum().getGeoSpheroid().getName();
				this.geoCoordSys.getGeoDatum().getGeoSpheroid().setType(GeoSpheroidType.SPHEROID_USER_DEFINED);
				this.geoCoordSys.getGeoDatum().getGeoSpheroid().setName(name);
			}
			this.geoCoordSys.getGeoDatum().getGeoSpheroid().setAxis(value);

		}
		return true;
	}

	private void addListeners() {
		//comboBoxName.addItemListener(comboBoxNameListener);
		this.textFieldName.getDocument().addDocumentListener(this.textFieldNameListener);
		this.comboBoxGeoDatumPlane.addItemListener(this.comboBoxGeoDatumTypeListener);
		this.comboBoxReferenceSpheroid.addItemListener(this.comboBoxGeoSpheroidTypeListener);
		this.comboBoxCentralMeridianType.addItemListener(this.comboBoxCentralMeridianTypeListener);
	}

	private void removeListeners() {
		//comboBoxName.removeItemListener(comboBoxNameListener);
		this.textFieldName.getDocument().removeDocumentListener(this.textFieldNameListener);
		this.comboBoxGeoDatumPlane.removeItemListener(this.comboBoxGeoDatumTypeListener);
		this.comboBoxReferenceSpheroid.removeItemListener(this.comboBoxGeoSpheroidTypeListener);
		this.comboBoxCentralMeridianType.removeItemListener(this.comboBoxCentralMeridianTypeListener);
	}

	// region 初始化布局
	private void initLayout() {
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);
		this.setLayout(groupLayout);
		//@formatter:off
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
				.addGroup(groupLayout.createSequentialGroup()
						.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(this.labelName)
								//.addComponent(this.labelEPSG)
								.addComponent(this.labelGeoDatumPlane)
								.addComponent(this.labelReferenceSpheroid)
								.addComponent(this.labelGeoSpheroidAxis)
								.addComponent(this.labelGeoSpheroidFlatten)
								.addComponent(this.labelCentralMeridianType)
								.addComponent(this.labelCentralBasisMeridian))
						.addGroup(groupLayout.createParallelGroup()
								.addComponent(this.textFieldName)
								//.addComponent(this.textFieldEPSG)
								.addComponent(this.comboBoxGeoDatumPlane)
								.addComponent(this.comboBoxReferenceSpheroid)
								.addComponent(this.textFieldGeoSpheroidAxis)
								.addComponent(this.textFieldGeoSpheroidFlatten)
								.addComponent(this.comboBoxCentralMeridianType)
								.addComponent(this.panelCentralBasisMeridian))));
		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createSequentialGroup()
						.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(this.labelName)
								.addComponent(this.textFieldName, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
						//.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						//.addComponent(this.labelEPSG)
						//.addComponent(this.textFieldEPSG, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(this.labelGeoDatumPlane)
								.addComponent(this.comboBoxGeoDatumPlane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(this.labelReferenceSpheroid)
								.addComponent(this.comboBoxReferenceSpheroid, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(this.labelGeoSpheroidAxis)
								.addComponent(this.textFieldGeoSpheroidAxis, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(this.labelGeoSpheroidFlatten)
								.addComponent(this.textFieldGeoSpheroidFlatten, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(this.labelCentralMeridianType)
								.addComponent(this.comboBoxCentralMeridianType, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(this.labelCentralBasisMeridian)
								.addComponent(this.panelCentralBasisMeridian, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))))
		;
		//@formatter:on
	}

	private void initResources() {
		this.labelName.setText(ControlsProperties.getString("String_Label_Name"));
		//labelEPSG.setText(ControlsProperties.getString("String_Label_EPSG_Code"));
		this.labelGeoDatumPlane.setText(ControlsProperties.getString("String_Label_GeoDatumPlane"));
		this.labelReferenceSpheroid.setText(CoreProperties.getString("String_Label_GeoCoordSys_ReferenceSpheroid"));
		this.labelGeoSpheroidAxis.setText(CoreProperties.getString("String_Label_GeoSpheroid_Axis"));
		this.labelGeoSpheroidFlatten.setText(CoreProperties.getString("String_Label_GeoSpheroid_Flatten"));
		this.labelCentralMeridianType.setText(ControlsProperties.getString("String_Label_CentralBasisMeridian"));
		this.labelCentralBasisMeridian.setText(ControlsProperties.getString("String_Label_CentralBasisMeridian"));
	}

	private void initComponentStates() {
		this.lock = true;
		this.lockGeo = true;
		this.lockAxis = true;
		this.lockCenter = true;
		this.textFieldName.setText(this.geoCoordSys.getName());
		this.comboBoxGeoDatumPlane.setSelectedItem(PrjCoordSysTypeUtilities.getDescribe(this.geoCoordSys.getGeoDatum().getType().name()));
		this.comboBoxReferenceSpheroid.setSelectedItem(PrjCoordSysTypeUtilities.getDescribe(this.geoCoordSys.getGeoDatum().getGeoSpheroid().getType().name()));
		this.textFieldGeoSpheroidAxis.setText(String.valueOf(this.geoCoordSys.getGeoDatum().getGeoSpheroid().getAxis()));
		this.textFieldGeoSpheroidFlatten.setText(String.valueOf(this.geoCoordSys.getGeoDatum().getGeoSpheroid().getFlatten()));
		this.comboBoxCentralMeridianType.setSelectedItem(PrjCoordSysTypeUtilities.getDescribe(this.geoCoordSys.getGeoPrimeMeridian().getType().name()));
		this.panelCentralBasisMeridian.setValue(this.geoCoordSys.getGeoPrimeMeridian().getLongitudeValue());
		this.lock = false;
		this.lockGeo = false;
		this.lockAxis = false;
		this.lockCenter = false;
	}

	public GeoCoordSys getGeoCoordSys() {
		return geoCoordSys.clone();
	}

	public void setGeoCoordSys(GeoCoordSys geoCoordSys) {
		if (this.geoCoordSys != null) {
			this.geoCoordSys.dispose();
		}
		this.geoCoordSys = geoCoordSys.clone();
		initComponentStates();
	}

	///**
	// * 设置面板控件是否只能选择不能编辑
	// *
	// * @param isEditable
	// */
	//public void setPanelEditable(Boolean isEditable) {
	//	this.comboBoxName.setEditable(isEditable);
	//	this.comboBoxGeoDatumPlane.setEditable(isEditable);
	//	this.comboBoxReferenceSpheroid.setEditable(isEditable);
	//	this.comboBoxCentralMeridianType.setEditable(isEditable);
	//}

	public void dispose() {
		removeListeners();
		if (this.geoCoordSys != null) {
			this.geoCoordSys.dispose();
		}
	}
}
