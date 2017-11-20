package com.supermap.desktop.controls.GeometryPropertyBindWindow;

import com.supermap.data.Dataset;
import com.supermap.data.Recordset;
import com.supermap.desktop.Application;
import com.supermap.desktop.CommonToolkit;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.Interface.IFormMap;
import com.supermap.desktop.Interface.IFormTabular;
import com.supermap.desktop.enums.WindowType;
import com.supermap.desktop.ui.FormManager;

import javax.swing.*;
import java.awt.*;

public class BindUtilties {

	private static IFormTabular tabular;

	public static void windowBindProperty(IFormMap formMap) {
		BindHandler handler = BindHandler.getInstance();
		handler.removeFormMapsAndFormTabularsBind();
		handler.getFormMapList().add(formMap);
		handler.getFormTabularList().add(tabular);
		handler.bindFormMapsAndFormTabulars();
		resetMDILayout();
	}

	public static void resetMDILayout() {
		BindHandler handler = BindHandler.getInstance();
		FormManager formManager = (FormManager) Application.getActiveApplication().getMainFrame().getFormManager();
		int formMapsSize = handler.getFormMapList().size();
		formManager.setLayoutStrategy(new BindLayoutStrategy(formManager, handler));

		if (formMapsSize > 0) {
			Application.getActiveApplication().setActiveForm((IForm) handler.getFormMapList().get(0));
		}
	}

	/**
	 * 创建属性表
	 *
	 * @param dataset
	 * @param recordset
	 */
	public static void openTabular(Dataset dataset, Recordset recordset) {
		// 打开一个默认的属性表，然后修改属性表的title和数据与当前图层对应的数据匹配
		tabular = (IFormTabular) CommonToolkit.FormWrap.fireNewWindowEvent(WindowType.TABULAR,dataset.getName() + "@" + dataset.getDatasource().getAlias());
		tabular.setRecordset(recordset);
	}

	/**
	 * 打开关联浏览窗口
	 *
	 * @param caller
	 */
	public static void showPopumenu(IBaseItem caller) {
		Point point = ((JComponent) caller).getLocationOnScreen();
		int x = (int) point.getX();
		final JPopupMenuBind popupMenuBind = JPopupMenuBind.instance();
		int y = (int) point.getY() + ((JComponent) caller).getHeight();
		JFrame mainFrame = (JFrame) Application.getActiveApplication().getMainFrame();
		popupMenuBind.init();
		popupMenuBind.show(mainFrame, x, y);
		popupMenuBind.setVisible(true);
	}

	public static boolean isFormInBind(IForm activeForm) {
		return BindHandler.getInstance().getFormTabularList().contains(activeForm) || BindHandler.getInstance().getFormMapList().contains(activeForm);
	}

	public static void queryMap(IFormTabular activeForm) {
		BindHandler.getInstance().queryMap(activeForm);
	}
}
