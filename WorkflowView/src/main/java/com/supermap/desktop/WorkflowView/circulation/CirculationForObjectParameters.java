package com.supermap.desktop.WorkflowView.circulation;

import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.core.AbstractCirculationParameters;
import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.process.parameter.ipls.ParameterFile;
import com.supermap.desktop.process.parameters.ParameterPanels.Circulation.ParameterForObjectCirculation;
import com.supermap.desktop.utilities.StringUtilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by xie on 2017/11/1.
 */
public class CirculationForObjectParameters extends AbstractCirculationParameters {
	private ParameterFile parameterFile;
	private ParameterForObjectCirculation parameterForObjectCirculation;

	public CirculationForObjectParameters(OutputData outputData) {
		this.outputData = outputData;
		initParameters();
		registEvents();
	}

	private void registEvents() {
		this.parameterFile.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(parameterFile.FILE_COMMITTED)
						&& !StringUtilities.isNullOrEmpty(evt.getNewValue().toString())) {
					parameterForObjectCirculation.addRow(evt.getNewValue());
				}
			}
		});
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
		this.parameterForObjectCirculation = new ParameterForObjectCirculation();
		this.parameterForObjectCirculation.setShowAddButton(false);
		this.parameterFile = new ParameterFile(ProcessProperties.getString("String_InputValue"));
		addParameters(parameterFile, parameterForObjectCirculation);
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
