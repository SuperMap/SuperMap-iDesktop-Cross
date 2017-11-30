package com.supermap.desktop.WorkflowView.graphics.graphs;

import com.supermap.desktop.WorkflowView.graphics.GraphCanvas;
import com.supermap.desktop.WorkflowView.graphics.connection.IConnectable;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Created by highsad on 2017/1/19.
 */
public class EllipseGraph extends AbstractGraph implements IConnectable {

	private Color defaultColor = new Color(254, 244, 236);

	public EllipseGraph(GraphCanvas canvas) {
		super(canvas, new Ellipse2D.Double(0, 0, 140, 50));
	}

	@Override
	public Ellipse2D getShape() {
		return (Ellipse2D) super.getShape();
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
	public boolean contains(Point p) {
		return getShape().contains(p);
	}

	@Override
	protected void onPaint(Graphics g) {
		g.setColor(getDefaultColor());
		((Graphics2D) g).fill(this.shape);
	}

	@Override
	public IGraph getConnector() {
		return this;
	}

	public Color getDefaultColor() {
		return defaultColor;
	}

	public void setDefaultColor(Color defaultColor) {
		this.defaultColor = defaultColor;
	}
}
