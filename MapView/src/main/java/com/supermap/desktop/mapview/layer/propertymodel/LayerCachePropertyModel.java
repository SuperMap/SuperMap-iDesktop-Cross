package com.supermap.desktop.mapview.layer.propertymodel;

import com.supermap.desktop.Interface.IFormMap;
import com.supermap.mapping.Layer;
import com.supermap.mapping.LayerCache;

import java.util.List;

/**
 * Created by ChenS on 2017/11/8 0008.
 */
public class LayerCachePropertyModel extends LayerPropertyModel {
    public static String CURRENT_VERSION = "currentVersion";
    public static String CURRENT_DESCRIPTION = "currentDescription";
    public static String VERSIONS = "versions";
    public static String DESCRIPTIONS = "descriptions";
    private String currentDescription;
    private String currentVersion;
    private List<String> versions;
    private List<String> descriptions;

    public LayerCachePropertyModel() {
    }

    public LayerCachePropertyModel(Layer[] layers, IFormMap formMap) {
        super(layers, formMap);
        initializeProperties(layers);
    }

    private void initializeProperties(Layer[] layers) {
        resetProperties();
        initializeEnabledMap();

        for (Layer layer : layers) {
            if (layer == null || layer.isDisposed()) {
                break;
            }
            LayerCache layerCache = (LayerCache) layer;
            this.versions = ComplexPropertyUtilties.union(this.versions, layerCache.getVersions());
            this.descriptions = ComplexPropertyUtilties.union(this.descriptions, layerCache.getDescriptions());
            this.currentVersion = ComplexPropertyUtilties.union(this.currentVersion, layerCache.getCurrentVersion());
            for (int i = 0; i < layerCache.getVersions().size(); i++) {
                if (layerCache.getVersions().get(i).equals(currentVersion)) {
                    this.currentDescription = ComplexPropertyUtilties.union(this.currentDescription, layerCache.getDescriptions().get(i));
                }
            }
        }
    }

    private void initializeEnabledMap() {
        this.propertyEnabled.put(VERSIONS, true);
        this.propertyEnabled.put(CURRENT_VERSION, true);
        this.propertyEnabled.put(CURRENT_DESCRIPTION, true);
        this.propertyEnabled.put(DESCRIPTIONS, true);
    }

    private void resetProperties() {
        this.currentVersion = null;
        if (getLayers() != null && getLayers().length > 0 && getLayers()[0] != null && !getLayers()[0].isDisposed()) {
            LayerCache layerCache = (LayerCache) getLayers()[0];
            this.currentVersion = (layerCache).getCurrentVersion();
            this.versions = (layerCache).getVersions();
            this.descriptions = (layerCache).getDescriptions();
            for (int i = 0; i < (layerCache).getVersions().size(); i++) {
                if (layerCache.getVersions().get(i).equals(currentVersion)) {
                    this.currentDescription = layerCache.getDescriptions().get(i);
                }
            }
        }
    }

    @Override
    protected void apply(Layer layer) {
        if (this.propertyEnabled.get(CURRENT_VERSION) && this.currentVersion != null) {
            ((LayerCache) layer).setCurrentVersion(currentVersion);
        }
    }

    @Override
    public void setProperties(LayerPropertyModel model) {
        LayerCachePropertyModel cachePropertyModel = (LayerCachePropertyModel) model;
        if (cachePropertyModel != null) {
            this.currentVersion = cachePropertyModel.getCurrentVersion();
            this.versions = cachePropertyModel.getVersions();
            this.descriptions = cachePropertyModel.getDescriptions();
            this.currentDescription = cachePropertyModel.getCurrentDescription();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String version) {
        this.currentVersion = version;
    }

    public List<String> getVersions() {
        return versions;
    }

    public void setVersions(List<String> versions) {
        this.versions = versions;
    }

    public String getCurrentDescription() {
        return currentDescription;
    }

    public void setCurrentDescription(String currentDescription) {
        this.currentDescription = currentDescription;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<String> descriptions) {
        this.descriptions = descriptions;
    }
}
