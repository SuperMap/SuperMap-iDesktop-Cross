package com.supermap.desktop.dialog;

import com.supermap.data.*;
import com.supermap.desktop.layoutview.LayoutViewProperties;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.UICommonToolkit;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.ui.controls.SmDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/29
 * Time: 16:20
 * Description:
 */
public class DialogAddMap extends SmDialog {

	private JLabel labelChooseMap;
	private JComboBox comboBox;
	private JButton buttonOK;
	private JButton buttonCancel;
	//	private GeoMap geoMap;
	private String selectedMapName = "";

	public DialogAddMap() {
		super();
//		this.geoMap = geoMap;
		setSize(350, 136);
		setLocationRelativeTo(null);
		initComponents();
		initLayout();
		initResources();
		unRegisterEvents();
		registerEvents();
	}

	private void initComponents() {
		this.labelChooseMap = new JLabel();
		this.comboBox = new JComboBox();
		this.buttonOK = new JButton();
		this.buttonCancel = new JButton();
		fillComboBox();
	}

	private void initLayout() {
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);

		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
								.addComponent(this.labelChooseMap)
								.addComponent(this.comboBox))
						.addGroup(groupLayout.createSequentialGroup()
								.addGap(170, 170, Short.MAX_VALUE)
								.addComponent(this.buttonOK)
								.addComponent(this.buttonCancel)
						)
				)
		);

		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.labelChooseMap)
						.addComponent(this.comboBox, 23, 23, 23))
				.addGap(23, 23, Short.MAX_VALUE)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.buttonOK)
						.addComponent(this.buttonCancel)
				)
		);
		getContentPane().setLayout(groupLayout);
	}

	private void initResources() {
		setTitle(LayoutViewProperties.getString("String_ChooseMapTitle"));
		this.labelChooseMap.setText(LayoutViewProperties.getString("String_ChooseMap"));
		this.buttonOK.setText(CoreProperties.getString("String_OK"));
		this.buttonCancel.setText(CoreProperties.getString("String_Cancel"));
	}

	private void registerEvents() {
		this.buttonOK.addActionListener(this.actionListener);
		this.buttonCancel.addActionListener(this.actionListener);
	}

	private void unRegisterEvents() {
		this.buttonOK.removeActionListener(this.actionListener);
		this.buttonCancel.removeActionListener(this.actionListener);
	}

	private void fillComboBox() {
		Maps maps = UICommonToolkit.getWorkspaceManager().getWorkspace().getMaps();
		this.comboBox.removeAllItems();
		for (int i = 0; i < maps.getCount(); i++) {
			this.comboBox.addItem(maps.get(i));
		}

	}

	private ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == buttonOK) {
				if (comboBox.getSelectedItem() != null) {
					selectedMapName = comboBox.getSelectedItem().toString();
				}
				DialogAddMap.this.setDialogResult(DialogResult.OK);
				DialogAddMap.this.dispose();
			} else if (e.getSource() == buttonCancel) {
				DialogAddMap.this.dispose();
			}
		}
	};

//	private void addMap(String mapXML) {
//		LayoutElements elements = ((IFormLayout) Application.getActiveApplication().getActiveForm()).getMapLayoutControl().getMapLayout().getElements();
//		//构造GeoMap
//		this.geoMap.fromXML(mapXML);
//		//设置GeoMap对象的外切矩形
//		this.geoMap.setMapName(this.comboBox.getSelectedItem().toString());
////		System.out.println(mapXML);
//		int startIndex = mapXML.indexOf("<sml:CoordinateReferenceSystem>");
//		int endIndex = mapXML.indexOf("</sml:CoordinateReferenceSystem>");
//		String prjXML = mapXML.substring(startIndex, endIndex+ 32);
//		PrjCoordSys prj = new PrjCoordSys();
//		prj.fromXML(prjXML);
//		if (prj.getType() == PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
//			this.geoMap.getMapGrid().setGridType(GeoMapGridType.GRATICULE);
//		} else {
//			this.geoMap.getMapGrid().setGridType(GeoMapGridType.MEASUREDGRID);
//		}
////		Rectangle2D rect = new Rectangle2D(this.geoMap.getBounds());
////		GeoRectangle geoRect = new GeoRectangle(rect, 0);
////		this.geoMap.setShape(this.geoMap);
//		elements.addNew(this.geoMap);
////		((IFormLayout) Application.getActiveApplication().getActiveForm()).getMapLayoutControl().setActiveGeoMapID(elements.getID());
//	}


	public String getSelectedMapName() {
		return this.selectedMapName;
	}

}
