package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.gridStatisticsAnalyst;

import com.supermap.analyst.spatialanalyst.StatisticsAnalyst;
import com.supermap.analyst.spatialanalyst.StatisticsCompareType;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetGrid;
import com.supermap.data.DatasetType;
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
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * Created By Chens on 2017/8/15 0015
 */
public class MetaProcessCommonStatistics extends MetaProcess {
	private final static String INPUT_DATA = SOURCE_PANEL_DESCRIPTION;
	private final static String OUTPUT_DATA = "CommonStatisticsResult";

    private final static int NUMBER = 0;
    private final static int TABLE = 1;

	private ParameterDatasourceConstrained sourceDatasource;
	private ParameterSingleDataset sourceDataset;
	private ParameterSaveDataset resultDataset;
	private ParameterComboBox comboBoxCompareType;
	private ParameterCheckBox checkBoxIgnore;
    private ParameterRadioButton radioButton;
    private ParameterNumber number;
    private ParameterDatasetChooseTable chooseTable;
    private ParameterSwitch parameterSwitch;

    public MetaProcessCommonStatistics() {
        setTitle(ControlsProperties.getString("String_Title_CommonStatistics"));
        initParameters();
        initParameterConstraint();
        initParametersState();
        registerListener();
    }

    private void registerListener() {
        radioButton.addPropertyListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                parameterSwitch.switchParameter(((ParameterDataNode) radioButton.getSelectedItem()).getData().equals(NUMBER) ? "0" : "1");
            }
        });
    }

	private void initParameters() {
		sourceDatasource = new ParameterDatasourceConstrained();
		sourceDataset = new ParameterSingleDataset(DatasetType.GRID);
		resultDataset = new ParameterSaveDataset();
		comboBoxCompareType = new ParameterComboBox(ControlsProperties.getString("String_CompareType"));
		checkBoxIgnore = new ParameterCheckBox(ControlsProperties.getString("String_IgnoreNoValue"));
        radioButton = new ParameterRadioButton();
        number = new ParameterNumber(ProcessProperties.getString("String_Label_CommonStatisticType_Single"));
        chooseTable = new ParameterDatasetChooseTable();
        parameterSwitch = new ParameterSwitch();

        parameterSwitch.add("0", number);
        parameterSwitch.add("1", chooseTable);

		ParameterCombine sourceCombine = new ParameterCombine();
		sourceCombine.setDescribe(CoreProperties.getString("String_GroupBox_SourceData"));
		sourceCombine.addParameters(sourceDatasource, sourceDataset);
		ParameterCombine settingCombine = new ParameterCombine();
		settingCombine.setDescribe(ProcessProperties.getString("String_setParameter"));
        settingCombine.addParameters(comboBoxCompareType, radioButton, parameterSwitch, checkBoxIgnore);
        ParameterCombine resultCombine = new ParameterCombine();
		resultCombine.setDescribe(CoreProperties.getString("String_GroupBox_ResultData"));
		resultCombine.addParameters(resultDataset);

		parameters.setParameters(sourceCombine, settingCombine, resultCombine);
		this.parameters.addInputParameters(INPUT_DATA, DatasetTypes.GRID, sourceCombine);
		this.parameters.addOutputParameters(OUTPUT_DATA,
				ProcessOutputResultProperties.getString("String_CommonStatisticResult"),
				DatasetTypes.GRID, resultCombine);
	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint constraintSource = new EqualDatasourceConstraint();
		constraintSource.constrained(sourceDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		constraintSource.constrained(sourceDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);
		EqualDatasetConstraint datasetConstraint = new EqualDatasetConstraint();
		datasetConstraint.constrained(sourceDataset, ParameterSingleDataset.DATASET_FIELD_NAME);
        datasetConstraint.constrained(chooseTable, ParameterDatasetChooseTable.DATASET_FIELD_NAME);
        DatasourceConstraint.getInstance().constrained(resultDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);
	}

	private void initParametersState() {
		DatasetGrid datasetGrid = DatasetUtilities.getDefaultDatasetGrid();
		if (datasetGrid != null) {
			sourceDatasource.setSelectedItem(datasetGrid.getDatasource());
			sourceDataset.setSelectedItem(datasetGrid);
            chooseTable.setDataset(datasetGrid);
        }
		resultDataset.setDefaultDatasetName("result_commonStatistics");
		checkBoxIgnore.setSelectedItem(true);
        radioButton.setItems(new ParameterDataNode[]{
                new ParameterDataNode(ProcessProperties.getString("String_RadioButton_CommonStatisticType_Single"), NUMBER),
                new ParameterDataNode(CoreProperties.getString("String_DatasetType_Grid"), TABLE)});
        comboBoxCompareType.setItems(new ParameterDataNode("<", StatisticsCompareType.LESS),
				new ParameterDataNode("<=", StatisticsCompareType.LESS_OR_EQUAL),
				new ParameterDataNode("==", StatisticsCompareType.EQUAL),
				new ParameterDataNode(">=", StatisticsCompareType.GREATER_OR_EQUAL),
				new ParameterDataNode(">", StatisticsCompareType.GREATER));
	}

	@Override
	public IParameters getParameters() {
		return super.getParameters();
	}

	@Override
	public String getKey() {
		return MetaKeys.COMMON_STATISTIC;
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		try {
			DatasetGrid src = null;
			if (parameters.getInputs().getData(INPUT_DATA).getValue() != null) {
				src = (DatasetGrid) parameters.getInputs().getData(INPUT_DATA).getValue();
			} else {
				src = (DatasetGrid) sourceDataset.getSelectedItem();
			}
			StatisticsCompareType type = (StatisticsCompareType) comboBoxCompareType.getSelectedData();
			boolean isIgnore = Boolean.parseBoolean(checkBoxIgnore.getSelectedItem());
			String datasetName = resultDataset.getDatasetName();
			datasetName = resultDataset.getResultDatasource().getDatasets().getAvailableDatasetName(datasetName);
			StatisticsAnalyst.addSteppedListener(steppedListener);
			DatasetGrid result = null;
            if (((ParameterDataNode) radioButton.getSelectedItem()).getData().equals(NUMBER)) {
                double value = Double.parseDouble(number.getSelectedItem());
                result = StatisticsAnalyst.commonStatistics(src, value, type, isIgnore, resultDataset.getResultDatasource(), datasetName);
			} else {
                ArrayList<Dataset> datasetArrayList = (ArrayList<Dataset>) chooseTable.getSelectedItem();
                DatasetGrid[] datasetGrids = new DatasetGrid[datasetArrayList.size()];
				for (int i = 0; i < datasetArrayList.size(); i++) {
					datasetGrids[i] = (DatasetGrid) datasetArrayList.get(i);
				}
				result = StatisticsAnalyst.commonStatistics(src, datasetGrids, type, isIgnore, resultDataset.getResultDatasource(), datasetName);
			}
			isSuccessful = result != null;
			this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(result);
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			StatisticsAnalyst.removeSteppedListener(steppedListener);
		}
		return isSuccessful;
	}
}
