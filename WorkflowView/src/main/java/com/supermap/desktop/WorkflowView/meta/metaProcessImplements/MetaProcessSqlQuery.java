package com.supermap.desktop.WorkflowView.meta.metaProcessImplements;

import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.interfaces.IParameterPanel;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;
import com.supermap.desktop.utilities.StringUtilities;

import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * Created by xie on 2017/2/21.
 * sql查询简单实现
 */
public class MetaProcessSqlQuery extends MetaProcess {
	private final static String INPUT_DATA = CoreProperties.getString("String_GroupBox_SourceData");
	private final static String OUTPUT_DATA = "SqlQueryResult";
	private ParameterDatasourceConstrained datasource;
	private ParameterSingleDataset dataset;
	private ParameterTextArea parameterAttributeFilter;
	private ParameterTextArea parameterResultFields;
	private ParameterSaveDataset parameterSaveDataset;
	private DatasetType[] datasetTypes = new DatasetType[]{
			DatasetType.POINT, DatasetType.LINE, DatasetType.REGION,
			DatasetType.POINT3D, DatasetType.LINE3D, DatasetType.REGION3D,
			DatasetType.TEXT, DatasetType.TABULAR, DatasetType.CAD
	};

	public MetaProcessSqlQuery() {
		setTitle(ProcessProperties.getString("String_SqlQuery"));
		initMetaInfo();
	}

	private void initMetaInfo() {

		datasource = new ParameterDatasourceConstrained();
		this.datasource.setDescribe(CoreProperties.getString("String_SourceDatasource"));
		this.dataset = new ParameterSingleDataset(datasetTypes);
		DatasetVector datasetVector = DatasetUtilities.getDefaultDatasetVector();
		if (datasetVector != null) {
			datasource.setSelectedItem(datasetVector.getDatasource());
			dataset.setSelectedItem(datasetVector);
		}
		parameterResultFields = new ParameterTextArea(CoreProperties.getString("String_QueryField"));
		parameterAttributeFilter = new ParameterTextArea(CoreProperties.getString("String_QueryCondition"));
		parameterSaveDataset = new ParameterSaveDataset();
		parameterSaveDataset.setDefaultDatasetName("result_query");
		initParameterConstraint();

		ParameterCombine parameterCombineSourceData = new ParameterCombine();
		parameterCombineSourceData.addParameters(datasource, this.dataset);
		parameterCombineSourceData.setDescribe(ControlsProperties.getString("String_GroupBox_SourceDataset"));
		parameterCombineSourceData.setRequisite(true);
//		ParameterCombine parameterCombineSetting = new ParameterCombine();
//		parameterCombineSetting.addParameters(this.parameterResultFields, this.parameterAttributeFilter);
//		parameterCombineSetting.setDescribe(CommonProperties.getString("String_GroupBox_ParamSetting"));

		ParameterCombine parameterCombineResultData = new ParameterCombine();
		parameterCombineResultData.addParameters(parameterSaveDataset);
		parameterCombineResultData.setDescribe(CoreProperties.getString("String_ResultSet"));

		parameters.setParameters(parameterCombineSourceData, this.parameterResultFields, this.parameterAttributeFilter, parameterCombineResultData);
		this.parameters.addInputParameters(INPUT_DATA, DatasetTypes.VECTOR, parameterCombineSourceData);
		this.parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_SQLQueryResult"), DatasetTypes.VECTOR, parameterCombineResultData);
	}

