package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.hydrologyAnalyst;

import com.supermap.analyst.terrainanalyst.HydrologyAnalyst;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetGrid;
import com.supermap.data.DatasetType;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.ipls.ParameterCombine;
import com.supermap.desktop.process.parameter.ipls.ParameterDatasource;
import com.supermap.desktop.process.parameter.ipls.ParameterDatasourceConstrained;
import com.supermap.desktop.process.parameter.ipls.ParameterSingleDataset;
import com.supermap.desktop.process.types.DatasetTypes;
import com.supermap.desktop.utilities.DatasetUtilities;

/**
 * Created By Chens on 2017/8/29 0029
 */
public class MetaProcessFlowAccumulation extends MetaProcessHydrology {
	protected static final String WEIGHT_DATA = ProcessProperties.getString("String_GroupBox_WeightData");

	private ParameterDatasourceConstrained weightDatasource;
	private ParameterSingleDataset weightDataset;

	public MetaProcessFlowAccumulation() {
		setTitle(ProcessProperties.getString("String_Title_FlowAccumulation"));
	}

	@Override
	protected void initField() {
		INPUT_DATA = ProcessProperties.getString("String_GroupBox_FlowDirectionData");
		OUTPUT_DATA = "FlowAccumulationResult";
	}

	@Override
	protected void initParaComponent() {
		weightDatasource = new ParameterDatasourceConstrained();
		weightDataset = new ParameterSingleDataset(DatasetType.GRID);

		EqualDatasourceConstraint constraintSource1 = new EqualDatasourceConstraint();
		constraintSource1.constrained(weightDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		constraintSource1.constrained(weightDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		DatasetGrid datasetGrid = DatasetUtilities.getDefaultDatasetGrid();
		if (datasetGrid != null) {
			weightDatasource.setSelectedItem(datasetGrid.getDatasource());
			weightDataset.setDatasource(datasetGrid.getDatasource());
		}
		weightDataset.setShowNullValue(true);

		ParameterCombine weightCombine = new ParameterCombine();
		weightCombine.setDescribe(WEIGHT_DATA);
		weightCombine.addParameters(weightDatasource, weightDataset);

		parameters.setParameters(sourceCombine, weightCombine, resultCombine);
		parameters.addInputParameters(WEIGHT_DATA, DatasetTypes.GRID, weightCombine);
	}

	@Override
	protected String getResultName() {
		return "result_flowAccumulation";
	}

	@Override
	protected String getOutputText() {
		return ProcessOutputResultProperties.getString("String_Result_FlowAccumulation");
	}

	@Override
	protected Dataset doWork(DatasetGrid src) {
		DatasetGrid srcWeight = null;
		if (parameters.getInputs().getData(WEIGHT_DATA).getValue() != null) {
			srcWeight = (DatasetGrid) parameters.getInputs().getData(WEIGHT_DATA).getValue();
		} else if (weightDataset.getSelectedItem() != null) {
			srcWeight = (DatasetGrid) weightDataset.getSelectedItem();
		}
		DatasetGrid result = HydrologyAnalyst.flowAccumulation(src, srcWeight, resultDataset.getResultDatasource(),
				resultDataset.getResultDatasource().getDatasets().getAvailableDatasetName(resultDataset.getSelectedItem().toString()));
		return result;
	}

	@Override
	public IParameters getParameters() {
		return super.getParameters();
	}

	@Override
	public String getKey() {
		return MetaKeys.FLOW_ACCUMULATION;
	}
}
