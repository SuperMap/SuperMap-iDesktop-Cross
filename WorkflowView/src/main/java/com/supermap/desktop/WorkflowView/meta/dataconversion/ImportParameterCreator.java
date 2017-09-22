package com.supermap.desktop.WorkflowView.meta.dataconversion;

import com.supermap.data.Datasource;
import com.supermap.data.EncodeType;
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
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.process.util.EnumParser;
import com.supermap.desktop.properties.CommonProperties;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.SmFileChoose;
import com.supermap.desktop.utilities.EncodeTypeUtilities;
import com.supermap.desktop.utilities.StringUtilities;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by xie on 2017/3/31.
 */
public class ImportParameterCreator implements IParameterCreator {

	private ParameterCombine parameterCombineSourceInfoSet;
	private ParameterCombine parameterCombineResultSet;
	private ParameterCombine parameterCombineParamSet;
	private ParameterCombine parameterCombineModelSet;
	// simpleJson有文件夹/文件两种导入模式
	private ParameterRadioButton parameterRadioButtonFolderOrFile;
	// simpleJson选择文件夹模式的ParameterFile
	private ParameterFile parameterFileFolder;
	private ParameterFile parameterFile;
	private ParameterFile parameterChooseFile;
	private ParameterButton parameterButton;
	private ParameterTextArea parameterTextArea;
	private ParameterRadioButton parameterRadioButton;
	private ParameterCharset parameterCharset;
	private ParameterDatasourceConstrained parameterDatasource;
	private ParameterTextField parameterDataset;
	private ParameterEnum parameterEncodeType;
	private ParameterEnum parameterImportMode;
	private ParameterDatasetType parameterDatasetTypeEnum;

	/**
	 * 获得文件/文件夹单选框参数，外部添加监听
	 *
	 * @return
	 */
	public ParameterRadioButton getParameterRadioButtonFolderOrFile() {
		return parameterRadioButtonFolderOrFile;
	}

