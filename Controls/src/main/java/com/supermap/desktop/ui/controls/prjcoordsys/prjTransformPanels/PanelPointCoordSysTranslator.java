package com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels;

import com.supermap.desktop.Interface.ISmTextFieldLegit;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.TextFields.DMSLatitudeTextField;
import com.supermap.desktop.ui.controls.TextFields.DMSLongitudeTextField;
import com.supermap.desktop.ui.controls.TextFields.SmTextFieldLegit;
import com.supermap.desktop.utilities.DoubleUtilities;
import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by yuanR on 2017/10/12 0012.
 * 坐标点转换功能.坐标点键入面板
 * 面板一共有三态：x/y、经纬度（度形式）、经纬度（度分秒形式）
 * <p>
 * 外部通过model的设置改变面板的显示
 */
public class PanelPointCoordSysTranslator extends JPanel {

	private final static int METERMODEL = 1;
	private final static int DEGREEMODEL = 2;
	private final static int DMSMODEL = 3;
	private int currentModel = METERMODEL;
	private int lastModel = METERMODEL;

	private JLabel labelX = new JLabel("X:");
	private JLabel labelY = new JLabel("Y:");

	private JLabel labelLongtitudeDegree = new JLabel(ControlsProperties.getString("String_Label_LongitudeValue"));
	private JLabel labelLatitudeDegree = new JLabel(ControlsProperties.getString("String_Label_LatitudeValue"));

	private JLabel labelLongtitude = new JLabel(ControlsProperties.getString("String_Label_LongitudeValue"));
	private JLabel labelLatitude = new JLabel(ControlsProperties.getString("String_Label_LatitudeValue"));

	// 单位为“米”的textField
	private UnitMeterTextField unitMeterTextFieldX = new UnitMeterTextField();
	private UnitMeterTextField unitMeterTextFieldY = new UnitMeterTextField();
	// 单位为“度、分、秒”的textField
	private DMSLongitudeTextField textFieldLongitudeValue = new DMSLongitudeTextField();
	private DMSLatitudeTextField textFieldLatitudeValue = new DMSLatitudeTextField();
	// 单位为“度”的textField
	private UnitDegreeTextField unitDegreeTextFieldLongtitude = new UnitDegreeTextField();
	private UnitDegreeTextField unitDegreeTextFieldLatitude = new UnitDegreeTextField();

	private JCheckBox checkBoxShowAsDMS = new JCheckBox(ControlsProperties.getString("String_CheckBox_ShowAsDMS"));


	/**
	 * 默认构造方法
	 */
	public PanelPointCoordSysTranslator() {
		initLayout();
		initListener();
		initStates();
	}

