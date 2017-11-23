package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.dataRun;

import com.supermap.analyst.spatialanalyst.DissolveParameter;
import com.supermap.analyst.spatialanalyst.DissolveType;
import com.supermap.analyst.spatialanalyst.Generalization;
import com.supermap.analyst.spatialanalyst.StatisticsType;
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
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.process.types.Type;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;
import com.supermap.desktop.utilities.RecordsetUtilities;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static com.supermap.desktop.process.types.DatasetTypes.*;

/**
 * Created by lixiaoyao on 2017/8/8.
 */
public class MetaProcessDissolve extends MetaProcess {
    private static final String INPUT_DATA = CoreProperties.getString("String_GroupBox_SourceData");
    private final static String OUTPUT_DATA = "DissolveResult";
    private static final double STANDARD_NUMBER = 1000000.0;
    private DecimalFormat decimalFormat = new DecimalFormat("###############0.00000000#");

    private ParameterDatasourceConstrained sourceDatasource;
    private ParameterSingleDataset sourceDataset;
    private ParameterComboBox comboBoxDissolveMode;
    private ParameterNumber numberDissolveTolerance;
    private ParameterTextField textAreaSQLExpression;
    private ParameterSQLExpression textSQLExpression;
    private ParameterCheckBox checkBoxIsNullValue;
    private ParameterFieldGroup fieldsDissolve;
    private ParameterSimpleStatisticsFieldGroup statisticsFieldGroup;
    private ParameterSaveDataset resultDataset;


    public MetaProcessDissolve() {
        setTitle(ProcessProperties.getString("String_Form_Dissolve"));
        initParameters();
        initParameterConstraint();
        initParametersState();
        registerListener();
    }

    private void initParameters() {
        this.sourceDatasource = new ParameterDatasourceConstrained();
        this.sourceDataset = new ParameterSingleDataset(DatasetType.LINE, DatasetType.REGION, DatasetType.TEXT);

        this.comboBoxDissolveMode = new ParameterComboBox();
        this.comboBoxDissolveMode.setDescribe(ProcessProperties.getString("String_DissolveMode"));
        this.numberDissolveTolerance = new ParameterNumber();
        this.numberDissolveTolerance.setDescribe(ProcessProperties.getString("String_DissolveTolerance"));
        this.textAreaSQLExpression = new ParameterTextField();
        this.textAreaSQLExpression.setDescribe(ControlsProperties.getString("String_LabelFilter"));
        this.textSQLExpression = new ParameterSQLExpression();
        this.textSQLExpression.setDescribe(ControlsProperties.getString("String_SuspensionPoints"));
        this.checkBoxIsNullValue = new ParameterCheckBox();
        this.checkBoxIsNullValue.setDescribe(ProcessProperties.getString("String_IsNullValue"));
        this.fieldsDissolve = new ParameterFieldGroup(ProcessProperties.getString("String_DissolveFields"));
        this.statisticsFieldGroup = new ParameterSimpleStatisticsFieldGroup(ProcessProperties.getString("String_StatisticsField"));
        this.resultDataset = new ParameterSaveDataset();

        ParameterCombine sourceData = new ParameterCombine();
        sourceData.setDescribe(SOURCE_PANEL_DESCRIPTION);
        sourceData.addParameters(this.sourceDatasource, this.sourceDataset);

        ParameterCombine parameterCombineParent = new ParameterCombine(ParameterCombine.HORIZONTAL);
        parameterCombineParent.addParameters(this.textAreaSQLExpression, this.textSQLExpression);
        parameterCombineParent.setWeightIndex(0);
        this.textSQLExpression.setAnchor(GridBagConstraints.EAST);
        ParameterCombine parameterSetting = new ParameterCombine();
        parameterSetting.setDescribe(CoreProperties.getString("String_GroupBox_ParamSetting"));
        parameterSetting.addParameters(this.comboBoxDissolveMode, this.numberDissolveTolerance, parameterCombineParent, this.checkBoxIsNullValue, this.fieldsDissolve, this.statisticsFieldGroup);

        ParameterCombine targetData = new ParameterCombine();
        targetData.setDescribe(CoreProperties.getString("String_GroupBox_ResultData"));
        targetData.addParameters(this.resultDataset);

        Type type = Type.instance("").and(TEXT).and(LINE).and(REGION);
        this.parameters.setParameters(sourceData, parameterSetting, targetData);
        this.parameters.addInputParameters(INPUT_DATA, type, sourceData);
        this.parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_Result_Dissolve"), type, targetData);

    }