	@Override
	public CopyOnWriteArrayList<ReflectInfo> create(Object importSetting) {
		//转换参数设置
		parameterCombineParamSet = null;
		CopyOnWriteArrayList<ReflectInfo> result = new CopyOnWriteArrayList<>();
		if (importSetting instanceof ImportSettingRAW || importSetting instanceof ImportSettingTEMSClutter
				|| importSetting instanceof ImportSettingBIP || importSetting instanceof ImportSettingBSQ
				|| importSetting instanceof ImportSettingGBDEM || importSetting instanceof ImportSettingUSGSDEM
				|| importSetting instanceof ImportSettingBIL || importSetting instanceof ImportSettingGRD) {
			ReflectInfo pyramidBuiltInfo = new ReflectInfo();
			pyramidBuiltInfo.methodName = "setPyramidBuilt";
			pyramidBuiltInfo.parameter = new ParameterCheckBox(ControlsProperties.getString("String_Form_BuildDatasetPyramid"));
			result.add(pyramidBuiltInfo);
			parameterCombineParamSet = new ParameterCombine();
			parameterCombineParamSet.setDescribe(ProcessProperties.getString("String_ParamSet"));
			parameterCombineParamSet.addParameters(pyramidBuiltInfo.parameter);
			return result;
		}
		if (importSetting instanceof ImportSettingSHP || importSetting instanceof ImportSettingE00
				|| importSetting instanceof ImportSettingLIDAR || importSetting instanceof ImportSettingTAB
				|| importSetting instanceof ImportSettingMIF || importSetting instanceof ImportSettingFileGDBVector) {
			ReflectInfo setAttributeIgnored = new ReflectInfo();
			setAttributeIgnored.methodName = "setAttributeIgnored";
			setAttributeIgnored.parameter = new ParameterCheckBox(CommonProperties.getString("String_IngoreProperty"));
			result.add(setAttributeIgnored);
			parameterCombineParamSet = new ParameterCombine();
			parameterCombineParamSet.setDescribe(ProcessProperties.getString("String_ParamSet"));
			parameterCombineParamSet.addParameters(setAttributeIgnored.parameter);
			return result;
		}
		if (importSetting instanceof ImportSettingDGN) {
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

			result.add(importCellAsPoint);
			result.add(setImportingByLayer);
			parameterCombineParamSet = new ParameterCombine();
			parameterCombineParamSet.setDescribe(ProcessProperties.getString("String_ParamSet"));
			parameterCombineParamSet.addParameters(parameterImportingCellAsPoint, parameterImportingByLayer);
			return result;
		}
		if (importSetting instanceof ImportSettingDXF || importSetting instanceof ImportSettingDWG) {
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

			if (importSetting instanceof ImportSettingDXF) {
				ParameterTextField parameterTextField = new ParameterTextField(CommonProperties.getString("String_CurveSegment"));
				parameterTextField.setSelectedItem(((ImportSettingDXF) importSetting).getCurveSegment());
				setCurveSegment.parameter = parameterTextField;

				ParameterCheckBox parameterImportExternalData = new ParameterCheckBox(CommonProperties.getString("string_ImportExtendsData"));
				parameterImportExternalData.setSelectedItem(((ImportSettingDXF) importSetting).isImportingExternalData() ? "true" : "false");
				setImportingExternalData.parameter = parameterImportExternalData;

				ParameterCheckBox parameterImportingXRecord = new ParameterCheckBox(CommonProperties.getString("String_ImportExtendsRecord"));
				parameterImportingXRecord.setSelectedItem(((ImportSettingDXF) importSetting).isImportingXRecord() ? "true" : "false");
				setImportingXRecord.parameter = parameterImportingXRecord;

				ParameterCheckBox parameterImporttingAs3D = new ParameterCheckBox(CommonProperties.getString("String_SaveHeight"));
				parameterImporttingAs3D.setSelectedItem(((ImportSettingDXF) importSetting).isImporttingAs3D() ? "true" : "false");
				setImporttingAs3D.parameter = parameterImporttingAs3D;

				ParameterCheckBox parameterImportingInvisibleLayer = new ParameterCheckBox(CommonProperties.getString("String_ImportInvisibleLayer"));
				parameterImportingInvisibleLayer.setSelectedItem(((ImportSettingDXF) importSetting).isImportingInvisibleLayer() ? "true" : "false");
				setImportingInvisibleLayer.parameter = parameterImportingInvisibleLayer;

				ParameterCheckBox parameterLWPLineWidthIgnored = new ParameterCheckBox(CommonProperties.getString("String_SaveWPLineWidth"));
				parameterLWPLineWidthIgnored.setSelectedItem(((ImportSettingDXF) importSetting).isLWPLineWidthIgnored() ? "false" : "true");
				setLWPLineWidthIgnored.parameter = parameterLWPLineWidthIgnored;

				ParameterCheckBox parameterImportingByLayer = new ParameterCheckBox(CommonProperties.getString("String_MergeLayer"));
				parameterImportingByLayer.setSelectedItem(((ImportSettingDXF) importSetting).isImportingByLayer() ? "false" : "true");
				setImportingByLayer.parameter = parameterImportingByLayer;

				ParameterCheckBox parameterBlockAttributeIgnored = new ParameterCheckBox(CommonProperties.getString("String_ImportProperty"));
				parameterBlockAttributeIgnored.setSelectedItem(((ImportSettingDXF) importSetting).isBlockAttributeIgnored() ? "false" : "true");
				setBlockAttributeIgnored.parameter = parameterBlockAttributeIgnored;

				ParameterCheckBox parameterKeepingParametricPart = new ParameterCheckBox(CommonProperties.getString("String_SaveField"));
				parameterKeepingParametricPart.setSelectedItem(((ImportSettingDXF) importSetting).isKeepingParametricPart() ? "true" : "false");
				setKeepingParametricPart.parameter = parameterKeepingParametricPart;

				ParameterCheckBox parameterImportingBlockAsPoint = new ParameterCheckBox(CommonProperties.getString("String_ImportingSymbol"));
				parameterImportingBlockAsPoint.setSelectedItem(((ImportSettingDXF) importSetting).isImportingBlockAsPoint() ? "false" : "true");
				setImportingBlockAsPoint.parameter = parameterImportingBlockAsPoint;
			} else {
				ParameterTextField parameterTextField = new ParameterTextField(CommonProperties.getString("String_CurveSegment"));
				parameterTextField.setSelectedItem(((ImportSettingDWG) importSetting).getCurveSegment());
				setCurveSegment.parameter = parameterTextField;

				ParameterCheckBox parameterImportExternalData = new ParameterCheckBox(CommonProperties.getString("string_ImportExtendsData"));
				parameterImportExternalData.setSelectedItem(((ImportSettingDWG) importSetting).isImportingExternalData() ? "true" : "false");
				setImportingExternalData.parameter = parameterImportExternalData;

				ParameterCheckBox parameterImportingXRecord = new ParameterCheckBox(CommonProperties.getString("String_ImportExtendsRecord"));
				parameterImportingXRecord.setSelectedItem(((ImportSettingDWG) importSetting).isImportingXRecord() ? "true" : "false");
				setImportingXRecord.parameter = parameterImportingXRecord;

				ParameterCheckBox parameterImporttingAs3D = new ParameterCheckBox(CommonProperties.getString("String_SaveHeight"));
				parameterImporttingAs3D.setSelectedItem(((ImportSettingDWG) importSetting).isImporttingAs3D() ? "true" : "false");
				setImporttingAs3D.parameter = parameterImporttingAs3D;

				ParameterCheckBox parameterImportingInvisibleLayer = new ParameterCheckBox(CommonProperties.getString("String_ImportInvisibleLayer"));
				parameterImportingInvisibleLayer.setSelectedItem(((ImportSettingDWG) importSetting).isImportingInvisibleLayer() ? "true" : "false");
				setImportingInvisibleLayer.parameter = parameterImportingInvisibleLayer;

				ParameterCheckBox parameterLWPLineWidthIgnored = new ParameterCheckBox(CommonProperties.getString("String_SaveWPLineWidth"));
				parameterLWPLineWidthIgnored.setSelectedItem(((ImportSettingDWG) importSetting).isLWPLineWidthIgnored() ? "false" : "true");
				setLWPLineWidthIgnored.parameter = parameterLWPLineWidthIgnored;

				ParameterCheckBox parameterImportingByLayer = new ParameterCheckBox(CommonProperties.getString("String_MergeLayer"));
				parameterImportingByLayer.setSelectedItem(((ImportSettingDWG) importSetting).isImportingByLayer() ? "false" : "true");
				setImportingByLayer.parameter = parameterImportingByLayer;

				ParameterCheckBox parameterBlockAttributeIgnored = new ParameterCheckBox(CommonProperties.getString("String_ImportProperty"));
				parameterBlockAttributeIgnored.setSelectedItem(((ImportSettingDWG) importSetting).isBlockAttributeIgnored() ? "false" : "true");
				setBlockAttributeIgnored.parameter = parameterBlockAttributeIgnored;

				ParameterCheckBox parameterKeepingParametricPart = new ParameterCheckBox(CommonProperties.getString("String_SaveField"));
				parameterKeepingParametricPart.setSelectedItem(((ImportSettingDWG) importSetting).isKeepingParametricPart() ? "true" : "false");
				setKeepingParametricPart.parameter = parameterKeepingParametricPart;

				ParameterCheckBox parameterImportingBlockAsPoint = new ParameterCheckBox(CommonProperties.getString("String_ImportingSymbol"));
				parameterImportingBlockAsPoint.setSelectedItem(((ImportSettingDWG) importSetting).isImportingBlockAsPoint() ? "false" : "true");
				setImportingBlockAsPoint.parameter = parameterImportingBlockAsPoint;
			}
			result.add(setCurveSegment);
			result.add(setImportingByLayer);
			result.add(setImportingInvisibleLayer);
			result.add(setImporttingAs3D);
			result.add(setImportingBlockAsPoint);
			result.add(setBlockAttributeIgnored);
			result.add(setKeepingParametricPart);
			result.add(setImportingExternalData);
			result.add(setImportingXRecord);
			result.add(setLWPLineWidthIgnored);
			parameterCombineParamSet = new ParameterCombine();
			parameterCombineParamSet.setDescribe(ProcessProperties.getString("String_ParamSet"));
			parameterCombineParamSet.addParameters(
					setCurveSegment.parameter,
					new ParameterCombine(ParameterCombine.HORIZONTAL).addParameters(
							new ParameterCombine().addParameters(setImportingByLayer.parameter, setImportingBlockAsPoint.parameter, setImportingExternalData.parameter),
							new ParameterCombine().addParameters(setImportingInvisibleLayer.parameter, setBlockAttributeIgnored.parameter, setImportingXRecord.parameter),
							new ParameterCombine().addParameters(setImporttingAs3D.parameter, setKeepingParametricPart.parameter, setLWPLineWidthIgnored.parameter)));
			return result;
		}
		if (importSetting instanceof ImportSettingSIT) {
			ReflectInfo password = new ReflectInfo();
			password.methodName = "setPassword";
			ParameterPassword parameterPassword = new ParameterPassword(CoreProperties.getString("String_FormLogin_Password"));
			parameterPassword.setSelectedItem(((ImportSettingSIT) importSetting).getPassword());
			password.parameter = parameterPassword;
			result.add(password);
			parameterCombineParamSet = new ParameterCombine();
			parameterCombineParamSet.setDescribe(ProcessProperties.getString("String_ParamSet"));
			parameterCombineParamSet.addParameters(password.parameter);
			return result;
		}
		if (importSetting instanceof ImportSettingTIF) {
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

			result.add(importBandMode);
			result.add(pyramidBuiltInfo);
			result.add(setWorldFilePath);
			parameterCombineParamSet = new ParameterCombine();
			parameterCombineParamSet.setDescribe(ProcessProperties.getString("String_ParamSet"));
			parameterCombineParamSet.addParameters(importBandMode.parameter, setWorldFilePath.parameter, pyramidBuiltInfo.parameter);
			return result;
		}
		if (importSetting instanceof ImportSettingIMG) {
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
			result.add(importBandMode);
			result.add(pyramidBuiltInfo);
			parameterCombineParamSet = new ParameterCombine();
			parameterCombineParamSet.setDescribe(ProcessProperties.getString("String_ParamSet"));
			parameterCombineParamSet.addParameters(importBandMode.parameter, pyramidBuiltInfo.parameter);
			return result;
		}
		if (importSetting instanceof ImportSettingMrSID || importSetting instanceof ImportSettingECW) {
			ReflectInfo importBandMode = new ReflectInfo();
			importBandMode.methodName = "setMultiBandImportMode";
			ParameterEnum parameterBandMode = new ParameterEnum(new EnumParser(MultiBandImportMode.class, new String[]{"SINGLEBAND", "MULTIBAND", "COMPOSITE"},
					new String[]{CommonProperties.getString("String_MultiBand_SingleBand"), CommonProperties.getString("String_MultiBand_MultiBand"),
							CommonProperties.getString("String_MultiBand_Composite")}));
			parameterBandMode.setDescribe(ProcessProperties.getString("String_BandImportMode"));
			parameterBandMode.setSelectedItem(MultiBandImportMode.COMPOSITE);
			importBandMode.parameter = parameterBandMode;
			result.add(importBandMode);
			parameterCombineParamSet = new ParameterCombine();
			parameterCombineParamSet.setDescribe(ProcessProperties.getString("String_ParamSet"));
			parameterCombineParamSet.addParameters(parameterBandMode);
			return result;
		}
		if (importSetting instanceof ImportSettingBMP || importSetting instanceof ImportSettingPNG
				|| importSetting instanceof ImportSettingJPG || importSetting instanceof ImportSettingGIF) {
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

			result.add(pyramidBuiltInfo);
			result.add(setWorldFilePath);
			parameterCombineParamSet = new ParameterCombine();
			parameterCombineParamSet.setDescribe(ProcessProperties.getString("String_ParamSet"));
			parameterCombineParamSet.addParameters(pyramidBuiltInfo.parameter, setWorldFilePath.parameter);
			return result;
		}
		if (importSetting instanceof ImportSettingKML || importSetting instanceof ImportSettingKMZ) {
			ReflectInfo setUnvisibleObjectIgnored = new ReflectInfo();
			setUnvisibleObjectIgnored.methodName = "setUnvisibleObjectIgnored";
			ParameterCheckBox parameterImportUnvisibleObject = new ParameterCheckBox(CommonProperties.getString("String_ImportUnvisibleObject"));
			if (importSetting instanceof ImportSettingKML) {
				parameterImportUnvisibleObject.setSelectedItem(((ImportSettingKML) importSetting).isUnvisibleObjectIgnored() ? "false" : "true");
				setUnvisibleObjectIgnored.parameter = parameterImportUnvisibleObject;
			} else {
				parameterImportUnvisibleObject.setSelectedItem(((ImportSettingKMZ) importSetting).isUnvisibleObjectIgnored() ? "false" : "true");
				setUnvisibleObjectIgnored.parameter = parameterImportUnvisibleObject;
			}
			result.add(setUnvisibleObjectIgnored);
			parameterCombineParamSet = new ParameterCombine();
			parameterCombineParamSet.setDescribe(ProcessProperties.getString("String_ParamSet"));
			parameterCombineParamSet.addParameters(setUnvisibleObjectIgnored.parameter);
			return result;
		}
		if (importSetting instanceof ImportSettingMAPGIS) {
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

			result.add(setColorIndexFilePath);
			parameterCombineParamSet = new ParameterCombine();
			parameterCombineParamSet.setDescribe(ProcessProperties.getString("String_ParamSet"));
			parameterCombineParamSet.addParameters(setColorIndexFilePath.parameter);
			return result;
		}
		if (importSetting instanceof ImportSettingCSV) {
			ReflectInfo setSeparator = new ReflectInfo();
			setSeparator.methodName = "setSeparator";
			ParameterTextField parameterSeparator = new ParameterTextField(CommonProperties.getString("String_Separator"));
			parameterSeparator.setSelectedItem(",");
			setSeparator.parameter = parameterSeparator;
			ReflectInfo setFirstRowIsField = new ReflectInfo();
			setFirstRowIsField.methodName = "setFirstRowIsField";
			setFirstRowIsField.parameter = new ParameterCheckBox(CommonProperties.getString("String_FirstRowisField"));

			parameterCombineParamSet = new ParameterCombine();
			parameterCombineParamSet.setDescribe(ProcessProperties.getString("String_ParamSet"));
			if (importSetting instanceof ImportSettingExcel) {
				result.add(setFirstRowIsField);
				parameterCombineParamSet.addParameters(setFirstRowIsField.parameter);
			} else {
				result.add(setSeparator);
				result.add(setFirstRowIsField);
				parameterCombineParamSet.addParameters(setSeparator.parameter, setFirstRowIsField.parameter);
			}
			return result;
		}
		if (importSetting instanceof ImportSettingModelOSG || importSetting instanceof ImportSettingModelX
				|| importSetting instanceof ImportSettingModelDXF || importSetting instanceof ImportSettingModelFBX
				|| importSetting instanceof ImportSettingModelFLT || importSetting instanceof ImportSettingModel3DS) {

			ReflectInfo setX = new ReflectInfo();
			setX.methodName = "setX";
			ParameterTextField textFieldX = new ParameterTextField(CommonProperties.getString("string_longitude"));
			textFieldX.setSelectedItem("0");
			setX.parameter = textFieldX;

			ReflectInfo setY = new ReflectInfo();
			setY.methodName = "setY";
			ParameterTextField textFieldY = new ParameterTextField(CommonProperties.getString("string_latitude"));
			textFieldY.setSelectedItem("0");
			setY.parameter = textFieldY;

			ReflectInfo setZ = new ReflectInfo();
			setZ.methodName = "setZ";
			ParameterTextField textFieldZ = new ParameterTextField(CommonProperties.getString("string_elevation"));
			textFieldZ.setSelectedItem("0");
			setZ.parameter = textFieldZ;
			result.add(setX);
			result.add(setY);
			result.add(setZ);
			parameterCombineModelSet = new ParameterCombine();
			parameterCombineModelSet.setDescribe(ProcessProperties.getString("String_modelPoint"));
			parameterCombineModelSet.addParameters(setX.parameter, setY.parameter, setZ.parameter);
			ReflectInfo setPrjCoordSys = new ReflectInfo();
			setPrjCoordSys.methodName = "setTargetPrjCoordSys";
			parameterRadioButton = new ParameterRadioButton();
			parameterRadioButton.setLayout(ParameterRadioButton.VATICAL);
			ParameterDataNode[] parameterDataNodes = {new ParameterDataNode(ProcessProperties.getString("String_setProject"), true), new ParameterDataNode(ProcessProperties.getString("string_importPrjFile"), false)};
			parameterRadioButton.setItems(parameterDataNodes);
			parameterRadioButton.setSelectedItem(parameterDataNodes[0]);
			setPrjCoordSys.parameter = parameterRadioButton;
			result.add(setPrjCoordSys);
			ReflectInfo chooseFile = new ReflectInfo();
			chooseFile.methodName = "";
			String moduleName = "ImportPrjFileFromProcess";
			if (!SmFileChoose.isModuleExist(moduleName)) {
				String fileFilters = SmFileChoose.buildFileFilters(
						SmFileChoose.createFileFilter(ProcessProperties.getString("String_ImportPrjFiles"), "prj", "xml"),
						SmFileChoose.createFileFilter(ProcessProperties.getString("String_ImportPrjFileShape"), "prj"),
						SmFileChoose.createFileFilter(ProcessProperties.getString("String_ImportPrjFileXml"), "xml"));
				SmFileChoose.addNewNode(fileFilters, CommonProperties.getString("String_DefaultFilePath"),
						ProcessProperties.getString("string_importPrjFile"), moduleName, "OpenMany");
			}

			parameterChooseFile = new ParameterFile();
			parameterChooseFile.setModuleName(moduleName);
			parameterChooseFile.setEnabled(false);
			chooseFile.parameter = parameterChooseFile;
			result.add(chooseFile);
			ReflectInfo selectButton = new ReflectInfo();
			selectButton.methodName = "";
			parameterButton = new ParameterButton(ProcessProperties.getString("String_setButton"));
			parameterButton.setEnabled(true);
			selectButton.parameter = parameterButton;
			result.add(selectButton);
			ReflectInfo textArea = new ReflectInfo();
			textArea.methodName = "";
			parameterTextArea = new ParameterTextArea();
			textArea.parameter = parameterTextArea;
			result.add(textArea);
			parameterCombineParamSet = new ParameterCombine();
			ParameterCombine parameterCombineProjectSet = new ParameterCombine();
			parameterCombineProjectSet.setDescribe(ProcessProperties.getString("String_setProject"));
			parameterCombineParamSet.setDescribe(ProcessProperties.getString("String_ParamSet"));
			parameterCombineParamSet.addParameters(parameterCombineModelSet, parameterCombineProjectSet.addParameters(
					new ParameterCombine(ParameterCombine.HORIZONTAL).addParameters(parameterRadioButton, new ParameterCombine().addParameters(parameterButton, parameterChooseFile)), parameterTextArea));
			return result;
		}
		return null;
	}

