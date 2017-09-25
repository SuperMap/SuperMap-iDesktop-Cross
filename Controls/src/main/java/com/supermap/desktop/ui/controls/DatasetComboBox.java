package com.supermap.desktop.ui.controls;

import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.controls.ControlDefaultValues;
import com.supermap.desktop.controls.utilities.JComboBoxUIUtilities;
import com.supermap.desktop.implement.DefaultComboBoxUI;
import com.supermap.desktop.ui.controls.CellRenders.ListDataCellRender;

import javax.swing.*;
import java.awt.event.ItemEvent;

/**
 * 数据集下拉列表控件
 *
 * @author YuanR 2017.2.14
 * <p>
 * 提供的构造方法：
 * 默认构造空的ComboBox（可以通过setDatasets（）方法重新填充，并支持setSupportedDatasetTypes（））
 * 数据集集合类构造（通过  DatasetComboBox(Datasets datasets) 进行构造）
 * <p>
 * 可获得的参数：
 * SupportedDatasetTypes、SelectedDataset、构建的datasets
 * <p>
 * 重构ComboBox的实现方式，直接将数据集存入item，再通过setRenderer(new ListDataCellRender());进行显示及图标的渲染
 */

public class DatasetComboBox extends JComboBox<Dataset> {

    private static final long serialVersionUID = 1L;
    private transient DatasetType[] datasetTypes;
    private transient Datasets datasets;
    private boolean isFireItemListener = true;
    private boolean isShowNullValue = false;
    private PixelFormat[] pixelFormats = null;

    private DatasetCreatedListener datasetCreatedListener = new DatasetCreatedListener() {
        @Override
        public void datasetCreated(DatasetCreatedEvent datasetCreatedEvent) {
            checkDatasetComboBox();
        }
    };

    private DatasetDeletedListener datasetDeletedListener = new DatasetDeletedListener() {
        @Override
        public void DatasetDeleted(DatasetDeletedEvent datasetDeletedEvent) {
            checkDatasetComboBox();
        }
    };

    private DatasetRenamedListener datasetRenamedListener = new DatasetRenamedListener() {
        @Override
        public void datasetRenamed(DatasetRenamedEvent datasetRenamedEvent) {
            checkDatasetComboBox();
        }
    };

    private DatasetDeletedAllListener datasetDeletedAllListener = new DatasetDeletedAllListener() {
        @Override
        public void datasetDeletedAll(DatasetDeletedAllEvent datasetDeletedAllEvent) {
            checkDatasetComboBox();
        }
    };

    /**
     * 覆盖原有的updateUI方法
     * 2016.12.26
     */
    @Override
    public void updateUI() {
        this.setUI(new DefaultComboBoxUI());
    }

    /**
     * 默认构造一个空的下来列表框
     */
    public DatasetComboBox() {
        this.setBorder(BorderFactory.createEtchedBorder(1));
        setRenderer(new ListDataCellRender());
        this.setPreferredSize(ControlDefaultValues.DEFAULT_PREFERREDSIZE);
    }

    /**
     * 根据给定的数据集集合类创建下拉选择框
     *
     * @param datasets
     */
    public DatasetComboBox(Datasets datasets) {
        super(initDatasetComboBoxItem(datasets));
        changeDatasets(datasets);
        //设置渲染方式
        this.setBorder(BorderFactory.createEtchedBorder(1));
        setRenderer(new ListDataCellRender());
        this.setPreferredSize(ControlDefaultValues.DEFAULT_PREFERREDSIZE);
    }

    /**
     * @param datasets
     * @return
     */
    private static Dataset[] initDatasetComboBoxItem(Datasets datasets) {
        Dataset[] result = new Dataset[datasets.getCount()];
        for (int i = 0; i < datasets.getCount(); i++) {
            result[i] = datasets.get(i);
        }
        return result;
    }

