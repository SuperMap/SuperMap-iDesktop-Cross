package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.gridStatisticsAnalyst;

import com.supermap.analyst.spatialanalyst.*;
import com.supermap.data.DatasetGrid;
import com.supermap.data.DatasetType;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasetConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;
import com.supermap.desktop.utilities.GridStatisticsModeUtilities;

/**
 * Created By Chens on 2017/8/15 0015
 */
public class MetaProcessNeighbourStatistics extends MetaProcess {
	private final static String INPUT_DATA = SOURCE_PANEL_DESCRIPTION;
	private final static String OUTPUT_DATA = "NeighbourStatisticsResult";

	private ParameterDatasourceConstrained sourceDatasource;
	private ParameterSingleDataset sourceDataset;
	private ParameterSaveDataset resultDataset;
	private ParameterComboBox comboBoxStatisticMode;
	private ParameterShapeType shapeType;
	private ParameterCheckBox checkBoxIgnore;

	public MetaProcessNeighbourStatistics() {
		setTitle(ProcessProperties.getString("String_Title_NeighbourStatistics"));
		initParameters();
		initParameterConstraint();
		initParametersState();
	}

	private void initParameters() {
		sourceDatasource = new ParameterDatasourceConstrained();
		sourceDataset = new ParameterSingleDataset(DatasetType.GRID);
		resultDataset = new ParameterSaveDataset();
		comboBoxStatisticMode = new ParameterComboBox(ProcessProperties.getString("String_Label_StatisticType"));
		checkBoxIgnore = new ParameterCheckBox(ProcessProperties.getString("String_IgnoreNoValue"));
		shapeType = new ParameterShapeType();

		ParameterCombine sourceCombine = new ParameterCombine();
		sourceCombine.setDescribe(CoreProperties.getString("String_GroupBox_SourceData"));
		sourceCombine.addParameters(sourceDatasource, sourceDataset);
		ParameterCombine settingCombine = new ParameterCombine();
		settingCombine.setDescribe(ProcessProperties.getString("String_setParameter"));
		settingCombine.addParameters(comboBoxStatisticMode, shapeType, checkBoxIgnore);
		ParameterCombine resultCombine = new ParameterCombine();
		resultCombine.setDescribe(CoreProperties.getString("String_GroupBox_ResultData"));
		resultCombine.addParameters(resultDataset);

		parameters.setParameters(sourceCombine, settingCombine, resultCombine);
		this.parameters.addInputParameters(INPUT_DATA, DatasetTypes.GRID, sourceCombine);
		this.parameters.addOutputParameters(OUTPUT_DATA,
				ProcessOutputResultProperties.getString("String_NeighbourStatisticResult"),
				DatasetTypes.GRID, resultCombine);
	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint constraintSource = new EqualDatasourceConstraint();
		constraintSource.constrained(sourceDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		constraintSource.constrained(sourceDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);
		EqualDatasetConstraint equalDatasetConstraint = new EqualDatasetConstraint();
		equalDatasetConstraint.constrained(sourceDataset, ParameterSingleDataset.DATASET_FIELD_NAME);
		equalDatasetConstraint.constrained(shapeType, ParameterShapeType.DATASET_FIELD_NAME);
		DatasourceConstraint.getInstance().constrained(resultDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);
	}

	private void initParametersState() {
		DatasetGrid datasetGrid = DatasetUtilities.getDefaultDatasetGrid();
		if (datasetGrid != null) {
			sourceDatasource.setSelectedItem(datasetGrid.getDatasource());
			sourceDataset.setSelectedItem(datasetGrid);
		}
		resultDataset.setDefaultDatasetName("result_neighbourStatistics");
		comboBoxStatisticMode.setItems(new ParameterDataNode(GridStatisticsModeUtilities.getGridStatisticsModeName(GridStatisticsMode.MIN), GridStatisticsMode.MIN),
				new ParameterDataNode(GridStatisticsModeUtilities.getGridStatisticsModeName(GridStatisticsMode.MAX), GridStatisticsMode.MAX),
				new ParameterDataNode(GridStatisticsModeUtilities.getGridStatisticsModeName(GridStatisticsMode.MEAN), GridStatisticsMode.MEAN),
				new ParameterDataNode(GridStatisticsModeUtilities.getGridStatisticsModeName(GridStatisticsMode.STDEV), GridStatisticsMode.STDEV),
				new ParameterDataNode(GridStatisticsModeUtilities.getGridStatisticsModeName(GridStatisticsMode.SUM), GridStatisticsMode.SUM),
				new ParameterDataNode(GridStatisticsModeUtilities.getGridStatisticsModeName(GridStatisticsMode.VARIETY), GridStatisticsMode.VARIETY),
				new ParameterDataNode(GridStatisticsModeUtilities.getGridStatisticsModeName(GridStatisticsMode.RANGE), GridStatisticsMode.RANGE),
				new ParameterDataNode(GridStatisticsModeUtilities.getGridStatisticsModeName(GridStatisticsMode.MAJORITY), GridStatisticsMode.MAJORITY),
				new ParameterDataNode(GridStatisticsModeUtilities.getGridStatisticsModeName(GridStatisticsMode.MINORITY), GridStatisticsMode.MINORITY),
				new ParameterDataNode(GridStatisticsModeUtilities.getGridStatisticsModeName(GridStatisticsMode.MEDIAN), GridStatisticsMode.MEDIAN));
		comboBoxStatisticMode.setSelectedItem(comboBoxStatisticMode.getItemAt(4));
		checkBoxIgnore.setSelectedItem(true);
	}

	@Override
	public IParameters getParameters() {
		return super.getParameters();
	}

	@Override
	public String getKey() {
		return MetaKeys.NEIGHBOUR_STATISTIC;
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		try {
			DatasetGrid src = null;
			if (parameters.getInputs().getData(INPUT_DATA).getValue() != null) {
				src = (DatasetGrid) parameters.getInputs().getData(INPUT_DATA).getValue();
			} else {
				src = (DatasetGrid) sourceDataset.getSelectedItem();
			}
			boolean isIgnore = Boolean.parseBoolean(checkBoxIgnore.getSelectedItem());
			GridStatisticsMode mode = (GridStatisticsMode) comboBoxStatisticMode.getSelectedData();
			String datasetName = resultDataset.getResultDatasource().getDatasets().getAvailableDatasetName(resultDataset.getDatasetName());
			NeighbourStatisticsParameter neighbourStatisticsParameter = new NeighbourStatisticsWedgeParameter();
			NeighbourShape neighbourShape = (NeighbourShape) shapeType.getSelectedItem();
			if (neighbourShape.getShapeType().equals(NeighbourShapeType.RECTANGLE)) {
				NeighbourStatisticsRectangleParameter neighbourStatisticsRectangleParameter = new NeighbourStatisticsRectangleParameter();
				neighbourStatisticsRectangleParameter.setHeight(((NeighbourShapeRectangle) neighbourShape).getHeight());
				neighbourStatisticsRectangleParameter.setWidth(((NeighbourShapeRectangle) neighbourShape).getWidth());
				neighbourStatisticsParameter = neighbourStatisticsRectangleParameter;
			} else if (neighbourShape.getShapeType().equals(NeighbourShapeType.CIRCLE)) {
				NeighbourStatisticsCircleParameter neighbourStatisticsCircleParameter = new NeighbourStatisticsCircleParameter();
				neighbourStatisticsCircleParameter.setRadius(((NeighbourShapeCircle) neighbourShape).getRadius());
				neighbourStatisticsParameter = neighbourStatisticsCircleParameter;
			} else if (neighbourShape.getShapeType().equals(NeighbourShapeType.ANNULUS)) {
				NeighbourStatisticsAnnulusParameter neighbourStatisticsAnnulusParameter = new NeighbourStatisticsAnnulusParameter();
				neighbourStatisticsAnnulusParameter.setInnerRadius(((NeighbourShapeAnnulus) neighbourShape).getInnerRadius());
				neighbourStatisticsAnnulusParameter.setOuterRadius(((NeighbourShapeAnnulus) neighbourShape).getOuterRadius());
				neighbourStatisticsParameter = neighbourStatisticsAnnulusParameter;
			} else if (neighbourShape.getShapeType().equals(NeighbourShapeType.WEDGE)) {
				NeighbourStatisticsWedgeParameter neighbourStatisticsWedgeParameter = new NeighbourStatisticsWedgeParameter();
				neighbourStatisticsWedgeParameter.setRadius(((NeighbourShapeWedge) neighbourShape).getRadius());
				neighbourStatisticsWedgeParameter.setStartAngle(((NeighbourShapeWedge) neighbourShape).getStartAngle());
				neighbourStatisticsWedgeParameter.setEndAngle(((NeighbourShapeWedge) neighbourShape).getEndAngle());
				neighbourStatisticsParameter = neighbourStatisticsWedgeParameter;
			}
			neighbourStatisticsParameter.setUnitType(neighbourShape.getUnitType());
			neighbourStatisticsParameter.setSourceDataset(src);
			neighbourStatisticsParameter.setIgnoreNoValue(isIgnore);
			neighbourStatisticsParameter.setTargetDatasetName(datasetName);
			neighbourStatisticsParameter.setTargetDatasource(resultDataset.getResultDatasource());
			neighbourStatisticsParameter.setStatisticsMode(mode);

			StatisticsAnalyst.addSteppedListener(steppedListener);
			DatasetGrid result = StatisticsAnalyst.neighbourStatistics(neighbourStatisticsParameter);
			isSuccessful = result != null;
			this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(result);
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			StatisticsAnalyst.removeSteppedListener(steppedListener);
		}
		return isSuccessful;
	}
}
