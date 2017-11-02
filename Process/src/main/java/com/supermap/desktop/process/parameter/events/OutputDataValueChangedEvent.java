package com.supermap.desktop.process.parameter.events;

/**
 * Created by xie on 2017/10/27.
 */
public class OutputDataValueChangedEvent {
	private Object newValue;

	public OutputDataValueChangedEvent(Object newValue) {
		this.newValue = newValue;
	}

	public Object getNewValue() {
		return newValue;
	}
}
