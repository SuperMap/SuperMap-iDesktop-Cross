package com.supermap.desktop.ui.xmlStartMenus;

import com.supermap.desktop.ui.XMLCommand;
import com.supermap.desktop.ui.xmlRibbons.XmlRibbonCommandMenuButton;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntrySecondary;

import java.util.ArrayList;

/**
 * @author XiaJT
 */
public class SmSubMenus {
	private XMLSubMenus xmlSubMenus;
	private ArrayList<RibbonApplicationMenuEntrySecondary> ribbonApplicationMenuEntrySecondaries = new ArrayList<>();

	public SmSubMenus(XMLSubMenus xmlSubMenus) {
		this.xmlSubMenus = xmlSubMenus;
		if (this.xmlSubMenus.getLength() > 0) {
			for (int i = 0; i < this.xmlSubMenus.getLength(); i++) {
				XMLCommand xmlCommand = this.xmlSubMenus.getCommandAtIndex(i);
				if (xmlCommand instanceof XmlRibbonCommandMenuButton) {
					SmSubMenu smSubMenu = new SmSubMenu(xmlCommand);
					ribbonApplicationMenuEntrySecondaries.add(smSubMenu);
				}
			}
		}

	}

	public String getTitle() {
		return xmlSubMenus.getLabel();
	}

	public RibbonApplicationMenuEntrySecondary[] getMenus() {
		return ribbonApplicationMenuEntrySecondaries.toArray(new RibbonApplicationMenuEntrySecondary[ribbonApplicationMenuEntrySecondaries.size()]);
	}
}
