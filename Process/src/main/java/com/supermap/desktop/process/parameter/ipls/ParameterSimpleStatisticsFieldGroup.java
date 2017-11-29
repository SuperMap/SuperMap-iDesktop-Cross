package com.supermap.desktop.process.parameter.ipls;

import com.supermap.analyst.spatialanalyst.StatisticsType;
import com.supermap.data.DatasetVector;
import com.supermap.data.FieldInfo;
import com.supermap.data.FieldType;
import com.supermap.desktop.process.constraint.annotation.ParameterField;
import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.interfaces.AbstractParameter;
import com.supermap.desktop.process.parameter.interfaces.IMultiSelectionParameter;

import java.beans.PropertyChangeEvent;

/**
 * Created by lixiaoyao on 2017/8/8.
 */
public class ParameterSimpleStatisticsFieldGroup extends AbstractParameter implements IMultiSelectionParameter {
	public static final String FIELD_DATASET="dataset";

	@ParameterField(name = FIELD_DATASET)
	private DatasetVector dataset;
	private String describe;
	private FieldType[] fieldType;
	private FieldInfo[] selectedFields;
	private StatisticsType[] selectedStatisticsType;

	public ParameterSimpleStatisticsFieldGroup(){

	}

	public ParameterSimpleStatisticsFieldGroup(String describe){
		this.describe=describe;
	}

	@Override
	public void setSelectedItem(Object item) {

	}

	@Override
	public Object getSelectedItem() {
		return getSelectedFields();
	}

	@Override
	public String getType() {
		return ParameterType.SIMPLE_STATISTICS_FIELD;
	}

	@Override
	public String getDescription() {
		return describe;
	}

	public DatasetVector getDataset() {
		return dataset;
	}

	public void setDataset(DatasetVector dataset) {
		DatasetVector oldValue = this.dataset;
		this.dataset = dataset;
		firePropertyChangeListener(new PropertyChangeEvent(this, FIELD_DATASET, oldValue, dataset));
	}

	public void setFieldType(FieldType[] fieldType) {
		this.fieldType = fieldType;
	}

	public FieldType[] getFieldType() {
		return fieldType;
	}

	public void setSelectedFields(FieldInfo[] fieldInfos) {
		this.selectedFields = fieldInfos;
	}

	public FieldInfo[] getSelectedFields() {
		return selectedFields;
	}

	public StatisticsType[] getSelectedStatisticsType() {
		return selectedStatisticsType;
	}

	public void setSelectedStatisticsType(StatisticsType[] selectedStatisticsType) {
		this.selectedStatisticsType = selectedStatisticsType;
	}
}
