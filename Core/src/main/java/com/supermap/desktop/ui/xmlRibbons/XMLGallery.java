package com.supermap.desktop.ui.xmlRibbons;

import com.supermap.desktop.PluginInfo;
import com.supermap.desktop.ui.XMLCommand;
import com.supermap.desktop.ui.XMLCommandBase;
import com.supermap.desktop.utilities.StringUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * @author XiaJT
 */
public class XMLGallery extends XMLCommand {
	private ArrayList<XMLCommand> galleryGroups = new ArrayList<>();

	public XMLGallery(PluginInfo pluginInfo, XMLCommandBase parent) {
		super(pluginInfo, parent);
		canMerge = true;
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
		if (item.getNodeName().equals(g_NodeRibbonGalleryGroup)) {
			return new XMLGalleryGroup(getPluginInfo(), this);
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

	@Override
	public void merge(XMLCommand otherCommand) {
		if (otherCommand instanceof XMLGallery) {
			XMLGallery otherGallery = (XMLGallery) otherCommand;
			for (XMLCommand galleryGroup : otherGallery.galleryGroups) {
				boolean isContain = false;
				for (XMLCommand group : galleryGroups) {
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
	protected XMLCommandBase createNew(XMLCommandBase parent) {
		XMLGallery xmlGallery = new XMLGallery(getPluginInfo(), this);
		xmlGallery.galleryGroups = galleryGroups;
		return xmlGallery;
	}

	public int getLength() {
		return galleryGroups.size();
	}

	public XMLCommand getXMLCommand(int index) {
		return galleryGroups.get(index);
	}
}
