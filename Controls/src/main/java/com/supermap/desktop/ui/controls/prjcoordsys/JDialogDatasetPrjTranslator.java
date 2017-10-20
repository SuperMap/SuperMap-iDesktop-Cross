package com.supermap.desktop.ui.controls.prjcoordsys;

import com.supermap.data.*;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.ui.controls.*;
import com.supermap.desktop.ui.controls.borderPanel.PanelButton;
import com.supermap.desktop.ui.controls.borderPanel.PanelResultDataset;
import com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels.DoSome;
import com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels.PanelCoordSysInfo;
import com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels.PanelReferSysTransSettings;
import com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels.PanelTargetCoordSys;
import com.supermap.desktop.ui.controls.progress.FormProgress;
import com.supermap.desktop.utilities.DatasetUtilities;
import com.supermap.desktop.utilities.PrjCoordSysUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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

	private Boolean isOKSourcePrj = false;
	private Boolean isOKTargetPrj = false;
	private Boolean isHasResultDataset = false;


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
			isOKTargetPrj = null != targetPrj;
		}

		@Override
		public void setOKButtonEnabled(boolean isEnabled) {
			panelButton.getButtonOk().setEnabled(isEnabled && isOKSourcePrj && isHasResultDataset && isOKTargetPrj);
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
				setResultPanelEnabled();
				// 确定按钮是否可用： 原数据投影是否为平面坐标系、目标投影是否为空、
				isOKSourcePrj = dataset.getSelectedDataset().getPrjCoordSys().getType() != PrjCoordSysType.PCS_NON_EARTH;
				doSome.setOKButtonEnabled(isOKSourcePrj);
			}
		}
	};

	/**
	 *
	 */
	private ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(panelButton.getButtonOk())) {
				FormProgress formProgress = new FormProgress();
				formProgress.setTitle(ControlsProperties.getString("String_Title_DatasetPrjTransform"));
				formProgress.doWork(new DatasetPrjTranslatorCallable(
						isSaveAsResult(),
						getSourceDataset(),
						getTargetPrj(),
						getMethod(),
						getParameter(),
						getSelectedResultDatasource(),
						getResultDatasetName()));
				dialogResult = DialogResult.OK;
			} else {
				dialogResult = DialogResult.CANCEL;
			}
			JDialogDatasetPrjTranslator.this.dispose();
		}
	};

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
		if (dataset != null) {
			this.datasource.setSelectedDatasource(dataset.getDatasource());
			this.dataset.setDatasets(dataset.getDatasource().getDatasets());
			this.dataset.setSelectedDataset(dataset);
			this.panelCoordSysInfo.setCoordInfo(PrjCoordSysUtilities.getDescription(dataset.getPrjCoordSys()));
			this.panelResultDataset.setResultName(dataset.getName());
			setResultPanelEnabled();
			// 确定按钮是否可用： 原数据投影是否为平面坐标系、目标投影是否为空、
			this.isOKSourcePrj = dataset.getPrjCoordSys().getType() != PrjCoordSysType.PCS_NON_EARTH;
			//this.isOKTargetPrj = null != panelTargetCoordSys.getTargetPrjCoordSys();
			this.isHasResultDataset = null != panelResultDataset.getComboBoxResultDataDatasource().getSelectedDatasource();
			this.panelButton.getButtonOk().setEnabled(isHasResultDataset && isOKSourcePrj && targetPrj != null);
		} else {
			this.panelButton.getButtonOk().setEnabled(false);
		}
	}


	private void initializeComponents() {
		this.labelDataset = new JLabel("Dataset");
		this.labelDatasource = new JLabel("Datasource");
		this.datasource = new DatasourceComboBox();
		this.dataset = new DatasetComboBox();
		this.panelSourceData = new JPanel();

		this.panelCoordSysInfo = new PanelCoordSysInfo("", true);
		this.panelReferSysTransSettings = new PanelReferSysTransSettings("");
		this.panelResultDataset = new PanelResultDataset("", true);
		this.panelTargetCoordSys = new PanelTargetCoordSys(doSome);

	}

	private void initializeResources() {
		this.setTitle(ControlsProperties.getString("String_Title_DatasetPrjTransform"));
		this.labelDatasource.setText(ControlsProperties.getString("String_Label_Datasource"));
		this.labelDataset.setText(ControlsProperties.getString("String_Label_Dataset"));
		this.panelSourceData.setBorder(BorderFactory.createTitledBorder(ControlsProperties.getString("String_GroupBox_SourceDataset")));
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

		// 调整布局之用
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(ControlsProperties.getString("String_GroupBox_SrcCoordSys")));
		panel.setLayout(new GridBagLayout());
		panel.add(this.panelCoordSysInfo, new GridBagConstraintsHelper(0, 0, 1, 1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setInsets(10, 10, 10, 10).setWeight(1, 1));

		// 主面板布局
		JPanel mianPanel = new JPanel();
		mianPanel.setLayout(new GridBagLayout());
		mianPanel.add(this.panelSourceData, new GridBagConstraintsHelper(0, 0, 1, 1).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.CENTER).setInsets(10, 5, 0, 0).setWeight(1, 0));
		mianPanel.add(panel, new GridBagConstraintsHelper(0, 1, 1, 1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setInsets(0, 5, 0, 0).setWeight(1, 1));
		mianPanel.add(this.panelReferSysTransSettings, new GridBagConstraintsHelper(0, 2, 1, 1).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.CENTER).setInsets(0, 5, 0, 0).setWeight(1, 0));
		mianPanel.add(this.panelResultDataset.getPanel(), new GridBagConstraintsHelper(1, 0, 1, 1).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.CENTER).setIpad(100, 0).setInsets(5, 0, 0, 5).setWeight(1, 0));
		mianPanel.add(this.panelTargetCoordSys, new GridBagConstraintsHelper(1, 1, 1, 2).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setIpad(100, 0).setInsets(0, 0, 0, 5).setWeight(1, 1));

		this.setLayout(new GridBagLayout());
		this.add(mianPanel, new GridBagConstraintsHelper(0, 0, 1, 1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setWeight(1, 1));
		this.add(this.panelButton, new GridBagConstraintsHelper(0, 1, 1, 1).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.EAST).setWeight(1, 0));
	}

	///**
	// * 设置面板是否可用
	// *
	// * @param isEnable
	// */
	//public void setPanelEnabled(Boolean isEnable) {
	//	// 参照系转换设置面板块
	//	this.panelReferSysTransSettings.setPanelEnabled(isEnable);
	//	// 结果另存为面板块
	//	this.panelResultDataset.setPanelEnable(isEnable);
	//	// 目标坐标系块
	//	this.panelTargetCoordSys.setPanelEnabled(isEnable);
	//	// 确定取消按钮；
	//	this.panelButton.getButtonOk().setEnabled(isEnable);
	//}

	/**
	 * 当转换的数据为栅格和影像时，必须另存结果
	 * 结果数据面板状态由原数据集情况决定
	 */
	public void setResultPanelEnabled() {
		Boolean isGridDatasetType = this.dataset.getSelectedDataset().getType().equals(DatasetType.GRID);
		Boolean isImageDatasetType = this.dataset.getSelectedDataset().getType().equals(DatasetType.IMAGE);
		Boolean isOnlyReadSourceDataset = this.dataset.getSelectedDataset().isReadOnly();

		// 当原数据集是只读、影像、栅格checkBox必选中
		if (!this.panelResultDataset.getCheckBoxUsed().isSelected()) {
			this.panelResultDataset.getCheckBoxUsed().setSelected(isOnlyReadSourceDataset || isGridDatasetType || isImageDatasetType);
		}
		this.panelResultDataset.getCheckBoxUsed().setEnabled(!isOnlyReadSourceDataset && !isGridDatasetType && !isImageDatasetType);
	}
}

