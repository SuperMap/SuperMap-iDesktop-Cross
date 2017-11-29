package com.supermap.desktop.ui.xmlStartMenus;

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
public class XMLStartMenu  extends XMLCommand {
	private ArrayList<XMLCommand> commands = new ArrayList<>();

	public XMLStartMenu(PluginInfo pluginInfo, XMLCommandBase parent) {
		super(pluginInfo, parent);
		canMerge = true;
	}

	@Override
	public void merge(XMLCommand otherCommand) {
		if (otherCommand instanceof XMLStartMenu) {
			XMLStartMenu otherMenu = (XMLStartMenu) otherCommand;
			for (int i = 0; i < otherMenu.getLength(); i++) {
				XMLCommand otherStartMenuCommand = otherMenu.getCommandAtIndex(i);
				boolean isContain = false;
				for (XMLCommand command : commands) {
					if (command.canMerge() && !StringUtilities.isNullOrEmpty(command.getID()) && command.getID().equals(otherStartMenuCommand.getID())) {
						command.merge(otherStartMenuCommand);
						isContain = true;
						break;
					}

				}
				if (!isContain) {
					otherStartMenuCommand.copyTo(this);
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
			for (int i = 0; i < element.getChildNodes().getLength(); i++) {
				Node item = element.getChildNodes().item(i);
				if (item.getNodeType() == Node.ELEMENT_NODE) {
					XMLCommand xmlCommand = this.buildCommand((Element) item);
					if (xmlCommand != null) {
						xmlCommand.initialize((Element) item);
					}
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

	private XMLCommand buildCommand(Element item) {
		String nodeName = item.getNodeName();
		XMLCommand xmlCommand = null;
		if (nodeName.equalsIgnoreCase(g_NodeSecondMenu)) {
			xmlCommand = new XMLSubMenus(getPluginInfo(),this);
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
		XMLStartMenu xmlStartMenu = new XMLStartMenu(getPluginInfo(), parent);
		xmlStartMenu.commands = commands;
		return xmlStartMenu;
	}
}
