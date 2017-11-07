package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.spatialStatistics;

import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasetConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.ParameterCombine;
import com.supermap.desktop.process.parameter.ipls.ParameterDatasource;
import com.supermap.desktop.process.parameter.ipls.ParameterSingleDataset;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;

/**
 * @author XiaJT
 */
public abstract class MetaProcessAnalyzingPatterns extends MetaProcess {
	private static final String INPUT_SOURCE_DATASET = CoreProperties.getString("String_GroupBox_SourceData");
	protected static final String INPUT_SPATIALWEIGHTMATRIXFILE = ProcessOutputResultProperties.getString("String_SpatialWeightMatrixFile");
	protected ParameterDatasource datasource = new ParameterDatasource();
	protected ParameterSingleDataset dataset = new ParameterSingleDataset(DatasetType.POINT, DatasetType.LINE, DatasetType.REGION);
	protected ParameterPatternsParameter parameterPatternsParameter = new ParameterPatternsParameter(getKey());


	public MetaProcessAnalyzingPatterns() {
		initParameters();
		initComponentState();
		initParameterConstraint();
		initHook();
	}

	protected void initHook() {

	}

	private void initParameters() {
		ParameterCombine parameterCombine = new ParameterCombine();
		parameterCombine.addParameters(datasource, dataset);
		parameters.setParameters(parameterCombine, parameterPatternsParameter);
		parameterCombine.setDescribe(CoreProperties.getString("String_ColumnHeader_SourceData"));
		parameters.addInputParameters(INPUT_SOURCE_DATASET, DatasetTypes.VECTOR, parameterCombine);
	}

	private void initParameterConstraint() {
		DatasourceConstraint.getInstance().constrained(datasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		DatasourceConstraint.getInstance().constrained(dataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(datasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(dataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasetConstraint equalDatasetConstraint = new EqualDatasetConstraint();
		equalDatasetConstraint.constrained(dataset, ParameterSingleDataset.DATASET_FIELD_NAME);
		equalDatasetConstraint.constrained(parameterPatternsParameter, ParameterPatternsParameter.DATASET_FIELD_NAME);
	}

	private void initComponentState() {
		DatasetVector datasetVector = DatasetUtilities.getDefaultDatasetVector();
		if (datasetVector != null) {
			datasource.setSelectedItem(datasetVector.getDatasource());
			dataset.setSelectedItem(datasetVector);
			parameterPatternsParameter.setCurrentDataset(datasetVector);
		}
	}


	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		DatasetVector datasetVector;

		if (parameters.getInputs().getData(INPUT_SOURCE_DATASET) != null &&
				parameters.getInputs().getData(INPUT_SOURCE_DATASET).getValue() instanceof DatasetVector) {
			datasetVector = (DatasetVector) parameters.getInputs().getData(INPUT_SOURCE_DATASET).getValue();
		} else {
			datasetVector = (DatasetVector) dataset.getSelectedItem();
		}
		isSuccessful = doWork(datasetVector);
		return isSuccessful;
	}

	protected abstract boolean doWork(DatasetVector datasetVector);

}
