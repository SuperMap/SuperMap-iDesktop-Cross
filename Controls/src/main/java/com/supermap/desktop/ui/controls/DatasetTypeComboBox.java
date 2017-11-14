package com.supermap.desktop.ui.controls;

import com.supermap.data.DatasetType;
import com.supermap.desktop.Application;
import com.supermap.desktop.CommonToolkit;
import com.supermap.desktop.controls.ControlDefaultValues;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.ControlsResources;
import com.supermap.desktop.implement.DefaultComboBoxUI;
import com.supermap.desktop.properties.CoreProperties;

import javax.swing.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * 数据集类型选择器
 * <p>
 * 提供的构造方法：
 * 默认构造不含“所有数据集类型”项的ComboBox，item存储对象为数据集类型以及其字符串
 * 数据集类型数组构造
 * 数据集类型字符串数组构造（需保证数据集类型名称正确）
 * <p>\
 * 提供的可获得的参数：
 * SelectedDatasetTypes、SelectedDatasetTypeName
 *
 * @author YuanR 2017.2.13
 */
public class DatasetTypeComboBox extends JComboBox<DataCell> {
	private static final long serialVersionUID = 1L;
//	private final static Dimension DIMENSION = new Dimension(80, 20);
	// 没有获取当前所有支持类型的方法，需要的时候再开
	private transient DatasetType[] supportedDatasetTypes;
	//是否含有“所有数据类型”这一项，默认不含
	private boolean isAllShown = false;
	private boolean isSimpleDatasetShown = false;

	// 为了保证顺序，不能遍历
	public static final DatasetType[] ALL_DATASET_TYPE = new DatasetType[]{
			DatasetType.POINT,
			DatasetType.LINE,
			DatasetType.REGION,
			DatasetType.TEXT,
			DatasetType.CAD,
			DatasetType.TABULAR,
			DatasetType.LINKTABLE,
			DatasetType.NETWORK,
			DatasetType.NETWORK3D,
			DatasetType.LINEM,
			DatasetType.PARAMETRICLINE,
			DatasetType.PARAMETRICREGION,
			DatasetType.GRIDCOLLECTION,
			DatasetType.IMAGECOLLECTION,
			DatasetType.MODEL,
			DatasetType.IMAGE,
			DatasetType.WMS,
			DatasetType.WCS,
			DatasetType.GRID,
			DatasetType.TOPOLOGY,
			DatasetType.POINT3D,
			DatasetType.LINE3D,
			DatasetType.REGION3D,
	};

	/**
	 * 覆盖原有的updateUI方法
	 * 2017.2.13
	 */
	public void updateUI() {
		this.setUI(new DefaultComboBoxUI());
	}

	/**
	 * 得到包含所有类型的数据集类型选择器
	 */
	public DatasetTypeComboBox() {
		supportedDatasetTypes = DatasetTypeComboBox.ALL_DATASET_TYPE;
		this.setPreferredSize(ControlDefaultValues.DEFAULT_PREFERREDSIZE);
		inits();
	}

	/**
	 * 得到包含选定类型的数据集类型选择器
	 *
	 * @param supportedDatasetTypes
	 */
	public DatasetTypeComboBox(DatasetType[] supportedDatasetTypes) {
		this.supportedDatasetTypes = supportedDatasetTypes;
		this.setPreferredSize(ControlDefaultValues.DEFAULT_PREFERREDSIZE);
		inits();
	}

