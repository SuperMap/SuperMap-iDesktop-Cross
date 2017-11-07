package com.supermap.desktop.WorkflowView.meta.dataconversion;

import com.supermap.data.*;
import com.supermap.data.conversion.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.implement.UserDefineType.ImportSettingExcel;
import com.supermap.desktop.implement.UserDefineType.ImportSettingGPX;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.events.ParameterValueLegalEvent;
import com.supermap.desktop.process.parameter.events.ParameterValueLegalListener;
import com.supermap.desktop.process.parameter.events.ParameterValueSelectedEvent;
import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.interfaces.datas.types.BasicTypes;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.process.util.EnumParser;
import com.supermap.desktop.properties.CommonProperties;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.ui.controls.SmFileChoose;
import com.supermap.desktop.ui.controls.prjcoordsys.JDialogPrjCoordSysSettings;
import com.supermap.desktop.utilities.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by xie on 2017/9/28.
 * 导入界面创建,事件添加类
 */
public class ImportParameterCreator implements IImportParameterCreator {

	private ParameterCombine parameterCombineResultSet;
	private CopyOnWriteArrayList reflectInfoArray = new CopyOnWriteArrayList();
	private CopyOnWriteArrayList parameterCombineArray = new CopyOnWriteArrayList();
	private ParameterFile parameterFile;
	private boolean isSelectingFile = false;
	private ParameterFile parameterFileFolder;
	private ParameterDatasourceConstrained parameterDatasource;
	private ParameterTextField parameterDataset;
	private ParameterCharset parameterCharset;
	private ParameterComboBox parameterWKTFieldName;
	private ParameterComboBox parameterXFieldName;
	private ParameterComboBox parameterYFieldName;
	private ParameterComboBox parameterZFieldName;
	private ParameterCheckBox parameterImportIndexData;
	private ImportSetting importSetting;

