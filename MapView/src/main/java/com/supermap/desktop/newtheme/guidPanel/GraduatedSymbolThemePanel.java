package com.supermap.desktop.newtheme.guidPanel;

import com.supermap.desktop.newtheme.commonUtils.ThemeGuideFactory;
import com.supermap.desktop.newtheme.commonUtils.ThemeUtil;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.InternalImageIconFactory;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GraduatedSymbolThemePanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private ThemeLabelDecorator graduatedSymbolTheme;
	private transient ThemeGuidDialog themeGuidDialog;
	private transient LocalMouseListener mouseListener = new LocalMouseListener();

	public GraduatedSymbolThemePanel(ThemeGuidDialog themeGuidDialog) {
		this.themeGuidDialog = themeGuidDialog;
		initComponents();
		registListener();
	}

	/**
	 * 界面布局入口
	 */
	private void initComponents() {
		// @formatter:off
		this.graduatedSymbolTheme = new ThemeLabelDecorator(InternalImageIconFactory.THEMEGUIDE_GRADUATEDSYMBOL, CoreProperties.getString("String_Default"));
		this.graduatedSymbolTheme.selected(true);
		this.setBackground(Color.WHITE);
		this.setBorder(new LineBorder(Color.LIGHT_GRAY));
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
								.addComponent(graduatedSymbolTheme)
								.addContainerGap(160, Short.MAX_VALUE))
				);
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
								.addComponent(graduatedSymbolTheme)
								.addContainerGap(240, Short.MAX_VALUE))
				);
		setLayout(groupLayout);
		// @formatter:on
	}

	/**
	 * 注册事件
	 */
	private void registListener() {
		this.graduatedSymbolTheme.addMouseListener(this.mouseListener);
	}

	/**
	 * 注销事件
	 */
	public void unregistListener() {
		this.graduatedSymbolTheme.removeMouseListener(this.mouseListener);
	}

	class LocalMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				// 等级符号专题图
				ThemeGuideFactory.buildGraduatedSymbolTheme(ThemeUtil.getActiveLayer());
				themeGuidDialog.dispose();
				unregistListener();
			}
		}

	}
}
