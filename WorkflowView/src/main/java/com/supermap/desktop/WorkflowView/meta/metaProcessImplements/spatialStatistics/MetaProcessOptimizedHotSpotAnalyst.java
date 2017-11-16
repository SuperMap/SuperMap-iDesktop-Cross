package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.spatialStatistics;

import com.supermap.analyst.spatialstatistics.AggregationMethod;
import com.supermap.analyst.spatialstatistics.ClusteringDistributions;
import com.supermap.analyst.spatialstatistics.OptimizedParameter;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasetConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.process.types.DatasetTypes;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;
import com.supermap.desktop.utilities.StringUtilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author XiaJT
 *         重构界面yuanR2017.9.15
 *         1、根据需选择的数据集类型，对界面进行选择：当是线和面数据集时，所需控件只要字段选择界面
 *         2、当聚合方式虚选择网络时，范围数据集可以为空
 *         3、根据数据集类型重写执行
 */
public class MetaProcessOptimizedHotSpotAnalyst extends MetaProcess {
	private static final String INPUT_SOURCE_DATASET = CoreProperties.getString("String_GroupBox_SourceData");
	private final static String OUTPUT_DATASET = "OptimizedHotSpotResult";
	private ParameterDatasource parameterDatasource;
	private ParameterSingleDataset parameterSingleDataset;
	private ParameterSaveDataset parameterSaveDataset;

	// 线面数据集所需要的面板:
	private ParameterFieldComboBox parameterFieldComboBoxNotPoint;
	// 点数据需要的面板:
	private ParameterFieldComboBox parameterFieldComboBoxPoint;
	private ParameterComboBox parameterComboBoxAggregationMethod;
	private ParameterDatasource parameterDatasourceAggregating;
	private ParameterSingleDataset parameterSingleDatasetAggregating;
	private ParameterDatasource parameterDatasourceBounding;
	// 网络聚合方式范围数据集可以为空
	private ParameterSingleDataset parameterSingleDatasetBounding;

	private ParameterSwitch parameterSwitchDatasetType = new ParameterSwitch();
	private ParameterSwitch parameterSwitchPointFieldisNull = new ParameterSwitch();
	private ParameterSwitch parameterSwitchAggregationMethod = new ParameterSwitch();

	/**
	 * 根据数据类型显示不同的面板
	 * 根据不同的聚合方式选择不同的数据集类型面板
	 */
	private PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (null != parameterSingleDataset.getSelectedDataset()) {
				if (parameterSingleDataset.getSelectedDataset().getType().equals(DatasetType.POINT)) {
					parameterSwitchDatasetType.switchParameter("PointType");
				} else {
					parameterSwitchDatasetType.switchParameter("NotPointType");
				}
			}

			if (parameterComboBoxAggregationMethod.getSelectedData().equals(AggregationMethod.AGGREGATIONPOLYGONS)) {
				parameterSwitchAggregationMethod.switchParameter("AggregationPolygons");
			} else if (parameterComboBoxAggregationMethod.getSelectedData().equals(AggregationMethod.NETWORKPOLYGONS)) {
				parameterSwitchAggregationMethod.switchParameter("Bounding");
			} else {
				parameterSwitchAggregationMethod.switchParameter("AggregationPoints");
			}

