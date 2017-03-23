package com.supermap.desktop.process.graphics.graphs;

import com.supermap.desktop.process.core.IProcess;
import com.supermap.desktop.process.graphics.GraphCanvas;

/**
 * Created by highsad on 2017/1/24.
 */
public class ProcessGraph extends RectangleGraph {

	private IProcess process;

	public ProcessGraph(GraphCanvas canvas, IProcess process) {
		super(canvas);
		this.process = process;
	}

	public IProcess getProcess() {
		return process;
	}

	public String getTitle() {
		return this.process == null ? "未知" : this.process.getTitle();
	}
}
