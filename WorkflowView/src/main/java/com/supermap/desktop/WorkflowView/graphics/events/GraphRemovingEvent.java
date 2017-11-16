package com.supermap.desktop.WorkflowView.graphics.events;

import com.supermap.desktop.WorkflowView.graphics.GraphCanvas;
import com.supermap.desktop.WorkflowView.graphics.graphs.IGraph;
import com.supermap.desktop.event.CancellationEvent;

/**
 * Created by highsad on 2017/5/27.
 */
public class GraphRemovingEvent extends CancellationEvent {
	private GraphCanvas canvas;
	private IGraph graph;

	/**
	 * Constructs a prototypical Event.
	 *
	 * @param canvas The object on which the Event initially occurred.
	 * @throws IllegalArgumentException if source is null.
	 */
	public GraphRemovingEvent(GraphCanvas canvas, IGraph graph) {
		super(canvas, false);
		this.canvas = canvas;
		this.graph = graph;
	}

	public GraphCanvas getCanvas() {
		return canvas;
	}

	public IGraph getGraph() {
		return graph;
	}
}
