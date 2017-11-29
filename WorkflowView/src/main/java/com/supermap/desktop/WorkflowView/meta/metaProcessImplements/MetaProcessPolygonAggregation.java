package com.supermap.desktop.WorkflowView.meta.metaProcessImplements;

import com.supermap.data.DatasetType;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.lbs.params.CommonSettingCombine;
import com.supermap.desktop.lbs.params.JobResultResponse;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.events.RunningEvent;
import com.supermap.desktop.process.messageBus.NewMessageBus;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.interfaces.IParameterPanel;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.process.parameters.ParameterPanels.DefaultOpenServerMap;
import com.supermap.desktop.utilities.CursorUtilities;

/**
 * Created by caolp on 2017-05-26.
 * 多边形聚合分析
 */
public class MetaProcessPolygonAggregation extends MetaProcess {
	private ParameterIServerLogin parameterIServerLogin = new ParameterIServerLogin();
	private ParameterInputDataType parameterInputDataType = new ParameterInputDataType();
	private ParameterInputDataType parameterAnalystDataType = new ParameterInputDataType();
	private ParameterComboBox parameterAggregationType = new ParameterComboBox().setDescribe(ProcessProperties.getString("String_AggregationType"));
	private ParameterDefaultValueTextField parameterStaticModel = new ParameterDefaultValueTextField().setDescribe(ControlsProperties.getString("String_StaticModel"));
	private ParameterDefaultValueTextField parameterWeightIndex = new ParameterDefaultValueTextField().setDescribe(ProcessProperties.getString("String_Index"));

	public MetaProcessPolygonAggregation() {
		setTitle(ProcessProperties.getString("String_PolygonAggregation"));
		initComponents();
		initComponentLayout();
		initComponentState();
	}

	private void initComponents() {
		//设置输入数据
		parameterInputDataType.setDescribe(ProcessProperties.getString("String_FileInputPath"));
		parameterInputDataType.setiServerLogin(parameterIServerLogin);
		parameterAnalystDataType.setiServerLogin(parameterIServerLogin);
		ParameterDataNode parameterDataNode = new ParameterDataNode(ProcessProperties.getString("String_PolygonAggregationType"), "SUMMARYREGION");
		parameterAggregationType.setRequired(true);
		parameterAggregationType.setItems(parameterDataNode);
		parameterAggregationType.setSelectedItem(parameterDataNode);
		parameterStaticModel.setTipButtonMessage(ProcessProperties.getString("String_StatisticsModeTip"));
		parameterWeightIndex.setTipButtonMessage(ProcessProperties.getString("String_WeightIndexTip"));
	}

	private void initComponentLayout() {
		ParameterCombine parameterCombineSetting = new ParameterCombine();
		parameterCombineSetting.setDescribe(ProcessProperties.getString("String_setParameter"));
		parameterCombineSetting.addParameters(
				parameterAggregationType,
				parameterAnalystDataType,
				parameterStaticModel,
				parameterWeightIndex);
		parameters.setParameters(
				parameterIServerLogin,
				parameterInputDataType,
				parameterCombineSetting
		);
		parameters.getOutputs().addData("PolygonAggregationResult", ProcessOutputResultProperties.getString("String_PolygonAnalysisResult"), null);
	}

	private void initComponentState() {
		parameterAnalystDataType.resetInputItems(new ParameterDataNode(ProcessProperties.getString("String_BigDataStore"), "3"),
				new ParameterDataNode(ControlsProperties.getString("String_UDBFileFilters"), "1"), new ParameterDataNode(ProcessProperties.getString("String_PG"), "2"));
		parameterAnalystDataType.parameterSwitch.switchParameter("3");
		parameterAnalystDataType.setBool(true);
		parameterInputDataType.setSupportDatasetType(DatasetType.POINT);
		parameterAnalystDataType.setSupportDatasetType(DatasetType.REGION);
	}

	@Override
	public IParameterPanel getComponent() {
		return this.parameters.getPanel();
	}

	@Override
	public boolean execute() {
		boolean isSuccessful;
		try {
			fireRunning(new RunningEvent(this, ProcessProperties.getString("String_Running")));
			CommonSettingCombine input = new CommonSettingCombine("input", "");
			parameterInputDataType.initSourceInput(input);

			CommonSettingCombine analyst = new CommonSettingCombine("analyst", "");
			parameterAnalystDataType.initAnalystInput(analyst, 2);
			CommonSettingCombine fields = new CommonSettingCombine("fields", parameterWeightIndex.getSelectedItem());
			CommonSettingCombine statisticModes = new CommonSettingCombine("statisticModes", parameterStaticModel.getSelectedItem());
			analyst.add(fields, statisticModes);

			CommonSettingCombine type = new CommonSettingCombine("type", parameterAggregationType.getSelectedData().toString());
			CommonSettingCombine commonSettingCombine = new CommonSettingCombine("", "");
			commonSettingCombine.add(input, analyst, type);
			if (null == parameterIServerLogin.getService()) {
				isSuccessful = parameterIServerLogin.login();
				if (!isSuccessful){
					return isSuccessful;
				}
			}
			JobResultResponse response = parameterIServerLogin.getService().queryResult(MetaKeys.POLYGON_AGGREGATION, commonSettingCombine.getFinalJSon());
			CursorUtilities.setWaitCursor();
			if (null != response) {
				NewMessageBus messageBus = new NewMessageBus(response, DefaultOpenServerMap.INSTANCE);
				isSuccessful = messageBus.run();
			} else {
				isSuccessful = false;
			}

			parameters.getOutputs().getData("PolygonAggregationResult").setValue("");// TODO: 2017/6/26 也许没结果,but
		} catch (Exception e) {
			isSuccessful = false;
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			CursorUtilities.setDefaultCursor();
		}

		return isSuccessful;
	}

	@Override
	public String getKey() {
		return MetaKeys.POLYGON_AGGREGATION;
	}
}
