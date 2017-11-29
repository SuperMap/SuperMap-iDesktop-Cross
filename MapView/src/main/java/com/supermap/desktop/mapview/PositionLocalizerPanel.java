package com.supermap.desktop.mapview;

import com.supermap.desktop.Interface.IFormMap;
import com.supermap.desktop.controls.utilities.ControlsResources;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by ChenS on 2017/11/28 0028.
 */
public class PositionLocalizerPanel extends JPanel {
    //region Component
    private JLabel labelCondition;
    private JTextField textFieldCondition;
    private JButton buttonFinder;
    private JCheckBox checkBoxExact;
    private JLabel labelLayer;
    private JComboBox comboBoxLayer;
    private JLabel labelField;
    private JComboBox<String> comboBoxField;
    private JTable tableResult;
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
        comboBoxField = new JComboBox<>();
        initTable();
    }

    private void initLayout() {
        this.setLayout(new GridBagLayout());
        this.add(labelCondition, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(10, 10, 5, 5));
        this.add(textFieldCondition, new GridBagConstraintsHelper(1, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0).setInsets(10, 0, 5, 0));
        this.add(buttonFinder, new GridBagConstraintsHelper(2, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(10, 0, 5, 5));
        this.add(checkBoxExact, new GridBagConstraintsHelper(3, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(10, 0, 5, 10));
        this.add(labelLayer, new GridBagConstraintsHelper(0, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 10, 5, 10));
        this.add(comboBoxLayer, new GridBagConstraintsHelper(1, 1, 3, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0).setInsets(0, 0, 5, 10));
        this.add(labelField, new GridBagConstraintsHelper(0, 2, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(0, 10, 5, 10));
        this.add(comboBoxField, new GridBagConstraintsHelper(1, 2, 3, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0).setInsets(0, 0, 5, 10));
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(tableResult);
        scrollPane.setBorder(new TitledBorder(CoreProperties.getString("String_ResultOfSearching")));
        this.add(scrollPane, new GridBagConstraintsHelper(0, 3, 4, 1).setAnchor(GridBagConstraints.WEST).setWeight(1, 1).setFill(GridBagConstraints.BOTH).setInsets(0, 10, 10, 10));
    }

    private void registerListener() {

    }

    private void removeListener() {

    }

    private void initTable() {
        tableResult = new JTable(new LayerNameWithIDTableModel());
        tableResult.setRowHeight(20);
        tableResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public void setFormMap(IFormMap forMap) {
        this.formMap = forMap;
    }

    private class LayerNameWithIDTableModel extends DefaultTableModel {
        ArrayList<LayerNameWithID> nameWithIDList = new ArrayList<>();
        static final int COLUMN_SMID = 0;
        static final int COLUMN_LAYER_NAME = 1;
        String[] columnNames = new String[]{"SmID", MapViewProperties.getString("String_TerrainUniformLayer")};

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public int getRowCount() {
            return nameWithIDList == null ? 0 : nameWithIDList.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == COLUMN_SMID) {
                return Integer.class;
            } else {
                return String.class;
            }
        }

        public void removeAll() {
            int size = nameWithIDList.size();
            if (size > 0) {
                this.nameWithIDList.clear();
                fireTableRowsDeleted(0, size - 1);
            }
        }

        public void addItem(String name, int id) {
            if (this.nameWithIDList == null) {
                this.nameWithIDList = new ArrayList<>();
            }
            this.nameWithIDList.add(new LayerNameWithID(id, name));
            fireTableDataChanged();
        }
    }

    private class LayerNameWithID {
        int id;
        String name;

        public LayerNameWithID(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
