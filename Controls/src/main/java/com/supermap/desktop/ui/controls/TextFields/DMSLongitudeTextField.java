package com.supermap.desktop.ui.controls.TextFields;

import com.supermap.desktop.Interface.ISmTextFieldLegit;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.utilities.DoubleUtilities;
import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;

/**
 * Created by yuanR on 2017/10/13 0013.
 * 支持输入度分秒的组合控件（经度版）
 * 组合控件由三个TextField和三个Label组合而成，
 */
public class DMSLongitudeTextField extends JPanel {

	public SmTextFieldLegit getTextFieldD() {
		return textFieldD;
	}

	public SmTextFieldLegit getTextFieldM() {
		return textFieldM;
	}

	public SmTextFieldLegit getTextFieldS() {
		return textFieldS;
	}

	private SmTextFieldLegit textFieldD;
	private SmTextFieldLegit textFieldM;
	private SmTextFieldLegit textFieldS;

	private JLabel labelD;
	private JLabel labelM;
	private JLabel labelS;


	/**
	 * 获得之前秒textField中的值，用以判断当转换显示方式时，是否要重新计算以度为单位的textField的值
	 *
	 * @return
	 */
	public String getCurrentSText() {
		return currentSText;
	}

	public void setCurrentSText(String currentSText) {
		this.currentSText = currentSText;
	}

	// 存储当前秒的text
	private String currentSText;

	// 键盘限制输入事件
	private KeyListener keyAdapter = new KeyAdapter() {
		@Override
		public void keyTyped(KeyEvent e) {
			int keyChar = e.getKeyChar();
			if (e.getSource().equals(textFieldM)) {
				//分textField键盘输入限定
				String textM = textFieldM.getText();
				//限制输入负号和小数点
				if (keyChar == KeyEvent.VK_PERIOD || keyChar == KeyEvent.VK_MINUS || keyChar == KeyEvent.VK_SLASH) {
					e.consume();
				}
				// 只能输入数字
				if (keyChar < 45 || keyChar > 57) {
					e.consume();
				}

				// 最多输入两个数字
				if (textM.length() >= 2) {
					e.consume();
				}
				// 当输入一个数字时，并且大于五，不能在输入
				if (textM.length() >= 1 && StringUtilities.getNumber(textM) >= 6) {
					e.consume();
				}

			} else if (e.getSource().equals(textFieldS)) {
				// 秒textField键盘输入限定
				String textS = textFieldS.getText();
				//限制输入负号和小数点
				if (keyChar == KeyEvent.VK_PERIOD || keyChar == KeyEvent.VK_MINUS || keyChar == KeyEvent.VK_SLASH) {
					e.consume();
				}
				// 只能输入数字
				if (keyChar < 45 || keyChar > 57) {
					e.consume();
				}

				// 最多输入两个数字
				if (textS.length() >= 2) {
					e.consume();
				}

				// 当输入一个数字时，并且大于五，不能在输入
				if (textS.length() >= 1 && StringUtilities.getNumber(textS) >= 6) {
					e.consume();
				}

			} else if (e.getSource().equals(textFieldD)) {
				// 度textField键盘输入限定
				String textD = textFieldD.getText();
				//“-”负号在首位，并且只能输入一次
				if (textFieldD.getCaretPosition() != 0 && keyChar == KeyEvent.VK_MINUS || textD.contains("-") && keyChar == KeyEvent.VK_MINUS) {// keyChar == 45代表负号
					e.consume();
				}

				if (keyChar == KeyEvent.VK_PERIOD || keyChar == KeyEvent.VK_SLASH) {
					e.consume();
				}

				// 只能输入数字
				if (keyChar < 45 || keyChar > 57) {
					e.consume();
				}

				// 最多输入三个数字
				if (textD.contains("-") && textD.length() >= 4 || !textD.contains("-") && textD.length() >= 3) {
					e.consume();
				}

				// 当已经输入了两个字符，并且大于18
				if (textD.length() >= 2 && StringUtilities.getNumber(textD.replace("-", "")) > 18) {
					e.consume();
				}

				// 当且仅当分、秒都为0时，才可以输入180
				if (textD.length() >= 2 && StringUtilities.getNumber(textD.replace("-", "")) == 18) {
					if (!(Integer.valueOf(textFieldM.getText()).equals(0) && Integer.valueOf(textFieldS.getText()).equals(0) && keyChar == 48)) {
						e.consume();
					}
				}
			}
		}
	};