			if (StringUtilities.isNullOrEmpty(parameterFieldComboBoxPoint.getFieldName())) {
				parameterSwitchPointFieldisNull.switchParameter("FieldisNull");
			} else {
				parameterSwitchPointFieldisNull.switchParameter("FieldisNotNull");
			}

		}
	};

	public MetaProcessOptimizedHotSpotAnalyst() {
		setTitle(ProcessProperties.getString("String_optimizedHotSpotAnalyst"));
		initParameters();
		initComponentLayout();
		initParameterState();
		initListener();
		initParameterConstraint();
	}

	private void initParameters() {
		parameterDatasource = new ParameterDatasource();
		parameterSingleDataset = new ParameterSingleDataset(DatasetType.REGION, DatasetType.LINE, DatasetType.POINT);
		parameterSaveDataset = new ParameterSaveDataset();

		parameterFieldComboBoxNotPoint = new ParameterFieldComboBox(ProcessProperties.getString("String_AssessmentField"));
		parameterFieldComboBoxNotPoint.setFieldType(fieldType);

		parameterFieldComboBoxPoint = new ParameterFieldComboBox(ProcessProperties.getString("String_AssessmentField")).setShowNullValue(true);
		parameterFieldComboBoxPoint.setFieldType(fieldType);

		parameterComboBoxAggregationMethod = new ParameterComboBox(ProcessProperties.getString("String_AggregationMethod"));
		parameterComboBoxAggregationMethod.addItem(new ParameterDataNode(ProcessProperties.getString("String_AGGREGATION"), AggregationMethod.AGGREGATIONPOLYGONS));
		parameterComboBoxAggregationMethod.addItem(new ParameterDataNode(ProcessProperties.getString("String_NETWORK"), AggregationMethod.NETWORKPOLYGONS));
		parameterComboBoxAggregationMethod.addItem(new ParameterDataNode(ProcessProperties.getString("String_SNAPNEARBYPOINTS"), AggregationMethod.SNAPNEARBYPOINTS));

		parameterDatasourceAggregating = new ParameterDatasource();
		parameterSingleDatasetAggregating = new ParameterSingleDataset(DatasetType.REGION);
		parameterDatasourceBounding = new ParameterDatasource();
		parameterSingleDatasetBounding = new ParameterSingleDataset(DatasetType.REGION).setShowNullValue(true);
		parameterDatasourceBounding.setDescribe(ProcessProperties.getString("String_BoundingPolygons_Datasource"));
		parameterSingleDatasetBounding.setDescribe(ProcessProperties.getString("String_BoundingPolygons_Dataset"));
		parameterDatasourceAggregating.setDescribe(ProcessProperties.getString("String_AggregatingPolygons_Datasource"));
		parameterSingleDatasetAggregating.setDescribe(ProcessProperties.getString("String_AggregatingPolygons_Dataset"));

	}

	private void initComponentLayout() {

		// 聚合面
		ParameterCombine parameterCombineAggregating = new ParameterCombine();
		parameterCombineAggregating.addParameters(parameterDatasourceAggregating);
		parameterCombineAggregating.addParameters(parameterSingleDatasetAggregating);
		// 网格面、聚合点
		ParameterCombine parameterCombineBounding = new ParameterCombine();
		parameterCombineBounding.addParameters(parameterDatasourceBounding);
		parameterCombineBounding.addParameters(parameterSingleDatasetBounding);
		// 聚合方式选择下两种面板
		parameterSwitchAggregationMethod.add("AggregationPolygons", parameterCombineAggregating);
		parameterSwitchAggregationMethod.add("Bounding", parameterCombineBounding);
		parameterSwitchAggregationMethod.add("AggregationPoints", new ParameterCombine());

		// 点类型数据、评估字段不为空
		ParameterCombine parameterCombinePointFieldisNull = new ParameterCombine();
		parameterCombinePointFieldisNull.addParameters(parameterComboBoxAggregationMethod);
		parameterCombinePointFieldisNull.addParameters(parameterSwitchAggregationMethod);

		// 点数据评估字段是否为空下两种面板
		parameterSwitchPointFieldisNull.add("FieldisNull", parameterCombinePointFieldisNull);
		parameterSwitchPointFieldisNull.add("FieldisNotNull", new ParameterCombine());

		// 点数据集参数面板
		ParameterCombine parameterCombinePoint = new ParameterCombine();
		parameterCombinePoint.addParameters(parameterFieldComboBoxPoint);
		parameterCombinePoint.addParameters(parameterSwitchPointFieldisNull);

		// 数据类型下两种面板
		parameterSwitchDatasetType.add("NotPointType", parameterFieldComboBoxNotPoint);
		parameterSwitchDatasetType.add("PointType", parameterCombinePoint);

		// 源数据集
		ParameterCombine parameterCombineSource = new ParameterCombine();
		parameterCombineSource.addParameters(parameterDatasource);
		parameterCombineSource.addParameters(parameterSingleDataset);
		parameterCombineSource.setDescribe(CoreProperties.getString("String_ColumnHeader_SourceData"));
		// 参数面板
		ParameterCombine parameterCombineSet = new ParameterCombine();
		parameterCombineSet.addParameters(parameterSwitchDatasetType);
		parameterCombineSet.setDescribe(ProcessProperties.getString("String_setParameter"));
		// 结果
		ParameterCombine parameterCombineResult = new ParameterCombine();
		parameterCombineResult.addParameters(parameterSaveDataset);
		parameterCombineResult.setDescribe(CoreProperties.getString("String_ResultSet"));

		parameters.setParameters(parameterCombineSource, parameterCombineSet, parameterCombineResult);
		parameters.addInputParameters(INPUT_SOURCE_DATASET, DatasetTypes.VECTOR, parameterCombineSource);
		parameters.addOutputParameters(OUTPUT_DATASET, ProcessOutputResultProperties.getString("String_OptimizedHotSpotAnalystResult"), DatasetTypes.VECTOR, parameterCombineResult);
	}

	private void initParameterState() {
		parameterSaveDataset.setDefaultDatasetName("result_optimizedHotSpot");
		DatasetVector defaultDatasetVector = DatasetUtilities.getDefaultDatasetVector();
		Dataset defaultDatasetRegion = DatasetUtilities.getDefaultDataset(DatasetType.REGION);
		if (defaultDatasetRegion != null) {
			parameterDatasourceAggregating.setSelectedItem(defaultDatasetRegion.getDatasource());
			parameterSingleDatasetAggregating.setSelectedItem(defaultDatasetRegion);
		}

		if (defaultDatasetVector != null) {
			parameterDatasource.setSelectedItem(defaultDatasetVector.getDatasource());
			parameterSingleDataset.setSelectedItem(defaultDatasetVector);
			parameterDatasourceBounding.setSelectedItem(defaultDatasetVector.getDatasource());
			parameterSingleDatasetBounding.setSelectedItem(defaultDatasetVector);

			if (defaultDatasetVector.getType().equals(DatasetType.POINT)) {
				parameterFieldComboBoxPoint.setFieldName(defaultDatasetVector);
				parameterSwitchDatasetType.switchParameter("PointType");
			} else {
				parameterFieldComboBoxNotPoint.setFieldName(defaultDatasetVector);
				parameterSwitchDatasetType.switchParameter("NotPointType");
			}
		}
		parameterComboBoxAggregationMethod.setSelectedItem(AggregationMethod.AGGREGATIONPOLYGONS);
		parameterSwitchAggregationMethod.switchParameter("Aggregating");

		parameterSwitchPointFieldisNull.switchParameter("FieldisNull");
	}

	private void initListener() {
		parameterSingleDataset.addPropertyListener(propertyChangeListener);
		parameterComboBoxAggregationMethod.addPropertyListener(propertyChangeListener);
		parameterFieldComboBoxPoint.addPropertyListener(propertyChangeListener);
	}

	private void initParameterConstraint() {
		DatasourceConstraint.getInstance().constrained(parameterDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		DatasourceConstraint.getInstance().constrained(parameterSaveDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);
		DatasourceConstraint.getInstance().constrained(parameterDatasourceBounding, ParameterDatasource.DATASOURCE_FIELD_NAME);
		DatasourceConstraint.getInstance().constrained(parameterDatasourceAggregating, ParameterDatasource.DATASOURCE_FIELD_NAME);

		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(parameterDatasourceBounding, ParameterDatasource.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(parameterSingleDatasetBounding, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasourceConstraint equalDatasourceConstraint1 = new EqualDatasourceConstraint();
		equalDatasourceConstraint1.constrained(parameterDatasourceAggregating, ParameterDatasource.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint1.constrained(parameterSingleDatasetAggregating, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasourceConstraint equalDatasourceConstraint2 = new EqualDatasourceConstraint();
		equalDatasourceConstraint2.constrained(parameterDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint2.constrained(parameterSingleDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasetConstraint equalDatasetConstraint = new EqualDatasetConstraint();
		equalDatasetConstraint.constrained(parameterSingleDataset, ParameterSingleDataset.DATASET_FIELD_NAME);
		equalDatasetConstraint.constrained(parameterFieldComboBoxNotPoint, ParameterFieldComboBox.DATASET_FIELD_NAME);
		equalDatasetConstraint.constrained(parameterFieldComboBoxPoint, ParameterFieldComboBox.DATASET_FIELD_NAME);
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		DatasetVector datasetVector;
		if (parameters.getInputs().getData(INPUT_SOURCE_DATASET) != null &&
				parameters.getInputs().getData(INPUT_SOURCE_DATASET).getValue() instanceof DatasetVector) {
			datasetVector = (DatasetVector) parameters.getInputs().getData(INPUT_SOURCE_DATASET).getValue();
		} else {
			datasetVector = (DatasetVector) parameterSingleDataset.getSelectedItem();
		}
		// 根据数据集类型进行不同参数设置
		OptimizedParameter optimizedParameter = new OptimizedParameter();
		if (datasetVector.getType().equals(DatasetType.POINT)) {
			if (!StringUtilities.isNullOrEmpty(parameterFieldComboBoxPoint.getFieldName())) {
				optimizedParameter.setAssessmentFieldName(parameterFieldComboBoxPoint.getFieldName());
			} else {
				optimizedParameter.setAggregationMethod((AggregationMethod) parameterComboBoxAggregationMethod.getSelectedData());
				if (optimizedParameter.getAggregationMethod().equals(AggregationMethod.AGGREGATIONPOLYGONS)) {
					optimizedParameter.setAggregatingPolygons((DatasetVector) parameterSingleDatasetAggregating.getSelectedItem());
				} else if (optimizedParameter.getAggregationMethod().equals(AggregationMethod.NETWORKPOLYGONS)) {
					optimizedParameter.setBoundingPolygons((DatasetVector) parameterSingleDatasetBounding.getSelectedItem());
				}
			}
			// 点类型评估字段为空
		} else {
			// 线面类型
			optimizedParameter.setAssessmentFieldName(parameterFieldComboBoxNotPoint.getFieldName());
		}
		try {
			ClusteringDistributions.addSteppedListener(steppedListener);
			DatasetVector result = ClusteringDistributions.optimizedHotSpotAnalyst(datasetVector, parameterSaveDataset.getResultDatasource(),
					parameterSaveDataset.getResultDatasource().getDatasets().getAvailableDatasetName(parameterSaveDataset.getDatasetName()), optimizedParameter);
			this.getParameters().getOutputs().getData(OUTPUT_DATASET).setValue(result);
			isSuccessful = (result != null);
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			ClusteringDistributions.removeSteppedListener(steppedListener);
		}
		return isSuccessful;
	}

	@Override
	public String getKey() {
		return MetaKeys.OPTIMIZED_HOT_SPOT_ANALYST;
	}
}
