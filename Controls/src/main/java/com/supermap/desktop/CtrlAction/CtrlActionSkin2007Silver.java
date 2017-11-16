package com.supermap.desktop.CtrlAction;

import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.utilities.SkinUtilties;
import org.pushingpixels.substance.api.skin.OfficeSilver2007Skin;

/**
 * @author XiaJT
 */
public class CtrlActionSkin2007Silver extends CtrlAction {
	public CtrlActionSkin2007Silver(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	protected void run() {
		SkinUtilties.setSkin(new OfficeSilver2007Skin());
	}
}
