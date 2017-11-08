package com.supermap.desktop.WorkflowView.meta.metaProcessImplements;

import com.supermap.analyst.spatialanalyst.DensityAnalyst;
import com.supermap.analyst.spatialanalyst.DensityAnalystParameter;
import com.supermap.analyst.spatialanalyst.NeighbourShape;
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
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;
import com.supermap.desktop.utilities.DoubleUtilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created By Chens on 2017/8/22 0022
 * 点密度（本地）
 */
public class MetaProcessSimpleDensityOffline extends MetaProcess {
	private final static String INPUT_DATA = SOURCE_PANEL_DESCRIPTION;
	private final static String OUTPUT_DATA = "SimpleDensityOfflineResult";

	private ParameterDatasourceConstrained sourceDatasource;
	private ParameterSingleDataset sourceDataset;
	private ParameterSaveDataset resultDataset;
	private ParameterFieldComboBox comboBoxField;
	private ParameterShapeType shapeType;
	private ParameterNumber numberTop;
	private ParameterNumber numberBottom;
	private ParameterNumber numberRight;
	private ParameterNumber numberLeft;
	private ParameterNumber numberCellSize;

	private PropertyChangeListener topListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			numberBottom.setMaxValue(Double.parseDouble(numberTop.getSelectedItem()));
		}
	};
	private PropertyChangeListener bottomListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			numberTop.setMinValue(Double.parseDouble(numberBottom.getSelectedItem()));
		}
	};
	private PropertyChangeListener leftListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			numberRight.setMinValue(Double.parseDouble(numberLeft.getSelectedItem()));
		}
	};
	private PropertyChangeListener rightListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			numberLeft.setMaxValue(Double.parseDouble(numberRight.getSelectedItem()));
		}
	};

	public MetaProcessSimpleDensityOffline() {
		setTitle(ProcessProperties.getString("String_SimpleDensityAnalyst"));
		initParameters();
		initParameterConstraint();
		initParametersState();
		registerListener();
	}

	private void initParameters() {
		sourceDatasource = new ParameterDatasourceConstrained();
		sourceDataset = new ParameterSingleDataset(DatasetType.POINT);
		resultDataset = new ParameterSaveDataset();
		comboBoxField = new ParameterFieldComboBox(ProcessProperties.getString("String_DensityAnalyst_DensityField"));
		shapeType = new ParameterShapeType();
		numberCellSize = new ParameterNumber(CoreProperties.getString("String_Resolution"));
		numberTop = new ParameterNumber(ControlsProperties.getString("String_LabelTop"));
		numberBottom = new ParameterNumber(ControlsProperties.getString("String_LabelBottom"));
		numberRight = new ParameterNumber(ControlsProperties.getString("String_LabelRight"));
		numberLeft = new ParameterNumber(ControlsProperties.getString("String_LabelLeft"));

		ParameterCombine sourceCombine = new ParameterCombine();
		sourceCombine.setDescribe(CoreProperties.getString("String_GroupBox_SourceData"));
		sourceCombine.addParameters(sourceDatasource, sourceDataset);
		ParameterCombine settingCombine = new ParameterCombine();
		settingCombine.setDescribe(ProcessProperties.getString("String_setParameter"));
		settingCombine.addParameters(comboBoxField, numberLeft, numberBottom, numberRight, numberTop, numberCellSize, shapeType);
		ParameterCombine resultCombine = new ParameterCombine();
		resultCombine.setDescribe(CoreProperties.getString("String_GroupBox_ResultData"));
		resultCombine.addParameters(resultDataset);

		parameters.setParameters(sourceCombine, settingCombine, resultCombine);
		this.parameters.addInputParameters(INPUT_DATA, DatasetTypes.POINT, sourceCombine);
		this.parameters.addOutputParameters(OUTPUT_DATA,
				ProcessOutputResultProperties.getString("String_SimpleDensityAnalysisResult"),
				DatasetTypes.GRID, resultCombine);
	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint constraintSource = new EqualDatasourceConstraint();
		constraintSource.constrained(sourceDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		constraintSource.constrained(sourceDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);
		EqualDatasetConstraint equalDatasetConstraint = new EqualDatasetConstraint();
		equalDatasetConstraint.constrained(sourceDataset, ParameterSingleDataset.DATASET_FIELD_NAME);
		equalDatasetConstraint.constrained(comboBoxField, ParameterFieldComboBox.DATASET_FIELD_NAME);
		EqualDatasetConstraint equalDatasetConstraint1 = new EqualDatasetConstraint();
		equalDatasetConstraint1.constrained(sourceDataset, ParameterSingleDataset.DATASET_FIELD_NAME);
		equalDatasetConstraint1.constrained(shapeType, ParameterShapeType.DATASET_FIELD_NAME);
		DatasourceConstraint.getInstance().constrained(resultDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);
	}

	private void initParametersState() {
		numberLeft.setSelectedItem(0);
		numberRight.setSelectedItem(0);
		numberTop.setSelectedItem(0);
		numberBottom.setSelectedItem(0);
		Dataset dataset = DatasetUtilities.getDefaultDataset(DatasetType.POINT);
		if (dataset != null) {
			sourceDatasource.setSelectedItem(dataset.getDatasource());
			sourceDataset.setSelectedItem(dataset);
			comboBoxField.setFieldName((DatasetVector) dataset);
			updateBound(dataset);
		}
		comboBoxField.setFieldType(fieldType);
		comboBoxField.setShowSystemField(true);
		comboBoxField.setSelectedItem("SmID");
		resultDataset.setDefaultDatasetName("result_simpleDensity");
		numberRight.setMinValue(Double.parseDouble(numberLeft.getSelectedItem()));
		numberLeft.setMaxValue(Double.parseDouble(numberRight.getSelectedItem()));
		numberTop.setMinValue(Double.parseDouble(numberBottom.getSelectedItem()));
		numberBottom.setMaxValue(Double.parseDouble(numberTop.getSelectedItem()));
	}

	private void registerListener() {
		sourceDataset.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (sourceDataset.getSelectedItem() != null && evt.getNewValue() instanceof Dataset) {
					updateBound((Dataset) evt.getNewValue());
					comboBoxField.setSelectedItem("SmID");
				}
			}
		});
		registerBTRLListener();
	}

	private void registerBTRLListener() {
		numberBottom.addPropertyListener(bottomListener);
		numberTop.addPropertyListener(topListener);
		numberRight.addPropertyListener(rightListener);
		numberLeft.addPropertyListener(leftListener);
	}

	private void removeBTRLListener() {
		numberBottom.removePropertyListener(bottomListener);
		numberTop.removePropertyListener(topListener);
		numberRight.removePropertyListener(rightListener);
		numberLeft.removePropertyListener(leftListener);
	}

	private void updateBound(Dataset dataset) {
		removeBTRLListener();
		Rectangle2D rectangle2D = dataset.getBounds();
		numberTop.setMinValue(rectangle2D.getBottom());
		numberBottom.setMaxValue(rectangle2D.getTop());
		numberLeft.setMaxValue(rectangle2D.getRight());
		numberRight.setMinValue(rectangle2D.getLeft());
		numberBottom.setSelectedItem(DoubleUtilities.getFormatString(rectangle2D.getBottom()));
		numberLeft.setSelectedItem(DoubleUtilities.getFormatString(rectangle2D.getLeft()));
		numberRight.setSelectedItem(DoubleUtilities.getFormatString(rectangle2D.getRight()));
		numberTop.setSelectedItem(DoubleUtilities.getFormatString(rectangle2D.getTop()));
		Double x = rectangle2D.getWidth() / 500;
		Double y = rectangle2D.getHeight() / 500;
		Double cellSize = x > y ? y : x;
		numberCellSize.setSelectedItem(DoubleUtilities.getFormatString(cellSize));
		registerBTRLListener();
	}

	@Override
	public IParameters getParameters() {
		return super.getParameters();
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		DensityAnalystParameter densityAnalystParameter = new DensityAnalystParameter();
		try {
			DatasetVector src = null;
			if (parameters.getInputs().getData(INPUT_DATA).getValue() != null) {
				src = (DatasetVector) parameters.getInputs().getData(INPUT_DATA).getValue();
			} else {
				src = (DatasetVector) sourceDataset.getSelectedItem();
			}
			DensityAnalyst.addSteppedListener(steppedListener);
			double top = Double.parseDouble(numberTop.getSelectedItem());
			double bottom = Double.parseDouble(numberBottom.getSelectedItem());
			double right = Double.parseDouble(numberRight.getSelectedItem());
			double left = Double.parseDouble(numberLeft.getSelectedItem());
			densityAnalystParameter.setBounds(new Rectangle2D(left, bottom, right, top));
			densityAnalystParameter.setResolution(Double.parseDouble(numberCellSize.getSelectedItem()));
			densityAnalystParameter.setSearchNeighbourhood((NeighbourShape) shapeType.getSelectedItem());
			DatasetGrid result = DensityAnalyst.pointDensity(densityAnalystParameter, src, comboBoxField.getFieldName(),
					resultDataset.getResultDatasource(), resultDataset.getResultDatasource().getDatasets().getAvailableDatasetName(resultDataset.getDatasetName()));
			isSuccessful = result != null;
			this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(result);
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			DensityAnalyst.removeSteppedListener(steppedListener);
			densityAnalystParameter.dispose();
		}
		return isSuccessful;
	}

	@Override
	public String getKey() {
		return MetaKeys.SIMPLE_DENSITY_OFFLINE;
	}
}
