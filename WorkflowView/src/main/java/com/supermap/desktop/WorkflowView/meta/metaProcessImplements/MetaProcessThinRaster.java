package com.supermap.desktop.WorkflowView.meta.metaProcessImplements;

import com.supermap.analyst.spatialanalyst.ConversionAnalyst;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetGrid;
import com.supermap.data.DatasetImage;
import com.supermap.data.DatasetType;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * 栅格细化
 * Created by lixiaoyao on 2017/7/11.
 */
public class MetaProcessThinRaster extends MetaProcessGridAnalyst {
	private final static String INPUT_DATA = CoreProperties.getString("String_GroupBox_SourceData");
	private final static String OUTPUT_DATA = "ThinRasterResult";

	private ParameterDatasourceConstrained sourceDatasource;
	private ParameterSingleDataset sourceDataset;
	private ParameterCombine sourceData;

	private ParameterNumber textFieldNoValue;
	private ParameterNumber textFieldNoValueTolerance;
	private ParameterCombine parameterSetting;

	private ParameterSaveDataset resultDataset;
	private ParameterCombine resultData;

	public MetaProcessThinRaster() {
		setTitle(ProcessProperties.getString("String_Form_GridThinRaster"));
		initParameters();
		initParameterConstraint();
		initParametersState();
		registerListener();
	}

	private void initParameters() {
		initEnvironment();
		sourceDatasource = new ParameterDatasourceConstrained();
		sourceDatasource.setDescribe(CoreProperties.getString("String_Label_Datasource"));
		sourceDataset = new ParameterSingleDataset(DatasetType.GRID, DatasetType.IMAGE);
		sourceDataset.setDescribe(CoreProperties.getString("String_Label_Dataset"));

		resultDataset = new ParameterSaveDataset();

		textFieldNoValue = new ParameterNumber(CoreProperties.getString("String_Label_NoData"));
		textFieldNoValueTolerance = new ParameterNumber(CoreProperties.getString("String_Label_NoValueTolerance"));

		sourceData = new ParameterCombine();
		sourceData.setDescribe(CoreProperties.getString("String_GroupBox_SourceData"));
		sourceData.addParameters(sourceDatasource, sourceDataset);

		parameterSetting = new ParameterCombine();
		parameterSetting.setDescribe(CoreProperties.getString("String_GroupBox_ParamSetting"));
		parameterSetting.addParameters(textFieldNoValue, textFieldNoValueTolerance);

		resultData = new ParameterCombine();
		resultData.setDescribe(CoreProperties.getString("String_GroupBox_ResultData"));
		resultData.addParameters(resultDataset);


		this.parameters.setParameters(sourceData, parameterSetting, resultData);
//		this.parameters.addInputParameters(INPUT_DATA, DatasetTypes.GRID, sourceData);
		this.parameters.addInputParameters(INPUT_DATA, DatasetTypes.ALL_RASTER, sourceData);
//		this.parameters.addOutputParameters(OUTPUT_DATA, DatasetTypes.GRID, resultData);
		this.parameters.addOutputParameters(OUTPUT_DATA,
				ProcessOutputResultProperties.getString("String_ThinRasterResult"),
				DatasetTypes.ALL_RASTER, resultData);
	}

	private void initEnvironment() {
		parameterGridAnalystSetting.setResultBoundsCustomOnly(true);
		parameterGridAnalystSetting.setCellSizeCustomOnly(true);
	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(sourceDatasource, ParameterDatasourceConstrained.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(sourceDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		DatasourceConstraint.getInstance().constrained(resultDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);
	}

	private void initParametersState() {
		Dataset datasetGrid = DatasetUtilities.getDefaultDataset(DatasetType.GRID, DatasetType.IMAGE);
		if (datasetGrid != null) {
			this.sourceDatasource.setSelectedItem(datasetGrid.getDatasource());
			this.sourceDataset.setSelectedItem(datasetGrid);
			if (datasetGrid instanceof DatasetGrid) {
				textFieldNoValue.setSelectedItem(((DatasetGrid) datasetGrid).getNoValue());
			} else {
				textFieldNoValue.setSelectedItem("16777215");
			}
		}

		resultDataset.setDefaultDatasetName("result_thinRaster");
		//textFieldNoValue.setSelectedItem("-9999");
		textFieldNoValue.setRequisite(true);
		textFieldNoValueTolerance.setSelectedItem("0");
		textFieldNoValueTolerance.setMinValue(0);
		textFieldNoValueTolerance.setIsIncludeMin(true);
		textFieldNoValueTolerance.setRequisite(true);
	}

	private void registerListener() {
		sourceDataset.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (sourceDataset.getSelectedItem() instanceof DatasetGrid) {
					textFieldNoValue.setSelectedItem(((DatasetGrid) sourceDataset.getSelectedItem()).getNoValue());
				} else if (sourceDataset.getSelectedItem() instanceof DatasetImage) {
					textFieldNoValue.setSelectedItem("16777215");
				}
			}
		});
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
				src = sourceDataset.getSelectedDataset();
			}
			ConversionAnalyst.addSteppedListener(steppedListener);

			Dataset result = ConversionAnalyst.thinRaster(src, Math.round(Double.valueOf(this.textFieldNoValue.getSelectedItem().toString())), Double.valueOf(this.textFieldNoValueTolerance.getSelectedItem().toString()), this.resultDataset.getResultDatasource(), datasetName);

			this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(result);
			isSuccessful = result != null;

		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			ConversionAnalyst.removeSteppedListener(steppedListener);
		}
		return isSuccessful;
	}

	@Override
	public IParameters getParameters() {
		return parameters;
	}

	@Override
	public String getKey() {
		return MetaKeys.THINRASTER;
	}
}
