package com.supermap.desktop.process.core;

import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;

/**
 * Created by xie on 2017/10/26.
 */
public class OutPutDataMatch implements IRelation<OutputData> {
	private OutputData from;
	private OutputData to;

	public OutPutDataMatch(OutputData from, OutputData to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public OutputData getFrom() {
		return from;
	}

	@Override
	public OutputData getTo() {
		return to;
	}

	@Override
	public void clear() {
		this.from = null;
		this.to = null;
	}
}
