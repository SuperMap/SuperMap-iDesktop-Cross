package com.supermap.desktop.CtrlAction.LayoutAddMap;

import com.supermap.data.Workspace;
import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.Interface.IFormLayout;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.ui.Action;
import com.supermap.ui.MapLayoutControl;
import com.supermap.ui.TrackMode;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/30
 * Time: 13:43
 * Description:
 */
public class CtrlActionAddMapBase extends CtrlAction {

	public CtrlActionAddMapBase(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	public Action getAction() {
		return Action.NULL;
	}

	@Override
	public void run() {
		try {
			MapLayoutControl activeMapControl = ((IFormLayout) Application.getActiveApplication().getActiveForm()).getMapLayoutControl();
			activeMapControl.setTrackMode(TrackMode.EDITGEOMAP);
			activeMapControl.setLayoutAction(getAction());
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	@Override
	public boolean enable() {
		Boolean enable = false;
		Workspace workspace = Application.getActiveApplication().getWorkspace();
		if (workspace.getMaps().getCount() > 0) {
			enable = true;
		}
		return enable;
	}

	@Override
	public boolean check() {
		boolean checkState = true;
		IFormLayout formLayout = (IFormLayout) Application.getActiveApplication().getActiveForm();
		if (formLayout != null && formLayout.getMapLayoutControl().getLayoutAction() == getAction() &&
				formLayout.getMapLayoutControl().getTrackMode() == TrackMode.EDITGEOMAP) {
			checkState = false;
		}

		return checkState;
	}

}
