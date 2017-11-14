package com.supermap.desktop.dialog.cacheClip;

import com.mongodb.Mongo;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import com.supermap.data.*;
import com.supermap.data.Toolkit;
import com.supermap.data.processing.CacheWriter;
import com.supermap.data.processing.MapCacheBuilder;
import com.supermap.data.processing.MapTilingMode;
import com.supermap.data.processing.StorageType;
import com.supermap.desktop.Application;
import com.supermap.desktop.GlobalParameters;
import com.supermap.desktop.Interface.IFormMap;
import com.supermap.desktop.ScaleModel;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.dialog.SmOptionPane;
import com.supermap.desktop.dialog.cacheClip.cache.CacheUtilities;
import com.supermap.desktop.mapview.MapViewProperties;
import com.supermap.desktop.properties.CacheProperties;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.ChooseTable.MultipleCheckboxItem;
import com.supermap.desktop.ui.controls.ChooseTable.MultipleCheckboxTableHeaderCellRenderer;
import com.supermap.desktop.ui.controls.ChooseTable.MultipleCheckboxTableRenderer;
import com.supermap.desktop.ui.controls.ChooseTable.SmChooseTable;
import com.supermap.desktop.ui.controls.*;
import com.supermap.desktop.ui.controls.ProviderLabel.WarningOrHelpProvider;
import com.supermap.desktop.ui.controls.mutiTable.DDLExportTableModel;
import com.supermap.desktop.ui.controls.mutiTable.component.MutiTable;
import com.supermap.desktop.utilities.*;
import com.supermap.mapping.Map;
import com.supermap.tilestorage.TileStorageConnection;
import com.supermap.tilestorage.TileStorageManager;
import com.supermap.tilestorage.TileStorageType;
import com.supermap.tilestorage.TileVersion;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

/**
 * Created by xie on 2017/4/26.
 * Panel for clipping cache(first step)
 */
public class FirstStepPane extends JPanel implements IState {
    private final static String MONGODB = MapViewProperties.getString("MapCache_SaveType_MongoDB");
    private final static String MONGODB_MULTI = MapViewProperties.getString("MapCache_SaveType_MongoDBMuti");
    private final static String ORIGIN = MapViewProperties.getString("MapCache_SaveType_Origin");
    private final static String COMPACT = MapViewProperties.getString("MapCache_SaveType_Compact");
    private final static String GEOPACKAGE = MapViewProperties.getString("MapCache_SaveType_GeoPackage");
    private int cmdType;
    private final String urlStr = "/coreresources/ToolBar/";
    public MapCacheBuilder mapCacheBuilder;
    public HashMap<Double, String> globalSplitScale;
    public final int COLUMN_INDEX = 0;
    public final int COLUMN_SCALE = 1;
    public final int COLUMN_TITLE = 2;
    private String[] globalSplitTableTitle = {"", MapViewProperties.getString("MapCache_Scale")};
    public Double[] globalScaleSortKeys;
    private boolean mongoDBConnectSate;
    private Mongo mongo;
    private static final int DEFAULT_PORT = 27017;
    private static final String DEFAULT_ADDRESS = "localhost";
    private NumberFormat scientificNotation = NumberFormat.getInstance();
    private boolean textFieldServerNameGetFocus = false;
    private Vector<EnabledListener> enabledListeners;

    //region Component
    private JToolBar toolBar;
    public ComponentDropDown addScaleDropDown;
    private ComponentDropDown importDropDown;
    private ComponentDropDown exportDropDown;
    //basic setting
    private JButton buttonSelectAll;
    private JButton buttonSelectInverse;
    private JButton buttonDelete;
    private JMenuItem jMenuItemAddScale;
    private JMenuItem jMenuItemDefaultScale;
    private JMenuItem jMenuItemImportCacheConfigs;
    private JMenuItem jMenuItemImportScale;
    private JMenuItem jMenuItemExportCacheConfigs;
    private JMenuItem jMenuItemExportScale;
    public JScrollPane scrollPane;
    public MutiTable localSplitTable;
    public SmChooseTable globalSplitTable;
    private JLabel labelVersion;
    private JLabel labelSplitMode;
    private JLabel labelConfig;
    public JLabel labelConfigValue;
    private JComboBox<String> comboboxVersion;
    private JComboBox<String> comboBoxSplitMode;
    private JLabel labelCacheName;
    private JLabel labelCachePath;
    private WarningOrHelpProvider warningProviderCacheNameIllegal;
    //	private WarningOrHelpProvider warningProviderCachePathIllegal;
    public JTextField textFieldCacheName;

    private JLabel labelSaveType;
    private JLabel labelUserName;
    private JLabel labelUserPassword;
    private JLabel labelConfirmPassword;
    private JLabel labelServerName;
    private JLabel labelDatabaseName;
    private JLabel labelMultiTenseVersion;
    private WarningOrHelpProvider helpProviderForDatabaseName;
    private WarningOrHelpProvider warningProviderPasswordNotSame;
    public JCheckBox checkBoxFilterSelectionObjectInLayer;
    public JComboBox<String> comboBoxSaveType;
    public JComboBox<String> comboBoxDatabaseName;
    public JComboBox<TileVersion> comboBoxMultiTenseVersion;
    public JTextField textFieldUserName;
    public JPasswordField textFieldUserPassword;
    private JPasswordField textFieldConfirmPassword;
    public JTextField textFieldServerName;
    private JButton buttonGetServer;
    private String sciPath;
    //endregion

    private double originMapCacheScale[];

    public ArrayList<Double> currentMapCacheScale;
    private static final String SHOW_SCALE_PRE_PART = "1:";
    private static final String COLON = ":";
    public boolean importCacheConfigs;//flag for recording if cache configs import or not

    public JFileChooserControl fileChooserControlFileCache;
    private Map currentMap;
    private DialogMapCacheClipBuilder parent;

    //region Listener
    private ActionListener addScaleListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            addScale();
        }
    };
    private ActionListener addScaleToolbarJmenuListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            addScale();
        }
    };
    private ActionListener defaultScaleListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            addDefaultScale();
        }
    };

    private ActionListener inputCacheConfigFileListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            importCacheConfigFile();
        }
    };
    private ActionListener inputCacheConfigFileToolbarJmenuListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            importCacheConfigFile();
        }
    };
    private ActionListener exportCacheConfigFileListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            exportCacheConfigFile();
        }
    };
    private ActionListener exportCacheConfigFileToolbarJmenuListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            exportCacheConfigFile();
        }
    };

    private ActionListener exportScalesListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            exportXml();
        }
    };
    private ActionListener inputScalesListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            importXml();
            checkButtonState();
        }
    };
    private ActionListener selectAllScaleListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (addScaleDropDown.isEnabled()) {
                localSplitTable.setRowSelectionInterval(0, localSplitTable.getRowCount() - 1);
            } else {
                globalSplitTable.setRowSelectionInterval(0, globalSplitTable.getRowCount() - 1);
            }
            checkButtonState();
        }
    };
    private ActionListener selectScaleInverseListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            selectInvert();
            checkButtonState();
        }
    };

    private ActionListener deleteScaleListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            deleteScale();
            checkButtonState();
        }
    };
    private TableModelListener tableModelListener = new TableModelListener() {
        @Override
        public void tableChanged(TableModelEvent e) {
            tableModelChanged(e);
        }
    };

    private ItemListener saveTypeListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                initPanelImageSaveOutputDisplay(e.getItem().toString());
                fireEnabled(enabled());
            }
        }
    };
    private FileChooserPathChangedListener chooseCacheFilePathTextChangeListener = new FileChooserPathChangedListener() {
        @Override
        public void pathChanged() {
            fireEnabled(enabled());
        }
    };
    private DocumentListener passwordChangeListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
//			updateDBNames();
            fireEnabled(enabled());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
