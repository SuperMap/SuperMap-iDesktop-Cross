package com.supermap.desktop.dialog;

import com.supermap.data.Toolkit;
import com.supermap.desktop.Application;
import com.supermap.desktop.FormMap;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.UICommonToolkit;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.ui.controls.SmDialog;
import com.supermap.desktop.ui.controls.button.SmButton;
import com.supermap.desktop.utilities.StringUtilities;
import com.supermap.mapping.LayerCache;
import com.supermap.mapping.Map;
import com.supermap.tilestorage.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by ChenS on 2017/10/30 0030.
 */
public class DialogMongoDBCacheLoaded extends SmDialog {
    //region Field
    private JLabel labelServer;
    private JTextField textFieldServer;
    private JLabel labelDatabase;
    private JComboBox<String> comboBoxDatabase;
    private JLabel labelCache;
    private JComboBox<String> comboBoxCache;
    private JLabel labelClient;
    private JTextField textFieldClient;
    private JLabel labelPassword;
    private JTextField textFieldPassword;
    private JLabel labelVersion;
    private JComboBox<String> comboBoxVersion;
    private SmButton buttonCancel;
    private SmButton buttonOK;

    private FormMap formMap;
    private boolean isDatabaseNeedRefresh = true;
    private boolean isCacheNeedRefresh = false;
    private boolean isVersionNeedRefresh = false;

