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
	private ArrayList<XMLTaskBar> taskBars = new ArrayList<>();
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
			for (XMLTaskBar taskBar : ribbons.taskBars) {
				if (taskBar.getID() != null) {
					XMLTaskBar currentTaskBar = getTaskBar(taskBar.getID());
					if (currentTaskBar != null) {
						currentTaskBar.merge(taskBar);
					}else {
						taskBar.copyTo(this);
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

	private XMLTaskBar getTaskBar(String id) {
		for (XMLTaskBar taskBar : taskBars) {
			if (taskBar.getID().equals(id)) {
				return taskBar;
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
				Node item = childNodes.item(i);
				if (item.getNodeType() == Node.ELEMENT_NODE) {
					if (item.getNodeName().equalsIgnoreCase(_XMLTag.g_NodeRibbon)) {
						XMLRibbon xmlRibbon = new XMLRibbon(this.getPluginInfo(), this);
						xmlRibbon.initialize((Element) item);
						addSubItem(xmlRibbon);
					} else if (item.getNodeName().equalsIgnoreCase(_XMLTag.g_NodeTaskBar)) {
						XMLTaskBar xmlTaskBar = new XMLTaskBar(getPluginInfo(), this);
						xmlTaskBar.initialize((Element) item);
						addSubItem(xmlTaskBar);
					}
				}
			}
		}
	}


	@Override
	public void addSubItem(XMLCommandBase xmlCommandBase) {
		if (xmlCommandBase instanceof XMLRibbon) {
			for (int j = 0; j < this.ribbons.size(); j++) {
				if (ribbons.get(j).getIndex() > xmlCommandBase.getIndex()) {
					ribbons.add(j, (XMLRibbon) xmlCommandBase);
					break;
				}
			}
			if (!ribbons.contains(xmlCommandBase)) {
				ribbons.add((XMLRibbon) xmlCommandBase);
			}
		} else if (xmlCommandBase instanceof XMLTaskBar) {
			for (int i = 0; i < this.taskBars.size(); i++) {
				if (taskBars.get(i).getIndex() > xmlCommandBase.getIndex()) {
					taskBars.add(i, (XMLTaskBar) xmlCommandBase);
				}
			}
			if (!taskBars.contains(xmlCommandBase)) {
				taskBars.add((XMLTaskBar) xmlCommandBase);
			}
		}

	}


	public ArrayList<XMLTaskBar> getTaskBars() {
		return taskBars;
	}
}
