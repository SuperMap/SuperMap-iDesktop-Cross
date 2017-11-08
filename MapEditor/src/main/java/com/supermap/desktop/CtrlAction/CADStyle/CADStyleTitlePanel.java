package com.supermap.desktop.CtrlAction.CADStyle;

import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.ControlsResources;
import com.supermap.desktop.controls.utilities.SymbolDialogFactory;
import com.supermap.desktop.dialog.symbolDialogs.ISymbolApply;
import com.supermap.desktop.dialog.symbolDialogs.JpanelSymbols.*;
import com.supermap.desktop.dialog.symbolDialogs.SymbolDialog;
import com.supermap.desktop.enums.SymbolMarkerType;
import com.supermap.desktop.event.ResourcesChangedEvent;
import com.supermap.desktop.event.ResourcesChangedListener;
import com.supermap.desktop.mapeditor.MapEditorProperties;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.utilities.CursorUtilities;
import com.supermap.desktop.utilities.GeometryUtilities;
import com.supermap.desktop.utilities.MapUtilities;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xie on 2016/8/26.
 */
public class CADStyleTitlePanel extends JPanel {
    private final CADStyleContainer parent;
    private int styleType;
    private Color COLOR_SYSTEM_DEFAULT;
    private Color COLOR_SYSTEM_SELECTED = new Color(185, 214, 244);
    public static final int GEOPOINTTYPE = 0;
    public static final int GEOLINETYPE = 1;
    public static final int GEOREGIONTYPE = 2;
    private JPanel panelMore;
    private JLabel buttonMoreImage;
    private JLabel labelTitle;
    private EditHistory editHistory;
    private JPanelSymbols panelSymbols;
    private JScrollPane jScrollPane;
    private GeoStyle geoStyle;
    private ArrayList<Recordset> tempRecordsets;
    private Map<JPanelSymbols, SymbolSelectedChangedListener> listenerMap = new HashMap<>();

    // FIXME: 2016/11/14 UGDJ-530
    // 关闭或打开工作空间时重新加载
    private WorkspaceClosedListener workSpaceClosedListener = new WorkspaceClosedListener() {
        @Override
        public void workspaceClosed(WorkspaceClosedEvent workspaceClosedEvent) {

            workSpaceChanged(Application.getActiveApplication().getWorkspace());
        }
    };

    private WorkspaceOpenedListener workSpaceOpenedListener = new WorkspaceOpenedListener() {

        @Override
        public void workspaceOpened(WorkspaceOpenedEvent workspaceOpenedEvent) {
            try {
                Method reset = SymbolGroup.class.getDeclaredMethod("reset");
                reset.setAccessible(true);
                reset.invoke(Application.getActiveApplication().getWorkspace().getResources().getMarkerLibrary().getRootGroup());
                reset.invoke(Application.getActiveApplication().getWorkspace().getResources().getLineLibrary().getRootGroup());
                reset.invoke(Application.getActiveApplication().getWorkspace().getResources().getFillLibrary().getRootGroup());
                reset.setAccessible(false);
            } catch (Exception e) {
                Application.getActiveApplication().getOutput().output(e);
            }
            workSpaceChanged(workspaceOpenedEvent.getWorkspace());
        }
    };
    private ResourcesChangedListener resourcesChangeListener = new ResourcesChangedListener() {
        @Override
        public void resourcesChanged(ResourcesChangedEvent e) {
            if (e.getSymbolGroup().getCount() > 0 && e.getSymbolGroup().get(0).getType().equals(SymbolType.MARKER)) {
                panelSymbols.setSymbolGroup(e.getNewResources(), e.getSymbolGroup());
            }
        }
    };

    public CADStyleTitlePanel(CADStyleContainer parent, int styleType) {
        this.parent = parent;
        this.styleType = styleType;
        this.COLOR_SYSTEM_DEFAULT = getBackground();
        initComponents(Application.getActiveApplication().getWorkspace());
        initResources();
        registEvents();
        Application.getActiveApplication().getWorkspace().addClosedListener(workSpaceClosedListener);
        Application.getActiveApplication().getWorkspace().addOpenedListener(workSpaceOpenedListener);
    }

