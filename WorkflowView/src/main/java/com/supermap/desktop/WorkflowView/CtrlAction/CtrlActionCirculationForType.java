package com.supermap.desktop.WorkflowView.CtrlAction;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.WorkflowView.FormWorkflow;
import com.supermap.desktop.WorkflowView.circulation.CirculationType;
import com.supermap.desktop.implement.CtrlAction;

/**
 * Created by xie on 2017/10/25.
 */
public class CtrlActionCirculationForType extends CtrlAction {
	public CtrlActionCirculationForType(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	protected void run() {
		IForm form = Application.getActiveApplication().getMainFrame().getFormManager().getActiveForm();
		if (form instanceof FormWorkflow) {
			((FormWorkflow) form).getCanvas().getCirculationAction().addCirculationGraph(CirculationType.forType);
		}
	}

	@Override
	public boolean enable() {
		boolean result = false;
		if (Application.getActiveApplication().getActiveForm() instanceof FormWorkflow
				&& null == ((FormWorkflow) Application.getActiveApplication().getActiveForm()).iterator()) {
			result = true;
		}
		return result;
	}
}
