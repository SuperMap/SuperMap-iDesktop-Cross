package com.supermap.desktop.WorkflowView.graphics.graphs;

import com.supermap.desktop.WorkflowView.graphics.GraphCanvas;
import com.supermap.desktop.WorkflowView.graphics.connection.IConnectable;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * Created by xie on 2017/10/18.
 */
public class CircleGraph extends AbstractGraph implements IConnectable {


	public CircleGraph(GraphCanvas canvas, Shape shape) {
		super(canvas, new Ellipse2D.Double(0, 0, 24, 24));
	}

	@Override
	public IGraph getConnector() {
		return this;
	}

	@Override
	public Ellipse2D getShape() {
		return (Ellipse2D) super.shape;
	}

	@Override
	protected void applyLocation(Point point) {
		getShape().setFrame(point.getX(), point.getY(), getShape().getWidth(), getShape().getHeight());
	}

	@Override
	protected void applySize(int width, int height) {
		getShape().setFrame(getShape().getX(), getShape().getY(), width, height);
	}

	@Override
	protected void onPaint(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		((Graphics2D) g).fill(this.shape);
	}
}
