package com.supermap.desktop.WorkflowView.CtrlAction;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.WorkflowView.FormWorkflow;
import com.supermap.desktop.WorkflowView.circulation.CirculationGraphFactory;
import com.supermap.desktop.WorkflowView.circulation.CirculationType;
import com.supermap.desktop.implement.CtrlAction;

/**
 * Created by xie on 2017/11/7.
 */
public class CtrlActionCirculationForObjectType extends CtrlAction {
	public CtrlActionCirculationForObjectType(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	protected void run() {
		CirculationGraphFactory.addCirculationGraph(CirculationType.forObjectType);
	}

	@Override
	public boolean enable() {
		return Application.getActiveApplication().getActiveForm() instanceof FormWorkflow;
	}

}
