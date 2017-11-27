package com.supermap.desktop.mapview.map.propertycontrols;

import com.supermap.data.GeoRegion;
import com.supermap.data.Point2D;
import com.supermap.data.Rectangle2D;
import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.Interface.IFormMap;
import com.supermap.desktop.ScaleModel;
import com.supermap.desktop.controls.ControlDefaultValues;
import com.supermap.desktop.exception.InvalidScaleException;
import com.supermap.desktop.mapview.MapViewProperties;
import com.supermap.desktop.ui.SMFormattedTextField;
import com.supermap.desktop.ui.controls.ScaleEditor;
import com.supermap.desktop.ui.controls.button.SmButton;
import com.supermap.desktop.utilities.DoubleUtilities;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapDrawnEvent;
import com.supermap.mapping.MapDrawnListener;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class MapBoundsPropertyControl extends AbstractPropertyControl {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_LABELSIZE = 50;

	private JLabel labelScale;
	private ScaleEditor scaleEditor;
	private JCheckBox checkBoxIsVisibleScalesEnabled; // 设置固定比例尺
	private SmButton buttonSetVisibleScales; // 设置固定比例尺
	private JCheckBox checkBoxIsClipRegionEnabled;
	private SmButton buttonClipRegion;
	private JCheckBox checkBoxIsViewBoundsLocked; // 锁定最大显示范围
	private SmButton buttonSetLockedViewBounds; // 设置最大显示范围
	private JCheckBox checkBoxIsCustomBoundsEnabled; // 自定义全幅范围
	private SmButton buttonSetCustomBounds; // 设置自定义全幅范围

	private JLabel labelCenterX;
	private SMFormattedTextField textFieldCenterX;
	private JLabel labelCenterY;
	private SMFormattedTextField textFieldCenterY;

	private JLabel labelCurrentViewLeft;
	private SMFormattedTextField textFieldCurrentViewLeft;
	private JLabel labelCurrentViewTop;
	private SMFormattedTextField textFieldCurrentViewTop;
	private JLabel labelCurrentViewRight;
	private SMFormattedTextField textFieldCurrentViewRight;
	private JLabel labelCurrentViewBottom;
	private SMFormattedTextField textFieldCurrentViewBottom;

	private JLabel labelMapViewLeft;
	private SMFormattedTextField textFieldMapViewLeft;
	private JLabel labelMapViewTop;
	private SMFormattedTextField textFieldMapViewTop;
	private JLabel labelMapViewRight;
	private SMFormattedTextField textFieldMapViewRight;
	private JLabel labelMapViewBottom;
	private SMFormattedTextField textFieldMapViewBottom;

	private double scale = 0.0;
	public static boolean isVisibleScalesEnabled = false;
	public static double[] visibleScales;
	private boolean isClipRegionEnabled = false;
	private transient GeoRegion clipRegion;
	private boolean isViewBoundsLocked = false;
	private transient Rectangle2D lockedViewBounds = Rectangle2D.getEMPTY();
	private boolean isCustomBoundsEnabled = false;
	private transient Rectangle2D customBounds = Rectangle2D.getEMPTY();

	private double centerX = 0.0;
	private double centerY = 0.0;

	private double currentViewLeft = 0.0;
	private double currentViewTop = 0.0;
	private double currentViewRight = 0.0;
	private double currentViewBottom = 0.0;

	private double mapViewL = 0.0;
	private double mapViewT = 0.0;
	private double mapViewR = 0.0;
	private double mapViewB = 0.0;


	public OperationType operationType = OperationType.NONE;

	private JPopupMenuBounds popupMenuClipRegion = new JPopupMenuBounds(JPopupMenuBounds.CLIP_REGION, Rectangle2D.getEMPTY());
	private JPopupMenuBounds popupMenuLockedViewBounds = new JPopupMenuBounds(JPopupMenuBounds.VIEW_BOUNDS_LOCKED, Rectangle2D.getEMPTY());
	private JPopupMenuBounds popupMenuCustomBounds = new JPopupMenuBounds(JPopupMenuBounds.CUSTOM_BOUNDS, Rectangle2D.getEMPTY());

	public static ScaleEnabledContainer container = new ScaleEnabledContainer();

	public Component currentListenerComponent = null;

	private boolean isIgnoreListener = false; // 如果是在控件初始化值得时候不需要进行Apply操作

	private transient PropertyChangeListener boundsPropertyChangeListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (isIgnoreListener) {
				return;
			}
			if (evt.getPropertyName().equals(JPopupMenuBounds.CLIP_REGION)) {
				MapBoundsPropertyControl.this.clipRegion = popupMenuClipRegion.getGeoRegion();
				verify();
			} else if (evt.getPropertyName().equals(JPopupMenuBounds.VIEW_BOUNDS_LOCKED)) {
				MapBoundsPropertyControl.this.lockedViewBounds = popupMenuLockedViewBounds.getRectangle2D();
				verify();
			} else if (evt.getPropertyName().equals(JPopupMenuBounds.CUSTOM_BOUNDS)) {
				MapBoundsPropertyControl.this.customBounds = popupMenuCustomBounds.getRectangle2D();
				verify();
			}
		}
	};

	private transient MouseListener setMouseListener = new MouseAdapter() {

		@Override
		public void mouseReleased(MouseEvent e) {
			if(isIgnoreListener){
				return;
			}
			JButton button = (JButton) e.getComponent();
			Rectangle Rectangle = button.getBounds();
			if (button == buttonClipRegion && buttonClipRegion.isEnabled()) {
				popupMenuClipRegion.setPreferredSize(new Dimension((int) Rectangle.getWidth(), 140));
				popupMenuClipRegion.show(button, 0, button.getHeight());
			} else if (button == buttonSetLockedViewBounds && buttonSetLockedViewBounds.isEnabled()) {
				popupMenuLockedViewBounds.setPreferredSize(new Dimension((int) Rectangle.getWidth(), 140));
				popupMenuLockedViewBounds.show(button, 0, button.getHeight());
			} else if (button == buttonSetCustomBounds && buttonSetCustomBounds.isEnabled()) {
				popupMenuCustomBounds.setPreferredSize(new Dimension((int) Rectangle.getWidth(), 140));
				popupMenuCustomBounds.show(button, 0, button.getHeight());
			} else if (button == buttonSetVisibleScales && buttonSetVisibleScales.isEnabled() && !container.isVisible()) {
				try {
					container.setScales(visibleScales);
				} catch (InvalidScaleException e1) {
					e1.printStackTrace();
				}
				container.init(MapBoundsPropertyControl.this, getMap());
			}
		}

	};

	private transient ItemListener checkBoxItemListener = new ItemListener() {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (isIgnoreListener) {
				return;
			}
			if (e.getSource() == checkBoxIsVisibleScalesEnabled) {
				checkBoxIsVisibleScalesEnabledCheckedChange();
			} else if (e.getSource() == checkBoxIsClipRegionEnabled) {
				checkBoxIsClipRegionEnabledCheckedChange();
			} else if (e.getSource() == checkBoxIsViewBoundsLocked) {
				checkBoxIsViewBoundsLockedCheckedChange();
			} else if (e.getSource() == checkBoxIsCustomBoundsEnabled) {
				checkBoxIsCustomBoundsEnabledCheckedChange();
			}
		}
	};
	private transient PropertyChangeListener scaleEditorValueChangeListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (isIgnoreListener) {
				return;
			}
			scaleEditorValueChange();
		}
	};
	private transient PropertyChangeListener textFieldPropertyChangeListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (isIgnoreListener) {
				return;
			}
			abstractPropertyChange(evt);
		}

	};
	private transient MapDrawnListener mapDrawnListener = new MapDrawnListener() {

		@Override
		public void mapDrawn(MapDrawnEvent arg0) {
			if (isIgnoreListener) {
				return;
			}
			MapBoundsPropertyControl.this.mapDrawn();
		}
	};

	private void abstractPropertyChange(PropertyChangeEvent evt) {
		if (isIgnoreListener) {
			return;
		}
		if ("value".equalsIgnoreCase(evt.getPropertyName())) {
			if (evt.getSource() == textFieldCenterX) {
				textFieldCenterXValueChange(evt);
			} else if (evt.getSource() == textFieldCenterY) {
				textFieldCenterYValueChange(evt);
			} else if (evt.getSource() == textFieldCurrentViewLeft) {
				textFieldCurrentViewLValueChange(evt);
			} else if (evt.getSource() == textFieldCurrentViewTop) {
				textFieldCurrentViewTValueChange(evt);
			} else if (evt.getSource() == textFieldCurrentViewRight) {
				textFieldCurrentViewRValueChange(evt);
			} else if (evt.getSource() == textFieldCurrentViewBottom) {
				textFieldCurrentViewBValueChange(evt);
			}
		}
	}

	/**
	 * Create the panel.
	 */
	public MapBoundsPropertyControl() {
		super(MapViewProperties.getString("String_TabPage_BoundsSetting"));
	}

	@Override
	public void apply() {
		//
		Map activeMap = getMap();
		IForm activeForm = Application.getActiveApplication().getActiveForm();
		if (activeForm != null && activeForm instanceof IFormMap) {
			((IFormMap) activeForm).setVisibleScalesEnabled(isVisibleScalesEnabled);
		}
		if (isVisibleScalesChanged()) {
			// 没有事件，只能开接口
			if (activeForm != null && activeForm instanceof IFormMap) {
				((IFormMap) activeForm).setVisibleScales(visibleScales);
			}
		}
		activeMap.setClipRegionEnabled(this.isClipRegionEnabled);
		if (activeMap.getClipRegion() != this.clipRegion) {
			activeMap.setClipRegion(this.clipRegion);
		}

		activeMap.setViewBoundsLocked(this.isViewBoundsLocked);
		// 从地图中读取的 lockedViewBounds 默认为 (0,0,0,0)，然而将这个值设置到地图中会抛异常，理论上应该是 (0,0,0,0) 表示无限制
		if (!activeMap.getLockedViewBounds().equals(this.lockedViewBounds) && this.lockedViewBounds.getHeight() > 0 && this.lockedViewBounds.getWidth() > 0) {
			// 设为地图范围时才需要全幅显示
			if (activeMap.getBounds().equals(this.lockedViewBounds)) {
				activeMap.viewEntire();
			}
			activeMap.setLockedViewBounds(this.lockedViewBounds);
			//
		}
		activeMap.setCustomBoundsEnabled(this.isCustomBoundsEnabled);
		if (!activeMap.getCustomBounds().equals(this.customBounds)) {
			activeMap.setCustomBounds(this.customBounds);
			if (isCustomBoundsEnabled) {
				activeMap.viewEntire();
			}
		}

		// 以下三个设置相互之间会影响，所以当前改的是哪个就设置哪个，然后联动刷新地图，刷新控件
		if (operationType == OperationType.SCALE) {
			activeMap.setScale(this.scale);
		} else if (operationType == OperationType.CENTERPOINT) {
			activeMap.setCenter(new Point2D(this.centerX, this.centerY));
		} else if (operationType == OperationType.CURRENTVIEW) {
			ArrayList<SMFormattedTextField> changedBoundTextFields = new ArrayList<>();
			Rectangle2D viewBounds = activeMap.getViewBounds();
			// 找到改变了的范围
			if (!DoubleUtilities.equals(viewBounds.getLeft(), currentViewLeft)) {
				changedBoundTextFields.add(textFieldCurrentViewLeft);
			}
			if (!DoubleUtilities.equals(viewBounds.getBottom(), currentViewBottom)) {
				changedBoundTextFields.add(textFieldCurrentViewBottom);
			}
			if (!DoubleUtilities.equals(viewBounds.getRight(), currentViewRight)) {
				changedBoundTextFields.add(textFieldCurrentViewRight);
			}
			if (!DoubleUtilities.equals(viewBounds.getTop(), currentViewTop)) {
				changedBoundTextFields.add(textFieldCurrentViewTop);
			}

			if (changedBoundTextFields.size() == 1) {
				// 如果只有一项改变，则以这一项为准
				SMFormattedTextField textField = changedBoundTextFields.get(0);
				activeMap.setViewBounds(new Rectangle2D(
						textField == textFieldCurrentViewRight ? currentViewRight - viewBounds.getWidth() : currentViewLeft,
						textField == textFieldCurrentViewTop ? currentViewTop - viewBounds.getHeight() : currentViewBottom,
						textField == textFieldCurrentViewLeft ? currentViewLeft + viewBounds.getWidth() : currentViewRight,
						textField == textFieldCurrentViewBottom ? currentViewBottom + viewBounds.getHeight() : currentViewTop
				));
			} else if (changedBoundTextFields.size() >= 2) {
				activeMap.setViewBounds(new Rectangle2D(this.currentViewLeft, this.currentViewBottom, this.currentViewRight, this.currentViewTop));
			}
		}
		operationType = OperationType.NONE;
		activeMap.refresh();
	}

	@Override
	protected void initializeComponents() {

		this.labelScale = new JLabel("CurrentScale:");
		this.scaleEditor = new ScaleEditor(ScaleModel.NONE_SCALE);
		this.checkBoxIsVisibleScalesEnabled = new JCheckBox("IsVisibleScaleEnabled");
		this.buttonSetVisibleScales = new SmButton("SetVisibleScales");
		this.checkBoxIsClipRegionEnabled = new JCheckBox("IsClipRegionEnabled");
		this.buttonClipRegion = new SmButton("SetClipRegion");
		this.checkBoxIsViewBoundsLocked = new JCheckBox("IsViewBoundsLocked");
		this.buttonSetLockedViewBounds = new SmButton("SetLockedViewBounds");
		this.checkBoxIsCustomBoundsEnabled = new JCheckBox("IsCustomBoundsEnabled");
		this.buttonSetCustomBounds = new SmButton("SetCustomBounds");

		this.labelCenterX = new JLabel("X:");
		this.textFieldCenterX = new SMFormattedTextField();
		this.labelCenterY = new JLabel("Y:");
		this.textFieldCenterY = new SMFormattedTextField();

		this.labelCurrentViewLeft = new JLabel("Left:");
		this.textFieldCurrentViewLeft = new SMFormattedTextField();
		this.labelCurrentViewTop = new JLabel("Top:");
		this.textFieldCurrentViewTop = new SMFormattedTextField();
		this.labelCurrentViewRight = new JLabel("Right:");
		this.textFieldCurrentViewRight = new SMFormattedTextField();
		this.labelCurrentViewBottom = new JLabel("Bottom:");
		this.textFieldCurrentViewBottom = new SMFormattedTextField();

		this.labelMapViewLeft = new JLabel("Left:");
		this.textFieldMapViewLeft = new SMFormattedTextField();
		this.textFieldMapViewLeft.setEditable(false);
		this.labelMapViewTop = new JLabel("Top:");
		this.textFieldMapViewTop = new SMFormattedTextField();
		this.textFieldMapViewTop.setEditable(false);
		this.labelMapViewRight = new JLabel("Right:");
		this.textFieldMapViewRight = new SMFormattedTextField();
		this.textFieldMapViewRight.setEditable(false);
		this.labelMapViewBottom = new JLabel("Bottom:");
		this.textFieldMapViewBottom = new SMFormattedTextField();
		this.textFieldMapViewBottom.setEditable(false);

		// 中心点位置
		JPanel panelCenter = new JPanel();
		panelCenter.setBorder(BorderFactory.createTitledBorder(MapViewProperties.getString("String_GroupBox_MapCenter")));
		GroupLayout gl_panelCenter = new GroupLayout(panelCenter);
		gl_panelCenter.setAutoCreateContainerGaps(true);
		gl_panelCenter.setAutoCreateGaps(true);
		panelCenter.setLayout(gl_panelCenter);

		// @formatter:off
		gl_panelCenter.setHorizontalGroup(gl_panelCenter.createSequentialGroup()
				.addGroup(gl_panelCenter.createParallelGroup(Alignment.LEADING)
						.addComponent(this.labelCenterX, GroupLayout.PREFERRED_SIZE, DEFAULT_LABELSIZE, DEFAULT_LABELSIZE)
						.addComponent(this.labelCenterY, GroupLayout.PREFERRED_SIZE, DEFAULT_LABELSIZE, DEFAULT_LABELSIZE))
				.addGroup(gl_panelCenter.createParallelGroup(Alignment.LEADING)
						.addComponent(this.textFieldCenterX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(this.textFieldCenterY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		
		gl_panelCenter.setVerticalGroup(gl_panelCenter.createSequentialGroup()
				.addGroup(gl_panelCenter.createParallelGroup(Alignment.CENTER)
						.addComponent(this.labelCenterX)
						.addComponent(this.textFieldCenterX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_panelCenter.createParallelGroup(Alignment.CENTER)
						.addComponent(this.labelCenterY)
						.addComponent(this.textFieldCenterY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		// @formatter:on

		// 当前视图范围
		JPanel panelCurrentView = new JPanel();
		panelCurrentView.setBorder(BorderFactory.createTitledBorder(MapViewProperties.getString("String_GroupBox_ViewBounds")));
		GroupLayout gl_panelCurrentView = new GroupLayout(panelCurrentView);
		gl_panelCurrentView.setAutoCreateContainerGaps(true);
		gl_panelCurrentView.setAutoCreateGaps(true);
		panelCurrentView.setLayout(gl_panelCurrentView);

		// @formatter:off
		gl_panelCurrentView.setHorizontalGroup(gl_panelCurrentView.createSequentialGroup()
				.addGroup(gl_panelCurrentView.createParallelGroup(Alignment.LEADING)
						.addComponent(this.labelCurrentViewLeft, GroupLayout.PREFERRED_SIZE, DEFAULT_LABELSIZE, DEFAULT_LABELSIZE)
						.addComponent(this.labelCurrentViewTop, GroupLayout.PREFERRED_SIZE, DEFAULT_LABELSIZE, DEFAULT_LABELSIZE)
						.addComponent(this.labelCurrentViewRight, GroupLayout.PREFERRED_SIZE, DEFAULT_LABELSIZE, DEFAULT_LABELSIZE)
						.addComponent(this.labelCurrentViewBottom, GroupLayout.PREFERRED_SIZE, DEFAULT_LABELSIZE, DEFAULT_LABELSIZE))
				.addGroup(gl_panelCurrentView.createParallelGroup(Alignment.LEADING)
						.addComponent(this.textFieldCurrentViewLeft, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(this.textFieldCurrentViewTop, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(this.textFieldCurrentViewRight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(this.textFieldCurrentViewBottom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		
		gl_panelCurrentView.setVerticalGroup(gl_panelCurrentView.createSequentialGroup()
				.addGroup(gl_panelCurrentView.createParallelGroup(Alignment.CENTER)
						.addComponent(this.labelCurrentViewLeft)
						.addComponent(this.textFieldCurrentViewLeft, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_panelCurrentView.createParallelGroup(Alignment.CENTER)
						.addComponent(this.labelCurrentViewTop)
						.addComponent(this.textFieldCurrentViewTop, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_panelCurrentView.createParallelGroup(Alignment.CENTER)
						.addComponent(this.labelCurrentViewRight)
						.addComponent(this.textFieldCurrentViewRight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_panelCurrentView.createParallelGroup(Alignment.CENTER)
						.addComponent(this.labelCurrentViewBottom)
						.addComponent(this.textFieldCurrentViewBottom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		// @formatter:on	

		// 地图范围
		JPanel panelMapView = new JPanel();
		panelMapView.setBorder(BorderFactory.createTitledBorder(MapViewProperties.getString("String_GroupBox_MapBounds")));
		GroupLayout gl_panelMapView = new GroupLayout(panelMapView);
		gl_panelMapView.setAutoCreateContainerGaps(true);
		gl_panelMapView.setAutoCreateGaps(true);
		panelMapView.setLayout(gl_panelMapView);

		// @formatter:off
		gl_panelMapView.setHorizontalGroup(gl_panelMapView.createSequentialGroup()
				.addGroup(gl_panelMapView.createParallelGroup(Alignment.LEADING)
						.addComponent(this.labelMapViewLeft, GroupLayout.PREFERRED_SIZE, DEFAULT_LABELSIZE, DEFAULT_LABELSIZE)
						.addComponent(this.labelMapViewTop, GroupLayout.PREFERRED_SIZE, DEFAULT_LABELSIZE, DEFAULT_LABELSIZE)
						.addComponent(this.labelMapViewRight, GroupLayout.PREFERRED_SIZE, DEFAULT_LABELSIZE, DEFAULT_LABELSIZE)
						.addComponent(this.labelMapViewBottom, GroupLayout.PREFERRED_SIZE, DEFAULT_LABELSIZE, DEFAULT_LABELSIZE))
				.addGroup(gl_panelMapView.createParallelGroup(Alignment.LEADING)
						.addComponent(this.textFieldMapViewLeft, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(this.textFieldMapViewTop, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(this.textFieldMapViewRight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(this.textFieldMapViewBottom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		
		gl_panelMapView.setVerticalGroup(gl_panelMapView.createSequentialGroup()
				.addGroup(gl_panelMapView.createParallelGroup(Alignment.CENTER)
						.addComponent(this.labelMapViewLeft)
						.addComponent(this.textFieldMapViewLeft, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_panelMapView.createParallelGroup(Alignment.CENTER)
						.addComponent(this.labelMapViewTop)
						.addComponent(this.textFieldMapViewTop,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_panelMapView.createParallelGroup(Alignment.CENTER)
						.addComponent(this.labelMapViewRight)
						.addComponent(this.textFieldMapViewRight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_panelMapView.createParallelGroup(Alignment.CENTER)
						.addComponent(this.labelMapViewBottom)
						.addComponent(this.textFieldMapViewBottom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		// @formatter:on

		// 整个面板总体布局
		GroupLayout gl_mainContent = new GroupLayout(this);
		gl_mainContent.setAutoCreateContainerGaps(true);
		gl_mainContent.setAutoCreateGaps(true);
		this.setLayout(gl_mainContent);

		// @formatter:off
		gl_mainContent.setHorizontalGroup(gl_mainContent.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_mainContent.createSequentialGroup()
						.addGroup(gl_mainContent.createParallelGroup(Alignment.LEADING)
								.addComponent(this.labelScale)
								.addComponent(this.checkBoxIsVisibleScalesEnabled)
								.addComponent(this.checkBoxIsClipRegionEnabled)
								.addComponent(this.checkBoxIsViewBoundsLocked)
								.addComponent(this.checkBoxIsCustomBoundsEnabled))
						.addGroup(gl_mainContent.createParallelGroup(Alignment.LEADING)
								.addComponent(this.scaleEditor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(this.buttonSetVisibleScales, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(this.buttonClipRegion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(this.buttonSetLockedViewBounds, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(this.buttonSetCustomBounds, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
				.addComponent(panelCenter)
				.addComponent(panelCurrentView)
				.addComponent(panelMapView));
		
		gl_mainContent.setVerticalGroup(gl_mainContent.createSequentialGroup()
				.addGroup(gl_mainContent.createParallelGroup(Alignment.CENTER)
						.addComponent(this.labelScale)
						.addComponent(this.scaleEditor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_mainContent.createParallelGroup(Alignment.CENTER)
						.addComponent(this.checkBoxIsVisibleScalesEnabled)
						.addComponent(this.buttonSetVisibleScales, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_mainContent.createParallelGroup(Alignment.CENTER)
						.addComponent(this.checkBoxIsClipRegionEnabled)
						.addComponent(this.buttonClipRegion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_mainContent.createParallelGroup(Alignment.CENTER)
						.addComponent(this.checkBoxIsViewBoundsLocked)
						.addComponent(this.buttonSetLockedViewBounds, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_mainContent.createParallelGroup(Alignment.CENTER)
						.addComponent(this.checkBoxIsCustomBoundsEnabled)
						.addComponent(this.buttonSetCustomBounds, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(panelCenter)
				.addComponent(panelCurrentView)
				.addComponent(panelMapView));
		// @formatter:on
	}

	@Override
	protected void initializeResources() {
		this.labelScale.setText(MapViewProperties.getString("String_Label_MapScale"));
		this.checkBoxIsVisibleScalesEnabled.setText(MapViewProperties.getString("String_CheckBox_IsMapScaleFixed"));
		this.buttonSetVisibleScales.setText(MapViewProperties.getString("String_Button_SetVisibleScales"));
		this.checkBoxIsClipRegionEnabled.setText(MapViewProperties.getString("String_CheckBox_IsClipRegionEnabled"));
		this.checkBoxIsViewBoundsLocked.setText(MapViewProperties.getString("String_CheckBox_IsViewBoundsLocked"));
		this.checkBoxIsCustomBoundsEnabled.setText(MapViewProperties.getString("String_CheckBox_IsCustomBoundsEnabled"));
		this.labelCurrentViewLeft.setText(MapViewProperties.getString("String_Left"));
		this.labelCurrentViewTop.setText(MapViewProperties.getString("String_Top"));
		this.labelCurrentViewRight.setText(MapViewProperties.getString("String_Right"));
		this.labelCurrentViewBottom.setText(MapViewProperties.getString("String_Bottom"));
		this.labelMapViewLeft.setText(MapViewProperties.getString("String_Left"));
		this.labelMapViewTop.setText(MapViewProperties.getString("String_Top"));
		this.labelMapViewRight.setText(MapViewProperties.getString("String_Right"));
		this.labelMapViewBottom.setText(MapViewProperties.getString("String_Bottom"));
		this.buttonClipRegion.setText(MapViewProperties.getString("String_LayerGridAggregation_SetingLabel"));
		this.buttonSetLockedViewBounds.setText(MapViewProperties.getString("String_LayerGridAggregation_SetingLabel"));
		this.buttonSetCustomBounds.setText(MapViewProperties.getString("String_LayerGridAggregation_SetingLabel"));
	}

	@Override
	protected void initializePropertyValues(Map map) {
		this.scale = map.getScale();
		isVisibleScalesEnabled = map.isVisibleScalesEnabled();
		visibleScales = map.getVisibleScales();
		this.isClipRegionEnabled = map.isClipRegionEnabled();
		this.clipRegion = map.getClipRegion();
		this.isViewBoundsLocked = map.isViewBoundsLocked();
		this.lockedViewBounds = map.getLockedViewBounds();
		this.isCustomBoundsEnabled = map.isCustomBoundsEnabled();
		this.customBounds = map.getCustomBounds();
		this.centerX = map.getCenter().getX();
		this.centerY = map.getCenter().getY();
		this.currentViewLeft = map.getViewBounds().getLeft();
		this.currentViewTop = map.getViewBounds().getTop();
		this.currentViewRight = map.getViewBounds().getRight();
		this.currentViewBottom = map.getViewBounds().getBottom();
		this.mapViewL = map.getBounds().getLeft();
		this.mapViewT = map.getBounds().getTop();
		this.mapViewR = map.getBounds().getRight();
		this.mapViewB = map.getBounds().getBottom();
		this.popupMenuClipRegion.setGeoRegion(clipRegion);
		this.popupMenuLockedViewBounds.setRectangle2D(lockedViewBounds);
		this.popupMenuCustomBounds.setRectangle2D(customBounds);
	}

	@Override
	protected void registerEvents() {
		super.registerEvents();
		this.scaleEditor.addPropertyChangeListener(ControlDefaultValues.SCALE_PROPERTY_VALUE, this.scaleEditorValueChangeListener);
		this.checkBoxIsVisibleScalesEnabled.addItemListener(this.checkBoxItemListener);
		this.checkBoxIsClipRegionEnabled.addItemListener(this.checkBoxItemListener);
		this.checkBoxIsViewBoundsLocked.addItemListener(this.checkBoxItemListener);
		this.checkBoxIsCustomBoundsEnabled.addItemListener(this.checkBoxItemListener);
		this.textFieldCenterX.addPropertyChangeListener(this.textFieldPropertyChangeListener);
		this.textFieldCenterY.addPropertyChangeListener(this.textFieldPropertyChangeListener);
		this.textFieldCurrentViewLeft.addPropertyChangeListener(this.textFieldPropertyChangeListener);
		this.textFieldCurrentViewTop.addPropertyChangeListener(this.textFieldPropertyChangeListener);
		this.textFieldCurrentViewRight.addPropertyChangeListener(this.textFieldPropertyChangeListener);
		this.textFieldCurrentViewBottom.addPropertyChangeListener(this.textFieldPropertyChangeListener);
		getMap().addDrawnListener(this.mapDrawnListener);
		this.buttonClipRegion.addMouseListener(setMouseListener);
		this.buttonSetLockedViewBounds.addMouseListener(setMouseListener);
		this.buttonSetCustomBounds.addMouseListener(setMouseListener);
		this.buttonSetVisibleScales.addMouseListener(setMouseListener);
		this.popupMenuClipRegion.addPropertyChangeListeners(boundsPropertyChangeListener);
		this.popupMenuLockedViewBounds.addPropertyChangeListeners(boundsPropertyChangeListener);
		this.popupMenuCustomBounds.addPropertyChangeListeners(boundsPropertyChangeListener);
	}

	@Override
	protected void unregisterEvents() {
		super.unregisterEvents();
		this.scaleEditor.removePropertyChangeListener(ControlDefaultValues.PROPERTYNAME_VALUE, this.scaleEditorValueChangeListener);
		this.checkBoxIsVisibleScalesEnabled.removeItemListener(this.checkBoxItemListener);
		this.checkBoxIsClipRegionEnabled.removeItemListener(this.checkBoxItemListener);
		this.checkBoxIsViewBoundsLocked.removeItemListener(this.checkBoxItemListener);
		this.checkBoxIsCustomBoundsEnabled.removeItemListener(this.checkBoxItemListener);
		this.textFieldCenterX.removePropertyChangeListener(this.textFieldPropertyChangeListener);
		this.textFieldCenterY.removePropertyChangeListener(this.textFieldPropertyChangeListener);
		this.textFieldCurrentViewLeft.removePropertyChangeListener(this.textFieldPropertyChangeListener);
		this.textFieldCurrentViewTop.removePropertyChangeListener(this.textFieldPropertyChangeListener);
		this.textFieldCurrentViewRight.removePropertyChangeListener(this.textFieldPropertyChangeListener);
		this.textFieldCurrentViewBottom.removePropertyChangeListener(this.textFieldPropertyChangeListener);
		if (getMap() != null) {
			getMap().removeDrawnListener(this.mapDrawnListener);
		}
		this.buttonClipRegion.removeMouseListener(setMouseListener);
		this.buttonSetLockedViewBounds.removeMouseListener(setMouseListener);
		this.buttonSetCustomBounds.removeMouseListener(setMouseListener);
		this.buttonSetVisibleScales.removeMouseListener(setMouseListener);
		this.popupMenuClipRegion.removePropertyChangeListeners(boundsPropertyChangeListener);
		this.popupMenuLockedViewBounds.removePropertyChangeListeners(boundsPropertyChangeListener);
		this.popupMenuCustomBounds.removePropertyChangeListeners(boundsPropertyChangeListener);
	}

	@Override
	protected void fillComponents() {
		try {
			isIgnoreListener = true;
			this.scaleEditor.setScale(this.scale);
			this.checkBoxIsVisibleScalesEnabled.setSelected(isVisibleScalesEnabled);
			this.checkBoxIsClipRegionEnabled.setSelected(this.isClipRegionEnabled);
			this.checkBoxIsViewBoundsLocked.setSelected(this.isViewBoundsLocked);
			this.checkBoxIsCustomBoundsEnabled.setSelected(this.isCustomBoundsEnabled);
			this.textFieldCenterX.setValue(this.centerX);
			this.textFieldCenterY.setValue(this.centerY);
			this.textFieldCurrentViewLeft.setValue(this.currentViewLeft);
			this.textFieldCurrentViewTop.setValue(this.currentViewTop);
			this.textFieldCurrentViewRight.setValue(this.currentViewRight);
			this.textFieldCurrentViewBottom.setValue(this.currentViewBottom);
			this.textFieldMapViewLeft.setValue(this.mapViewL);
			this.textFieldMapViewTop.setValue(this.mapViewT);
			this.textFieldMapViewRight.setValue(this.mapViewR);
			this.textFieldMapViewBottom.setValue(this.mapViewB);
			isIgnoreListener = false;
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	@Override
	protected void setComponentsEnabled() {
		// this.buttonSetVisibleScales.setEnabled(this.isVisibleScalesEnabled);
		this.buttonClipRegion.setEnabled(this.isClipRegionEnabled);
		this.buttonSetLockedViewBounds.setEnabled(this.isViewBoundsLocked);
		this.buttonSetCustomBounds.setEnabled(this.isCustomBoundsEnabled);
		this.textFieldMapViewLeft.setEditable(false);
		this.textFieldMapViewTop.setEditable(false);
		this.textFieldMapViewRight.setEditable(false);
		this.textFieldMapViewBottom.setEditable(false);
	}

	@Override
	protected boolean verifyChange() {
		return Double.compare(this.scale, getMap().getScale()) != 0 || isVisibleScalesEnabled != getMap().isVisibleScalesEnabled() || !isVisibleScalesChanged()
				|| this.isClipRegionEnabled != getMap().isClipRegionEnabled() || this.clipRegion != getMap().getClipRegion()
				|| this.isViewBoundsLocked != getMap().isViewBoundsLocked() || this.lockedViewBounds != getMap().getLockedViewBounds()
				|| this.isCustomBoundsEnabled != getMap().isCustomBoundsEnabled() || this.customBounds != getMap().getCustomBounds()
				|| Double.compare(this.centerX, getMap().getCenter().getX()) != 0 || Double.compare(this.centerY, getMap().getCenter().getY()) != 0
				|| Double.compare(this.currentViewLeft, getMap().getViewBounds().getLeft()) != 0
				|| Double.compare(this.currentViewTop, getMap().getViewBounds().getTop()) != 0
				|| Double.compare(this.currentViewRight, getMap().getViewBounds().getRight()) != 0
				|| Double.compare(this.currentViewBottom, getMap().getViewBounds().getBottom()) != 0
				|| !this.lockedViewBounds.equals(getMap().getLockedViewBounds()) || !this.customBounds.equals(getMap().getCustomBounds());
	}

	/**
	 * 无论属性是否可用，有更改都会发送属性改变的事件
	 *
	 * @return
	 */
	private boolean isVisibleScalesChanged() {
		boolean result = false;

		if (visibleScales == null && getMap().getVisibleScales() != null) {
			return true;
		}

		if (visibleScales != null && getMap().getVisibleScales() == null) {
			return true;
		}

		if (visibleScales == null && getMap().getVisibleScales() == null) {
			return false;
		}

		if (visibleScales.length != getMap().getVisibleScales().length) {
			return true;
		}

		for (double visibleScale : visibleScales) {
			boolean exist = false;

			for (int j = 0; j < getMap().getVisibleScales().length; j++) {
				if (Double.compare(visibleScale, getMap().getVisibleScales()[j]) == 0) {
					exist = true;
					break;
				}
			}

			if (!exist) {
				result = true;
				break;
			}
		}

		return result;
	}

	private void checkBoxIsVisibleScalesEnabledCheckedChange() {
		try {
			isVisibleScalesEnabled = this.checkBoxIsVisibleScalesEnabled.isSelected();
			// this.buttonSetVisibleScales.setEnabled(this.isVisibleScalesEnabled);
			verify();
		} catch (Exception e2) {
			Application.getActiveApplication().getOutput().output(e2);
		}
	}

	private void checkBoxIsClipRegionEnabledCheckedChange() {
		try {
			this.isClipRegionEnabled = this.checkBoxIsClipRegionEnabled.isSelected();
			this.buttonClipRegion.setEnabled(this.isClipRegionEnabled);
			verify();
		} catch (Exception e2) {
			Application.getActiveApplication().getOutput().output(e2);
		}
	}

	private void checkBoxIsViewBoundsLockedCheckedChange() {
		try {
			this.isViewBoundsLocked = this.checkBoxIsViewBoundsLocked.isSelected();
			this.buttonSetLockedViewBounds.setEnabled(this.isViewBoundsLocked);
			if (this.lockedViewBounds.getHeight() == 0 && this.lockedViewBounds.getWidth() == 0 && getMap().getBounds().getWidth() > 0
					&& getMap().getBounds().getHeight() > 0) {
				this.lockedViewBounds = getMap().getBounds();
			}
			verify();
		} catch (Exception e2) {
			Application.getActiveApplication().getOutput().output(e2);
		}
	}

	private void checkBoxIsCustomBoundsEnabledCheckedChange() {
		try {
			this.isCustomBoundsEnabled = this.checkBoxIsCustomBoundsEnabled.isSelected();
			this.buttonSetCustomBounds.setEnabled(this.isCustomBoundsEnabled);
			verify();
		} catch (Exception e2) {
			Application.getActiveApplication().getOutput().output(e2);
		}
	}

	private void textFieldCenterXValueChange(PropertyChangeEvent e) {
		try {
			this.operationType = OperationType.CENTERPOINT;
			this.centerX = Double.valueOf(e.getNewValue().toString());
			verify();
		} catch (Exception e2) {
			Application.getActiveApplication().getOutput().output(e2);
		}
	}

	private void textFieldCenterYValueChange(PropertyChangeEvent e) {
		try {
			this.operationType = OperationType.CENTERPOINT;
			this.centerY = Double.valueOf(e.getNewValue().toString());
			verify();
		} catch (Exception e2) {
			Application.getActiveApplication().getOutput().output(e2);
		}
	}

	private void textFieldCurrentViewLValueChange(PropertyChangeEvent e) {
		try {
			this.operationType = OperationType.CURRENTVIEW;
			this.currentViewLeft = Double.valueOf(e.getNewValue().toString());
			verify();
		} catch (Exception e2) {
			Application.getActiveApplication().getOutput().output(e2);
		}
	}

	private void textFieldCurrentViewTValueChange(PropertyChangeEvent e) {
		try {
			this.operationType = OperationType.CURRENTVIEW;
			this.currentViewTop = Double.valueOf(e.getNewValue().toString());
			verify();
		} catch (Exception e2) {
			Application.getActiveApplication().getOutput().output(e2);
		}
	}

	private void scaleEditorValueChange() {
		try {
			currentListenerComponent = scaleEditor;
			this.operationType = OperationType.SCALE;
			if (!DoubleUtilities.equals(scaleEditor.getScale(), scale, 16)) {
				this.scale = scaleEditor.getScale();
				verify();
			}
			currentListenerComponent = null;
		} catch (Exception e2) {
			Application.getActiveApplication().getOutput().output(e2);
		}
	}

	private void textFieldCurrentViewRValueChange(PropertyChangeEvent e) {
		try {
			this.operationType = OperationType.CURRENTVIEW;
			this.currentViewRight = Double.valueOf(e.getNewValue().toString());
			verify();
		} catch (Exception e2) {
			Application.getActiveApplication().getOutput().output(e2);
		}
	}

	private void textFieldCurrentViewBValueChange(PropertyChangeEvent e) {
		try {
			this.operationType = OperationType.CURRENTVIEW;
			this.currentViewBottom = Double.valueOf(e.getNewValue().toString());
			verify();
		} catch (Exception e2) {
			Application.getActiveApplication().getOutput().output(e2);
		}
	}

	// @formatter:off
	/**
	 * 由于地图刷新导致的控件值的更改，不需要在过程中触发控件相关事件多次刷新地图，因此先注销事件，方法结束再注册。
	 *  1.只需要写回控件值，不需要刷新地图。
	 *  2.由于操作地图要刷新控件，控件值改变又要刷新地图，稍有不慎就会死循环，所以尽量让程序逻辑保持简单，不要绕成线团。
	 *  3.在 mapDrawn 中不要调用 map.refresh 方法。即便在 mapDrawn 开始注销，在 refresh 之后再注册也不行，mapDrawn 会在注册之后才 fire。
	 *  4.在 mapDrawn 中值写控件的值，不触发控件的相关事件。
	 *  5.编辑控件的值导致地图刷新，由于比例尺、中心点、当前视图范围相互之间会有影响，改一个其他也可能会改变，需要在刷新地图之后取地图值设置，这里
	 *  也通过刷新地图触发 mapDrawn 来刷新其他控件值。
	 * 
	 * @param
	 */
	// @formatter:on
	private void mapDrawn() {
//		unregisterEvents();
		try {
			isIgnoreListener = true;
			this.scale = getMap().getScale();
			this.centerX = getMap().getCenter().getX();
			this.centerY = getMap().getCenter().getY();
			this.currentViewLeft = getMap().getViewBounds().getLeft();
			this.currentViewTop = getMap().getViewBounds().getTop();
			this.currentViewRight = getMap().getViewBounds().getRight();
			this.currentViewBottom = getMap().getViewBounds().getBottom();

			if (!DoubleUtilities.equals(scaleEditor.getScale(), scale, 16)) {
				this.scaleEditor.setScale(this.scale);
			}
			if (!DoubleUtilities.equals(DoubleUtilities.stringToValue(textFieldCenterX.getText()), centerX, 6)) {
				this.textFieldCenterX.setValue(this.centerX);
			}
			if (!DoubleUtilities.equals(DoubleUtilities.stringToValue(textFieldCenterY.getText()), centerY, 6)) {
				this.textFieldCenterY.setValue(this.centerY);
			}
			if (!DoubleUtilities.equals(DoubleUtilities.stringToValue(textFieldCurrentViewLeft.getText()), currentViewLeft, 6)) {
				this.textFieldCurrentViewLeft.setValue(this.currentViewLeft);
			}
			if (!DoubleUtilities.equals(DoubleUtilities.stringToValue(textFieldCurrentViewTop.getText()), currentViewTop, 6)) {
				this.textFieldCurrentViewTop.setValue(this.currentViewTop);
			}
			if (!DoubleUtilities.equals(DoubleUtilities.stringToValue(textFieldCurrentViewRight.getText()), currentViewRight, 6)) {
				this.textFieldCurrentViewRight.setValue(this.currentViewRight);
			}
			if (!DoubleUtilities.equals(DoubleUtilities.stringToValue(textFieldCurrentViewBottom.getText()), currentViewBottom, 6)) {
				this.textFieldCurrentViewBottom.setValue(this.currentViewBottom);
			}
		} catch (Exception e2) {
			Application.getActiveApplication().getOutput().output(e2);
		} finally {
			isIgnoreListener = false;
//			registerEvents();
		}
	}

	private enum OperationType {
		NONE, SCALE, CENTERPOINT, CURRENTVIEW
	}

	public JCheckBox getCheckBoxIsVisibleScalesEnabled() {
		return checkBoxIsVisibleScalesEnabled;
	}

	public void setCheckBoxIsVisibleScalesEnabled(JCheckBox checkBoxIsVisibleScalesEnabled) {
		this.checkBoxIsVisibleScalesEnabled = checkBoxIsVisibleScalesEnabled;
	}

}
