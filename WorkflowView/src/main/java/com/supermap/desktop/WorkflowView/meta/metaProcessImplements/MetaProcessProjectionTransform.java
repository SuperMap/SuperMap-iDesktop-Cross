package com.supermap.desktop.WorkflowView.meta.metaProcessImplements;

import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.events.RunningEvent;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.interfaces.IParameterPanel;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.process.parameters.ParameterPanels.PrjTranslator.ParameterTargetCoordSys;
import com.supermap.desktop.properties.CommonProperties;
import com.supermap.desktop.properties.CoordSysTransMethodProperties;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.ui.controls.prjcoordsys.JDialogPrjCoordSysTranslatorSettings;
import com.supermap.desktop.utilities.DatasetUtilities;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

/**
 * Created by yuanR on 2017/9/27 0027.
 * 投影转换
 */
public class MetaProcessProjectionTransform extends MetaProcess {
	private final static String INPUT_DATA = CommonProperties.getString("String_GroupBox_SourceData");
	private final static String OUTPUT_DATA = "ProjectionTransformResult";

	private PrjCoordSys prjCoordSys = null;
	private CoordSysTransParameter parameter = new CoordSysTransParameter();
	private ParameterDatasourceConstrained parameterDatasource;
	private ParameterSingleDataset parameterDataset;

	private ParameterComboBox parameterMode = new ParameterComboBox(ControlsProperties.getString("String_TransMethod"));
	private ParameterButton parameterSetTransform = new ParameterButton(ProcessProperties.getString("String_ParamSet"));

	// 目标坐标系
	private ParameterTargetCoordSys parameterTargetCoordSys = new ParameterTargetCoordSys();

	private ParameterSaveDataset parameterSaveDataset;

	public MetaProcessProjectionTransform() {
		setTitle(ProcessProperties.getString("String_ProjectionTransform"));
		initParameters();
		initParameterConstraint();
		initParameterListeners();
		initParameterState();
	}


	private void initParameters() {
		this.parameterDatasource = new ParameterDatasourceConstrained();
		this.parameterDataset = new ParameterSingleDataset();
		//  支持可读
		this.parameterDatasource.setReadOnlyNeeded(true);

		ParameterCombine parameterCombineSource = new ParameterCombine();
		parameterCombineSource.setDescribe(SOURCE_PANEL_DESCRIPTION);
		parameterCombineSource.addParameters(this.parameterDatasource, this.parameterDataset);

		this.parameterMode.setItems(
				new ParameterDataNode(CoordSysTransMethodProperties.getString(CoordSysTransMethodProperties.GeocentricTranslation), CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION),
				new ParameterDataNode(CoordSysTransMethodProperties.getString(CoordSysTransMethodProperties.Molodensky), CoordSysTransMethod.MTH_MOLODENSKY),
				new ParameterDataNode(CoordSysTransMethodProperties.getString(CoordSysTransMethodProperties.MolodenskyAbridged), CoordSysTransMethod.MTH_MOLODENSKY_ABRIDGED),
				new ParameterDataNode(CoordSysTransMethodProperties.getString(CoordSysTransMethodProperties.PositionVector), CoordSysTransMethod.MTH_POSITION_VECTOR),
				new ParameterDataNode(CoordSysTransMethodProperties.getString(CoordSysTransMethodProperties.CoordinateFrame), CoordSysTransMethod.MTH_COORDINATE_FRAME),
				new ParameterDataNode(CoordSysTransMethodProperties.getString(CoordSysTransMethodProperties.BursaWolf), CoordSysTransMethod.MTH_BURSA_WOLF)
		);

		ParameterCombine parameterCombine = new ParameterCombine(ParameterCombine.HORIZONTAL);
		parameterCombine.addParameters(this.parameterMode, this.parameterSetTransform);
		parameterCombine.setWeightIndex(0);

		ParameterCombine parameterCombineTargetCoordSys = new ParameterCombine();
		parameterCombineTargetCoordSys.addParameters(this.parameterTargetCoordSys);
		ParameterCombine parameterCombineSetting = new ParameterCombine();
		parameterCombineSetting.setDescribe(SETTING_PANEL_DESCRIPTION);
		parameterCombineSetting.addParameters(parameterCombine, new ParameterLabel().setDescribe(ControlsProperties.getString("String_GroupBox_TarCoorSys") + ":"), parameterCombineTargetCoordSys);

		this.parameterSaveDataset = new ParameterSaveDataset();
		this.parameterSaveDataset.setDefaultDatasetName("result_prjTransform");
		ParameterCombine parameterResult = new ParameterCombine();
		parameterResult.setDescribe(RESULT_PANEL_DESCRIPTION);
		parameterResult.addParameters(this.parameterSaveDataset);

		this.parameters.setParameters(parameterCombineSource, parameterCombineSetting, parameterResult);
		this.parameters.addInputParameters(INPUT_DATA, DatasetTypes.DATASET, parameterCombineSource);
		this.parameters.addOutputParameters(OUTPUT_DATA,
				ProcessOutputResultProperties.getString("String_PrjTransformResult"),
				DatasetTypes.DATASET, parameterResult);
	}

