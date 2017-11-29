package com.supermap.desktop.process.core;

import com.supermap.desktop.process.parameter.interfaces.ISelectionParameter;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by xie on 2017/11/1.
 */
public interface CirculationIterator<T> extends Iterator {
	/**
	 * 提供一个reset方法来重新设定数据
	 */
	void reset();

	/**
	 * 获取容器
	 * @return
	 */
	ArrayList<T> getInfoList();

	/**
	 * 设置容器
	 * @param infoList
	 */
	void setInfoList(ArrayList<T> infoList);

	/**
	 * 获取循环类型
	 * @return
	 */
	CirculationType getCirculationType();

	/**
	 * 设置循环类型
	 * @param circulationType
	 */
	void setCirculationType(CirculationType circulationType);

	void setBindProcess(IProcess process);

	IProcess getBindProcess();

	void setBindParameterDescription(String parameterDescription);

	String getBindParameterDescription();

	boolean isRunning();

	void setRunning(boolean b);
}
