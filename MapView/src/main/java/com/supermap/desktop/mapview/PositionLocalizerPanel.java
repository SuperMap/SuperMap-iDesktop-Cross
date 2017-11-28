package com.supermap.desktop.mapview;

import com.supermap.desktop.Interface.IFormMap;
import com.supermap.desktop.controls.utilities.ControlsResources;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Created by ChenS on 2017/11/28 0028.
 */
public class PositionLocalizerPanel extends JPanel {
    //region Component
    JLabel labelCondition;
    JTextField textFieldCondition;
    JButton buttonFinder;
    JCheckBox checkBoxExact;
    JLabel labelLayer;
    JComboBox comboBoxLayer;
    JLabel labelField;
    JComboBox comboBoxField;
    JTable tableResult;
    //endregion

    private transient IFormMap formMap;


    public PositionLocalizerPanel() {
        initComponent();
        initLayout();
        registerListener();
    }

    private void initComponent() {
        labelCondition = new JLabel(CoreProperties.getString("String_QueryCondition"));
        textFieldCondition = new JTextField();
        buttonFinder = new JButton();
        buttonFinder.setIcon(ControlsResources.getIcon("/controlsresources/SortType/Image_FindFiles.png"));
        checkBoxExact = new JCheckBox(CoreProperties.getString("String_FindExacted"));
        labelLayer = new JLabel(CoreProperties.getString("String_Label_ChooseLayer"));
        comboBoxLayer = new JComboBox();
        labelField = new JLabel(CoreProperties.getString("String_QueryField"));
        comboBoxField = new JComboBox();
        initTable();
    }

    private void initLayout() {
        this.setLayout(new GridBagLayout());
        this.add(labelCondition, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(10, 10, 5, 5));
        this.add(textFieldCondition, new GridBagConstraintsHelper(1, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0).setInsets(10, 0, 5, 5));
        this.add(buttonFinder, new GridBagConstraintsHelper(2, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(10, 0, 5, 5));
        this.add(checkBoxExact, new GridBagConstraintsHelper(3, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(10, 0, 5, 10));
        this.add(labelLayer, new GridBagConstraintsHelper(0, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 10, 5, 10));
        this.add(comboBoxLayer, new GridBagConstraintsHelper(1, 1, 3, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0).setInsets(0, 0, 5, 10));
        this.add(labelField, new GridBagConstraintsHelper(0, 2, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 10, 5, 10));
        this.add(comboBoxField, new GridBagConstraintsHelper(1, 2, 3, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0).setInsets(0, 0, 5, 10));
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(tableResult);
        this.add(scrollPane, new GridBagConstraintsHelper(0, 3, 4, 1).setAnchor(GridBagConstraints.WEST).setWeight(1, 1).setFill(GridBagConstraints.BOTH).setInsets(0, 10, 10, 10));
    }

    private void registerListener() {

    }

    private void removeListener() {

    }

    private void initTable() {
        tableResult = new JTable();
    }

    public void setFormMap(IFormMap forMap) {

    }
}
