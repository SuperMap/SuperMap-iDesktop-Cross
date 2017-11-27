package com.supermap.desktop.controls.utilities;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IFormMain;
import com.supermap.desktop.Interface.IFormMap;
import com.supermap.desktop.Interface.IToolbar;
import com.supermap.desktop.enums.WindowType;
import com.supermap.desktop.implement.SmToolbar;
import com.supermap.desktop.ui.LayersComponentManager;
import com.supermap.desktop.ui.ToolbarManager;
import com.supermap.desktop.ui.UICommonToolkit;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.ribbon.AbstractRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.internal.ui.ribbon.AbstractBandControlPanel;
import org.pushingpixels.flamingo.internal.ui.ribbon.JRibbonGallery;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by Administrator on 2015/11/10.
 */
public class ToolbarUIUtilities {
	private ToolbarUIUtilities() {
		// 公共类不提供构造方法
	}

	/**
	 * 刷新工具条
	 */
	public static void updataToolbarsState() {
		updateRibbonState();
//		updateToolbar();
	}

	private static void updateRibbonState() {
		updateTaskBar();
		IFormMain mainFrame = Application.getActiveApplication().getMainFrame();
		if (mainFrame instanceof JRibbonFrame) {
			JRibbon ribbon = ((JRibbonFrame) mainFrame).getRibbon();
			// 检查当前选项卡下的控件状态
			updateRibbonTaskState(ribbon.getSelectedTask());
//			for (int i = 0; i < ribbon.getTaskCount(); i++) {
//				RibbonTask task = ribbon.getTask(i);
//				updateRibbonTaskState(task);
//			}
//			for (int i = 0; i < ribbon.getContextualTaskGroupCount(); i++) {
//				RibbonContextualTaskGroup contextualTaskGroup = ribbon.getContextualTaskGroup(i);
//				if (ribbon.isVisible(contextualTaskGroup)) {
//					for (int j = 0; j < contextualTaskGroup.getTaskCount(); j++) {
//						updateRibbonTaskState(contextualTaskGroup.getTask(j));
//					}
//				}
//			}
		}
	}

	private static void updateTaskBar() {
		IFormMain mainFrame = Application.getActiveApplication().getMainFrame();
		if (mainFrame instanceof JRibbonFrame) {
			JRibbon ribbon = ((JRibbonFrame) mainFrame).getRibbon();
			List<Component> taskbarComponents = ribbon.getTaskbarComponents();
			for (Component taskbarComponent : taskbarComponents) {
				if (taskbarComponent instanceof IBaseItem) {
					if (((IBaseItem) taskbarComponent).getCtrlAction() == null) {
						taskbarComponent.setEnabled(false);
					} else {
						taskbarComponent.setEnabled(((IBaseItem) taskbarComponent).getCtrlAction().enable());
					}
				}
			}
		}
	}

	private static void updateRibbonTaskState(RibbonTask task) {
		if (task == null || task.getBands() == null || task.getBands().size() <= 0) {
			return;
		}
		for (AbstractRibbonBand<?> abstractRibbonBand : task.getBands()) {
			updateRibbonBandTask(abstractRibbonBand);
		}
	}

	/**
	 * 传入当前选项卡下的控件
	 * @param abstractRibbonBand
	 */
	private static void updateRibbonBandTask(AbstractRibbonBand<?> abstractRibbonBand) {
		AbstractBandControlPanel controlPanel = abstractRibbonBand.getControlPanel();
		if (controlPanel != null) {
			for (int i = 0; i < controlPanel.getComponentCount(); i++) {
				Component component = controlPanel.getComponent(i);
				if (component instanceof IBaseItem) {
					if (((IBaseItem) component).getCtrlAction() != null) {
						try {
							component.setEnabled(((IBaseItem) component).getCtrlAction().enable());
							((IBaseItem) component).setIgnoreEvent(true);
							if (component instanceof AbstractButton) {
								((AbstractButton) component).setSelected(((IBaseItem) component).getCtrlAction().check());
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							((IBaseItem) component).setIgnoreEvent(false);
						}
					}
				} else if (component instanceof JRibbonGallery) {
					JRibbonGallery ribbonGallery = (JRibbonGallery) component;
					for (int j = 0; j < ribbonGallery.getButtonCount(); j++) {
						JCommandToggleButton button = ribbonGallery.getButtonAt(j);
						if (button instanceof IBaseItem) {
							if (((IBaseItem) button).getCtrlAction() != null) {
								try {
									button.setEnabled(((IBaseItem) button).getCtrlAction().enable());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
	}

	public static JToolBar.Separator getVerticalSeparator() {
		JToolBar.Separator separator = new JToolBar.Separator();
		separator.setOrientation(SwingConstants.VERTICAL);
		return separator;
	}

	public static SmToolbar getSmToolbarAtPoint(Point point) {
		Component deepestComponent = SwingUtilities.getDeepestComponentAt(Application.getActiveApplication().getMainFrame().getToolbarManager().getToolbarsContainer(), point.x, point.y);
		if (deepestComponent != null) {
			return getParentToolBar(deepestComponent);
		}
		return null;
	}

	public static SmToolbar getParentToolBar(Component component) {
		if (component == null) {
			return null;
		}
		if (component instanceof SmToolbar) {
			return ((SmToolbar) component);
		}
		return getParentToolBar(component.getParent());
	}

	/**
	 * 刷新工具条的代码
	 */
	private static void updateToolbar() {
		if (Application.getActiveApplication() == null || Application.getActiveApplication().getMainFrame() == null
				|| Application.getActiveApplication().getMainFrame().getToolbarManager() == null) {
			return;
		}

		ToolbarManager toolbarManager = (ToolbarManager) Application.getActiveApplication().getMainFrame().getToolbarManager();
		for (int toolbarIndex = 0; toolbarIndex < toolbarManager.getCount(); toolbarIndex++) {
			IToolbar toolbar = toolbarManager.get(toolbarIndex);
			if (toolbar.isVisible()) {
				for (int itemIndex = 0; itemIndex < toolbar.getCount(); itemIndex++) {
					IBaseItem item = toolbar.getAt(itemIndex);
					if (item.getCtrlAction() != null) {
						item.getCtrlAction().setCaller(item);
						// if (item instanceof SmButtonDropdown) {
						// // 默认实现
						// }
						item.getCtrlAction().getCaller().setEnabled(item.getCtrlAction().enable());
						item.getCtrlAction().getCaller().setChecked(item.getCtrlAction().check());
					}
				}
			}
		}

		// 刷新子窗口工具条
		if (Application.getActiveApplication().getActiveForm() != null) {
			WindowType windowType = Application.getActiveApplication().getActiveForm().getWindowType();
			for (int toolbarIndex = 0; toolbarIndex < toolbarManager.getChildToolbarCount(windowType); toolbarIndex++) {
				IToolbar toolbar = toolbarManager.getChildToolbar(windowType, toolbarIndex);
				if (toolbar.isVisible()) {
					for (int itemIndex = 0; itemIndex < toolbar.getCount(); itemIndex++) {
						IBaseItem item = toolbar.getAt(itemIndex);
						if (item.getCtrlAction() != null) {
							item.getCtrlAction().setCaller(item);
							item.getCtrlAction().getCaller().setEnabled(item.getCtrlAction().enable());
							item.getCtrlAction().getCaller().setChecked(item.getCtrlAction().check());
						}
					}
				}

			}
		}
	}
}
