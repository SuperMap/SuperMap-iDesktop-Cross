package com.supermap.desktop.spatialanalyst.vectoranalyst;

import com.supermap.data.Datasource;
import com.supermap.desktop.controls.ControlDefaultValues;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.spatialanalyst.SpatialAnalystProperties;
import com.supermap.desktop.ui.controls.DatasourceComboBox;
import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * 原先的缓冲区分析结果设置面板，用新的结果设置面板代替：PanelResultDataset
 * yuanR2017.9.26
 */
public class PanelResultData extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JLabel labelDataset;
	private JLabel labelDatasource;
	private DatasourceComboBox comboBoxResultDataDatasource;
	private JTextField textFieldResultDataDataset;

	public DatasourceComboBox getComboBoxResultDataDatasource() {
		return comboBoxResultDataDatasource;
	}

	public JTextField getTextFieldResultDataDataset() {
		return textFieldResultDataDataset;
	}

	public PanelResultData() {
		initComponent();
		setComponentName();
		initResources();
		resetDatasetName();
		initComboBoxResultDataDatasource();
		setPanelResultDataLayout();
	}

	private void initResources() {
		this.labelDatasource.setText(ControlsProperties.getString("String_Label_ResultDatasource"));
		this.labelDataset.setText(ControlsProperties.getString("String_Label_ResultDataset"));
	}

	private void initComponent() {
		this.labelDataset = new JLabel("Dataset");
		this.labelDatasource = new JLabel("Datasource");
		this.comboBoxResultDataDatasource = new DatasourceComboBox();
		this.textFieldResultDataDataset = new JTextField("Buffer");
		this.textFieldResultDataDataset.setPreferredSize(ControlDefaultValues.DEFAULT_PREFERREDSIZE);
		this.comboBoxResultDataDatasource.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				resetDatasetName();
			}
		});

		// 给数据集名称输入框添加焦点监听，当文本框为空的时候，给予正确的数据集名称--yuanR 2017.3.3
		this.textFieldResultDataDataset.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (StringUtilities.isNullOrEmpty(textFieldResultDataDataset.getText())) {
					resetDatasetName();
				}
			}
		});

	}

	private void setComponentName() {
		ComponentUIUtilities.setName(this.comboBoxResultDataDatasource, "PanelResultData_comboBoxResultDataDatasource");
		ComponentUIUtilities.setName(this.textFieldResultDataDataset, "PanelResultData_textFieldResultDataDataset");
	}

	private void setPanelResultDataLayout() {
		this.setBorder(BorderFactory.createTitledBorder(SpatialAnalystProperties.getString("String_ResultData")));

		GroupLayout panelResultDataLayout = new GroupLayout(this);
		panelResultDataLayout.setAutoCreateGaps(true);
		panelResultDataLayout.setAutoCreateContainerGaps(true);
		this.setLayout(panelResultDataLayout);

		//@formatter:off
		panelResultDataLayout.setHorizontalGroup(panelResultDataLayout.createSequentialGroup()
				.addGroup(panelResultDataLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(this.labelDatasource)
						.addComponent(this.labelDataset))
				.addGroup(panelResultDataLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(this.comboBoxResultDataDatasource, 5, 5, Short.MAX_VALUE)
						.addComponent(this.textFieldResultDataDataset, 5, 5, Short.MAX_VALUE)));

		panelResultDataLayout.setVerticalGroup(panelResultDataLayout.createSequentialGroup()
				.addGroup(panelResultDataLayout.createParallelGroup(Alignment.CENTER)
						.addComponent(this.labelDatasource)
						.addComponent(this.comboBoxResultDataDatasource, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(panelResultDataLayout.createParallelGroup(Alignment.CENTER)
						.addComponent(this.labelDataset)
						.addComponent(this.textFieldResultDataDataset, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(5, 5, Short.MAX_VALUE));
		//@formatter:on
	}

	private void initComboBoxResultDataDatasource() {

		for (int i = this.comboBoxResultDataDatasource.getItemCount() - 1; i >= 0; i--) {
			if (this.comboBoxResultDataDatasource.getItemAt(i) instanceof Datasource && this.comboBoxResultDataDatasource.getItemAt(i).isReadOnly()) {
				this.comboBoxResultDataDatasource.removeItemAt(i);
			}
		}
	}

	private void resetDatasetName() {
		String name = "Buffer";
		if (this.comboBoxResultDataDatasource.getSelectedDatasource() != null) {
			this.textFieldResultDataDataset.setText(this.comboBoxResultDataDatasource.getSelectedDatasource().getDatasets().getAvailableDatasetName(name));
		}
	}

	/**
	 * 创建面板是否可用方法
	 * 2017.3.2 yuanR
	 *
	 * @param isEnable
	 */
	public void setPanelEnable(boolean isEnable) {
		this.comboBoxResultDataDatasource.setEnabled(isEnable);
		this.textFieldResultDataDataset.setEnabled(isEnable);
	}
}
