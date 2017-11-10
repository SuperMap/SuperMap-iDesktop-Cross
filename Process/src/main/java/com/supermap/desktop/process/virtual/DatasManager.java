package com.supermap.desktop.process.virtual;

import com.supermap.desktop.process.core.Workflow;

/**
 * 1. 推断整个工作流的结果数据，给每个节点的输出数据一个合理的名字
 * 2. 推断每个节点输入数据的字段
 * 3. 推断每个节点输出数据的类型，可能随输入的不同而改变
 *
 * Created by highsad on 2017/11/10.
 */
public class DatasManager {
	private Workflow workflow;

	public DatasManager(Workflow workflow) {
		this.workflow = workflow;
	}
}
