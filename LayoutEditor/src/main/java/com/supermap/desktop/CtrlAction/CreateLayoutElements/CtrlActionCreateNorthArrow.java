package com.supermap.desktop.CtrlAction.CreateLayoutElements;

import com.supermap.data.GeoNorthArrow;
import com.supermap.data.Geometry;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.layout.LayoutElements;
import com.supermap.ui.*;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/29
 * Time: 17:27
 * Description:
 */
public class CtrlActionCreateNorthArrow extends CtrlActionCreateElementBase {

	public CtrlActionCreateNorthArrow(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public Action getAction() {
		return Action.CREATE_NORTHARROW;
	}
}
