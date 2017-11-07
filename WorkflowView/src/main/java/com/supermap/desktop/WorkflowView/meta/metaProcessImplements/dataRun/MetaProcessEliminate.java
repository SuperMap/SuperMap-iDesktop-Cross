package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.dataRun;

import com.supermap.analyst.spatialanalyst.EliminateMode;
import com.supermap.analyst.spatialanalyst.Generalization;
import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

/**
 * Created By Chens on 2017/8/8 0008
 * 碎多边形合并
 */
public class MetaProcessEliminate extends MetaProcess {
	private final static String INPUT_DATA = CoreProperties.getString("String_GroupBox_SourceData");
	private final static String OUTPUT_DATA = "EliminateResult";

	private ParameterDatasourceConstrained sourceDatasources;
	private ParameterSingleDataset sourceDataset;
	private ParameterNumber numberTolerance;
	private ParameterNumber numberArea;
	private ParameterCheckBox checkBoxDelete;

	public MetaProcessEliminate() {
		setTitle(ProcessProperties.getString("String_Title_Eliminate"));
		initParameters();
		initParameterConstraint();
		initParametersState();
		registerListener();
	}

	private void initParameters() {
		sourceDatasources = new ParameterDatasourceConstrained();
		sourceDataset = new ParameterSingleDataset(DatasetType.REGION);
		numberTolerance = new ParameterNumber(ProcessProperties.getString("String_Resample_VertexInterval"));
		numberArea = new ParameterNumber(ProcessProperties.getString("String_ColumnPolygonAreaTolerance"));
		checkBoxDelete = new ParameterCheckBox(ProcessProperties.getString("String_ColumnDeleteSingleRegion"));

		ParameterCombine sourceCombine = new ParameterCombine();
		sourceCombine.setDescribe(CoreProperties.getString("String_GroupBox_SourceData"));
		sourceCombine.addParameters(sourceDatasources, sourceDataset);
		ParameterCombine settingCombine = new ParameterCombine();
		settingCombine.setDescribe(CoreProperties.getString("String_GroupBox_ParamSetting"));
		settingCombine.addParameters(checkBoxDelete, numberTolerance, numberArea);

		parameters.setParameters(sourceCombine, settingCombine);
		parameters.addInputParameters(INPUT_DATA, DatasetTypes.REGION, sourceCombine);
		parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_Result_Eliminate"), DatasetTypes.REGION, sourceCombine);
	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint constraintSource = new EqualDatasourceConstraint();
		constraintSource.constrained(sourceDatasources, ParameterDatasource.DATASOURCE_FIELD_NAME);
		constraintSource.constrained(sourceDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);
	}

	private void initParametersState() {
		numberTolerance.setSelectedItem(1);
		Dataset dataset = DatasetUtilities.getDefaultDataset(DatasetType.REGION);
		if (dataset != null) {
			sourceDatasources.setSelectedItem(dataset.getDatasource());
			sourceDataset.setSelectedItem(dataset);
			numberArea.setSelectedItem(getMaxAreaTolerance((DatasetVector) dataset));
			resetNumTolerance((DatasetVector) dataset);
		}
		numberTolerance.setMinValue(0);
		numberArea.setMinValue(0);
	}

	private void registerListener() {
		sourceDataset.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (sourceDataset.getSelectedItem() != null && evt.getNewValue() instanceof DatasetVector) {
					DatasetVector datasetVector = (DatasetVector) evt.getNewValue();
					numberArea.setSelectedItem(getMaxAreaTolerance(datasetVector));
					resetNumTolerance(datasetVector);
				}
			}
		});
	}

	@Override
	public IParameters getParameters() {
		return super.getParameters();
	}

	@Override
	public String getKey() {
		return MetaKeys.ELIMINATE;
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		try {
			Generalization.addSteppedListener(steppedListener);

			DatasetVector src = null;
			if (parameters.getInputs().getData(INPUT_DATA).getValue() != null) {
				src = (DatasetVector) parameters.getInputs().getData(INPUT_DATA).getValue();
			} else {
				src = (DatasetVector) sourceDataset.getSelectedItem();
			}
			double vertexTolerance = Double.parseDouble(numberTolerance.getSelectedItem());
			double regionTolerance = Double.parseDouble(numberArea.getSelectedItem());
			boolean isDeleteSingleRegion = Boolean.parseBoolean(checkBoxDelete.getSelectedItem());
			isSuccessful = Generalization.eliminate(src, regionTolerance, vertexTolerance, EliminateMode.ELIMINATE_BY_AREA, isDeleteSingleRegion);
			this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(src);
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			Generalization.removeSteppedListener(steppedListener);
		}

		return isSuccessful;
	}

	private String getMaxAreaTolerance(DatasetVector datasetVector) {
		double maxAreaTolerance = 0;
		Recordset recordset = datasetVector.getRecordset(false, CursorType.DYNAMIC);
		while (!recordset.isEOF()) {
			double area = recordset.getDouble("SmArea");
			maxAreaTolerance = maxAreaTolerance > area ? maxAreaTolerance : area;
			recordset.moveNext();
		}
		recordset.close();
		recordset.dispose();
		maxAreaTolerance = maxAreaTolerance / Math.pow(10, 6);
		DecimalFormat format = new DecimalFormat("##0.00");
		return format.format(maxAreaTolerance);
	}

	private void resetNumTolerance(DatasetVector datasetVector) {
		double tolerance = datasetVector.getTolerance().getNodeSnap();
		if (tolerance == 0) {
			tolerance = 0.00001;
		}
		DecimalFormat format = new DecimalFormat("#0.000000000000000");
		String result = format.format(tolerance);
		while (result.charAt(result.length() - 1) == '0') {
			result = result.substring(0, result.length() - 1);
		}
		numberTolerance.setSelectedItem(result);
	}

	@Override
	public boolean isChangeSourceData() {
		return true;
	}
}
