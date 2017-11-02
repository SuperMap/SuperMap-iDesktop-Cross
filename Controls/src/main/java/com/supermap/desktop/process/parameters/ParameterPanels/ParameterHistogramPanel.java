package com.supermap.desktop.process.parameters.ParameterPanels;

import com.supermap.analyst.spatialanalyst.FunctionType;
import com.supermap.analyst.spatialanalyst.GridHistogram;
import com.supermap.analyst.spatialanalyst.HistogramSegmentInfo;
import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.enums.ParameterType;
import com.supermap.desktop.process.parameter.interfaces.AbstractParameter;
import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.interfaces.IParameterPanel;
import com.supermap.desktop.process.parameter.interfaces.ParameterPanelDescribe;
import com.supermap.desktop.process.parameter.ipls.ParameterHistogram;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.ui.controls.TextFields.NumTextFieldLegit;
import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created By Chens on 2017/8/18 0018
 */
@ParameterPanelDescribe(parameterPanelType = ParameterType.HISTOGRAM)
public class ParameterHistogramPanel extends SwingPanel implements IParameterPanel {
	private ParameterHistogram parameterHistogram;
	private JCheckBox checkBox;
	private JLabel labelCount;
	private JLabel labelFunction;
	private NumTextFieldLegit numCount;
	private JComboBox<String> comboBoxFunction;
	private HistogramPanel histogramPanel;
	private JScrollPane scrollPane;

	private static final String NONE = "NONE";
	private static final String ARCSIN = "ARCSIN";
	private static final String LOG = "LOG";

