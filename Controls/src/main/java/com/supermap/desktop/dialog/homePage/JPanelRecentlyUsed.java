package com.supermap.desktop.dialog.homePage;

import com.supermap.desktop.implement.SmMenu;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.utilities.RecentFileUtilties;

import javax.swing.*;
import java.awt.*;

/**
 * Created by yuanR on 2017/11/30 0030.
 * 最近使用面板
 */
public class JPanelRecentlyUsed extends JFrame {
	private JLabel labelRecentlyUsed;
	private JTabbedPane tabbedPane;

	private JPanel panelWorkspace;
	private JPanel panelDatasource;
	private JPanel panelPath;


	public JPanelRecentlyUsed() {
		initComponents();
		initLayout();
		initListeners();
		initResources();
		initComponentState();


		this.setSize(600, 400);
		this.setVisible(true);
	}

	private void initComponents() {
		panelWorkspace = new JPanel();
		panelDatasource = new JPanel();
		panelPath = new JPanel();


		labelRecentlyUsed = new JLabel();
		labelRecentlyUsed.setFont(new Font("宋体", Font.BOLD, 26));
		tabbedPane = new JTabbedPane();

		tabbedPane.addTab("工作空间", panelWorkspace);
		tabbedPane.addTab("数据源", panelDatasource);
		tabbedPane.addTab("路径", panelPath);

		JList list = new JList();
		// 构建List
		SmMenu menu = RecentFileUtilties.getRecentDatasourceMenu();
		for (int i = 0; i < menu.getCount(); i++) {
			list.add(menu.getMenuComponent(i));
		}

		//DefaultListModel<String> listModel;
		//listModel = new DefaultListModel<>();
		//list.setModel(listModel);
		//list.setCellRenderer(new DefaultListCellRenderer() {
		//	@Override
		//	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		//		JLabel listCellRendererComponent = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		//		listCellRendererComponent.setHorizontalAlignment(CENTER);
		//		listCellRendererComponent.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
		//		return listCellRendererComponent;
		//	}
		//});


		panelWorkspace.add(list);
	}

	private void initLayout() {
		this.getContentPane().setLayout(new GridBagLayout());
		this.getContentPane().add(labelRecentlyUsed, new GridBagConstraintsHelper(0, 0, 1, 1).setFill(GridBagConstraints.NONE).setWeight(1, 0).setAnchor(GridBagConstraints.WEST).setInsets(5, 10, 5, 0));
		this.getContentPane().add(this.tabbedPane, new GridBagConstraintsHelper(0, 1, 2, 1).setFill(GridBagConstraints.BOTH).setWeight(1, 1).setAnchor(GridBagConstraints.CENTER).setInsets(5, 10, 0, 10));

	}

	private void initListeners() {
	}

	private void initResources() {
		labelRecentlyUsed.setText("最近使用");
	}

	private void initComponentState() {
	}

}
