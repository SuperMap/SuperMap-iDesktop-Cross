package com.supermap.desktop.WorkflowView.meta.metaProcessImplements;

import com.supermap.analyst.spatialanalyst.OverlayAnalyst;
import com.supermap.analyst.spatialanalyst.OverlayAnalystParameter;
import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.WorkflowView.meta.loader.OverlayProcessLoader;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.enums.LengthUnit;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasetConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.events.RunningEvent;
import com.supermap.desktop.process.loader.IProcessLoader;
import com.supermap.desktop.process.parameter.ParameterOverlayAnalystInfo;
import com.supermap.desktop.process.parameter.interfaces.IParameterPanel;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.properties.CommonProperties;
import com.supermap.desktop.utilities.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;

/**
 * Created by xie on 2017/2/14.
 * 叠加分析
 */
public class MetaProcessOverlayAnalyst extends MetaProcess {
	private final static String INPUT_DATA = CommonProperties.getString("String_GroupBox_SourceData");
	private final static String OVERLAY_DATA = CommonProperties.getString("String_GroupBox_OverlayDataset");
	private final static String OUTPUT_DATA = "OverlayResult";

	private OverlayAnalystType analystType;
	private ParameterDatasourceConstrained parameterSourceDatasource = new ParameterDatasourceConstrained();
	private ParameterSingleDataset parameterSourceDataset = new ParameterSingleDataset();
	private ParameterDatasourceConstrained parameterOverlayDatasource = new ParameterDatasourceConstrained();
	private ParameterSingleDataset parameterOverlayDataset = new ParameterSingleDataset(DatasetType.POINT, DatasetType.LINE, DatasetType.REGION);
	private ParameterDatasourceConstrained parameterResultDatasource = new ParameterDatasourceConstrained();
	private ParameterTextField parameterSaveDataset = new ParameterTextField();
	private ParameterFieldSetDialog parameterFieldSetDialog = new ParameterFieldSetDialog();
	private ParameterNumber parameterTolerance = new ParameterNumber();
	private ParameterLabel parameterUnit = new ParameterLabel();

	private SteppedListener steppedListener = new SteppedListener() {
		@Override
		public void stepped(SteppedEvent steppedEvent) {
			RunningEvent event = new RunningEvent(MetaProcessOverlayAnalyst.this, steppedEvent.getPercent(), steppedEvent.getMessage());
			fireRunning(event);

			if (event.isCancel()) {
				steppedEvent.setCancel(true);
			}
		}
	};

