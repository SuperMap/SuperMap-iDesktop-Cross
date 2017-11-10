package com.supermap.desktop.CtrlAction.MapOperator;

import com.supermap.desktop.Application;
import com.supermap.desktop.FormMap;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.Interface.IFormMap;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.mapview.CachePlayer.CachePlayerBar;
import com.supermap.mapping.LayerCache;
import com.supermap.mapping.Layers;

/**
 * Created by ChenS on 2017/11/9 0009.
 */
public class CtrlActionShowPlayer extends CtrlAction {
    public CtrlActionShowPlayer(IBaseItem caller, IForm formClass) {
        super(caller, formClass);
    }

    @Override
    protected void run() {
        CachePlayerBar cachePlayerBar = ((FormMap) Application.getActiveApplication().getActiveForm()).getCachePlayerBar();
        cachePlayerBar.setVisible(!cachePlayerBar.isVisible());
    }

    @Override
    public boolean enable() {
        Layers layers = ((IFormMap) Application.getActiveApplication().getActiveForm()).getMapControl().getMap().getLayers();
        for (int i = 0; i < layers.getCount(); i++) {
            if (layers.get(i) instanceof LayerCache) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean check() {
        return ((FormMap) Application.getActiveApplication().getActiveForm()).getCachePlayerBar().isVisible();
    }
}
