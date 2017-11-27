import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/22
 * Time: 9:46
 * Description:
 */
public class DialogMain extends JDialog {

	private JPanel panelProcessFile;
	private JScrollPane scrollPane;
	private JTable tableKey;
	private JScrollPane ScrollPanelChineseValue;
	private JTextArea textAreaChineseValue;
	private JScrollPane ScrollPanelEnglishValue;
	private JTextArea textAreaEnglishValue;
	private JButton buttonCommit;
	private JScrollPane ScrollPanelRecommendValue;
	private JTextArea textAreaRecommendValue;

	private static final int DEFAULT_HEIGHT = 23;
	private static final Font FONT = new Font("Serif", 0, 20);
	private ModelKeyValue modelKeyValue;
	private static UndoManager undoManager = new UndoManager();

	public DialogMain() {
		this.setSize(new Dimension(1400, 900));
		this.setLocationRelativeTo(null);
		initComponents();
		initLayout();
		initResource();
		TranslateManager.run();
		if (SystemFileUtilities.getUnTranslateKeyCount() != 0) {
			JOptionPane.showMessageDialog(this, ConfigToolProperties.getString("String_MessageUnTranslatedKey"));
		} else {
			if (TranslateManager.isNeedAutoTranslated()) {
				JOptionPane.showMessageDialog(this, ConfigToolProperties.getString("String_MessageTranslatedAllKey"));
			} else {
				JOptionPane.showMessageDialog(this, ConfigToolProperties.getString("String_MessageNotTranslatedAllKey"));
				this.buttonCommit.setEnabled(false);
			}
		}
		initTable();
		removeEvents();
		registerEvents();
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	}

