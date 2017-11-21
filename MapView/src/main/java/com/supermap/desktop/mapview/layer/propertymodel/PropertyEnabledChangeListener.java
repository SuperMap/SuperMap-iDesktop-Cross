package com.supermap.desktop.mapview.layer.propertymodel;

import java.util.EventListener;

public interface PropertyEnabledChangeListener extends EventListener {
    void propertyEnabledChange(PropertyEnabledChangeEvent e);
}
