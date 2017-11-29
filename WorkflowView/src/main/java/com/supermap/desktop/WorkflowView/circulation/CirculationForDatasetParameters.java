package com.supermap.desktop.WorkflowView.circulation;

import com.supermap.data.DatasetType;
import com.supermap.data.Datasets;
import com.supermap.data.Datasource;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.EqualDatasetTypeConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.core.AbstractCirculationParameters;
import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasourceUtilities;
import com.supermap.desktop.utilities.StringUtilities;

/**
 * Created by xie on 2017/11/2.
 */
public class CirculationForDatasetParameters extends AbstractCirculationParameters {
	private ParameterDatasourceConstrained datasourceConstrained;
	private ParameterSingleDataset dataset;
	private ParameterDatasetType datasetType;
	private ParameterTextField wildcard;
	private Datasource currentDatasource;

	public CirculationForDatasetParameters(OutputData outputData) {
		this.outputData = outputData;
		initParameters();
		initConstrained();
	}

	private void initConstrained() {
		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(this.datasourceConstrained, ParameterDatasource.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(this.dataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);
		EqualDatasetTypeConstraint equalDatasetTypeConstraint = new EqualDatasetTypeConstraint();
		equalDatasetTypeConstraint.constrained(this.datasetType, ParameterDatasetType.DATASETTYPE_FIELD_NAME);
		equalDatasetTypeConstraint.constrained(this.dataset, ParameterSingleDataset.DATASETTYPES_FIELD_NAME);
	}

	private void initParameters() {
		this.datasourceConstrained = new ParameterDatasourceConstrained();
		this.currentDatasource = DatasourceUtilities.getDefaultDatasource();
		this.datasetType = new ParameterDatasetType();
		this.datasetType.setDescribe(ProcessProperties.getString("string_label_lblDatasetType"));
		this.datasetType.setAllShown(true);
		this.datasourceConstrained.setDescribe(CoreProperties.getString(CoreProperties.Label_Datasource));
		this.dataset = new ParameterSingleDataset();
		this.dataset.setDescribe(CoreProperties.getString(CoreProperties.Label_Dataset));
		this.wildcard = new ParameterTextField(ProcessProperties.getString("String_Wildcard"));
		if (null != currentDatasource) {
			this.datasourceConstrained.setSelectedItem(currentDatasource);
			this.dataset.setDatasource(currentDatasource);
		}
		addParameters(this.datasourceConstrained, this.datasetType, this.dataset, this.wildcard);
	}


	@Override
	public void reset() {
		this.infoList.clear();
		if (null != this.datasourceConstrained.getSelectedItem()) {
			Datasource datasource = this.datasourceConstrained.getSelectedItem();
			DatasetType[] datasetTypes = (DatasetType[]) this.datasetType.getSelectedItem();
			Datasets datasets = datasource.getDatasets();
			//首先添加选中的数据集
			String wildcardStr = wildcard.getSelectedItem();
			if (StringUtilities.isNullOrEmpty(wildcardStr) && null != this.dataset.getSelectedDataset()) {
				this.infoList.add(this.dataset.getSelectedDataset());
			} else if (!StringUtilities.isNullOrEmpty(wildcardStr) && null != this.dataset.getSelectedDataset()
					&& isMatching(this.dataset.getSelectedDataset().getName(), wildcardStr)) {
				this.infoList.add(this.dataset.getSelectedDataset());
			}
			//添加未选中的数据集
			for (int i = 0, size = datasets.getCount(); i < size; i++) {
				for (int j = 0, length = datasetTypes.length; j < length; j++) {
					if (datasets.get(i).getType().equals(datasetTypes[j])
							&& datasets.get(i) != this.dataset.getSelectedDataset()) {
						if (StringUtilities.isNullOrEmpty(wildcardStr)) {
							this.infoList.add(datasets.get(i));
						} else if (isMatching(datasets.get(i).getName(), wildcardStr)) {
							this.infoList.add(datasets.get(i));
						}
					}
				}
			}
		}
		if (this.infoList.size() > 0) {
			this.outputData.setValue(this.infoList.get(0));
		}
	}
}
