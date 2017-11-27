package com.supermap.desktop.geometryoperation.editor;

import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IFormMap;
import com.supermap.desktop.core.recordset.RecordsetDelete;
import com.supermap.desktop.geometry.Abstract.IGeometry;
import com.supermap.desktop.geometry.Abstract.ILineFeature;
import com.supermap.desktop.geometry.Implements.DGeometryFactory;
import com.supermap.desktop.geometryoperation.*;
import com.supermap.desktop.geometryoperation.control.JDialogLineInterrruptSelect;
import com.supermap.desktop.geometryoperation.control.MapControlTip;
import com.supermap.desktop.mapeditor.MapEditorProperties;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.utilities.ArrayUtilities;
import com.supermap.desktop.utilities.MapUtilities;
import com.supermap.desktop.utilities.RecordsetUtilities;
import com.supermap.desktop.utilities.TabularUtilities;
import com.supermap.mapping.Layer;
import com.supermap.ui.Action;
import com.supermap.ui.ActionChangedEvent;
import com.supermap.ui.TrackMode;
import com.supermap.ui.TrackedEvent;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Map;

/**
 * Created by lixiaoyao on 2017/1/12.
 */
public class LineInterruptEditor extends AbstractEditor {
    private static final String TAG_LineInterruptByPoint = "TAG_LineInterrupt";

    private IEditController lineInterruptController = new EditControllerAdapter() {
        @Override
        public void mouseClicked(EditEnvironment environment, MouseEvent e) {
            environment.getMap().getTrackingLayer().clear();
            if (SwingUtilities.isRightMouseButton(e)) {
                LineInterruptModel editModel = (LineInterruptModel) environment.getEditModel();
                editModel.oldMapControlAction = Action.SELECT2;
                environment.stopEditor();
            } else if (SwingUtilities.isLeftMouseButton(e)) {
                if (isCanInterrupt(environment)) {
                    Recordset resultRecordset = queryGeometryIntersectSelectedGeometry(((LineInterruptModel) environment.getEditModel()).geometry,
                            (DatasetVector) environment.getActiveEditableLayer().getDataset());
                    if (resultRecordset.getRecordCount() == 1) {
                        runInterruptLine(environment);
                    }
                    //多条线需要选择并且,选择后确定才能打断
                    if (resultRecordset.getRecordCount() > 1) {
                        JDialogLineInterrruptSelect jDialogLineInterrruptSelect = new JDialogLineInterrruptSelect(environment, resultRecordset, ((LineInterruptModel) environment.getEditModel()).hasCommonNodeLineIDs, (JFrame) Application.getActiveApplication().getMainFrame(), true);
                        DialogResult result = jDialogLineInterrruptSelect.showDialog();
                        if (result == DialogResult.OK) {
                            if (jDialogLineInterrruptSelect.getSelectedLineIds().size() > 0) {
                                ((LineInterruptModel) environment.getEditModel()).setHasCommonNodeLineIDs(jDialogLineInterrruptSelect.getSelectedLineIds());
                                runInterruptLine(environment);
                            }
                        }

                    }

                }
            }
        }

        @Override
        public void tracked(EditEnvironment environment, TrackedEvent e) {
            mapControlTracked(environment, e);
        }

        @Override
        public void actionChanged(EditEnvironment environment, ActionChangedEvent e) {

            if (environment.getEditor() instanceof LineInterruptEditor && e.getOldAction() == Action.CREATEPOINT) {
                if (e.getNewAction() == Action.PAN || e.getNewAction() == Action.PAN2 || e.getNewAction() == Action.ZOOMFREE || e.getNewAction() == Action.ZOOMFREE2 || e.getNewAction() == Action.ZOOMIN || e.getNewAction() == Action.ZOOMOUT) {
                    environment.getMapControl().setAction(Action.CREATEPOINT);
                } else {
                    LineInterruptModel editModel = (LineInterruptModel) environment.getEditModel();
                    editModel.oldMapControlAction = e.getNewAction();
                    environment.stopEditor();
                }
            }
        }

        @Override
        public void undone(EditEnvironment environment, EventObject arg0) {
            if (!(environment.getEditModel() instanceof LineInterruptModel)) {
                return;
            }
            MapUtilities.clearTrackingObjects(environment.getMap(), TAG_LineInterruptByPoint);
        }
    };

