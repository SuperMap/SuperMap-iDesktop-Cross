package com.supermap.desktop.geometryoperation.editor;

import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.geometry.Abstract.IRegionFeature;
import com.supermap.desktop.geometryoperation.EditEnvironment;
import com.supermap.desktop.geometryoperation.control.JDialogFieldOperationSetting;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.utilities.CursorUtilities;
import com.supermap.desktop.utilities.GeometryUtilities;
import com.supermap.desktop.utilities.ListUtilities;
import com.supermap.desktop.utilities.TabularUtilities;
import com.supermap.mapping.Layer;
import com.supermap.mapping.Selection;

import java.util.List;
import java.util.Map;

// @formatter:off
/**
 * 对象编辑—求交。多个对象，两两依次求交。
 * 仅支持面特征的集合对象求交。
 * 不支持二维和三维几何对象混合求交。
 * 选中的对象如果有不支持的对象，直接忽略。
 * 结果对象风格以结果图层为主。
 * 考虑到求交的对象不会有很多，因此实现中历史记录不使用提升性能的批量编辑模式。
 * （历史记录提示性能的批量编辑操作需要以 recordset 整体为单位进行操作，而本功能使用的历史记录批量操作的目的是将多个单独的操作合并为一条历史记录）
 * @author highsad
 *
 */
// @formatter:on
public class IntersectEditor extends AbstractEditor {

	@Override
	public void activate(EditEnvironment environment) {
		try {
			// 设置目标数据集类型
			DatasetType datasetType = DatasetType.CAD;
			if (environment.getEditProperties().getSelectedGeometryTypes().size() == 1) {
				datasetType = DatasetType.REGION;
			}
			JDialogFieldOperationSetting form = new JDialogFieldOperationSetting(CoreProperties.getString("String_OverlayAnalystMethod_Intersect"),
					environment.getMapControl().getMap(), datasetType);
			if (form.showDialog() == DialogResult.OK) {
				CursorUtilities.setWaitCursor(environment.getMapControl());
				intersect(environment, form.getEditLayer(), form.getPropertyData());
				TabularUtilities.refreshTabularStructure((DatasetVector) form.getEditLayer().getDataset());
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		} finally {
			CursorUtilities.setDefaultCursor(environment.getMapControl());
			
			// 结束当前编辑。如果是交互性编辑，environment 会自动管理结束，就无需主动调用。
			environment.activateEditor(NullEditor.INSTANCE);
		}
	}

	// @formatter:off
	/* 
	 * 1.仅支持面面求交，线线求交意义不大，不支持。
	 * 2.不支持三维对象。
	 */
	// @formatter:on
	@Override
	public boolean enble(EditEnvironment environment) {
		boolean enable = false;
		if (environment.getEditProperties().getSelectedGeometryCount() > 1 // 选中数至少2个
				&& ListUtilities.isListOnlyContain(environment.getEditProperties().getSelectedGeometryTypeFeatures(), IRegionFeature.class)
				&& environment.getEditProperties().getEditableDatasetTypes().size() > 0
				&& ListUtilities.isListContainAny(environment.getEditProperties().getEditableDatasetTypes(), DatasetType.CAD, DatasetType.REGION)) {
			enable = true;
		}
		return enable;
	}

	private void intersect(EditEnvironment environment, Layer editLayer, Map<String, Object> propertyData) {
		Geometry result = null;
		Recordset targetRecordset = null;
		environment.getMapControl().getEditHistory().batchBegin();

		try {

			// 对选中数据求交
			List<Layer> selectedLayers = environment.getEditProperties().getSelectedLayers();

			for (Layer layer : selectedLayers) {
				if (layer.getDataset().getType() == DatasetType.CAD || layer.getDataset().getType() == DatasetType.REGION) {
					result = GeometryUtilities.intersetct(result, GeometryUtilities.intersect(layer), true);
				}
			}

			if (editLayer != null) {
				Selection selection = editLayer.getSelection();
				targetRecordset = ((DatasetVector) editLayer.getDataset()).getRecordset(false, CursorType.DYNAMIC);

				// 删除目标图层上的选中几何对象
				targetRecordset.getBatch().begin();
				for (int i = 0; i < selection.getCount(); i++) {
					int id = selection.get(i);
					targetRecordset.seekID(id);
					environment.getMapControl().getEditHistory().add(EditType.DELETE, targetRecordset, true);
					targetRecordset.delete();
				}
				targetRecordset.getBatch().update();

				// 添加结果几何对象
				targetRecordset.addNew(result, propertyData);
				targetRecordset.update();

				// 清空选择集，并选中结果对象
				selection.clear();
				int addedId = targetRecordset.getID();
				if (addedId > -1) {
					selection.add(addedId);
				}
				environment.getMapControl().getEditHistory().add(EditType.ADDNEW, targetRecordset, true);
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		} finally {
			environment.getMapControl().getEditHistory().batchEnd();
			environment.getMapControl().revalidate();

			if (result != null) {
				result.dispose();
			}

			if (targetRecordset != null) {
				targetRecordset.close();
				targetRecordset.dispose();
			}
		}
	}
}
