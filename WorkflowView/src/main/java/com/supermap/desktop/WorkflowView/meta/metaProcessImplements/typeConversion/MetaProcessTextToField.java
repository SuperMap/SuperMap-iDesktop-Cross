package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.typeConversion;

import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.EqualDatasetConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;

/**
 * Created by yuanR on 2017/7/26 0026.
 * 文本转字段
 */
public class MetaProcessTextToField extends MetaProcessTypeConversion {

	private ParameterFieldComboBox parameterFieldComboBox;

	public MetaProcessTextToField() {
		setTitle(ProcessProperties.getString("String_Title_TextToField"));
		initParameters();
		initParameterConstraint();
		initParametersState();
	}

	private void initParameters() {
		OUTPUT_DATA = "TextToFieldResult";
		inputDatasource = new ParameterDatasourceConstrained();
		inputDataset = new ParameterSingleDataset(DatasetType.TEXT);

		parameterFieldComboBox = new ParameterFieldComboBox(ProcessProperties.getString("String_ComboBox_PendingTextField"));
		parameterFieldComboBox.setFieldType(new FieldType[]{FieldType.TEXT, FieldType.WTEXT});

		parameterFieldComboBox.setShowNullValue(false);
		parameterFieldComboBox.setRequisite(true);

		// 源数据
		ParameterCombine parameterCombineSourceData = new ParameterCombine();
		parameterCombineSourceData.addParameters(inputDatasource, inputDataset);
		parameterCombineSourceData.setDescribe(ControlsProperties.getString("String_GroupBox_SourceDataset"));

		//参数设置
		ParameterCombine parameterCombineSet = new ParameterCombine();
		parameterCombineSet.addParameters(parameterFieldComboBox);
		parameterCombineSet.setDescribe(CoreProperties.getString("String_GroupBox_ParamSetting"));


		parameters.setParameters(parameterCombineSourceData, parameterCombineSet);
		this.parameters.addInputParameters(INPUT_DATA, DatasetTypes.TEXT, parameterCombineSourceData);
		this.parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_Result_Append"), DatasetTypes.TEXT, parameterCombineSourceData);
	}

	private void initParameterConstraint() {

		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(inputDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(inputDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasetConstraint equalDatasetConstraint = new EqualDatasetConstraint();
		equalDatasetConstraint.constrained(inputDataset, ParameterSingleDataset.DATASET_FIELD_NAME);
		equalDatasetConstraint.constrained(parameterFieldComboBox, ParameterFieldComboBox.DATASET_FIELD_NAME);
	}

	private void initParametersState() {
		Dataset defaultDataset = DatasetUtilities.getDefaultDataset(DatasetType.TEXT);
		if (defaultDataset != null) {
			inputDatasource.setSelectedItem(defaultDataset.getDatasource());
			inputDataset.setSelectedItem(defaultDataset);
			parameterFieldComboBox.setFieldName((DatasetVector) defaultDataset);
		}
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		Recordset recordsetInput = null;
		try {
			DatasetVector src;
			if (parameters.getInputs().getData(INPUT_DATA).getValue() != null) {
				src = (DatasetVector) parameters.getInputs().getData(INPUT_DATA).getValue();
			} else {
				src = (DatasetVector) inputDataset.getSelectedDataset();
			}

			// 首先判断操作的字段是原先有的还是需要新创建
			Boolean isAdd = true;
			String filedName = (String) parameterFieldComboBox.getSelectedItem();
			FieldInfos fieldInfos = src.getFieldInfos();
			for (int i = 0; i < fieldInfos.getCount(); i++) {
				if (fieldInfos.get(i).getName().equals(filedName)) {
					isAdd = false;
					break;
				}
			}
			if (isAdd) {
				FieldInfo newFieldInfo = new FieldInfo();
				newFieldInfo.setName(filedName);
				newFieldInfo.setType(FieldType.TEXT);
				fieldInfos.add(newFieldInfo);
			}

			// 通过记录集获得其数据集的文本内容,并赋值
			recordsetInput = src.getRecordset(false, CursorType.DYNAMIC);
			recordsetInput.addSteppedListener(steppedListener);
			recordsetInput.getBatch().setMaxRecordCount(2000);
			recordsetInput.getBatch().begin();
			recordsetInput.moveFirst();
			while (!recordsetInput.isEOF()) {
				GeoText geoText = (GeoText) recordsetInput.getGeometry();
				String value = geoText.getText();
				isSuccessful = recordsetInput.setFieldValue(filedName, value);
				if (!isSuccessful) {
					break;
				}
				geoText.clone();
				recordsetInput.moveNext();
			}
			recordsetInput.getBatch().update();
			this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(src);
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			if (recordsetInput != null) {
				recordsetInput.removeSteppedListener(steppedListener);
				recordsetInput.close();
				recordsetInput.dispose();
			}
		}
		return isSuccessful;
	}

	@Override
	public IParameters getParameters() {
		return parameters;
	}

	@Override
	public String getKey() {
		return MetaKeys.CONVERSION_TEXT_TO_FILED;
	}
}
