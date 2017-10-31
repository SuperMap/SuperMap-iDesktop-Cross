package com.supermap.desktop.CtrlAction.LayerSetting;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.Interface.IFormMap;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.ui.UICommonToolkit;
import com.supermap.mapping.Layer;

public class CtrlActionClearVisibleScale extends CtrlAction {

	public CtrlActionClearVisibleScale(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	public void run() {
		try {
			IFormMap formMap = (IFormMap) Application.getActiveApplication().getActiveForm();
			if (null != formMap) {
				for(Layer currentLayer : formMap.getActiveLayers()){
					currentLayer.setMaxVisibleScale(0);
					currentLayer.setMinVisibleScale(0);
				}
			}
			formMap.getMapControl().getMap().refresh();
			// 手动触发更改事件
			formMap.setActiveLayers(formMap.getActiveLayers());
            for (Layer layer : formMap.getActiveLayers()) {
                UICommonToolkit.getLayersManager().getLayersTree().refreshNode(layer);
            }
        } catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

}
