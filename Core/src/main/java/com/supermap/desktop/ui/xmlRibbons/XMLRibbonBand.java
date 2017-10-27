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
					addCommand(xmlCommand);
				}
			}
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
		XMLCommand command = null;
		if (nodeName.equals(g_ControlButton)) {
			command = new XmlRibbonButton(getPluginInfo(), this);
			command.initialize(item);
		} else if (nodeName.equals(g_Gallery)) {
			command = new XmlGallery(getPluginInfo(), this);
			command.initialize(item);
		}
		return command;
	}

	@Override
	public void merge(XMLCommand otherCommand) {
		// TODO: 2017/10/27
		super.merge(otherCommand);
	}

	@Override
	public XMLCommandBase copyTo(XMLCommandBase parent) {
		return super.copyTo(parent);
	}

	@Override
	protected XMLCommandBase createNew(XMLCommandBase parent) {
		return new XMLRibbonBand(getPluginInfo(), parent);
	}
}
