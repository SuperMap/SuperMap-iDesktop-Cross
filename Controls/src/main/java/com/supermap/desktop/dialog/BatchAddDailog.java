package com.supermap.desktop.dialog;

import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.ui.controls.SmDialog;
import com.supermap.desktop.ui.controls.TextFields.RightValueListener;
import com.supermap.desktop.ui.controls.TextFields.WaringTextField;
import com.supermap.desktop.ui.controls.button.SmButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by lixiaoyao on 2017/3/14.
 */
public class BatchAddDailog extends SmDialog {
    private JLabel startValue = new JLabel();
    private JLabel endValue = new JLabel();
    private JRadioButton stepLength = new JRadioButton();
    private JRadioButton seriesNum = new JRadioButton();
    private ButtonGroup buttonGroup = new ButtonGroup();
    private JCheckBox resetEndValue = new JCheckBox();
    private WaringTextField startValueText;
    private WaringTextField endValueText;
    private WaringTextField stepLengthText;
    private WaringTextField seriesNumText;
    private SmButton okSmButton = new SmButton();
    private SmButton cancelSmButton = new SmButton();
    private final int ROW_HRIGHT = 23;
    private boolean isNeedResetCalculEndValue = false;
    private double inputStartValue;
    private double inputEndVale;
    private double inputStepLength;
    private int inputSeriesNum;
    private double resultKeys[];

    public BatchAddDailog(double startValue, double endVale, int seriesNum, JFrame owner, boolean model) {
        super(owner, model);
        this.inputStartValue = startValue;
        this.inputEndVale = endVale;
        this.inputSeriesNum = seriesNum;
        initComponents();
        initResources();
        removeEvents();
        registerEvents();
    }

