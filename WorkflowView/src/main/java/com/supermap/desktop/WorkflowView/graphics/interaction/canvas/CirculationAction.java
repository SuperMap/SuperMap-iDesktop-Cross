package com.supermap.desktop.WorkflowView.graphics.interaction.canvas;

import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.WorkflowCanvas;
import com.supermap.desktop.WorkflowView.graphics.graphs.CirculationGraph;
import com.supermap.desktop.WorkflowView.graphics.graphs.CirculationOutputGraph;
import com.supermap.desktop.WorkflowView.graphics.graphs.IGraph;
import com.supermap.desktop.process.core.CirculationType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by xie on 2017/10/25.
 */
public class CirculationAction extends CanvasActionAdapter {
	private WorkflowCanvas workflowCanvas;


	public CirculationAction(WorkflowCanvas workflowCanvas) {
		this.workflowCanvas = workflowCanvas;
	}

	public void addCirculationGraph(CirculationType circulationType) {
		Point point = new Point(305, 336);
		Point canvasLocation = this.workflowCanvas.getCoordinateTransform().inverse(point);
		int locationX = canvasLocation.x + 180;
		int locationY = canvasLocation.y - 6;
		Point outputPoint = new Point(locationX, locationY);
		this.workflowCanvas.addCirculationGraph(circulationType, point, outputPoint);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		try {
			if (SwingUtilities.isRightMouseButton(e)) {
				clean();
			}
			if (e.getClickCount() == 2) {
				IGraph graph = this.workflowCanvas.findGraph(e.getPoint());
				if (graph instanceof CirculationGraph) {
					((CirculationGraph) graph).getCirculationDialog().setVisible(true);
				} else if (graph instanceof CirculationOutputGraph) {
					((CirculationOutputGraph) graph).getCirculationDialog().setVisible(true);
				}
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void clean() {

	}
}
