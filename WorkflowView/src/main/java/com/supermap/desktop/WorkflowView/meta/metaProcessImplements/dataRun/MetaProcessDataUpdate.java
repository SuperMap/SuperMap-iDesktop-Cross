package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.dataRun;

import com.supermap.data.Dataset;
import com.supermap.data.DatasetGrid;
import com.supermap.data.DatasetImage;
import com.supermap.data.DatasetType;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.EqualDatasetConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.ParameterCombine;
import com.supermap.desktop.process.parameter.ipls.ParameterDatasetChooseTable;
import com.supermap.desktop.process.parameter.ipls.ParameterDatasourceConstrained;
import com.supermap.desktop.process.parameter.ipls.ParameterSingleDataset;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;

import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * Created by lixiaoyao on 2017/8/31.
 */
public class MetaProcessDataUpdate extends MetaProcess {
	private final static String INPUT_DATA = CoreProperties.getString("String_GroupBox_SourceData");
	private final static String OUTPUT_DATA = "DataUpdateResult";

	private ParameterDatasourceConstrained sourceDatasource;
	private ParameterSingleDataset sourceDataset;
	private ParameterDatasetChooseTable chooseTable;

	public MetaProcessDataUpdate() {
		setTitle(ProcessProperties.getString("String_RasterDataUpdate"));
		initParameters();
		initParametersState();
		initParameterConstraint();
		registerListner();
	}

	private void initParameters() {
		this.sourceDatasource = new ParameterDatasourceConstrained();
		this.sourceDatasource.setDescribe(CoreProperties.getString("String_Label_Datasource"));
		this.sourceDatasource.setReadOnlyNeeded(false);
		this.sourceDataset = new ParameterSingleDataset(DatasetType.GRID, DatasetType.IMAGE);
		this.sourceDataset.setDescribe(CoreProperties.getString("String_Label_Dataset"));
		this.chooseTable = new ParameterDatasetChooseTable();


		ParameterCombine sourceData = new ParameterCombine();
		sourceData.setDescribe(CoreProperties.getString("String_GroupBox_TargetData"));
		sourceData.addParameters(this.sourceDatasource, this.sourceDataset);

		this.parameters.setParameters(sourceData, this.chooseTable);
		this.parameters.addInputParameters(INPUT_DATA, DatasetTypes.ALL_RASTER, sourceData);
		this.parameters.addOutputParameters(OUTPUT_DATA,
				ProcessOutputResultProperties.getString("String_DataUpdateResult"),
				DatasetTypes.ALL_RASTER, sourceDataset);
	}

	private void initParametersState() {
		Dataset dataset = DatasetUtilities.getDefaultDataset(DatasetType.GRID, DatasetType.IMAGE);
		if (dataset != null) {
			this.sourceDatasource.setSelectedItem(dataset.getDatasource());
			this.sourceDataset.setSelectedItem(dataset);
			this.chooseTable.setDataset(dataset);
		}
		chooseTable.setComplexParameter(true);
	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(this.sourceDatasource, ParameterDatasourceConstrained.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(this.sourceDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasetConstraint equalDataset = new EqualDatasetConstraint();
		equalDataset.constrained(this.sourceDataset, ParameterSingleDataset.DATASET_FIELD_NAME);
		equalDataset.constrained(this.chooseTable, ParameterDatasetChooseTable.DATASET_FIELD_NAME);
	}

	private void registerListner() {
//		this.chooseTable.addPropertyListener(new PropertyChangeListener() {
//			@Override
//			public void propertyChange(PropertyChangeEvent evt) {
//				ArrayList<Dataset> datasets = (ArrayList<Dataset>) evt.getNewValue();
//				if (datasets == null || datasets.size() == 0) {
//					sourceDatasource.setEnabled(true);
//					sourceDataset.setEnabled(true);
//				} else {
//					sourceDatasource.setEnabled(false);
//					sourceDataset.setEnabled(false);
//				}
//			}
//		});
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		try {
			Dataset dataset = this.sourceDataset.getSelectedItem();
			ArrayList<Dataset> datasets = (ArrayList<Dataset>) this.chooseTable.getSelectedItem();
			boolean isGrid = (dataset.getType() == DatasetType.GRID);
			for (int i = 0; i < datasets.size(); i++) {
				if (!dataset.getBounds().hasIntersection(datasets.get(i).getBounds())) {
					Application.getActiveApplication().getOutput().output(
							MessageFormat.format(ProcessProperties.getString("String_DataUpdateInvalidBounds"),
									datasets.get(i).getDatasource().getAlias(), datasets.get(i).getName()));
					continue;
				} else if (isGrid) {
					if (!((DatasetGrid) dataset).getPixelFormat().equals(((DatasetGrid) datasets.get(i)).getPixelFormat())) {
						Application.getActiveApplication().getOutput().output(
								MessageFormat.format(ProcessProperties.getString("String_DataUpdatePixFormatIsNotEqual"),
										datasets.get(i).getDatasource().getAlias(), datasets.get(i).getName()));
						continue;
					}
				}
				if (isGrid) {
					((DatasetGrid) dataset).update((DatasetGrid) datasets.get(i));
				} else {
					((DatasetImage) dataset).update((DatasetImage) datasets.get(i));
				}
			}
			isSuccessful = dataset != null;
			this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(dataset);
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		}
		return isSuccessful;
	}

	@Override
	public IParameters getParameters() {
		return parameters;
	}

	@Override
	public String getKey() {
		return MetaKeys.DATA_UPDATE;
	}

	@Override
	public boolean isChangeSourceData() {
		return true;
	}
}
