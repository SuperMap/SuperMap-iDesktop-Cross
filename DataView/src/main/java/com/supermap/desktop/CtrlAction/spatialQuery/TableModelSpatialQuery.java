package com.supermap.desktop.CtrlAction.spatialQuery;

import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.dataview.DataViewProperties;
import com.supermap.desktop.utilities.StringUtilities;
import com.supermap.mapping.Layer;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

/**
 * @author XiaJT
 */
public class TableModelSpatialQuery extends DefaultTableModel {
	private ArrayList<TableRowData> rowDatas;
	private final String defaultDatasetName = "SpatialQuery";

	private String[] columns = new String[]{
			"",
			ControlsProperties.getString("String_Type"),
			DataViewProperties.getString("String__SearchedLayerName"),
			DataViewProperties.getString("String_SpatialQueryMode"),
			DataViewProperties.getString("String_TabularQueryCondition"),
	};
	private ArrayList<DatasetType> supportDatasetTypes;
	public static final int COLUMN_INDEX_IS_SELECTED = 0;
	public static final int COLUMN_INDEX_DATASET_TYPE = 1;
	public static final int COLUMN_INDEX_LAYER_NAME = 2;
	public static final int COLUMN_INDEX_SPATIAL_QUERY_MODE = 3;
	public static final int COLUMN_INDEX_SQL = 4;

