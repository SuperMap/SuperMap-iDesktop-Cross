package com.supermap.desktop.process.core;

import com.supermap.desktop.process.core.CirculationIterator;
import com.supermap.desktop.process.core.CirculationType;
import com.supermap.desktop.process.core.IProcess;
import com.supermap.desktop.process.parameter.interfaces.ISelectionParameter;
import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.process.parameter.ipls.DefaultParameters;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xie on 2017/10/27.
 */
public class AbstractCirculationParameters<T> extends DefaultParameters implements CirculationIterator {
	protected ArrayList<T> infoList = new ArrayList<>();
	protected int count = 0;
	protected OutputData outputData;
	protected CirculationType circulationType;
	protected IProcess process;
	protected String parameterDescription;

	public AbstractCirculationParameters(IProcess process) {
		super(process);
	}

	public AbstractCirculationParameters() {
		this(null);
	}

	/**
	 * 提供给子类覆盖,用于数据的初始化
	 */
	@Override
	public void reset() {

	}

	@Override
	public boolean hasNext() {
		return count < infoList.size();
	}

	@Override
	public Object next() {
		Object result = infoList.get(count);
		count++;
		return result;
	}

	/**
	 * 暂时未使用为后期清理数据提供接口
	 */
	@Override
	public void remove() {
		this.infoList.clear();
		this.infoList = null;
	}

	public OutputData getOutputData() {
		return outputData;
	}

	public void setOutputData(OutputData outputData) {
		this.outputData = outputData;
	}

	public ArrayList<T> getInfoList() {
		return infoList;
	}

	public void setInfoList(ArrayList infoList) {
		this.infoList = infoList;
	}

	@Override
	public CirculationType getCirculationType() {
		return this.circulationType;
	}

	@Override
	public void setCirculationType(CirculationType circulationType) {
		this.circulationType = circulationType;
	}

	@Override
	public void setBindProcess(IProcess process) {
		this.process = process;
	}

	@Override
	public IProcess getBindProcess() {
		return this.process;
	}

	@Override
	public void setBindParameterDescription(String parameterDescription) {
		this.parameterDescription = parameterDescription;
	}

	@Override
	public String getBindParameterDescription() {
		return this.parameterDescription;
	}

	/**
	 * 判断是否满足设置的通配符
	 * @param src
	 * @param des
	 * @return
	 */
	public boolean isMatching(String src,String des){

		String des1 = des.replace("*", "\\w*");
		des1 = des1.replace("?", "\\w{1}");
		Pattern p = Pattern.compile(des1);
		Matcher m = p.matcher(src);
		return m.matches();
	}
}
