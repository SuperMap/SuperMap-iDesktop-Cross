package com.supermap.desktop.process.parameters.ParameterPanels.Circulation;

import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.interfaces.IParameterPanel;
import com.supermap.desktop.process.parameter.interfaces.ParameterPanelDescribe;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 * Created by xie on 2017/11/7.
 */
@ParameterPanelDescribe(parameterPanelType = ParameterType.CIRCULATION_FOR_OBJECT)
public class ParameterForObjectCirculationPanel implements IParameterPanel {
	private PanelForObjectCirculation panelForObjectCirculation;
	private ParameterForObjectCirculation parameterForObjectCirculation;

	public ParameterForObjectCirculationPanel(IParameter parameter) {
		this.parameterForObjectCirculation = (ParameterForObjectCirculation) parameter;
		init();
	}

	private void init() {
		this.panelForObjectCirculation = new PanelForObjectCirculation();
		this.panelForObjectCirculation.getExchangeTableModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				parameterForObjectCirculation.setSelectedItem(panelForObjectCirculation.getPathList());
			}
		});
	}

	@Override
	public Object getPanel() {
		return this.panelForObjectCirculation;
	}
}
