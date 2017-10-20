package com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels;

import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.ui.controls.ProviderLabel.WarningOrHelpProvider;

import javax.swing.*;
import java.awt.*;

/**
 * Created by yuanR on 2017/9/25 0025.
 * 坐标系信息面板
 */
public class PanelCoordSysInfo extends JPanel {
	private JLabel labelCoordInfo;
	private WarningOrHelpProvider labelCoordInfoWithProvider;
	private JTextArea textAreaCoordInfo;
	private String coordInfo = "";
	private Boolean isAddTip = false;

	public PanelCoordSysInfo(String text, Boolean isAddTip) {
		this.coordInfo = text;
		this.isAddTip = isAddTip;
		initComponents();
		initLayout();
	}

	private void initComponents() {
		this.labelCoordInfo = new JLabel("CoordInfo:");
		this.labelCoordInfoWithProvider = new WarningOrHelpProvider(ControlsProperties.getString("String_TipText_NonsupportNullProjectionData"), false);
		this.labelCoordInfo.setText(ControlsProperties.getString("String_ProjectionInfoControl_LabelProjectionInfo"));
		this.labelCoordInfoWithProvider.setText(ControlsProperties.getString("String_ProjectionInfoControl_LabelProjectionInfo"));
		this.textAreaCoordInfo = new JTextArea();
		this.textAreaCoordInfo.setEditable(false);
		setCoordInfo(this.coordInfo);
	}

	private void initLayout() {
		JScrollPane scrollPane = new JScrollPane(this.textAreaCoordInfo);
		this.setLayout(new GridBagLayout());
		if (this.isAddTip) {
			this.add(labelCoordInfoWithProvider, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.NORTH).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL).setInsets(0, 0, 0, 0));
		} else {
			this.add(labelCoordInfo, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.NORTH).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL).setInsets(0, 0, 0, 0));
		}
		this.add(scrollPane, new GridBagConstraintsHelper(0, 1, 1, 3).setAnchor(GridBagConstraints.CENTER).setWeight(1, 1).setFill(GridBagConstraints.BOTH).setInsets(10, 0, 0, 0));
	}

	/**
	 * 设置坐标系信息
	 *
	 * @param text
	 */
	public void setCoordInfo(String text) {
		this.coordInfo = text;
		textAreaCoordInfo.setText(coordInfo);
		// 滚动条自动滚动到顶端
		textAreaCoordInfo.setCaretPosition(0);

	}
}
