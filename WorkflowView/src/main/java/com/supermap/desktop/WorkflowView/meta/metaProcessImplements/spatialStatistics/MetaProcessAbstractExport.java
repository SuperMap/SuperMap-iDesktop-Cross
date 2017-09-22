package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.spatialStatistics;

import com.supermap.data.Dataset;
import com.supermap.data.DatasetVector;
import com.supermap.data.conversion.DataExport;
import com.supermap.data.conversion.ExportResult;
import com.supermap.data.conversion.ExportSetting;
import com.supermap.data.conversion.ExportSteppedListener;
import com.supermap.data.conversion.FileType;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.WorkflowViewProperties;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.WorkflowView.meta.dataconversion.ExportSettingUtilities;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.implement.UserDefineType.ExportSettingExcel;
import com.supermap.desktop.implement.UserDefineType.ExportSettingGPX;
import com.supermap.desktop.implement.UserDefineType.GPXAnalytic;
import com.supermap.desktop.implement.UserDefineType.UserDefineExportResult;
import com.supermap.desktop.implement.UserDefineType.UserDefineFileType;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.core.IProcess;
import com.supermap.desktop.process.core.IReadyChecker;
import com.supermap.desktop.process.core.ReadyEvent;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.interfaces.IParameterPanel;
import com.supermap.desktop.process.parameter.interfaces.datas.types.BasicTypes;
import com.supermap.desktop.process.parameter.ipls.ParameterCheckBox;
import com.supermap.desktop.process.parameter.ipls.ParameterCombine;
import com.supermap.desktop.process.parameter.ipls.ParameterComboBox;
import com.supermap.desktop.process.parameter.ipls.ParameterDatasource;
import com.supermap.desktop.process.parameter.ipls.ParameterFile;
import com.supermap.desktop.process.parameter.ipls.ParameterSingleDataset;
import com.supermap.desktop.process.parameter.ipls.ParameterTextField;
import com.supermap.desktop.properties.CommonProperties;
import com.supermap.desktop.utilities.DatasetUtilities;
import com.supermap.desktop.utilities.DatasourceUtilities;
import com.supermap.desktop.utilities.StringUtilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.MessageFormat;

/**
 * Created by xie on 2017/6/29.
 */
public class MetaProcessAbstractExport extends MetaProcess {
	protected final static String INPUT_DATA = "SourceDataset";
	protected final static String OUTPUT_DATA = "ExportResult";
	protected static String OUTPUT_DATA_TYPE;
	//	protected ParameterDatasetChooser chooseDataset;
	protected ParameterDatasource datasource;
	protected ParameterSingleDataset dataset;
	protected ParameterCombine sourceInfo;

	protected ParameterComboBox supportType;
	protected ParameterTextField targetName;
	protected ParameterFile exportPath;
	protected ParameterCheckBox cover;
	protected ParameterCombine basicCombine;
	protected Dataset selectDataset;
	protected ExportSetting exportSetting;
	protected boolean isSelectChanged = false;

