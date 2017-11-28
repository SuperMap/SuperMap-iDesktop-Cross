package com.supermap.desktop.CtrlAction.MapOperator;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IDockbar;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.Interface.IFormMap;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.mapview.PositionLocalizerPanel;

/**
 * Created by ChenS on 2017/11/28 0028.
 */
public class CtrlActionPositionLocalizer extends CtrlAction {
    public CtrlActionPositionLocalizer(IBaseItem caller, IForm formClass) {
        super(caller, formClass);
    }

    @Override
    protected void run() {
        try {
            IDockbar dockbarPropertyContainer = Application.getActiveApplication().getMainFrame().getDockbarManager()
                    .get(Class.forName("com.supermap.desktop.mapview.PositionLocalizerPanel"));

            if (dockbarPropertyContainer != null) {
                PositionLocalizerPanel panel = (PositionLocalizerPanel) dockbarPropertyContainer.getInnerComponent();
                if (Application.getActiveApplication().getActiveForm() instanceof IFormMap) {
                    panel.setFormMap((IFormMap) Application.getActiveApplication().getActiveForm());
                }
                dockbarPropertyContainer.setVisible(true);
                dockbarPropertyContainer.active();
            }
        } catch (ClassNotFoundException e) {
            Application.getActiveApplication().getOutput().output(e);
        }
    }

    @Override
    public boolean enable() {
        IForm activeForm = Application.getActiveApplication().getActiveForm();
        return activeForm != null && activeForm instanceof IFormMap;
    }
}
