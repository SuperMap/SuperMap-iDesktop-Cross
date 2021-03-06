package com.supermap.desktop.process.parameter.ipls;

import com.alibaba.fastjson.JSON;
import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.lbs.IServerServiceImpl;
import com.supermap.desktop.lbs.params.CommonSettingCombine;
import com.supermap.desktop.lbs.params.IServerLoginInfo;
import com.supermap.desktop.lbs.params.QueryDatasetNamesResult;
import com.supermap.desktop.lbs.params.QueryDatasetTypeResult;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.ProcessResources;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.interfaces.IConGetter;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetTypeUtilities;
import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * Created by caolp on 2017-07-27.
 */
public class ParameterInputDataType extends ParameterCombine {
	private IServerServiceImpl service;
	private ParameterComboBox parameterDataInputWay = new ParameterComboBox(ProcessProperties.getString("String_DataInputWay"));
	private ParameterHDFSPath parameterHDFSPath = new ParameterHDFSPath();

	private ParameterTextField parameterDataSourceType = new ParameterTextField(ProcessProperties.getString("String_DataSourceType"));
	private ParameterFile parameterDataSourcePath = new ParameterFile(ProcessProperties.getString("String_DataSourcePath"));
	private ParameterComboBox parameterDatasetName = new ParameterComboBox(CoreProperties.getString("String_Label_Dataset"));
	private ParameterTextField parameterDatasetName1 = new ParameterTextField(ProcessProperties.getString("String_Label_DatasetName"));
	private ParameterComboBox parameterDatasetType = new ParameterComboBox(ProcessProperties.getString("string_label_lblDatasetType"));
	private ParameterDefaultValueTextField parameterSpark = new ParameterDefaultValueTextField(ProcessProperties.getString("String_numSlices"));
	private ParameterSwitch parameterSwitchUDB = new ParameterSwitch();
	private ParameterCombine parameterCombineDatasetInfo = new ParameterCombine();

	public ParameterComboBox parameterSourceDataset = new ParameterComboBox(CoreProperties.getString("String_Label_Dataset"));
	private ParameterTextField parameterEngineType = new ParameterTextField(ProcessProperties.getString("String_EngineType"));
	private ParameterDefaultValueTextField parameterDataBaseName = new ParameterDefaultValueTextField(ProcessProperties.getString("String_DataBaseName"));
	private ParameterDefaultValueTextField parameterTextFieldAddress = new ParameterDefaultValueTextField(CoreProperties.getString("String_Server"));
	private ParameterDefaultValueTextField parameterTextFieldUserName = new ParameterDefaultValueTextField(ProcessProperties.getString("String_UserName"));
	private ParameterPassword parameterTextFieldPassword = new ParameterPassword(ProcessProperties.getString("String_Password"));
	private ParameterButton parameterButton = new ParameterButton(CoreProperties.getString("String_Open"));
	private static final int OVERLAY_ANALYST_GEO = 0;
	private static final int SINGLE_QUERY = 1;
	private static final int POLYGON_AGGREGATION = 2;
	private static final int SUMMARY_REGION = 3;

	public ParameterComboBox bigDataStoreName = new ParameterComboBox(CoreProperties.getString("String_Label_Dataset"));
	public ParameterSwitch parameterSwitch = new ParameterSwitch();
	public DatasetType[] supportDatasetType;
	private Boolean bool = false;
	private ParameterCombine parameterCombine1;
	private static final String DATASETS_URL = "/iserver/services/datacatalog/rest/datacatalog/relationship/datasets";
	private ParameterIServerLogin iServerLogin;

	public ParameterInputDataType() {
		super();
		initComponents();
		registerEvents();
	}

