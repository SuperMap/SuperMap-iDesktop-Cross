package com.supermap.desktop.framemenus;

import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.implement.SmMenuItem;
import com.supermap.desktop.ui.xmlStartMenus.SmSubMenu;
import com.supermap.desktop.utilities.DatasourceUtilities;
import com.supermap.desktop.utilities.PathUtilities;
import com.supermap.desktop.utilities.WorkspaceUtilities;

import java.io.File;

public class CtrlActionExampleData extends CtrlAction {

	public CtrlActionExampleData(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public void run() {
		try {
			if (this.getCaller() instanceof SmMenuItem) {
				String filePath = ((SmMenuItem) this.getCaller()).getToolTipText();
				String configFile = PathUtilities.getFullPathName(filePath, false);
				File file = new File(configFile);
				if (isWorkSpaceFile(file)) {
					WorkspaceConnectionInfo connectionInfo = new WorkspaceConnectionInfo(file.getAbsolutePath());
					WorkspaceUtilities.openWorkspace(connectionInfo, true);
				} else {
					DatasourceUtilities.openFileDatasource(file.getAbsolutePath(), null, false);
				}

			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}

	}

	private boolean isWorkSpaceFile(File file) {
		boolean flag = false;
		String fileName = file.getName();
		String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		if ("smwu".equalsIgnoreCase(fileType) || "sxmu".equalsIgnoreCase(fileType)) {
			flag = true;
		}
		return flag;
	}

	@Override
	public boolean enable() {
		boolean result = false;
		String filePath = ((SmSubMenu) this.getCaller()).getTooltip();
		String configFile = PathUtilities.getFullPathName(filePath, false);
		File file = new File(configFile);
		if (file.exists()) {
			result = true;
		}
		return result;
	}

}