	private void initParameterConstraint() {
		DatasourceConstraint.getInstance().constrained(dataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);
		DatasourceConstraint.getInstance().constrained(parameterSaveDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);

		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(datasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(dataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);
	}

	@Override
	public IParameterPanel getComponent() {
		return parameters.getPanel();
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		Recordset resultRecord = null;
		try {
			DatasetVector currentDatasetVector = null;
			if (this.getParameters().getInputs().getData(INPUT_DATA).getValue() instanceof DatasetVector) {
				currentDatasetVector = (DatasetVector) this.getParameters().getInputs().getData(INPUT_DATA).getValue();
			} else {
				currentDatasetVector = (DatasetVector) dataset.getSelectedItem();
			}

			if (null != currentDatasetVector) {
				// 构建查询语句
				QueryParameter queryParameter = new QueryParameter();
				queryParameter.setCursorType(CursorType.DYNAMIC);
				queryParameter.setHasGeometry(true);

				// 查询字段
				String queryFields = (String) parameterResultFields.getSelectedItem();
				String[] queryFieldNames = getQueryFieldNames(queryFields);
				queryParameter.setResultFields(queryFieldNames);
				// 查询条件
				queryParameter.setAttributeFilter((String) parameterAttributeFilter.getSelectedItem());
				preProcessSQLQuery(queryParameter);
				queryParameter.setSpatialQueryObject(currentDatasetVector);
				resultRecord = currentDatasetVector.query(queryParameter);
				if (resultRecord != null && resultRecord.getRecordCount() > 0) {
					// 保存查询结果
					DatasetVector datasetVector = saveQueryResult(resultRecord);
					isSuccessful = true;
					this.parameters.getOutputs().getData(OUTPUT_DATA).setValue(datasetVector);
				}
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			if (resultRecord != null) {
				resultRecord.close();
			}
		}
		return isSuccessful;
	}

	@Override
	public String getKey() {
		return MetaKeys.SQL_QUERY;
	}

	private DatasetVector saveQueryResult(Recordset resultRecord) {
		DatasetVector resultDataset = null;
		Datasource resultDatasource = parameterSaveDataset.getResultDatasource();
		String datasetName = parameterSaveDataset.getDatasetName();
		if (resultDatasource != null && !StringUtilities.isNullOrEmpty(datasetName)) {
			try {
				resultDataset = resultDatasource.recordsetToDataset(resultRecord, resultDatasource.getDatasets().getAvailableDatasetName(datasetName));
			} catch (Exception e) {
				resultDataset = null;
			}
			resultRecord.moveFirst();
			if (resultDataset == null) {
				Application.getActiveApplication().getOutput().output(CoreProperties.getString("String_SQLQuerySaveAsResultFaield"));
			} else {
				Application.getActiveApplication().getOutput()
						.output(MessageFormat.format(CoreProperties.getString("String_SQLQuerySavaAsResultSucces"), resultDataset.getName()));
			}
		}

		return resultDataset;
	}

	private void preProcessSQLQuery(QueryParameter queryParameter) {
		try {
			for (String field : queryParameter.getResultFields()) {
				String strText = field.toUpperCase();
				if (strText.contains("SUM(") || strText.contains("MAX(") || strText.contains("MIN(") || strText.contains("AVG(") || strText.contains("COUNT(")
						|| strText.contains("STDEV(") || strText.contains("STDEVP(") || strText.contains("VAR(") || strText.contains("VARP(")) {
					queryParameter.setCursorType(CursorType.STATIC);
					break;
				}
			}

			if (queryParameter.getGroupBy().length > 0) {
				queryParameter.setCursorType(CursorType.STATIC);
			}

		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	private String[] getQueryFieldNames(String queryFields) {
		int bracketsCount = 0;
		java.util.List<String> fieldNames = new ArrayList<>();
		if (StringUtilities.isNullOrEmpty(queryFields)) {
			return null;
		}
		char[] fieldNamesChars = queryFields.toCharArray();
		StringBuilder builderFieldName = new StringBuilder();
		for (char fieldNamesChar : fieldNamesChars) {
			if (fieldNamesChar == ',' && bracketsCount == 0 && builderFieldName.length() > 0) {
				fieldNames.add(builderFieldName.toString());
				builderFieldName.setLength(0);
			} else {
				builderFieldName.append(fieldNamesChar);
				if (fieldNamesChar == '(') {
					bracketsCount++;
				} else if (fieldNamesChar == ')' && bracketsCount > 0) {
					bracketsCount--;
				}
			}
		}
		if (builderFieldName.length() > 0) {
			fieldNames.add(builderFieldName.toString());
			builderFieldName.setLength(0);
		}
		return fieldNames.toArray(new String[fieldNames.size()]);
	}
}
