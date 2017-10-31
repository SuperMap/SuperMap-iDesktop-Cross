package com.supermap.desktop.ui.mdi.NextAndPrePageStrategy;

import com.supermap.desktop.ui.mdi.MdiGroup;
import com.supermap.desktop.ui.mdi.plaf.feature.IMdiFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixiaoyao on 2017/10/16.
 * <p>
 * 计算显示个数：只需要考虑显示范围，如何都都能摆放下，那就都放下
 */
public class LayoutVisibleIndexStrategy implements INextAndPrePageStrategy {

	private int firstVisibleTabIndex = 0;
	private int lastVisibleTabIndex = 0;
	private int effectiveWidth = 0;
	private List<IMdiFeature> features = new ArrayList<>();
	private int tabGap = 0;

	@Override
	public int getFirstVisibleTabIndex() {
		return this.firstVisibleTabIndex;
	}

	@Override
	public int getLastVisibleTabIndex() {
		return this.lastVisibleTabIndex;
	}

	@Override
	public void resetVisibleIndex(MdiGroup mdiGroup, int effectiveWidth, List<IMdiFeature> features, int firstVisibleTabIndex,
	                              int lastVisibleTabIndex, int tabGap, boolean isActivePageChanged) {
		this.firstVisibleTabIndex = firstVisibleTabIndex;
		this.lastVisibleTabIndex = lastVisibleTabIndex;
		this.effectiveWidth = effectiveWidth;
		this.features = features;
		this.tabGap = tabGap;

		if (mdiGroup != null && this.features.size() > 0 && this.effectiveWidth > 0) {
			if (this.features.size() > 1) {
				this.lastVisibleTabIndex = this.features.size() - 1;
			} else if (this.features.size() == 1) {
				this.lastVisibleTabIndex = 0;
				this.firstVisibleTabIndex = 0;
				return;
			}

			processVisibleLastIndex();
			// fix by lixiaoyao 2017/10/19
			// 当进行了激活操作，如果激活页面处于lastIndex的后面，那么就将lastIndex赋值为ActivePageIndex,并以新的lastIndex重新
			// 计算firstVisibleIndex
			// 如果激活页面处于firstIndex的前面，那么就将firstIndex赋值为ActivePageIndex,并以新的firstIndex重新计算lastVisibleIndex
			if (isActivePageChanged) {
				if (mdiGroup.getActivePageIndex() > this.lastVisibleTabIndex) {
					this.lastVisibleTabIndex = mdiGroup.getActivePageIndex();
					processVisibleFirstIndex();
				} else if (mdiGroup.getActivePageIndex() < this.firstVisibleTabIndex) {
					this.firstVisibleTabIndex = mdiGroup.getActivePageIndex();
					processVisibleLastIndex();
				}
			}
		} else {
			this.firstVisibleTabIndex = 0;
			this.lastVisibleTabIndex = 0;
		}
	}

	// 从 firstIndex 往后遍历计算宽度，直至所有的 Features 摆放完毕或者 sum 总宽度超过 effectiveWidth
	// 算出一个可见的lastIndex
	private void processVisibleLastIndex() {
		int sum = 0;
		for (int i = this.firstVisibleTabIndex; i < this.features.size(); i++) {
			IMdiFeature childFeature = this.features.get(i);
			sum += sum == 0 ? childFeature.getWidth() : childFeature.getWidth() + this.tabGap;

			if (sum > this.effectiveWidth) {
				this.lastVisibleTabIndex = i - 1;
				break;
			}
		}
	}

	//从 lastIndex 从后往前遍历计算宽度，直至所有的 Features 摆放完毕或者 sum 总宽度超过 effectiveWidth
	private void processVisibleFirstIndex() {
		int sum = 0;
		for (int i = this.lastVisibleTabIndex; i >= 0; i--) {
			IMdiFeature childFeature = this.features.get(i);
			sum += sum == 0 ? childFeature.getWidth() : childFeature.getWidth() + this.tabGap;

			if (sum > this.effectiveWidth) {
				this.firstVisibleTabIndex = i + 1;
				break;
			}
		}
	}
}