	public ParameterHistogramPanel(IParameter parameterHistogram) {
		super(parameterHistogram);
		this.parameterHistogram = (ParameterHistogram) parameterHistogram;
		initComponent();
		this.parameterHistogram.setGroupCount(Integer.parseInt(numCount.getText()));
		this.parameterHistogram.setCreate(checkBox.isSelected());
		ComponentUIUtilities.setName(this.checkBox, parameter.getDescribe() + "_checkBox");
		ComponentUIUtilities.setName(this.numCount, parameter.getDescribe() + "_textField");
		ComponentUIUtilities.setName(this.comboBoxFunction, parameter.getDescribe() + "_comboBox1");
		ComponentUIUtilities.setName(this.histogramPanel, parameter.getDescribe() + "_panel");
		ComponentUIUtilities.setName(this.scrollPane, parameter.getDescribe() + "_scrollPane");

		panel.setLayout(new GridBagLayout());
		panel.add(checkBox, new GridBagConstraintsHelper(0, 0, 2, 1).setWeight(1, 1).setFill(GridBagConstraints.HORIZONTAL).setInsets(0,0,5,0));
		panel.add(labelCount, new GridBagConstraintsHelper(0, 1, 1, 1).setWeight(0, 1).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.WEST).setInsets(0,0,5,25));
		panel.add(numCount, new GridBagConstraintsHelper(1, 1, 1, 1).setWeight(1, 1).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.WEST).setInsets(0, 20, 5, 0));
		panel.add(labelFunction, new GridBagConstraintsHelper(0, 2, 1, 1).setWeight(0, 1).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.WEST).setInsets(0,0,5,25));
		panel.add(comboBoxFunction, new GridBagConstraintsHelper(1, 2, 1, 1).setWeight(1, 1).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.WEST).setInsets(0, 20, 5, 0));
		panel.add(scrollPane, new GridBagConstraintsHelper(0, 3, 2, 1).setWeight(1, 1).setFill(GridBagConstraints.HORIZONTAL));
		initListener();
	}

	private void initComponent() {
		scrollPane = new JScrollPane();
		histogramPanel =new HistogramPanel();
		histogramPanel.setPreferredSize(new Dimension(0,300));
		checkBox = new JCheckBox();
		labelCount = new JLabel();
		labelFunction = new JLabel();
		numCount = new NumTextFieldLegit();
		comboBoxFunction = new JComboBox<>();
		checkBox.setText(ProcessProperties.getString("String_CheckBox_CreateHistogram"));
		labelCount.setText(ProcessProperties.getString("String_Label_GroupCount"));
		labelFunction.setText(ProcessProperties.getString("String_Label_ChangeFunc"));
		scrollPane.setViewportView(histogramPanel);
		numCount.setText("5");
		numCount.setMinValue(1);
		numCount.setBit(-1);
		comboBoxFunction.addItem(NONE);
		comboBoxFunction.addItem(ARCSIN);
		comboBoxFunction.addItem(LOG);
		labelCount.setVisible(false);
		labelFunction.setVisible(false);
		numCount.setVisible(false);
		comboBoxFunction.setVisible(false);
		parameterHistogram.setCreate(false);
		scrollPane.setVisible(false);
		parameterHistogram.setFunctionType(FunctionType.NONE);
	}

	private void initListener() {
		parameter.addPropertyListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(AbstractParameter.PROPERTY_VALE)) {
					histogramPanel.setGridHistogram(evt.getNewValue() == null ? null : (GridHistogram) evt.getNewValue());
				}
			}
		});
		checkBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				histogramPanel.setGridHistogram(null);
				labelCount.setVisible(checkBox.isSelected());
				labelFunction.setVisible(checkBox.isSelected());
				numCount.setVisible(checkBox.isSelected());
				parameterHistogram.setCreate(checkBox.isSelected());
				scrollPane.setVisible(checkBox.isSelected());
				comboBoxFunction.setVisible(checkBox.isSelected());
			}
		});
		numCount.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				change();
			}

			private void change() {
				if (StringUtilities.isInteger(numCount.getText())) {
					parameterHistogram.setGroupCount(Integer.parseInt(numCount.getText()));
				} else {
					parameterHistogram.setGroupCount(Integer.parseInt(numCount.getBackUpValue()));
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				change();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				change();
			}
		});
		comboBoxFunction.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (comboBoxFunction.getSelectedItem().equals(NONE)) {
					parameterHistogram.setFunctionType(FunctionType.NONE);
				} else if (comboBoxFunction.getSelectedItem().equals(ARCSIN)) {
					parameterHistogram.setFunctionType(FunctionType.ARCSIN);
				} else {
					parameterHistogram.setFunctionType(FunctionType.LOG);
				}
			}
		});
	}

	/**
	 * 直方图面板
	 */
	private class HistogramPanel extends JPanel{
		private GridHistogram gridHistogram;

		public void setGridHistogram(GridHistogram gridHistogram) {
			this.gridHistogram = gridHistogram;
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int width = getWidth();
            int height = 300;
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            if (null == gridHistogram) {
                return;
            }
            int groupCount = gridHistogram.getGroupCount();
            HistogramSegmentInfo[] infos = gridHistogram.getSegmentInfos();

			g.setFont(new Font(null, Font.PLAIN, 15));
			g.setColor(Color.BLACK);
			g.drawLine(30, 0, 30, height - 15); // 画Y坐标
			g.drawLine(30, height - 15, width-20, height - 15);// 画X坐标
			g.drawLine(30,0,20,10);
			g.drawLine(30,0,40,10);
			g.drawLine(width-10,height-15,width-20,height-25);
			g.drawLine(width-10,height-15,width-20,height-5);
			g.drawString(ProcessProperties.getString("String_Histogram_Frequency"),0,20);
			g.drawString(ProcessProperties.getString("String_Histogram_Interval"),width-30,height-25);

			g.setFont(new Font(null, Font.PLAIN, 10));
			double hInterval = (width - 40) / groupCount;
			g.drawString("0", 10, height);
			double maxFrequency = 0;
			for (int i = 0; i < infos.length; i++) {
				g.drawString((double)((int) (infos[i].getRangeMaxValue() * 100)) / 100 + "", (int) hInterval * (i + 1), height);
				maxFrequency = maxFrequency > gridHistogram.getFrequencies()[i] ? maxFrequency : gridHistogram.getFrequencies()[i];
			}
			g.drawString(maxFrequency+"",10,0);
			for (int i = 0; i < infos.length; i++) {
				int heightRange = (int) ((height - 40) * gridHistogram.getFrequencies()[i] / maxFrequency);
				g.setColor(Color.YELLOW);
				g.fillRect(30+(int) hInterval * i,height-15-heightRange, (int) hInterval,heightRange);
				g.setColor(Color.BLACK);
				g.drawRect(30+(int) hInterval * i,height-15-heightRange, (int) hInterval,heightRange);
				g.drawString(gridHistogram.getFrequencies()[i] + "", 30 + (int) hInterval * i, height - 18 - heightRange);
			}
		}
	}
}


