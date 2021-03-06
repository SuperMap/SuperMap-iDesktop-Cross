package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.spatialStatistics;

import com.supermap.analyst.spatialstatistics.*;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.data.FieldInfo;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.EqualDatasetConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.process.types.DatasetTypes;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.OutputFrame;
import com.supermap.desktop.utilities.DatasetUtilities;
import com.supermap.desktop.utilities.FieldTypeUtilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;

/**
 * @author XiaJT
 * 地理加权回归分析
 */
public class MetaProcessGeographicWeightedRegression extends MetaProcess {
	private static final String INPUT_SOURCE_DATASET = CoreProperties.getString("String_GroupBox_SourceData");
	private static final String OUTPUT_DATASET = "GeographicWeightedRegression";

	private ParameterDatasourceConstrained datasourceConstraint = new ParameterDatasourceConstrained();
	private ParameterSingleDataset parameterSingleDataset = new ParameterSingleDataset(DatasetType.POINT, DatasetType.LINE, DatasetType.REGION);

	private ParameterComboBox parameterBandWidthType = new ParameterComboBox(ProcessProperties.getString("String_BandWidthType"));
	private ParameterNumber parameterDistanceTolerance = new ParameterNumber(ProcessProperties.getString("String_BandWidthDistanceTolerance"));
	private ParameterFieldGroup parameterExplanatory = new ParameterFieldGroup(ProcessProperties.getString("String_ExplanatoryFields"));
	private ParameterComboBox parameterKernelFunction = new ParameterComboBox(ProcessProperties.getString("String_KernelFunction"));
	private ParameterComboBox parameterKernelType = new ParameterComboBox(ProcessProperties.getString("String_KernelType"));
	private ParameterFieldComboBox parameterModelField = new ParameterFieldComboBox(ProcessProperties.getString("String_ModelField"));
	private ParameterNumber parameterNeighbors = new ParameterNumber(ProcessProperties.getString("String_Neighbors"));

	private ParameterSaveDataset parameterSaveDataset = new ParameterSaveDataset();

	public MetaProcessGeographicWeightedRegression() {
		setTitle(ProcessProperties.getString("String_geographicWeightedRegression"));
		initParameter();
		initParameterState();
		initConstraints();
	}