    public void enabled(boolean enabled) {
        this.panelSymbols.setVisible(enabled);
        this.buttonMoreImage.setEnabled(enabled);
        this.labelTitle.setEnabled(enabled);
        this.jScrollPane.setVisible(enabled);
        this.buttonMoreImage.setVisible(enabled);
        this.labelTitle.setVisible(enabled);
    }

    public void registEvents() {
        if (null != panelSymbols) {
            SymbolSelectedChangedListener listener = new SymbolSelectedChangedListener() {
                @Override
                public void SymbolSelectedChangedEvent(Symbol symbol) {
                    resetSymbol(symbol);
                    parent.setModify(true);
                }

                @Override
                public void SymbolSelectedDoubleClicked() {

                }
            };
            this.panelSymbols.addSymbolSelectedChangedListener(listener);
            Application.getActiveApplication().addResourcesChangedListener(this.resourcesChangeListener);
            listenerMap.put(panelSymbols, listener);
        }
    }

    private void workSpaceChanged(Workspace workspace) {
        initComponents(workspace);
    }

    private void initResources() {
        switch (styleType) {
            case GEOPOINTTYPE:
                this.setBorder(new TitledBorder(ControlsProperties.getString("String_Point")));
                break;
            case GEOLINETYPE:
                this.setBorder(new TitledBorder(MapEditorProperties.getString("String_Line")));
                break;
            case GEOREGIONTYPE:
                this.setBorder(new TitledBorder(MapEditorProperties.getString("String_Fill")));
                break;
            default:
                break;
        }
    }

    private void initComponents(Workspace workSpace) {


        Resources resources = workSpace.getResources();
        if (styleType == GEOPOINTTYPE) {
            panelSymbols = new JPanelSymbolsPoint();
            panelSymbols.setSymbolGroup(resources, resources.getMarkerLibrary().getRootGroup());
            setLayout(panelSymbols);
            this.setPreferredSize(new Dimension(160, 300));
        } else if (styleType == GEOLINETYPE) {
            panelSymbols = new JPanelSymbolsLine();
            panelSymbols.setSymbolGroup(resources, resources.getLineLibrary().getRootGroup());
            setLayout(panelSymbols);
            this.setPreferredSize(new Dimension(160, 300));
        } else {
            panelSymbols = new JPanelSymbolsFill();
            panelSymbols.setSymbolGroup(resources, resources.getFillLibrary().getRootGroup());
            setLayout(panelSymbols);
            this.setPreferredSize(new Dimension(160, 300));
        }
    }

    public GeoStyle getInitializeGeoStyle() {
        return this.geoStyle;
    }

    private GeoStyle getGeoStyle() {
        GeoStyle result = new GeoStyle();
        ArrayList<Recordset> recordsets = CADStyleUtilities.getActiveRecordset(MapUtilities.getActiveMap());
        if (null == recordsets) {
            return result;
        }
        int recordsetCount = recordsets.size();
        for (int i = 0; i < recordsetCount; i++) {
            Recordset recordset = recordsets.get(i);
            recordset.moveFirst();
            while (!recordset.isEOF()) {
                if (null != recordset.getGeometry() && null != recordset.getGeometry().getStyle()) {
                    result = recordset.getGeometry().getStyle().clone();
                    break;
                } else {
                    recordset.moveNext();
                }
            }
            recordset.dispose();
        }
        return result;
    }

    public void initializeGeoStyle() {
        GeoStyle tempStyle = getGeoStyle();
        panelSymbols.setGeoStyle(tempStyle);
        geoStyle = tempStyle;
    }

