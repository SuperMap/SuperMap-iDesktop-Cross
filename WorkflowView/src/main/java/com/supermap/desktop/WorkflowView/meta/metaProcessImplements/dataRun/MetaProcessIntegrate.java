package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.dataRun;

import com.supermap.analyst.spatialanalyst.Generalization;
import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.enums.LengthUnit;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.process.types.DatasetTypes;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by lixiaoyao on 2017/10/17.
 */
public class MetaProcessIntegrate extends MetaProcess {
	private static final String INPUT_DATA = CoreProperties.getString("String_GroupBox_SourceData");
	private final static String OUTPUT_DATA = "IntegrateResult";

	private ParameterDatasourceConstrained sourceDatasource;
	private ParameterSingleDataset sourceDataset;
	private ParameterNumber numberTolerance;
	private ParameterComboBox comboBoxUnit;

	private ParameterDataNode dataNodeMiliMeter = new ParameterDataNode(LengthUnit.convertForm(Unit.MILIMETER).toString(), Unit.MILIMETER);
	private ParameterDataNode dataNodeCentiMeter = new ParameterDataNode(LengthUnit.convertForm(Unit.CENTIMETER).toString(), Unit.CENTIMETER);
	private ParameterDataNode dataNodeDeciMeter = new ParameterDataNode(LengthUnit.convertForm(Unit.DECIMETER).toString(), Unit.DECIMETER);
	private ParameterDataNode dataNodeMeter = new ParameterDataNode(LengthUnit.convertForm(Unit.METER).toString(), Unit.METER);
	private ParameterDataNode dataNodeKiloMeter = new ParameterDataNode(LengthUnit.convertForm(Unit.KILOMETER).toString(), Unit.KILOMETER);
	private ParameterDataNode dataNodeInch = new ParameterDataNode(LengthUnit.convertForm(Unit.INCH).toString(), Unit.INCH);
	private ParameterDataNode dataNodeFoot = new ParameterDataNode(LengthUnit.convertForm(Unit.FOOT).toString(), Unit.FOOT);
	private ParameterDataNode dataNodeYard = new ParameterDataNode(LengthUnit.convertForm(Unit.YARD).toString(), Unit.YARD);
	private ParameterDataNode dataNodeMile = new ParameterDataNode(LengthUnit.convertForm(Unit.MILE).toString(), Unit.MILE);
	private ParameterDataNode dataNodeSecond = new ParameterDataNode(LengthUnit.convertForm(Unit.SECOND).toString(), Unit.SECOND);
	private ParameterDataNode dataNodeMinute = new ParameterDataNode(LengthUnit.convertForm(Unit.MINUTE).toString(), Unit.MINUTE);
	private ParameterDataNode dataNodeDegree = new ParameterDataNode(LengthUnit.convertForm(Unit.DEGREE).toString(), Unit.DEGREE);
	private ParameterDataNode dataNodeRadian = new ParameterDataNode(LengthUnit.convertForm(Unit.RADIAN).toString(), Unit.RADIAN);

	public MetaProcessIntegrate() {
		setTitle(ProcessProperties.getString("String_IntegrateTitle"));
		initParameters();
		initParameterConstraint();
		initParametersState();
		registerListener();
	}

