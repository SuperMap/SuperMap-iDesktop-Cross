package com.supermap.desktop.WorkflowView.meta.metaProcessImplements;

import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.lbs.params.CommonSettingCombine;
import com.supermap.desktop.lbs.params.JobResultResponse;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.messageBus.NewMessageBus;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.interfaces.IParameterPanel;
import com.supermap.desktop.process.parameter.interfaces.datas.types.BasicTypes;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.process.parameters.ParameterPanels.DefaultOpenServerMap;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.CursorUtilities;

/**
 * Created by xie on 2017/2/10.
 * 计算热度图
 */
public class MetaProcessHeatMap extends MetaProcess {
	private ParameterIServerLogin parameterIServerLogin = new ParameterIServerLogin();
	private ParameterHDFSPath parameterHDFSPath;
	private ParameterTextField parameterTextFieldXIndex = new ParameterTextField(ControlsProperties.getString("String_XIndex"));
	private ParameterTextField parameterTextFieldYIndex = new ParameterTextField(ControlsProperties.getString("String_YIndex"));
	private ParameterTextField parameterTextFieldSeparator = new ParameterTextField(ProcessProperties.getString("String_Separator"));
	private ParameterComboBox parameterCacheType;
	private ParameterTextField parameterBounds;
	private ParameterTextField parameterCacheLevel;
	private ParameterTextField parameterCacheName;
	private ParameterComboBox parameterDatabaseType;
	private ParameterTextField parameterServiceAddress;
	private ParameterTextField parameterDatabase;
	private ParameterTextField parameterVersion;

	ParameterTextArea parameterTextAreaOutPut = new ParameterTextArea();

	public MetaProcessHeatMap() {
		setTitle(ControlsProperties.getString("String_HeatMap"));
		initMetaInfo();
	}

	private void initMetaInfo() {
		parameterTextFieldXIndex.setSelectedItem("10");
		parameterTextFieldYIndex.setSelectedItem("11");
		parameterTextFieldSeparator.setSelectedItem(",");
		parameterTextFieldSeparator.setEnabled(false);
		parameterHDFSPath = new ParameterHDFSPath();
		parameterHDFSPath.setSelectedItem("hdfs://192.168.20.189:9000/data/newyork_taxi_2013-01_14k.csv");
		parameterCacheType = new ParameterComboBox(ProcessProperties.getString("String_CacheType"));
		ParameterDataNode parameterDataNode1 = new ParameterDataNode("heatmap", "heatmap");
		parameterCacheType.setItems(parameterDataNode1);
		parameterCacheType.setSelectedItem(parameterDataNode1);
		//流程图中不支持在地图中绘制范围，范围表示与iServer的表示相同
		parameterBounds = new ParameterTextField().setDescribe(ProcessProperties.getString("String_CacheBounds"));
		parameterBounds.setSelectedItem("-74.050,40.650,-73.850,40.850");
		parameterCacheLevel = new ParameterTextField().setDescribe(ControlsProperties.getString("String_CacheLevel"));
		parameterCacheLevel.setSelectedItem("1");
		parameterCacheName = new ParameterTextField().setDescribe(ControlsProperties.getString("String_CacheName"));
		parameterCacheName.setSelectedItem("test1_heat");
		parameterDatabaseType = new ParameterComboBox(ControlsProperties.getString("String_DatabaseType"));
		ParameterDataNode parameterDataNode = new ParameterDataNode("MongoDB", "MongoDB");
		parameterDatabaseType.setItems(parameterDataNode);
		parameterDatabaseType.setSelectedItem(parameterDataNode);
		parameterServiceAddress = new ParameterTextField().setDescribe(CoreProperties.getString("String_Server"));
		parameterServiceAddress.setSelectedItem("192.168.13.161:27017");
		parameterDatabase = new ParameterTextField().setDescribe(ControlsProperties.getString("String_Database"));
		parameterDatabase.setSelectedItem("test");
		parameterVersion = new ParameterTextField().setDescribe(ControlsProperties.getString("String_Version"));
		parameterVersion.setSelectedItem("V1");

		ParameterCombine parameterCombineSetting = new ParameterCombine();
		parameterCombineSetting.setDescribe(ProcessProperties.getString("String_setParameter"));
		parameterCombineSetting.addParameters(parameterHDFSPath,
				parameterTextFieldXIndex,
				parameterTextFieldYIndex,
				parameterTextFieldSeparator,
				parameterCacheType,
				parameterBounds,
				parameterCacheLevel,
				parameterCacheName,
				parameterDatabaseType,
				parameterServiceAddress,
				parameterDatabase,
				parameterVersion);

		ParameterCombine parameterCombineResult = new ParameterCombine();
		parameterCombineResult.setDescribe(ProcessProperties.getString("String_result"));
		parameterCombineResult.addParameters(parameterTextAreaOutPut);
		parameters.setParameters(
				parameterIServerLogin,
				parameterCombineSetting
		);
		parameters.getOutputs().addData("HeatMapResult", BasicTypes.STRING);
	}

