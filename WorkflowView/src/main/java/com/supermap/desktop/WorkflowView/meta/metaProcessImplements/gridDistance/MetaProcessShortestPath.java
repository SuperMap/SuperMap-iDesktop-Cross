package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.gridDistance;

import com.supermap.analyst.spatialanalyst.ComputeType;
import com.supermap.analyst.spatialanalyst.DistanceAnalyst;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetGrid;
import com.supermap.data.DatasetType;
import com.supermap.data.Datasets;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.metaProcessImplements.MetaProcessGridAnalyst;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.properties.CommonProperties;
import com.supermap.desktop.utilities.DatasetUtilities;

/**
 * 计算最短距离
 * Created by lixiaoyao on 2017/8/7.
 */
public class MetaProcessShortestPath extends MetaProcessGridAnalyst {
	private final static String INPUT_DATA = CommonProperties.getString("String_GroupBox_SourceData");
	private final static String DISTANCE_DATA = CommonProperties.getString("String_GroupBox_DistanceData");
	private final static String DIRECTION_DATA = CommonProperties.getString("String_GroupBox_DirectionData");
	private final static String OUTPUT_DATA = "ShortestPathResult";

	private ParameterDatasourceConstrained sourceDatasource;
	private ParameterSingleDataset sourceDataset;
	private ParameterDatasourceConstrained distanceDatasource;
	private ParameterSingleDataset distanceDataset;
	private ParameterDatasourceConstrained directionDatasource;
	private ParameterSingleDataset directionDataset;
	private ParameterRadioButton parameterRadioButton;
	private ParameterDataNode parameterDataNodeCell;
	private ParameterDataNode parameterDataNodeAll;
	private ParameterDataNode parameterDataNodeZone;
	private ParameterSaveDataset resultDataset;


	public MetaProcessShortestPath() {
		setTitle(ProcessProperties.getString("String_Calculator_ShortestPath"));
		initParameters();
		initParametersState();
		initParameterConstraint();
	}

	private void initParameters() {
		initEnvironment();
		this.sourceDatasource = new ParameterDatasourceConstrained();
		this.sourceDatasource.setDescribe(CommonProperties.getString("String_SourceDatasource"));
		this.sourceDataset = new ParameterSingleDataset(DatasetType.GRID, DatasetType.POINT, DatasetType.LINE, DatasetType.REGION);
		this.sourceDataset.setDescribe(CommonProperties.getString("String_Label_Dataset"));
		this.distanceDatasource = new ParameterDatasourceConstrained();
		this.distanceDatasource.setDescribe(CommonProperties.getString("String_SourceDatasource"));
		this.distanceDataset = new ParameterSingleDataset(DatasetType.GRID);
		this.distanceDataset.setDescribe(CommonProperties.getString("String_Label_Dataset"));
		this.directionDatasource = new ParameterDatasourceConstrained();
		this.directionDatasource.setDescribe(CommonProperties.getString("String_SourceDatasource"));
		this.directionDataset = new ParameterSingleDataset(DatasetType.GRID);
		this.directionDataset.setDescribe(CommonProperties.getString("String_Label_Dataset"));
		this.parameterRadioButton = new ParameterRadioButton();
		this.parameterDataNodeCell = new ParameterDataNode(ProcessProperties.getString("String_ComputeType_Cell"), null);
		this.parameterDataNodeAll = new ParameterDataNode(ProcessProperties.getString("String_ComputeType_All"), null);
		this.parameterDataNodeZone = new ParameterDataNode(ProcessProperties.getString("String_ComputeType_Zone"), null);
		this.parameterRadioButton.setItems(new ParameterDataNode[]{parameterDataNodeCell, parameterDataNodeAll, parameterDataNodeZone});
		this.resultDataset = new ParameterSaveDataset();

		ParameterCombine sourceData = new ParameterCombine();
		sourceData.setDescribe(CommonProperties.getString("String_GroupBox_TargetData"));
		sourceData.addParameters(this.sourceDatasource, this.sourceDataset);
		ParameterCombine distanceData = new ParameterCombine();
		distanceData.setDescribe(ProcessProperties.getString("String_GroupBox_DistanceData"));
		distanceData.addParameters(this.distanceDatasource, this.distanceDataset);
		ParameterCombine directionData = new ParameterCombine();
		directionData.setDescribe(ProcessProperties.getString("String_GroupBox_DirectionData"));
		directionData.addParameters(this.directionDatasource, this.directionDataset);
		ParameterCombine computeType = new ParameterCombine();
		computeType.setDescribe(ProcessProperties.getString("String_ComputeType"));
		computeType.addParameters(this.parameterRadioButton);
		ParameterCombine targetData = new ParameterCombine();
		targetData.setDescribe(CommonProperties.getString("String_GroupBox_ResultData"));
		targetData.addParameters(this.resultDataset);

		this.parameters.setParameters(sourceData, distanceData, directionData, computeType, targetData);
		this.parameters.addInputParameters(INPUT_DATA, DatasetTypes.SIMPLE_VECTOR_AND_GRID, sourceData);
		this.parameters.addInputParameters(DISTANCE_DATA, DatasetTypes.GRID, distanceData);
		this.parameters.addInputParameters(DIRECTION_DATA, DatasetTypes.GRID, directionData);
		this.parameters.addOutputParameters(OUTPUT_DATA,
				ProcessOutputResultProperties.getString("String_ShortestPathGridResult"),
				DatasetTypes.GRID, targetData);
	}

