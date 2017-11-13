package com.supermap.desktop.ui.controls.prjcoordsys;

import com.supermap.data.Enum;
import com.supermap.data.*;
import com.supermap.desktop.Interface.ISmTextFieldLegit;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.TextFields.SmTextFieldLegit;
import com.supermap.desktop.ui.controls.comboBox.JSearchComboBox;
import com.supermap.desktop.ui.controls.comboBox.SearchItemValueGetter;
import com.supermap.desktop.utilities.EnumComparator;
import com.supermap.desktop.utilities.PrjCoordSysTypeUtilities;
import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;

/**
 * @author XiaJT
 * 重构界面-yuanR
 */
public class JPanelGeoCoordSys extends JPanel {

	// 名称
	private JLabel labelName = new JLabel();
	private JSearchComboBox<GeoCoordSysType> comboBoxName = new JSearchComboBox<>();
	// EPSG Code
	//private JLabel labelEPSG = new JLabel();
	//private SmTextFieldLegit textFieldEPSG = new SmTextFieldLegit();
	// 大地基准面
	private JLabel labelGeoDatumPlane = new JLabel();
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
	private JLabel labelCentralMeridian = new JLabel();
	// 用度分秒控件替换
	JPanelFormat panelCentralMeridian = new JPanelFormat();
	//private SmTextFieldLegit textFieldCentralMeridian = new SmTextFieldLegit();

