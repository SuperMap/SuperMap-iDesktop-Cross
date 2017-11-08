package com.supermap.desktop.process.parameters.ParameterPanels;

import com.supermap.analyst.spatialanalyst.*;
import com.supermap.data.*;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.events.FieldConstraintChangedEvent;
import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.interfaces.IParameterPanel;
import com.supermap.desktop.process.parameter.interfaces.ParameterPanelDescribe;
import com.supermap.desktop.process.parameter.ipls.ParameterShapeType;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.ui.controls.TextFields.NumTextFieldLegit;
import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created By Chens on 2017/8/16 0016
 */
@ParameterPanelDescribe(parameterPanelType = ParameterType.SHAPE_TYPE)
public class ParameterShapeTypePanel extends SwingPanel implements IParameterPanel {
	private JLabel labelShapeType;
	private JComboBox comboBoxShapeType;
	private JLabel labelUnitType;
	private JComboBox comboBoxUnitType;
	private JLabel labelWidth;
	private NumTextFieldLegit textFieldWidth;
	private JLabel labelHeight;
	private NumTextFieldLegit textFieldHeight;
	private JLabel labelRadius;
	private NumTextFieldLegit textFieldRadius;
	private JLabel labelInnerRadius;
	private NumTextFieldLegit textFieldInnerRadius;
	private JLabel labelOuterRadius;
	private NumTextFieldLegit textFieldOuterRadius;
	private JLabel labelStartAngle;
	private NumTextFieldLegit textFieldStartAngle;
	private JLabel labelEndAngle;
	private NumTextFieldLegit textFieldEndAngle;

	private boolean isSelectingItem = false;
	private ParameterShapeType parameterShapeType;
	private NeighbourShape neighbourShape;
	Dataset dataset;

	private static final String RECTANGLE = ControlsProperties.getString("String_ThemeLabelBackShape_Rect");
	private static final String CIRCLE = ControlsProperties.getString("String_Circle");
	private static final String ANNULUS = ControlsProperties.getString("String_Annulus");
	private static final String WEDGE = ControlsProperties.getString("String_Wedge");
	private static final String UNIT_TYPE_CELL = ProcessProperties.getString("String_NeighbourUnitType_Cell");
	private static final String UNIT_TYPE_MAP = ControlsProperties.getString("String_NeighbourUnitType_Map");

	public ParameterShapeTypePanel(IParameter parameterShapeType) {
		super(parameterShapeType);
		this.parameterShapeType = (ParameterShapeType) parameterShapeType;
		this.neighbourShape = (NeighbourShape) ((ParameterShapeType) parameterShapeType).getSelectedItem();
		this.dataset = ((ParameterShapeType) parameterShapeType).getDataset();
		initComponent();
		initResources();
		initLayout();
		initComponentState();
		initListener();
	}

	private void initComponent() {
		labelShapeType =new JLabel();
		labelUnitType =new JLabel();
		labelWidth =new JLabel();
		labelHeight =new JLabel();
		labelRadius =new JLabel();
		labelInnerRadius =new JLabel();
		labelOuterRadius =new JLabel();
		labelStartAngle =new JLabel();
		labelEndAngle =new JLabel();
		comboBoxShapeType = new JComboBox();
		comboBoxUnitType = new JComboBox();
		textFieldWidth = new NumTextFieldLegit();
		textFieldHeight = new NumTextFieldLegit();
		textFieldRadius = new NumTextFieldLegit();
		textFieldInnerRadius = new NumTextFieldLegit();
		textFieldOuterRadius = new NumTextFieldLegit();
		textFieldStartAngle = new NumTextFieldLegit();
		textFieldEndAngle = new NumTextFieldLegit();
		ComponentUIUtilities.setName(this.comboBoxShapeType, "ParameterShapeTypePanel" + "_comboBoxShapeType");
		ComponentUIUtilities.setName(this.comboBoxUnitType, "ParameterShapeTypePanel" + "_comboBoxUnitType");
		ComponentUIUtilities.setName(this.textFieldWidth, "ParameterShapeTypePanel" + "_textFieldWidth");
		ComponentUIUtilities.setName(this.textFieldHeight, "ParameterShapeTypePanel" + "_textFieldHeight");
		ComponentUIUtilities.setName(this.textFieldRadius, "ParameterShapeTypePanel" + "_textFieldRadius");
		ComponentUIUtilities.setName(this.textFieldInnerRadius, "ParameterShapeTypePanel" + "_textFieldInnerRadius");
		ComponentUIUtilities.setName(this.textFieldOuterRadius, "ParameterShapeTypePanel" + "_textFieldOuterRadius");
		ComponentUIUtilities.setName(this.textFieldStartAngle, "ParameterShapeTypePanel" + "_textFieldStartAngle");
		ComponentUIUtilities.setName(this.textFieldEndAngle, "ParameterShapeTypePanel" + "_textFieldEndAngle");
	}

