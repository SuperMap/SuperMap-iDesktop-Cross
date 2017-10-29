package com.supermap.desktop.ui.xmlRibbons;

import com.supermap.desktop.Application;
import com.supermap.desktop.PluginInfo;
import com.supermap.desktop.ui.XMLCommand;
import com.supermap.desktop.ui.XMLCommandBase;
import com.supermap.desktop.utilities.StringUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;

/**
 * @author XiaJT
 */
public class XMLRibbon extends XMLCommand {
	private ArrayList<XMLCommand> commands = new ArrayList<>();
	private String formClassName;

	public XMLRibbon(PluginInfo pluginInfo, XMLCommandBase parent) {
		super(pluginInfo, parent);
		canMerge = true;
	}

	@Override
	public void merge(XMLCommand otherCommand) {
		if (otherCommand instanceof XMLRibbon) {
			XMLRibbon otherRibbon = (XMLRibbon) otherCommand;
			for (int i = 0; i < otherRibbon.getLength(); i++) {
				XMLCommand otherRibbonCommand = otherRibbon.getCommandAtIndex(i);
				boolean isContain = false;
				for (XMLCommand command : commands) {
					if (command.canMerge() && !StringUtilities.isNullOrEmpty(command.getID()) && command.getID().equals(otherRibbonCommand.getID())) {
						command.merge(otherRibbonCommand);
						isContain = true;
						break;
					}

				}
				if (!isContain) {
					otherRibbonCommand.copyTo(this);
				}
			}
		}

	}

	public XMLCommand getCommandAtIndex(int i) {
		return commands.get(i);
	}

	public int getLength() {
		return commands.size();
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
						addSubItem(xmlCommand);
					}
				}
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
			return false;
		}
		return true;
	}

	@Override
	public void addSubItem(XMLCommandBase xmlCommand) {
		for (int i = 0; i < commands.size(); i++) {
			XMLCommand command = commands.get(i);
			if (command.getIndex() > xmlCommand.getIndex()) {
				commands.add(i, (XMLCommand) xmlCommand);
				break;
			}
		}
		if (!commands.contains(xmlCommand)) {
			commands.add((XMLCommand) xmlCommand);
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

	@Override
	protected XMLCommandBase createNew(XMLCommandBase parent) {
		XMLRibbon xmlRibbon = new XMLRibbon(getPluginInfo(), parent);
		xmlRibbon.commands = commands;
		xmlRibbon.formClassName = formClassName;
		return xmlRibbon;
	}

	public String getFormClassName() {
		return this.formClassName;
	}
}