	public ParameterTextField getParameterDataset() {
		return parameterDataset;
	}

	public ParameterCharset getParameterCharset() {
		return parameterCharset;
	}

	@Override
	public CopyOnWriteArrayList<ReflectInfo> createSourceInfo(Object o, final String importType) {
		//源文件信息设置
		CopyOnWriteArrayList<ReflectInfo> sourceInfo = new CopyOnWriteArrayList<>();
		ImportSetting importSetting = null;
		if (o instanceof ImportSetting) {
			importSetting = (ImportSetting) o;
		}
		if (importSetting == null) return sourceInfo;
		ReflectInfo reflectInfoFilePath = new ReflectInfo();
		reflectInfoFilePath.methodName = "setSourceFilePath";
		parameterFile = FileType.createImportFileChooser(importType);
		parameterFile.setRequisite(true);
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
		sourceInfo.add(reflectInfoFilePath);
		parameterCombineSourceInfoSet = new ParameterCombine();
		parameterCombineSourceInfoSet.setDescribe(ProcessProperties.getString("String_ImportSettingPanel_SourceFileInfo"));
		// 将文件类型选择单选组合框加入面板-yuanR2017.9.1
		if (importSetting instanceof ImportSettingSimpleJson) {
			// 文件/文件夹，单选框,默认选择文件-yuanR2017.9.1
			parameterRadioButtonFolderOrFile = new ParameterRadioButton();
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
			sourceInfo.add(reflectInfoFolderPath);
			parameterCombineSourceInfoSet.addParameters(parameterFileFolder);
		}
		parameterCombineSourceInfoSet.addParameters(parameterFile);
		if (hasCharsetParameter) {
			sourceInfo.add(reflectInfoCharset);
			parameterCombineSourceInfoSet.addParameters(parameterCharset);
		}
		return sourceInfo;
	}