	private PropertyChangeListener chooseDatasetListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (!isSelectChanged && null != evt.getNewValue()) {
				isSelectChanged = true;
				resetDataset();
				isSelectChanged = false;
			}
		}
	};
	private PropertyChangeListener resetExportsettingListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (!isSelectChanged && null != evt.getNewValue()) {
				isSelectChanged = true;
				resetExportSetting();
				isSelectChanged = false;
			}
		}
	};

	public MetaProcessAbstractExport() {
		this.addProcessReadyChecker(new IReadyChecker<IProcess>() {
			@Override
			public boolean isReady(ReadyEvent<IProcess> readyEvent) {
				String parentPath = exportPath.getSelectedItem();
				if (StringUtilities.isNullOrEmpty(parentPath)) {
					if (readyEvent.isOutputMessage()) {
						Application.getActiveApplication().getOutput().output(MessageFormat.format(WorkflowViewProperties.getString("String_ExportPathCannotBeNull"), getTitle()));
					}
					return false;
				}
				if (readyEvent.isOutputMessage()) {
					File file = new File(parentPath);
					if (!file.exists()) {
						try {
							file.mkdirs();
						} catch (Exception e) {
							Application.getActiveApplication().getOutput().output((MessageFormat.format(WorkflowViewProperties.getString("String_ExportPathUnLegal"), getTitle())));
							return false;
						}
					}
				}
				return true;
			}
		});
	}

	protected void registerEvents() {
		removeEvents();
		this.dataset.addPropertyListener(chooseDatasetListener);
		this.supportType.addPropertyListener(resetExportsettingListener);
	}

	protected void removeEvents() {
		this.dataset.removePropertyListener(chooseDatasetListener);
		this.supportType.removePropertyListener(resetExportsettingListener);
	}

	protected void initParameters() {
		this.datasource = new ParameterDatasource();
		this.sourceInfo = new ParameterCombine();
		this.sourceInfo.setDescribe(ControlsProperties.getString("String_GroupBox_SourceDataset"));
		this.dataset = new ParameterSingleDataset();
		this.dataset.setDatasource(DatasourceUtilities.getDefaultResultDatasource());
		this.sourceInfo.addParameters(datasource, dataset);
		EqualDatasourceConstraint constraint = new EqualDatasourceConstraint();
		constraint.constrained(datasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		constraint.constrained(dataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);
		Dataset dataset = DatasetUtilities.getDefaultDataset();
		this.supportType = new ParameterComboBox(ProcessProperties.getString("String_ExportType"));
		this.supportType.setEnabled(false);
		this.targetName = new ParameterTextField(ProcessProperties.getString("String_TargetName"));
		this.targetName.setEnabled(false);
		this.exportPath = new ParameterFile(ProcessProperties.getString("String_ExportPath"));
		this.exportPath.setRequisite(true);
		this.exportPath.setEnabled(false);
		this.cover = new ParameterCheckBox(ProcessProperties.getString("String_Cover"));
		this.cover.setEnabled(false);
		this.basicCombine = new ParameterCombine();
		this.basicCombine.setDescribe(CommonProperties.getString("String_ResultSet"));
		this.basicCombine.addParameters(this.supportType, this.targetName, this.exportPath, this.cover);
		//输出为文件路径，没有控件能对应
		this.parameters.addOutputParameters(OUTPUT_DATA,
				MessageFormat.format(ProcessOutputResultProperties.getString("String_OutputResult"), OUTPUT_DATA_TYPE),
				BasicTypes.STRING, basicCombine);
	}

	protected void resetDataset() {
		selectDataset = dataset.getSelectedItem();
		if (null == selectDataset) {
			return;
		}
		ExportSetting tempExportSetting = new ExportSetting();
		tempExportSetting.setSourceData(selectDataset);
		FileType[] fileTypes = tempExportSetting.getSupportedFileType();
		int size = fileTypes.length;
		if (size > 0) {
			boolean isGpx = false;
			if (tempExportSetting.getSourceData() instanceof DatasetVector) {
				isGpx = GPXAnalytic.isGPXType((DatasetVector) tempExportSetting.getSourceData());
			}
			supportType.removeAllItems();
			ParameterDataNode selectNode = null;
			if (isGpx) {
				selectNode = new ParameterDataNode(ExportSettingUtilities.getDatasetName(UserDefineFileType.GPX.toString()), UserDefineFileType.GPX);
				supportType.addItem(selectNode);
				for (int i = 0; i < size; i++) {
					if (!StringUtilities.isNullOrEmpty(ExportSettingUtilities.getDatasetName(fileTypes[i].toString()))) {
						supportType.addItem(new ParameterDataNode(ExportSettingUtilities.getDatasetName(fileTypes[i].toString()), fileTypes[i]));
					}
				}
				supportType.setSelectedItem(selectNode);
				exportSetting = ExportSettingUtilities.createExportSetting(UserDefineFileType.GPX);
			} else {
				for (int i = 0; i < size; i++) {
					if (!StringUtilities.isNullOrEmpty(ExportSettingUtilities.getDatasetName(fileTypes[i].toString())) && fileTypes[i] != FileType.ModelX) {
						if (0 == i) {
							selectNode = new ParameterDataNode(ExportSettingUtilities.getDatasetName(fileTypes[0].toString()), fileTypes[0]);
							supportType.addItem(selectNode);
						} else {
							supportType.addItem(new ParameterDataNode(ExportSettingUtilities.getDatasetName(fileTypes[i].toString()), fileTypes[i]));
						}
					}
				}
				supportType.setSelectedItem(selectNode);
				exportSetting = ExportSettingUtilities.createExportSetting(fileTypes[0]);
			}
		}
		if (selectDataset instanceof DatasetVector) {
			supportType.addItem(new ParameterDataNode(ExportSettingUtilities.getDatasetName(UserDefineFileType.EXCEL.toString()), UserDefineFileType.EXCEL));
		}
		exportSetting.setSourceData(selectDataset);
		targetName.setSelectedItem(selectDataset.getName());

		supportType.setEnabled(true);
		targetName.setEnabled(true);
		exportPath.setEnabled(true);
		cover.setEnabled(true);
	}

	protected void resetExportSetting() {
		exportSetting = ExportSettingUtilities.createExportSetting(supportType.getSelectedData());
		if (null != selectDataset) {
			exportSetting.setSourceData(selectDataset);
		}
	}

	protected void setExportSettingInfo(boolean isOverwrite) {
		if ((supportType.getSelectedData() instanceof FileType) && (supportType.getSelectedData().equals(FileType.SHP) || supportType.getSelectedData().equals(FileType.E00)
				|| supportType.getSelectedData().equals(FileType.MIF) || supportType.getSelectedData().equals(FileType.TAB)
				|| supportType.getSelectedData().equals(FileType.IMG) || supportType.getSelectedData().equals(FileType.GRD)
				|| supportType.getSelectedData().equals(FileType.DBF) || supportType.getSelectedData().equals(FileType.TEMSClutter))) {
			exportSetting.setTargetFileType((FileType) supportType.getSelectedData());
		}
		exportSetting.setOverwrite(isOverwrite);
		exportSetting.setTargetFilePath(getFilePath());
	}

	protected String getFilePath() {
		String result = "";
		if (null != exportPath.getSelectedItem()) {
			String filePath = exportPath.getSelectedItem().toString();
			String fileName = targetName.getSelectedItem().toString();
			if (supportType.getSelectedData() instanceof FileType) {
				FileType fileType = (FileType) supportType.getSelectedData();
				if (null == fileType) {
					return result;
				}
				if (FileType.TEMSClutter == fileType) {
					if (!filePath.endsWith(File.separator)) {
						result = filePath + File.separator + fileName + ".b";
					} else {
						result = filePath + fileName + ".b";
					}
				} else if (FileType.ModelX == fileType) {
					if (!filePath.endsWith(File.separator)) {
						result = filePath + File.separator + fileName + ".x";
					} else {
						result = filePath + fileName + ".x";
					}
				} else if (FileType.SimpleJson == fileType || FileType.GEOJSON == fileType) {
					if (!filePath.endsWith(File.separator)) {
						result = filePath + File.separator + fileName + ".json";
					} else {
						result = filePath + fileName + ".json";
					}
				} else {
					if (!filePath.endsWith(File.separator)) {
						result = filePath + File.separator + fileName + "." + fileType.toString().toLowerCase();
					} else {
						result = filePath + fileName + "." + fileType.toString().toLowerCase();
					}
				}
			} else {
				UserDefineFileType fileType = (UserDefineFileType) supportType.getSelectedData();
				if (null == fileType) {
					return result;
				}
				if (!filePath.endsWith(File.separator)) {
					result = filePath + File.separator + fileName + "." + fileType.toString().toLowerCase();
				} else {
					result = filePath + fileName + "." + fileType.toString().toLowerCase();
				}
			}
		}
		return result;
	}

	protected boolean printResultInfo(boolean isSuccessful, String targetPath, ExportSteppedListener exportListener) {
		long startTime = System.currentTimeMillis();
		String time;
		if (exportSetting instanceof ExportSettingGPX) {
			((ExportSettingGPX) exportSetting).addExportSteppedListener(exportListener);
			UserDefineExportResult result = ((ExportSettingGPX) exportSetting).run();
			time = String.valueOf((System.currentTimeMillis() - startTime) / 1000.0);
			isSuccessful = printExportInfo(result, time);
			((ExportSettingGPX) exportSetting).removeExportSteppedListener(exportListener);
		} else if (exportSetting instanceof ExportSettingExcel) {
			((ExportSettingExcel) exportSetting).addExportSteppedListener(exportListener);
			UserDefineExportResult result = ((ExportSettingExcel) exportSetting).run();
			time = String.valueOf((System.currentTimeMillis() - startTime) / 1000.0);
			isSuccessful = printExportInfo(result, time);
			((ExportSettingExcel) exportSetting).removeExportSteppedListener(exportListener);
		} else {
			DataExport dataExport = new DataExport();
			dataExport.getExportSettings().add(exportSetting);
			try {
				dataExport.addExportSteppedListener(exportListener);

				ExportResult result = dataExport.run();
				ExportSetting[] succeedSettings = result.getSucceedSettings();
				if (succeedSettings.length > 0) {
					isSuccessful = true;
					time = String.valueOf((System.currentTimeMillis() - startTime) / 1000);
					Application.getActiveApplication().getOutput().output(MessageFormat.format(ProcessProperties.getString("String_FormExport_OutPutInfoTwo"),
							selectDataset.getName() + "@" + selectDataset.getDatasource().getAlias(), targetPath, time));
				} else {
					Application.getActiveApplication().getOutput().output(MessageFormat.format(ProcessProperties.getString("String_FormExport_OutPutInfoOne"), selectDataset.getName() + "@" + selectDataset.getDatasource().getAlias()));
				}
			} catch (Exception e) {
				Application.getActiveApplication().getOutput().output(e);
			} finally {
				dataExport.removeExportSteppedListener(exportListener);
			}
		}
		return isSuccessful;
	}

	/**
	 * 打印导出gps信息
	 *
	 * @param result
	 */
	private boolean printExportInfo(UserDefineExportResult result, String time) {
		boolean isSuccess = false;
		try {
			if (null != result) {
				String successExportInfo = ProcessProperties.getString("String_FormExport_OutPutInfoTwo");
				String failExportInfo = ProcessProperties.getString("String_FormExport_OutPutInfoOne");
				if (null != result.getSuccess()) {
					isSuccess = true;
					String successDatasetAlis = getDatasetAlis(result.getSuccess());
					Application.getActiveApplication().getOutput().output(MessageFormat.format(successExportInfo, successDatasetAlis, result.getSuccess().getTargetFilePath(), time));
				} else if (null != result.getFail()) {
					String failDatasetAlis = getDatasetAlis(result.getFail());
					Application.getActiveApplication().getOutput().output(MessageFormat.format(failExportInfo, failDatasetAlis));
				}
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
		return isSuccess;
	}

	private String getDatasetAlis(ExportSetting tempSetting) {
		Dataset tempDataset = (Dataset) tempSetting.getSourceData();
		return tempDataset.getName() + ProcessProperties.getString("string_index_and") + tempDataset.getDatasource().getAlias();
	}

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public boolean execute() {
		return false;
	}

	@Override
	public String getKey() {
		return null;
	}

	@Override
	public IParameterPanel getComponent() {
		return parameters.getPanel();
	}
}