    /**
     * 选中指定数据集的项
     *
     * @param dataset
     */
    public void setSelectedDataset(Dataset dataset) {
        int selectIndex = 0;
        if (dataset != null) {
            for (int i = 0; i < getItemCount(); i++) {
                Dataset ComboBoxDataset = getItemAt(i);
                if (ComboBoxDataset == dataset) {
                    selectIndex = i;
                    break;
                }
            }
        }
        setSelectedIndex(selectIndex);
    }

    /**
     * 设置数据集集合
     *
     * @param datasets
     */
    public void setDatasets(Datasets datasets) {
        changeDatasets(datasets);
        updateItems();
    }

    @Override
    protected void fireItemStateChanged(ItemEvent e) {
        if (isFireItemListener) {
            super.fireItemStateChanged(e);
        }
    }

    /**
     * 数据集改变时需要检查一下
     */
    private void checkDatasetComboBox() {
        Dataset selectItem = getSelectedDataset();
        isFireItemListener = false;
        try {
            updateItems();
            this.setSelectedDataset(selectItem);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isFireItemListener = true;
        }
    }

    /**
     * datasets改变后事件需要移除和添加
     *
     * @param datasets
     */
    private void changeDatasets(Datasets datasets) {
        removeListener(this.datasets);
        this.datasets = datasets;
        addListeners(this.datasets);
    }

    private void removeListener(Datasets datasets) {
        if (datasets != null) {
            datasets.removeCreatedListener(datasetCreatedListener);
            datasets.removeDeletedListener(datasetDeletedListener);
            datasets.removeRenamedListener(datasetRenamedListener);
            datasets.removeDeletedAllListener(datasetDeletedAllListener);
        }
    }

    private void addListeners(Datasets datasets) {
        if (datasets != null) {
            datasets.addCreatedListener(datasetCreatedListener);
            datasets.addDeletedListener(datasetDeletedListener);
            datasets.addRenamedListener(datasetRenamedListener);
            datasets.addDeletedAllListener(datasetDeletedAllListener);
        }
    }


    /**
     * 设置支持的数据集类型
     *
     * @param datasetTypes
     */
    public void setSupportedDatasetTypes(DatasetType[] datasetTypes) {
        this.datasetTypes = datasetTypes;
        updateItems();
    }

    public void setPixelFormats(PixelFormat[] pixelFormats) {
        this.pixelFormats = pixelFormats;
        updateItems();
    }

    /**
     * 更改设置之后，更新组合框的子项
     */
    public void updateItems() {
        boolean isFireItemListener = this.isFireItemListener;
        try {
            Dataset selectedDataset = getSelectedDataset();
            this.removeAllItems();
            if (this.datasets != null) {
                try {
                    if (isShowNullValue) {
                        this.addItem(null);//添加首项为空
                    }
                    for (int i = 0; i < this.datasets.getCount(); i++) {
                        Dataset dataset = this.datasets.get(i);
                        DatasetType type = dataset.getType();
                        if (this.getSupportedDatasetTypes() != null && this.getSupportedDatasetTypes().length > 0 && !isSupportDatasetType(type)) {
                            continue;
                        } else if (dataset instanceof DatasetGrid && this.pixelFormats != null) {
                            //需要筛选栅格数据集像素类型的
                            for (PixelFormat pixelFormat:pixelFormats) {
                                if (pixelFormat.equals(((DatasetGrid) dataset).getPixelFormat())) {
                                    this.addItem(dataset);
                                }
                            }
                        } else {
                            this.addItem(dataset);
                        }
                    }
                    if (!isShowNullValue && selectedDataset != null && JComboBoxUIUtilities.getItemIndex(this, selectedDataset) != -1) {
                        this.isFireItemListener = false;
                        setSelectedDataset(selectedDataset);
                    } else {
                        this.setSelectedIndex(0);
                    }
                } catch (Exception ex) {
                    return;
                }
            }
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        } finally {
            this.isFireItemListener = isFireItemListener;
        }
    }


