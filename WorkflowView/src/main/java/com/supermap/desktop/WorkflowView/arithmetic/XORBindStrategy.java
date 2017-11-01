package com.supermap.desktop.WorkflowView.arithmetic;

import com.supermap.desktop.WorkflowView.graphics.graphs.OutputGraph;
import com.supermap.desktop.WorkflowView.graphics.graphs.ProcessGraph;
import com.supermap.desktop.process.core.DataMatch;
import com.supermap.desktop.process.core.Workflow;

/**
 * Created by xie on 2017/10/17.
 * 一进入一出
 */
public class XORBindStrategy extends AbstractArithmeticStrategy implements IArithmeticStrategy {
	private OutputGraph startGraph;
	private ProcessGraph endGraph;

	public XORBindStrategy(Workflow workflow) {
		super(workflow);
	}

	public OutputGraph getStartGraph() {
		return startGraph;
	}

	public void setStartGraph(OutputGraph startGraph) {
		this.startGraph = startGraph;
	}

	public ProcessGraph getEndGraph() {
		return endGraph;
	}

	public void setEndGraph(ProcessGraph endGraph) {
		this.endGraph = endGraph;
	}

	@Override
	public void buildStrategy(String itemName) {
		DataMatch relation = new DataMatch(startGraph.getProcessGraph().getProcess(), endGraph.getProcess(), startGraph.getName(), itemName);
		this.workflow.addRelation(relation);
	}
}