	private void initComponents() {
		this.panelProcessFile = new JPanel();
		this.scrollPane = new JScrollPane();
		this.tableKey = new JTable();
		this.ScrollPanelChineseValue = new JScrollPane();
		this.textAreaChineseValue = new JTextArea();
		this.ScrollPanelEnglishValue = new JScrollPane();
		this.textAreaEnglishValue = new JTextArea();
		this.ScrollPanelRecommendValue = new JScrollPane();
		this.textAreaRecommendValue = new JTextArea();
		this.buttonCommit = new JButton();
		this.scrollPane.setViewportView(this.tableKey);
		this.ScrollPanelChineseValue.setViewportView(this.textAreaChineseValue);
		this.ScrollPanelEnglishValue.setViewportView(this.textAreaEnglishValue);
		this.ScrollPanelRecommendValue.setViewportView(this.textAreaRecommendValue);
		this.textAreaChineseValue.setFont(FONT);
		this.textAreaEnglishValue.setFont(FONT);
		this.textAreaRecommendValue.setFont(FONT);
		this.textAreaChineseValue.setEditable(false);
		this.textAreaChineseValue.setLineWrap(true);
		this.textAreaRecommendValue.setEditable(false);
		this.textAreaRecommendValue.setLineWrap(true);
		this.textAreaEnglishValue.setLineWrap(true);
		this.modelKeyValue = new ModelKeyValue();
		this.tableKey.setModel(this.modelKeyValue);
		this.tableKey.getColumnModel().getColumn(this.modelKeyValue.COLUMN_INDEX).setPreferredWidth(30);
		this.tableKey.getColumnModel().getColumn(this.modelKeyValue.COLUMN_KEY).setMinWidth(500);
		this.tableKey.setRowHeight(23);
		this.tableKey.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	private void initLayout() {
		initLayoutForPanelProcess();

		GroupLayout groupLayout = new GroupLayout(this.getContentPane());
		groupLayout.setAutoCreateContainerGaps(true);
		groupLayout.setAutoCreateGaps(true);

		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.panelProcessFile)
						.addGroup(groupLayout.createSequentialGroup()
								.addGap(200, 200, Short.MAX_VALUE)
								.addComponent(this.buttonCommit))
				));
		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addComponent(this.panelProcessFile, 250, 250, Short.MAX_VALUE)
				.addComponent(this.buttonCommit, DEFAULT_HEIGHT, DEFAULT_HEIGHT, DEFAULT_HEIGHT)
		);
		this.setLayout(groupLayout);
	}

	private void initLayoutForPanelProcess() {
		GroupLayout groupLayout = new GroupLayout(this.panelProcessFile);
		groupLayout.setAutoCreateContainerGaps(true);
		groupLayout.setAutoCreateGaps(true);

		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
				.addComponent(this.scrollPane, 250, 250, Short.MAX_VALUE)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.ScrollPanelRecommendValue, 250, 250, Short.MAX_VALUE)
						.addComponent(this.ScrollPanelChineseValue, 250, 250, Short.MAX_VALUE)
						.addComponent(this.ScrollPanelEnglishValue, 250, 250, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(this.scrollPane, 250, 250, Short.MAX_VALUE)
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(this.ScrollPanelRecommendValue, 100, 100, Short.MAX_VALUE)
						.addComponent(this.ScrollPanelChineseValue, 100, 100, Short.MAX_VALUE)
						.addComponent(this.ScrollPanelEnglishValue, 100, 100, Short.MAX_VALUE))
		);
		this.panelProcessFile.setLayout(groupLayout);
	}

	private void initResource() {
		this.setTitle(ConfigToolProperties.getString("String_DialogMainTitle"));
		this.ScrollPanelRecommendValue.setBorder(BorderFactory.createTitledBorder(ConfigToolProperties.getString("String_RecommendValue")));
		this.ScrollPanelChineseValue.setBorder(BorderFactory.createTitledBorder(ConfigToolProperties.getString("String_Chinese")));
		this.ScrollPanelEnglishValue.setBorder(BorderFactory.createTitledBorder(ConfigToolProperties.getString("String_English")));
		this.buttonCommit.setText(ConfigToolProperties.getString("String_AutoTranslate"));
	}

	private void registerEvents() {
		this.buttonCommit.addActionListener(this.listenerCommitLocal);
		this.buttonCommit.registerKeyboardAction(this.listenerCommitLocal, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
		this.textAreaEnglishValue.addKeyListener(this.keyListener);
		this.textAreaEnglishValue.getDocument().addUndoableEditListener(undoManager);
		this.tableKey.getSelectionModel().addListSelectionListener(this.listSelectionListener);
	}

	private void removeEvents() {
		this.buttonCommit.removeActionListener(this.listenerCommitLocal);
		this.textAreaEnglishValue.removeKeyListener(this.keyListener);
		this.textAreaEnglishValue.getDocument().removeUndoableEditListener(undoManager);
		this.tableKey.getSelectionModel().removeListSelectionListener(this.listSelectionListener);
	}

	private KeyListener keyListener = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_KP_UP || e.getKeyCode() == KeyEvent.VK_UP) {
				if (DialogMain.this.tableKey.getSelectedRowCount() == 1 && DialogMain.this.tableKey.getSelectedRow() != 0) {
					int originSelectedRowIndex = DialogMain.this.tableKey.getSelectedRow();
					DialogMain.this.tableKey.setRowSelectionInterval(originSelectedRowIndex - 1, originSelectedRowIndex - 1);
				}
			} else if (e.getKeyCode() == KeyEvent.VK_KP_DOWN || e.getKeyCode() == KeyEvent.VK_DOWN) {
				if (DialogMain.this.tableKey.getSelectedRowCount() == 1 && DialogMain.this.tableKey.getSelectedRow() != DialogMain.this.tableKey.getRowCount() - 1) {
					int originSelectedRowIndex = DialogMain.this.tableKey.getSelectedRow();
					DialogMain.this.tableKey.setRowSelectionInterval(originSelectedRowIndex + 1, originSelectedRowIndex + 1);
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

	private ListSelectionListener listSelectionListener = new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			changeTextValue();
		}
	};

	private void initTable() {
		clearTextArea();
		this.modelKeyValue.resetTableData();
	}

	private void changeTextValue() {
		if (this.tableKey.getSelectedRow() != -1) {
			this.tableKey.scrollRectToVisible(tableKey.getCellRect(this.tableKey.getSelectedRow(), 0, true));
			this.textAreaRecommendValue.setText(this.modelKeyValue.getRecommendValue(this.tableKey.getSelectedRow()));
			this.textAreaChineseValue.setText(this.modelKeyValue.getChineseValue(this.tableKey.getSelectedRow(), this.textAreaEnglishValue.getText()));
			this.textAreaEnglishValue.setText(this.modelKeyValue.getEnglishValue(this.tableKey.getSelectedRow()));
		}
	}

	private void clearTextArea() {
		this.textAreaChineseValue.setText("");
		this.textAreaEnglishValue.setText("");
		this.textAreaRecommendValue.setText("");
	}

	public void commitLocal() {
		changeTextValue();
		if (TranslateManager.autoTranslate()) {
			JOptionPane.showMessageDialog(this, ConfigToolProperties.getString("String_AutoTranslateSuccess"));
		} else {
			JOptionPane.showMessageDialog(this, ConfigToolProperties.getString("String_AutoTranslateFailed"));
		}
		initTable();
		if (this.tableKey.getRowCount() == 0) {
			this.buttonCommit.setEnabled(false);
		}
	}

}
