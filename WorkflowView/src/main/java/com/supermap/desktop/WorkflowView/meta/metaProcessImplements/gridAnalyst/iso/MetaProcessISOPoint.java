package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.gridAnalyst.iso;

import com.supermap.analyst.spatialanalyst.SmoothMethod;
import com.supermap.analyst.spatialanalyst.SurfaceAnalyst;
import com.supermap.analyst.spatialanalyst.SurfaceExtractParameter;
import com.supermap.analyst.spatialanalyst.TerrainInterpolateType;
import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasetConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.events.RunningEvent;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.interfaces.AbstractParameter;
import com.supermap.desktop.process.parameter.interfaces.IParameterPanel;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;
import com.supermap.desktop.utilities.DoubleUtilities;
import com.supermap.desktop.utilities.StringUtilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by xie on 2017/3/10.
 */
public class MetaProcessISOPoint extends MetaProcess {
	private final static String INPUT_DATA = CoreProperties.getString("String_GroupBox_SourceData");
	private final static String OUTPUT_DATA = "ExtractResult";

	private ParameterDatasourceConstrained sourceDatasource;
	private ParameterSingleDataset sourceDataset;
	private ParameterFieldComboBox fields;
	private ParameterSaveDataset targetDataset;
	private ParameterTextField maxISOLine;
	private ParameterTextField minISOLine;
	private ParameterTextField isoLine;
	private ParameterComboBox terrainInterpolateType;
	private ParameterNumber resolution;
	private ParameterNumber datumValue;
	private ParameterNumber interval;
	private ParameterNumber resampleTolerance;
	private ParameterComboBox smoothMethod;
	private ParameterNumber smoothness;
	private boolean isSelectChanged = false;
	private SteppedListener stepListener = new SteppedListener() {
		@Override
		public void stepped(SteppedEvent steppedEvent) {
			RunningEvent event = new RunningEvent(MetaProcessISOPoint.this, steppedEvent.getPercent(), AbstractParameter.PROPERTY_VALE);
			fireRunning(event);

			if (event.isCancel()) {
				steppedEvent.setCancel(true);
			}
		}
	};

