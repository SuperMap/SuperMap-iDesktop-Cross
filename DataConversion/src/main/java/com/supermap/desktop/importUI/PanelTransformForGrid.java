package com.supermap.desktop.importUI;

import com.supermap.data.conversion.*;
import com.supermap.desktop.baseUI.PanelTransform;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.ui.StateChangeEvent;
import com.supermap.desktop.ui.StateChangeListener;
import com.supermap.desktop.ui.TristateCheckBox;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by xie on 2016/10/11.
 * raw,.b,bin,bip,bil,bsq,dem等栅格文件的导入参数设置界面
 */
public class PanelTransformForGrid extends PanelTransform {
    private ArrayList<PanelImport> panelImports;
    private TristateCheckBox checkBoxBuildImgPyramid;
    private StateChangeListener buildImgPyramidListener = new StateChangeListener() {

        @Override
        public void stateChange(StateChangeEvent e) {
            if (null != panelImports) {
                for (PanelImport tempPanelImport : panelImports) {
                    ((PanelTransformForGrid) tempPanelImport.getTransform()).getCheckBoxBuildImgPyramid().setSelected(checkBoxBuildImgPyramid.isSelected());
                }
            } else {
                if (importSetting instanceof ImportSettingRAW) {
                    ((ImportSettingRAW) importSetting).setPyramidBuilt(checkBoxBuildImgPyramid.isSelected());
                } else if (importSetting instanceof ImportSettingTEMSClutter) {
                    ((ImportSettingTEMSClutter) importSetting).setPyramidBuilt(checkBoxBuildImgPyramid.isSelected());
                } else if (importSetting instanceof ImportSettingBIP) {
                    ((ImportSettingBIP) importSetting).setPyramidBuilt(checkBoxBuildImgPyramid.isSelected());
                } else if (importSetting instanceof ImportSettingBSQ) {
                    ((ImportSettingBSQ) importSetting).setPyramidBuilt(checkBoxBuildImgPyramid.isSelected());
                } else if (importSetting instanceof ImportSettingGBDEM) {
                    ((ImportSettingGBDEM) importSetting).setPyramidBuilt(checkBoxBuildImgPyramid.isSelected());
                } else if (importSetting instanceof ImportSettingUSGSDEM) {
                    ((ImportSettingUSGSDEM) importSetting).setPyramidBuilt(checkBoxBuildImgPyramid.isSelected());
                } else if (importSetting instanceof ImportSettingBIL) {
                    ((ImportSettingBIL) importSetting).setPyramidBuilt(checkBoxBuildImgPyramid.isSelected());
                }
            }
        }
    };

    public PanelTransformForGrid(ImportSetting importSetting) {
        super(importSetting);
        registEvents();
    }

    public PanelTransformForGrid(ArrayList<PanelImport> panelImports, int layoutType) {
        super(panelImports, layoutType);
        this.panelImports = panelImports;
        initLayerout();
        registEvents();
    }

    @Override
    public void initComponents() {
        this.checkBoxBuildImgPyramid = new TristateCheckBox();
        if (importSetting instanceof ImportSettingRAW) {
            checkBoxBuildImgPyramid.setSelected(((ImportSettingRAW) importSetting).isPyramidBuilt());
        } else if (importSetting instanceof ImportSettingTEMSClutter) {
            checkBoxBuildImgPyramid.setSelected(((ImportSettingTEMSClutter) importSetting).isPyramidBuilt());
        } else if (importSetting instanceof ImportSettingBIP) {
            checkBoxBuildImgPyramid.setSelected(((ImportSettingBIP) importSetting).isPyramidBuilt());
        } else if (importSetting instanceof ImportSettingBSQ) {
            checkBoxBuildImgPyramid.setSelected(((ImportSettingBSQ) importSetting).isPyramidBuilt());
        } else if (importSetting instanceof ImportSettingGBDEM) {
            checkBoxBuildImgPyramid.setSelected(((ImportSettingGBDEM) importSetting).isPyramidBuilt());
        } else if (importSetting instanceof ImportSettingUSGSDEM) {
            checkBoxBuildImgPyramid.setSelected(((ImportSettingUSGSDEM) importSetting).isPyramidBuilt());
        } else if (importSetting instanceof ImportSettingBIL) {
            checkBoxBuildImgPyramid.setSelected(((ImportSettingBIL) importSetting).isPyramidBuilt());
        }
    }

    @Override
    public void setComponentName() {
        super.setComponentName();
        ComponentUIUtilities.setName(this.checkBoxBuildImgPyramid, "PanelTransformForGrid_checkBoxBuildImgPyramid");
    }

    @Override
    public void initLayerout() {
        this.removeAll();
        this.setLayout(new GridBagLayout());
        this.add(this.checkBoxBuildImgPyramid, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setInsets(5, 5, 5, 10).setFill(GridBagConstraints.NONE).setWeight(1, 0));
        if (null != panelImports) {
            this.checkBoxBuildImgPyramid.setSelectedEx(externalDataSelectAll());
        }
    }

    private Boolean externalDataSelectAll() {
        Boolean result = null;
        int selectCount = 0;
        int unSelectCount = 0;
        for (PanelImport tempPanel : panelImports) {
            boolean select = ((PanelTransformForGrid) tempPanel.getTransform()).getCheckBoxBuildImgPyramid().isSelected();
            if (select) {
                selectCount++;
            } else if (!select) {
                unSelectCount++;
            }
        }
        if (selectCount == panelImports.size()) {
            result = true;
        } else if (unSelectCount == panelImports.size()) {
            result = false;
        }
        return result;
    }

    @Override
    public void registEvents() {
        removeEvents();
        this.checkBoxBuildImgPyramid.addStateChangeListener(this.buildImgPyramidListener);
    }

    @Override
    public void removeEvents() {
        this.checkBoxBuildImgPyramid.removeStateChangeListener(this.buildImgPyramidListener);
    }

    @Override
    public void initResources() {
        this.setBorder(new TitledBorder(ControlsProperties.getString("string_border_panelTransform")));
        this.checkBoxBuildImgPyramid.setText(ControlsProperties.getString("string_checkbox_chckbxImageInfo"));
    }

    public JCheckBox getCheckBoxBuildImgPyramid() {
        return checkBoxBuildImgPyramid;
    }
}
