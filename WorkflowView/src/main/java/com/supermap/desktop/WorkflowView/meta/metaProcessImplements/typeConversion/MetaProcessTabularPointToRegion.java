package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.typeConversion;

import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.ParameterCombine;
import com.supermap.desktop.process.parameter.ipls.ParameterDatasource;
import com.supermap.desktop.process.parameter.ipls.ParameterDatasourceConstrained;
import com.supermap.desktop.process.parameter.ipls.ParameterSingleDataset;
import com.supermap.desktop.utilities.DatasetUtilities;

import java.util.ArrayList;

/**
 * Created by yuanR on 2017/7/27 0027.
 * 点属性->面属性
 * 待优化内容：追加点属性到面属性之前需要手动创建追加字段，其创建的字段类型都为文本，之后修改为创建的字段类型和点数据字段类型相同（yuanR已优化）。
 */
public class MetaProcessTabularPointToRegion extends MetaProcessTypeConversion {

	private ParameterDatasourceConstrained sourceDatasource;
	private ParameterSingleDataset sourceDataset;
	private ParameterDatasourceConstrained targetDatasource;
	private ParameterSingleDataset targetDataset;

	public MetaProcessTabularPointToRegion() {
		setTitle(ProcessProperties.getString("String_Title_TabularPointToRegion"));
		initParameters();
		initParameterConstraint();
		initParametersState();

	}

