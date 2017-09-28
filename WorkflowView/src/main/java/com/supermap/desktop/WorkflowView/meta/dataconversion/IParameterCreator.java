package com.supermap.desktop.WorkflowView.meta.dataconversion;

import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.ipls.*;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by xie on 2017/3/31.
 */
public interface IParameterCreator<T> {
	/**
	 * Use t to create a parameter
	 *
	 * @param t
	 * @return
	 */
	CopyOnWriteArrayList<ReflectInfo> create(T t);

	/**
	 * Create result info parameter
	 *
	 * @return
	 */
	CopyOnWriteArrayList<ReflectInfo> createResult(T t, String type);

	CopyOnWriteArrayList<ReflectInfo> createSourceInfo(T t, String type);

	IParameter getParameterCombineResultSet();

	IParameter getParameterCombineParamSet();

	IParameter getParameterCombineSourceInfoSet();

	ParameterFile getParameterFile();

	ParameterFile getParameterFileFolder();

	ParameterTextField getParameterDataset();

	ParameterCharset getParameterCharset();

	ParameterButton getParameterButton();

	ParameterTextArea getParameterTextArea();

	ParameterFile getParameterFilePrjChoose();

	ParameterRadioButton getParameterSetRadioButton();

	ParameterRadioButton getParameterRadioButtonFolderOrFile();

	ParameterDatasource getParameterResultDatasource();
}
