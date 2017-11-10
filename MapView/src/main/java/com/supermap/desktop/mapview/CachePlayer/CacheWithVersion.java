package com.supermap.desktop.mapview.CachePlayer;

import com.supermap.mapping.LayerCache;

/**
 * Created by ChenS on 2017/11/9 0009.
 */
public class CacheWithVersion {
    private LayerCache layerCache;
    private String version;
    private String description;

    public CacheWithVersion(LayerCache layerCache, String description) {
        this.layerCache = layerCache;
        this.description = description;
        for (int i = 0; i < layerCache.getVersions().size(); i++) {
            if (layerCache.getDescriptions().get(i).equals(description)) {
                this.version = layerCache.getVersions().get(i);
                break;
            }
        }
    }

    public CacheWithVersion(LayerCache layerCache, String version, String description) {
        this.layerCache = layerCache;
        this.version = version;
        this.description = description;
    }

    public LayerCache getLayerCache() {
        return layerCache;
    }

    public void setLayerCache(LayerCache layerCache) {
        this.layerCache = layerCache;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
