package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.gridStatisticsAnalyst;

import com.supermap.analyst.spatialanalyst.BasicStatisticsAnalystResult;
import com.supermap.analyst.spatialanalyst.StatisticsAnalyst;
import com.supermap.data.DatasetGrid;
import com.supermap.data.DatasetType;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.definedClass.StatisticsCollection;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.interfaces.datas.types.CommonTypes;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;

import javax.swing.*;

/**
 * Created By Chens on 2017/8/15 0015
 */
public class MetaProcessBasicStatistics extends MetaProcess {
	private final static String INPUT_DATA = SOURCE_PANEL_DESCRIPTION;
	private final static String OUTPUT_DATA = "BasicStatisticResult";

	private ParameterDatasourceConstrained sourceDatasource;
	private ParameterSingleDataset sourceDataset;
	private ParameterTextArea textAreaResult;
	private ParameterHistogram histogram;

	public MetaProcessBasicStatistics() {
		setTitle(ProcessProperties.getString("String_Title_BasicStatistics"));
		initParameters();
		initParameterState();
		initParameterConstraint();
	}

	private void initParameters() {
		sourceDatasource = new ParameterDatasourceConstrained();
		sourceDataset = new ParameterSingleDataset(DatasetType.GRID);
		textAreaResult = new ParameterTextArea();
		histogram = new ParameterHistogram();

		ParameterCombine sourceCombine = new ParameterCombine();
		sourceCombine.setDescribe(CoreProperties.getString("String_GroupBox_SourceData"));
		sourceCombine.addParameters(sourceDatasource, sourceDataset);
		ParameterCombine resultCombine = new ParameterCombine();
		resultCombine.setDescribe(ControlsProperties.getString("String_StatisticResult"));
		resultCombine.addParameters(textAreaResult, histogram);

		parameters.setParameters(sourceCombine, resultCombine);
		this.parameters.addInputParameters(INPUT_DATA, DatasetTypes.GRID, sourceCombine);
		this.parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_BasicStatisticResult"), CommonTypes.STATISTICS, resultCombine);
	}

	private void initParameterState() {
		DatasetGrid datasetGrid = DatasetUtilities.getDefaultDatasetGrid();
		if (datasetGrid != null) {
			sourceDatasource.setSelectedItem(datasetGrid.getDatasource());
			sourceDataset.setSelectedItem(datasetGrid);
		}
		((JTextArea)((JScrollPane)((JPanel)textAreaResult.getParameterPanel().getPanel()).getComponent(0)).getViewport().getView()).setEditable(false);
	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint constraintSource = new EqualDatasourceConstraint();
		constraintSource.constrained(sourceDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		constraintSource.constrained(sourceDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);
	}

	@Override
	public IParameters getParameters() {
		return super.getParameters();
	}

	@Override
	public String getKey() {
		return MetaKeys.BASIC_STATISTIC;
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		try {
			StatisticsAnalyst.addSteppedListener(steppedListener);
			DatasetGrid src;
			if (parameters.getInputs().getData(INPUT_DATA).getValue() != null) {
				src = (DatasetGrid) parameters.getInputs().getData(INPUT_DATA).getValue();
			} else {
				src = (DatasetGrid) sourceDataset.getSelectedItem();
			}
			BasicStatisticsAnalystResult basicStatisticsAnalystResult = StatisticsAnalyst.basicStatistics(src);
			isSuccessful = basicStatisticsAnalystResult != null;
			double max = basicStatisticsAnalystResult.getMax();
			double min = basicStatisticsAnalystResult.getMin();
			double mean = basicStatisticsAnalystResult.getMean();
			double std = basicStatisticsAnalystResult.getStandardDeviation();
			double var = Math.pow(std, 2);
			textAreaResult.setSelectedItem(ControlsProperties.getString("String_LabelMaxValue") + max + "\n" +
					ControlsProperties.getString("String_LabelMinValue") + min + "\n" +
					CoreProperties.getString("String_Mean") + mean + "\n" +
					ProcessProperties.getString("String_Result_StandardDeviation") + std + "\n" +
					ControlsProperties.getString("String_Label_Variance") + var);
			StatisticsCollection statisticsCollection = new StatisticsCollection(max, min, mean, std, var);
			if (histogram.isCreate()) {
				histogram.setSelectedItem(StatisticsAnalyst.createHistogram(src, histogram.getGroupCount(), histogram.getFunctionType()));
			}
			this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(statisticsCollection);
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			StatisticsAnalyst.removeSteppedListener(steppedListener);
		}

		return isSuccessful;
	}
}
