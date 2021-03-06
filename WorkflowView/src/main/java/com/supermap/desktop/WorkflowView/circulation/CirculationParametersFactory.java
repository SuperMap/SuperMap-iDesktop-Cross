package com.supermap.desktop.WorkflowView.circulation;

import com.supermap.desktop.process.core.AbstractCirculationParameters;
import com.supermap.desktop.process.core.CirculationType;
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
			case forDatasetType:
				circulationOutParameters = new CirculationForDatasetOutputParameters(outputData);
				break;
			case forObjectType:
				circulationOutParameters = new CirculationForObjectOutputParameters(outputData);
				break;
			case forFieldType:
				circulationOutParameters = new CirculationForFieldOutputParameters(outputData);
				break;
			case forDatasourceType:
				circulationOutParameters = new CirculationForDatasourceOutputParameters(outputData);
				break;
			case forFileType:
				circulationOutParameters = new CirculationForFileOutputParameters(outputData);
				break;
			case whileType:
				circulationOutParameters = new CirculationWhileOutputParameters(outputData);
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
			case forDatasetType:
				circulationParameters = new CirculationForDatasetParameters(outputData);
				break;
			case forObjectType:
				circulationParameters = new CirculationForObjectParameters(outputData);
				break;
			case forFieldType:
				circulationParameters = new CirculationForFieldParameters(outputData);
				break;
			case forDatasourceType:
				circulationParameters = new CirculationForDatasourceParameters(outputData);
				break;
			case forFileType:
				circulationParameters = new CirculationForFileParameters(outputData);
				break;
			case whileType:
				circulationParameters = new CirculationWhileParameters(outputData);
				break;
			default:
				break;
		}
		circulationParameters.setCirculationType(type);
		return circulationParameters;
	}
}
