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
    public static String VERSIONS = "versions";
    private String currentVersion;
    private List<String> versions;

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
            this.versions = ComplexPropertyUtilties.union(this.versions, ((LayerCache) layer).getVersions());
            this.currentVersion = ComplexPropertyUtilties.union(this.currentVersion, ((LayerCache) layer).getCurrentVersion());
        }
    }

    private void initializeEnabledMap() {
        this.propertyEnabled.put(VERSIONS, true);
        this.propertyEnabled.put(CURRENT_VERSION, true);
    }

    private void resetProperties() {
        this.currentVersion = null;
        if (getLayers() != null && getLayers().length > 0 && getLayers()[0] != null && !getLayers()[0].isDisposed()) {
            this.currentVersion = ((LayerCache) getLayers()[0]).getCurrentVersion();
            this.versions = ((LayerCache) getLayers()[0]).getVersions();
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
}
