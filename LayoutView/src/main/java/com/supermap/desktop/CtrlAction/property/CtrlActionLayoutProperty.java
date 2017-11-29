package com.supermap.desktop.CtrlAction.property;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.*;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.layoutview.propertycontrols.LayoutPropertyContainer;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/28
 * Time: 10:33
 * Description:
 */
public class CtrlActionLayoutProperty extends CtrlAction{
	private static final String MAP_PRPERTY_CONTROL_CLASS = "com.supermap.desktop.layoutview.propertycontrols.LayoutPropertyContainer";

	public CtrlActionLayoutProperty(IBaseItem caller, IForm formClass) {
		super(caller, formClass);

	}

	@Override
	public void run() {
		try {
			IDockbar dockbarPropertyContainer = Application.getActiveApplication().getMainFrame().getDockbarManager()
					.get(Class.forName(MAP_PRPERTY_CONTROL_CLASS));

			if (dockbarPropertyContainer != null) {
				LayoutPropertyContainer container = (LayoutPropertyContainer) dockbarPropertyContainer.getInnerComponent();
				if (Application.getActiveApplication().getActiveForm() instanceof IFormLayout) {
					container.setFormLayout((IFormLayout) Application.getActiveApplication().getActiveForm());
				}
				dockbarPropertyContainer.setVisible(true);
				dockbarPropertyContainer.active();
			}
		} catch (ClassNotFoundException e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}
}
