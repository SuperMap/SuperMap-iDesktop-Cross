package com.supermap.desktop.CtrlAction;

import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.utilities.SkinUtilties;
import org.pushingpixels.substance.api.skin.CeruleanSkin;

/**
 * @author XiaJT
 */
public class CtrlActionSkinDefault extends CtrlAction {
	public CtrlActionSkinDefault(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	protected void run() {
		SkinUtilties.setSkin(new CeruleanSkin());
	}

	@Override
	public boolean enable() {
		return super.enable();
	}
}
