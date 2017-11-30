package com.supermap.desktop.CtrlAction;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.Interface.IFormManager;
import com.supermap.desktop.dialog.homePage.HomePageForm;
import com.supermap.desktop.implement.CtrlAction;

/**
 * Created by yuanR on 2017/11/30 0030.
 * 起始页 CtrlAction
 */
public class CtrlActionHomePage extends CtrlAction {
	private HomePageForm homePageForm;


	public CtrlActionHomePage(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public void run() {

		try {
			if (this.homePageForm == null) {
				this.homePageForm = new HomePageForm();
			}

			Boolean isAdd = true;
			IFormManager formManager = Application.getActiveApplication().getMainFrame().getFormManager();
			for (int i = 0; i < formManager.getCount(); i++) {
				if (formManager.get(i) instanceof HomePageForm) {
					isAdd = false;
					break;
				}
			}

			if (isAdd) {
				formManager.showChildForm(this.homePageForm);
			} else {
				formManager.close(this.homePageForm);

			}

		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}

	}

	@Override
	public boolean enable() {
		return true;
	}
}
