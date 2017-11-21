package com.supermap.desktop.mapview.CachePlayer;

import com.supermap.desktop.Application;
import com.supermap.desktop.FormMap;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.ControlsResources;
import com.supermap.desktop.dialog.DialogCachePlayerSetting;
import com.supermap.desktop.mapview.MapViewProperties;
import com.supermap.desktop.ui.controls.progress.RoundProgressBar;
import com.supermap.desktop.utilities.CoreResources;
import com.supermap.mapping.Layer;
import com.supermap.mapping.LayerCache;
import com.supermap.mapping.Layers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ChenS on 2017/11/9 0009.
 */
public class CachePlayerBar extends JToolBar {
    private static Icon ICON_PLAY = ControlsResources.getIcon("/controlsresources/CachePlayerBar/Image_play.png");
    private static Icon ICON_STOP = ControlsResources.getIcon("/controlsresources/CachePlayerBar/Image_stop.png");

    private FormMap formMap;
    private ArrayList<LayerCache> layerCaches;
    private ArrayList<CacheWithVersion> playList;
    private int interval = 3;
    private int index = 0;
    private boolean isEffects = false;
    private int effectsInterval = 3;

    private Timer timer;
    private JButton buttonPrevious;
    private JButton buttonNext;
    private JButton buttonPlay;
    private JButton buttonSetting;
    private RoundProgressBar progressBar;