    private void resetRecordsetGeoStyle() {
        SymbolType symbolType = SymbolType.MARKER;
        if (styleType == GEOPOINTTYPE) {
            symbolType = SymbolType.MARKER;
        } else if (styleType == GEOLINETYPE) {
            symbolType = SymbolType.LINE;
        } else {
            symbolType = SymbolType.FILL;
        }
        GeoStyle beforeGeostyle = getGeoStyle();
        GeoStyle geostyle = changeGeoStyle(beforeGeostyle, symbolType, new ISymbolApply() {
            @Override
            public void apply(GeoStyle geoStyle) {
                tempRecordsets = CADStyleUtilities.getActiveRecordset(MapUtilities.getActiveMap());
                if (null != tempRecordsets) {
                    resetGeoStyle(geoStyle, tempRecordsets);
                }
            }
        });
        if (geostyle != null) {
            tempRecordsets = CADStyleUtilities.getActiveRecordset(MapUtilities.getActiveMap());
            if (null != tempRecordsets) {
                resetGeoStyle(geostyle, tempRecordsets);
            }
        }
    }

    private GeoStyle changeGeoStyle(GeoStyle beforeStyle, SymbolType symbolType, ISymbolApply symbolApply) {
        GeoStyle result = null;
        SymbolDialog symbolDialog = null;
        try {
            CursorUtilities.setWaitCursor();
            symbolDialog = SymbolDialogFactory.getSymbolDialog(symbolType);
            DialogResult dialogResult = symbolDialog.showDialog(beforeStyle, symbolApply);
            if (dialogResult == DialogResult.OK) {
                result = symbolDialog.getCurrentGeoStyle();
                panelMore.removeMouseListener(null);
            }
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        } finally {
            CursorUtilities.setDefaultCursor();
        }
        return result;
    }

    private void resetSymbol(Symbol symbol) {
        if (null == symbol) {
            if (styleType == GEOREGIONTYPE) {
                parent.setSpinnerFillOpaqueEnable(true);
            } else if (styleType == GEOPOINTTYPE) {
                parent.setSymstemPointEnable(false);
            }
        } else {
            if (styleType == GEOREGIONTYPE) {
                parent.setSpinnerFillOpaqueEnable(false);
            } else if (styleType == GEOPOINTTYPE) {
                parent.setSymstemPointEnable(true);
            }
        }
        if (symbol instanceof SymbolMarker3D || (symbol instanceof SymbolMarker && SymbolMarkerType.getSymbolMarkerType(((SymbolMarker) symbol)).equals(SymbolMarkerType.Raster))) {
            parent.setButtonPointColorEnable(false);
        } else {
            parent.setButtonPointColorEnable(true);
        }
        ArrayList<Recordset> recordsets = CADStyleUtilities.getActiveRecordset(MapUtilities.getActiveMap());
        if (null == recordsets) {
            return;
        }
        int recordsetCount = recordsets.size();
        editHistory = MapUtilities.getMapControl().getEditHistory();
        for (int i = 0; i < recordsetCount; i++) {
            Recordset recordset = recordsets.get(i);
            recordset.moveFirst();
            while (!recordset.isEOF()) {
                editHistory.add(EditType.MODIFY, recordset, true);
                if (!recordset.isReadOnly()) {
                    recordset.edit();
                    Geometry tempGeometry = recordset.getGeometry().clone();
                    GeoStyle geoStyle = tempGeometry.getStyle();
                    if (null == geoStyle) {
                        geoStyle = new GeoStyle();
                    }
                    if (GeometryUtilities.isPointGeometry(tempGeometry) && styleType == GEOPOINTTYPE) {
                        // 修改点符号
                        geoStyle.setMarkerSymbolID(panelSymbols.getCurrentGeoStyle().getMarkerSymbolID());
                    }
                    if ((GeometryUtilities.isLineGeometry(tempGeometry) || GeometryUtilities.isRegionGeometry(tempGeometry)) && styleType == GEOLINETYPE) {
                        geoStyle.setLineSymbolID(panelSymbols.getCurrentGeoStyle().getLineSymbolID());
                    }
                    if (GeometryUtilities.isRegionGeometry(tempGeometry) && styleType == GEOREGIONTYPE) {
                        geoStyle.setFillSymbolID(panelSymbols.getCurrentGeoStyle().getFillSymbolID());
                    }
                    if (!GeometryUtilities.isTextGeometry(tempGeometry) && !tempGeometry.getType().equals(GeometryType.GEOREGION3D)
                            && !tempGeometry.getType().equals(GeometryType.GEOLINE3D) && !tempGeometry.getType().equals(GeometryType.GEOPOINT3D)) {
                        tempGeometry.setStyle(geoStyle);
                    }
                    recordset.setGeometry(tempGeometry);
                    tempGeometry.dispose();
                    recordset.update();
                    recordset.moveNext();
                }
            }
            recordset.close();
            recordset.dispose();
            editHistory.batchEnd();
        }
        MapUtilities.getActiveMap().refresh();
    }

