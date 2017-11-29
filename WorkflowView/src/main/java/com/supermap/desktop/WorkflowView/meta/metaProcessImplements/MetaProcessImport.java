package com.supermap.desktop.WorkflowView.meta.metaProcessImplements;

import com.supermap.data.Charset;
import com.supermap.data.Dataset;
import com.supermap.data.Datasource;
import com.supermap.data.conversion.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.WorkflowView.meta.dataconversion.ImportParameterCreator;
import com.supermap.desktop.WorkflowView.meta.dataconversion.ImportSettingCreator;
import com.supermap.desktop.WorkflowView.meta.dataconversion.ImportSettingSetter;
import com.supermap.desktop.WorkflowView.meta.dataconversion.ReflectInfo;
import com.supermap.desktop.WorkflowView.meta.loader.ImportProcessLoader;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.DatasetUIUtilities;
import com.supermap.desktop.implement.UserDefineType.ImportSettingExcel;
import com.supermap.desktop.implement.UserDefineType.ImportSettingGPX;
import com.supermap.desktop.implement.UserDefineType.UserDefineImportResult;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.events.RunningEvent;
import com.supermap.desktop.process.loader.IProcessLoader;
import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.process.types.BasicTypes;
import com.supermap.desktop.process.types.DatasetTypes;
import com.supermap.desktop.process.types.Type;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.UICommonToolkit;
import com.supermap.desktop.ui.trees.WorkspaceTree;
import com.supermap.desktop.utilities.DatasourceUtilities;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author XiaJT
 */
public class MetaProcessImport extends MetaProcess {
	private final static String INPUT_DATA = CoreProperties.getString("String_SelectFile");
	private final static String OUTPUT_DATA = "ImportResult";
	protected ImportSetting importSetting;
	private String importType = "";
	private ImportSetting resultImportSetting = null;
	private ImportParameterCreator parameterCreator;
	private ImportSteppedListener importStepListener = new ImportSteppedListener() {
		@Override
		public void stepped(ImportSteppedEvent e) {
			RunningEvent event = new RunningEvent(MetaProcessImport.this, e.getSubPercent(), "");
			fireRunning(event);

			if (event.isCancel()) {
				e.setCancel(true);
			}
		}
	};


	public MetaProcessImport(ImportSetting importSetting, String importType) {
		this.importSetting = importSetting;
		this.importType = importType;
		initTitle();
		initParameters();
	}

	public void initTitle() {
		if (importType.equalsIgnoreCase("GBDEM")) {
			setTitle(MessageFormat.format(ProcessProperties.getString("String_ImportTitle"), "ArcGIS DEM"));
		} else if (importType.equalsIgnoreCase("GRD_DEM")) {
			setTitle(MessageFormat.format(ProcessProperties.getString("String_ImportTitle"), ControlsProperties.getString("String_Grid") + "DEM"));
		} else {
			setTitle(MessageFormat.format(ProcessProperties.getString("String_ImportTitle"), importType));
		}
	}

	public void initParameters() {
		parameterCreator = new ImportParameterCreator();
		updateParameters();
	}

	private void updateParameters() {
		CopyOnWriteArrayList<IParameter> parameterCombineArray = parameterCreator.getParameterCombineArray(importSetting, importType);
		addOutPutParameters();
		if (parameterCombineArray.size() > 0) {
			for (IParameter parameter : parameterCombineArray) {
				if (null != parameter) {
					parameters.addParameters(parameter);
				}
			}
		}
		this.parameters.addInputParameters(INPUT_DATA, BasicTypes.STRING, parameters.getParameters().get(0));
	}

