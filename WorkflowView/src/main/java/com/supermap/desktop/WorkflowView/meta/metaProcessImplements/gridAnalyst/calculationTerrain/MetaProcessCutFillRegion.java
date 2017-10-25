package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.gridAnalyst.calculationTerrain;

import com.supermap.analyst.spatialanalyst.CalculationTerrain;
import com.supermap.analyst.spatialanalyst.CutFillResult;
import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.properties.CommonProperties;
import com.supermap.desktop.utilities.DatasetUtilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by ChenS on 2017/10/24 0024.
 */
public class MetaProcessCutFillRegion extends MetaProcessCalTerrain {
    private final static String OUTPUT_DATASET = "CutFill";
    private final static String PARAMETER_DATASET = ProcessProperties.getString("String_ZonalStatistic_ZonalData");

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
        datasetParameter = new ParameterSingleDataset(DatasetType.REGION, DatasetType.LINE3D);
        numberHeight = new ParameterNumber(ProcessProperties.getString("String_SurfaceAnalyst_ViewShed_Unit_AddHeight"));
        comboBoxType = new ParameterComboBox(ProcessProperties.getString("String_Label_BufferType"));
        numberRadius = new ParameterNumber(ProcessProperties.getString("String_Label_BufferRadius"));

        EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
        equalDatasourceConstraint.constrained(this.datasourceParameter, ParameterDatasource.DATASOURCE_FIELD_NAME);
        equalDatasourceConstraint.constrained(this.datasetParameter, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

        ParameterCombine parameterCombine = new ParameterCombine();
        parameterCombine.setDescribe(PARAMETER_DATASET);
        parameterCombine.addParameters(datasourceParameter, datasetParameter);

        ParameterCombine bufferCombine = new ParameterCombine();
        bufferCombine.setDescribe(CommonProperties.getString("String_ResultSet"));
        bufferCombine.addParameters(numberHeight, comboBoxType, numberRadius);

        parameters.addParameters(parameterCombine, bufferCombine);
        parameters.addInputParameters(PARAMETER_DATASET, new DatasetTypes("", DatasetTypes.REGION.getValue() | DatasetTypes.LINE3D.getValue()), parameterCombine);
        parameters.addOutputParameters(OUTPUT_DATASET, ProcessOutputResultProperties.getString("String_Result_CutFill"), DatasetTypes.GRID, parameterCombineResultDataset);


        Dataset defaultDataset = DatasetUtilities.getDefaultDataset(DatasetType.REGION, DatasetType.LINE3D);
        if (defaultDataset != null) {
            datasourceParameter.setSelectedItem(defaultDataset.getDatasource());
            datasetParameter.setSelectedItem(defaultDataset);
            comboBoxType.setEnabled(!defaultDataset.getType().equals(DatasetType.REGION));
            numberRadius.setEnabled(!defaultDataset.getType().equals(DatasetType.REGION));
        }
        comboBoxType.setItems(new ParameterDataNode(ProcessProperties.getString("String_CheckBox_BufferFlat"), false),
                new ParameterDataNode(ProcessProperties.getString("String_CheckBox_BufferRound"), true));
        numberHeight.setSelectedItem(0);
        numberRadius.setSelectedItem(10);
        numberRadius.setMinValue(0);
        numberRadius.setIsIncludeMin(false);

        datasetParameter.addPropertyListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                comboBoxType.setEnabled(!datasetParameter.getSelectedItem().getType().equals(DatasetType.REGION));
                numberRadius.setEnabled(!datasetParameter.getSelectedItem().getType().equals(DatasetType.REGION));
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
            recordset = datasetVectorParameter.getRecordset(false, CursorType.DYNAMIC);
            if (datasetVectorParameter.getType().equals(DatasetType.REGION)) {
                GeoRegion geometry = (GeoRegion) recordset.getGeometry();
                double height = Double.parseDouble(numberHeight.getSelectedItem());
                result = CalculationTerrain.cutFill(datasetGrid, geometry, height, parameterSaveDataset.getResultDatasource(), parameterSaveDataset.getDatasetName());
                geometry.dispose();
            } else {
                GeoLine3D geometry = (GeoLine3D) recordset.getGeometry();
                double radius = Double.parseDouble(numberRadius.getSelectedItem());
                boolean isRoundHead = (boolean) comboBoxType.getSelectedData();
                result = CalculationTerrain.cutFill(datasetGrid, geometry, radius, isRoundHead, parameterSaveDataset.getResultDatasource(), parameterSaveDataset.getDatasetName());
                geometry.dispose();
            }
            isSuccessful = result != null;
            if (isSuccessful) {
                Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_Print_FillVolume") + result.getFillVolume());
                Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_Print_CutVolume") + result.getCutVolume());
                Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_Print_FillArea") + result.getFillArea());
                Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_Print_CutArea") + result.getCutArea());
                Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_Print_RemainderArea") + result.getRemainderArea());
                this.getParameters().getOutputs().getData(OUTPUT_DATASET).setValue(result.getCutFillGridResult());
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
