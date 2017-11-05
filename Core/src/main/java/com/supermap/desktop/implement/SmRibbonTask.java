package com.supermap.desktop.implement;

import com.supermap.desktop.Application;
import com.supermap.desktop.ui.XMLCommand;
import com.supermap.desktop.ui.xmlRibbons.*;
import com.supermap.desktop.utilities.SystemPropertyUtilities;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;

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
			XMLRibbonBand group = ((XMLRibbonBand) this.xmlRibbon.getCommandAtIndex(i));
			if (SystemPropertyUtilities.isSupportPlatform(group.getPlatform())) {
				loadMenuGroup(group, this);
			}
		}
	}

	private void loadMenuGroup(XMLRibbonBand group, SmRibbonTask parent) {
		try {
			if (group.getVisible() && group.getLength() > 0 && !"RecentFile".equals(group.getID())) {
				SmXMLRibbonBand smXMLRibbonBand = new SmXMLRibbonBand(group);
				int count = 0;
				for (int i = 0; i < group.getLength(); i++) {
					XMLCommand commandAtIndex = group.getCommandAtIndex(i);
					if (commandAtIndex instanceof XmlRibbonButton) {
						count++;
						SmXmlRibbonButton commandButton = new SmXmlRibbonButton((XmlRibbonButton) commandAtIndex);
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


//					ArrayList<JCommandToggleButton> jCommandToggleButtons = new ArrayList<>();
//					for (XMLCommand xmlCommand : xmlCommandsList) {
//						Image image = XmlCommandUtilities.getXmlCommandImage(xmlCommand);
//						ImageWrapperResizableIcon icon = image == null ? null : ImageWrapperResizableIcon.getIcon(image, new Dimension(23, 23));
//						jCommandToggleButtons.add(new JCommandToggleButton(xmlCommand.getLabel(), icon));
//					}
//					Map<RibbonElementPriority, Integer> prefers = new HashMap<>();
//					prefers.put(RibbonElementPriority.LOW, 2);
//					prefers.put(RibbonElementPriority.MEDIUM, 3);
//					prefers.put(RibbonElementPriority.TOP, 3);
//					StringValuePair<List<JCommandToggleButton>> listStringValuePair = new StringValuePair<List<JCommandToggleButton>>(group.getLabel() + "1", jCommandToggleButtons);
//					List<StringValuePair<List<JCommandToggleButton>>> svps = new ArrayList<>();
//					svps.add(listStringValuePair);
//					String label = group.getLabel();
//					jRibbonBand.addRibbonGallery(label, svps, prefers, 5, 3, RibbonElementPriority.TOP);



			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

}
