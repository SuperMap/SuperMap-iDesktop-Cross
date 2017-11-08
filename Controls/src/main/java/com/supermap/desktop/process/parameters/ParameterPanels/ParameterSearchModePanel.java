package com.supermap.desktop.process.parameters.ParameterPanels;

import com.supermap.analyst.spatialanalyst.SearchMode;
import com.supermap.data.Dataset;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.ParameterSearchModeInfo;
import com.supermap.desktop.process.parameter.events.FieldConstraintChangedEvent;
import com.supermap.desktop.process.parameter.interfaces.AbstractParameter;
import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.interfaces.IParameterPanel;
import com.supermap.desktop.process.parameter.interfaces.ParameterPanelDescribe;
import com.supermap.desktop.process.parameter.ipls.ParameterSearchMode;
import com.supermap.desktop.process.util.ParameterUtil;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.ui.controls.TextFields.NumTextFieldLegit;
import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

/**
 * Created by xie on 2017/2/17.
 */
@ParameterPanelDescribe(parameterPanelType = ParameterType.SEARCH_MODE)
public class ParameterSearchModePanel extends SwingPanel implements IParameterPanel {
    private JLabel labelSearchModel;
    private JRadioButton radioSearchModelCount;
    private JRadioButton radioSearchModelRadius;
    private JRadioButton radioSearchModelQuadTree;
    private JLabel labelMaxRadius;
    private JLabel labelMaxPointCount;
    private JLabel labelSearchCount;
    private NumTextFieldLegit textFieldMaxRadius;
    private NumTextFieldLegit textFieldMaxPointCount;
    private NumTextFieldLegit textFieldSearchCount;
    private ButtonGroup buttonGroup = new ButtonGroup();

    private boolean isSelectingItem = false;
    private ParameterSearchMode parameterSearchMode;
    private ParameterSearchModeInfo info;
    private Dataset dataset;

	public ParameterSearchModePanel(IParameter parameterSearchMode) {
		super(parameterSearchMode);
		this.parameterSearchMode = (ParameterSearchMode) parameterSearchMode;
		this.info = (ParameterSearchModeInfo) this.parameterSearchMode.getSelectedItem();
        this.dataset = ((ParameterSearchMode) parameterSearchMode).getDataset();
        initComponents();
        initLayout();
        initListener();
    }

