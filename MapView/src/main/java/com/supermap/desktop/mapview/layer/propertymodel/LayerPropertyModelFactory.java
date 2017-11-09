package com.supermap.desktop.mapview.layer.propertymodel;

import com.supermap.data.DatasetVector;
import com.supermap.desktop.Interface.IFormMap;
import com.supermap.mapping.*;

import java.util.ArrayList;

public class LayerPropertyModelFactory {

	private static final int LAYER_NONE = 0;
	private static final int LAYER_UNKNOWN = 1;
	private static final int LAYER_VECTOR = 2;
	private static final int LAYER_GRID = 4;
	private static final int LAYER_IMAGE = 8;
    private static final int LAYER_GROUP = 16;
    private static final int LAYER_HEATMAP = 32;
    private static final int LAYER_GRID_AGGREGATION=64;
    private static final int LAYER_CACHE = 128;

	private LayerPropertyModelFactory() {
		// 工具类，不提供构造方法
	}

	public static LayerPropertyModel[] getLayerPropertyModels(Layer[] layers, IFormMap formMap) {
		int layerType = LAYER_NONE;
		ArrayList<LayerPropertyModel> models = new ArrayList<LayerPropertyModel>();

		for (Layer layer : layers) {
			if (layer == null || layer.isDisposed()) {
				continue;
			}
			if (layer instanceof LayerGroup) {
                layerType = LAYER_GROUP;
                break;
            } else if (layer instanceof LayerHeatmap) {
                layerType |= LAYER_HEATMAP;
            } else if (layer instanceof LayerCache) {
                layerType |= LAYER_CACHE;
            } else if (layer instanceof LayerGridAggregation) {
                layerType |= LAYER_GRID_AGGREGATION;
            } else if (layer.getAdditionalSetting() != null) {
                if (layer.getAdditionalSetting().getType() == LayerSettingType.VECTOR) {
                    layerType |= LAYER_VECTOR;
                } else if (layer.getAdditionalSetting().getType() == LayerSettingType.GRID) {
                    layerType |= LAYER_GRID;
                } else if (layer.getAdditionalSetting().getType() == LayerSettingType.IMAGE) {
                    layerType |= LAYER_IMAGE;
                } else {
                    layerType |= LAYER_UNKNOWN;
                }
            } else if (layer.getTheme() != null) {
                if (layer.getDataset() instanceof DatasetVector) {
                    layerType |= LAYER_VECTOR;
                }

            }
        }

		if (layerType == LAYER_VECTOR) {
			models.add(new LayerBasePropertyModel(layers, formMap));
			models.add(new LayerRelocateDatasetPropertyModel(layers, formMap));
			models.add(new LayerVectorParamPropertyModel(layers, formMap));
		} else if (layerType == LAYER_IMAGE) {
			models.add(new LayerBasePropertyModel(layers, formMap));
			models.add(new LayerRelocateDatasetPropertyModel(layers, formMap));
			models.add(new LayerImageParamPropertyModel(layers, formMap));
			if (isContainDatasetImageCollection(layers)) {
				models.add(new LayerStretchOptionPropertyModel(layers, formMap));
			}
		} else if (layerType == LAYER_GRID) {
			models.add(new LayerBasePropertyModel(layers, formMap));
			models.add(new LayerRelocateDatasetPropertyModel(layers, formMap));
			models.add(new LayerGridParamPropertyModel(layers, formMap));
        } else if (layerType == LAYER_GROUP) {
            models.add(new LayerBasePropertyModel(layers, formMap));
            models.add(new LayerCachePropertyModel(layers, formMap));
        } else if (layerType == LAYER_CACHE) {
            models.add(new LayerBasePropertyModel(layers, formMap));
            //TODO
        } else if (layerType == LAYER_HEATMAP) {
            models.add(new LayerBasePropertyModel(layers, formMap));
            models.add(new LayerRelocateDatasetPropertyModel(layers, formMap));
            models.add(new LayerHeatmapPropertyModel(layers, formMap));
        } else if (layerType == LAYER_GRID_AGGREGATION) {
            models.add(new LayerBasePropertyModel(layers, formMap));
            models.add(new LayerRelocateDatasetPropertyModel(layers, formMap));
            models.add(new LayerGridAggregationPropertyModel(layers, formMap));
        } else {
            models.add(new LayerBasePropertyModel(layers, formMap));
            models.add(new LayerRelocateDatasetPropertyModel(layers, formMap));
        }
        return models.toArray(new LayerPropertyModel[models.size()]);
	}

	/**
	 * 组件不支持影像数据集集合图层拉伸，暂时在这里处理一下
	 *
	 * @param layers 影像数据集图层集合
	 * @return 时候包含影像数据集集合
	 */
	// FIXME: 2016/4/14  组件不支持影像数据集集合图层拉伸，暂时在这里处理一下
	private static boolean isContainDatasetImageCollection(Layer[] layers) {
		for (Layer layer : layers) {
			if (((LayerSettingImage) layer.getAdditionalSetting()).getImageStretchOption() == null) {
				return false;
			}
		}
		return true;
	}
}
