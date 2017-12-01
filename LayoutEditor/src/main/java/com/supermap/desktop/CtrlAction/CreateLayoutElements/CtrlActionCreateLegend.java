package com.supermap.desktop.CtrlAction.CreateLayoutElements;

import com.supermap.data.*;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.ui.UICommonToolkit;
import com.supermap.layout.LayoutElements;
import com.supermap.ui.Action;
import com.supermap.ui.MapLayoutControl;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/29
 * Time: 17:56
 * Description:
 */
public class CtrlActionCreateLegend extends CtrlActionCreateElementBase {

	public CtrlActionCreateLegend(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public Action getAction() {
		return Action.CREATE_GEOLEGEND;
	}

}
