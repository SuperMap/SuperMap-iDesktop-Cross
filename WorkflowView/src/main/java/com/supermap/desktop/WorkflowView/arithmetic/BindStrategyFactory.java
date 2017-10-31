package com.supermap.desktop.WorkflowView.arithmetic;

/**
 * Created by xie on 2017/10/17.
 */
public class BindStrategyFactory {
	private BindStrategyFactory(){

	}

	public static IArithmeticStrategy getBindStrategy(ArithmeticStrategyType type){
		IArithmeticStrategy result = null;
			switch (type){
				case XOR:

					break;
				case OR:

					break;
				case AND:

					break;
				case LOOP:

					break;
			}
		return result;
	}
}
