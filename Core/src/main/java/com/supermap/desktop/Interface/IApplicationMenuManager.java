package com.supermap.desktop.Interface;

import com.supermap.desktop.WorkEnvironment;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenu;

/**
 * @author XiaJT
 */
public interface IApplicationMenuManager  {
	void load(RibbonApplicationMenu menu, WorkEnvironment workEnvironment);
}
