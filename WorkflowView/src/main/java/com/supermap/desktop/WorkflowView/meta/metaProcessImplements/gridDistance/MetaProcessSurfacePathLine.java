package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.gridDistance;

import com.supermap.analyst.spatialanalyst.DistanceAnalyst;
import com.supermap.analyst.spatialanalyst.DistanceAnalystParameter;
import com.supermap.analyst.spatialanalyst.PathLineResult;
import com.supermap.analyst.spatialanalyst.SmoothMethod;
import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.MetaProcess;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.constraint.ipls.DatasourceConstraint;
import com.supermap.desktop.process.constraint.ipls.EqualDatasourceConstraint;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.ipls.*;
import com.supermap.desktop.process.types.DatasetTypes;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.DatasetUtilities;
import com.supermap.desktop.utilities.StringUtilities;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * 两点间最短地表路径
 * Created by yuanR on 2017/8/7.
 */
public class MetaProcessSurfacePathLine extends MetaProcess {

	private final static String INPUT_DATA = CoreProperties.getString("String_GroupBox_SourceData");
	private final static String OUTPUT_DATA = "DEMPathLine";

	private ParameterDatasourceConstrained sourceDatasource;
	private ParameterSingleDataset sourceDataset;

	// 原点和目标点的坐标值
	private ParameterNumber parameterSourcePointX;
	private ParameterNumber parameterSourcePointY;
	private ParameterNumber parameterTargetPointX;
	private ParameterNumber parameterTargetPointY;

	// 键入原点和目标点
	private ParameterButton parameterInputSourcePoint;
	private ParameterButton parameterInputTargetPoint;
	//  光滑方法
	private ParameterComboBox parameterPathLineSmoothMethod;
	// 光滑系数
	private ParameterNumber parameterPathLineSmoothDegree;
	// 最大上坡角度
	private ParameterNumber parameterMaxUpslopeDegree;
	// 最大下坡角度
	private ParameterNumber parameterMaxDownslopeDegree;

	private ParameterSaveDataset resultDataset;

