package com.supermap.desktop.WorkflowView.circulation;

import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;

/**
 * Created by xie on 2017/10/27.
 */
public class CirculationParametersFactory {

	public static AbstractCirculationParameters getCirculationOutParameters(CirculationType type, OutputData outputData) {
		AbstractCirculationParameters circulationOutParameters = null;
		switch (type) {
			case forType:
				circulationOutParameters = new CirculationForOutputParameters(outputData);
				break;
			default:
				break;
		}
		return circulationOutParameters;
	}


	public static AbstractCirculationParameters getCirculationParameters(CirculationType type, OutputData outputData) {
		AbstractCirculationParameters circulationParameters = null;
		switch (type) {
			case forType:
				circulationParameters = new CirculationForParameters(outputData);
				break;
			default:
				break;
		}
		return circulationParameters;
	}
}