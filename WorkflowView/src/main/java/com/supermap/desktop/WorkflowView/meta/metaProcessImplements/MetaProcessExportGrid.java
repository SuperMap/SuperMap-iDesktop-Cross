package com.supermap.desktop.WorkflowView.meta.metaProcessImplements;

import com.supermap.data.DatasetGrid;
import com.supermap.data.conversion.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.metaProcessImplements.spatialStatistics.MetaProcessAbstractExport;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.events.RunningEvent;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.process.types.DatasetTypes;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.SmFileChoose;
import com.supermap.desktop.utilities.DatasetTypeUtilities;
import com.supermap.desktop.utilities.DatasetUtilities;
import com.supermap.desktop.utilities.StringUtilities;

import java.io.File;
import java.text.MessageFormat;

/**
 * Created by xie on 2017/6/28.
 */
public class MetaProcessExportGrid extends MetaProcessAbstractExport {
	private ParameterCombine gridCombine;
	private ParameterTextField compressionRatio;
	private ParameterFile prjFile;
	private ParameterCheckBox checkBoxTFW;
	private ParameterPassword password;
	private ParameterPassword confirmPassword;


	private ExportSteppedListener exportListener = new ExportSteppedListener() {
		@Override
		public void stepped(ExportSteppedEvent e) {
			RunningEvent event = new RunningEvent(MetaProcessExportGrid.this, e.getSubPercent(), "");
			fireRunning(event);
			if (event.isCancel()) {
				e.setCancel(true);
			}
		}
	};

	public MetaProcessExportGrid() {
		setTitle(ProcessProperties.getString("String_ExportGrid"));
		this.OUTPUT_DATA_TYPE = ControlsProperties.getString("String_Grid");
		initParameters();
		registerEvents();
	}

	protected void initParameters() {
		super.initParameters();
		this.dataset.setDatasetTypes(DatasetTypeUtilities.getDatasetTypeGrid());
		DatasetGrid datasetGrid = DatasetUtilities.getDefaultDatasetGrid();
		if (datasetGrid != null) {
			this.datasource.setSelectedItem(datasetGrid.getDatasource());
			this.dataset.setSelectedItem(datasetGrid);
		}
		String module = "ExportGrid_OutPutDirectories";
		if (!SmFileChoose.isModuleExist(module)) {
			SmFileChoose.addNewNode("", System.getProperty("user.dir"), ControlsProperties.getString("String_FormExport_FormText"),
					module, "GetDirectories");
		}
		exportPath.setModuleName(module);

		this.gridCombine = new ParameterCombine();
		this.gridCombine.setDescribe(ControlsProperties.getString("string_border_panelTransform"));
		this.compressionRatio = new ParameterTextField(ControlsProperties.getString("String_CompressionRatio"));
		this.compressionRatio.setEnabled(false);
		this.prjFile = new ParameterFile(CoreProperties.getString("String_WorldFile"));
		String moduleForTFW = "ExportGrid_TFW";
		if (!SmFileChoose.isModuleExist(moduleForTFW)) {
			String fileFilters = SmFileChoose.createFileFilter(ControlsProperties.getString("string_filetype_tfw"), "tfw");
			SmFileChoose.addNewNode(fileFilters, System.getProperty("user.dir"),
					ControlsProperties.getString("String_FormExport_FormText"), moduleForTFW, "SaveOne");
		}
		prjFile.setModuleName(moduleForTFW);

		this.prjFile.setEnabled(false);
		this.checkBoxTFW = new ParameterCheckBox(ControlsProperties.getString("String_ExportingAsGeoTransformFile"));
		this.checkBoxTFW.setEnabled(false);
		this.checkBoxTFW.setSelectedItem(true);
		this.password = new ParameterPassword(ProcessProperties.getString("String_Password"));
		this.password.setEnabled(false);
		this.confirmPassword = new ParameterPassword(ControlsProperties.getString("String_Label_ConfirmPassword"));
		this.confirmPassword.setEnabled(false);
		this.basicCombine.addParameters(this.supportType, this.targetName
				, this.exportPath, this.cover);
		this.gridCombine.addParameters(this.compressionRatio, this.prjFile, this.checkBoxTFW,
				this.password, this.confirmPassword);
		this.parameters.setParameters(this.sourceInfo, this.basicCombine, this.gridCombine);
		this.parameters.addInputParameters(INPUT_DATA, DatasetTypes.GRID, this.sourceInfo);
		resetDataset();
	}

