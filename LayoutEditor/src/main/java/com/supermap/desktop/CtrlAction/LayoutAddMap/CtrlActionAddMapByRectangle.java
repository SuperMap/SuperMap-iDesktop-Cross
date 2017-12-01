package com.supermap.desktop.CtrlAction.LayoutAddMap;

import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.ui.*;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/29
 * Time: 15:46
 * Description:
 */
public class CtrlActionAddMapByRectangle extends CtrlActionAddMapBase {

	public CtrlActionAddMapByRectangle(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public Action getAction() {
		return Action.CREATERECTANGLE;
	}
}
