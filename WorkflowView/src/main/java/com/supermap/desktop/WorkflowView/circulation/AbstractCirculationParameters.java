package com.supermap.desktop.WorkflowView.circulation;

import com.supermap.desktop.process.core.IProcess;
import com.supermap.desktop.process.parameter.ipls.DefaultParameters;

/**
 * Created by xie on 2017/10/27.
 */
public class AbstractCirculationParameters extends DefaultParameters {

	public AbstractCirculationParameters(IProcess process) {
		super(process);
	}

	public AbstractCirculationParameters() {
		this(null);
	}


}
