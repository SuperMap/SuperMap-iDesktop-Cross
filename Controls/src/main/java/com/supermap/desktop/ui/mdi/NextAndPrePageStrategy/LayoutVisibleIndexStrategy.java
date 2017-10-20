package com.supermap.desktop.ui.mdi.NextAndPrePageStrategy;

import com.supermap.desktop.ui.mdi.MdiGroup;
import com.supermap.desktop.ui.mdi.plaf.feature.IMdiFeature;

import java.util.List;

/**
 * Created by lixiaoyao on 2017/10/16.
 * <p>
 * 计算显示个数：只需要考虑显示范围，如何都都能摆放下，那就都放下
 */
public class LayoutVisibleIndexStrategy implements INextAndPrePageStrategy {

	private int firstVisibleTabIndex = 0;
	private int lastVisibleTabIndex = 0;

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
	                              int lastVisibleTabIndex, int tabGap) {
		this.firstVisibleTabIndex = firstVisibleTabIndex;
		this.lastVisibleTabIndex = lastVisibleTabIndex;
		if (mdiGroup != null && features.size() > 0 && effectiveWidth > 0) {
			this.lastVisibleTabIndex = features.size() - 1;
			int sum = 0;

			// 从 firstIndex 往后遍历计算宽度，直至所有的 Features 摆放完毕或者 sum 总宽度超过 effectiveWidth
			for (int i = this.firstVisibleTabIndex; i < features.size(); i++) {
				IMdiFeature childFeature = features.get(i);
				sum += sum == 0 ? childFeature.getWidth() : childFeature.getWidth() + tabGap;

				if (sum > effectiveWidth) {
					this.lastVisibleTabIndex = i - 1;
					break;
				}
			}

			// 如果 tabs 区域可以摆放的下，那么就全部显示出来。
			// 如果 lastVisibleTabIndex 已经是最后一个了，那么就说明可用的 tabs 区域还有发挥空间，就从 startIndex 往前继续运算
			if (this.lastVisibleTabIndex == features.size() && this.firstVisibleTabIndex > 0) {
				for (int i = this.firstVisibleTabIndex - 1; i >= 0; i--) {
					IMdiFeature childFeature = features.get(i);
					sum += sum == 0 ? childFeature.getWidth() : childFeature.getWidth() + tabGap;

					if (sum > effectiveWidth && i < this.firstVisibleTabIndex) {
						this.firstVisibleTabIndex = i + 1;
						break;
					}
				}
			}
		} else {
			this.firstVisibleTabIndex = 0;
			this.lastVisibleTabIndex = 0;
		}
	}
}
