package com.supermap.desktop.WorkflowView.graphics.graphs;

import com.supermap.desktop.WorkflowView.graphics.GraphCanvas;
import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.utilities.DoubleUtilities;
import com.supermap.desktop.utilities.FontUtilities;
import sun.swing.SwingUtilities2;

import java.awt.*;

/**
 * Created by xie on 2017/11/29.
 * 单独的输入节点图
 * 为工作流中计算值/while循环等功能提供输入
 */
public class InputGraph extends EllipseGraph {
	//实时修改显示文本（如外部修改为了radius表示半径等可以一眼明白输入值的含义）
	private String text;

	public InputGraph(GraphCanvas canvas) {
		super(canvas);
	}

	public InputGraph(GraphCanvas canvas, OutputData outputData) {
		super(canvas);
		setDefaultColor(new Color(103, 166, 223));
	}


	@Override
	protected void onPaint(Graphics g) {
		super.onPaint(g);
		Font font = new Font(FontUtilities.getMainFrameFontName(), Font.PLAIN, 14);
		g.setFont(font);
		g.setColor(Color.black);

		int fontHeight = getCanvas().getFontMetrics(font).getHeight();
		int fontWidth = SwingUtilities2.stringWidth(getCanvas(), getCanvas().getFontMetrics(font), getText());
		int fontDescent = getCanvas().getFontMetrics(font).getDescent();

		Point location = getLocation();
		double width = getWidth();
		double height = getHeight();
		g.drawString(getText(), DoubleUtilities.intValue(location.getX() + (width - fontWidth) / 2), DoubleUtilities.intValue(location.getY() + height / 2 + fontHeight / 2 - fontDescent));
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
