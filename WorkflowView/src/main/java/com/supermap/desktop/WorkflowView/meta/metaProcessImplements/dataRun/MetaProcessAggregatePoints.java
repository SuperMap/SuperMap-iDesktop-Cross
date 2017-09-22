package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.dataRun;

import com.supermap.analyst.spatialanalyst.Generalization;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.data.Unit;
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

/**
 * Created by yuanR on 2017/7/24
 * 点聚类
 */
public class MetaProcessAggregatePoints extends MetaProcess {

	private final static String INPUT_DATA = CommonProperties.getString("String_GroupBox_SourceData");
	private final static String OUTPUT_DATA = "AggregateResult";

	private ParameterDatasourceConstrained sourceDatasource;
	private ParameterSingleDataset dataset;

	// 距离
	private ParameterNumber parameterNumberDistance;
	// 阀值
	private ParameterNumber parameterNumberMinPilePointCount;
	// 单位
	private ParameterComboBox parameterComboBoxUnit;

	private ParameterSaveDataset saveDataset;

	public MetaProcessAggregatePoints() {
		initParameters();
		initParameterConstraint();
		initParametersState();

	}

	private void initParameters() {
		sourceDatasource = new ParameterDatasourceConstrained();
		dataset = new ParameterSingleDataset(DatasetType.POINT);

		parameterNumberDistance = new ParameterNumber(ProcessProperties.getString("String_AggregatePoints_Distance"));
		parameterNumberDistance.setMaxBit(22);
		parameterNumberDistance.setMinValue(0);
		parameterNumberDistance.setIsIncludeMin(false);
		parameterNumberDistance.setRequisite(true);

		parameterNumberMinPilePointCount = new ParameterNumber(ProcessProperties.getString("String_AggregatePoints_MinPilePointCount"));
		// 防止linux显示不全，先不添加提示图标-yuanR2017.9.21
		//parameterNumberMinPilePointCount.setTipButtonMessage(ProcessProperties.getString("String_AggregatePoints_MinPilePointCountTip"));
		parameterNumberMinPilePointCount.setMaxBit(0);
		parameterNumberMinPilePointCount.setMinValue(2);
		parameterNumberMinPilePointCount.setIsIncludeMin(true);
		parameterNumberMinPilePointCount.setRequisite(true);

		parameterComboBoxUnit = new ParameterComboBox(null);
		parameterComboBoxUnit.addItem(new ParameterDataNode(CommonProperties.getString("String_DistanceUnit_Millimeter"), Unit.MILIMETER));
		parameterComboBoxUnit.addItem(new ParameterDataNode(CommonProperties.getString("String_DistanceUnit_Centimeter"), Unit.CENTIMETER));
		parameterComboBoxUnit.addItem(new ParameterDataNode(CommonProperties.getString("String_DistanceUnit_Decimeter"), Unit.DECIMETER));
		parameterComboBoxUnit.addItem(new ParameterDataNode(CommonProperties.getString("String_DistanceUnit_Meter"), Unit.METER));
		parameterComboBoxUnit.addItem(new ParameterDataNode(CommonProperties.getString("String_DistanceUnit_Kilometer"), Unit.KILOMETER));
		parameterComboBoxUnit.addItem(new ParameterDataNode(CommonProperties.getString("String_DistanceUnit_Mile"), Unit.MILE));
		parameterComboBoxUnit.addItem(new ParameterDataNode(CommonProperties.getString("String_DistanceUnit_Inch"), Unit.INCH));
		parameterComboBoxUnit.addItem(new ParameterDataNode(CommonProperties.getString("String_DistanceUnit_Foot"), Unit.FOOT));

		this.saveDataset = new ParameterSaveDataset();

		// 源数据
		ParameterCombine parameterCombineSourceData = new ParameterCombine();
		parameterCombineSourceData.addParameters(sourceDatasource, dataset);
		parameterCombineSourceData.setDescribe(ControlsProperties.getString("String_GroupBox_SourceDataset"));

		//参数设置
		ParameterCombine parameterCombineParent = new ParameterCombine(ParameterCombine.HORIZONTAL);
		parameterCombineParent.addParameters(parameterNumberDistance);
		parameterCombineParent.addParameters(parameterComboBoxUnit);
		parameterCombineParent.setWeightIndex(0);
		ParameterCombine parameterCombineSet = new ParameterCombine();
		parameterCombineSet.addParameters(parameterCombineParent, parameterNumberMinPilePointCount);
		parameterCombineSet.setDescribe(CommonProperties.getString("String_GroupBox_ParamSetting"));

		// 结果数据
		ParameterCombine parameterCombineResultData = new ParameterCombine();
		parameterCombineResultData.setDescribe(CommonProperties.getString("String_GroupBox_ResultData"));
		parameterCombineResultData.addParameters(saveDataset);

		parameters.setParameters(parameterCombineSourceData, parameterCombineSet, parameterCombineResultData);
		this.parameters.addInputParameters(INPUT_DATA, DatasetTypes.POINT, parameterCombineSourceData);
		this.parameters.addOutputParameters(OUTPUT_DATA, ProcessOutputResultProperties.getString("String_Result_AggregatePoints"), DatasetTypes.REGION, parameterCombineResultData);
	}

	private void initParameterConstraint() {
		Dataset defaultDataset = DatasetUtilities.getDefaultDataset(DatasetType.POINT);
		if (defaultDataset != null) {
			sourceDatasource.setSelectedItem(defaultDataset.getDatasource());
			dataset.setSelectedItem(defaultDataset);
			saveDataset.setResultDatasource(defaultDataset.getDatasource());
		}
		saveDataset.setDefaultDatasetName("result_aggregatePoints");

		parameterNumberDistance.setSelectedItem(1000);
		parameterComboBoxUnit.setSelectedItem(Unit.METER);
		parameterNumberMinPilePointCount.setSelectedItem(4);

	}

	private void initParametersState() {
		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(sourceDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(dataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		DatasourceConstraint.getInstance().constrained(saveDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		try {
			String datasetName = saveDataset.getDatasetName();
			datasetName = saveDataset.getResultDatasource().getDatasets().getAvailableDatasetName(datasetName);
			DatasetVector src = null;
			if (this.getParameters().getInputs().getData(INPUT_DATA) != null
					&& this.getParameters().getInputs().getData(INPUT_DATA).getValue() instanceof DatasetVector) {
				src = (DatasetVector) this.getParameters().getInputs().getData(INPUT_DATA).getValue();
			} else {
				src = (DatasetVector) this.dataset.getSelectedItem();
			}
			Generalization.addSteppedListener(steppedListener);
			Generalization.aggregatePoints(src, Double.valueOf(parameterNumberDistance.getSelectedItem().toString()),
					(Unit) parameterComboBoxUnit.getSelectedData(),
					Integer.valueOf(parameterNumberMinPilePointCount.getSelectedItem().toString()),
					saveDataset.getResultDatasource(), datasetName, null);
			Dataset dataset = saveDataset.getResultDatasource().getDatasets().get(datasetName);
			this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(dataset);
			isSuccessful = dataset != null;
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			Generalization.removeSteppedListener(steppedListener);
		}
		return isSuccessful;
	}

	@Override
	public IParameters getParameters() {
		return parameters;
	}

	@Override
	public String getKey() {
		return MetaKeys.AGGREGATE_POINTS;
	}

	@Override
	public String getTitle() {
		return ProcessProperties.getString("String_AggregatePoints");
	}
}
