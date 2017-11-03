package com.supermap.desktop.ui.xmlRibbons;

import com.supermap.desktop.PluginInfo;
import com.supermap.desktop.ui.XMLCommand;
import com.supermap.desktop.ui.XMLCommandBase;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;

/**
 * @author XiaJT
 */
public class XmlGalleryGroup extends XMLCommand {
	private ArrayList<XMLCommand> commands = new ArrayList<>();

	public XmlGalleryGroup(PluginInfo pluginInfo, XMLCommandBase parent) {
		super(pluginInfo, parent);
	}

	@Override
	public boolean initialize(Element element) {
		super.initialize(element);
		for (int i = 0; i < element.getChildNodes().getLength(); i++) {
			Node item = element.getChildNodes().item(i);
			if (item.getNodeType() == Node.ELEMENT_NODE) {
				XMLCommand xmlCommand = buildCommand(item);
				if (xmlCommand != null) {
					xmlCommand.initialize((Element) item);
					addSubItem(xmlCommand);
				}
			}
		}
		return true;
	}

	private XMLCommand buildCommand(Node item) {
		if (item.getNodeName().equals(g_NodeRibbonGalleryButton)) {
			return new XMLRibbonGalleryButton(getPluginInfo(), this);
		}
		return null;
	}

	@Override
	public void addSubItem(XMLCommandBase subItem) {
		for (int i = 0; i < commands.size(); i++) {
			XMLCommand command = commands.get(i);
//			if(command.)
		}
	}
}
