package com.supermap.desktop.process.graphics.storage;

import com.supermap.desktop.process.graphics.graphs.IGraph;

import java.awt.*;

/**
 * Created by highsad on 2017/3/2.
 */
public interface IGraphStorage {
	int getCount();

	IGraph[] getGraphs();

	IGraph getGraph(int index);

	Rectangle getBounds(IGraph graph);

	boolean contains(IGraph graph);

	void add(IGraph graph);

	void add(IGraph graph, Rectangle bounds);

	void remove(IGraph graph);

	/**
	 * 获取指定点最上层的 IGraph
	 *
	 * @param point
	 * @return
	 */
	IGraph findGraph(Point point);

	/**
	 * 获取指定点的所有 Graph
	 *
	 * @param point
	 * @return
	 */
	IGraph[] findGraphs(Point point);

	IGraph[] findGraphs(Rectangle rect);

	void clear();
}
