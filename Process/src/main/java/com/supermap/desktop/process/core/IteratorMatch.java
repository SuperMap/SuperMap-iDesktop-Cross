package com.supermap.desktop.process.core;

import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.utilities.StringUtilities;
import org.apache.commons.lang.NullArgumentException;

/**
 * Created by xie on 2017/10/31.
 */
public class IteratorMatch implements IRelation<Object> {
	private OutputData from;
	private IProcess to;

	public IteratorMatch(OutputData from, IProcess to) {
		if (from == null || to == null ) {
			throw new NullArgumentException("parameter can not be null.");
		}
		this.from = from;
		this.to = to;
	}

	@Override
	public OutputData getFrom() {
		return from;
	}

	@Override
	public IProcess getTo() {
		return to;
	}

	@Override
	public void clear() {
		from = null;
		to = null;
	}
}