    private void initParametersState() {
        Dataset dataset = DatasetUtilities.getDefaultDataset(DatasetType.TEXT, DatasetType.LINE, DatasetType.REGION);
        this.numberDissolveTolerance.setSelectedItem(0.00001);
        if (dataset != null) {
            this.sourceDatasource.setSelectedItem(dataset.getDatasource());
            this.sourceDataset.setSelectedItem(dataset);
            this.resultDataset.setResultDatasource(dataset.getDatasource());
            this.numberDissolveTolerance.setUnit(dataset.getPrjCoordSys().getCoordUnit().toString());
            //BigDecimal temp = new BigDecimal(Double.valueOf(DatasetUtilities.getDefaultTolerance((DatasetVector)sourceDataset).getNodeSnap()));
            this.numberDissolveTolerance.setSelectedItem(DatasetUtilities.getDefaultTolerance((DatasetVector) dataset).getNodeSnap());
            this.fieldsDissolve.setDataset((DatasetVector) dataset);
            this.statisticsFieldGroup.setDataset((DatasetVector) dataset);
            this.textSQLExpression.setSelectDataset(dataset);
            numberDissolveTolerance.setEnabled(!dataset.getType().equals(DatasetType.TEXT));
            comboBoxDissolveMode.setEnabled(!dataset.getType().equals(DatasetType.TEXT));
        }
        this.resultDataset.setDefaultDatasetName("result_Dissolve");

        ParameterDataNode parameterDataNodeOnlyMultipart = new ParameterDataNode(ControlsProperties.getString("String_GeometryOperation_Combination"), DissolveType.ONLYMULTIPART);
        ParameterDataNode parameterDataNodeOnlySingle = new ParameterDataNode(ProcessProperties.getString("String_Dissolve_Mode_Single"), DissolveType.SINGLE);
        ParameterDataNode parameterDataNodeMultipart = new ParameterDataNode(ProcessProperties.getString("String_Dissolve_Mode_MultiPart"), DissolveType.MULTIPART);
        this.comboBoxDissolveMode.setItems(parameterDataNodeOnlyMultipart, parameterDataNodeOnlySingle, parameterDataNodeMultipart);
        this.comboBoxDissolveMode.setSelectedItem(parameterDataNodeOnlySingle);
        this.numberDissolveTolerance.setMinValue(0);
        this.numberDissolveTolerance.setIsIncludeMin(true);
        this.numberDissolveTolerance.setRequisite(true);
    }

    private void initParameterConstraint() {
        EqualDatasourceConstraint constraintSource = new EqualDatasourceConstraint();
        constraintSource.constrained(sourceDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
        constraintSource.constrained(sourceDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

        EqualDatasetConstraint equalDatasetConstraint = new EqualDatasetConstraint();
        equalDatasetConstraint.constrained(sourceDataset, ParameterSingleDataset.DATASET_FIELD_NAME);
        equalDatasetConstraint.constrained(fieldsDissolve, ParameterFieldGroup.FIELD_DATASET);
        equalDatasetConstraint.constrained(statisticsFieldGroup, ParameterSimpleStatisticsFieldGroup.FIELD_DATASET);
        equalDatasetConstraint.constrained(textSQLExpression, ParameterSQLExpression.DATASET_FIELD_NAME);

        DatasourceConstraint.getInstance().constrained(resultDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);
    }

    private void registerListener() {
        this.sourceDataset.addPropertyListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (sourceDataset.getSelectedItem() != null && evt.getNewValue() instanceof Dataset) {
                    DatasetVector newValue = (DatasetVector) evt.getNewValue();
                    numberDissolveTolerance.setSelectedItem(DatasetUtilities.getDefaultTolerance(newValue).getNodeSnap());
                    numberDissolveTolerance.setEnabled(!newValue.getType().equals(DatasetType.TEXT));
                    comboBoxDissolveMode.setEnabled(!newValue.getType().equals(DatasetType.TEXT));
                }
            }
        });