	private void registerEvents() {
		parameterDataInputWay.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(ParameterComboBox.comboBoxValue)) {
					if (parameterDataInputWay.getSelectedData().toString().equals("0")) {
						parameterSwitch.switchParameter("0");
					} else if (parameterDataInputWay.getSelectedData().toString().equals("1")) {
						parameterSwitch.switchParameter("1");
						if (true) {
							parameterCombine1.removeParameter(parameterSpark);
						}
					} else if (parameterDataInputWay.getSelectedData().toString().equals("2")) {
						parameterSwitch.switchParameter("2");
					} else {
						parameterSwitch.switchParameter("3");
					}

				}
			}
		});
		parameterDataSourcePath.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (!StringUtilities.isNullOrEmpty(evt.getNewValue().toString())) {
					if (!new File(evt.getNewValue().toString()).exists()) {
						parameterSwitchUDB.switchParameter("1");
					} else {
						DatasourceConnectionInfo connectionInfo = new DatasourceConnectionInfo();
						connectionInfo.setServer(evt.getNewValue().toString());
						connectionInfo.setEngineType(EngineType.UDB);
						Workspace workspace = new Workspace();

						Datasource datasource = null;
						try {
							datasource = workspace.getDatasources().open(connectionInfo);
						} catch (Exception e) {
							if (parameterSwitchUDB.getCurrentParameter().equals(parameterCombineDatasetInfo)) {
								parameterSwitchUDB.switchParameter("0");
							}
							Application.getActiveApplication().getOutput().output(CoreProperties.getString("String_OpenDatasourceFaild"));
						}
						if (null != datasource) {
							if (parameterSwitchUDB.getCurrentParameter().equals(parameterCombineDatasetInfo)) {
								parameterSwitchUDB.switchParameter("0");
							}
							Datasets datasets = datasource.getDatasets();
							if (parameterDatasetName.getItems().size() > 0) {
								parameterDatasetName.removeAllItems();
							}
							ParameterDataNode datasetNode = null;
							for (int i = 0, size = datasets.getCount(); i < size; i++) {
								for (int j = 0, length = supportDatasetType.length; j < length; j++) {
									if (datasets.get(i).getType() == supportDatasetType[j]) {
										datasetNode = new ParameterDataNode(datasets.get(i).getName(), datasets.get(i).getType().name());
										parameterDatasetName.addItem(datasetNode);
									}
								}
							}
							parameterDatasetName.setSelectedItem(datasetNode);
							datasource.close();
							datasource = null;
							workspace.close();
							workspace = null;
						}
					}
				}
			}
		});
		bigDataStoreName.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("ComboBoxClicked"))
					loginAndInitInputDataType();
			}
		});


		parameterButton.setActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String pgServer = parameterTextFieldAddress.getSelectedItem().toString();
				String pgDatabase = parameterDataBaseName.getSelectedItem().toString();
				String pgUsername = parameterTextFieldUserName.getSelectedItem().toString();
				String pgPassword = parameterTextFieldPassword.getSelectedItem().toString();
				if (!StringUtilities.isNullOrEmpty(pgServer) && !StringUtilities.isNullOrEmpty(pgDatabase)
						&& !StringUtilities.isNullOrEmpty(pgUsername) && !StringUtilities.isNullOrEmpty(pgPassword)) {
					DatasourceConnectionInfo connectionInfo = new DatasourceConnectionInfo();
					connectionInfo.setServer(pgServer);
					connectionInfo.setDatabase(pgDatabase);
					connectionInfo.setUser(pgUsername);
					connectionInfo.setPassword(pgPassword);
					connectionInfo.setEngineType(EngineType.POSTGRESQL);
					Workspace workspace = new Workspace();
					Datasource datasource = null;
					try {
						datasource = workspace.getDatasources().open(connectionInfo);
					} catch (Exception ex) {
						if (parameterSourceDataset.getItems().size() > 0) {
							parameterSourceDataset.removeAllItems();
						}
						Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_OpenDatasetDatasourceFaild"));
					}
					if (null != datasource) {
						Datasets datasets = datasource.getDatasets();
						if (parameterSourceDataset.getItems().size() > 0) {
							parameterSourceDataset.removeAllItems();
						}
						ParameterDataNode datasetNode = null;
						for (int i = 0, size = datasets.getCount(); i < size; i++) {
							for (int j = 0, length = supportDatasetType.length; j < length; j++) {
								if (datasets.get(i).getType() == supportDatasetType[j]) {
									datasetNode = new ParameterDataNode(datasets.get(i).getName(), datasets.get(i).getType().name());
									parameterSourceDataset.addItem(datasetNode);
								}
							}
						}
						parameterSourceDataset.setSelectedItem(datasetNode);
						datasource.close();
						datasource = null;
						workspace.close();
						workspace = null;
					}
				}
			}

		});
	}

	public void loginAndInitInputDataType() {
		boolean result = iServerLogin.login();
		this.service = iServerLogin.service;
		if (!result) {
			removeAllDatasets();
			return;
		}
		if (null != IServerLoginInfo.client && null != parameterTextFieldAddress.getSelectedItem()) {
			removeAllDatasets();
			initBigDataStoreName();
		}
	}

	private void initBigDataStoreName() {
		String ipAndPort = IServerLoginInfo.ipAddr + ":" + IServerLoginInfo.port;
		String datasetsURL = service.HTTP_STR + ipAndPort + DATASETS_URL;
		String resultDatasets = service.query(datasetsURL);
		QueryDatasetNamesResult queryDatasetNamesResult = JSON.parseObject(resultDatasets, QueryDatasetNamesResult.class);
		ParameterDataNode parameterDataNode = null;
		for (int i = 0, size = queryDatasetNamesResult.datasetNames.size(); i < size; i++) {
			String resultDataset = service.query(service.HTTP_STR + ipAndPort + DATASETS_URL + "/" + queryDatasetNamesResult.datasetNames.get(i));
			QueryDatasetTypeResult queryDatasetTypeResult = JSON.parseObject(resultDataset, QueryDatasetTypeResult.class);
			String datasetType = queryDatasetTypeResult.DatasetInfo.type;
			for (int j = 0, length = supportDatasetType.length; j < length; j++) {
				if (supportDatasetType[j].name().equalsIgnoreCase(datasetType)) {
					parameterDataNode = new ParameterDataNode(queryDatasetNamesResult.datasetNames.get(i), datasetType);
					bigDataStoreName.addItem(parameterDataNode);
				}
			}
		}
		bigDataStoreName.setSelectedItem(parameterDataNode);
	}


	private void removeAllDatasets() {
		if (bigDataStoreName.getItems().size() > 0) {
			bigDataStoreName.removeAllItems();
		}
	}

	public void resetInputItems(ParameterDataNode... items) {
		parameterDataInputWay.removeAllItems();
		parameterDataInputWay.setItems(items);
	}

	private void initComponents() {
		parameterDataInputWay.setItems(new ParameterDataNode(ProcessProperties.getString("String_CSVFile"), "0"), new ParameterDataNode(ProcessProperties.getString("String_BigDataStore"), "3"),
				new ParameterDataNode(ProcessProperties.getString("String_UDBFile"), "1"), new ParameterDataNode(ProcessProperties.getString("String_PG"), "2"));
		//csv文件
		parameterHDFSPath.setSelectedItem("hdfs://192.168.20.189:9000/data/newyork_taxi_2013-01_14k.csv");
		ParameterCombine parameterCombine = new ParameterCombine();
		parameterCombine.addParameters(parameterHDFSPath);
		//udb文件
		parameterDataSourceType.setSelectedItem("UDB");
		parameterDataSourceType.setEnabled(false);
		parameterDataSourcePath.setRequired(true);
		parameterDataSourcePath.setModuleName("InputDataTypeDatasource");
		parameterDataSourcePath.addExtension(ProcessProperties.getString("String_UDBFileFilterName"), "udb");
		parameterDataSourcePath.setModuleType("OpenOne");
		parameterDatasetName.setRequired(true);
		parameterDatasetName1.setRequired(true);
		parameterCombineDatasetInfo.addParameters(parameterDatasetName1, parameterDatasetType);

		parameterSwitchUDB.add("0", parameterDatasetName);
		parameterSwitchUDB.add("1", parameterCombineDatasetInfo);
		parameterSpark.setRequired(true);
		parameterSpark.setDefaultWarningValue("36");
		parameterCombine1 = new ParameterCombine();
		parameterCombine1.addParameters(
				parameterDataSourcePath,
				parameterSwitchUDB,
				parameterSpark);

		//pg数据库
		parameterSourceDataset.setRequired(true);
		parameterSourceDataset.setDescribe(ProcessProperties.getString("String_Label_DatasetName"));
		ParameterCombine parameterOpenPG = new ParameterCombine(ParameterCombine.HORIZONTAL);
		parameterOpenPG.addParameters(parameterSourceDataset, parameterButton);
		parameterEngineType.setSelectedItem("POSTGRESQL");
		parameterEngineType.setEnabled(false);
		parameterTextFieldAddress.setRequired(true);
		parameterTextFieldAddress.setDefaultWarningValue("{ip}");
		parameterDataBaseName.setRequired(true);
//		parameterDataBaseName.setDefaultWarningValue("");
		parameterTextFieldUserName.setRequired(true);
//		parameterTextFieldUserName.setDefaultWarningValue("");
		parameterTextFieldPassword.setRequired(true);
//		parameterTextFieldPassword.setSelectedItem("supermap");
		ParameterCombine parameterCombine2 = new ParameterCombine();
		parameterCombine2.addParameters(
				parameterTextFieldAddress,
				parameterDataBaseName,
				parameterTextFieldUserName,
				parameterTextFieldPassword,
				parameterOpenPG
		);

		//BigDataStore
		bigDataStoreName.setRequired(true);
		ParameterCombine parameterCombine3 = new ParameterCombine();
		parameterCombine3.addParameters(bigDataStoreName);
		IConGetter getter = new IConGetter() {
			@Override
			public Icon getICon(ParameterDataNode parameterDataNode) {
				Icon result = null;
				if (null != parameterDataNode && null != parameterDataNode.getData()) {
					if ("POINT".equals(parameterDataNode.getData().toString())) {
						result = ProcessResources.getIcon("/processresources/Image_DatasetPoint_Normal.png");
					}
					if ("LINE".equals(parameterDataNode.getData().toString())) {
						result = ProcessResources.getIcon("/processresources/Image_DatasetLine_Normal.png");
					}
					if ("REGION".equals(parameterDataNode.getData().toString())) {
						result = ProcessResources.getIcon("/processresources/Image_DatasetRegion_Normal.png");
					}
				}
				return result;
			}
		};
		bigDataStoreName.setIConGetter(getter);
		parameterDatasetName.setIConGetter(getter);
		parameterSourceDataset.setIConGetter(getter);
		parameterSwitch.add("0", parameterCombine);
		parameterSwitch.add("1", parameterCombine1);
		parameterSwitch.add("2", parameterCombine2);
		parameterSwitch.add("3", parameterCombine3);
		this.addParameters(parameterDataInputWay, parameterSwitch);
	}

	public void initSourceInput(CommonSettingCombine input) {
		CommonSettingCombine datasetInfo = new CommonSettingCombine("datasetInfo", "");
		if (parameterDataInputWay.getSelectedData().toString().equals("0")) {
			CommonSettingCombine filePath = new CommonSettingCombine("filePath", parameterHDFSPath.getSelectedItem().toString());
			input.add(filePath);
		} else if (parameterDataInputWay.getSelectedData().toString().equals("3")) {
			CommonSettingCombine datasetName = new CommonSettingCombine("datasetName", ((ParameterDataNode) bigDataStoreName.getSelectedItem()).getDescribe());
			input.add(datasetName);
		} else if (parameterDataInputWay.getSelectedData().toString().equals("1")) {
			CommonSettingCombine datasetName = null;
			CommonSettingCombine datasetType = null;
			CommonSettingCombine type = new CommonSettingCombine("type", parameterDataSourceType.getSelectedItem().toString());
			String udbPathStr = parameterDataSourcePath.getSelectedItem().toString();
			String udbPath = udbPathStr.replaceAll("\\\\", "\\\\\\\\");
			CommonSettingCombine url = new CommonSettingCombine("url", udbPath);
			if (parameterSwitchUDB.getCurrentParameter().equals(parameterCombineDatasetInfo)) {
				datasetName = new CommonSettingCombine("datasetName", parameterDatasetName1.getSelectedItem().toString());
				datasetType = new CommonSettingCombine("datasetType", parameterDatasetType.getSelectedData().toString());
			} else {
				ParameterDataNode datasetNode = (ParameterDataNode) parameterDatasetName.getSelectedItem();
				datasetName = new CommonSettingCombine("datasetName", datasetNode.getDescribe());
				datasetType = new CommonSettingCombine("datasetType", datasetNode.getData().toString());
			}
			CommonSettingCombine numSlices = new CommonSettingCombine("numSlices", parameterSpark.getSelectedItem().toString());
			datasetInfo.add(type, url, datasetName, datasetType);
			input.add(datasetInfo, numSlices);
		} else {
			ParameterDataNode sourceDataset = (ParameterDataNode) parameterSourceDataset.getSelectedItem();
			CommonSettingCombine name = new CommonSettingCombine("name", sourceDataset.getDescribe());
			CommonSettingCombine type = new CommonSettingCombine("type", sourceDataset.getData().toString());
			CommonSettingCombine engineType = new CommonSettingCombine("engineType", parameterEngineType.getSelectedItem().toString());
			CommonSettingCombine server = new CommonSettingCombine("server", parameterTextFieldAddress.getSelectedItem().toString());
			CommonSettingCombine dataBase = new CommonSettingCombine("dataBase", parameterDataBaseName.getSelectedItem().toString());
			CommonSettingCombine user = new CommonSettingCombine("user", parameterTextFieldUserName.getSelectedItem().toString());
			CommonSettingCombine password = new CommonSettingCombine("password", parameterTextFieldPassword.getSelectedItem().toString());
			CommonSettingCombine datasourceConnectionInfo = new CommonSettingCombine("datasourceConnectionInfo", "");
			datasourceConnectionInfo.add(engineType, server, dataBase, user, password);
			datasetInfo.add(type, name, datasourceConnectionInfo);
			input.add(datasetInfo);
		}
	}

	public void initAnalystInput(CommonSettingCombine analyst, int m) {
		String datasetBigDataStore = null;
		String datasetPG = null;
		switch (m) {
			case OVERLAY_ANALYST_GEO:
				datasetBigDataStore = "datasetOverlay";
				datasetPG = "inputOverlay";
				break;
			case SINGLE_QUERY:
				datasetBigDataStore = "datasetQuery";
				datasetPG = "inputQuery";
				break;
			case POLYGON_AGGREGATION:
				datasetBigDataStore = "regionDataset";
				datasetPG = "regionDatasource";
				break;
			case SUMMARY_REGION:
				datasetBigDataStore = "regionDataset";
				datasetPG = "regionDatasource";
				break;
		}
		if (parameterDataInputWay.getSelectedData().toString().equals("3")) {
			//HDFS
			CommonSettingCombine datasetOverlayCombine = new CommonSettingCombine(datasetBigDataStore, ((ParameterDataNode) bigDataStoreName.getSelectedItem()).getDescribe());
			analyst.add(datasetOverlayCombine);
		} else if (parameterDataInputWay.getSelectedData().toString().equals("1")) {
			//udb
			String inputOverlayStr = null;
			String udbPathStr = parameterDataSourcePath.getSelectedItem().toString();
			String udbPath = udbPathStr.replaceAll("\\\\", "//");
			if (parameterSwitchUDB.getCurrentParameter().equals(parameterCombineDatasetInfo)) {
				inputOverlayStr = "{\\\"type\\\":\\\"udb\\\",\\\"info\\\":[{\\\"server\\\":\\\"" + udbPath + "\\\",\\\"datasetNames\\\":[\\\"" + parameterDatasetName1.getSelectedItem().toString() + "\\\"]}]}";
			} else {
				ParameterDataNode datasetNode = (ParameterDataNode) parameterDatasetName.getSelectedItem();
				inputOverlayStr = "{\\\"type\\\":\\\"udb\\\",\\\"info\\\":[{\\\"server\\\":\\\"" + udbPath + "\\\",\\\"datasetNames\\\":[\\\"" + datasetNode.getDescribe() + "\\\"]}]}";
			}
			CommonSettingCombine inputOverlayCombine = new CommonSettingCombine(datasetPG, inputOverlayStr);
			analyst.add(inputOverlayCombine);
		} else {
			//pg
			ParameterDataNode sourceDataset = (ParameterDataNode) parameterSourceDataset.getSelectedItem();
			String inputOverlayStr = "{\\\"type\\\":\\\"pg\\\",\\\"info\\\":[{\\\"server\\\":\\\"" + parameterTextFieldAddress.getSelectedItem() + "\\\",\\\"datasetNames\\\":[\\\"" + sourceDataset.getDescribe() + "\\\"],\\\"database\\\":\\\"" + parameterDataBaseName.getSelectedItem() + "\\\",\\\"user\\\":\\\"" + parameterTextFieldUserName.getSelectedItem() + "\\\",\\\"password\\\":\\\"" + parameterTextFieldPassword.getSelectedItem() + "\\\"}]}";
			CommonSettingCombine inputOverlayCombine = new CommonSettingCombine(datasetPG, inputOverlayStr);
			analyst.add(inputOverlayCombine);
		}
	}

	public void setSupportDatasetType(DatasetType... datasetTypes) {
		parameterDatasetType.removeAllItems();
		for (DatasetType datasetType : datasetTypes) {
			parameterDatasetType.addItem(new ParameterDataNode(DatasetTypeUtilities.toString(datasetType), datasetType.name()));
		}
		supportDatasetType = datasetTypes;
	}

	public Boolean getBool() {
		return bool;
	}

	public void setBool(Boolean bool) {
		this.bool = bool;
	}

	public void setiServerLogin(ParameterIServerLogin iServerLogin) {
		this.iServerLogin = iServerLogin;
	}
}


