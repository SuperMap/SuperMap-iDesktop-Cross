package com.supermap.desktop.ui;

import com.supermap.desktop.WorkEnvironment;
import com.supermap.desktop.implement.SmRibbonTask;
import com.supermap.desktop.utilities.SystemPropertyUtilities;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;

import java.util.ArrayList;

/**
 * @author XiaJT
 */
public class RibbonManager implements IRibbonManager {
	@Override
	public void load(JRibbon ribbon, WorkEnvironment workEnvironment) {
		ArrayList<XMLMenu> menus = workEnvironment.getPluginInfos().getFrameMenus().getMenus();
		for (int i = menus.size() - 1; i >= 0; i--) {
			for (int j = 0; j < i; j++) {
				if (menus.get(j).getIndex() > menus.get(j + 1).getIndex()) {
					XMLMenu xmlMenu = menus.get(j);
					menus.set(j, menus.get(j + 1));
					menus.set(j + 1, xmlMenu);
				}
			}
		}
		// FIXME: 2017/10/19
		for (XMLMenu xmlMenu : menus) {
			if (SystemPropertyUtilities.isSupportPlatform(xmlMenu.getPlatform())) {
				SmRibbonTask smRibbonTask = new SmRibbonTask(xmlMenu);
				ribbon.addTask(smRibbonTask);
			}
		}
	}
}
