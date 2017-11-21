package com.supermap.desktop.CtrlAction;

import com.supermap.desktop.Application;
import com.supermap.desktop.CommonToolkit;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.Interface.IFormMap;
import com.supermap.desktop.enums.WindowType;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.ui.controls.DialogMongoDBLoader;

/**
 * Created by ChenS on 2017/11/21 0021.
 */
public class CtrlActionMongoDBNewMap extends CtrlAction {
    public CtrlActionMongoDBNewMap(IBaseItem caller, IForm formClass) {
        super(caller, formClass);
    }

    @Override
    protected void run() {
        try {
            IFormMap formMap = null;
            if ((Application.getActiveApplication().getActiveForm() != null && Application.getActiveApplication().getActiveForm() instanceof IFormMap)
                    && (((IFormMap) Application.getActiveApplication().getActiveForm()).getMapControl().getMap().getLayers() == null || ((IFormMap) Application
                    .getActiveApplication().getActiveForm()).getMapControl().getMap().getLayers().getCount() <= 0)) {
                formMap = (IFormMap) Application.getActiveApplication().getActiveForm();
            }

            if (formMap == null) {
                formMap = (IFormMap) CommonToolkit.FormWrap.fireNewWindowEvent(WindowType.MAP);
            }
            if (formMap != null) {
                DialogMongoDBLoader dialogMongoDBLoader = new DialogMongoDBLoader();
                dialogMongoDBLoader.showDialog();
            }
        } catch (Exception e) {
            Application.getActiveApplication().getOutput().output(e);
        }
    }
}
