package com.supermap.desktop.ui.xmlStartMenus;

import com.supermap.desktop.Application;
import com.supermap.desktop.PluginInfo;
import com.supermap.desktop._XMLTag;
import com.supermap.desktop.enums.XMLCommandType;
import com.supermap.desktop.ui.XMLCommandBase;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * @author XiaJT
 */
public class XMLStartMenus extends XMLCommandBase {
	private ArrayList<XMLStartMenu> startMenus = new ArrayList<>();

	public XMLStartMenus() {
		this.commandType = XMLCommandType.STARTMENUS;
	}

	public XMLStartMenus(PluginInfo pluginInfo) {
		super(pluginInfo);
		this.commandType = XMLCommandType.STARTMENUS;
	}


	public boolean merge(XMLStartMenus startMenus) {
		boolean result = true;
		try {
			for (XMLStartMenu startMenu : startMenus.startMenus) {
				if (startMenu.getID() != null) {
					XMLStartMenu currentStartMenu = this.getStartMenu(startMenu.getID());
					if (currentStartMenu != null) {
						currentStartMenu.merge(startMenu);
					} else {
						startMenu.copyTo(this);
					}
				}
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
			result = false;
		}
		return result;
	}

	private XMLStartMenu getStartMenu(String id) {
		for (XMLStartMenu startMenu : startMenus) {
			if (startMenu.getID().equalsIgnoreCase(id)) {
				return startMenu;
			}
		}
		return null;
	}

	public void load(Element startMenusElement) {
		if (startMenusElement != null) {
			NodeList childNodes = startMenusElement.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node item = childNodes.item(i);
				if (item.getNodeType() == Node.ELEMENT_NODE) {
					if (item.getNodeName().equalsIgnoreCase(_XMLTag.g_NodeStartMenu)) {
						XMLStartMenu xmlStartMenu = new XMLStartMenu(this.getPluginInfo(), this);
						xmlStartMenu.initialize(((Element) item));
						addSubItem(xmlStartMenu);
					}
				}
			}
		}
	}

	@Override
	public void addSubItem(XMLCommandBase subItem) {
		for (int j = 0; j < this.startMenus.size(); j++) {
			if (startMenus.get(j).getIndex() > subItem.getIndex()) {
				startMenus.add(j, (XMLStartMenu) subItem);
				break;
			}
		}
		if (!startMenus.contains(subItem)) {
			startMenus.add((XMLStartMenu) subItem);
		}
	}

	public ArrayList<XMLStartMenu> getStartMenus() {
		return startMenus;
	}
}