	public PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (parameterSourceDataset.getSelectedItem() != null && evt.getNewValue() instanceof DatasetVector) {
				parameterTolerance.setSelectedItem(DoubleUtilities.getFormatString(DatasetUtilities.getDefaultTolerance((DatasetVector) evt.getNewValue()).getNodeSnap()));
				parameterUnit.setSelectedItem(LengthUnit.convertForm(((DatasetVector) evt.getNewValue()).getPrjCoordSys().getCoordUnit()));
			}
		}
	};

	@Override
	public Class<? extends IProcessLoader> getLoader() {
		return OverlayProcessLoader.class;
	}

	public MetaProcessOverlayAnalyst(OverlayAnalystType analystType) {
		this.analystType = analystType;
		setTitle(analystType.toString());
		initParameters();
		initParameterLayout();
		initParameterConstraint();
		initParameterStates();
		registerEvents();
	}

	private void initParameters() {

		parameterSourceDatasource.setDescribe(CommonProperties.getString(CommonProperties.Label_Datasource));
		parameterOverlayDatasource.setDescribe(CommonProperties.getString(CommonProperties.Label_Datasource));
		parameterResultDatasource.setDescribe(CommonProperties.getString(CommonProperties.Label_Datasource));
		parameterResultDatasource.setReadOnlyNeeded(false);
		parameterSaveDataset.setDescribe(CommonProperties.getString(CommonProperties.Label_Dataset));
		parameterTolerance.setDescribe(CommonProperties.getString("String_Label_Tolerance"));

//		parameterCheckBoxIsCompareResult.setDescribe(CommonProperties.getString("String_CheckBox_ResultComparison"));
	}

	private void initParameterLayout() {
		ParameterCombine parameterCombineSource = new ParameterCombine();
		parameterCombineSource.setDescribe(ControlsProperties.getString("String_GroupBox_SourceDataset"));
		parameterCombineSource.addParameters(parameterSourceDatasource, parameterSourceDataset);

		ParameterCombine parameterCombineResult = new ParameterCombine();
		parameterCombineResult.setDescribe(CommonProperties.getString("String_GroupBox_OverlayDataset"));
		parameterCombineResult.addParameters(parameterOverlayDatasource, parameterOverlayDataset);

		ParameterCombine parameterCombineResultSet = new ParameterCombine();
		parameterCombineResultSet.setDescribe(CommonProperties.getString("String_ResultSet"));
		ParameterCombine parameterCombineParent = new ParameterCombine(ParameterCombine.HORIZONTAL);
		parameterCombineParent.addParameters(
				new ParameterCombine().addParameters(parameterSaveDataset, parameterTolerance),
				new ParameterCombine().addParameters(parameterFieldSetDialog, parameterUnit));
		parameterCombineParent.setWeightIndex(0);
		parameterCombineResultSet.addParameters(parameterResultDatasource, parameterCombineParent
//				, parameterCheckBoxIsCompareResult);
		);
		parameters.setParameters(parameterCombineSource, parameterCombineResult, parameterCombineResultSet);
		this.getParameters().addInputParameters(INPUT_DATA, DatasetTypes.VECTOR, parameterCombineSource);
		this.getParameters().addInputParameters(OVERLAY_DATA, DatasetTypes.VECTOR, parameterCombineResult);
		this.getParameters().addOutputParameters(OUTPUT_DATA,
				MessageFormat.format(ProcessOutputResultProperties.getString("String_Result"), analystType.toString()),
				DatasetTypes.VECTOR, parameterCombineResultSet);
	}

	private void initParameterConstraint() {
		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(parameterSourceDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(parameterSourceDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasourceConstraint equalDatasourceConstraint1 = new EqualDatasourceConstraint();
		equalDatasourceConstraint1.constrained(parameterOverlayDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint1.constrained(parameterOverlayDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		EqualDatasetConstraint equalDatasetConstraint = new EqualDatasetConstraint();
		equalDatasetConstraint.constrained(parameterSourceDataset, ParameterSingleDataset.DATASET_FIELD_NAME);
		equalDatasetConstraint.constrained(parameterFieldSetDialog, ParameterFieldSetDialog.SOURCE_DATASET_FIELD_NAME);

		EqualDatasetConstraint equalDatasetConstraint1 = new EqualDatasetConstraint();
		equalDatasetConstraint1.constrained(parameterOverlayDataset, ParameterSingleDataset.DATASET_FIELD_NAME);
		equalDatasetConstraint1.constrained(parameterFieldSetDialog, ParameterFieldSetDialog.RESULT_DATASET_FIELD_NAME);

		DatasourceConstraint.getInstance().constrained(parameterResultDatasource, ParameterDatasource.DATASOURCE_FIELD_NAME);
		DatasourceConstraint.getInstance().constrained(parameterSaveDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);
	}

	private void initParameterStates() {
		DatasetVector dataset;
		if (this.analystType == OverlayAnalystType.UNION || this.analystType == OverlayAnalystType.XOR || this.analystType == OverlayAnalystType.UPDATE) {
			parameterSourceDataset.setDatasetTypes(DatasetType.REGION);
			dataset = (DatasetVector) DatasetUtilities.getDefaultDataset(DatasetType.REGION);
		} else {
			parameterSourceDataset.setDatasetTypes(DatasetType.POINT, DatasetType.LINE, DatasetType.REGION);
			dataset = DatasetUtilities.getDefaultDatasetVector();
		}
		parameterOverlayDataset.setDatasetTypes(DatasetType.REGION);

		if (dataset != null) {
			parameterSourceDatasource.setSelectedItem(dataset.getDatasource());
			parameterSourceDataset.setDatasource(dataset.getDatasource());
			parameterSourceDataset.setSelectedItem(dataset);
			if (!dataset.getDatasource().isReadOnly()) {
				parameterResultDatasource.setSelectedItem(dataset.getDatasource());
			} else {
				Datasource defaultResultDatasource = DatasourceUtilities.getDefaultResultDatasource();
				parameterResultDatasource.setSelectedItem(defaultResultDatasource);
			}
			String resultName = this.analystType.defaultResultName();
			Datasource datasource = parameterResultDatasource.getSelectedItem();
			if (datasource != null) {
				resultName = datasource.getDatasets().getAvailableDatasetName(resultName);
			}
			parameterSaveDataset.setSelectedItem(resultName);
			if ((this.analystType == OverlayAnalystType.UNION || this.analystType == OverlayAnalystType.XOR || this.analystType == OverlayAnalystType.UPDATE) && dataset.getType() == DatasetType.REGION) {
				parameterTolerance.setSelectedItem(DoubleUtilities.getFormatString(DatasetUtilities.getDefaultTolerance(dataset).getNodeSnap()));
				parameterUnit.setDescribe(LengthUnit.convertForm(dataset.getPrjCoordSys().getCoordUnit()).toString());
			}
			if (this.analystType == OverlayAnalystType.CLIP || this.analystType == OverlayAnalystType.ERASE || this.analystType == OverlayAnalystType.INTERSECT || this.analystType == OverlayAnalystType.IDENTITY) {
				parameterTolerance.setSelectedItem(DoubleUtilities.getFormatString(DatasetUtilities.getDefaultTolerance(dataset).getNodeSnap()));
				parameterUnit.setDescribe(LengthUnit.convertForm(dataset.getPrjCoordSys().getCoordUnit()).toString());
			}
			parameterTolerance.setMinValue(0);
			parameterTolerance.setIsIncludeMin(true);
			parameterTolerance.setRequisite(true);
		} else {
			parameterTolerance.setSelectedItem("");
			parameterUnit.setDescribe("");
		}
		if (this.analystType == OverlayAnalystType.CLIP || this.analystType == OverlayAnalystType.ERASE || this.analystType == OverlayAnalystType.UPDATE) {
			parameterFieldSetDialog.setEnabled(false);
		} else {
			parameterFieldSetDialog.setEnabled(true);
		}
	}

	private void registerEvents() {
		this.parameterSourceDataset.addPropertyListener(this.propertyChangeListener);
	}

	@Override
	public IParameterPanel getComponent() {
		return parameters.getPanel();
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;

		try {
			ParameterOverlayAnalystInfo info = new ParameterOverlayAnalystInfo();
			if (parameters.getInputs().getData(INPUT_DATA) != null && parameters.getInputs().getData(INPUT_DATA).getValue() instanceof DatasetVector) {
				info.sourceDataset = (DatasetVector) parameters.getInputs().getData(INPUT_DATA).getValue();
				info.sourceDatatsource = info.sourceDataset.getDatasource();
			} else {
				info.sourceDatatsource = parameterSourceDatasource.getSelectedItem();
				info.sourceDataset = (DatasetVector) parameterSourceDataset.getSelectedItem();
			}

			if (parameters.getInputs().getData(OVERLAY_DATA) != null && parameters.getInputs().getData(OVERLAY_DATA).getValue() instanceof DatasetVector) {
				info.overlayAnalystDataset = (DatasetVector) parameters.getInputs().getData(OVERLAY_DATA).getValue();
				info.overlayAnalystDatasource = info.overlayAnalystDataset.getDatasource();
			} else {
				info.overlayAnalystDatasource = parameterOverlayDatasource.getSelectedItem();
				info.overlayAnalystDataset = (DatasetVector) parameterOverlayDataset.getSelectedItem();
			}

			if (info.sourceDataset == info.overlayAnalystDataset) {
				Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_SameDataSet_error"));
				return false;
			}

			info.targetDatasource = parameterResultDatasource.getSelectedItem();
			// 对叠加分析生成的数据集名称做一个限制-yuanR2017.9.20
			if (StringUtilities.isNullOrEmptyString(parameterSaveDataset.getSelectedItem())) {
				info.targetDataset = this.analystType.defaultResultName();
			} else {
				info.targetDataset = parameterSaveDataset.getSelectedItem();
			}
			OverlayAnalystParameter overlayAnalystParameter = new OverlayAnalystParameter();
			if (parameterFieldSetDialog.getSourceFieldNames() != null) {
				overlayAnalystParameter.setSourceRetainedFields(parameterFieldSetDialog.getSourceFieldNames());
				overlayAnalystParameter.setOperationRetainedFields(parameterFieldSetDialog.getResultFieldNames());
			}
			overlayAnalystParameter.setTolerance(DoubleUtilities.stringToValue(parameterTolerance.getSelectedItem()));
			info.analystParameter = overlayAnalystParameter;

			if (null == info.sourceDataset || null == info.overlayAnalystDataset
					|| null == info.targetDataset) {
				return false;
			}
			if (!isSameProjection(info.sourceDataset.getPrjCoordSys(), info.overlayAnalystDataset.getPrjCoordSys())) {
				Application.getActiveApplication().getOutput().output(ControlsProperties.getString("String_PrjCoordSys_Different") + "\n" + ControlsProperties.getString("String_Parameters"));
				Application.getActiveApplication().getOutput().output(MessageFormat.format(ControlsProperties.getString("String_OverlayAnalyst_Failed"), info.sourceDataset.getName() + "@" + info.sourceDataset.getDatasource().getAlias()
						, info.overlayAnalystDataset.getName() + "@" + info.overlayAnalystDataset.getDatasource().getAlias(), analystType.toString()));
				return false;
			}
			OverlayAnalyst.addSteppedListener(this.steppedListener);
			DatasetVectorInfo datasetVectorInfo = new DatasetVectorInfo();
			datasetVectorInfo.setType(info.sourceDataset.getType());
			datasetVectorInfo.setEncodeType(info.sourceDataset.getEncodeType());
			// 名称合法时可以设置名称
			datasetVectorInfo.setName(info.targetDatasource.getDatasets().getAvailableDatasetName(info.targetDataset));
			DatasetVector targetDataset = info.targetDatasource.getDatasets().create(datasetVectorInfo);
			targetDataset.setPrjCoordSys(info.sourceDataset.getPrjCoordSys());

			switch (analystType) {
				case CLIP:
					isSuccessful = OverlayAnalyst.clip(info.sourceDataset, info.overlayAnalystDataset, targetDataset, info.analystParameter);
					break;
				case ERASE:
					isSuccessful = OverlayAnalyst.erase(info.sourceDataset, info.overlayAnalystDataset, targetDataset, info.analystParameter);
					break;
				case IDENTITY:
					isSuccessful = OverlayAnalyst.identity(info.sourceDataset, info.overlayAnalystDataset, targetDataset, info.analystParameter);
					break;
				case INTERSECT:
					isSuccessful = OverlayAnalyst.intersect(info.sourceDataset, info.overlayAnalystDataset, targetDataset, info.analystParameter);
					break;
				case UNION:
					isSuccessful = OverlayAnalyst.union(info.sourceDataset, info.overlayAnalystDataset, targetDataset, info.analystParameter);
					break;
				case XOR:
					isSuccessful = OverlayAnalyst.xOR(info.sourceDataset, info.overlayAnalystDataset, targetDataset, info.analystParameter);
					break;
				case UPDATE:
					isSuccessful = OverlayAnalyst.update(info.sourceDataset, info.overlayAnalystDataset, targetDataset, info.analystParameter);
					break;
				default:
					break;
			}
			this.parameters.getOutputs().getData(OUTPUT_DATA).setValue(targetDataset);
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			OverlayAnalyst.removeSteppedListener(this.steppedListener);
			if (!isSuccessful) {
				parameterResultDatasource.getSelectedItem().getDatasets().delete(parameterSaveDataset.getSelectedItem());
			}
		}
		return isSuccessful;
	}

	@Override
	public String getKey() {
		String key = "";
		switch (analystType) {
			case CLIP:
				key = MetaKeys.OVERLAY_ANALYST_CLIP;
				break;
			case UNION:
				key = MetaKeys.OVERLAY_ANALYST_UNION;
				break;
			case ERASE:
				key = MetaKeys.OVERLAY_ANALYST_ERASE;
				break;
			case IDENTITY:
				key = MetaKeys.OVERLAY_ANALYST_IDENTITY;
				break;
			case INTERSECT:
				key = MetaKeys.OVERLAY_ANALYST_INTERSECT;
				break;
			case UPDATE:
				key = MetaKeys.OVERLAY_ANALYST_UPDATE;
				break;
			case XOR:
				key = MetaKeys.OVERLAY_ANALYST_XOR;
				break;

		}
		return key;
	}

	private boolean isSameProjection(PrjCoordSys prjCoordSys, PrjCoordSys prjCoordSys1) {
		if (prjCoordSys.getType() != prjCoordSys1.getType()) {
			return false;
		}
		if (prjCoordSys.getGeoCoordSys() == prjCoordSys1.getGeoCoordSys()) {
			return true;
		}
		if (prjCoordSys.getGeoCoordSys() == null || prjCoordSys1.getGeoCoordSys() == null) {
			return false;
		}
		if (prjCoordSys.getGeoCoordSys().getType() != prjCoordSys1.getGeoCoordSys().getType()) {
			return false;
		}
		return true;
	}


}
