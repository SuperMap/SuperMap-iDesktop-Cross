package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.dataRun;

import com.supermap.analyst.spatialanalyst.GeneralizeAnalyst;
import com.supermap.analyst.spatialanalyst.ResampleMode;
import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.properties.CommonProperties;
import com.supermap.desktop.utilities.DatasetUtilities;
import com.supermap.desktop.utilities.ResampleModeUtilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

/**
 * Created By Chens on 2017/8/14 0014
 * 栅格重采样
 */
public class MetaProcessGridResample extends MetaProcess {
	private final static String INPUT_DATA = SOURCE_PANEL_DESCRIPTION;
	private final static String OUTPUT_DATA = "GridResampleResult";

	private ParameterDatasourceConstrained sourceDatasource;
	private ParameterSingleDataset sourceDataset;
	private ParameterSaveDataset resultDataset;
	private ParameterTextField textFieldSourceXPixel;
	private ParameterTextField textFieldSourceYPixel;
	private ParameterTextField textFieldSourceRow;
	private ParameterTextField textFieldSourceColumn;
	private ParameterComboBox comboBoxMethod;
	private ParameterNumber numberPixel;
	private ParameterTextField textFieldRow;
	private ParameterTextField textFieldColumn;


	public MetaProcessGridResample() {
		setTitle(ProcessProperties.getString("String_Title_GridResample"));
		initParameters();
		initParameterConstraint();
		initParametersState();
		registerListener();
	}

