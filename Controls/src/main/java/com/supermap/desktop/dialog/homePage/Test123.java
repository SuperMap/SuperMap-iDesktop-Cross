package com.supermap.desktop.dialog.homePage;

import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import org.flexdock.demos.util.DemoUtility;

import javax.swing.*;
import java.awt.*;

/**
 * Created by yuanR on 2017/11/30 0030.
 */
public class Test123 {


	public static void main(String[] args) {
		JFrame f = new JFrame("Custom Conatainers Docking Demo");
		f.setContentPane(new JPanelRecentlyUsed());
		f.setSize(600, 400);
		DemoUtility.setCloseOperation(f);
		f.setVisible(true);
	}


	static class JPanelRecentlyUsed extends JPanel {
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

		}

		private void initComponents() {
			panelWorkspace = new JPanel();
			panelDatasource = new JPanel();
			panelPath = new JPanel();


			labelRecentlyUsed = new JLabel();

			tabbedPane = new JTabbedPane();
			tabbedPane.addTab("工作空间", panelWorkspace);
			tabbedPane.addTab("数据源", panelDatasource);
			tabbedPane.addTab("路径", panelPath);


		}

		private void initLayout() {
			this.setLayout(new GridBagLayout());
			this.add(labelRecentlyUsed, new GridBagConstraintsHelper(0, 0, 3, 2).setFill(GridBagConstraints.NONE).setWeight(1, 1).setAnchor(GridBagConstraints.EAST).setInsets(0, 0, 10, 10));
			this.add(tabbedPane, new GridBagConstraintsHelper(0, 2, 3, 5).setFill(GridBagConstraints.CENTER).setWeight(0, 1).setAnchor(GridBagConstraints.CENTER).setInsets(0, 0, 10, 10));

		}

		private void initListeners() {
		}

		private void initResources() {
			labelRecentlyUsed.setText("最近使用");
		}

		private void initComponentState() {
		}


	}

}
