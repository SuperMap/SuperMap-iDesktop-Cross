package com.supermap.desktop.WorkflowView.graphics.graphs;

import com.supermap.desktop.WorkflowView.circulation.CirculationType;
import com.supermap.desktop.WorkflowView.graphics.GraphCanvas;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.utilities.DoubleUtilities;
import com.supermap.desktop.utilities.FontUtilities;
import sun.swing.SwingUtilities2;

import java.awt.*;

/**
 * Created by xie on 2017/10/25.
 */
public class CirculationGraph extends RectangleGraph {
	private CirculationType circulationType;
	private OutputData outputData;

	public CirculationGraph(GraphCanvas canvas, CirculationType circulationType) {
		super(canvas, 0, 0);
		this.circulationType = circulationType;
		this.outputData = new OutputData(circulationType.getName(), ProcessProperties.getString("String_CurrentValue"), circulationType.getType());
	}

	@Override
	protected Color getBackColor() {
		return new Color(223, 193, 44);
	}

	public String getTitle() {
		return circulationType.getName();
	}


	public OutputData getOutputData() {
		return outputData;
	}

	public void setOutputData(OutputData outputData) {
		this.outputData = outputData;
	}

	@Override
	public void onPaint(Graphics g) {
		super.onPaint(g);
		Font font = new Font(FontUtilities.getMainFrameFontName(), Font.PLAIN, 14);
		g.setFont(font);
		g.setColor(Color.black);

		String title = getTitle();
		int fontHeight = getCanvas().getFontMetrics(font).getHeight();
		int fontWidth = SwingUtilities2.stringWidth(getCanvas(), getCanvas().getFontMetrics(font), title);
		int fontDescent = getCanvas().getFontMetrics(font).getDescent();

		// 字符绘制时，坐标点指定的是基线的位置，而实际上我们希望指定的坐标点是整个字符块最下边的位置，因此使用 fontDescent 做个处理
		Point location = getLocation();
		double width = getWidth();
		double height = getHeight();
		g.drawString(title, DoubleUtilities.intValue(location.getX() + (width - fontWidth) / 2), DoubleUtilities.intValue(location.getY() + height / 2 + fontHeight / 2 - fontDescent));

	}
}