	private void initResources() {
		labelShapeType.setText(ControlsProperties.getString("String_Label_NeighbourShapeType"));
		labelUnitType.setText(ControlsProperties.getString("String_Label_NeighbourUnitType"));
		labelWidth.setText(CoreProperties.getString("String_Label_Width"));
		labelHeight.setText(ProcessProperties.getString("String_Label_Height"));
		labelRadius.setText(ControlsProperties.getString("String_Label_Radius"));
		labelInnerRadius.setText(ControlsProperties.getString("String_Label_InnerRadius"));
		labelOuterRadius.setText(ControlsProperties.getString("String_Label_OuterRadius"));
		labelStartAngle.setText(ControlsProperties.getString("String_ThemeGraphAdvance_LabelStartAngle"));
		labelEndAngle.setText(ControlsProperties.getString("String_Label_EndAngle"));
	}

	private void initLayout() {
		panel.setLayout(new GridBagLayout());

		panel.add(labelUnitType, new GridBagConstraintsHelper(0,0,1,1).setWeight(0,1).setFill(GridBagConstraints.NONE).setInsets(0,0,5,25));
		panel.add(comboBoxUnitType, new GridBagConstraintsHelper(1,0,1,1).setWeight(1,1).setFill(GridBagConstraints.HORIZONTAL).setInsets(0,20,5,0));
		panel.add(labelShapeType, new GridBagConstraintsHelper(0,1,1,1).setWeight(0,1).setFill(GridBagConstraints.NONE).setInsets(0,0,5,25));
		panel.add(comboBoxShapeType, new GridBagConstraintsHelper(1,1,1,1).setWeight(1,1).setFill(GridBagConstraints.HORIZONTAL).setInsets(0,20,5,0));
		panel.add(labelWidth, new GridBagConstraintsHelper(0, 2, 1, 1).setWeight(0,1).setFill(GridBagConstraints.NONE).setInsets(0,0,5,25).setAnchor(GridBagConstraints.WEST));
		panel.add(textFieldWidth, new GridBagConstraintsHelper(1,2,1,1).setWeight(1,1).setFill(GridBagConstraints.HORIZONTAL).setInsets(0,20,5,0));
		panel.add(labelHeight, new GridBagConstraintsHelper(0, 3, 1, 1).setWeight(0,1).setFill(GridBagConstraints.NONE).setInsets(0,0,5,25).setAnchor(GridBagConstraints.WEST));
		panel.add(textFieldHeight, new GridBagConstraintsHelper(1,3,1,1).setWeight(1,1).setFill(GridBagConstraints.HORIZONTAL).setInsets(0,20,5,0));
		panel.add(labelRadius, new GridBagConstraintsHelper(0, 4, 1, 1).setWeight(0,1).setFill(GridBagConstraints.NONE).setInsets(0,0,5,25).setAnchor(GridBagConstraints.WEST));
		panel.add(textFieldRadius, new GridBagConstraintsHelper(1,4,1,1).setWeight(1,1).setFill(GridBagConstraints.HORIZONTAL).setInsets(0,20,5,0));
		panel.add(labelInnerRadius, new GridBagConstraintsHelper(0, 5, 1, 1).setWeight(0,1).setFill(GridBagConstraints.NONE).setInsets(0,0,5,25).setAnchor(GridBagConstraints.WEST));
		panel.add(textFieldInnerRadius, new GridBagConstraintsHelper(1,5,1,1).setWeight(1,1).setFill(GridBagConstraints.HORIZONTAL).setInsets(0,20,5,0));
		panel.add(labelOuterRadius, new GridBagConstraintsHelper(0, 6, 1, 1).setWeight(0,1).setFill(GridBagConstraints.NONE).setInsets(0,0,5,25).setAnchor(GridBagConstraints.WEST));
		panel.add(textFieldOuterRadius, new GridBagConstraintsHelper(1,6,1,1).setWeight(1,1).setFill(GridBagConstraints.HORIZONTAL).setInsets(0,20,5,0));
		panel.add(labelStartAngle, new GridBagConstraintsHelper(0, 7, 1, 1).setWeight(0,1).setFill(GridBagConstraints.NONE).setInsets(0,0,5,25).setAnchor(GridBagConstraints.WEST));
		panel.add(textFieldStartAngle, new GridBagConstraintsHelper(1,7,1,1).setWeight(1,1).setFill(GridBagConstraints.HORIZONTAL).setInsets(0,20,5,0));
		panel.add(labelEndAngle, new GridBagConstraintsHelper(0, 8, 1, 1).setWeight(0,1).setFill(GridBagConstraints.NONE).setInsets(0,0,0,25).setAnchor(GridBagConstraints.WEST));
		panel.add(textFieldEndAngle, new GridBagConstraintsHelper(1,8,1,1).setWeight(1,1).setFill(GridBagConstraints.HORIZONTAL).setInsets(0,20,0,0));
		setComponentVisible(new JComponent[]{labelWidth,labelHeight,textFieldWidth,textFieldHeight});
	}

