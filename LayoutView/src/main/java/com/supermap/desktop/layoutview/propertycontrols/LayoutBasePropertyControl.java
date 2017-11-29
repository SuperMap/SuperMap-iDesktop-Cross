package com.supermap.desktop.layoutview.propertycontrols;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IFormLayout;
import com.supermap.desktop.controls.DefaultValues;
import com.supermap.desktop.dialog.DialogRulerLinesManager;
import com.supermap.desktop.layoutview.LayoutViewProperties;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.layout.MapLayout;
import com.supermap.layout.RulerLine;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/28
 * Time: 11:20
 * Description:base property panel
 */
public class LayoutBasePropertyControl extends AbstractPropertyControl {

	private static final long serialVersionUID = 1L;
	private JLabel labelLayoutName;
	private JTextField textFieldLayoutName;
	private JCheckBox checkBoxHScrollBar;
	private JCheckBox checkBoxVScrollBar;
	private JCheckBox checkBoxRuler;
	private JCheckBox checkBoxOverlapDisplayed;
	private JCheckBox checkBoxRulerLines;
	private JButton buttonRulerLinesManager;
	private JLabel labelMinZoomRatio;
	private JComboBox comboBoxMinZoomRatio;
	private JLabel labelMaxZoomRatio;
	private JComboBox comboBoxMaxZoomRatio;

	private String layoutName = "";
	private boolean isShowHScrollBar = false;
	private boolean isShowVScrollBar = false;
	private boolean isShowRuler = false;
	private boolean isOverlapDisplayed = false;
	private boolean isShowRulerLines = false;
	private double minZoomRatio = 0;
	private double maxZoomRatio = 0;
	private RulerLine[] rulerLines=null;

