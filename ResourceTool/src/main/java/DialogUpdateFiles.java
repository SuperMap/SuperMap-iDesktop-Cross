package main.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

/**
 * Created by lixiaoyao on 2017/11/9.
 */
public class DialogUpdateFiles extends SmDialog {

	private JLabel labelRootPath;
	private JTextField textFieldRootPath;
	private JLabel labelProcessFolder;
	private JScrollPane scrollPane;
	private JTextArea textAreaProcessFolder;
	private JButton buttonOk;
	private JButton buttonCancel;

	private static final int DEFAULT_HEIGHT = 23;
	private ArrayList<String> resultStr = new ArrayList<>();
	private static final int ROOT_PATH_INDEX = 0;
	private static final int ROOT_FOLDER_INDEX = 1;
	private static final String ROOT_PATH = "RootPath=";
	private static final String PROPERTIES_TYPE = "PropertiesType=";
	private static final String SYSTEM_FILE_PATH = "ResourceTool/src/main/system/system.txt";

	public DialogUpdateFiles()  {
		this.setSize(new Dimension(527, 423));
		this.setLocationRelativeTo(null);
		initComponent();
		initLayout();
		initResource();
		readSystemSetting();
		removeEvents();
		registerEvents();
	}

	private void initComponent() {
		this.labelRootPath = new JLabel();
		this.textFieldRootPath = new JTextField();
		this.labelProcessFolder = new JLabel();
		this.scrollPane = new JScrollPane();
		this.textAreaProcessFolder = new JTextArea();
		this.buttonOk = new JButton();
		this.buttonCancel = new JButton();
		this.scrollPane.setViewportView(this.textAreaProcessFolder);
		this.textAreaProcessFolder.setLineWrap(true);
	}

	private void initLayout() {
		GroupLayout groupLayout = new GroupLayout(this.getContentPane());
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);

		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(this.labelRootPath)
						.addComponent(this.textFieldRootPath, 250, 250, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(this.labelProcessFolder)
						.addComponent(this.scrollPane, 250, 250, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
						.addGap(200, 200, Short.MAX_VALUE)
						.addComponent(this.buttonOk)
						.addComponent(this.buttonCancel))
		);

		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.labelRootPath, DEFAULT_HEIGHT, DEFAULT_HEIGHT, DEFAULT_HEIGHT)
						.addComponent(this.textFieldRootPath, DEFAULT_HEIGHT, DEFAULT_HEIGHT, DEFAULT_HEIGHT)
				)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.labelProcessFolder, DEFAULT_HEIGHT, DEFAULT_HEIGHT, DEFAULT_HEIGHT)
						.addComponent(this.scrollPane, 300, 300, Short.MAX_VALUE)
				)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(this.buttonOk, DEFAULT_HEIGHT, DEFAULT_HEIGHT, DEFAULT_HEIGHT)
						.addComponent(this.buttonCancel, DEFAULT_HEIGHT, DEFAULT_HEIGHT, DEFAULT_HEIGHT)
				)
		);
		this.setLayout(groupLayout);
	}

	private void initResource() {
		this.setTitle(ResourceToolProperties.getString("String_DialogUpdateFiles"));
		this.labelRootPath.setText(ResourceToolProperties.getString("String_ProjectPath"));
		this.labelProcessFolder.setText(ResourceToolProperties.getString("String_FileType"));
		this.buttonOk.setText(ResourceToolProperties.getString("String_Ok"));
		this.buttonCancel.setText(ResourceToolProperties.getString("String_Cancel"));
	}

	private void registerEvents() {
		this.buttonOk.addActionListener(this.listenerOk);
		this.buttonCancel.addActionListener(this.listenerCancel);
	}

	private void removeEvents() {
		this.buttonOk.removeActionListener(this.listenerOk);
		this.buttonCancel.removeActionListener(this.listenerCancel);
	}

	private void readSystemSetting()  {
		try {
			File file = new File(SYSTEM_FILE_PATH);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));//构造一个BufferedReader类来读取文件

			String readStr = "";
			while ((readStr = br.readLine()) != null) {
				readStr = readStr.split("=")[1]; //为什么直接add split之后的是单个字符
				this.resultStr.add(readStr);
				readStr = "";
			}
			this.textFieldRootPath.setText(this.resultStr.get(ROOT_PATH_INDEX));
			this.textAreaProcessFolder.setText(this.resultStr.get(ROOT_FOLDER_INDEX));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readPropertiesFile() {
		try {
			PropertiesUtilites.clearParameter();
			if (!this.textAreaProcessFolder.getText().equals("")) {
				String[] currentFolders = this.textAreaProcessFolder.getText().split(",");
				for (int i = 0; i < currentFolders.length; i++) {
					String temp = currentFolders[i].replace(" ", "");
					PropertiesUtilites.addFileType(temp);
				}
			}
			String rootPath = this.textFieldRootPath.getText().replace("\\", "/");
			readFileDirectory(new File(rootPath));
			ArrayList<String> latestSystemTxt = new ArrayList<>();
			latestSystemTxt.add(ROOT_PATH + this.textFieldRootPath.getText());
			latestSystemTxt.add(PROPERTIES_TYPE + this.textAreaProcessFolder.getText());
			FileUtilites.outPutToFile(latestSystemTxt, SYSTEM_FILE_PATH);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readFileDirectory(File file) {
		File[] files = file.listFiles();
		for (File a : files) {
			if (a.isFile() && !a.isDirectory()) {
				String absolutePath = a.getAbsolutePath();
				String fileType = absolutePath.substring(absolutePath.lastIndexOf(".") + 1);
				fileType = fileType.toLowerCase();
				if (fileType.equals("properties") && a.getAbsolutePath().indexOf("target") == -1) {
					PropertiesUtilites.addAllPropertiesFile(a);
				}
			}
			if (a.isDirectory() && !a.getName().equals("ResourceTool")) {
				readFileDirectory(a);
			}
		}
	}

	private ActionListener listenerOk = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			readPropertiesFile();
			PropertiesUtilites.moveFile();
			DialogUpdateFiles.this.dispose();

		}
	};

	private ActionListener listenerCancel=new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			DialogUpdateFiles.this.dispose();
		}
	};

}