    private ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == BatchAddDailog.this.okSmButton) {
                calculResultKeys();
                BatchAddDailog.this.setDialogResult(DialogResult.OK);
                BatchAddDailog.this.dispose();
            } else if (e.getSource() == BatchAddDailog.this.cancelSmButton) {
                BatchAddDailog.this.dispose();
            } else if (e.getSource() == BatchAddDailog.this.resetEndValue) {
                isNeedResetCalculEndValue = BatchAddDailog.this.resetEndValue.isSelected();
            } else if (e.getSource() == BatchAddDailog.this.stepLength) {
                resetTextEnableForRadio();
            } else if (e.getSource() == BatchAddDailog.this.seriesNum) {
                resetTextEnableForRadio();
            }
        }
    };

    private RightValueListener rightStartValue = new RightValueListener() {
        @Override
        public void update(String value) {
            if (!value.isEmpty() && Double.compare(inputStartValue, Double.valueOf(value)) != 0) {
                startValurOrEndValueChange();
                inputStartValue = Double.valueOf(value);
            }
        }
    };

    private RightValueListener rightEndValue = new RightValueListener() {
        @Override
        public void update(String value) {
            if (!value.isEmpty() && Double.compare(inputEndVale, Double.valueOf(value)) != 0) {
                startValurOrEndValueChange();
                inputEndVale = Double.valueOf(value);
            }
        }
    };

    private RightValueListener rightStepValue = new RightValueListener() {
        @Override
        public void update(String value) {
            if (!value.isEmpty() && Double.compare(inputStepLength, Double.valueOf(value)) != 0 && !endValueText.getText().isEmpty() &&!startValueText.getText().isEmpty()) {
                double currentStepValue = Double.valueOf(value);
                double currentSeriesNum = (Double.valueOf(endValueText.getText()) - Double.valueOf(startValueText.getText())) / currentStepValue;
                Integer seriesNum = (int) Math.ceil(Math.abs(currentSeriesNum));
                if (Double.compare(Double.valueOf(startValueText.getText()) + seriesNum * currentStepValue, Double.valueOf(endValueText.getText())) == -1 || Double.compare(Double.valueOf(startValueText.getText()) + seriesNum * currentStepValue, Double.valueOf(endValueText.getText())) == 0) {
                    seriesNum = seriesNum + 1;
                }
                inputSeriesNum = seriesNum;
                seriesNumText.setText(String.valueOf(seriesNum));
            }
        }
    };

    private RightValueListener rightSeriesNum = new RightValueListener() {
        @Override
        public void update(String value) {
            if (!value.isEmpty() && Integer.compare(inputSeriesNum, Integer.valueOf(value)) != 0 && !endValueText.getText().isEmpty() &&!startValueText.getText().isEmpty()) {
                Integer currentSeriesNum = Integer.valueOf(value);
                double currentStepValue = (Double.valueOf(endValueText.getText()) - Double.valueOf(startValueText.getText())) / (currentSeriesNum - 1);
                inputStepLength = currentStepValue;
                stepLengthText.setText(String.valueOf(currentStepValue));
                inputSeriesNum=Integer.valueOf(value);
            }
        }
    };


    private void initComponents() {
        Dimension dimension = new Dimension(336, 226);
        setSize(dimension);
        setMinimumSize(dimension);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(okSmButton);
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setAutoCreateContainerGaps(true);
        groupLayout.setAutoCreateGaps(true);

        this.startValueText = new WaringTextField(String.valueOf(this.inputStartValue), true);
        this.startValueText.setInitInfo(Short.MIN_VALUE, Short.MAX_VALUE, WaringTextField.FLOAT_TYPE, "null");
        this.endValueText = new WaringTextField(String.valueOf(this.inputEndVale), true);
        this.endValueText.setInitInfo(Short.MIN_VALUE, Short.MAX_VALUE, WaringTextField.FLOAT_TYPE, "null");
        this.inputStepLength = (this.inputEndVale - this.inputStartValue) / (this.inputSeriesNum-1);
        this.stepLengthText = new WaringTextField(String.valueOf(this.inputStepLength), true);
        this.stepLengthText.setInitInfo(Short.MIN_VALUE, Short.MAX_VALUE, WaringTextField.FLOAT_TYPE, "null");
        this.seriesNumText = new WaringTextField(String.valueOf(this.inputSeriesNum), true);
        this.seriesNumText.setInitInfo(2, Short.MAX_VALUE, WaringTextField.INTEGER_TYPE, "null");

        groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
                .addGroup(groupLayout.createParallelGroup()
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(startValue)
                                        .addComponent(endValue)
                                        .addComponent(stepLength)
                                        .addComponent(seriesNum))
                                .addContainerGap(50, 50)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(startValueText)
                                        .addComponent(endValueText)
                                        .addComponent(stepLengthText)
                                        .addComponent(seriesNumText)))
                        .addComponent(resetEndValue)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(5, 5, Integer.MAX_VALUE)
                                .addComponent(okSmButton)
                                .addComponent(cancelSmButton)))
        );
        groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(startValue)
                        .addComponent(startValueText, ROW_HRIGHT, ROW_HRIGHT, ROW_HRIGHT))
                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(endValue)
                        .addComponent(endValueText, ROW_HRIGHT, ROW_HRIGHT, ROW_HRIGHT))
                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(stepLength)
                        .addComponent(stepLengthText, ROW_HRIGHT, ROW_HRIGHT, ROW_HRIGHT))
                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(seriesNum)
                        .addComponent(seriesNumText, ROW_HRIGHT, ROW_HRIGHT, ROW_HRIGHT))
                .addComponent(resetEndValue)
                .addContainerGap(0, Short.MAX_VALUE)
                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(okSmButton)
                        .addComponent(cancelSmButton))
        );
        buttonGroup.add(stepLength);
        buttonGroup.add(seriesNum);
        getContentPane().setLayout(groupLayout);
        this.seriesNum.setSelected(true);
        resetTextEnableForRadio();
        setComponentName();
    }
    private void setComponentName() {
        ComponentUIUtilities.setName(this.startValue, "BatchAddColorTableDailog_startValue");
        ComponentUIUtilities.setName(this.endValue, "BatchAddColorTableDailog_endValue");
        ComponentUIUtilities.setName(this.stepLength, "BatchAddColorTableDailog_stepLength");
        ComponentUIUtilities.setName(this.seriesNum, "BatchAddColorTableDailog_seriesNum");
        ComponentUIUtilities.setName(this.resetEndValue, "BatchAddColorTableDailog_resetEndValue");
        ComponentUIUtilities.setName(this.startValueText, "BatchAddColorTableDailog_startValueText");
        ComponentUIUtilities.setName(this.endValueText, "BatchAddColorTableDailog_endValueText");
        ComponentUIUtilities.setName(this.stepLengthText, "BatchAddColorTableDailog_stepLengthText");
        ComponentUIUtilities.setName(this.seriesNumText, "BatchAddColorTableDailog_seriesNumText");
        ComponentUIUtilities.setName(this.okSmButton, "BatchAddColorTableDailog_okSmButton");
        ComponentUIUtilities.setName(this.cancelSmButton, "BatchAddColorTableDailog_cancelSmButton");
    }
    private void initResources() {
        setTitle(ControlsProperties.getString("String_AddRange"));
        this.startValue.setText(ControlsProperties.getString("String_BatchAddColorTableMinValue"));
        this.endValue.setText(ControlsProperties.getString("String_BatchAddColorTableMaxValue"));
        this.stepLength.setText(ControlsProperties.getString("String_BatchAddColorTableStepLength"));
        this.seriesNum.setText(ControlsProperties.getString("String_BatchAddColorTableSeriesNum"));
        this.resetEndValue.setText(ControlsProperties.getString("String_BatchAddColorTableResetCalculValue"));
        this.okSmButton.setText(ControlsProperties.getString("String_BatchAddColorTableOKButton"));
        this.cancelSmButton.setText(ControlsProperties.getString("String_Cancel"));
    }

    private void registerEvents() {
        this.okSmButton.addActionListener(actionListener);
        this.cancelSmButton.addActionListener(actionListener);
        this.resetEndValue.addActionListener(actionListener);
        this.stepLength.addActionListener(actionListener);
        this.seriesNum.addActionListener(actionListener);
        this.startValueText.addRightValueListener(rightStartValue);
        this.endValueText.addRightValueListener(rightEndValue);
        this.stepLengthText.addRightValueListener(rightStepValue);
        this.seriesNumText.addRightValueListener(rightSeriesNum);
    }

    private void removeEvents() {
        this.okSmButton.removeActionListener(actionListener);
        this.cancelSmButton.removeActionListener(actionListener);
        this.resetEndValue.removeActionListener(actionListener);
        this.stepLength.removeActionListener(actionListener);
        this.seriesNum.removeActionListener(actionListener);
        this.startValueText.removeRightValueListener(rightStartValue);
        this.endValueText.removeRightValueListener(rightEndValue);
        this.stepLengthText.removeRightValueListener(rightStepValue);
        this.seriesNumText.removeRightValueListener(rightSeriesNum);
    }

    //  起始值或者结尾值更改则更改相对应的步长与级数
    private void startValurOrEndValueChange() {
        if (this.seriesNum.isSelected() && !this.endValueText.getText().isEmpty() && !this.seriesNumText.getText().isEmpty() && !this.startValueText.getText().isEmpty()) {
            Integer currentSeriesNum = Integer.valueOf(this.seriesNumText.getText());
            double currentStepValue = (Double.valueOf(this.endValueText.getText()) - Double.valueOf(this.startValueText.getText())) / (currentSeriesNum-1);
            this.inputStepLength = currentStepValue;
            this.stepLengthText.setText(String.valueOf(currentStepValue));
        } else if (!this.seriesNum.isSelected() && !this.endValueText.getText().isEmpty() && !this.stepLengthText.getText().isEmpty() && !this.startValueText.getText().isEmpty()) {
            double currentStepValue = Double.valueOf(this.stepLengthText.getText());
            double currentSeriesNum = (Double.valueOf(this.endValueText.getText()) - Double.valueOf(this.startValueText.getText())) / currentStepValue;
            Integer cSeriesNum = Math.abs((int) currentSeriesNum);
            this.inputSeriesNum = cSeriesNum;
            this.seriesNumText.setText(String.valueOf(cSeriesNum));
        }
    }

    private void calculResultKeys() {
        if (!this.startValueText.isError() && !this.endValueText.isError() && !this.stepLengthText.isError() && !this.seriesNumText.isError()) {
            Integer currentSeriesNum = Integer.valueOf(this.seriesNumText.getText());
            double start = Double.valueOf(this.startValueText.getText());
            double end = Double.valueOf(this.endValueText.getText());
            double step = Double.valueOf(this.stepLengthText.getText());
            if (Double.compare(start,end)==1 && Double.compare(step,0)==1){
                step=0-step;
            }
            this.resultKeys = new double[currentSeriesNum];
            for (int i = 0; i < currentSeriesNum; i++) {
                if (i + 1 == currentSeriesNum && !isNeedResetCalculEndValue) {
                    resultKeys[i] = end;
                } else {
                    resultKeys[i] = start + i * step;
                }
            }
        }
    }

    public double[] getResultKeys() {
        return this.resultKeys;
    }

    private void resetTextEnableForRadio() {
        if (this.seriesNum.isSelected()) {
            this.seriesNumText.setEnable(true);
            this.stepLengthText.setEnable(false);
            this.resetEndValue.setEnabled(false);
            this.resetEndValue.setSelected(true);
        } else {
            this.seriesNumText.setEnable(false);
            this.stepLengthText.setEnable(true);
            this.resetEndValue.setEnabled(true);
            this.resetEndValue.setSelected(false);
        }
    }

}
