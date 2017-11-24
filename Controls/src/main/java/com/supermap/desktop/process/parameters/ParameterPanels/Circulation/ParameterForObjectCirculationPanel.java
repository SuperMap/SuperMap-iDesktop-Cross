package com.supermap.desktop.process.parameters.ParameterPanels.Circulation;

import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.interfaces.IParameterPanel;
import com.supermap.desktop.process.parameter.interfaces.ParameterPanelDescribe;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by xie on 2017/11/7.
 */
@ParameterPanelDescribe(parameterPanelType = ParameterType.CIRCULATION_FOR_OBJECT)
public class ParameterForObjectCirculationPanel implements IParameterPanel {
	private PanelForObjectCirculation panelForObjectCirculation;
	private ParameterForObjectCirculation parameterForObjectCirculation;
	private boolean isSelectedChanged = false;

	public ParameterForObjectCirculationPanel(IParameter parameter) {
		this.parameterForObjectCirculation = (ParameterForObjectCirculation) parameter;
		init();
	}

	private void init() {
		this.panelForObjectCirculation = new PanelForObjectCirculation();
		this.parameterForObjectCirculation.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (!isSelectedChanged && evt.getPropertyName().equals(parameterForObjectCirculation.FILE_TYPE_CHANGED)) {
					isSelectedChanged = true;
					panelForObjectCirculation.setFileType((String) evt.getNewValue());
					isSelectedChanged = false;
				}
			}
		});
		this.panelForObjectCirculation.getExchangeTableModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				if (!isSelectedChanged) {
					isSelectedChanged = true;
					parameterForObjectCirculation.setSelectedItem(panelForObjectCirculation.getPathList());
					isSelectedChanged = false;
				}
			}
		});
	}

	@Override
	public Object getPanel() {
		return this.panelForObjectCirculation;
	}
}
