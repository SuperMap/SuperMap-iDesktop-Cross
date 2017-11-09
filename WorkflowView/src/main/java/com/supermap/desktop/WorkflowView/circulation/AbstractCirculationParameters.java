package com.supermap.desktop.WorkflowView.circulation;

import com.supermap.desktop.process.core.IProcess;
import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.process.parameter.ipls.DefaultParameters;

import java.util.ArrayList;

/**
 * Created by xie on 2017/10/27.
 */
public class AbstractCirculationParameters<T> extends DefaultParameters implements CirculationIterator {
	protected ArrayList<T> infoList = new ArrayList<>();
	protected int count = 0;
	protected OutputData outputData;

	public AbstractCirculationParameters(IProcess process) {
		super(process);
	}

	public AbstractCirculationParameters() {
		this(null);
	}

	/**
	 * //提供给子类覆盖,用于数据的初始化
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
}
