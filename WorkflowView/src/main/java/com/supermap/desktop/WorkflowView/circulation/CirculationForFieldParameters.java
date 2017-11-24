package com.supermap.desktop.WorkflowView.circulation;

import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.FieldInfos;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.EqualDatasetTypeConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.core.AbstractCirculationParameters;
import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetTypeUtilities;
import com.supermap.desktop.utilities.DatasetUtilities;
import com.supermap.desktop.utilities.DatasourceUtilities;
import com.supermap.desktop.utilities.StringUtilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by xie on 2017/11/8.
 */
public class CirculationForFieldParameters extends AbstractCirculationParameters {
	private ParameterDatasourceConstrained datasource;
	private ParameterDatasetType datasetType;
	private ParameterSingleDataset dataset;
	private ParameterFieldComboBox fieldComboBox;
	private ParameterTextField wildcard;
	private Datasource currentDatasource;

	private PropertyChangeListener datasetChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (dataset.getSelectedDataset() instanceof DatasetVector)
				fieldComboBox.setFieldName((DatasetVector) dataset.getSelectedDataset());
		}
	};

	public CirculationForFieldParameters(OutputData outputData) {
		this.outputData = outputData;
		initParameters();
		initConstrained();
		registEvents();
	}

	private void registEvents() {
		this.dataset.addPropertyListener(datasetChangeListener);
	}

	private void initConstrained() {
		EqualDatasourceConstraint datasourceConstraint = new EqualDatasourceConstraint();
		datasourceConstraint.constrained(this.datasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		datasourceConstraint.constrained(this.dataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);
		EqualDatasetTypeConstraint datasetTypeConstraint = new EqualDatasetTypeConstraint();
		datasetTypeConstraint.constrained(this.datasetType, ParameterDatasetType.DATASETTYPE_FIELD_NAME);
		datasetTypeConstraint.constrained(this.dataset, ParameterSingleDataset.DATASETTYPES_FIELD_NAME);
	}

	private void initParameters() {
		this.datasource = new ParameterDatasourceConstrained();
		this.datasetType = new ParameterDatasetType();
		this.datasetType.setAllShown(true);
		this.datasetType.setDescribe(ProcessProperties.getString("String_DatasetType"));
		this.dataset = new ParameterSingleDataset();
		this.dataset.setDescribe(CoreProperties.getString(CoreProperties.Label_Dataset));
		this.dataset.setDatasetTypes(DatasetTypeUtilities.getDatasetTypeVector());
		this.fieldComboBox = new ParameterFieldComboBox();
		this.fieldComboBox.setDescribe(CoreProperties.getString("String_FieldValue"));
		this.wildcard = new ParameterTextField(ProcessProperties.getString("String_Wildcard"));

		this.currentDatasource = DatasourceUtilities.getDefaultResultDatasource();
		if (null != this.currentDatasource) {
			this.datasource.setSelectedItem(this.currentDatasource);
			this.dataset.setDatasource(this.currentDatasource);
		}
		DatasetVector datasetVector = DatasetUtilities.getDefaultDatasetVector();
		if (null != DatasetUtilities.getDefaultDatasetVector()) {
			this.dataset.setSelectedItem(datasetVector);
			this.fieldComboBox.setFieldName(datasetVector);
		}
		addParameters(this.datasource, this.datasetType, this.dataset, this.fieldComboBox, this.wildcard);
	}

	@Override
	public void reset() {
		this.count = 0;
		this.infoList.clear();
		if (null != this.fieldComboBox.getFieldName()) {
			String wildcardStr = wildcard.getSelectedItem();
			if (StringUtilities.isNullOrEmpty(wildcardStr)) {
				this.infoList.add(this.fieldComboBox.getFieldName());
			} else if (isMatching(this.fieldComboBox.getFieldName(), wildcardStr)) {
				this.infoList.add(this.fieldComboBox.getFieldName());
			}
			if (null != this.dataset.getSelectedDataset() && this.dataset.getSelectedDataset() instanceof DatasetVector) {
				FieldInfos tempFieldInfo = ((DatasetVector) this.dataset.getSelectedDataset()).getFieldInfos();
				for (int i = 0, size = tempFieldInfo.getCount(); i < size; i++) {
					if (!fieldComboBox.getFieldName().equalsIgnoreCase(tempFieldInfo.get(i).getName()) && !tempFieldInfo.get(i).isSystemField()) {
						if (StringUtilities.isNullOrEmpty(wildcardStr)) {
							infoList.add(tempFieldInfo.get(i).getName());
						} else if (isMatching(tempFieldInfo.get(i).getName(), wildcardStr)) {
							infoList.add(tempFieldInfo.get(i).getName());
						}
					}
				}
			}
			if (this.infoList.size()>0) {
				outputData.setValue(this.infoList.get(0));
			}
		}
	}

}