	private GeoCoordSys geoCoordSys = new GeoCoordSys();
	// 加锁防止事件循环触发
	private boolean lock = false;
	private final ItemListener comboBoxNameListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED && comboBoxName.getSelectedItem() != null) {
				if (lock) {
					return;
				}
				Object selectedItem = comboBoxName.getSelectedItem();
				if (selectedItem instanceof GeoCoordSysType && selectedItem != GeoCoordSysType.GCS_USER_DEFINE) {
					geoCoordSys.setType((GeoCoordSysType) selectedItem);
					lock = true;
					comboBoxGeoDatumPlane.setSelectedItem(geoCoordSys.getGeoDatum().getType());
					comboBoxCentralMeridianType.setSelectedItem(geoCoordSys.getGeoPrimeMeridian().getType());
					comboBoxName.setSelectedItem(PrjCoordSysTypeUtilities.getDescribe(((GeoCoordSysType) selectedItem).name()));
					geoCoordSys.setType(GeoCoordSysType.GCS_USER_DEFINE);
					geoCoordSys.setName(PrjCoordSysTypeUtilities.getDescribe(((GeoCoordSysType) selectedItem).name()));
					lock = false;
				} else {
					if (StringUtilities.isNullOrEmptyString(selectedItem)) {
						return;
					}
					if (selectedItem instanceof String) {
						geoCoordSys.setType(GeoCoordSysType.GCS_USER_DEFINE);
						geoCoordSys.setName((String) selectedItem);
					}
				}
				firePropertyChange("GeoCoordSysType", "", "");
			}
		}
	};
	private boolean lockGeo = false;
	private final ItemListener comboBoxGeoDatumTypeListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (lockGeo) {
				return;
			}
			Object selectedItem = comboBoxGeoDatumPlane.getSelectedItem();
			if (e.getStateChange() == ItemEvent.SELECTED && selectedItem != null) {

				if (selectedItem instanceof GeoDatumType) {
					if (geoCoordSys.getType() != GeoCoordSysType.GCS_USER_DEFINE) {
						geoCoordSys.setType(GeoCoordSysType.GCS_USER_DEFINE);
					}
					geoCoordSys.getGeoDatum().setType((GeoDatumType) selectedItem);
					lockGeo = true;
					comboBoxReferenceSpheroid.setSelectedItem(geoCoordSys.getGeoDatum().getGeoSpheroid().getType());
					comboBoxGeoDatumPlane.setSelectedItem(PrjCoordSysTypeUtilities.getDescribe(((GeoDatumType) selectedItem).name()));
					geoCoordSys.getGeoDatum().setType(GeoDatumType.DATUM_USER_DEFINED);
					geoCoordSys.getGeoDatum().setName(PrjCoordSysTypeUtilities.getDescribe(((GeoDatumType) selectedItem).name()));
					lockGeo = false;
				} else {
					if (StringUtilities.isNullOrEmptyString(selectedItem)) {
						return;
					}
					geoCoordSys.getGeoDatum().setType(GeoDatumType.DATUM_USER_DEFINED);
					if (selectedItem instanceof String) {
						geoCoordSys.setName((String) selectedItem);
					}
				}
			}
		}
	};
	private boolean lockAxis = false;
	private final ItemListener comboBoxGeoSpheroidTypeListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (lockAxis) {
				return;
			}
			Object selectedItem = comboBoxReferenceSpheroid.getSelectedItem();
			if (e.getStateChange() == ItemEvent.SELECTED && selectedItem != null) {
				if (selectedItem instanceof GeoSpheroidType) {
					geoCoordSys.getGeoDatum().getGeoSpheroid().setType((GeoSpheroidType) selectedItem);
					lockAxis = true;
					textFieldGeoSpheroidAxis.setText(String.valueOf(geoCoordSys.getGeoDatum().getGeoSpheroid().getAxis()));
					textFieldGeoSpheroidFlatten.setText(String.valueOf(geoCoordSys.getGeoDatum().getGeoSpheroid().getFlatten()));
					comboBoxReferenceSpheroid.setSelectedItem(PrjCoordSysTypeUtilities.getDescribe(((GeoSpheroidType) selectedItem).name()));
					geoCoordSys.getGeoDatum().getGeoSpheroid().setType(GeoSpheroidType.SPHEROID_USER_DEFINED);
					geoCoordSys.getGeoDatum().getGeoSpheroid().setName(PrjCoordSysTypeUtilities.getDescribe(((GeoSpheroidType) selectedItem).name()));
					lockAxis = false;
				} else {
					if (StringUtilities.isNullOrEmptyString(selectedItem)) {
						return;
					}
					geoCoordSys.getGeoDatum().getGeoSpheroid().setType(GeoSpheroidType.SPHEROID_USER_DEFINED);
					geoCoordSys.getGeoDatum().getGeoSpheroid().setName((String) selectedItem);
				}
			}
		}
	};
	private boolean lockCenter = false;
	private final ItemListener comboBoxCentralMeridianTypeListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {

			if (lockCenter) {
				return;
			}
			if (e.getStateChange() == ItemEvent.SELECTED) {
				Object selectedItem = comboBoxCentralMeridianType.getSelectedItem();

				if (selectedItem instanceof GeoPrimeMeridianType) {
					if (geoCoordSys.getType() != GeoCoordSysType.GCS_USER_DEFINE) {
						geoCoordSys.setType(GeoCoordSysType.GCS_USER_DEFINE);
					}
					geoCoordSys.getGeoPrimeMeridian().setType((GeoPrimeMeridianType) selectedItem);
					lockCenter = true;
					panelCentralMeridian.setValue(geoCoordSys.getGeoPrimeMeridian().getLongitudeValue());
					//textFieldCentralMeridian.setText(String.valueOf(geoCoordSys.getGeoPrimeMeridian().getLongitudeValue()));
					comboBoxCentralMeridianType.setSelectedItem(PrjCoordSysTypeUtilities.getDescribe(((GeoPrimeMeridianType) selectedItem).name()));
					geoCoordSys.getGeoPrimeMeridian().setType(GeoPrimeMeridianType.PRIMEMERIDIAN_USER_DEFINED);
					geoCoordSys.getGeoPrimeMeridian().setName(PrjCoordSysTypeUtilities.getDescribe(((GeoPrimeMeridianType) selectedItem).name()));

					lockCenter = false;
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
		SearchItemValueGetter<Enum> searchItemValueGetter = PrjCoordSysSettingsUtilties.getSearchItemValueGetter();
		comboBoxName.setSearchItemValueGetter(searchItemValueGetter);
		Enum[] enums = Enum.getEnums(GeoCoordSysType.class);

		Arrays.sort(enums, 0, enums.length, new EnumComparator());
		for (Enum anEnum : enums) {
			if (anEnum instanceof GeoCoordSysType && anEnum != GeoCoordSysType.GCS_USER_DEFINE) {
				comboBoxName.addItem((GeoCoordSysType) anEnum);
			}
		}
		comboBoxName.setRenderer(new MyEnumCellRender(comboBoxName));

		// region 大地参考系类型
		Enum[] enumsGeoDatum = Enum.getEnums(GeoDatumType.class);
		comboBoxGeoDatumPlane.setSearchItemValueGetter(searchItemValueGetter);
		Arrays.sort(enumsGeoDatum, 0, enumsGeoDatum.length, new EnumComparator());

		for (Enum anEnum : enumsGeoDatum) {
			if (anEnum instanceof GeoDatumType && anEnum != GeoDatumType.DATUM_USER_DEFINED) {
				comboBoxGeoDatumPlane.addItem((GeoDatumType) anEnum);
			}
		}
		comboBoxGeoDatumPlane.setRenderer(new MyEnumCellRender(comboBoxGeoDatumPlane));
		// endregion

		// region 椭球参数类型
		Enum[] enumsGeoSpheroid = Enum.getEnums(GeoSpheroidType.class);
		comboBoxReferenceSpheroid.setSearchItemValueGetter(searchItemValueGetter);
		Arrays.sort(enumsGeoSpheroid, 0, enumsGeoSpheroid.length, new EnumComparator());
		for (Enum anEnum : enumsGeoSpheroid) {
			if (anEnum instanceof GeoSpheroidType && anEnum != GeoSpheroidType.SPHEROID_USER_DEFINED) {
				comboBoxReferenceSpheroid.addItem((GeoSpheroidType) anEnum);
			}
		}
		comboBoxReferenceSpheroid.setRenderer(new MyEnumCellRender(comboBoxReferenceSpheroid));
		// endregion

		// region 赤道半径
		textFieldGeoSpheroidAxis.setSmTextFieldLegit(new ISmTextFieldLegit() {
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
		// endregion

		// region 扁率
		textFieldGeoSpheroidFlatten.setSmTextFieldLegit(new ISmTextFieldLegit() {
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
		// endregion
		// region 中央经线
		Enum[] enumsCenter = Enum.getEnums(GeoPrimeMeridianType.class);
		comboBoxCentralMeridianType.setSearchItemValueGetter(searchItemValueGetter);
		for (Enum anEnum : enumsCenter) {
			if (anEnum instanceof GeoPrimeMeridianType && anEnum != GeoPrimeMeridianType.PRIMEMERIDIAN_USER_DEFINED)
				comboBoxCentralMeridianType.addItem((GeoPrimeMeridianType) anEnum);
		}
		comboBoxCentralMeridianType.setRenderer(new MyEnumCellRender(comboBoxCentralMeridianType));
		// endregion
		// region 经度
		//textFieldCentralMeridian.setSmTextFieldLegit(new ISmTextFieldLegit() {
		//	@Override
		//	public boolean isTextFieldValueLegit(String textFieldValue) {
		//		if (StringUtilities.isNullOrEmpty(textFieldValue) || textFieldValue.contains("d")) {
		//			return false;
		//		}
		//		try {
		//			double value = Double.valueOf(textFieldValue);
		//			return longitudeValueChanged(value);
		//		} catch (Exception e) {
		//			return false;
		//		}
		//	}
		//
		//	@Override
		//	public String getLegitValue(String currentValue, String backUpValue) {
		//		return backUpValue;
		//	}
		//});
		// endregion
	}

	//private boolean longitudeValueChanged(double value) {
	//	if (value < -180 || value > 180) {
	//		return false;
	//	}
	//	if (!textFieldCentralMeridian.getText().equals(textFieldCentralMeridian.getBackUpValue())) {
	//		if (geoCoordSys.getGeoPrimeMeridian().getType() != GeoPrimeMeridianType.PRIMEMERIDIAN_USER_DEFINED) {
	//			String name = geoCoordSys.getGeoPrimeMeridian().getName();
	//			geoCoordSys.getGeoPrimeMeridian().setType(GeoPrimeMeridianType.PRIMEMERIDIAN_USER_DEFINED);
	//			geoCoordSys.getGeoPrimeMeridian().setName(name);
	//		}
	//		geoCoordSys.getGeoPrimeMeridian().setLongitudeValue(value);
	//
	//	}
	//	return true;
	//}

	private boolean flattenValueChanged(double value) {
		if (value < 0 || value > 1) {
			return false;
		}
		if (!textFieldGeoSpheroidFlatten.getText().equals(textFieldGeoSpheroidFlatten.getBackUpValue())) {
			if (geoCoordSys.getGeoDatum().getGeoSpheroid().getType() != GeoSpheroidType.SPHEROID_USER_DEFINED) {
				String name = geoCoordSys.getGeoDatum().getGeoSpheroid().getName();
				geoCoordSys.getGeoDatum().getGeoSpheroid().setType(GeoSpheroidType.SPHEROID_USER_DEFINED);
				geoCoordSys.getGeoDatum().getGeoSpheroid().setName(name);
			}
			geoCoordSys.getGeoDatum().getGeoSpheroid().setFlatten(value);
		}
		return true;
	}

	private boolean axisValueChanged(double value) {
		if (value < 5000000 || value > 10000000) {
			return false;
		}
		if (!textFieldGeoSpheroidAxis.getText().equals(textFieldGeoSpheroidAxis.getBackUpValue())) {
			// if (!lockAxis) {
			// comboBoxReferenceSpheroid.setSelectedItem(GeoSpheroidType.SPHEROID_USER_DEFINED);
			// }
			if (geoCoordSys.getGeoDatum().getGeoSpheroid().getType() != GeoSpheroidType.SPHEROID_USER_DEFINED) {
				String name = geoCoordSys.getGeoDatum().getGeoSpheroid().getName();
				geoCoordSys.getGeoDatum().getGeoSpheroid().setType(GeoSpheroidType.SPHEROID_USER_DEFINED);
				geoCoordSys.getGeoDatum().getGeoSpheroid().setName(name);
			}
			geoCoordSys.getGeoDatum().getGeoSpheroid().setAxis(value);

		}
		return true;
	}

	private void addListeners() {
		comboBoxName.addItemListener(comboBoxNameListener);
		comboBoxGeoDatumPlane.addItemListener(comboBoxGeoDatumTypeListener);
		comboBoxReferenceSpheroid.addItemListener(comboBoxGeoSpheroidTypeListener);
		comboBoxCentralMeridianType.addItemListener(comboBoxCentralMeridianTypeListener);
	}

	private void removeListeners() {
		comboBoxName.removeItemListener(comboBoxNameListener);
		comboBoxGeoDatumPlane.removeItemListener(comboBoxGeoDatumTypeListener);
		comboBoxReferenceSpheroid.removeItemListener(comboBoxGeoSpheroidTypeListener);
		comboBoxCentralMeridianType.removeItemListener(comboBoxCentralMeridianTypeListener);
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
								.addComponent(this.labelCentralMeridian))
						.addGroup(groupLayout.createParallelGroup()
								.addComponent(this.comboBoxName)
								//.addComponent(this.textFieldEPSG)
								.addComponent(this.comboBoxGeoDatumPlane)
								.addComponent(this.comboBoxReferenceSpheroid)
								.addComponent(this.textFieldGeoSpheroidAxis)
								.addComponent(this.textFieldGeoSpheroidFlatten)
								.addComponent(this.comboBoxCentralMeridianType)
								.addComponent(this.panelCentralMeridian))));
		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createSequentialGroup()
						.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(this.labelName)
								.addComponent(this.comboBoxName, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
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
								.addComponent(this.labelCentralMeridian)
								.addComponent(this.panelCentralMeridian, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))));
		//@formatter:on
	}

	private void initResources() {
		labelName.setText(ControlsProperties.getString("String_Label_Name"));
		//labelEPSG.setText(ControlsProperties.getString("String_Label_EPSG_Code"));
		labelGeoDatumPlane.setText(ControlsProperties.getString("String_Label_GeoDatumPlane"));
		labelReferenceSpheroid.setText(CoreProperties.getString("String_Label_GeoCoordSys_ReferenceSpheroid"));
		labelGeoSpheroidAxis.setText(CoreProperties.getString("String_Label_GeoSpheroid_Axis"));
		labelGeoSpheroidFlatten.setText(CoreProperties.getString("String_Label_GeoSpheroid_Flatten"));
		labelCentralMeridianType.setText(ControlsProperties.getString("String_CentralMeridian"));
		labelCentralMeridian.setText(ControlsProperties.getString("String_CentralMeridian"));
	}

	private void initComponentStates() {
		//lock = true;
		lockGeo = true;
		lockAxis = true;
		lockCenter = true;
		comboBoxName.setSelectedItem("New_Geographic_Coordinate_System");
		comboBoxGeoDatumPlane.setSelectedItem(this.geoCoordSys.getGeoDatum().getType());
		comboBoxReferenceSpheroid.setSelectedItem(this.geoCoordSys.getGeoDatum().getGeoSpheroid().getName());
		textFieldGeoSpheroidAxis.setText(String.valueOf(this.geoCoordSys.getGeoDatum().getGeoSpheroid().getAxis()));
		textFieldGeoSpheroidFlatten.setText(String.valueOf(this.geoCoordSys.getGeoDatum().getGeoSpheroid().getFlatten()));

		comboBoxCentralMeridianType.setSelectedItem(this.geoCoordSys.getGeoPrimeMeridian().getName());
		panelCentralMeridian.setValue(this.geoCoordSys.getGeoPrimeMeridian().getLongitudeValue());
		//textFieldCentralMeridian.setText(String.valueOf(this.geoCoordSys.getGeoPrimeMeridian().getLongitudeValue()));

		//lock = false;
		lockGeo = false;
		lockAxis = false;
		lockCenter = false;
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
