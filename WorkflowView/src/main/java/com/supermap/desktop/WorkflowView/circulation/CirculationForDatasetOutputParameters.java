package com.supermap.desktop.WorkflowView.circulation;

import com.supermap.desktop.process.parameter.events.OutputDataValueChangedEvent;
import com.supermap.desktop.process.parameter.events.OutputDataValueChangedListener;
import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.process.parameter.ipls.ParameterSingleDataset;

/**
 * Created by xie on 2017/11/3.
 */
public class CirculationForDatasetOutputParameters extends AbstractCirculationParameters {
	private ParameterSingleDataset dataset;

	public CirculationForDatasetOutputParameters(OutputData outputData) {
		this.outputData = outputData;
		initParameters();
	}

	private void initParameters() {
		this.dataset = new ParameterSingleDataset();
		this.dataset.setEnabled(false);
		addParameters(this.dataset);
		this.outputData.addOutputDataValueChangedListener(new OutputDataValueChangedListener() {
			@Override
			public void updateDataValue(OutputDataValueChangedEvent e) {
				dataset.setSelectedItem(e.getNewValue());
			}
		});
	}
}
