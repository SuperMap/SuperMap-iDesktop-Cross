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
public class XMLGalleryGroup extends XMLCommand {
	private ArrayList<XMLCommand> commands = new ArrayList<>();

	public XMLGalleryGroup(PluginInfo pluginInfo, XMLCommandBase parent) {
		super(pluginInfo, parent);
		canMerge = true;
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
	public void merge(XMLCommand otherCommand) {
		if (otherCommand instanceof XMLGalleryGroup) {
			XMLGalleryGroup otherGallery = (XMLGalleryGroup) otherCommand;
			for (XMLCommand galleryGroup : otherGallery.commands) {
				boolean isContain = false;
				for (XMLCommand group : commands) {
					if (group.canMerge() && !StringUtilities.isNullOrEmpty(group.getID()) && group.getID().equals(galleryGroup.getID())) {
						group.merge(galleryGroup);
						isContain = true;
						break;
					}
				}
				if (!isContain) {
					galleryGroup.copyTo(this);
				}
			}
		}
	}

	@Override
	public void addSubItem(XMLCommandBase subItem) {
		for (int i = 0; i < commands.size(); i++) {
			if (commands.get(i).getIndex() > subItem.getIndex()) {
				commands.add(i, ((XMLCommand) subItem));
				break;
			}
		}
		if (!commands.contains(subItem)) {
			commands.add(((XMLCommand) subItem));
		}
	}

	@Override
	protected XMLCommandBase createNew(XMLCommandBase parent) {
		XMLGalleryGroup xmlGalleryGroup = new XMLGalleryGroup(getPluginInfo(), this);
		xmlGalleryGroup.commands = commands;
		return xmlGalleryGroup;
	}

	public int getLength() {
		return commands.size();
	}

	public XMLCommand getXMLCommandAtIndex(int index) {
		return commands.get(index);
	}
}
