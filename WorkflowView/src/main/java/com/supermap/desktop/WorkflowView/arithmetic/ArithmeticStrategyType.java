package com.supermap.desktop.WorkflowView.arithmetic;

/**
 * Created by xie on 2017/10/17.
 */
public enum ArithmeticStrategyType {
	XOR("XOR Gateway"),//一进一出!
	OR("OR Gateway"),//一进多出
	AND("AND Gateway"),//合并
	LOOP("LOOP Gateway")//循环
	;
	private String type;

	ArithmeticStrategyType(String type) {
		this.type = type;
	}

	@Override
public String toString() {
		return this.type;
	}
}
