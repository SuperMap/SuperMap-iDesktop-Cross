package com.supermap.desktop.ui.controls;

import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.desktop.Application;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.button.SmButton;
import com.supermap.desktop.utilities.CursorUtilities;
import com.supermap.desktop.utilities.WorkspaceUtilities;

import javax.swing.GroupLayout.Alignment;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 打开数据库型工作空间
 *
 * @author huchenpu
 */
public class JDialogWorkspaceOpenSQL extends SmDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * 创建一个打开的配置窗口
	 *
	 * @param DataBase
	 *            数据库类型(SQL/Oracle)
	 */
	public JDialogWorkspaceOpenSQL(JFrame parent, boolean modal, String DataBase) {
		super(parent, modal);
		this.DataBase = DataBase;
		if ("SQL".equals(DataBase)) {
			this.setTitle(ControlsProperties.getString("String_Title_OpenSQLWorkspace"));
		} else if ("Oracle".equals(DataBase)) {
			this.setTitle(ControlsProperties.getString("String_Title_OpenOracleWorkspace"));
		}
		initComponents();
		setLocationRelativeTo(null);
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	/**
	 * 组件初始化
	 */
	private void initComponents() {

		jButtonClose = new SmButton();
		jButtonOpen = new SmButton();
		jComboBoxServer = new javax.swing.JComboBox();
		jTextFieldDatabase = new javax.swing.JTextField();
		jTextFieldUser = new javax.swing.JTextField();
		jPasswordFieldPassword = new javax.swing.JPasswordField();
		jComboBoxWorkspaceName = new javax.swing.JComboBox();
		jLabelServer = new javax.swing.JLabel();
		jLabelDatabase = new javax.swing.JLabel();
		jLabelUser = new javax.swing.JLabel();
		jLabelPassword = new javax.swing.JLabel();
		jLabelName = new javax.swing.JLabel();
		jLabelEmptyName = new JLabel();
		jLabelEmptyServer = new JLabel();
		jLabelEmptyUser = new JLabel();
		jLabelEmptyLabel = new JLabel();
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		getRootPane().setDefaultButton(this.jButtonOpen);
		jButtonClose.setText(CoreProperties.getString("String_Cancel"));
		jButtonClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jButtonCloseActionPerformed();
			}
		});
		jButtonOpen.setText(CoreProperties.getString("String_Open"));
		jButtonOpen.setPreferredSize(new java.awt.Dimension(75, 23));

		// TODO Oracle或Sql Server实例列表初始化
		jComboBoxServer.setModel(new javax.swing.DefaultComboBoxModel(new String[] {}));
		jComboBoxServer.setEditable(true);

		jComboBoxServer.getEditor().getEditorComponent().addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				jComboBoxServerItemChange();
			}

			@Override
			public void focusGained(FocusEvent e) {
				jComboBoxServerItemChange();
			}
		});

		jComboBoxServer.getEditor().getEditorComponent().addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				jComboBoxServerItemChange();
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					jButtonOpenActionPerformed();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				jComboBoxServerItemChange();

			}

			@Override
			public void keyPressed(KeyEvent e) {
				jComboBoxServerItemChange();
			}
		});
		jComboBoxServer.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				jComboBoxServerItemChange();
			}
		});

		jTextFieldDatabase.setText("");
		jTextFieldDatabase.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					jButtonOpenActionPerformed();
				}
			}
		});
		jTextFieldUser.setText("");
		jTextFieldUser.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				jTextFieldUserValueChange();
			}

			@Override
			public void focusGained(FocusEvent e) {
				jTextFieldUserValueChange();
			}
		});
		jTextFieldUser.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					jButtonOpenActionPerformed();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				jTextFieldUserValueChange();

			}

			@Override
			public void keyPressed(KeyEvent e) {
				// do nothing
			}
		});

		jPasswordFieldPassword.setText("");
		jPasswordFieldPassword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					jButtonOpenActionPerformed();
				}
			}
		});
		jComboBoxWorkspaceName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "" }));
		jComboBoxWorkspaceName.setEditable(true);
		jComboBoxWorkspaceName.getComponent(0).addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				jComboBoxWorkspaceNamemouseClicked();
			}
		});

		jComboBoxWorkspaceName.getEditor().getEditorComponent().addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				jComboBoxWorkspaceNameItemChange();
			}

			@Override
			public void focusGained(FocusEvent e) {
				jComboBoxWorkspaceNameItemChange();
			}
		});

		jComboBoxWorkspaceName.getEditor().getEditorComponent().addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					jButtonOpenActionPerformed();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				jComboBoxWorkspaceNameItemChange();
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});

		jComboBoxWorkspaceName.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				jComboBoxWorkspaceNameItemChange();
			}
		});
		if ("Oracle".equals(DataBase)) {
			jLabelServer.setText(ControlsProperties.getString("String_Label_InstanceName"));
		} else if ("SQL".equals(DataBase)) {
			jLabelServer.setText(ControlsProperties.getString("String_Label_ServersName"));
		}

		jLabelDatabase.setText(ControlsProperties.getString("String_Label_DatabaseName"));
		jLabelUser.setText(ControlsProperties.getString("String_Label_UserName"));
		jLabelPassword.setText(ControlsProperties.getString("String_Label_UserPassword"));
		jLabelName.setText(ControlsProperties.getString("String_Label_WorkspaceName"));

		jLabelEmptyServer.setText("!");
		jLabelEmptyServer.setVisible(false);
		jLabelEmptyUser.setText("!");
		jLabelEmptyUser.setVisible(false);
		jLabelEmptyName.setText("!");
		jLabelEmptyName.setVisible(false);
		if ("Oracle".equals(DataBase)) {
			jLabelEmptyServer.setToolTipText(ControlsProperties.getString("String_ToolTipText_InstanceShouldNotEmpty"));
		} else if ("SQL".equals(DataBase)) {
			jLabelEmptyServer.setToolTipText(ControlsProperties.getString("String_ToolTipText_ServersNameShouldNotEmpty"));
		}

		jLabelEmptyUser.setToolTipText(ControlsProperties.getString("String_ToolTipText_UserNameShouldNotEmpty"));
		jLabelEmptyName.setToolTipText(ControlsProperties.getString("String_ToolTipText_WorkSpaceNameShouldNotEmpty"));
		jButtonOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jButtonOpenActionPerformed();
			}
		});
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(
												javax.swing.GroupLayout.Alignment.TRAILING,
												layout.createSequentialGroup()
														.addGap(0, 0, Short.MAX_VALUE)
														.addComponent(jButtonOpen, javax.swing.GroupLayout.PREFERRED_SIZE, 75,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(jButtonClose, javax.swing.GroupLayout.PREFERRED_SIZE, 75,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGroup(
												layout.createSequentialGroup()
														.addGroup(
																layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(jLabelServer).addComponent(jLabelDatabase).addComponent(jLabelUser)
																		.addComponent(jLabelPassword).addComponent(jLabelName))
														.addGap(48, 48, 48)
														.addGroup(
																layout.createParallelGroup(Alignment.LEADING).addComponent(jLabelEmptyServer)
																		.addComponent(jLabelEmptyLabel, 10, 10, 10).addComponent(jLabelEmptyUser)
																		.addComponent(jLabelEmptyName))
														.addGroup(
																layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(jComboBoxServer).addComponent(jTextFieldDatabase)
																		.addComponent(jTextFieldUser).addComponent(jPasswordFieldPassword)
																		.addComponent(jComboBoxWorkspaceName)))).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jComboBoxServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabelEmptyServer).addComponent(jLabelServer))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jTextFieldDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabelDatabase))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jTextFieldUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabelEmptyUser).addComponent(jLabelUser))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jPasswordFieldPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabelEmptyLabel).addComponent(jLabelPassword))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jComboBoxWorkspaceName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabelEmptyName).addComponent(jLabelName))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jButtonClose)
										.addComponent(jButtonOpen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap()));

		jButtonClose.getAccessibleContext().setAccessibleName("");
		jButtonOpen.getAccessibleContext().setAccessibleName("");

		pack();

		this.setSize(393, 228);
	}

	// </editor-fold>

	/**
	 * 工作空间下拉列表按钮点击事件 <li>标记出不能为空的项目 <li>查询数据库得出工作空间列表
	 */
	private void jComboBoxWorkspaceNamemouseClicked() {
		/**
		 * flag 标记是否有未填写项目
		 */
		boolean flag = false;
		jComboBoxWorkspaceName.removeAllItems();
		String jComboBoxServerTemp = (String) this.jComboBoxServer.getSelectedItem();
		if (null == jComboBoxServerTemp || jComboBoxServerTemp.length() <= 0) {
			flag = true;
		}
		String jTextFieldUserTemp = this.jTextFieldUser.getText();
		if (null == jTextFieldUserTemp || jTextFieldUserTemp.length() <= 0) {
			flag = true;
		}
        if (flag) {
            return;
		}
	}

	/**
	 * 工作空间下拉列表编辑框改变监听事件，根据结果是否为空提示信息。
	 */
	private void jComboBoxWorkspaceNameItemChange() {
		String jComboBoxWorkspaceNameTemp = ((JTextField) this.jComboBoxWorkspaceName.getEditor().getEditorComponent()).getText();
		if (null != jComboBoxWorkspaceNameTemp && jComboBoxWorkspaceNameTemp.length() > 0) {
			jLabelEmptyName.setVisible(false);
		}
	}

	/**
	 * open按钮点击事件 <li>标记出不能为空的项目 <li>打开工作空间
	 */
	private void jButtonOpenActionPerformed() {
		int flag = 0;
		String jComboBoxServerTemp = (String) this.jComboBoxServer.getSelectedItem();
		if (null == jComboBoxServerTemp || jComboBoxServerTemp.length() <= 0) {
			jLabelEmptyServer.setVisible(true);
			flag = 1;
		}
		String jTextFieldDatabaseTemp = this.jTextFieldDatabase.getText();
		String jTextFieldUserTemp = this.jTextFieldUser.getText();
		if (null == jTextFieldUserTemp || jTextFieldUserTemp.length() <= 0) {
			jLabelEmptyUser.setVisible(true);
			flag = 1;
		}
		String jPasswordFieldPasswordTemp = String.valueOf(this.jPasswordFieldPassword.getPassword());
		String jComboBoxWorkspaceNameTemp = (String) this.jComboBoxWorkspaceName.getSelectedItem();
		if (null == jComboBoxWorkspaceNameTemp || jComboBoxWorkspaceNameTemp.length() <= 0) {
			jLabelEmptyName.setVisible(true);
			flag = 1;
		}
		if (1 == flag) {
			return;
		}

		WorkspaceConnectionInfo Info = new WorkspaceConnectionInfo();
		if ("Oracle".equals(DataBase)) {
			Info.setType(WorkspaceType.ORACLE);
		} else if ("SQL".equals(DataBase)) {
			Info.setDriver("SQL SERVER");
			Info.setType(WorkspaceType.SQL);
		}
		Info.setServer(jComboBoxServerTemp);
		Info.setDatabase(jTextFieldDatabaseTemp);
		Info.setUser(jTextFieldUserTemp);
		Info.setPassword(jPasswordFieldPasswordTemp);
		Info.setName(jComboBoxWorkspaceNameTemp);
		try {
			CursorUtilities.setWaitCursor();
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			Workspace workspaceTemp = new Workspace();
			boolean openResult = workspaceTemp.open(Info);
			if (openResult && WorkspaceUtilities.closeWorkspace()) {
				Application.getActiveApplication().getWorkspace().open(Info);
				Application.getActiveApplication().getOutput().output(ControlsProperties.getString("String_OpenWorkspaceSuccessful"));
				DialogExit();
			}
			if (!openResult) {
				Application.getActiveApplication().getOutput().output(ControlsProperties.getString("String_OpenWorkspaceFailed"));
			}
			// 打开失败但没关闭当前工作空间啥也不干
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		} finally {
			CursorUtilities.setDefaultCursor();
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	/**
	 * 实例编辑框改变时判断是否显示提示信息
	 */
	private void jComboBoxServerItemChange() {
		String jComboBoxServerTemp = ((JTextField) this.jComboBoxServer.getEditor().getEditorComponent()).getText();
		if (null != jComboBoxServerTemp && jComboBoxServerTemp.length() > 0) {
			jLabelEmptyServer.setVisible(false);
		}
	}

	/**
	 * 用户名输入框丢失焦点时判断是否为空
	 */
	private void jTextFieldUserValueChange() {
		String jTextFieldUserTemp = this.jTextFieldUser.getText();
		if (null != jTextFieldUserTemp && jTextFieldUserTemp.length() > 0) {
			jLabelEmptyUser.setVisible(false);
		}
	}

	/**
	 * 关闭按钮点击事件
	 */
	private void jButtonCloseActionPerformed() {
		DialogExit();
	}

	/**
	 * 关闭窗口
	 */
	private void DialogExit() {
		this.dispose();
	}

	// Variables declaration - do not modify
	private String DataBase;

	private SmButton jButtonClose;
	private SmButton jButtonOpen;
	private javax.swing.JComboBox jComboBoxServer;
	private javax.swing.JComboBox jComboBoxWorkspaceName;
	private javax.swing.JLabel jLabelDatabase;
	private javax.swing.JLabel jLabelName;
	private javax.swing.JLabel jLabelPassword;
	protected javax.swing.JLabel jLabelServer;
	private javax.swing.JLabel jLabelUser;
	private javax.swing.JTextField jTextFieldDatabase;
	private javax.swing.JPasswordField jPasswordFieldPassword;
	private javax.swing.JTextField jTextFieldUser;
	private JLabel jLabelEmptyName;
	private JLabel jLabelEmptyServer;
	private JLabel jLabelEmptyUser;
	private JLabel jLabelEmptyLabel;
	// End of variables declaration

}