	public MetaProcessISOPoint() {
		setTitle(CoreProperties.getString("String_SurfaceISOPoint"));
		initParameters();
		initParameterConstraint();
		initParametersState();
		initParametersListener();
	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(sourceDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(sourceDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);
		EqualDatasetConstraint equalDatasetConstraint = new EqualDatasetConstraint();
		equalDatasetConstraint.constrained(sourceDataset, ParameterSingleDataset.DATASET_FIELD_NAME);
		equalDatasetConstraint.constrained(fields, ParameterFieldComboBox.DATASET_FIELD_NAME);
		DatasourceConstraint.getInstance().constrained(targetDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);
	}

	private void initParametersState() {
		this.sourceDatasource.setDescribe(CoreProperties.getString("String_Label_Datasource"));
		this.targetDataset.setDefaultDatasetName("result_ISOPoint");
		Dataset datasetVector = DatasetUtilities.getDefaultDataset(DatasetType.POINT, DatasetType.POINT3D);
		if (datasetVector != null) {
			sourceDatasource.setSelectedItem(datasetVector.getDatasource());
			sourceDataset.setSelectedItem(datasetVector);
			targetDataset.setResultDatasource(datasetVector.getDatasource());
			targetDataset.setSelectedItem(datasetVector.getDatasource().getDatasets().getAvailableDatasetName("result_ISOPoint"));
			fields.setFieldName((DatasetVector) datasetVector);
			if (datasetVector.getType().equals(DatasetType.POINT3D)) {
				fields.setEnabled(false);
				fields.setShowSystemField(true);
				fields.setSelectedItem("SmZ");
			}
			reloadValue();
		}
		ParameterDataNode selectedInterpolateType = new ParameterDataNode(CoreProperties.getString("String_TerrainInterpolateType_IDW"), TerrainInterpolateType.IDW);
		this.terrainInterpolateType.setItems(selectedInterpolateType,
				new ParameterDataNode(CoreProperties.getString("String_TerrainInterpolateType_Kriging"), TerrainInterpolateType.KRIGING),
				new ParameterDataNode(CoreProperties.getString("String_TerrainInterpolateType_TIN"), TerrainInterpolateType.TIN));
		ParameterDataNode selectedSmoothNode = new ParameterDataNode(CoreProperties.getString("String_SmoothMethod_NONE"), SmoothMethod.NONE);
		this.smoothMethod.setItems(selectedSmoothNode,
				new ParameterDataNode(CoreProperties.getString("String_SmoothMethod_BSLine"), SmoothMethod.BSPLINE),
				new ParameterDataNode(CoreProperties.getString("String_SmoothMethod_POLISH"), SmoothMethod.POLISH));
		this.smoothMethod.setSelectedItem(selectedSmoothNode);
		this.smoothness.setEnabled(false);
		maxISOLine.setEnabled(false);
		minISOLine.setEnabled(false);
		isoLine.setEnabled(false);
	}

	private void reloadValue() {
		Dataset dataset = sourceDataset.getSelectedDataset();
		if (dataset != null) {
			double resolution = getResolution(dataset);
			this.resolution.setSelectedItem(DoubleUtilities.getFormatString(resolution));
			double maxValue = 0;
			double minValue = 0;
			Recordset recordset = ((DatasetVector) dataset).getRecordset(false, CursorType.STATIC);
			FieldInfos fieldInfos = recordset.getFieldInfos();
			for (int i = 0; i < fieldInfos.getCount(); i++) {
				if (fieldInfos.get(i).getName().equals(fields.getFieldName())) {
					maxValue = recordset.statistic(fieldInfos.get(i).getName(), StatisticMode.MAX);
					minValue = recordset.statistic(fieldInfos.get(i).getName(), StatisticMode.MIN);
					break;
				}
			}
			double dSpan = getDefaultInterval(maxValue, minValue);
			interval.setSelectedItem(dSpan);
			double baseValue = Double.valueOf(datumValue.getSelectedItem());
			double lineDistance = Double.valueOf(interval.getSelectedItem());
			double dRemain = baseValue % lineDistance;
			double maxIsoValue = Math.round((maxValue - dRemain) / lineDistance) * lineDistance + dRemain;
			double minIsoValue = Math.ceil((minValue - dRemain) / lineDistance) * lineDistance + dRemain;
			int isoCount = (int) ((maxIsoValue - minIsoValue) / lineDistance) + 1;
			maxISOLine.setSelectedItem(DoubleUtilities.getFormatString(maxIsoValue));
			minISOLine.setSelectedItem(DoubleUtilities.getFormatString(minIsoValue));
			isoLine.setSelectedItem(String.valueOf(isoCount));
		}
	}

	private void reload() {
		Dataset dataset = sourceDataset.getSelectedDataset();
		String fieldName = fields.getFieldName();
		if (dataset != null && !StringUtilities.isNullOrEmpty(fieldName)) {
			double maxValue = 0;
			double minValue = 0;
			Recordset recordset = ((DatasetVector) dataset).getRecordset(false, CursorType.STATIC);
			if (recordset != null && recordset.getFieldCount() > 0) {
				maxValue = recordset.statistic(fieldName, StatisticMode.MAX);
				minValue = recordset.statistic(fieldName, StatisticMode.MIN);
			}
			double baseValue = Double.valueOf(datumValue.getSelectedItem());
			double lineDistance = Double.valueOf(interval.getSelectedItem());
			double dRemain = baseValue % lineDistance;
			double maxIsoValue = Math.round((maxValue - dRemain) / lineDistance) * lineDistance + dRemain;
			double minIsoValue = Math.ceil((minValue - dRemain) / lineDistance) * lineDistance + dRemain;
			int isoCount = (int) ((maxIsoValue - minIsoValue) / lineDistance) + 1;
			maxISOLine.setSelectedItem(DoubleUtilities.getFormatString(maxIsoValue));
			minISOLine.setSelectedItem(DoubleUtilities.getFormatString(minIsoValue));
			isoLine.setSelectedItem(String.valueOf(isoCount));
		}
	}

	private double getResolution(Dataset dataset) {
		double resolution = 1.0;
		if (dataset != null && dataset.getBounds() != null && dataset.getBounds().getHeight() > 0 && dataset.getBounds().getWidth() > 0) {
			Rectangle2D bounds = dataset.getBounds();
			double x = bounds.getWidth() / 500.0;
			double y = bounds.getHeight() / 500.0;
			resolution = x > y ? x : y;
		}
		return resolution;

	}

	private double getDefaultInterval(double maxValue, double minValue) {
		double result = 1;
		try {
			double dCount = 12;
			Double dValue = maxValue - minValue;
			if (dValue > 0) {
				double dSpan = dValue / dCount;
				if (dSpan > 1 && dSpan <= 10) {
					result = (int) dSpan;
				} else if (dSpan > 10 && dSpan <= 100) {
					result = (int) (dSpan / 10) * 10;
				} else if (dSpan > 100) {
					result = (int) (dSpan / 100) * 100;
				} else {
					result = dSpan;
				}
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
		return result;
	}

	private void initParameters() {
		this.sourceDatasource = new ParameterDatasourceConstrained();
		this.sourceDataset = new ParameterSingleDataset(DatasetType.POINT, DatasetType.POINT3D);
		this.fields = new ParameterFieldComboBox();
		this.fields.setDescribe(CoreProperties.getString("String_FieldsName"));
		FieldType[] fieldType = {FieldType.INT16, FieldType.INT32, FieldType.INT64, FieldType.SINGLE, FieldType.DOUBLE};
		this.fields.setFieldType(fieldType);
		this.fields.setEditable(true);
		this.targetDataset = new ParameterSaveDataset();
		this.maxISOLine = new ParameterTextField(CoreProperties.getString("String_MAXISOLine"));
		this.minISOLine = new ParameterTextField(CoreProperties.getString("String_MINISOLine"));
		this.isoLine = new ParameterTextField(CoreProperties.getString("String_ISOData"));
		this.terrainInterpolateType = new ParameterComboBox(CoreProperties.getString("String_InterpolateType"));
		this.resolution = new ParameterNumber(CoreProperties.getString("String_Resolution"));
		resolution.setMinValue(0);
		resolution.setIsIncludeMin(false);
		this.datumValue = new ParameterNumber(CoreProperties.getString("String_DatumValue"));
		this.datumValue.setSelectedItem("0");
		datumValue.setMinValue(0);
		datumValue.setIsIncludeMin(true);
		this.interval = new ParameterNumber(CoreProperties.getString("String_Interval"));
		interval.setMinValue(0);
		interval.setIsIncludeMin(false);
		this.resampleTolerance = new ParameterNumber(CoreProperties.getString("String_ResampleTolerance"));
		this.resampleTolerance.setSelectedItem("0");
		resampleTolerance.setMinValue(0);
		resampleTolerance.setIsIncludeMin(true);
		this.smoothMethod = new ParameterComboBox().setDescribe(CoreProperties.getString("String_SmoothMethod"));
		this.smoothness = new ParameterNumber(CoreProperties.getString("String_SmoothNess"));
		smoothness.setMinValue(2);
		smoothness.setMaxValue(5);
		smoothness.setMaxBit(0);
		this.smoothness.setSelectedItem("2");

		ParameterCombine sourceData = new ParameterCombine();
		sourceData.setDescribe(CoreProperties.getString("String_GroupBox_SourceData"));
		sourceData.addParameters(sourceDatasource, sourceDataset);
		ParameterCombine resultData = new ParameterCombine();
		resultData.setDescribe(CoreProperties.getString("String_GroupBox_ResultData"));
		resultData.addParameters(targetDataset, maxISOLine, minISOLine, isoLine);
		ParameterCombine paramSet = new ParameterCombine();
		paramSet.setDescribe(CoreProperties.getString("String_FormEdgeCount_Text"));
		paramSet.addParameters(fields, terrainInterpolateType, resolution, datumValue, interval,
				resampleTolerance, smoothMethod, smoothness);

		this.parameters.setParameters(sourceData, paramSet, resultData);
		this.parameters.addInputParameters(INPUT_DATA, DatasetTypes.POINT, sourceData);
		this.parameters.addOutputParameters(OUTPUT_DATA,
				ProcessOutputResultProperties.getString("String_SurfaceAnalyst_ISOLineResult"),
				DatasetTypes.LINE, resultData);
	}

	private void initParametersListener() {
		smoothMethod.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(ParameterComboBox.comboBoxValue)) {
					smoothness.setEnabled(smoothMethod.getSelectedIndex() != 0);
				}
			}
		});

