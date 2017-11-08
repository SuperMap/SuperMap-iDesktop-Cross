package com.supermap.desktop.ui.xmlRibbons;

import com.supermap.desktop.Interface.IDefaultValueCreator;
import com.supermap.desktop.Interface.IXMLCreator;
import com.supermap.desktop.enums.XMLCommandType;
import com.supermap.desktop.ui.XMLCommandBase;

/**
 * @author XiaJT
 */
public class XMLRibbonCreator implements IXMLCreator {

	private XMLRibbons xmlRibbons;
	private IDefaultValueCreator defaultValueCreator;

	public XMLRibbonCreator(XMLRibbons xmlRibbons) {
		this.xmlRibbons = xmlRibbons;
	}

	@Override
	public XMLCommandBase createElement(XMLCommandType commandType) {
		// TODO: 2017/10/26
		return null;
	}

	@Override
	public IDefaultValueCreator getDefaultValueCreator() {
		return defaultValueCreator;
	}

	@Override
	public void setDefaultValueCreator(IDefaultValueCreator defaultValueCreator) {
		this.defaultValueCreator = defaultValueCreator;
	}
}