	private void initParameters() {
		this.sourceDatasource = new ParameterDatasourceConstrained();
		this.sourceDatasource.setReadOnlyNeeded(false);
		this.sourceDatasource.setDescribe(CoreProperties.getString("String_Label_Datasource"));
		this.sourceDataset = new ParameterSingleDataset(DatasetType.POINT, DatasetType.LINE, DatasetType.REGION);
		this.sourceDataset.setDescribe(CoreProperties.getString("String_Label_Dataset"));
		this.sourceDataset.setRequisite(true);
		this.numberTolerance = new ParameterNumber(ProcessProperties.getString("String_IntegrateTolerance"));
		this.numberTolerance.setRequisite(true);
		this.comboBoxUnit = new ParameterComboBox(ProcessProperties.getString("Label_BufferRadius"));
		this.comboBoxUnit.setRequisite(true);
		changeUnit(null);

		ParameterCombine sourceData = new ParameterCombine();
		sourceData.setDescribe(CoreProperties.getString("String_GroupBox_SourceData"));
		sourceData.addParameters(this.sourceDatasource, this.sourceDataset);
		ParameterCombine parameterSetting = new ParameterCombine();
		parameterSetting.setDescribe(CoreProperties.getString("String_GroupBox_ParamSetting"));
		parameterSetting.addParameters(this.numberTolerance, this.comboBoxUnit);
		this.parameters.setParameters(sourceData, parameterSetting);
		this.parameters.addInputParameters(INPUT_DATA, DatasetTypes.SIMPLE_VECTOR, sourceData);
		this.parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_IntegrateResult"), DatasetTypes.SIMPLE_VECTOR, sourceData);
	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint constraintSource = new EqualDatasourceConstraint();
		constraintSource.constrained(sourceDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		constraintSource.constrained(sourceDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);
	}

	private void initParametersState() {
		Dataset dataset = DatasetUtilities.getDefaultDataset(DatasetType.POINT, DatasetType.LINE, DatasetType.REGION);
		this.numberTolerance.setSelectedItem(0.00001);
		if (dataset != null) {
			this.sourceDatasource.setSelectedItem(dataset.getDatasource());
			this.sourceDataset.setSelectedItem(dataset);
			this.numberTolerance.setSelectedItem(DatasetUtilities.getDefaultTolerance((DatasetVector) dataset).getNodeSnap());
			changeUnit((DatasetVector) dataset);
		}
		this.numberTolerance.setMinValue(0);
		this.numberTolerance.setIsIncludeMin(false);
	}

	private void registerListener() {
		this.sourceDataset.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (sourceDataset.getSelectedItem() != null && evt.getNewValue() instanceof Dataset) {
					numberTolerance.setSelectedItem(DatasetUtilities.getDefaultTolerance((DatasetVector) evt.getNewValue()).getNodeSnap());
					changeUnit((DatasetVector) evt.getNewValue());
				}
			}
		});
	}

	private void changeUnit(DatasetVector datasetVector) {
		this.comboBoxUnit.removeAllItems();
		if (datasetVector == null || datasetVector.getPrjCoordSys().getType() == PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
			this.comboBoxUnit.setItems(this.dataNodeMiliMeter, this.dataNodeCentiMeter, this.dataNodeDeciMeter,
					this.dataNodeMeter, this.dataNodeKiloMeter, this.dataNodeInch, this.dataNodeFoot,
					this.dataNodeYard, this.dataNodeMile, this.dataNodeSecond, this.dataNodeMinute, this.dataNodeDegree, this.dataNodeRadian);
		} else if (datasetVector.getPrjCoordSys().getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
			this.comboBoxUnit.setItems(this.dataNodeMiliMeter, this.dataNodeCentiMeter, this.dataNodeDeciMeter,
					this.dataNodeMeter, this.dataNodeKiloMeter, this.dataNodeInch, this.dataNodeFoot,
					this.dataNodeYard, this.dataNodeMile);
		}
		if (datasetVector != null) {
			this.comboBoxUnit.setSelectedItem(datasetVector.getPrjCoordSys().getCoordUnit());
		}
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		try {
			DatasetVector src = null;
			if (this.getParameters().getInputs().getData(INPUT_DATA).getValue() != null) {
				src = (DatasetVector) this.getParameters().getInputs().getData(INPUT_DATA).getValue();
			} else {
				src = (DatasetVector) sourceDataset.getSelectedItem();
			}
			Generalization.addSteppedListener(steppedListener);
			isSuccessful = Generalization.integrate(src, Double.valueOf(this.numberTolerance.getSelectedItem().toString()),
					(Unit) this.comboBoxUnit.getSelectedData());
			this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(src);

		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			Generalization.removeSteppedListener(steppedListener);
		}
		return isSuccessful;
	}

	@Override
	public IParameters getParameters() {
		return parameters;
	}

	@Override
	public String getKey() {
		return MetaKeys.INTEGRATE;
	}
}
