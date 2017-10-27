package com.supermap.desktop.ui.xmlRibbons;

import com.supermap.desktop.PluginInfo;
import com.supermap.desktop.ui.XMLCommand;
import com.supermap.desktop.ui.XMLCommandBase;
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
	}

	@Override
	public boolean initialize(Element xmlNodeCommand) {
		super.initialize(xmlNodeCommand);
		NodeList childNodes = xmlNodeCommand.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node item = childNodes.item(i);
			XMLCommand xmlCommand = null;
			if (item.getNodeName().equals(g_NodeRibbonPopupMenuItem)) {
				xmlCommand = new XmlRibbonPopupMenuItems(getPluginInfo(), this);
				xmlCommand.initialize((Element) item);
			} else if (item.getNodeName().equals(g_NodeRibbonPopupMenuSeparator)) {
				xmlCommand = new XmlRibbonPopupMenuSeparator(getPluginInfo(), this);
				xmlCommand.initialize((Element) item);
			} else if (item.getNodeName().equals(g_NodeRibbonGallery)) {
				// todo 下拉列表是gallery的情况
			}
			if (xmlCommand != null) {
				menuItems.add(xmlCommand);
			}

		}
		return true;
	}
}
