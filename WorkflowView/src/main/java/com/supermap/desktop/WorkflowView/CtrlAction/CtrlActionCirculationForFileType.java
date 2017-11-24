package com.supermap.desktop.WorkflowView.CtrlAction;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.WorkflowView.FormWorkflow;
import com.supermap.desktop.WorkflowView.circulation.CirculationGraphFactory;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.process.core.CirculationType;

/**
 * Created by xie on 2017/11/23.
 */
public class CtrlActionCirculationForFileType extends CtrlAction {
	public CtrlActionCirculationForFileType(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	protected void run() {
		CirculationGraphFactory.addCirculationGraph(CirculationType.forFileType);
	}

	@Override
	public boolean enable() {
		return Application.getActiveApplication().getActiveForm() instanceof FormWorkflow;
	}

}