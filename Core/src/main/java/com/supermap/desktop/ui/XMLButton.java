package com.supermap.desktop.ui;

import com.supermap.desktop.Application;
import com.supermap.desktop.PluginInfo;
import com.supermap.desktop.enums.XMLCommandType;
import org.w3c.dom.Element;

public class XMLButton extends XMLCommand {

	private XMLCommandBase parentTemp;

	public XMLCommandBase getParentTemp() {
		return parentTemp;
	}

	public void setParentTemp(XMLCommand parentTemp) {
		this.parentTemp = parentTemp;
	}

	public XMLButton(PluginInfo pluginInfo, XMLCommandBase parent) {
		super(pluginInfo);
		this.parentTemp = parent;
		this.commandType = XMLCommandType.BUTTON;
	}

	@Override
	public boolean getIsContainer() {
		return false;
	}

	@Override
	public boolean initialize(Element xmlNodeCommand) {
		boolean result = false;
		try {
			result = super.initialize(xmlNodeCommand);
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}

		return result;
	}

	@Override
	public XMLCommandBase clone(XMLCommandBase parent) {
		XMLButton result = null;
		try {
			result = (XMLButton) super.clone(parent);
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}

		return result;
	}

	@Override
	public XMLCommandBase saveToPluginInfo(PluginInfo pluginInfo, XMLCommandBase parent) {
		XMLButton result = null;
		try {
			result = (XMLButton) super.saveToPluginInfo(pluginInfo, parent);
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}

		return result;
	}
}
