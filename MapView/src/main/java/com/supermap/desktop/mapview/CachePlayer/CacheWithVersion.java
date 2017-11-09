package com.supermap.desktop.mapview.CachePlayer;

import com.supermap.mapping.LayerCache;

/**
 * Created by ChenS on 2017/11/9 0009.
 */
public class CacheWithVersion {
    private LayerCache layerCache;
    private String version;

    public CacheWithVersion(LayerCache layerCache, String version) {
        this.layerCache = layerCache;
        this.version = version;
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
}
