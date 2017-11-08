package com.supermap.desktop.ui.xmlRibbons;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IXMLCreator;
import com.supermap.desktop.PluginInfo;
import com.supermap.desktop._XMLTag;
import com.supermap.desktop.enums.XMLCommandType;
import com.supermap.desktop.ui.XMLCommandBase;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * @author XiaJT
 */
public class XMLRibbons extends XMLCommandBase {
	private IXMLCreator xmlCreator = null;
	private ArrayList<XMLRibbon> ribbons;


	public XMLRibbons(PluginInfo pluginInfo) {
		super(pluginInfo);
		this.commandType = XMLCommandType.RIBBONS;
		ribbons = new ArrayList<>();

	}

	public XMLRibbons() {
		this.commandType = XMLCommandType.RIBBONS;
		ribbons = new ArrayList<>();
	}

	@Override
	public IXMLCreator getXMLCreator() {
		if (this.xmlCreator == null) {
			this.xmlCreator = new XMLRibbonCreator(this);
			this.xmlCreator.setDefaultValueCreator(new RibbonDefaultValueCreator(this));
		}
		return this.xmlCreator;
	}

	public boolean merge(XMLRibbons ribbons) {
		boolean result = true;
		try {
			for (XMLRibbon xmlRibbon : ribbons.getRibbons()) {
				if (xmlRibbon.getID() != null) {
					XMLRibbon currentRibbon = this.getRibbon(xmlRibbon.getID());
					if (currentRibbon != null) {
						currentRibbon.merge(xmlRibbon);
					} else {
						xmlRibbon.copyTo(this);
					}
				}
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
			result = false;
		}
		return result;
	}

	private XMLRibbon getRibbon(String id) {
		for (XMLRibbon ribbon : ribbons) {
			if (ribbon.getID().equals(id)) {
				return ribbon;
			}
		}
		return null;
	}

	public ArrayList<XMLRibbon> getRibbons() {
		return ribbons;
	}

	public void load(Element ribbonsElement) {
		if (ribbonsElement != null) {
			NodeList childNodes = ribbonsElement.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				XMLRibbon xmlRibbon = null;
				Node item = childNodes.item(i);
				if (item.getNodeType() == Node.ELEMENT_NODE && item.getNodeName().equalsIgnoreCase(_XMLTag.g_NodeRibbon)) {
					xmlRibbon = new XMLRibbon(this.getPluginInfo(), this);
					xmlRibbon.initialize((Element) item);
				}
				if (xmlRibbon != null) {
					addSubItem(xmlRibbon);
				}
			}
		}
	}


	@Override
	public void addSubItem(XMLCommandBase xmlRibbon) {
		for (int j = 0; j < this.ribbons.size(); j++) {
			if (ribbons.get(j).getIndex() > xmlRibbon.getIndex()) {
				ribbons.add(j, (XMLRibbon) xmlRibbon);
				break;
			}
		}
		if (!ribbons.contains(xmlRibbon)) {
			ribbons.add((XMLRibbon) xmlRibbon);
		}
	}


}
