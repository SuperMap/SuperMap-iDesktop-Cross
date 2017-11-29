package com.supermap.desktop.geometryoperation.editor;

import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.geometry.Abstract.IRegionFeature;
import com.supermap.desktop.geometryoperation.*;
import com.supermap.desktop.geometryoperation.control.MapControlTip;
import com.supermap.desktop.mapeditor.MapEditorProperties;
import com.supermap.desktop.utilities.GeometryUtilities;
import com.supermap.desktop.utilities.ListUtilities;
import com.supermap.desktop.utilities.MapUtilities;
import com.supermap.desktop.utilities.RecordsetUtilities;
import com.supermap.mapping.Layer;
import com.supermap.mapping.Selection;
import com.supermap.ui.Action;
import com.supermap.ui.TrackMode;
import com.supermap.ui.TrackedEvent;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ChenS on 2017/11/24 0024.
 * 填缝：选择基面后开始绘面，绘制完成后会自动擦除与已有面重合的部分，并与基面合并。绘成的面若与基面不相交则执行失败。
 */
public class FillRegionEditor extends AbstractEditor {
    private final static String TAG_FILLREGION = "Tag_FillRegion";
    private static final Action MAP_CONTROL_ACTION = Action.CREATEPOLYGON;

    private IEditController fillRegionController = new EditControllerAdapter() {
        @Override
        public void mousePressed(EditEnvironment environment, MouseEvent e) {
            FillRegionEditModel editModel = (FillRegionEditModel) environment.getEditModel();
            if (!editModel.isTracking && SwingUtilities.isLeftMouseButton(e)) {
                editModel.isTracking = true;
                editModel.setTipMessage(MapEditorProperties.getString("String_RightClickToEnd"));
            } else if (SwingUtilities.isRightMouseButton(e)) {
                if (editModel.isTracking) {
                    editModel.isTracking = false;
                    fillRegion(environment);
                }
                environment.stopEditor();
                MapUtilities.clearTrackingObjects(environment.getMap(), TAG_FILLREGION);
                MapUtilities.clearTrackingObjects(environment.getMap(), MapEditorProperties.getString("String_GeometryOperation_FillRegionByDrawingRegion"));
            }
        }

        @Override
        public void tracked(EditEnvironment environment, TrackedEvent e) {
            if (!(environment.getEditModel() instanceof FillRegionEditModel)) {
                return;
            }
            FillRegionEditModel editModel = (FillRegionEditModel) environment.getEditModel();

            try {
                editModel.createdRegion = (GeoRegion) e.getGeometry();
                //所绘制的面与基面无交集，停止编辑
                if (Geometrist.isDisjointed(editModel.createdRegion, editModel.baseRegion)) {
                    MapUtilities.clearTrackingObjects(environment.getMap(), TAG_FILLREGION);
                    MapUtilities.clearTrackingObjects(environment.getMap(), MapEditorProperties.getString("String_GeometryOperation_FillRegionByDrawingRegion"));
                    Application.getActiveApplication().getOutput().output(MapEditorProperties.getString("String_Warning_IsDrawedRegionDisjoined"));
                    environment.stopEditor();
                }
            } catch (Exception ex) {
                Application.getActiveApplication().getOutput().output(ex);
            }
        }
    };

    @Override
    public void activate(EditEnvironment environment) {
        FillRegionEditModel editModel;
        if (environment.getEditModel() instanceof FillRegionEditModel) {
            editModel = (FillRegionEditModel) environment.getEditModel();
        } else {
            editModel = new FillRegionEditModel();
            environment.setEditModel(editModel);
        }
        environment.setEditController(fillRegionController);
        editModel.oldAction = environment.getMapControl().getAction();
        editModel.oldTrackMode = environment.getMapControl().getTrackMode();
        environment.getMapControl().setAction(MAP_CONTROL_ACTION);
        environment.getMapControl().setTrackMode(TrackMode.TRACK);
        editModel.tip.bind(environment.getMapControl());

        initializeBaseRegion(environment);
    }

    @Override
    public void deactivate(EditEnvironment environment) {
        if (environment.getEditModel() instanceof FillRegionEditModel) {
            FillRegionEditModel editModel = (FillRegionEditModel) environment.getEditModel();

            try {
                environment.getMapControl().setAction(editModel.oldAction);
                environment.getMapControl().setTrackMode(editModel.oldTrackMode);
                clear(environment);
            } finally {
                editModel.tip.unbind();
                environment.setEditModel(null);
                environment.setEditController(NullEditController.instance());
            }
        }
    }

    @Override
    public boolean enable(EditEnvironment environment) {
        boolean isEditableLayerSelected = false;
        for (Layer layer : environment.getEditableLayers()) {
            isEditableLayerSelected = layer.getSelection().getCount() > 0 || isEditableLayerSelected;
        }
        return environment.getEditProperties().getSelectedGeometryCount() == 1 && isEditableLayerSelected
                && ListUtilities.isListOnlyContain(environment.getEditProperties().getSelectedGeometryTypeFeatures(), IRegionFeature.class)
                && environment.getEditProperties().getEditableDatasetTypes().size() > 0
                && ListUtilities.isListContainAny(environment.getEditProperties().getEditableDatasetTypes(), DatasetType.CAD, DatasetType.REGION);
    }

    @Override
    public boolean check(EditEnvironment environment) {
        return environment.getEditor() instanceof FillRegionEditor;
    }

