package com.supermap.desktop.ui.controls.prjcoordsys;

import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.WorkspaceTreeManagerUIUtilities;
import com.supermap.desktop.ui.controls.*;
import com.supermap.desktop.ui.controls.borderPanel.PanelButton;
import com.supermap.desktop.ui.controls.borderPanel.PanelResultDataset;
import com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels.DoSome;
import com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels.PanelCoordSysInfo;
import com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels.PanelReferSysTransSettings;
import com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels.PanelTargetCoordSys;
import com.supermap.desktop.utilities.DatasetUtilities;
import com.supermap.desktop.utilities.PrjCoordSysUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.MessageFormat;

/**
 * Created by yuanR on 2017/9/25 0025.
 * 投影转换主窗体
 */
public class JDialogDatasetPrjTranslator extends SmDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	// 源数据面板块
	private JPanel panelSourceData;
	private JLabel labelDatasource;
	private JLabel labelDataset;
	private DatasourceComboBox datasource;
	private DatasetComboBox dataset;

	// 坐标系信息面板块
	private PanelCoordSysInfo panelCoordSysInfo;
	// 参照系转换设置面板块
	private PanelReferSysTransSettings panelReferSysTransSettings;
	// 结果另存为面板块
	private PanelResultDataset panelResultDataset;
	// 目标坐标系块
	private PanelTargetCoordSys panelTargetCoordSys;
	// 确定取消按钮；
	private PanelButton panelButton = new PanelButton();

	private transient PrjCoordSys targetPrj = null;


	public CoordSysTransMethod getMethod() {
		return this.panelReferSysTransSettings.getMethod();
	}

	public CoordSysTransParameter getParameter() {
		return this.panelReferSysTransSettings.getParameter();
	}

	public PrjCoordSys getTargetPrj() {
		return this.targetPrj;
	}

	public Boolean isSaveAsResult() {
		return this.panelResultDataset.getCheckBoxUsed().isSelected();
	}

	public Datasource getSelectedResultDatasource() {
		if (isSaveAsResult()) {
			return this.panelResultDataset.getComboBoxResultDataDatasource().getSelectedDatasource();
		} else {
			return null;
		}
	}

	public String getResultDatasetName() {
		if (isSaveAsResult()) {
			return this.panelResultDataset.getTextFieldResultDataDataset().getText();
		} else {
			return "";
		}
	}

	public Dataset getSourceDataset() {
		return this.dataset.getSelectedDataset();
	}

	private DoSome doSome = new DoSome() {
		@Override
		public void setTargetPrjCoordSys(PrjCoordSys targetPrjCoordSys) {
			targetPrj = targetPrjCoordSys;
		}

		@Override
		public void setOKButtonEnabled(boolean isEnabled) {
			panelButton.getButtonOk().setEnabled(isEnabled);

		}
	};


	/**
	 * 数据源、数据及改变监听
	 */
	private ItemListener comboBoxChangedListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getSource().equals(datasource) && e.getStateChange() == ItemEvent.SELECTED) {
				dataset.setDatasets(datasource.getSelectedDatasource().getDatasets());
			} else if (e.getSource().equals(dataset) && e.getStateChange() == ItemEvent.SELECTED) {
				// 当数据集改变时，更新投影信息、结果数据及名称、面板可用否
				panelCoordSysInfo.setCoordInfo(PrjCoordSysUtilities.getDescription(dataset.getSelectedDataset().getPrjCoordSys()));
				panelResultDataset.setResultName(dataset.getSelectedDataset().getName());
				if (dataset.getSelectedDataset().getPrjCoordSys().getType().equals(PrjCoordSysType.PCS_NON_EARTH)) {
					setPanelEnabled(false);
				} else {
					setPanelEnabled(true);
					// 每次切换数据集时，当选中数据集投影坐标含有地理坐标时可以进行投影转换，此时需要判断下目标坐标系值是否为空
					doSome.setOKButtonEnabled(panelTargetCoordSys.getTargetPrjCoordSys() != null);
					setResultPanelEnabled();
				}
			}
		}
	};

	/**
	 *
	 */
	private ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(panelButton.getButtonOk()) && Translator()) {
				dialogResult = DialogResult.OK;
			} else {
				dialogResult = DialogResult.CANCEL;
			}
			JDialogDatasetPrjTranslator.this.dispose();
		}
	};

	/**
	 * 投影转换功能实现
	 */
	private Boolean Translator() {
		Boolean result;
		try {
			Application.getActiveApplication().getOutput().output(MessageFormat.format(ControlsProperties.getString("String_BeginTrans_Dataset"), getSourceDataset().getName()));
			if (!isSaveAsResult()) {
				result = CoordSysTranslator.convert(getSourceDataset(), getTargetPrj(), getParameter(), getMethod());
				if (result) {
					Application.getActiveApplication().getOutput().output(MessageFormat.format(ControlsProperties.getString("String_CoordSysTrans_VectorSuccess"),
							getSourceDataset().getDatasource().getAlias(), getSourceDataset().getName()));
				} else {
					Application.getActiveApplication().getOutput().output(MessageFormat.format(ControlsProperties.getString("String_CoordSysTrans_Failed"),
							getSourceDataset().getDatasource().getAlias(), getSourceDataset().getName()));
				}
			} else {
				Dataset targetDataset = CoordSysTranslator.convert(getSourceDataset(), getTargetPrj(), getSelectedResultDatasource(), getResultDatasetName(), getParameter(), getMethod());
				result = targetDataset != null;
				if (result) {
					Application
							.getActiveApplication()
							.getOutput()
							.output(MessageFormat.format(ControlsProperties.getString("String_CoordSysTrans_RasterSuccess"),
									getSourceDataset().getDatasource().getAlias(), getSourceDataset().getName(), getSelectedResultDatasource().getAlias(), getResultDatasetName()));
				} else {
					Application
							.getActiveApplication()
							.getOutput()
							.output(MessageFormat.format(ControlsProperties.getString("String_CoordSysTrans_Failed"),
									getSourceDataset().getDatasource().getAlias(), getSourceDataset().getName()));
				}
				// 这种转换方式主要针对非矢量数据，转换之后会生成新的数据集，但是树的显示状态很诡异，这里对目标数据源的节点进行一次刷新
				WorkspaceTreeManagerUIUtilities.refreshNode(getSelectedResultDatasource());
			}
		} catch (Exception e) {
			result = false;
			Application.getActiveApplication().getOutput().output(e);
		} finally {
			removeListener();
			if (getParameter() != null) {
				getParameter().dispose();
			}
		}
		return result;
	}

	public JDialogDatasetPrjTranslator() {
		initializeComponents();
		initializeResources();
		initializeLayout();
		initStates();
		initListener();

		setSize(800, 500);
		setLocationRelativeTo(null);
	}

	private void removeListener() {
		this.datasource.removeItemListener(this.comboBoxChangedListener);
		this.dataset.removeItemListener(this.comboBoxChangedListener);
		this.panelButton.getButtonOk().removeActionListener(this.actionListener);
		this.panelButton.getButtonCancel().removeActionListener(this.actionListener);
	}

	private void initListener() {
		removeListener();
		this.datasource.addItemListener(this.comboBoxChangedListener);
		this.dataset.addItemListener(this.comboBoxChangedListener);
		this.panelButton.getButtonOk().addActionListener(this.actionListener);
		this.panelButton.getButtonCancel().addActionListener(this.actionListener);
	}

	private void initStates() {
		Dataset dataset = DatasetUtilities.getDefaultDataset();
		this.datasource.setSelectedDatasource(dataset.getDatasource());
		this.dataset.setDatasets(dataset.getDatasource().getDatasets());
		this.dataset.setSelectedDataset(dataset);
		this.panelCoordSysInfo.setCoordInfo(PrjCoordSysUtilities.getDescription(dataset.getPrjCoordSys()));
		this.panelResultDataset.setResultName(dataset.getName());
		if (dataset.getPrjCoordSys().getType().equals(PrjCoordSysType.PCS_NON_EARTH)) {
			setPanelEnabled(false);
		}
		setResultPanelEnabled();
	}


	private void initializeComponents() {
		this.labelDataset = new JLabel("Dataset");
		this.labelDatasource = new JLabel("Datasource");
		this.datasource = new DatasourceComboBox();
		this.dataset = new DatasetComboBox();
		this.panelSourceData = new JPanel();

		this.panelCoordSysInfo = new PanelCoordSysInfo("");
		this.panelReferSysTransSettings = new PanelReferSysTransSettings();
		this.panelResultDataset = new PanelResultDataset("", true);
		this.panelTargetCoordSys = new PanelTargetCoordSys(doSome);

	}

	private void initializeResources() {
		this.setTitle(ControlsProperties.getString("String_Title_DatasetPrjTransform"));
		this.labelDatasource.setText(ControlsProperties.getString("String_Label_Datasource"));
		this.labelDataset.setText(ControlsProperties.getString("String_Label_Dataset"));
		this.panelSourceData.setBorder(BorderFactory.createTitledBorder(ControlsProperties.getString("String_GroupBox_SourceDataset")));
		this.panelCoordSysInfo.setBorder(BorderFactory.createTitledBorder(ControlsProperties.getString("String_GroupBox_SrcCoordSys")));
		this.panelReferSysTransSettings.setBorder(BorderFactory.createTitledBorder(ControlsProperties.getString("String_GroupBox_CoordSysTranslatorSetting")));
		this.panelTargetCoordSys.setBorder(BorderFactory.createTitledBorder(ControlsProperties.getString("String_GroupBox_TarCoorSys")));
	}

	private void initializeLayout() {
		// 原数据面板布局
		GroupLayout groupLayout = new GroupLayout(this.panelSourceData);
		groupLayout.setAutoCreateContainerGaps(true);
		groupLayout.setAutoCreateGaps(true);
		this.panelSourceData.setLayout(groupLayout);

		// @formatter:off
		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.labelDatasource)
						.addComponent(this.labelDataset))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.datasource)
						.addComponent(this.dataset)));

		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelDatasource)
						.addComponent(this.datasource, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelDataset)
						.addComponent(this.dataset, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)));
		// @formatter:on

		// 主面板布局
		JPanel mianPanel = new JPanel();
		mianPanel.setLayout(new GridBagLayout());
		mianPanel.add(this.panelSourceData, new GridBagConstraintsHelper(0, 0, 1, 1).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.CENTER).setInsets(10, 5, 0, 0).setWeight(1, 0));
		mianPanel.add(this.panelCoordSysInfo, new GridBagConstraintsHelper(0, 1, 1, 1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setInsets(0, 5, 0, 0).setWeight(1, 1));
		mianPanel.add(this.panelReferSysTransSettings, new GridBagConstraintsHelper(0, 2, 1, 1).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.CENTER).setInsets(0, 5, 0, 0).setWeight(1, 0));
		mianPanel.add(this.panelResultDataset.getPanel(), new GridBagConstraintsHelper(1, 0, 1, 1).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.CENTER).setInsets(5, 0, 0, 5).setWeight(1, 0));
		mianPanel.add(this.panelTargetCoordSys, new GridBagConstraintsHelper(1, 1, 1, 2).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setInsets(0, 0, 0, 5).setWeight(1, 1));

		this.setLayout(new GridBagLayout());
		this.add(mianPanel, new GridBagConstraintsHelper(0, 0, 1, 1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setWeight(1, 1));
		this.add(this.panelButton, new GridBagConstraintsHelper(0, 1, 1, 1).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.EAST).setWeight(1, 0));
	}

	/**
	 * 设置面板是否可用
	 *
	 * @param isEnable
	 */
	public void setPanelEnabled(Boolean isEnable) {
		// 参照系转换设置面板块
		this.panelReferSysTransSettings.setPanelEnabled(isEnable);
		// 结果另存为面板块
		this.panelResultDataset.setPanelEnable(isEnable);
		// 目标坐标系块
		this.panelTargetCoordSys.setPanelEnabled(isEnable);
		// 确定取消按钮；
		this.panelButton.getButtonOk().setEnabled(isEnable);
	}

	/**
	 * 当转换的数据为栅格和影像时，必须另存结果
	 */
	public void setResultPanelEnabled() {
		if (this.dataset.getSelectedDataset().getType().equals(DatasetType.GRID) || this.dataset.getSelectedDataset().getType().equals(DatasetType.IMAGE)) {
			this.panelResultDataset.getCheckBoxUsed().setSelected(true);
			this.panelResultDataset.getCheckBoxUsed().setEnabled(false);
			this.panelResultDataset.getComboBoxResultDataDatasource().setEnabled(true);
			this.panelResultDataset.getTextFieldResultDataDataset().setEnabled(true);
		} else {
			this.panelResultDataset.getCheckBoxUsed().setEnabled(true);
		}
	}
}
