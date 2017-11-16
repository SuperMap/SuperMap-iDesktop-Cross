package com.supermap.desktop.CtrlAction.Map.MapMeasure;

import com.supermap.data.PrjCoordSysType;
import com.supermap.desktop.Application;
import com.supermap.desktop.CtrlAction.Map.MapMeasure.Measure.MeasureGeodesic;
import com.supermap.desktop.CtrlAction.Map.MapMeasure.Measure.MeasureUtilties;
import com.supermap.desktop.FormMap;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.enums.MeasureType;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.ui.MapControl;

import java.util.HashMap;

/**
 * Created by ChenS on 2017/11/10 0010.
 */
public class CtrlActionGeodesic extends CtrlAction {
    public static HashMap<MapControl, MeasureGeodesic> hashMap;
    public CtrlActionGeodesic(IBaseItem caller, IForm formClass) {
        super(caller, formClass);
    }

    @Override
    protected void run() {
        IForm activeForm = Application.getActiveApplication().getActiveForm();
        if (activeForm instanceof FormMap) {
            MeasureUtilties.startMeasure((FormMap) activeForm, MeasureType.Geodesic, null);
        }
    }

    @Override
    public boolean enable() {
        IForm activeForm = Application.getActiveApplication().getActiveForm();
        return activeForm instanceof FormMap && ((FormMap) activeForm).getMapControl().getMap().getPrjCoordSys().getType()
                == PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE;
    }
}