	private void initParameters() {
		sourceDatasource = new ParameterDatasourceConstrained();
		sourceDataset = new ParameterSingleDataset(DatasetType.GRID, DatasetType.IMAGE);
		resultDataset = new ParameterSaveDataset();
		textFieldSourceXPixel = new ParameterTextField(ControlsProperties.getString("String_LabelXPixelFormat"));
		textFieldSourceYPixel = new ParameterTextField(ControlsProperties.getString("String_LabelYPixelFormat"));
		textFieldSourceRow = new ParameterTextField(CommonProperties.getString("String_Row"));
		textFieldSourceColumn = new ParameterTextField(CommonProperties.getString("String_Column"));
		comboBoxMethod = new ParameterComboBox(ProcessProperties.getString("String_Label_ResampleMethod"));
		numberPixel = new ParameterNumber(CommonProperties.getString("String_Resolution"));
		textFieldRow = new ParameterTextField(CommonProperties.getString("String_Row"));
		textFieldColumn = new ParameterTextField(CommonProperties.getString("String_Column"));

		ParameterCombine sourceCombine = new ParameterCombine();
		sourceCombine.setDescribe(CommonProperties.getString("String_GroupBox_SourceData"));
		sourceCombine.addParameters(sourceDatasource, sourceDataset);
		ParameterCombine infoCombine = new ParameterCombine();
		infoCombine.setDescribe(ProcessProperties.getString("String_GroupBox_SourceInfo"));
		infoCombine.addParameters(textFieldSourceXPixel, textFieldSourceYPixel, textFieldSourceRow, textFieldSourceColumn);
		ParameterCombine settingCombine = new ParameterCombine();
		settingCombine.setDescribe(ProcessProperties.getString("String_setParameter"));
		settingCombine.addParameters(comboBoxMethod, numberPixel, textFieldRow, textFieldColumn);
		ParameterCombine resultCombine = new ParameterCombine();
		resultCombine.setDescribe(CommonProperties.getString("String_GroupBox_ResultData"));
		resultCombine.addParameters(resultDataset);

		parameters.setParameters(sourceCombine, infoCombine, settingCombine, resultCombine);
		this.parameters.addInputParameters(INPUT_DATA, DatasetTypes.ALL_RASTER, sourceCombine);
		this.parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_Result_Resample"), DatasetTypes.ALL_RASTER, resultCombine);
	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint constraintSource = new EqualDatasourceConstraint();
		constraintSource.constrained(sourceDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		constraintSource.constrained(sourceDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);
		DatasourceConstraint.getInstance().constrained(resultDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);
	}

	private void initParametersState() {
		this.resultDataset.setDefaultDatasetName("result_gridResample");
		Dataset dataset = DatasetUtilities.getDefaultDataset(DatasetType.GRID, DatasetType.IMAGE);
		if (dataset != null) {
			sourceDatasource.setSelectedItem(dataset.getDatasource());
			sourceDataset.setSelectedItem(dataset);
			updateCellSize(dataset);
		}
		comboBoxMethod.setItems(new ParameterDataNode(ResampleModeUtilities.toString(ResampleMode.NEAREST), ResampleMode.NEAREST),
				new ParameterDataNode(ResampleModeUtilities.toString(ResampleMode.CUBIC), ResampleMode.CUBIC),
				new ParameterDataNode(ResampleModeUtilities.toString(ResampleMode.BILINEAR), ResampleMode.BILINEAR));
		textFieldSourceXPixel.setEnabled(false);
		textFieldSourceYPixel.setEnabled(false);
		textFieldSourceRow.setEnabled(false);
		textFieldSourceColumn.setEnabled(false);
		textFieldRow.setEnabled(false);
		textFieldColumn.setEnabled(false);
	}

	private void registerListener() {
		sourceDataset.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (sourceDataset.getSelectedItem() != null && evt.getNewValue() instanceof Dataset) {
					updateCellSize((Dataset) evt.getNewValue());
				}
			}
		});
		numberPixel.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				double cellSize = Double.parseDouble(numberPixel.getSelectedItem().toString());
				Rectangle2D bounds = sourceDataset.getSelectedDataset().getBounds();
				textFieldRow.setSelectedItem((int) (bounds.getHeight() / cellSize + 0.5));
				textFieldColumn.setSelectedItem((int) (bounds.getWidth() / cellSize + 0.5));
			}
		});
	}

	@Override
	public IParameters getParameters() {
		return super.getParameters();
	}

	@Override
	public String getKey() {
		return MetaKeys.GRID_RESAMPLE;
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		try {
			Dataset src = null;
			if (parameters.getInputs().getData(INPUT_DATA).getValue() != null) {
				src = (Dataset) parameters.getInputs().getData(INPUT_DATA).getValue();
			} else {
				src = sourceDataset.getSelectedItem();
			}
			double cellSize = Double.parseDouble(numberPixel.getSelectedItem().toString());
			ResampleMode mode = (ResampleMode) comboBoxMethod.getSelectedData();
			GeneralizeAnalyst.addSteppedListener(steppedListener);
			Dataset result = GeneralizeAnalyst.resample(src, cellSize, mode, resultDataset.getResultDatasource(), resultDataset.getDatasetName());
			isSuccessful = result != null;
			this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(result);
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			GeneralizeAnalyst.removeSteppedListener(steppedListener);
		}

		return isSuccessful;
	}

	private void updateCellSize(Dataset dataset) {
		Rectangle2D bounds = dataset.getBounds();
		double cellSizeX;
		double cellSizeY;
		if (dataset.getType().equals(DatasetType.GRID)) {
			cellSizeX = bounds.getWidth() / ((DatasetGrid) dataset).getWidth();
			cellSizeY = bounds.getHeight() / ((DatasetGrid) dataset).getHeight();
		} else {
			cellSizeX = bounds.getWidth() / ((DatasetImage) dataset).getWidth();
			cellSizeY = bounds.getHeight() / ((DatasetImage) dataset).getHeight();
		}
		textFieldSourceXPixel.setSelectedItem(convertToDecimal(cellSizeX));
		textFieldSourceYPixel.setSelectedItem(convertToDecimal(cellSizeY));
		double cellSize = 2 * (cellSizeX > cellSizeY ? cellSizeX : cellSizeY);
		numberPixel.setSelectedItem(cellSize);
		textFieldSourceRow.setSelectedItem((int) (bounds.getWidth() / cellSizeX + 0.5));
		textFieldSourceColumn.setSelectedItem((int) (bounds.getHeight() / cellSizeY + 0.5));
		textFieldRow.setSelectedItem((int) (bounds.getWidth() / cellSize + 0.5));
		textFieldColumn.setSelectedItem((int) (bounds.getHeight() / cellSize + 0.5));
	}

	private String convertToDecimal(double num) {
		DecimalFormat format = new DecimalFormat("#0.000000000000000");
		String result = format.format(num);
		while (result.charAt(result.length() - 1) == '0') {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
}
