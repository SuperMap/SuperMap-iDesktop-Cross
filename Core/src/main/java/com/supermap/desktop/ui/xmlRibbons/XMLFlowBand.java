package com.supermap.desktop.ui.xmlRibbons;

import com.supermap.desktop.Application;
import com.supermap.desktop.PluginInfo;
import com.supermap.desktop.ui.IUserDefineComponent;
import com.supermap.desktop.ui.XMLCheckBox;
import com.supermap.desktop.ui.XMLCommand;
import com.supermap.desktop.ui.XMLCommandBase;
import com.supermap.desktop.ui.XMLLabel;
import com.supermap.desktop.ui.XMLTextField;
import com.supermap.desktop.utilities.StringUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 * @author XiaJT
 */
public class XMLFlowBand extends XMLCommand {
	private ArrayList<XMLCommand> commands = new ArrayList<>();
	private int rowCount = 2;

	public XMLFlowBand(PluginInfo pluginInfo, XMLCommandBase xmlRibbon) {
		super(pluginInfo, xmlRibbon);
		canMerge = true;
	}

	@Override
	public boolean initialize(Element element) {
		super.initialize(element);
		try {
			String rowCount = element.getAttribute("rowCount");
			this.rowCount = Integer.valueOf(rowCount);
			if (this.rowCount != 2 && this.rowCount != 3) {
				// 只能为 2 或 3
				this.rowCount = 2;
			}
		} catch (Exception e) {
			// 没有就算了
		}
		for (int i = 0; i < element.getChildNodes().getLength(); i++) {
			Node item = element.getChildNodes().item(i);
			if (item.getNodeType() == Node.ELEMENT_NODE) {
				XMLCommand xmlCommand = this.buildCommand(((Element) item));
				if (xmlCommand != null) {
					xmlCommand.initialize(((Element) item));
					addSubItem(xmlCommand);
				}
			}
		}
		return true;
	}

	@Override
	public void addSubItem(XMLCommandBase xmlCommand) {
		for (int i = 0; i < commands.size(); i++) {
			XMLCommand command = commands.get(i);
			if (command.getIndex() > xmlCommand.getIndex()) {
				commands.add(i, (XMLCommand) xmlCommand);
				break;
			}
		}
		if (!commands.contains(xmlCommand)) {
			commands.add((XMLCommand) xmlCommand);
		}
	}

	private XMLCommand buildCommand(Element item) {
		String nodeName = item.getNodeName();
		XMLCommand command = null;
		if (nodeName.equals(g_ControlButton)) {
			command = new XMLRibbonButton(getPluginInfo(), this);
		} else if(nodeName.equals(g_ControlCheckbox)){
			command = new XMLCheckBox(getPluginInfo(), this);
		} else if (nodeName.equals(g_ControlEditBox)) {
			command = new XMLTextField(getPluginInfo(), this);
		} else if (nodeName.equals(g_ControlLabel)) {
			command = new XMLLabel(getPluginInfo(), this);
		} else if (nodeName.equals(USER_DEFINE_COMPONENT)) {
			try {
				 // 自定义控件通过提供的类来查找，需要实现IUserDefineComponent接口
				String aClass = item.getAttribute("class");
				Class<?> aClass1 = Class.forName(aClass);
				Class<?>[] interfaces = aClass1.getInterfaces();
				for (Class<?> anInterface : interfaces) {
					if (anInterface== IUserDefineComponent.class) {
						Constructor<?> constructor = aClass1.getConstructor(PluginInfo.class, XMLCommandBase.class);
						command = ((XMLCommand) constructor.newInstance(getPluginInfo(), this));
						break;
					}
				}
			} catch (Exception e) {
				Application.getActiveApplication().getOutput().output(e);
			}
		}
		return command;
	}

	@Override
	public void merge(XMLCommand otherCommand) {
		if (otherCommand instanceof XMLRibbonBand) {
			XMLRibbonBand otherCommandBand = (XMLRibbonBand) otherCommand;
			for (int i = 0; i < otherCommandBand.getLength(); i++) {
				XMLCommand mergeCommand = otherCommandBand.getCommandAtIndex(i);
				boolean isContain = false;
				for (XMLCommand command : commands) {
					if (command.canMerge() && !StringUtilities.isNullOrEmpty(command.getID()) && command.getID().equals(mergeCommand.getID())) {
						command.merge(mergeCommand);
						isContain = true;
						break;
					}
				}
				if (!isContain) {
					mergeCommand.copyTo(this);
				}
			}
		}
	}

	public XMLCommand getCommandAtIndex(int i) {
		return commands.get(i);
	}

	public int getLength() {
		return commands.size();
	}

	@Override
	public XMLCommandBase copyTo(XMLCommandBase parent) {
		return super.copyTo(parent);
	}

	@Override
	protected XMLCommandBase createNew(XMLCommandBase parent) {
		XMLFlowBand xmlFlowBand = new XMLFlowBand(getPluginInfo(), parent);
		xmlFlowBand.commands = commands;

		return xmlFlowBand;
	}

	public int getRowCount() {
		return rowCount;
	}
}
