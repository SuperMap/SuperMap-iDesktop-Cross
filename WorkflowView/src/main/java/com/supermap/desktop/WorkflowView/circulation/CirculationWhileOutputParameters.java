package com.supermap.desktop.WorkflowView.circulation;

import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.core.AbstractCirculationParameters;
import com.supermap.desktop.process.parameter.events.OutputDataValueChangedEvent;
import com.supermap.desktop.process.parameter.events.OutputDataValueChangedListener;
import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.process.parameter.ipls.ParameterTextField;

/**
 * Created by xie on 2017/11/29.
 */
public class CirculationWhileOutputParameters extends AbstractCirculationParameters {
	private ParameterTextField currentValue;
	private OutputData outputData;

	public CirculationWhileOutputParameters(OutputData outputData) {
		this.outputData = outputData;
		initParameters();
	}

	protected void initParameters() {
		this.currentValue = new ParameterTextField(ProcessProperties.getString("String_ConditionStr"));
		if (null != this.outputData.getValue()) {
			this.currentValue.setSelectedItem(this.outputData.getValue());
		}
		this.currentValue.setEnabled(false);
		addParameters(currentValue);
		outputData.addOutputDataValueChangedListener(new OutputDataValueChangedListener() {
			@Override
			public void updateDataValue(OutputDataValueChangedEvent e) {
				currentValue.setSelectedItem(e.getNewValue().toString());
			}
		});
	}

}

