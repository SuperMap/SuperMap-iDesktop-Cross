package com.supermap.desktop.ui.mdi.NextAndPrePageStrategy;

import com.supermap.desktop.Application;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.ui.mdi.MdiGroup;
import com.supermap.desktop.ui.mdi.plaf.feature.IMdiFeature;

import java.util.List;

/**
 * Created by lixiaoyao on 2017/10/16.
 * <p>
 * 限制显示个数：无论是从前向后还是从后向前计算显示个数，如果算的个数没超过限制个数，并且没超出显示范围，那么就都摆下；
 * 如果算的个数超过限制的显示个数，那么就到此为止；
 * 如果超出显示范围，同样到此为止；总而言之，就是在设置显示个数限制的情况下，首先考虑显示个数，其次还要考虑显示范围
 */
public class LimitVisibleIndexStrategy implements INextAndPrePageStrategy {

	private int firstVisibleTabIndex = 0;
	private int lastVisibleTabIndex = 0;
	private static final int DEFAULT_SHOW_FORMS_COUNT = 0; // 0代表显示的窗体没有数量限制
	private int showFormsCount = DEFAULT_SHOW_FORMS_COUNT;

	public LimitVisibleIndexStrategy(int showFormsCount) {
		setShowFormsCount(showFormsCount);
	}

	public int getShowFormsCount() {
		return this.showFormsCount;
	}

	public void setShowFormsCount(int showFormsCount) {
		if (showFormsCount <= DEFAULT_SHOW_FORMS_COUNT) {
			Application.getActiveApplication().getOutput().output(ControlsProperties.getString("String_SetShowFormsCountError"));
		} else {
			this.showFormsCount = showFormsCount;
		}
	}

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
			boolean isChangeShowFormsCount = !(DEFAULT_SHOW_FORMS_COUNT == this.showFormsCount);

			// 从 firstIndex 往后遍历计算宽度，直至所有的 Features 摆放完毕或者满足限制条件
			for (int i = this.firstVisibleTabIndex; i < features.size(); i++) {
				IMdiFeature childFeature = features.get(i);
				sum += sum == 0 ? childFeature.getWidth() : childFeature.getWidth() + tabGap;

				if (isChangeShowFormsCount && i - this.firstVisibleTabIndex >= getShowFormsCount() - 1) {
					this.lastVisibleTabIndex = i;
					break;
				}

				if (sum > effectiveWidth) {
					this.lastVisibleTabIndex = i - 1;
					break;
				}
			}

			// 如果 tabs 区域可以摆放的下，那么就全部显示出来。
			// 如果 lastVisibleTabIndex 已经是最后一个了，那么就说明可用的 tabs 区域还有发挥空间，就从 startIndex 往前继续运算，
			if (this.lastVisibleTabIndex == features.size() && this.firstVisibleTabIndex > 0) {
				for (int i = this.firstVisibleTabIndex - 1; i >= 0; i--) {
					IMdiFeature childFeature = features.get(i);
					sum += sum == 0 ? childFeature.getWidth() : childFeature.getWidth() + tabGap;

					if (isChangeShowFormsCount && this.lastVisibleTabIndex - i >= getShowFormsCount() - 1) {
						this.firstVisibleTabIndex = i;
						break;
					}

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
