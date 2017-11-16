package com.supermap.desktop.CtrlAction.LayerSetting;

import com.supermap.desktop.Application;
import com.supermap.desktop.FormMap;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.ui.UICommonToolkit;
import com.supermap.desktop.ui.trees.LayersTree;
import com.supermap.desktop.ui.trees.NodeDataType;
import com.supermap.desktop.ui.trees.TreeNodeData;
import com.supermap.mapping.Layer;
import com.supermap.mapping.LayerGroup;
import com.supermap.mapping.Layers;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;

/**
 * Created by lixiaoyao on 2017/10/19.
 */
public class CtrlActionUnLayerGroup extends CtrlAction {
	private LayerGroup selectedLayerGroup[] =null;

	public CtrlActionUnLayerGroup(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public void run() {
		IForm iForm = Application.getActiveApplication().getActiveForm();
		if (this.selectedLayerGroup != null && iForm != null && iForm instanceof FormMap) {
			FormMap formMap = (FormMap) iForm;
			ArrayList<Layer> selectLayers=new ArrayList<>();
			Layer selectLayer=null;
			int firstIndex=Short.MAX_VALUE;
			int lastIndex=0;
			LayerGroup parentGroup=this.selectedLayerGroup[0].getParentGroup();
			Layers currentLayers=formMap.getMapControl().getMap().getLayers();
			if (parentGroup!=null) {
				for (int i = 0; i < this.selectedLayerGroup.length; i++) {
					int tempIndex=parentGroup.indexOf(this.selectedLayerGroup[i]);
					firstIndex=firstIndex>tempIndex? tempIndex:firstIndex;
					lastIndex=lastIndex<tempIndex? tempIndex:lastIndex;
				}
				if (lastIndex==parentGroup.getCount()-1){
					if (firstIndex==0){
						if (this.selectedLayerGroup.length==parentGroup.getCount()) {
							selectLayer = parentGroup;
						}else{
							selectLayer=null;
						}
					}else{
						selectLayer=parentGroup.get(firstIndex-1);
					}
				}else{
					selectLayer=parentGroup.get(lastIndex+1);
				}
			}else{
				for (int i = 0; i < this.selectedLayerGroup.length; i++) {
					int tempIndex=currentLayers.indexOf(this.selectedLayerGroup[i].getName());
					firstIndex=firstIndex>tempIndex? tempIndex:firstIndex;
					lastIndex=lastIndex<tempIndex? tempIndex:lastIndex;
				}
				if (lastIndex==currentLayers.getCount()-1){
					if (firstIndex==0){
						if (this.selectedLayerGroup.length==currentLayers.getCount()) {
							selectLayer = null;
						}else{
							selectLayer=null;
						}
					}else{
						selectLayer=currentLayers.get(firstIndex-1);
					}
				}else{
					selectLayer=currentLayers.get(lastIndex+1);
				}
			}

			for (int i = 0; i<this.selectedLayerGroup.length; i++) {
				LayerGroup layerGroup = this.selectedLayerGroup[i];
				if (layerGroup.getCount()>0){
					for (int j=0;j<layerGroup.getCount();j++){
						selectLayers.add(layerGroup.get(j));
					}
				}
				layerGroup.ungroup();
			}
			if (selectLayers.size()>0){
				Layer[] temp=new Layer[selectLayers.size()];
				selectLayers.toArray(temp);
				if (temp.length>1){
					formMap.setActiveLayers(temp[0]);
				}
				formMap.setActiveLayers(temp);
			}else {
				if (parentGroup==null){
					if (selectLayer!=null){
						formMap.setActiveLayers(selectLayer);
					}else if (currentLayers.getCount()>0){
						formMap.setActiveLayers(currentLayers.get(currentLayers.getCount()-1));
					}
				}else{
					if (selectLayer!=null){
						formMap.setActiveLayers(selectLayer);
					}else if (parentGroup.getCount()>0){
						formMap.setActiveLayers(parentGroup.get(parentGroup.getCount()-1));
					}
				}
			}
		}
	}

	@Override
	public boolean enable() {
		boolean enable = false;
		LayersTree layersTree = UICommonToolkit.getLayersManager().getLayersTree();
		if (layersTree != null &&layersTree.getSelectionPaths()!=null) {
			TreePath treePath[]= layersTree.getSelectionPaths();
			this.selectedLayerGroup =new LayerGroup[treePath.length];

			for (int i=0;i<treePath.length;i++){
				DefaultMutableTreeNode selectedTreeNode = (DefaultMutableTreeNode)treePath[i].getLastPathComponent();
				TreeNodeData selectedTreeNodeData=(TreeNodeData) selectedTreeNode.getUserObject();
				if (selectedTreeNodeData.getType() != NodeDataType.LAYER_GROUP && selectedTreeNodeData.getType()!=NodeDataType.LAYER_SNAPSHOT) {
					break;
				}else{
					this.selectedLayerGroup[i]=(LayerGroup)selectedTreeNodeData.getData();
					if (i==treePath.length-1){
						enable=true;
					}
				}
			}
		}
		if (!enable) {
			this.selectedLayerGroup = null;
		}
		return enable;
	}
}