	public TableModelSpatialQuery() {
		super();
		rowDatas = new ArrayList<>();
		supportDatasetTypes = new ArrayList<>();
		supportDatasetTypes.add(DatasetType.POINT);
//		supportDatasetTypes.add(DatasetType.POINT3D);
		supportDatasetTypes.add(DatasetType.LINE);
//		supportDatasetTypes.add(DatasetType.LINE3D);
		supportDatasetTypes.add(DatasetType.LINEM);
		supportDatasetTypes.add(DatasetType.NETWORK);
//		supportDatasetTypes.add(DatasetType.NETWORK3D);
		supportDatasetTypes.add(DatasetType.REGION);
//		supportDatasetTypes.add(DatasetType.REGION3D);
		supportDatasetTypes.add(DatasetType.CAD);
		supportDatasetTypes.add(DatasetType.TEXT);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0) {
			return Boolean.class;
		} else if (columnIndex == 1) {
			return DatasetType.class;
		} else if (columnIndex == 2) {
			return String.class;
		} else if (columnIndex == 3) {
			return SpatialQueryMode.class;
		} else if (columnIndex == 4) {
			return String.class;
		}
		return String.class;
	}

	@Override
	public int getRowCount() {
		if (rowDatas == null) {
			return 0;
		}
		return rowDatas.size();
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public String getColumnName(int column) {
		return columns[column];
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return column != 1 && column != 2;
	}

	@Override
	public Object getValueAt(int row, int column) {
		if (column == 0) {
			return rowDatas.get(row).isSelected();
		} else if (column == 1) {
			return rowDatas.get(row).getDatasetType();
		} else if (column == 2) {
			return rowDatas.get(row).getLayerName();
		} else if (column == 3) {
			return rowDatas.get(row).getSpatialQueryMode();
		} else if (column == 4) {
			return rowDatas.get(row).getSql();
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		if (column == 0) {
			rowDatas.get(row).setSelected(((Boolean) aValue));
		} else if (column == 3) {
			rowDatas.get(row).setSpatialQueryMode(((SpatialQueryMode) aValue));
		} else if (column == 4) {
			rowDatas.get(row).setSql((String) aValue);
		}
		fireTableCellUpdated(row, column);
	}

	public void setLayers(ArrayList<Layer> layers) {
		if (rowDatas != null) {
			rowDatas.clear();
		}
		if (layers != null && layers.size() > 0) {
			Datasource defaultDatasource = null;
			Datasource[] activeDatasources = Application.getActiveApplication().getActiveDatasources();
			if (activeDatasources.length > 0) {
				for (Datasource activeDatasource : activeDatasources) {
					if (activeDatasource.isOpened() && !activeDatasource.isReadOnly()) {
						defaultDatasource = activeDatasource;
						break;
					}
				}
			}
			if (defaultDatasource == null) {
				Datasources datasources = Application.getActiveApplication().getWorkspace().getDatasources();
				for (int i = 0; i < datasources.getCount(); i++) {
					if (datasources.get(i).isOpened() && !datasources.get(i).isReadOnly()) {
						defaultDatasource = datasources.get(i);
						break;
					}
				}
			}


			int i = 0;
			for (Layer layer : layers) {
				if (layer.getDataset() != null && supportDatasetTypes.contains(layer.getDataset().getType())) {
					TableRowData rowData = new TableRowData(layer);
					if (defaultDatasource != null) {
						rowData.setResultDatasource(defaultDatasource);
						for (; true; i++) {
							String tempDatasetName = i == 0 ? defaultDatasetName : defaultDatasetName + "_" + i;
							String resultName = defaultDatasource.getDatasets().getAvailableDatasetName(tempDatasetName);
							if (tempDatasetName.equalsIgnoreCase(resultName)) {
								rowData.setResultDataset(resultName);
								i++;
								break;
							}
						}
					}

					rowDatas.add(rowData);
				}
			}
		}
//		fireTableDataChanged();
//		fireTableRowsInserted(0, rowDatas.size());
		fireTableDataChanged();
	}

	public void Reset() {
		for (TableRowData rowData : rowDatas) {
			rowData.reset();
		}
	}

	//region 是否保存数据集
	public void setIsSave(int[] selectedRows, boolean isSave) {
		for (int selectedRow : selectedRows) {
			rowDatas.get(selectedRow).setSave(isSave);
		}
	}

	public Boolean isSave(int... rows) {
		Boolean result = null;
		for (int row : rows) {
			if (result == null) {
				result = rowDatas.get(row).isSave();
			} else {
				if (result != rowDatas.get(row).isSave()) {
					result = null;
					break;
				}
			}
		}
		return result;
	}

	//endregion
	//region 结果数据源
	public void setDatasource(int[] selectedRows, Datasource datasource) {
		for (int selectedRow : selectedRows) {
			if (datasource != rowDatas.get(selectedRow).getResultDatasource()) {
				rowDatas.get(selectedRow).setResultDatasource(datasource);
				if (datasource != null) {
					String resultDataset = rowDatas.get(selectedRow).getResultDataset();
					if (StringUtilities.isNullOrEmpty(resultDataset)) {
						resultDataset = defaultDatasetName;
					}
					rowDatas.get(selectedRow).setResultDataset(datasource.getDatasets().getAvailableDatasetName(resultDataset));
				}
			}
		}
	}

	public Datasource getResultDatasource(int... rows) {
		Datasource datasource = null;
		for (int row : rows) {
			if (datasource == null) {
				datasource = rowDatas.get(row).getResultDatasource();
			} else {
				if (datasource != rowDatas.get(row).getResultDatasource()) {
					datasource = null;
					break;
				}
			}
		}
		return datasource;
	}
	//endregion

	public boolean isSupportDatasetName(int row, String datasetName) {
		if (StringUtilities.isNullOrEmpty(datasetName)) {
			// 空
			return false;
		}
		TableRowData currentRowData = rowDatas.get(row);
		Datasource datasource = currentRowData.getResultDatasource();
		if (!datasource.getDatasets().isAvailableDatasetName(datasetName)) {
			// 数据源内存在
			return false;
		}

//		for (TableRowData rowData : rowDatas) {
//			if (rowData != currentRowData && rowData.isSave() && rowData.getResultDatasource() == datasource && datasetName.equalsIgnoreCase(rowData.getResultDataset())) {
//				// 当前确认保存的数据集
//				return false;
//			}
//		}
		return true;
	}

	//region 数据集名称
	public void setDatasetName(int row, String datasetName) {
		rowDatas.get(row).setResultDataset(datasetName);
	}

//	private Integer getIndex(String resultDataset) {
//		if (resultDataset.indexOf("defaultDatasetName") == 0) {
//			String[] defaultDatasetNames = resultDataset.split(defaultDatasetName);
//			if (defaultDatasetNames.length == 0) {
//				return 0;
//			} else if (defaultDatasetNames.length == 1 && defaultDatasetNames[0].charAt(0) == '_' ) {
//				String substring = defaultDatasetNames[0].substring(1);
//				try {
//					return Integer.valueOf(substring);
//				} catch (Exception e) {
//					return null;
//				}
//			}
//		}
//		return null;
//	}


	public String getDatasetName(int... row) {
		if (row.length != 1) {
			return "";
		}
		return rowDatas.get(row[0]).getResultDataset();
	}
	//endregion

	//region 只保存空间信息
	public void setIsOnlySaveSpatialInfo(int[] rows, boolean isOnlySaveSpatialInfo) {
		for (int row : rows) {
			rowDatas.get(row).setOnlySaveSpatialInfo(isOnlySaveSpatialInfo);
		}
	}

	public Boolean isOnlySaveSpatialInfo(int... rows) {
		Boolean result = null;
		for (int row : rows) {
			if (result == null) {
				result = rowDatas.get(row).isOnlySaveSpatialInfo();
			} else {
				if (result != rowDatas.get(row).isOnlySaveSpatialInfo()) {
					result = null;
					break;
				}
			}
		}
		return result;
	}
	//endregion


	//region 是否在属性表中显示
	public void setShowInTabular(int[] rows, boolean isShowInTabular) {
		for (int row : rows) {
			rowDatas.get(row).setShowInTabular(isShowInTabular);
		}
	}

	public Boolean isShowInTabular(int... rows) {
		Boolean result = null;
		for (int row : rows) {
			if (result == null) {
				result = rowDatas.get(row).isShowInTabular();
			} else {
				if (result != rowDatas.get(row).isShowInTabular()) {
					result = null;
					break;
				}
			}
		}
		return result;
	}
	//endregion

	//region 是否在地图中显示
	public void setShowInMap(int[] rows, boolean isShowInMap) {
		for (int row : rows) {
			rowDatas.get(row).setShowInMap(isShowInMap);
		}
	}

	public Boolean isShowInMap(int... rows) {
		Boolean result = null;
		for (int row : rows) {
			if (result == null) {
				result = rowDatas.get(row).isShowInMap();
			} else {
				if (result != rowDatas.get(row).isShowInMap()) {
					result = null;
					break;
				}
			}
		}
		return result;
	}
	//endregion

	//region 是否在场景中显示
	public void setShowInScene(int[] rows, boolean isShowInScene) {
		for (int row : rows) {
			rowDatas.get(row).setShowInScene(isShowInScene);
		}
	}

	public Boolean isShowInScene(int... rows) {
		Boolean result = null;

		for (int row : rows) {
			if (result == null) {
				result = rowDatas.get(row).isShowInScene();
			} else {
				if (result != rowDatas.get(row).isShowInScene()) {
					result = null;
					break;
				}
			}
		}
		return result;
	}

	public Dataset getDataset(int row) {
		return rowDatas.get(row).getCurrentDataset();
	}

	public Recordset queryRecordset(int row, Recordset searchingFeatures) {
		return rowDatas.get(row).queryRecordset(searchingFeatures);
	}

	public boolean isQueryEnable(int row) {
		return rowDatas.get(row).isQueryEnable();
	}

	public void addLayer(Layer layer) {
		if (layer != null && layer.getDataset() != null && supportDatasetTypes.contains(layer.getDataset().getType())) {
			TableRowData tableRowData = new TableRowData(layer);
			rowDatas.add(tableRowData);
			Datasource defaultDatasource = null;
			if (layer.getDataset().getDatasource().isOpened() && !layer.getDataset().getDatasource().isReadOnly()) {
				defaultDatasource = layer.getDataset().getDatasource();
			}
			if (defaultDatasource == null) {
				Datasources datasources = Application.getActiveApplication().getWorkspace().getDatasources();
				for (int i = 0; i < datasources.getCount(); i++) {
					if (datasources.get(i).isOpened() && !datasources.get(i).isReadOnly()) {
						defaultDatasource = datasources.get(i);
						break;
					}
				}
			}
			if (defaultDatasource != null) {

				tableRowData.setResultDatasource(defaultDatasource);
				for (int i = 0; true; i++) {
					String tempDatasetName = i == 0 ? defaultDatasetName : defaultDatasetName + "_" + i;
					String resultName = defaultDatasource.getDatasets().getAvailableDatasetName(tempDatasetName);
					if (tempDatasetName.equalsIgnoreCase(resultName)) {
						tableRowData.setResultDataset(resultName);
						break;
					}
				}
			}
			fireTableRowsInserted(rowDatas.size() - 1, rowDatas.size() - 1);
		}
	}

	public void removeLayer(Layer layer) {
		for (int i = 0; i < rowDatas.size(); i++) {
			TableRowData rowData = rowDatas.get(i);
			if (rowData.getLayer() == layer) {
				rowDatas.remove(rowData);
				fireTableRowsDeleted(i, i);
				break;
			}
		}
	}


	//endregion


//	private String getSuitDatasetName(Datasource datasource, int row) {
//		ArrayList<String> existDatasetNames = new ArrayList<>();
//		TableRowData currentRowData = rowDatas.get(row);
//		String datasetName = currentRowData.getResultDataset();
//		for (TableRowData rowData : rowDatas) {
//			if (rowData != currentRowData && rowData.isSave() && rowData.getResultDatasource() == datasource && !StringUtilities.isNullOrEmpty(rowData.getResultDataset())) {
//				existDatasetNames.add(rowData.getResultDataset());
//			}
//		}
//		return DatasetUtilities.getAvailableDatasetName(datasource, datasetName, existDatasetNames.toArray(new String[existDatasetNames.size()]));
//	}
}
