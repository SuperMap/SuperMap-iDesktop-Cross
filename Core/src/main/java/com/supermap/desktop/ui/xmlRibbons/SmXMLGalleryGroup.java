package com.supermap.desktop.ui.xmlRibbons;

import com.supermap.desktop.ui.XMLCommand;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.common.StringValuePair;

import java.util.ArrayList;
import java.util.List;

public class SmXMLGalleryGroup {

	private XMLGalleryGroup command;
	private StringValuePair<List<JCommandToggleButton>> stringValuePair;

	public SmXMLGalleryGroup(XMLGalleryGroup xmlCommand) {
		command = xmlCommand;
		List<JCommandToggleButton> smXMlGalleryButtons = new ArrayList<>();
		for (int i = 0; i < command.getLength(); i++) {
			XMLCommand childCommand = command.getXMLCommandAtIndex(i);
			if (childCommand instanceof XMLRibbonGalleryButton) {
				smXMlGalleryButtons.add(new SmXMlGalleryButton((XMLRibbonGalleryButton) childCommand));
			}
		}
		stringValuePair = new StringValuePair<>(command.getLabel(), smXMlGalleryButtons);
	}

	public XMLCommand getXMLCommand() {
		return command;
	}

	public StringValuePair<List<JCommandToggleButton>> getStringValuePair() {
		return stringValuePair;
	}
}
