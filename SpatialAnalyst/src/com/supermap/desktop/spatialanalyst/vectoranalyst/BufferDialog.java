package com.supermap.desktop.spatialanalyst.vectoranalyst;

import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.desktop.Application;
import com.supermap.desktop.spatialanalyst.SpatialAnalystProperties;
import com.supermap.desktop.ui.controls.DatasetComboBox;
import com.supermap.desktop.ui.controls.SmDialog;
import com.supermap.desktop.utilties.SystemPropertyUtilties;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BufferDialog extends SmDialog {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JLabel labelDataType;
	private JRadioButton radioButtonPointOrRegion = new JRadioButton("PointOrRegion");
	private JRadioButton radioButtonLine = new JRadioButton("Line");
	private JPanel panelDataType;
	private JPanel panelBufferBasic;
	private JPanel panelButtonGroup;
	private ButtonGroup buttonGroup;
	private LocalActionListener localActionListener = new LocalActionListener();
	private PanelButton panelButton;
	private final static String DATASET_NOT_NULL = "源数据集不能为空";
	public final static Dimension DEFAULT_WINDOWS_BUFFER_LINE_DIMENSION = new Dimension(575, 435);
	public final static Dimension DEFAULT_WINDOWS_BUFFER_POINTORREGION_DIMENSION = new Dimension(575, 332);
	public final static Dimension DEFAULT_LINUX_BUFFER_LINE_DIMENSION = new Dimension(670, 470);
	public final static Dimension DEFAULT_LINUX_BUFFER_POINTORREGION_DIMENSION = new Dimension(670, 370);
	private DoSome some = new DoSome() {
		@Override
		public void doSome(boolean enable) {
			panelButton.getButtonOk().setEnabled(enable);
		}
	};

	public BufferDialog() {
		super();
		initPanelBufferBasic();
		setBufferFactory();
		setLocationRelativeTo(null);
		setResizable(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private void initPanelBufferBasic() {
		this.setTitle(SpatialAnalystProperties.getString("String_SingleBufferAnalysis_Capital"));

		Dataset[] activeDatasets = Application.getActiveApplication().getActiveDatasets();

		if (activeDatasets.length > 0
				&& (activeDatasets[0].getType() == DatasetType.POINT || activeDatasets[0].getType() == DatasetType.POINT3D
						|| activeDatasets[0].getType() == DatasetType.REGION || activeDatasets[0].getType() == DatasetType.REGION3D)) {

			this.panelBufferBasic = new PanelPointOrRegionAnalyst();
			setSize(getPointPanelDimension());
			this.radioButtonPointOrRegion.setSelected(true);
			((PanelPointOrRegionAnalyst) panelBufferBasic).setSome(some);
		} else {
			this.panelBufferBasic = new PanelLineBufferAnalyst();
			setSize(getLinePanelDimension());
			this.radioButtonLine.setSelected(true);
			((PanelLineBufferAnalyst) panelBufferBasic).setSome(some);
		}
	}

	private void setBufferFactory() {
		removeRegisterEvent();
		initComponent();
		initResources();
		registerEvent();
	}

	private void initComponent() {
		this.labelDataType = new JLabel("DataType");
		this.panelDataType = new JPanel();
		this.panelButton = new PanelButton();
		this.panelButtonGroup = new JPanel();
		this.buttonGroup = new ButtonGroup();
		this.buttonGroup.add(this.radioButtonPointOrRegion);
		this.buttonGroup.add(this.radioButtonLine);

		GroupLayout panelButtonGroupLayout = new GroupLayout(this.panelButtonGroup);
		this.panelButtonGroup.setLayout(panelButtonGroupLayout);

		//@formatter:off
          panelButtonGroupLayout.setHorizontalGroup(panelButtonGroupLayout.createSequentialGroup()
                    .addComponent(this.radioButtonPointOrRegion).addGap(30)
                    .addComponent(this.radioButtonLine));
          panelButtonGroupLayout.setVerticalGroup(panelButtonGroupLayout.createSequentialGroup()
                    .addGroup(panelButtonGroupLayout.createParallelGroup(Alignment.CENTER)
                              .addComponent(this.radioButtonPointOrRegion)
                              .addComponent(this.radioButtonLine)));
         
        //@formatter:on

		GroupLayout panelDataTypeLayout = new GroupLayout(this.panelDataType);
		this.panelDataType.setLayout(panelDataTypeLayout);

		//@formatter:off
          panelDataTypeLayout.setHorizontalGroup(panelDataTypeLayout.createSequentialGroup()
                    .addGap(20)
                    .addComponent(this.labelDataType).addGap(30)
                    .addComponent(this.panelButtonGroup));
         
          panelDataTypeLayout.setVerticalGroup(panelDataTypeLayout.createSequentialGroup()
                    .addGap(10)
                    .addGroup(panelDataTypeLayout.createParallelGroup(Alignment.CENTER)
                              .addComponent(this.labelDataType)
                              .addComponent(this.panelButtonGroup)));

         
          GridBagLayout gridBagConstraints = new GridBagLayout();
          this.getContentPane().setLayout(gridBagConstraints);
         
          this.setLayout(new BorderLayout());
          this.add(panelDataType, BorderLayout.NORTH);
          this.add(panelBufferBasic, BorderLayout.CENTER);
          this.add(panelButton, BorderLayout.SOUTH);
     }

     private void initResources() {
          this.labelDataType.setText(SpatialAnalystProperties.getString("String_BufferAnalysis_DataType"));
          this.radioButtonLine.setText(SpatialAnalystProperties.getString("String_BufferAnalysis_Line"));
          this.radioButtonPointOrRegion.setText(SpatialAnalystProperties.getString("String_BufferAnalysis_PointAndRegion"));
     }
    

     private void registerEvent() {
          this.radioButtonPointOrRegion.addActionListener(this.localActionListener);
          this.radioButtonLine.addActionListener(this.localActionListener);
          this.panelButton.getButtonOk().addActionListener(this.localActionListener);
          this.panelButton.getButtonCancel().addActionListener(this.localActionListener);
         
          if (panelBufferBasic instanceof PanelLineBufferAnalyst) {
               ((PanelLineBufferAnalyst)panelBufferBasic).addListener();
          }else if (panelBufferBasic instanceof PanelPointOrRegionAnalyst) {
			((PanelPointOrRegionAnalyst)panelBufferBasic).addListener();
		}
     }
    
     private void removeRegisterEvent(){
          this.radioButtonLine.removeActionListener(this.localActionListener);
          this.radioButtonPointOrRegion.removeActionListener(this.localActionListener);
     }

	private Dimension getLinePanelDimension() {
		if (SystemPropertyUtilties.isWindows()) {
			return BufferDialog.DEFAULT_WINDOWS_BUFFER_LINE_DIMENSION;
		}else{
			return BufferDialog.DEFAULT_LINUX_BUFFER_LINE_DIMENSION;
		}

	}

	private Dimension getPointPanelDimension() {
		if (SystemPropertyUtilties.isWindows()) {
			return BufferDialog.DEFAULT_WINDOWS_BUFFER_POINTORREGION_DIMENSION;
		}else{
			return BufferDialog.DEFAULT_LINUX_BUFFER_POINTORREGION_DIMENSION;
		}
	}

	class LocalActionListener implements ActionListener {
          private boolean flag;
         
          @Override
          public void actionPerformed(ActionEvent e) {
               if (e.getSource() == radioButtonPointOrRegion) {
            	   if(panelBufferBasic instanceof PanelPointOrRegionAnalyst){
            		   ((PanelPointOrRegionAnalyst) panelBufferBasic).setSome(null);
            	   }else {
            		   ((PanelLineBufferAnalyst) panelBufferBasic).setSome(null);
            	   }
	               BufferDialog.this.getContentPane().remove(panelBufferBasic);
                    panelBufferBasic = new PanelPointOrRegionAnalyst();
                    BufferDialog.this.getContentPane().add(panelBufferBasic);
                    ((PanelPointOrRegionAnalyst) panelBufferBasic).addListener();
	               ((PanelPointOrRegionAnalyst) panelBufferBasic).setSome(some);
                    setSize(getPointPanelDimension());
               } else if (e.getSource() == radioButtonLine) {
            	   if(panelBufferBasic instanceof PanelPointOrRegionAnalyst){
            		   ((PanelPointOrRegionAnalyst) panelBufferBasic).setSome(null);
            	   }else {
            		   ((PanelLineBufferAnalyst) panelBufferBasic).setSome(null);
            	   }
	               BufferDialog.this.getContentPane().remove(panelBufferBasic);
	               panelBufferBasic = new PanelLineBufferAnalyst();
	               BufferDialog.this.getContentPane().add(panelBufferBasic);
	               setSize(getLinePanelDimension());
	               ((PanelLineBufferAnalyst) panelBufferBasic).addListener();
	               ((PanelLineBufferAnalyst) panelBufferBasic).setSome(some);
                    
               }else if (e.getSource() == panelButton.getButtonOk()) {
                    try {
						if(panelBufferBasic instanceof PanelPointOrRegionAnalyst){
						     flag = ((PanelPointOrRegionAnalyst)panelBufferBasic).createCurrentBuffer();
						     if(!((PanelPointOrRegionAnalyst) panelBufferBasic).isButtonEnabled()){
						    	 flag = false;
						    	 JOptionPane.showMessageDialog(BufferDialog.this, DATASET_NOT_NULL);
						     }
						     
						}else if (panelBufferBasic instanceof  PanelLineBufferAnalyst) {
						     flag = ((PanelLineBufferAnalyst)panelBufferBasic).CreateCurrentBuffer();
						     if(!((PanelLineBufferAnalyst) panelBufferBasic).isButtonEnabled()){
						    	 flag = false;
						    	 JOptionPane.showMessageDialog(BufferDialog.this, DATASET_NOT_NULL);
						     }
						}
					} catch (Exception e1) {
						BufferDialog.this.dispose();
					}
	               if (flag) {
		               BufferDialog.this.dispose();
                    }
               }else if (e.getSource()==panelButton.getButtonCancel()) {
                    BufferDialog.this.dispose();
               }
          }
     }
}
