package com.supermap.desktop.layoutview.propertycontrols;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IFormLayout;
import com.supermap.desktop.controls.DefaultValues;
import com.supermap.desktop.layoutview.LayoutViewProperties;
import com.supermap.desktop.ui.SMSpinner;
import com.supermap.layout.MapLayout;
import com.supermap.mapping.GridSetting;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/28
 * Time: 11:21
 * Description:grid setting panel
 */
public class LayoutGridPropertyControl extends AbstractPropertyControl {

	private static final long serialVersionUID = 1L;
	private JCheckBox checkBoxGrid;
	private JCheckBox checkBoxGridCatch;
	private JLabel labelHInterval;
	private SMSpinner smSpinnerHInterval;
	private JLabel labelVInterval;
	private SMSpinner smSpinnerVInterval;

	private boolean isShowGrid = false;
	private boolean isGridCatch;
	private double hIntervalValue = 0;
	private double vIntervalValue = 0;

	private transient ItemListener itemListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getSource() == checkBoxGrid) {
				checkBoxIsShowGridChange();
			} else if (e.getSource() == checkBoxGridCatch) {
				checkBoxIsGridCatchChange();
			}
		}
	};

	private transient ChangeListener changeListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == smSpinnerHInterval) {
				smSpinnerHIntervalChange();
			} else if (e.getSource() == smSpinnerVInterval) {
				smSpinnerVIntervalChange();
			}
		}
	};

	public LayoutGridPropertyControl() {
		super(LayoutViewProperties.getString("String_GridSetting"));
	}

	@Override
	protected void initializeComponents() {
		this.checkBoxGrid = new JCheckBox();
		this.checkBoxGridCatch = new JCheckBox();
		this.labelHInterval = new JLabel("HInterval");
		this.labelVInterval = new JLabel("VInterval");
		this.smSpinnerHInterval = new SMSpinner(new SpinnerNumberModel(0, 0, Short.MAX_VALUE, 1));
		this.smSpinnerVInterval = new SMSpinner(new SpinnerNumberModel(0, 0, Short.MAX_VALUE, 1));
		initLayout();
	}

	private void initLayout() {
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);

		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.checkBoxGrid)
						.addComponent(this.labelHInterval,GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_LABEL_WIDTH, Short.MAX_VALUE)
						.addComponent(this.labelVInterval,GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_LABEL_WIDTH, Short.MAX_VALUE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.checkBoxGridCatch)
						.addComponent(this.smSpinnerHInterval, GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_COMPONENT_WIDTH, Short.MAX_VALUE)
						.addComponent(this.smSpinnerVInterval, GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_COMPONENT_WIDTH, Short.MAX_VALUE))
		);

		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.checkBoxGrid)
						.addComponent(this.checkBoxGridCatch))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelHInterval)
						.addComponent(this.smSpinnerHInterval, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelVInterval)
						.addComponent(this.smSpinnerVInterval, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);
		this.setLayout(groupLayout);
	}

	@Override
	public void apply() {
		if (getFormLayout() != null) {
			MapLayout mapLayout = getFormLayout().getMapLayoutControl().getMapLayout();
			mapLayout.getPaper().getGrid().setVisible(this.isShowGrid);
			mapLayout.getPaper().getGrid().setSnapable(this.isGridCatch);
			mapLayout.getPaper().getGrid().setHorizontalSpacing(this.hIntervalValue);
			mapLayout.getPaper().getGrid().setVerticalSpacing(this.vIntervalValue);
			mapLayout.refresh();
		}
	}

	@Override
	protected void initializeResources() {
		this.checkBoxGrid.setText(LayoutViewProperties.getString("String_ShowGrid"));
		this.checkBoxGridCatch.setText(LayoutViewProperties.getString("String_GridCatch"));
		this.labelHInterval.setText(LayoutViewProperties.getString("String_HInterval"));
		this.labelVInterval.setText(LayoutViewProperties.getString("String_VInterval"));
	}

	@Override
	protected void initializePropertyValues(IFormLayout formLayout) {
		if (formLayout != null) {
			GridSetting gridSetting = formLayout.getMapLayoutControl().getMapLayout().getPaper().getGrid();
			this.isShowGrid = gridSetting.isVisible();
			this.isGridCatch = gridSetting.isSnapable();
			this.hIntervalValue = gridSetting.getHorizontalSpacing();
			this.vIntervalValue = gridSetting.getVerticalSpacing();
		}
	}

	@Override
	protected void registerEvents() {
		super.registerEvents();
		this.checkBoxGrid.addItemListener(this.itemListener);
		this.checkBoxGridCatch.addItemListener(this.itemListener);
		this.smSpinnerHInterval.addChangeListener(this.changeListener);
		this.smSpinnerVInterval.addChangeListener(this.changeListener);
	}

	@Override
	protected void unregisterEvents() {
		super.unregisterEvents();
		this.checkBoxGrid.removeItemListener(this.itemListener);
		this.checkBoxGridCatch.removeItemListener(this.itemListener);
		this.smSpinnerHInterval.removeChangeListener(this.changeListener);
		this.smSpinnerVInterval.removeChangeListener(this.changeListener);
	}

	@Override
	protected void fillComponents() {
		this.checkBoxGrid.setSelected(this.isShowGrid);
		this.checkBoxGridCatch.setSelected(this.isGridCatch);

		this.smSpinnerHInterval.setValue((new Double(hIntervalValue)).intValue());
		this.smSpinnerVInterval.setValue((new Double(vIntervalValue)).intValue());
	}

	@Override
	protected void setComponentsEnabled() {
	}

	@Override
	protected boolean verifyChange() {
		GridSetting gridSetting = getFormLayout().getMapLayoutControl().getMapLayout().getPaper().getGrid();
		return gridSetting.isVisible() != this.isShowGrid || gridSetting.isSnapable() != this.isGridCatch
				|| Double.compare(gridSetting.getHorizontalSpacing(), this.hIntervalValue) != 0
				|| Double.compare(gridSetting.getVerticalSpacing(), this.vIntervalValue) != 0;
	}

	private void checkBoxIsShowGridChange() {
		try {
			this.isShowGrid = this.checkBoxGrid.isSelected();
			verify();
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	private void checkBoxIsGridCatchChange() {
		try {
			this.isGridCatch = this.checkBoxGridCatch.isSelected();
			verify();
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	private void smSpinnerHIntervalChange() {
		try {
			this.hIntervalValue = Double.valueOf(this.smSpinnerHInterval.getValue().toString());
			verify();
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	private void smSpinnerVIntervalChange() {
		try {
			this.vIntervalValue = Double.valueOf(this.smSpinnerVInterval.getValue().toString());
			verify();
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

}
