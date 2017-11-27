package com.supermap.desktop.framemenus;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.ui.controls.JDialogWorkspaceSaveAs;

import javax.swing.*;

/**
 * Created by ChenS on 2017/11/23 0023.
 */
public class CtrlActionWorkspaceSaveAsMYSQL extends CtrlAction {
    public CtrlActionWorkspaceSaveAsMYSQL(IBaseItem caller, IForm formClass) {
        super(caller, formClass);
    }

    @Override
    protected void run() {
        JFrame parent = (JFrame) Application.getActiveApplication().getMainFrame();
        JDialogWorkspaceSaveAs dialog = new JDialogWorkspaceSaveAs(parent, true, JDialogWorkspaceSaveAs.saveAsMySQL);
        dialog.showDialog();
    }
}
