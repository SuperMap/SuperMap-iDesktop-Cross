package com.supermap.desktop.ui;

import com.supermap.desktop.Interface.IApplicationMenuManager;
import com.supermap.desktop.WorkEnvironment;
import com.supermap.desktop.ui.xmlStartMenus.SmStartMenu;
import com.supermap.desktop.ui.xmlStartMenus.XMLStartMenu;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenu;

import java.util.ArrayList;

/**
 * @author XiaJT
 */
public class ApplicationMenuManager implements IApplicationMenuManager {

	private RibbonApplicationMenu menu;

	@Override
	public void load(RibbonApplicationMenu menu, WorkEnvironment workEnvironment) {
		this.menu = menu;
		ArrayList<XMLStartMenu> startMenus = workEnvironment.getPluginInfos().getXmlStartMenus().getStartMenus();
		for (XMLStartMenu startMenu : startMenus) {
			menu.addMenuEntry(new SmStartMenu(startMenu));
		}
	}
}
