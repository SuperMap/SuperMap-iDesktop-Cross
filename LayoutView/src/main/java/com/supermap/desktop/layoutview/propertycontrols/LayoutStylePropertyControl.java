package com.supermap.desktop.layoutview.propertycontrols;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IFormLayout;
import com.supermap.desktop.controls.DefaultValues;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.ComponentDropDown;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/28
 * Time: 11:21
 * Description:
 */
public class LayoutStylePropertyControl extends AbstractPropertyControl {

	private static final long serialVersionUID = 1L;
	private JLabel labelBackColor;
	private ComponentDropDown dropDownBackColor;

	private Color backColor = Color.WHITE;

	private transient PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == dropDownBackColor) {
				backgroundColorChange();
			}
		}
	};

	public LayoutStylePropertyControl() {
		super("");
	}

	@Override
	protected void initializeComponents() {
		this.labelBackColor = new JLabel("BackColor");
		this.dropDownBackColor = new ComponentDropDown(ComponentDropDown.COLOR_TYPE);

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);

		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.labelBackColor,GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_LABEL_WIDTH, Short.MAX_VALUE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.dropDownBackColor, GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_COMPONENT_WIDTH, Short.MAX_VALUE))
		);

		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelBackColor)
						.addComponent(this.dropDownBackColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);
		this.setLayout(groupLayout);
	}

	@Override
	public void apply() {
		if (getFormLayout() != null) {
//			getFormLayout().getMapLayoutControl().getMapLayout().getBackgroundStyle().setFillForeColor(this.backColor);
			getFormLayout().getMapLayoutControl().getMapLayout().getPaper().setBackgroundColor(this.backColor);
			getFormLayout().getMapLayoutControl().getMapLayout().refresh();
		}
	}

	@Override
	protected void initializeResources() {
		this.labelBackColor.setText(CoreProperties.getString("String_Label_BackColor"));
	}

	@Override
	protected void initializePropertyValues(IFormLayout formLayout) {
		if (formLayout!=null){
			this.backColor=formLayout.getMapLayoutControl().getMapLayout().getPaper().getBackgroundColor();
		}
	}

	@Override
	protected void registerEvents() {
		super.registerEvents();
		this.dropDownBackColor.addPropertyChangeListener(ComponentDropDown.CHANGECOLOR, this.propertyChangeListener);
	}

	@Override
	protected void unregisterEvents() {
		super.unregisterEvents();
		this.dropDownBackColor.removePropertyChangeListener(ComponentDropDown.CHANGECOLOR, this.propertyChangeListener);
	}

	@Override
	protected void fillComponents() {
		this.dropDownBackColor.setColor(this.backColor);
		this.updateUI();
	}

	@Override
	protected void setComponentsEnabled() {}

	@Override
	protected boolean verifyChange() {
		return !getFormLayout().getMapLayoutControl().getMapLayout().getPaper().getBackgroundColor().equals(this.backColor);
	}

	private void backgroundColorChange() {
		try {
			this.backColor = this.dropDownBackColor.getColor();
			verify();
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}
}
