package com.supermap.desktop.CtrlAction;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.Interface.IFormMap;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.ui.controls.DialogMongoDBLoader;

/**
 * Created by ChenS on 2017/10/30 0030.
 */
public class CtrlActionMongoDBCache extends CtrlAction {
    public CtrlActionMongoDBCache(IBaseItem caller, IForm formClass) {
        super(caller, formClass);
    }

    @Override
    protected void run() {
        if (Application.getActiveApplication().getActiveForm() instanceof IFormMap) {
            DialogMongoDBLoader dialogMongoDBLoader = new DialogMongoDBLoader();
            dialogMongoDBLoader.showDialog();
        }
    }

    @Override
    public boolean enable() {
        return Application.getActiveApplication().getActiveForm() instanceof IFormMap;
    }
}