	private void initListener() {
		comboBoxShapeType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (!isSelectingItem && e.getStateChange() == ItemEvent.SELECTED) {
					isSelectingItem = true;
					if (comboBoxShapeType.getSelectedItem().equals(RECTANGLE)) {
						setComponentVisible(new JComponent[]{labelWidth,labelHeight,textFieldWidth,textFieldHeight});
					} else if (comboBoxShapeType.getSelectedItem().equals(CIRCLE)) {
						setComponentVisible(new JComponent[]{labelRadius,textFieldRadius});
					} else if (comboBoxShapeType.getSelectedItem().equals(ANNULUS)) {
						setComponentVisible(new JComponent[]{labelInnerRadius,labelOuterRadius,textFieldInnerRadius,textFieldOuterRadius});
					} else if (comboBoxShapeType.getSelectedItem().equals(WEDGE)) {
						setComponentVisible(new JComponent[]{labelRadius,labelStartAngle,labelEndAngle,textFieldRadius,textFieldStartAngle,textFieldEndAngle});
					}
					resetTextField();
					parameterShapeType.setSelectedItem(neighbourShape);
					isSelectingItem = false;
				}
			}
		});
		textFieldWidth.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				change();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				change();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				change();
			}

			private void change() {
				if (!isSelectingItem && !StringUtilities.isNullOrEmpty(textFieldWidth.getText())) {
					isSelectingItem = true;
					resetNeighbourShape();
					isSelectingItem = false;
				}
			}
		});
		textFieldHeight.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				change();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				change();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				change();
			}

			private void change() {
				if (!isSelectingItem && !StringUtilities.isNullOrEmpty(textFieldHeight.getText())) {
					isSelectingItem = true;
					resetNeighbourShape();
					isSelectingItem = false;
				}
			}
		});
		textFieldRadius.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				change();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				change();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				change();
			}

			private void change() {
				if (!isSelectingItem && !StringUtilities.isNullOrEmpty(textFieldRadius.getText())) {
					isSelectingItem = true;
					resetNeighbourShape();
					isSelectingItem = false;
				}
			}
		});
		textFieldInnerRadius.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				change();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				change();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				change();
			}

			private void change() {
				if (!isSelectingItem && !StringUtilities.isNullOrEmpty(textFieldInnerRadius.getText())) {
					isSelectingItem = true;
					resetNeighbourShape();
					try {
						textFieldOuterRadius.setMinValue(Double.valueOf(textFieldInnerRadius.getText()));
					} catch (Exception e) {

					}
					isSelectingItem = false;
				}
			}
		});
		textFieldOuterRadius.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				change();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				change();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				change();
			}

			private void change() {
				if (!isSelectingItem && !StringUtilities.isNullOrEmpty(textFieldOuterRadius.getText())) {
					isSelectingItem = true;
					resetNeighbourShape();
					try {
						textFieldInnerRadius.setMaxValue(Double.valueOf(textFieldOuterRadius.getText()));
					} catch (Exception e) {

					}
					isSelectingItem = false;
				}
			}
		});
		textFieldStartAngle.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				change();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				change();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				change();
			}

			private void change() {
				textFieldStartAngle.setMaxValue(Double.valueOf(textFieldEndAngle.getBackUpValue().toString()));
				if (!isSelectingItem && !StringUtilities.isNullOrEmpty(textFieldStartAngle.getText())) {
					isSelectingItem = true;
					resetNeighbourShape();
					isSelectingItem = false;
				}
			}
		});
		textFieldEndAngle.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				change();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				change();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				change();
			}

			private void change() {
				textFieldEndAngle.setMinValue(Double.valueOf(textFieldStartAngle.getBackUpValue().toString()));
				if (!isSelectingItem && !StringUtilities.isNullOrEmpty(textFieldEndAngle.getText())) {
					isSelectingItem = true;
					resetNeighbourShape();
					isSelectingItem = false;
				}
			}
		});
		comboBoxUnitType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (!isSelectingItem && e.getStateChange() == ItemEvent.SELECTED) {
					isSelectingItem = true;
					resetTextField();
					parameterShapeType.setSelectedItem(neighbourShape);
					isSelectingItem = false;
				}
			}
		});
	}

	private void initComponentState() {
		comboBoxUnitType.addItem(UNIT_TYPE_CELL);
		comboBoxUnitType.addItem(UNIT_TYPE_MAP);
		comboBoxShapeType.addItem(RECTANGLE);
		comboBoxShapeType.addItem(CIRCLE);
		comboBoxShapeType.addItem(ANNULUS);
		comboBoxShapeType.addItem(WEDGE);
		textFieldWidth.setText("3");
		textFieldHeight.setText("3");
		textFieldRadius.setText("3");
		textFieldInnerRadius.setText("1");
		textFieldOuterRadius.setText("3");
		textFieldStartAngle.setText("0");
		textFieldEndAngle.setText("360");
		textFieldWidth.setMinValue(0);
		textFieldHeight.setMinValue(0);
		textFieldRadius.setMinValue(0);
		textFieldInnerRadius.setMinValue(0);
		textFieldOuterRadius.setMinValue(0);
		textFieldStartAngle.setMinValue(0);
		textFieldEndAngle.setMinValue(0);
		textFieldEndAngle.setMaxValue(360);
		textFieldStartAngle.setMaxValue(360);
		resetNeighbourShape();
	}

	private void setComponentVisible(JComponent[] components) {
		labelWidth.setVisible(false);
		labelHeight.setVisible(false);
		labelRadius.setVisible(false);
		labelInnerRadius.setVisible(false);
		labelOuterRadius.setVisible(false);
		labelStartAngle.setVisible(false);
		labelEndAngle.setVisible(false);
		textFieldWidth.setVisible(false);
		textFieldHeight.setVisible(false);
		textFieldRadius.setVisible(false);
		textFieldInnerRadius.setVisible(false);
		textFieldOuterRadius.setVisible(false);
		textFieldStartAngle.setVisible(false);
		textFieldEndAngle.setVisible(false);
		for (JComponent component : components) {
			component.setVisible(true);
		}
	}

	private void resetNeighbourShape() {
		if (comboBoxShapeType.getSelectedItem().equals(RECTANGLE)) {
			neighbourShape = new NeighbourShapeRectangle();
			((NeighbourShapeRectangle) neighbourShape).setWidth(Double.valueOf(textFieldWidth.getBackUpValue()));
			((NeighbourShapeRectangle) neighbourShape).setHeight(Double.valueOf(textFieldHeight.getBackUpValue()));
		} else if (comboBoxShapeType.getSelectedItem().equals(CIRCLE)) {
			neighbourShape = new NeighbourShapeCircle();
			((NeighbourShapeCircle) neighbourShape).setRadius(Double.valueOf(textFieldRadius.getBackUpValue()));
		} else if (comboBoxShapeType.getSelectedItem().equals(ANNULUS)) {
			neighbourShape = new NeighbourShapeAnnulus();
			((NeighbourShapeAnnulus) neighbourShape).setInnerRadius(Double.valueOf(textFieldInnerRadius.getBackUpValue()));
			((NeighbourShapeAnnulus) neighbourShape).setOuterRadius(Double.valueOf(textFieldOuterRadius.getBackUpValue()));
		} else if (comboBoxShapeType.getSelectedItem().equals(WEDGE)) {
			neighbourShape = new NeighbourShapeWedge();
			((NeighbourShapeWedge) neighbourShape).setRadius(Double.valueOf(textFieldRadius.getBackUpValue()));
			((NeighbourShapeWedge) neighbourShape).setStartAngle(Double.valueOf(textFieldStartAngle.getBackUpValue()));
			((NeighbourShapeWedge) neighbourShape).setEndAngle(Double.valueOf(textFieldEndAngle.getBackUpValue()));
		}
		if (comboBoxUnitType.getSelectedItem().equals(UNIT_TYPE_CELL)) {
			neighbourShape.setUnitType(NeighbourUnitType.CELL);
		} else {
			neighbourShape.setUnitType(NeighbourUnitType.MAP);
		}
		parameterShapeType.setSelectedItem(neighbourShape);
	}

	private void resetTextField() {
		NeighbourUnitType unitType =null;
		if (comboBoxUnitType.getSelectedItem().equals(UNIT_TYPE_CELL)) {
			unitType = NeighbourUnitType.CELL;
		}else {
			unitType = NeighbourUnitType.MAP;
		}
		if (dataset != null) {
			Rectangle2D bounds = dataset.getBounds();
			double max = bounds.getWidth() > bounds.getHeight() ? bounds.getWidth() : bounds.getHeight();
			double min = bounds.getWidth() < bounds.getHeight() ? bounds.getWidth() : bounds.getHeight();
			boolean isPrjEarth = dataset.getPrjCoordSys().getType().equals(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
			boolean isVector = dataset instanceof DatasetVector;
			boolean isUnitMap = unitType.equals(NeighbourUnitType.MAP);
			if (comboBoxShapeType.getSelectedItem().equals(RECTANGLE)) {
				textFieldWidth.setText("" + ((isUnitMap && (isVector || isPrjEarth)) ? bounds.getWidth() / 20 : "3"));
				textFieldHeight.setText("" + ((isUnitMap && (isVector || isPrjEarth) ? bounds.getHeight() / 20 : "3")));
			} else if (comboBoxShapeType.getSelectedItem().equals(CIRCLE)) {
				textFieldRadius.setText("" + ((isUnitMap && (isVector || isPrjEarth)) ? max / 20 : "3"));
			} else if (comboBoxShapeType.getSelectedItem().equals(ANNULUS)) {
				String inner = "" + ((isUnitMap && (isVector || isPrjEarth)) ? min / 20 : "1");
				String outer = "" + ((isUnitMap && (isVector || isPrjEarth)) ? max / 20 : "3");
				textFieldInnerRadius.setMaxValue(Double.parseDouble(outer));
				textFieldOuterRadius.setMinValue(Double.parseDouble(inner));
				textFieldInnerRadius.setText(inner);
				textFieldOuterRadius.setText(outer);
			} else {
				textFieldRadius.setText("" + ((isUnitMap && (isVector || isPrjEarth)) ? max / 20 : "3"));
				textFieldStartAngle.setText("0");
				textFieldEndAngle.setText("360");
			}
			if (!isVector) {
				textFieldWidth.setMaxValue(getMaxRange());
				textFieldHeight.setMaxValue(getMaxRange());
				textFieldOuterRadius.setMaxValue(getMaxRange());
				textFieldRadius.setMaxValue(getMaxRange());
			}
		}
		resetNeighbourShape();
	}

	private double getMaxRange() {
		if (dataset != null && dataset instanceof DatasetGrid) {
			DatasetGrid datasetGrid = (DatasetGrid) dataset;
			return comboBoxUnitType.getSelectedItem().equals(UNIT_TYPE_CELL) ? Math.min(datasetGrid.getWidth(), datasetGrid.getHeight()) / 2 : Math.min(datasetGrid.getBounds().getWidth(), datasetGrid.getBounds().getHeight())/2;
		}
		return 0;
	}

	@Override
	public void fieldConstraintChanged(FieldConstraintChangedEvent event) {
		if (event.getFieldName().equals(ParameterShapeType.DATASET_FIELD_NAME)) {
			this.dataset = parameterShapeType.getDataset();
			resetTextField();
			parameterShapeType.setSelectedItem(neighbourShape);
		}
	}
}
