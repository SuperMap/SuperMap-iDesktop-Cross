package com.supermap.desktop.CtrlAction.LayoutAddMap;

import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.ui.Action;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/30
 * Time: 14:01
 * Description:
 */
public class CtrlActionAddMapByEllipse extends CtrlActionAddMapBase {

	public CtrlActionAddMapByEllipse(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public Action getAction() {
		return Action.CREATEELLIPSE;
	}
}
