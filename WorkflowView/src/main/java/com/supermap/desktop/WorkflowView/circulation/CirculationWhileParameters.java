package com.supermap.desktop.WorkflowView.circulation;

import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.core.AbstractCirculationParameters;
import com.supermap.desktop.process.core.CirculationIterator;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.process.parameter.ipls.ParameterComboBox;
import com.supermap.desktop.process.parameter.ipls.ParameterFile;
import com.supermap.desktop.process.parameters.ParameterPanels.Circulation.ParameterForObjectCirculation;
import com.supermap.desktop.utilities.StringUtilities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by xie on 2017/11/28.
 * while 循环没有与之对应的数据节点，条件自己定义，
 * 用ScriptEngineManager转义js(javascript)字符串做逻辑判断
 * 当所有条件都为true/false时执行
 */
public class CirculationWhileParameters extends AbstractCirculationParameters implements CirculationIterator {
	private ParameterFile inputValue;
	private ParameterForObjectCirculation valueInfo;
	private ParameterComboBox whileValue;

	public CirculationWhileParameters(OutputData outputData) {
		this.outputData = outputData;
		initComponents();
		registEvents();
	}

	private void initComponents() {
		this.inputValue = new ParameterFile(ProcessProperties.getString("String_InputValue"));
		this.valueInfo = new ParameterForObjectCirculation();
		this.valueInfo.setShowAddButton(false);
		this.whileValue = new ParameterComboBox(ProcessProperties.getString("String_CirculationValue"));
		ParameterDataNode selectedNode = new ParameterDataNode("true", true);
		this.whileValue.setItems(selectedNode,
				new ParameterDataNode("false", false));
		this.whileValue.setSelectedItem(selectedNode);
		this.outputData.setValue(selectedNode.getData());
		this.addParameters(inputValue, valueInfo, whileValue);
	}

	private void registEvents() {
		this.inputValue.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(inputValue.FILE_COMMITTED)
						&& !StringUtilities.isNullOrEmpty(evt.getNewValue().toString())) {
					valueInfo.addRow(evt.getNewValue());
				}
			}
		});
		this.whileValue.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue() instanceof ParameterDataNode) {
					outputData.setValue(((ParameterDataNode) evt.getNewValue()).getData());
				}
			}
		});
	}

	@Override
	public void reset() {
		//while循环
		if (null != valueInfo.getSelectedItem() && valueInfo.getSelectedItem().size() > 0) {
			infoList.clear();
			infoList.addAll(valueInfo.getSelectedItem());
		}
	}

}
