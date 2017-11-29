package com.supermap.desktop.geometryoperation.editor;

import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.geometryoperation.*;
import com.supermap.desktop.geometryoperation.control.MapControlTip;
import com.supermap.desktop.mapeditor.MapEditorProperties;
import com.supermap.desktop.utilities.*;
import com.supermap.mapping.Layer;
import com.supermap.ui.Action;
import com.supermap.ui.TrackMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeometryCopyEditor extends AbstractEditor {

    private static final String TAG_GEOMETRYCOPY = "Tag_GeometryCopy";
    private static final Action MAPCONTROL_ACTION = Action.CREATEPOINT;
    private static final TrackMode MAPCONTROL_TRACKMODE = TrackMode.TRACK;

    private IEditController geometryCopyController = new EditControllerAdapter() {

        public void mousePressed(EditEnvironment environment, MouseEvent e) {
            if (environment.getActiveEditableLayer() == null) {
                environment.stopEditor();
            } else {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    GeometryCopyEditModel editModel = (GeometryCopyEditModel) environment.getEditModel();

                    if (editModel.basePoint == null || editModel.basePoint.equals(Point2D.getEMPTY())) {
                        editModel.basePoint = getMousePointOnMap(environment, e.getPoint());
                        editModel.tip.unbind();
                    } else {
                        geometryCopy(environment, e);
                    }
                }
            }
        }

        @Override
        public void mouseClicked(EditEnvironment environment, MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                environment.stopEditor();
            }
        }

        @Override
        public void mouseMoved(EditEnvironment environment, MouseEvent e) {
            mapControl_mouseMoved(environment, e);
        }
    };

    @Override
    public void activate(EditEnvironment environment) {
        GeometryCopyEditModel editModel = null;
        if (environment.getEditModel() instanceof GeometryCopyEditModel) {
            editModel = (GeometryCopyEditModel) environment.getEditModel();
        } else {
            editModel = new GeometryCopyEditModel();
            environment.setEditModel(editModel);
        }
        environment.setEditController(this.geometryCopyController);
        editModel.oldAction = environment.getMapControl().getAction();
        editModel.oldTrackMode = environment.getMapControl().getTrackMode();
        environment.getMapControl().setAction(MAPCONTROL_ACTION);
        environment.getMapControl().setTrackMode(MAPCONTROL_TRACKMODE);
        editModel.tip.bind(environment.getMapControl());
        initializeSrc(environment);
    }

    @Override
    public void deactivate(EditEnvironment environment) {
        if (environment.getEditModel() instanceof GeometryCopyEditModel) {
            GeometryCopyEditModel editModel = (GeometryCopyEditModel) environment.getEditModel();

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
        return environment.getEditProperties().getEditableSelectedGeometryCount() > 0;
    }

    @Override
    public boolean check(EditEnvironment environment) {
        return environment.getEditor() instanceof GeometryCopyEditor;
    }

    private void initializeSrc(EditEnvironment environment) {
        GeometryCopyEditModel editModel = (GeometryCopyEditModel) environment.getEditModel();
        editModel.trackingGeoCompound = new GeoCompound();
        ArrayList<Layer> layers = MapUtilities.getLayers(environment.getMap());

        for (Layer layer : layers) {
            if (layer.isEditable() && layer.getSelection() != null && layer.getSelection().getCount() > 0) {
                editModel.copyGeometries.put(layer, new ArrayList<Integer>());
                Recordset recordset = layer.getSelection().toRecordset();

                if (recordset == null) {
                    continue;
                }

                while (!recordset.isEOF()) {
                    Geometry geometry = recordset.getGeometry();

                    if (geometry != null) {
                        editModel.copyGeometries.get(layer).add(recordset.getID());
                        editModel.trackingGeoCompound.addPart(geometry);
                        geometry.dispose();
                    }
                    recordset.moveNext();
                }
                recordset.close();
                recordset.dispose();
            }
        }
    }

    private void mapControl_mouseMoved(EditEnvironment environment, MouseEvent e) {
        GeometryCopyEditModel editModel = (GeometryCopyEditModel) environment.getEditModel();

        try {
            if (editModel.basePoint != null && !editModel.basePoint.equals(Point2D.getEMPTY())) {
                Point2D currentPoint = getMousePointOnMap(environment, e.getPoint());
                Double offsetX = currentPoint.getX() - editModel.basePoint.getX();
                Double offsetY = currentPoint.getY() - editModel.basePoint.getY();

                if (editModel.trackingGeoCompound != null) {
                    editModel.trackingGeoCompound.offset(offsetX, offsetY);
                    GeoStyleUtilities.setGeometryStyle(editModel.trackingGeoCompound, EditorUtilities.getTrackingLineStyle(),
                            EditorUtilities.getTrackingLineStyle3D());

                    int index = environment.getMap().getTrackingLayer().indexOf(TAG_GEOMETRYCOPY);
                    if (index >= 0) {
                        environment.getMap().getTrackingLayer().set(index, editModel.trackingGeoCompound);
                    } else {
                        environment.getMap().getTrackingLayer().add(editModel.trackingGeoCompound, TAG_GEOMETRYCOPY);
                    }
                    editModel.trackingGeoCompound.offset(-1 * offsetX, -1 * offsetY);
                    environment.getMap().refreshTrackingLayer();
                    environment.getMapControl().repaint();
                }
            }
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        }
    }

    private void geometryCopy(EditEnvironment environment, MouseEvent e) {
        GeometryCopyEditModel editModel = (GeometryCopyEditModel) environment.getEditModel();
        environment.getMapControl().getEditHistory().batchBegin();

        try {
            Point2D mousePointOnMap = getMousePointOnMap(environment, e.getPoint());

            if (editModel.basePoint.equals(Point2D.getEMPTY()) && environment.getActiveEditableLayer() != null) {
                editModel.basePoint = new Point2D(mousePointOnMap.getX(), mousePointOnMap.getY());
            } else if (editModel.copyGeometries != null && environment.getActiveEditableLayer() != null) {
                double offsetX = mousePointOnMap.getX() - editModel.basePoint.getX();
                double offsetY = mousePointOnMap.getY() - editModel.basePoint.getY();

                for (Layer layer : editModel.copyGeometries.keySet()) {
                    List<Integer> selectionIDs = new ArrayList<Integer>();
                    List<Integer> selectedDs = editModel.copyGeometries.get(layer);
                    Recordset recordset=null;
                    try {

                         recordset = ((DatasetVector) layer.getDataset()).query(
                                ArrayUtilities.convertToInt(selectedDs.toArray(new Integer[selectedDs.size()])), CursorType.DYNAMIC);
                    }catch (Exception ex){
                        continue;
                    }
                    for (Integer id : selectedDs) {
                        recordset.seekID(id);
                        Geometry geometry = recordset.getGeometry();
                        if (geometry != null) {
                            geometry.offset(offsetX, offsetY);

                            Map<String, Object> values = RecordsetUtilities.getFieldValues(recordset);

                            if (recordset.addNew(geometry, values)) {
                                recordset.update();
                                selectionIDs.add(recordset.getID());
                            }
                            geometry.dispose();
                        }
                    }
                    recordset.close();
                    recordset.dispose();

                    if (selectionIDs.size() > 0) {
                        int[] toSelected = ArrayUtilities.convertToInt(selectionIDs.toArray(new Integer[selectionIDs.size()]));
                        layer.getSelection().clear();
                        layer.getSelection().addRange(toSelected);
                        Recordset toSelectedRecordset = ((DatasetVector) layer.getDataset()).query(toSelected, CursorType.DYNAMIC);
                        environment.getMapControl().getEditHistory().add(EditType.ADDNEW, toSelectedRecordset, false);

                        // 刷新一下桌面的属性表窗口
	                    TabularUtilities.refreshTabularStructure(toSelectedRecordset.getDataset());
	                    toSelectedRecordset.close();
                        toSelectedRecordset.dispose();
                    }
                }

                environment.getMap().refresh();
                environment.getMapControl().revalidate();
            }
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        } finally {
            environment.getMapControl().getEditHistory().batchEnd();
        }
    }

    /**
     * 获取当前鼠标在地图上的坐标点，如果有捕捉，返回捕捉点坐标，没有捕捉，返回鼠标点对应的地图坐标
     *
     * @param environment
     * @param mousePointOnMapControl
     * @return
     */
    private Point2D getMousePointOnMap(EditEnvironment environment, Point mousePointOnMapControl) {
        Point2D mousePointOnMap = Point2D.getEMPTY();
        mousePointOnMap = EditorUtilities.getSnapModePoint(environment.getMapControl());

        if (mousePointOnMap == null || mousePointOnMap.equals(Point2D.getEMPTY())) {
            mousePointOnMap = environment.getMap().pixelToMap(mousePointOnMapControl);
        }
        return mousePointOnMap;
    }

    private void clear(EditEnvironment environment) {
        GeometryCopyEditModel editModel = (GeometryCopyEditModel) environment.getEditModel();
        editModel.clear();
        MapUtilities.clearTrackingObjects(environment.getMap(), TAG_GEOMETRYCOPY);
    }

    private class GeometryCopyEditModel implements IEditModel {

        public MapControlTip tip;
        private JLabel labelMsg = new JLabel(MapEditorProperties.getString("String_Tip_Edit_CopyObj"));
        public GeoCompound trackingGeoCompound;
        public Map<Layer, List<Integer>> copyGeometries = new HashMap<>();
        public Point2D basePoint = Point2D.getEMPTY();

        public Action oldAction = Action.SELECT2;
        public TrackMode oldTrackMode = TrackMode.EDIT;

        public GeometryCopyEditModel() {
            this.tip = new MapControlTip();
            this.tip.addLabel(this.labelMsg);
        }

        public void setMsg(String msg) {
            this.labelMsg.setText(msg);
            this.labelMsg.repaint();
        }

        public void clear() {
            setMsg(MapEditorProperties.getString("String_Tip_Edit_CopyObj"));
            this.basePoint = Point2D.getEMPTY();
            this.oldAction = Action.SELECT2;
            this.oldTrackMode = TrackMode.EDIT;

            if (this.trackingGeoCompound != null) {
                this.trackingGeoCompound.dispose();
                this.trackingGeoCompound = null;
            }

            if (this.copyGeometries != null) {
                this.copyGeometries.clear();
            }
        }
    }
}
