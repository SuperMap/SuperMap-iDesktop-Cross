package com.supermap.desktop;

import com.supermap.data.*;
import com.supermap.desktop.Interface.*;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.ToolbarUIUtilities;
import com.supermap.desktop.dialog.DialogAddMap;
import com.supermap.desktop.dialog.DialogSaveAsLayout;
import com.supermap.desktop.enums.WindowType;
import com.supermap.desktop.event.FormClosingEvent;
import com.supermap.desktop.ui.FormBaseChild;
import com.supermap.desktop.ui.UICommonToolkit;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.layout.LayoutElements;
import com.supermap.layout.LayoutSelection;
import com.supermap.ui.*;
import com.supermap.ui.Action;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static com.supermap.data.GeometryType.GEONORTHARROW;

public class FormLayout extends FormBaseChild implements IFormLayout {

	private MapLayoutControl mapLayoutControl = null;
	private String title = "";
	JScrollPane jScrollPaneChildWindow = null;

	private boolean isRegisterEvents = false;
	private static int selectedGeoMapID = -1;
	private static String selectedMapName = "";
	private Geometry firstSelectedGeometry;
	private LayoutSelection originSelection;
	private static int addedGeoScaleID = -1;
	private static int addMapGeoNorthArrow = -1;

	// 布局窗口右键菜单
	private JPopupMenu formLayoutContextMenu;

	public JPopupMenu getFormLayoutContextMenu() {
		return formLayoutContextMenu;
	}

	// 复合布局元素菜单
	private JPopupMenu layoutElementsContextMenu;

	public JPopupMenu getLayoutElementsContextMenu() {
		return layoutElementsContextMenu;
	}

	// 对象右键菜单
	private JPopupMenu layoutGeometryContextMenu;

	public JPopupMenu getLayoutGeometryContextMenu() {
		return layoutGeometryContextMenu;
	}

	private JPopupMenu layoutGeopictureContextMenu;

	public JPopupMenu getLayoutGeopictureContextMenu() {
		return layoutGeopictureContextMenu;
	}

	private JPopupMenu layoutTextObjContextMenu;

	public JPopupMenu getLayoutTextObjContextMenu() {
		return layoutTextObjContextMenu;
	}

	private JPopupMenu layoutMapObjContextMenu;

	public JPopupMenu getLayoutMapObjContextMenu() {
		return layoutMapObjContextMenu;
	}

	private JPopupMenu layoutMapLegendObjContextMenu;

	public JPopupMenu getLayoutMapLegendObjContextMenu() {
		return layoutMapLegendObjContextMenu;
	}

	private JPopupMenu layoutMapScaleObjContextMenu;

	public JPopupMenu getLayoutMapScaleObjContextMenu() {
		return layoutMapScaleObjContextMenu;
	}

	private JPopupMenu layoutMapNorthArrowObjContextMenu;

	public JPopupMenu getLayoutMapNorthArrowObjContextMenuMap() {
		return layoutMapNorthArrowObjContextMenu;
	}

	private JPopupMenu layoutGeoArtTextObjContextMenu;

	public JPopupMenu getLayoutGeoArtTextObjContextMenu() {
		return this.layoutGeoArtTextObjContextMenu;
	}

	private transient MouseListener layoutControl_MouseListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			int buttonType = e.getButton();
			int clickCount = e.getClickCount();

			if ((buttonType == MouseEvent.BUTTON3 && clickCount == 1)
					&& (getMapLayoutControl().getLayoutAction() == Action.SELECT || getMapLayoutControl().getLayoutAction() == Action.SELECT2 || getMapLayoutControl()
					.getLayoutAction() == Action.SELECTCIRCLE)) {
				showPopupMenu(e);
			}

