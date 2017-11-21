package com.supermap.desktop.dialog;

import com.supermap.desktop.mapview.CachePlayer.CachePlayerBar;
import com.supermap.desktop.mapview.CachePlayer.CacheWithVersion;
import com.supermap.desktop.mapview.MapViewProperties;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.ui.controls.SmDialog;
import com.supermap.desktop.ui.controls.SortTable.SmSortTable;
import com.supermap.desktop.ui.controls.TextFields.NumTextFieldLegit;
import com.supermap.desktop.ui.controls.button.SmButton;
import com.supermap.mapping.LayerCache;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by ChenS on 2017/11/8 0008.
 */
public class DialogCachePlayerSetting extends SmDialog {
    private CachePlayerBar cachePlayerBar;

    private SmSortTable table;
    private JLabel labelPlayInterval;
    private NumTextFieldLegit textFieldPlayInterval;
    private JLabel labelEffectsInterval;
    private NumTextFieldLegit textFieldEffectsInterval;
    private JCheckBox checkBoxShowBar;
    private JCheckBox checkBoxEffectsEnable;
    private SmButton buttonOK;
    private SmButton buttonCancel;


    public DialogCachePlayerSetting(CachePlayerBar cachePlayerBar) {
        this.cachePlayerBar = cachePlayerBar;
        this.setSize(620, 420);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        initComponent();
        initLayout();
        registerListener();
    }

    private void initComponent() {
        initTable();
        labelPlayInterval = new JLabel();
        labelEffectsInterval = new JLabel();
        textFieldPlayInterval = new NumTextFieldLegit();
        textFieldEffectsInterval = new NumTextFieldLegit();
        checkBoxEffectsEnable = new JCheckBox();
        checkBoxShowBar = new JCheckBox();
        buttonOK = new SmButton();
        buttonCancel = new SmButton();

        textFieldEffectsInterval.setMinValue(0);
        textFieldPlayInterval.setMinValue(0);
        textFieldPlayInterval.setIncludeMin(false);
        textFieldEffectsInterval.setIncludeMin(false);
        textFieldEffectsInterval.setBit(0);
        textFieldEffectsInterval.setText(cachePlayerBar.getEffectsInterval() + "");
        textFieldPlayInterval.setBit(0);
        textFieldPlayInterval.setText(cachePlayerBar.getInterval() + "");
        checkBoxEffectsEnable.setSelected(cachePlayerBar.isEffects());
        checkBoxShowBar.setSelected(true);
        checkBoxEffectsEnable.setSelected(true);

        initResources();
    }

    private void initResources() {
        this.setTitle(MapViewProperties.getString("String_MultiCachePlayerSetting"));
        labelPlayInterval.setText(MapViewProperties.getString("String_Label_PlayInterval"));
        labelEffectsInterval.setText(MapViewProperties.getString("String_Label_EffectsInterval"));
        checkBoxShowBar.setText(MapViewProperties.getString("String_ShowBar"));
        checkBoxEffectsEnable.setText(MapViewProperties.getString("String_EffectsEnable"));
        buttonOK.setText(CoreProperties.getString(CoreProperties.OK));
        buttonCancel.setText(CoreProperties.getString(CoreProperties.Cancel));
        checkBoxShowBar.setToolTipText(MapViewProperties.getString("String_Tip_YouCanSetPlayerBarVisibleAgainInPopMenu"));
    }