	private void initLayout() {
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setAutoCreateContainerGaps(true);
		groupLayout.setAutoCreateGaps(true);
		this.setLayout(groupLayout);
		// @formatter:off
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
				.addGroup(groupLayout.createSequentialGroup()
						.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(this.labelX)
								.addComponent(this.labelY)
								.addComponent(this.labelLongtitudeDegree)
								.addComponent(this.labelLatitudeDegree)
								.addComponent(this.labelLongtitude)
								.addComponent(this.labelLatitude))
						.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(this.unitMeterTextFieldX)
								.addComponent(this.unitMeterTextFieldY)
								.addComponent(this.unitDegreeTextFieldLongtitude)
								.addComponent(this.unitDegreeTextFieldLatitude)
								.addComponent(this.textFieldLongitudeValue)
								.addComponent(this.textFieldLatitudeValue)))
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(this.checkBoxShowAsDMS)));

		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelX)
						.addComponent(this.unitMeterTextFieldX, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelY)
						.addComponent(this.unitMeterTextFieldY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelLongtitudeDegree)
						.addComponent(this.unitDegreeTextFieldLongtitude, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelLatitudeDegree)
						.addComponent(this.unitDegreeTextFieldLatitude, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelLongtitude)
						.addComponent(this.textFieldLongitudeValue, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelLatitude)
						.addComponent(this.textFieldLatitudeValue, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(this.checkBoxShowAsDMS))
		);
		// @formatter:on
	}

	private void initListener() {
		removeListener();
		this.checkBoxShowAsDMS.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 当点击checkBoxShowAsDMS时，算值，并显示相应面板
				if (checkBoxShowAsDMS.isSelected()) {
					// 两种值得表达方式，当值得改变小于一定精度时，认为两个值相等，不做改变
					textFieldLongitudeValue.setDMSValue(unitDegreeTextFieldLongtitude.getTextField().getText());
					textFieldLatitudeValue.setDMSValue(unitDegreeTextFieldLatitude.getTextField().getText());
				} else {
					if (!textFieldLongitudeValue.getCurrentSText().equals(textFieldLongitudeValue.getTextFieldS().getText().toString())) {
						unitDegreeTextFieldLongtitude.getTextField().setText(DoubleUtilities.toString(textFieldLongitudeValue.getDMSValue(), 15));
					}
					if (!textFieldLatitudeValue.getCurrentSText().equals(textFieldLatitudeValue.getTextFieldS().getText().toString())) {
						unitDegreeTextFieldLatitude.getTextField().setText(DoubleUtilities.toString(textFieldLatitudeValue.getDMSValue(), 15));
					}
				}
				setComponentVisible();
			}
		});

		//this.textFieldLongitudeValue.getTextFieldD().addCaretListener(LongtitudeTextFieldCaretListener);
		//this.textFieldLongitudeValue.getTextFieldM().addCaretListener(LongtitudeTextFieldCaretListener);
		//this.textFieldLongitudeValue.getTextFieldS().addCaretListener(LongtitudeTextFieldCaretListener);
		//this.unitDegreeTextFieldLongtitude.getTextField().addCaretListener(LongtitudeTextFieldCaretListener);

	}

	private void removeListener() {
		//this.textFieldLongitudeValue.getTextFieldD().removeCaretListener(LongtitudeTextFieldCaretListener);
		//this.textFieldLongitudeValue.getTextFieldM().removeCaretListener(LongtitudeTextFieldCaretListener);
		//this.textFieldLongitudeValue.getTextFieldS().removeCaretListener(LongtitudeTextFieldCaretListener);
		//this.unitDegreeTextFieldLongtitude.getTextField().removeCaretListener(LongtitudeTextFieldCaretListener);
	}

	public void initStates() {
		this.unitDegreeTextFieldLongtitude.getTextField().setToolTipText(ControlsProperties.getString("String_RangeSection") + "[-180,180]");
		this.unitDegreeTextFieldLongtitude.setSmTextFieldLegit(new ISmTextFieldLegit() {
			@Override
			public boolean isTextFieldValueLegit(String textFieldValue) {
				if (StringUtilities.isNullOrEmpty(textFieldValue)) {
					return false;
				}
				try {
					Double value = Double.valueOf(textFieldValue.replace("-", ""));
					if (value > 180) {
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
		});
		this.unitDegreeTextFieldLatitude.getTextField().setToolTipText(ControlsProperties.getString("String_RangeSection") + "[-90,90]");
		this.unitDegreeTextFieldLatitude.setSmTextFieldLegit(new ISmTextFieldLegit() {
			@Override
			public boolean isTextFieldValueLegit(String textFieldValue) {
				if (StringUtilities.isNullOrEmpty(textFieldValue)) {
					return false;
				}
				try {
					Double value = Double.valueOf(textFieldValue.replace("-", ""));
					if (value > 90) {
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
		});

		this.checkBoxShowAsDMS.setEnabled(false);
		setComponentVisible();
	}


	/**
	 * 设置控件是否可见
	 */
	private void setComponentVisible() {

		this.labelX.setVisible(!this.checkBoxShowAsDMS.isSelected() && getCurrentModel() == METERMODEL);
		this.labelY.setVisible(!this.checkBoxShowAsDMS.isSelected() && getCurrentModel() == METERMODEL);
		this.unitMeterTextFieldX.setVisible(!this.checkBoxShowAsDMS.isSelected() && getCurrentModel() == METERMODEL);
		this.unitMeterTextFieldY.setVisible(!this.checkBoxShowAsDMS.isSelected() && getCurrentModel() == METERMODEL);

		this.labelLongtitudeDegree.setVisible(!this.checkBoxShowAsDMS.isSelected() && getCurrentModel() == DEGREEMODEL);
		this.labelLatitudeDegree.setVisible(!this.checkBoxShowAsDMS.isSelected() && getCurrentModel() == DEGREEMODEL);
		this.unitDegreeTextFieldLongtitude.setVisible(!this.checkBoxShowAsDMS.isSelected() && getCurrentModel() == DEGREEMODEL);
		this.unitDegreeTextFieldLatitude.setVisible(!this.checkBoxShowAsDMS.isSelected() && getCurrentModel() == DEGREEMODEL);

		this.labelLongtitude.setVisible(this.checkBoxShowAsDMS.isSelected());
		this.labelLatitude.setVisible(this.checkBoxShowAsDMS.isSelected());
		this.textFieldLongitudeValue.setVisible(this.checkBoxShowAsDMS.isSelected());
		this.textFieldLatitudeValue.setVisible(this.checkBoxShowAsDMS.isSelected());
	}

	/**
	 * 设置面板不可编辑但可以赋值其内容，并不是不可用
	 *
	 * @param enabled
	 */
	public void setComponentEditable(Boolean enabled) {
		this.unitMeterTextFieldX.setTextFieldEditable(enabled);
		this.unitMeterTextFieldY.setTextFieldEditable(enabled);
		this.unitDegreeTextFieldLongtitude.setTextFieldEditable(enabled);
		this.unitDegreeTextFieldLatitude.setTextFieldEditable(enabled);
		this.textFieldLongitudeValue.setPanelEditable(enabled);
		this.textFieldLatitudeValue.setPanelEditable(enabled);
		this.checkBoxShowAsDMS.setEnabled(!(getCurrentModel() == METERMODEL));
	}


	/**
	 * 获得当前model
	 *
	 * @return
	 */
	private int getCurrentModel() {
		return currentModel;
	}

	/**
	 * 设置当前model
	 * 当改变model时，设置面板显示也改变
	 * 暂时只接受：model=1和model=2，其他属性无效果
	 * <p>
	 * 当投影改变时，引发model改变，当两次model相同时，不做textField值改变
	 * *
	 *
	 * @param currentModel
	 */
	public void setCurrentModel(int currentModel) {
		if (currentModel == DEGREEMODEL || currentModel == METERMODEL) {
			this.currentModel = currentModel;
		}
		// 为逻辑清晰考虑，外界model属性改变时，无法直接显示度分秒类型面板，因此先设置checkBox不选中，并且不可用
		this.checkBoxShowAsDMS.setSelected(false);
		this.checkBoxShowAsDMS.setEnabled(this.currentModel == DEGREEMODEL);

		if (this.currentModel == DEGREEMODEL && this.lastModel != this.currentModel) {
			// 米——>度，需要考虑是否超出限制的问题
			if (StringUtilities.getNumber(this.unitMeterTextFieldX.getTextField().getText()) >= -180 && StringUtilities.getNumber(this.unitMeterTextFieldX.getTextField().getText()) <= 180) {
				this.unitDegreeTextFieldLongtitude.getTextField().setText(this.unitMeterTextFieldX.getTextField().getText().toString());
			}
			if (StringUtilities.getNumber(this.unitMeterTextFieldY.getTextField().getText()) >= -90 && StringUtilities.getNumber(this.unitMeterTextFieldY.getTextField().getText()) <= 90) {
				this.unitDegreeTextFieldLatitude.getTextField().setText(this.unitMeterTextFieldY.getTextField().getText().toString());
			}
			this.lastModel = this.currentModel;
		} else if (this.currentModel == METERMODEL && this.lastModel != this.currentModel) {
			this.unitMeterTextFieldX.getTextField().setText(this.unitDegreeTextFieldLongtitude.getTextField().getText().toString());
			this.unitMeterTextFieldY.getTextField().setText(this.unitDegreeTextFieldLatitude.getTextField().getText().toString());
			this.lastModel = this.currentModel;
		}
		setComponentVisible();
	}

	public double getXValue() {
		// 获得值之前先同步三种状态下的值
		if (this.checkBoxShowAsDMS.isSelected()) {
			this.unitDegreeTextFieldLongtitude.getTextField().setText(DoubleUtilities.getFormatString(this.textFieldLongitudeValue.getDMSValue()));
		}

		if (this.currentModel == METERMODEL) {
			return StringUtilities.getNumber(this.unitMeterTextFieldX.getTextField().getText());
		} else {
			return StringUtilities.getNumber(this.unitDegreeTextFieldLongtitude.getTextField().getText());
		}
	}

	public double getYValue() {
		// 获得值之前先同步三种状态下的值
		if (this.checkBoxShowAsDMS.isSelected()) {
			this.unitDegreeTextFieldLatitude.getTextField().setText(DoubleUtilities.getFormatString(this.textFieldLatitudeValue.getDMSValue()));
		}

		if (this.currentModel == METERMODEL) {
			return StringUtilities.getNumber(this.unitMeterTextFieldY.getTextField().getText());
		} else {
			return StringUtilities.getNumber(this.unitDegreeTextFieldLatitude.getTextField().getText());
		}
	}

	public void setXValue(String xValue) {
		this.unitMeterTextFieldX.getTextField().setText(xValue);
		this.unitDegreeTextFieldLongtitude.getTextField().setText(xValue);
		this.textFieldLongitudeValue.setDMSValue(xValue);
	}

	public void setYValue(String yValue) {
		this.unitMeterTextFieldY.getTextField().setText(yValue);
		this.unitDegreeTextFieldLatitude.getTextField().setText(yValue);
		this.textFieldLatitudeValue.setDMSValue(yValue);
	}


	/**
	 * 自带“米”单位的textField
	 */
	class UnitMeterTextField extends JPanel {
		public SmTextFieldLegit getTextField() {
			return textField;
		}

		private SmTextFieldLegit textField;
		private JLabel labelUnitMeter;

		// 键盘限制输入事件
		private KeyListener keyAdapter = new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				int keyChar = e.getKeyChar();
				// 度textField键盘输入限定
				String text = textField.getText();
				//“-”负号在首位，并且只能输入一次
				if (textField.getCaretPosition() != 0 && keyChar == KeyEvent.VK_MINUS || text.contains("-") && keyChar == KeyEvent.VK_MINUS) {// keyChar == 45代表负号
					e.consume();
				}

				//“.”不能在首位，并且只能输入一次
				if (StringUtilities.isNullOrEmpty(text) && keyChar == KeyEvent.VK_PERIOD || text.contains(".") && keyChar == KeyEvent.VK_PERIOD) {//keyChar == 46代表小数点
					e.consume();
				}
				// 负号后面不能跟小数点
				if (keyChar == KeyEvent.VK_PERIOD && text.equals("-")) {
					e.consume();
				}
				// 限制只能输入数字、负号、小数点
				if (keyChar < 45 || keyChar > 57) {
					e.consume();
				}
				if (keyChar == KeyEvent.VK_SLASH) {
					e.consume();
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
					// 判断是否为数字就好
					Double.valueOf(textFieldValue.replace("-", ""));
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


		public UnitMeterTextField() {
			initComponent();
			initLayout();
			registEvents();
		}

		private void initComponent() {
			this.textField = new SmTextFieldLegit("0");
			this.labelUnitMeter = new JLabel(CoreProperties.getString("String_DistanceUnit_Meter"));
		}

		private void initLayout() {
			this.setPreferredSize(new Dimension(100, 23));
			GroupLayout groupLayoutPanel = new GroupLayout(this);
			this.setLayout(groupLayoutPanel);
			// @formatter off
			groupLayoutPanel.setHorizontalGroup(groupLayoutPanel.createSequentialGroup()
					.addComponent(this.textField)
					.addComponent(this.labelUnitMeter));
			groupLayoutPanel.setVerticalGroup(groupLayoutPanel.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(this.textField)
					.addComponent(this.labelUnitMeter));
			// @formatter on
		}


		private void registEvents() {
			removeListener();
			this.textField.addKeyListener(this.keyAdapter);
			this.textField.setSmTextFieldLegit(iSmTextFieldLegit);

		}

		private void removeListener() {
			this.textField.removeKeyListener(this.keyAdapter);
			this.textField.removeEvents();
		}

		public void setTextFieldEditable(Boolean enabled) {
			this.textField.setEditable(enabled);
		}
	}

	/**
	 * 自带“度”单位的textField
	 */
	class UnitDegreeTextField extends JPanel {
		public SmTextFieldLegit getTextField() {
			return textField;
		}

		private SmTextFieldLegit textField;
		private JLabel labelUnitDegree;

		// 键盘限制输入事件
		private KeyListener keyAdapter = new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				int keyChar = e.getKeyChar();
				// 度textField键盘输入限定
				String text = textField.getText();
				//“-”负号在首位，并且只能输入一次
				if (textField.getCaretPosition() != 0 && keyChar == KeyEvent.VK_MINUS || text.contains("-") && keyChar == KeyEvent.VK_MINUS) {// keyChar == 45代表负号
					e.consume();
				}
				//“.”不能在首位，并且只能输入一次
				if (StringUtilities.isNullOrEmpty(text) && keyChar == KeyEvent.VK_PERIOD || text.contains(".") && keyChar == KeyEvent.VK_PERIOD) {//keyChar == 46代表小数点
					e.consume();
				}
				// 负号后面不能跟小数点
				if (keyChar == KeyEvent.VK_PERIOD && text.equals("-")) {
					e.consume();
				}
				// 限制只能输入数字、负号、小数点
				if (keyChar < 45 || keyChar > 57) {
					e.consume();
				}
				if (keyChar == KeyEvent.VK_SLASH) {
					e.consume();
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
					Double value = Double.valueOf(textFieldValue.replace("-", ""));
					if (value > 180) {
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


		public UnitDegreeTextField() {
			initComponent();
			initLayout();
			registEvents();
		}

		private void initComponent() {
			this.textField = new SmTextFieldLegit("0");
			this.textField.setToolTipText(ControlsProperties.getString("String_RangeSection") + "[-180,180]");
			this.labelUnitDegree = new JLabel(CoreProperties.getString("String_AngleUnit_Degree"));
		}

		private void initLayout() {
			this.setPreferredSize(new Dimension(100, 23));
			GroupLayout groupLayoutPanel = new GroupLayout(this);
			this.setLayout(groupLayoutPanel);
			// @formatter off
			groupLayoutPanel.setHorizontalGroup(groupLayoutPanel.createSequentialGroup()
					.addComponent(this.textField)
					.addComponent(this.labelUnitDegree));
			groupLayoutPanel.setVerticalGroup(groupLayoutPanel.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(this.textField)
					.addComponent(this.labelUnitDegree));
			// @formatter on
		}

		private void registEvents() {
			removeListener();
			this.textField.addKeyListener(this.keyAdapter);
			//this.textField.setSmTextFieldLegit(iSmTextFieldLegit);
		}

		public void setSmTextFieldLegit(ISmTextFieldLegit iSmTextFieldLegit) {
			this.textField.setSmTextFieldLegit(iSmTextFieldLegit);
		}

		private void removeListener() {
			this.textField.removeKeyListener(this.keyAdapter);
			this.textField.removeEvents();
		}

		public void setTextFieldEditable(Boolean enabled) {
			this.textField.setEditable(enabled);
		}
	}
}
