package com.supermap.desktop.iml;

import com.supermap.data.conversion.FileType;
import com.supermap.desktop.Interface.IExportPanelFactory;
import com.supermap.desktop.baseUI.PanelExportTransform;
import com.supermap.desktop.exportUI.DataExportDialog;
import com.supermap.desktop.exportUI.PanelExportTransformForGrid;
import com.supermap.desktop.exportUI.PanelExportTransformForVCT;
import com.supermap.desktop.exportUI.PanelExportTransformForVector;
import com.supermap.desktop.localUtilities.FiletypeUtilities;

import java.util.ArrayList;

/**
 * Created by xie on 2016/10/31.
 */
public class ExportPanelFactory implements IExportPanelFactory {
	public static final int SAMETYPE = 0;
	public static final int GRIDTYPE = 1;
	public static final int VECTORTYPE = 2;
	public static final int GRID_AND_VECTORTYPE = 3;
	public static final int ALL_VCT_TYPE = 4;

	@Override
	public PanelExportTransform createExportPanel(DataExportDialog owner, ExportFileInfo exportsFileInfo) {
		PanelExportTransform result = new PanelExportTransform(exportsFileInfo);
		Object fileType = exportsFileInfo.getFileType();
		if (FiletypeUtilities.isGridType(fileType)) {
			result = new PanelExportTransformForGrid(owner, exportsFileInfo);
		} else if (fileType.equals(FileType.VCT) && FiletypeUtilities.isVectorType(fileType)) {
			// 增加导出为VCT专属信息设置面板
			result = new PanelExportTransformForVCT(exportsFileInfo);
		} else if (FiletypeUtilities.isVectorType(fileType)) {
			result = new PanelExportTransformForVector(exportsFileInfo);

		}
		return result;
	}

	/**
	 * @param owner
	 * @param panelExports
	 * @return
	 */
	@Override
	public PanelExportTransform createExportPanel(DataExportDialog owner, ArrayList<PanelExportTransform> panelExports) {
		PanelExportTransform exportPanel = null;
		// 对选中的项进行是否全为vct类型的判断-yuanR2017.11.27
		if (isAllVCTType(panelExports)) {
			exportPanel = new PanelExportTransformForVCT(panelExports, ALL_VCT_TYPE);
		} else if (isSameType(panelExports)) {
			ExportFileInfo fileInfo = panelExports.get(0).getExportsFileInfo();
			if (FiletypeUtilities.isGridType(fileInfo.getFileType()) && fileInfo.getFileType() != FileType.CSV) {
				exportPanel = new PanelExportTransformForGrid(owner, panelExports, SAMETYPE);
			} else {
				exportPanel = new PanelExportTransformForVector(panelExports, SAMETYPE);
			}
		} else if (isGridTypes(panelExports)) {
			exportPanel = new PanelExportTransformForGrid(owner, panelExports, GRIDTYPE);
		} else if (isVectorTypes(panelExports)) {
			exportPanel = new PanelExportTransformForVector(panelExports, VECTORTYPE);
		} else {
			exportPanel = new PanelExportTransform(panelExports, GRID_AND_VECTORTYPE);
		}
		return exportPanel;
	}


	/**
	 * 判断是否全为VCT类型
	 *
	 * @param panelExports
	 * @return
	 */
	private boolean isAllVCTType(ArrayList<PanelExportTransform> panelExports) {
		boolean isContainVCTType = true;
		int size = panelExports.size();
		for (int i = 0; i < size; i++) {
			if (!FileType.VCT.equals(panelExports.get(i).getExportsFileInfo().getFileType())) {
				return false;
			}
		}
		return isContainVCTType;
	}

	/**
	 * @param panelExports
	 * @return
	 */
	private boolean isSameType(ArrayList<PanelExportTransform> panelExports) {
		boolean isSameType = true;
		ExportFileInfo exportsFileInfo = panelExports.get(0).getExportsFileInfo();
		int size = panelExports.size();
		for (int i = 0; i < size; i++) {
			if (!exportsFileInfo.getFileType().equals(panelExports.get(i).getExportsFileInfo().getFileType())) {
				isSameType = false;
			}
		}
		return isSameType;
	}

	/**
	 * 给是否都为矢量类型增加，是否含有VCT类型的判断，VCT类型属于矢量类型但是其参数面板与其他矢量类型不同
	 *
	 * @param panelExports
	 * @return
	 */
	private boolean isVectorTypes(ArrayList<PanelExportTransform> panelExports) {
		int count = 0;
		for (PanelExportTransform tempPanelExport : panelExports) {
			for (Object tempFileType : FiletypeUtilities.getVectorValue()) {
				if (null != tempPanelExport.getExportsFileInfo().getFileType() && tempPanelExport.getExportsFileInfo().getFileType().equals(tempFileType)
						&& !tempPanelExport.getExportsFileInfo().getFileType().equals(FileType.VCT)) {
					count++;
				}
			}
		}
		return count == panelExports.size();
	}

	public boolean isGridTypes(ArrayList<PanelExportTransform> panelExports) {
		int count = 0;
		for (PanelExportTransform tempPanelExport : panelExports) {
			for (Object tempFileType : FiletypeUtilities.getGridValue()) {
				if (null != tempPanelExport.getExportsFileInfo().getFileType() && tempPanelExport.getExportsFileInfo().getFileType().equals(tempFileType)) {
					count++;
				}
			}
		}
		return count == panelExports.size();
	}
}
