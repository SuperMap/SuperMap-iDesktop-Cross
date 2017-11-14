package com.supermap.desktop.implement;

import com.supermap.desktop.Application;
import com.supermap.desktop.ui.IUserDefineComponent;
import com.supermap.desktop.ui.XMLCommand;
import com.supermap.desktop.ui.xmlRibbons.SmXMLFlowBand;
import com.supermap.desktop.ui.xmlRibbons.SmXMLGallery;
import com.supermap.desktop.ui.xmlRibbons.SmXMLRibbonBand;
import com.supermap.desktop.ui.xmlRibbons.SmXmlRibbonButton;
import com.supermap.desktop.ui.xmlRibbons.XMLFlowBand;
import com.supermap.desktop.ui.xmlRibbons.XMLGallery;
import com.supermap.desktop.ui.xmlRibbons.XMLRibbon;
import com.supermap.desktop.ui.xmlRibbons.XMLRibbonBand;
import com.supermap.desktop.ui.xmlRibbons.XMLRibbonButton;
import com.supermap.desktop.utilities.StringUtilities;
import com.supermap.desktop.utilities.SystemPropertyUtilities;
import org.pushingpixels.flamingo.api.common.CommandButtonDisplayState;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;

import javax.swing.*;

/**
 * @author XiaJT
 */
public class SmRibbonTask extends RibbonTask {

	private XMLRibbon xmlRibbon;
	private int index;

	public SmRibbonTask(XMLRibbon xmlMenu) {
		super(xmlMenu.getLabel());
		this.xmlRibbon = xmlMenu;
		initialize();
	}

	private void initialize() {
		if (xmlRibbon != null) {
			index = xmlRibbon.getIndex();
			load();
			reBuildResizeSequencingPolicy();
		}
	}

	private void load() {
		for (int i = 0; i < xmlRibbon.getLength(); i++) {
			XMLCommand group = this.xmlRibbon.getCommandAtIndex(i);
			if (SystemPropertyUtilities.isSupportPlatform(group.getPlatform())) {
				if (group instanceof XMLRibbonBand) {
					loadBandGroup(((XMLRibbonBand) group), this);
				} else if (group instanceof XMLFlowBand) {
					loadBandGroup(((XMLFlowBand) group), this);
				}
			}
		}
	}

	private void loadBandGroup(XMLFlowBand group, SmRibbonTask parent) {
		if (group.getVisible() && group.getLength() > 0) {
			SmXMLFlowBand smXMLFlowBand = new SmXMLFlowBand(group);
			int count = 0;
			for (int i = 0; i < group.getLength(); i++) {
				XMLCommand commandAtIndex = group.getCommandAtIndex(i);
				if (commandAtIndex instanceof XMLRibbonButton) {
					SmXmlRibbonButton button = new SmXmlRibbonButton(((XMLRibbonButton) commandAtIndex));
					if (!StringUtilities.isNullOrEmpty(((XMLRibbonButton) commandAtIndex).getStyle())) {
						if (button.getRibbonElementPriority() == RibbonElementPriority.TOP) {
							button.setDisplayState(CommandButtonDisplayState.BIG);
						} else if (button.getRibbonElementPriority() == RibbonElementPriority.MEDIUM) {
							button.setDisplayState(CommandButtonDisplayState.MEDIUM);
						} else if (button.getRibbonElementPriority() == RibbonElementPriority.LOW) {
							button.setDisplayState(CommandButtonDisplayState.SMALL);
						}
					} else {
						button.setDisplayState(CommandButtonDisplayState.MEDIUM);
					}
					smXMLFlowBand.addFlowComponent(button);
					count++;
				} else if (!(commandAtIndex instanceof IUserDefineComponent)) {
					smXMLFlowBand.addFlowComponent((JComponent) SmComponentFactory.create(commandAtIndex, smXMLFlowBand));
					count++;
				} else {
					smXMLFlowBand.addFlowComponent(((IUserDefineComponent) commandAtIndex).getComponent(smXMLFlowBand));
					count++;
				}
			}
			if (count > 0) {
				parent.addBands(smXMLFlowBand);
			}
		}
	}

	private void loadBandGroup(XMLRibbonBand group, SmRibbonTask parent) {
		try {
			if (group.getVisible() && group.getLength() > 0 && !"RecentFile".equals(group.getID())) {
				SmXMLRibbonBand smXMLRibbonBand = new SmXMLRibbonBand(group);
				int count = 0;
				for (int i = 0; i < group.getLength(); i++) {
					XMLCommand commandAtIndex = group.getCommandAtIndex(i);
					if (commandAtIndex instanceof XMLRibbonButton) {
						count++;
						SmXmlRibbonButton commandButton = new SmXmlRibbonButton((XMLRibbonButton) commandAtIndex);
						commandButton.putInBand(smXMLRibbonBand);
					} else if (commandAtIndex instanceof XMLGallery) {
						count++;
						SmXMLGallery smXmlGallery = new SmXMLGallery((XMLGallery) commandAtIndex);
						smXmlGallery.putInBand(smXMLRibbonBand);
					}
				}
				if (count > 0) {
					parent.addBands(smXMLRibbonBand);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

}
