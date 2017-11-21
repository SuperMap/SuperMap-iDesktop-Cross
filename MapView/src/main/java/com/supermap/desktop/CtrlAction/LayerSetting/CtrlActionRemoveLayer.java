package com.supermap.desktop.CtrlAction.LayerSetting;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.Interface.IFormMap;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.ui.UICommonToolkit;
import com.supermap.desktop.ui.trees.LayersTree;
import com.supermap.desktop.ui.trees.NodeDataType;
import com.supermap.desktop.ui.trees.TreeNodeData;
import com.supermap.mapping.Layer;
import com.supermap.mapping.LayerGroup;
import com.supermap.mapping.Layers;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class CtrlActionRemoveLayer extends CtrlAction {

    public CtrlActionRemoveLayer(IBaseItem caller, IForm formClass) {
        super(caller, formClass);
    }

    @Override
    public void run() {
        try {
            IFormMap formMap = (IFormMap) Application.getActiveApplication().getActiveForm();
            Layer[] selectedLayers = formMap.getActiveLayers();
            ArrayList<Layer> selectLayers = new ArrayList<>();
            Layer selectLayer = null;
            int firstIndex = Short.MAX_VALUE;
            int lastIndex = 0;
            LayerGroup parentGroup = selectedLayers[0].getParentGroup();
            Layers currentLayers = formMap.getMapControl().getMap().getLayers();
            if (parentGroup != null) {
                for (int i = 0; i < selectedLayers.length; i++) {
                    int tempIndex = parentGroup.indexOf(selectedLayers[i]);
                    firstIndex = firstIndex > tempIndex ? tempIndex : firstIndex;
                    lastIndex = lastIndex < tempIndex ? tempIndex : lastIndex;
                }
                if (lastIndex == parentGroup.getCount() - 1) {
                    if (firstIndex == 0) {
                        if (selectedLayers.length == parentGroup.getCount()) {
                            selectLayer = parentGroup;
                        } else {
                            selectLayer = null;
                        }
                    } else {
                        selectLayer = parentGroup.get(firstIndex - 1);
                    }
                } else {
                    selectLayer = parentGroup.get(lastIndex + 1);
                }
            } else {
                for (int i = 0; i < selectedLayers.length; i++) {
                    int tempIndex = currentLayers.indexOf(selectedLayers[i].getName());
                    firstIndex = firstIndex > tempIndex ? tempIndex : firstIndex;
                    lastIndex = lastIndex < tempIndex ? tempIndex : lastIndex;
                }
                if (lastIndex == currentLayers.getCount() - 1) {
                    if (firstIndex == 0) {
                        if (selectedLayers.length == currentLayers.getCount()) {
                            selectLayer = null;
                        } else {
                            selectLayer = null;
                        }
                    } else {
                        selectLayer = currentLayers.get(firstIndex - 1);
                    }
                } else {
                    selectLayer = currentLayers.get(lastIndex + 1);
                }
            }
            if(formMap.removeLayers(selectedLayers)) {
                if (parentGroup == null) {
                    if (selectLayer != null) {
                        formMap.setActiveLayers(selectLayer);
                    } else if (currentLayers.getCount() > 0) {
                        formMap.setActiveLayers(currentLayers.get(currentLayers.getCount() - 1));
                    }
                } else {
                    if (selectLayer != null) {
                        formMap.setActiveLayers(selectLayer);
                    } else if (parentGroup.getCount() > 0) {
                        formMap.setActiveLayers(parentGroup.get(parentGroup.getCount() - 1));
                    }
                }
//                LayersTree tree = UICommonToolkit.getLayersManager().getLayersTree();
//                tree.firePropertyChangeWithLayerSelect();
            }
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        }
    }

    @Override
    public boolean enable() {
        boolean enable = false;
        if (Application.getActiveApplication().getActiveForm() instanceof IFormMap) {
            IFormMap formMap = (IFormMap) Application.getActiveApplication().getActiveForm();
            if (formMap != null) {
                LayersTree layersTree = UICommonToolkit.getLayersManager().getLayersTree();
                if (layersTree != null) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) layersTree.getSelectionPaths()[0].getLastPathComponent();
                    TreeNodeData selectedNodeData = (TreeNodeData) selectedNode.getUserObject();
                    Layer layer = (Layer) selectedNodeData.getData();
                    if (layer != null && selectedNodeData.getType() != NodeDataType.WMSSUB_LAYER && selectedNodeData.getType() != NodeDataType.THEME_GRAPH_ITEM) {
                        enable = true;
                    }
                }
            }
        }
        return enable;
    }

}