    private KeyAdapter keyAdapter = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            isCacheNeedRefresh = true;
        }
    };
    private FocusAdapter focusAdapter = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
            if (isCacheNeedRefresh) {
                updateCacheName();
            }
            isCacheNeedRefresh = false;
        }
    };
    //endregion

    public DialogMongoDBCacheLoaded(FormMap formMap) {
        this.formMap = formMap;
        initFrame();
        initComponent();
        initLayout();
        registerListener();
    }

    private void initFrame() {
        setTitle(CoreProperties.getString("String_Form_MongoDBCacheLoaded"));
        setSize(300, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }

    private void initLayout() {
        this.setLayout(new GridBagLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        JPanel panelButton = new JPanel();
        panelButton.setLayout(new GridBagLayout());
        panel.add(this.labelServer, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(5, 10, 5, 10));
        panel.add(this.textFieldServer, new GridBagConstraintsHelper(1, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(5, 0, 5, 10).setWeight(1, 0));
        panel.add(this.labelDatabase, new GridBagConstraintsHelper(0, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(5, 10, 5, 10));
        panel.add(this.comboBoxDatabase, new GridBagConstraintsHelper(1, 1, 2, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(5, 0, 5, 10).setWeight(1, 0));
        panel.add(this.labelCache, new GridBagConstraintsHelper(0, 2, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(5, 10, 5, 10));
        panel.add(this.comboBoxCache, new GridBagConstraintsHelper(1, 2, 2, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(5, 0, 5, 10).setWeight(1, 0));
        panel.add(this.labelClient, new GridBagConstraintsHelper(0, 3, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(5, 10, 5, 10));
        panel.add(this.textFieldClient, new GridBagConstraintsHelper(1, 3, 2, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(5, 0, 5, 10).setWeight(1, 0));
        panel.add(this.labelPassword, new GridBagConstraintsHelper(0, 4, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(5, 10, 5, 10));
        panel.add(this.textFieldPassword, new GridBagConstraintsHelper(1, 4, 2, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(5, 0, 5, 10).setWeight(1, 0));
        panel.add(this.labelVersion, new GridBagConstraintsHelper(0, 5, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(5, 10, 5, 10));
        panel.add(this.comboBoxVersion, new GridBagConstraintsHelper(1, 5, 2, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(5, 0, 5, 10).setWeight(1, 0));
        panelButton.add(this.buttonOK, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.EAST).setFill(GridBagConstraints.NONE).setInsets(0, 15, 10, 5).setWeight(1, 1));
        panelButton.add(this.buttonCancel, new GridBagConstraintsHelper(1, 0, 1, 1).setAnchor(GridBagConstraints.EAST).setFill(GridBagConstraints.NONE).setInsets(0, 0, 10, 5).setWeight(0, 1));
        this.add(panel, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0).setInsets(5, 10, 5, 10));
        this.add(new JPanel(), new GridBagConstraintsHelper(0, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.BOTH).setWeight(1, 1));
        this.add(panelButton, new GridBagConstraintsHelper(0, 2, 1, 1).setAnchor(GridBagConstraints.EAST).setFill(GridBagConstraints.HORIZONTAL).setWeight(1, 0).setInsets(0, 10, 5, 10));
    }

    private void initComponent() {
        labelServer = new JLabel(ControlsProperties.getString("String_Label_ServersName"));
        labelDatabase = new JLabel(ControlsProperties.getString("String_Label_DatabaseName"));
        labelCache = new JLabel(ControlsProperties.getString("String_CacheName"));
        labelClient = new JLabel(CoreProperties.getString("String_Label_DataUser"));
        labelPassword = new JLabel(ControlsProperties.getString("String_Label_UserPassword"));
        labelVersion = new JLabel(ControlsProperties.getString("String_Label_CurrentVersion"));
        textFieldServer = new JTextField();
        textFieldClient = new JTextField();
        textFieldPassword = new JTextField();
        comboBoxDatabase = new JComboBox<>();
        comboBoxCache = new JComboBox<>();
        comboBoxVersion = new JComboBox<>();
	    buttonCancel = new SmButton(CoreProperties.getString("String_Cancel"));
	    buttonOK = new SmButton(CoreProperties.getString("String_OK"));

        textFieldServer.setText("localhost:27017");
        comboBoxDatabase.setEditable(true);
        comboBoxCache.setEditable(true);
        checkButtonEnable();
    }

    private void registerListener() {
        textFieldServer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                isDatabaseNeedRefresh = true;
                isCacheNeedRefresh = true;
            }
        });
        textFieldServer.addFocusListener(focusAdapter);

        comboBoxDatabase.getComponent(0).addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isDatabaseNeedRefresh) {
                    updateDatabaseName();
                    updateCacheName();
                }
                isDatabaseNeedRefresh = false;
            }
        });
        comboBoxDatabase.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateCacheName();
            }
        });
        comboBoxDatabase.addKeyListener(keyAdapter);
        comboBoxDatabase.addFocusListener(focusAdapter);

        comboBoxCache.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                isVersionNeedRefresh = true;
            }
        });
        comboBoxCache.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateVersionName();
            }
        });
        comboBoxCache.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (isVersionNeedRefresh) {
                    updateVersionName();
                }
                isVersionNeedRefresh = false;
            }
        });

        textFieldClient.addFocusListener(focusAdapter);
        textFieldClient.addKeyListener(keyAdapter);

        textFieldPassword.addFocusListener(focusAdapter);
        textFieldPassword.addKeyListener(keyAdapter);

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDialogResult(DialogResult.OK);
                try {
                    Map map = formMap.getMapControl().getMap();
                    LayerCache layerCache = map.getLayers().AddCache(textFieldServer.getText(), comboBoxDatabase.getSelectedItem().toString(),
                            comboBoxCache.getSelectedItem().toString(), true);
                    layerCache.setCurrentVersion(comboBoxVersion.getSelectedItem().toString());
                    map.refresh();
                    formMap.setActiveLayers(layerCache);
                    UICommonToolkit.getLayersManager().setMap(map);
                } catch (Exception e1) {
                    Application.getActiveApplication().getOutput().output(e1);
                }
                dispose();
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDialogResult(DialogResult.CANCEL);
                dispose();
            }
        });
    }

    private void updateDatabaseName() {
        try {
            comboBoxDatabase.removeAllItems();
            String[] names = null;
            if (!StringUtilities.isNullOrEmpty(textFieldServer.getText())) {
                names = Toolkit.GetMongoDBNames(textFieldServer.getText(), "", "");
            }
            if (names != null && names.length > 0) {
                for (String name : names) {
                    comboBoxDatabase.addItem(name);
                }
            }
            comboBoxDatabase.setSelectedIndex(comboBoxDatabase.getItemCount() > 0 ? 0 : -1);
        } catch (Exception e) {
            Application.getActiveApplication().getOutput().output(e);
        }
    }

    private void updateCacheName() {
        comboBoxCache.removeAllItems();
        try {
            if (comboBoxDatabase.getSelectedItem() != null && !StringUtilities.isNullOrEmpty(textFieldServer.getText())) {
                TileStorageMiniInfo[] mongoTileStorageMiniInfo = TileStorageManager.getMongoTileStorageMiniInfo(
                        textFieldServer.getText(), comboBoxDatabase.getSelectedItem().toString(),
                        textFieldClient.getText(), textFieldPassword.getText());
                for (TileStorageMiniInfo tileStorageMiniInfo : mongoTileStorageMiniInfo) {
                    comboBoxCache.addItem(tileStorageMiniInfo.getName());
                }
            }
            comboBoxCache.setSelectedIndex(comboBoxCache.getItemCount() > 0 ? 0 : -1);
            updateVersionName();
        } catch (Exception e) {
            Application.getActiveApplication().getOutput().output(e);
        }
    }

    private void updateVersionName() {
        comboBoxVersion.removeAllItems();
        try {
            if (comboBoxDatabase.getSelectedItem() != null && !StringUtilities.isNullOrEmpty(textFieldServer.getText()) && comboBoxCache.getSelectedItem() != null) {
                TileStorageConnection tileStorageConnection = new TileStorageConnection();
                tileStorageConnection.setServer(textFieldServer.getText());
                tileStorageConnection.setStorageType(TileStorageType.MONGO);
                tileStorageConnection.setDatabase(comboBoxDatabase.getSelectedItem().toString());
                tileStorageConnection.setUser(textFieldClient.getText());
                tileStorageConnection.setPassword(textFieldPassword.getText());
                tileStorageConnection.setName(comboBoxCache.getSelectedItem().toString());

                TileStorageManager tileStorageManager = new TileStorageManager();
                tileStorageManager.open(tileStorageConnection);
                TileVersion[] versions = tileStorageManager.getVersions();
                for (TileVersion version : versions) {
                    comboBoxVersion.addItem(version.GetVersionName());
                }
                comboBoxVersion.setSelectedIndex(comboBoxVersion.getItemCount() > 0 ? 0 : -1);
            }
        } catch (Exception e) {
            Application.getActiveApplication().getOutput().output(e);
        }
        checkButtonEnable();
    }

    private void checkButtonEnable() {
        buttonOK.setEnabled(!StringUtilities.isNullOrEmpty(textFieldServer.getText()) &&
                comboBoxDatabase.getSelectedItem() != null &&
                comboBoxCache.getSelectedItem() != null &&
                comboBoxVersion.getSelectedItem() != null);
    }
}
