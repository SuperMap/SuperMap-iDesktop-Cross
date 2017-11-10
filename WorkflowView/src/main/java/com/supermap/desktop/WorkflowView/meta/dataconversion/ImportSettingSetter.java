package com.supermap.desktop.WorkflowView.meta.dataconversion;

import com.supermap.data.DatasetType;
import com.supermap.data.Datasource;
import com.supermap.data.Point3D;
import com.supermap.data.conversion.DataImport;
import com.supermap.data.conversion.ImportSetting;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.interfaces.ISelectionParameter;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.utilities.StringUtilities;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by xie on 2017/4/6.
 */
public class ImportSettingSetter {
	//Utilities class
	private ImportSettingSetter() {
	}

	public static DataImport getDataImport(ImportSetting importSetting, CopyOnWriteArrayList<ReflectInfo> reflectInfoArray) {
		Class importSettingClass = importSetting.getClass();
		try {
			Method[] methods = importSettingClass.getMethods();

			for (Method method : methods) {
				//默认设置导入空数据集
				if (method.getName().equals("setImportEmptyDataset")) {
					method.invoke(importSetting, true);
					break;
				}
			}
			String methodName;
			for (ReflectInfo reflectInfo : reflectInfoArray) {
				for (Method method : methods) {
					methodName = method.getName();
					//todo 设置后有崩溃问题，暂时屏蔽
					if (methodName.equals(reflectInfo.methodName)) {
						Object arg = null;
						if (null != reflectInfo.mixReflectInfo && !reflectInfo.mixReflectInfo.isEmpty()) {
							if (methodName.equals("setTargetPrjCoordSys")) {
								arg = reflectInfo.mixReflectInfo.get("newPrjCoordSys");
							} else if (methodName.equals("setPosition")) {
								Point3D point3D = new Point3D();
								point3D.setX(Double.valueOf(((ISelectionParameter) reflectInfo.mixReflectInfo.get("setX")).getSelectedItem().toString()));
								point3D.setY(Double.valueOf(((ISelectionParameter) reflectInfo.mixReflectInfo.get("setY")).getSelectedItem().toString()));
								point3D.setZ(Double.valueOf(((ISelectionParameter) reflectInfo.mixReflectInfo.get("setZ")).getSelectedItem().toString()));
								arg = point3D;
							} else if (methodName.equals("setFieldsAsPoint")) {
								if (!((ParameterComboBox) reflectInfo.mixReflectInfo.get("setXFieldName")).isEnabled()) {
									continue;
								}
								ArrayList<String> fields = new ArrayList();
								String setXFieldName = (String) ((ParameterComboBox) reflectInfo.mixReflectInfo.get("setXFieldName")).getSelectedData();
								if (!StringUtilities.isNullOrEmpty(setXFieldName) && !" ".equals(setXFieldName)) {
									fields.add(setXFieldName.toString());
								}
								String setYFieldName = (String) ((ParameterComboBox) reflectInfo.mixReflectInfo.get("setYFieldName")).getSelectedData();
								if (!StringUtilities.isNullOrEmpty(setYFieldName) && !" ".equals(setYFieldName)) {
									fields.add(setYFieldName.toString());
								}
								String setZFieldName = (String) ((ParameterComboBox) reflectInfo.mixReflectInfo.get("setZFieldName")).getSelectedData();
								if (!StringUtilities.isNullOrEmpty(setZFieldName) && !" ".equals(setZFieldName)) {
									fields.add(setZFieldName.toString());
								}
								arg = fields.toArray(new String[fields.size()]);
							}
						} else if ("setIndexAsGeometry".equals(methodName)) {
							if (!reflectInfo.parameter.isEnabled()) {
								continue;
							}
							arg = ((ParameterComboBox) reflectInfo.parameter).getSelectedData();
						} else {
							Object selectItem = ((ISelectionParameter) reflectInfo.parameter).getSelectedItem();
							if (null == selectItem || "".equals(selectItem.toString())) {
								break;
							}
							if (reflectInfo.parameter instanceof ParameterDatasetType) {
								//特殊导入为cad/简单数据集/模型数据集的设置
								arg = false;
								Object datasetType = selectItem;
								if (datasetType instanceof DatasetType[]) {
									DatasetType[] tempDataset = (DatasetType[]) datasetType;
									if (null != tempDataset[0] && (tempDataset[0].equals(DatasetType.CAD) || tempDataset[0].equals(DatasetType.GRID))) {
										arg = true;
									}
								}
							} else if (reflectInfo.parameter instanceof ParameterEnum
									|| reflectInfo.parameter instanceof ParameterComboBox
									|| reflectInfo.parameter instanceof ParameterCharset) {

								arg = ((ParameterDataNode) selectItem).getData();
							} else if (reflectInfo.parameter instanceof ParameterCheckBox) {
								if (methodName.equals("setImportingByLayer")
										|| methodName.equals("setUnvisibleObjectIgnored")) {
									arg = "true".equals(selectItem.toString()) ? false : true;
								} else {
									arg = "true".equals(selectItem.toString()) ? true : false;
								}
							} else if (!StringUtilities.isNullOrEmpty(selectItem.toString()) && !(selectItem instanceof Datasource)) {
								if (StringUtilities.isInteger(selectItem.toString())) {
									arg = Integer.valueOf(selectItem.toString());
								} else {
									arg = selectItem.toString();
								}
							} else {
								arg = selectItem;
							}
						}
						if (null != arg) {
							method.invoke(importSetting, arg);
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		DataImport dataImport = new DataImport();
		dataImport.getImportSettings().add(importSetting);
		return dataImport;
	}
}
