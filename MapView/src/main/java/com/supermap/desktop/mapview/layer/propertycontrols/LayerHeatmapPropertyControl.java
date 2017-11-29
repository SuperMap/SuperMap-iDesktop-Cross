package com.supermap.desktop.mapview.layer.propertycontrols;

import com.supermap.data.FieldInfos;
import com.supermap.data.FieldType;
import com.supermap.desktop.controls.DefaultValues;
import com.supermap.desktop.Interface.ISmTextFieldLegit;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.colorScheme.ColorsComboBox;
import com.supermap.desktop.mapview.MapViewProperties;
import com.supermap.desktop.mapview.layer.propertymodel.LayerHeatmapPropertyModel;
import com.supermap.desktop.ui.SMSpinner;
import com.supermap.desktop.ui.controls.ComponentDropDown;
import com.supermap.desktop.ui.controls.SmComboBox;
import com.supermap.desktop.ui.controls.TextFields.SmTextFieldLegit;
import com.supermap.desktop.utilities.DoubleUtilities;
import com.supermap.desktop.utilities.StringUtilities;
import com.supermap.mapping.LayerHeatmap;
import com.supermap.mapping.MapDrawingEvent;
import com.supermap.mapping.MapDrawingListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by lixiaoyao on 2017/7/19.
 */
public class LayerHeatmapPropertyControl extends AbstractLayerPropertyControl {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JLabel labelKernelRadius;
	private SmTextFieldLegit textFieldKernelRadius;
	private JLabel labelWeightField;
	private SmComboBox comboBoxWeightField;
	private JLabel labelColorScheme;
	private ColorsComboBox colorsComboBox;
	private JLabel labelMaxColor;
	private ComponentDropDown componentDropDownMaxColor;
	private JLabel labelMaxColorTransparence;
	private SMSpinner smSpinnerMaxColorTransparence;
	private JLabel labelMinColor;
	private ComponentDropDown componentDropDownMinColor;
	private JLabel labelMinColorTransparence;
	private SMSpinner smSpinnerMinColorTransparence;
	private JLabel labelFuzzyDegree;
	private SMSpinner smSpinnerFuzzyDegree;
	private JLabel labelIntensity;
	private SMSpinner smSpinnerIntensity;
	private JPanel panelMaximumSetting;
	private ButtonGroup buttonGroupMaximumSetting;
	private JRadioButton radioButtonCurrentViewMaximum;
	private JLabel labelCurrentViewMaximumLeft;
	private JLabel labelCurrentViewMaximumLeftCenter;
	private JLabel labelCurrentViewMaximumCenter;
	private JLabel labelCurrentViewMaximumRightCenter;
	private JLabel labelCurrentViewMaximumRight;
	private JRadioButton radioButtonSystemMaximum;
	private JLabel labelSystemMaximumLeft;
	private JLabel labelSystemMaximumLeftCenter;
	private JLabel labelSystemMaximumCenter;
	private JLabel labelSystemMaximumRightCenter;
	private JLabel labelSystemMaximumRight;
	private JRadioButton radioButtonCustomMaximum;
	private JLabel labelCustomMaximumLeft;
	private SmTextFieldLegit waringTextFieldCustomMinValue;
	private JLabel labelCustomMaximumCenter;
	private SmTextFieldLegit waringTextFieldCustomMaxValue;
	private JLabel labelCustomMaximumRight;

	private static final int POINT_NUM = 6;

	public LayerHeatmapPropertyControl() {
		// do nothing
	}

	@Override
	public LayerHeatmapPropertyModel getLayerPropertyModel() {
		return (LayerHeatmapPropertyModel) super.getLayerPropertyModel();
	}

	@Override
	public LayerHeatmapPropertyModel getModifiedLayerPropertyModel() {
		return (LayerHeatmapPropertyModel) super.getModifiedLayerPropertyModel();
	}

