package com.supermap.desktop.process.parameters.ParameterPanels;

import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.events.FieldConstraintChangedEvent;
import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.interfaces.ParameterPanelDescribe;
import com.supermap.desktop.process.parameter.ipls.ParameterFieldGroup;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.ui.controls.smTables.tables.TableFieldNameCaptionType;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author XiaJT
 */
@ParameterPanelDescribe(parameterPanelType = ParameterType.FIELD_GROUP)
public class ParameterFieldGroupPanel extends SwingPanel {
	private ParameterFieldGroup parameterFieldGroup;
	private JLabel label = new JLabel();
	private TableFieldNameCaptionType tableFieldNameCaptionType = new TableFieldNameCaptionType();

	public ParameterFieldGroupPanel(IParameter parameter) {
		super(parameter);
		this.parameterFieldGroup = (ParameterFieldGroup) parameter;
		initComponent();
		initLayout();
		initListener();
	}

	@Override
	public void fieldConstraintChanged(FieldConstraintChangedEvent event) {
		if (event.getFieldName().equals(ParameterFieldGroup.FIELD_DATASET)) {
			this.tableFieldNameCaptionType.setDataset(this.parameterFieldGroup.getDataset());
		}
	}

	private void initComponent() {
		this.label.setText(this.parameterFieldGroup.getDescription());
		this.label.setToolTipText(this.parameterFieldGroup.getDescription());
		// 设置字段类型,类型设置在数据集设置之前-yuanR2017.11.29
		this.tableFieldNameCaptionType.setFieldTypes(this.parameterFieldGroup.getFieldType());
		this.tableFieldNameCaptionType.setDataset(this.parameterFieldGroup.getDataset());
		ComponentUIUtilities.setName(this.tableFieldNameCaptionType, this.parameter.getDescription());
	}

	private void initListener() {
		this.parameterFieldGroup.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(ParameterFieldGroup.FIELD_DATASET)) {
					tableFieldNameCaptionType.setDataset(parameterFieldGroup.getDataset());
				}
			}
		});

		this.tableFieldNameCaptionType.getTablesModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getType() == TableModelEvent.UPDATE) {
					parameterFieldGroup.setSelectedFields(tableFieldNameCaptionType.getSelectedFields());
				}
			}
		});
	}

	private void initLayout() {
		this.panel.setPreferredSize(new Dimension(200, 200));
		this.panel.setLayout(new GridBagLayout());
		this.panel.add(this.label, new GridBagConstraintsHelper(0, 0, 1, 1).setWeight(1, 0).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE));
		JScrollPane scrollPane = new JScrollPane(this.tableFieldNameCaptionType);
		scrollPane.setPreferredSize(new Dimension(200, 200));
		this.panel.add(scrollPane, new GridBagConstraintsHelper(0, 1, 1, 1).setWeight(1, 1).setAnchor(GridBagConstraints.CENTER).setFill(GridBagConstraints.BOTH).setInsets(5, 0, 0, 0));
	}
}
