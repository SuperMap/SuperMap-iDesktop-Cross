package com.supermap.desktop.WorkflowView.meta.metaProcessImplements;

import com.supermap.analyst.spatialanalyst.ProximityAnalyst;
import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasetConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.properties.CommonProperties;
import com.supermap.desktop.utilities.DatasetUtilities;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by Chen on 2017/7/3 0003.
 */
public class MetaProcessComputeDistance extends MetaProcess {
	private final static String INPUT_DATA = CommonProperties.getString("String_GroupBox_SourceData");
	private final static String PROXIMITY_DATA = CommonProperties.getString("String_GroupBox_ProximityData");
	private final static String OUTPUT_DATA = "ComputeDistanceResult";

	private final static String MIN_DISTANCE = "MinDistance";
	private final static String RANGE_DISTANCE = "RangeDistance";

	private ParameterDatasourceConstrained sourceDatasource;
	private ParameterSingleDataset sourceDataset;
	private ParameterDatasourceConstrained proximityDatasource;
	private ParameterSingleDataset proximityDataset;
	private ParameterTextArea textAreaSourceSQL;
	private ParameterTextArea textAreaProximitySQL;
	private ParameterSQLExpression expressionSource;
	private ParameterSQLExpression expressionProximity;
	private ParameterComboBox comboBoxComputeMethod;
	private ParameterCheckBox checkBoxMin;
	private ParameterCheckBox checkBoxMax;
	private ParameterNumber textNumMin;
	private ParameterNumber textNumMax;
	private ParameterSaveDataset resultDataset;

	public MetaProcessComputeDistance() {
		setTitle(ProcessProperties.getString("String_Form_ComputeDistance"));
		initParameters();
		initParameterConstraint();
		initParametersState();
		registerListener();
	}

