/*
 * Copyright (c) 2005-2016 Flamingo Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of Flamingo Kirill Grouchnikov nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package org.pushingpixels.flamingo.internal.ui.ribbon;

import org.pushingpixels.flamingo.api.common.AbstractCommandButton;
import org.pushingpixels.flamingo.api.common.CommandButtonDisplayState;
import org.pushingpixels.flamingo.api.ribbon.AbstractRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.IconRibbonBandResizePolicy;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;
import org.pushingpixels.flamingo.internal.ui.ribbon.BasicRibbonBandUI.CollapsedButtonPopupPanel;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Basic UI for control panel of ribbon band {@link JBandControlPanel}.
 *
 * @author Kirill Grouchnikov
 */
public class BasicFlowBandControlPanelUI extends AbstractBandControlPanelUI {
	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
	 */
	public static ComponentUI createUI(JComponent c) {
		return new BasicFlowBandControlPanelUI();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.jvnet.flamingo.ribbon.ui.AbstractBandControlPanelUI#createLayoutManager
	 * ()
	 */
	@Override
	protected LayoutManager createLayoutManager() {
		return new FlowControlPanelLayout();
	}

	/**
	 * Layout for the control panel of flow ribbon band.
	 *
	 * @author Kirill Grouchnikov
	 */
	private class FlowControlPanelLayout implements LayoutManager {

		/*
		 * (non-Javadoc)
		 *
		 * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String,
		 * java.awt.Component)
		 */
		public void addLayoutComponent(String name, Component c) {
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
		 */
		public void removeLayoutComponent(Component c) {
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
		 */
		public Dimension preferredLayoutSize(Container c) {
			// The height of ribbon band control panel is
			// computed based on the preferred height of a command
			// button in BIG state.
			int buttonHeight = dummy.getPreferredSize().height;
			int vGap = getLayoutGap() * 3 / 4;
			int minusGaps = buttonHeight - 2 * vGap;
			switch (minusGaps % 3) {
				case 1:
					buttonHeight += 2;
					break;
				case 2:
					buttonHeight++;
					break;
			}

			Insets ins = c.getInsets();
			return new Dimension(c.getWidth(), buttonHeight + ins.top
					+ ins.bottom);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
		 */
		public Dimension minimumLayoutSize(Container c) {
			return this.preferredLayoutSize(c);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
		 */
		public void layoutContainer(Container c) {
			JFlowBandControlPanel flowBandControlPanel = (JFlowBandControlPanel) c;
			AbstractRibbonBand ribbonBand = flowBandControlPanel
					.getRibbonBand();
			RibbonBandResizePolicy currentResizePolicy = ribbonBand
					.getCurrentResizePolicy();
			if (currentResizePolicy == null)
				return;

			boolean ltr = c.getComponentOrientation().isLeftToRight();
			// need place for border
			Insets ins = c.getInsets();
			int x = ins.left;
			int gap = getLayoutGap();
			int availableHeight = c.getHeight() - ins.top - ins.bottom;

			if (SwingUtilities.getAncestorOfClass(
					CollapsedButtonPopupPanel.class, c) != null) {
				List<RibbonBandResizePolicy> resizePolicies = ribbonBand
						.getResizePolicies();
				// install the most permissive resize policy on the popup
				// panel of a collapsed ribbon band
				resizePolicies.get(0).install(availableHeight, gap);
			} else {
				if (currentResizePolicy instanceof IconRibbonBandResizePolicy) {
					return;
				}
				// Installs the resize policy
				currentResizePolicy.install(availableHeight, gap);
			}

			// TODO: 2017/11/10 重新布局
			if (currentResizePolicy instanceof CoreRibbonResizePolicies.FlowTwoRows) {
				int tempWidth = 0;
				List<JComponent> flowComponents = flowBandControlPanel.getFlowComponents();
				for (int i = 0; i < flowComponents.size(); i++) {
					JComponent flowComponent = flowComponents.get(i);
					Dimension preferredSize = flowComponent.getPreferredSize();
					int rowIndex = 0;
					if (flowComponent instanceof AbstractCommandButton && ((AbstractCommandButton) flowComponent).getDisplayState() == CommandButtonDisplayState.BIG) {
						x += tempWidth;
						flowComponent.setBounds(x, ins.top, preferredSize.width, availableHeight);
						x += preferredSize.width + gap;
						tempWidth = 0;
					} else if (((int) preferredSize.getHeight()) << 1 > availableHeight) {
						x += tempWidth;
						flowComponent.setBounds(x, ins.top, preferredSize.width, Math.min(availableHeight, preferredSize.height));
						x += preferredSize.width + gap;
						tempWidth = 0;
					} else {
						int componentHeight = Math.min((availableHeight - gap) / 2, preferredSize.height);
						if (tempWidth == 0) {
							int width = preferredSize.width;

							if (i + 1 < flowComponents.size()) {
								JComponent nextComponent = flowComponents.get(i + 1);
								if (nextComponent.getPreferredSize().height << 1 < availableHeight) {
									width = Math.max(preferredSize.width, nextComponent.getPreferredSize().width);
								}
							}
							// 计算控件大小，然后把控件大小和剩余空间平分，不然控件太小的时候间隔太大
							flowComponent.setBounds(x, ins.top + ((availableHeight - gap) / 2 - componentHeight) / 4, width, componentHeight + ((availableHeight - gap) / 2 - componentHeight) / 2);
							tempWidth = width + gap;
						} else {
							flowComponent.setBounds(x, ins.top + (availableHeight - gap) / 2, Math.max(tempWidth - gap, preferredSize.width), componentHeight + ((availableHeight - gap) / 2 - componentHeight) / 2);
							x += Math.max(tempWidth, preferredSize.width + gap);
							tempWidth = 0;
							rowIndex = 1;
						}
					}
					flowComponent.putClientProperty(
							AbstractBandControlPanelUI.TOP_ROW, rowIndex == 0);
					flowComponent.putClientProperty(
							AbstractBandControlPanelUI.MID_ROW, rowIndex == 1);
					flowComponent.putClientProperty(
							AbstractBandControlPanelUI.BOTTOM_ROW, false);
				}
			} else if (currentResizePolicy instanceof CoreRibbonResizePolicies.FlowThreeRows) {
				int maxComponentHeight = (availableHeight - 2 * gap) / 3;
				ArrayList<JComponent> components = new ArrayList<>();
				int componentsRow=0;
				List<JComponent> flowComponents = flowBandControlPanel.getFlowComponents();
				for (JComponent flowComponent : flowComponents) {
					Dimension preferredSize = flowComponent.getPreferredSize();
					if (flowComponent instanceof AbstractCommandButton && ((AbstractCommandButton) flowComponent).getDisplayState() == CommandButtonDisplayState.BIG) {
						// 大图标特殊处理
						if (components.size() > 0) {
							x = putComponentsInPanelThree(ins, x, gap, maxComponentHeight, components);
							componentsRow = 0;
						}
						flowComponent.setBounds(x, ins.top, preferredSize.width, availableHeight);
					} else {
						int componentRow = preferredSize.height / availableHeight+1;
						if (componentRow + componentsRow == 3) {
							// 等于3先放进去再布局
							components.add(flowComponent);
							x = putComponentsInPanelThree(ins, x, gap, maxComponentHeight, components);
							componentsRow = 0;
						}else if (componentRow + componentsRow > 3) {
							// 大于3，先布局再放进去
							x = putComponentsInPanelThree(ins, x, gap, maxComponentHeight, components);
							if (componentRow > 3) {
								// 这放的啥，这么大？？
								flowComponent.setBounds(x, ins.top, preferredSize.width, availableHeight);
								x += preferredSize.width + gap;
								componentsRow = 0;
							}else {
								components.add(flowComponent);
								componentsRow = componentRow;
							}
						} else {
							// 小于3. 恩，还可以放
							components.add(flowComponent);
							componentsRow += componentRow;
						}
					}
				}
				if (components.size() > 0) {
					putComponentsInPanelThree(ins, x, gap, maxComponentHeight, components);
				}
			} else {
				// compute the max preferred height of the components and the
				// number of rows
				int maxHeight = 0;
				int rowCount = 1;
				for (JComponent flowComponent : flowBandControlPanel
						.getFlowComponents()) {
					Dimension prefSize = flowComponent.getPreferredSize();
					if ((x + prefSize.width) > (c.getWidth() - ins.right)) {
						x = ins.left;
						rowCount++;
					}
					x += prefSize.width + gap;
					maxHeight = Math.max(maxHeight, prefSize.height);
				}

				int vGap = (availableHeight - rowCount * maxHeight) / rowCount;
				if (vGap < 0) {
					vGap = 2;
					maxHeight = (availableHeight - vGap * (rowCount - 1))
							/ rowCount;
				}
				int y = ins.top + vGap / 2;
				x = ltr ? ins.left : c.getWidth() - ins.right;
				int rowIndex = 0;
				for (JComponent flowComponent : flowBandControlPanel
						.getFlowComponents()) {
					Dimension prefSize = flowComponent.getPreferredSize();
					if (ltr) {
						if ((x + prefSize.width) > (c.getWidth() - ins.right)) {
							x = ins.left;
							y += (maxHeight + vGap);
							rowIndex++;
						}
					} else {
						if ((x - prefSize.width) < ins.left) {
							x = c.getWidth() - ins.right;
							y += (maxHeight + vGap);
							rowIndex++;
						}
					}
					int height = Math.min(maxHeight, prefSize.height);
					if (ltr) {
						flowComponent.setBounds(x, y + (maxHeight - height) / 2,
								prefSize.width, height);
					} else {
						flowComponent.setBounds(x - prefSize.width, y
								+ (maxHeight - height) / 2, prefSize.width, height);
					}
					flowComponent.putClientProperty(
							AbstractBandControlPanelUI.TOP_ROW, Boolean
									.valueOf(rowIndex == 0));
					flowComponent.putClientProperty(
							AbstractBandControlPanelUI.MID_ROW, Boolean
									.valueOf((rowIndex > 0)
											&& (rowIndex < (rowCount - 1))));
					flowComponent.putClientProperty(
							AbstractBandControlPanelUI.BOTTOM_ROW, Boolean
									.valueOf(rowIndex == (rowCount - 1)));
					if (ltr) {
						x += (prefSize.width + gap);
					} else {
						x -= (prefSize.width + gap);
					}
				}
			}
		}

		private int putComponentsInPanelThree(Insets ins, int x, int gap, int maxComponentHeight, ArrayList<JComponent> components) {
			if (components.size()<=0) {
				return x;
			}
			int y = ins.top;
			int tempWidth = 0;
			for (JComponent component : components) {
				Dimension preferredSize1 = component.getPreferredSize();
				tempWidth = Math.max(tempWidth, preferredSize1.width);
			}
			for (JComponent component : components) {
				Dimension preferredSize1 = component.getPreferredSize();
				int rowCount = preferredSize1.height / maxComponentHeight + 1;
				component.setBounds(x, y + (maxComponentHeight * rowCount - preferredSize1.height) / 2, tempWidth, preferredSize1.height);
				component.putClientProperty(
						AbstractBandControlPanelUI.TOP_ROW, y==ins.top );
				component.putClientProperty(
						AbstractBandControlPanelUI.MID_ROW, y==ins.top+maxComponentHeight);
				component.putClientProperty(
						AbstractBandControlPanelUI.BOTTOM_ROW, y==ins.top+maxComponentHeight*2);
				y += rowCount * maxComponentHeight + gap;

			}
			components.clear();
			x += tempWidth + gap;
			return x;
		}

	}
}
