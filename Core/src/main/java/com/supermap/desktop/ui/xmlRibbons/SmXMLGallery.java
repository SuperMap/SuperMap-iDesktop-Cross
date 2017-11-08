package com.supermap.desktop.ui.xmlRibbons;

import com.supermap.desktop.ui.XMLCommand;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.common.StringValuePair;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmXMLGallery {

	private XMLGallery xmlGallery;
	private ArrayList<SmXMLGalleryGroup> galleryGroups = new ArrayList<>();

	public SmXMLGallery(XMLGallery xmlGallery) {
		this.xmlGallery = xmlGallery;
		for (int i = 0; i < xmlGallery.getLength(); i++) {
			XMLCommand xmlCommand = xmlGallery.getXMLCommand(i);
			if (xmlCommand instanceof XMLGalleryGroup) {
				galleryGroups.add(new SmXMLGalleryGroup((XMLGalleryGroup) xmlCommand));
			}
		}
	}

	public void putInBand(SmXMLRibbonBand smXMLRibbonBand) {
		ArrayList<StringValuePair<List<JCommandToggleButton>>> stringValuePairs = new ArrayList<>();
		for (SmXMLGalleryGroup galleryGroup : galleryGroups) {
			stringValuePairs.add(galleryGroup.getStringValuePair());
		}
		Map<RibbonElementPriority, Integer> ribbonElementPriorityIntegerMap = new HashMap<>();
		ribbonElementPriorityIntegerMap.put(RibbonElementPriority.LOW, 4);
		ribbonElementPriorityIntegerMap.put(RibbonElementPriority.MEDIUM, 4);
		ribbonElementPriorityIntegerMap.put(RibbonElementPriority.TOP, 4);
		smXMLRibbonBand.addRibbonGallery(xmlGallery.getLabel(), stringValuePairs, ribbonElementPriorityIntegerMap,
				5, 10, RibbonElementPriority.TOP);

	}
}
