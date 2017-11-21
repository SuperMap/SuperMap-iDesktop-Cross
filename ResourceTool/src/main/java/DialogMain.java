package main.java;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import static main.java.PropertiesUtilites.modifyTypeAndFiles;
import static main.java.PropertiesUtilites.processResultPath;

/**
 * Created by lixiaoyao on 2017/11/9.
 */
public class DialogMain extends SmDialog {

	private JPanel panelFileChooseAndUpdate;
	private JLabel labelKeyModifyType;
	private JComboBox comboBoxModifyType;
	private JLabel labelFileName;
	private JComboBox comboBoxFileName;
	private JButton buttonUpdateFile;
	private JPanel panelProcessFile;
	private JScrollPane scrollPane;
	private JTable tableKey;
	private JScrollPane ScrollPanelChineseValue;
	private JTextArea textAreaChineseValue;
	private JScrollPane ScrollPanelEnglishValue;
	private JTextArea textAreaEnglishValue;
	private JButton buttonCommit;

	private static final int DEFAULT_HEIGHT = 23;
	private static final int COMBOX_WIDTH = 200;
	private static final Font FONT=new Font("Serif",0,20);
	private String selectedModifyType = "";
	private String selectedFileName = "";
	private ModelKeyValue modelKeyValue;
	private DialogUpdateFiles dialogUpdateFiles= new DialogUpdateFiles();
	private static UndoManager undoManager=new UndoManager();

	public DialogMain() {
		this.setSize(new Dimension(1400, 900));
		this.setLocationRelativeTo(null);
		initComponents();
		initLayout();
		initResource();
		if (PropertiesUtilites.readUnProcessKeyValue()) {
			JOptionPane.showMessageDialog(this, ResourceToolProperties.getString("String_Message"));
		}
		PropertiesUtilites.initFilePath();
		initTable();
		removeEvents();
		registerEvents();
	}

