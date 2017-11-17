package com.supermap.desktop.process.virtual;

import com.supermap.desktop.process.core.IProcess;
import com.supermap.desktop.process.types.Type;

import java.util.Vector;

/**
 * 1. 按类型分组？
 * 2. 按节点分组？
 * 3. 单节点选中？
 * 4. 先实现名字、类型、字段的推理吧
 * Created by highsad on 2017/11/10.
 */
public class VirtualData {
	private boolean isCreated = false;
	private Type type = Type.OBJECT;
	private IProcess source;
	private String name; // 输出参数的节点名
	private String resultDatasetName; // 输出结果的数据名
	private Vector<VirtualField> fields;

	public VirtualData(String name, Type type, IProcess source) {
		this.name = name;
		this.type = type;
		this.source = source;
		this.fields = new Vector<>();
	}

	public Type getType() {
		return type;
	}

	public IProcess getSource() {
		return source;
	}

	public String getName() {
		return name;
	}


}