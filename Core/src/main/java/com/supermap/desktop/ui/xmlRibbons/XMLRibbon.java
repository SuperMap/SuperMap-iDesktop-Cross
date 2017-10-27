package com.supermap.desktop.ui.xmlRibbons;

import com.supermap.desktop.Application;
import com.supermap.desktop.PluginInfo;
import com.supermap.desktop.ui.XMLCommand;
import com.supermap.desktop.ui.XMLCommandBase;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;

/**
 * @author XiaJT
 */
public class XMLRibbon extends XMLCommand {
	private ArrayList<XMLCommand> commands = null;
	private String formClassName;

	public XMLRibbon(PluginInfo pluginInfo, XMLCommandBase parent) {
		super(pluginInfo, parent);
	}

	@Override
	public void merge(XMLCommand otherCommand) {
		if (otherCommand instanceof XMLRibbon) {
			XMLRibbon otherRibbon = (XMLRibbon) otherCommand;
			if (otherRibbon.)
		}

	}

	@Override
	public boolean initialize(Element element) {
		super.initialize(element);
		try {
			if (element.hasAttribute(g_AttributionFormClass)) {
				formClassName = element.getAttribute(g_AttributionFormClass);
			}
			for (int i = 0; i < element.getChildNodes().getLength(); i++) {
				Node item = element.getChildNodes().item(i);
				if (item.getNodeType() == Node.ELEMENT_NODE) {
					XMLCommand xmlCommand = this.buildCommand((Element) item);
					if (xmlCommand != null) {
						addCommand(xmlCommand);
					}
				}
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
			return false;
		}
		return true;
	}

	private void addCommand(XMLCommand xmlCommand) {
		for (int i = 0; i < commands.size(); i++) {
			XMLCommand command = commands.get(i);
			if (command.getIndex() > xmlCommand.getIndex()) {
				commands.add(i, xmlCommand);
				break;
			}
		}
		if (!commands.contains(xmlCommand)) {
			commands.add(xmlCommand);
		}
	}

	private XMLCommand buildCommand(Element item) {
		String nodeName = item.getNodeName();
		XMLCommand xmlCommand = null;
		if (nodeName.equals(g_NodeGroup)) {
			xmlCommand = new XMLRibbonBand(getPluginInfo(), this);
		}
		if (xmlCommand != null) {
			xmlCommand.initialize(item);
		}
		return xmlCommand;
	}
}
