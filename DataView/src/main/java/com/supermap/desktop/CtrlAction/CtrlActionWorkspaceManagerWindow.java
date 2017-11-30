package com.supermap.desktop.CtrlAction;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.Interface.IFormManager;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.workspacemanagerwindow.WorkspaceManagerWindow;

/**
 * @author YuanR
 * 存留问题：1、当打开或者关闭工作空间时，因为closeAll()方法的执行，会导致窗口被关闭，应该保持窗口的常在，当工作空间被改变时，刷新窗口显示
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
