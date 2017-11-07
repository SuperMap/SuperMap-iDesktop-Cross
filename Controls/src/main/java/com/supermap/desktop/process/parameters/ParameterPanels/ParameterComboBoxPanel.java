package com.supermap.desktop.process.parameters.ParameterPanels;

import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.controls.utilities.JComboBoxUIUtilities;
import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.ParameterComboBoxCellRender;
import com.supermap.desktop.process.parameter.ParameterDataNode;
import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.interfaces.IParameterPanel;
import com.supermap.desktop.process.parameter.interfaces.ParameterPanelDescribe;
import com.supermap.desktop.process.parameter.ipls.ParameterComboBox;
import com.supermap.desktop.process.util.ParameterUtil;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.ui.controls.SmComboBox;
import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * @author XiaJT
 */
@ParameterPanelDescribe(parameterPanelType = ParameterType.COMBO_BOX)
public class ParameterComboBoxPanel extends SwingPanel implements IParameterPanel {
	private ParameterComboBox parameterComboBox;
	// 防止多次触发事件 Prevent multiple trigger events
	private boolean isSelectingItem = false;
	private JLabel label = new JLabel();
	private SmComboBox<ParameterDataNode> comboBox = new SmComboBox<>();

	public ParameterComboBoxPanel(IParameter parameterComboBox) {
		super(parameterComboBox);
		this.parameterComboBox = ((ParameterComboBox) parameterComboBox);
		ComponentUIUtilities.setName(this.comboBox, parameter.getDescribe());
		ArrayList<ParameterDataNode> items = this.parameterComboBox.getItems();
		if (items != null && items.size() > 0) {
			for (ParameterDataNode item : items) {
				comboBox.addItem(item);
			}
		}
		if (this.parameterComboBox.getSelectedItem() != null) {
			comboBox.setSelectedItem(this.parameterComboBox.getSelectedItem());
		} else {
			parameterComboBox.setFieldVale(ParameterComboBox.comboBoxValue, comboBox.getSelectedItem());
		}
		initListeners();
		label.setText(getDescribe());
		label.setToolTipText(this.parameterComboBox.getDescribe());
		label.setVisible(parameterComboBox.isDescriptionVisible());
		comboBox.setRenderer(new ParameterComboBoxCellRender(this.parameterComboBox.getIConGetter()));
		//comboBox.setEditable(this.parameterComboBox.isEditable());
		initLayout();
	}

	private void initLayout() {
		label.setPreferredSize(ParameterUtil.LABEL_DEFAULT_SIZE);
		comboBox.setPreferredSize(new Dimension(50, 23));
		comboBox.setEnabled(parameterComboBox.isEnabled());
		panel.setLayout(new GridBagLayout());

		panel.add(label, new GridBagConstraintsHelper(0, 0, 1, 1).setWeight(0, 1));
		panel.add(comboBox, new GridBagConstraintsHelper(1, 0, 1, 1).setWeight(1, 1).setAnchor(GridBagConstraints.CENTER).setFill(GridBagConstraints.HORIZONTAL).setInsets(0, 5, 0, 0));
		// 当描述为空时，不显示其label-yuanR
		if (StringUtilities.isNullOrEmpty(parameterComboBox.getDescribe())) {
			label.setVisible(false);
		}
	}

	private void initListeners() {
		// 意义何在？-yuanR存疑2017.11.3{
		//MouseAdapter comboBoxClicked = new MouseAdapter() {
		//	//添加右边按钮点击时事件
		//	@Override
		//	public void mouseReleased(MouseEvent e) {
		//		parameterComboBox.firePropertyChangeListener(new PropertyChangeEvent(parameterComboBox, "ComboBoxClicked", "", ""));
		//	}
		//};
		//comboBox.addMouseListener(comboBoxClicked);
		//comboBox.getComponent(0).addMouseListener(comboBoxClicked);
		//}
		parameterComboBox.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (!isSelectingItem && evt.getPropertyName().equals(ParameterComboBox.comboBoxValue)) {
					isSelectingItem = true;
					ParameterComboBoxPanel.this.comboBox.setSelectedItem(evt.getNewValue());
					isSelectingItem = false;
				} else if (!isSelectingItem && evt.getPropertyName().equals(ParameterComboBox.comboBoxItems)) {
					isSelectingItem = true;
					Object selectedItem = ParameterComboBoxPanel.this.comboBox.getSelectedItem();
					ParameterComboBoxPanel.this.comboBox.removeAllItems();
					ArrayList<ParameterDataNode> items = ParameterComboBoxPanel.this.parameterComboBox.getItems();
					if (items != null && items.size() > 0) {
						for (ParameterDataNode item : items) {
							ParameterComboBoxPanel.this.comboBox.addItem(item);
						}
					}
					if (JComboBoxUIUtilities.getItemIndex(ParameterComboBoxPanel.this.comboBox, selectedItem) != -1) {
						ParameterComboBoxPanel.this.comboBox.setSelectedItem(selectedItem);
					} else {
						ParameterComboBoxPanel.this.comboBox.setSelectedItem(null);
					}
					ParameterComboBoxPanel.this.parameterComboBox.setSelectedItem(ParameterComboBoxPanel.this.comboBox.getSelectedItem());
					isSelectingItem = false;
				}
			}
		});
		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (!isSelectingItem && e.getStateChange() == ItemEvent.SELECTED) {
					isSelectingItem = true;
					ParameterComboBoxPanel.this.parameterComboBox.setSelectedItem(comboBox.getSelectedItem());
					isSelectingItem = false;
				}
			}
		});
	}

	@Override
	protected void descriptionVisibleChanged(boolean newValue) {
		label.setVisible(newValue);
	}

	/**
	 * @return
	 */
	private String getDescribe() {
		String describe = parameterComboBox.getDescribe();
		if (parameterComboBox.isRequisite()) {
			return MessageFormat.format(CoreProperties.getString("String_IsRequiredLable"), describe);
		} else {
			return describe;
		}
	}
}