	private void initParameters() {
		OUTPUT_DATA = "TabularPointToRegionResult";
		sourceDatasource = new ParameterDatasourceConstrained();
		sourceDataset = new ParameterSingleDataset(DatasetType.POINT);
		targetDatasource = new ParameterDatasourceConstrained();
		targetDataset = new ParameterSingleDataset(DatasetType.REGION);

		// 源数据
		ParameterCombine parameterCombineSourceData = new ParameterCombine();
		parameterCombineSourceData.addParameters(sourceDatasource, sourceDataset);
		parameterCombineSourceData.setDescribe(ControlsProperties.getString("String_GroupBox_SourceDataset"));

		ParameterCombine parameterCombineTargetData = new ParameterCombine();
		parameterCombineTargetData.addParameters(targetDatasource, targetDataset);
		parameterCombineTargetData.setDescribe(ControlsProperties.getString("String_GroupBox_TargetDataset"));

		parameters.setParameters(parameterCombineSourceData, parameterCombineTargetData);
		this.parameters.addInputParameters(INPUT_DATA, DatasetTypes.POINT, parameterCombineSourceData);
		this.parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_Result_Append"), DatasetTypes.REGION, parameterCombineTargetData);
	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint equalSourceDatasourceConstraint = new EqualDatasourceConstraint();
		equalSourceDatasourceConstraint.constrained(sourceDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		equalSourceDatasourceConstraint.constrained(sourceDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasourceConstraint equalTargetDatasetConstraint = new EqualDatasourceConstraint();
		equalTargetDatasetConstraint.constrained(targetDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		equalTargetDatasetConstraint.constrained(targetDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);
	}

	private void initParametersState() {
		Dataset defaultPointDataset = DatasetUtilities.getDefaultDataset(DatasetType.POINT);
		if (defaultPointDataset != null) {
			sourceDatasource.setSelectedItem(defaultPointDataset.getDatasource());
			sourceDataset.setSelectedItem(defaultPointDataset);
		}
		Dataset defaultRegionDataset = DatasetUtilities.getDefaultDataset(DatasetType.REGION);
		if (defaultPointDataset != null) {
			targetDatasource.setSelectedItem(defaultRegionDataset.getDatasource());
			targetDataset.setSelectedItem(defaultRegionDataset);
		}
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		Recordset targetRecordset = null;
		Recordset sourceRecordset = null;
		try {
			DatasetVector sourceDataset = null;
			if (parameters.getInputs().getData(INPUT_DATA).getValue() != null) {
				sourceDataset = (DatasetVector) parameters.getInputs().getData(INPUT_DATA).getValue();
			} else {
				sourceDataset = (DatasetVector) this.sourceDataset.getSelectedDataset();
			}

			DatasetVector targetDataset = null;
			targetDataset = (DatasetVector) this.targetDataset.getSelectedDataset();

			// 获得源数据字段
			FieldInfos sourceFieldInfos = sourceDataset.getFieldInfos();
			// 获得目标数据字段
			FieldInfos tragetFieldInfos = targetDataset.getFieldInfos();
			ArrayList<FieldInfo> newFielInfos = new ArrayList();
			for (int i = 0; i < sourceFieldInfos.getCount(); i++) {
				if (sourceFieldInfos.get(i).isSystemField()) {
					continue;
				}
				String sourceFielName = sourceFieldInfos.get(i).getName();
				Boolean isAdd = true;
				for (int j = 0; j < tragetFieldInfos.getCount(); j++) {
					if (tragetFieldInfos.get(j).isSystemField()) {
						continue;
					}
					String tragetFielName = tragetFieldInfos.get(j).getName();
					if (sourceFielName.equals(tragetFielName)) {
						// 目标数据集中含有与源数据集相同的字段名，不进行添加，结束循环
						isAdd = false;
						break;
					}
				}
				if (isAdd) {
					newFielInfos.add(sourceFieldInfos.get(i));
				}
			}

			// 给目标数据集创建新的字段
			int num = 0;
			for (int i = 0; i < newFielInfos.size(); i++) {
				if (targetDataset.isAvailableFieldName((String) newFielInfos.get(i).getName())) {
					FieldInfo fieldInfo = new FieldInfo();
					fieldInfo.setName(newFielInfos.get(i).getName());
					fieldInfo.setType(newFielInfos.get(i).getType());
					tragetFieldInfos.add(fieldInfo);
					num++;
				} else {
					newFielInfos.remove(i);
					i--;
				}
			}

			// 多增加一个字段，记录面数据集中包含的点的个数信息
			FieldInfo fieldInfo = new FieldInfo();
			String stasticInfo = targetDataset.getAvailableFieldName("StasticInfo");
			fieldInfo.setName(stasticInfo);
			fieldInfo.setType(FieldType.INT16);
			tragetFieldInfos.add(fieldInfo);

			// 获得面数据包含的点数据的个数以及对象信息   一组数据格式为（smid面，个数，smid点1、smid点2...）
			int[] numList = targetDataset.getIDsByGeoRelation(sourceDataset, SpatialRelationType.WITHIN, true, true);
			ArrayList<Integer> value = new ArrayList();
			// 对得到的信息进行处理，筛选出面数据中点的个数信息
			for (int i = 1; i < numList.length; i++) {

				value.add(numList[i]);
				i = i + numList[i] + 1;
			}
			// 给“StasticInfo”字段赋值
			targetRecordset = targetDataset.getRecordset(false, CursorType.DYNAMIC);
			targetRecordset.getBatch().setMaxRecordCount(2000);
			targetRecordset.getBatch().begin();
			targetRecordset.moveFirst();
			int n = 0;
			while (!targetRecordset.isEOF()) {
				targetRecordset.setFieldValue("StasticInfo", value.get(n));
				n++;
				targetRecordset.moveNext();
			}
			targetRecordset.getBatch().update();

			// 获得要追加的字段名称集合
			String[] newName = new String[num];
			for (int i = 0; i < newName.length; i++) {
				newName[i] = newFielInfos.get(i).getName();
			}
			sourceRecordset = sourceDataset.getRecordset(false, CursorType.DYNAMIC);
			sourceRecordset.addSteppedListener(steppedListener);
			isSuccessful = targetDataset.updateFields(sourceRecordset, SpatialRelationType.WITHIN, newName, newName, AttributeStatisticsType.VALUE, true, stasticInfo, true);
			this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(targetDataset);
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			if (targetRecordset != null) {
				targetRecordset.close();
				targetRecordset.dispose();
			}
			if (sourceRecordset != null) {
				sourceRecordset.removeSteppedListener(steppedListener);
				sourceRecordset.close();
				sourceRecordset.dispose();
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
		return MetaKeys.CONVERSION_TABULARPOINT_TO_REGION;
	}
}
