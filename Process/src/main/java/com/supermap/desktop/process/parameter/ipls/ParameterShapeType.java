package com.supermap.desktop.process.parameter.ipls;

import com.supermap.analyst.spatialanalyst.NeighbourShape;
import com.supermap.data.Dataset;
import com.supermap.desktop.process.constraint.annotation.ParameterField;
import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.interfaces.AbstractParameter;
import com.supermap.desktop.process.parameter.interfaces.ISelectionParameter;

import java.beans.PropertyChangeEvent;

/**
 * Created By Chens on 2017/8/16 0016
 */
public class ParameterShapeType extends AbstractParameter implements ISelectionParameter {
	@ParameterField(name = "value")
	private NeighbourShape selectedItem;
	public static final String DATASET_FIELD_NAME = "Dataset";
	@ParameterField(name = DATASET_FIELD_NAME)
	private Dataset dataset;

	@Override
	public void setSelectedItem(Object item) {
		if (item instanceof NeighbourShape) {
			NeighbourShape oldValue = selectedItem;
			selectedItem = (NeighbourShape) item;
			firePropertyChangeListener(new PropertyChangeEvent(ParameterShapeType.this, "value", oldValue, selectedItem));
		}
	}

	@Override
	public Object getSelectedItem() {
		return selectedItem;
	}

	@Override
	public String getType() {
		return ParameterType.SHAPE_TYPE;
	}

	@Override
	public void dispose() {

	}

	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	@Override
	public String getDescription() {
		return "ShapeType";
	}
}
