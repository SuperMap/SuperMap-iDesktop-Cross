package com.supermap.desktop.CtrlAction.LayerSetting;

import com.supermap.desktop.Application;
import com.supermap.desktop.FormMap;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.ui.UICommonToolkit;
import com.supermap.desktop.ui.trees.LayersTree;

/**
 * Created by lixiaoyao on 2017/10/11.
 */
public class CtrlActionCreateRootLayerGroup extends CtrlAction {

	public CtrlActionCreateRootLayerGroup(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public void run() {
		IForm iForm=Application.getActiveApplication().getActiveForm();
		if (iForm instanceof FormMap){
			FormMap formMap=(FormMap)iForm;
			String layerGroupName=formMap.getMapControl().getMap().getLayers().getAvailableCaption("LayerGroup");
			formMap.getMapControl().getMap().getLayers().addGroup(layerGroupName);
			LayersTree layersTree = UICommonToolkit.getLayersManager().getLayersTree();
			int selectRow=layersTree.getRowCount()-1;
//			layersTree.setSelectionRow(selectRow);
//			layersTree.clearSelection();
			formMap.setActiveLayers(formMap.getMapControl().getMap().getLayers().get(layerGroupName));
			layersTree.startEditingAtPath(layersTree.getPathForRow(selectRow));
		}
	}

	@Override
	public boolean enable() {
		return true;
	}
}