	private transient ActionListener actionListener=new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			buttonRulerLineManagerChange();
		}
	};

	private transient ItemListener itemListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getSource() == checkBoxHScrollBar) {
				checkBoxIsShowHScrollBarChange();
			} else if (e.getSource() == checkBoxVScrollBar) {
				checkBoxIsShowVScrollBarChange();
			} else if (e.getSource() == checkBoxRuler) {
				checkBoxIsShowRulerChange();
			} else if (e.getSource() == checkBoxOverlapDisplayed) {
				checkBoxIsOverlapDisplayedChange();
			} else if (e.getSource() == checkBoxRulerLines) {
				checkBoxIsShowRulerLinesChange();
			} else if (e.getSource() == comboBoxMinZoomRatio) {
				comboBoxMinZoomRatioChange();
			} else if (e.getSource() == comboBoxMaxZoomRatio) {
				comboBoxMaxZoomRatioChange();
			}
		}
	};


	public LayoutBasePropertyControl() {
		super("");
	}

	@Override
	protected void initializeComponents() {
		this.labelLayoutName = new JLabel("LayoutName");
		this.textFieldLayoutName = new JTextField();
		this.checkBoxHScrollBar = new JCheckBox();
		this.checkBoxVScrollBar = new JCheckBox();
		this.checkBoxRuler = new JCheckBox();
		this.checkBoxOverlapDisplayed = new JCheckBox();
		this.checkBoxRulerLines = new JCheckBox();
		this.buttonRulerLinesManager = new JButton();
		this.labelMinZoomRatio = new JLabel("MinZoomRatio");
		this.comboBoxMinZoomRatio = new JComboBox();
		this.labelMaxZoomRatio = new JLabel("MaxZoomRatio");
		this.comboBoxMaxZoomRatio = new JComboBox();

		this.textFieldLayoutName.setEditable(false);
		initLayout();
	}

	private void initLayout() {
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);

		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.labelLayoutName, GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_LABEL_WIDTH, Short.MAX_VALUE)
						.addComponent(this.checkBoxHScrollBar)
						.addComponent(this.checkBoxRuler)
						.addComponent(this.checkBoxRulerLines)
						.addComponent(this.labelMinZoomRatio, GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_LABEL_WIDTH, Short.MAX_VALUE)
						.addComponent(this.labelMaxZoomRatio, GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_LABEL_WIDTH, Short.MAX_VALUE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.textFieldLayoutName, GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_COMPONENT_WIDTH, Short.MAX_VALUE)
						.addComponent(this.checkBoxVScrollBar)
						.addComponent(this.checkBoxOverlapDisplayed)
						.addComponent(this.buttonRulerLinesManager, GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_COMPONENT_WIDTH, Short.MAX_VALUE)
						.addComponent(this.comboBoxMinZoomRatio, GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_COMPONENT_WIDTH, Short.MAX_VALUE)
						.addComponent(this.comboBoxMaxZoomRatio, GroupLayout.PREFERRED_SIZE, DefaultValues.DEFAULT_COMPONENT_WIDTH, Short.MAX_VALUE))
		);

		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelLayoutName)
						.addComponent(this.textFieldLayoutName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.checkBoxHScrollBar)
						.addComponent(this.checkBoxVScrollBar))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.checkBoxRuler)
						.addComponent(this.checkBoxOverlapDisplayed))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.checkBoxRulerLines)
						.addComponent(this.buttonRulerLinesManager, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelMinZoomRatio)
						.addComponent(this.comboBoxMinZoomRatio, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelMaxZoomRatio)
						.addComponent(this.comboBoxMaxZoomRatio, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);
		this.setLayout(groupLayout);
	}

	@Override
	public void apply() {
		if (getFormLayout() != null) {
			MapLayout mapLayout = getFormLayout().getMapLayoutControl().getMapLayout();
			getFormLayout().getMapLayoutControl().setHorizontalScrollbarVisible(this.isShowHScrollBar);
			getFormLayout().getMapLayoutControl().setVerticalScrollbarVisible(this.isShowVScrollBar);
			mapLayout.getRulerSetting().setRulerVisible(this.isShowRuler);
			mapLayout.setOverlapDisplayed(this.isOverlapDisplayed);
			mapLayout.getRulerLines().setVisible(this.isShowRulerLines);
			mapLayout.setMinZoomRatio(this.minZoomRatio);
			mapLayout.setMaxZoomRatio(this.maxZoomRatio);
			mapLayout.getRulerLines().clear();
			mapLayout.getRulerLines().addRange(this.rulerLines);
			mapLayout.refresh();
		}
	}

	@Override
	protected void initializeResources() {
		this.labelLayoutName.setText(LayoutViewProperties.getString("String_LayoutName"));
		this.checkBoxHScrollBar.setText(LayoutViewProperties.getString("String_HScrollBar"));
		this.checkBoxVScrollBar.setText(LayoutViewProperties.getString("String_VScrollBar"));
		this.checkBoxRuler.setText(LayoutViewProperties.getString("String_ShowRuler"));
		this.checkBoxOverlapDisplayed.setText(LayoutViewProperties.getString("String_OverlapDisplayed"));
		this.checkBoxRulerLines.setText(LayoutViewProperties.getString("String_RulerLines"));
		this.buttonRulerLinesManager.setText(LayoutViewProperties.getString("String_RulerLinesManager"));
		this.labelMinZoomRatio.setText(LayoutViewProperties.getString("String_MinZoomRatio"));
		this.labelMaxZoomRatio.setText(LayoutViewProperties.getString("String_MaxZoomRatio"));
	}

	@Override
	protected void initializePropertyValues(IFormLayout formLayout) {
		if (formLayout != null) {
			MapLayout mapLayout = getFormLayout().getMapLayoutControl().getMapLayout();
			this.layoutName = formLayout.getText();
			this.isShowHScrollBar = getFormLayout().getMapLayoutControl().isHorizontalScrollbarVisible();
			this.isShowVScrollBar = getFormLayout().getMapLayoutControl().isVerticalScrollbarVisible();
			this.isShowRuler = mapLayout.getRulerSetting().isRulerVisible();
			this.isOverlapDisplayed = mapLayout.isOverlapDisplayed();
			this.isShowRulerLines = mapLayout.getRulerLines().isVisible();
			this.minZoomRatio = mapLayout.getMinZoomRatio();
			this.maxZoomRatio = mapLayout.getMaxZoomRatio();
			this.rulerLines=mapLayout.getRulerLines().toArray();
		}
	}

	@Override
	protected void registerEvents() {
		super.registerEvents();
		this.checkBoxHScrollBar.addItemListener(this.itemListener);
		this.checkBoxVScrollBar.addItemListener(this.itemListener);
		this.checkBoxRuler.addItemListener(this.itemListener);
		this.checkBoxOverlapDisplayed.addItemListener(this.itemListener);
		this.checkBoxRulerLines.addItemListener(this.itemListener);
		this.buttonRulerLinesManager.addActionListener(this.actionListener);
		this.comboBoxMinZoomRatio.addItemListener(this.itemListener);
		this.comboBoxMaxZoomRatio.addItemListener(this.itemListener);
	}

	@Override
	protected void unregisterEvents() {
		super.unregisterEvents();
		this.checkBoxHScrollBar.removeItemListener(this.itemListener);
		this.checkBoxVScrollBar.removeItemListener(this.itemListener);
		this.checkBoxRuler.removeItemListener(this.itemListener);
		this.checkBoxOverlapDisplayed.removeItemListener(this.itemListener);
		this.checkBoxRulerLines.removeItemListener(this.itemListener);
		this.buttonRulerLinesManager.removeActionListener(this.actionListener);
		this.comboBoxMinZoomRatio.removeItemListener(this.itemListener);
		this.comboBoxMaxZoomRatio.removeItemListener(this.itemListener);
	}

	@Override
	protected void fillComponents() {
		this.textFieldLayoutName.setText(this.layoutName);
		this.checkBoxHScrollBar.setSelected(this.isShowHScrollBar);
		this.checkBoxVScrollBar.setSelected(this.isShowVScrollBar);
		this.checkBoxRuler.setSelected(this.isShowRuler);
		this.checkBoxOverlapDisplayed.setSelected(this.isOverlapDisplayed);
		this.checkBoxRulerLines.setSelected(this.isShowRulerLines);
		fillComboBoxZoomRatio();
		this.comboBoxMinZoomRatio.setSelectedItem(getTextByScale(this.minZoomRatio));
		this.comboBoxMaxZoomRatio.setSelectedItem(getTextByScale(this.maxZoomRatio));
	}

	@Override
	protected void setComponentsEnabled() {
	}

	@Override
	protected boolean verifyChange() {
		return  getFormLayout().getMapLayoutControl().isHorizontalScrollbarVisible() != this.isShowHScrollBar
				|| getFormLayout().getMapLayoutControl().isVerticalScrollbarVisible() != this.isShowVScrollBar
				|| getFormLayout().getMapLayoutControl().getMapLayout().getRulerSetting().isRulerVisible() != this.isShowRuler
				|| getFormLayout().getMapLayoutControl().getMapLayout().isOverlapDisplayed() != this.isOverlapDisplayed
				|| getFormLayout().getMapLayoutControl().getMapLayout().getRulerLines().isVisible() != this.isShowRulerLines
				|| Double.compare(getFormLayout().getMapLayoutControl().getMapLayout().getMinZoomRatio(), this.minZoomRatio) != 0
				|| Double.compare(getFormLayout().getMapLayoutControl().getMapLayout().getMaxZoomRatio(), this.maxZoomRatio) != 0
				|| getFormLayout().getMapLayoutControl().getMapLayout().getRulerLines().toArray()!=this.rulerLines;
	}

	private void fillComboBoxZoomRatio() {
		try {
			//最小显示比例尺
			comboBoxMinZoomRatio.removeAllItems();
			comboBoxMinZoomRatio.addItem("1%");
			comboBoxMinZoomRatio.addItem("5%");
			comboBoxMinZoomRatio.addItem("10%");
			comboBoxMinZoomRatio.addItem("20%");
			comboBoxMinZoomRatio.addItem("30%");
			comboBoxMinZoomRatio.addItem("40%");
			comboBoxMinZoomRatio.addItem("50%");
			comboBoxMinZoomRatio.addItem("60%");
			comboBoxMinZoomRatio.addItem("70%");
			comboBoxMinZoomRatio.addItem("80%");
			comboBoxMinZoomRatio.addItem("90%");
			//m_comboBoxMinDisplayScale.SelectedIndex = 0;
			//最大显示比例尺
			comboBoxMaxZoomRatio.removeAllItems();
			comboBoxMaxZoomRatio.addItem("100%");
			comboBoxMaxZoomRatio.addItem("120%");
			comboBoxMaxZoomRatio.addItem("150%");
			comboBoxMaxZoomRatio.addItem("180%");
			comboBoxMaxZoomRatio.addItem("200%");
			comboBoxMaxZoomRatio.addItem("300%");
			comboBoxMaxZoomRatio.addItem("400%");
			comboBoxMaxZoomRatio.addItem("500%");
			comboBoxMaxZoomRatio.addItem("1000%");
			//m_comboBoxMaxDisplayScale.SelectedIndex = defaultIndex;
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	private String getTextByScale(double dScale) {
		String strScale = "";
		try {
			if (dScale < 0.0001) {
				dScale = 0.0001;
			}
			if (dScale > 10) {
				dScale = 10;
			}
			strScale = String.valueOf((int)(dScale * 100)) + "%";
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
		return strScale;
	}

	private double getScaleByText(String strScale) {
		double dScale = 0.0;
		try {
			if (strScale.length() > 0 && strScale.endsWith("%")) {
				strScale = strScale.substring(0, strScale.length() - 1);
			}
			if (strScale.length() > 0) {
				dScale = Double.valueOf(strScale) / 100;

				if (dScale < 0.0001) {
					dScale = 0.0001;
				}
				if (dScale > 10) {
					dScale = 10;
				}
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
		return dScale;
	}

	private void checkBoxIsShowHScrollBarChange() {
		try {
			this.isShowHScrollBar = this.checkBoxHScrollBar.isSelected();
			verify();
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	private void checkBoxIsShowVScrollBarChange() {
		try {
			this.isShowVScrollBar = this.checkBoxVScrollBar.isSelected();
			verify();
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	private void checkBoxIsShowRulerChange() {
		try {
			this.isShowRuler = this.checkBoxRuler.isSelected();
			verify();
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	private void checkBoxIsOverlapDisplayedChange() {
		try {
			this.isOverlapDisplayed = this.checkBoxOverlapDisplayed.isSelected();
			verify();
		} catch (Exception e2) {
			Application.getActiveApplication().getOutput().output(e2);
		}
	}

	private void checkBoxIsShowRulerLinesChange() {
		try {
			this.isShowRulerLines = this.checkBoxRulerLines.isSelected();
			verify();
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	private void comboBoxMinZoomRatioChange() {
		try {
			this.minZoomRatio = getScaleByText(this.comboBoxMinZoomRatio.getSelectedItem().toString());
			verify();
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	private void comboBoxMaxZoomRatioChange() {
		try {
			this.maxZoomRatio = getScaleByText(this.comboBoxMaxZoomRatio.getSelectedItem().toString());
			verify();
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	private void buttonRulerLineManagerChange(){
		try {
			DialogRulerLinesManager dialogRulerLinesManager=new DialogRulerLinesManager(this.rulerLines);
			dialogRulerLinesManager.showDialog();
			DialogResult dialogResult=dialogRulerLinesManager.getDialogResult();
			if (dialogResult==DialogResult.OK){
				this.rulerLines=dialogRulerLinesManager.getRulerLines();
				verify();
			}
		}catch (Exception e){
			Application.getActiveApplication().getOutput().output(e);
		}
	}
}
