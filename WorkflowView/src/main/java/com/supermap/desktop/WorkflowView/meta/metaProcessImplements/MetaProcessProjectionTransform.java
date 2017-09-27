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
import com.supermap.desktop.properties.CommonProperties;
import com.supermap.desktop.properties.CoordSysTransMethodProperties;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.ui.controls.prjcoordsys.JDialogPrjCoordSysSettings;
import com.supermap.desktop.ui.controls.prjcoordsys.JDialogPrjCoordSysTranslatorSettings;
import com.supermap.desktop.utilities.DatasetUtilities;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;

/**
 * Created by yuanR on 2017/9/27 0027.
 * 投影转换
 */
public class MetaProcessProjectionTransform extends MetaProcess {
	private final static String INPUT_DATA = CommonProperties.getString("String_GroupBox_SourceData");
	private final static String OUTPUT_DATA = "ProjectionTransformResult";

	private PrjCoordSys prjCoordSys = null;
	private CoordSysTransParameter parameter = null;
	private ParameterDatasourceConstrained parameterDatasource;
	private ParameterSingleDataset parameterDataset;

	private ParameterComboBox parameterMode = new ParameterComboBox(ControlsProperties.getString("String_TransMethod"));
	private ParameterButton parameterSetTransform = new ParameterButton(ProcessProperties.getString("String_ParamSet"));
	private ParameterButton parameterSetProjection = new ParameterButton(ProcessProperties.getString("String_setProject"));

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
		this.parameterDatasource.setDescribe(CommonProperties.getString("String_SourceDatasource"));
		// 不支持可读
		this.parameterDatasource.setReadOnlyNeeded(false);

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
		parameterCombine.addParameters(new ParameterCombine(), this.parameterSetTransform, this.parameterSetProjection);
		parameterCombine.setWeightIndex(0);
		ParameterCombine parameterCombineSetting = new ParameterCombine();
		parameterCombineSetting.setDescribe(SETTING_PANEL_DESCRIPTION);
		parameterCombineSetting.addParameters(this.parameterMode, parameterCombine);

		this.parameterSaveDataset = new ParameterSaveDataset();
		this.parameterSaveDataset.setDefaultDatasetName("result_prjTransform");
		ParameterCombine parameterResult = new ParameterCombine();
		parameterResult.setDescribe(RESULT_PANEL_DESCRIPTION);
		parameterResult.addParameters(this.parameterSaveDataset);

		this.parameters.setParameters(parameterCombineSource, parameterCombineSetting, parameterResult);
		this.parameters.addInputParameters(INPUT_DATA, DatasetTypes.DATASET, parameterCombineSource);
		this.parameters.addOutputParameters(OUTPUT_DATA,
				ProcessOutputResultProperties.getString("String_PrjTransformResult"),
				DatasetTypes.DATASET, this.parameterDataset);
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
		this.parameterSetProjection.setActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialogPrjCoordSysSettings jDialogPrjCoordSysSettings = new JDialogPrjCoordSysSettings();
				if (jDialogPrjCoordSysSettings.showDialog() == DialogResult.OK) {
					prjCoordSys = jDialogPrjCoordSysSettings.getPrjCoordSys();
				}
			}
		});

		this.parameterSetTransform.setActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialogPrjCoordSysTranslatorSettings dialogPrjCoordSysTranslatorSettings = new JDialogPrjCoordSysTranslatorSettings();
				if (dialogPrjCoordSysTranslatorSettings.showDialog() == DialogResult.OK) {
					parameter = dialogPrjCoordSysTranslatorSettings.getParameter();
					parameterMode.setSelectedItem(dialogPrjCoordSysTranslatorSettings.getMethod());
				}
			}
		});

		this.parameterDataset.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				parameterSaveDataset.setEnabled(parameterDataset.getSelectedDataset().getType().equals(DatasetType.GRID)
						|| parameterDataset.getSelectedDataset().getType().equals(DatasetType.IMAGE));
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
		Object value = this.getParameters().getInputs().getData(INPUT_DATA).getValue();
		if (value != null && value instanceof Dataset) {
			src = (Dataset) this.getParameters().getInputs().getData(INPUT_DATA).getValue();
		} else {
			src = this.parameterDataset.getSelectedItem();
		}
		// 当未设置投影时，给定原数据集投影,防止参数为空报错-yuanR2017.9.6
		if (this.prjCoordSys == null) {
			this.prjCoordSys = src.getPrjCoordSys();
			Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_NeedSetProjection"));
			return isSuccessful;
		}
		try {
			CoordSysTransMethod method = (CoordSysTransMethod) this.parameterMode.getSelectedData();
			fireRunning(new RunningEvent(this, 0, "Start set geoCoorSys"));
			if (parameterSaveDataset.isEnabled()) {
				String resultDatasetName = parameterSaveDataset.getResultDatasource().getDatasets().getAvailableDatasetName(parameterSaveDataset.getDatasetName());
				Dataset dataset = CoordSysTranslator.convert(src, this.prjCoordSys, parameterSaveDataset.getResultDatasource(), resultDatasetName, this.parameter, method);
				isSuccessful = (dataset != null);

				if (isSuccessful) {
					getParameters().getOutputs().getData(OUTPUT_DATA).setValue(dataset);
					Application.getActiveApplication().getOutput().output(MessageFormat.format(ControlsProperties.getString("String_CoordSysTrans_RasterSuccess"),
							src.getDatasource().getAlias(), src.getName(), parameterSaveDataset.getResultDatasource().getAlias(), resultDatasetName));
				} else {
					Application.getActiveApplication().getOutput().output(MessageFormat.format(ControlsProperties.getString("String_CoordSysTrans_Failed"),
							src.getDatasource().getAlias(), src.getName(), parameterSaveDataset.getResultDatasource().getAlias(), resultDatasetName));
				}

			} else {
				isSuccessful = CoordSysTranslator.convert(src, this.prjCoordSys, this.parameter, method);
				if (isSuccessful) {
					Application.getActiveApplication().getOutput().output(MessageFormat.format(ControlsProperties.getString("String_CoordSysTrans_VectorSuccess"),
							src.getDatasource().getAlias(), src.getName()));
					getParameters().getOutputs().getData(OUTPUT_DATA).setValue(src);
				} else {
					Application.getActiveApplication().getOutput().output(MessageFormat.format(ControlsProperties.getString("String_CoordSysTrans_Failed"),
							src.getDatasource().getAlias(), src.getName()));
				}
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

	@Override
	public boolean isChangeSourceData() {
		return true;
	}
}
