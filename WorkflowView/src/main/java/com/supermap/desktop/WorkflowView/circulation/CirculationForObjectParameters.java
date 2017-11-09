package com.supermap.desktop.WorkflowView.circulation;

import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.process.parameters.ParameterPanels.Circulation.ParameterForObjectCirculation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by xie on 2017/11/1.
 */
public class CirculationForObjectParameters extends AbstractCirculationParameters {
	private ParameterForObjectCirculation parameterForObjectCirculation;

	public CirculationForObjectParameters(OutputData outputData) {
		this.outputData = outputData;
		initParameters();
		registEvents();
	}

	private void registEvents() {
		this.parameterForObjectCirculation.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				infoList.clear();
				if (null != parameterForObjectCirculation.getSelectedItem())
					infoList.addAll(parameterForObjectCirculation.getSelectedItem());
			}
		});
	}

	private void initParameters() {
		parameterForObjectCirculation = new ParameterForObjectCirculation();
		addParameters(parameterForObjectCirculation);
	}

	@Override
	public void reset() {
		count = 0;
		if (null != parameterForObjectCirculation.getSelectedItem() && parameterForObjectCirculation.getSelectedItem().size() > 0) {
			infoList.clear();
			infoList.addAll(parameterForObjectCirculation.getSelectedItem());
			outputData.setValue(infoList.get(count));
		}
	}
}
