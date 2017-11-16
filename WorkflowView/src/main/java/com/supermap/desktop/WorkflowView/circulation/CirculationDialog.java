package com.supermap.desktop.WorkflowView.circulation;

import com.supermap.desktop.controls.utilities.ComponentFactory;
import com.supermap.desktop.process.core.AbstractCirculationParameters;
import com.supermap.desktop.process.core.CirculationIterator;
import com.supermap.desktop.process.core.CirculationType;
import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.ui.controls.SmDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by xie on 2017/10/27.
 */
public class CirculationDialog extends SmDialog {
	private JButton buttonOK;
	private JButton buttonClose;
	private JPanel contentPanel;
	private AbstractCirculationParameters parameters;
	private boolean isOutput;
	private ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(buttonOK) && !isOutput) {
				parameters.reset();
			}
			CirculationDialog.this.dispose();
		}
	};

	public CirculationDialog(CirculationType type, boolean isOutput, OutputData outputData) {
		super();
		this.isOutput = isOutput;
		if (isOutput) {
			parameters = CirculationParametersFactory.getCirculationOutParameters(type, outputData);
		} else {
			parameters = CirculationParametersFactory.getCirculationParameters(type, outputData);
		}

		this.contentPanel = (JPanel) parameters.getPanel().getPanel();
		this.setTitle(type.getName());
		init(type);
	}

	private void init(CirculationType type) {
		this.buttonOK = ComponentFactory.createButtonOK();
		this.buttonClose = ComponentFactory.createButtonClose();
		this.setLayout(new GridBagLayout());
		this.add(contentPanel, new GridBagConstraintsHelper(0, 0, 2, 3).setWeight(1, 0).setAnchor(GridBagConstraints.NORTH).setFill(GridBagConstraints.BOTH).setInsets(5, 10, 0, 0));
		this.add(new JPanel(), new GridBagConstraintsHelper(0, 2, 2, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.BOTH).setWeight(0, 1));
		this.add(this.buttonOK, new GridBagConstraintsHelper(0, 4, 1, 1).setWeight(1, 0).setAnchor(GridBagConstraints.EAST).setFill(GridBagConstraints.NONE).setInsets(0, 10, 10, 0));
		this.add(this.buttonClose, new GridBagConstraintsHelper(1, 4, 1, 1).setWeight(0, 0).setAnchor(GridBagConstraints.EAST).setFill(GridBagConstraints.NONE).setInsets(0, 5, 10, 10));
		this.buttonOK.addActionListener(this.actionListener);
		this.buttonClose.addActionListener(this.actionListener);
		int size;
		if (parameters.getParameters().size() == 1 && (type != CirculationType.forObjectType || isOutput)) {
			size = 120;
		} else if (parameters.getParameters().size() == 1 && type == CirculationType.forObjectType && !isOutput) {
			size = 300;
		} else {
			size = parameters.getParameters().size() * 60;
		}
		this.setSize(new Dimension(420, size));
		this.setMinimumSize(new Dimension(420, size));
		this.setLocationRelativeTo(null);
	}

	public CirculationIterator iterator() {
		return parameters;
	}
}