    /**
     * 由于填充的是DatasetCell 返回时要得到DatasetCell中JLabel显示的字符串
     *
     * @return
     */
    public String getSelectItem() {
        if (getSelectedIndex() == -1) {
            return null;
        }
        Dataset temp = (Dataset) getSelectedItem();
        return temp.getName();
    }

    /**
     * 获取选中的数据集
     *
     * @return
     */
    public Dataset getSelectedDataset() {
        Dataset result = null;
        if (getSelectedItem() instanceof Dataset) {
            Dataset selected = (Dataset) getSelectedItem();
            if (selected instanceof Dataset) {
                result = selected;
            }
        }
        return result;
    }

    /**
     * 获得支持的数据集类型
     *
     * @return
     */
    public DatasetType[] getSupportedDatasetTypes() {
        return this.datasetTypes;
    }

    /**
     * 获得构造ComboBox的数据集集合类
     *
     * @return
     */
    public Datasets getDatasets() {
        return this.datasets;
    }

    /**
     * 根据传入的数据集类型判断此类型是否支持显示
     *
     * @param type 数据集类型
     * @return
     */
    private boolean isSupportDatasetType(DatasetType type) {
        boolean isSupport = false;
        try {
            if (this.datasetTypes == null || this.datasetTypes.length == 0) {
                isSupport = true;
            } else {
                for (DatasetType datasetType : this.datasetTypes) {
                    if (datasetType == type) {
                        isSupport = true;
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        }
        return isSupport;
    }

    /**
     * 判断ComboBox中是否含有此名称的数据集
     *
     * @param datasetName
     * @return
     */
    public boolean hasDataset(String datasetName) {
        for (int i = 0; i < this.getItemCount(); i++) {
            if (this.getDatasetAt(i).getName().equals(datasetName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置ComboBox选中的数据集为该数据集
     *
     * @param datasetName
     */
    public void setSelectedDataset(String datasetName) {
        for (int i = 0; i < this.getItemCount(); i++) {
            if (this.getDatasetAt(i).getName().equals(datasetName)) {
                this.setSelectedIndex(i);
                return;
            }
        }
    }

    /**
     * 通过数据集名称移除ComboBox中该item
     *
     * @param datasetName
     */
    public void removeDataset(String datasetName) {
        for (int i = 0; i < this.getItemCount(); i++) {
            if (this.getDatasetAt(i).getName().equals(datasetName)) {
                this.removeItem(this.getItemAt(i));
                repaint();
            }
        }
    }

    /**
     * 移除ComboBox中传入的该数据集item
     *
     * @param currentDataset
     */
    public void removeDataset(Dataset currentDataset) {
        removeDataset(currentDataset.getName());
    }

    /**
     * 通过item次序获得数据集
     *
     * @param index
     * @return
     */
    public Dataset getDatasetAt(int index) {
        Dataset dataset = null;
        if (index >= 0 && index < this.getItemCount() && this.getItemAt(index) instanceof Dataset) {
            dataset = this.getItemAt(index);
        }
        return dataset;
    }

    /**
     * 增加一数据集
     */
    public void addItemAt(int index, Dataset item) {
        ((DefaultComboBoxModel<Dataset>) this.getModel()).insertElementAt(item, index);
    }

    public boolean addDataset(Dataset dataset) {
        if (dataset == null) {
            return false;
        }
        DatasetType type = dataset.getType();
        if (datasetTypes == null) {
            if (!this.datasets.contains(dataset.getName())) {
                addItem(dataset);
            }
            return true;
        }
        for (DatasetType datasetType : datasetTypes) {
            if (type == datasetType) {
                if (!this.datasets.contains(dataset.getName())) {
                    addItem(dataset);
                }
                return true;
            }
        }
        return false;
    }

    public boolean isShowNullValue() {
        return isShowNullValue;
    }

    public void setShowNullValue(boolean showNullValue) {
        isShowNullValue = showNullValue;
    }
}