	private void initParameterState() {
		Dataset defaultDataset = DatasetUtilities.getDefaultDataset();
		if (defaultDataset != null) {
			this.parameterDatasource.setSelectedItem(defaultDataset.getDatasource());
			this.parameterDataset.setSelectedItem(defaultDataset);
			this.parameterSaveDataset.setResultDatasource(defaultDataset.getDatasource());
		}
	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(this.parameterDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(this.parameterDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		DatasourceConstraint.getInstance().constrained(this.parameterSaveDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);

	}

	private void initParameterListeners() {

		this.parameterSetTransform.setActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialogPrjCoordSysTranslatorSettings dialogPrjCoordSysTranslatorSettings = new JDialogPrjCoordSysTranslatorSettings();
				dialogPrjCoordSysTranslatorSettings.fillCoordSysTransMethodValue((CoordSysTransMethod) parameterMode.getSelectedData());
				dialogPrjCoordSysTranslatorSettings.fillCoordSysTransParameter(parameter);
				if (dialogPrjCoordSysTranslatorSettings.showDialog() == DialogResult.OK) {
					parameter = dialogPrjCoordSysTranslatorSettings.getParameter();
					parameterMode.setSelectedItem(dialogPrjCoordSysTranslatorSettings.getMethod());
				}
			}
		});
	}


	@Override
	public IParameterPanel getComponent() {
		return parameters.getPanel();
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		Dataset src;
		if (this.getParameters().getInputs().getData(INPUT_DATA).getValue() != null) {
			src = (Dataset) this.getParameters().getInputs().getData(INPUT_DATA).getValue();
		} else {
			src = this.parameterDataset.getSelectedItem();
		}

		this.prjCoordSys = parameterTargetCoordSys.getSelectedItem();
		// 当未设置投影时，给定原数据集投影,防止参数为空报错-yuanR2017.9.6
		if (this.prjCoordSys == null) {
			Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_NeedSetProjection"));
			return false;
		}
		try {
			fireRunning(new RunningEvent(this, 0, "Start set geoCoorSys"));
			CoordSysTransMethod method = (CoordSysTransMethod) this.parameterMode.getSelectedData();
			String resultDatasetName = this.parameterSaveDataset.getResultDatasource().getDatasets().getAvailableDatasetName(this.parameterSaveDataset.getDatasetName());
			Dataset dataset = CoordSysTranslator.convert(src, this.prjCoordSys, this.parameterSaveDataset.getResultDatasource(), resultDatasetName, this.parameter, method);
			this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(dataset);
			isSuccessful = (dataset != null);

			if (isSuccessful) {
				getParameters().getOutputs().getData(OUTPUT_DATA).setValue(dataset);
				Application.getActiveApplication().getOutput().output(MessageFormat.format(ControlsProperties.getString("String_CoordSysTrans_RasterSuccess"),
						src.getName(), src.getDatasource().getAlias(), resultDatasetName, this.parameterSaveDataset.getResultDatasource().getAlias()));
			} else {
				Application.getActiveApplication().getOutput().output(MessageFormat.format(ControlsProperties.getString("String_CoordSysTrans_Failed"),
						src.getName(), src.getDatasource().getAlias(), resultDatasetName, this.parameterSaveDataset.getResultDatasource().getAlias()));
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {

		}
		return isSuccessful;
	}

	@Override
	public IParameters getParameters() {
		return parameters;
	}

	@Override
	public String getKey() {
		return MetaKeys.PROJECTIONTRANSFORM;
	}

}