    private void fillRegion(EditEnvironment environment) {
        GeoRegion result = null;
        Recordset targetRecordset = null;
        environment.getMapControl().getEditHistory().batchBegin();
        FillRegionEditModel editModel = (FillRegionEditModel) environment.getEditModel();
        try {
            targetRecordset = ((DatasetVector) editModel.layer.getDataset()).getRecordset(false, CursorType.DYNAMIC);
            while (!targetRecordset.isEOF()) {
                GeoRegion geoRegion = (GeoRegion) targetRecordset.getGeometry();
                editModel.createdRegion = (GeoRegion) Geometrist.erase(editModel.createdRegion, geoRegion);
                targetRecordset.moveNext();
            }
            result = (GeoRegion) GeometryUtilities.union(editModel.baseRegion, editModel.createdRegion, true);
            targetRecordset.getBatch().begin();
            Selection selection = editModel.layer.getSelection();
            targetRecordset.seekID(selection.get(0));
            HashMap<String, Object> propertyData = mergePropertyData(((DatasetVector) editModel.layer.getDataset()), targetRecordset.getFieldInfos(), RecordsetUtilities.getFieldValuesIgnoreCase(targetRecordset));
            environment.getMapControl().getEditHistory().add(EditType.DELETE, targetRecordset, true);
            targetRecordset.delete();

            targetRecordset.addNew(result, propertyData);
            targetRecordset.getBatch().update();

            editModel.layer.getSelection().clear();
            int addedId = targetRecordset.getID();
            if (addedId > -1) {
                selection.add(addedId);
            }
            environment.getMapControl().getEditHistory().add(EditType.ADDNEW, targetRecordset, true);
        } catch (Exception e) {
            Application.getActiveApplication().getOutput().output(e);
        } finally {
            environment.getMapControl().getEditHistory().batchEnd();
            if (result != null) {
                result.dispose();
            }
            if (targetRecordset != null) {
                targetRecordset.dispose();
                targetRecordset.close();
            }
            environment.getMapControl().getMap().refresh();
        }
    }

    private void initializeBaseRegion(EditEnvironment environment) {
        if (!(environment.getEditModel() instanceof FillRegionEditModel)) {
            return;
        }

        FillRegionEditModel editModel = (FillRegionEditModel) environment.getEditModel();
        List<Layer> layers = MapUtilities.getLayers(environment.getMap());

        for (Layer layer : layers) {
            if (!(layer.isEditable()
                    && layer.getDataset() != null
                    && layer.getDataset() instanceof DatasetVector
                    && (layer.getDataset().getType() == DatasetType.REGION || layer.getDataset().getType() == DatasetType.CAD)
                    && layer.getSelection().getCount() == 1)) {
                continue;
            }
            editModel.layer = layer;
            GeoRegion geometry = (GeoRegion) layer.getSelection().toRecordset().getGeometry();
            GeometryUtilities.setGeometryStyle(geometry, RegionAndLineHighLightStyle.getRegionStyleRed());
            environment.getMap().getTrackingLayer().add(geometry, TAG_FILLREGION);
            editModel.baseRegion = geometry;
            environment.getMap().refreshTrackingLayer();
            break;
        }
    }

    private void clear(EditEnvironment environment) {
        FillRegionEditModel editModel = (FillRegionEditModel) environment.getEditModel();
        editModel.clear();
        MapUtilities.clearTrackingObjects(environment.getMap(), TAG_FILLREGION);
    }

    private HashMap<String, Object> mergePropertyData(DatasetVector des, FieldInfos srcFieldInfos, Map<String, Object> properties) {
        HashMap<String, Object> results = new HashMap<>();
        FieldInfos desFieldInfos = des.getFieldInfos();

        for (int i = 0; i < desFieldInfos.getCount(); i++) {
            FieldInfo desFieldInfo = desFieldInfos.get(i);

            if (!desFieldInfo.isSystemField() && properties.containsKey(desFieldInfo.getName().toLowerCase())) {
                FieldInfo srcFieldInfo = srcFieldInfos.get(desFieldInfo.getName());

                if (desFieldInfo.getType() == srcFieldInfo.getType()) {
                    // 如果要源字段和目标字段类型一致，直接保存
                    results.put(desFieldInfo.getName(), properties.get(desFieldInfo.getName().toLowerCase()));
                } else if (desFieldInfo.getType() == FieldType.WTEXT || desFieldInfo.getType() == FieldType.TEXT) {

                    // 如果目标字段与源字段类型不一致，则只有目标字段是文本型字段时，将源字段值做 toString 处理
                    results.put(desFieldInfo.getName(), properties.get(desFieldInfo.getName().toLowerCase()).toString());
                }
            }
        }
        return results;
    }

    private class FillRegionEditModel implements IEditModel {
        MapControlTip tip;
        Layer layer;
        JLabel labelMessage;
        GeoRegion baseRegion;
        GeoRegion createdRegion;
        boolean isTracking;
        Action oldAction = Action.SELECT;
        TrackMode oldTrackMode = TrackMode.EDIT;
        final String TIP_FILLREGION = MapEditorProperties.getString("String_GeometryOperation_FillRegionByDrawingRegion");

        FillRegionEditModel() {
            this.tip = new MapControlTip();
            this.labelMessage = new JLabel(TIP_FILLREGION);
            tip.addLabel(labelMessage);
            isTracking = false;
        }

        void clear() {
            oldAction = Action.SELECT;
            oldTrackMode = TrackMode.EDIT;
            if (baseRegion != null) {
                baseRegion.dispose();
            }
            layer = null;
            createdRegion = null;
            isTracking = false;
        }

        public void setTipMessage(String string) {
            labelMessage.setText(string);
            labelMessage.repaint();
        }
    }
}
