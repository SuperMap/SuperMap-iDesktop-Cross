package com.supermap.desktop.process.parameter.interfaces;

import com.supermap.desktop.process.core.IProcess;
import com.supermap.desktop.process.core.Workflow;
import com.supermap.desktop.process.parameter.events.ParameterPropertyChangedListener;
import com.supermap.desktop.process.parameter.interfaces.datas.Inputs;
import com.supermap.desktop.process.parameter.interfaces.datas.Outputs;
import com.supermap.desktop.process.types.Type;

import java.util.ArrayList;

/**
 * Created by highsad on 2017/1/5.
 */
public interface IParameters {

	void bindWorkflow(Workflow workflow);

	void unbindWorkflow(Workflow workflow);

	void setParameters(IParameter... iParameters);

	void addParameters(IParameter... iParameters);

	void addEnvironmentParameters(IEnvironmentParameter... environmentParameter);

	ArrayList<IParameter> getParameters();

	IParameter getParameter(String key);

	IParameter getParameter(int index);

	int size();

	IParameterPanel getPanel();

	IParameterPanel createPanel(IParameter parameter);

	void addInputParameters(String name, Type type, IParameter... parameters);

	void addOutputParameters(String name, Type type, IParameter... parameters);

	void addInputParameters(String name, String text, Type type, IParameter... parameters);

	void addOutputParameters(String name, String text, Type type, IParameter... parameters);

	Inputs getInputs();

	Outputs getOutputs();

	void replace(ArrayList<IParameter> sources, IParameter... results);

	IProcess getProcess();

	boolean isReady();

	void addParameterPropertyChangedListener(ParameterPropertyChangedListener parameterPropertyChangedListener);

	void removeParameterPropertyChangedListener(ParameterPropertyChangedListener parameterPropertyChangedListener);


	void dispose();
}