        this.textSQLExpression.addPropertyListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (null != evt.getNewValue()) {
                    textAreaSQLExpression.setSelectedItem(textSQLExpression.getSelectedItem());
                }
            }
        });
    }

    @Override
    public boolean execute() {
        boolean isSuccessful = false;
        try {
            DissolveParameter dissolveParameter = new DissolveParameter();

            String datasetName = resultDataset.getDatasetName();
            datasetName = resultDataset.getResultDatasource().getDatasets().getAvailableDatasetName(datasetName);
            DatasetVector src;
            if (this.getParameters().getInputs().getData(INPUT_DATA).getValue() != null) {
                src = (DatasetVector) this.getParameters().getInputs().getData(INPUT_DATA).getValue();
            } else {
                src = (DatasetVector) sourceDataset.getSelectedItem();
            }

            dissolveParameter.setDissolveType((DissolveType) this.comboBoxDissolveMode.getSelectedData());
            dissolveParameter.setTolerance(Double.valueOf(this.numberDissolveTolerance.getSelectedItem()));
            dissolveParameter.setFilterString(this.textAreaSQLExpression.getSelectedItem());
            dissolveParameter.setNullValue(Boolean.parseBoolean(checkBoxIsNullValue.getSelectedItem()));
            String[] fieldNames = getFieldName(this.fieldsDissolve.getSelectedFields());
            dissolveParameter.setFieldNames(fieldNames);
            if (this.statisticsFieldGroup.getSelectedFields() != null) {
                dissolveParameter.setStatisticsFieldNames(getFieldName(this.statisticsFieldGroup.getSelectedFields()));
                dissolveParameter.setStatisticsTypes(this.statisticsFieldGroup.getSelectedStatisticsType());
            }

            Generalization.addSteppedListener(steppedListener);
            DatasetVector result;
            if (src.getType().equals(DatasetType.TEXT)) {
                result = DissolveText(src, this.resultDataset.getResultDatasource(), datasetName, dissolveParameter);
                if (result.getRecordset(false, CursorType.DYNAMIC).getRecordCount() == 0) {
                    resultDataset.getResultDatasource().getDatasets().delete(datasetName);
                }
            } else {
                result = Generalization.dissolve(src, this.resultDataset.getResultDatasource(), datasetName, dissolveParameter);
            }
            this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(result);
            isSuccessful = result != null;
        } catch (Exception e) {
            Application.getActiveApplication().getOutput().output(e.getMessage());
            e.printStackTrace();
        } finally {
            Generalization.removeSteppedListener(steppedListener);
        }

        return isSuccessful;
    }

    private DatasetVector DissolveText(DatasetVector src, Datasource resultDatasource, String datasetName, DissolveParameter dissolveParameter) {
        Recordset recordsetResult = null;
        DatasetVector resultDataset = null;
        try {
            DatasetVectorInfo datasetVectorInfo = new DatasetVectorInfo();
            datasetVectorInfo.setName(resultDatasource.getDatasets().getAvailableDatasetName(datasetName));
            datasetVectorInfo.setType(DatasetType.TEXT);
            resultDataset = resultDatasource.getDatasets().create(datasetVectorInfo);

            String[] statisticFieldNames = null;
            StatisticsType[] statisticsTypes = null;
            try {
                statisticFieldNames = dissolveParameter.getStatisticsFieldNames();
                statisticsTypes = dissolveParameter.getStatisticsTypes();
            } catch (Exception e) {
            }

            resultDataset.setPrjCoordSys(src.getPrjCoordSys());
            for (int i = 0; i < src.getFieldInfos().getCount(); i++) {
                FieldInfo fieldInfo = src.getFieldInfos().get(i);
                if (!fieldInfo.isSystemField() && !fieldInfo.getName().toLowerCase().equals("smuserid")) {
                    resultDataset.getFieldInfos().add(fieldInfo);
                }
            }
            if (statisticFieldNames != null) {
                for (int i = 0; i < statisticFieldNames.length; i++) {
                    String appendSuffix = appendSuffix(statisticFieldNames[i], statisticsTypes[i]);
                    FieldType type = src.getFieldInfos().get(statisticFieldNames[i]).getType();
                    resultDataset.getFieldInfos().add(new FieldInfo(appendSuffix, type));
                }
            }

            recordsetResult = resultDataset.getRecordset(false, CursorType.DYNAMIC);
            recordsetResult.addSteppedListener(steppedListener);

            //记录对应SmID的记录是否已经进行过查询
            boolean[] isQueryAlready = new boolean[src.getRecordCount()];

            Stack<Recordset> queryStack = new Stack<>();
            //将满足字段相等条件的记录放到一个记录集里，再将所有这样的记录集用栈queryStack来存储
            String[] fieldNames = dissolveParameter.getFieldNames();
            Recordset srcRecordset = src.getRecordset(false, CursorType.DYNAMIC);
            while (!srcRecordset.isEOF()) {
                //没进行过查询的方能执行之后的步骤
                if (!isQueryAlready[srcRecordset.getID() - 1]) {
                    boolean isContainNull = false;
                    StringBuilder s = new StringBuilder();
                    for (String fieldName : fieldNames) {
                        Object fieldValue = srcRecordset.getFieldValue(fieldName);
                        if (fieldValue == null) {
                            if (dissolveParameter.isNullValue()) {
                                s.append(fieldName).append(" is");
                            } else {
                                isContainNull = true;
                                break;
                            }
                        } else {
                            s.append(fieldName).append(" = ");
                            if (fieldValue instanceof String) {
                                fieldValue = "'" + fieldValue + "'";
                            }
                        }
                        s.append(fieldValue).append(" AND ");
                    }
                    Recordset query;
                    s.delete(s.length() - 5, s.length());
                    if (!isContainNull) {
                        s.append(dissolveParameter.getFilterString());
                        query = src.query(s.toString(), CursorType.DYNAMIC);
                    } else {
                        query = src.query(new int[]{srcRecordset.getID()}, CursorType.DYNAMIC);
                    }
                    while (!query.isEOF()) {
                        isQueryAlready[query.getID() - 1] = true;
                        query.moveNext();
                    }
                    queryStack.push(query);
                }
                srcRecordset.moveNext();
            }
            srcRecordset.dispose();

            recordsetResult.getBatch().setMaxRecordCount(2000);
            recordsetResult.getBatch().begin();
            while (!queryStack.empty()) {
                Recordset pop = queryStack.pop();
                Map<String, Object> value = mergePropertyData(resultDataset, pop, statisticFieldNames, statisticsTypes);
                GeoText geoText = new GeoText();
                while (!pop.isEOF()) {
                    GeoText popText = (GeoText) pop.getGeometry();
                    for (int i = 0; i < popText.getPartCount(); i++) {
                        geoText.addPart(popText.getPart(i));
                    }
                    pop.moveNext();
                }
                recordsetResult.addNew(geoText, value);
                geoText.dispose();
                pop.dispose();
            }
            recordsetResult.getBatch().update();
        } catch (Exception e) {
            Application.getActiveApplication().getOutput().output(e);
        } finally {
            if (recordsetResult != null) {
                recordsetResult.removeSteppedListener(steppedListener);
                recordsetResult.dispose();
            }
        }

        return resultDataset;
    }

    private String[] getFieldName(FieldInfo fieldInfo[]) {
        String[] fieldNames = new String[fieldInfo.length];
        for (int i = 0; i < fieldInfo.length; i++) {
            fieldNames[i] = fieldInfo[i].getName();
        }
        return fieldNames;
    }

    @Override
    public IParameters getParameters() {
        return parameters;
    }

    @Override
    public String getKey() {
        return MetaKeys.DISSOLVE;
    }

    private HashMap<String, Object> mergePropertyData(DatasetVector des, Recordset recordset, String[] statisticFieldNames, StatisticsType[] statisticsTypes) {
        HashMap<String, Object> results = new HashMap<>();
        FieldInfos desFieldInfos = des.getFieldInfos();
        recordset.moveFirst();
        Map<String, Object> properties = RecordsetUtilities.getFieldValuesIgnoreCase(recordset);
        FieldInfos fieldInfos = recordset.getFieldInfos();

        for (int i = 0; i < desFieldInfos.getCount(); i++) {
            FieldInfo desFieldInfo = desFieldInfos.get(i);

            if (!desFieldInfo.isSystemField() && properties.containsKey(desFieldInfo.getName().toLowerCase())) {
                FieldInfo srcFieldInfo = fieldInfos.get(desFieldInfo.getName());

                if (desFieldInfo.getType() == srcFieldInfo.getType()) {
                    // 如果要源字段和目标字段类型一致，直接保存
                    results.put(desFieldInfo.getName(), properties.get(desFieldInfo.getName().toLowerCase()));
                } else if (desFieldInfo.getType() == FieldType.WTEXT || desFieldInfo.getType() == FieldType.TEXT) {

                    // 如果目标字段与源字段类型不一致，则只有目标字段是文本型字段时，将源字段值做 toString 处理
                    results.put(desFieldInfo.getName(), properties.get(desFieldInfo.getName().toLowerCase()).toString());
                }
            }
        }
        if (statisticFieldNames != null) {
            for (int i = 0; i < statisticFieldNames.length; i++) {
                results.put(appendSuffix(statisticFieldNames[i], statisticsTypes[i]), getStatisticsResult(recordset, statisticFieldNames[i], statisticsTypes[i]));
            }
        }
        return results;
    }

    private Object getStatisticsResult(Recordset recordset, String fieldName, StatisticsType type) {
        if (type == StatisticsType.FIRST) {
            recordset.moveFirst();
            return recordset.getFieldValue(fieldName);
        } else if (type == StatisticsType.LAST) {
            recordset.moveLast();
            return recordset.getFieldValue(fieldName);
        } else if (type == StatisticsType.MAX) {
            return recordset.statistic(fieldName, StatisticMode.MAX);
        } else if (type == StatisticsType.MIN) {
            return recordset.statistic(fieldName, StatisticMode.MIN);
        } else if (type == StatisticsType.SUM) {
            return recordset.statistic(fieldName, StatisticMode.SUM);
        } else if (type == StatisticsType.MEAN) {
            return recordset.statistic(fieldName, StatisticMode.AVERAGE);
        }
        return null;
    }

    private String appendSuffix(String fieldName, StatisticsType type) {
        if (type == StatisticsType.FIRST) {
            fieldName += "_FIRST";
        } else if (type == StatisticsType.LAST) {
            fieldName += "_LAST";
        } else if (type == StatisticsType.SUM) {
            fieldName += "_SUM";
        } else if (type == StatisticsType.MAX) {
            fieldName += "_MAX";
        } else if (type == StatisticsType.MIN) {
            fieldName += "_MIN";
        } else if (type == StatisticsType.MEAN) {
            fieldName += "_MEAN";
        }
        return fieldName;
    }
}
