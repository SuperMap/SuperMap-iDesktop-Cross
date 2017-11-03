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
public class XmlGallery extends XMLCommand {
	private ArrayList<XMLCommand> galleryGroups = new ArrayList<>();
	public XmlGallery(PluginInfo pluginInfo, XMLCommandBase parent) {
		super(pluginInfo, parent);
	}

	@Override
	public boolean initialize(Element element) {
		super.initialize(element);
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node item = childNodes.item(i);
			if (item.getNodeType() == Node.ELEMENT_NODE) {
				XMLCommand xmlCommand = this.buildCommand(item);
				if (xmlCommand != null) {
					xmlCommand.initialize((Element) item);
					addSubItem(xmlCommand);
				}
			}
		}
		return true;
	}

	private XMLCommand buildCommand(Node item) {
		if (item.getNodeName().equals(g_NodeGroup)) {
			return new XmlGalleryGroup(getPluginInfo(), this);
		}
		return null;
	}

	@Override
	public void addSubItem(XMLCommandBase subItem) {
		for (int i = 0; i < galleryGroups.size(); i++) {
			if (galleryGroups.get(i).getIndex() > subItem.getIndex()) {
				galleryGroups.add(i, ((XMLCommand) subItem));
				break;
			}
		}
		if (!galleryGroups.contains(subItem)) {
			galleryGroups.add(((XMLCommand) subItem));
		}
	}
}
