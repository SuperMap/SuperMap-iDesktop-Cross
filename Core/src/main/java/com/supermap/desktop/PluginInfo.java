package com.supermap.desktop;

import com.supermap.desktop.Interface.IToolbarManager;
import com.supermap.desktop.enums.XMLCommandType;
import com.supermap.desktop.ui.XMLDockbars;
import com.supermap.desktop.ui.XMLMenus;
import com.supermap.desktop.ui.XMLStatusbars;
import com.supermap.desktop.ui.XMLToolbar;
import com.supermap.desktop.ui.XMLToolbars;
import com.supermap.desktop.ui.xmlRibbons.XMLRibbons;
import com.supermap.desktop.ui.xmlStartMenus.XMLStartMenus;
import com.supermap.desktop.utilities.XmlUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PluginInfo {

	private Element frameMenusElement = null;
	private Element toolbarsElement = null;
	private Element statusbarsElement = null;
	private Element dockbarsElement = null;
	private Element ribbonsElement = null;
	private Element startMenusElement = null;
	private Element contextMenusElement = null;
	private Element processManagerElement = null;

	private String configLocation = "";
	private String bundleName = "";
	private String description = "";
	private String uiDefinition = "";
	private Boolean enable = false;
	private Boolean hasRecentFile = false;
	private String recentFile = "";
	private Boolean isCurrent = false;
	private String helpLocalRoot = "";
	private String name = "";
	private String author = "";
	private String url = "";
	String namespaceURL = "";
	private String helpOnlineRoot = "";

	private XMLMenus xmlFrameMenus = null;
	private XMLToolbars xmlToolbars = null;
	private XMLStatusbars xmlStatusbars = null;
	private XMLDockbars xmlDockbars = null;
	private XMLMenus xmlContextMenus = null;
	private XMLRibbons xmlRibbons = null;
	private XMLStartMenus startMenus;


	public PluginInfo(Element element) {
		this.name = "";
		this.author = "";
		this.url = "";
		this.configLocation = "";
		this.bundleName = "";
		this.description = "";
		this.uiDefinition = "";
		this.hasRecentFile = false;
		this.namespaceURL = "";
		this.enable = true;
		this.FromConfig(element);
	}

	public PluginInfo(PluginInfo pluginInfo) {
		this.setName(pluginInfo.getName());
	}


	//region getter and setter
	public String getNamespaceURL() {
		return this.namespaceURL;
	}

	public void setNamespaceURL(String namespaceURL) {
		this.namespaceURL = namespaceURL;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getURL() {
		return this.url;
	}

	public void setURL(String url) {
		this.url = url;
	}

	public String getConfigLocation() {
		return this.configLocation;
	}

	public void setConfigLocation(String configLocation) {
		this.configLocation = configLocation;
	}


	public String getBundleName() {
		return this.bundleName;
	}

	public void setBundleName(String bundleName) {
		this.bundleName = bundleName;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUIDefinition() {
		return this.uiDefinition;
	}

	public void setUIDefinition(String uiDefinition) {
		this.uiDefinition = uiDefinition;
	}

	public Boolean getEnable() {
		return this.enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public Boolean getHasRecentFile() {
		return this.hasRecentFile;
	}

	public void setHasRecentFile(Boolean hasRecentFile) {
		this.hasRecentFile = hasRecentFile;
	}

	public String getRecentFile() {
		return this.recentFile;
	}

	public void setRecentFile(String recentFile) {
		this.recentFile = recentFile;
	}

	public Boolean getIsCurrent() {
		return this.isCurrent;
	}

	public void setHasIsCurrent(Boolean isCurrent) {
		this.isCurrent = isCurrent;
	}

	public String getHelpLocalRoot() {
		return this.helpLocalRoot;
	}

	public void setHelpLocalRoot(String helpLocalRoot) {
		this.helpLocalRoot = helpLocalRoot;
	}

	public String getHelpOnlineRoot() {
		return this.helpOnlineRoot;
	}

	public void setHelpOnlineRoot(String helpOnlineRoot) {
		this.helpOnlineRoot = helpOnlineRoot;
	}


	public XMLMenus getFrameMenus() {
		return this.xmlFrameMenus;
	}

	public XMLToolbars getToolbars() {
		return this.xmlToolbars;
	}

	public XMLStatusbars getStatusbars() {
		return this.xmlStatusbars;
	}

	public XMLDockbars getDockbars() {
		return this.xmlDockbars;
	}

	public XMLMenus getContextMenus() {
		return this.xmlContextMenus;
	}

	public Element getProcessManagerElement() {
		return this.processManagerElement;
	}

	public XMLRibbons getRibbons() {
		// TODO: 2017/10/25
		return xmlRibbons;
	}
	//endregion

	public Boolean IsValid() {
		return true;
	}

	public boolean FromConfig(Element element) {
		boolean result = false;

		try {
			if (element.hasAttribute(_XMLTag.g_AttributionName)) {
				this.setName(element.getAttribute(_XMLTag.g_AttributionName));
			}
			if (element.hasAttribute(_XMLTag.g_AttributionAuthor)) {

				this.setAuthor(element.getAttribute(_XMLTag.g_AttributionAuthor));
			}
			if (element.hasAttribute(_XMLTag.g_AttributionDescription)) {

				this.setAuthor(element.getAttribute(_XMLTag.g_AttributionDescription));
			}
			if (element.hasAttribute(_XMLTag.g_AttributionURL)) {
				this.setURL(element.getAttribute(_XMLTag.g_AttributionURL));
			}
			if (element.getNamespaceURI() != null) {
				this.setNamespaceURL(element.getNamespaceURI());
			}
			if (element.hasAttribute(_XMLTag.g_AttributionHelpLocalRoot)) {
				this.setHelpLocalRoot(element.getAttribute(_XMLTag.g_AttributionHelpLocalRoot));
			}
			if (element.hasAttribute(_XMLTag.g_AttributionHelpOnlineRoot)) {
				this.setHelpOnlineRoot(element.getAttribute(_XMLTag.g_AttributionHelpOnlineRoot));
			}

			NodeList nodes = element.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {

				if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element item = (Element) (nodes.item(i));

					String nodeName = item.getNodeName();
					if (nodeName.equalsIgnoreCase(_XMLTag.g_Runtime)) {
						this.setBundleName(item.getAttribute(_XMLTag.g_AttributionBundleName));
						if (item.hasAttribute(_XMLTag.g_AttributionEnabled) && "false".equalsIgnoreCase(item.getAttribute(_XMLTag.g_AttributionEnabled))) {
							this.setEnable(false);
						}
					} else if (nodeName.equalsIgnoreCase(_XMLTag.g_NodeFrameMenus)) {
						this.frameMenusElement = item;
					} else if (nodeName.equalsIgnoreCase(_XMLTag.g_NodeToolbars)) {
						this.toolbarsElement = item;
					} else if (nodeName.equalsIgnoreCase(_XMLTag.g_NodeStatusbars)) {
						this.statusbarsElement = item;
					} else if (nodeName.equalsIgnoreCase(_XMLTag.g_NodeDockbars)) {
						this.dockbarsElement = item;
					} else if (nodeName.equalsIgnoreCase(_XMLTag.g_NodeContextMenus)) {
						this.contextMenusElement = item;
					} else if (nodeName.equalsIgnoreCase("ProcessManager")) {
						this.processManagerElement = item;
					} else if (nodeName.equalsIgnoreCase(_XMLTag.g_NodeRibbons)) {
						this.ribbonsElement = item;
					} else if (nodeName.equalsIgnoreCase(_XMLTag.g_NodeStartMenus)) {
						this.startMenusElement = item;
					}
				}
			}

			if (this.IsValid()) {
				result = true;
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
		return result;
	}

	public Boolean parseUI() {

		this.xmlFrameMenus = new XMLMenus(this, XMLCommandType.FRAMEMENUS);
		this.xmlFrameMenus.load(this.frameMenusElement);

		this.xmlToolbars = new XMLToolbars(this);
		this.xmlToolbars.load(this.toolbarsElement);

		this.xmlContextMenus = new XMLMenus(this, XMLCommandType.CONTEXTMENUS);
		this.xmlContextMenus.load(this.contextMenusElement);

		this.xmlDockbars = new XMLDockbars(this);
		this.xmlDockbars.load(this.dockbarsElement);

		this.xmlStatusbars = new XMLStatusbars(this);
		this.xmlStatusbars.load(this.statusbarsElement);

		this.xmlRibbons = new XMLRibbons(this);
		this.xmlRibbons.load(this.ribbonsElement);

		this.startMenus = new XMLStartMenus(this);
		this.startMenus.load(this.startMenusElement);

		return true;
	}

	public void toXML() {
		Element toolbarsElementTemp = null;
		Document document = XmlUtilities.getDocument(this.configLocation);
		if (document != null) {
			Element documentElement = document.getDocumentElement();
			NodeList nodeToolbars = documentElement.getElementsByTagName(_XMLTag.g_NodeToolbars);

			if (nodeToolbars != null && nodeToolbars.getLength() > 0) {
				toolbarsElementTemp = (Element) nodeToolbars.item(0);
			}

			if (this.xmlToolbars != null && toolbarsElementTemp != null) {
				NodeList childNodes = toolbarsElementTemp.getChildNodes();

				for (int i = 0; i < childNodes.getLength(); i++) {
					Node childNode = childNodes.item(i);

					if (childNode != null && childNode.getNodeType() == Node.ELEMENT_NODE) {
						Element childElement = (Element) childNode;

						if (childElement.getNodeName().equalsIgnoreCase(_XMLTag.g_NodeToolbar)) {
							String id = childElement.getAttribute(_XMLTag.g_AttributionID);
							IToolbarManager toolbarManager = Application.getActiveApplication().getMainFrame().getToolbarManager();
							if (toolbarManager != null && toolbarManager.get(id) != null) {
								XMLToolbar xmlToolbar = toolbarManager.get(id).getXMLToolbar();
								if (xmlToolbar != null) {
									childElement.setAttribute(_XMLTag.g_AttributionIndex, Integer.toString(xmlToolbar.getIndex()));

									childElement.setAttribute(_XMLTag.g_AttributionVisible, Boolean.toString(xmlToolbar.getVisible()));
									childElement.setAttribute(_XMLTag.g_RowIndex, Integer.toString(xmlToolbar.getRowIndex()));
								}
							}

						}
					}
				}

				XmlUtilities.saveXml(this.configLocation, document, document.getXmlEncoding());
			}
		}
	}

	@Override
	public String toString() {
		return this.getName();
	}


	public XMLStartMenus getStartMenus() {
		return startMenus;
	}
}