	/**
	 * 分、秒文本框正确值范围设置
	 */
	private ISmTextFieldLegit iSmTextFieldLegit = new ISmTextFieldLegit() {
		@Override
		public boolean isTextFieldValueLegit(String textFieldValue) {
			if (StringUtilities.isNullOrEmpty(textFieldValue)) {
				return false;
			}
			try {
				Integer integer = Integer.valueOf(textFieldValue);
				Integer degree = Integer.valueOf(textFieldD.getText().replace("-", ""));
				if ((degree.equals(180) && integer != 0)) {
					return false;
				}
				if (integer < 0 || integer >= 60) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
			return true;
		}

		@Override
		public String getLegitValue(String currentValue, String backUpValue) {
			return backUpValue;
		}
	};


	/**
	 * 度文本框正确值范围设置
	 */
	private ISmTextFieldLegit iSmTextFieldLegitDegree = new ISmTextFieldLegit() {
		@Override
		public boolean isTextFieldValueLegit(String textFieldValue) {
			if (StringUtilities.isNullOrEmpty(textFieldValue)) {
				return false;
			}
			try {

				Integer integer = Integer.valueOf(textFieldValue.replace("-", ""));
				Integer minute = Integer.valueOf(textFieldM.getText());
				Integer second = Integer.valueOf(textFieldS.getText());

				if ((!minute.equals(0) || !second.equals(0)) && integer == 180) {
					return false;
				}

				if (integer > 180) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
			return true;
		}

		@Override
		public String getLegitValue(String currentValue, String backUpValue) {
			return backUpValue;
		}
	};


	public DMSLongitudeTextField() {
		initComponent();
		initLayout();
		initResources();
		registEvents();

	}


	private void initComponent() {
		this.textFieldD = new SmTextFieldLegit("0");
		this.textFieldD.setToolTipText(ControlsProperties.getString("String_RangeSection") + "[-180,180]");
		this.textFieldD.setBorder(null);
		this.textFieldM = new SmTextFieldLegit("0");
		this.textFieldM.setToolTipText(ControlsProperties.getString("String_RangeSection") + "[0,60)");
		this.textFieldM.setBorder(null);
		this.textFieldS = new SmTextFieldLegit("0");
		this.textFieldS.setToolTipText(ControlsProperties.getString("String_RangeSection") + "[0,60)");
		this.textFieldS.setBorder(null);

		this.labelD = new JLabel();
		this.labelM = new JLabel();
		this.labelS = new JLabel();

		this.setBorder(new JTextField().getBorder());
		this.setBackground(Color.white);
	}

	private void initLayout() {
		this.setPreferredSize(new Dimension(100, 23));
		this.textFieldD.setPreferredSize(new Dimension(30, 23));
		this.textFieldM.setPreferredSize(new Dimension(30, 23));
		this.textFieldS.setPreferredSize(new Dimension(30, 23));
		GroupLayout groupLayoutPanel = new GroupLayout(this);
		this.setLayout(groupLayoutPanel);
		// @formatter off
		groupLayoutPanel.setHorizontalGroup(groupLayoutPanel.createSequentialGroup()
				.addComponent(this.textFieldD)
				.addComponent(this.labelD)
				.addComponent(this.textFieldM)
				.addComponent(this.labelM)
				.addComponent(this.textFieldS)
				.addComponent(this.labelS));
		groupLayoutPanel.setVerticalGroup(groupLayoutPanel.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(this.textFieldD)
				.addComponent(this.labelD)
				.addComponent(this.textFieldM)
				.addComponent(this.labelM)
				.addComponent(this.textFieldS)
				.addComponent(this.labelS));
		// @formatter on

	}

	private void initResources() {
		//this.labelD.setText(CommonProperties.getString("String_AngleUnit_Degree"));
		//this.labelM.setText(CommonProperties.getString("String_AngleUnit_Minute"));
		//this.labelS.setText(CommonProperties.getString("String_AngleUnit_Second"));
		this.labelD.setText("°");
		this.labelM.setText("′");
		this.labelS.setText("″");

	}

	private void registEvents() {
		removeListener();
		this.textFieldD.addKeyListener(this.keyAdapter);
		this.textFieldM.addKeyListener(this.keyAdapter);
		this.textFieldS.addKeyListener(this.keyAdapter);

		this.textFieldD.setSmTextFieldLegit(iSmTextFieldLegitDegree);
		this.textFieldM.setSmTextFieldLegit(iSmTextFieldLegit);
		this.textFieldS.setSmTextFieldLegit(iSmTextFieldLegit);

	}

	private void removeListener() {
		this.textFieldD.removeKeyListener(this.keyAdapter);
		this.textFieldM.removeKeyListener(this.keyAdapter);
		this.textFieldS.removeKeyListener(this.keyAdapter);

		this.textFieldD.removeEvents();
		this.textFieldM.removeEvents();
		this.textFieldS.removeEvents();
	}

	/**
	 * 设置textField是否可用
	 * 为保证控件可用和不可用时的颜色显示，根据是否可用进行调整
	 *
	 * @param enabled
	 */
	public void setPanelEditable(Boolean enabled) {
		this.textFieldD.setEditable(enabled);
		this.textFieldM.setEditable(enabled);
		this.textFieldS.setEditable(enabled);
		// 当设置控件不可用时，设置面板颜色为空
		if (!enabled) {
			this.setBackground(null);
		}
	}

	/**
	 * 获得度分秒的度值
	 *
	 * @return
	 */
	public double getDMSValue() {
		String du = textFieldD.getText();
		String fen = textFieldM.getText();
		String miao = textFieldS.getText();
		if (StringUtilities.isNumber(du) && StringUtilities.isNumeric(fen) && StringUtilities.isNumeric(miao)) {
			double DMSvalue = StringUtilities.getNumber(du) + StringUtilities.getNumber(fen) / 60 + StringUtilities.getNumber(miao) / 3600;
			return DMSvalue;
		}
		return 0;
	}

	/**
	 * 设置度分秒的值
	 * 根据传入的内容，设置三个text的值
	 */
	public void setDMSValue(String dmsValue) {
		if (StringUtilities.isNumber(dmsValue)) {
			double value = StringUtilities.getNumber(dmsValue);

			int du = (int) Math.floor(Math.abs(value));
			double temp = getdPoint(Math.abs(value)) * 60;
			int fen = (int) Math.floor(temp); //获取整数部分
			double miaoDouble = getdPoint(temp) * 60;
			int miao = DoubleUtilities.intValue(miaoDouble);
			if (value < 0) {
				this.textFieldD.setText("-" + String.valueOf(du));
			} else {
				this.textFieldD.setText(String.valueOf(du));
			}
			this.textFieldM.setText(String.valueOf(fen));
			this.textFieldS.setText(String.valueOf(miao));
			setCurrentSText(this.textFieldS.getText().toString());
		}
	}

	//获取小数部分
	private static double getdPoint(double num) {
		double d = num;
		int fInt = (int) d;
		BigDecimal b1 = new BigDecimal(Double.toString(d));
		BigDecimal b2 = new BigDecimal(Integer.toString(fInt));
		double dPoint = b1.subtract(b2).doubleValue();
		return dPoint;
	}
}