	private void initEnvironment() {
		parameterGridAnalystSetting.setClipBoundsEnable(true);
	}

	private void initParametersState() {
		this.resultDataset.setDefaultDatasetName("result_shortestPath");
		Dataset dataset = DatasetUtilities.getDefaultDataset(DatasetType.POINT, DatasetType.LINE, DatasetType.REGION, DatasetType.GRID);
		if (dataset != null) {
			this.sourceDatasource.setSelectedItem(dataset.getDatasource());
			this.sourceDataset.setSelectedItem(dataset);
			this.resultDataset.setResultDatasource(dataset.getDatasource());
			this.distanceDatasource.setSelectedItem(dataset.getDatasource());
			this.directionDatasource.setSelectedItem(dataset.getDatasource());
			this.distanceDataset.setDatasource(dataset.getDatasource());
			this.directionDataset.setDatasource(dataset.getDatasource());
			this.distanceDataset.setSelectedItem(getDefaultDataset(dataset, new String[]{"Distance", "distance"}));
			this.directionDataset.setSelectedItem(getDefaultDataset(dataset, new String[]{"Direction", "direction"}));
//			if (dataset.getType() == DatasetType.GRID) {
//				this.distanceDataset.setSelectedItem(dataset);
//				this.directionDataset.setSelectedItem(dataset);
//			}
		}
		this.parameterRadioButton.setSelectedItem(parameterDataNodeCell);
	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(this.sourceDatasource, ParameterDatasourceConstrained.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(this.sourceDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		DatasourceConstraint.getInstance().constrained(this.resultDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);

		EqualDatasourceConstraint distanceConstraint = new EqualDatasourceConstraint();
		distanceConstraint.constrained(this.distanceDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		distanceConstraint.constrained(this.distanceDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasourceConstraint directionConstraint = new EqualDatasourceConstraint();
		directionConstraint.constrained(this.directionDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		directionConstraint.constrained(this.directionDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);
	}

	private Dataset getDefaultDataset(Dataset srcDataset, String[] characterKeys) {
		Dataset dataset = srcDataset;
		try {
			Datasets datasets = srcDataset.getDatasource().getDatasets();
			for (int i = 0; i < datasets.getCount(); i++) {
				for (int j = 0; j < characterKeys.length; j++) {
					if (datasets.get(i).getName().indexOf(characterKeys[j]) != -1) {
						dataset = datasets.get(i);
						break;
					}
				}
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
		return dataset;
	}

	@Override
	public boolean childExecute() {
		boolean isSuccessful = false;
		try {

			String datasetName = resultDataset.getDatasetName();
			datasetName = resultDataset.getResultDatasource().getDatasets().getAvailableDatasetName(datasetName);
			Dataset src = null;
			if (this.getParameters().getInputs().getData(INPUT_DATA).getValue() != null) {
				src = (Dataset) this.getParameters().getInputs().getData(INPUT_DATA).getValue();
			} else {
				src = sourceDataset.getSelectedItem();
			}
			DatasetGrid distance = null;
			if (this.getParameters().getInputs().getData(DISTANCE_DATA).getValue() != null) {
				distance = (DatasetGrid) this.getParameters().getInputs().getData(DISTANCE_DATA).getValue();
			} else {
				distance = (DatasetGrid) distanceDataset.getSelectedItem();
			}
			DatasetGrid direction = null;
			if (this.getParameters().getInputs().getData(DIRECTION_DATA).getValue() != null) {
				direction = (DatasetGrid) this.getParameters().getInputs().getData(DIRECTION_DATA).getValue();
			} else {
				direction = (DatasetGrid) directionDataset.getSelectedItem();
			}
			ComputeType computeType = null;
			if (parameterRadioButton.getSelectedItem().equals(parameterDataNodeCell)) {
				computeType = ComputeType.CELL;
			} else if (parameterRadioButton.getSelectedItem().equals(parameterDataNodeAll)) {
				computeType = ComputeType.ALL;
			} else if (parameterRadioButton.getSelectedItem().equals(parameterDataNodeZone)) {
				computeType = ComputeType.ZONE;
			}

			DistanceAnalyst.addSteppedListener(steppedListener);
			DatasetGrid result = DistanceAnalyst.costPath(src, distance, direction, computeType, this.resultDataset.getResultDatasource(), datasetName);
			this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(result);
			isSuccessful = result != null;

		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			DistanceAnalyst.removeSteppedListener(steppedListener);
		}
		return isSuccessful;
	}

	@Override
	public IParameters getParameters() {
		return parameters;
	}

	@Override
	public String getKey() {
		return MetaKeys.SHORTEST_PATH;
	}
}
