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

public class RangeThemePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private ThemeLabelDecorator labelRangeTheme;
	private transient LocalMouseListener mouseListener = new LocalMouseListener();
	private transient ThemeGuidDialog themeGuidDialog;

	public RangeThemePanel(ThemeGuidDialog themeGuidDialog) {
		this.themeGuidDialog = themeGuidDialog;
		initComponents();
		registListener();
	}

	private void initComponents() {
		this.labelRangeTheme = new ThemeLabelDecorator(InternalImageIconFactory.THEMEGUIDE_RANGE, CoreProperties.getString("String_Default"));
		this.labelRangeTheme.selected(true);
		this.setBackground(Color.WHITE);
		this.setBorder(new LineBorder(Color.LIGHT_GRAY));
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout.createSequentialGroup().addComponent(labelRangeTheme).addContainerGap(160, Short.MAX_VALUE)));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout.createSequentialGroup().addComponent(labelRangeTheme).addContainerGap(240, Short.MAX_VALUE)));
		setLayout(groupLayout);
	}

	private void registListener() {
		this.labelRangeTheme.addMouseListener(this.mouseListener);
	}

	public void unregistListener() {
		this.labelRangeTheme.removeMouseListener(this.mouseListener);
	}

	class LocalMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				// 分段专题图
				ThemeGuideFactory.buildRangeTheme(ThemeUtil.getActiveLayer());
				themeGuidDialog.dispose();
				unregistListener();
			}
		}

	}

}
