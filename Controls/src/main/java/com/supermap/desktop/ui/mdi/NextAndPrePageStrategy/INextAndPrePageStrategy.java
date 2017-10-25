package com.supermap.desktop.ui.mdi.NextAndPrePageStrategy;

import com.supermap.desktop.ui.mdi.MdiGroup;
import com.supermap.desktop.ui.mdi.plaf.feature.IMdiFeature;

import java.util.List;

/**
 * Created by lixiaoyao on 2017/10/16.
 */
public interface INextAndPrePageStrategy {

	public int getFirstVisibleTabIndex();

	public int getLastVisibleTabIndex();

	public void resetVisibleIndex(MdiGroup mdiGroup, int effectiveWidth, List<IMdiFeature> features, int firstVisibleTabIndex,
	                              int lastVisibleTabIndex, int tabGap, boolean isActivePageChanged);
}
