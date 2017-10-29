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
public class XMLRibbonBand extends XMLCommand {
	private ArrayList<XMLCommand> commands = new ArrayList<>();

	public XMLRibbonBand(PluginInfo pluginInfo, XMLCommandBase parent) {
		super(pluginInfo, parent);
		canMerge = true;
	}

	@Override
	public boolean initialize(Element element) {
		super.initialize(element);
		for (int i = 0; i < element.getChildNodes().getLength(); i++) {
			Node item = element.getChildNodes().item(i);
			if (item.getNodeType() == Node.ELEMENT_NODE) {
				XMLCommand xmlCommand = this.buildCommand(((Element) item));
				if (xmlCommand != null) {
					xmlCommand.initialize(((Element) item));
					addSubItem(xmlCommand);
				}
			}
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
		XMLCommand command = null;
		if (nodeName.equals(g_ControlButton)) {
			command = new XmlRibbonButton(getPluginInfo(), this);
		} else if (nodeName.equals(g_Gallery)) {
			command = new XmlGallery(getPluginInfo(), this);
		}
		return command;
	}

	@Override
	public void merge(XMLCommand otherCommand) {
		if (otherCommand instanceof XMLRibbonBand) {
			XMLRibbonBand otherCommandBand = (XMLRibbonBand) otherCommand;
			for (int i = 0; i < otherCommandBand.getLength(); i++) {
				XMLCommand mergeCommand = otherCommandBand.getCommandAtIndex(i);
				boolean isContain = false;
				for (XMLCommand command : commands) {
					if (command.canMerge() && !StringUtilities.isNullOrEmpty(command.getID()) && command.getID().equals(mergeCommand.getID())) {
						command.merge(mergeCommand);
						isContain = true;
						break;
					}
				}
				if (!isContain) {
					mergeCommand.copyTo(this);
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
	public XMLCommandBase copyTo(XMLCommandBase parent) {
		return super.copyTo(parent);
	}

	@Override
	protected XMLCommandBase createNew(XMLCommandBase parent) {
		XMLRibbonBand xmlRibbonBand = new XMLRibbonBand(getPluginInfo(), parent);
		xmlRibbonBand.commands = commands;
		return xmlRibbonBand;
	}
}