    @Override
    public void activate(EditEnvironment environment) {
        LineInterruptModel editModel;
        if (environment.getEditModel() instanceof LineInterruptModel) {
            editModel = (LineInterruptModel) environment.getEditModel();
        } else {
            editModel = new LineInterruptModel();
            environment.setEditModel(editModel);
        }
        environment.setEditController(this.lineInterruptController);
        editModel.oldMapControlAction = environment.getMapControl().getAction();
        environment.getMapControl().setAction(Action.CREATEPOINT);
        environment.getMapControl().setTrackMode(TrackMode.TRACK);
        editModel.tip.bind(environment.getMapControl());
    }

    @Override
    public void deactivate(EditEnvironment environment) {
        if (environment.getEditModel() instanceof LineInterruptModel) {
            LineInterruptModel editModel = (LineInterruptModel) environment.getEditModel();
            try {
                environment.getMapControl().setAction(editModel.oldMapControlAction);
                environment.getMapControl().setTrackMode(editModel.oldTrackMode);
                clear(environment);
            } finally {
                editModel.tip.unbind();
                environment.setEditController(NullEditController.instance());
                environment.setEditModel(null);
            }
        }
    }

    @Override
    public boolean enable(EditEnvironment environment) {
        return ((IFormMap) Application.getActiveApplication().getActiveForm()).getMapControl().getActiveEditableLayer() != null &&
                (((IFormMap) Application.getActiveApplication().getActiveForm()).getMapControl().getActiveEditableLayer().isEditable() && environment.getActiveEditableLayer().getDataset().getType() == DatasetType.LINE
                        || environment.getActiveEditableLayer().getDataset().getType() == DatasetType.CAD);
    }

    private void clear(EditEnvironment environment) {
        if (!(environment.getEditModel() instanceof LineInterruptModel)) {
            return;
        }
        LineInterruptModel editModel = (LineInterruptModel) environment.getEditModel();
        MapUtilities.clearTrackingObjects(environment.getMap(), TAG_LineInterruptByPoint);
        editModel.clear();
    }

    @Override
    public boolean check(EditEnvironment environment) {
        return environment.getEditor() instanceof LineInterruptEditor;
    }

    /**
     * 鼠标左击之后，获取所绘制的对象
     */
    private void mapControlTracked(EditEnvironment environment, TrackedEvent e) {
        if (!(environment.getEditModel() instanceof LineInterruptModel)) {
            return;
        }
        LineInterruptModel editModel = (LineInterruptModel) environment.getEditModel();

        try {
            editModel.geometry = e.getGeometry();
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        }
    }

    //空间查询判断是否可以打断
    private boolean isCanInterrupt(EditEnvironment environment) {
        LineInterruptModel editModel = (LineInterruptModel) environment.getEditModel();
        Layer layer = environment.getActiveEditableLayer();
        boolean result = false;
        if (layer == null) {
            return result;
        }
        editModel.hasCommonNodeLineIDs.clear();

        Recordset resultRecordset = queryGeometryIntersectSelectedGeometry(editModel.geometry, (DatasetVector) layer.getDataset());
        if (resultRecordset.getRecordCount() >= 1) {
            resultRecordset.moveFirst();
            for (int i = 0; i < resultRecordset.getRecordCount(); ++i) {
                IGeometry dGeoemtry = DGeometryFactory.create(resultRecordset.getGeometry());
                if (dGeoemtry instanceof ILineFeature) {
                    GeoLine lineTemp = (GeoLine) (dGeoemtry.getGeometry());
                    Point2Ds point2Ds = lineTemp.getPart(0);
                    //加一个条件判断，且不为端点
                    if (!point2Ds.getItem(0).equals(new Point2D(((GeoPoint) (editModel.geometry)).getX(), ((GeoPoint) (editModel.geometry)).getY()))
                            && !point2Ds.getItem(point2Ds.getCount() - 1).equals(new Point2D(((GeoPoint) (editModel.geometry)).getX(), ((GeoPoint) (editModel.geometry)).getY()))) {
                        result = true;
                        editModel.hasCommonNodeLineIDs.add(dGeoemtry.getGeometry().getID());
                    }

                }
                resultRecordset.moveNext();
            }
        }

        return result;
    }

    /**
     * 定义空间查询
     */
    private Recordset queryGeometryIntersectSelectedGeometry(Geometry selectedGeometry, DatasetVector nowDatasetVector) {
        Recordset resultRecordset = null;

        QueryParameter parameter = new QueryParameter();
        parameter.setCursorType(CursorType.DYNAMIC);
        parameter.setSpatialQueryMode(SpatialQueryMode.INTERSECT);
        parameter.setHasGeometry(true);
        Point2D tempPoint = new Point2D(((GeoPoint) selectedGeometry).getX(), ((GeoPoint) selectedGeometry).getY());
        parameter.setSpatialQueryObject(tempPoint);

        resultRecordset = nowDatasetVector.query(parameter);
        return resultRecordset;
    }