	/**
	 * 根据所给的数据集类型字符串来构建comboBox
	 * 需要输入正确的数据集类型字符串
	 */
	public DatasetTypeComboBox(String[] supportedDatasetTypesName) {
		//对传进的字符参数进行筛选
		List<DatasetType> datasetTypesNames = new LinkedList<>();
		for (String allDatasetTypesName : supportedDatasetTypesName) {
			if (CommonToolkit.DatasetTypeWrap.findType(allDatasetTypesName) instanceof DatasetType) {
				datasetTypesNames.add(CommonToolkit.DatasetTypeWrap.findType(allDatasetTypesName));
			}
		}
		DatasetType[] datasetTypes = new DatasetType[datasetTypesNames.size()];
		for (int i = 0; i < datasetTypesNames.size(); i++) {
			datasetTypes[i] = datasetTypesNames.get(i);
		}

		//当输入的字符串无法构建ComboBox时，构建默认值的ComboBox
		if (datasetTypes.length <= 0) {
			this.supportedDatasetTypes = DatasetTypeComboBox.ALL_DATASET_TYPE;
		} else {
			this.supportedDatasetTypes = datasetTypes;
		}
		this.setPreferredSize(ControlDefaultValues.DEFAULT_PREFERREDSIZE);
		inits();
	}

	/**
	 * 初始化
	 */
	private void inits() {
		if (supportedDatasetTypes == null || supportedDatasetTypes.length <= 0) {
			this.setModel(null);
		} else {
			List<DataCell> datasetTypeModel = new LinkedList<>();
			if (isAllShown) {
				datasetTypeModel.add(new DataCell(CoreProperties.getString("String_DatasetType_All")));
			}
			for (DatasetType datasetType : supportedDatasetTypes) {
				datasetTypeModel.add(new DataCell(datasetType, CommonToolkit.DatasetTypeWrap.findName(datasetType)));
			}
			DataCell[] result = new DataCell[datasetTypeModel.size()];
			for (int i = 0; i < datasetTypeModel.size(); i++) {
				result[i] = datasetTypeModel.get(i);
			}
			this.setModel(new DefaultComboBoxModel<>(result));
			this.setBorder(BorderFactory.createEtchedBorder(1));
			if (isSimpleDatasetShown) {
				String fileParentPath = "/controlsresources/WorkspaceManager/Dataset/Image_SimpleDataset_Normal.png";
				URL url = ControlsResources.getResourceURL(fileParentPath);
				ImageIcon simpleDatasetIcon = new ImageIcon(url);
				this.addItem(new DataCell(simpleDatasetIcon, ControlsProperties.getString("string_comboboxitem_sample")));
			}
			ListCellRenderer<Object> renderer = new CommonListCellRenderer();
			setRenderer(renderer);
		}
	}

	/**
	 * 设置可以显示的数据集类型
	 *
	 * @param value
	 */
	public void setSupportedDatasetTypes(DatasetType[] value) {
		this.supportedDatasetTypes = value;
		inits();
	}


	/**
	 * 设置是否显示 “所有数据集”选项
	 *
	 * @param isAllShown true-显示/ false-不显示
	 */
	public void setAllShown(boolean isAllShown) {
		this.isAllShown = isAllShown;
		inits();
	}

	/**
	 * 设置是否显示 “简答数据集”选项
	 *
	 * @param isSimpleDatasetShown true-显示/ false-不显示
	 */
	public void setSimpleDatasetShown(boolean isSimpleDatasetShown) {
		this.isSimpleDatasetShown = isSimpleDatasetShown;
		inits();
	}

	/**
	 * 获得选中项的数据集类型字符串
	 */
	public String getSelectedDatasetTypeName() {
		if (getSelectedIndex() == -1) {
			return null;
		}
		String name = null;
		try {
			DataCell temp = (DataCell) getSelectedItem();
			name = temp.getDataName();
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
		return name;
	}

	/**
	 * 获得选中的数据集类型数组
	 *
	 * @return
	 */
	public DatasetType[] getSelectedDatasetTypes() {
		if (getSelectedIndex() == -1) {
			return null;
		}
		String selectedItem = ((DataCell) this.getSelectedItem()).getDataName();
		if (selectedItem == null) {
			return null;
		} else if (selectedItem.equals(CoreProperties.getString("String_DatasetType_All"))) {
			return supportedDatasetTypes;
		} else {
			return new DatasetType[]{CommonToolkit.DatasetTypeWrap.findType(selectedItem)};
		}
	}
}
