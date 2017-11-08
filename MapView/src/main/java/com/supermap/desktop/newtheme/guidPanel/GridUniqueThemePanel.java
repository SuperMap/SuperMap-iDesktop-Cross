package com.supermap.desktop.newtheme.guidPanel;

import com.supermap.desktop.newtheme.commonUtils.ThemeGuideFactory;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.InternalImageIconFactory;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GridUniqueThemePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private transient ThemeGuidDialog themeGuidDialog;
	private ThemeLabelDecorator labelGridUniqueTheme;
	private LocalMouseListener localMouseListener = new LocalMouseListener();

	public GridUniqueThemePanel(ThemeGuidDialog themeGuidDialog) {
		this.themeGuidDialog = themeGuidDialog;
		initComponents();
		registListener();
	}

	private void initComponents() {
		// @formatter:off
		this.labelGridUniqueTheme = new ThemeLabelDecorator(InternalImageIconFactory.THEMEGUIDE_GRIDUNIQUE, CoreProperties.getString("String_Default"));
		this.labelGridUniqueTheme.selected(true);
		this.setBackground(Color.WHITE);
		this.setBorder(new LineBorder(Color.LIGHT_GRAY));
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout.createSequentialGroup().addComponent(labelGridUniqueTheme).addContainerGap(160, Short.MAX_VALUE)));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout.createSequentialGroup().addComponent(labelGridUniqueTheme).addContainerGap(240, Short.MAX_VALUE)));
		setLayout(groupLayout);
		// @formatter:on
	}

	/**
	 * 注册事件
	 */
	private void registListener() {
		this.labelGridUniqueTheme.addMouseListener(this.localMouseListener);
	}

	public void unregistListener() {
		this.labelGridUniqueTheme.removeMouseListener(this.localMouseListener);
	}

	class LocalMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				// 单值专题图
				ThemeGuideFactory.buildGridUniqueTheme();
				themeGuidDialog.dispose();
				unregistListener();
			}
		}
	}
}
