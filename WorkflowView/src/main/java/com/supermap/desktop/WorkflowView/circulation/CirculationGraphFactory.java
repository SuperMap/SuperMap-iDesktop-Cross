package com.supermap.desktop.WorkflowView.circulation;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.WorkflowView.FormWorkflow;

/**
 * Created by xie on 2017/11/9.
 */
public class CirculationGraphFactory {
	public static void addCirculationGraph(CirculationType circulationType){
		IForm form = Application.getActiveApplication().getMainFrame().getFormManager().getActiveForm();
		if (null != form && form instanceof FormWorkflow) {
			((FormWorkflow) form).getCanvas().getCirculationAction().addCirculationGraph(circulationType);
		}
	}
}
