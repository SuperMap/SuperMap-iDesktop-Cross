package com.supermap.desktop.WorkflowView.graphics.interaction.canvas;

import com.supermap.desktop.Application;
import com.supermap.desktop.WorkflowView.WorkflowCanvas;
import com.supermap.desktop.WorkflowView.arithmetic.ArithmeticStrategyType;
import com.supermap.desktop.WorkflowView.graphics.CanvasCursor;
import com.supermap.desktop.WorkflowView.graphics.connection.LineGraph;
import com.supermap.desktop.WorkflowView.graphics.graphs.ArithmeticGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by xie on 2017/10/19.
 */
public class GraphArithmeticAction extends CanvasActionAdapter {
	private boolean isCreateArithmetic = false;
	private WorkflowCanvas workflowCanvas;
	private ArithmeticGraph arithmeticGraph;
	private CopyOnWriteArrayList<LineGraph> fromConnectionGraphs = new CopyOnWriteArrayList<>();
	private CopyOnWriteArrayList<LineGraph> toConnectionGraphs = new CopyOnWriteArrayList<>();

	public GraphArithmeticAction(WorkflowCanvas workflowCanvas) {
		this.workflowCanvas = workflowCanvas;
	}

	public void addArithmeticGraph(ArithmeticStrategyType type) {
		CanvasCursor.setArithmeticCursor(this.workflowCanvas);
		createArithmeticGraph(type);
	}

	private void createArithmeticGraph(ArithmeticStrategyType type) {
		switch (type) {
			case OR:
				this.fromConnectionGraphs.add(new LineGraph(this.workflowCanvas));
				this.toConnectionGraphs.add(new LineGraph(this.workflowCanvas));
				break;
			case AND:

				break;
			default:
				break;
		}
		this.arithmeticGraph = new ArithmeticGraph(this.workflowCanvas, type);
		this.isCreateArithmetic = true;

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e) && null != this.arithmeticGraph) {
			Point screenLocation = new Point(e.getX() - this.arithmeticGraph.getWidth() / 2, e.getY() - this.arithmeticGraph.getHeight() / 2);
			Point canvasLocation = this.workflowCanvas.getCoordinateTransform().inverse(screenLocation);
			this.arithmeticGraph.setLocation(canvasLocation);
			this.workflowCanvas.addGraph(this.arithmeticGraph);
			this.isCreateArithmetic = false;
		}
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		try {
			if (SwingUtilities.isRightMouseButton(e)) {
				clean();
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			clean();
		}
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled() && isCreateArithmetic;
	}

	@Override
	public void clean() {
		this.arithmeticGraph = null;
		this.isCreateArithmetic = false;
	}
}
