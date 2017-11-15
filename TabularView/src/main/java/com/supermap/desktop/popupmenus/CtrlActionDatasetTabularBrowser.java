package com.supermap.desktop.popupmenus;

import com.supermap.data.Dataset;
import com.supermap.data.DatasetVector;
import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.utilities.TabularUtilities;

public class CtrlActionDatasetTabularBrowser extends CtrlAction {

	public CtrlActionDatasetTabularBrowser(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public void run() {
		try {
			Dataset[] activeDatasets = Application.getActiveApplication().getActiveDatasets();
			for (Dataset activeDataset : activeDatasets) {
				if (activeDataset instanceof DatasetVector) {
					TabularUtilities.openDatasetVectorFormTabular(activeDataset);
				}
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	@Override
	public boolean enable() {
		Dataset[] activeDatasets = Application.getActiveApplication().getActiveDatasets();
		for (Dataset activeDataset : activeDatasets) {
			if (activeDataset instanceof DatasetVector) {
				return true;
			}
		}
		return false;
	}

}
