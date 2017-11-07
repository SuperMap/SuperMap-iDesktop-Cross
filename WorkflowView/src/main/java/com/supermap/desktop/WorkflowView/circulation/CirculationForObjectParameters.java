package com.supermap.desktop.WorkflowView.circulation;

import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.process.parameters.ParameterPanels.Circulation.ParameterForObjectCirculation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * Created by xie on 2017/11/1.
 */
public class CirculationForObjectParameters extends AbstractCirculationParameters implements CirculationIterator {
	private ParameterForObjectCirculation parameterForObjectCirculation;
	private ArrayList<String> list = new ArrayList<>();
	private int count;
	private OutputData outputData;

	public CirculationForObjectParameters(OutputData outputData) {
		this.outputData = outputData;
		initParameters();
		registEvents();
	}

	private void registEvents() {
		this.parameterForObjectCirculation.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				list.clear();
				if (null != parameterForObjectCirculation.getSelectedItem())
					list.addAll(parameterForObjectCirculation.getSelectedItem());
			}
		});
	}

	private void initParameters() {
		parameterForObjectCirculation = new ParameterForObjectCirculation();
		addParameters(parameterForObjectCirculation);
	}

	@Override
	public boolean hasNext() {
		return count < list.size();
	}

	@Override
	public Object next() {
		String result = list.get(count);
		count++;
		return result;
	}

	@Override
	public void reset() {
		count = 0;
		if (null != parameterForObjectCirculation.getSelectedItem() && parameterForObjectCirculation.getSelectedItem().size() > 0) {
			list.clear();
			list.addAll(parameterForObjectCirculation.getSelectedItem());
			outputData.setValue(list.get(count));
		}
	}

	@Override
	public void remove() {
		list.clear();
		list = null;
	}
}
