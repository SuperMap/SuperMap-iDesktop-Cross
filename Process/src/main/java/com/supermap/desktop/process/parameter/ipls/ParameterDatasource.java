package com.supermap.desktop.process.parameter.ipls;

import com.supermap.data.Datasource;
import com.supermap.desktop.process.constraint.annotation.ParameterField;
import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.events.ParameterValueLegalEvent;
import com.supermap.desktop.process.parameter.events.ParameterValueLegalListener;
import com.supermap.desktop.process.parameter.events.ParameterValueSelectedEvent;
import com.supermap.desktop.process.parameter.interfaces.AbstractParameter;
import com.supermap.desktop.process.parameter.interfaces.ISelectionParameter;
import com.supermap.desktop.properties.CoreProperties;

import java.beans.PropertyChangeEvent;

import static com.supermap.desktop.process.parameter.events.ParameterValueLegalListener.DO_NOT_CARE;

/**
 * @author XiaJT
 */
public class ParameterDatasource extends AbstractParameter implements ISelectionParameter {

	public static final String DATASOURCE_FIELD_NAME = "DATASOURCE_FIELD_NAME";

	@ParameterField(name = DATASOURCE_FIELD_NAME)
	private Datasource datasource;
	private String describe = CoreProperties.getString(CoreProperties.Label_Datasource);
	// 默认需要只读数据-yuanR2017.9.12
	private boolean isReadOnlyNeeded = true;
//	private boolean isDatasourceRequisite = true;
//
//	public void setDatasourceRequisite(boolean isRequisite) {
//		this.isDatasourceRequisite = isRequisite;
//	}
//	/**
//	 * 默认数据源为必要参数
//	 * yuanR
//	 *
//	 * @return
//	 */
//	@Override
//	public boolean isRequisite() {
//		return this.isDatasourceRequisite;
//	}

	public ParameterDatasource() {
		this.addValueLegalListener(new ParameterValueLegalListener() {
			@Override
			public boolean isValueLegal(ParameterValueLegalEvent event) {
				if (event.getFieldName().equals(ParameterDatasource.DATASOURCE_FIELD_NAME)) {
					Object parameterValue = event.getParameterValue();
					if (parameterValue instanceof Datasource) {
						return isDatasourceValueLegal(((Datasource) parameterValue));
					}
				}
				return false;
			}

			@Override
			public Object isValueSelected(ParameterValueSelectedEvent event) {
				if (event.getFieldName().equals(DATASOURCE_FIELD_NAME)) {
					if (event.getParameterValue() == null || event.getParameterValue() instanceof Datasource) {
						return isDatasourceSelected(((Datasource) event.getParameterValue()));
					}
				}
				return DO_NOT_CARE;
			}
		});
	}

	public boolean isReadOnlyNeeded() {
		return this.isReadOnlyNeeded;
	}

	public void setReadOnlyNeeded(boolean readOnlyNeeded) {
		this.isReadOnlyNeeded = readOnlyNeeded;
	}

	protected Object isDatasourceSelected(Datasource parameterValue) {
		return DO_NOT_CARE;
	}

	protected boolean isDatasourceValueLegal(Datasource parameterValue) {
		return true;
	}

	@Override
	public String getType() {
		return ParameterType.DATASOURCE;
	}

	@Override
	public void setSelectedItem(Object value) {
		if (value instanceof Datasource) {
			Datasource oldValue = this.datasource;
			this.datasource = (Datasource) value;
			firePropertyChangeListener(new PropertyChangeEvent(this, this.DATASOURCE_FIELD_NAME, oldValue, this.datasource));
		}
	}


	@Override
	public Datasource getSelectedItem() {
		return this.datasource;
	}

	public String getDescribe() {
		return this.describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	@Override
	public void dispose() {

	}
}
