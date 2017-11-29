package com.supermap.desktop.layoutview.propertycontrols;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.Interface.IFormLayout;

import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.event.ActiveFormChangedEvent;
import com.supermap.desktop.event.ActiveFormChangedListener;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.ui.controls.button.SmButton;
import com.supermap.layout.MapLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/28
 * Time: 10:35
 * Description:Layout property container
 */
public class LayoutPropertyContainer extends JPanel {

	private static final long serialVersionUID = 1L;
	private JCheckBox checkBoxAutoApply;
	private SmButton buttonApply;
	private ArrayList<AbstractPropertyControl> propertyControls;
	private JPanel panelContainer;
	private transient IFormLayout formLayout = null;
	private transient ChangedListener propertyChangedListener=new ChangedListener() {
		@Override
		public void changed(ChangedEvent e) {
			buttonApply.setEnabled(!checkBoxAutoApply.isSelected() && e.getCurrentState() == ChangedEvent.CHANGED);
		}
	};


	public LayoutPropertyContainer() {
		initializeComponents();
		initializeResources();

		this.checkBoxAutoApply.setSelected(true);
		this.checkBoxAutoApply.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				checkBoxAutoApply();
			}
		});
		this.buttonApply.setEnabled(false);
		this.buttonApply.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				buttonApplyClicked();
			}
		});
		Application.getActiveApplication().getMainFrame().getFormManager().addActiveFormChangedListener(new ActiveFormChangedListener() {

			@Override
			public void activeFormChanged(ActiveFormChangedEvent e) {
				if (e.getNewActiveForm() instanceof IFormLayout) {
					setFormLayout((IFormLayout) e.getNewActiveForm());
				} else {
					setFormLayout(null);
				}
			}
		});
	}

	public void setFormLayout(IFormLayout formLayout) {
		if (propertyControls == null) {
			propertyControls = new ArrayList<>();
			propertyControls.add(new LayoutBasePropertyControl());
			propertyControls.add(new LayoutGridPropertyControl());
			propertyControls.add(new LayoutStylePropertyControl());
			for (AbstractPropertyControl abstractPropertyControl : propertyControls) {
				if (!abstractPropertyControl.getPropertyTitle().equals("")) {
					abstractPropertyControl.setBorder(BorderFactory.createTitledBorder(abstractPropertyControl.getPropertyTitle()));
				}
				abstractPropertyControl.addChangedListener(this.propertyChangedListener);
				this.panelContainer.add(abstractPropertyControl);
			}
		}
		this.formLayout = formLayout;

		if (formLayout == null) {
			setMapLayout(null);
		} else {
			setMapLayout(formLayout.getMapLayoutControl().getMapLayout());
		}
	}

	private void setMapLayout(MapLayout mapLayout) {
		if (mapLayout == null) {
			for (AbstractPropertyControl abstractPropertyControl : propertyControls) {
				abstractPropertyControl.setVisible(false);
			}
		} else {
			for (AbstractPropertyControl abstractPropertyControl : propertyControls) {
				abstractPropertyControl.setVisible(true);
				abstractPropertyControl.setFormLayout(this.formLayout);
			}
		}
//		this.panelContainer.updateUI();
		this.updateUI();
	}

	private void initializeComponents() {
		this.checkBoxAutoApply = new JCheckBox("AutoApply");
		this.buttonApply = new SmButton("Apply");
		this.panelContainer = new JPanel();
		this.panelContainer.setLayout(new BoxLayout(this.panelContainer, BoxLayout.Y_AXIS));
		JScrollPane scrollPaneContainer = new JScrollPane(this.panelContainer);
		scrollPaneContainer.setBorder(null);

		this.setLayout(new GridBagLayout());
		this.add(
				scrollPaneContainer,
				new GridBagConstraintsHelper(0, 0, 2, 1).setFill(GridBagConstraints.BOTH).setWeight(1, 1).setAnchor(GridBagConstraints.CENTER)
						.setInsets(10, 10, 5, 10));
		this.add(
				this.checkBoxAutoApply,
				new GridBagConstraintsHelper(0, 1, 1, 1).setFill(GridBagConstraints.NONE).setWeight(1, 0).setAnchor(GridBagConstraints.WEST)
						.setInsets(0, 10, 5, 0));
		this.add(this.buttonApply, new GridBagConstraintsHelper(1, 1, 1, 1).setFill(GridBagConstraints.NONE).setWeight(0, 0).setAnchor(GridBagConstraints.EAST)
				.setInsets(0, 10, 5, 10));
	}

	private void initializeResources() {
		this.checkBoxAutoApply.setText(ControlsProperties.getString("String_AutoApply"));
		this.buttonApply.setText(CoreProperties.getString("String_Apply"));
	}

	private void checkBoxAutoApply() {
		if (propertyControls != null && !propertyControls.isEmpty()) {
			for (AbstractPropertyControl abstractPropertyControl : propertyControls) {
				abstractPropertyControl.setAutoApply(this.checkBoxAutoApply.isSelected());
			}
			if (this.checkBoxAutoApply.isSelected()) {
				this.buttonApply.setEnabled(false);
			}
		} else {
			this.buttonApply.setEnabled(false);
		}
	}

	private void buttonApplyClicked() {
		if (propertyControls != null && !propertyControls.isEmpty()) {
			for (AbstractPropertyControl abstractPropertyControl : propertyControls) {
				abstractPropertyControl.apply();
			}
		}
		this.buttonApply.setEnabled(false);
	}

	@Override
	public void setVisible(boolean aFlag) {
		super.setVisible(aFlag);
		if (aFlag) {
			IForm activeForm = Application.getActiveApplication().getActiveForm();
			if (activeForm instanceof IFormLayout) {
				setFormLayout((IFormLayout) activeForm);
			}
		}
	}
}