	private void initParameter() {
		parameterExplanatory.setFieldType(FieldTypeUtilities.getNumericFieldType());
		parameterModelField.setFieldType(FieldTypeUtilities.getNumericFieldType());
		parameterBandWidthType.setItems(new ParameterDataNode(ProcessProperties.getString("String_BindWidthType_AICC"), BandWidthType.AICC),
				new ParameterDataNode(ProcessProperties.getString("String_BindWidthType_BANDWIDTH"), BandWidthType.BANDWIDTH),
				new ParameterDataNode(ProcessProperties.getString("String_BindWidthType_CV"), BandWidthType.CV));
		parameterKernelFunction.setItems(new ParameterDataNode(ProcessProperties.getString("String_KernelFunction_BISQUARE"), KernelFunction.BISQUARE),
				new ParameterDataNode(ProcessProperties.getString("String_KernelFunction_BOXCAR"), KernelFunction.BOXCAR),
				new ParameterDataNode(ProcessProperties.getString("String_KernelFunction_GAUSSIAN"), KernelFunction.GAUSSIAN),
				new ParameterDataNode(ProcessProperties.getString("String_KernelFunction_TRICUBE"), KernelFunction.TRICUBE));
		parameterKernelType.setItems(new ParameterDataNode(ProcessProperties.getString("String_KernelType_ADAPTIVE"), KernelType.ADAPTIVE),
				new ParameterDataNode(ProcessProperties.getString("String_KernelType_FIXED"), KernelType.FIXED));

		ParameterCombine parameterCombineSourceDataset = new ParameterCombine();
		parameterCombineSourceDataset.addParameters(datasourceConstraint);
		parameterCombineSourceDataset.addParameters(parameterSingleDataset);
		parameterCombineSourceDataset.setDescribe(CoreProperties.getString("String_GroupBox_SourceData"));

		ParameterCombine parameterSetting = new ParameterCombine();

		final ParameterSwitch parameterSwitch = new ParameterSwitch();
		parameterSwitch.add("0", parameterDistanceTolerance);
		parameterSwitch.add("1", parameterNeighbors);
		parameterSwitch.switchParameter("1");

		parameterDistanceTolerance.setMinValue(0.0);
		parameterDistanceTolerance.setIsIncludeMin(false);
		parameterNeighbors.setMinValue(2);
		parameterNeighbors.setMaxBit(0);

		final ParameterSwitch parameterSwitchParent = new ParameterSwitch();
		parameterSwitchParent.add("0", parameterSwitch);
		parameterSwitchParent.switchParameter((IParameter) null);
		parameterBandWidthType.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(ParameterComboBox.comboBoxValue)) {
					if (parameterBandWidthType.getSelectedData() == BandWidthType.AICC || parameterBandWidthType.getSelectedData() == BandWidthType.CV) {
						parameterSwitchParent.switchParameter((IParameter) null);
					} else {
						parameterSwitchParent.switchParameter(parameterSwitch);
					}
				}
			}
		});
		parameterKernelType.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(ParameterComboBox.comboBoxValue)) {
					if (parameterKernelType.getSelectedData() == KernelType.ADAPTIVE) {
						parameterSwitch.switchParameter("1");
					} else {
						parameterSwitch.switchParameter("0");
					}
				}
			}
		});


		parameterSetting.addParameters(parameterExplanatory, parameterKernelFunction, parameterModelField,
				parameterBandWidthType, parameterKernelType, parameterSwitchParent);
		parameterSetting.setDescribe(CoreProperties.getString("String_GroupBox_ParamSetting"));

		ParameterCombine parameterResultSet = new ParameterCombine();
		parameterResultSet.setDescribe(CoreProperties.getString("String_ResultSet"));
		parameterResultSet.addParameters(parameterSaveDataset);

		parameters.setParameters(parameterCombineSourceDataset, parameterSetting, parameterResultSet);
		parameters.addInputParameters(INPUT_SOURCE_DATASET, DatasetTypes.VECTOR, parameterCombineSourceDataset);
		parameters.addOutputParameters(OUTPUT_DATASET, ProcessOutputResultProperties.getString("String_Result_Analyst"), DatasetTypes.VECTOR, parameterSaveDataset);
	}

	private void initConstraints() {
		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(datasourceConstraint, ParameterDatasourceConstrained.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(parameterSingleDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasetConstraint equalDatasetConstraint = new EqualDatasetConstraint();
		equalDatasetConstraint.constrained(parameterSingleDataset, ParameterSingleDataset.DATASET_FIELD_NAME);
		equalDatasetConstraint.constrained(parameterExplanatory, ParameterFieldGroup.FIELD_DATASET);
		equalDatasetConstraint.constrained(parameterModelField, ParameterFieldComboBox.DATASET_FIELD_NAME);
	}

	private void initParameterState() {
		DatasetVector defaultDatasetVector = DatasetUtilities.getDefaultDatasetVector();
		if (defaultDatasetVector != null) {
			datasourceConstraint.setSelectedItem(defaultDatasetVector.getDatasource());
			parameterSingleDataset.setSelectedItem(defaultDatasetVector);
			parameterExplanatory.setDataset(defaultDatasetVector);
			parameterModelField.setFieldName(defaultDatasetVector);
		}
		parameterDistanceTolerance.setSelectedItem("");
		parameterNeighbors.setSelectedItem("2");
		parameterSaveDataset.setDefaultDatasetName("result_geoWeightedRegression");
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;

		DatasetVector datasetVector;
		Object value = parameters.getInputs().getData(INPUT_SOURCE_DATASET).getValue();
		if (value != null && value instanceof DatasetVector) {
			datasetVector = (DatasetVector) value;
		} else {
			datasetVector = (DatasetVector) parameterSingleDataset.getSelectedItem();
		}
		// 判断原始数据数量是否大于20
		if (datasetVector.getRecordCount() < 20) {
			Application.getActiveApplication().getOutput().output(MessageFormat.format(ProcessProperties.getString("String_SampleSize_Need_To_More_Than"), 20));
			return false;
		}
		GWRParameter gwrParameter = new GWRParameter();
		BandWidthType bandWidthType = (BandWidthType) parameterBandWidthType.getSelectedData();
		// 带宽方式
		gwrParameter.setBandWidthType(bandWidthType);
		FieldInfo[] selectedFields = parameterExplanatory.getSelectedFields();
		if (selectedFields != null) {
			String[] explanatoryFields = new String[selectedFields.length];
			for (int i = 0; i < selectedFields.length; i++) {
				explanatoryFields[i] = selectedFields[i].getName();
			}
			// 解释字段
			gwrParameter.setExplanatoryFeilds(explanatoryFields);
		} else {
			Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_ExplanatoryFieldsIsNull"));
			return false;
		}
		// 核函数类型
		gwrParameter.setKernelFunction((KernelFunction) parameterKernelFunction.getSelectedData());
		// 建模字段
		gwrParameter.setModelFeild(parameterModelField.getFieldName());
		KernelType kernelType = (KernelType) parameterKernelType.getSelectedData();
		//带宽类型
		gwrParameter.setKernelType(kernelType);

		if (bandWidthType == BandWidthType.BANDWIDTH) {
			if (kernelType == KernelType.ADAPTIVE) {
				// 相邻数目
				gwrParameter.setNeighbors(Integer.valueOf(parameterNeighbors.getSelectedItem()));
			} else {
				try {
					// 带宽范围
					gwrParameter.setDistanceTolerance(Double.valueOf(parameterDistanceTolerance.getSelectedItem()));
				} catch (NumberFormatException e) {
					Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_DistanceToleranceMustOverZero"));
					return false;
				}
			}
		}
		try {
			SpatialRelModeling.addSteppedListener(steppedListener);
			GWRAnalystResult gwrAnalystResult = SpatialRelModeling.geographicWeightedRegression(datasetVector, parameterSaveDataset.getResultDatasource(),
					parameterSaveDataset.getResultDatasource().getDatasets().getAvailableDatasetName(parameterSaveDataset.getDatasetName()), gwrParameter);
			if (gwrAnalystResult != null) {
				isSuccessful = true;
				parameters.getOutputs().getData(OUTPUT_DATASET).setValue(gwrAnalystResult.getResultDataset());
				GWRSummary gwrSummary = gwrAnalystResult.getGWRSummary();
				String result = ProcessProperties.getString("String_AIC") + gwrSummary.getAIC() + "\n"
						+ ProcessProperties.getString("String_AICc") + gwrSummary.getAICc() + "\n"
						+ ProcessProperties.getString("String_BandWidthDistanceTolerance") + gwrSummary.getBandwidth() + "\n"
						+ ProcessProperties.getString("String_Edf") + gwrSummary.getEdf() + "\n"
						+ ProcessProperties.getString("String_EffectiveNumber") + gwrSummary.getEffectiveNumber() + "\n"
						+ ProcessProperties.getString("String_Neighbors") + gwrSummary.getNeighbors() + "\n"
						+ ProcessProperties.getString("String_R²") + gwrSummary.getR2() + "\n"
						+ ProcessProperties.getString("String_R²Adjusted") + gwrSummary.getR2Adjusted() + "\n"
						+ ProcessProperties.getString("String_ResidualSquares") + gwrSummary.getResidualSquares() + "\n"
						+ ProcessProperties.getString("String_Sigma") + gwrSummary.getSigma() + "\n"
						// 输出窗口增加"how to use"链接信息-yuanR2017.9.5

						+ "-----------------------------------------" + "\n"
						+ ProcessProperties.getString("String_Label_DetailedUse")
						+ "http://supermap-idesktop.github.io/SuperMap-iDesktop-Cross/docs/SpatialStatisticalAnalysis/SpatialRelationshipModeling.html?SpatialStatisticalAnalysis";

				// 不显示时间-yuanR2017.9.6
				((OutputFrame) Application.getActiveApplication().getOutput()).setShowTime(false);
				Application.getActiveApplication().getOutput().output(result);
				((OutputFrame) Application.getActiveApplication().getOutput()).setShowTime(true);
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			gwrParameter.dispose();
			SpatialRelModeling.removeSteppedListener(steppedListener);
			return isSuccessful;
		}
	}

	@Override
	public String getKey() {
		return MetaKeys.GEOGRAPHIC_WEIGHTED_REGRESSION;
	}
}
