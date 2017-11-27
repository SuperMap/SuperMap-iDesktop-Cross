package com.supermap.desktop.geometryoperation.editor;

import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.geometry.Abstract.IRegionFeature;
import com.supermap.desktop.geometryoperation.*;
import com.supermap.desktop.geometryoperation.control.JDialogFieldOperationSetting;
import com.supermap.desktop.geometryoperation.control.MapControlTip;
import com.supermap.desktop.mapeditor.MapEditorProperties;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.utilities.*;
import com.supermap.mapping.Layer;
import com.supermap.mapping.Selection;
import com.supermap.ui.Action;
import com.supermap.ui.TrackMode;
import com.supermap.ui.TrackedEvent;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

/**
 * Created by ChenS on 2017/11/24 0024.
 * 依障碍拓面：选择基面后开始绘面，绘制完成后会自动擦除与已有面重合的部分，并与基面合并。绘成的面若与基面不相交则执行失败。
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
                    JDialogFieldOperationSetting form = new JDialogFieldOperationSetting(MapEditorProperties.getString("String_GeometryOperation_FillRegion"), environment
                            .getMapControl().getMap(), environment.getEditProperties().getSelectedDatasetTypes().get(0));
                    if (form.showDialog() == DialogResult.OK) {
                        CursorUtilities.setWaitCursor(environment.getMapControl());
                        fillRegion(environment, form.getEditLayer(), form.getPropertyData());
                        TabularUtilities.refreshTabularStructure((DatasetVector) form.getEditLayer().getDataset());
                    }
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
        return environment.getEditProperties().getSelectedGeometryCount() == 1 // 选中数至少2个
                && ListUtilities.isListOnlyContain(environment.getEditProperties().getSelectedGeometryTypeFeatures(), IRegionFeature.class)
                && environment.getEditProperties().getEditableDatasetTypes().size() > 0
                && ListUtilities.isListContainAny(environment.getEditProperties().getEditableDatasetTypes(), DatasetType.CAD, DatasetType.REGION);
    }

    @Override
    public boolean check(EditEnvironment environment) {
        return environment.getEditor() instanceof FillRegionEditor;
    }

    private void fillRegion(EditEnvironment environment, Layer editLayer, Map<String, Object> propertyData) {
        GeoRegion result = null;
        Recordset targetRecordset = null;
        environment.getMapControl().getEditHistory().batchBegin();
        FillRegionEditModel editModel = (FillRegionEditModel) environment.getEditModel();
        try {
            List<Layer> layers = environment.getEditProperties().getSelectedLayers();
            for (Layer layer : layers) {
                if (layer.getDataset().getType() != DatasetType.REGION && layer.getDataset().getType() != DatasetType.CAD) {
                    break;
                }
                Recordset recordset = ((DatasetVector) layer.getDataset()).getRecordset(false, CursorType.STATIC);
                while (!recordset.isEOF()) {
                    if (!(recordset.getGeometry() instanceof GeoRegion)) {
                        break;
                    }
                    GeoRegion geoRegion = (GeoRegion) recordset.getGeometry();
                    editModel.createdRegion = (GeoRegion) Geometrist.erase(editModel.createdRegion, geoRegion);
                    recordset.moveNext();
                }
                recordset.dispose();
            }
            result = (GeoRegion) GeometryUtilities.union(editModel.baseRegion, editModel.createdRegion, true);
            if (editLayer != null && result != null) {
                targetRecordset = ((DatasetVector) editLayer.getDataset()).getRecordset(false, CursorType.DYNAMIC);
                targetRecordset.getBatch().begin();
                Selection selection = editLayer.getSelection();
                for (int i = 0; i < selection.getCount(); i++) {
                    targetRecordset.seekID(selection.get(i));
                    environment.getMapControl().getEditHistory().add(EditType.DELETE, targetRecordset, true);
                    targetRecordset.delete();
                }
                targetRecordset.addNew(result, propertyData);
                targetRecordset.getBatch().update();

                editLayer.getSelection().clear();
                int addedId = targetRecordset.getID();
                if (addedId > -1) {
                    selection.add(addedId);
                }
                environment.getMapControl().getEditHistory().add(EditType.ADDNEW, targetRecordset, true);
            } else {
                Application.getActiveApplication().getOutput().output(MapEditorProperties.getString("String_FailedToFillRegion"));
            }
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
                break;
            }
            GeoRegion geometry = (GeoRegion) layer.getSelection().toRecordset().getGeometry();
            GeometryUtilities.setGeometryStyle(geometry, RegionAndLineHighLightStyle.getRegionStyleRed());
            environment.getMap().getTrackingLayer().add(geometry, TAG_FILLREGION);
            editModel.baseRegion = geometry;
            environment.getMap().refreshTrackingLayer();
        }
    }

    private void clear(EditEnvironment environment) {
        FillRegionEditModel editModel = (FillRegionEditModel) environment.getEditModel();
        editModel.clear();
        MapUtilities.clearTrackingObjects(environment.getMap(), TAG_FILLREGION);
    }

    private class FillRegionEditModel implements IEditModel {
        MapControlTip tip;
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
            createdRegion = null;
            isTracking = false;
        }

        public void setTipMessage(String string) {
            labelMessage.setText(string);
            labelMessage.repaint();
        }
    }
}
