package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.gridAnalyst.calculationTerrain;

import com.supermap.analyst.spatialanalyst.CalculationTerrain;
import com.supermap.data.Colors;
import com.supermap.data.DatasetGrid;
import com.supermap.data.DatasetImage;
import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.parameter.interfaces.datas.types.DatasetTypes;
import com.supermap.desktop.process.parameter.ipls.ParameterColor;
import com.supermap.desktop.process.parameter.ipls.ParameterColorsTable;
import com.supermap.desktop.process.parameter.ipls.ParameterCombine;
import com.supermap.desktop.properties.CommonProperties;

import java.awt.*;

/**
 * Created by yuanR on 2017/8/29 0029.
 * 生成正射三维影像
 * OrthoImage
 * 重构：
 * 修改颜色表选择方式，用ColorsComboBox控件构建ParameterColorsTable颜色表参数
 * 类同"无值颜色"参数的获取方式，因此暂时废弃DatasetGridBean类，因而舍弃属性设置的记录功能-yuanR2017.9.5
 */
public class MetaProcessCalculateOrthoImage extends MetaProcessCalTerrain {
	private final static String OUTPUT_DATASET = "CalculateHillShadeResult";
	private ParameterColor parameterColorNoColor;
	private ParameterColorsTable parameterColorsTable;

	public MetaProcessCalculateOrthoImage() {
		setTitle(ProcessProperties.getString("String_CalculateOrthoImage"));
	}

	@Override
	protected void initHook() {
		// 参数设置
		parameterColorNoColor = new ParameterColor(ProcessProperties.getString("String_Label_NoColor_Color"));
		parameterColorNoColor.setSelectedItem(Color.WHITE);
		parameterColorNoColor.setRequisite(true);
		parameterColorsTable = new ParameterColorsTable(ProcessProperties.getString("String_Label_ColorTable"));
		parameterColorsTable.setRequisite(true);
		parameterColorsTable.setSelectedItem(new Colors());

		ParameterCombine parameterCombineSet = new ParameterCombine();
		parameterCombineSet.setDescribe(CommonProperties.getString("String_GroupBox_ParamSetting"));
		parameterCombineSet.addParameters(parameterColorNoColor, parameterColorsTable);

		// 结果设置
		parameters.addParameters(parameterCombineSet, parameterCombineResultDataset);
		parameters.addOutputParameters(OUTPUT_DATASET, ProcessOutputResultProperties.getString("String_CalculateOrthoImageResult"), DatasetTypes.IMAGE, parameterCombineResultDataset);
	}

	@Override
	protected String getDefaultResultName() {
		return "result_calculateOrthoImage";
	}

	@Override
	public String getKey() {
		return MetaKeys.CALCULATE_ORTHOIMAGE;
	}

	@Override
	protected boolean doWork(DatasetGrid datasetGrid) {
		boolean isSuccessful = false;
		DatasetImage datasetImageResult = null;
		try {
			// 这个进度监听有问题，无法生效，先用fireRunning代替-yuanR存疑2017.8.30
//			CalculationTerrain.addSteppedListener(steppedListener);
			datasetImageResult = CalculationTerrain.calculateOrthoImage(datasetGrid,
					(Colors) parameterColorsTable.getSelectedItem(),
					(Color) parameterColorNoColor.getSelectedItem(),
					parameterSaveDataset.getResultDatasource(),
					parameterSaveDataset.getDatasetName());
			this.getParameters().getOutputs().getData(OUTPUT_DATASET).setValue(datasetImageResult);
			isSuccessful = datasetImageResult != null;

		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e.getMessage());
			e.printStackTrace();
		} finally {
//			CalculationTerrain.removeSteppedListener(steppedListener);
		}
		return isSuccessful;
	}
}
