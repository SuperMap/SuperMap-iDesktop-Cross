package com.supermap.desktop.ui.xmlRibbons;

import com.supermap.desktop.Interface.IDefaultValueCreator;
import com.supermap.desktop.PluginInfo;

/**
 * @author XiaJT
 */
public class RibbonDefaultValueCreator implements IDefaultValueCreator {

	private XMLRibbons xmlRibbons;

	public RibbonDefaultValueCreator(XMLRibbons xmlRibbons) {
		this.xmlRibbons = xmlRibbons;

	}

	@Override
	public String getDefaultLabel(String label) {
		return label;
	}

	@Override
	public String getDefaultID(String id) {
		return id;
	}

	@Override
	public Boolean isIDEnabled(String id) {
		return true;
	}

	@Override
	public int getDefaultIndex() {
		return 0;
	}

	@Override
	public PluginInfo getDefaultPluginInfo() {
		return null;
	}
}
