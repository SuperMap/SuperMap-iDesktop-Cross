package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.dataRun;

import com.supermap.analyst.spatialanalyst.GeneralizeAnalyst;
import com.supermap.data.Dataset;
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
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.process.parameters.ParameterPanels.RasterReclass.ParameterRasterReclass;
import com.supermap.desktop.process.types.DatasetTypes;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;

/**
 * Created by lixiaoyao on 2017/9/1.
 */
public class MetaProcessRasterReclass extends MetaProcess {
	private final static String INPUT_DATA = CoreProperties.getString("String_GroupBox_SourceData");
	private final static String OUTPUT_DATA = "ReclassResult";

	private ParameterDatasourceConstrained sourceDatasource;
	private ParameterSingleDataset dataset;
	private ParameterRasterReclass parameterRasterReclass;
	private ParameterSaveDataset saveDataset;

	public MetaProcessRasterReclass() {
		setTitle(ProcessProperties.getString("String_RasterReclass"));
		initParameters();
		initParameterConstraint();
		initParametersState();
	}

	private void initParameters() {
		this.sourceDatasource = new ParameterDatasourceConstrained();
		this.dataset = new ParameterSingleDataset(DatasetType.GRID);
		this.parameterRasterReclass = new ParameterRasterReclass(CoreProperties.getString("String_GroupBox_ParamSetting"));
		this.saveDataset = new ParameterSaveDataset();

		ParameterCombine sourceData = new ParameterCombine();
		sourceData.setDescribe(CoreProperties.getString("String_GroupBox_SourceData"));
		sourceData.addParameters(this.sourceDatasource, this.dataset);
		ParameterCombine targetData = new ParameterCombine();
		targetData.setDescribe(CoreProperties.getString("String_GroupBox_ResultData"));
		targetData.addParameters(this.saveDataset);

		this.parameters.setParameters(sourceData, this.parameterRasterReclass, targetData);
		this.parameters.addInputParameters(INPUT_DATA, DatasetTypes.GRID, sourceData);
		this.parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_ReclassResult"), DatasetTypes.GRID, targetData);
	}


	private void initParameterConstraint() {
		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(this.sourceDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(this.dataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasetConstraint equalDatasetConstraint = new EqualDatasetConstraint();
		equalDatasetConstraint.constrained(this.dataset, ParameterSingleDataset.DATASET_FIELD_NAME);
		equalDatasetConstraint.constrained(this.parameterRasterReclass, ParameterRasterReclass.FIELD_DATASET);

		DatasourceConstraint.getInstance().constrained(this.saveDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);
	}

	private void initParametersState() {
		this.saveDataset.setDefaultDatasetName("result_Reclass");
		Dataset defaultDataset = DatasetUtilities.getDefaultDataset(DatasetType.GRID);
		if (defaultDataset != null) {
			this.sourceDatasource.setSelectedItem(defaultDataset.getDatasource());
			this.dataset.setSelectedItem(defaultDataset);
			this.saveDataset.setResultDatasource(defaultDataset.getDatasource());
			this.parameterRasterReclass.setDataset((DatasetGrid) defaultDataset);
		}
		this.sourceDatasource.setDescribe(CoreProperties.getString("String_Label_Datasource"));
		this.parameterRasterReclass.setComplexParameter(true);
	}


	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		try {

			String datasetName = this.saveDataset.getDatasetName();
			datasetName = this.saveDataset.getResultDatasource().getDatasets().getAvailableDatasetName(datasetName);
			DatasetGrid src = null;
			if (this.getParameters().getInputs().getData(INPUT_DATA).getValue() != null) {
				src = (DatasetGrid) this.getParameters().getInputs().getData(INPUT_DATA).getValue();
			} else {
				src = (DatasetGrid) this.dataset.getSelectedItem();
			}

			GeneralizeAnalyst.addSteppedListener(steppedListener);
			Dataset result = GeneralizeAnalyst.reclass(src, this.parameterRasterReclass.getReclassMappingTable(),
					this.parameterRasterReclass.getReclassPixelFormat(), this.saveDataset.getResultDatasource(), datasetName);
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
		return this.parameters;
	}

	@Override
	public String getKey() {
		return MetaKeys.RASTER_RECLASS;
	}
}
