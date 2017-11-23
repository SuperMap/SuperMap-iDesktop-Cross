package com.supermap.desktop.process.parameter.interfaces;

import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.annotation.ParameterField;
import com.supermap.desktop.process.parameter.events.*;
import com.supermap.desktop.process.parameter.interfaces.datas.IRequired;
import com.supermap.desktop.process.types.Type;
import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.event.EventListenerList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XiaJT
 */
public abstract class AbstractParameter implements IParameter, IRequired {

	private Type valueType;
	protected IParameterPanel panel;
	public static final String PROPERTY_VALE = "value";
	protected IParameters parameters;

	public boolean isEnabled = true;

	private EventListenerList listenerList = new EventListenerList();
	private List<UpdateValueListener> updateValueListeners = new ArrayList<>();
	private boolean isDescriptionVisible = true;
	// 添加是否为必填参数属性，默认为非必填
	private boolean isRequisite = false;
	// For custom Parameter, there are many components, some of which are available and some are unavailable.
	// The current implementation initialization is either available or unavailable.
	// Therefore, the new variable is used as a flag to determine whether the Panel need to be reset.
	private boolean isComplexParameter = false;

	@Override
	public void addPanelPropertyChangedListener(PanelPropertyChangedListener panelPropertyChangedListener) {
		listenerList.add(PanelPropertyChangedListener.class, panelPropertyChangedListener);
	}

	@Override
	public void removePanelPropertyChangedListener(PanelPropertyChangedListener panelPropertyChangedListener) {
		listenerList.remove(PanelPropertyChangedListener.class, panelPropertyChangedListener);
	}

	protected void firePanelPropertyChangedListener(PropertyChangeEvent propertyChangeEvent) {
		Object[] listenerList = this.listenerList.getListenerList();
		for (int i = listenerList.length - 2; i >= 0; i -= 2) {
			if (listenerList[i] == PanelPropertyChangedListener.class) {
				((PanelPropertyChangedListener) listenerList[i + 1]).propertyChanged(propertyChangeEvent);
			}
		}
	}

	@Override
	public void addPropertyListener(PropertyChangeListener propertyChangeListener) {
		listenerList.add(PropertyChangeListener.class, propertyChangeListener);
	}

	@Override
	public void removePropertyListener(PropertyChangeListener propertyChangeListener) {
		listenerList.remove(PropertyChangeListener.class, propertyChangeListener);
	}

	public void firePropertyChangeListener(PropertyChangeEvent propertyChangeEvent) {
		Object[] listenerList = this.listenerList.getListenerList();
		for (int i = listenerList.length - 2; i >= 0; i -= 2) {
			if (listenerList[i] == PropertyChangeListener.class) {
				((PropertyChangeListener) listenerList[i + 1]).propertyChange(propertyChangeEvent);
			}
		}
	}

	@Override
	public void addValueLegalListener(ParameterValueLegalListener parameterValueLegalListener) {
		listenerList.add(ParameterValueLegalListener.class, parameterValueLegalListener);
	}

	@Override
	public void removeValueLegalListener(ParameterValueLegalListener parameterValueLegalListener) {
		listenerList.remove(ParameterValueLegalListener.class, parameterValueLegalListener);
	}

	@Override
	public boolean isValueLegal(String fieldName, Object value) {
		ParameterValueLegalEvent event = new ParameterValueLegalEvent(this, fieldName, value);

		Object[] listenerList = this.listenerList.getListenerList();
		for (int i = listenerList.length - 2; i >= 0; i -= 2) {
			if (listenerList[i] == ParameterValueLegalListener.class) {
				if (!((ParameterValueLegalListener) listenerList[i + 1]).isValueLegal(event)) {
					return false;
				}
			}
		}
		return true;
	}

	public Object isValueSelected(String fieldName, Object value) {
		ParameterValueSelectedEvent valueSelectedEvent = new ParameterValueSelectedEvent(this, fieldName, value);

		Object[] listenerList = this.listenerList.getListenerList();
		for (int i = listenerList.length - 2; i >= 0; i -= 2) {
			if (listenerList[i] == ParameterValueLegalListener.class) {
				Object valueSelected = ((ParameterValueLegalListener) listenerList[i + 1]).isValueSelected(valueSelectedEvent);
				if (valueSelected != ParameterValueLegalListener.DO_NOT_CARE) {
					return valueSelected;
				}
			}
		}
		return ParameterValueLegalListener.DO_NOT_CARE;
	}

	@Override
	public void addFieldConstraintChangedListener(FieldConstraintChangedListener fieldConstraintChangedListener) {
		listenerList.add(FieldConstraintChangedListener.class, fieldConstraintChangedListener);
	}

	@Override
	public void removeFieldConstraintChangedListener(FieldConstraintChangedListener fieldConstraintChangedListener) {
		listenerList.remove(FieldConstraintChangedListener.class, fieldConstraintChangedListener);
	}

