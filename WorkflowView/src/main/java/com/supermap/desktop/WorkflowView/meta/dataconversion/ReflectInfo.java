package com.supermap.desktop.WorkflowView.meta.dataconversion;

import com.supermap.desktop.process.parameter.interfaces.IParameter;

import java.util.HashMap;

/**
 * Created by xie on 2017/3/31.
 * 工作流导入参数包装类，以名称和参数本身进行构建
 */
public class ReflectInfo {
	public String methodName;
	public IParameter parameter;
	public HashMap<String, Object> mixReflectInfo;
}
