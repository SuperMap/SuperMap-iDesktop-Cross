package com.supermap.desktop.layoutview.propertycontrols;

import java.util.EventListener;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/28
 * Time: 15:27
 * Description:
 */
public interface ChangedListener extends EventListener {
	void changed(ChangedEvent e);
}
