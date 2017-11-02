package com.supermap.desktop.WorkflowView.arithmetic;

import com.supermap.desktop.process.core.IProcess;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by xie on 2017/10/17.
 */
public  class ArithmeticHandler implements IArithmetic {
	//序列化时用于存储from的节点集合
	private CopyOnWriteArrayList<IProcess> fromProcesses = new CopyOnWriteArrayList<>();
	//序列化时用于存储to的节点集合
	private CopyOnWriteArrayList<IProcess> toProcesses = new CopyOnWriteArrayList<>();
	private IArithmeticStrategy bindStrategy;

	public ArithmeticHandler() {

	}

	public CopyOnWriteArrayList<IProcess> getFromProcesses() {
		return fromProcesses;
	}

	public ArithmeticHandler setFromProcesses(CopyOnWriteArrayList<IProcess> fromProcesses) {
		this.fromProcesses = fromProcesses;
		return this;
	}

	public CopyOnWriteArrayList<IProcess> getToProcesses() {
		return toProcesses;
	}

	public ArithmeticHandler setToProcesses(CopyOnWriteArrayList<IProcess> toProcesses) {
		this.toProcesses = toProcesses;
		return this;
	}

	@Override
	public void bind() {

	}
}
