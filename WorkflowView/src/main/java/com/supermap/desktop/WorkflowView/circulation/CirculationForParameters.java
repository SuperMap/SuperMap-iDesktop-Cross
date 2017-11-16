package com.supermap.desktop.WorkflowView.circulation;

import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.core.AbstractCirculationParameters;
import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.process.parameter.ipls.ParameterTextField;
import com.supermap.desktop.utilities.StringUtilities;

/**
 * Created by xie on 2017/10/27.
 */
public class CirculationForParameters extends AbstractCirculationParameters {
	private ParameterTextField startValue;
	private ParameterTextField endValue;
	private ParameterTextField iteratorValue;
	private int start;
	private int nowValue;
	private int iterator;
	private int end;

	public CirculationForParameters(OutputData outputData) {
		this.outputData = outputData;
		initParameters();
	}

	private void initParameters() {
		this.startValue = new ParameterTextField(ControlsProperties.getString("String_BatchAddColorTableMinValue"));
		this.startValue.setSelectedItem(0);
		this.endValue = new ParameterTextField(ControlsProperties.getString("String_BatchAddColorTableMaxValue"));
		this.endValue.setRequisite(true);
		this.iteratorValue = new ParameterTextField(ProcessProperties.getString("String_MeasureValue"));
		this.iteratorValue.setSelectedItem(1);
		nowValue = start = 0;
		end = 1;
		iterator = 0;
		addParameters(startValue, endValue, iteratorValue);
	}

	@Override
	public void reset() {
		count = 0;
		if (StringUtilities.isInteger(startValue.getSelectedItem()) || "0".equals(startValue.getSelectedItem())) {
			nowValue = start = Integer.valueOf(startValue.getSelectedItem());
			outputData.setValue(start);
		}
		if (StringUtilities.isInteger(endValue.getSelectedItem())) {
			end = Integer.valueOf(endValue.getSelectedItem());
		}
		if (StringUtilities.isInteger(iteratorValue.getSelectedItem())) {
			iterator = Integer.valueOf(iteratorValue.getSelectedItem());
		}
		infoList.clear();
		for (int i = nowValue, tempCount = 0; i <= end; i = start + iterator * tempCount) {
			infoList.add(i);
			tempCount++;
		}
	}
}
