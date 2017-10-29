package com.supermap.desktop.ui.xmlRibbons;

import com.supermap.desktop.PluginInfo;
import com.supermap.desktop.ui.XMLCommand;
import com.supermap.desktop.ui.XMLCommandBase;
import com.supermap.desktop.utilities.StringUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * @author XiaJT
 */
public class XmlRibbonButton extends XMLCommand {

	private ArrayList<XMLCommand> menuItems = new ArrayList<>();
	private ArrayList<XmlRibbonGallery> galleries = new ArrayList<>();

	public XmlRibbonButton(PluginInfo pluginInfo, XMLCommandBase parent) {
		super(pluginInfo, parent);
		canMerge = true;
	}

	@Override
	public boolean initialize(Element xmlNodeCommand) {
		super.initialize(xmlNodeCommand);
		NodeList childNodes = xmlNodeCommand.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node item = childNodes.item(i);
			if (item.getNodeType() == Node.ELEMENT_NODE) {
				XMLCommand xmlCommand = null;

				if (item.getNodeName().equals(g_NodeRibbonPopupMenuItem)) {
					xmlCommand = new XmlRibbonCommandMenuButton(getPluginInfo(), this);
					xmlCommand.initialize((Element) item);
				} else if (item.getNodeName().equals(g_NodeRibbonPopupMenuSeparator)) {
					xmlCommand = new XmlRibbonCommandMenuSeparator(getPluginInfo(), this);
					xmlCommand.initialize((Element) item);
				} else if (item.getNodeName().equals(g_NodeRibbonGallery)) {
					// todo 下拉列表是gallery的情况
				}
				if (xmlCommand != null) {
					addSubItem(xmlCommand);
				}
			}
		}
		return true;
	}

	@Override
	public void addSubItem(XMLCommandBase subItem) {
		if (subItem instanceof XmlRibbonCommandMenuButton) {
			for (int i = 0; i < menuItems.size(); i++) {
				XMLCommand menuItem = menuItems.get(i);
				if (menuItem.getIndex() > subItem.getIndex()) {
					menuItems.add(i, (XmlRibbonCommandMenuButton) subItem);
					break;
				}
			}
			if (!menuItems.contains(subItem)) {
				menuItems.add((XmlRibbonCommandMenuButton) subItem);
			}
		} else if (subItem instanceof XmlRibbonGallery) {
			for (int i = 0; i < galleries.size(); i++) {
				XMLCommand menuItem = galleries.get(i);
				if (menuItem.getIndex() > subItem.getIndex()) {
					galleries.add(i, (XmlRibbonGallery) subItem);
					break;
				}
			}
			if (!galleries.contains(subItem)) {
				galleries.add((XmlRibbonGallery) subItem);
			}
		}
	}

	public int getLength() {
		return menuItems.size() > 0 ? menuItems.size() : galleries.size();
	}

	public int getMenuItemLength() {
		return menuItems.size();
	}

	public int getGalleriesLength() {
		return galleries.size();
	}

	public ArrayList<XMLCommand> getMenuItems() {
		return menuItems;
	}

	@Override
	public void merge(XMLCommand otherCommand) {
		if (otherCommand instanceof XmlRibbonButton) {
			XmlRibbonButton otherRibbonButton = (XmlRibbonButton) otherCommand;
			if (otherCommand.getCtrlActionClass() != null) {
				this.setCtrlActionClass(otherCommand.getCtrlActionClass());
				this.setPluginInfo(otherCommand.getPluginInfo());
			}
			if (StringUtilities.isNullOrEmpty(this.getImageFile())) {
				this.setImageFile(otherCommand.getImageFile());
			}
			if (StringUtilities.isNullOrEmpty(this.getTooltip())) {
				this.setTooltip(otherCommand.getTooltip());
			}
			if (StringUtilities.isNullOrEmpty(this.getDescription())) {
				this.setDescription(otherCommand.getDescription());
			}
			if (StringUtilities.isNullOrEmpty(this.getHelpURL())) {
				this.setHelpURL(otherCommand.getHelpURL());
			}
			if (otherRibbonButton.menuItems.size() > 0) {
				for (XMLCommand menuItem : otherRibbonButton.menuItems) {
					addSubItem(menuItem);
				}
			} else {
				for (XmlRibbonGallery gallery : otherRibbonButton.galleries) {
					addSubItem(gallery);
				}
			}

		}
	}


	@Override
	protected XMLCommandBase createNew(XMLCommandBase parent) {
		XmlRibbonButton xmlRibbonButton = new XmlRibbonButton(getPluginInfo(), getParent());
		xmlRibbonButton.menuItems = menuItems;
		xmlRibbonButton.galleries = galleries;
		return xmlRibbonButton;
	}
}
