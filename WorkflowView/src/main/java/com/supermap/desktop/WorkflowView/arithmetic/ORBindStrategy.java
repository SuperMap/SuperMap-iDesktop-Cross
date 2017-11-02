package com.supermap.desktop.WorkflowView.arithmetic;

import com.supermap.desktop.process.core.Workflow;

/**
 * Created by xie on 2017/10/18.
 * 一进多出
 */
public class ORBindStrategy extends AbstractArithmeticStrategy implements IArithmeticStrategy {

	public ORBindStrategy(Workflow workflow) {
		super(workflow);
	}

	@Override
	public void buildStrategy(String itemName) {}
}
