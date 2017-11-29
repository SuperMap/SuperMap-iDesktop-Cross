package com.supermap.desktop.process.parameter.ipls;

import com.supermap.data.*;
import com.supermap.desktop.process.constraint.annotation.ParameterField;
import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.interfaces.AbstractParameter;
import com.supermap.desktop.process.parameter.interfaces.ISelectionParameter;
import com.supermap.desktop.properties.CoreProperties;

import java.beans.PropertyChangeEvent;

/**
 * Created by xie on 2017/2/16.
 */
public class ParameterSingleDataset extends AbstractParameter implements ISelectionParameter {

	public static final String DATASET_FIELD_NAME = "value";
	public static final String DATASETTYPES_FIELD_NAME = "datasetTypes";
	public static final String DATASOURCE_FIELD_NAME = "datasource";

	@ParameterField(name = DATASET_FIELD_NAME)
	private Dataset selectedItem;
	//	@ParameterField(name = DATASET_TYPES_FIELD_NAME) not ready
	private DatasetType[] datasetTypes;
	private PixelFormat[] pixelFormats = null;//栅格数据集像素类型
	@ParameterField(name = DATASOURCE_FIELD_NAME)
	private Datasource datasource;
	private String describe = CoreProperties.getString(CoreProperties.Label_Dataset);
	private DatasourceClosingListener datasourceClosingListener = new DatasourceClosingListener() {
		@Override
		public void datasourceClosing(DatasourceClosingEvent datasourceClosingEvent) {
			if (datasourceClosingEvent.getDatasource() == ParameterSingleDataset.this.datasource) {
				setDatasource(null);
			}
		}
	};

	/**
	 * 是否为必填参数和是否显示未空值有关
	 *
	 * @return
	 */
	@Override
	public boolean isRequired() {
		return !this.isShowNullValue;
	}

	public ParameterSingleDataset(DatasetType... datasetTypes) {
		this.datasetTypes = datasetTypes;
	}

	@Override
	public void setSelectedItem(Object item) {
		Dataset oldValue = null;
		if (item == null) {
			oldValue = this.selectedItem;
			this.selectedItem = null;
		} else if (item instanceof Dataset) {
			oldValue = this.selectedItem;
			this.selectedItem = (Dataset) item;
			setDatasource(selectedItem.getDatasource());
		}
		firePropertyChangeListener(new PropertyChangeEvent(this, DATASET_FIELD_NAME, oldValue, selectedItem));
	}

	@Override
	public Dataset getSelectedItem() {
		return selectedItem;
	}

	public Dataset getSelectedDataset() {
		return selectedItem;
	}

	@Override
	public String getType() {
		return ParameterType.SINGLE_DATASET;
	}


	@Override
	public void dispose() {

	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	@Override
	public String getDescription() {
		return describe;
	}

	public void setDatasource(Datasource datasource) {
		if (this.datasource != datasource) {
			if (this.datasource != null) {
				try {
					this.datasource.getWorkspace().getDatasources().removeClosingListener(this.datasourceClosingListener);
				} catch (Exception e) {
					// 对象已被释放，无视之
				}
			}
			Datasource oldValue = this.datasource;
			this.datasource = datasource;
			firePropertyChangeListener(new PropertyChangeEvent(this, DATASOURCE_FIELD_NAME, oldValue, this.datasource));


			if (this.datasource != null) {
				this.datasource.getWorkspace().getDatasources().addClosingListener(this.datasourceClosingListener);
			}
		}
	}

	public Datasource getDatasource() {
		return datasource;
	}

	public DatasetType[] getDatasetTypes() {
		return datasetTypes;
	}

	public void setDatasetTypes(DatasetType... datasetTypes) {
		this.datasetTypes = datasetTypes;
	}

	private boolean isShowNullValue = false;

	public ParameterSingleDataset setShowNullValue(boolean isShowNullValue) {
		this.isShowNullValue = isShowNullValue;
		return this;
	}

	public boolean isShowNullValue() {
		return isShowNullValue;
	}

	public PixelFormat[] getPixelFormat() {
		return pixelFormats;
	}

	public void setPixelFormat(PixelFormat[] pixelFormats) {
		this.pixelFormats = pixelFormats;
	}
}
