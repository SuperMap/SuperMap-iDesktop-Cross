package com.supermap.desktop.process.parameter.interfaces.datas;

import com.supermap.desktop.process.core.IProcess;
import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.interfaces.datas.types.Type;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by highsad on 2017/4/5.
 */
public class OutputData implements IDataDescription, IValueProvider {
	private IProcess process;
	private Object value;
	private String name;
	private String text;
	private String tips;
	private Type dataType;
	// 仿照InputData类增加parameters参数，用于参数值是否异常的检查，参见：AbstractProcess类-yuanR2017.9.12
	private ArrayList<IParameter> parameters = new ArrayList<>();

	public OutputData(IProcess process, String name, Type dataType) {
		this(process, name, name, dataType);
	}

	public OutputData(IProcess process, String name, String text, Type dataType) {
		this(process, name, text, null, dataType);
	}

	public OutputData(IProcess process, String name, String text, String tips, Type dataType) {
		this.process = process;
		this.name = name;
		this.text = text;
		this.tips = tips;
		this.dataType = dataType;
	}

	public IProcess getProcess() {
		return process;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public Object getValue() {
		return this.value;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getText() {
		return this.text;
	}

	@Override
	public String getTips() {
		return this.tips;
	}

	@Override
	public Type getType() {
		return this.dataType;
	}

	public String toString() {
		if (tips == null) {
			tips = "";
		}
		return name + "," + tips + "," + dataType;
	}

	public void addParameters(IParameter... parameters) {
		Collections.addAll(this.parameters, parameters);
	}

	public ArrayList<IParameter> getParameters() {
		return parameters;
	}
}
