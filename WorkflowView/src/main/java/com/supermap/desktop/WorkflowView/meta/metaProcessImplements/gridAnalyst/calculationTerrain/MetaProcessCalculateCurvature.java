package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.gridAnalyst.calculationTerrain;

import com.supermap.analyst.spatialanalyst.CalculationTerrain;
import com.supermap.data.DatasetGrid;
import com.supermap.data.DatasetType;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.utilities.DatasetUtilities;

/**
 * Created by ChenS on 2017/10/24 0024.
 */
public class MetaProcessCalculateCurvature extends MetaProcess {
    private final static String INPUT_DATA = SOURCE_PANEL_DESCRIPTION;
    private final static String AVERAGE_CURVATURE = "AverageCurvature";
    private final static String PROFILE_CURVATURE = "ProfileCurvature";
    private final static String PLAN_CURVATURE = "PlanCurvature";

    private ParameterDatasourceConstrained sourceDatasource;
    private ParameterSingleDataset sourceDataset;
    private ParameterNumber numberZFactor;
    private ParameterDatasourceConstrained resultDatasource;
    private ParameterTextField textFieldAverageCurvature;
    private ParameterTextField textFieldProfileCurvature;
    private ParameterTextField textFieldPlanCurvature;

    public MetaProcessCalculateCurvature() {
        setTitle(ProcessProperties.getString("String_Form_CalculateCurvature"));
        initParameters();
        initParameterConstraint();
        initParametersState();
    }

    private void initParameters() {
        sourceDatasource = new ParameterDatasourceConstrained();
        sourceDataset = new ParameterSingleDataset(DatasetType.GRID);
        numberZFactor = new ParameterNumber(ProcessProperties.getString("String_Label_ZFactor"));
        resultDatasource = new ParameterDatasourceConstrained();
        textFieldAverageCurvature = new ParameterTextField(ProcessProperties.getString("String_Label_AverageCurvatureDataset"));
        textFieldProfileCurvature = new ParameterTextField(ProcessProperties.getString("String_Label_ProfileCurvatureDataset"));
        textFieldPlanCurvature = new ParameterTextField(ProcessProperties.getString("String_Label_PlanCurvatureDataset"));

        ParameterCombine sourceCombine = new ParameterCombine();
        sourceCombine.setDescribe(INPUT_DATA);
        sourceCombine.addParameters(sourceDatasource, sourceDataset);

        ParameterCombine settingCombine = new ParameterCombine();
        settingCombine.setDescribe(SETTING_PANEL_DESCRIPTION);
        settingCombine.addParameters(numberZFactor);

        ParameterCombine resultCombine = new ParameterCombine();
        resultCombine.setDescribe(RESULT_PANEL_DESCRIPTION);
        resultCombine.addParameters(resultDatasource, textFieldAverageCurvature, textFieldProfileCurvature, textFieldPlanCurvature);

        parameters.setParameters(sourceCombine, settingCombine, resultCombine);
        parameters.addInputParameters(INPUT_DATA, DatasetTypes.GRID, sourceCombine);
        parameters.addOutputParameters(AVERAGE_CURVATURE, ProcessOutputResultProperties.getString("String_Result_AverageCurvatureDataset"), DatasetTypes.GRID, textFieldAverageCurvature);
        parameters.addOutputParameters(PROFILE_CURVATURE, ProcessOutputResultProperties.getString("String_Result_ProfileCurvatureDataset"), DatasetTypes.GRID, textFieldProfileCurvature);
        parameters.addOutputParameters(PLAN_CURVATURE, ProcessOutputResultProperties.getString("String_Result_PlanCurvatureDataset"), DatasetTypes.GRID, textFieldPlanCurvature);
    }

    private void initParameterConstraint() {
        EqualDatasourceConstraint constraintSource = new EqualDatasourceConstraint();
        constraintSource.constrained(sourceDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
        constraintSource.constrained(sourceDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);
        DatasourceConstraint.getInstance().constrained(resultDatasource, ParameterDatasourceConstrained.DATASOURCE_FIELD_NAME);
    }

    private void initParametersState() {
        DatasetGrid defaultDatasetGrid = DatasetUtilities.getDefaultDatasetGrid();
        if (defaultDatasetGrid != null) {
            sourceDataset.setSelectedItem(defaultDatasetGrid);
            sourceDatasource.setSelectedItem(defaultDatasetGrid.getDatasource());
            resultDatasource.setSelectedItem(defaultDatasetGrid.getDatasource());
        }
        numberZFactor.setIsIncludeMin(false);
        numberZFactor.setSelectedItem(1);
        numberZFactor.setMinValue(0);
        textFieldAverageCurvature.setSelectedItem("result_averageCurvature");
        textFieldProfileCurvature.setSelectedItem("result_profileCurvature");
        textFieldPlanCurvature.setSelectedItem("result_planCurvature");
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
            double zFactor = Double.parseDouble(numberZFactor.getSelectedItem());
            String averageCurvatureName = resultDatasource.getSelectedItem().getDatasets().getAvailableDatasetName(textFieldAverageCurvature.getSelectedItem());
            String profileCurvatureName = resultDatasource.getSelectedItem().getDatasets().getAvailableDatasetName(textFieldProfileCurvature.getSelectedItem());
            String planCurvatureName = resultDatasource.getSelectedItem().getDatasets().getAvailableDatasetName(textFieldPlanCurvature.getSelectedItem());
            DatasetGrid datasetAverageCurvature = CalculationTerrain.calculateCurvature(src, zFactor, resultDatasource.getSelectedItem(),
                    averageCurvatureName, profileCurvatureName, planCurvatureName);
            isSuccessful = datasetAverageCurvature != null;
            DatasetGrid datasetProfileCurvature = (DatasetGrid) resultDatasource.getSelectedItem().getDatasets().get(profileCurvatureName);
            DatasetGrid datasetPlanCurvature = (DatasetGrid) resultDatasource.getSelectedItem().getDatasets().get(planCurvatureName);
            this.getParameters().getOutputs().getData(AVERAGE_CURVATURE).setValue(datasetAverageCurvature);
            this.getParameters().getOutputs().getData(PROFILE_CURVATURE).setValue(datasetProfileCurvature);
            this.getParameters().getOutputs().getData(PLAN_CURVATURE).setValue(datasetPlanCurvature);
        } catch (Exception e) {
            Application.getActiveApplication().getOutput().output(e);
        } finally {
            CalculationTerrain.removeSteppedListener(steppedListener);
        }

        return isSuccessful;
    }

    @Override
    public String getKey() {
        return MetaKeys.CALCULATE_CURVATURE;
    }
}
