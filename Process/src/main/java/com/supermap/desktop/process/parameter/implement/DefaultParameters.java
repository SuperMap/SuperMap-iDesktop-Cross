package com.supermap.desktop.process.parameter.implement;

import com.supermap.desktop.Application;
import com.supermap.desktop.process.FormProcess;
import com.supermap.desktop.process.graphics.GraphCanvas;
import com.supermap.desktop.process.graphics.graphs.IGraph;
import com.supermap.desktop.process.graphics.graphs.ProcessGraph;
import com.supermap.desktop.process.graphics.storage.IGraphStorage;
import com.supermap.desktop.process.meta.MetaProcess;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.ParameterPanels.EmptyParameterPanel;
import com.supermap.desktop.process.parameter.events.ValueProviderBindEvent;
import com.supermap.desktop.process.parameter.events.ValueProviderBindListener;
import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.interfaces.IParameterPanel;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.interfaces.datas.InputData;
import com.supermap.desktop.process.parameter.interfaces.datas.Inputs;
import com.supermap.desktop.process.parameter.interfaces.datas.Outputs;
import com.supermap.desktop.process.parameter.interfaces.datas.types.Type;
import com.supermap.desktop.process.util.ParameterUtil;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author XiaJT
 */
public class DefaultParameters implements IParameters {
	private MetaProcess metaProcess;
	protected ArrayList<IParameter> parameters = new ArrayList<>();
	protected JPanel panel;
	private ArrayList<ParameterClassBundleNode> packages = new ArrayList<>();
	protected EmptyParameterPanel parameterPanel = new EmptyParameterPanel();
	private InputParametersManager inputParametersManager = new InputParametersManager(this);
	private Inputs inputs = new Inputs(this);
	private Outputs outputs = new Outputs(this);

	public DefaultParameters(final MetaProcess metaProcess) {
		this.metaProcess = metaProcess;
		packages.add(new ParameterClassBundleNode("com.supermap.desktop.process.parameter.ParameterPanels", "SuperMap.Desktop.Process"));

		inputs.addValueProviderBindListener(new ValueProviderBindListener() {
			@Override
			public void valueBind(ValueProviderBindEvent event) {
				int type = event.getType();
				InputData inputData = event.getInputData();
				if (type == ValueProviderBindEvent.BIND) {
					inputParametersManager.bind(inputData);
				}
			}
		});
		inputParametersManager.addPropertyChangedListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// 修改来源节点时图上联动
				String propertyName = evt.getPropertyName();
				IGraph graph = (IGraph) ((ParameterDataNode) evt.getOldValue()).getData();
				IGraph newGraph = (IGraph) ((ParameterDataNode) evt.getNewValue()).getData();
				GraphCanvas canvas = ((FormProcess) Application.getActiveApplication().getActiveForm()).getCanvas();
				IGraphStorage graphStorage = canvas.getGraphStorage();
				IGraph processGraph = null;
				for (IGraph iGraph : graphStorage.getGraphs()) {
					if (iGraph instanceof ProcessGraph && ((ProcessGraph) iGraph).getProcess() == metaProcess) {
						processGraph = iGraph;
						break;
					}
				}
				// FIXME: 2017/5/16 所有节点都在，就差连接了！
			}
		});
	}

	@Override
	public void setParameters(IParameter... iParameters) {
		if (this.parameters != null && this.parameters.size() > 0) {
			for (IParameter parameter : parameters) {
				parameter.dispose();
				parameter.setParameters(null);
			}
		}
		if (panel != null) {
			panel.removeAll();
		}
		panel = null;
		Collections.addAll(parameters, iParameters);
		if (this.parameters != null && this.parameters.size() > 0) {
			for (IParameter parameter : parameters) {
				parameter.setParameters(this);
			}
		}
	}

	@Override
	public void addParameters(IParameter... iParameters) {
		Collections.addAll(parameters, iParameters);
		for (IParameter iParameter : iParameters) {
			iParameter.setParameters(this);
		}
		if (panel != null) {
			panel.removeAll();
		}
		panel = null;
	}


	@Override
	public ArrayList<IParameter> getParameters() {
		return parameters;
	}

	@Override
	public IParameter getParameter(String key) {
		for (IParameter parameter : parameters) {
			if (parameter.getType().equals(key)) {
				return parameter;
			}
		}
		return null;
	}

	@Override
	public IParameter getParameter(int index) {
		if (index >= 0 && index < parameters.size()) {
			return parameters.get(index);
		}
		return null;
	}

	@Override
	public int size() {
		return parameters.size();
	}

	@Override
	public IParameterPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			for (int i = 0; i < parameters.size(); i++) {
				panel.add((JPanel) parameters.get(i).getParameterPanel().getPanel(), new GridBagConstraintsHelper(0, i, 1, 1).setWeight(1, 0).setAnchor(GridBagConstraints.CENTER).setFill(GridBagConstraints.HORIZONTAL).setInsets(5, 10, 0, 10));
			}
			panel.add(new JPanel(), new GridBagConstraintsHelper(0, parameters.size(), 1, 1).setWeight(1, 1).setAnchor(GridBagConstraints.CENTER).setFill(GridBagConstraints.BOTH));
		}
		parameterPanel.setPanel(panel);
		return parameterPanel;
	}

	@Override
	public IParameterPanel createPanel(IParameter parameter) {
		Class clazz = ParameterUtil.getParameterPanel(parameter.getType(), packages);
		if (clazz != null) {
			try {
				Constructor constructor = clazz.getConstructor(IParameter.class);
				return (IParameterPanel) constructor.newInstance(parameter);
			} catch (Exception e) {
				Application.getActiveApplication().getOutput().output((parameter.getType() + " can't find panel"));
			}
		}
		return null;
	}

	@Override
	public void addInputParameters(String name, Type type, IParameter... parameters) {
		inputs.addData(name, type);
		inputs.getData(name).addParameters(parameters);
		inputParametersManager.add(name, parameters);

	}

	@Override
	public void addOutputParameters(String name, Type type, IParameter... parameters) {
		outputs.addData(name, type);
	}

	@Override
	public Inputs getInputs() {
		return inputs;
	}

	@Override
	public Outputs getOutputs() {
		return outputs;
	}
}
