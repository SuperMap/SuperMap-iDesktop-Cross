package com.supermap.desktop.WorkflowView.CtrlAction;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.WorkflowView.FormWorkflow;
import com.supermap.desktop.WorkflowView.arithmetic.ArithmeticStrategyType;
import com.supermap.desktop.implement.CtrlAction;

/**
 * Created by xie on 2017/10/19.
 */
public class CtrlActionArithmeticOR extends CtrlAction {
	public CtrlActionArithmeticOR(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	protected void run() {
		IForm form = Application.getActiveApplication().getMainFrame().getFormManager().getActiveForm();
		if (form instanceof FormWorkflow) {
			((FormWorkflow) form).getCanvas().getArithmeticAction().addArithmeticGraph(ArithmeticStrategyType.OR);
		}
	}

	@Override
	public boolean enable() {
		return Application.getActiveApplication().getMainFrame().getFormManager().getActiveForm() instanceof FormWorkflow;
	}
}
