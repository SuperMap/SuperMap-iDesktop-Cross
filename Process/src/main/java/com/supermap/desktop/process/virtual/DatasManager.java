package com.supermap.desktop.process.virtual;

import com.supermap.desktop.process.core.IProcess;
import com.supermap.desktop.process.core.Workflow;
import com.supermap.desktop.process.events.*;
import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.process.parameter.interfaces.datas.Outputs;

import java.util.Vector;

/**
 * 1. 推断整个工作流的结果数据，给每个节点的输出数据一个合理的名字
 * 2. 推断每个节点输入数据的字段
 * 3. 推断每个节点输出数据的类型，可能随输入的不同而改变
 *
 *  先尝试实现数据集的推断和管理
 * <p>
 * Created by highsad on 2017/11/10.
 */
public class DatasManager {
	private Workflow workflow;
	private Vector<VirtualData> datas;

	public DatasManager(Workflow workflow) {
		if (this.workflow == null) {
			throw new IllegalArgumentException("workflow");
		}

		this.workflow = workflow;
		this.datas = new Vector<>();

		this.workflow.addWorkflowChangeListener(new WorkflowChangeHandler());
		this.workflow.addRelationAddedListener(new RelationAddedHandler());
		this.workflow.addRelationRemovedListener(new RelationRemovedHandler());
	}

	private void initDatas() {
		Vector<IProcess> processes = this.workflow.getProcesses();

		if (processes != null && processes.size() != 0) {
			for (int i = 0; i < processes.size(); i++) {
				initDatas(processes.get(i));
			}
		}
	}

	private void initDatas(IProcess process) {
		Outputs outputs = process.getOutputs();
		OutputData[] outputDatas = outputs.getDatas();

		for (int i = 0; i < outputDatas.length; i++) {
			OutputData outputData = outputDatas[i];
			VirtualData vData = new VirtualData(outputData.getName(), outputData.getType(), outputData.getProcess());
			this.datas.add(vData);
		}
	}

	private class WorkflowChangeHandler implements WorkflowChangeListener {
		@Override
		public void workflowChange(WorkflowChangeEvent e) {

		}
	}


	private class RelationAddedHandler implements RelationAddedListener {
		@Override
		public void relationAdded(RelationAddedEvent e) {

		}
	}


	private class RelationRemovedHandler implements RelationRemovedListener {
		@Override
		public void relationRemoved(RelationRemovedEvent e) {

		}
	}

}
