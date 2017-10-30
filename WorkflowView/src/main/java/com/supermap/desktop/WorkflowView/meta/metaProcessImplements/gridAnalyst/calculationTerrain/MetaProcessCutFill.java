package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.gridAnalyst.calculationTerrain;

import com.supermap.analyst.spatialanalyst.CalculationTerrain;
import com.supermap.analyst.spatialanalyst.CutFillResult;
import com.supermap.data.DatasetGrid;
import com.supermap.data.DatasetType;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.ParameterCombine;
import com.supermap.desktop.process.parameter.ipls.ParameterDatasource;
import com.supermap.desktop.process.parameter.ipls.ParameterDatasourceConstrained;
import com.supermap.desktop.process.parameter.ipls.ParameterSingleDataset;
import com.supermap.desktop.properties.CommonProperties;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;

/**
 * Created by ChenS on 2017/10/24 0024.
 */
public class MetaProcessCutFill extends MetaProcessCalTerrain {
    private final static String OUTPUT_DATASET = "CutFill";
    private final static String BEHIND_DATASET = ProcessProperties.getString("String_GroupBox_AfterCutFill");

    private ParameterDatasourceConstrained datasourceBehind;
    private ParameterSingleDataset datasetBehind;

    public MetaProcessCutFill() {
        setTitle(ProcessProperties.getString("String_Title_CutFill"));
    }

    @Override
    protected void initHook() {
        datasourceBehind = new ParameterDatasourceConstrained();
        datasetBehind = new ParameterSingleDataset(DatasetType.GRID);

        EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
        equalDatasourceConstraint.constrained(this.datasourceBehind, ParameterDatasource.DATASOURCE_FIELD_NAME);
        equalDatasourceConstraint.constrained(this.datasetBehind, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

        DatasetGrid defaultDatasetGrid = DatasetUtilities.getDefaultDatasetGrid();
        if (defaultDatasetGrid != null) {
            datasourceBehind.setSelectedItem(defaultDatasetGrid.getDatasource());
            datasetBehind.setSelectedItem(defaultDatasetGrid);
        }

        parameterCombineSourceDataset.setDescribe(ProcessProperties.getString("String_GroupBox_BeforeCutFill"));
        ParameterCombine parameterCombine = new ParameterCombine();
        parameterCombine.setDescribe(BEHIND_DATASET);
        parameterCombine.addParameters(datasourceBehind, datasetBehind);

        parameters.setParameters(parameterCombineSourceDataset, parameterCombine, parameterCombineResultDataset);
        parameters.addInputParameters(BEHIND_DATASET, DatasetTypes.GRID, parameterCombine);
        parameters.addOutputParameters(OUTPUT_DATASET, ProcessOutputResultProperties.getString("String_Result_CutFill"), DatasetTypes.GRID, parameterCombineResultDataset);
    }

    @Override
    protected boolean doWork(DatasetGrid datasetGrid) {
        boolean isSuccessful = false;

        try {
            DatasetGrid datasetGridBehind;
            if (this.parameters.getInputs().getData(BEHIND_DATASET).getValue() != null) {
                datasetGridBehind = (DatasetGrid) this.parameters.getInputs().getData(BEHIND_DATASET).getValue();
            } else {
                datasetGridBehind = (DatasetGrid) datasetBehind.getSelectedDataset();
            }
            CutFillResult cutFillResult = CalculationTerrain.cutFill(datasetGrid, datasetGridBehind, parameterSaveDataset.getResultDatasource(), parameterSaveDataset.getDatasetName());
            isSuccessful = cutFillResult != null;
            Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_Print_FillVolume") + cutFillResult.getFillVolume() + CoreProperties.getString("String_VolumnUnit_Meter"));
            Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_Print_CutVolume") + cutFillResult.getCutVolume() + CoreProperties.getString("String_VolumnUnit_Meter"));
            Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_Print_FillArea") + cutFillResult.getFillArea() + CommonProperties.getString("String_AreaUnit_Meter"));
            Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_Print_CutArea") + cutFillResult.getCutArea() + CommonProperties.getString("String_AreaUnit_Meter"));
            Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_Print_RemainderArea") + cutFillResult.getRemainderArea() + CommonProperties.getString("String_AreaUnit_Meter"));
            this.getParameters().getOutputs().getData(OUTPUT_DATASET).setValue(cutFillResult.getCutFillGridResult());
        } catch (Exception e) {
            Application.getActiveApplication().getOutput().output(e);
        }

        return isSuccessful;
    }

    @Override
    protected String getDefaultResultName() {
        return "result_cutFill";
    }

    @Override
    public String getKey() {
        return MetaKeys.CUT_FILL;
    }
}
