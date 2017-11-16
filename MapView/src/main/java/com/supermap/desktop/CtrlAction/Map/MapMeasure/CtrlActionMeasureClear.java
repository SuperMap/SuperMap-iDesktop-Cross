package com.supermap.desktop.CtrlAction.Map.MapMeasure;

import com.supermap.desktop.Application;
import com.supermap.desktop.CtrlAction.Map.MapMeasure.Measure.Measure;
import com.supermap.desktop.CtrlAction.Map.MapMeasure.Measure.MeasureUtilties;
import com.supermap.desktop.FormMap;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.controls.utilities.ToolbarUIUtilities;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.mapping.TrackingLayer;
import com.supermap.ui.Action;
import com.supermap.ui.TrackMode;

/**
 * Created by Administrator on 2016/1/26.
 */
public class CtrlActionMeasureClear extends CtrlAction {
	public CtrlActionMeasureClear(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public void run() {
		IForm activeForm = Application.getActiveApplication().getActiveForm();
		if (activeForm instanceof FormMap) {
			FormMap formMap = (FormMap) activeForm;
			MeasureUtilties.endMeasure(formMap);
			formMap.getMapControl().setTrackMode(TrackMode.TRACK);
			TrackingLayer trackingLayer = formMap.getMapControl().getMap().getTrackingLayer();
			for (int i = trackingLayer.getCount() - 1; i >= 0; i--) {
                if (trackingLayer.getTag(i).startsWith(Measure.TRACKING_OBJECT_NAME)) {
                    trackingLayer.remove(i);
                }
			}
			formMap.getMapControl().setAction(Action.SELECT2);
			formMap.getMapControl().getMap().refreshTrackingLayer();
			formMap.getMapControl().setTrackMode(TrackMode.EDIT);

			ToolbarUIUtilities.updataToolbarsState();
		}
	}

	@Override
	public boolean enable() {
		IForm activeForm = Application.getActiveApplication().getActiveForm();
		return activeForm != null && activeForm instanceof FormMap && ((FormMap) activeForm).getMapControl().getMap().getTrackingLayer().getCount() > 0;
	}
}
