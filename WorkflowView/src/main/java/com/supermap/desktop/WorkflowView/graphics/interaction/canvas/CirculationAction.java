package com.supermap.desktop.WorkflowView.graphics.interaction.canvas;

import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.WorkflowCanvas;
import com.supermap.desktop.WorkflowView.circulation.CirculationDialog;
import com.supermap.desktop.WorkflowView.circulation.CirculationType;
import com.supermap.desktop.WorkflowView.graphics.connection.ConnectionLineGraph;
import com.supermap.desktop.WorkflowView.graphics.graphs.CirculationGraph;
import com.supermap.desktop.WorkflowView.graphics.graphs.CirculationOutputGraph;
import com.supermap.desktop.WorkflowView.graphics.graphs.IGraph;
import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by xie on 2017/10/25.
 */
public class CirculationAction extends CanvasActionAdapter {
	private WorkflowCanvas workflowCanvas;
	private CirculationGraph circulationGraph;
	private CirculationOutputGraph outputGraph;
	private ConnectionLineGraph lineGraph;
	private CirculationDialog circulationDialog;
	private CirculationDialog CirculationOutputDialog;

	public CirculationAction(WorkflowCanvas workflowCanvas) {
		this.workflowCanvas = workflowCanvas;
	}

	public void addCirculationGraph(CirculationType circulationType) {
		if (null == circulationGraph) {
			this.circulationGraph = new CirculationGraph(workflowCanvas, circulationType);
			Point screenLocation = new Point(305, 336);
			Point canvasLocation = this.workflowCanvas.getCoordinateTransform().inverse(screenLocation);
			this.workflowCanvas.setCirculationGraph(this.circulationGraph);
			this.circulationGraph.setLocation(canvasLocation);
			this.workflowCanvas.addGraph(this.circulationGraph);
			int locationX = circulationGraph.getLocation().x + circulationGraph.getWidth() * 3 / 2;
			int locationY = circulationGraph.getLocation().y + circulationGraph.getHeight() / 2 - 26;

			Point point = new Point(locationX, locationY);
			OutputData outputData = circulationGraph.getOutputData();
			this.outputGraph = new CirculationOutputGraph(this.workflowCanvas, outputData);
			this.outputGraph.setLocation(point);
			this.workflowCanvas.getOutputMap().put(outputData, outputGraph);
			this.workflowCanvas.addGraph(this.outputGraph);
			// 添加 process 和 output 之间的连接线
			lineGraph = new ConnectionLineGraph(this.workflowCanvas, circulationGraph, outputGraph);
			this.workflowCanvas.getOutputLinesMap().put(outputData, lineGraph);
			this.workflowCanvas.addGraph(lineGraph);
			this.workflowCanvas.repaint();
			circulationDialog = new CirculationDialog(circulationType, false, outputData);
			this.workflowCanvas.setIterator(circulationDialog.iterator());
			CirculationOutputDialog = new CirculationDialog(circulationType, true, outputData);
		}
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
					circulationDialog.setVisible(true);
				} else if (graph instanceof CirculationOutputGraph) {
					CirculationOutputDialog.setVisible(true);
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
		this.circulationGraph = null;
		this.outputGraph = null;
		this.lineGraph = null;
	}
}
