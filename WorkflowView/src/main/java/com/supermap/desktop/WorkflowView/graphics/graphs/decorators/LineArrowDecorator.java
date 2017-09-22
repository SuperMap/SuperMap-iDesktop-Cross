package com.supermap.desktop.WorkflowView.graphics.graphs.decorators;

import com.supermap.desktop.WorkflowView.graphics.GraphCanvas;
import com.supermap.desktop.WorkflowView.graphics.GraphicsUtil;
import com.supermap.desktop.WorkflowView.graphics.connection.LineGraph;
import com.supermap.desktop.WorkflowView.graphics.graphs.IGraph;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * Created by highsad on 2017/5/2.
 */
public class LineArrowDecorator extends AbstractDecorator {
	private int crossWidth = 6;

	public LineArrowDecorator(GraphCanvas canvas) {
		super(canvas);
	}

	private LineGraph getDecoratedLine() {
		if (getGraph() instanceof LineGraph) {
			return (LineGraph) getGraph();
		} else {
			return null;
		}
	}

	@Override
	public boolean contains(Point point) {
		LineGraph line = getDecoratedLine();
		if (line != null && line.getPointCount() > 1) {
			Point endPoint = line.getPoint(line.getPointCount() - 1);
			Point[] arrowVertexes = GraphicsUtil.computeArrow(line.getPoint(line.getPointCount() - 2), endPoint);
			if (arrowVertexes.length != 0) {
				int length = 2;
				return GraphicsUtil.pointToLineLength(point, arrowVertexes[0], endPoint) <= length || GraphicsUtil.pointToLineLength(point, arrowVertexes[1], endPoint) <= length;
			}
		}
		return false;
	}

	@Override
	public Rectangle getBounds() {
		GeneralPath arrow = getArrowPath();

		if (arrow != null) {
			return arrow.getBounds();
		} else {
			return null;
		}
	}

	private GeneralPath getArrowPath() {
		GeneralPath arrowPath = null;
		LineGraph line = getDecoratedLine();
		if (line != null && line.getPointCount() > 1) {
			Point start = line.getPoint(line.getPointCount() - 2);
			Point end = line.getPoint(line.getPointCount() - 1);

			if (start == null || end == null || start.equals(end)) {
				return null;
			}

			Point[] arrowVertexes = GraphicsUtil.computeArrow(line.getPoint(line.getPointCount() - 2), line.getPoint(line.getPointCount() - 1));

			if (arrowVertexes != null && arrowVertexes.length != 0) {
				arrowPath = new GeneralPath();
				arrowPath.moveTo(arrowVertexes[0].getX(), arrowVertexes[0].getY());
				arrowPath.lineTo(line.getPoint(line.getPointCount() - 1).getX(), line.getPoint(line.getPointCount() - 1).getY());
				arrowPath.lineTo(arrowVertexes[1].getX(), arrowVertexes[1].getY());
			}
		}
		return arrowPath;
	}

	@Override
	public void decorate(IGraph graph) {
		if (graph instanceof LineGraph) {
			super.decorate(graph);
		}
	}

	@Override
	protected void onPaint(Graphics g) {
		GeneralPath arrow = getArrowPath();
		if (arrow != null) {
			Graphics2D graphics2D = (Graphics2D) g;
			Stroke stroke = new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10);
			graphics2D.setStroke(stroke);
			graphics2D.setColor(Color.GRAY);
			graphics2D.draw(arrow);
		}
	}
}
