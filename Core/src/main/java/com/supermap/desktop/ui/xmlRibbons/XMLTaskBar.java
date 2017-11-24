package com.supermap.desktop.ui.xmlRibbons;

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
public class XMLTaskBar extends XMLCommand {
	private ArrayList<XMLCommand> commands = new ArrayList<>();

	public XMLTaskBar(PluginInfo pluginInfo, XMLCommandBase parent) {
		super(pluginInfo, parent);
		canMerge = true;
	}

	@Override
	public void merge(XMLCommand otherCommand) {
		if (otherCommand instanceof XMLTaskBar) {
			XMLTaskBar otherRibbon = (XMLTaskBar) otherCommand;
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
		for (int i = 0; i < element.getChildNodes().getLength(); i++) {
			Node item = element.getChildNodes().item(i);
			if (item.getNodeType() == Node.ELEMENT_NODE) {
				XMLCommand xmlCommand = this.buildCommand((Element) item);
				xmlCommand.initialize((Element) item);
				if (xmlCommand != null) {
					addSubItem(xmlCommand);
				}
			}
		}

		return true;
	}

	private XMLCommand buildCommand(Element item) {
		String nodeName = item.getNodeName();
		XMLCommand xmlCommand = null;
		if (nodeName.equals(g_ControlButton)) {
			xmlCommand = new XMLRibbonButton(getPluginInfo(), this);
		}
		return xmlCommand;
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
	@Override
	protected XMLCommandBase createNew(XMLCommandBase parent) {
		XMLTaskBar xmlRibbon = new XMLTaskBar(getPluginInfo(), parent);
		xmlRibbon.commands = commands;
		return xmlRibbon;
	}
}
