package com.supermap.desktop.WorkflowView.graphics.graphs;

import com.supermap.desktop.WorkflowView.arithmetic.ArithmeticStrategyType;
import com.supermap.desktop.WorkflowView.graphics.GraphCanvas;
import com.supermap.desktop.process.ProcessResources;

import javax.swing.*;
import java.awt.*;

/**
 * Created by xie on 2017/10/18.
 */
public class ArithmeticGraph extends CircleGraph {
	private ArithmeticStrategyType strategyType;

	public ArithmeticGraph(GraphCanvas canvas, ArithmeticStrategyType strategyType) {
		super(canvas, null);
		this.strategyType = strategyType;
	}


	@Override
	protected void onPaint(Graphics g) {
		super.onPaint(g);
		String iconPath = "/processresources/task/image_done.png";
//				strategyType.toString();
		g.drawImage(((ImageIcon) ProcessResources.getIcon(iconPath)).getImage(), getLocation().x + 3, getLocation().y + 3, 18, 18, null);
	}
}
