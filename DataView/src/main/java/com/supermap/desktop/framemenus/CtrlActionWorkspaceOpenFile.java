package com.supermap.desktop.framemenus;

import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.dataview.DataViewProperties;
import com.supermap.desktop.dialog.JDialogGetPassword;
import com.supermap.desktop.enums.OpenWorkspaceResult;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.SmFileChoose;
import com.supermap.desktop.utilities.LogUtilities;
import com.supermap.desktop.utilities.WorkspaceUtilities;

import javax.swing.*;
import java.text.MessageFormat;

public class CtrlActionWorkspaceOpenFile extends CtrlAction {
	private WorkspaceConnectionInfo info;
	private OpenWorkspaceResult result;

	public CtrlActionWorkspaceOpenFile(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public void run() {
		try {
			LogUtilities.outPut(LogUtilities.getSeparator());
			LogUtilities.outPut(DataViewProperties.getString("String_openWorkspaceing"));

			if (!SmFileChoose.isModuleExist("WorkspaceOpenFile")) {
				String fileFilters = SmFileChoose.createFileFilter(ControlsProperties.getString("String_FileFilters_Workspace"), "smwu", "sxwu");
				SmFileChoose.addNewNode(fileFilters, CoreProperties.getString("String_DefaultFilePath"),
						ControlsProperties.getString("String_OpenWorkspace"), "WorkspaceOpenFile", "OpenOne");
			}

			SmFileChoose fileChooser = new SmFileChoose("WorkspaceOpenFile");
			if (fileChooser.showDefaultDialog() == JFileChooser.APPROVE_OPTION && !"".equals(fileChooser.getSelectedFile().getAbsolutePath())) {

				LogUtilities.outPut(MessageFormat.format(DataViewProperties.getString("String_ChooseedFilePath"), fileChooser.getFilePath()));

				info = new WorkspaceConnectionInfo(fileChooser.getFilePath());
				result = WorkspaceUtilities.openWorkspace(info, true);
				if (result == OpenWorkspaceResult.SUCCESSED) {
					if (Application.getActiveApplication().getMainFrame().getFormManager().getCount() > 0) {
						LogUtilities.outPut(MessageFormat.format(CoreProperties.getString("String_CloseForms"), Application.getActiveApplication().getMainFrame().getFormManager().getCount()));
						Application.getActiveApplication().getMainFrame().getFormManager().closeAll();
						LogUtilities.outPut(CoreProperties.getString("String_CloseFormsSuccess"));
					}
				} else if (result == OpenWorkspaceResult.FAILED_PASSWORD_WRONG) {
					LogUtilities.outPut(CoreProperties.getString("String_inputPassword"));
					JDialogGetPassword dialogGetPassword = new JDialogGetPassword(CoreProperties.getString("String_WorkspacePasswordPrompt")) {

						private static final long serialVersionUID = 1L;

						public boolean isRightPassword(String password) {
							info.setPassword(getPassword());
							result = WorkspaceUtilities.openWorkspace(info, false);
							return result != OpenWorkspaceResult.FAILED_PASSWORD_WRONG;

						}
					};
					dialogGetPassword.showDialog();
				}

				if (result != OpenWorkspaceResult.SUCCESSED) {
					String stMsg;
					if (result != OpenWorkspaceResult.FAILED_CANCEL) {
						stMsg = MessageFormat.format(CoreProperties.getString("String_OpenWorkspaceFailed"), fileChooser.getFilePath());
						Application.getActiveApplication().getWorkspace().close();
					} else if (result == OpenWorkspaceResult.FAILED_PASSWORD_WRONG) {
						stMsg = MessageFormat.format(CoreProperties.getString("String_OpenWorkspaceFailed_WrongPassword"), fileChooser.getFilePath());
					} else {
						stMsg = CoreProperties.getString("String_openWorkspaceCancle");
					}
					Application.getActiveApplication().getOutput().output(stMsg);
					LogUtilities.outPut(stMsg);
				} else {
					LogUtilities.outPut(MessageFormat.format(CoreProperties.getString("String_openWorksapceSuccess"), Application.getActiveApplication().getWorkspace().getCaption()));
				}
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	@Override
	public boolean enable() {
		return true;
	}

}
