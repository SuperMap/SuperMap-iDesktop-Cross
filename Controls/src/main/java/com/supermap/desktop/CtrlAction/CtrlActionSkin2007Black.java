package com.supermap.desktop.CtrlAction;

import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.utilities.SkinUtilties;
import org.pushingpixels.substance.api.skin.GraphiteGoldSkin;

/**
 * @author XiaJT
 */
public class CtrlActionSkin2007Black extends CtrlAction {
	public CtrlActionSkin2007Black(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	protected void run() {
		SkinUtilties.setSkin(new GraphiteGoldSkin());
	}
}
