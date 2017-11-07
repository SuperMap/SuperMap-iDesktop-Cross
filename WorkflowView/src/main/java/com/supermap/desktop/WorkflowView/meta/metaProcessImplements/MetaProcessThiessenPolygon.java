package com.supermap.desktop.WorkflowView.meta.metaProcessImplements;

import com.supermap.analyst.spatialanalyst.ProximityAnalyst;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.ParameterCombine;
import com.supermap.desktop.process.parameter.ipls.ParameterDatasourceConstrained;
import com.supermap.desktop.process.parameter.ipls.ParameterSaveDataset;
import com.supermap.desktop.process.parameter.ipls.ParameterSingleDataset;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;

/**
 * Created by Chen on 2017/7/3 0003.
 */
public class MetaProcessThiessenPolygon extends MetaProcess {
	private static final String INPUT_DATA = SOURCE_PANEL_DESCRIPTION;
	private static final String OUTPUT_DATA = "ThiessenPolygonResult";

	private ParameterDatasourceConstrained sourceDatasource;
	private ParameterSingleDataset sourceDataset;
	private ParameterSaveDataset resultDataset;

	public MetaProcessThiessenPolygon() {
		setTitle(ProcessProperties.getString("String_Form_ThiessenPolygon"));
		initParameters();
		initParameterConstraint();
		initParametersState();
	}

	private void initParameters() {
		sourceDatasource = new ParameterDatasourceConstrained();
		sourceDatasource.setDescribe(CoreProperties.getString("String_Label_Datasource"));
		sourceDataset = new ParameterSingleDataset(DatasetType.POINT);
		sourceDataset.setDescribe(CoreProperties.getString("String_Label_Dataset"));
		ParameterCombine sourceData = new ParameterCombine();
		sourceData.setDescribe(CoreProperties.getString("String_GroupBox_SourceData"));
		sourceData.addParameters(sourceDatasource, sourceDataset);

		resultDataset = new ParameterSaveDataset();

		ParameterCombine resultData = new ParameterCombine();
		resultData.setDescribe(CoreProperties.getString("String_GroupBox_ResultData"));
		resultData.addParameters(resultDataset);

		parameters.setParameters(sourceData, resultData);
		parameters.addInputParameters(INPUT_DATA, DatasetTypes.POINT, sourceData);
		parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_Result_ThiessenPolygon"), DatasetTypes.REGION, resultData);
	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(sourceDatasource, ParameterDatasourceConstrained.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(sourceDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);
	}

	private void initParametersState() {
		Dataset datasetVector = DatasetUtilities.getDefaultDataset(DatasetType.POINT);
		if (datasetVector != null) {
			sourceDatasource.setSelectedItem(datasetVector.getDatasource());
			sourceDataset.setSelectedItem(datasetVector);
		}
		resultDataset.setDefaultDatasetName("result_thiessen");
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;

		try {
			ProximityAnalyst.addSteppedListener(steppedListener);
			DatasetVector src = null;
			if (this.getParameters().getInputs().getData(INPUT_DATA).getValue() != null) {
				src = (DatasetVector) getParameters().getInputs().getData(INPUT_DATA).getValue();
			} else {
				src = (DatasetVector) sourceDataset.getSelectedItem();
			}
			String datasetName = resultDataset.getDatasetName();
			datasetName = resultDataset.getResultDatasource().getDatasets().getAvailableDatasetName(datasetName);
			DatasetVector result = ProximityAnalyst.createThiessenPolygon(src, resultDataset.getResultDatasource(), datasetName, null);
			this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(result);
			isSuccessful = result != null;
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			ProximityAnalyst.removeSteppedListener(steppedListener);
		}
		return isSuccessful;
	}

	@Override
	public IParameters getParameters() {
		return parameters;
	}

	@Override
	public String getKey() {
		return MetaKeys.THIESSENPOLYGON;
	}
}
