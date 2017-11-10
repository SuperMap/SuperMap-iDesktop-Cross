package com.supermap.desktop.CtrlAction.Map.MapMeasure;

import com.supermap.desktop.Application;
import com.supermap.desktop.CtrlAction.Map.MapMeasure.Measure.MeasureUtilties;
import com.supermap.desktop.FormMap;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.enums.MeasureType;
import com.supermap.desktop.implement.CtrlAction;

/**
 * Created by ChenS on 2017/11/10 0010.
 */
public class CtrlActionGeodesic extends CtrlAction {
    public CtrlActionGeodesic(IBaseItem caller, IForm formClass) {
        super(caller, formClass);
    }

    @Override
    protected void run() {
        IForm activeForm = Application.getActiveApplication().getActiveForm();
        if (activeForm instanceof FormMap) {
            MeasureUtilties.startMeasure((FormMap) activeForm, MeasureType.Geodesic);
        }
    }

    @Override
    public boolean enable() {
        return Application.getActiveApplication().getActiveForm() instanceof FormMap;
    }
}