	private void addOutPutParameters() {
		// TODO: 2017/6/16
		Type types = DatasetTypes.DATASET;
		if (importSetting instanceof ImportSettingSHP || importSetting instanceof ImportSettingE00
				|| importSetting instanceof ImportSettingDWG || importSetting instanceof ImportSettingDXF
				|| importSetting instanceof ImportSettingTAB || importSetting instanceof ImportSettingMIF
				|| importSetting instanceof ImportSettingMAPGIS || importSetting instanceof ImportSettingSIT
				|| importSetting instanceof ImportSettingModelOSG || importSetting instanceof ImportSettingModel3DS
				|| importSetting instanceof ImportSettingModelX || importSetting instanceof ImportSettingKML
				|| importSetting instanceof ImportSettingKMZ || importSetting instanceof ImportSettingDGN
				|| importSetting instanceof ImportSettingVCT || importSetting instanceof ImportSettingGJB
				|| importSetting instanceof ImportSettingFileGDBVector) {
			types = DatasetTypes.SIMPLE_VECTOR;
		} else if (importSetting instanceof ImportSettingGRD || importSetting instanceof ImportSettingGBDEM
				|| importSetting instanceof ImportSettingBIL || importSetting instanceof ImportSettingRAW
				|| importSetting instanceof ImportSettingBSQ || importSetting instanceof ImportSettingBIP) {
			types = DatasetTypes.GRID;
		} else if (importSetting instanceof ImportSettingDBF || importSetting instanceof ImportSettingCSV) {
			types = DatasetTypes.TABULAR;
		} else if (importSetting instanceof ImportSettingWOR) {
			types = DatasetTypes.DATASET;
		} else if (importSetting instanceof ImportSettingIMG || importSetting instanceof ImportSettingTIF
				|| importSetting instanceof ImportSettingBMP || importSetting instanceof ImportSettingPNG
				|| importSetting instanceof ImportSettingGIF || importSetting instanceof ImportSettingJPG
				|| importSetting instanceof ImportSettingJP2 || importSetting instanceof ImportSettingMrSID
				|| importSetting instanceof ImportSettingECW) {
			// 类型可选
//			types = new DatasetTypes("gridAndImage",DatasetTypes.GRID.getValue() | DatasetTypes.IMAGE.getValue());
			types = DatasetTypes.IMAGE;
		} else if (importSetting instanceof ImportSettingTEMSVector) {
			types = DatasetTypes.LINE;
		} else if (importSetting instanceof ImportSettingTEMSBuildingVector) {
			types = DatasetTypes.REGION;
		}

		this.getParameters().addOutputParameters(OUTPUT_DATA,
				MessageFormat.format(ProcessOutputResultProperties.getString("String_InputResult"), importType),
				types, parameterCreator.getParameterCombineResultSet());
	}


	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		String datasetName = importSetting.getTargetDatasetName();
		Dataset dataset = DatasourceUtilities.getDataset(datasetName, importSetting.getTargetDatasource());
		if (importSetting.getImportMode().equals(ImportMode.OVERWRITE) && dataset != null) {
			ArrayList<Dataset> datasets = new ArrayList<>();
			datasets.add(dataset);
			java.util.List<Dataset> closedDatasets = DatasetUIUtilities.sureDatasetClosed(datasets);
			if (closedDatasets.size() > 0) {
				isSuccessful = doImport();
			}
		} else {
			isSuccessful = doImport();
		}
		return isSuccessful;
	}

	@Override
	public Class<? extends IProcessLoader> getLoader() {
		return ImportProcessLoader.class;
	}

	private boolean doImport() {
		boolean isSuccessful = false;
		long startTime = System.currentTimeMillis();
		long endTime;
		long time;
		CopyOnWriteArrayList<ReflectInfo> parameters = parameterCreator.getReflectInfoArray();
		if (importSetting instanceof ImportSettingSimpleJson) {
			if (null == ((ParameterFile) (parameters.get(0)).parameter).getSelectedItem() && null == ((ParameterFile) (parameters.get(1)).parameter).getSelectedItem()) {
				Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_ImportFailed"));
				return isSuccessful;
			}
		} else {
			if (null == ((ParameterFile) (parameters.get(0)).parameter).getSelectedItem()) {
				Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_ImportFailed"));
				return isSuccessful;
			}
		}

		if (importSetting instanceof ImportSettingGPX) {
			importSetting.setSourceFilePath(((ParameterFile) (parameters.get(0)).parameter).getSelectedItem().toString());
			final Datasource datasource = ((ParameterDatasource) parameters.get(1).parameter).getSelectedItem();
			importSetting.setTargetDatasource(datasource);
			importSetting.setTargetDatasetName(((ParameterTextField) parameters.get(2).parameter).getSelectedItem().toString());
			((ImportSettingGPX) importSetting).addImportSteppedListener(this.importStepListener);
			UserDefineImportResult result = ((ImportSettingGPX) importSetting).run();
			if (null != result) {
				isSuccessful = true;
				updateDataset(result.getSuccess());
				endTime = System.currentTimeMillis(); // 获取结束时间
				time = endTime - startTime;
				printMessage(result, time);
			} else {
				Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_ImportFailed"));
			}
			((ImportSettingGPX) importSetting).removeImportSteppedListener(this.importStepListener);
		} else if (importSetting instanceof ImportSettingExcel) {
			importSetting.setSourceFilePath(((ParameterFile) (parameters.get(0)).parameter).getSelectedItem().toString());
			importSetting.setSourceFileCharset((Charset) ((ParameterCharset) parameters.get(1).parameter).getSelectedData());
			final Datasource datasource = ((ParameterDatasource) parameters.get(2).parameter).getSelectedItem();
			importSetting.setTargetDatasource(datasource);
			importSetting.setTargetDatasetName(((ParameterTextField) parameters.get(3).parameter).getSelectedItem().toString());
			((ImportSettingExcel) importSetting).setFirstRowIsField(Boolean.valueOf(((ParameterCheckBox) parameters.get(4).parameter).getSelectedItem()));
			((ImportSettingExcel) importSetting).addImportSteppedListener(this.importStepListener);
			startTime = System.currentTimeMillis(); // 获取开始时间
			UserDefineImportResult[] result = ((ImportSettingExcel) importSetting).run();
			if (null != result) {
				isSuccessful = true;
				endTime = System.currentTimeMillis(); // 获取结束时间
				time = endTime - startTime;
				for (UserDefineImportResult tempResult : result) {
					if (null != tempResult && null != tempResult.getSuccess()) {
						isSuccessful = true;
						updateDataset(tempResult.getSuccess());
						printMessage(tempResult, time);
					} else {
						Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_ImportFailed"));
					}
				}
			}
			((ImportSettingExcel) importSetting).removeImportSteppedListener(importStepListener);
		} else {
			ImportSetting newImportSetting = new ImportSettingCreator().create(importType);
			DataImport dataImport = ImportSettingSetter.getDataImport(newImportSetting, parameterCreator.getReflectInfoArray());
			try {
				dataImport.addImportSteppedListener(this.importStepListener);
				ImportResult result = dataImport.run();
				isSuccessful = getCommonResult(isSuccessful, startTime, result);
			} catch (Exception e) {
				Application.getActiveApplication().getOutput().output(e);
			} finally {
				dataImport.removeImportSteppedListener(this.importStepListener);
				updateDataset(resultImportSetting);
			}
		}
		return isSuccessful;
	}

	private boolean getCommonResult(boolean isSuccessful, long startTime, ImportResult result) {
		long endTime;
		long time;
		ImportSetting[] succeedSettings = result.getSucceedSettings();
		if (succeedSettings.length > 0) {
			isSuccessful = true;
			resultImportSetting = succeedSettings[0];
			endTime = System.currentTimeMillis(); // 获取结束时间
			time = endTime - startTime;
			printMessage(result, time);
		} else {
			Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_ImportFailed"));
		}
		return isSuccessful;
	}

	private void printMessage(ImportResult result, long time) {
		ImportSetting[] successImportSettings = result.getSucceedSettings();
		ImportSetting[] failImportSettings = result.getFailedSettings();
		String successImportInfo = ProcessProperties.getString("String_FormImport_OutPutInfoOne");
		String failImportInfo = ProcessProperties.getString("String_FormImport_OutPutInfoTwo");
		if (null != successImportSettings && 0 < successImportSettings.length) {
			String[] names = result.getSucceedDatasetNames(successImportSettings[0]);
			// 创建空间索引，字段索引
			ImportSetting sucessSetting = successImportSettings[0];
			if (null != names && names.length > 0) {
				for (int j = 0; j < names.length; j++) {
					Application.getActiveApplication().getOutput().output(MessageFormat.format(successImportInfo, sucessSetting.getSourceFilePath(), "->", names[j], sucessSetting
							.getTargetDatasource().getAlias(), String.valueOf((time / names.length) / 1000.0)));
				}
			}
		} else if (null != failImportSettings && 0 < failImportSettings.length) {
			Application.getActiveApplication().getOutput().output(MessageFormat.format(failImportInfo, failImportSettings[0].getSourceFilePath(), "->", ""));
		}
	}

	private void printMessage(UserDefineImportResult result, long time) {
		if (null != result.getSuccess()) {
			String successImportInfo = ProcessProperties.getString("String_FormImport_OutPutInfoOne");
			Application.getActiveApplication().getOutput().output(MessageFormat.format(successImportInfo, result.getSuccess().getSourceFilePath(), "->",
					result.getSuccess().getTargetDatasetName(), result.getSuccess()
							.getTargetDatasource().getAlias(), String.valueOf(time / 1000.0)));
		}
	}

	private void updateDataset(final ImportSetting succeedSetting) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (null != succeedSetting.getTargetDatasource()) {
					UICommonToolkit.refreshSelectedDatasourceNode(succeedSetting.getTargetDatasource().getAlias());
				}
				if (null != succeedSetting && null != succeedSetting.getTargetDatasource().getDatasets().get(succeedSetting.getTargetDatasetName())) {
					UICommonToolkit.refreshSelectedDatasetNode(succeedSetting.getTargetDatasource().getDatasets().get(succeedSetting.getTargetDatasetName()));
				}

			}
		});
		if (importSetting instanceof ImportSettingWOR) {
			// 刷新地图节点
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					WorkspaceTree workspaceTree = UICommonToolkit.getWorkspaceManager().getWorkspaceTree();
					DefaultTreeModel treeModel = (DefaultTreeModel) workspaceTree.getModel();
					MutableTreeNode treeNode = (MutableTreeNode) treeModel.getRoot();
					UICommonToolkit.getWorkspaceManager().getWorkspaceTree().refreshNode((DefaultMutableTreeNode) treeNode.getChildAt(1));
				}
			});
		}
		if (null != succeedSetting.getTargetDatasource()) {
			Dataset dataset = succeedSetting.getTargetDatasource().getDatasets().get(succeedSetting.getTargetDatasetName());
			this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(dataset);
		}
	}

	@Override
	public String getKey() {
		return MetaKeys.IMPORT + importType;
	}

}
