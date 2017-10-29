package com.supermap.desktop.controls.utilities;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IFormMain;
import com.supermap.desktop.Interface.IToolbar;
import com.supermap.desktop.enums.WindowType;
import com.supermap.desktop.implement.SmToolbar;
import com.supermap.desktop.ui.ToolbarManager;
import org.pushingpixels.flamingo.api.ribbon.*;
import org.pushingpixels.flamingo.internal.ui.ribbon.JBandControlPanel;

import javax.swing.*;
import java.awt.*;

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

	private static void updateRibbonState() {
		IFormMain mainFrame = Application.getActiveApplication().getMainFrame();
		if (mainFrame instanceof JRibbonFrame) {
			JRibbon ribbon = ((JRibbonFrame) mainFrame).getRibbon();
			for (int i = 0; i < ribbon.getTaskCount(); i++) {
				RibbonTask task = ribbon.getTask(i);
				updateRibbonTaskState(task);
			}
		}
	}

	private static void updateRibbonTaskState(RibbonTask task) {
		for (AbstractRibbonBand<?> abstractRibbonBand : task.getBands()) {
			updateRibbonBandTask(abstractRibbonBand);
		}
	}

	private static void updateRibbonBandTask(AbstractRibbonBand<?> abstractRibbonBand) {
		if (abstractRibbonBand instanceof JRibbonBand) {
			JBandControlPanel controlPanel = ((JRibbonBand) abstractRibbonBand).getControlPanel();
			if (controlPanel != null) {

				for (int i = 0; i < controlPanel.getComponentCount(); i++) {
					if (controlPanel.getComponent(i) instanceof IBaseItem && ((IBaseItem) controlPanel.getComponent(i)).getCtrlAction() != null) {
						try {
							controlPanel.getComponent(i).setEnabled(((IBaseItem) controlPanel.getComponent(i)).getCtrlAction().enable());
						} catch (Exception e) {
							// 谁写的enable出问题了啊，丫的！
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
}