    private void resetGeoStyle(GeoStyle newGeoStyle, ArrayList<Recordset> recordsets) {
        int recordsetCount = recordsets.size();
        editHistory = MapUtilities.getMapControl().getEditHistory();
        for (int i = 0; i < recordsetCount; i++) {
            Recordset tempRecordset = recordsets.get(i);
            tempRecordset.moveFirst();
            while (!tempRecordset.isEOF()) {
                editHistory.add(EditType.MODIFY, tempRecordset, true);
                if (!tempRecordset.isReadOnly()) {
                    tempRecordset.edit();
                    Geometry tempGeometry = tempRecordset.getGeometry();
                    if (!GeometryUtilities.isTextGeometry(tempGeometry) && !tempGeometry.getType().equals(GeometryType.GEOREGION3D)
                            && !tempGeometry.getType().equals(GeometryType.GEOLINE3D) && !tempGeometry.getType().equals(GeometryType.GEOPOINT3D)) {
                        tempGeometry.setStyle(newGeoStyle);
                    }
                    tempRecordset.setGeometry(tempGeometry);
                    tempGeometry.dispose();
                    tempRecordset.update();
                    tempRecordset.moveNext();
                }
            }
            editHistory.batchEnd();
            tempRecordset.close();
            tempRecordset.dispose();
        }
        MapUtilities.getActiveMap().refresh();
    }

    private void setLayout(JPanelSymbols panelSymbols) {
        if (panelMore != null && jScrollPane != null) {
            this.remove(panelMore);
            this.remove(jScrollPane);
        }
        panelMore = new JPanel();
        buttonMoreImage = new JLabel(ControlsResources.getIcon("/controlsresources/Image_SymbolDictionary.png"));
        labelTitle = new JLabel(ControlsProperties.getString("String_SymbolLibraryManager"));
        panelMore.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelMore.add(buttonMoreImage);
        panelMore.add(labelTitle);
        jScrollPane = new JScrollPane();
        jScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        this.setLayout(new GridBagLayout());
        this.add(jScrollPane, new GridBagConstraintsHelper(0, 0, 1, 2).setWeight(1, 1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setWeight(1, 2));
        this.add(panelMore, new GridBagConstraintsHelper(0, 2, 1, 1).setWeight(1, 1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setWeight(0, 0));
        jScrollPane.setViewportView(panelSymbols);
        initializeGeoStyle();
        panelMore.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                resetRecordsetGeoStyle();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                panelMore.setBackground(CADStyleTitlePanel.this.COLOR_SYSTEM_SELECTED);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panelMore.setBackground(CADStyleTitlePanel.this.COLOR_SYSTEM_DEFAULT);
            }
        });
    }

    // FIXME: 2016/12/23 UGDJ-547
    //删除符号选择事件
    public void removeEvents() {
        if (null != listenerMap.get(panelSymbols)) {
            panelSymbols.removeSymbolSelectedChangedListener(listenerMap.get(panelSymbols));
        }
        Application.getActiveApplication().removeResourcesChangedListener(this.resourcesChangeListener);
    }
}
