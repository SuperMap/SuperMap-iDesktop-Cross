package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.dataRun;

import com.supermap.analyst.spatialanalyst.GeneralizeAnalyst;
import com.supermap.data.DatasetGrid;
import com.supermap.data.DatasetType;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.process.types.DatasetTypes;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by lixiaoyao on 2017/10/18.
 */
public class MetaProcessGridSlice extends MetaProcess {
	private final static String INPUT_DATA = CoreProperties.getString("String_GroupBox_SourceData");
	private final static String OUTPUT_DATA = "SliceResult";

	private ParameterDatasourceConstrained sourceDatasource;
	private ParameterSingleDataset sourceDataset;
	private ParameterNumber numberSeries;
	private ParameterNumber numberMinValue;
	private ParameterSaveDataset resultDataset;

	public MetaProcessGridSlice() {
		setTitle(ProcessProperties.getString("String_GridSliceTitle"));
		initParameters();
		initParameterConstraint();
		initParametersState();
		registerListener();
	}

	private void initParameters() {
		this.sourceDatasource = new ParameterDatasourceConstrained();
		this.sourceDatasource.setDescribe(CoreProperties.getString("String_Label_Datasource"));
		this.sourceDataset = new ParameterSingleDataset(DatasetType.GRID);
		this.sourceDataset.setDescribe(CoreProperties.getString("String_Label_Dataset"));
		this.sourceDataset.setRequired(true);
		this.numberSeries = new ParameterNumber(ProcessProperties.getString("String_SeriesNumber"));
		this.numberSeries.setRequired(true);
		this.numberMinValue = new ParameterNumber(ControlsProperties.getString("String_LabelMinValue"));
		this.numberMinValue.setRequired(true);
		this.numberMinValue.setTipButtonMessage(ProcessProperties.getString("String_SeriesTip"));
		this.resultDataset = new ParameterSaveDataset();

		ParameterCombine sourceData = new ParameterCombine();
		sourceData.setDescribe(CoreProperties.getString("String_GroupBox_SourceData"));
		sourceData.addParameters(this.sourceDatasource, this.sourceDataset);
		ParameterCombine parameterSetting = new ParameterCombine();
		parameterSetting.setDescribe(CoreProperties.getString("String_GroupBox_ParamSetting"));
		parameterSetting.addParameters(this.numberSeries, this.numberMinValue);
		ParameterCombine targetData = new ParameterCombine();
		targetData.setDescribe(CoreProperties.getString("String_GroupBox_ResultData"));
		targetData.addParameters(this.resultDataset);

		this.parameters.setParameters(sourceData, parameterSetting, targetData);
		this.parameters.addInputParameters(INPUT_DATA, DatasetTypes.GRID, sourceData);
		this.parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_SliceResult"), DatasetTypes.GRID, targetData);
	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint constraintSource = new EqualDatasourceConstraint();
		constraintSource.constrained(sourceDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		constraintSource.constrained(sourceDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		DatasourceConstraint.getInstance().constrained(resultDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);
	}

	private void initParametersState() {
		this.numberSeries.setMinValue(1);
		this.numberSeries.setIsIncludeMin(true);
		this.numberMinValue.setSelectedItem(0);
		DatasetGrid dataset = (DatasetGrid) DatasetUtilities.getDefaultDataset(DatasetType.GRID);
		if (dataset != null) {
			this.sourceDatasource.setSelectedItem(dataset.getDatasource());
			this.sourceDataset.setSelectedItem(dataset);
			this.resultDataset.setResultDatasource(dataset.getDatasource());
			changeDataset(dataset);
		}
		this.resultDataset.setDefaultDatasetName("result_Slice");
	}

	private void registerListener() {
		this.sourceDataset.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (sourceDataset.getSelectedItem() != null && evt.getNewValue() instanceof DatasetGrid) {
					changeDataset((DatasetGrid) evt.getNewValue());
				}
			}
		});
	}

	private void changeDataset(DatasetGrid datasetGrid) {
		if (Double.compare(datasetGrid.getMinValue(), datasetGrid.getMaxValue()) == 0) {
			this.numberSeries.setSelectedItem(1);
		} else {
			this.numberSeries.setSelectedItem(10);
		}
		this.numberMinValue.setSelectedItem(datasetGrid.getMinValue());
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		try {
			String datasetName = this.resultDataset.getDatasetName();
			datasetName = this.resultDataset.getResultDatasource().getDatasets().getAvailableDatasetName(datasetName);
			DatasetGrid src = null;
			if (this.getParameters().getInputs().getData(INPUT_DATA).getValue() != null) {
				src = (DatasetGrid) this.getParameters().getInputs().getData(INPUT_DATA).getValue();
			} else {
				src = (DatasetGrid) sourceDataset.getSelectedItem();
			}

			GeneralizeAnalyst.addSteppedListener(steppedListener);
			DatasetGrid result = GeneralizeAnalyst.slice(src, this.resultDataset.getResultDatasource(), datasetName,
					Integer.valueOf(this.numberSeries.getSelectedItem().toString()),
					Integer.valueOf(this.numberMinValue.getSelectedItem().toString()));
			this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(result);
			isSuccessful = result != null;
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			GeneralizeAnalyst.removeSteppedListener(steppedListener);
		}
		return isSuccessful;
	}

	@Override
	public IParameters getParameters() {
		return parameters;
	}

	@Override
	public String getKey() {
		return MetaKeys.GRID_SLICE;
	}
}
