package com.supermap.desktop.CtrlAction.CreateLayoutElements;

import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.ui.Action;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/30
 * Time: 14:12
 * Description:
 */
public class CtrlActionCreateMapScale extends CtrlActionCreateElementBase{

	public CtrlActionCreateMapScale(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public Action getAction() {
		return Action.CREATE_MAPSCALE;
	}
}
