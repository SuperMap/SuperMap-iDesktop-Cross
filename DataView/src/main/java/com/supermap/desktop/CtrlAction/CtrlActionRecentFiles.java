package com.supermap.desktop.CtrlAction;

import com.supermap.data.Datasource;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.DatasourceOpenFileUtilties;
import com.supermap.desktop.dialog.JDialogGetPassword;
import com.supermap.desktop.enums.OpenWorkspaceResult;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.implement.SmMenuItem;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.UICommonToolkit;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.utilities.RecentFileUtilties;
import com.supermap.desktop.utilities.WorkspaceUtilities;

import java.io.File;
import java.text.MessageFormat;

public class CtrlActionRecentFiles extends CtrlAction {

	public CtrlActionRecentFiles(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
		// Auto-generated constructor stub
	}

	@Override
	public void run() {
		try {
			if (this.getCaller() instanceof SmMenuItem) {
				String filePath = ((SmMenuItem) this.getCaller()).getToolTipText();
				final File file = new File(filePath);
				if (file.exists()) {
					if (isWorkSpaceFile(file)) {
						final WorkspaceConnectionInfo connectionInfo = new WorkspaceConnectionInfo(file.getAbsolutePath());

						OpenWorkspaceResult result = WorkspaceUtilities.openWorkspace(connectionInfo, true);
						if (result == OpenWorkspaceResult.SUCCESSED) {
							if (Application.getActiveApplication().getMainFrame().getFormManager().getCount() > 0) {
								Application.getActiveApplication().getMainFrame().getFormManager().closeAll();
							}
						} else {
							while (result == OpenWorkspaceResult.FAILED_PASSWORD_WRONG) {
								JDialogGetPassword dialogGetPassword = new JDialogGetPassword(CoreProperties.getString("String_WorkspacePasswordPrompt")) {
									@Override
									public boolean isRightPassword(String password) {
										connectionInfo.setPassword(password);
										return WorkspaceUtilities.openWorkspace(connectionInfo, false) != OpenWorkspaceResult.FAILED_PASSWORD_WRONG;
									}
								};
								if (dialogGetPassword.showDialog() == DialogResult.OK) {
									// 按取消按钮不执行打开工作空间的操作。
									result = OpenWorkspaceResult.SUCCESSED;
								} else {
									result = OpenWorkspaceResult.FAILED_CANCEL;
								}
							}
						}

						if (result != OpenWorkspaceResult.SUCCESSED) {
							String stMsg = MessageFormat.format(CoreProperties.getString("String_OpenWorkspaceFailed"), filePath);
							if (result != OpenWorkspaceResult.FAILED_CANCEL) {
								Application.getActiveApplication().getWorkspace().close();
							}
							Application.getActiveApplication().getOutput().output(stMsg);
						}
					} else {
						DatasourceOpenFileUtilties.resetReadOnlyProperties();
						Datasource datasource = DatasourceOpenFileUtilties.openFileDatasource(file);
						if (datasource == null) {
							Application.getActiveApplication().getOutput()
									.output(MessageFormat.format(ControlsProperties.getString("String_DataSource_Openfail"), file.getPath()));
						}
					}
				} else {
					// 提示文件不存在，删除最近文件项
					UICommonToolkit.showMessageDialog(ControlsProperties.getString("String_Error_FileUnExisted"));
					if (isWorkSpaceFile(file)) {
						RecentFileUtilties.removeRecentFile(RecentFileUtilties.FILE_TYPE_WORKSPACE, filePath);
						RecentFileUtilties.fillMenu(RecentFileUtilties.FILE_TYPE_WORKSPACE);
					} else {
						RecentFileUtilties.removeRecentFile(RecentFileUtilties.FILE_TYPE_DATASOURCE, filePath);
						RecentFileUtilties.fillMenu(RecentFileUtilties.FILE_TYPE_DATASOURCE);
					}
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
		if ("smwu".equalsIgnoreCase(fileType) || "sxwu".equalsIgnoreCase(fileType)) {
			flag = true;
		}
		return flag;
	}

}