	@Override
	protected void initializeComponents() {
		this.setBorder(BorderFactory.createTitledBorder("parameterSetting"));

		this.labelKernelRadius = new JLabel("KernelRadius:");
		this.textFieldKernelRadius = new SmTextFieldLegit();
		this.labelWeightField = new JLabel("WeightField:");
		this.comboBoxWeightField = new SmComboBox();
		this.labelColorScheme = new JLabel("ColorScheme:");
		this.colorsComboBox = new ColorsComboBox(ControlsProperties.getString("String_ColorSchemeManager_Map_GridAggregation"));
		this.labelMaxColor = new JLabel("MaxColor:");
		this.componentDropDownMaxColor = new ComponentDropDown(ComponentDropDown.COLOR_TYPE);
		this.labelMinColor = new JLabel("MinColor:");
		this.componentDropDownMinColor = new ComponentDropDown(ComponentDropDown.COLOR_TYPE);
		this.labelMaxColorTransparence = new JLabel("MaxColorTransparence:");
		this.smSpinnerMaxColorTransparence = new SMSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		this.labelMinColorTransparence = new JLabel("MinColorTransparence:");
		this.smSpinnerMinColorTransparence = new SMSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		this.labelFuzzyDegree = new JLabel("FuzzyDegree:");
		this.smSpinnerFuzzyDegree = new SMSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		this.labelIntensity = new JLabel("Intensity:");
		this.smSpinnerIntensity = new SMSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		this.panelMaximumSetting = new JPanel();
		this.buttonGroupMaximumSetting = new ButtonGroup();
		this.radioButtonCurrentViewMaximum = new JRadioButton("CurrentViewMaximum:");
		this.labelCurrentViewMaximumLeft = new JLabel("CurrentViewMaximumLeft");
		this.labelCurrentViewMaximumLeftCenter = new JLabel("CurrentViewMaximumLeftCenter");
		this.labelCurrentViewMaximumCenter = new JLabel("CurrentViewMaximumCenter");
		this.labelCurrentViewMaximumRightCenter = new JLabel("CurrentViewMaximumRightCenter");
		this.labelCurrentViewMaximumRight = new JLabel("CurrentViewMaximumRight");
		this.radioButtonSystemMaximum = new JRadioButton("SystemMaximum:");
		this.labelSystemMaximumLeft = new JLabel("SystemMaximumLeft");
		this.labelSystemMaximumLeftCenter = new JLabel("SystemMaximumLeftCenter");
		this.labelSystemMaximumCenter = new JLabel("SystemMaximumCenter");
		this.labelSystemMaximumRightCenter = new JLabel("SystemMaximumRightCenter");
		this.labelSystemMaximumRight = new JLabel("SystemMaximumRight");
		this.radioButtonCustomMaximum = new JRadioButton("CustomMaximum");
		this.labelCustomMaximumLeft = new JLabel("CustomMaximumLeft");
		this.waringTextFieldCustomMinValue = new SmTextFieldLegit();
		this.labelCustomMaximumCenter = new JLabel("CustomMaximumCenter");
		this.waringTextFieldCustomMaxValue = new SmTextFieldLegit();
		this.labelCustomMaximumRight = new JLabel("CustomMaximumRight");

		this.buttonGroupMaximumSetting.add(this.radioButtonCurrentViewMaximum);
		this.buttonGroupMaximumSetting.add(this.radioButtonSystemMaximum);
		this.buttonGroupMaximumSetting.add(this.radioButtonCustomMaximum);

		GroupLayout groupLayoutMaximumSet = new GroupLayout(this.panelMaximumSetting);
		groupLayoutMaximumSet.setAutoCreateContainerGaps(true);
		groupLayoutMaximumSet.setAutoCreateGaps(true);
		this.panelMaximumSetting.setLayout(groupLayoutMaximumSet);

		groupLayoutMaximumSet.setHorizontalGroup(groupLayoutMaximumSet.createSequentialGroup()
				.addGroup(groupLayoutMaximumSet.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.radioButtonCurrentViewMaximum)
						.addComponent(this.radioButtonSystemMaximum)
						.addComponent(this.radioButtonCustomMaximum))
				.addGroup(groupLayoutMaximumSet.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.labelCurrentViewMaximumLeft)
						.addComponent(this.labelSystemMaximumLeft)
						.addComponent(this.labelCustomMaximumLeft))
				.addGroup(groupLayoutMaximumSet.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelCurrentViewMaximumLeftCenter)
						.addComponent(this.labelSystemMaximumLeftCenter)
						.addComponent(this.waringTextFieldCustomMinValue, 80, 80, Short.MAX_VALUE))
				.addGroup(groupLayoutMaximumSet.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelCurrentViewMaximumCenter)
						.addComponent(this.labelSystemMaximumCenter)
						.addComponent(this.labelCustomMaximumCenter))
				.addGroup(groupLayoutMaximumSet.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelCurrentViewMaximumRightCenter)
						.addComponent(this.labelSystemMaximumRightCenter)
						.addComponent(this.waringTextFieldCustomMaxValue, 80, 80, Short.MAX_VALUE))
				.addGroup(groupLayoutMaximumSet.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.labelCurrentViewMaximumRight)
						.addComponent(this.labelSystemMaximumRight)
						.addComponent(this.labelCustomMaximumRight))
		);

		groupLayoutMaximumSet.setVerticalGroup(groupLayoutMaximumSet.createSequentialGroup()
				.addGroup(groupLayoutMaximumSet.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.radioButtonCurrentViewMaximum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.labelCurrentViewMaximumLeft)
						.addComponent(this.labelCurrentViewMaximumLeftCenter)
						.addComponent(this.labelCurrentViewMaximumCenter)
						.addComponent(this.labelCurrentViewMaximumRightCenter)
						.addComponent(this.labelCurrentViewMaximumRight))
				.addGroup(groupLayoutMaximumSet.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.radioButtonSystemMaximum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.labelSystemMaximumLeft)
						.addComponent(this.labelSystemMaximumLeftCenter)
						.addComponent(this.labelSystemMaximumCenter)
						.addComponent(this.labelSystemMaximumRightCenter)
						.addComponent(this.labelSystemMaximumRight))
				.addGroup(groupLayoutMaximumSet.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.radioButtonCustomMaximum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.labelCustomMaximumLeft)
						.addComponent(this.waringTextFieldCustomMinValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.labelCustomMaximumCenter)
						.addComponent(this.waringTextFieldCustomMaxValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.labelCustomMaximumRight))
		);

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setAutoCreateContainerGaps(true);
		groupLayout.setAutoCreateGaps(true);
		this.setLayout(groupLayout);

		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(this.labelKernelRadius, GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_LABEL_WIDTH, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.labelWeightField, GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_LABEL_WIDTH, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.labelColorScheme, GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_LABEL_WIDTH, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.labelMaxColor, GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_LABEL_WIDTH, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.labelMinColor, GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_LABEL_WIDTH, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.labelFuzzyDegree, GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_LABEL_WIDTH, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.labelIntensity, GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_LABEL_WIDTH, GroupLayout.PREFERRED_SIZE))

						.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(this.textFieldKernelRadius)
								.addComponent(this.comboBoxWeightField, GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_COMPONENT_WIDTH, Short.MAX_VALUE)
								.addComponent(this.colorsComboBox, GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_COMPONENT_WIDTH, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup()
										.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(this.componentDropDownMaxColor, 55, 55, Short.MAX_VALUE)
												.addComponent(this.componentDropDownMinColor, 55, 55, Short.MAX_VALUE))
										.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(this.labelMaxColorTransparence)
												.addComponent(this.labelMinColorTransparence))
										.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(this.smSpinnerMaxColorTransparence, 55, 55, Short.MAX_VALUE)
												.addComponent(this.smSpinnerMinColorTransparence, 55, 55, Short.MAX_VALUE)))
								.addComponent(this.smSpinnerFuzzyDegree, GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_COMPONENT_WIDTH, Short.MAX_VALUE)
								.addComponent(this.smSpinnerIntensity, GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_COMPONENT_WIDTH, Short.MAX_VALUE)))
				.addComponent(this.panelMaximumSetting)
		);

		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelKernelRadius)
						.addComponent(this.textFieldKernelRadius, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelWeightField)
						.addComponent(this.comboBoxWeightField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelColorScheme)
						.addComponent(this.colorsComboBox, GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_COMPONENT_HEIGHT, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelMaxColor)
						.addComponent(this.componentDropDownMaxColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.labelMaxColorTransparence, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.smSpinnerMaxColorTransparence, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelMinColor)
						.addComponent(this.componentDropDownMinColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.labelMinColorTransparence, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.smSpinnerMinColorTransparence, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelFuzzyDegree)
						.addComponent(this.smSpinnerFuzzyDegree, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelIntensity)
						.addComponent(this.smSpinnerIntensity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(this.panelMaximumSetting)
		);

	}

	@Override
	protected void initializeResources() {
		((TitledBorder) this.getBorder()).setTitle(MapViewProperties.getString("String_LayerHeatmap_ParameterSetting"));
		this.panelMaximumSetting.setBorder(BorderFactory.createTitledBorder(MapViewProperties.getString("String_LayerHeatmap_MaxiumSetting")));
		this.labelKernelRadius.setText(MapViewProperties.getString("String_LayerHeatmap_KernelRadius"));
		this.labelKernelRadius.setToolTipText(ControlsProperties.getString("String_ValidRange") + "[1,50]");
		this.labelWeightField.setText(ControlsProperties.getString("String_LayerHeatmap_WeightField"));
		this.labelColorScheme.setText(ControlsProperties.getString("String_LabelColorScheme"));
		this.labelMaxColor.setText(MapViewProperties.getString("String_LayerHeatmap_MaxColor"));
		this.labelMaxColorTransparence.setText(ControlsProperties.getString("String_Label_Transparence"));
		this.labelMinColor.setText(MapViewProperties.getString("String_LayerHeatmap_MinColor"));
		this.labelMinColorTransparence.setText(ControlsProperties.getString("String_Label_Transparence"));
		this.labelFuzzyDegree.setText(MapViewProperties.getString("String_LayerHeatmap_FuzzyDegree"));
		this.labelIntensity.setText(MapViewProperties.getString("String_LayerHeatmap_Intensity"));
		this.radioButtonCurrentViewMaximum.setText(MapViewProperties.getString("String_LayerHeatmap_CurrentMaximun"));
		this.radioButtonSystemMaximum.setText(MapViewProperties.getString("String_LayerHeatmap_SystemMaximum"));
		this.labelSystemMaximumLeft.setText("[");
		this.labelSystemMaximumLeftCenter.setText("0");
		this.labelSystemMaximumCenter.setText(",");
		this.labelSystemMaximumRightCenter.setText("1");
		this.labelSystemMaximumRight.setText("]");
		this.labelCurrentViewMaximumLeft.setText("[");
		this.labelCurrentViewMaximumLeftCenter.setText("0");
		this.labelCurrentViewMaximumCenter.setText(",");
		this.labelCurrentViewMaximumRightCenter.setText("1");
		this.labelCurrentViewMaximumRight.setText("]");
		this.radioButtonCustomMaximum.setText(MapViewProperties.getString("String_LayerHeatmap_CustomMaximum"));
		this.labelCustomMaximumLeft.setText("[");
		this.labelCustomMaximumCenter.setText(",");
		this.labelCustomMaximumRight.setText("]");
	}

	@Override
	protected void fillComponents() {
		if (getLayerPropertyModel() != null) {
			this.textFieldKernelRadius.setText(getLayerPropertyModel().getKernelRadius().toString());
			this.componentDropDownMaxColor.setColor(getLayerPropertyModel().getMaxColor());
			this.componentDropDownMinColor.setColor(getLayerPropertyModel().getMinColor());
			this.smSpinnerMaxColorTransparence.setValue(getLayerPropertyModel().getMaxColorTransparence());
			this.smSpinnerMinColorTransparence.setValue(getLayerPropertyModel().getMinColorTransparence());
			this.smSpinnerFuzzyDegree.setValue((int) (getLayerPropertyModel().getFuzzyDegree() * 100));
			this.smSpinnerIntensity.setValue(((int) (getLayerPropertyModel().getIntensity() * 100)));
			this.labelSystemMaximumLeftCenter.setText(DoubleUtilities.toString(getLayerPropertyModel().getSystemMinValue(), POINT_NUM));
			this.labelSystemMaximumRightCenter.setText(DoubleUtilities.toString(getLayerPropertyModel().getSystemMaxValue(), POINT_NUM));
			this.labelCurrentViewMaximumLeftCenter.setText(DoubleUtilities.toString(getLayerPropertyModel().getCurrentViewMinValue(), POINT_NUM));
			this.labelCurrentViewMaximumRightCenter.setText(DoubleUtilities.toString(getLayerPropertyModel().getCurrentViewMaxValue(), POINT_NUM));
			this.waringTextFieldCustomMinValue.setText(DoubleUtilities.toString(getLayerPropertyModel().getCustomMinValue(), POINT_NUM));
			this.waringTextFieldCustomMaxValue.setText(DoubleUtilities.toString(getLayerPropertyModel().getCustomMaxValue(), POINT_NUM));
			fillWeightField(getLayerPropertyModel().getFieldInfos(), getLayerPropertyModel().getWeightField());
			resetRadioButtonGroup();
			this.getModifiedLayerPropertyModel().getFormMap().getMapControl().getMap().removeDrawingListener(this.mapDrawingListener);
			this.getModifiedLayerPropertyModel().getFormMap().getMapControl().getMap().addDrawingListener(this.mapDrawingListener);
		}
	}

	@Override
	protected void registerEvents() {
		this.textFieldKernelRadius.setSmTextFieldLegit(this.iSmTextFieldLegitKernelRadius);
		this.comboBoxWeightField.addItemListener(this.selectedChangeListenerWeightField);
		this.colorsComboBox.addItemListener(this.selectedChangeListenerColors);
		this.componentDropDownMaxColor.addPropertyChangeListener(this.propertyChangeListenerMaxColor);
		this.smSpinnerMaxColorTransparence.addChangeListener(this.changeListenerMaxColorTransparence);
		this.componentDropDownMinColor.addPropertyChangeListener(this.propertyChangeListenerMinColor);
		this.smSpinnerMinColorTransparence.addChangeListener(this.changeListenerMinColorTransparence);
		this.smSpinnerFuzzyDegree.addChangeListener(this.changeListenerFuzzyDegree);
		this.smSpinnerIntensity.addChangeListener(this.changeListenerIntensity);
		this.radioButtonCurrentViewMaximum.addActionListener(this.selectedChangeListenerCurrentView);
		this.radioButtonSystemMaximum.addActionListener(this.selectedChangeListenerSystem);
		this.radioButtonCustomMaximum.addActionListener(this.selectedChangeListenerCustom);
		this.waringTextFieldCustomMinValue.setSmTextFieldLegit(this.iSmCustomMinValueLegitKernelRadius);
		this.waringTextFieldCustomMaxValue.setSmTextFieldLegit(this.iSmCustomMaxValueLegitKernelRadius);
	}

	@Override
	protected void unregisterEvents() {
		this.textFieldKernelRadius.removeEvents();
		this.comboBoxWeightField.removeItemListener(this.selectedChangeListenerWeightField);
		this.colorsComboBox.removeItemListener(this.selectedChangeListenerColors);
		this.componentDropDownMaxColor.removePropertyChangeListener(this.propertyChangeListenerMaxColor);
		this.smSpinnerMaxColorTransparence.removeChangeListener(this.changeListenerMaxColorTransparence);
		this.componentDropDownMinColor.removePropertyChangeListener(this.propertyChangeListenerMinColor);
		this.smSpinnerMinColorTransparence.removeChangeListener(this.changeListenerMinColorTransparence);
		this.smSpinnerFuzzyDegree.removeChangeListener(this.changeListenerFuzzyDegree);
		this.smSpinnerIntensity.removeChangeListener(this.changeListenerIntensity);
		this.radioButtonCurrentViewMaximum.removeActionListener(this.selectedChangeListenerCurrentView);
		this.radioButtonSystemMaximum.removeActionListener(this.selectedChangeListenerSystem);
		this.radioButtonCustomMaximum.removeActionListener(this.selectedChangeListenerCustom);
		this.getModifiedLayerPropertyModel().getFormMap().getMapControl().getMap().removeDrawingListener(this.mapDrawingListener);
		this.waringTextFieldCustomMinValue.removeEvents();
		this.waringTextFieldCustomMaxValue.removeEvents();
	}

	@Override
	protected void setControlEnabled(String propertyName, boolean enabled) {
		if (propertyName.equals(LayerHeatmapPropertyModel.KERNEL_RADIUS)) {
			this.textFieldKernelRadius.setEnabled(enabled);
		} else if (propertyName.equals(LayerHeatmapPropertyModel.WEIGHT_FIELD)) {
			this.comboBoxWeightField.setEnabled(enabled);
		} else if (propertyName.equals(LayerHeatmapPropertyModel.COLOR_PLAN)) {
			this.colorsComboBox.setEnabled(enabled);
		} else if (propertyName.equals(LayerHeatmapPropertyModel.MAX_COLOR)) {
			this.componentDropDownMaxColor.setEnabled(enabled);
		} else if (propertyName.equals(LayerHeatmapPropertyModel.MAX_COLOR_TRANSPARENCE)) {
			this.smSpinnerMaxColorTransparence.setEnabled(enabled);
		} else if (propertyName.equals(LayerHeatmapPropertyModel.MIN_COLOR)) {
			this.componentDropDownMinColor.setEnabled(enabled);
		} else if (propertyName.equals(LayerHeatmapPropertyModel.MIN_COLOR_TRANSPARENCE)) {
			this.smSpinnerMinColorTransparence.setEnabled(enabled);
		} else if (propertyName.equals(LayerHeatmapPropertyModel.FUZZY_DEGREE)) {
			this.smSpinnerFuzzyDegree.setEnabled(enabled);
		} else if (propertyName.equals(LayerHeatmapPropertyModel.INTENSITY)) {
			this.smSpinnerIntensity.setEnabled(enabled);
		}
	}

	private void fillWeightField(FieldInfos fieldInfos, String weightField) {
		this.comboBoxWeightField.removeAllItems();
		this.comboBoxWeightField.addItem("");
		for (int i = 0; i < fieldInfos.getCount(); i++) {
			if (fieldInfos.get(i).getType() == FieldType.INT32 || fieldInfos.get(i).getType() == FieldType.INT16 ||
					fieldInfos.get(i).getType() == FieldType.INT64 || fieldInfos.get(i).getType() == FieldType.SINGLE ||
					fieldInfos.get(i).getType() == FieldType.DOUBLE) {
				this.comboBoxWeightField.addItem(fieldInfos.get(i).getName());
			}
		}
		this.comboBoxWeightField.setSelectedItem(weightField);
	}

	private void resetRadioButtonGroup() {
		if (getLayerPropertyModel().getMaximumState() == LayerHeatmapPropertyModel.CUSTOMVIEW) {
			this.radioButtonCustomMaximum.setSelected(true);
			this.waringTextFieldCustomMaxValue.setEnabled(true);
			this.waringTextFieldCustomMinValue.setEnabled(true);
		} else if (getLayerPropertyModel().getMaximumState() == LayerHeatmapPropertyModel.SYSTEMVIEW) {
			this.radioButtonSystemMaximum.setSelected(true);
			this.waringTextFieldCustomMaxValue.setEnabled(false);
			this.waringTextFieldCustomMinValue.setEnabled(false);
		} else if (getLayerPropertyModel().getMaximumState() == LayerHeatmapPropertyModel.CURRENTVIEW) {
			this.radioButtonCurrentViewMaximum.setSelected(true);
			this.waringTextFieldCustomMaxValue.setEnabled(false);
			this.waringTextFieldCustomMinValue.setEnabled(false);
		}
	}

	private ISmTextFieldLegit iSmTextFieldLegitKernelRadius = new ISmTextFieldLegit() {
		@Override
		public boolean isTextFieldValueLegit(String textFieldValue) {
			if (StringUtilities.isNullOrEmpty(textFieldValue)) {
				return false;
			}
			try {
				Integer integer = Integer.valueOf(textFieldValue);
				if (integer < 1 || integer > 50) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
			textfieldKernelRadiusChange();
			return true;
		}

		@Override
		public String getLegitValue(String currentValue, String backUpValue) {
			return backUpValue;
		}
	};

	private ItemListener selectedChangeListenerWeightField = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			comboBoxWeightFieldChange();
		}
	};

	private ItemListener selectedChangeListenerColors = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			comboBoxColorsChange();
		}
	};

	private PropertyChangeListener propertyChangeListenerMaxColor = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			maxColorChange();
		}
	};

	private PropertyChangeListener propertyChangeListenerMinColor = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			minColorChange();
		}
	};

	private ChangeListener changeListenerMaxColorTransparence = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			maxColorTransparenceChange();
		}
	};

	private ChangeListener changeListenerMinColorTransparence = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			minColorTransparenceChange();
		}
	};

	private ChangeListener changeListenerFuzzyDegree = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			fuzzyDegreeChange();
		}
	};

	private ChangeListener changeListenerIntensity = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			intensityChange();
		}
	};

	private ActionListener selectedChangeListenerCurrentView = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			currentViewChange();
		}
	};

	private ActionListener selectedChangeListenerSystem = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			systemChange();
		}
	};

	private ActionListener selectedChangeListenerCustom = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			customChange();
		}
	};

	private ISmTextFieldLegit iSmCustomMinValueLegitKernelRadius = new ISmTextFieldLegit() {
		@Override
		public boolean isTextFieldValueLegit(String textFieldValue) {
			if (StringUtilities.isNullOrEmpty(textFieldValue)) {
				return false;
			}
			try {
				Double minValue = Double.valueOf(textFieldValue);
			} catch (Exception e) {
				return false;
			}
			minValueChange();
			return true;
		}

		@Override
		public String getLegitValue(String currentValue, String backUpValue) {
			return backUpValue;
		}
	};

	private ISmTextFieldLegit iSmCustomMaxValueLegitKernelRadius = new ISmTextFieldLegit() {
		@Override
		public boolean isTextFieldValueLegit(String textFieldValue) {
			if (StringUtilities.isNullOrEmpty(textFieldValue)) {
				return false;
			}
			try {
				Double maxValue = Double.valueOf(textFieldValue);
			} catch (Exception e) {
				return false;
			}
			maxValueChange();
			return true;
		}

		@Override
		public String getLegitValue(String currentValue, String backUpValue) {
			return backUpValue;
		}
	};

	private MapDrawingListener mapDrawingListener = new MapDrawingListener() {
		@Override
		public void mapDrawing(MapDrawingEvent mapDrawingEvent) {
			resetModelAllMaxiumValue();
		}
	};

	private void textfieldKernelRadiusChange() {
		getModifiedLayerPropertyModel().setKernelRadius(Integer.valueOf(this.textFieldKernelRadius.getText()));
		checkChanged();
	}

	private void comboBoxWeightFieldChange() {
		getModifiedLayerPropertyModel().setWeightField(String.valueOf(this.comboBoxWeightField.getSelectedItem()));
		checkChanged();
	}

	private void comboBoxColorsChange() {
		getModifiedLayerPropertyModel().setColors(this.colorsComboBox.getSelectedItem());
		this.componentDropDownMaxColor.setColor(this.colorsComboBox.getSelectedItem().get(0));
		this.componentDropDownMinColor.setColor(this.colorsComboBox.getSelectedItem().get((this.colorsComboBox.getSelectedItem().getCount() - 1)));
		maxColorChange();
		minColorChange();
	}

	private void maxColorChange() {
		getModifiedLayerPropertyModel().setMaxColor(this.componentDropDownMaxColor.getColor());
		int temp = (int) Math.round((1.0 - this.componentDropDownMaxColor.getColor().getAlpha() / 255.0) * 100);
		getModifiedLayerPropertyModel().setMaxColorTransparence(temp);
		this.smSpinnerMaxColorTransparence.removeChangeListener(this.changeListenerMaxColorTransparence);
		this.smSpinnerMaxColorTransparence.setValue(temp);
		this.smSpinnerMaxColorTransparence.addChangeListener(this.changeListenerMaxColorTransparence);
		checkChanged();
	}

	private void minColorChange() {
		getModifiedLayerPropertyModel().setMinColor(this.componentDropDownMinColor.getColor());
		int temp = (int) Math.round((1.0 - this.componentDropDownMinColor.getColor().getAlpha() / 255.0) * 100);
		getModifiedLayerPropertyModel().setMinColorTransparence(temp);
		this.smSpinnerMinColorTransparence.removeChangeListener(this.changeListenerMinColorTransparence);
		this.smSpinnerMinColorTransparence.setValue(temp);
		this.smSpinnerMinColorTransparence.addChangeListener(this.changeListenerMinColorTransparence);
		checkChanged();
	}

	private void maxColorTransparenceChange() {
		this.componentDropDownMaxColor.setColor(resetColor(this.componentDropDownMaxColor.getColor(), (int) this.smSpinnerMaxColorTransparence.getValue()));
		this.componentDropDownMaxColor.repaint();
		getModifiedLayerPropertyModel().setMaxColor(this.componentDropDownMaxColor.getColor());
		getModifiedLayerPropertyModel().setMaxColorTransparence((int) Math.round((1.0 - this.componentDropDownMaxColor.getColor().getAlpha() / 255.0) * 100));
		checkChanged();
	}

	private void minColorTransparenceChange() {
		this.componentDropDownMinColor.setColor(resetColor(this.componentDropDownMinColor.getColor(), (int) this.smSpinnerMinColorTransparence.getValue()));
		this.componentDropDownMinColor.repaint();
		getModifiedLayerPropertyModel().setMinColor(this.componentDropDownMinColor.getColor());
		int temp = (int) Math.round((1.0 - this.componentDropDownMinColor.getColor().getAlpha() / 255.0) * 100);
		getModifiedLayerPropertyModel().setMinColorTransparence(temp);
		checkChanged();
	}

	private void intensityChange() {
		getModifiedLayerPropertyModel().setIntensity((Integer) this.smSpinnerIntensity.getValue() / 100.0);
		checkChanged();
	}

	private void fuzzyDegreeChange() {
		getModifiedLayerPropertyModel().setFuzzyDegree((Integer) this.smSpinnerFuzzyDegree.getValue() / 100.0);
		checkChanged();
	}

	private Color resetColor(Color oldColor, int transparence) {
		Color newColor = new Color((float) (oldColor.getRed() / 255.0), (float) (oldColor.getGreen() / 255.0), (float) (oldColor.getBlue() / 255.0), (float) (1.0 - transparence / 100.0));
		return newColor;
	}

	private void currentViewChange() {
		if (this.radioButtonCurrentViewMaximum.isSelected()) {
			getModifiedLayerPropertyModel().setCurrentViewMinValue(Double.valueOf(this.labelCurrentViewMaximumLeftCenter.getText()));
			getModifiedLayerPropertyModel().setCurrentViewMaxValue(Double.valueOf(this.labelCurrentViewMaximumRightCenter.getText()));
			this.waringTextFieldCustomMaxValue.setEnabled(this.radioButtonCustomMaximum.isSelected());
			this.waringTextFieldCustomMinValue.setEnabled(this.radioButtonCustomMaximum.isSelected());
			getModifiedLayerPropertyModel().setMaximumState(LayerHeatmapPropertyModel.CURRENTVIEW);
			//getModifiedLayerPropertyModel().setCurrentView(true);
			checkChanged();
		}
	}

	private void systemChange() {
		if (this.radioButtonSystemMaximum.isSelected()) {
			getModifiedLayerPropertyModel().setSystemMinValue(Double.valueOf(this.labelSystemMaximumLeftCenter.getText()));
			getModifiedLayerPropertyModel().setSystemMaxValue(Double.valueOf(this.labelSystemMaximumRightCenter.getText()));
			this.waringTextFieldCustomMaxValue.setEnabled(this.radioButtonCustomMaximum.isSelected());
			this.waringTextFieldCustomMinValue.setEnabled(this.radioButtonCustomMaximum.isSelected());
			getModifiedLayerPropertyModel().setMaximumState(LayerHeatmapPropertyModel.SYSTEMVIEW);
			checkChanged();
		}
	}

	private void customChange() {
		if (this.radioButtonCustomMaximum.isSelected()) {
			this.waringTextFieldCustomMaxValue.setEnabled(this.radioButtonCustomMaximum.isSelected());
			this.waringTextFieldCustomMinValue.setEnabled(this.radioButtonCustomMaximum.isSelected());
			getModifiedLayerPropertyModel().setCustomMinValue(Double.valueOf(this.waringTextFieldCustomMinValue.getText()));
			getModifiedLayerPropertyModel().setCustomMaxValue(Double.valueOf(this.waringTextFieldCustomMaxValue.getText()));
			getModifiedLayerPropertyModel().setMaximumState(LayerHeatmapPropertyModel.CUSTOMVIEW);
			checkChanged();
		}
	}

	private void minValueChange() {
		if (this.radioButtonCustomMaximum.isSelected()) {
			getModifiedLayerPropertyModel().setCustomMinValue(Double.valueOf(this.waringTextFieldCustomMinValue.getText()));
			getModifiedLayerPropertyModel().setMaximumState(LayerHeatmapPropertyModel.CUSTOMVIEW);
			checkChanged();
		}
	}

	private void maxValueChange() {
		if (this.radioButtonCustomMaximum.isSelected()) {
			getModifiedLayerPropertyModel().setCustomMaxValue(Double.valueOf(this.waringTextFieldCustomMaxValue.getText()));
			getModifiedLayerPropertyModel().setMaximumState(LayerHeatmapPropertyModel.CUSTOMVIEW);
			checkChanged();
		}
	}

	private void resetModelAllMaxiumValue() {
		if (getLayerPropertyModel().getFormMap() != null && getLayerPropertyModel().getFormMap().getActiveLayers() != null && getLayerPropertyModel().getFormMap().getActiveLayers().length > 0 && getLayerPropertyModel().getFormMap().getActiveLayers()[0] instanceof LayerHeatmap) {
			LayerHeatmap layerHeatmap = ((LayerHeatmap) getLayerPropertyModel().getFormMap().getActiveLayers()[0]);
			if (Double.compare(this.getModifiedLayerPropertyModel().getSystemMinValue(), this.getModifiedLayerPropertyModel().getCustomMinValue()) == 0 && !this.radioButtonCustomMaximum.isSelected()) {
				this.waringTextFieldCustomMinValue.setText(DoubleUtilities.toString(layerHeatmap.getInternalMinValue(), POINT_NUM));
			}

			if (Double.compare(this.getModifiedLayerPropertyModel().getSystemMaxValue(), this.getModifiedLayerPropertyModel().getCustomMaxValue()) == 0 && !this.radioButtonCustomMaximum.isSelected()) {
				this.waringTextFieldCustomMaxValue.setText(DoubleUtilities.toString(layerHeatmap.getInternalMaxValue(), POINT_NUM));
			}

			if (Double.compare(layerHeatmap.getInternalMaxValue(), this.getModifiedLayerPropertyModel().getSystemMaxValue()) != 0) {
				this.getModifiedLayerPropertyModel().setSystemMaxValue(DoubleUtilities.stringToValue(DoubleUtilities.toString(layerHeatmap.getInternalMaxValue(), POINT_NUM)));
				this.labelSystemMaximumRightCenter.setText(DoubleUtilities.toString(layerHeatmap.getInternalMaxValue(), POINT_NUM));
			}

			if (Double.compare(layerHeatmap.getInternalMinValue(), this.getModifiedLayerPropertyModel().getSystemMinValue()) != 0) {
				this.getModifiedLayerPropertyModel().setSystemMinValue(DoubleUtilities.stringToValue(DoubleUtilities.toString(layerHeatmap.getInternalMinValue(), POINT_NUM)));
				this.labelSystemMaximumLeftCenter.setText(DoubleUtilities.toString(layerHeatmap.getInternalMinValue(), POINT_NUM));
			}

			if (Double.compare(layerHeatmap.getCurrentWindowMinValue(), this.getModifiedLayerPropertyModel().getCurrentViewMaxValue()) != 0) {
				this.getModifiedLayerPropertyModel().setCurrentViewMinValue(DoubleUtilities.stringToValue(DoubleUtilities.toString(layerHeatmap.getCurrentWindowMinValue(), POINT_NUM)));
				this.labelCurrentViewMaximumLeftCenter.setText(DoubleUtilities.toString(layerHeatmap.getCurrentWindowMinValue(), POINT_NUM));
			}

			if (Double.compare(layerHeatmap.getCurrentWindowMaxValue(), this.getModifiedLayerPropertyModel().getCurrentViewMaxValue()) != 0) {
				this.getModifiedLayerPropertyModel().setCurrentViewMaxValue(DoubleUtilities.stringToValue(DoubleUtilities.toString(layerHeatmap.getCurrentWindowMaxValue(), POINT_NUM)));
				this.labelCurrentViewMaximumRightCenter.setText(DoubleUtilities.toString(layerHeatmap.getCurrentWindowMaxValue(), POINT_NUM));
			}
		}
	}
}