    private void initListener() {
        this.radioSearchModelCount.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSelectingItem) {
                    isSelectingItem = true;
                    if (radioSearchModelCount.isSelected()) {
                        if (null == info) {
                            info = new ParameterSearchModeInfo();
                        }
                        info.searchMode = SearchMode.KDTREE_FIXED_COUNT;
                        radioChange(info.searchMode);
                        parameterSearchMode.setSelectedItem(info);
                    }
                    isSelectingItem = false;
                }
            }
        });
        this.radioSearchModelRadius.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSelectingItem) {
                    isSelectingItem = true;
                    if (radioSearchModelRadius.isSelected()) {
                        if (null == info) {
                            info = new ParameterSearchModeInfo();
                        }
                        info.searchMode = SearchMode.KDTREE_FIXED_RADIUS;
                        radioChange(info.searchMode);
                        parameterSearchMode.setSelectedItem(info);
                    }
                    isSelectingItem = false;
                }
            }
        });
        this.radioSearchModelQuadTree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isSelectingItem) {
                    isSelectingItem = true;
                    if (radioSearchModelQuadTree.isSelected()) {
                        if (null == info) {
                            info = new ParameterSearchModeInfo();
                        }
                        info.searchMode = SearchMode.QUADTREE;
                        radioChange(info.searchMode);
                        parameterSearchMode.setSelectedItem(info);
                    }
                    isSelectingItem = false;
                }
            }
        });
        this.textFieldMaxRadius.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changeRadius();
            }

            private void changeRadius() {
                if (!isSelectingItem && !StringUtilities.isNullOrEmpty(textFieldMaxRadius.getText())) {
                    isSelectingItem = true;
                    if (null == info) {
                        info = new ParameterSearchModeInfo();
                    }
                    info.searchRadius = Double.parseDouble(textFieldMaxRadius.getBackUpValue());
                    isSelectingItem = false;
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changeRadius();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changeRadius();
            }
        });
        this.textFieldSearchCount.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changeSearchCount();
            }

            private void changeSearchCount() {
                if (!isSelectingItem && !StringUtilities.isNullOrEmpty(textFieldSearchCount.getText())) {
                    isSelectingItem = true;
                    if (null == info) {
                        info = new ParameterSearchModeInfo();
                    }
                    info.expectedCount = Integer.parseInt(textFieldSearchCount.getBackUpValue());
                    isSelectingItem = false;
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changeSearchCount();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changeSearchCount();
            }
        });
        this.textFieldMaxPointCount.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changeSearchCount();
            }

            private void changeSearchCount() {
                if (!isSelectingItem && !StringUtilities.isNullOrEmpty(textFieldMaxPointCount.getText())) {
                    isSelectingItem = true;
                    if (null == info) {
                        info = new ParameterSearchModeInfo();
                    }
                    info.maxPointCount = Integer.parseInt(textFieldMaxPointCount.getBackUpValue());
                    isSelectingItem = false;
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changeSearchCount();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changeSearchCount();
            }
        });
        parameterSearchMode.addPropertyListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (!isSelectingItem && Objects.equals(evt.getPropertyName(), AbstractParameter.PROPERTY_VALE)) {
                    isSelectingItem = true;
                    if (null != evt.getNewValue() && evt.getNewValue() instanceof ParameterSearchModeInfo) {
                        ParameterSearchModeInfo selectItem = (ParameterSearchModeInfo) evt.getNewValue();
                        radioChange(selectItem.searchMode);
                    }
                    isSelectingItem = false;
                }
            }
        });
    }

    private void initComponents() {
        this.labelSearchModel = new JLabel();
        this.labelSearchModel.setText(ControlsProperties.getString("String_SearchMode"));
        this.radioSearchModelCount = new JRadioButton();
        this.radioSearchModelRadius = new JRadioButton();
        this.radioSearchModelQuadTree = new JRadioButton();
        this.radioSearchModelCount.setText(ProcessProperties.getString("String_SearchModelCount"));
        this.radioSearchModelRadius.setText(ProcessProperties.getString("String_SearchModelRadius"));
        this.radioSearchModelQuadTree.setText(ProcessProperties.getString("String_SearchModelQuadtree"));
        this.labelMaxRadius = new JLabel();
        this.labelSearchCount = new JLabel();
        this.labelMaxPointCount = new JLabel();
        this.textFieldMaxRadius = new NumTextFieldLegit();
        this.textFieldSearchCount = new NumTextFieldLegit();
        this.textFieldMaxPointCount = new NumTextFieldLegit();
	    labelMaxPointCount.setText(CoreProperties.getString("String_InterpolationAnalyst_QuadTree_Max_IntePolate_Point"));
	    buttonGroup.add(this.radioSearchModelCount);
        buttonGroup.add(this.radioSearchModelRadius);
        buttonGroup.add(this.radioSearchModelQuadTree);
        if (null != info) {
            radioChange(info.searchMode);
        }

        textFieldMaxRadius.setMinValue(0);
        textFieldSearchCount.setMinValue(1);
        textFieldMaxPointCount.setMinValue(4);
        textFieldMaxPointCount.setIncludeMin(false);
	    ComponentUIUtilities.setName(this.radioSearchModelCount, parameter.getDescribe() + "_radio");
	    ComponentUIUtilities.setName(this.radioSearchModelRadius, parameter.getDescribe() + "_radio1");
	    ComponentUIUtilities.setName(this.radioSearchModelQuadTree, parameter.getDescribe() + "_radio2");
	    ComponentUIUtilities.setName(this.textFieldMaxRadius, parameter.getDescribe() + "_textField");
	    ComponentUIUtilities.setName(this.textFieldMaxPointCount, parameter.getDescribe() + "_textField1");
	    ComponentUIUtilities.setName(this.textFieldSearchCount, parameter.getDescribe() + "_textField2");
    }

    private void initLayout() {
	    panel.setLayout(new GridBagLayout());
	    this.labelSearchModel.setPreferredSize(ParameterUtil.LABEL_DEFAULT_SIZE);
        this.labelMaxRadius.setPreferredSize(ParameterUtil.LABEL_DEFAULT_SIZE);
        this.labelSearchCount.setPreferredSize(ParameterUtil.LABEL_DEFAULT_SIZE);
	    panel.add(this.labelSearchModel, new GridBagConstraintsHelper(0, 0, 1, 1).setWeight(0, 1));
        JPanel panelRadio = new JPanel();
        panelRadio.add(radioSearchModelCount);
        panelRadio.add(radioSearchModelRadius);
        if (parameterSearchMode.isQuadTree()) {
            panelRadio.add(radioSearchModelQuadTree);
        }
        panel.add(panelRadio, new GridBagConstraintsHelper(1, 0, 1, 1).setAnchor(GridBagConstraints.CENTER).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 1).setInsets(5, 5, 0, 0));
        panel.add(this.labelMaxRadius, new GridBagConstraintsHelper(0, 1, 1, 1).setWeight(0, 1).setInsets(5, 0, 0, 0));
	    panel.add(this.textFieldMaxRadius, new GridBagConstraintsHelper(1, 1, 1, 1).setAnchor(GridBagConstraints.CENTER).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 1).setInsets(5, 5, 0, 0));
	    panel.add(this.labelMaxPointCount, new GridBagConstraintsHelper(0, 2, 1, 1).setAnchor(GridBagConstraints.WEST).setWeight(0, 1).setInsets(5, 0, 0, 0));
	    panel.add(this.textFieldMaxPointCount, new GridBagConstraintsHelper(1, 2, 1, 1).setAnchor(GridBagConstraints.CENTER).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 1).setInsets(5, 5, 0, 0));
        panel.add(this.labelSearchCount, new GridBagConstraintsHelper(0, 3, 1, 1).setWeight(0, 1).setInsets(5, 0, 0, 0));
        panel.add(this.textFieldSearchCount, new GridBagConstraintsHelper(1, 3, 1, 1).setAnchor(GridBagConstraints.CENTER).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 1).setInsets(5, 5, 0, 0));
    }

    private void radioChange(SearchMode mode) {
        if (mode == SearchMode.KDTREE_FIXED_COUNT) {
            radioSearchModelCount.setSelected(true);
	        labelMaxRadius.setText(CoreProperties.getString("String_MaxRadius"));
	        labelSearchCount.setText(CoreProperties.getString("String_SearchCount"));
	        labelMaxRadius.setVisible(true);
            labelMaxPointCount.setVisible(false);
            textFieldMaxRadius.setVisible(true);
            textFieldMaxPointCount.setVisible(false);
            textFieldMaxRadius.setText("0");
            textFieldSearchCount.setText("12");
            textFieldSearchCount.setMinValue(1);
        } else if (mode == SearchMode.KDTREE_FIXED_RADIUS) {
            radioSearchModelRadius.setSelected(true);
	        labelMaxRadius.setText(CoreProperties.getString("String_SearchRadius"));
	        labelSearchCount.setText(CoreProperties.getString("String_MinCount"));
	        labelMaxRadius.setVisible(true);
            labelMaxPointCount.setVisible(false);
            textFieldMaxRadius.setVisible(true);
            textFieldMaxPointCount.setVisible(false);
            if (dataset != null) {
                double width = dataset.getBounds().getWidth();
                double height = dataset.getBounds().getHeight();
                textFieldMaxRadius.setText((int) ((width < height ? width : height) / 5) + "");
            }
            textFieldSearchCount.setText("5");
            textFieldSearchCount.setMinValue(2);
            textFieldSearchCount.setMaxValue(12);
        } else if (mode == SearchMode.QUADTREE) {
            radioSearchModelQuadTree.setSelected(true);
	        labelSearchCount.setText(CoreProperties.getString("String_InterpolationAnalyst_QuadTree_MaxPoint_InBlock"));
	        labelMaxRadius.setVisible(false);
            labelMaxPointCount.setVisible(true);
            textFieldMaxRadius.setVisible(false);
            textFieldMaxPointCount.setVisible(true);
            textFieldMaxPointCount.setText("20");
            textFieldSearchCount.setText("5");
            textFieldSearchCount.setMinValue(1);
        }
    }

    @Override
    public void fieldConstraintChanged(FieldConstraintChangedEvent event) {
        if (event.getFieldName().equals(ParameterSearchMode.DATASET_FIELD_NAME)) {
            this.dataset = parameterSearchMode.getDataset();
            radioChange(info.searchMode);
        }
    }
}
