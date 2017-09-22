package com.supermap.desktop.WorkflowView.graphics.graphs;

import com.supermap.desktop.WorkflowView.graphics.GraphCanvas;
import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.utilities.DoubleUtilities;
import com.supermap.desktop.utilities.FontUtilities;
import sun.swing.SwingUtilities2;

import java.awt.*;

/**
 * Created by highsad on 2017/2/28.
 */
public class OutputGraph extends RectangleGraph {

	private ProcessGraph processGraph;
	private OutputData processData;

	private OutputGraph() {
		super(null);
	}

	public OutputGraph(GraphCanvas canvas, ProcessGraph processGraph, OutputData processData) {
		super(canvas, 30, 30);
		this.processGraph = processGraph;
		this.processData = processData;
	}

	public ProcessGraph getProcessGraph() {
		return processGraph;
	}

	public OutputData getProcessData() {
		return processData;
	}

	@Override
	protected Color getBackColor() {
		return new Color(123, 136, 189);
	}

	@Override
	protected void onPaint(Graphics g) {
		super.onPaint(g);

		Font font = new Font(FontUtilities.getMainFrameFontName(), Font.PLAIN, 14);
		g.setFont(font);
		g.setColor(Color.WHITE);

		String text = this.processData.getText();
		int fontHeight = getCanvas().getFontMetrics(font).getHeight();
		int fontWidth = SwingUtilities2.stringWidth(getCanvas(), getCanvas().getFontMetrics(font), text);
		int fontDescent = getCanvas().getFontMetrics(font).getDescent();

		// 字符绘制时，坐标点指定的是基线的位置，而实际上我们希望指定的坐标点是整个字符块最下边的位置，因此使用 fontDescent 做个处理
		Point location = getLocation();
		double width = getWidth();
		double height = getHeight();
		g.drawString(text, DoubleUtilities.intValue(location.getX() + (width - fontWidth) / 2), DoubleUtilities.intValue(location.getY() + height / 2 + fontHeight / 2 - fontDescent));
	}

	public void setProcessGraph(ProcessGraph processGraph) {
		this.processGraph = processGraph;
	}

	public String getName() {
		return this.processData.getName();
	}

	public void setProcessData(OutputData processData) {
		this.processData = processData;
	}

}