	@Override
	public IParameterPanel getComponent() {
		return this.parameters.getPanel();
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;

		try {
			//热度图分析功能
			CommonSettingCombine filePath = new CommonSettingCombine("filePath", parameterHDFSPath.getSelectedItem().toString());
			CommonSettingCombine xIndex = new CommonSettingCombine("xIndex", parameterTextFieldXIndex.getSelectedItem().toString());
			CommonSettingCombine yIndex = new CommonSettingCombine("yIndex", parameterTextFieldYIndex.getSelectedItem().toString());
			CommonSettingCombine separator = new CommonSettingCombine("separator", parameterTextFieldSeparator.getSelectedItem().toString());
			CommonSettingCombine input = new CommonSettingCombine("input", "");
			input.add(filePath, xIndex, yIndex, separator);

			CommonSettingCombine cacheName = new CommonSettingCombine("cacheName", parameterCacheName.getSelectedItem().toString());
			CommonSettingCombine cacheType = new CommonSettingCombine("cacheType", (String) parameterDatabaseType.getSelectedData());
			CommonSettingCombine serverAdresses = new CommonSettingCombine("serverAdresses", parameterServiceAddress.getSelectedItem().toString());
			CommonSettingCombine database = new CommonSettingCombine("database", parameterDatabase.getSelectedItem().toString());
			CommonSettingCombine version = new CommonSettingCombine("version", parameterVersion.getSelectedItem().toString());
			CommonSettingCombine output = new CommonSettingCombine("output", "");
			output.add(cacheName, cacheType, serverAdresses, database, version);

			CommonSettingCombine imageType = new CommonSettingCombine("imageType", (String) parameterCacheType.getSelectedData());
			CommonSettingCombine bounds = new CommonSettingCombine("bounds", parameterBounds.getSelectedItem().toString());
			CommonSettingCombine level = new CommonSettingCombine("level", parameterCacheLevel.getSelectedItem().toString());
			CommonSettingCombine drawing = new CommonSettingCombine("drawing", "");
			drawing.add(imageType, bounds, level);

			CommonSettingCombine commonSettingCombine = new CommonSettingCombine("", "");
			commonSettingCombine.add(input, output, drawing);
			String finalJSon = commonSettingCombine.getFinalJSon();
			finalJSon = finalJSon.replaceAll("\"" + parameterServiceAddress.getSelectedItem().toString() + "\"", "[\"" + parameterServiceAddress.getSelectedItem().toString() + "\"]");
			JobResultResponse response = parameterIServerLogin.getService().queryResult(MetaKeys.HEAT_MAP, finalJSon);
			CursorUtilities.setWaitCursor();
			if (null != response) {
				NewMessageBus messageBus = new NewMessageBus(response, DefaultOpenServerMap.INSTANCE);
				isSuccessful = messageBus.run();
			} else {
				isSuccessful = false;
			}

			parameters.getOutputs().getData("HeatMapResult").setValue("");
			CursorUtilities.setDefaultCursor();
		} catch (Exception e) {
			isSuccessful = false;
			Application.getActiveApplication().getOutput().output(e.getMessage());
		}
		return isSuccessful;
	}

	@Override
	public String getKey() {
		return MetaKeys.HEAT_MAP;
	}

}
