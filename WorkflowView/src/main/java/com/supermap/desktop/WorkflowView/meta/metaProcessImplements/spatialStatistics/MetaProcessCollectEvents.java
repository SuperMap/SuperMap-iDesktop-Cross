package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.spatialStatistics;

import com.supermap.analyst.spatialstatistics.StatisticsUtilities;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.properties.CommonProperties;
import com.supermap.desktop.utilities.DatasetUtilities;

/**
 * Created by lixiaoyao on 2017/10/17.
 */
public class MetaProcessCollectEvents extends MetaProcess {

	private final static String INPUT_DATA = CommonProperties.getString("String_GroupBox_SourceData");
	private final static String OUTPUT_DATA = "CollectEventsResult";

	private ParameterDatasourceConstrained sourceDatasource;
	private ParameterSingleDataset sourceDataset;

	private ParameterSaveDataset resultDataset;

	public MetaProcessCollectEvents() {
		setTitle(ProcessProperties.getString("String_CollectEventsTitle"));
		initParameters();
		initParameterConstraint();
		initParametersState();
	}

	private void initParameters() {
		this.sourceDatasource = new ParameterDatasourceConstrained();
		this.sourceDatasource.setDescribe(CommonProperties.getString("String_SourceDatasource"));
		this.sourceDataset = new ParameterSingleDataset(DatasetType.POINT, DatasetType.LINE, DatasetType.REGION);
		this.sourceDataset.setDescribe(CommonProperties.getString("String_Label_Dataset"));
		this.sourceDataset.setRequisite(true);
		this.resultDataset = new ParameterSaveDataset();

		ParameterCombine sourceData = new ParameterCombine();
		sourceData.setDescribe(CommonProperties.getString("String_GroupBox_SourceData"));
		sourceData.addParameters(this.sourceDatasource, this.sourceDataset);

		ParameterCombine targetData = new ParameterCombine();
		targetData.setDescribe(CommonProperties.getString("String_GroupBox_ResultData"));
		targetData.addParameters(this.resultDataset);

		this.parameters.setParameters(sourceData, targetData);
		this.parameters.addInputParameters(INPUT_DATA, DatasetTypes.SIMPLE_VECTOR, sourceData);
		this.parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_CollectEventsResult"), DatasetTypes.POINT, targetData);
	}

	private void initParametersState() {
		Dataset dataset = DatasetUtilities.getDefaultDataset(DatasetType.POINT, DatasetType.LINE, DatasetType.REGION);
		if (dataset != null) {
			this.sourceDatasource.setSelectedItem(dataset.getDatasource());
			this.sourceDataset.setSelectedItem(dataset);
			this.resultDataset.setResultDatasource(dataset.getDatasource());
		}
		this.resultDataset.setDefaultDatasetName("result_CollectEvents");
	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint constraintSource = new EqualDatasourceConstraint();
		constraintSource.constrained(sourceDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		constraintSource.constrained(sourceDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		DatasourceConstraint.getInstance().constrained(resultDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		try {
			String datasetName = resultDataset.getDatasetName();
			datasetName = resultDataset.getResultDatasource().getDatasets().getAvailableDatasetName(datasetName);
			DatasetVector src = null;
			if (this.getParameters().getInputs().getData(INPUT_DATA).getValue() != null) {
				src = (DatasetVector) this.getParameters().getInputs().getData(INPUT_DATA).getValue();
			} else {
				src = (DatasetVector) sourceDataset.getSelectedItem();
			}
			StatisticsUtilities.addSteppedListener(steppedListener);
			DatasetVector result = StatisticsUtilities.collectEvents(src, resultDataset.getResultDatasource(), datasetName);
			this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(result);
			isSuccessful = result != null;

		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		}
		return isSuccessful;
	}

	@Override
	public IParameters getParameters() {
		return parameters;
	}

	@Override
	public String getKey() {
		return MetaKeys.COLLECT_EVENTS;
	}
}
