package com.supermap.desktop.CtrlAction.Map.MapMeasure;

import com.supermap.data.DatasetGrid;
import com.supermap.desktop.Application;
import com.supermap.desktop.CtrlAction.Map.MapMeasure.Measure.MeasureUtilties;
import com.supermap.desktop.FormMap;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.enums.MeasureType;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.mapping.Layer;

/**
 * Created by ChenS on 2017/11/10 0010.
 */
public class CtrlActionMeasureSurfaceDistance extends CtrlAction {
    public CtrlActionMeasureSurfaceDistance(IBaseItem caller, IForm formClass) {
        super(caller, formClass);
    }

    @Override
    protected void run() {
        IForm activeForm = Application.getActiveApplication().getActiveForm();
        if (activeForm instanceof FormMap) {
            MeasureUtilties.startMeasure((FormMap) activeForm, MeasureType.Distance_Surface);
        }
    }

    @Override
    public boolean enable() {
        IForm activeForm = Application.getActiveApplication().getActiveForm();
        if (activeForm instanceof FormMap) {
            for (Layer layer : ((FormMap) activeForm).getActiveLayers()) {
                if (layer.getDataset() instanceof DatasetGrid) {
                    return true;
                }
            }
        }
        return false;
    }
}
