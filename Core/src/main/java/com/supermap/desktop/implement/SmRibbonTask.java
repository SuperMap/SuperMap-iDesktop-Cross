package com.supermap.desktop.implement;

import com.supermap.desktop.Application;
import com.supermap.desktop.ui.XMLCommand;
import com.supermap.desktop.ui.XMLMenu;
import com.supermap.desktop.ui.XMLMenuButton;
import com.supermap.desktop.ui.XMLMenuButtonDropdown;
import com.supermap.desktop.ui.XMLMenuGroup;
import com.supermap.desktop.utilities.SystemPropertyUtilities;
import com.supermap.desktop.utilities.XmlCommandUtilities;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.common.StringValuePair;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.IconRibbonBandResizePolicy;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author XiaJT
 */
public class SmRibbonTask extends RibbonTask {

	private XMLMenu xmlMenu;
	private int index;

	public SmRibbonTask(XMLMenu xmlMenu) {
		super(xmlMenu.getLabel());
		this.xmlMenu = xmlMenu;
		initialize();
	}

	private void initialize() {
		if (xmlMenu != null) {
			index = xmlMenu.getIndex();
			load();
			reBuildResizeSequencingPolicy();
		}
	}

	private void load() {
		for (int i = 0; i < xmlMenu.groups().size(); i++) {
			XMLMenuGroup group = this.xmlMenu.groups().get(i);
			if (SystemPropertyUtilities.isSupportPlatform(group.getPlatform())) {
				loadMenuGroup(group, this);
			}
		}
	}

	private void loadMenuGroup(XMLMenuGroup group, SmRibbonTask parent) {
		try {
			if (group.getVisible() && !group.items().isEmpty() && !"RecentFile".equals(group.getID())) {
				ArrayList<XMLCommand> xmlCommandsList = new ArrayList<>();
				getXmlCommands(group, xmlCommandsList);
				JRibbonBand jRibbonBand = new JRibbonBand(group.getID(), null);
				ArrayList<RibbonBandResizePolicy> ribbonBandResizePolicies = new ArrayList<>();
				ribbonBandResizePolicies.add(new CoreRibbonResizePolicies.Mirror(jRibbonBand.getControlPanel()));
				ribbonBandResizePolicies.add(new IconRibbonBandResizePolicy(jRibbonBand.getControlPanel()));
				jRibbonBand.setResizePolicies(ribbonBandResizePolicies);

				if (xmlCommandsList.size() < 6) {
					for (XMLCommand xmlCommand : xmlCommandsList) {
						Image image = XmlCommandUtilities.getXmlCommandImage(xmlCommand);
						ImageWrapperResizableIcon icon = image == null ? null : ImageWrapperResizableIcon.getIcon(image, new Dimension(23, 23));
						jRibbonBand.addCommandButton(new JCommandButton(xmlCommand.getLabel(), icon), RibbonElementPriority.TOP);
					}
				} else {
					ArrayList<JCommandToggleButton> jCommandToggleButtons = new ArrayList<>();
					for (XMLCommand xmlCommand : xmlCommandsList) {
						Image image = XmlCommandUtilities.getXmlCommandImage(xmlCommand);
						ImageWrapperResizableIcon icon = image == null ? null : ImageWrapperResizableIcon.getIcon(image, new Dimension(23, 23));
						jCommandToggleButtons.add(new JCommandToggleButton(xmlCommand.getLabel(), icon));
					}
					Map<RibbonElementPriority, Integer> prefers = new HashMap<>();
					prefers.put(RibbonElementPriority.LOW, 2);
					prefers.put(RibbonElementPriority.MEDIUM, 3);
					prefers.put(RibbonElementPriority.TOP, 3);
					StringValuePair<List<JCommandToggleButton>> listStringValuePair = new StringValuePair<List<JCommandToggleButton>>(group.getLabel() + "1", jCommandToggleButtons);
					List<StringValuePair<List<JCommandToggleButton>>> svps = new ArrayList<>();
					svps.add(listStringValuePair);
					String label = group.getLabel();
					jRibbonBand.addRibbonGallery(label, svps, prefers, 5, 3, RibbonElementPriority.TOP);
				}
				parent.addBands(jRibbonBand);


			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	private void getXmlCommands(XMLMenuGroup group, ArrayList<XMLCommand> xmlCommandsList) {
		if (!"RecentFile".equals(group.getID()) && SystemPropertyUtilities.isSupportPlatform(group.getPlatform())) {
			for (XMLCommand xmlCommand : group.items()) {
				getXmlCommands(xmlCommand, xmlCommandsList);
			}
		}
	}

	private void getXmlCommands(XMLCommand xmlCommand, ArrayList<XMLCommand> xmlCommandsList) {
		if (xmlCommand instanceof XMLMenuButtonDropdown) {
			for (XMLMenuGroup xmlMenuGroup : ((XMLMenuButtonDropdown) xmlCommand).groups()) {
				getXmlCommands(xmlMenuGroup, xmlCommandsList);
			}
		} else if (xmlCommand instanceof XMLMenuButton) {
			if (SystemPropertyUtilities.isSupportPlatform(xmlCommand.getPlatform())) {
				xmlCommandsList.add(xmlCommand);
			}
		}
	}


	private void loadMenuButton(XMLCommand xmlCommand, JRibbonBand jRibbonBand) {
		if (xmlCommand instanceof XMLMenuButtonDropdown) {
			for (XMLMenuGroup xmlMenuGroup : ((XMLMenuButtonDropdown) xmlCommand).groups()) {
				if (xmlMenuGroup.getVisible() && !xmlMenuGroup.items().isEmpty()) {
					for (XMLCommand command : xmlMenuGroup.items()) {
						loadMenuButton(command, jRibbonBand);
					}
				}
			}
		} else if (xmlCommand instanceof XMLMenuButton) {
			Image image = XmlCommandUtilities.getXmlCommandImage(xmlCommand);
			jRibbonBand.addCommandButton(new JCommandButton(xmlCommand.getLabel(), image == null ? null : ImageWrapperResizableIcon.getIcon(image, new Dimension(23, 23))), RibbonElementPriority.MEDIUM);
		}
	}

}
