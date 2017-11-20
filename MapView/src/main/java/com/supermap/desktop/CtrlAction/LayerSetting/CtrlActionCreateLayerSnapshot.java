package com.supermap.desktop.CtrlAction.LayerSetting;

import com.supermap.desktop.Application;
import com.supermap.desktop.FormMap;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.ui.UICommonToolkit;
import com.supermap.desktop.ui.trees.LayersTree;

/**
 * Created by lixiaoyao on 2017/10/31.
 */
public class CtrlActionCreateLayerSnapshot extends CtrlAction {

	public CtrlActionCreateLayerSnapshot(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public void run() {
		IForm iForm= Application.getActiveApplication().getActiveForm();
		if (iForm instanceof FormMap){
			FormMap formMap=(FormMap)iForm;
			String layerSnapshotName=formMap.getMapControl().getMap().getLayers().getAvailableCaption("SnapshotLayer");
			formMap.getMapControl().getMap().getLayers().insertLayerSnapshot(formMap.getMapControl().getMap().getLayers().getCount(),layerSnapshotName);
			LayersTree layersTree = UICommonToolkit.getLayersManager().getLayersTree();
			int selectRow=layersTree.getRowCount()-1;
//			layersTree.setSelectionRow(selectRow);
			formMap.setActiveLayers(formMap.getMapControl().getMap().getLayers().get(layerSnapshotName));
			layersTree.startEditingAtPath(layersTree.getPathForRow(selectRow));
		}
	}

	@Override
	public boolean enable() {
		if(UICommonToolkit.getLayersManager().getLayersTree().getMap().getLayers().getCount()>0){
			return true;
		}else{
			return false;
		}
	}
}