	@Override
	public void fireFieldConstraintChanged(String fieldName) {
		FieldConstraintChangedEvent fieldConstraintChangedEvent = new FieldConstraintChangedEvent(fieldName, this);

		Object[] listenerList = this.listenerList.getListenerList();
		for (int i = listenerList.length - 2; i >= 0; i -= 2) {
			if (listenerList[i] == FieldConstraintChangedListener.class) {
				((FieldConstraintChangedListener) listenerList[i + 1]).fieldConstraintChanged(fieldConstraintChangedEvent);
			}
		}
	}

	@Override
	public void addUpdateValueListener(UpdateValueListener updateValueListener) {
		if (!updateValueListeners.contains(updateValueListener)) {
			updateValueListeners.add(updateValueListener);
		}
	}

	@Override
	public void removeUpdateValueListener(UpdateValueListener updateValueListener) {
		updateValueListeners.remove(updateValueListener);
	}

	@Override
	public void fireUpdateValue(String fieldName) {
		for (UpdateValueListener updateValueListener : updateValueListeners) {
			updateValueListener.fireUpdateValue(new ParameterUpdateValueEvent(fieldName));
		}
	}

	@Override
	public ArrayList<String> getFieldNameList(Class<AbstractParameter> clazz) {
		ArrayList<String> nameList = new ArrayList<>();
		Field[] fields = clazz.getClass().getFields();
		for (Field field : fields) {
			ParameterField annotation = field.getAnnotation(ParameterField.class);
			if (annotation != null) {
				String name = annotation.name();
				nameList.add(name);
			}
		}
		return nameList;
	}


	@Override
	public IParameterPanel getParameterPanel() {
		if (panel == null) {
			panel = parameters.createPanel(this);
		}
		return panel;
	}

	public IParameters getParameters() {
		return parameters;
	}

	public void setParameters(IParameters parameters) {
		this.parameters = parameters;
	}


	@Override
	public void dispose() {

	}


	/**
	 * 直接通过反射设置字段值，不会触发属性改变事件
	 * Setting field values directly by reflection, does not trigger an event to change the property
	 *
	 * @param fieldName 字段的ParameterField注解里面的name值；The name value of the ParameterField annotation in the field
	 * @param value     值
	 * @return
	 */
	@Override
	public boolean setFieldValue(String fieldName, Object value) {
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			ParameterField annotation = field.getAnnotation(ParameterField.class);
			if (annotation != null && annotation.name().equals(fieldName)) {
				field.setAccessible(true);
				try {
					field.set(this, value);
				} catch (Exception e) {
					return false;
				} finally {
					field.setAccessible(false);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public Object getFieldValue(String fieldName) throws Exception {
		Field[] fields = this.getClass().getFields();
		for (Field field : fields) {
			ParameterField annotation = field.getAnnotation(ParameterField.class);
			if (annotation != null && annotation.name().equals(fieldName)) {
				return field.get(this);
			}
		}
		throw new Exception(ProcessProperties.getString("String_FieldDontExist"));
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (enabled == this.isEnabled()) {
			return;
		}
		boolean oldValue = this.isEnabled;
		this.isEnabled = enabled;
		firePanelPropertyChangedListener(new PropertyChangeEvent(this, PanelPropertyChangedListener.ENABLE, oldValue, enabled));
	}

	@Override
	public boolean isEnabled() {
		return this.isEnabled;
	}

	@Override
	public boolean isDescriptionVisible() {
		return isDescriptionVisible;
	}

	@Override
	public void setDescriptionVisible(boolean isDescriptionVisible) {
		if (this.isDescriptionVisible == isDescriptionVisible) {
			return;
		}
		boolean oldValue = this.isDescriptionVisible;
		this.isDescriptionVisible = isDescriptionVisible;
		firePanelPropertyChangedListener(new PropertyChangeEvent(this, PanelPropertyChangedListener.DESCRIPTION_VISIBLE, oldValue, isDescriptionVisible));
	}

	@Override
	public String getDescription() {
		return null;
	}

	/**
	 * 控件所呈参数是否为必填参数
	 * yuanR
	 *
	 * @return
	 */
	@Override
	public boolean isRequired() {
		return this.isRequisite;
	}

	/**
	 * 设置控件所呈参数是否为必填参数
	 * yuanR
	 *
	 * @param isRequisite
	 */
	@Override
	public void setRequired(boolean isRequisite) {
		this.isRequisite = isRequisite;
	}

	@Override
	public boolean isReady() {
		if (!(this instanceof ISelectionParameter)) {
			return true;
		}
		Object item = ((ISelectionParameter) this).getSelectedItem();
		if (item == null) {
			return false;
		}
		if (item instanceof String) {
			return !StringUtilities.isNullOrEmptyString(item);
		}
		return true;
	}

	@Override
	public boolean isComplexParameter() {
		return this.isComplexParameter;
	}

	@Override
	public void setComplexParameter(boolean complexParameter) {
		this.isComplexParameter = complexParameter;
	}

	public Type getValueType() {
		return valueType;
	}

	public void setValueType(Type valueType) {
		this.valueType = valueType;
	}
}