    private void registerListener() {
        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDialogResult(DialogResult.OK);
                apply();
                dispose();
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDialogResult(DialogResult.CANCEL);
                dispose();
            }
        });
    }

    private void initLayout() {
        this.setLayout(new GridBagLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(table);
        scrollPane.setPreferredSize(new Dimension(480, 200));
        panel.add(scrollPane, new GridBagConstraintsHelper(0, 0, 3, 1).setAnchor(GridBagConstraints.CENTER).setFill(GridBagConstraints.BOTH).setWeight(1, 1).setInsets(5, 10, 5, 10));
        panel.add(labelPlayInterval, new GridBagConstraintsHelper(0, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(5, 10, 5, 10));
        panel.add(textFieldPlayInterval, new GridBagConstraintsHelper(1, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0).setInsets(5, 0, 5, 5));
        panel.add(new JLabel("s"), new GridBagConstraintsHelper(2, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(5, 0, 5, 10));
        panel.add(labelEffectsInterval, new GridBagConstraintsHelper(0, 2, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(5, 10, 5, 10));
        panel.add(textFieldEffectsInterval, new GridBagConstraintsHelper(1, 2, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0).setInsets(5, 0, 5, 5));
        panel.add(new JLabel("s"), new GridBagConstraintsHelper(2, 2, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(5, 0, 5, 10));
        panel.add(checkBoxShowBar, new GridBagConstraintsHelper(0, 3, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(5, 10, 5, 10));
        panel.add(checkBoxEffectsEnable, new GridBagConstraintsHelper(0, 4, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(5, 10, 5, 10));
        JPanel panelButton = new JPanel();
        panelButton.setLayout(new GridBagLayout());
        panelButton.add(this.buttonOK, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.EAST).setFill(GridBagConstraints.NONE).setInsets(0, 15, 10, 5).setWeight(1, 1));
        panelButton.add(this.buttonCancel, new GridBagConstraintsHelper(1, 0, 1, 1).setAnchor(GridBagConstraints.EAST).setFill(GridBagConstraints.NONE).setInsets(0, 0, 10, 5).setWeight(0, 1));
        this.add(panel, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.BOTH).setWeight(1, 1).setInsets(5, 10, 5, 10));
        this.add(panelButton, new GridBagConstraintsHelper(0, 1, 1, 1).setAnchor(GridBagConstraints.EAST).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0).setInsets(0, 10, 5, 10));
    }

    private void initTable() {
        table = new SmSortTable();
        table.setModel(new CachePlayerSettingTableModel(cachePlayerBar.getLayerCaches(), cachePlayerBar.getPlayList()));
        table.getColumnModel().getColumn(0).setMaxWidth(100);
        table.setDefaultRenderer(LayerCache.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel jLabel = new JLabel();
                jLabel.setText(((LayerCache) value).getName());
                jLabel.setOpaque(true);
                jLabel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                jLabel.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                return jLabel;
            }
        });
    }

    private void apply() {
        cachePlayerBar.setInterval(Integer.parseInt(textFieldPlayInterval.getText()));
        cachePlayerBar.setEffectsTime(Integer.parseInt(textFieldEffectsInterval.getText()));
        cachePlayerBar.setEffects(checkBoxEffectsEnable.isSelected());
        cachePlayerBar.setVisible(checkBoxShowBar.isSelected());
        ArrayList<TableData> tableDatas = ((CachePlayerSettingTableModel) table.getModel()).getTableDatas();
        ArrayList<CacheWithVersion> playList = cachePlayerBar.getPlayList();
        playList.clear();
        for (TableData tableData : tableDatas) {
            if (tableData.isPlay) {
                playList.add(tableData.cacheWithVersion);
            }
        }
        cachePlayerBar.getProgressBar().setProgress(0);
    }

    private class CachePlayerSettingTableModel extends DefaultTableModel {
        private String[] columnHeaders = new String[]{
                MapViewProperties.getString("String_IsPlay"),
                MapViewProperties.getString("String_Cache_Version"),
                MapViewProperties.getString("String_Cache_Layer")
        };
        private ArrayList<TableData> tableDatas = new ArrayList<>();

        public CachePlayerSettingTableModel(ArrayList<LayerCache> layerCaches, ArrayList<CacheWithVersion> cacheWithVersions) {
            for (LayerCache layerCache : layerCaches) {
                for (int i = 0; i < layerCache.getVersions().size(); i++) {
                    CacheWithVersion cacheWithVersion = new CacheWithVersion(layerCache, layerCache.getVersions().get(i), layerCache.getDescriptions().get(i));
                    tableDatas.add(new TableData(cacheWithVersion));
                }
            }
            for (TableData tableData : tableDatas) {
                CacheWithVersion tableDataVersion = tableData.cacheWithVersion;
                for (CacheWithVersion cacheWithVersion : cacheWithVersions) {
                    if (cacheWithVersion.getLayerCache().equals(tableDataVersion.getLayerCache()) &&
                            cacheWithVersion.getDescription().equals(tableDataVersion.getDescription())) {
                        tableData.isPlay = true;
                        break;
                    }
                }
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 0;
        }

        @Override
        public int getRowCount() {
            return tableDatas == null ? 0 : tableDatas.size();
        }

        @Override
        public int getColumnCount() {
            return columnHeaders == null ? 0 : columnHeaders.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnHeaders[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return Boolean.class;
            } else if (columnIndex == 1) {
                return String.class;
            } else {
                return LayerCache.class;
            }
        }

        @Override
        public Object getValueAt(int row, int column) {
            switch (column) {
                case 0:
                    return tableDatas.get(row).isPlay;
                case 1:
                    return tableDatas.get(row).cacheWithVersion.getDescription();
                case 2:
                    return tableDatas.get(row).cacheWithVersion.getLayerCache();
            }
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            if (column == 0) {
                tableDatas.get(row).isPlay = (boolean) aValue;
            }
            fireTableCellUpdated(row, column);
        }

        public ArrayList<TableData> getTableDatas() {
            return tableDatas;
        }
    }

    private class TableData {
        CacheWithVersion cacheWithVersion;
        boolean isPlay;

        public TableData(CacheWithVersion cacheWithVersion) {
            this.cacheWithVersion = cacheWithVersion;
            this.isPlay = false;
        }
    }
}