	/**
	 * 复制和粘贴的监听响应事件
	 */
	public ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals(parameterInputSourcePoint.getDescription())) {
				inputSourcePoint();
			} else if (e.getActionCommand().equals(parameterInputTargetPoint.getDescription())) {
				inputTargetPoint();
			}
		}
	};

	public MetaProcessSurfacePathLine() {
		setTitle(ProcessProperties.getString("String_SurfacePathLine"));
		initParameters();
		initParametersState();
		initParameterConstrint();
	}

	private void initParameters() {
		// 源数据
		this.sourceDatasource = new ParameterDatasourceConstrained();
		this.sourceDatasource.setDescribe(CoreProperties.getString("String_Label_Datasource"));
		this.sourceDataset = new ParameterSingleDataset(DatasetType.GRID);
		this.sourceDataset.setDescribe(CoreProperties.getString("String_Label_Dataset"));

		ParameterCombine sourceData = new ParameterCombine();
		sourceData.setDescribe(CoreProperties.getString("String_GroupBox_SourceData"));
		sourceData.addParameters(this.sourceDatasource, this.sourceDataset);

		// 参数设置
		// 原点和目标点坐标值
		parameterSourcePointX = new ParameterNumber(ControlsProperties.getString("String_SourcePointX"));
		parameterSourcePointY = new ParameterNumber(ControlsProperties.getString("String_SourcePointY"));
		parameterTargetPointX = new ParameterNumber(ControlsProperties.getString("String_TargetPointX"));
		parameterTargetPointY = new ParameterNumber(ControlsProperties.getString("String_TargetPointY"));

		parameterSourcePointX.setRequisite(true);
		parameterSourcePointY.setRequisite(true);
		parameterTargetPointX.setRequisite(true);
		parameterTargetPointY.setRequisite(true);

		parameterInputSourcePoint = new ParameterButton(ControlsProperties.getString("String_Label_InputSourcePoint"));
		parameterInputTargetPoint = new ParameterButton(ControlsProperties.getString("String_Label_InputTargetPoint"));

		parameterInputSourcePoint.setActionListener(actionListener);
		parameterInputTargetPoint.setActionListener(actionListener);
		ParameterCombine parameterCombineCopyPaste = new ParameterCombine(ParameterCombine.HORIZONTAL);
		parameterCombineCopyPaste.addParameters(new ParameterCombine(), parameterInputSourcePoint, parameterInputTargetPoint);
		parameterCombineCopyPaste.setWeightIndex(0);

		// 光滑方式
		parameterPathLineSmoothMethod = new ParameterComboBox(CoreProperties.getString("String_SmoothMethod"));
		parameterPathLineSmoothMethod.setItems(new ParameterDataNode(CoreProperties.getString("String_SmoothMethod_NONE"), SmoothMethod.NONE),
				new ParameterDataNode(CoreProperties.getString("String_SmoothMethod_BSLine"), SmoothMethod.BSPLINE),
				new ParameterDataNode(CoreProperties.getString("String_SmoothMethod_POLISH"), SmoothMethod.POLISH));
		// 光滑系数
		parameterPathLineSmoothDegree = new ParameterNumber(CoreProperties.getString("String_Smooth"));
		parameterPathLineSmoothDegree.setSelectedItem("2");
		parameterPathLineSmoothDegree.setMinValue(2);
		parameterPathLineSmoothDegree.setMaxValue(10);
		parameterPathLineSmoothDegree.setMaxBit(0);
		parameterPathLineSmoothDegree.setIncludeMax(true);
		parameterPathLineSmoothDegree.setIsIncludeMin(true);

		// 最大上坡角度
		parameterMaxUpslopeDegree = new ParameterNumber(ControlsProperties.getString("String_Label_MaxUpslopeDegree"));
		parameterMaxUpslopeDegree.setSelectedItem("90");
		parameterMaxUpslopeDegree.setMinValue(0);
		parameterMaxUpslopeDegree.setMaxValue(90);
		parameterMaxUpslopeDegree.setMaxBit(22);
		parameterMaxUpslopeDegree.setIncludeMax(true);
		parameterMaxUpslopeDegree.setIsIncludeMin(true);
		// 最大下坡角度
		parameterMaxDownslopeDegree = new ParameterNumber(ControlsProperties.getString("String_Label_MaxDownslopeDegree"));
		parameterMaxDownslopeDegree.setSelectedItem("90");
		parameterMaxDownslopeDegree.setMinValue(0);
		parameterMaxDownslopeDegree.setMaxValue(90);
		parameterMaxDownslopeDegree.setMaxBit(22);
		parameterMaxDownslopeDegree.setIncludeMax(true);
		parameterMaxDownslopeDegree.setIsIncludeMin(true);

		ParameterCombine parameterCombineSet = new ParameterCombine();
		parameterCombineSet.setDescribe(ProcessProperties.getString("String_setParameter"));
		parameterCombineSet.addParameters(
				parameterSourcePointX, parameterSourcePointY,
				parameterTargetPointX, parameterTargetPointY,
				parameterCombineCopyPaste,
				parameterPathLineSmoothMethod, parameterPathLineSmoothDegree,
				parameterMaxUpslopeDegree, parameterMaxDownslopeDegree
		);


		// 结果设置
		this.resultDataset = new ParameterSaveDataset();

		ParameterCombine resultData = new ParameterCombine();
		resultData.setDescribe(CoreProperties.getString("String_GroupBox_ResultData"));
		resultData.addParameters(this.resultDataset);

		this.parameters.setParameters(sourceData, parameterCombineSet, resultData);
		this.parameters.addInputParameters(INPUT_DATA, DatasetTypes.GRID, sourceData);
		this.parameters.addOutputParameters(OUTPUT_DATA,
				ProcessOutputResultProperties.getString("String_ShortestPathLineResult"),
				DatasetTypes.LINE, resultData);

	}


	private void initParametersState() {
		resultDataset.setDefaultDatasetName("result_DEMPastLine");
		DatasetGrid datasetGrid = DatasetUtilities.getDefaultDatasetGrid();
		if (datasetGrid != null) {
			sourceDatasource.setSelectedItem(datasetGrid.getDatasource());
			sourceDataset.setSelectedItem(datasetGrid);
			resultDataset.setResultDatasource(datasetGrid.getDatasource());
		}
		parameterPathLineSmoothDegree.setEnabled(false);
	}

	private void initParameterConstrint() {
		EqualDatasourceConstraint equalDatasourceConstraint = new EqualDatasourceConstraint();
		equalDatasourceConstraint.constrained(this.sourceDatasource, ParameterDatasourceConstrained.DATASOURCE_FIELD_NAME);
		equalDatasourceConstraint.constrained(this.sourceDataset, ParameterSingleDataset.DATASOURCE_FIELD_NAME);

		DatasourceConstraint.getInstance().constrained(this.resultDataset, ParameterSaveDataset.DATASOURCE_FIELD_NAME);

		parameterPathLineSmoothMethod.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (parameterPathLineSmoothMethod.getSelectedData().equals(SmoothMethod.NONE)) {
					parameterPathLineSmoothDegree.setEnabled(false);
				} else {
					parameterPathLineSmoothDegree.setEnabled(true);
				}
			}
		});
	}

	@Override
	public boolean execute() {
		boolean isSuccessful = false;
		PathLineResult pathLineResult;
		DatasetGrid datasetGrid;
		Recordset resultRecordset = null;
		String resultDatasetName = null;
		try {
			if (this.getParameters().getInputs().getData(INPUT_DATA) != null
					&& this.getParameters().getInputs().getData(INPUT_DATA).getValue() instanceof DatasetGrid) {
				datasetGrid = (DatasetGrid) this.getParameters().getInputs().getData(INPUT_DATA).getValue();
			} else {
				datasetGrid = (DatasetGrid) sourceDataset.getSelectedItem();
			}
			// 原点以及目标点坐标值参数
			Point2D sourcePoint = new Point2D();
			Point2D targetPoint = new Point2D();
			sourcePoint.setX(Double.valueOf(parameterSourcePointX.getSelectedItem()));
			sourcePoint.setY(Double.valueOf(parameterSourcePointY.getSelectedItem()));
			targetPoint.setX(Double.valueOf(parameterTargetPointX.getSelectedItem()));
			targetPoint.setY(Double.valueOf(parameterTargetPointY.getSelectedItem()));

			// 其他参数
			DistanceAnalystParameter distanceAnalystParameter = new DistanceAnalystParameter();
			distanceAnalystParameter.setSurfaceGrid(datasetGrid);
			distanceAnalystParameter.setPathLineSmoothMethod((SmoothMethod) parameterPathLineSmoothMethod.getSelectedData());
			distanceAnalystParameter.setPathLineSmoothDegree(Integer.valueOf(parameterPathLineSmoothDegree.getSelectedItem()));
			distanceAnalystParameter.setMaxUpslopeDegree(Double.valueOf(parameterMaxUpslopeDegree.getSelectedItem()));
			distanceAnalystParameter.setMaxUpslopeDegree(Double.valueOf(parameterMaxDownslopeDegree.getSelectedItem()));

			// run
			DistanceAnalyst.addSteppedListener(this.steppedListener);
			pathLineResult = DistanceAnalyst.surfacePathLine(sourcePoint, targetPoint, distanceAnalystParameter);

			if (pathLineResult != null) {
				// 创建新的线数据集
				resultDatasetName = this.resultDataset.getResultDatasource().getDatasets().getAvailableDatasetName(this.resultDataset.getDatasetName());
				DatasetVectorInfo datasetVectorInfo = new DatasetVectorInfo();
				datasetVectorInfo.setName(resultDatasetName);
				datasetVectorInfo.setType(DatasetType.LINE);
				DatasetVector resultDataset = this.resultDataset.getResultDatasource().getDatasets().create(datasetVectorInfo);
				resultDataset.setPrjCoordSys(datasetGrid.getPrjCoordSys());
				// 给新建的数据集中添加分析出的结果
				resultRecordset = resultDataset.getRecordset(false, CursorType.DYNAMIC);
				resultRecordset.addNew(pathLineResult.getPathLine());
				resultRecordset.update();

				this.getParameters().getOutputs().getData(OUTPUT_DATA).setValue(resultDataset);
				isSuccessful = resultDataset != null;
			} else {
				isSuccessful = false;
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
			// 当结果数据集不为空，并且tree中存在结果数据集时，进行删除操作
			if (!isSuccessful && !StringUtilities.isNullOrEmpty(resultDatasetName) &&
					!this.resultDataset.getResultDatasource().getDatasets().isAvailableDatasetName(resultDatasetName)) {
				this.resultDataset.getResultDatasource().getDatasets().delete(resultDatasetName);
			}
			if (resultRecordset != null) {
				resultRecordset.close();
				resultRecordset.dispose();
			}
			DistanceAnalyst.removeSteppedListener(this.steppedListener);
		}
		return isSuccessful;
	}

	/**
	 * 键入目标点
	 */
	private void inputSourcePoint() {
		String clipBoard = getSysClipboardText();

		if (clipBoard.contains("X:") && clipBoard.contains("Y:")) {
			String sourceX = clipBoard.substring(clipBoard.indexOf("X:"), clipBoard.indexOf("Y:"));
			String sourceY;
			if (clipBoard.contains(ControlsProperties.getString("String_Label_LongitudeValue"))) {
				sourceY = clipBoard.substring(clipBoard.indexOf("Y:"), clipBoard.indexOf(ControlsProperties.getString("String_Label_LongitudeValue")));
			} else {
				sourceY = clipBoard.substring(clipBoard.indexOf("Y:"));
			}

			sourceX = (sourceX.replace("X:", "")).replace(",", "");
			// 为了和.net互通-yuanR2017.9.18
			sourceY = ((sourceY.replace("Y:", "")).replace(",", "")).replace("   ", "");

			if (StringUtilities.isNumber(sourceX) && StringUtilities.isNumber(sourceY)) {
				this.parameterSourcePointX.setSelectedItem(sourceX);
				this.parameterSourcePointY.setSelectedItem(sourceY);
			}
		} else {
			Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_SourcePointGetFailed"));
		}
	}

	/**
	 * 调用windows的剪贴板
	 * 获得系统剪贴板内
	 *
	 * @return
	 */
	public static String getSysClipboardText() {
		Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable clipTf = sysClip.getContents(null);
		if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				return (String) clipTf.getTransferData(DataFlavor.stringFlavor);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 键入目标点
	 */
	private void inputTargetPoint() {
		String clipBoard = getSysClipboardText();

		if (clipBoard.contains("X:") && clipBoard.contains("Y:")) {

			String targetX = clipBoard.substring(clipBoard.indexOf("X:"), clipBoard.indexOf("Y:"));
			String targetY;
			if (clipBoard.contains(ControlsProperties.getString("String_Label_LongitudeValue"))) {
				targetY = clipBoard.substring(clipBoard.indexOf("Y:"), clipBoard.indexOf(ControlsProperties.getString("String_Label_LongitudeValue")));
			} else {
				targetY = clipBoard.substring(clipBoard.indexOf("Y:"));
			}

			targetX = (targetX.replace("X:", "")).replace(",", "");
			// 为了和.net互通-yuanR2017.9.18
			targetY = (targetY.replace("Y:", "")).replace(",", "").replace("   ", "");
			if (StringUtilities.isNumber(targetX) && StringUtilities.isNumber(targetY)) {
				this.parameterTargetPointX.setSelectedItem(targetX);
				this.parameterTargetPointY.setSelectedItem(targetY);
			}
		} else {
			Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_TargetPointGetFailed"));
		}
	}


	@Override
	public IParameters getParameters() {
		return parameters;
	}

	@Override
	public String getKey() {
		return MetaKeys.SURFACE_PATH_LINE;
	}
}
