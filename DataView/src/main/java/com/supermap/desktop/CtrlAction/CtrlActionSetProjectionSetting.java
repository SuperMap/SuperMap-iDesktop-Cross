package com.supermap.desktop.CtrlAction;

import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.Datasource;
import com.supermap.data.PrjCoordSys;
import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.dataview.DataViewProperties;
import com.supermap.desktop.dialog.JDialogConfirm;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.ui.controls.prjcoordsys.JDialogPrjCoordSysSettings;
import com.supermap.desktop.utilities.DatasetUtilities;
import com.supermap.desktop.utilities.PropertyManagerUtilities;

import java.text.MessageFormat;

/**
 * 坐标系设置
 *
 * @author XiaJT
 */
public class CtrlActionSetProjectionSetting extends CtrlAction {

	public CtrlActionSetProjectionSetting(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public void run() {
		Dataset[] datasets = Application.getActiveApplication().getActiveDatasets();
		Datasource[] datasources = Application.getActiveApplication().getActiveDatasources();
		PrjCoordSys prjCoordSys = null;
		// 实现对话框title说明坐标系的来源-yuanR2017.11.15
		String titleName = "";
		// 数据集
		if (datasets.length > 0) {
			for (Dataset dataset : datasets) {
				if (dataset.getType() != DatasetType.TABULAR) {
					prjCoordSys = dataset.getPrjCoordSys();
					titleName = ControlsProperties.getString("String_Label_Dataset") + dataset.getName() + "@" + dataset.getDatasource().getAlias();
					break;
				}
			}
		} else if (datasources.length > 0) {
			// 数据源
			prjCoordSys = datasources[0].getPrjCoordSys();
			titleName = ControlsProperties.getString("String_Label_Datasource") + datasources[0].getAlias();
		}

		JDialogPrjCoordSysSettings dialogPrjCoordSysSettings = new JDialogPrjCoordSysSettings(titleName);

		if (prjCoordSys != null) {
			dialogPrjCoordSysSettings.setPrjCoordSys(prjCoordSys);
		}

		if (dialogPrjCoordSysSettings.showDialog() == DialogResult.OK) {
			// 修改
			PrjCoordSys newPrjCoordSys = dialogPrjCoordSysSettings.getPrjCoordSys();
			if (newPrjCoordSys != null) {
				if (datasets.length > 0) {
					for (Dataset dataset : datasets) {
						if (dataset.getType() != DatasetType.TABULAR) {
							dataset.setPrjCoordSys(newPrjCoordSys);
							Application
									.getActiveApplication()
									.getOutput()
									.output(MessageFormat.format(DataViewProperties.getString("String_DatasetPrjCoordSysSuccessful"), dataset.getName(),
											newPrjCoordSys.getName()));
						}
					}
				} else {
					boolean isDontAskSetToAllDatasets = false;
					boolean isSetToAllDatasets = false;
					boolean isDontAskCloseDatasets = false;
					boolean isCloseDatasets = false;
					for (Datasource datasource : datasources) {
						datasource.setPrjCoordSys(newPrjCoordSys);

						if (!isDontAskSetToAllDatasets) {
							// 提示是否设置到所有数据集
							JDialogConfirm dialogConfirm = new JDialogConfirm(MessageFormat.format(ControlsProperties.getString("String_ApplyPrjCoordSys"),
									datasource.getAlias()), true);
							dialogConfirm.showDialog();
							isDontAskSetToAllDatasets = dialogConfirm.isUsedAsDefault();
							if (dialogConfirm.getDialogResult() == DialogResult.OK) {
								isSetToAllDatasets = true;
							} else {
								isSetToAllDatasets = false;
							}
						}

						if (isSetToAllDatasets) {
							// 先判断数据集是否打开
							boolean isDatasetOpened = false;
							for (int i = 0; i < datasource.getDatasets().getCount(); i++) {
								if (DatasetUtilities.isDatasetOpened(datasource.getDatasets().get(i))) {
									isDatasetOpened = true;
									break;
								}
							}
							if (isDatasetOpened) {
								if (!isDontAskCloseDatasets) {
									// 有数据集打开,提示是否关闭数据集
									JDialogConfirm dialogConfirm = new JDialogConfirm(MessageFormat.format(ControlsProperties.getString("String_DatasetNotClosed"),
											datasource.getAlias()), true);
									dialogConfirm.showDialog();
									isDontAskCloseDatasets = dialogConfirm.isUsedAsDefault();
									if (dialogConfirm.getDialogResult() == DialogResult.OK) {
										isCloseDatasets = true;
									} else {
										isCloseDatasets = false;
									}
								}

								if (isCloseDatasets) {
									// 关闭数据集
									DatasetUtilities.closeDataset(datasource.getDatasets());
								}
							}
							if (!isDatasetOpened) {
								// 数据集关闭或本来就没打开
								for (int i = 0; i < datasource.getDatasets().getCount(); i++) {
									datasource.getDatasets().get(i).setPrjCoordSys(newPrjCoordSys);
								}
							}
							Application
									.getActiveApplication()
									.getOutput()
									.output(MessageFormat.format(DataViewProperties.getString("String_DatasourcePrjCoordSysSuccessful"), datasource.getAlias(),
											newPrjCoordSys.getName()));
						}
					}
				}
			}
			PropertyManagerUtilities.refreshPropertyManager();
		}
	}

	@Override
	public boolean enable() {
		if (null != Application.getActiveApplication().getActiveDatasets() && null != Application.getActiveApplication().getActiveDatasources()) {
			Dataset[] datasets = Application.getActiveApplication().getActiveDatasets();
			Datasource[] datasources = Application.getActiveApplication().getActiveDatasources();

			// 选中数据源且不含只读数据源
			if (datasources.length > 0) {
				for (Datasource datasource : datasources) {
					if (datasource.isReadOnly()) {
						return false;
					}
				}
				return true;
			}

			// 选中数据集且存在不为属性表的数据集
			if (datasets.length > 0) {
				for (Dataset dataset : datasets) {
					if (DatasetType.TABULAR != dataset.getType() && !dataset.isReadOnly()) {
						return true;
					}
				}
				return false;
			}

		}
		return false;
	}
}
