package com.supermap.desktop.spatialanalyst.vectoranalyst;

import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IFormMap;
import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.spatialanalyst.SpatialAnalystProperties;
import com.supermap.desktop.ui.UICommonToolkit;
import com.supermap.desktop.ui.controls.SmDialog;
import com.supermap.desktop.ui.trees.TreeNodeData;
import com.supermap.desktop.ui.trees.WorkspaceTree;
import com.supermap.desktop.ui.controls.borderPanel.PanelButton;
import com.supermap.desktop.utilities.MapUtilities;
import com.supermap.mapping.Layer;
import com.supermap.ui.MapControl;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BufferDialog extends SmDialog {
	/**
	 * 对缓冲区功能进行重构--yuanR 2017.3.6
	 */
	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;
	// 数据类型面板：点、面数据/线数据
	private JPanel panelDataType;
	private JLabel labelDataType;
	private JRadioButton radioButtonPointOrRegion = new JRadioButton("PointOrRegion");
	private JRadioButton radioButtonLine = new JRadioButton("Line");
	// 数据类型对应的缓冲区面板
	private JPanel panelBufferType;
	private PanelLineBufferAnalyst panelLineBufferAnalyst;
	private PanelPointOrRegionAnalyst panelPointOrRegionAnalyst;
	// 确定/取消按钮面板
	private PanelButton panelButton;
	private MapControl mapControl;

	private LocalActionListener localActionListener = new LocalActionListener();


	private DoSome some = new DoSome() {
		@Override
		public void doSome(boolean isArcSegmentNumSuitable, boolean isComboBoxDatasetNotNull, boolean isRadiusNumSuitable, boolean isHasResultDataset) {
			panelButton.getButtonOk().setEnabled(isArcSegmentNumSuitable && isComboBoxDatasetNotNull && isRadiusNumSuitable && isHasResultDataset);
		}
	};

	public BufferDialog() {
		super();
		intBufferDialog();
		// 初始化数据类型对应的缓冲区面板--yaunR
		initPanelBufferBasic();

		setResizable(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setSize(new Dimension(720, this.getPreferredSize().height));
		setVisible(true);
	}

	/**
	 * 设置缓冲区功能底层控件属性
	 */
	private void intBufferDialog() {
		initComponent();
		initLayout();
		initResources();
		registerEvent();
		initTraversalPolicy();
		setComponentName();
	}

	private void initComponent() {
		this.mainPanel = new JPanel();
		// 初始化数据类型面板及其控件
		this.labelDataType = new JLabel("DataType");
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(this.radioButtonPointOrRegion);
		buttonGroup.add(this.radioButtonLine);
		this.panelDataType = new JPanel();
		// 初始化其他面板
		this.panelBufferType = new JPanel();
		this.panelBufferType.setLayout(new BorderLayout());
		this.panelButton = new PanelButton();
	}

	/**
	 * 设置其面板布局
	 * yuanR 2017.3.6
	 */
	private void initLayout() {
		setPanelDataTypeLayout();
		this.mainPanel.setLayout(new BorderLayout());
		this.mainPanel.add(this.panelDataType, BorderLayout.NORTH);
		this.mainPanel.add(this.panelBufferType, BorderLayout.CENTER);
		this.mainPanel.add(this.panelButton, BorderLayout.SOUTH);
		this.getContentPane().add(this.mainPanel);
	}

	private void initResources() {
		this.setTitle(SpatialAnalystProperties.getString("String_SingleBufferAnalysis_Capital"));
		this.labelDataType.setText(SpatialAnalystProperties.getString("String_BufferAnalysis_DataType"));
		this.radioButtonLine.setText(SpatialAnalystProperties.getString("String_BufferAnalysis_Line"));
		this.radioButtonPointOrRegion.setText(SpatialAnalystProperties.getString("String_BufferAnalysis_PointAndRegion"));
	}

	/**
	 * 添加监听事件
	 */
	private void registerEvent() {
		removeRegisterEvent();
		this.radioButtonPointOrRegion.addActionListener(this.localActionListener);
		this.radioButtonLine.addActionListener(this.localActionListener);
		this.panelButton.getButtonOk().addActionListener(this.localActionListener);
		this.panelButton.getButtonCancel().addActionListener(this.localActionListener);
	}

	/**
	 * 初始化tab键的功能
	 */
	private void initTraversalPolicy() {
		if (this.componentList.size() > 0) {
			this.componentList.clear();
		}
		this.componentList.add(panelButton.getButtonOk());
		this.componentList.add(panelButton.getButtonCancel());
		this.setFocusTraversalPolicy(policy);
		this.getRootPane().setDefaultButton(panelButton.getButtonOk());
	}

	private void setComponentName() {
		ComponentUIUtilities.setName(this.mainPanel, "BufferDialog_mainPanel");
		ComponentUIUtilities.setName(this.panelDataType, "BufferDialog_panelDataType");
		ComponentUIUtilities.setName(this.labelDataType, "BufferDialog_labelDataType");
		ComponentUIUtilities.setName(this.radioButtonPointOrRegion, "BufferDialog_radioButtonPointOrRegion");
		ComponentUIUtilities.setName(this.radioButtonLine, "BufferDialog_radioButtonLine");
		ComponentUIUtilities.setName(this.panelBufferType, "BufferDialog_panelBufferType");
		ComponentUIUtilities.setName(this.panelLineBufferAnalyst, "BufferDialog_panelLineBufferAnalyst");
		ComponentUIUtilities.setName(this.panelPointOrRegionAnalyst, "BufferDialog_panelPointOrRegionAnalyst");
		ComponentUIUtilities.setName(this.panelButton, "BufferDialog_panelButton");
		ComponentUIUtilities.setName(this.mapControl, "BufferDialog_mapControl");
	}

	/**
	 * 存放两个checkBox和确定/取消按钮的面板即基层面板
	 */
	private void setPanelDataTypeLayout() {
		GroupLayout panelDataTypeLayout = new GroupLayout(this.panelDataType);
		panelDataTypeLayout.setAutoCreateGaps(true);
		panelDataTypeLayout.setAutoCreateContainerGaps(true);
		this.panelDataType.setLayout(panelDataTypeLayout);

		//@formatter:off
		panelDataTypeLayout.setHorizontalGroup(panelDataTypeLayout.createSequentialGroup()
				.addComponent(this.labelDataType).addGap(30)
				.addComponent(this.radioButtonPointOrRegion).addGap(30)
				.addComponent(this.radioButtonLine));
		panelDataTypeLayout.setVerticalGroup(panelDataTypeLayout.createSequentialGroup()
				.addGroup(panelDataTypeLayout.createParallelGroup(Alignment.CENTER)
						.addComponent(this.labelDataType)
						.addComponent(this.radioButtonPointOrRegion)
						.addComponent(this.radioButtonLine)));
		//@formatter:on
	}

	private void removeRegisterEvent() {
		this.radioButtonLine.removeActionListener(this.localActionListener);
		this.radioButtonPointOrRegion.removeActionListener(this.localActionListener);
		this.panelButton.getButtonOk().removeActionListener(this.localActionListener);
		this.panelButton.getButtonCancel().removeActionListener(this.localActionListener);
	}


	/**
	 * 根据地图以及树节点的选择情况，设置其缓冲区功能的核心面板属性
	 */
	private void initPanelBufferBasic() {
		int layersCount;
		// 打开地图时，如果选中点面或线数据集时，初始化打开界面为对应的选中缓冲区类型界面，如果选中的数据类型没有点，面，线，网络等类型时，默认打开线缓冲区界面
		if (Application.getActiveApplication().getActiveForm() != null && Application.getActiveApplication().getActiveForm() instanceof IFormMap) {
			this.mapControl = ((IFormMap) Application.getActiveApplication().getActiveForm()).getMapControl();
			// 不能直接获得地图中的所有图层，没有考虑图层分组的情况，会导致报错--yuanR 2017.3.10
			ArrayList<Layer> arrayList;
			arrayList = MapUtilities.getLayers(this.mapControl.getMap(), true);
			layersCount = arrayList.size();
			if (layersCount > 0) {
				for (int i = 0; i < layersCount; i++) {
					Layer[] activeLayer = new Layer[layersCount];
					activeLayer[i] = arrayList.get(i);
					if (activeLayer[i].getSelection() != null && activeLayer[i].getSelection().getCount() != 0) {
						if (activeLayer[i].getDataset().getType() == DatasetType.POINT || activeLayer[i].getDataset().getType() == DatasetType.REGION) {
							getPointorRegionType();
							return;
						} else if (activeLayer[i].getDataset().getType() == DatasetType.LINE || activeLayer[i].getDataset().getType() == DatasetType.NETWORK) {
							getLineType();
							return;
						}
					}
				}
			}
		}

		// 没有打开地图时，当选中数据集节点，如果为点，面类型时，打开点面缓冲区界面，选中其他节点打开线缓冲区界面
		WorkspaceTree workspaceTree = UICommonToolkit.getWorkspaceManager().getWorkspaceTree();
		TreePath selectedPath = workspaceTree.getSelectionPath();
		if (selectedPath != null) {
			if (selectedPath.getLastPathComponent() instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
				TreeNodeData nodeData = (TreeNodeData) selectedNode.getUserObject();
				if (nodeData.getData() instanceof Dataset) {
					Dataset selectedDataset = (Dataset) nodeData.getData();
					if (selectedDataset.getType() == DatasetType.POINT || selectedDataset.getType() == DatasetType.REGION) {
						getPointorRegionType();
						return;
					}
				}
			}
		}
		getLineType();
	}

	private void getPointorRegionType() {
		this.panelBufferType.removeAll();
		if (this.panelPointOrRegionAnalyst == null) {
			this.panelPointOrRegionAnalyst = new PanelPointOrRegionAnalyst(some);
		}
		this.panelBufferType.add(this.panelPointOrRegionAnalyst);
		pack();
		setSize(new Dimension(720, this.getPreferredSize().height));
		this.radioButtonPointOrRegion.setSelected(true);
	}

	private void getLineType() {
		this.panelBufferType.removeAll();
		if (this.panelLineBufferAnalyst == null) {
			this.panelLineBufferAnalyst = new PanelLineBufferAnalyst(some);
		}
		this.panelBufferType.add(this.panelLineBufferAnalyst);
		pack();
		setSize(new Dimension(720, this.getPreferredSize().height));
		this.radioButtonLine.setSelected(true);
	}


	class LocalActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == radioButtonPointOrRegion) {
				getPointorRegionType();
			} else if (e.getSource() == radioButtonLine) {
				getLineType();
			} else if (e.getSource() == panelButton.getButtonOk()) {
				okButtonClicked();
			} else if (e.getSource() == panelButton.getButtonCancel()) {
				escapePressed();
			}
		}
	}

	private void okButtonClicked() {
		try {
			if (radioButtonPointOrRegion.isSelected()) {
				panelPointOrRegionAnalyst.createCurrentBuffer();
			} else if (radioButtonLine.isSelected()) {
				panelLineBufferAnalyst.CreateCurrentBuffer();
			}
		} catch (Exception ignored) {

		} finally {
			BufferDialog.this.dispose();
		}
	}

	private void escapePressed() {
		BufferDialog.this.dispose();
	}

}
