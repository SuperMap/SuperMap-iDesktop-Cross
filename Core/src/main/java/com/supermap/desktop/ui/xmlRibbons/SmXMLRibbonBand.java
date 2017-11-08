package com.supermap.desktop.ui.xmlRibbons;

import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.IconRibbonBandResizePolicy;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import java.util.ArrayList;

public class SmXMLRibbonBand extends JRibbonBand {

	private XMLRibbonBand xmlRibbonBand;

	public SmXMLRibbonBand(XMLRibbonBand xmlRibbonBand) {
		super(xmlRibbonBand.getLabel(), null);
		this.xmlRibbonBand = xmlRibbonBand;
		ArrayList<RibbonBandResizePolicy> ribbonBandResizePolicies = new ArrayList<>();
		ribbonBandResizePolicies.add(new CoreRibbonResizePolicies.Mirror(this.getControlPanel()));
		ribbonBandResizePolicies.add(new IconRibbonBandResizePolicy(this.getControlPanel()));
		this.setResizePolicies(ribbonBandResizePolicies);
	}

}
