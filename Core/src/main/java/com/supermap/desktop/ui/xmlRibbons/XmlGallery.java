package com.supermap.desktop.ui.xmlRibbons;

import com.supermap.desktop.PluginInfo;
import com.supermap.desktop.ui.XMLCommand;
import com.supermap.desktop.ui.XMLCommandBase;
import org.w3c.dom.Element;

/**
 * @author XiaJT
 */
public class XmlGallery extends XMLCommand {
	public XmlGallery(PluginInfo pluginInfo, XMLCommandBase parent) {
		super(pluginInfo, parent);
	}

	@Override
	public boolean initialize(Element xmlNodeCommand) {
		return super.initialize(xmlNodeCommand);
	}
}
