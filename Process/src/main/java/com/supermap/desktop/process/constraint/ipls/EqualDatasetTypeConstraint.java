package com.supermap.desktop.process.constraint.ipls;

import com.supermap.data.DatasetType;
import com.supermap.desktop.process.parameter.events.ParameterValueLegalListener;
import com.supermap.desktop.process.parameter.events.ParameterValueSelectedEvent;
import com.supermap.desktop.process.parameter.interfaces.IParameter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by xie on 2017/11/2.
 */
public class EqualDatasetTypeConstraint extends DefaultConstraint {
	private DatasetType[] datasetTypes;

	@Override
	public Object isValueSelected(ParameterValueSelectedEvent event) {
		for (ParameterNode parameterNode : parameterNodes) {
			if (parameterNode.getParameter() == event.getParameter() && parameterNode.getName().equals(event.getFieldName())) {
				return datasetTypes;
			}
		}
		return ParameterValueLegalListener.DO_NOT_CARE;
	}

	@Override
	protected void constrainedHook(final IParameter parameter, final String name) {
		parameter.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(name)) {
					datasetTypes = (DatasetType[]) evt.getNewValue();
					fireConstraintChanged(parameter);
				}
			}
		});
	}

	private void fireConstraintChanged(IParameter parameter) {
		for (ParameterNode parameterNode : parameterNodes) {
			if (parameterNode.getParameter() != parameter) {
				parameterNode.getParameter().fireFieldConstraintChanged(parameterNode.getName());
			}
		}
	}
}