	PropertyChangeListener fileChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (!isSelectingFile && evt.getNewValue() instanceof String && evt.getSource().equals(parameterFile)) {
				try {
					isSelectingFile = true;
					String fileName = (String) evt.getNewValue();
					//set dataset name
					String fileAlis = FileUtilities.getFileAlias(fileName);
					//文件选择器编辑过程中会不断响应，所以未修改到正确的路径时不变。JFileChooserControl是否需要一个编辑提交listener
					if (fileAlis != null) {
						if (parameterDatasource != null && parameterDatasource.getSelectedItem() != null) {
							fileAlis = parameterDatasource.getSelectedItem().getDatasets().getAvailableDatasetName(fileAlis);
						}
						parameterDataset.setSelectedItem(fileAlis);
					}
					//set charset
					if (importSetting instanceof ImportSettingTAB || importSetting instanceof ImportSettingMIF) {
						if (fileName != null && new File(fileName).exists()) {
							importSetting.setSourceFilePath(fileName);
							Charset charset = importSetting.getSourceFileCharset();
							parameterCharset.setSelectedItem(charset);
						}
					}

					/**
					 * 给导入csv面板中的文件选择器也添加监听事件，用于当文件文件路径改变时，对选中的csv文件进行预读，得到可供选择的字段-yuanR
					 */
					if (importSetting instanceof ImportSettingCSV && !(importSetting instanceof ImportSettingGPX) && !(importSetting instanceof ImportSettingExcel)) {
						parameterWKTFieldName.removeAllItems();
						parameterXFieldName.removeAllItems();
						parameterYFieldName.removeAllItems();
						parameterZFieldName.removeAllItems();
						parameterImportIndexData.setSelectedItem(false);
						parameterImportIndexData.setEnabled(false);

						String path = (String) evt.getNewValue();
						if (new File(path).exists() && XlsUtilities.getData(path) != null) {
							String[][] data = XlsUtilities.getData(path);
							String[] tempValues = data[0];
							for (int i = 0, tempLength = tempValues.length; i < tempLength; i++) {
								tempValues[i] = tempValues[i].replace("\"", "");
							}
							String[] indexX = tempValues;
							int geoIndex = -1;
							for (int i = 0, size = indexX.length; i < size; i++) {
								if ("Geometry".equals(indexX[i])) {
									geoIndex = i;
								}
							}
							ParameterDataNode dataNode = null;
							if (geoIndex != -1) {
								dataNode = new ParameterDataNode("Geometry", geoIndex);
								parameterWKTFieldName.addItem(dataNode);
							}
							parameterZFieldName.addItem(new ParameterDataNode("", " "));
							for (int i = 0; i < indexX.length; i++) {
								parameterXFieldName.addItem(new ParameterDataNode(indexX[i], indexX[i]));
								parameterYFieldName.addItem(new ParameterDataNode(indexX[i], indexX[i]));
								parameterZFieldName.addItem(new ParameterDataNode(indexX[i], indexX[i]));
							}
							parameterWKTFieldName.setSelectedItem(dataNode);
							parameterXFieldName.setSelectedItem(indexX[0]);
							parameterYFieldName.setSelectedItem(indexX[0]);
							parameterZFieldName.setSelectedItem(" ");
							parameterImportIndexData.setEnabled(true);
						}
					}
				} finally {
					isSelectingFile = false;
				}
				// 以文件夹的形式选择导入文件，当选定了文件夹，根据文件夹名称自动设置导入数据集的名称-yuanR2017.9.1
			} else if (!isSelectingFile && evt.getNewValue() instanceof String && evt.getSource().equals(parameterFileFolder)) {
				try {
					isSelectingFile = true;
					String fileName = (String) evt.getNewValue();
					//set dataset name
					String fileAlis = fileName.substring(fileName.lastIndexOf(File.separator) + 1, fileName.length());
					if (fileAlis.length() > 0) {
						if (parameterDatasource != null && parameterDatasource.getSelectedItem() != null) {
							fileAlis = parameterDatasource.getSelectedItem().getDatasets().getAvailableDatasetName(fileAlis);
						}
						parameterDataset.setSelectedItem(fileAlis);
					}
				} finally {
					isSelectingFile = false;
				}
			}
		}
	};


	@Override
	public ParameterCombine getParameterCombineResultSet() {
		return parameterCombineResultSet;
	}

	@Override
	public CopyOnWriteArrayList<IParameter> getParameterCombineArray(Object o, String type) {
		if (parameterCombineArray.size() > 0) {
			parameterCombineArray.clear();
		}
		if (o instanceof ImportSetting) {
			this.importSetting = (ImportSetting) o;
		}
		parameterCombineArray.add(getSourceInfoParameterCombine(importSetting, type));
		parameterCombineArray.add(getResultSetParameterCombine(importSetting));
		parameterCombineArray.add(getTransformParameterCombine(importSetting));
		return parameterCombineArray;
	}

	@Override
	public CopyOnWriteArrayList<ReflectInfo> getReflectInfoArray() {
		return reflectInfoArray;
	}

	//结果设置界面
	private IParameter getResultSetParameterCombine(ImportSetting importSetting) {
		if (importSetting == null) return null;
		//Target dataset reflect info
		ReflectInfo targetDatasource = new ReflectInfo();
		targetDatasource.methodName = "setTargetDatasource";
		parameterDatasource = new ParameterDatasourceConstrained();
		parameterDatasource.addValueLegalListener(new ParameterValueLegalListener() {
			@Override
			public boolean isValueLegal(ParameterValueLegalEvent event) {
				if (event.getFieldName().equals(ParameterDatasourceConstrained.DATASOURCE_FIELD_NAME)) {
					Datasource datasource = (Datasource) event.getParameterValue();
					if (datasource.isReadOnly()) {
						return false;
					}
					return true;
				}
				return true;
			}

			@Override
			public Object isValueSelected(ParameterValueSelectedEvent event) {
				return ParameterValueLegalListener.DO_NOT_CARE;
			}
		});
		parameterDatasource.setDescribe(CommonProperties.getString(CommonProperties.Label_Datasource));
		Datasource[] activeDatasources = Application.getActiveApplication().getActiveDatasources();
		if (activeDatasources.length > 0) {
			for (Datasource activeDatasource : activeDatasources) {
				if (!activeDatasource.isReadOnly()) {
					parameterDatasource.setSelectedItem(activeDatasource);
					break;
				}
			}
		} else if (Application.getActiveApplication().getActiveDatasets().length > 0) {
			Datasource datasource = Application.getActiveApplication().getActiveDatasets()[0].getDatasource();
			if (!datasource.isReadOnly()) {
				parameterDatasource.setSelectedItem(datasource);
			}
		}
		targetDatasource.parameter = parameterDatasource;

		final ReflectInfo targetDatasetName = new ReflectInfo();
		targetDatasetName.methodName = "setTargetDatasetName";
		parameterDataset = new ParameterTextField(CommonProperties.getString(CommonProperties.Label_Dataset));
		parameterDataset.setSelectedItem(importSetting.getTargetDatasetName());
		targetDatasetName.parameter = parameterDataset;

		//EncodeType reflect info
		ParameterEnum parameterEncodeType = createEnumParser(importSetting);
		ReflectInfo reflectInfoEncodeType = new ReflectInfo();
		if (null != parameterEncodeType) {
			reflectInfoEncodeType.methodName = "setTargetEncodeType";
			reflectInfoEncodeType.parameter = parameterEncodeType;
			parameterEncodeType.setDescribe(ProcessProperties.getString("label_encodingType"));
		}

		//ImportMode reflect info
		String[] ReflectInfoImportModelValue = new String[]{"NONE", "APPEND", "OVERWRITE"};
		String[] importModel = new String[]{
				ProcessProperties.getString("String_FormImport_None"),
				ProcessProperties.getString("String_FormImport_Append"),
				ProcessProperties.getString("String_FormImport_OverWrite")
		};

		ReflectInfo reflectInfoImportMode = new ReflectInfo();
		reflectInfoImportMode.methodName = "setImportMode";
		ParameterEnum parameterImportMode = null;
		if (!(importSetting instanceof ImportSettingGPX)) {
			parameterImportMode = new ParameterEnum(new EnumParser(ImportMode.class, ReflectInfoImportModelValue, importModel)).setDescribe(ProcessProperties.getString("Label_ImportMode"));
			parameterImportMode.setSelectedItem(ProcessProperties.getString("String_FormImport_None"));
			reflectInfoImportMode.parameter = parameterImportMode;
		}
		ParameterCombine parameterCombineSaveResult = new ParameterCombine(ParameterCombine.VERTICAL).addParameters(parameterDatasource, parameterDataset);
		ParameterCombine parameterCombineSecond;
		if (null != parameterEncodeType && null != parameterImportMode) {
			parameterCombineSecond = new ParameterCombine(ParameterCombine.VERTICAL).addParameters(parameterEncodeType, parameterImportMode);
		} else if (null != parameterImportMode) {
			parameterCombineSecond = new ParameterCombine(ParameterCombine.VERTICAL).addParameters(parameterImportMode);
		} else {
			parameterCombineSecond = new ParameterCombine();
		}
		//#region specifyResultParameter
		//创建字段索引
		ReflectInfo reflectInfoFieldIndex = new ReflectInfo();
		reflectInfoFieldIndex.methodName = null;//创建字段索引不是importsetting参数，而是桌面导入之后单独处理的。--by xiexj
		ParameterCheckBox parameterFieldIndex = new ParameterCheckBox(ProcessProperties.getString("string_checkbox_chckbxFieldIndex"));
		reflectInfoFieldIndex.parameter = parameterFieldIndex;
		//创建字段索引
		ReflectInfo reflectInfoSpatialIndex = new ReflectInfo();
		reflectInfoSpatialIndex.methodName = null;
		ParameterCheckBox parameterSpatialIndex = new ParameterCheckBox(ProcessProperties.getString("string_checkbox_chckbxSpatialIndex"));
		reflectInfoSpatialIndex.parameter = parameterSpatialIndex;
		ParameterCombine parameterCombineDatasetIndex = new ParameterCombine(ParameterCombine.HORIZONTAL).addParameters(parameterSpatialIndex, parameterFieldIndex);
		ReflectInfo reflectInfoDatasetType;
		if (importSetting instanceof ImportSettingGPX) {
			//导入为GPX类型时直接返回
			reflectInfoArray.add(targetDatasource);
			reflectInfoArray.add(targetDatasetName);
			return initResultsetParameterCombine(parameterCombineSaveResult);
		} else if (importSetting instanceof ImportSettingCSV) {
			//导入数据集类型
			reflectInfoArray.add(targetDatasource);
			reflectInfoArray.add(targetDatasetName);
			return initResultsetParameterCombine(parameterCombineSaveResult);
		} else if (importSetting instanceof ImportSettingWOR) {
			reflectInfoArray.add(targetDatasource);
			reflectInfoArray.add(reflectInfoEncodeType);
			reflectInfoArray.add(reflectInfoImportMode);
			return initResultsetParameterCombine(parameterDatasource, parameterEncodeType, parameterImportMode);
		} else if (importSetting instanceof ImportSettingModel3DS || importSetting instanceof ImportSettingModelDXF
				|| importSetting instanceof ImportSettingModelFBX || importSetting instanceof ImportSettingModelOSG
				|| importSetting instanceof ImportSettingModelX) {
			//数据集类型combobox
			//// FIXME: 2017/4/25 comboBox形式，实际设置值为Boolean，需要单独解析
			ParameterDatasetType parameterDatasetTypeEnum = createDatasetTypeEnum(importSetting);
			reflectInfoDatasetType = new ReflectInfo();
			reflectInfoDatasetType.methodName = "setImportingAsCAD";
			reflectInfoDatasetType.parameter = parameterDatasetTypeEnum;
			reflectInfoArray.add(targetDatasetName);
			reflectInfoArray.add(targetDatasource);
			reflectInfoArray.add(reflectInfoDatasetType);
			reflectInfoArray.add(reflectInfoImportMode);
			return initResultsetParameterCombine(new ParameterCombine(ParameterCombine.VERTICAL).addParameters(parameterDatasource, parameterDataset),
					new ParameterCombine(ParameterCombine.VERTICAL).addParameters(parameterDatasetTypeEnum, parameterImportMode));
		} else if (importSetting instanceof ImportSettingTAB || importSetting instanceof ImportSettingMIF
				|| importSetting instanceof ImportSettingDWG || importSetting instanceof ImportSettingDXF
				|| importSetting instanceof ImportSettingKML || importSetting instanceof ImportSettingKMZ
				|| importSetting instanceof ImportSettingMAPGIS || importSetting instanceof ImportSettingDGN
				|| importSetting instanceof ImportSettingGeoJson) {
			ParameterDatasetType parameterDatasetTypeEnum = createDatasetTypeEnum(importSetting);
			reflectInfoDatasetType = new ReflectInfo();
			reflectInfoDatasetType.methodName = "setImportingAsCAD";
			reflectInfoDatasetType.parameter = parameterDatasetTypeEnum;
			reflectInfoArray.add(targetDatasetName);
			reflectInfoArray.add(targetDatasource);
			reflectInfoArray.add(reflectInfoEncodeType);
			reflectInfoArray.add(reflectInfoImportMode);
			reflectInfoArray.add(reflectInfoDatasetType);
			reflectInfoArray.add(reflectInfoSpatialIndex);
			reflectInfoArray.add(reflectInfoFieldIndex);
			if (importSetting instanceof ImportSettingGeoJson) {
				return initResultsetParameterCombine(parameterCombineSaveResult,
						parameterCombineSecond,
						parameterDatasetTypeEnum);
			} else {
				return initResultsetParameterCombine(parameterCombineSaveResult,
						parameterCombineSecond,
						parameterDatasetTypeEnum,
						parameterCombineDatasetIndex);
			}

		} else if (importSetting instanceof ImportSettingJPG || importSetting instanceof ImportSettingJP2 ||
				importSetting instanceof ImportSettingPNG || importSetting instanceof ImportSettingBMP ||
				importSetting instanceof ImportSettingIMG || importSetting instanceof ImportSettingTIF ||
				importSetting instanceof ImportSettingGIF || importSetting instanceof ImportSettingMrSID
				|| importSetting instanceof ImportSettingECW) {
			ParameterDatasetType parameterDatasetTypeEnum = createDatasetTypeEnum(importSetting);
			reflectInfoDatasetType = new ReflectInfo();
			reflectInfoDatasetType.methodName = "setImportingAsGrid";
			reflectInfoDatasetType.parameter = parameterDatasetTypeEnum;
			reflectInfoArray.add(targetDatasetName);
			reflectInfoArray.add(targetDatasource);
			reflectInfoArray.add(reflectInfoEncodeType);
			reflectInfoArray.add(reflectInfoImportMode);
			reflectInfoArray.add(reflectInfoDatasetType);
			return initResultsetParameterCombine(parameterCombineSaveResult, parameterCombineSecond, parameterDatasetTypeEnum);
		} else if (importSetting instanceof ImportSettingSIT || importSetting instanceof ImportSettingGRD ||
				importSetting instanceof ImportSettingGBDEM || importSetting instanceof ImportSettingUSGSDEM ||
				importSetting instanceof ImportSettingSHP || importSetting instanceof ImportSettingE00 ||
				importSetting instanceof ImportSettingDBF || importSetting instanceof ImportSettingBIL ||
				importSetting instanceof ImportSettingBSQ || importSetting instanceof ImportSettingBIP ||
				importSetting instanceof ImportSettingTEMSClutter || importSetting instanceof ImportSettingVCT ||
				importSetting instanceof ImportSettingRAW || importSetting instanceof ImportSettingGJB ||
				importSetting instanceof ImportSettingTEMSVector || importSetting instanceof ImportSettingTEMSBuildingVector
				|| importSetting instanceof ImportSettingFileGDBVector || importSetting instanceof ImportSettingSimpleJson) {
			reflectInfoArray.add(targetDatasetName);
			reflectInfoArray.add(targetDatasource);
			reflectInfoArray.add(reflectInfoEncodeType);
			reflectInfoArray.add(reflectInfoImportMode);
			if (importSetting instanceof ImportSettingGJB || importSetting instanceof ImportSettingTEMSVector
					|| importSetting instanceof ImportSettingTEMSBuildingVector || importSetting instanceof ImportSettingFileGDBVector) {
				parameterDataset.setEnabled(false);
				parameterDataset.setSelectedItem("");
			}
			if (importSetting instanceof ImportSettingSHP) {
				reflectInfoArray.add(reflectInfoSpatialIndex);
				reflectInfoArray.add(reflectInfoFieldIndex);
				return initResultsetParameterCombine(parameterCombineSaveResult, parameterCombineSecond, parameterCombineDatasetIndex);
			} else if (importSetting instanceof ImportSettingE00 || importSetting instanceof ImportSettingGJB
					|| importSetting instanceof ImportSettingTEMSVector || importSetting instanceof ImportSettingTEMSBuildingVector
					|| importSetting instanceof ImportSettingFileGDBVector) {
				reflectInfoArray.add(reflectInfoSpatialIndex);
				return initResultsetParameterCombine(parameterCombineSaveResult, parameterCombineSecond, parameterSpatialIndex);
			} else {
				return initResultsetParameterCombine(parameterCombineSaveResult, parameterCombineSecond);
			}
		} else if (importSetting instanceof ImportSettingLIDAR) {
			ParameterComboBox parameterDatasetType = new ParameterComboBox();
			parameterDatasetType.addItem(new ParameterDataNode(ProcessProperties.getString("String_datasetType2D"), false));
			parameterDatasetType.addItem(new ParameterDataNode(ProcessProperties.getString("String_datasetType3D"), true));
			parameterDatasetType.setDescribe(ProcessProperties.getString("string_label_lblDatasetType"));
			reflectInfoDatasetType = new ReflectInfo();
			reflectInfoDatasetType.methodName = "setImportingAs3D";
			reflectInfoDatasetType.parameter = parameterDatasetType;
			reflectInfoArray.add(targetDatasetName);
			reflectInfoArray.add(targetDatasource);
			reflectInfoArray.add(reflectInfoEncodeType);
			reflectInfoArray.add(reflectInfoImportMode);
			reflectInfoArray.add(reflectInfoDatasetType);
			reflectInfoArray.add(reflectInfoSpatialIndex);
			reflectInfoArray.add(reflectInfoFieldIndex);
			return initResultsetParameterCombine(parameterCombineSaveResult, parameterCombineSecond, parameterDatasetType, parameterSpatialIndex);
		}
		//#endregion
		return null;
	}


	//转换参数界面
	private IParameter getTransformParameterCombine(ImportSetting importSetting) {
		if (importSetting instanceof ImportSettingRAW || importSetting instanceof ImportSettingTEMSClutter
				|| importSetting instanceof ImportSettingBIP || importSetting instanceof ImportSettingBSQ
				|| importSetting instanceof ImportSettingGBDEM || importSetting instanceof ImportSettingUSGSDEM
				|| importSetting instanceof ImportSettingBIL || importSetting instanceof ImportSettingGRD) {
			return getGridTransformParameterCombine();
		}
		if (importSetting instanceof ImportSettingSHP || importSetting instanceof ImportSettingE00
				|| importSetting instanceof ImportSettingLIDAR || importSetting instanceof ImportSettingTAB
				|| importSetting instanceof ImportSettingMIF || importSetting instanceof ImportSettingFileGDBVector) {
			return getGRDTransformParameterCombine();
		}
		if (importSetting instanceof ImportSettingDGN) {
			return getDGNTransformParameterCombine(importSetting);
		}
		if (importSetting instanceof ImportSettingDXF || importSetting instanceof ImportSettingDWG) {
			return getDTransformParameterCombine(importSetting);
		}
		if (importSetting instanceof ImportSettingSIT) {
			return getSITTransformParameterCombine(importSetting);
		}
		if (importSetting instanceof ImportSettingTIF) {
			return getTIFTransformParameterCombine();
		}
		if (importSetting instanceof ImportSettingIMG) {
			return getIMGTransformParameterCombine();
		}
		if (importSetting instanceof ImportSettingMrSID || importSetting instanceof ImportSettingECW) {
			return getSIDTransformParameterCombine();
		}
		if (importSetting instanceof ImportSettingBMP || importSetting instanceof ImportSettingPNG
				|| importSetting instanceof ImportSettingJPG || importSetting instanceof ImportSettingGIF) {
			return getBMPTransformParameterCombine();
		}
		if (importSetting instanceof ImportSettingKML || importSetting instanceof ImportSettingKMZ) {
			return getKMLTransformParameterCombine(importSetting);
		}
		if (importSetting instanceof ImportSettingMAPGIS) {
			return getMapGISTransformParameterCombine(importSetting);

		}
		if (importSetting instanceof ImportSettingCSV) {
			return getCSVTransformParameterCombine(importSetting);
		}
		if (importSetting instanceof ImportSettingModelOSG || importSetting instanceof ImportSettingModelX
				|| importSetting instanceof ImportSettingModelDXF || importSetting instanceof ImportSettingModelFBX
				|| importSetting instanceof ImportSettingModelFLT || importSetting instanceof ImportSettingModel3DS) {
			return getModelTransformParameterCombine();
		}
		return null;
	}

	//源文件信息界面
	private IParameter getSourceInfoParameterCombine(ImportSetting importSetting, String importType) {
		if (importSetting == null) return null;
		ReflectInfo reflectInfoFilePath = new ReflectInfo();
		reflectInfoFilePath.methodName = "setSourceFilePath";
		parameterFile = FileType.createImportFileChooser(importType);
		parameterFile.setDescribe(ProcessProperties.getString("label_ChooseFile"));
		reflectInfoFilePath.parameter = parameterFile;
		// 字符集
		ReflectInfo reflectInfoCharset = new ReflectInfo();
		reflectInfoCharset.methodName = "setSourceFileCharset";
		parameterCharset = new ParameterCharset();
		if ((importSetting instanceof ImportSettingTAB || importSetting instanceof ImportSettingMIF) && importSetting.getTargetDataInfos("").getCount() > 0) {
			ImportDataInfos dataInfos = importSetting.getTargetDataInfos("");
			parameterCharset.setSelectedItem(dataInfos.get(0).getSourceCharset());
			importSetting.setSourceFileCharset(dataInfos.get(0).getSourceCharset());
		} else if (null != importSetting.getSourceFileCharset()) {
			parameterCharset.setSelectedItem(importSetting.getSourceFileCharset());
		}
		parameterCharset.setSelectedItem(importSetting.getSourceFileCharset());
		reflectInfoCharset.parameter = parameterCharset;

		boolean hasCharsetParameter = true;
		if (importSetting instanceof ImportSettingDXF || importSetting instanceof ImportSettingDWG || importSetting instanceof ImportSettingGPX) {
			hasCharsetParameter = false;
		}
		reflectInfoArray.add(reflectInfoFilePath);
		ParameterCombine parameterCombineSourceInfoSet = new ParameterCombine();
		parameterCombineSourceInfoSet.setDescribe(ProcessProperties.getString("String_ImportSettingPanel_SourceFileInfo"));
		parameterFile.addPropertyListener(this.fileChangeListener);
		parameterFile.setValueType(BasicTypes.STRING);
		// 将文件类型选择单选组合框加入面板-yuanR2017.9.1
		if (importSetting instanceof ImportSettingSimpleJson) {
			// 文件/文件夹，单选框,默认选择文件-yuanR2017.9.1
			final ParameterRadioButton parameterRadioButtonFolderOrFile = new ParameterRadioButton();
			ParameterDataNode file = new ParameterDataNode(ProcessProperties.getString("String_Label_SelectFolder"), 0);
			ParameterDataNode folder = new ParameterDataNode(ProcessProperties.getString("String_Label_SelectFile"), 1);
			parameterRadioButtonFolderOrFile.setItems(new ParameterDataNode[]{file, folder});
			parameterRadioButtonFolderOrFile.setSelectedItem(parameterRadioButtonFolderOrFile.getItemAt(1));

			parameterCombineSourceInfoSet.addParameters(parameterRadioButtonFolderOrFile);
			parameterFileFolder = FileType.createImportFolderChooser(importType);
			parameterFileFolder.setDescribe(ProcessProperties.getString("label_ChooseFolder"));
			parameterFileFolder.setEnabled(false);
			ReflectInfo reflectInfoFolderPath = new ReflectInfo();
			reflectInfoFolderPath.methodName = "setSourceFilePath";
			reflectInfoFolderPath.parameter = parameterFileFolder;
			reflectInfoArray.add(reflectInfoFolderPath);
			parameterCombineSourceInfoSet.addParameters(parameterFileFolder);
			parameterFileFolder.addPropertyListener(fileChangeListener);
			parameterFileFolder.setValueType(BasicTypes.STRING);
			parameterRadioButtonFolderOrFile.addPropertyListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					boolean filePathIsSelect = parameterRadioButtonFolderOrFile.getSelectedItem().equals(parameterRadioButtonFolderOrFile.getItemAt(1));
					parameterFile.setEnabled(filePathIsSelect);
					parameterFileFolder.setEnabled(!filePathIsSelect);
					if (!parameterFile.isEnabled) {
						parameterFile.removePropertyListener(fileChangeListener);
						parameterFile.setSelectedItem("");
						parameterFile.addPropertyListener(fileChangeListener);
					}
					if (!parameterFileFolder.isEnabled) {
						parameterFileFolder.removePropertyListener(fileChangeListener);
						parameterFileFolder.setSelectedItem("");
						parameterFileFolder.addPropertyListener(fileChangeListener);
					}
				}
			});
		} else {
			parameterFile.setRequisite(true);
		}
		parameterCombineSourceInfoSet.addParameters(parameterFile);
		if (hasCharsetParameter) {
			reflectInfoArray.add(reflectInfoCharset);
			parameterCombineSourceInfoSet.addParameters(parameterCharset);
		}

		return parameterCombineSourceInfoSet;
	}

	private IParameter initResultsetParameterCombine(IParameter... parameter) {
		parameterCombineResultSet = new ParameterCombine();
		parameterCombineResultSet.setDescribe(CommonProperties.getString("String_ResultSet"));
		parameterCombineResultSet.addParameters(parameter);
		return parameterCombineResultSet;
	}

	private ParameterDatasetType createDatasetTypeEnum(ImportSetting importSetting) {
		ParameterDatasetType result;
		result = new ParameterDatasetType();
		result.setDescribe(ProcessProperties.getString("string_label_lblDatasetType"));
		if (importSetting instanceof ImportSettingModel3DS || importSetting instanceof ImportSettingModelDXF
				|| importSetting instanceof ImportSettingModelFBX || importSetting instanceof ImportSettingModelOSG
				|| importSetting instanceof ImportSettingModelX) {
			result.setSupportedDatasetTypes(new String[]{ProcessProperties.getString("string_comboboxitem_model")});
		} else if (importSetting instanceof ImportSettingTAB || importSetting instanceof ImportSettingMIF
				|| importSetting instanceof ImportSettingDWG || importSetting instanceof ImportSettingDXF
				|| importSetting instanceof ImportSettingKML || importSetting instanceof ImportSettingKMZ
				|| importSetting instanceof ImportSettingMAPGIS || importSetting instanceof ImportSettingDGN
				|| importSetting instanceof ImportSettingGeoJson) {
			result.setSupportedDatasetTypes(new String[]{ProcessProperties.getString("String_DatasetType_CAD")});
			result.setSimpleDatasetShown(true);//显示简单数据集选项
		} else if (importSetting instanceof ImportSettingJPG || importSetting instanceof ImportSettingJP2 ||
				importSetting instanceof ImportSettingPNG || importSetting instanceof ImportSettingBMP ||
				importSetting instanceof ImportSettingIMG || importSetting instanceof ImportSettingTIF ||
				importSetting instanceof ImportSettingGIF || importSetting instanceof ImportSettingMrSID
				|| importSetting instanceof ImportSettingECW) {
			result.setSupportedDatasetTypes(new String[]{ProcessProperties.getString("string_comboboxitem_image"), ProcessProperties.getString("string_comboboxitem_grid")});
		}
		return result;
	}

	private ParameterEnum createEnumParser(ImportSetting importSetting) {
		ParameterEnum result = null;
		EnumParser parser = new EnumParser();
		if (importSetting instanceof ImportSettingWOR || importSetting instanceof ImportSettingTAB
				|| importSetting instanceof ImportSettingMIF || importSetting instanceof ImportSettingDWG
				|| importSetting instanceof ImportSettingDXF || importSetting instanceof ImportSettingKML
				|| importSetting instanceof ImportSettingKMZ || importSetting instanceof ImportSettingMAPGIS
				|| importSetting instanceof ImportSettingDGN || importSetting instanceof ImportSettingLIDAR
				|| importSetting instanceof ImportSettingSHP || importSetting instanceof ImportSettingE00
				|| importSetting instanceof ImportSettingDBF || importSetting instanceof ImportSettingBIL
				|| importSetting instanceof ImportSettingBSQ || importSetting instanceof ImportSettingBIP
				|| importSetting instanceof ImportSettingTEMSClutter || importSetting instanceof ImportSettingVCT
				|| importSetting instanceof ImportSettingRAW || importSetting instanceof ImportSettingGJB
				|| importSetting instanceof ImportSettingTEMSVector || importSetting instanceof ImportSettingTEMSBuildingVector
				|| importSetting instanceof ImportSettingFileGDBVector || importSetting instanceof ImportSettingGeoJson
				|| importSetting instanceof ImportSettingSimpleJson) {
			parser.setEnumNames(new String[]{"NONE", "BYTE", "INT16", "INT24", "INT32"});
			parser.setChName(new String[]{
					EncodeTypeUtilities.toString(EncodeType.NONE),
					EncodeTypeUtilities.toString(EncodeType.BYTE),
					EncodeTypeUtilities.toString(EncodeType.INT16),
					EncodeTypeUtilities.toString(EncodeType.INT24),
					EncodeTypeUtilities.toString(EncodeType.INT32)
			});
			parser.setEnumClass(EncodeType.class);
			parser.parse();
			result = new ParameterEnum(parser);
			result.setSelectedItem(EncodeType.NONE);
		} else if (importSetting instanceof ImportSettingGRD
				|| importSetting instanceof ImportSettingGBDEM || importSetting instanceof ImportSettingUSGSDEM) {
			parser.setEnumNames(new String[]{"NONE", "SGL", "LZW"});
			parser.setChName(new String[]{CommonProperties.getString("String_EncodeType_None"), "SGL", "LZW"});
			parser.setEnumClass(EncodeType.class);
			parser.parse();
			result = new ParameterEnum(parser);
			result.setSelectedItem(EncodeType.NONE);
		} else if (importSetting instanceof ImportSettingJPG ||
				importSetting instanceof ImportSettingPNG || importSetting instanceof ImportSettingBMP ||
				importSetting instanceof ImportSettingIMG || importSetting instanceof ImportSettingTIF ||
				importSetting instanceof ImportSettingGIF || importSetting instanceof ImportSettingMrSID
				|| importSetting instanceof ImportSettingSIT) {
			parser.setChName(new String[]{CommonProperties.getString("String_EncodeType_None"), "DCT", "PNG", "LZW"});
			parser.setEnumNames(new String[]{"NONE", "DCT", "PNG", "LZW"});
			parser.setEnumClass(EncodeType.class);
			parser.parse();
			result = new ParameterEnum(parser);
			result.setSelectedItem(EncodeType.DCT);
		} else if (importSetting instanceof ImportSettingECW) {
			parser.setEnumNames(new String[]{"NONE"});
			parser.setChName(new String[]{CommonProperties.getString("String_EncodeType_None")});
			parser.setEnumClass(EncodeType.class);
			parser.parse();
			result = new ParameterEnum(parser);
			result.setSelectedItem(EncodeType.NONE);
		} else if (importSetting instanceof ImportSettingJP2) {
			parser.setChName(new String[]{CommonProperties.getString("String_EncodeType_None"), "DCT", "SGL", "PNG", "LZW"});
			parser.setEnumNames(new String[]{"NONE", "DCT", "SGL", "PNG", "LZW"});
			parser.setEnumClass(EncodeType.class);
			parser.parse();
			result = new ParameterEnum(parser);
			result.setSelectedItem(EncodeType.DCT);
		}
		return result;
	}

	private IParameter getModelTransformParameterCombine() {
		ParameterTextField textFieldX = new ParameterTextField(CommonProperties.getString("string_longitude"));
		textFieldX.setSelectedItem("0");
		ParameterTextField textFieldY = new ParameterTextField(CommonProperties.getString("string_latitude"));
		textFieldY.setSelectedItem("0");
		ParameterTextField textFieldZ = new ParameterTextField(CommonProperties.getString("string_elevation"));
		textFieldZ.setSelectedItem("0");
		ReflectInfo setPosition = new ReflectInfo();
		setPosition.methodName = "setPosition";
		setPosition.mixReflectInfo = new HashMap<>();
		setPosition.mixReflectInfo.put("setX", textFieldX);
		setPosition.mixReflectInfo.put("setY", textFieldY);
		setPosition.mixReflectInfo.put("setZ", textFieldZ);
		reflectInfoArray.add(setPosition);
		ParameterCombine parameterCombineModelSet = new ParameterCombine();
		parameterCombineModelSet.setDescribe(ProcessProperties.getString("String_modelPoint"));
		parameterCombineModelSet.addParameters(textFieldX, textFieldY, textFieldZ);
		final ReflectInfo setPrjCoordSys = new ReflectInfo();
		setPrjCoordSys.methodName = "setTargetPrjCoordSys";
		ParameterRadioButton parameterRadioButton = new ParameterRadioButton();
		parameterRadioButton.setLayout(ParameterRadioButton.VATICAL);
		ParameterDataNode[] parameterDataNodes = {new ParameterDataNode(ProcessProperties.getString("String_setProject"), true), new ParameterDataNode(ControlsProperties.getString("String_ImportPrjFile"), false)};
		parameterRadioButton.setItems(parameterDataNodes);
		parameterRadioButton.setSelectedItem(parameterDataNodes[0]);
		ReflectInfo chooseFile = new ReflectInfo();
		chooseFile.methodName = "";
		String moduleName = "ImportPrjFile";
		if (!SmFileChoose.isModuleExist(moduleName)) {
			String fileFilters = SmFileChoose.buildFileFilters(
					SmFileChoose.createFileFilter(ControlsProperties.getString("String_ImportPrjFiles"), "prj", "xml"),
					SmFileChoose.createFileFilter(ControlsProperties.getString("String_ImportPrjFileShape"), "prj"),
					SmFileChoose.createFileFilter(ControlsProperties.getString("String_ImportPrjFileXml"), "xml"));
			SmFileChoose.addNewNode(fileFilters, CommonProperties.getString("String_DefaultFilePath"),
					ControlsProperties.getString("String_ImportPrjFile"), moduleName, "OpenMany");
		}

		final ParameterFile parameterFilePrjChoose = new ParameterFile();
		parameterFilePrjChoose.setModuleName(moduleName);
		parameterFilePrjChoose.setEnabled(false);
		chooseFile.parameter = parameterFilePrjChoose;
		ReflectInfo selectButton = new ReflectInfo();
		selectButton.methodName = "";
		final ParameterButton parameterButton = new ParameterButton(ProcessProperties.getString("String_setButton"));
		parameterButton.setEnabled(true);
		selectButton.parameter = parameterButton;
		ReflectInfo textArea = new ReflectInfo();
		textArea.methodName = "";
		final ParameterTextArea parameterTextArea = new ParameterTextArea();
		textArea.parameter = parameterTextArea;
		ParameterCombine parameterCombineProjectSet = new ParameterCombine();
		parameterCombineProjectSet.setDescribe(ProcessProperties.getString("String_setProject"));
		parameterFilePrjChoose.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (parameterFilePrjChoose.getSelectedItem() != null) {
					String filePath = parameterFilePrjChoose.getSelectedItem();

					// 设置投影信息
					if (!StringUtilities.isNullOrEmpty(filePath)) {
						PrjCoordSys newPrjCoordSys = new PrjCoordSys();
						String fileType = FileUtilities.getFileType(filePath);
						boolean isPrjFile;
						if (fileType.equalsIgnoreCase(".prj")) {
							isPrjFile = newPrjCoordSys.fromFile(filePath, PrjFileType.ESRI);
						} else {
							isPrjFile = newPrjCoordSys.fromFile(filePath, PrjFileType.SUPERMAP);
						}
						if (isPrjFile) {
							addPrj(newPrjCoordSys, setPrjCoordSys);
							String prjCoordSysInfo = PrjCoordSysUtilities.getDescription(newPrjCoordSys);
							parameterTextArea.setSelectedItem(prjCoordSysInfo);
						}
					}
				}
			}
		});
		final boolean[] isSelectingChange = new boolean[1];
		parameterRadioButton.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (!isSelectingChange[0]) {
					isSelectingChange[0] = true;
					ParameterDataNode node = (ParameterDataNode) evt.getNewValue();
					boolean select = (boolean) node.getData();
					if (select) {
						parameterButton.setEnabled(select);
						parameterFilePrjChoose.setEnabled(!select);
					} else {
						parameterButton.setEnabled(select);
						parameterFilePrjChoose.setEnabled(!select);
					}
					isSelectingChange[0] = false;
				}
			}
		});
		parameterButton.setActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialogPrjCoordSysSettings dialogPrjCoordSysSettings = new JDialogPrjCoordSysSettings();
				if (dialogPrjCoordSysSettings.showDialog() == DialogResult.OK) {
					PrjCoordSys newPrjCoordSys = dialogPrjCoordSysSettings.getPrjCoordSys();
					addPrj(newPrjCoordSys, setPrjCoordSys);
					String prjCoorSysInfo = PrjCoordSysUtilities.getDescription(newPrjCoordSys);
					parameterTextArea.setSelectedItem(prjCoorSysInfo);
				}
			}
		});
		return initTransformParameterCombine(parameterCombineModelSet, parameterCombineProjectSet.addParameters(
				new ParameterCombine(ParameterCombine.HORIZONTAL).addParameters(parameterRadioButton,
						new ParameterCombine().addParameters(parameterButton, parameterFilePrjChoose)), parameterTextArea));
	}

	private void addPrj(PrjCoordSys newPrjCoorSys, ReflectInfo setPrjCoordSys) {
		setPrjCoordSys.mixReflectInfo = new HashMap<>();
		setPrjCoordSys.mixReflectInfo.put("newPrjCoordSys", newPrjCoorSys);
		reflectInfoArray.add(setPrjCoordSys);
	}

	private IParameter getMapGISTransformParameterCombine(Object importSetting) {
		ReflectInfo setColorIndexFilePath = new ReflectInfo();
		setColorIndexFilePath.methodName = "setColorIndexFilePath";

		ParameterFile colorIndex = new ParameterFile(CommonProperties.getString("String_ColorIndexFile"));
		colorIndex.setModuleName("ColorIndexFile");
		colorIndex.addExtension(ProcessProperties.getString("string_filetype_color"), "wat");
		String filePath = ((ImportSettingMAPGIS) importSetting).getColorIndexFilePath();
		if (!StringUtilities.isNullOrEmpty(filePath)) {
			colorIndex.setSelectedItem(new File(filePath).getAbsolutePath());
		}
		setColorIndexFilePath.parameter = colorIndex;

		reflectInfoArray.add(setColorIndexFilePath);
		return initTransformParameterCombine(setColorIndexFilePath.parameter);
	}

	private IParameter getKMLTransformParameterCombine(Object importSetting) {
		ReflectInfo setUnvisibleObjectIgnored = new ReflectInfo();
		setUnvisibleObjectIgnored.methodName = "setUnvisibleObjectIgnored";
		ParameterCheckBox parameterImportUnvisibleObject = new ParameterCheckBox(CommonProperties.getString("String_ImportUnvisibleObject"));
		parameterImportUnvisibleObject.setSelectedItem(importSetting instanceof ImportSettingKML ? (((ImportSettingKML) importSetting).isUnvisibleObjectIgnored() ? "false" : "true")
				: (((ImportSettingKMZ) importSetting).isUnvisibleObjectIgnored() ? "false" : "true"));
		setUnvisibleObjectIgnored.parameter = parameterImportUnvisibleObject;
		reflectInfoArray.add(setUnvisibleObjectIgnored);
		return initTransformParameterCombine(setUnvisibleObjectIgnored.parameter);
	}

	private IParameter getBMPTransformParameterCombine() {
		ReflectInfo pyramidBuiltInfo = new ReflectInfo();
		pyramidBuiltInfo.methodName = "setPyramidBuilt";
		pyramidBuiltInfo.parameter = new ParameterCheckBox(ControlsProperties.getString("String_Form_BuildDatasetPyramid"));

		ReflectInfo setWorldFilePath = new ReflectInfo();
		setWorldFilePath.methodName = "setWorldFilePath";
		ParameterFile worldFilePath = new ParameterFile(CommonProperties.getString("String_WorldFile"));
		worldFilePath.setModuleName("WorldFile");
		worldFilePath.setModuleType("OpenOne");
		worldFilePath.addExtension(ProcessProperties.getString("string_filetype_tfw"), "tfw");
		setWorldFilePath.parameter = worldFilePath;

		reflectInfoArray.add(pyramidBuiltInfo);
		reflectInfoArray.add(setWorldFilePath);
		return initTransformParameterCombine(pyramidBuiltInfo.parameter, setWorldFilePath.parameter);
	}

	private IParameter getSIDTransformParameterCombine() {
		ReflectInfo importBandMode = new ReflectInfo();
		importBandMode.methodName = "setMultiBandImportMode";
		ParameterEnum parameterBandMode = new ParameterEnum(new EnumParser(MultiBandImportMode.class, new String[]{"SINGLEBAND", "MULTIBAND", "COMPOSITE"},
				new String[]{CommonProperties.getString("String_MultiBand_SingleBand"), CommonProperties.getString("String_MultiBand_MultiBand"),
						CommonProperties.getString("String_MultiBand_Composite")}));
		parameterBandMode.setDescribe(ProcessProperties.getString("String_BandImportMode"));
		parameterBandMode.setSelectedItem(MultiBandImportMode.COMPOSITE);
		importBandMode.parameter = parameterBandMode;
		reflectInfoArray.add(importBandMode);
		return initTransformParameterCombine(parameterBandMode);
	}

	private IParameter getGridTransformParameterCombine() {
		ReflectInfo pyramidBuiltInfo = new ReflectInfo();
		pyramidBuiltInfo.methodName = "setPyramidBuilt";
		pyramidBuiltInfo.parameter = new ParameterCheckBox(ControlsProperties.getString("String_Form_BuildDatasetPyramid"));
		reflectInfoArray.add(pyramidBuiltInfo);
		return initTransformParameterCombine(pyramidBuiltInfo.parameter);
	}

	private IParameter getGRDTransformParameterCombine() {
		ReflectInfo setAttributeIgnored = new ReflectInfo();
		setAttributeIgnored.methodName = "setAttributeIgnored";
		setAttributeIgnored.parameter = new ParameterCheckBox(CommonProperties.getString("String_IngoreProperty"));
		reflectInfoArray.add(setAttributeIgnored);
		return initTransformParameterCombine(setAttributeIgnored.parameter);
	}

	private IParameter getDGNTransformParameterCombine(Object importSetting) {
		ReflectInfo importCellAsPoint = new ReflectInfo();
		importCellAsPoint.methodName = "setImportingCellAsPoint";
		ParameterCheckBox parameterImportingCellAsPoint = new ParameterCheckBox(CommonProperties.getString("String_ImportCellAsPoint"));
		parameterImportingCellAsPoint.setSelectedItem(((ImportSettingDGN) importSetting).isImportingCellAsPoint() ? "true" : "false");
		importCellAsPoint.parameter = parameterImportingCellAsPoint;

		ReflectInfo setImportingByLayer = new ReflectInfo();
		setImportingByLayer.methodName = "setImportingByLayer";
		ParameterCheckBox parameterImportingByLayer = new ParameterCheckBox(CommonProperties.getString("String_MergeLayer"));
		parameterImportingByLayer.setSelectedItem(((ImportSettingDGN) importSetting).isImportingByLayer() ? "false" : "true");
		setImportingByLayer.parameter = parameterImportingByLayer;

		reflectInfoArray.add(importCellAsPoint);
		reflectInfoArray.add(setImportingByLayer);
		return initTransformParameterCombine(parameterImportingCellAsPoint, parameterImportingByLayer);
	}

	private IParameter getSITTransformParameterCombine(Object importSetting) {
		ReflectInfo password = new ReflectInfo();
		password.methodName = "setPassword";
		ParameterPassword parameterPassword = new ParameterPassword(CoreProperties.getString("String_FormLogin_Password"));
		parameterPassword.setSelectedItem(((ImportSettingSIT) importSetting).getPassword());
		password.parameter = parameterPassword;
		reflectInfoArray.add(password);
		return initTransformParameterCombine(password.parameter);
	}

	private IParameter getTIFTransformParameterCombine() {
		ReflectInfo importBandMode = new ReflectInfo();
		importBandMode.methodName = "setMultiBandImportMode";
		ParameterEnum parameterBandMode = new ParameterEnum(new EnumParser(MultiBandImportMode.class, new String[]{"SINGLEBAND", "MULTIBAND", "COMPOSITE"},
				new String[]{CommonProperties.getString("String_MultiBand_SingleBand"), CommonProperties.getString("String_MultiBand_MultiBand"),
						CommonProperties.getString("String_MultiBand_Composite")}));
		parameterBandMode.setSelectedItem(MultiBandImportMode.COMPOSITE);
		parameterBandMode.setDescribe(ProcessProperties.getString("String_BandImportMode"));
		importBandMode.parameter = parameterBandMode;

		ReflectInfo pyramidBuiltInfo = new ReflectInfo();
		pyramidBuiltInfo.methodName = "setPyramidBuilt";
		pyramidBuiltInfo.parameter = new ParameterCheckBox(ControlsProperties.getString("String_Form_BuildDatasetPyramid"));

		ReflectInfo setWorldFilePath = new ReflectInfo();
		setWorldFilePath.methodName = "setWorldFilePath";
		ParameterFile worldFilePath = new ParameterFile(CommonProperties.getString("String_WorldFile"));
		worldFilePath.setModuleName("WorldFile");
		worldFilePath.setModuleType("OpenOne");
		worldFilePath.addExtension(ProcessProperties.getString("string_filetype_tfw"), "tfw");
		setWorldFilePath.parameter = worldFilePath;
		reflectInfoArray.add(importBandMode);
		reflectInfoArray.add(pyramidBuiltInfo);
		reflectInfoArray.add(setWorldFilePath);
		return initTransformParameterCombine(importBandMode.parameter, setWorldFilePath.parameter, pyramidBuiltInfo.parameter);
	}

	private IParameter getIMGTransformParameterCombine() {
		ReflectInfo importBandMode = new ReflectInfo();
		importBandMode.methodName = "setMultiBandImportMode";
		ParameterEnum parameterBandMode = new ParameterEnum(new EnumParser(MultiBandImportMode.class, new String[]{"SINGLEBAND", "MULTIBAND", "COMPOSITE"},
				new String[]{CommonProperties.getString("String_MultiBand_SingleBand"), CommonProperties.getString("String_MultiBand_MultiBand"),
						CommonProperties.getString("String_MultiBand_Composite")}));
		parameterBandMode.setDescribe(ProcessProperties.getString("String_BandImportMode"));
		parameterBandMode.setSelectedItem(MultiBandImportMode.COMPOSITE);
		importBandMode.parameter = parameterBandMode;

		ReflectInfo pyramidBuiltInfo = new ReflectInfo();
		pyramidBuiltInfo.methodName = "setPyramidBuilt";
		pyramidBuiltInfo.parameter = new ParameterCheckBox(ControlsProperties.getString("String_Form_BuildDatasetPyramid"));
		reflectInfoArray.add(importBandMode);
		reflectInfoArray.add(pyramidBuiltInfo);
		return initTransformParameterCombine(importBandMode.parameter, pyramidBuiltInfo.parameter);
	}

	private IParameter initTransformParameterCombine(IParameter... parameter) {
		ParameterCombine parameterCombineTransform = new ParameterCombine();
		parameterCombineTransform.setDescribe(ProcessProperties.getString("String_ParamSet"));
		parameterCombineTransform.addParameters(parameter);
		return parameterCombineTransform;
	}

	private IParameter getCSVTransformParameterCombine(Object importSetting) {
		// 对导入CSV文件参数面板进行重构，支持导入点线面
		// 首行为字段信息
		ReflectInfo setFirstRowIsField = new ReflectInfo();
		setFirstRowIsField.methodName = "setFirstRowIsField";
		ParameterCheckBox parameterSetFirstRowIsField = new ParameterCheckBox(CommonProperties.getString("String_FirstRowisField"));
		parameterSetFirstRowIsField.setSelectedItem("true");
		setFirstRowIsField.parameter = parameterSetFirstRowIsField;
		if (importSetting instanceof ImportSettingExcel || importSetting instanceof ImportSettingGPX) {
			reflectInfoArray.add(setFirstRowIsField);
			return initTransformParameterCombine(setFirstRowIsField.parameter);
		} else {
			// 分隔符
			ReflectInfo setSeparator = new ReflectInfo();
			setSeparator.methodName = "setSeparator";
			ParameterTextField parameterSeparator = new ParameterTextField(CommonProperties.getString("String_Separator"));
			parameterSeparator.setSelectedItem(",");
			setSeparator.parameter = parameterSeparator;
			// 导入空间数据
			ReflectInfo setImportIndexData = new ReflectInfo();
			setSeparator.methodName = "setImportIndexData";
			parameterImportIndexData = new ParameterCheckBox(CommonProperties.getString("String_ImportIndexData"));
			parameterImportIndexData.setSelectedItem(false);
			setImportIndexData.parameter = parameterImportIndexData;
			//设置wkt字段
			ReflectInfo setWKTField = new ReflectInfo();
			setWKTField.methodName = "setWKTField";
			final ParameterRadioButton parameterRadioButtonSetWKTField = new ParameterRadioButton();
			ParameterDataNode[] parameterDataNodes = {new ParameterDataNode(CommonProperties.getString("String_WKTIndex"), true), new ParameterDataNode(CommonProperties.getString("String_XYField"), false)};
			parameterRadioButtonSetWKTField.setItems(parameterDataNodes);
			parameterRadioButtonSetWKTField.setSelectedItem(parameterDataNodes[0]);
			parameterRadioButtonSetWKTField.setEnabled(false);
			setWKTField.parameter = parameterRadioButtonSetWKTField;
			//字段选择器
			ReflectInfo setIndexAsGeometry = new ReflectInfo();
			setIndexAsGeometry.methodName = "setIndexAsGeometry";
			parameterWKTFieldName = new ParameterComboBox(CommonProperties.getString("String_WKTIndex"));
			parameterWKTFieldName.setEnabled(false);
			setIndexAsGeometry.parameter = parameterWKTFieldName;

			ReflectInfo setFieldsAsPoint = new ReflectInfo();
			setFieldsAsPoint.methodName = "setFieldsAsPoint";


			parameterXFieldName = new ParameterComboBox(CommonProperties.getString("string_longitude"));
			parameterXFieldName.setEnabled(false);
			setFieldsAsPoint.mixReflectInfo = new HashMap<>();
			setFieldsAsPoint.mixReflectInfo.put("setXFieldName", parameterXFieldName);

			parameterYFieldName = new ParameterComboBox(CommonProperties.getString("string_latitude"));
			setFieldsAsPoint.mixReflectInfo.put("setYFieldName", parameterYFieldName);
			parameterYFieldName.setEnabled(false);

			parameterZFieldName = new ParameterComboBox(CommonProperties.getString("string_elevation"));
			parameterZFieldName.setEnabled(false);
			setFieldsAsPoint.mixReflectInfo.put("setZFieldName", parameterZFieldName);

			reflectInfoArray.add(setSeparator);
			reflectInfoArray.add(setFirstRowIsField);
			reflectInfoArray.add(setImportIndexData);
			reflectInfoArray.add(setIndexAsGeometry);
			reflectInfoArray.add(setFieldsAsPoint);

			// 增加监听事件
			parameterImportIndexData.addPropertyListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					Boolean isEnabled = Boolean.valueOf(parameterImportIndexData.getSelectedItem());
					parameterRadioButtonSetWKTField.setEnabled(isEnabled);
					parameterWKTFieldName.setEnabled(isEnabled && (Boolean) ((ParameterDataNode) parameterRadioButtonSetWKTField.getSelectedItem()).getData());
					parameterXFieldName.setEnabled(isEnabled && !(Boolean) ((ParameterDataNode) parameterRadioButtonSetWKTField.getSelectedItem()).getData());
					parameterYFieldName.setEnabled(isEnabled && !(Boolean) ((ParameterDataNode) parameterRadioButtonSetWKTField.getSelectedItem()).getData());
					parameterZFieldName.setEnabled(isEnabled && !(Boolean) ((ParameterDataNode) parameterRadioButtonSetWKTField.getSelectedItem()).getData());
				}
			});

			parameterRadioButtonSetWKTField.addPropertyListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					Boolean isEnabled = Boolean.valueOf(parameterImportIndexData.getSelectedItem());
					parameterWKTFieldName.setEnabled(isEnabled && (Boolean) ((ParameterDataNode) parameterRadioButtonSetWKTField.getSelectedItem()).getData());
					parameterXFieldName.setEnabled(isEnabled && !(Boolean) ((ParameterDataNode) parameterRadioButtonSetWKTField.getSelectedItem()).getData());
					parameterYFieldName.setEnabled(isEnabled && !(Boolean) ((ParameterDataNode) parameterRadioButtonSetWKTField.getSelectedItem()).getData());
					parameterZFieldName.setEnabled(isEnabled && !(Boolean) ((ParameterDataNode) parameterRadioButtonSetWKTField.getSelectedItem()).getData());

				}
			});
			return initTransformParameterCombine(setSeparator.parameter, setFirstRowIsField.parameter, setImportIndexData.parameter, setWKTField.parameter,
					parameterWKTFieldName, parameterXFieldName, parameterYFieldName, parameterZFieldName);
		}
	}

	private IParameter getDTransformParameterCombine(Object importSetting) {
		ReflectInfo setCurveSegment = new ReflectInfo();
		setCurveSegment.methodName = "setCurveSegment";
		ReflectInfo setImportingExternalData = new ReflectInfo();
		setImportingExternalData.methodName = "setImportingExternalData";
		ReflectInfo setImportingXRecord = new ReflectInfo();
		setImportingXRecord.methodName = "setImportingXRecord";
		ReflectInfo setImporttingAs3D = new ReflectInfo();
		setImporttingAs3D.methodName = "setImporttingAs3D";
		ReflectInfo setImportingInvisibleLayer = new ReflectInfo();
		setImportingInvisibleLayer.methodName = "setImportingInvisibleLayer";
		ReflectInfo setLWPLineWidthIgnored = new ReflectInfo();
		setLWPLineWidthIgnored.methodName = "setLWPLineWidthIgnored";
		ReflectInfo setImportingByLayer = new ReflectInfo();
		setImportingByLayer.methodName = "setImportingByLayer";
		ReflectInfo setBlockAttributeIgnored = new ReflectInfo();
		setBlockAttributeIgnored.methodName = "setBlockAttributeIgnored";
		ReflectInfo setKeepingParametricPart = new ReflectInfo();
		setKeepingParametricPart.methodName = "setKeepingParametricPart";
		ReflectInfo setImportingBlockAsPoint = new ReflectInfo();
		setImportingBlockAsPoint.methodName = "setImportingBlockAsPoint";

		ParameterTextField parameterTextField = new ParameterTextField(CommonProperties.getString("String_CurveSegment"));
		parameterTextField.setSelectedItem(importSetting instanceof ImportSettingDXF ? ((ImportSettingDXF) importSetting).getCurveSegment() : ((ImportSettingDWG) importSetting).getCurveSegment());
		setCurveSegment.parameter = parameterTextField;

		ParameterCheckBox parameterImportExternalData = new ParameterCheckBox(CommonProperties.getString("string_ImportExtendsData"));
		parameterImportExternalData.setSelectedItem(importSetting instanceof ImportSettingDXF ? (((ImportSettingDXF) importSetting).isImportingExternalData() ? "true" : "false")
				: (((ImportSettingDWG) importSetting).isImportingExternalData() ? "true" : "false"));
		setImportingExternalData.parameter = parameterImportExternalData;

		ParameterCheckBox parameterImportingXRecord = new ParameterCheckBox(CommonProperties.getString("String_ImportExtendsRecord"));
		parameterImportingXRecord.setSelectedItem(importSetting instanceof ImportSettingDXF ? (((ImportSettingDXF) importSetting).isImportingXRecord() ? "true" : "false") :
				(((ImportSettingDWG) importSetting).isImportingXRecord() ? "true" : "false"));
		setImportingXRecord.parameter = parameterImportingXRecord;

		ParameterCheckBox parameterImporttingAs3D = new ParameterCheckBox(CommonProperties.getString("String_SaveHeight"));
		parameterImporttingAs3D.setSelectedItem(importSetting instanceof ImportSettingDXF ? (((ImportSettingDXF) importSetting).isImporttingAs3D() ? "true" : "false")
				: (((ImportSettingDWG) importSetting).isImporttingAs3D() ? "true" : "false"));
		setImporttingAs3D.parameter = parameterImporttingAs3D;

		ParameterCheckBox parameterImportingInvisibleLayer = new ParameterCheckBox(CommonProperties.getString("String_ImportInvisibleLayer"));
		parameterImportingInvisibleLayer.setSelectedItem(importSetting instanceof ImportSettingDXF ? (((ImportSettingDXF) importSetting).isImportingInvisibleLayer() ? "true" : "false")
				: (((ImportSettingDWG) importSetting).isImportingInvisibleLayer() ? "true" : "false"));
		setImportingInvisibleLayer.parameter = parameterImportingInvisibleLayer;

		ParameterCheckBox parameterLWPLineWidthIgnored = new ParameterCheckBox(CommonProperties.getString("String_SaveWPLineWidth"));
		parameterLWPLineWidthIgnored.setSelectedItem(importSetting instanceof ImportSettingDXF ? (((ImportSettingDXF) importSetting).isLWPLineWidthIgnored() ? "false" : "true")
				: (((ImportSettingDWG) importSetting).isLWPLineWidthIgnored() ? "false" : "true"));
		setLWPLineWidthIgnored.parameter = parameterLWPLineWidthIgnored;

		ParameterCheckBox parameterImportingByLayer = new ParameterCheckBox(CommonProperties.getString("String_MergeLayer"));
		parameterImportingByLayer.setSelectedItem(importSetting instanceof ImportSettingDXF ? (((ImportSettingDXF) importSetting).isImportingByLayer() ? "false" : "true")
				: (((ImportSettingDWG) importSetting).isImportingByLayer() ? "false" : "true"));
		setImportingByLayer.parameter = parameterImportingByLayer;

		ParameterCheckBox parameterBlockAttributeIgnored = new ParameterCheckBox(CommonProperties.getString("String_ImportProperty"));
		parameterBlockAttributeIgnored.setSelectedItem(importSetting instanceof ImportSettingDXF ? (((ImportSettingDXF) importSetting).isBlockAttributeIgnored() ? "false" : "true")
				: (((ImportSettingDWG) importSetting).isBlockAttributeIgnored() ? "false" : "true"));
		setBlockAttributeIgnored.parameter = parameterBlockAttributeIgnored;

		ParameterCheckBox parameterKeepingParametricPart = new ParameterCheckBox(CommonProperties.getString("String_SaveField"));
		parameterKeepingParametricPart.setSelectedItem(importSetting instanceof ImportSettingDXF ? (((ImportSettingDXF) importSetting).isKeepingParametricPart() ? "true" : "false")
				: (((ImportSettingDWG) importSetting).isKeepingParametricPart() ? "true" : "false"));
		setKeepingParametricPart.parameter = parameterKeepingParametricPart;

		ParameterCheckBox parameterImportingBlockAsPoint = new ParameterCheckBox(CommonProperties.getString("String_ImportingSymbol"));
		parameterImportingBlockAsPoint.setSelectedItem(importSetting instanceof ImportSettingDXF ? (((ImportSettingDXF) importSetting).isImportingBlockAsPoint() ? "false" : "true")
				: (((ImportSettingDWG) importSetting).isImportingBlockAsPoint() ? "false" : "true"));
		setImportingBlockAsPoint.parameter = parameterImportingBlockAsPoint;
		reflectInfoArray.add(setCurveSegment);
		reflectInfoArray.add(setImportingByLayer);
		reflectInfoArray.add(setImportingInvisibleLayer);
		reflectInfoArray.add(setImporttingAs3D);
		reflectInfoArray.add(setImportingBlockAsPoint);
		reflectInfoArray.add(setBlockAttributeIgnored);
		reflectInfoArray.add(setKeepingParametricPart);
		reflectInfoArray.add(setImportingExternalData);
		reflectInfoArray.add(setImportingXRecord);
		reflectInfoArray.add(setLWPLineWidthIgnored);
		return initTransformParameterCombine(setCurveSegment.parameter,
				new ParameterCombine(ParameterCombine.HORIZONTAL).addParameters(
						new ParameterCombine().addParameters(setImportingByLayer.parameter, setImportingBlockAsPoint.parameter, setImportingExternalData.parameter),
						new ParameterCombine().addParameters(setImportingInvisibleLayer.parameter, setBlockAttributeIgnored.parameter, setImportingXRecord.parameter),
						new ParameterCombine().addParameters(setImporttingAs3D.parameter, setKeepingParametricPart.parameter, setLWPLineWidthIgnored.parameter)));
	}
}