	private void initComponents() {
		this.panelFileChooseAndUpdate = new JPanel();
		this.labelKeyModifyType = new JLabel();
		this.comboBoxModifyType = new JComboBox();
		this.labelFileName = new JLabel();
		this.comboBoxFileName = new JComboBox();
		this.buttonUpdateFile = new JButton();
		this.panelProcessFile = new JPanel();
		this.scrollPane = new JScrollPane();
		this.tableKey = new JTable();
		this.ScrollPanelChineseValue = new JScrollPane();
		this.textAreaChineseValue = new JTextArea();
		this.ScrollPanelEnglishValue = new JScrollPane();
		this.textAreaEnglishValue = new JTextArea();
		this.buttonCommit = new JButton();
		this.scrollPane.setViewportView(this.tableKey);
		this.ScrollPanelChineseValue.setViewportView(this.textAreaChineseValue);
		this.ScrollPanelEnglishValue.setViewportView(this.textAreaEnglishValue);
		this.textAreaChineseValue.setFont(FONT);
		this.textAreaEnglishValue.setFont(FONT);
		this.textAreaChineseValue.setEditable(false);
		this.modelKeyValue = new ModelKeyValue();
		this.tableKey.setModel(this.modelKeyValue);
		this.tableKey.getColumnModel().getColumn(this.modelKeyValue.COLUMN_INDEX).setPreferredWidth(30);
		this.tableKey.getColumnModel().getColumn(this.modelKeyValue.COLUMN_KEY).setMinWidth(500);
		this.tableKey.setRowHeight(23);
		this.tableKey.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	private void initLayout() {
		initLayoutForPanelFileChoose();
		initLayoutForPanelProcess();

		GroupLayout groupLayout = new GroupLayout(this.getContentPane());
		groupLayout.setAutoCreateContainerGaps(true);
		groupLayout.setAutoCreateGaps(true);

		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.panelFileChooseAndUpdate)
						.addComponent(this.panelProcessFile)
						.addGroup(groupLayout.createSequentialGroup()
								.addGap(200, 200, Short.MAX_VALUE)
								.addComponent(this.buttonCommit))
				));
		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addComponent(this.panelFileChooseAndUpdate, 35, 35, 35)
				.addComponent(this.panelProcessFile, 250, 250, Short.MAX_VALUE)
				.addComponent(this.buttonCommit, DEFAULT_HEIGHT, DEFAULT_HEIGHT, DEFAULT_HEIGHT)
		);
		this.setLayout(groupLayout);
	}

	private void initLayoutForPanelFileChoose() {
		GroupLayout groupLayout = new GroupLayout(this.panelFileChooseAndUpdate);
		groupLayout.setAutoCreateContainerGaps(true);
		groupLayout.setAutoCreateGaps(true);

		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
				.addComponent(this.labelKeyModifyType)
				.addComponent(this.comboBoxModifyType, COMBOX_WIDTH, COMBOX_WIDTH, COMBOX_WIDTH)
				.addComponent(this.labelFileName)
				.addComponent(this.comboBoxFileName, COMBOX_WIDTH, COMBOX_WIDTH, COMBOX_WIDTH)
				.addComponent(this.buttonUpdateFile)
		);

		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.labelKeyModifyType, DEFAULT_HEIGHT, DEFAULT_HEIGHT, DEFAULT_HEIGHT)
						.addComponent(this.comboBoxModifyType, DEFAULT_HEIGHT, DEFAULT_HEIGHT, DEFAULT_HEIGHT)
						.addComponent(this.labelFileName, DEFAULT_HEIGHT, DEFAULT_HEIGHT, DEFAULT_HEIGHT)
						.addComponent(this.comboBoxFileName, DEFAULT_HEIGHT, DEFAULT_HEIGHT, DEFAULT_HEIGHT)
						.addComponent(this.buttonUpdateFile, DEFAULT_HEIGHT, DEFAULT_HEIGHT, DEFAULT_HEIGHT)
				)
		);
		this.panelFileChooseAndUpdate.setLayout(groupLayout);
	}

	private void initLayoutForPanelProcess() {
		GroupLayout groupLayout = new GroupLayout(this.panelProcessFile);
		groupLayout.setAutoCreateContainerGaps(true);
		groupLayout.setAutoCreateGaps(true);

		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
				.addComponent(this.scrollPane, 250, 250, Short.MAX_VALUE)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.ScrollPanelChineseValue, 250, 250, Short.MAX_VALUE)
						.addComponent(this.ScrollPanelEnglishValue, 250, 250, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(this.scrollPane, 250, 250, Short.MAX_VALUE)
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(this.ScrollPanelChineseValue, 100, 100, Short.MAX_VALUE)
						.addComponent(this.ScrollPanelEnglishValue, 100, 100, Short.MAX_VALUE))
		);
		this.panelProcessFile.setLayout(groupLayout);
	}

	private void initResource() {
		this.setTitle(ResourceToolProperties.getString("String_DialogMainTitle"));
		this.labelKeyModifyType.setText(ResourceToolProperties.getString("String_ProcessModifyType"));
		this.labelFileName.setText(ResourceToolProperties.getString("String_ChooseFile"));
		this.buttonUpdateFile.setText(ResourceToolProperties.getString("String_UpdateFile"));
		this.ScrollPanelChineseValue.setBorder(BorderFactory.createTitledBorder(ResourceToolProperties.getString("String_Chinese")));
		this.ScrollPanelEnglishValue.setBorder(BorderFactory.createTitledBorder(ResourceToolProperties.getString("String_English")));
		this.buttonCommit.setText(ResourceToolProperties.getString("String_CommitLocal"));
	}

	private void registerEvents() {
		this.buttonUpdateFile.addActionListener(this.listenerUpdate);
		this.buttonCommit.addActionListener(this.listenerCommitLocal);
		this.buttonCommit.registerKeyboardAction(this.listenerCommitLocal, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
		this.textAreaEnglishValue.addKeyListener(this.keyListener);
		this.textAreaEnglishValue.getDocument().addUndoableEditListener(undoManager);
		registerComboBoxListener();
		this.tableKey.getSelectionModel().addListSelectionListener(this.listSelectionListener);
		this.dialogUpdateFiles.addWindowListener(this.windowAdapter);
	}

	private void removeEvents() {
		this.buttonUpdateFile.removeActionListener(this.listenerUpdate);
		this.buttonCommit.removeActionListener(this.listenerCommitLocal);
		this.textAreaEnglishValue.removeKeyListener(this.keyListener);
		this.textAreaEnglishValue.getDocument().removeUndoableEditListener(undoManager);
		removeComboBoxListener();
		this.tableKey.getSelectionModel().removeListSelectionListener(this.listSelectionListener);
		this.dialogUpdateFiles.removeWindowListener(this.windowAdapter);
	}

	private void removeComboBoxListener() {
		this.comboBoxModifyType.removeItemListener(this.modifyTypeListener);
		this.comboBoxFileName.removeItemListener(this.fileListener);
	}

	private void registerComboBoxListener() {
		this.comboBoxModifyType.addItemListener(this.modifyTypeListener);
		this.comboBoxFileName.addItemListener(this.fileListener);
	}

	private ActionListener listenerUpdate = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				DialogMain.this.dialogUpdateFiles.showDialog();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};


	private KeyListener keyListener=new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode()==KeyEvent.VK_KP_UP ||e.getKeyCode()==KeyEvent.VK_UP){
				if (DialogMain.this.tableKey.getSelectedRowCount()==1 && DialogMain.this.tableKey.getSelectedRow()!=0){
					int originSelectedRowIndex= DialogMain.this.tableKey.getSelectedRow();
					DialogMain.this.tableKey.setRowSelectionInterval(originSelectedRowIndex-1,originSelectedRowIndex-1);
				}
			}else if (e.getKeyCode()==KeyEvent.VK_KP_DOWN ||e.getKeyCode()==KeyEvent.VK_DOWN){
				if (DialogMain.this.tableKey.getSelectedRowCount()==1 && DialogMain.this.tableKey.getSelectedRow()!= DialogMain.this.tableKey.getRowCount()-1){
					int originSelectedRowIndex= DialogMain.this.tableKey.getSelectedRow();
					DialogMain.this.tableKey.setRowSelectionInterval(originSelectedRowIndex+1,originSelectedRowIndex+1);
				}
			}
			if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z) {
				if (undoManager.canUndo()) {
					undoManager.undo();
				}
			}
			if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Y) {
				if (undoManager.canRedo()) {
					undoManager.redo();
				}
			}
		}
	};

	private ActionListener listenerCommitLocal = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			commitLocal();
		}
	};

	private ItemListener modifyTypeListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (!DialogMain.this.comboBoxModifyType.getSelectedItem().equals(DialogMain.this.selectedModifyType)) {
				DialogMain.this.selectedModifyType = (String) DialogMain.this.comboBoxModifyType.getSelectedItem();
				DialogMain.this.comboBoxFileName.removeItemListener(DialogMain.this.fileListener);
				changeTextValue();
				changeComboBoxFile();
				clearTextArea();
				DialogMain.this.comboBoxFileName.addItemListener(DialogMain.this.fileListener);
				DialogMain.this.modelKeyValue.setParameter(DialogMain.this.selectedModifyType, DialogMain.this.selectedFileName);
			}
		}
	};

	private ItemListener fileListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (!DialogMain.this.comboBoxFileName.getSelectedItem().equals(DialogMain.this.selectedFileName)) {
				DialogMain.this.selectedFileName = (String) DialogMain.this.comboBoxFileName.getSelectedItem();
				changeTextValue();
				clearTextArea();
				DialogMain.this.modelKeyValue.setParameter(DialogMain.this.selectedModifyType, DialogMain.this.selectedFileName);
			}
		}
	};

	private ListSelectionListener listSelectionListener = new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			changeTextValue();
		}
	};

	private WindowAdapter windowAdapter=new WindowAdapter() {
		@Override
		public void windowClosed(WindowEvent e) {
			if (PropertiesUtilites.isUpdate()) {
				removeComboBoxListener();
				initTable();
				registerComboBoxListener();
			}
		}
	};

	private void initTable() {
		clearTextArea();
		this.comboBoxModifyType.removeAllItems();
		for (String str : PropertiesUtilites.modifyTypeAndFiles.keySet()) {
			this.comboBoxModifyType.addItem(str);
		}
		this.comboBoxModifyType.setSelectedIndex(0);
		this.selectedModifyType = (String) this.comboBoxModifyType.getSelectedItem();
		changeComboBoxFile();
		this.modelKeyValue.initParameter();
		this.modelKeyValue.setParameter(this.selectedModifyType, this.selectedFileName);
	}

	private void changeComboBoxFile() {
		this.comboBoxFileName.removeAllItems();
		for (int i = 0; i < PropertiesUtilites.modifyTypeAndFiles.get(this.selectedModifyType).size(); i++) {
			this.comboBoxFileName.addItem(PropertiesUtilites.modifyTypeAndFiles.get(this.selectedModifyType).get(i));
		}
		if(this.comboBoxFileName.getItemCount()>0) {
			this.comboBoxFileName.setSelectedIndex(0);
		}
		this.selectedFileName = (String) this.comboBoxFileName.getSelectedItem();
	}

	private void changeTextValue() {
		if (this.tableKey.getSelectedRow() != -1) {
			this.tableKey.scrollRectToVisible(tableKey.getCellRect(this.tableKey.getSelectedRow(), 0, true));
			this.textAreaChineseValue.setText(this.modelKeyValue.getChineseValue(this.tableKey.getSelectedRow(), this.textAreaEnglishValue.getText()));
			this.textAreaEnglishValue.setText(this.modelKeyValue.getEnglishValue(this.tableKey.getSelectedRow()));
		}
	}

	private void clearTextArea(){
		this.textAreaChineseValue.setText("");
		this.textAreaEnglishValue.setText("");
	}

	private void commitLocal(){
		changeTextValue();
		if (PropertiesUtilites.commitLocal()) {
			removeComboBoxListener();
			initTable();
			registerComboBoxListener();
			JOptionPane.showMessageDialog(this, ResourceToolProperties.getString("String_CommitLocalSuccess"));
		} else {
			JOptionPane.showMessageDialog(this, ResourceToolProperties.getString("String_CommitLocalFailed"));
		}
	}
}