//			updateDBNames();
            fireEnabled(enabled());
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
//			updateDBNames();
            fireEnabled(enabled());
        }
    };

    private ItemListener comboboxSplitModeItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            splitModeChanged(e);
        }
    };

    private FocusListener serverNameFocusListener = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            textFieldServerNameGetFocus = true;
            fireEnabled(enabled());
        }

        @Override
        public void focusLost(FocusEvent e) {
            textFieldServerNameGetFocus = false;

//			updateDBNames();
            fireEnabled(enabled());
        }
    };

    private ActionListener serverGettingListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            mongoDBConnectSate = isDBValidate();
            updateDBNames();
        }
    };

    private DocumentListener databaseNameChangeListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            fireEnabled(enabled());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            fireEnabled(enabled());
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            fireEnabled(enabled());
        }
    };

    private MouseAdapter versionUpdateListener = new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            comboBoxMultiTenseVersion.removeAllItems();
            try {
                if (comboBoxDatabaseName.getSelectedItem() != null && !StringUtilities.isNullOrEmpty(textFieldServerName.getText()) && textFieldCacheName.getText() != null) {
                    TileStorageConnection tileStorageConnection = new TileStorageConnection();
                    tileStorageConnection.setServer(textFieldServerName.getText());
                    tileStorageConnection.setStorageType(TileStorageType.MONGO);
                    tileStorageConnection.setDatabase(comboBoxDatabaseName.getSelectedItem().toString());
                    tileStorageConnection.setUser(textFieldUserName.getText());
                    tileStorageConnection.setPassword(textFieldUserPassword.getText());
                    tileStorageConnection.setName(textFieldCacheName.getText());

                    TileStorageManager tileStorageManager = new TileStorageManager();
                    tileStorageManager.open(tileStorageConnection);
                    TileVersion[] versions = tileStorageManager.getVersions();
                    for (TileVersion version : versions) {
                        comboBoxMultiTenseVersion.addItem(version);
                    }
                    comboBoxMultiTenseVersion.setSelectedIndex(-1);
                }
            } catch (Exception exp) {
                Application.getActiveApplication().getOutput().output(exp);
            }
            fireEnabled(enabled());
        }
    };
    //endregion

    public FirstStepPane(MapCacheBuilder mapCacheBuilder, DialogMapCacheClipBuilder parent, int cmdType) {
        super();
        this.parent = parent;
        this.mapCacheBuilder = mapCacheBuilder;
        this.currentMap = mapCacheBuilder.getMap();
        this.cmdType = cmdType;
        init();
    }

    private void init() {
        initComponents();
        initLayout();
        initLocalSplitTable();
        initGlobalValue();
        initBasicSettingPanelValue();
        initResources();
        registerEvents();
        enabled();
    }

    private void initLayout() {
        JPanel outputSetting = new JPanel();
        outputSetting.setBorder(BorderFactory.createTitledBorder(MapViewProperties.getString("MapCache_OutputSetting")));
        JPanel pathSetting = new JPanel();
        pathSetting.setBorder(BorderFactory.createTitledBorder(MapViewProperties.getString("MapCache_PathSetting")));
        outputSetting.setLayout(new GridBagLayout());
        outputSetting.add(this.labelVersion, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(5, 10, 5, 10));
        outputSetting.add(this.comboboxVersion, new GridBagConstraintsHelper(1, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(5, 0, 5, 10).setWeight(1, 0));
        outputSetting.add(this.labelSplitMode, new GridBagConstraintsHelper(0, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(0, 10, 5, 10));
        outputSetting.add(this.comboBoxSplitMode, new GridBagConstraintsHelper(1, 1, 2, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(0, 0, 5, 10).setWeight(1, 0));
        outputSetting.add(this.labelConfig, new GridBagConstraintsHelper(0, 2, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(0, 10, 5, 10));
        outputSetting.add(this.labelConfigValue, new GridBagConstraintsHelper(1, 2, 2, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(0, 0, 5, 10).setWeight(1, 0));
        pathSetting.setLayout(new GridBagLayout());
        pathSetting.add(this.labelCacheName, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(5, 10, 5, 10));
        pathSetting.add(this.warningProviderCacheNameIllegal, new GridBagConstraintsHelper(1, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(5, 0, 5, 10));
        pathSetting.add(this.textFieldCacheName, new GridBagConstraintsHelper(2, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(5, 0, 5, 10).setWeight(1, 0));
        pathSetting.add(this.labelCachePath, new GridBagConstraintsHelper(0, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(0, 10, 5, 10));
//		pathSetting.add(this.warningProviderCachePathIllegal, new GridBagConstraintsHelper(1, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(0, 0, 5, 10));
        pathSetting.add(this.fileChooserControlFileCache, new GridBagConstraintsHelper(2, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(0, 0, 5, 10).setWeight(1, 0));
        JPanel storeType = new JPanel();
        storeType.setBorder(BorderFactory.createTitledBorder(MapViewProperties.getString("MapCache_SaveSetting")));
        storeType.setLayout(new GridBagLayout());
        storeType.add(this.labelSaveType, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(5, 10, 5, 10));
        storeType.add(this.comboBoxSaveType, new GridBagConstraintsHelper(2, 0, 2, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(5, 0, 5, 10).setWeight(1, 0));
        storeType.add(this.labelServerName, new GridBagConstraintsHelper(0, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(0, 10, 5, 0));
        storeType.add(this.textFieldServerName, new GridBagConstraintsHelper(2, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(0, 0, 5, 10).setWeight(1, 0));
        storeType.add(this.buttonGetServer, new GridBagConstraintsHelper(3, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(0, 0, 5, 10).setWeight(1, 0));
        storeType.add(this.labelDatabaseName, new GridBagConstraintsHelper(0, 2, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(0, 10, 5, 10));
        storeType.add(this.helpProviderForDatabaseName, new GridBagConstraintsHelper(1, 2, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(0, 0, 5, 0));
        storeType.add(this.comboBoxDatabaseName, new GridBagConstraintsHelper(2, 2, 2, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(0, 0, 5, 10).setWeight(1, 0));
        storeType.add(this.labelUserName, new GridBagConstraintsHelper(0, 3, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(0, 10, 5, 10));
        storeType.add(this.textFieldUserName, new GridBagConstraintsHelper(2, 3, 2, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(0, 0, 5, 10).setWeight(1, 0));
        storeType.add(this.labelUserPassword, new GridBagConstraintsHelper(0, 4, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(0, 10, 5, 10));
        storeType.add(this.textFieldUserPassword, new GridBagConstraintsHelper(2, 4, 2, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(0, 0, 5, 10));
        storeType.add(this.labelMultiTenseVersion, new GridBagConstraintsHelper(0, 5, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(0, 10, 5, 10).setWeight(1, 0));
        storeType.add(this.comboBoxMultiTenseVersion, new GridBagConstraintsHelper(2, 5, 2, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(0, 0, 5, 10).setWeight(1, 0));
        storeType.add(this.labelConfirmPassword, new GridBagConstraintsHelper(0, 6, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(0, 10, 5, 10));
        storeType.add(this.warningProviderPasswordNotSame, new GridBagConstraintsHelper(1, 6, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(0, 0, 5, 10));
        storeType.add(this.textFieldConfirmPassword, new GridBagConstraintsHelper(2, 6, 2, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(0, 0, 5, 10).setWeight(1, 0));
        storeType.add(this.checkBoxFilterSelectionObjectInLayer, new GridBagConstraintsHelper(0, 7, 4, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.NONE).setInsets(0, 10, 5, 10));

        JPanel panelRight = new JPanel();
        panelRight.setLayout(new GridBagLayout());
        panelRight.add(outputSetting, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(5).setWeight(1, 0));
        panelRight.add(pathSetting, new GridBagConstraintsHelper(0, 1, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(0, 5, 5, 5).setWeight(1, 0));
        panelRight.add(storeType, new GridBagConstraintsHelper(0, 2, 1, 1).setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL).setInsets(0, 5, 5, 5).setWeight(1, 0));
        panelRight.add(new JPanel(), new GridBagConstraintsHelper(0, 3, 1, 1).setAnchor(GridBagConstraints.CENTER).setFill(GridBagConstraints.BOTH).setWeight(1, 1));

        JPanel panelLeft = new JPanel();
        GroupLayout leftLayout = new GroupLayout(panelLeft);
        leftLayout.setHorizontalGroup(leftLayout.createParallelGroup().addComponent(this.toolBar, 260, 260, 260).addGap(5)
                .addComponent(this.scrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        leftLayout.setVerticalGroup(leftLayout.createSequentialGroup().addComponent(this.toolBar, 30, 30, 30)
                .addComponent(this.scrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        leftLayout.setAutoCreateContainerGaps(true);
        leftLayout.setAutoCreateGaps(true);
        panelLeft.setLayout(leftLayout);
        this.scrollPane.setViewportView(localSplitTable);

        GroupLayout layout = new GroupLayout(this);
        layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(panelLeft).addContainerGap().addComponent(panelRight));
        layout.setVerticalGroup(layout.createParallelGroup().addComponent(panelLeft).addComponent(panelRight));
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
        this.setLayout(layout);
        initComponentsState();
    }

    private void initComponentsState() {
        boolean componentsState = !(cmdType == DialogMapCacheClipBuilder.MultiUpdateProcessClip || cmdType == DialogMapCacheClipBuilder.SingleUpdateProcessClip);
        this.labelVersion.setEnabled(componentsState);
        this.comboboxVersion.setEnabled(componentsState);
        this.labelSplitMode.setEnabled(componentsState);
        this.comboBoxSplitMode.setEnabled(componentsState);
        this.labelCacheName.setEnabled(componentsState);
        this.textFieldCacheName.setEnabled(componentsState);
        this.labelCachePath.setEnabled(componentsState);
        this.fileChooserControlFileCache.setEnabled(componentsState);
        this.labelSaveType.setEnabled(componentsState);
        this.comboBoxSaveType.setEnabled(componentsState);
        this.labelUserPassword.setEnabled(componentsState);
        this.labelConfirmPassword.setEnabled(componentsState);
        this.labelUserName.setEnabled(componentsState);
        this.labelServerName.setEnabled(componentsState);
        this.textFieldServerName.setEnabled(componentsState);
        this.labelDatabaseName.setEnabled(componentsState);
        this.comboBoxDatabaseName.setEnabled(componentsState);
        this.comboBoxMultiTenseVersion.setEnabled(componentsState);
        this.comboBoxDatabaseName.setEditable(true);
        this.comboBoxMultiTenseVersion.setEditable(true);
        comboBoxMultiTenseVersion.setRenderer(new ListCellRenderer<TileVersion>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends TileVersion> list, TileVersion value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = new JLabel("");
                if (value != null) {
                    label = new JLabel(value.GetVersionDesc());
                }
                label.setOpaque(true);
                if (isSelected) {
                    label.setBackground(list.getSelectionBackground());
                    label.setForeground(list.getSelectionForeground());
                } else {
                    label.setBackground(list.getBackground());
                }
                return label;
            }
        });
    }

    private void initBasicSettingPanelValue() {
        this.comboBoxSaveType.addItem(COMPACT);
        this.comboBoxSaveType.addItem(ORIGIN);
        this.comboBoxSaveType.addItem(MONGODB);
        this.comboBoxSaveType.addItem(MONGODB_MULTI);
        this.comboBoxSaveType.setSelectedItem(ORIGIN);
        StorageType storageType = this.mapCacheBuilder.getStorageType();
        String storageStr = null;
        if (storageType == StorageType.Original) {
            storageStr = ORIGIN;
        } else if (storageType == StorageType.Compact) {
            storageStr = COMPACT;
        } else if (storageType == StorageType.MongoDB) {
            storageStr = MONGODB;
        } else {
            storageStr = GEOPACKAGE;
        }
        initPanelImageSaveOutputDisplay(storageStr);

        this.comboboxVersion.addItem(MapViewProperties.getString("MapCache_ComboboxVersionItem"));
        this.comboBoxSplitMode.addItem(MapViewProperties.getString("MapCache_ComboboxSplitModeLocalSplit"));
        this.comboBoxSplitMode.addItem(MapViewProperties.getString("MapCache_ComboboxSplitModeGlobalSplit"));
        this.comboBoxSplitMode.setEnabled(false);
        if (null != this.currentMap && this.currentMap.getPrjCoordSys() != null && this.currentMap.getPrjCoordSys().getType() == PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE
                && this.currentMap.getPrjCoordSys().getGeoCoordSys() != null && this.currentMap.getPrjCoordSys().getGeoCoordSys().getType() == GeoCoordSysType.GCS_WGS_1984
                && this.currentMap.getPrjCoordSys().getGeoCoordSys().getGeoSpatialRefType() == GeoSpatialRefType.SPATIALREF_EARTH_LONGITUDE_LATITUDE) {
            this.comboBoxSplitMode.setEnabled(true);
            this.textFieldCacheName.setText(this.currentMap.getName());
        } else {
            this.comboBoxSplitMode.setEnabled(false);
            this.textFieldCacheName.setText(this.mapCacheBuilder.getCacheName());
        }
        this.warningProviderCacheNameIllegal.hideWarning();
//		this.warningProviderCachePathIllegal.hideWarning();
    }

    private void registerEvents() {
        removeEvents();
        this.jMenuItemAddScale.addActionListener(this.addScaleListener);
        this.jMenuItemDefaultScale.addActionListener(this.defaultScaleListener);
        this.jMenuItemImportCacheConfigs.addActionListener(this.inputCacheConfigFileListener);
        this.jMenuItemExportCacheConfigs.addActionListener(this.exportCacheConfigFileListener);
        this.jMenuItemExportScale.addActionListener(this.exportScalesListener);
        this.jMenuItemImportScale.addActionListener(this.inputScalesListener);
        this.buttonSelectAll.addActionListener(this.selectAllScaleListener);
        this.buttonSelectInverse.addActionListener(this.selectScaleInverseListener);
        this.buttonDelete.addActionListener(this.deleteScaleListener);
        this.addScaleDropDown.getDisplayButton().addActionListener(this.addScaleToolbarJmenuListener);
        this.importDropDown.getDisplayButton().addActionListener(this.inputCacheConfigFileToolbarJmenuListener);
        this.exportDropDown.getDisplayButton().addActionListener(this.exportCacheConfigFileToolbarJmenuListener);
        this.localSplitTable.getModel().addTableModelListener(this.tableModelListener);
        this.comboBoxSplitMode.addItemListener(this.comboboxSplitModeItemListener);
        this.textFieldCacheName.getDocument().addDocumentListener(this.cacheNameTextChangeListener);
        this.comboBoxSaveType.addItemListener(this.saveTypeListener);
        this.textFieldServerName.addFocusListener(this.serverNameFocusListener);
        this.buttonGetServer.addActionListener(this.serverGettingListener);
        this.fileChooserControlFileCache.addFileChangedListener(this.chooseCacheFilePathTextChangeListener);
        this.comboBoxDatabaseName.getComponent(0).addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                mongoDBConnectSate = isDBValidate();
                updateDBNames();
            }
        });
        ((JTextField) this.comboBoxDatabaseName.getEditor().getEditorComponent()).getDocument().addDocumentListener(this.databaseNameChangeListener);
        ((JTextField) this.comboBoxMultiTenseVersion.getEditor().getEditorComponent()).getDocument().addDocumentListener(this.databaseNameChangeListener);
        this.textFieldUserName.getDocument().addDocumentListener(this.passwordChangeListener);
        this.textFieldUserPassword.getDocument().addDocumentListener(this.passwordChangeListener);
        this.textFieldConfirmPassword.getDocument().addDocumentListener(this.passwordChangeListener);
        this.comboBoxMultiTenseVersion.getComponent(0).addMouseListener(this.versionUpdateListener);
    }

    public void removeEvents() {
        this.jMenuItemAddScale.removeActionListener(this.addScaleListener);
        this.jMenuItemDefaultScale.removeActionListener(this.defaultScaleListener);
        this.jMenuItemImportCacheConfigs.removeActionListener(this.inputCacheConfigFileListener);
        this.jMenuItemExportCacheConfigs.removeActionListener(this.exportCacheConfigFileListener);
        this.jMenuItemExportScale.removeActionListener(this.exportScalesListener);
        this.jMenuItemImportScale.removeActionListener(this.inputScalesListener);
        this.buttonSelectAll.removeActionListener(this.selectAllScaleListener);
        this.buttonSelectInverse.removeActionListener(this.selectScaleInverseListener);
        this.buttonDelete.removeActionListener(this.deleteScaleListener);
        this.addScaleDropDown.getDisplayButton().removeActionListener(this.addScaleToolbarJmenuListener);
        this.importDropDown.getDisplayButton().removeActionListener(this.inputCacheConfigFileToolbarJmenuListener);
        this.exportDropDown.getDisplayButton().removeActionListener(this.exportCacheConfigFileToolbarJmenuListener);
        this.localSplitTable.getModel().removeTableModelListener(this.tableModelListener);
        this.comboBoxSplitMode.removeItemListener(this.comboboxSplitModeItemListener);
        this.textFieldCacheName.getDocument().removeDocumentListener(this.cacheNameTextChangeListener);
        this.comboBoxSaveType.removeItemListener(this.saveTypeListener);
        this.textFieldServerName.removeFocusListener(this.serverNameFocusListener);
        this.buttonGetServer.removeActionListener(this.serverGettingListener);
        this.fileChooserControlFileCache.removePathChangedListener(this.chooseCacheFilePathTextChangeListener);
        ((JTextField) this.comboBoxDatabaseName.getEditor().getEditorComponent()).getDocument().removeDocumentListener(this.databaseNameChangeListener);
        ((JTextField) this.comboBoxMultiTenseVersion.getEditor().getEditorComponent()).getDocument().removeDocumentListener(this.databaseNameChangeListener);
        comboBoxMultiTenseVersion.getComponent(0).removeMouseListener(versionUpdateListener);
        this.textFieldUserName.getDocument().removeDocumentListener(this.passwordChangeListener);
        this.textFieldUserPassword.getDocument().removeDocumentListener(this.passwordChangeListener);
        this.textFieldConfirmPassword.getDocument().removeDocumentListener(this.passwordChangeListener);
    }

    private void initPanelImageSaveOutputDisplay(String currentShowItem) {
        if (currentShowItem.equals(COMPACT)
                || currentShowItem.equals(ORIGIN)
                || currentShowItem.equals(GEOPACKAGE)) {
            this.labelServerName.setVisible(false);
            this.textFieldServerName.setVisible(false);
            this.buttonGetServer.setVisible(false);
            this.labelDatabaseName.setVisible(false);
            this.helpProviderForDatabaseName.setVisible(false);
            this.comboBoxDatabaseName.setVisible(false);
            this.labelUserName.setVisible(false);
            this.textFieldUserName.setVisible(false);
            this.labelMultiTenseVersion.setVisible(false);
            this.comboBoxMultiTenseVersion.setVisible(false);

            this.labelUserPassword.setVisible(true);
            this.labelConfirmPassword.setVisible(true);
            this.warningProviderPasswordNotSame.setVisible(true);
            this.warningProviderPasswordNotSame.hideWarning();
            this.textFieldUserPassword.setVisible(true);
            this.textFieldConfirmPassword.setVisible(true);
            this.textFieldUserPassword.setText("");
            this.textFieldConfirmPassword.setText("");
            if (currentShowItem.equals(COMPACT)) {
                this.textFieldUserPassword.setEnabled(true);
                this.textFieldConfirmPassword.setEnabled(true);
                this.mapCacheBuilder.setStorageType(StorageType.Compact);
            } else {
                this.textFieldUserPassword.setEnabled(false);
                this.textFieldConfirmPassword.setEnabled(false);
                if (currentShowItem.equals(ORIGIN)) {
                    this.mapCacheBuilder.setStorageType(StorageType.Original);
                } else if (currentShowItem.equals(GEOPACKAGE)) {
                    this.mapCacheBuilder.setStorageType(StorageType.GPKG);
                }
            }

        } else if (currentShowItem.equals(MONGODB) || currentShowItem.equals(MONGODB_MULTI)) {
            this.labelServerName.setVisible(true);
            this.textFieldServerName.setVisible(true);
            if (StringUtilities.isNullOrEmpty(textFieldServerName.getText())) {
                textFieldServerName.setText(MapViewProperties.getString("MapCache_MongoDB_DefaultServerName"));
            }
            this.buttonGetServer.setVisible(true);
            this.labelDatabaseName.setVisible(true);
            this.helpProviderForDatabaseName.setVisible(true);
            this.comboBoxDatabaseName.setVisible(true);
            this.comboBoxDatabaseName.removeAllItems();
            if (cmdType == DialogMapCacheClipBuilder.ResumeProcessClip
                    || cmdType == DialogMapCacheClipBuilder.SingleUpdateProcessClip
                    || cmdType == DialogMapCacheClipBuilder.MultiUpdateProcessClip) {
                this.labelUserName.setVisible(false);
                this.textFieldUserName.setVisible(false);
                this.labelUserPassword.setVisible(false);
                this.textFieldUserPassword.setVisible(false);
            } else {
                this.labelUserName.setVisible(true);
                this.textFieldUserName.setVisible(true);
                this.textFieldUserName.setText("");
                this.textFieldUserPassword.setEnabled(true);
                this.textFieldUserPassword.setText("");
            }
            this.labelConfirmPassword.setVisible(false);
            this.warningProviderPasswordNotSame.setVisible(false);
            this.textFieldConfirmPassword.setVisible(false);

            if (currentShowItem.equals(MONGODB_MULTI)) {
                this.labelMultiTenseVersion.setVisible(true);
                this.comboBoxMultiTenseVersion.setVisible(true);
                this.comboBoxMultiTenseVersion.removeAllItems();
            } else {
                this.labelMultiTenseVersion.setVisible(false);
                this.comboBoxMultiTenseVersion.setVisible(false);
                this.mapCacheBuilder.setStorageType(StorageType.MongoDB);
            }
            mongoDBConnectSate = isDBValidate();
        }
    }

    /**
     * 检查链接是否可用
     */
    private void updateDBNames() {
        if ((cmdType == DialogMapCacheClipBuilder.ResumeProcessClip
                || cmdType == DialogMapCacheClipBuilder.SingleUpdateProcessClip
                || cmdType == DialogMapCacheClipBuilder.MultiUpdateProcessClip)
                && null != comboBoxDatabaseName.getSelectedItem()) {
            return;
        }
        if (!mongoDBConnectSate) {
            this.comboBoxDatabaseName.removeAllItems();
            return;//如果IP都不对就直接返回，不然输一个字符获取一次列表，IP不对的情况下会很慢的
        }
        String serverName = textFieldServerName.getText();
        String userName = textFieldUserName.getText();
        String password = new String(textFieldUserPassword.getPassword());
        String dbName = comboBoxDatabaseName.getSelectedItem() == null ? null : comboBoxDatabaseName.getSelectedItem().toString();
        //重置database comboBox
        java.util.List<String> databaseNames = new ArrayList<>();
        try {
            //先尝试非验证模式--用户名密码为空
            String[] names = Toolkit.GetMongoDBNames(serverName, "", "");
            databaseNames.addAll(Arrays.asList(names));
            //非验证模式直接置灰账号输入--根据组件mongo2.6测试报告，非验证模式下输入密码没有必要
            /*this.textFieldUserName.setText(null);
            this.textFieldUserPassword.setText(null);*/
//			this.textFieldUserName.setEnabled(false);
//			this.textFieldUserPassword.setEnabled(false);
        } catch (Exception e) {
//			this.textFieldUserName.setEnabled(true);
//			this.textFieldUserPassword.setEnabled(true);
            //再尝试验证模式--输入用户名密码
            try {
                databaseNames.clear();
                String[] names = Toolkit.GetMongoDBNames(serverName, userName, password);
                databaseNames.addAll(Arrays.asList(names));
            } catch (Exception e1) {

            }
        }

        if (databaseNames.size() == 0) {
            //认证模式下，输入正确的用户名密码，得到列表之后选择，然后再修改为普通用户，此时不希望刚才的选择被清除掉。
            this.comboBoxDatabaseName.removeAllItems();
            if (dbName != null) {
                this.comboBoxDatabaseName.setSelectedItem(dbName);
            }
        } else {
            //修改了ip、账号等信息，是的获取到了数据库列表。判断列表是否完全一致，一致就不更新combox了，避免因修改账号导致选择被清除。
            boolean isDBNamesSame = true;
            if (this.comboBoxDatabaseName.getItemCount() != databaseNames.size()) {
                isDBNamesSame = false;
            } else {
                for (int i = 0; i < this.comboBoxDatabaseName.getItemCount(); i++) {
                    if (!databaseNames.contains(this.comboBoxDatabaseName.getItemAt(i))) {
                        isDBNamesSame = false;
                        break;
                    }
                }
            }
            if (!isDBNamesSame) {
                this.comboBoxDatabaseName.removeAllItems();
                for (int i = 0; i < databaseNames.size(); i++) {
                    this.comboBoxDatabaseName.addItem(databaseNames.get(i));
                }
            }
        }

    }

    /**
     * 简单测试连接是否正常。后面组件应该要提供数据库连接test,针对验证和非验证模式，对输入账号的要求不同。
     * fixme 关注 http://192.168.115.2:8090/browse/UGDC-2617
     */
    private boolean isDBValidate() {
        String address = "";
        Integer port = DEFAULT_PORT;
        if (StringUtilities.isNullOrEmpty(this.textFieldServerName.getText())) {
            return false;
        } else {
            if (this.textFieldServerName.getText().contains(COLON)) {
                String[] temp = this.textFieldServerName.getText().split(COLON);
                if (!temp[0].isEmpty() && !temp[1].isEmpty() && temp[1].matches("[0-9]+")) {
                    address = temp[0];
                    port = Integer.valueOf(temp[1]);
                }
            } else {
                address = this.textFieldServerName.getText();
            }
            try {
                ServerAddress serverAddress = new ServerAddress(address, port);
                Socket socket = new Socket();
                socket.connect(serverAddress.getSocketAddress(), 300);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    private void connectMongoDB(String address, Integer port) {
        try {
            ServerAddress serverAddress = new ServerAddress(address, port);
            Socket socket = new Socket();
            socket.connect(serverAddress.getSocketAddress(), 300);
        } catch (Exception e) {
            this.mongoDBConnectSate = false;
            this.comboBoxDatabaseName.removeAllItems();
            return;
        }
        Mongo.Holder holder = new Mongo.Holder();
        this.mongo = holder.connect(new MongoClientURI("mongodb://" + address));
        try {
            this.comboBoxDatabaseName.removeAllItems();
            java.util.List<String> databaseNames = new ArrayList<>();
            try {
                //先尝试验证模式--输入用户名密码
                String[] names = Toolkit.GetMongoDBNames(textFieldServerName.getText(), textFieldUserName.getText(), textFieldUserPassword.getPassword().toString());
                databaseNames.addAll(Arrays.asList(names));
            } catch (Exception e) {
                //再尝试非验证模式--用户名密码为空，否则抛异常
                try {
                    databaseNames.clear();
                    String[] names = Toolkit.GetMongoDBNames(textFieldServerName.getText(), "", "");
                    databaseNames.addAll(Arrays.asList(names));
                } catch (Exception e1) {

                }
            }
            this.mongoDBConnectSate = true;
            for (int i = 0; i < databaseNames.size(); i++) {
                this.comboBoxDatabaseName.addItem(databaseNames.get(i));
            }
            this.textFieldUserName.setText("");
            this.textFieldUserPassword.setText("");
            this.textFieldUserName.setEnabled(false);
            this.textFieldUserPassword.setEnabled(false);
        } catch (Exception e) {
            this.textFieldUserName.setEnabled(true);
            this.textFieldUserPassword.setEnabled(true);
            this.textFieldUserName.setText("");
            this.textFieldUserPassword.setText("");
        }
    }


    private DocumentListener cacheNameTextChangeListener = new DocumentListener() {
        private void modifyPath() {
            String directories = fileChooserControlFileCache.getPath();
            if (!directories.endsWith(File.separator)) {
                directories += File.separator;
                fileChooserControlFileCache.setPath(directories);
            }
            fireEnabled(enabled());
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            modifyPath();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            modifyPath();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            modifyPath();
        }
    };

    private boolean enabled() {
        return isRightCacheName() && isAllRightSaveTypeSetting();
    }

    //Validate cache name is right
    private boolean isRightCacheName() {
        boolean result = true;
        if (this.textFieldCacheName.getText().isEmpty()) {
            result = false;
            this.warningProviderCacheNameIllegal.showWarning();
        } else {
            this.warningProviderCacheNameIllegal.hideWarning();
        }
        return result;
    }

    //Validate save type is right
    private boolean isAllRightSaveTypeSetting() {
        boolean result = true;
        if (this.comboBoxSaveType.getSelectedItem().toString().equals(COMPACT)) {
            result = isPasswordSame();
        } else {
            if (this.comboBoxSaveType.getSelectedItem().toString().equals(ORIGIN) || this.comboBoxSaveType.getSelectedItem().toString().equals(GEOPACKAGE)) {

            } else if (this.comboBoxSaveType.getSelectedItem().toString().equals(MONGODB) || this.comboBoxSaveType.getSelectedItem().toString().equals(MONGODB_MULTI)) {
                if (textFieldServerNameGetFocus || !this.isDBValidate() || this.comboBoxDatabaseName.getEditor().getItem().toString().isEmpty()) {
                    result = false;
                } else if (this.comboBoxSaveType.getSelectedItem().toString().equals(MONGODB_MULTI) && this.comboBoxMultiTenseVersion.getEditor().getItem().toString().isEmpty()) {
                    result = false;
                }
            }
        }
        return result;
    }

    //Validate username and user password is empty
    private boolean isEmptyUserNameAndUserPassword() {
        boolean result = true;
        if (this.textFieldUserName.isVisible() && this.textFieldUserName.isEnabled() && this.textFieldUserPassword.isVisible() && this.textFieldUserPassword.isEnabled()) {
            if (this.textFieldUserName.getText().isEmpty() || this.textFieldUserPassword.getText().isEmpty()) {
                result = false;
            }
        }
        return result;
    }

    //Validate password is same
    private boolean isPasswordSame() {
        boolean result = true;
        if (this.textFieldConfirmPassword.getText().isEmpty() && this.textFieldUserPassword.getText().isEmpty()) {
            this.warningProviderPasswordNotSame.hideWarning();
            return result;
        }
        if (this.textFieldConfirmPassword.isVisible() && this.textFieldConfirmPassword.isEnabled() && !this.textFieldConfirmPassword.getText().isEmpty()) {
            if (!this.textFieldConfirmPassword.getText().equals(this.textFieldUserPassword.getText())) {
                result = false;
                this.warningProviderPasswordNotSame.showWarning();
            } else {
                this.warningProviderPasswordNotSame.hideWarning();
            }
        }
        return result;
    }

    //Validate is right cache path
//	private boolean isRightCachePath() {
//		boolean result = true;
//		if (this.fileChooserControlFileCache.getPath().isEmpty()) {
//			result = false;
//			this.warningProviderCachePathIllegal.showWarning();
//			return result;
//		} else {
//			File file = new File(this.fileChooserControlFileCache.getPath());
//			if (!file.exists() && !file.isDirectory()) {
//				result = false;
//				this.warningProviderCachePathIllegal.showWarning(MapViewProperties.getString("MapCache_WarningCachePathIsNotExits"));
//				return result;
//			} else {
//				this.warningProviderCachePathIllegal.hideWarning();
//			}
//		}
//		return result;
//	}

    private void initResources() {

        this.buttonSelectAll.setIcon(CoreResources.getIcon(urlStr + "Image_ToolButton_SelectAll.png"));
        this.buttonSelectAll.setToolTipText(CoreProperties.getString("String_ToolBar_SelectAll"));
        this.buttonSelectInverse.setIcon(CoreResources.getIcon(urlStr + "Image_ToolButton_SelectInverse.png"));
        this.buttonSelectInverse.setToolTipText(CoreProperties.getString("String_ToolBar_SelectInverse"));
        this.buttonDelete.setIcon(CoreResources.getIcon(urlStr + "Image_ToolButton_Delete.png"));
        this.buttonDelete.setToolTipText(CoreProperties.getString("String_Delete"));
        this.buttonGetServer.setText(CoreProperties.getString("String_Label_VersionCheck"));

        this.labelVersion.setText(MapViewProperties.getString("MapCache_LabelVersion"));
        this.labelSplitMode.setText(MapViewProperties.getString("MapCache_LabelSplitMode"));
        this.labelConfig.setText(MapViewProperties.getString("MapCache_LabelConfig"));
        this.labelConfigValue.setText(MapViewProperties.getString("MapCache_LabelConfigValue"));
        this.labelCacheName.setText(ControlsProperties.getString("String_CacheName"));
        this.labelCachePath.setText(CacheProperties.getString("MapCache_LabelCachePath"));
        this.labelSaveType.setText(ControlsProperties.getString("String_DatabaseType"));
        this.labelUserName.setText(ControlsProperties.getString("String_Label_UserName"));
        this.labelUserPassword.setText(ControlsProperties.getString("String_Label_UserPassword"));
        this.labelConfirmPassword.setText(ControlsProperties.getString("String_Label_ConfirmPassword"));
        this.labelServerName.setText(ControlsProperties.getString("String_Label_ServersName"));
        this.labelDatabaseName.setText(ControlsProperties.getString("String_Label_DatabaseName"));
        this.labelMultiTenseVersion.setText(MapViewProperties.getString("String_Label_VersionName"));
        this.checkBoxFilterSelectionObjectInLayer.setText(MapViewProperties.getString("MapCache_FilterSelectObjectInLayer"));
    }

    private void initComponents() {
        this.enabledListeners = new Vector<>();
        this.scrollPane = new JScrollPane();
        this.localSplitTable = new MutiTable();
        this.jMenuItemAddScale = new JMenuItem(MapViewProperties.getString("MapCache_AddScale"), CoreResources.getIcon(urlStr + "Image_ToolButton_AddScale.png"));
        this.jMenuItemDefaultScale = new JMenuItem(MapViewProperties.getString("MapCache_DefaultScale"), CoreResources.getIcon(urlStr + "Image_ToolButton_DefaultScale.png"));
        this.jMenuItemImportCacheConfigs = new JMenuItem(MapViewProperties.getString("MapCache_ImportCacheConfigs"), CoreResources.getIcon(urlStr + "Image_ToolButton_ExportScale.png"));
        this.jMenuItemImportScale = new JMenuItem(MapViewProperties.getString("MapCache_ImportScale"), CoreResources.getIcon(urlStr + "Image_ToolButton_DefaultScale.png"));
        this.jMenuItemExportCacheConfigs = new JMenuItem(MapViewProperties.getString("MapCache_ExportCacheConfig"), CoreResources.getIcon(urlStr + "Image_ToolButton_ExportScale.png"));
        this.jMenuItemExportScale = new JMenuItem(MapViewProperties.getString("MapCache_ExportScale"), CoreResources.getIcon(urlStr + "Image_ToolButton_DefaultScale.png"));
        this.labelVersion = new JLabel();
        this.labelSplitMode = new JLabel();
        this.labelConfig = new JLabel();
        this.labelConfigValue = new JLabel();
        this.comboboxVersion = new JComboBox();
        this.comboBoxSplitMode = new JComboBox();
        this.labelCacheName = new JLabel();
        this.labelCachePath = new JLabel();
        this.warningProviderCacheNameIllegal = new WarningOrHelpProvider(MapViewProperties.getString("MapCache_WarningCacheNameIsEmpty"), true);
//		this.warningProviderCachePathIllegal = new WarningOrHelpProvider(MapViewProperties.getString("MapCache_WarningCachePathIsEmpty"), true);
        this.textFieldCacheName = new JTextField();
        this.fileChooserControlFileCache = new JFileChooserControl();
        String moduleName = "ChooseCacheClipDirectories";
        if (!SmFileChoose.isModuleExist(moduleName)) {
            String defaultPath = System.getProperty("user.dir") + File.separator + "Cache";
            SmFileChoose.addNewNode("", defaultPath, GlobalParameters.getDesktopTitle(),
                    moduleName, "GetDirectories");
        }

        SmFileChoose fileChoose = new SmFileChoose(moduleName);
        this.fileChooserControlFileCache.setFileChooser(fileChoose);
        this.fileChooserControlFileCache.setPath(fileChoose.getModuleLastPath());
        this.labelSaveType = new JLabel();
        this.labelUserName = new JLabel();
        this.labelUserPassword = new JLabel();
        this.labelConfirmPassword = new JLabel();
        this.labelServerName = new JLabel();
        this.labelDatabaseName = new JLabel();
        this.labelMultiTenseVersion = new JLabel();
        this.helpProviderForDatabaseName = new WarningOrHelpProvider(MapViewProperties.getString("MapCache_DatabaseNameHelp"), false);
        this.warningProviderPasswordNotSame = new WarningOrHelpProvider(CoreProperties.getString("String_ErrorProvider_Password_Confirm"), true);
        this.checkBoxFilterSelectionObjectInLayer = new JCheckBox();
        this.checkBoxFilterSelectionObjectInLayer.setEnabled(false);
        this.comboBoxSaveType = new JComboBox();
        this.comboBoxDatabaseName = new JComboBox();
        this.comboBoxMultiTenseVersion = new JComboBox();
        this.textFieldUserName = new JTextField();
        this.textFieldUserPassword = new JPasswordField();
        this.textFieldConfirmPassword = new JPasswordField();
        this.textFieldServerName = new JTextField();
        this.buttonGetServer = new JButton();
        initToolBar();
    }

    private void initGlobalValue() {
        this.scientificNotation.setGroupingUsed(false);
        if (cmdType == DialogMapCacheClipBuilder.MultiUpdateProcessClip
                || cmdType == DialogMapCacheClipBuilder.ResumeProcessClip
                || cmdType == DialogMapCacheClipBuilder.SingleUpdateProcessClip) {
            this.originMapCacheScale = this.mapCacheBuilder.getOutputScales();
        } else {
            this.originMapCacheScale = this.mapCacheBuilder.getDefultOutputScales();
        }
        setLocalSplitTableValue(this.originMapCacheScale);
//		setLocalSplitTableValue(this.mapCacheBuilder.getOutputScaleCaptions());
        this.currentMapCacheScale = new ArrayList<>();
        for (int i = 0; i < this.originMapCacheScale.length; i++) {
            this.currentMapCacheScale.add(this.originMapCacheScale[i]);
        }
    }

    private void initLocalSplitTable() {
        DDLExportTableModel tableModel = new DDLExportTableModel(new String[]{"index", "scale", "title"}) {
            boolean[] columnEditables = new boolean[]{false, true, true};

            @Override
            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }
        };
        this.localSplitTable.setModel(tableModel);
        this.localSplitTable.setRowHeight(23);
        this.localSplitTable.getTableHeader().setReorderingAllowed(false);
        this.localSplitTable.getColumnModel().getColumn(COLUMN_INDEX).setMaxWidth(40);
        this.localSplitTable.getColumnModel().getColumn(COLUMN_TITLE).setMaxWidth(125);
        this.localSplitTable.getColumnModel().getColumn(COLUMN_TITLE).setMinWidth(125);
        this.localSplitTable.getColumnModel().getColumn(COLUMN_INDEX).setHeaderValue("");
        this.localSplitTable.getColumnModel().getColumn(COLUMN_SCALE).setHeaderValue(MapViewProperties.getString("MapCache_Scale"));
        this.localSplitTable.getColumnModel().getColumn(COLUMN_TITLE).setHeaderValue(CoreProperties.getString("String_Caption"));
    }

    private void setLocalSplitTableValue(double value[]) {
        try {
            for (int i = 0; i < value.length; i++) {
                Object[] temp = new Object[3];
                temp[COLUMN_INDEX] = i + 1 + " ";
                temp[COLUMN_SCALE] = SHOW_SCALE_PRE_PART + String.valueOf(DoubleUtilities.getFormatString(Double.valueOf(this.scientificNotation.format(1 / value[i]))));
                temp[COLUMN_TITLE] = (int) (Math.round(1 / value[i])) + " ";
                this.localSplitTable.addRow(temp);
            }
            addMoreScale();
            localSplitTable.setRowSelectionInterval(0, 0);
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        }
    }

    private void addMoreScale() {
        Object[] temp = new Object[3];
        temp[COLUMN_INDEX] = "*";
        temp[COLUMN_SCALE] = "";
        temp[COLUMN_TITLE] = "";
        this.localSplitTable.addRow(temp);
    }

    private void initToolBar() {
        this.toolBar = new JToolBar();
        this.addScaleDropDown = new ComponentDropDown(ComponentDropDown.IMAGE_TYPE);
        JPopupMenu popupMenuScale = new JPopupMenu();
        popupMenuScale.add(jMenuItemAddScale);
        popupMenuScale.add(jMenuItemDefaultScale);
        this.addScaleDropDown.setPopupMenu(popupMenuScale);
        this.addScaleDropDown.setToolTip(MapViewProperties.getString("String_AddScale"));
        this.addScaleDropDown.setIcon(CoreResources.getIcon(urlStr + "Image_ToolButton_AddScale.png"));

        this.importDropDown = new ComponentDropDown(ComponentDropDown.IMAGE_TYPE);
        JPopupMenu popupMenuImport = new JPopupMenu();
        popupMenuImport.add(jMenuItemImportCacheConfigs);
        popupMenuImport.add(jMenuItemImportScale);
        this.importDropDown.setPopupMenu(popupMenuImport);
        this.importDropDown.setToolTip(ControlsProperties.getString("String_Import"));
        this.importDropDown.setIcon(CoreResources.getIcon(urlStr + "Image_ToolButton_Import.png"));

        this.exportDropDown = new ComponentDropDown(ComponentDropDown.IMAGE_TYPE);
        JPopupMenu popupMenuExport = new JPopupMenu();
        popupMenuExport.add(jMenuItemExportCacheConfigs);
        popupMenuExport.add(jMenuItemExportScale);
        this.exportDropDown.setPopupMenu(popupMenuExport);
        this.exportDropDown.setToolTip(ControlsProperties.getString("String_Export"));
        this.exportDropDown.setIcon(CoreResources.getIcon(urlStr + "Image_ToolButton_Export.png"));

        this.buttonSelectAll = new JButton();
        this.buttonSelectInverse = new JButton();
        this.buttonDelete = new JButton();
        this.toolBar.add(this.addScaleDropDown);
        this.toolBar.addSeparator();
        this.toolBar.add(this.buttonSelectAll);
        this.toolBar.add(this.buttonSelectInverse);
        this.toolBar.add(this.buttonDelete);
        this.toolBar.addSeparator();
        this.toolBar.add(this.importDropDown);
        this.toolBar.add(this.exportDropDown);
        this.toolBar.setFloatable(false);
    }

    //Init global cache scales
    public void initGlobalCacheScales() {
        int[] levels = new int[21]; //Add global cache scale to 21
        for (int i = 0; i < 21; i++) {
            levels[i] = i;
        }
        this.mapCacheBuilder.setMap(MapUtilities.getActiveMap() == null ? CacheUtilities.getWorkspaceSelectedMap() : MapUtilities.getActiveMap());
        this.globalSplitScale = this.mapCacheBuilder.globalLevelToScale(levels);
        double avaliableScale = getAvailableScale();

        double dValue = Double.MAX_VALUE;
        String avaliableLevel = "0";
        if (avaliableScale != -1) {
            Object[] keys = this.globalSplitScale.keySet().toArray();
            for (int i = 0; i < keys.length; i++) {
                double temp = Double.valueOf(keys[i].toString());
                double currentDValue = Math.abs(avaliableScale - (1.0 / temp));
                if (currentDValue < dValue) {
                    dValue = currentDValue;
                    avaliableLevel = this.globalSplitScale.get(temp);
                }
            }
        }
        initGlobalSplitTable();
        setGlobalSplitTableValue(avaliableLevel);
    }

    private void initGlobalSplitTable() {
        boolean[] columnEditables = new boolean[]{false, false};
        if (this.globalSplitTable == null) {
            this.globalSplitTable = new SmChooseTable(new Object[this.globalSplitScale.size()][2], globalSplitTableTitle, columnEditables);
        }
        this.globalSplitTable.getTableHeader().setReorderingAllowed(false);
        this.globalSplitTable.getColumn(globalSplitTable.getModel().getColumnName(COLUMN_INDEX)).setMaxWidth(60);
        this.globalSplitTable.getColumnModel().getColumn(COLUMN_INDEX).setCellRenderer(new MultipleCheckboxTableRenderer());
        this.globalSplitTable.getTableHeader().getColumnModel().getColumn(COLUMN_INDEX).setHeaderRenderer(new MultipleCheckboxTableHeaderCellRenderer(this.globalSplitTable, MapViewProperties.getString("MapCache_LayerSeries"), true));
    }

    //Insert value into globalSplitTable
    private void setGlobalSplitTableValue(String avaliableLevel) {
        try {
            this.globalScaleSortKeys = this.globalSplitScale.keySet().toArray(new Double[this.globalSplitScale.size()]);
            double temp;
            for (int i = 0; i < globalScaleSortKeys.length - 1; i++) {
                for (int j = 0; j < globalScaleSortKeys.length - i - 1; j++) {
                    if (Double.compare(globalScaleSortKeys[j], globalScaleSortKeys[j + 1]) == 1) {
                        temp = globalScaleSortKeys[j];
                        globalScaleSortKeys[j] = globalScaleSortKeys[j + 1];
                        globalScaleSortKeys[j + 1] = temp;
                    }
                }
            }
            boolean isSelected = false;
            for (int i = 0; i < globalScaleSortKeys.length; i++) {
                temp = globalScaleSortKeys[i];
                MultipleCheckboxItem multipleCheckboxItem = new MultipleCheckboxItem();
                multipleCheckboxItem.setText(this.globalSplitScale.get(temp));
                if (this.globalSplitScale.get(temp).equals(avaliableLevel)
                        && (cmdType == DialogMapCacheClipBuilder.SingleProcessClip || cmdType == DialogMapCacheClipBuilder.MultiProcessClip)) {
                    isSelected = true;
                }
                multipleCheckboxItem.setSelected(isSelected);
                this.globalSplitTable.setValueAt(multipleCheckboxItem, i, 0);
                this.globalSplitTable.setValueAt(this.SHOW_SCALE_PRE_PART + String.valueOf(DoubleUtilities.getFormatString(Double.valueOf(this.scientificNotation.format(1 / temp)))), i, COLUMN_SCALE);
            }
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        }
    }

    private double getAvailableScale() {
        try {
            if (null != this.currentMap) {
                return getScale(this.currentMap);
            } else {
                Workspace wk = Application.getActiveApplication().getWorkspace();
                Map map = new Map(wk);
                map.setName(this.mapCacheBuilder.getCacheName());
                map.open(mapCacheBuilder.getCacheName());
                Map newMap = new Map(wk);
                newMap.fromXML(map.toXML());
                return getScale(newMap);
            }
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
            return 1.0;
        }
    }

    private double getScale(Map map) {
        double dScale = 0;
        if (null != map && null != map.getPrjCoordSys().getGeoCoordSys()) {
            double DPI = map.getDPI();
            double lp = DPI / 25.4;
            Integer tileSize = Convert.toInteger(mapCacheBuilder.getTileSize().value());
            double dLPSide = tileSize / lp * 10;
            Unit mapUnit = map.getCoordUnit();
            Rectangle2D rcFinalBounds = mapCacheBuilder.getBounds();
            double dLPMPCoordRatio = dLPSide / rcFinalBounds.getWidth();

            double axis = map.getPrjCoordSys().getGeoCoordSys().getGeoDatum().getGeoSpheroid().getAxis();
            if (mapUnit == Unit.DEGREE) {
                dScale = dLPMPCoordRatio / (axis * 10000 * Math.PI / 180);
            } else {
                dScale = dLPMPCoordRatio / (Convert.toDouble(mapUnit));
            }
        } else {
            dScale = 1;
        }
        return 1.0 / dScale;
    }

    //Add new scale
    private void addScale() {
        int insertIndex = this.localSplitTable.getRowCount();
        if (this.localSplitTable.getRowCount() == 1) {
            if (this.originMapCacheScale.length > 0) {
                this.currentMapCacheScale.add(this.originMapCacheScale[0]);
            } else if (null != Application.getActiveApplication().getActiveForm() && Application.getActiveApplication().getActiveForm() instanceof IFormMap) {
                this.currentMapCacheScale.add(((IFormMap) Application.getActiveApplication().getActiveForm()).getMapControl().getMap().getScale());
            } else if (null != CacheUtilities.getWorkspaceSelectedMap()) {
                this.currentMapCacheScale.add(CacheUtilities.getWorkspaceSelectedMap().getScale());
            }
        } else {
            double insertScale = 0;
            int selectedRowsIndex[] = this.localSplitTable.getSelectedRows();
            if (selectedRowsIndex != null && selectedRowsIndex.length >= 1 && selectedRowsIndex[selectedRowsIndex.length - 1] + 1 != this.localSplitTable.getRowCount()) {
                insertIndex = selectedRowsIndex[selectedRowsIndex.length - 1] + 2;
            }
            if (insertIndex == this.localSplitTable.getRowCount()) {
                insertScale = this.currentMapCacheScale.get(insertIndex - 2) * 2;
            } else {
                insertScale = (this.currentMapCacheScale.get(insertIndex - 2) + this.currentMapCacheScale.get(insertIndex - 1)) / 2;
            }
            this.currentMapCacheScale.add(insertIndex - 1, insertScale);
        }
        double addScaleAfter[] = new double[this.currentMapCacheScale.size()];
        for (int i = 0; i < addScaleAfter.length; i++) {
            addScaleAfter[i] = this.currentMapCacheScale.get(i);
        }
        DDLExportTableModel model = (DDLExportTableModel) this.localSplitTable.getModel();
        model.removeRows(0, model.getRowCount());
        setLocalSplitTableValue(addScaleAfter);
        this.localSplitTable.setRowSelectionInterval(insertIndex - 1, insertIndex - 1);
        checkButtonState();
    }

    //Add default scale
    private void addDefaultScale() {
        DDLExportTableModel model = (DDLExportTableModel) localSplitTable.getModel();
        model.removeRows(0, model.getRowCount());
        setLocalSplitTableValue(originMapCacheScale);
        currentMapCacheScale.clear();
        for (int i = 0; i < originMapCacheScale.length; i++) {
            currentMapCacheScale.add(originMapCacheScale[i]);
        }
        localSplitTable.clearSelection();
    }

    //Import scales
    private void importXml() {
        try {
            String filePath = getFilePathForImport();
            this.currentMapCacheScale.clear();
            if (!StringUtilities.isNullOrEmpty(filePath)) {
                File file = new File(filePath);
                FileInputStream fis = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                String tempstr = "";
                while ((tempstr = br.readLine()) != null) {
                    if (tempstr.contains("<Scale>")) {
                        tempstr = tempstr.substring(tempstr.indexOf(">") + 1, tempstr.lastIndexOf("<"));
                        String[] temp = tempstr.split(":");
                        this.currentMapCacheScale.add(1 / Double.valueOf(temp[1]));
                    }
                }
                double addScaleAfter[] = new double[this.currentMapCacheScale.size()];
                for (int i = 0; i < addScaleAfter.length; i++) {
                    addScaleAfter[i] = this.currentMapCacheScale.get(i);
                }
                DDLExportTableModel model = (DDLExportTableModel) this.localSplitTable.getModel();
                model.removeRows(0, model.getRowCount());
                setLocalSplitTableValue(addScaleAfter);
            }

        } catch (IOException e) {
            Application.getActiveApplication().getOutput().output(e);
        }
    }

    //Create a new FileChooser for import scales
    private String getFilePathForImport() {
        String filePath = "";
        String title = CoreProperties.getString("String_Import");
        if (!SmFileChoose.isModuleExist("ImportScale")) {
            String fileFilter = SmFileChoose.createFileFilter(MapViewProperties.getString("String_ScaleFile"), "xml");
            SmFileChoose.addNewNode(fileFilter, MapViewProperties.getString("String_ScaleFile"), title, "ImportScale", "OpenOne");
        }
        SmFileChoose fileChoose = new SmFileChoose("ImportScale");
        int stateTemp = fileChoose.showDefaultDialog();
        if (stateTemp == JFileChooser.APPROVE_OPTION) {
            filePath = fileChoose.getFilePath();
        }
        return filePath;
    }

    //Export scales
    protected void exportXml() {
        createXml(getFilePathForExport());
    }

    private void createXml(String filename) {
        try {
            DecimalFormat roundingTwoPoint = new DecimalFormat("#.00");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element scales = document.createElement("Scales");
            scales.setAttribute("xmlns", "http://www.supermap.com.cn/desktop");
            scales.setAttribute("version", "8.1.x");
            document.appendChild(scales);
            List<String> tempList = new ArrayList<String>();

            if (this.mapCacheBuilder.getTilingMode() == MapTilingMode.LOCAL) {
                for (int i = 0; i < this.localSplitTable.getRowCount() - 1; i++) {
                    String temp = this.localSplitTable.getValueAt(i, COLUMN_SCALE).toString();
                    tempList.add(i, SHOW_SCALE_PRE_PART + roundingTwoPoint.format(DoubleUtilities.stringToValue(temp.split(":")[1])).toString());
                }
            } else {
                for (int i = 0; i < this.globalSplitTable.getRowCount(); i++) {
                    String temp = this.globalSplitTable.getValueAt(i, COLUMN_SCALE).toString();
                    tempList.add(i, SHOW_SCALE_PRE_PART + roundingTwoPoint.format(DoubleUtilities.stringToValue(temp.split(":")[1])).toString());
                }
            }
            for (int i = 0; i < tempList.size(); i++) {
                Element scale = document.createElement("Scale");
                String scaleCaption = tempList.get(i);
                scale.appendChild(document.createTextNode(scaleCaption));
                scale.setNodeValue(scaleCaption);
                scales.appendChild(scale);
            }
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(document);
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            if (StringUtilities.isNullOrEmpty(filename)) {
                return;
            }
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
                parseFileToXML(transformer, source, file);
            } else if (JOptionPane.OK_OPTION == new SmOptionPane().showConfirmDialog(MapViewProperties.getString("String_RenameFile_Message"))) {
                parseFileToXML(transformer, source, file);
            }
            if (file.exists() && file.length() > 0) {
                Application.getActiveApplication().getOutput()
                        .output(MessageFormat.format(MapViewProperties.getString("String_ExportScale_Scucess_Info"), file.getPath()));
            }
        } catch (Exception e) {
            Application.getActiveApplication().getOutput().output(e);
        }
    }

    private void parseFileToXML(Transformer transformer, DOMSource source, File file) throws FileNotFoundException, TransformerException {
        PrintWriter pw = new PrintWriter(file);
        StreamResult streamResult = new StreamResult(pw);
        transformer.transform(source, streamResult);
    }

    private String getFilePathForExport() {
        String filePath = "";
        if (!SmFileChoose.isModuleExist("ExportScale")) {
            String fileFilter = SmFileChoose.createFileFilter(MapViewProperties.getString("String_ScaleFile"), "xml");
            SmFileChoose.addNewNode(fileFilter, MapViewProperties.getString("String_ScaleFile"), CoreProperties.getString("String_Export"),
                    "ExportScale", "GetDirectories");
        }
        SmFileChoose tempfileChooser = new SmFileChoose("ExportScale");
        tempfileChooser.setSelectedFile(new File(MapViewProperties.getString("String_Scales") + ".xml"));
        int state = tempfileChooser.showDefaultDialog();
        if (state == JFileChooser.APPROVE_OPTION) {
            filePath = tempfileChooser.getFilePath();
        }
        return filePath;
    }

    private void exportCacheConfigFile() {
        try {
            String moduleName = "ExportCacheConfigFile";
            if (!SmFileChoose.isModuleExist(moduleName)) {
                String fileFilters = SmFileChoose.createFileFilter(MapViewProperties.getString("MapCache_CacheConfigFile"), "sci");
                SmFileChoose.addNewNode(fileFilters, CoreProperties.getString("String_DefaultFilePath"),
                        ControlsProperties.getString("String_SaveAsFile"), moduleName, "SaveOne");
            }
            SmFileChoose smFileChoose = new SmFileChoose(moduleName);
            smFileChoose.setSelectedFile(new File(MapViewProperties.getString("MapCache_CacheConfigFileIsNotbrackets")));
            int state = smFileChoose.showDefaultDialog();
            String filePath = "";
            if (state == JFileChooser.APPROVE_OPTION) {
                filePath = smFileChoose.getFilePath();
                File oleFile = new File(filePath);
                filePath = filePath.substring(0, filePath.lastIndexOf(".")) + ".sci";
                File NewFile = new File(filePath);
                oleFile.renameTo(NewFile);
                if (oleFile.isFile() && oleFile.exists()) {
                    oleFile.delete();
                }
                parent.setOutputScalesInfo(this.mapCacheBuilder);
                parent.setMapCacheBuilderBasicInfo(this.mapCacheBuilder);
                boolean result = mapCacheBuilder.toConfigFile(filePath);
                if (result) {
                    Application.getActiveApplication().getOutput().output(MapViewProperties.getString("MapCache_ToCacheConfigFileIsSuccessed") + filePath);
                } else {
                    Application.getActiveApplication().getOutput().output(MapViewProperties.getString("MapCache_ToCacheConfigFileIsFailed"));
                }
            }
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex.toString());
        }
    }

    private void importCacheConfigFile() {
        String moduleName = "InputCacheConfigFile";
        if (!SmFileChoose.isModuleExist(moduleName)) {
            String fileFilters = SmFileChoose.buildFileFilters(SmFileChoose.createFileFilter(MapViewProperties.getString("MapCache_CacheConfigFile"), "sci"));
            SmFileChoose.addNewNode(fileFilters, CoreProperties.getString("String_DefaultFilePath"),
                    ControlsProperties.getString("String_OpenColorTable"), moduleName, "OpenMany");
        }
        SmFileChoose smFileChoose = new SmFileChoose(moduleName);
        int state = smFileChoose.showDefaultDialog();
        String filePath = "";
        if (state == JFileChooser.APPROVE_OPTION) {
            filePath = smFileChoose.getFilePath();
            if (mapCacheBuilder.fromConfigFile(filePath)) {
                File file = new File(filePath);
                importCacheConfigs = true;
                resetComponentsInfo();
                fileChooserControlFileCache.setPath(file.getParent());
                Application.getActiveApplication().getOutput().output(MapViewProperties.getString("MapCache_FromCacheConfigFileIsSuccessed") + filePath);
            } else {
                Application.getActiveApplication().getOutput().output(MapViewProperties.getString("MapCache_FromCacheConfigFileIsFailed"));
            }
            checkButtonState();
        }
    }

    public void resetComponentsInfo() {
        HashMap<Double, String> scalesAndNames = mapCacheBuilder.getOutputScaleCaptions();
        if (mapCacheBuilder.getTilingMode() == MapTilingMode.LOCAL) {
            comboBoxSplitMode.setSelectedItem(MapViewProperties.getString("MapCache_ComboboxSplitModeLocalSplit"));
            DDLExportTableModel model = (DDLExportTableModel) localSplitTable.getModel();
            model.removeRows(0, model.getRowCount());
            setLocalSplitTableValue(scalesAndNames);
        } else {
            comboBoxSplitMode.setSelectedItem(MapViewProperties.getString("MapCache_ComboboxSplitModeGlobalSplit"));
            for (String scaleName : scalesAndNames.values()) {
                MultipleCheckboxItem multipleCheckboxItem = (MultipleCheckboxItem) FirstStepPane.this.globalSplitTable.getValueAt(Integer.valueOf(scaleName), COLUMN_INDEX);
                multipleCheckboxItem.setSelected(true);
                FirstStepPane.this.globalSplitTable.setValueAt(multipleCheckboxItem, Integer.valueOf(scaleName), COLUMN_INDEX);
            }
        }
        labelConfigValue.setText(this.mapCacheBuilder.getCacheName());
        textFieldCacheName.setText(mapCacheBuilder.getCacheName());
        if (mapCacheBuilder.getStorageType() == StorageType.Compact) {
            comboBoxSaveType.setSelectedItem(COMPACT);
            textFieldUserPassword.setText(mapCacheBuilder.getPassword());
            textFieldConfirmPassword.setText(mapCacheBuilder.getPassword());
        } else if (mapCacheBuilder.getStorageType() == StorageType.Original) {
            comboBoxSaveType.setSelectedItem(ORIGIN);
        } else if (mapCacheBuilder.getStorageType() == StorageType.MongoDB) {
            comboBoxSaveType.setSelectedItem(MONGODB);
            CacheWriter cacheFile = new CacheWriter();
            cacheFile.FromConfigFile(sciPath);
            String[] mongoInfo = cacheFile.getMongoConnectionInfo();
            if (null != mongoInfo) {
                textFieldServerName.setText(mongoInfo[0]);
                comboBoxDatabaseName.removeAllItems();
                comboBoxDatabaseName.addItem(mongoInfo[1]);
                textFieldUserName.setText(mongoInfo[2]);
                if (mongoInfo.length == 4) {
                    textFieldUserPassword.setText(mongoInfo[3]);
                    textFieldConfirmPassword.setText(mongoInfo[3]);
                }
            }
        } else if (mapCacheBuilder.getStorageType() == StorageType.GPKG) {
            comboBoxSaveType.setSelectedItem(GEOPACKAGE);
        }
    }

    private void selectInvert() {
        try {
            int[] temp = localSplitTable.getSelectedRows();
            ArrayList<Integer> selectedRows = new ArrayList();
            for (int index = 0; index < temp.length; index++) {
                selectedRows.add(temp[index]);
            }

            ListSelectionModel selectionModel = localSplitTable.getSelectionModel();
            selectionModel.clearSelection();
            for (int index = 0; index < localSplitTable.getRowCount(); index++) {
                if (!selectedRows.contains(index)) {
                    selectionModel.addSelectionInterval(index, index);
                }
            }
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        }
    }

    private void checkButtonState() {
        if (this.localSplitTable.getRowCount() > 1) {
            resetButtonState(true);
        } else {
            resetButtonState(false);
        }
        if (this.localSplitTable.getSelectedRows().length >= 1 && this.localSplitTable.getRowCount() > 1) {
            this.buttonDelete.setEnabled(true);
        } else {
            this.buttonDelete.setEnabled(false);
        }
    }

    private void resetButtonState(boolean enable) {
        this.buttonSelectAll.setEnabled(enable);
        this.buttonSelectInverse.setEnabled(enable);
        this.exportDropDown.setEnabled(enable);
    }

    private void setLocalSplitTableValue(HashMap<Double, String> scalesAndNames) {
        try {
            Double[] inputScales = scalesAndNames.keySet().toArray(new Double[scalesAndNames.size()]);
            for (int i = 0; i < inputScales.length - 1; i++) {
                for (int j = 0; j < inputScales.length - i - 1; j++) {
                    if (Double.compare(inputScales[j], inputScales[j + 1]) == 1) {
                        double temp = inputScales[j];
                        inputScales[j] = inputScales[j + 1];
                        inputScales[j + 1] = temp;
                    }
                }
            }
            this.currentMapCacheScale.clear();
            for (int i = 0; i < scalesAndNames.size(); i++) {
                Object[] temp = new Object[3];
                temp[COLUMN_INDEX] = i + 1 + " ";
                temp[COLUMN_SCALE] = SHOW_SCALE_PRE_PART + String.valueOf(DoubleUtilities.getFormatString(Double.valueOf(this.scientificNotation.format(1 / inputScales[i]))));
                temp[COLUMN_TITLE] = scalesAndNames.get(inputScales[i]);
                this.currentMapCacheScale.add(inputScales[i]);
                this.localSplitTable.addRow(temp);
            }
            addMoreScale();
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex.toString());
        }
    }

    private void deleteScale() {
        int selectedRowsIndex[] = localSplitTable.getSelectedRows();
        int tableCount = localSplitTable.getRowCount();
        List<Double> removeInfo = new ArrayList();
        localSplitTable.clearSelection();
        if (selectedRowsIndex != null & selectedRowsIndex.length >= 1) {
            for (int i = 0; i < selectedRowsIndex.length; i++) {
                if (selectedRowsIndex[i] + 1 != tableCount) {
                    removeInfo.add(currentMapCacheScale.get(selectedRowsIndex[i]));
                }
            }
        }
        if (removeInfo.size() > 0) {
            currentMapCacheScale.removeAll(removeInfo);
            double addScaleAfter[] = new double[currentMapCacheScale.size()];
            for (int i = 0; i < addScaleAfter.length; i++) {
                addScaleAfter[i] = currentMapCacheScale.get(i);
            }
            DDLExportTableModel model = (DDLExportTableModel) localSplitTable.getModel();
            model.removeRows(selectedRowsIndex);
            localSplitTable.refreshContents(getNewLocalSplitTableValue(addScaleAfter));
            addMoreScale();
            if (selectedRowsIndex != null) {
                int deleteAfaterSelectedRowIndex = selectedRowsIndex[0];
                if (deleteAfaterSelectedRowIndex + 1 == localSplitTable.getRowCount() && localSplitTable.getRowCount() > 1) {
                    localSplitTable.setRowSelectionInterval(deleteAfaterSelectedRowIndex - 1, deleteAfaterSelectedRowIndex - 1);
                } else {
                    localSplitTable.setRowSelectionInterval(deleteAfaterSelectedRowIndex, deleteAfaterSelectedRowIndex);
                }
            }
        }
    }

    private Object[][] getNewLocalSplitTableValue(double value[]) {
        Object[][] result = new Object[value.length][3];
        for (int i = 0; i < value.length; i++) {
            result[i][COLUMN_INDEX] = i + 1 + " ";
            result[i][COLUMN_SCALE] = SHOW_SCALE_PRE_PART + String.valueOf(DoubleUtilities.getFormatString(Double.valueOf(this.scientificNotation.format(1 / value[i]))));
            result[i][COLUMN_TITLE] = (int) (Math.round(1 / value[i])) + " ";
        }
        return result;
    }

    //Table model listener for localSplitTable
    private void tableModelChanged(TableModelEvent e) {
        int selectRow = e.getFirstRow();
        if (selectRow >= this.currentMapCacheScale.size()) {
            setTableCell(e.getFirstRow(), e.getColumn(), "");
            return;
        }
        String oldValue = SHOW_SCALE_PRE_PART + String.valueOf(1 / this.currentMapCacheScale.get(selectRow));
        String selectValue = localSplitTable.getValueAt(selectRow, e.getColumn()).toString();
        if (e.getColumn() == COLUMN_SCALE) {

            if (!ScaleModel.isLegitScaleString(selectValue)) {
                setTableCell(selectRow, COLUMN_SCALE, oldValue);
                Application.getActiveApplication().getOutput().output(MapViewProperties.getString("String_ErrorInput"));
                return;
            }
            if (ScaleModel.isLegitScaleString(selectValue) && selectValue.contains(COLON)) {
                setTableCell(selectRow, COLUMN_SCALE, selectValue);
                return;
            }
            if (!selectValue.contains(COLON) && ScaleModel.isLegitScaleString(SHOW_SCALE_PRE_PART + selectValue)) {
                setTableCell(selectRow, COLUMN_SCALE, SHOW_SCALE_PRE_PART + selectValue);
                return;
            }
        } else if (e.getColumn() == COLUMN_TITLE) {
            setTableCell(selectRow, COLUMN_TITLE, selectValue);
        }
        checkButtonState();
    }

    //Change table cell value
    private void setTableCell(int selectRow, int selectColumn, String value) {
        this.localSplitTable.getModel().removeTableModelListener(this.tableModelListener);
        if (selectColumn == COLUMN_SCALE && !value.equals("")) {
            this.localSplitTable.setValueAt(SHOW_SCALE_PRE_PART + DoubleUtilities.getFormatString(DoubleUtilities.stringToValue(value.split(":")[1])), selectRow, selectColumn);
            this.currentMapCacheScale.set(selectRow, 1 / DoubleUtilities.stringToValue(value.split(COLON)[1]));
        } else if (selectColumn == COLUMN_TITLE && !value.equals("")) {
            this.localSplitTable.setValueAt(value, selectRow, selectColumn);
        } else if (value.equals("")) {
            this.localSplitTable.setValueAt(value, selectRow, selectColumn);
        }
        this.localSplitTable.getModel().addTableModelListener(this.tableModelListener);
    }

    //ComboBoxSplitMode change listener
    private void splitModeChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (MapViewProperties.getString("MapCache_ComboboxSplitModeLocalSplit").equals(e.getItem().toString())) {
                mapCacheBuilder.setTilingMode(MapTilingMode.LOCAL);
                scrollPane.setViewportView(localSplitTable);
                addScaleDropDown.setEnabled(true);
                buttonDelete.setEnabled(true);
                importDropDown.setEnabled(true);
            } else {
                mapCacheBuilder.setTilingMode(MapTilingMode.GLOBAL);
                globalSplitTable = null;
                initGlobalCacheScales();
                scrollPane.setViewportView(globalSplitTable);
                addScaleDropDown.setEnabled(false);
                buttonDelete.setEnabled(false);
                importDropDown.setEnabled(false);
            }
        }
    }

    public void setComponentsEnabled(boolean enabled) {
        this.comboboxVersion.setEnabled(enabled);
        this.comboBoxSplitMode.setEnabled(enabled);
        this.textFieldCacheName.setEnabled(enabled);
        this.fileChooserControlFileCache.setEnabled(enabled);
        this.comboBoxSaveType.setEnabled(enabled);
        this.comboBoxDatabaseName.setEnabled(enabled);
        this.comboBoxMultiTenseVersion.setEnabled(enabled);
        this.textFieldUserName.setEnabled(enabled);
        this.textFieldUserPassword.setEnabled(enabled);
        this.textFieldConfirmPassword.setEnabled(enabled);
        this.textFieldServerName.setEnabled(enabled);
    }

    @Override
    public void addEnabledListener(EnabledListener enabledListener) {
        if (null == enabledListeners) {
            enabledListeners = new Vector<>();
        }
        if (null != enabledListener && !enabledListeners.contains(enabledListener)) {
            enabledListeners.add(enabledListener);
        }
    }

    @Override
    public void removeEnabledListener(EnabledListener enabledListener) {
        if (null != enabledListener && enabledListeners.contains(enabledListener)) {
            this.enabledListeners.remove(enabledListener);
        }

    }

    public String getSciPath() {
        return sciPath;
    }

    public void setSciPath(String sciPath) {
        this.sciPath = sciPath;
    }

    private void fireEnabled(boolean enabled) {
        Vector<EnabledListener> listeners = this.enabledListeners;
        for (EnabledListener listener : listeners) {
            listener.setEnabled(new EnabledEvent(enabled));
        }
    }
}
