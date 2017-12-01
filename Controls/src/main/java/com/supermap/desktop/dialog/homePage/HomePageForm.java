package com.supermap.desktop.dialog.homePage;

import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.ui.FormBaseChild;

import javax.swing.*;
import java.awt.*;

/**
 * Created by yuanR on 2017/11/30 0030.
 */
public class HomePageForm extends FormBaseChild {

	JPanelRecentlyUsed panelRecentlyUsed = new JPanelRecentlyUsed();

	public HomePageForm(String title, Icon icon, Component component) {
		super(title, icon, component);
	}

	public HomePageForm() {
		super(ControlsProperties.getString("String_HomePage"), null, null);
		this.add(panelRecentlyUsed);
	}

}
