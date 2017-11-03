package com.supermap.desktop.WorkflowView.circulation;

import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.parameter.ipls.ParameterFile;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * Created by xie on 2017/11/1.
 */
public class CirculationForObjectParameters extends AbstractCirculationParameters implements CirculationIterator {
	private ParameterFile selectedFile;
	private ArrayList<String> list = new ArrayList<>();
	private int count;

	public CirculationForObjectParameters() {
		initParameters();
		registEvents();
	}

	private void registEvents() {
		this.selectedFile.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {

			}
		});
	}

	private void initParameters() {
		selectedFile = new ParameterFile(ProcessProperties.getString("String_InputValue"));

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
	}

	@Override
	public void remove() {
		list.clear();
		list = null;
	}
}
