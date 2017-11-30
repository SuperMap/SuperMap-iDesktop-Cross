package com.supermap.desktop.CtrlAction.CreateLayoutElements;

import com.supermap.data.GeoMap;
import com.supermap.data.Geometry;
import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.Interface.IFormLayout;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.layout.LayoutSelection;
import com.supermap.ui.*;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/29
 * Time: 17:57
 * Description:
 */
public class CtrlActionCreateElementBase extends CtrlAction {

	public CtrlActionCreateElementBase(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	public Action getAction() {
		return Action.NULL;
	}

	@Override
	public void run() {
		try {
			IFormLayout formLayout = (IFormLayout) Application.getActiveApplication().getActiveForm();
			if (formLayout.getMapLayoutControl().getTrackMode() != TrackMode.EDIT) {
				formLayout.getMapLayoutControl().setTrackMode(TrackMode.EDIT);
				formLayout.getMapLayoutControl().setLayoutAction(getAction());
			} else if (formLayout.getMapLayoutControl().getLayoutAction() == getAction()) {
				formLayout.getMapLayoutControl().setLayoutAction(Action.SELECT2);
			} else {
				formLayout.getMapLayoutControl().setLayoutAction(getAction());
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	@Override
	public boolean enable() {
		Boolean enable = false;
		try {
			IFormLayout formLayout = (IFormLayout) Application.getActiveApplication().getActiveForm();
			if (formLayout != null) {
				LayoutSelection layoutSelection = formLayout.getMapLayoutControl().getMapLayout().getSelection();
				if (layoutSelection.getCount() == 1 && formLayout.getMapLayoutControl().getActiveGeoMapID() == -1) {
					Geometry geoSelection = formLayout.getFirstSelectedGeometry();
					if (geoSelection != null && geoSelection instanceof GeoMap) {
						enable = true;
					}
				}
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
		return enable;
	}

	@Override
	public boolean check() {
		boolean checkState = true;
		try {
			IFormLayout formLayout = (IFormLayout) Application.getActiveApplication().getActiveForm();
			if (formLayout != null && formLayout.getMapLayoutControl().getLayoutAction() == getAction()) {
				checkState = false;
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
		return checkState;
	}
}
