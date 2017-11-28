package com.supermap.desktop.WorkflowView.circulation;

import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.core.AbstractCirculationParameters;
import com.supermap.desktop.process.parameter.events.OutputDataValueChangedEvent;
import com.supermap.desktop.process.parameter.events.OutputDataValueChangedListener;
import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.process.parameter.ipls.ParameterDatasourceConstrained;

/**
 * Created by xie on 2017/11/9.
 */
public class CirculationForDatasourceOutputParameters extends AbstractCirculationParameters {
	private ParameterDatasourceConstrained currentValue;

	public CirculationForDatasourceOutputParameters(OutputData outputData) {
		this.outputData = outputData;
		initParameters();
	}

	private void initParameters() {
		this.currentValue = new ParameterDatasourceConstrained();
		this.currentValue.setDescribe(ProcessProperties.getString("String_CurrentValueStr"));
		if (null != this.outputData.getValue()) {
			this.currentValue.setSelectedItem(this.outputData.getValue());
		}
		this.currentValue.setEnabled(false);
		addParameters(currentValue);
		outputData.addOutputDataValueChangedListener(new OutputDataValueChangedListener() {
			@Override
			public void updateDataValue(OutputDataValueChangedEvent e) {
				currentValue.setSelectedItem(e.getNewValue());
			}
		});
	}

}