	protected void resetDataset() {
		super.resetDataset();
		initComponentsState(exportSetting);
	}

	protected void resetExportSetting() {
		super.resetExportSetting();
		initComponentsState(exportSetting);
	}

	private void initComponentsState(ExportSetting exportSetting) {
		this.prjFile.setEnabled(false);
		this.prjFile.setSelectedItem("");
		this.compressionRatio.setEnabled(false);
		this.compressionRatio.setSelectedItem("");
		this.checkBoxTFW.setEnabled(false);
		this.checkBoxTFW.setSelectedItem("false");
		this.password.setEnabled(false);
		this.password.setSelectedItem("");
		this.confirmPassword.setEnabled(false);
		this.confirmPassword.setSelectedItem("");
		if (exportSetting instanceof ExportSettingBMP || exportSetting instanceof ExportSettingGIF ||
				exportSetting instanceof ExportSettingJPG || exportSetting instanceof ExportSettingPNG) {
			this.prjFile.setEnabled(true);
			if (exportSetting instanceof ExportSettingJPG) {
				compressionRatio.setEnabled(true);
				compressionRatio.setSelectedItem(String.valueOf(((ExportSettingJPG) exportSetting).getCompression()));
			}
		} else if (exportSetting instanceof ExportSettingTIF) {
			this.checkBoxTFW.setEnabled(true);
			this.checkBoxTFW.setSelectedItem(((ExportSettingTIF) exportSetting).isExportingGeoTransformFile());
		} else if (exportSetting instanceof ExportSettingSIT) {
			this.password.setEnabled(true);
			this.confirmPassword.setEnabled(true);
		}
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		boolean isOverwrite = Boolean.valueOf(cover.getSelectedItem().toString());
		String targetPath = getFilePath();
		if (StringUtilities.isNullOrEmpty(targetPath)) {
			Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_ExportPathIsNull"));
			return false;
		}
		if (new File(targetPath).exists() && !isOverwrite) {
			Application.getActiveApplication().getOutput().output(MessageFormat.format(ControlsProperties.getString("String_DuplicateFileError"), targetPath));
		} else if (!StringUtilities.isNullOrEmpty(targetPath)) {
			setExportSettingInfo(isOverwrite);
			isSuccessful = printResultInfo(isSuccessful, targetPath, this.exportListener);
		}
		return isSuccessful;
	}

	@Override
	protected void setExportSettingInfo(boolean isOverwrite) {
		super.setExportSettingInfo(isOverwrite);
		if (exportSetting instanceof ExportSettingJPG) {
			((ExportSettingJPG) exportSetting).setCompression(Integer.valueOf(compressionRatio.getSelectedItem()));
			((ExportSettingJPG) exportSetting).setWorldFilePath(prjFile.getSelectedItem());
		} else if (exportSetting instanceof ExportSettingBMP) {
			((ExportSettingBMP) exportSetting).setWorldFilePath(prjFile.getSelectedItem());
		} else if (exportSetting instanceof ExportSettingGIF) {
			((ExportSettingGIF) exportSetting).setWorldFilePath(prjFile.getSelectedItem());
		} else if (exportSetting instanceof ExportSettingPNG) {
			((ExportSettingPNG) exportSetting).setWorldFilePath(prjFile.getSelectedItem());
		} else if (exportSetting instanceof ExportSettingTIF) {
			((ExportSettingTIF) exportSetting).setExportAsTile(Boolean.valueOf(checkBoxTFW.getSelectedItem()));
		} else if (exportSetting instanceof ExportSettingSIT) {
			((ExportSettingSIT) exportSetting).setPassword(this.password.getSelectedItem().toString());
		}
	}

	@Override
	public String getKey() {
		return MetaKeys.EXPORTGRID;
	}
}
