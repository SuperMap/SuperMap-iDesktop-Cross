package com.supermap.desktop.ui.controls.prjcoordsys;

import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.ui.controls.CellRenders.TableDataCellRender;
import com.supermap.desktop.ui.controls.DatasourceComboBox;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.ui.controls.SmDialog;
import com.supermap.desktop.ui.controls.borderPanel.PanelButton;
import com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels.PanelCoordSysInfo;
import com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels.PanelReferSysTransSettings;
import com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels.TableModelBatchPrjTranslatorDatasetsList;
import com.supermap.desktop.ui.controls.progress.FormProgressTotal;
import com.supermap.desktop.ui.controls.smTables.CheckHeaderCellRender;
import com.supermap.desktop.utilities.DatasourceUtilities;
import com.supermap.desktop.utilities.PrjCoordSysUtilities;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

/**
 * Created by yuanR on 2017/10/10 0010.
 * 批量投影转换主面板
 */
public class JDialogBatchPrjTranslator extends SmDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	// 源数据面板块
	private JPanel panelSourceData;
	private JLabel labelSourceDatasource;
	private DatasourceComboBox sourceDatasource;
	private PanelCoordSysInfo sourcePanelCoordSysInfo;

	// 目标数据面板块
	private JPanel panelTargetData;
	private JLabel labelTargetDatasource;
	private DatasourceComboBox targetDatasource;
	private PanelCoordSysInfo targetPanelCoordSysInfo;

	// 参照系转换设置面板块
	private PanelReferSysTransSettings panelReferSysTransSettings;

	// 数据集列表
	private JPanel tablePanel;
	private JTable table;
	private TableModelBatchPrjTranslatorDatasetsList tableModel;
	private JCheckBox checkBox;

	// 确定取消按钮；
	private PanelButton panelButton = new PanelButton();

	public CoordSysTransMethod getMethod() {
		return this.panelReferSysTransSettings.getMethod();
	}

	public CoordSysTransParameter getParameter() {
		return this.panelReferSysTransSettings.getParameter();
	}


	private static final int TABLE_COLUMN_ISSELECTED = 0;
	private static final int TABLE_COLUMN_SOURCEDATASET = 1;
	//private static final int TABLE_COLUMN_TARGETDATASETNAME = 2;
	private ArrayList<TableModelBatchPrjTranslatorDatasetsList.TableData> dataList;

	private Boolean isOKTargetPrj = false;
	private Boolean isHasSelectedDataList = false;


	/**
	 * 数据源、数据及改变监听
	 */
	private ItemListener datasourceChangedListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getSource().equals(sourceDatasource) && e.getStateChange() == ItemEvent.SELECTED) {
				sourcePanelCoordSysInfo.setCoordInfo(PrjCoordSysUtilities.getDescription(sourceDatasource.getSelectedDatasource().getPrjCoordSys()));
				// 当源数据ComboBox改变时，JTable也跟随改变
				tableModel.setDataList(getAvailableDatasets(sourceDatasource.getSelectedDatasource().getDatasets()), targetDatasource.getSelectedDatasource());
			} else if (e.getSource().equals(targetDatasource) && e.getStateChange() == ItemEvent.SELECTED) {
				targetPanelCoordSysInfo.setCoordInfo(PrjCoordSysUtilities.getDescription(targetDatasource.getSelectedDatasource().getPrjCoordSys()));
				// 当目标数据ComboBox改变时，JTable也跟随改变，
				tableModel.updataDataList(targetDatasource.getSelectedDatasource());
			}

		}
	};

	private TableModelListener tableModelListener = new TableModelListener() {
		@Override
		public void tableChanged(TableModelEvent e) {
			// 当model改变时，
			dataList = tableModel.getDataList();
			Boolean isHasSelected = false;
			for (int i = 0; i < dataList.size(); i++) {
				if (dataList.get(i).isSelected()) {
					isHasSelected = true;
				}
			}
			checkBox.setSelected(isHasSelected);
			table.getTableHeader().repaint();
			isHasSelectedDataList = isHasSelected;
			panelButton.getButtonOk().setEnabled(isHasSelectedDataList && isOKTargetPrj);
		}
	};

	/**
	 *
	 */
	private ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(panelButton.getButtonOk())) {
				ArrayList<TableModelBatchPrjTranslatorDatasetsList.TableData> doDataList = new ArrayList<>();
				for (int i = 0; i < dataList.size(); i++) {
					if (dataList.get(i).isSelected()) {
						doDataList.add(dataList.get(i));
					}
				}
				FormProgressTotal formProgress = new FormProgressTotal();
				formProgress.doWork(new BatchPrjTranslatorCallable(doDataList, getMethod(), getParameter()));
				dialogResult = DialogResult.OK;
			} else {
				dialogResult = DialogResult.CANCEL;
			}
			JDialogBatchPrjTranslator.this.dispose();
		}
	};


	public JDialogBatchPrjTranslator() {
		initializeComponents();
		initializeResources();
		initializeLayout();
		initListener();
		initStates();

		setSize(800, 600);
		setLocationRelativeTo(null);
	}

	private void initStates() {
		// 设置源数据的数据源选择以及投影信息
		Datasource ActiveDatasource = DatasourceUtilities.getDefaultDatasource();
		this.sourceDatasource.setSelectedDatasource(ActiveDatasource);
		this.sourcePanelCoordSysInfo.setCoordInfo(PrjCoordSysUtilities.getDescription(sourceDatasource.getSelectedDatasource().getPrjCoordSys()));
		// 设置目标数据数据源选择以及投影信息
		// 目标数据源不支持平面无投影数据源
		Datasources datasources = Application.getActiveApplication().getWorkspace().getDatasources();
		for (int i = 0; i < datasources.getCount(); i++) {
			if (datasources.get(i).getPrjCoordSys().getType().equals(PrjCoordSysType.PCS_NON_EARTH) || datasources.get(i).isReadOnly()) {
				this.targetDatasource.removeDataSource(datasources.get(i));
			}
		}
		if (this.targetDatasource.getItemCount() > 0) {
			for (int i = 0; i < this.targetDatasource.getItemCount(); i++) {
				if (!this.targetDatasource.getDatasourceAt(i).equals(sourceDatasource.getSelectedDatasource())) {
					this.targetDatasource.setSelectedDatasource(this.targetDatasource.getDatasourceAt(i));
					break;
				}
			}
			this.targetPanelCoordSysInfo.setCoordInfo(PrjCoordSysUtilities.getDescription(targetDatasource.getSelectedDatasource().getPrjCoordSys()));
		}
		// 初始化JTable显示,对选中数据源中每条数据集记录进行遍历，找出可以填入列表的数据
		this.tableModel.setDataList(getAvailableDatasets(sourceDatasource.getSelectedDatasource().getDatasets()), this.targetDatasource.getSelectedDatasource());
		this.dataList = tableModel.getDataList();
		// 面板是否可用
		this.isOKTargetPrj = null != targetDatasource.getSelectedDatasource();
		// 当目标数据集为空时，设置jtable不可用，防止错误修改
		this.table.setEnabled(this.isOKTargetPrj);
		this.panelButton.getButtonOk().setEnabled(this.isOKTargetPrj && this.isHasSelectedDataList);
	}

	private void removeListener() {
		this.sourceDatasource.removeItemListener(this.datasourceChangedListener);
		this.targetDatasource.removeItemListener(this.datasourceChangedListener);
		this.tableModel.removeTableModelListener(this.tableModelListener);
		this.panelButton.getButtonOk().removeActionListener(this.actionListener);
		this.panelButton.getButtonCancel().removeActionListener(this.actionListener);
	}

	private void initListener() {
		removeListener();
		this.sourceDatasource.addItemListener(this.datasourceChangedListener);
		this.targetDatasource.addItemListener(this.datasourceChangedListener);
		this.tableModel.addTableModelListener(this.tableModelListener);
		this.panelButton.getButtonOk().addActionListener(this.actionListener);
		this.panelButton.getButtonCancel().addActionListener(this.actionListener);
	}


	private void initializeComponents() {

		// 源数据面板块
		this.panelSourceData = new JPanel();
		this.labelSourceDatasource = new JLabel("sourceDatasource");
		this.sourceDatasource = new DatasourceComboBox();
		this.sourcePanelCoordSysInfo = new PanelCoordSysInfo("", false);

		// 目标数据面板块
		this.panelTargetData = new JPanel();
		this.labelTargetDatasource = new JLabel();
		this.targetDatasource = new DatasourceComboBox();
		this.targetPanelCoordSysInfo = new PanelCoordSysInfo("", true);
		// 参照系转换设置面板块
		this.panelReferSysTransSettings = new PanelReferSysTransSettings("");
		//数据集列表
		this.tablePanel = new JPanel();
		this.table = new JTable();
		this.tableModel = new TableModelBatchPrjTranslatorDatasetsList();
		this.table.setModel(this.tableModel);

		this.table.setRowHeight(23);
		this.table.getColumn(this.table.getModel().getColumnName(TABLE_COLUMN_ISSELECTED)).setMaxWidth(50);
		this.table.getTableHeader().setReorderingAllowed(false);
		CheckHeaderCellRender checkHeaderCellRender = new CheckHeaderCellRender(this.table, "", true);
		this.checkBox = checkHeaderCellRender.getCheckBox();
		this.table.getTableHeader().getColumnModel().getColumn(TABLE_COLUMN_ISSELECTED).setHeaderRenderer(checkHeaderCellRender);
		this.table.getColumnModel().getColumn(TABLE_COLUMN_SOURCEDATASET).setCellRenderer(new TableDataCellRender());

	}

	private void initializeResources() {
		this.setTitle(ControlsProperties.getString("String_Title_BatchPrjTransform"));
		this.panelSourceData.setBorder(BorderFactory.createTitledBorder(ControlsProperties.getString("String_GroupBox_SourceDataset")));
		this.panelTargetData.setBorder(BorderFactory.createTitledBorder(ControlsProperties.getString("String_GroupBox_TargetDataset")));

		this.labelSourceDatasource.setText(ControlsProperties.getString("String_Label_Datasource"));
		this.labelTargetDatasource.setText(ControlsProperties.getString("String_Label_Datasource"));

		this.panelReferSysTransSettings.setBorder(BorderFactory.createTitledBorder(ControlsProperties.getString("String_GroupBox_CoordSysTranslatorSetting")));
		this.tablePanel.setBorder(BorderFactory.createTitledBorder(ControlsProperties.getString("String_GroupBox_DatasetsList")));

	}

	private void initializeLayout() {
		// 原数据面板布局
		GroupLayout sourceDataPanelLayout = new GroupLayout(panelSourceData);
		sourceDataPanelLayout.setAutoCreateContainerGaps(true);
		sourceDataPanelLayout.setAutoCreateGaps(true);
		panelSourceData.setLayout(sourceDataPanelLayout);
		// @formatter:off
		sourceDataPanelLayout.setHorizontalGroup(sourceDataPanelLayout.createParallelGroup()
				.addGroup(sourceDataPanelLayout.createSequentialGroup()
						.addComponent(this.labelSourceDatasource)
						.addComponent(this.sourceDatasource))
				.addComponent(this.sourcePanelCoordSysInfo));

		sourceDataPanelLayout.setVerticalGroup(sourceDataPanelLayout.createSequentialGroup()
				.addGroup(sourceDataPanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelSourceDatasource)
						.addComponent(this.sourceDatasource, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addComponent(this.sourcePanelCoordSysInfo));
		// @formatter:on

		// 目标数据
		GroupLayout targetDataPanelLayout = new GroupLayout(panelTargetData);
		targetDataPanelLayout.setAutoCreateContainerGaps(true);
		targetDataPanelLayout.setAutoCreateGaps(true);
		panelTargetData.setLayout(targetDataPanelLayout);
		// @formatter:off
		targetDataPanelLayout.setHorizontalGroup(targetDataPanelLayout.createParallelGroup()
				.addGroup(targetDataPanelLayout.createSequentialGroup()
						.addComponent(this.labelTargetDatasource)
						.addComponent(this.targetDatasource))
				.addGroup(targetDataPanelLayout.createSequentialGroup()
						.addComponent(this.targetPanelCoordSysInfo)));

		targetDataPanelLayout.setVerticalGroup(targetDataPanelLayout.createSequentialGroup()
				.addGroup(targetDataPanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelTargetDatasource)
						.addComponent(this.targetDatasource, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(targetDataPanelLayout.createSequentialGroup()
						.addComponent(this.targetPanelCoordSysInfo)));
		// @formatter:on

		//数据集列表
		JScrollPane pane = new JScrollPane();
		pane.setViewportView(this.table);
		this.tablePanel.setLayout(new GridBagLayout());
		this.tablePanel.add(pane, new GridBagConstraintsHelper(0, 0, 1, 1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setInsets(10, 10, 10, 10).setWeight(1, 1));
		// 主面板布局
		JPanel mianPanel = new JPanel();
		mianPanel.setLayout(new GridBagLayout());
		mianPanel.add(this.panelSourceData, new GridBagConstraintsHelper(0, 0, 1, 1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setInsets(10, 5, 0, 0).setWeight(0, 1));
		mianPanel.add(this.panelTargetData, new GridBagConstraintsHelper(0, 1, 1, 1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setInsets(0, 5, 0, 0).setWeight(0, 1));
		mianPanel.add(this.panelReferSysTransSettings, new GridBagConstraintsHelper(0, 2, 1, 1).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.CENTER).setInsets(0, 5, 0, 0).setWeight(0, 0));
		mianPanel.add(this.tablePanel, new GridBagConstraintsHelper(1, 0, 3, 3).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setInsets(10, 0, 0, 5).setWeight(1, 1));

		this.setLayout(new GridBagLayout());
		this.add(mianPanel, new GridBagConstraintsHelper(0, 0, 1, 1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.CENTER).setWeight(1, 1));
		this.add(this.panelButton, new GridBagConstraintsHelper(0, 1, 1, 1).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.EAST).setWeight(1, 0));
	}

	/**
	 * 获得可用的数据集填充数据集列表框
	 *
	 * @param datasets
	 * @return
	 */
	private ArrayList getAvailableDatasets(Datasets datasets) {
		ArrayList<Dataset> availableDatasets = new ArrayList();
		for (int i = 0; i < datasets.getCount(); i++) {
			// 属性表数据集无法进行投影转换，去除
			if (datasets.get(i).getPrjCoordSys().getType() != PrjCoordSysType.PCS_NON_EARTH && !datasets.get(i).getType().equals(DatasetType.TABULAR)) {
				availableDatasets.add(datasets.get(i));
			}
		}
		return availableDatasets;
	}
}
