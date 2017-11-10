package com.supermap.desktop.mapview.layer.propertycontrols;

import com.supermap.desktop.Application;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.mapview.MapViewProperties;
import com.supermap.desktop.mapview.layer.propertymodel.LayerCachePropertyModel;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by ChenS on 2017/11/8 0008.
 */
public class LayerCachePropertyControl extends AbstractLayerPropertyControl {
    private JLabel label;
    private JComboBox<String> comboBox;

    private ItemListener itemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED && comboBox.getSelectedItem() != null) {
                getLayerPropertyModel().setCurrentVersion(comboBox.getSelectedItem().toString());
                checkChanged();
            }
        }
    };

    public LayerCachePropertyControl() {

    }

    @Override
    public LayerCachePropertyModel getLayerPropertyModel() {
        return (LayerCachePropertyModel) super.getLayerPropertyModel();
    }

    @Override
    protected LayerCachePropertyModel getModifiedLayerPropertyModel() {
        return (LayerCachePropertyModel) super.getModifiedLayerPropertyModel();
    }

    @Override
    protected void initializeComponents() {
        this.setBorder(BorderFactory.createTitledBorder("VersionControl"));
        label = new JLabel(ControlsProperties.getString("String_Label_CurrentVersion"));
        comboBox = new JComboBox<>();
        setLayout(new GridBagLayout());
        this.add(label, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(10, 5, 10, 10));
        this.add(comboBox, new GridBagConstraintsHelper(1, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 0, 10, 5));
    }

    @Override
    protected void initializeResources() {
        ((TitledBorder) this.getBorder()).setTitle(MapViewProperties.getString("String_LayerProperty_Version"));
    }

    @Override
    protected void registerEvents() {
        comboBox.addItemListener(itemListener);
    }

    @Override
    protected void unregisterEvents() {
        comboBox.removeItemListener(itemListener);
    }

    @Override
    protected void fillComponents() {
        try {
            for (String version : getLayerPropertyModel().getVersions()) {
                comboBox.addItem(version);
            }
        } catch (Exception e) {
            Application.getActiveApplication().getOutput().output(e);
        }
    }

    @Override
    protected void setControlEnabled(String propertyName, boolean enabled) {
        if (propertyName.equals(LayerCachePropertyModel.CURRENT_VERSION)) {
            this.comboBox.setEnabled(enabled);
        }
    }
}
