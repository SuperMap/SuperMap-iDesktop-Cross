package com.supermap.desktop.CtrlAction;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.Interface.IFormManager;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.workspacemanagerwindow.WorkspaceManagerWindow;

/**
 * @author YuanR
 */
public class CtrlActionWorkspaceManagerWindow extends CtrlAction {
	public CtrlActionWorkspaceManagerWindow(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	private WorkspaceManagerWindow workspaceManagerWindow;

	public void run() {
		try {
			if (this.workspaceManagerWindow == null) {
				this.workspaceManagerWindow = new WorkspaceManagerWindow();
			}
			IFormManager formManager = Application.getActiveApplication().getMainFrame().getFormManager();
			if (!workspaceManagerWindow.isShowing()) {
				formManager.showChildForm(workspaceManagerWindow);
			} else {
				formManager.close(this.workspaceManagerWindow);
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	@Override
	public boolean enable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean check() {
		if (workspaceManagerWindow == null) {
			return false;
		}
		IFormManager formManager = Application.getActiveApplication().getMainFrame().getFormManager();
		return formManager.isContain(workspaceManagerWindow);
	}
}