			if (buttonType == MouseEvent.BUTTON1 && (getMapLayoutControl().getLayoutAction() == Action.SELECT || getMapLayoutControl().getLayoutAction() == Action.SELECT2)){
				elementSelectedChange(new ElementSelectedEvent(mapLayoutControl, mapLayoutControl.getMapLayout().getSelection().getCount()));
			}
		}
	};

	public FormLayout() {
		this("");
	}

	public FormLayout(String name) {
		this(name, null, null);
	}

	public FormLayout(String title, Icon icon, Component component) {
		super(title, icon, component);

		this.title = title;
		this.mapLayoutControl = new MapLayoutControl();
		this.mapLayoutControl.getMapLayout().setWorkspace(Application.getActiveApplication().getWorkspace());
		jScrollPaneChildWindow = new JScrollPane(this.mapLayoutControl);
		setLayout(new BorderLayout());
		add(jScrollPaneChildWindow, BorderLayout.CENTER);

		if (Application.getActiveApplication().getMainFrame() != null) {
			IContextMenuManager manager = Application.getActiveApplication().getMainFrame().getContextMenuManager();
			this.formLayoutContextMenu = (JPopupMenu) manager.get("SuperMap.Desktop._FormLayout.FormLayoutContextMenu");
			this.layoutElementsContextMenu = (JPopupMenu) manager.get("SuperMap.Desktop._FormLayout.LayoutElementsContextMenu");
			this.layoutGeometryContextMenu = (JPopupMenu) manager.get("SuperMap.Desktop._FormLayout.LayoutGeometryContextMenu");
			this.layoutGeopictureContextMenu = (JPopupMenu) manager.get("SuperMap.Desktop._FormLayout.LayoutGeopictureContextMenu");
			this.layoutTextObjContextMenu = (JPopupMenu) manager.get("SuperMap.Desktop._FormLayout.LayoutTextObjContextMenu");
			this.layoutMapObjContextMenu = (JPopupMenu) manager.get("SuperMap.Desktop._FormLayout.LayoutMapObjContextMenu");
			this.layoutMapLegendObjContextMenu = (JPopupMenu) manager.get("SuperMap.Desktop._FormLayout.LayoutMapLegendObjContextMenu");
			this.layoutMapScaleObjContextMenu = (JPopupMenu) manager.get("SuperMap.Desktop._FormLayout.LayoutMapScaleObjContextMenu");
			this.layoutMapNorthArrowObjContextMenu = (JPopupMenu) manager.get("SuperMap.Desktop._FormLayout.LayoutMapNorthArrowObjContextMenuMap");
			this.layoutGeoArtTextObjContextMenu = (JPopupMenu) manager.get("SuperMap.Desktop._FormLayout.LayoutGeoArtTextObjContextMenu");
		}
		registerEvents();
	}

	private void registerEvents() {
		if (!this.isRegisterEvents) {
			this.isRegisterEvents = true;
			this.mapLayoutControl.addMouseListener(layoutControl_MouseListener);
			this.mapLayoutControl.addElementAddedListener(this.elementAddedListener);
			this.mapLayoutControl.addTrackedListener(this.trackedListener);
			this.mapLayoutControl.addElementSelectedListener(this.elementSelectedListener);
		}
	}

	private void unRegisterEvents() {
		this.isRegisterEvents = false;
		this.mapLayoutControl.removeMouseListener(layoutControl_MouseListener);
		this.mapLayoutControl.removeElementAddedListener(this.elementAddedListener);
		this.mapLayoutControl.removeTrackedListener(this.trackedListener);
		this.mapLayoutControl.removeElementSelectedListener(this.elementSelectedListener);
	}

	@Override
	public void actived() {
		try {
			registerEvents();
		} catch (Exception e) {

		}
	}

	@Override
	public void deactived() {
		try {
			unRegisterEvents();
		} catch (Exception e) {

		}
	}

	@Override
	public WindowType getWindowType() {
		return WindowType.LAYOUT;
	}

	@Override
	public MapLayoutControl getMapLayoutControl() {
		return this.mapLayoutControl;
	}

	@Override
	public Geometry getFirstSelectedGeometry() {
		return this.firstSelectedGeometry;
	}

	@Override
	public boolean save() {
		Boolean result = false;
		try {
			if (this.isNeedSave()) {
				Workspace workspace = this.mapLayoutControl.getMapLayout().getWorkspace();

				if (workspace.getLayouts().indexOf(this.getText()) >= 0) {
					result = workspace.getLayouts().setLayoutXML(this.getText(), this.mapLayoutControl.getMapLayout().toXML());
				} else {
					result = save(true, true);
				}

				if (result) {
					this.mapLayoutControl.getMapLayout().setModified(false);
				}
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}

		return result;
	}

	@Override
	public boolean save(boolean notify, boolean isNewWindow) {
		Boolean result = false;
		try {
			if (this.isNeedSave()) {
				Workspace workspace = this.mapLayoutControl.getMapLayout().getWorkspace();
				if (workspace != null) {
					if (notify) {
						result = this.saveAs(isNewWindow);
					} else {
						result = workspace.getLayouts().add(this.getText(), this.mapLayoutControl.getMapLayout().toXML()) >= 0;
					}
				}

				if (result) {
					this.mapLayoutControl.getMapLayout().setModified(false);
				}
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}

		return result;
	}

	@Override
	public boolean saveAs(boolean isNewWindow) {
		boolean result = false;
		try {
			Workspace workspace = this.mapLayoutControl.getMapLayout().getWorkspace();
			DialogSaveAsLayout dialogSaveAs = new DialogSaveAsLayout();
			dialogSaveAs.setLayouts(workspace.getLayouts());
			dialogSaveAs.setLayoutName(this.getText());
			dialogSaveAs.setIsNewWindow(isNewWindow);

			if (dialogSaveAs.showDialog() == DialogResult.YES) {
				this.mapLayoutControl.getMapLayout().setName(dialogSaveAs.getLayoutName());
				result = workspace.getLayouts().add(dialogSaveAs.getLayoutName(), this.mapLayoutControl.getMapLayout().toXML()) >= 0;
				if (result) {
					this.setText(dialogSaveAs.getLayoutName());
				}
			} else {
				result = false;
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}

		return result;
	}

	@Override
	public boolean isNeedSave() {
		return this.mapLayoutControl.getMapLayout().isModified();
	}

	@Override
	public void setNeedSave(boolean needSave) {
		this.mapLayoutControl.getMapLayout().setModified(needSave);
	}

	@Override
	public boolean saveFormInfos() {
		// TODO Auto-generated method stub
		return false;
	}

	public void geometryViewEntire() {
		Geometry geoSelected = null;
		try {
			Rectangle2D rcViewBounds = Rectangle2D.getEMPTY();
			LayoutSelection selection = this.getMapLayoutControl().getMapLayout().getSelection();
			LayoutElements elements = this.getMapLayoutControl().getMapLayout().getElements();
			for (int i = 0; i < selection.getCount(); i++) {
				elements.seekID(selection.get(i));
				geoSelected = elements.getGeometry();
				if (rcViewBounds.isEmpty()) {
					rcViewBounds = geoSelected.getBounds();
				} else {
					rcViewBounds.union(geoSelected.getBounds());
				}
			}
			if (rcViewBounds != Rectangle2D.getEMPTY()) {
				this.getMapLayoutControl().getMapLayout().setViewBounds(rcViewBounds);
				this.getMapLayoutControl().getMapLayout().refresh();
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}

		if (geoSelected != null) {
			geoSelected.dispose();
		}
	}

	private void showPopupMenu(MouseEvent e) {
		Geometry geoSelElement = null;
		try {
			JPopupMenu contextMenuStrip = null;
			int selCount = this.getMapLayoutControl().getMapLayout().getSelection().getCount();
			if (selCount > 0) {
				this.getMapLayoutControl().getMapLayout().getElements().seekID(this.getMapLayoutControl().getMapLayout().getSelection().get(0));
				geoSelElement = this.getMapLayoutControl().getMapLayout().getElements().getGeometry();
			}

			Boolean bSameType = true;
			if (geoSelElement == null) {
				contextMenuStrip = formLayoutContextMenu;
				bSameType = false;
			} else if (selCount > 1) {
				contextMenuStrip = layoutGeometryContextMenu;
				for (int i = 1; i < selCount; i++) {
					this.getMapLayoutControl().getMapLayout().getElements().seekID(this.getMapLayoutControl().getMapLayout().getSelection().get(i));
					Geometry geoElement = this.getMapLayoutControl().getMapLayout().getElements().getGeometry();
					if (geoSelElement.getType() != geoElement.getType()) {
						if (geoSelElement.getType() == GeometryType.GEOMAP || geoSelElement.getType() == GeometryType.GEOLEGEND
								|| geoSelElement.getType() == GeometryType.GEOMAPSCALE || geoSelElement.getType() == GEONORTHARROW) {
							contextMenuStrip = layoutElementsContextMenu;
							bSameType = false;
							break;
						} else if (geoElement.getType() == GeometryType.GEOMAP || geoElement.getType() == GeometryType.GEOLEGEND
								|| geoElement.getType() == GeometryType.GEOMAPSCALE || geoElement.getType() == GEONORTHARROW) {
							contextMenuStrip = layoutElementsContextMenu;
							bSameType = false;
							break;
						}
					}
				}
			}
			// else
			if (bSameType) {
				if (geoSelElement.getType() == GeometryType.GEOTEXT) {
					contextMenuStrip = layoutTextObjContextMenu;
				} else if (geoSelElement.getType() == GeometryType.GEOMAP) {
					contextMenuStrip = layoutMapObjContextMenu;
				} else if (geoSelElement.getType() == GeometryType.GEOLEGEND) {
					contextMenuStrip = layoutMapLegendObjContextMenu;
				} else if (geoSelElement.getType() == GeometryType.GEOMAPSCALE) {
					contextMenuStrip = layoutMapScaleObjContextMenu;
				} else if (geoSelElement.getType() == GEONORTHARROW) {
					contextMenuStrip = layoutMapNorthArrowObjContextMenu;
				} else if (geoSelElement.getType() == GeometryType.GEOARC) {
					contextMenuStrip = layoutGeoArtTextObjContextMenu;
				} else if (geoSelElement.getType() == GeometryType.GEOPICTURE) {
					contextMenuStrip = layoutGeopictureContextMenu;
				} else {
					contextMenuStrip = layoutGeometryContextMenu;
				}
			}

			contextMenuStrip.show((Component) this.getMapLayoutControl(), (int) e.getPoint().getX(), (int) e.getPoint().getY());
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}

		if (geoSelElement != null) {
			geoSelElement.dispose();
			geoSelElement = null;
		}
	}

	@Override
	public void formClosing(FormClosingEvent e) {
		try {
			if (GlobalParameters.isShowFormClosingInfo()) {
				boolean isNeedSave = this.mapLayoutControl.getMapLayout().isModified();
				String message = String.format(ControlsProperties.getString("String_SaveLayoutPrompt"), getText());

				if (isNeedSave) {
					int result = GlobalParameters.isShowFormClosingInfo() ? UICommonToolkit.showConfirmDialogWithCancel(message) : JOptionPane.NO_OPTION;
					if (result == JOptionPane.YES_OPTION) {
						save();
						clean();
					} else if (result == JOptionPane.NO_OPTION) {
						// 不保存，直接关闭
						clean();
					} else if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
						// 取消关闭操作
						e.setCancel(true);
					}
				} else {
					clean();
				}
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	private TrackedListener trackedListener = new TrackedListener() {
		@Override
		public void tracked(TrackedEvent trackedEvent) {
			trackedFunction(trackedEvent);
		}
	};

	private ElementAddedListener elementAddedListener = new ElementAddedListener() {
		@Override
		public void elementAdded(ElementEvent elementEvent) {
			elementAdd(elementEvent);
		}
	};

	private ElementDeletedListener elementDeletedListener = new ElementDeletedListener() {
		@Override
		public void elementDeleted(ElementEvent elementEvent) {

		}
	};

	private ElementSelectedListener elementSelectedListener = new ElementSelectedListener() {
		@Override
		public void elementSelected(ElementSelectedEvent elementSelectedEvent) {
			elementSelectedChange(elementSelectedEvent);
		}
	};

	private void elementSelectedChange(ElementSelectedEvent elementSelectedEvent) {
		try {
			this.mapLayoutControl.getMapLayout().getElements().refresh();
			if (this.firstSelectedGeometry != null) {
				this.firstSelectedGeometry.dispose();
				this.firstSelectedGeometry = null;
			}
			LayoutSelection layoutSelection = this.mapLayoutControl.getMapLayout().getSelection();
			if (layoutSelection.getCount() > 0) {
//				if(this.mapLayoutControl.getMapLayout().getElements().moveTo(layoutSelection.get(0))){
				this.mapLayoutControl.getMapLayout().getElements().seekID(layoutSelection.get(0));
				this.firstSelectedGeometry = this.mapLayoutControl.getMapLayout().getElements().getGeometry().clone();
//				}
			} else {
				this.firstSelectedGeometry = null;
			}

//			LayoutSelection selection = this.mapLayoutControl.getMapLayout().getSelection();
//			Boolean changed = false;
//			if (this.originSelection == null) {
//				if (selection != null) {
//					changed = true;
//				}
//			} else if (this.originSelection.getCount() == selection.getCount()) {
//				for (int j = 0; j < this.originSelection.getCount(); j++) {
//					if (this.originSelection.get(j) != selection.get(j)) {
//						changed = true;
//						break;
//					}
//				}
//			} else {
//				changed = true;
//			}
//
//			if (changed && elementSelectedEvent != null) {
//				this.originSelection = new LayoutSelection(selection);
//				elementSelectedChange(elementSelectedEvent);
//			}
			ToolbarUIUtilities.updataToolbarsState();
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	private void elementAdd(ElementEvent elementEvent) {
		try {
			LayoutElements elements = this.mapLayoutControl.getMapLayout().getElements();
			elements.refresh();
//			elements.moveFirst();
//			for (int i=0;i<elements.getCount();i++){
//				System.out.println(elements.getID());
//				elements.moveNext();
//			}
//			elements.moveFirst();
			boolean isFindGeometry = elements.seekID(elementEvent.getID());
			if (isFindGeometry && this.mapLayoutControl.getTrackMode() == TrackMode.EDITGEOMAP) {
				GeoMap map = (GeoMap) elements.getGeometry();
				if (map != null) {
					if (selectedMapName.length() == 0) {
						elements.seekID(elementEvent.getID());
						elements.delete();
						selectedGeoMapID = -1;
					} else {
						map.setMapName(selectedMapName);
						String mapXML = Application.getActiveApplication().getWorkspace().getMaps().getMapXML(selectedMapName);
						int startIndex = mapXML.indexOf("<sml:CoordinateReferenceSystem>");
						int endIndex = mapXML.indexOf("</sml:CoordinateReferenceSystem>");
						String prjXML = mapXML.substring(startIndex, endIndex + 32);
						PrjCoordSys prj = new PrjCoordSys();
						prj.fromXML(prjXML);
						if (prj.getType() == PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
							map.getMapGrid().setGridType(GeoMapGridType.GRATICULE);
						} else {
							map.getMapGrid().setGridType(GeoMapGridType.MEASUREDGRID);
						}

						isFindGeometry = elements.setGeometry(map);
						selectedGeoMapID = elementEvent.getID();
					}
				}
				this.mapLayoutControl.setLayoutAction(Action.SELECT2);
				this.mapLayoutControl.setTrackMode(TrackMode.EDIT);
//				ToolbarUIUtilities.updataToolbarsState();
			} else if (isFindGeometry) {
				Geometry geometry = elements.getGeometry();
				if (geometry.getType() == GeometryType.GEONORTHARROW) {
					GeoNorthArrow geoNorthArrow = (GeoNorthArrow) geometry;
					if (geoNorthArrow != null) {
						geoNorthArrow.setBindingGeoMapID(selectedGeoMapID);
						elements.setGeometry(geoNorthArrow);
					}
					this.mapLayoutControl.setLayoutAction(Action.SELECT2);
					this.mapLayoutControl.setTrackMode(TrackMode.EDIT);
				} else if (geometry.getType() == GeometryType.GEOMAPSCALE) {
					GeoMapScale geoScale = (GeoMapScale) geometry;
					if (geoScale != null) {
						geoScale.setBindingGeoMapID(selectedGeoMapID);
						geoScale.getTextStyle().setBackColor(Color.white);
						elements.setGeometry(geoScale);
					}
					this.mapLayoutControl.setLayoutAction(Action.SELECT2);
					this.mapLayoutControl.setTrackMode(TrackMode.EDIT);
				} else if (geometry.getType() == GeometryType.GEOLEGEND) {
					GeoLegend legend = (GeoLegend) geometry;
					if (legend != null) {
						legend.setMapName(selectedMapName);
						legend.getItemTextStyle().setBackColor(Color.white);
						legend.getSubItemTextStyle().setBackColor(Color.white);
						legend.getTitleStyle().setBackColor(Color.white);
						legend.load(false);
					}
					elements.setGeometry(legend);
					this.mapLayoutControl.setLayoutAction(Action.SELECT2);
					this.mapLayoutControl.setTrackMode(TrackMode.EDIT);
				}
			}
			this.mapLayoutControl.getMapLayout().refresh();
			elementSelectedChange(new ElementSelectedEvent(this.mapLayoutControl, this.mapLayoutControl.getMapLayout().getSelection().getCount()));
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	private void trackedFunction(TrackedEvent trackedEvent) {
		try {
			this.mapLayoutControl.getMapLayout().setModified(true);
			if (this.mapLayoutControl.getLayoutAction() == Action.CREATE_ALONG_LINE_TEXT) {
				if (trackedEvent.getGeometry().getType() == GeometryType.GEOCOMPOUND) {
//					SetAlongLineText(e.Geometry as GeoCompound);
				}
			} else if (this.mapLayoutControl.getTrackMode() == TrackMode.EDITGEOMAP) {
				setMap((GeoMap) trackedEvent.getGeometry());
			}
//			else if (m_mapLayoutControl.CustomElementType == CustomElementType.Picture
//					&& m_mapLayoutControl.TrackMode == TrackMode.Track) {
//				try {
//					SetPicture();
//					GeoRegion geoRegion = e.Geometry as GeoRegion;
//					Rectangle2D rectangle2D = geoRegion.Bounds;
//					if (System.IO.File.Exists(m_path)) {
//						GeoPicture geoPicture = new GeoPicture(m_path, rectangle2D, 0);
//						if (m_isPictureOriginSize) {
//							Image image = Image.FromFile(m_path);
//							Rectangle2D Originrect = new Rectangle2D(new Point2D(rectangle2D.Left, rectangle2D.Bottom), image.Width, image.Height);
//							geoPicture = new GeoPicture(m_path, Originrect, 0);
//							image.Dispose();
//						}
//						m_mapLayoutControl.MapLayout.Elements.AddNew(geoPicture);
//					}
//					m_mapLayoutControl.LayoutAction = SuperMap.UI.Action.Select2;
//					m_mapLayoutControl.TrackMode = TrackMode.Edit;
//					m_mapLayoutControl.CustomElementType = CustomElementType.Null;
//				} catch (Exception ex) {
//					SuperMap.Desktop.Application.ActiveApplication.Output.Output(ex);
//				}
//			} else {
//
//			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	private void setMap(GeoMap geoMap) {
		try {
			DialogAddMap dialogAddMap = new DialogAddMap();
			DialogResult result = dialogAddMap.showDialog();
			if (result == DialogResult.OK) {
				String strMapName = dialogAddMap.getSelectedMapName();
				if (geoMap != null) {
					selectedMapName = strMapName;
				}

			} else {
				selectedMapName = "";
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

}
