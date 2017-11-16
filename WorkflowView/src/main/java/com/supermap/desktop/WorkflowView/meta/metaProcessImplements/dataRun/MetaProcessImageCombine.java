package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.dataRun;

import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.Toolkit;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.process.types.DatasetTypes;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;

/**
 * Created by lixiaoyao on 2017/8/15.
 */
public class MetaProcessImageCombine extends MetaProcess {
	private final static String INPUT_RED_DATA = CoreProperties.getString("String_RedBand");
	private final static String INPUT_GREEN_DATA = CoreProperties.getString("String_GreenBand");
	private final static String INPUT_BLUE_DATA = CoreProperties.getString("String_BlueBand");
	private final static String OUTPUT_DATA = "ImageCombineResult";

	private ParameterDatasourceConstrained redDatasource;
	private ParameterSingleDataset redDataset;
	private ParameterDatasourceConstrained greenDatasource;
	private ParameterSingleDataset greenDataset;
	private ParameterDatasourceConstrained blueDatasource;
	private ParameterSingleDataset blueDataset;
	private ParameterSaveDataset resultDataset;

	public MetaProcessImageCombine() {
		setTitle(ProcessProperties.getString("String_ImageCombine"));
		initParameters();
		initParametersState();
		initParameterConstraint();
	}

	private void initParameters() {
		this.redDatasource = new ParameterDatasourceConstrained();
		this.redDatasource.setDescribe(CoreProperties.getString("String_Label_Datasource"));
		this.redDataset = new ParameterSingleDataset(DatasetType.IMAGE);
		this.redDataset.setDescribe(CoreProperties.getString("String_Label_Dataset"));
		this.greenDatasource = new ParameterDatasourceConstrained();
		this.greenDatasource.setDescribe(CoreProperties.getString("String_Label_Datasource"));
		this.greenDataset = new ParameterSingleDataset(DatasetType.IMAGE);
		this.greenDataset.setDescribe(CoreProperties.getString("String_Label_Dataset"));
		this.blueDatasource = new ParameterDatasourceConstrained();
		this.blueDatasource.setDescribe(CoreProperties.getString("String_Label_Datasource"));
		this.blueDataset = new ParameterSingleDataset(DatasetType.IMAGE);
		this.blueDataset.setDescribe(CoreProperties.getString("String_Label_Dataset"));
		this.resultDataset = new ParameterSaveDataset();

		ParameterCombine redData = new ParameterCombine();
		redData.setDescribe(INPUT_RED_DATA);
		redData.addParameters(this.redDatasource, this.redDataset);
		ParameterCombine greenData = new ParameterCombine();
		greenData.setDescribe(INPUT_GREEN_DATA);
		greenData.addParameters(this.greenDatasource, this.greenDataset);
		ParameterCombine blueData = new ParameterCombine();
		blueData.setDescribe(INPUT_BLUE_DATA);
		blueData.addParameters(this.blueDatasource, this.blueDataset);
		ParameterCombine targetData = new ParameterCombine();
		targetData.setDescribe(CoreProperties.getString("String_GroupBox_ResultData"));
		targetData.addParameters(this.resultDataset);

		this.parameters.setParameters(redData, greenData, blueData, targetData);
		this.parameters.addInputParameters(INPUT_RED_DATA, DatasetTypes.IMAGE, redData);
		this.parameters.addInputParameters(INPUT_GREEN_DATA, DatasetTypes.IMAGE, greenData);
		this.parameters.addInputParameters(INPUT_BLUE_DATA, DatasetTypes.IMAGE, blueData);
		this.parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_Result_ImageCombine"), DatasetTypes.IMAGE, targetData);
	}

	private void initParametersState() {
		this.resultDataset.setDefaultDatasetName("result_ImageCombine");
		Dataset dataset = DatasetUtilities.getDefaultDataset(DatasetType.IMAGE);
		if (dataset != null) {
			this.redDatasource.setSelectedItem(dataset.getDatasource());
			this.redDataset.setSelectedItem(dataset);
			this.resultDataset.setResultDatasource(dataset.getDatasource());
			this.greenDatasource.setSelectedItem(dataset.getDatasource());
			this.greenDataset.setSelectedItem(dataset);
			this.blueDatasource.setSelectedItem(dataset.getDatasource());
			this.blueDataset.setSelectedItem(dataset);
		}
	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(this.redDatasource, ParameterDatasourceConstrained.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(this.redDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		DatasourceConstraint.getInstance().constrained(this.resultDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);

		EqualDatasourceConstraint distanceConstraint = new EqualDatasourceConstraint();
		distanceConstraint.constrained(this.greenDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		distanceConstraint.constrained(this.greenDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasourceConstraint directionConstraint = new EqualDatasourceConstraint();
		directionConstraint.constrained(this.blueDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		directionConstraint.constrained(this.blueDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		try {
			String datasetName = resultDataset.getDatasetName();
			datasetName = resultDataset.getResultDatasource().getDatasets().getAvailableDatasetName(datasetName);

			Dataset srcRed = null;
			if (this.getParameters().getInputs().getData(INPUT_RED_DATA).getValue() != null) {
				srcRed = (Dataset) this.getParameters().getInputs().getData(INPUT_RED_DATA).getValue();
			} else {
				srcRed = redDataset.getSelectedItem();
			}
			Dataset srcGreen = null;
			if (this.getParameters().getInputs().getData(INPUT_GREEN_DATA).getValue() != null) {
				srcGreen = (Dataset) this.getParameters().getInputs().getData(INPUT_GREEN_DATA).getValue();
			} else {
				srcGreen = greenDataset.getSelectedItem();
			}
			Dataset srcBlue = null;
			if (this.getParameters().getInputs().getData(INPUT_BLUE_DATA).getValue() != null) {
				srcBlue = (Dataset) this.getParameters().getInputs().getData(INPUT_BLUE_DATA).getValue();
			} else {
				srcBlue = blueDataset.getSelectedItem();
			}
			isSuccessful = Toolkit.CombineBand(datasetName, this.resultDataset.getResultDatasource(), srcRed, srcGreen, srcBlue);
			Dataset resultDataset = this.resultDataset.getResultDatasource().getDatasets().get(datasetName);
			this.resultDataset.getResultDatasource().refresh();
			this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(resultDataset);
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			return isSuccessful;
		}
	}

	@Override
	public IParameters getParameters() {
		return parameters;
	}

	@Override
	public String getKey() {
		return MetaKeys.IMAGE_COMBINE;
	}
}
