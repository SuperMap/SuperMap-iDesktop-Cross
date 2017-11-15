package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.gridAnalyst.calculationTerrain;

import com.supermap.analyst.spatialanalyst.CalculationTerrain;
import com.supermap.analyst.spatialanalyst.CutFillResult;
import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.process.types.DatasetTypes;
import com.supermap.desktop.process.types.Type;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by ChenS on 2017/10/24 0024.
 */
public class MetaProcessCutFillRegion extends MetaProcessCalTerrain {
	private final static String OUTPUT_DATASET = "CutFill";
	private final static String PARAMETER_DATASET = ProcessProperties.getString("String_GroupBox_ReferenceData");

	private ParameterDatasourceConstrained datasourceParameter;
	private ParameterSingleDataset datasetParameter;
	private ParameterNumber numberHeight;
	private ParameterComboBox comboBoxType;
	private ParameterNumber numberRadius;

	public MetaProcessCutFillRegion() {
		setTitle(ProcessProperties.getString("String_Title_CutFillRegion"));
	}

	@Override
	protected void initHook() {
		datasourceParameter = new ParameterDatasourceConstrained();
		datasetParameter = new ParameterSingleDataset(DatasetType.REGION, DatasetType.LINE3D, DatasetType.LINE);
		numberHeight = new ParameterNumber(ControlsProperties.getString("String_SurfaceAnalyst_ViewShed_Unit_AddHeight"));
		comboBoxType = new ParameterComboBox(ProcessProperties.getString("String_Label_BufferType"));
		numberRadius = new ParameterNumber(ProcessProperties.getString("String_Label_BufferRadius"));

		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(this.datasourceParameter, ParameterDatasource.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(this.datasetParameter, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		ParameterCombine parameterCombine = new ParameterCombine();
		parameterCombine.setDescribe(PARAMETER_DATASET);
		parameterCombine.addParameters(datasourceParameter, datasetParameter);

		ParameterCombine bufferCombine = new ParameterCombine();
		bufferCombine.setDescribe(SETTING_PANEL_DESCRIPTION);
		bufferCombine.addParameters(numberHeight, comboBoxType, numberRadius);

		parameterCombineResultDataset.addParameters(parameterSaveDataset);

		parameters.addParameters(parameterCombine, bufferCombine, parameterCombineResultDataset);
		parameters.addInputParameters(PARAMETER_DATASET, Type.instance("").and(DatasetTypes.REGION).and(DatasetTypes.LINE).and(DatasetTypes.LINE3D), parameterCombine);
		parameters.addOutputParameters(OUTPUT_DATASET, ProcessOutputResultProperties.getString("String_Result_CutFill"), DatasetTypes.GRID, parameterCombineResultDataset);


		Dataset defaultDataset = DatasetUtilities.getDefaultDataset(DatasetType.REGION, DatasetType.LINE, DatasetType.LINE3D);
		if (defaultDataset != null) {
			datasourceParameter.setSelectedItem(defaultDataset.getDatasource());
			datasetParameter.setSelectedItem(defaultDataset);
			comboBoxType.setEnabled(!defaultDataset.getType().equals(DatasetType.REGION));
			numberRadius.setEnabled(!defaultDataset.getType().equals(DatasetType.REGION));
		}
		comboBoxType.setItems(new ParameterDataNode(ControlsProperties.getString("String_CheckBox_BufferFlat"), false),
				new ParameterDataNode(ControlsProperties.getString("String_CheckBox_BufferRound"), true));
		numberHeight.setSelectedItem(0);
		numberRadius.setSelectedItem(10);
		numberRadius.setMinValue(0);
		numberRadius.setIsIncludeMin(false);
		parameterSaveDataset.setSelectedItem("result_cutFillRegion");

		datasetParameter.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (sourceDataset.getSelectedItem() != null && evt.getNewValue() instanceof DatasetVector) {
					comboBoxType.setEnabled(!datasetParameter.getSelectedItem().getType().equals(DatasetType.REGION));
					numberRadius.setEnabled(!datasetParameter.getSelectedItem().getType().equals(DatasetType.REGION));
				}
			}
		});
	}

	@Override
	protected boolean doWork(DatasetGrid datasetGrid) {
		boolean isSuccessful = false;
		Recordset recordset = null;
		try {
			DatasetVector datasetVectorParameter;
			if (this.parameters.getInputs().getData(PARAMETER_DATASET).getValue() != null) {
				datasetVectorParameter = (DatasetVector) this.parameters.getInputs().getData(PARAMETER_DATASET).getValue();
			} else {
				datasetVectorParameter = (DatasetVector) datasetParameter.getSelectedDataset();
			}
			CutFillResult result;
			String resultName = parameterSaveDataset.getDatasetName();
			recordset = datasetVectorParameter.getRecordset(false, CursorType.DYNAMIC);
			while (!recordset.isEOF() && !isSuccessful) {
				if (datasetVectorParameter.getType().equals(DatasetType.REGION)) {
					GeoRegion geometry = (GeoRegion) recordset.getGeometry();
					double height = Double.parseDouble(numberHeight.getSelectedItem());
					result = CalculationTerrain.cutFill(datasetGrid, geometry, height, parameterSaveDataset.getResultDatasource(), resultName);
					geometry.dispose();
				} else {
					GeoLine3D geoLine3D = new GeoLine3D();
					if (datasetVectorParameter.getType().equals(DatasetType.LINE3D)) {
						geoLine3D = (GeoLine3D) recordset.getGeometry();
					} else {
						GeoLine geoLine = (GeoLine) recordset.getGeometry();
						Point3Ds[] point3Ds = new Point3Ds[geoLine.getPartCount()];
						for (int i = 0; i < geoLine.getPartCount(); i++) {
							Point2Ds point2Ds = geoLine.getPart(i);
							point3Ds[i] = new Point3Ds();
							for (int j = 0; j < point2Ds.getCount(); j++) {
								point3Ds[i].add(new Point3D(point2Ds.getItem(j).getX(), point2Ds.getItem(j).getY(), 0));
							}
							geoLine3D.addPart(point3Ds[i]);
						}
					}
					double radius = Double.parseDouble(numberRadius.getSelectedItem());
					boolean isRoundHead = (boolean) comboBoxType.getSelectedData();
					result = CalculationTerrain.cutFill(datasetGrid, geoLine3D, radius, isRoundHead, parameterSaveDataset.getResultDatasource(), resultName);
					geoLine3D.dispose();
				}
				isSuccessful = result != null;
				if (isSuccessful) {
					Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_Print_FillVolume") + result.getFillVolume() + CoreProperties.getString("String_VolumnUnit_Meter"));
					Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_Print_CutVolume") + result.getCutVolume() + CoreProperties.getString("String_VolumnUnit_Meter"));
					Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_Print_FillArea") + result.getFillArea() + CoreProperties.getString("String_AreaUnit_Meter"));
					Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_Print_CutArea") + result.getCutArea() + CoreProperties.getString("String_AreaUnit_Meter"));
					Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_Print_RemainderArea") + result.getRemainderArea() + CoreProperties.getString("String_AreaUnit_Meter"));
					this.getParameters().getOutputs().getData(OUTPUT_DATASET).setValue(result.getCutFillGridResult());
				} else {
					recordset.moveNext();
				}
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		} finally {
			recordset.dispose();
		}

		return isSuccessful;
	}

	@Override
	protected String getDefaultResultName() {
		return "result_cutFillRegion";
	}

	@Override
	public String getKey() {
		return MetaKeys.CUT_FILL_REGION;
	}
}
