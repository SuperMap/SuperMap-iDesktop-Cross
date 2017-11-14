package com.supermap.desktop.ui.xmlRibbons;

import com.supermap.desktop.utilities.XmlCommandUtilities;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JFlowRibbonBand;

import java.awt.*;

/**
 * @author XiaJT
 */
public class SmXMLFlowBand extends JFlowRibbonBand{
	public SmXMLFlowBand(XMLFlowBand group) {
		super(group.getLabel(),null,group.getRowCount());
		Image image = XmlCommandUtilities.getXmlCommandImage(group);
		ImageWrapperResizableIcon iCon = XmlCommandUtilities.getICon(image);
		if (iCon != null) {
			this.setIcon(iCon);
		}
	}
}
