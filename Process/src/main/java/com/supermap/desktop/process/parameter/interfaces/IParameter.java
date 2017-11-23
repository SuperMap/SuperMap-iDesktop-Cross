package com.supermap.desktop.process.parameter.interfaces;

import com.supermap.desktop.process.parameter.events.FieldConstraintChangedListener;
import com.supermap.desktop.process.parameter.events.PanelPropertyChangedListener;
import com.supermap.desktop.process.parameter.events.ParameterValueLegalListener;
import com.supermap.desktop.process.parameter.events.UpdateValueListener;
import com.supermap.desktop.process.parameter.interfaces.datas.IRequired;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * Created by highsad on 2017/1/5.
 */
public interface IParameter extends IAbstractParameter, IRequired {
	String getType();

	boolean isEnabled();

	void setEnabled(boolean enabled);

	void setDescriptionVisible(boolean isDescripitionVisiable);

	boolean isDescriptionVisible();

	void addValueLegalListener(ParameterValueLegalListener parameterValueLegalListener);

	void removeValueLegalListener(ParameterValueLegalListener parameterValueLegalListener);

	boolean isValueLegal(String fieldName, Object value);

	Object isValueSelected(String fieldName, Object value);

	void addFieldConstraintChangedListener(FieldConstraintChangedListener fieldConstraintChangedListener);

	void removeFieldConstraintChangedListener(FieldConstraintChangedListener fieldConstraintChangedListener);

	void fireFieldConstraintChanged(String fieldName);

	void addUpdateValueListener(UpdateValueListener updateValueListener);

	void removeUpdateValueListener(UpdateValueListener updateValueListener);

	void fireUpdateValue(String fieldName);

	ArrayList<String> getFieldNameList(Class<AbstractParameter> clazz);

	/**
	 * 获取参数面板
	 * 对参数面板的修改需要设值到IParameter中，所以从这里获取比较好
	 *
	 * @return
	 */
	IParameterPanel getParameterPanel();

	void addPanelPropertyChangedListener(PanelPropertyChangedListener panelPropertyChangedListener);

	void removePanelPropertyChangedListener(PanelPropertyChangedListener panelPropertyChangedListener);

	void addPropertyListener(PropertyChangeListener propertyChangeListener);

	void removePropertyListener(PropertyChangeListener propertyChangeListener);

	void dispose();

	void setParameters(IParameters parameters);


	String getDescription();

	IParameters getParameters();

	boolean setFieldValue(String fieldName, Object value);

	Object getFieldValue(String fieldName) throws Exception;

	void setRequired(boolean value);

	boolean isRequired();

	boolean isReady();

	boolean isComplexParameter() ;

	void setComplexParameter(boolean complexParameter);
}
