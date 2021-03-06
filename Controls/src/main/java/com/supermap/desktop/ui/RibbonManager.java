package com.supermap.desktop.ui;

import com.supermap.desktop.Interface.IRibbonManager;
import com.supermap.desktop.WorkEnvironment;
import com.supermap.desktop.enums.WindowType;
import com.supermap.desktop.implement.SmRibbonTask;
import com.supermap.desktop.ui.xmlRibbons.SmXmlRibbonButton;
import com.supermap.desktop.ui.xmlRibbons.XMLRibbon;
import com.supermap.desktop.ui.xmlRibbons.XMLRibbonButton;
import com.supermap.desktop.ui.xmlRibbons.XMLTaskBar;
import com.supermap.desktop.utilities.StringUtilities;
import com.supermap.desktop.utilities.SystemPropertyUtilities;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.RibbonContextualTaskGroup;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;

/**
 * @author XiaJT
 */
public class RibbonManager implements IRibbonManager {
	private EnumMap<WindowType, RibbonContextualTaskGroup> childTaskGroups = new EnumMap<WindowType, RibbonContextualTaskGroup>(WindowType.class);
	private JRibbon ribbon;

	@Override
	public void load(JRibbon ribbon, WorkEnvironment workEnvironment) {
		this.ribbon = ribbon;
		ArrayList<XMLTaskBar> taskBars = workEnvironment.getPluginInfos().getXmlRibbons().getTaskBars();
		for (XMLTaskBar taskBar : taskBars) {
			for (int i = 0; i < taskBar.getLength(); i++) {
				if (taskBar.getCommandAtIndex(i) instanceof XMLRibbonButton) {
					ribbon.addTaskbarComponent(new SmXmlRibbonButton((XMLRibbonButton) taskBar.getCommandAtIndex(i)));
				}
			}
		}
		ArrayList<XMLRibbon> ribbons = workEnvironment.getPluginInfos().getXmlRibbons().getRibbons();
		EnumMap<WindowType, ArrayList<SmRibbonTask>> childFrameMenus = new EnumMap<>(WindowType.class);

		for (XMLRibbon xmlRibbon : ribbons) {
			if (SystemPropertyUtilities.isSupportPlatform(xmlRibbon.getPlatform())) {
				SmRibbonTask smRibbonTask = new SmRibbonTask(xmlRibbon);
				String formClassName = xmlRibbon.getFormClassName();
				if (StringUtilities.isNullOrEmpty(formClassName)) {
					ribbon.addTask(smRibbonTask);
				}
				if (!StringUtilities.isNullOrEmpty(formClassName)) {
					WindowType windowType = getWindowType(formClassName);
					ArrayList<SmRibbonTask> iMenus = childFrameMenus.get(windowType);
					if (iMenus == null) {
						iMenus = new ArrayList<>();
						childFrameMenus.put(windowType, iMenus);
					}
					iMenus.add(smRibbonTask);
				}
			}
		}

		for (WindowType windowType : childFrameMenus.keySet()) {
			ArrayList<SmRibbonTask> smRibbonTasks = childFrameMenus.get(windowType);
			RibbonContextualTaskGroup ribbonContextualTaskGroup = new RibbonContextualTaskGroup(smRibbonTasks.get(0).getTitle(), new Color(0x011E3C), smRibbonTasks.toArray(new RibbonTask[smRibbonTasks.size()]));
			childTaskGroups.put(windowType, ribbonContextualTaskGroup);
			ribbon.addContextualTaskGroup(ribbonContextualTaskGroup);
			ribbon.setVisible(ribbonContextualTaskGroup, false);
		}
	}

	private WindowType getWindowType(String controlClass) {
		WindowType windowType = WindowType.UNKNOWN;
		if ("SuperMap.Desktop.FormMap".equalsIgnoreCase(controlClass)) {
			windowType = WindowType.MAP;
		} else if ("SuperMap.Desktop.FormScene".equalsIgnoreCase(controlClass)) {
			windowType = WindowType.SCENE;
		} else if ("SuperMap.Desktop.FormLayout".equalsIgnoreCase(controlClass)) {
			windowType = WindowType.LAYOUT;
		} else if ("SuperMap.Desktop.FormTabular".equalsIgnoreCase(controlClass)) {
			windowType = WindowType.TABULAR;
		} else if ("SuperMap.Desktop.FormTransformation".equalsIgnoreCase(controlClass)) {
			windowType = WindowType.TRANSFORMATION;
		}

		return windowType;
	}

	public void loadChildMenu(WindowType activatedChildFormType) {
		if (childTaskGroups.get(activatedChildFormType) != null) {
			ribbon.setVisible(childTaskGroups.get(activatedChildFormType), true);
			ribbon.setSelectedTask(childTaskGroups.get(activatedChildFormType).getTask(0));
		}
	}

	public void removeChildMenu(WindowType beforeType) {
		if (childTaskGroups.get(beforeType) != null) {
			ribbon.setVisible(childTaskGroups.get(beforeType), false);
		}
	}
}