	private void initParameters() {
		sourceDatasource = new ParameterDatasourceConstrained();
		sourceDataset = new ParameterSingleDataset(DatasetType.POINT);
		ParameterCombine sourceData = new ParameterCombine();
		sourceData.setDescribe(CommonProperties.getString("String_GroupBox_SourceData"));
		sourceData.addParameters(sourceDatasource, sourceDataset);

		textAreaSourceSQL = new ParameterTextArea(ProcessProperties.getString("String_ExpressionForTextAreaSource"));
		expressionSource = new ParameterSQLExpression(ControlsProperties.getString("String_SQLExpression"));
		ParameterCombine emptyCombine = new ParameterCombine(ParameterCombine.HORIZONTAL);
		emptyCombine.addParameters(new ParameterLabel(), new ParameterLabel(), expressionSource);
		ParameterCombine sourceSQL = new ParameterCombine();
		sourceSQL.setDescribe(ProcessProperties.getString("String_GroupBox_ExpressionForTextArea"));
		sourceSQL.addParameters(textAreaSourceSQL, emptyCombine);

		proximityDatasource = new ParameterDatasourceConstrained();
		proximityDatasource.setDescribe(CommonProperties.getString("String_Label_Datasource"));
		proximityDataset = new ParameterSingleDataset(DatasetType.POINT, DatasetType.LINE, DatasetType.REGION, DatasetType.NETWORK);
		proximityDataset.setDescribe(CommonProperties.getString("String_Label_Dataset"));
		ParameterCombine proximityData = new ParameterCombine();
		proximityData.setDescribe(CommonProperties.getString("String_GroupBox_ProximityData"));
		proximityData.addParameters(proximityDatasource, proximityDataset);

		textAreaProximitySQL = new ParameterTextArea(ProcessProperties.getString("String_ExpressionForTextAreaProximity"));
		expressionProximity = new ParameterSQLExpression(ControlsProperties.getString("String_SQLExpression"));
		ParameterCombine emptyCombine1 = new ParameterCombine(ParameterCombine.HORIZONTAL);
		emptyCombine1.addParameters(new ParameterLabel(), new ParameterLabel(), expressionProximity);
		ParameterCombine proximitySQL = new ParameterCombine();
		proximitySQL.setDescribe(ProcessProperties.getString("String_GroupBox_ExpressionForTextArea"));
		proximitySQL.addParameters(textAreaProximitySQL, emptyCombine1);

		comboBoxComputeMethod = new ParameterComboBox(ProcessProperties.getString("String_Label_ComputeMethod"));
		checkBoxMax = new ParameterCheckBox(ProcessProperties.getString("String_CheckBox_MaxDistance"));
		checkBoxMin = new ParameterCheckBox(ProcessProperties.getString("String_CheckBox_MinDistance"));
		textNumMax = new ParameterNumber(ProcessProperties.getString("String_Label_MaxDistance"));
		textNumMin = new ParameterNumber(ProcessProperties.getString("String_Label_MinDistance"));
		ParameterCombine setting = new ParameterCombine();
		setting.setDescribe(CommonProperties.getString("String_GroupBox_ParamSetting"));
		setting.addParameters(comboBoxComputeMethod, new ParameterCombine(ParameterCombine.HORIZONTAL).addParameters(checkBoxMin, checkBoxMax), textNumMax, textNumMin);
		resultDataset = new ParameterSaveDataset();
		ParameterCombine resultData = new ParameterCombine();
		resultData.setDescribe(CommonProperties.getString("String_GroupBox_ResultData"));
		resultData.addParameters(resultDataset);

		parameters.setParameters(sourceData, sourceSQL, proximityData, proximitySQL, setting, resultData);
		parameters.addInputParameters(INPUT_DATA, DatasetTypes.POINT, sourceData);
		parameters.addInputParameters(PROXIMITY_DATA, DatasetTypes.VECTOR, proximityData);
		parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_Result_ComputeDistance"), DatasetTypes.TABULAR, resultData);
	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint datasourceConstraintSource = new EqualDatasourceConstraint();
		datasourceConstraintSource.constrained(sourceDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		datasourceConstraintSource.constrained(sourceDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasourceConstraint datasourceConstraintProximity = new EqualDatasourceConstraint();
		datasourceConstraintProximity.constrained(proximityDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		datasourceConstraintProximity.constrained(proximityDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasetConstraint datasetConstraintSource = new EqualDatasetConstraint();
		datasetConstraintSource.constrained(sourceDataset, ParameterSingleDataset.DATASET_FIELD_NAME);
		datasetConstraintSource.constrained(expressionSource, ParameterSQLExpression.DATASET_FIELD_NAME);

		EqualDatasetConstraint datasetConstraintProximity = new EqualDatasetConstraint();
		datasetConstraintProximity.constrained(proximityDataset, ParameterSingleDataset.DATASET_FIELD_NAME);
		datasetConstraintProximity.constrained(expressionProximity, ParameterSQLExpression.DATASET_FIELD_NAME);

		DatasourceConstraint.getInstance().constrained(resultDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);
	}

	private void initParametersState() {
		Dataset datasetPoint = DatasetUtilities.getDefaultDataset(DatasetType.POINT);
		if (datasetPoint != null) {
			sourceDatasource.setSelectedItem(datasetPoint.getDatasource());
			sourceDataset.setSelectedItem(datasetPoint);
			expressionSource.setSelectDataset(sourceDataset.getSelectedItem());
		}

		Dataset datasetRegion = DatasetUtilities.getDefaultDataset(DatasetType.REGION);
		if (datasetRegion != null) {
			proximityDatasource.setSelectedItem(datasetRegion.getDatasource());
			proximityDataset.setSelectedItem(datasetRegion);
			expressionProximity.setSelectDataset(proximityDataset.getSelectedItem());
		}
		comboBoxComputeMethod.setItems(new ParameterDataNode(ProcessProperties.getString("String_Item_ClosestDistance"), MIN_DISTANCE),
				new ParameterDataNode(ProcessProperties.getString("String_Item_DistanceInRange"), RANGE_DISTANCE));
		textNumMax.setEnabled(false);
		textNumMin.setEnabled(false);
		textNumMax.setSelectedItem(0);
		textNumMax.setMinValue(0);
		textNumMax.setIsIncludeMin(false);
		textNumMin.setSelectedItem(0);
		textNumMin.setMinValue(0);
		resultDataset.setDefaultDatasetName("result_computeDistance");
		expressionSource.setAnchor(GridBagConstraints.EAST);
		expressionProximity.setAnchor(GridBagConstraints.EAST);
	}

	private void registerListener() {
		checkBoxMax.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				textNumMax.setEnabled(Boolean.parseBoolean(checkBoxMax.getSelectedItem()));
			}
		});
		checkBoxMin.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				textNumMin.setEnabled(Boolean.parseBoolean(checkBoxMin.getSelectedItem()));
			}
		});
		expressionSource.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (null != evt.getNewValue()) {
					textAreaSourceSQL.setSelectedItem(expressionSource.getSelectedItem());
				}
			}
		});
		expressionProximity.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (null != evt.getNewValue()) {
					textAreaProximitySQL.setSelectedItem(expressionProximity.getSelectedItem());
				}
			}
		});
		comboBoxComputeMethod.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				checkBoxMax.setSelectedItem(true);
				checkBoxMin.setSelectedItem(true);
			}
		});
		textNumMin.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				textNumMax.setMinValue(Double.valueOf(textNumMin.getSelectedItem()));
			}
		});
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		try {

			ProximityAnalyst.addSteppedListener(steppedListener);
			DatasetVector src;
			if (this.getParameters().getInputs().getData(INPUT_DATA).getValue() != null) {
				src = (DatasetVector) getParameters().getInputs().getData(INPUT_DATA).getValue();
			} else {
				src = (DatasetVector) sourceDataset.getSelectedItem();
			}
			DatasetVector srcReference;
			if (this.getParameters().getInputs().getData(PROXIMITY_DATA).getValue() != null) {
				srcReference = (DatasetVector) getParameters().getInputs().getData(PROXIMITY_DATA).getValue();
			} else {
				srcReference = (DatasetVector) proximityDataset.getSelectedItem();
			}
			Recordset recordsetSource;
			if (textAreaSourceSQL.getSelectedItem() != null) {
				recordsetSource = src.query(textAreaSourceSQL.getSelectedItem().toString(), CursorType.STATIC);
			} else {
				recordsetSource = src.getRecordset(false, CursorType.STATIC);
			}
			Recordset recordsetReference;
			if (textAreaProximitySQL.getSelectedItem() != null) {
				recordsetReference = srcReference.query(textAreaProximitySQL.getSelectedItem().toString(), CursorType.STATIC);
			} else {
				recordsetReference = srcReference.getRecordset(false, CursorType.STATIC);
			}

			String datasetName = resultDataset.getDatasetName();
			datasetName = resultDataset.getResultDatasource().getDatasets().getAvailableDatasetName(datasetName);

			double min = textNumMin.isEnabled() ? Double.valueOf(textNumMin.getSelectedItem()) : 0;
			double max = textNumMax.isEnabled() && Double.valueOf(textNumMax.getSelectedItem()) > Double.valueOf(textNumMin.getSelectedItem()) ? Double.valueOf(textNumMax.getSelectedItem()) : -1;

			if (comboBoxComputeMethod.getSelectedData() == MIN_DISTANCE) {
				isSuccessful = ProximityAnalyst.computeMinDistance(recordsetSource, recordsetReference, min, max, resultDataset.getResultDatasource(), datasetName);
			} else {
				isSuccessful = ProximityAnalyst.computeRangeDistance(recordsetSource, recordsetReference, min, max, resultDataset.getResultDatasource(), datasetName);
			}
			this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(resultDataset);
			recordsetSource.dispose();
			recordsetReference.dispose();

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
		return MetaKeys.COMPUTEDISTANCE;
	}
}
