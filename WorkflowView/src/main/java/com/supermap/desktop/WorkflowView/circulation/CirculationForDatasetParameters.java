package com.supermap.desktop.WorkflowView.circulation;

import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.Datasets;
import com.supermap.data.Datasource;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.EqualDatasetTypeConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.process.parameter.ipls.ParameterDatasetType;
import com.supermap.desktop.process.parameter.ipls.ParameterDatasource;
import com.supermap.desktop.process.parameter.ipls.ParameterDatasourceConstrained;
import com.supermap.desktop.process.parameter.ipls.ParameterSingleDataset;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasourceUtilities;

import java.util.ArrayList;

/**
 * Created by xie on 2017/11/2.
 */
public class CirculationForDatasetParameters extends AbstractCirculationParameters implements CirculationIterator {
	private ParameterDatasourceConstrained datasourceConstrained;
	private ParameterSingleDataset dataset;
	private ParameterDatasetType datasetType;
	private ArrayList<Dataset> datasetList;
	private Datasource currentDatasource;
	private int count;
	private OutputData outputData;

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
		this.count = 0;
		this.datasetList = new ArrayList<>();
		this.datasourceConstrained = new ParameterDatasourceConstrained();
		this.currentDatasource = DatasourceUtilities.getDefaultDatasource();
		this.datasetType = new ParameterDatasetType();
		this.datasetType.setDescribe(ProcessProperties.getString("String_DatasetType"));
		this.datasetType.setAllShown(true);
		this.datasourceConstrained.setDescribe(CoreProperties.getString(CoreProperties.Label_Datasource));
		this.dataset = new ParameterSingleDataset();
		this.dataset.setDescribe(CoreProperties.getString(CoreProperties.Label_Dataset));
		if (null != currentDatasource) {
			this.datasourceConstrained.setSelectedItem(currentDatasource);
			this.dataset.setDatasource(currentDatasource);
		}
		addParameters(this.datasourceConstrained, this.datasetType, this.dataset);
	}


	@Override
	public void reset() {
		this.count = 0;
		this.datasetList.clear();
		if (null != this.datasourceConstrained.getSelectedItem()) {
			Datasource datasource = this.datasourceConstrained.getSelectedItem();
			DatasetType[] datasetTypes = (DatasetType[]) this.datasetType.getSelectedItem();
			Datasets datasets = datasource.getDatasets();
			this.datasetList.add(this.dataset.getSelectedDataset());
			for (int i = 0, size = datasets.getCount(); i < size; i++) {
				for (int j = 0, length = datasetTypes.length; j < length; j++) {
					if (datasets.get(i).getType().equals(datasetTypes[j])
							&& datasets.get(i) != this.dataset.getSelectedDataset()) {
						this.datasetList.add(datasets.get(i));
					}
				}
			}
		}
		this.outputData.setValue(dataset.getSelectedDataset());
	}

	@Override
	public boolean hasNext() {
		return count < datasetList.size();
	}

	@Override
	public Dataset next() {
		Dataset result = datasetList.get(count);
		count++;
		return result;
	}

	@Override
	public void remove() {
		datasetList.clear();
		datasetList = null;
	}
}
