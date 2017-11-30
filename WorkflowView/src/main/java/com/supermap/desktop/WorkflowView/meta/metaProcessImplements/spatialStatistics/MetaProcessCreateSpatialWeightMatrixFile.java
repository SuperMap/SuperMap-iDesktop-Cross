package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.spatialStatistics;

import com.supermap.analyst.spatialstatistics.ConceptualizationModel;
import com.supermap.analyst.spatialstatistics.WeightsUtilities;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.process.types.BasicTypes;
import com.supermap.desktop.process.types.DatasetTypes;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.SmFileChoose;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * Created by yuanR on 2017/8/29 0029.
 * 生成空间权重矩阵文件
 */
public class MetaProcessCreateSpatialWeightMatrixFile extends MetaProcessAnalyzingPatterns {
	private static final String OUTPUT_DATA = "CreateSpatialWeightMatrixFileResult";
	private final static String OUTPUT_DATA_TABULAR = "CreateTabularResult";
	private ParameterFile parameterFile;
	// 是否生成属性表
	private ParameterCheckBox parameterCheckBox;
	private ParameterSaveDataset parameterSaveDataset;

	public MetaProcessCreateSpatialWeightMatrixFile() {
		setTitle(ProcessProperties.getString("String_CreateSpatialWeightMatrixFile"));
	}

	@Override
	protected void initHook() {
		this.dataset.setDatasetTypes(DatasetType.REGION, DatasetType.POINT, DatasetType.LINE);
		// 生成空间权重矩阵文件功能提出空间权重矩阵文件模型的选项
		this.parameterPatternsParameter.getParameterComboBoxConceptModel().removeAllItems();
		this.parameterPatternsParameter.getParameterComboBoxConceptModel().setItems(
				new ParameterDataNode(ProcessProperties.getString("String_FIXEDDISTANCEBAND"), ConceptualizationModel.FIXEDDISTANCEBAND),
				new ParameterDataNode(ProcessProperties.getString("String_CONTIGUITYEDGESNODE"), ConceptualizationModel.CONTIGUITYEDGESNODE),
				new ParameterDataNode(ProcessProperties.getString("String_CONTIGUITYEDGESONLY"), ConceptualizationModel.CONTIGUITYEDGESONLY),
				new ParameterDataNode(ProcessProperties.getString("String_INVERSEDISTANCE"), ConceptualizationModel.INVERSEDISTANCE),
				new ParameterDataNode(ProcessProperties.getString("String_INVERSEDISTANCESQUARED"), ConceptualizationModel.INVERSEDISTANCESQUARED),
				new ParameterDataNode(ProcessProperties.getString("String_KNEARESTNEIGHBORS"), ConceptualizationModel.KNEARESTNEIGHBORS),
//				new ParameterDataNode(ControlsProperties.getString("String_SPATIALWEIGHTMATRIXFILE"), ConceptualizationModel.SPATIALWEIGHTMATRIXFILE),
				new ParameterDataNode(ProcessProperties.getString("String_ZONEOFINDIFFERENCE"), ConceptualizationModel.ZONEOFINDIFFERENCE));


		this.parameterFile = new ParameterFile(ControlsProperties.getString("String_FileInputPath"));
		String modelName = "CreateSWMBFile";
		if (!SmFileChoose.isModuleExist(modelName)) {
			String fileFilters = SmFileChoose.createFileFilter(ProcessProperties.getString("String_SWMFilePath"), "swmb");
			SmFileChoose.addNewNode(fileFilters, System.getProperty("user.dir"),
					ControlsProperties.getString("String_Save"), modelName, "SaveOne");
		}
		this.parameterFile.setModuleName(modelName);
		String defaultPath = System.getProperty("user.dir") + File.separator + "newFile.swmb";
		this.parameterFile.setSelectedItem(defaultPath);
		this.parameterFile.setRequired(true);

		// 生成属性表
		final ParameterSwitch parameterSwitchMain = new ParameterSwitch();
		this.parameterSaveDataset = new ParameterSaveDataset();
		this.parameterSaveDataset.setDefaultDatasetName("result_createTabular");
		DatasourceConstraint.getInstance().constrained(this.parameterSaveDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);
		parameterSwitchMain.add("true", parameterSaveDataset);
		parameterSwitchMain.add("false", new ParameterCombine());
		parameterSwitchMain.switchParameter("false");
		this.parameterCheckBox = new ParameterCheckBox(ProcessProperties.getString("String_CreateTabular"));
		this.parameterCheckBox.setSelectedItem(false);
		this.parameterCheckBox.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(ParameterCheckBox.PARAMETER_CHECK_BOX_VALUE)) {
					if (Boolean.valueOf(parameterCheckBox.getSelectedItem())) {
						parameterSwitchMain.switchParameter("true");
					} else {
						parameterSwitchMain.switchParameter("false");
					}
				}
			}
		});

		ParameterCombine parameterCombine = new ParameterCombine();
		parameterCombine.addParameters(this.parameterFile, this.parameterCheckBox, parameterSwitchMain);
		parameterCombine.setDescribe(CoreProperties.getString("String_ResultSet"));
		this.parameters.addParameters(parameterCombine);
		this.parameters.addOutputParameters(OUTPUT_DATA, ControlsProperties.getString("String_SPATIALWEIGHTMATRIXFILE"), BasicTypes.STRING, this.parameterFile);
		this.parameters.addOutputParameters(OUTPUT_DATA_TABULAR, ProcessOutputResultProperties.getString("String_Result_Tabular"), DatasetTypes.TABULAR, parameterCombine);
	}

	@Override
	public String getKey() {
		return MetaKeys.CREATE_SPATIAL_WEIGHT_MATRIX_FILE;
	}

	@Override
	protected boolean doWork(DatasetVector datasetVector) {
		boolean isSuccessful = false;

		try {
			WeightsUtilities.addSteppedListener(this.steppedListener);
			// 唯一字段默认给SmID
			isSuccessful = WeightsUtilities.buildWeightMatrix(
					datasetVector, "SmID",
					this.parameterFile.getSelectedItem(),
					this.parameterPatternsParameter.getPatternParameter());

			if (isSuccessful) {
				this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(this.parameterFile);
			}

			// 属性表的创建需要依托于生成的空间权重举证文件
			if (isSuccessful && Boolean.valueOf(this.parameterCheckBox.getSelectedItem())) {
				DatasetVector tabularDataset = WeightsUtilities.converToTableDataset(this.parameterFile.getSelectedItem(), this.parameterSaveDataset.getResultDatasource(), this.parameterSaveDataset.getDatasetName());
				this.getParameters().getOutputs().getData(OUTPUT_DATA_TABULAR).setValue(tabularDataset);
				isSuccessful = (tabularDataset != null);
			}

		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			WeightsUtilities.removeSteppedListener(this.steppedListener);
		}
		return isSuccessful;
	}
}