		sourceDataset.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (!isSelectChanged && evt.getPropertyName().equals(ParameterSingleDataset.DATASET_FIELD_NAME)) {
					isSelectChanged = true;
					reloadValue();
					isSelectChanged = false;
				}
				if (sourceDataset.getSelectedItem() != null && evt.getNewValue() instanceof DatasetVector) {
					fields.setEnabled(true);
					fields.setShowSystemField(false);
					fields.setSelectedItem("SmUserID");
					if (((DatasetVector) evt.getNewValue()).getType().equals(DatasetType.POINT3D)) {
						fields.setEnabled(false);
						fields.setShowSystemField(true);
						fields.setSelectedItem("SmZ");
					}
				}
			}
		});
		datumValue.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (!isSelectChanged && evt.getPropertyName().equals(ParameterTextField.PROPERTY_VALE)) {
					isSelectChanged = true;
					reload();
					isSelectChanged = false;
				}
			}
		});
		interval.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (!isSelectChanged && evt.getPropertyName().equals(ParameterTextField.PROPERTY_VALE)) {
					isSelectChanged = true;
					reload();
					isSelectChanged = false;
				}
			}
		});
		fields.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (!isSelectChanged && evt.getPropertyName().equals(ParameterFieldComboBox.PROPERTY_VALE)) {
					isSelectChanged = true;
					reloadValue();
					isSelectChanged = false;
				}
			}
		});
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;

		try {
			SurfaceExtractParameter surfaceExtractParameter = new SurfaceExtractParameter();
			surfaceExtractParameter.setDatumValue(Double.valueOf(datumValue.getSelectedItem()));
			surfaceExtractParameter.setInterval(Double.valueOf(interval.getSelectedItem()));
			surfaceExtractParameter.setResampleTolerance(Double.valueOf(resampleTolerance.getSelectedItem()));
			surfaceExtractParameter.setSmoothMethod((SmoothMethod) ((ParameterDataNode) smoothMethod.getSelectedItem()).getData());
			surfaceExtractParameter.setSmoothness(Integer.valueOf(smoothness.getSelectedItem()));
			SurfaceAnalyst.addSteppedListener(this.stepListener);

			DatasetVector src;
			if (this.getParameters().getInputs().getData(INPUT_DATA).getValue() != null) {
				src = (DatasetVector) this.getParameters().getInputs().getData(INPUT_DATA).getValue();
			} else {
				src = (DatasetVector) sourceDataset.getSelectedItem();
			}
			DatasetVector result = null;
			if (src.getType().equals(DatasetType.POINT)) {
				result = SurfaceAnalyst.extractIsoline(surfaceExtractParameter, src, fields.getFieldName(), targetDataset.getResultDatasource(),
						targetDataset.getResultDatasource().getDatasets().getAvailableDatasetName(targetDataset.getDatasetName()),
						Double.valueOf(resolution.getSelectedItem()), null);
			} else {
				Point3Ds point3Ds = new Point3Ds();
				Recordset recordset = src.getRecordset(false, CursorType.DYNAMIC);
				while (!recordset.isEOF()) {
					GeoPoint3D geoPoint3D = (GeoPoint3D) recordset.getGeometry();
					point3Ds.add(new Point3D(geoPoint3D.getX(), geoPoint3D.getY(), geoPoint3D.getZ()));
					geoPoint3D.dispose();
					recordset.moveNext();
				}
				if (point3Ds.getCount() > 0) {
					result = SurfaceAnalyst.extractIsoline(surfaceExtractParameter, point3Ds, targetDataset.getResultDatasource(),
							targetDataset.getResultDatasource().getDatasets().getAvailableDatasetName(targetDataset.getDatasetName()),
							Double.valueOf(resolution.getSelectedItem()), null);
				}
			}
			this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(result);
			isSuccessful = (result != null);
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			SurfaceAnalyst.removeSteppedListener(this.stepListener);
		}
		return isSuccessful;
	}

	@Override
	public String getKey() {
		return MetaKeys.ISOPOINT;
	}

	@Override
	public IParameterPanel getComponent() {
		return parameters.getPanel();
	}

}