    private void runInterruptLine(EditEnvironment environment) {
        LineInterruptModel editModel = (LineInterruptModel) environment.getEditModel();
        environment.getMapControl().getEditHistory().batchBegin();
        Recordset sourceRecordset = null;
        sourceRecordset = ((DatasetVector) environment.getActiveEditableLayer().getDataset()).getRecordset(false, CursorType.DYNAMIC);
        RecordsetDelete delete = new RecordsetDelete(sourceRecordset.getDataset(), environment.getMapControl().getEditHistory());
        delete.begin();
        // 记录打断线操作成功的对象的ID，在操作结束的时候重置一下它们的选中，用以刷新属性面板等
        ArrayList<Integer> succeededIDs = new ArrayList<>();
        try {
            for (int i = 0; i < editModel.hasCommonNodeLineIDs.size(); i++) {
                Geometry tempGeometry = null;
                Geometry newGeometry = null;
                try {

                    sourceRecordset.seekID(editModel.hasCommonNodeLineIDs.get(i));
                    tempGeometry = sourceRecordset.getGeometry();

                    Map<String, Object> values = RecordsetUtilities.getFieldValues(sourceRecordset);
                    GeoLine[] resultGeoline = Geometrist.splitLine((GeoLine) tempGeometry, editModel.geometry, sourceRecordset.getDataset().getTolerance().getNodeSnap());
                    if (resultGeoline != null && resultGeoline.length >= 2) {
                        delete.delete(tempGeometry.getID());
                        for (int j = 0; j < resultGeoline.length; j++) {
                            if (j % 2 == 0) {
                                resultGeoline[j].setStyle(RegionAndLineHighLightStyle.getLineStyleRed());
                            } else {
                                resultGeoline[j].setStyle(RegionAndLineHighLightStyle.getLineStyleBlue());
                            }
                            environment.getMap().getTrackingLayer().add(resultGeoline[j], TAG_LineInterruptByPoint);

                            newGeometry = resultGeoline[j];
                            newGeometry.setStyle(tempGeometry.getStyle());
                            sourceRecordset.addNew(newGeometry, values);
                            sourceRecordset.update();
                            succeededIDs.add(sourceRecordset.getID());
                            environment.getMapControl().getEditHistory().add(EditType.ADDNEW, sourceRecordset, true);
                        }
                    }
                } catch (Exception ex) {
                    Application.getActiveApplication().getOutput().output(ex.toString());
                }
            }
            if (delete != null) {// 更新数据集
                delete.update();
                environment.getActiveEditableLayer().getSelection().clear();
                environment.getActiveEditableLayer().getSelection().addRange(ArrayUtilities.convertToInt(succeededIDs.toArray(new Integer[succeededIDs.size()])));
            }
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex.toString());
        } finally {
            // 刷新一下桌面的属性表窗口
	        TabularUtilities.refreshTabularStructure(sourceRecordset.getDataset());
	        environment.getMapControl().getEditHistory().batchEnd();
            environment.getMap().refreshTrackingLayer();
            environment.getMap().refresh();

            if (sourceRecordset != null) {
                sourceRecordset.close();
                sourceRecordset.dispose();
            }
        }
    }

    private class LineInterruptModel implements IEditModel {
        public Action oldMapControlAction = Action.SELECT2;
        public TrackMode oldTrackMode = TrackMode.EDIT;
        public MapControlTip tip = new MapControlTip();
        private JLabel tipLabel = new JLabel(MapEditorProperties.getString("String_LineInterrupt"));
        public Geometry geometry = null;
        ArrayList<Integer> hasCommonNodeLineIDs = new ArrayList<Integer>();

        public LineInterruptModel() {
            this.tip.addLabel(this.tipLabel);
        }

        public void setTipMessage(String tipMessage) {
            this.tipLabel.setText(tipMessage);
            this.tipLabel.repaint();
        }

        //清除时要恢复原来的操作与跟踪层的模式
        public void clear() {
            this.oldMapControlAction = Action.SELECT2;
            this.oldTrackMode = TrackMode.EDIT;
            this.geometry = null;
            this.hasCommonNodeLineIDs.clear();
            this.tipLabel.setText(MapEditorProperties.getString("String_LineInterrupt"));
        }

        public void setHasCommonNodeLineIDs(ArrayList<Integer> iDs) {
            this.hasCommonNodeLineIDs = iDs;
        }
    }
}
