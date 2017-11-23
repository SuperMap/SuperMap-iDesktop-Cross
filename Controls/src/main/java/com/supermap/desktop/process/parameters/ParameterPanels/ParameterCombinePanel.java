package com.supermap.desktop.process.parameters.ParameterPanels;

import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.events.PanelPropertyChangedListener;
import com.supermap.desktop.process.parameter.events.ParameterCombineBuildPanelListener;
import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.interfaces.ParameterPanelDescribe;
import com.supermap.desktop.process.parameter.ipls.ParameterCombine;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

/**
 * @author XiaJT
 */
@ParameterPanelDescribe(parameterPanelType = ParameterType.COMBINE)
public class ParameterCombinePanel extends SwingPanel implements ParameterCombineBuildPanelListener {

	private ParameterCombine parameterCombine;

	public ParameterCombinePanel(IParameter parameterCombine) {
		super(parameterCombine);
		this.parameterCombine = ((ParameterCombine) parameterCombine);
		((ParameterCombine) parameterCombine).addParameterCombineBuildPanelListeners(this);
		buildPanel();
	}

	private void buildPanel() {
		int x = 0, y = 0;
		if (panel != null) {
			panel.removeAll();
		} else {
			panel = new JPanel();
		}
		panel.setLayout(new GridBagLayout());
		int defaultInset = 0;
		if (!StringUtilities.isNullOrEmpty(parameterCombine.getDescription())) {
			panel.setBorder(new TitledBorder(parameterCombine.getDescription()));
			defaultInset = 10;
		}

		ArrayList<IParameter> parameterList = parameterCombine.getParameterList();
		if (parameterList.size() <= 0) {
			return;
		}
		String combineType = parameterCombine.getCombineType();
		int weightIndex = parameterCombine.getWeightIndex();
		for (IParameter parameter : parameterList) {
			int weightX = combineType.equals(ParameterCombine.VERTICAL) ? 1 : (weightIndex == -1 || weightIndex == x ? 1 : 0);
			int weightY = combineType.equals(ParameterCombine.HORIZONTAL) ? 1 : (weightIndex == -1 || weightIndex == y ? 1 : 0);
			JPanel panel = (JPanel) parameter.getParameterPanel().getPanel();
			this.panel.add(panel, new GridBagConstraintsHelper(x, y, 1, 1).setWeight(weightX, weightY).setAnchor(GridBagConstraints.CENTER).setFill(GridBagConstraints.HORIZONTAL).setInsets(y > 0 ? 5 : defaultInset, x > 0 ? 5 : defaultInset, 0, 0));
			if (combineType.equals(ParameterCombine.VERTICAL)) {
				y++;
			} else {
				x++;
			}
		}
		ComponentUIUtilities.setName(panel, parameter.getDescription());
	}

	@Override
	public void rebuild() {
		buildPanel();
	}


	@Override
	protected void panelPropertyChanged(PropertyChangeEvent propertyChangeEvent) {
		if (propertyChangeEvent.getPropertyName().equals(PanelPropertyChangedListener.ENABLE)) {

			ArrayList<IParameter> parameterList = parameterCombine.getParameterList();
			for (IParameter iParameter : parameterList) {
				iParameter.setEnabled((Boolean) propertyChangeEvent.getNewValue());
			}
		}
	}

	@Override
	public Object getPanel() {
		if (parameterCombine.isRebuildEveryTime()) {
			rebuild();
		}
		return panel;
	}
}