    //region Listener
    private ActionListener nextListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttonPlay.getIcon().equals(ICON_PLAY)) {
                index++;
                if (index == playList.size()) {
                    index = 0;
                }
                CacheWithVersion cacheWithVersion = playList.get(index);
                cacheWithVersion.getLayerCache().setCurrentVersion(cacheWithVersion.getVersion());
                formMap.getMapControl().getMap().refresh();
                formMap.setActiveLayers(cacheWithVersion.getLayerCache());
                progressBar.setProgress(index);
            } else {
                if (index < playList.size()) {
                    timer.cancel();
                    play();
                }
            }
        }
    };
    private ActionListener previousListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttonPlay.getIcon().equals(ICON_PLAY)) {
                index--;
                if (index < 0) {
                    index = playList.size() - 1;
                }
                CacheWithVersion cacheWithVersion = playList.get(index);
                cacheWithVersion.getLayerCache().setCurrentVersion(cacheWithVersion.getVersion());
                formMap.getMapControl().getMap().refresh();
                formMap.setActiveLayers(cacheWithVersion.getLayerCache());
                progressBar.setProgress(index);
            } else {
                if (index > 0) {
                    index = index - 2;
                    timer.cancel();
                    play();
                }
            }
        }
    };
    private ActionListener playListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttonPlay.getIcon().equals(ICON_PLAY)) {
                index--;
                play();
            } else {
                timer.cancel();
                backToInitialState();
            }
        }
    };
    private ActionListener settingListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (timer != null) {
                timer.cancel();
            }
            backToInitialState();
            DialogCachePlayerSetting dialogCachePlayerSetting = new DialogCachePlayerSetting(((CachePlayerBar) ((JButton) e.getSource()).getParent()));
            dialogCachePlayerSetting.showDialog();
        }
    };
    //endregion

    public CachePlayerBar(FormMap formMap) {
        this.formMap = formMap;
        initComponent();
        initLayerCaches();
        registerListener();
    }

    private void initComponent() {
        buttonPrevious = new JButton();
        buttonNext = new JButton();
        buttonPlay = new JButton();
        buttonSetting = new JButton();
        progressBar = new RoundProgressBar();

        buttonPrevious.setIcon(ControlsResources.getIcon("/controlsresources/CachePlayerBar/Image_Back.png"));
        buttonNext.setIcon(ControlsResources.getIcon("/controlsresources/CachePlayerBar/Image_ForWard.png"));
        buttonPlay.setIcon(ICON_PLAY);
        buttonSetting.setIcon(CoreResources.getIcon("/coreresources/ToolBar/Image_ToolButton_Setting.PNG"));

        buttonPrevious.setToolTipText(MapViewProperties.getString("String_Previous"));
        buttonNext.setToolTipText(MapViewProperties.getString("String_Next"));
        buttonPlay.setToolTipText(MapViewProperties.getString("String_Play"));
        buttonSetting.setToolTipText(ControlsProperties.getString("String_Button_Setting"));

        progressBar.setPreferredSize(new Dimension(formMap.getPreferredSize().width / 2, 30));
        progressBar.setMinimumSize(new Dimension(formMap.getPreferredSize().width / 2, 30));
        progressBar.setMaximumSize(new Dimension(formMap.getPreferredSize().width / 2, 30));

        this.add(buttonPrevious);
        this.add(progressBar);
        this.add(buttonNext);
        this.add(buttonPlay);
        this.add(buttonSetting);
    }

    private void registerListener() {
        buttonPrevious.addActionListener(previousListener);
        buttonNext.addActionListener(nextListener);
        buttonPlay.addActionListener(playListener);
        buttonSetting.addActionListener(settingListener);
    }

    private void initLayerCaches() {
        layerCaches = new ArrayList<>();
        playList = new ArrayList<>();
        Layers layers = formMap.getMapControl().getMap().getLayers();
        for (int i = 0; i < layers.getCount(); i++) {
            if (layers.get(i) instanceof LayerCache) {
                LayerCache layerCache = (LayerCache) layers.get(i);
                layerCaches.add(layerCache);
                for (int j = 0; j < layerCache.getVersions().size(); j++) {
                    playList.add(new CacheWithVersion(layerCache, layerCache.getVersions().get(j), layerCache.getDescriptions().get(j)));
                }
            }
        }
        progressBar.setMaximumProgress(playList.size() - 1);
        this.setVisible(!layerCaches.isEmpty());
    }

    public void updateLayerCaches(Layer layer, boolean isAdd) {
        if (layer instanceof LayerCache) {
            LayerCache layerCache = (LayerCache) layer;
            if (isAdd) {
                layerCaches.add(layerCache);
                for (int j = 0; j < layerCache.getVersions().size(); j++) {
                    playList.add(new CacheWithVersion(layerCache, layerCache.getVersions().get(j), layerCache.getDescriptions().get(j)));
                }
            } else {
                layerCaches.remove(layer);
                Layers layers = formMap.getMapControl().getMap().getLayers();
                for (int i = 0; i < layers.getCount(); i++) {
                    if (layers.get(i) instanceof LayerCache) {
                        LayerCache cache = (LayerCache) layers.get(i);
                        layerCaches.add(cache);
                        for (int j = 0; j < cache.getVersions().size(); j++) {
                            playList.add(new CacheWithVersion(cache, cache.getVersions().get(j), cache.getDescriptions().get(j)));
                        }
                    }
                }
            }
            setVisible(!layerCaches.isEmpty());
            progressBar.setMaximumProgress(playList.size() - 1);
        }
    }

    private void backToInitialState() {
        index = 0;
        progressBar.setProgress(0);
        buttonPlay.setIcon(ICON_PLAY);
        try {
            LayerCache layerCache;
            if (playList.size() > 0) {
                layerCache = playList.get(0).getLayerCache();
            } else {
                layerCache = layerCaches.get(0);
            }
            layerCache.setCurrentVersion(layerCaches.get(0).getVersions().get(0));
            formMap.getMapControl().getMap().refresh();
            formMap.setActiveLayers(layerCache);
        } catch (Exception e) {
            Application.getActiveApplication().getOutput().output(e);
        }
    }

    private void play() {
        buttonPlay.setIcon(ICON_STOP);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                index = index + 1;
                if (playList.size() > 0 && index < playList.size()) {
                    CacheWithVersion cacheWithVersion = playList.get(index);
                    cacheWithVersion.getLayerCache().setCurrentVersion(cacheWithVersion.getVersion());
                    formMap.getMapControl().getMap().refresh();
                    formMap.setActiveLayers(cacheWithVersion.getLayerCache());
                    progressBar.setProgress(index);
                } else {
                    cancel();
                    backToInitialState();
                }
            }
        }, 0, interval * 1000);
    }

    //region Getter&Setter
    public void setEffectsTime(int effectsInterval) {
        this.effectsInterval = effectsInterval;
//        for (LayerCache layerCache : layerCaches) {
//            layerCache.setEffectsTime(effectsTime);
//        }
    }

    public void setEffects(boolean isEffects) {
        this.isEffects = isEffects;
        for (LayerCache layerCache : layerCaches) {
            layerCache.setEffectsEnable(isEffects);
        }
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public boolean isEffects() {
        return isEffects;
    }

    public int getEffectsInterval() {
        return effectsInterval;
    }

    public ArrayList<CacheWithVersion> getPlayList() {
        return playList;
    }

    public RoundProgressBar getProgressBar() {
        return progressBar;
    }

    public ArrayList<LayerCache> getLayerCaches() {
        return layerCaches;
    }
    //endregion
}
