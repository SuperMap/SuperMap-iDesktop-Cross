package com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels;

import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.ControlsResources;
import com.supermap.desktop.ui.controls.prjcoordsys.prjCoordSysSettingPanels.CoordSysDefine;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * Created by yuanR on 2017/11/8 0008.
 */
public class DefaultCoordsysTreeCellRenderer extends DefaultTreeCellRenderer {
	/**
	 * ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 重写父类DefaultTreeCellRenderer的方法
	 */
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
	                                              boolean sel, boolean expanded, boolean leaf, int row,
	                                              boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		//得到每个节点的TreeNode
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		if (node.getUserObject() instanceof CoordSysDefine) {

			if (((CoordSysDefine) node.getUserObject()).getCoordSysType() == CoordSysDefine.GEOGRAPHY_COORDINATE && !((CoordSysDefine) node.getUserObject()).getIsFolderNode()) {
				// 设置节点显示为地理坐标系的节点
				this.setIcon(ControlsResources.getIcon("/controlsresources/Projection/geoCoordsysFile.png"));
			} else if (((CoordSysDefine) node.getUserObject()).getCoordSysType() == CoordSysDefine.PROJECTION_SYSTEM && !((CoordSysDefine) node.getUserObject()).getIsFolderNode()) {
				// 设置节点显示为投影坐标系的节点
				this.setIcon(ControlsResources.getIcon("/controlsresources/Projection/prjCoordsysFile.png"));
			} else if (((CoordSysDefine) node.getUserObject()).getCoordSysType() == CoordSysDefine.NONE_EARTH && !((CoordSysDefine) node.getUserObject()).getIsFolderNode()) {
				// 设置节点显示为平面坐标系的节点
				this.setIcon(ControlsResources.getIcon("/controlsresources/Projection/noneEarthFile.png"));
			} else if (((CoordSysDefine) node.getUserObject()).getIsFolderNode() && ((CoordSysDefine) node.getUserObject()).getCaption().equals(ControlsProperties.getString("String_PrjCoorSys"))) {
				this.setIcon(ControlsResources.getIcon("/controlsresources/Projection/prjCoordsys.png"));
			} else if (((CoordSysDefine) node.getUserObject()).getIsFolderNode() && ((CoordSysDefine) node.getUserObject()).getCaption().equals(ControlsProperties.getString("String_GeoCoordSys"))) {
				this.setIcon(ControlsResources.getIcon("/controlsresources/Projection/geoCoordsys.png"));
			} else if (((CoordSysDefine) node.getUserObject()).getIsFolderNode() && ((CoordSysDefine) node.getUserObject()).getCaption().equals(ControlsProperties.getString("String_NoneEarth"))) {
				this.setIcon(ControlsResources.getIcon("/controlsresources/Projection/noneEarthCoordsys.png"));
			} else if (((CoordSysDefine) node.getUserObject()).getIsFolderNode() && ((CoordSysDefine) node.getUserObject()).getCaption().equals(ControlsProperties.getString("String_Custom"))) {
				this.setIcon(ControlsResources.getIcon("/controlsresources/Projection/folder.png"));
			} else if (((CoordSysDefine) node.getUserObject()).getIsFolderNode() && ((CoordSysDefine) node.getUserObject()).getCaption().equals(ControlsProperties.getString("String_Favorite"))) {
				this.setIcon(ControlsResources.getIcon("/controlsresources/Projection/favorite.png"));
			} else {
				this.setIcon(ControlsResources.getIcon("/controlsresources/Projection/folder.png"));
			}
		}
		return this;
	}

}

