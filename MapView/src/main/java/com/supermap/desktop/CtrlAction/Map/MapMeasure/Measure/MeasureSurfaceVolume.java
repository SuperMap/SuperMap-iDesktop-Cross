package com.supermap.desktop.CtrlAction.Map.MapMeasure.Measure;

import com.supermap.analyst.spatialanalyst.CalculationTerrain;
import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.enums.MeasureType;
import com.supermap.desktop.enums.VolumeUnit;
import com.supermap.desktop.mapview.MapViewProperties;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.ui.controls.SmDialog;
import com.supermap.desktop.ui.controls.TextFields.NumTextFieldLegit;
import com.supermap.desktop.ui.controls.button.SmButton;
import com.supermap.desktop.utilities.FontUtilities;
import com.supermap.mapping.TrackingLayer;
import com.supermap.ui.TrackedEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.MessageFormat;

/**
 * Created by ChenS on 2017/11/10 0010.
 */
public class MeasureSurfaceVolume extends MeasureSurfaceArea {
    private double altitude;

    public MeasureSurfaceVolume(DatasetGrid datasetGrid) {
        super(datasetGrid);
        altitude = 0;
    }

    @Override
    protected void getResult(TrackedEvent event) {
        ParaOptionPane paraOptionPane = new ParaOptionPane();
        DialogResult dialogResult = paraOptionPane.showDialog();
        if (dialogResult.equals(DialogResult.OK)) {
            GeoRegion region = (GeoRegion) event.getGeometry();
            double volume = CalculationTerrain.computeSurfaceVolume(datasetGrid, region, altitude);
            volume = VolumeUnit.convertVolume(mapControl.getMap().getPrjCoordSys(), getVolumeUnit().getUnit(), volume);
            showResult(CoreProperties.getString("String_Map_MeasureTotalVolume"), volume, event.getGeometry().getInnerPoint(), getVolumeUnit().toString());
        } else {
            TrackingLayer trackingLayer = mapControl.getMap().getTrackingLayer();
            trackingLayer.remove(trackingLayer.getCount() - 1);
            mapControl.getMap().refreshTrackingLayer();
        }
    }

    @Override
    protected void showResult(String s, double result, Point2D point2D, String unitString) {
        String info = MessageFormat.format(s, decimalFormat.format(result), unitString);

        TextPart part = new TextPart(info, point2D);
        GeoText geotext = new GeoText(part);

        TextStyle textStyle = geotext.getTextStyle();
        textStyle.setFontHeight(FontUtilities.fontSizeToMapHeight(textFontHeight,
                mapControl.getMap(), textStyle.isSizeFixed()));
        mapControl.getMap().getTrackingLayer().add(geotext, textTagTitle + "FinishedMeasure");
        if (result > 0) {

            Application.getActiveApplication().getOutput().output(MessageFormat.format(s, decimalFormat.format(result), unitString));
        } else {
            Application.getActiveApplication().getOutput().output(MapViewProperties.getString("String_Warning_MeasureVolumeFailed"));
        }
    }

    @Override
    public MeasureType getMeasureType() {
        return MeasureType.Volume_Surface;
    }

    private class ParaOptionPane extends SmDialog {
        JLabel label;
        NumTextFieldLegit fieldLegit;
        SmButton buttonOK;
        SmButton buttonCancel;

        public ParaOptionPane() {
            initPane();
            initComponent();
            initLayout();
            registerListener();
        }

        private void initLayout() {
            JPanel panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            panel.add(label, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(5, 10, 5, 10));
            panel.add(fieldLegit, new GridBagConstraintsHelper(1, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL).setInsets(5, 0, 5, 10));
            JPanel panel1 = new JPanel();
            panel1.setLayout(new GridBagLayout());
            panel1.add(this.buttonOK, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.EAST).setFill(GridBagConstraints.NONE).setWeight(1, 1).setInsets(2, 0, 10, 10));
            panel1.add(this.buttonCancel, new GridBagConstraintsHelper(1, 0, 1, 1).setAnchor(GridBagConstraints.EAST).setFill(GridBagConstraints.NONE).setWeight(0, 1).setInsets(2, 0, 10, 10));
            this.setLayout(new GridBagLayout());
            this.add(panel, new GridBagConstraintsHelper(0, 0, 1, 1).setFill(GridBagConstraints.BOTH).setAnchor(GridBagConstraints.WEST).setWeight(1, 1).setInsets(0, 10, 5, 10));
            this.add(panel1, new GridBagConstraintsHelper(0, 1, 1, 1).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.EAST).setWeight(1, 0).setInsets(5, 10, 5, 10));
        }

        private void registerListener() {
            buttonOK.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    altitude = Double.parseDouble(fieldLegit.getText());
                    dialogResult = DialogResult.OK;
                    ParaOptionPane.this.dispose();
                }
            });

            buttonCancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialogResult = DialogResult.CANCEL;
                    ParaOptionPane.this.dispose();
                }
            });

            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    dialogResult = DialogResult.CANCEL;
                    ParaOptionPane.this.dispose();
                }
            });

            this.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == 27) {
                        dialogResult = DialogResult.CANCEL;
                        ParaOptionPane.this.dispose();
                    }
                }
            });
        }

        private void initPane() {
            this.setTitle(MapViewProperties.getString("String_SurfaceVolumeAltitudeSetting"));
            this.setSize(300, 150);
            this.setLocationRelativeTo(null);
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        }

        private void initComponent() {
            label = new JLabel(ControlsProperties.getString("String_Label_BaseAltitude"));
            fieldLegit = new NumTextFieldLegit();
            buttonOK = new SmButton(ControlsProperties.getString("String_Calculate"));
            buttonCancel = new SmButton(CoreProperties.getString("String_Cancel"));
            fieldLegit.setText("0");
        }
    }
}