	@Override
	public CopyOnWriteArrayList<ReflectInfo> createResult(Object o, final String importType) {
		//结果设置
		CopyOnWriteArrayList<ReflectInfo> resultInfo = new CopyOnWriteArrayList<>();
		ImportSetting importSetting = null;
		if (o instanceof ImportSetting) {
			importSetting = (ImportSetting) o;
		}
		if (importSetting == null) return resultInfo;
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
		resultInfo.add(targetDatasource);

		final ReflectInfo targetDatasetName = new ReflectInfo();
		targetDatasetName.methodName = "setTargetDatasetName";
		parameterDataset = new ParameterTextField(CommonProperties.getString(CommonProperties.Label_Dataset));
		parameterDataset.setSelectedItem(importSetting.getTargetDatasetName());
		targetDatasetName.parameter = parameterDataset;
		resultInfo.add(targetDatasetName);

		//EncodeType reflect info
		parameterEncodeType = createEnumParser(importSetting);
		ReflectInfo reflectInfoEncodeType = new ReflectInfo();
		if (null != parameterEncodeType) {
			reflectInfoEncodeType.methodName = "setTargetEncodeType";
			reflectInfoEncodeType.parameter = parameterEncodeType;
			parameterEncodeType.setDescribe(ProcessProperties.getString("label_encodingType"));
			resultInfo.add(reflectInfoEncodeType);
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
		if (!(importSetting instanceof ImportSettingGPX)) {
			parameterImportMode = new ParameterEnum(new EnumParser(ImportMode.class, ReflectInfoImportModelValue, importModel)).setDescribe(ProcessProperties.getString("Label_ImportMode"));
			parameterImportMode.setSelectedItem(ProcessProperties.getString("String_FormImport_None"));
			reflectInfoImportMode.parameter = parameterImportMode;
			resultInfo.add(reflectInfoImportMode);
		}
		parameterCombineResultSet = new ParameterCombine();
		parameterCombineResultSet.setDescribe(CommonProperties.getString("String_ResultSet"));
		ParameterCombine parameterCombineSaveResult = new ParameterCombine(ParameterCombine.VERTICAL).addParameters(parameterDatasource, this.parameterDataset);
		ParameterCombine parameterCombineSecond;
		if (null != parameterEncodeType && null != parameterImportMode) {
			parameterCombineSecond = new ParameterCombine(ParameterCombine.VERTICAL).addParameters(parameterEncodeType, parameterImportMode);
		} else if (null != parameterImportMode) {
			parameterCombineSecond = new ParameterCombine(ParameterCombine.VERTICAL).addParameters(parameterImportMode);
		} else {
			parameterCombineSecond = new ParameterCombine();
		}
		parameterCombineResultSet.addParameters(
				parameterCombineSaveResult,
				parameterCombineSecond
		);

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
		if (importSetting instanceof ImportSettingGPX) {
			//导入为GPX类型时直接返回
			return resultInfo;
		}
		//导入数据集类型
		ReflectInfo reflectInfoDatasetType = new ReflectInfo();
		if (importSetting instanceof ImportSettingCSV) {
			resultInfo.clear();
			resultInfo.add(targetDatasource);
			resultInfo.add(targetDatasetName);
			parameterCombineResultSet = new ParameterCombine();
			parameterCombineResultSet.setDescribe(CommonProperties.getString("String_ResultSet"));
			parameterCombineResultSet.addParameters(parameterCombineSaveResult);
		} else if (importSetting instanceof ImportSettingWOR) {
			resultInfo.clear();
			resultInfo.add(targetDatasource);
			resultInfo.add(reflectInfoEncodeType);
			resultInfo.add(reflectInfoImportMode);
			parameterCombineResultSet = new ParameterCombine();
			parameterCombineResultSet.setDescribe(CommonProperties.getString("String_ResultSet"));
			parameterCombineResultSet.addParameters(parameterDatasource, parameterEncodeType, parameterImportMode);
		} else if (importSetting instanceof ImportSettingModel3DS || importSetting instanceof ImportSettingModelDXF
				|| importSetting instanceof ImportSettingModelFBX || importSetting instanceof ImportSettingModelOSG
				|| importSetting instanceof ImportSettingModelX) {
			resultInfo.clear();
			resultInfo.add(targetDatasetName);
			resultInfo.add(targetDatasource);
			resultInfo.add(reflectInfoDatasetType);
			resultInfo.add(reflectInfoImportMode);
			//数据集类型combobox
			//// FIXME: 2017/4/25 comboBox形式，实际设置值为Boolean，需要单独解析
			parameterDatasetTypeEnum = createDatasetTypeEnum(importSetting);
			reflectInfoDatasetType = new ReflectInfo();
			reflectInfoDatasetType.methodName = "setImportingAsCAD";
			reflectInfoDatasetType.parameter = parameterDatasetTypeEnum;
			parameterCombineResultSet = new ParameterCombine();
			parameterCombineResultSet.setDescribe(CommonProperties.getString("String_ResultSet"));
			parameterCombineResultSet.addParameters(
					new ParameterCombine(ParameterCombine.VERTICAL).addParameters(parameterDatasource, this.parameterDataset),
					new ParameterCombine(ParameterCombine.VERTICAL).addParameters(parameterDatasetTypeEnum, parameterImportMode)
			);
		} else if (importSetting instanceof ImportSettingTAB || importSetting instanceof ImportSettingMIF
				|| importSetting instanceof ImportSettingDWG || importSetting instanceof ImportSettingDXF
				|| importSetting instanceof ImportSettingKML || importSetting instanceof ImportSettingKMZ
				|| importSetting instanceof ImportSettingMAPGIS || importSetting instanceof ImportSettingDGN
				|| importSetting instanceof ImportSettingGeoJson) {
			parameterDatasetTypeEnum = createDatasetTypeEnum(importSetting);
			reflectInfoDatasetType = new ReflectInfo();
			reflectInfoDatasetType.methodName = "setImportingAsCAD";
			reflectInfoDatasetType.parameter = parameterDatasetTypeEnum;
			parameterCombineResultSet = new ParameterCombine();
			parameterCombineResultSet.setDescribe(CommonProperties.getString("String_ResultSet"));
			if (importSetting instanceof ImportSettingGeoJson) {
				parameterCombineResultSet.addParameters(
						parameterCombineSaveResult,
						parameterCombineSecond,
						parameterDatasetTypeEnum
				);
			} else {
				parameterCombineResultSet.addParameters(
						parameterCombineSaveResult,
						parameterCombineSecond,
						parameterDatasetTypeEnum,
						parameterCombineDatasetIndex
				);
			}
			resultInfo.clear();
			resultInfo.add(targetDatasetName);
			resultInfo.add(targetDatasource);
			resultInfo.add(reflectInfoEncodeType);
			resultInfo.add(reflectInfoImportMode);
			resultInfo.add(reflectInfoDatasetType);
			resultInfo.add(reflectInfoSpatialIndex);
			resultInfo.add(reflectInfoFieldIndex);
		} else if (importSetting instanceof ImportSettingJPG || importSetting instanceof ImportSettingJP2 ||
				importSetting instanceof ImportSettingPNG || importSetting instanceof ImportSettingBMP ||
				importSetting instanceof ImportSettingIMG || importSetting instanceof ImportSettingTIF ||
				importSetting instanceof ImportSettingGIF || importSetting instanceof ImportSettingMrSID
				|| importSetting instanceof ImportSettingECW) {
			parameterDatasetTypeEnum = createDatasetTypeEnum(importSetting);
			reflectInfoDatasetType = new ReflectInfo();
			reflectInfoDatasetType.methodName = "setImportingAsGrid";
			reflectInfoDatasetType.parameter = parameterDatasetTypeEnum;
			parameterCombineResultSet = new ParameterCombine();
			parameterCombineResultSet.setDescribe(CommonProperties.getString("String_ResultSet"));
			parameterCombineResultSet.addParameters(
					parameterCombineSaveResult,
					parameterCombineSecond,
					parameterDatasetTypeEnum
			);
			resultInfo.clear();
			resultInfo.add(targetDatasetName);
			resultInfo.add(targetDatasource);
			resultInfo.add(reflectInfoEncodeType);
			resultInfo.add(reflectInfoImportMode);
			resultInfo.add(reflectInfoDatasetType);
		} else if (importSetting instanceof ImportSettingSIT || importSetting instanceof ImportSettingGRD ||
				importSetting instanceof ImportSettingGBDEM || importSetting instanceof ImportSettingUSGSDEM ||
				importSetting instanceof ImportSettingSHP || importSetting instanceof ImportSettingE00 ||
				importSetting instanceof ImportSettingDBF || importSetting instanceof ImportSettingBIL ||
				importSetting instanceof ImportSettingBSQ || importSetting instanceof ImportSettingBIP ||
				importSetting instanceof ImportSettingTEMSClutter || importSetting instanceof ImportSettingVCT ||
				importSetting instanceof ImportSettingRAW || importSetting instanceof ImportSettingGJB ||
				importSetting instanceof ImportSettingTEMSVector || importSetting instanceof ImportSettingTEMSBuildingVector
				|| importSetting instanceof ImportSettingFileGDBVector) {
			parameterCombineResultSet = new ParameterCombine();
			parameterCombineResultSet.setDescribe(CommonProperties.getString("String_ResultSet"));
			parameterCombineResultSet.addParameters(
					parameterCombineSaveResult,
					parameterCombineSecond
			);
			resultInfo.clear();
			resultInfo.add(targetDatasetName);
			resultInfo.add(targetDatasource);
			resultInfo.add(reflectInfoEncodeType);
			resultInfo.add(reflectInfoImportMode);
			if (importSetting instanceof ImportSettingSHP) {
				parameterCombineResultSet.addParameters(parameterCombineDatasetIndex);
			} else if (importSetting instanceof ImportSettingE00 || importSetting instanceof ImportSettingGJB
					|| importSetting instanceof ImportSettingTEMSVector || importSetting instanceof ImportSettingTEMSBuildingVector
					|| importSetting instanceof ImportSettingFileGDBVector) {
				parameterCombineResultSet.addParameters(parameterSpatialIndex);
				resultInfo.add(reflectInfoSpatialIndex);
				resultInfo.add(reflectInfoFieldIndex);
			}
			if (importSetting instanceof ImportSettingGJB || importSetting instanceof ImportSettingTEMSVector
					|| importSetting instanceof ImportSettingTEMSBuildingVector || importSetting instanceof ImportSettingFileGDBVector) {
				this.parameterDataset.setEnabled(false);
				this.parameterDataset.setSelectedItem("");
			}

		} else if (importSetting instanceof ImportSettingLIDAR) {
			ParameterComboBox parameterDatasetType = new ParameterComboBox();
			parameterDatasetType.addItem(new ParameterDataNode(ProcessProperties.getString("String_datasetType2D"), false));
			parameterDatasetType.addItem(new ParameterDataNode(ProcessProperties.getString("String_datasetType3D"), true));
			parameterDatasetType.setDescribe(ProcessProperties.getString("string_label_lblDatasetType"));
			if (null != parameterDatasetType) {
				reflectInfoDatasetType = new ReflectInfo();
				reflectInfoDatasetType.methodName = "setImportingAs3D";
				reflectInfoDatasetType.parameter = parameterDatasetType;
			}
			parameterCombineResultSet = new ParameterCombine();
			parameterCombineResultSet.setDescribe(CommonProperties.getString("String_ResultSet"));
			parameterCombineResultSet.addParameters(
					parameterCombineSaveResult,
					parameterCombineSecond,
					parameterDatasetType,
					parameterSpatialIndex
			);
			resultInfo.clear();
			resultInfo.add(targetDatasetName);
			resultInfo.add(targetDatasource);
			resultInfo.add(reflectInfoEncodeType);
			resultInfo.add(reflectInfoImportMode);
			resultInfo.add(reflectInfoDatasetType);
			resultInfo.add(reflectInfoSpatialIndex);
			resultInfo.add(reflectInfoFieldIndex);
		}
		//#endregion
		return resultInfo;
	}

	@Override
	public ParameterFile getParameterFile() {
		return parameterFile;
	}

	public ParameterFile getParameterFileFolder() {
		return parameterFileFolder;
	}

	public ParameterFile getParameterChooseFile() {
		return parameterChooseFile;
	}

	public ParameterButton getParameterButton() {
		return parameterButton;
	}

	@Override
	public ParameterTextArea getParameterTextArea() {
		return parameterTextArea;
	}

	public ParameterRadioButton getParameterSetRadioButton() {
		return parameterRadioButton;
	}

	@Override
	public ParameterDatasource getParameterResultDatasource() {
		return parameterDatasource;
	}

	@Override
	public IParameter getParameterCombineResultSet() {
		return parameterCombineResultSet;
	}

	@Override
	public IParameter getParameterCombineParamSet() {
		return parameterCombineParamSet;
	}

	public ParameterCombine getParameterCombineSourceInfoSet() {
		return parameterCombineSourceInfoSet;
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

}
