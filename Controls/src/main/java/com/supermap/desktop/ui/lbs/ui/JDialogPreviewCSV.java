package com.supermap.desktop.ui.lbs.ui;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.supermap.data.Enum;
import com.supermap.data.FieldType;
import com.supermap.data.GeoCoordSysType;
import com.supermap.data.GeometryType;
import com.supermap.desktop.Application;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.lbs.HDFSDefine;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.CellRenders.ListDataCellRender;
import com.supermap.desktop.ui.controls.DataCell;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.ui.controls.ProviderLabel.WarningOrHelpProvider;
import com.supermap.desktop.ui.controls.SmDialog;
import com.supermap.desktop.ui.controls.SortTable.SmSortTable;
import com.supermap.desktop.ui.controls.button.SmButton;
import com.supermap.desktop.utilities.BracketMatchUtilties;
import com.supermap.desktop.utilities.FieldTypeUtilities;
import com.supermap.desktop.utilities.GeometryTypeUtilities;
import com.supermap.desktop.utilities.StringUtilities;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * @author XiaJT
 */
public class JDialogPreviewCSV extends SmDialog {
	private final HDFSTableModel hdfsModel;
	private String webURL;
	private HDFSDefine hdfsDefine;

	private SmSortTable tableField = new SmSortTable();
	private CSVFiledTableModel tableModelField;

	private SmSortTable tablePreviewCSV = new SmSortTable() {
		@Override
		public String getToolTipText(MouseEvent event) {
			Point point = event.getPoint();
			int row = tablePreviewCSV.rowAtPoint(point);
			int column = tablePreviewCSV.columnAtPoint(point);
			if (row != -1 && column != -1) {
				return (String) tablePreviewCSV.getValueAt(row, column);
			}
			return super.getToolTipText(event);
		}
	};
	private PreviewCSVTableModel tableModelPreviewCSV;

	private JLabel labelGeometryType = new JLabel();
	private JComboBox<GeometryType> comboBoxGeometryType = new JComboBox<>();

	private JLabel labelStorageType = new JLabel();
	private JComboBox<String> comboBoxStorageType = new JComboBox<>();

	private JCheckBox checkBoxHasHeader = new JCheckBox();

	private JLabel labelEncoding = new JLabel(CoreProperties.getString("String_Label_EncodeType"));
	private WarningOrHelpProvider warningOrHelpProvider = new WarningOrHelpProvider(ControlsProperties.getString("String_PreviewCsvOnly"), WarningOrHelpProvider.HELP);
	private JComboBox<String> comboBoxEncoding = new JComboBox<>();

	private JLabel labelCoordSysType = new JLabel(ControlsProperties.getString("String_Message_CoordSysName"));
	private JComboBox<GeoCoordSysType> comboBoxCoorSysType = new JComboBox<>();

	private SmButton buttonOk = new SmButton(ControlsProperties.getString("String_Save"));
	private SmButton buttonCancle = new SmButton(CoreProperties.getString(CoreProperties.Cancel));

	private static final int BUFF_LENGTH = 1024 * 8;

	private static final int DEFAULT_CSV_LENGTH = 10;

	private String metaFile = null;

	private boolean isMetaExists = false;
	private String defaultSeparator = ",";
	private byte[] csvFileBytes;

	public JDialogPreviewCSV(String webURL, HDFSDefine hdfsDefine, HDFSTableModel model) {
		hdfsModel = model;
		this.webURL = webURL;
		if (!webURL.endsWith("/")) {
			this.webURL += "/";
		}
		this.hdfsDefine = hdfsDefine;
		this.setTitle(hdfsDefine.getName() + "");
		init();
		this.setSize(800, 600);
		setLocationRelativeTo(null);
	}

	private void init() {
		initComponents();
		initLayout();
		initListeners();
		initComponentState();
		initResources();
	}

	private void initResources() {
		labelGeometryType.setText(ControlsProperties.getString("String_LabelGeometryType"));
		labelStorageType.setText(ControlsProperties.getString("String_Label_StorageType"));
		checkBoxHasHeader.setText(ControlsProperties.getString("String_isHasHead"));
	}

	private void initComponents() {
		comboBoxStorageType.setRenderer(new ListDataCellRender());
		comboBoxGeometryType.setRenderer(new ListCellRenderer<GeometryType>() {
			@Override
			public Component getListCellRendererComponent(JList<? extends GeometryType> list, GeometryType value, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel jLabel = new JLabel(GeometryTypeUtilities.toString(value));
				jLabel.setOpaque(true);
				if (isSelected) {
					jLabel.setBackground(list.getSelectionBackground());
					jLabel.setForeground(list.getSelectionForeground());
				} else {
					jLabel.setBackground(list.getBackground());
				}
				return jLabel;
			}
		});
		Enum[] enums = GeometryType.getEnums(GeometryType.class);
		for (Enum anEnum : enums) {
			comboBoxGeometryType.addItem((GeometryType) anEnum);
		}

		String[] storageTypes = {
				"WKT", "XYColumn"
		};
		for (String storageType : storageTypes) {
			comboBoxStorageType.addItem(storageType);
		}
		String[] encodings = {
				"UTF-8", "GB2312", "Big5", "GBK", "GB18030", "UTF-16", "UTF-32"
		};
		for (String encoding : encodings) {
			comboBoxEncoding.addItem(encoding);
		}

		Enum[] types = GeoCoordSysType.getEnums(GeoCoordSysType.class);
		for (Enum type : types) {
			comboBoxCoorSysType.addItem((GeoCoordSysType) type);
		}
		comboBoxCoorSysType.setRenderer(new ListCellRenderer<GeoCoordSysType>() {
			@Override
			public Component getListCellRendererComponent(JList<? extends GeoCoordSysType> list, GeoCoordSysType value, int index, boolean isSelected, boolean cellHasFocus) {
				DataCell dataCell = new DataCell(value.name());
				if (isSelected) {
					dataCell.setBackground(list.getSelectionBackground());
					dataCell.setForeground(list.getSelectionForeground());
				} else {
					dataCell.setBackground(list.getBackground());
					dataCell.setForeground(list.getForeground());
				}
				return dataCell;
			}
		});
	}

	private void initLayout() {
		this.setLayout(new GridBagLayout());
		Dimension preferredSize = new Dimension(200, 23);
		comboBoxGeometryType.setMinimumSize(preferredSize);
		comboBoxGeometryType.setPreferredSize(preferredSize);
		comboBoxGeometryType.setMaximumSize(preferredSize);
		comboBoxStorageType.setMinimumSize(preferredSize);
		comboBoxStorageType.setPreferredSize(preferredSize);
		comboBoxStorageType.setMaximumSize(preferredSize);

		comboBoxEncoding.setRenderer(new ListDataCellRender());
		comboBoxEncoding.setMinimumSize(preferredSize);
		comboBoxEncoding.setPreferredSize(preferredSize);
		comboBoxEncoding.setMaximumSize(preferredSize);

		comboBoxCoorSysType.setMinimumSize(preferredSize);
		comboBoxCoorSysType.setPreferredSize(preferredSize);
		comboBoxCoorSysType.setMaximumSize(preferredSize);
		this.add(labelGeometryType, new GridBagConstraintsHelper(0, 0, 1, 1).setWeight(0, 0).setAnchor(GridBagConstraints.WEST).setInsets(10, 10, 0, 0));
		this.add(comboBoxGeometryType, new GridBagConstraintsHelper(1, 0, 1, 1).setWeight(1, 0).setAnchor(GridBagConstraints.WEST).setInsets(10, 26, 0, 10).setFill(GridBagConstraints.NONE));
		this.add(labelStorageType, new GridBagConstraintsHelper(2, 0, 1, 1).setWeight(0, 0).setAnchor(GridBagConstraints.WEST).setInsets(10, 5, 0, 0));
		this.add(comboBoxStorageType, new GridBagConstraintsHelper(3, 0, 1, 1).setWeight(1, 0).setAnchor(GridBagConstraints.WEST).setInsets(10, 5, 0, 10).setFill(GridBagConstraints.NONE));

		this.add(labelEncoding, new GridBagConstraintsHelper(0, 1, 1, 1).setWeight(0, 0).setAnchor(GridBagConstraints.WEST).setInsets(5, 10, 0, 0));
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.add(warningOrHelpProvider, new GridBagConstraintsHelper(0, 0, 1, 1).setWeight(0, 0).setFill(GridBagConstraints.NONE));
		panel.add(comboBoxEncoding, new GridBagConstraintsHelper(1, 0, 1, 1).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL).setInsets(0, 5, 0, 0));
		this.add(panel, new GridBagConstraintsHelper(1, 1, 1, 1).setWeight(1, 0).setAnchor(GridBagConstraints.WEST).setInsets(5, 5, 0, 5).setFill(GridBagConstraints.NONE));
		this.add(labelCoordSysType, new GridBagConstraintsHelper(2, 1, 1, 1).setWeight(0, 0).setAnchor(GridBagConstraints.WEST).setInsets(5, 5, 0, 0));
		this.add(comboBoxCoorSysType, new GridBagConstraintsHelper(3, 1, 1, 1).setWeight(1, 0).setAnchor(GridBagConstraints.WEST).setInsets(5, 5, 0, 10).setFill(GridBagConstraints.NONE));

		this.add(checkBoxHasHeader, new GridBagConstraintsHelper(0, 2, 4, 1).setWeight(1, 0).setAnchor(GridBagConstraints.WEST).setInsets(5, 10, 0, 10));

		this.add(new JScrollPane(tableField), new GridBagConstraintsHelper(0, 3, 4, 1).setWeight(1, 0).setAnchor(GridBagConstraints.CENTER).setInsets(5, 10, 0, 0).setFill(GridBagConstraints.BOTH).setIpad(0, 200));

		this.add(new JScrollPane(tablePreviewCSV), new GridBagConstraintsHelper(0, 4, 4, 1).setWeight(1, 1).setAnchor(GridBagConstraints.CENTER).setInsets(5, 10, 0, 0).setFill(GridBagConstraints.BOTH));

		JPanel panelButton = new JPanel();
		panelButton.setLayout(new GridBagLayout());
		panelButton.add(buttonOk, new GridBagConstraintsHelper(0, 0, 1, 1).setAnchor(GridBagConstraints.EAST).setWeight(1, 0));
		panelButton.add(buttonCancle, new GridBagConstraintsHelper(1, 0, 1, 1).setAnchor(GridBagConstraints.EAST).setWeight(0, 0).setInsets(0, 5, 0, 0));

		this.add(panelButton, new GridBagConstraintsHelper(0, 5, 4, 1).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL).setInsets(5, 10, 10, 10));

	}

	private void initListeners() {
		buttonOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (updateMetaFile()) {
					dialogResult = DialogResult.OK;
					dispose();
				}
			}
		});

		buttonCancle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialogResult = DialogResult.CANCEL;
				dispose();
			}
		});
		comboBoxEncoding.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					updatePreviewData();
				}
			}
		});
		comboBoxGeometryType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (comboBoxGeometryType.getSelectedItem() == GeometryType.GEOPOINT) {
						comboBoxStorageType.setEnabled(true);
					} else {
						comboBoxStorageType.setEnabled(false);
						comboBoxStorageType.setSelectedIndex(0);
					}
				}
			}
		});
	}

	private boolean updateMetaFile() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("GeometryType", ((GeometryType) comboBoxGeometryType.getSelectedItem()).name().substring(3));
		jsonObject.put("StorageType", comboBoxStorageType.getSelectedItem());
		jsonObject.put("HasHeader", checkBoxHasHeader.isSelected());
		jsonObject.put("PrjCoordsys", ((GeoCoordSysType) comboBoxCoorSysType.getSelectedItem()).value());
		JSONArray array = new JSONArray();
		for (int i = 0; i < tableModelField.getRowCount(); i++) {
			JSONObject jsonObjectField = new JSONObject();
			jsonObjectField.put("name", tableModelField.getValueAt(i, 0));
			jsonObjectField.put("type", FieldTypeUtilities.getFieldType((String) tableModelField.getValueAt(i, 1)).name());
			array.add(jsonObjectField);
		}
		jsonObject.put("FieldInfos", array);
		String metaFile = jsonObject.toJSONString() + "\r\n";

		if (!deleteMetaFile() && isMetaExists) {
			return false;
		}

		boolean isCreateSuccess = createMetaFile();
		if (isCreateSuccess) {
			if (!postMetaFile(metaFile)) {
				return false;
			}
		}
		return true;
	}

	private boolean deleteMetaFile() {
		String url = getMetaUrl() + "?user.name=root&op=DELETE";
		HttpDelete requestPut = new HttpDelete(url);
		requestPut.setHeader("Accept-Encoding", "utf-8");
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			HttpResponse response = new DefaultHttpClient().execute(requestPut);
			if (response == null || response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return true;
			}
		} catch (Exception e) {
			return true;
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				Application.getActiveApplication().getOutput().output(e);
			}
		}
		return false;
	}

	private void updatePreviewData() {
		String csvFile = null;
		try {
			csvFile = new String(this.csvFileBytes, (String) comboBoxEncoding.getSelectedItem());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// TODO: 2017/8/17
		if (csvFile != null) {
			String[] split = csvFile.split("\n");
			for (int i = 0; i < split.length - 1 && i <= DEFAULT_CSV_LENGTH; i++) {
				String line = split[i];
				line = line.split("\r")[0];
				String[] columns = line.split(defaultSeparator);
				BracketMatchUtilties bracketMatchUtilties = new BracketMatchUtilties("");
				int columnIndex = 0;
				for (String column : columns) {
					if (bracketMatchUtilties.isMatch()) {
						bracketMatchUtilties = new BracketMatchUtilties(column);
					} else {
						bracketMatchUtilties.appendString(defaultSeparator + column);
					}
					if (bracketMatchUtilties.isMatch()) {
						tableModelPreviewCSV.setValueAt(bracketMatchUtilties.getMatchString(), i, columnIndex);
						bracketMatchUtilties.clear();
						columnIndex++;
					}
				}
				if (!StringUtilities.isNullOrEmpty(bracketMatchUtilties.getMatchString())) {
					tableModelPreviewCSV.setValueAt(bracketMatchUtilties.getMatchString(), i, columnIndex);
				}
			}
		}
	}

	private boolean postMetaFile(String metaFile) {
		String webFile = getMetaUrl() + "?user.name=root&op=APPEND";
		HttpPost requestPost = new HttpPost(webFile);
		requestPost.setHeader("Accept-Encoding", "utf-8");
		String locationURL = "";
		try {
			HttpResponse response = new DefaultHttpClient().execute(requestPost);
			if (response != null) {
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {
					Header locationHeader = response.getFirstHeader("Location");
					if (locationHeader != null) {
						locationURL = locationHeader.getValue();
					}
				}
			}
			if (!StringUtilities.isNullOrEmptyString(locationURL)) {
				// 利用post请求往服务器上上传内容
				URL nowURL = new URL(locationURL);
				HttpURLConnection connection = (HttpURLConnection) nowURL.openConnection();
				connection.setConnectTimeout(10000);
				// 设置读取数据超时时间为3000ms
				connection.setReadTimeout(10000);
				setHeader(connection);
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				OutputStream outputStream = connection.getOutputStream();
				while (outputStream == null) {
					outputStream = connection.getOutputStream();
				}

				byte[] bytes = metaFile.getBytes("UTF-8");
				// 将文件写入字节流中
				outputStream.write(bytes, 0, bytes.length);
				outputStream.flush();

				if (null != connection.getInputStream()) {
					InputStream inputStream = connection.getInputStream();
					// 写入文件到远程服务器
					inputStream.read(bytes);
				}

			}
		} catch (IOException e) {
			Application.getActiveApplication().getOutput().output(e);
			return false;
		} finally {
			// FIXME: 2017/7/29 资源释放 先保证可用
		}
		return true;
	}

	private void setHeader(URLConnection conn) {
		conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
		conn.setRequestProperty("Accept-Language", "en-us,en;q=0.7,zh-cn;q=0.3");
		conn.setRequestProperty("Accept-Encoding", "utf-8");
		conn.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		conn.setRequestProperty("Keep-Alive", "300");
		conn.setRequestProperty("connnection", "keep-alive");
		conn.setRequestProperty("If-Modified-Since", "Fri, 22 Jan 2016 12:00:00 GMT");
		conn.setRequestProperty("If-None-Match", "\"1261d8-4290-df64d224\"");
		conn.setRequestProperty("Cache-conntrol", "max-age=0");
		conn.setRequestProperty("Content-type", "application/x-java-serialized-object");
		conn.setRequestProperty("Referer", "http://www.baidu.com");
	}

	private boolean createMetaFile() {
		String metaFilePath = getMetaUrl();
		String webFile = metaFilePath + "?user.name=root&op=CREATE";
		HttpPut requestPut = new HttpPut(webFile);
		String locationURL = "";
		try {
			HttpResponse response = new DefaultHttpClient().execute(requestPut);
			if (response != null) {
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {
					Header locationHeader = response.getFirstHeader("Location");
					if (locationHeader != null) {
						// 获取登陆成功之后跳转链接
						locationURL = locationHeader.getValue();
					}
				}
			}
			if (!StringUtilities.isNullOrEmptyString(locationURL)) {
				requestPut = new HttpPut(locationURL);
				response = new DefaultHttpClient().execute(requestPut);
				if (response != null && response.getStatusLine().getStatusCode() == 201) {
					return true;
				}
			}
		} catch (IOException e) {
			Application.getActiveApplication().getOutput().output(e);
			return false;
		}

		return false;
	}

	private void initComponentState() {
		String csvFile = "";
		int startPos = 0;
		try {
			//region 获取Csv文件
			this.csvFileBytes = new byte[0];
			URL url = new URL(this.webURL + hdfsDefine.getName() + "?op=open&offset=" + 0 + "&length=" + hdfsDefine.getSize());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			setConnection(connection);
			//获取文件输入流，读取文件内容
			InputStream inputStream = connection.getInputStream();
			while (inputStream == null) {
				inputStream = connection.getInputStream();
			}
			byte[] buff = new byte[BUFF_LENGTH];
			int size = -1;


			while ((size = inputStream.read(buff)) > 0) {
				// 写入文件内容，返回最后写入的长度
				byte[] temp = new byte[this.csvFileBytes.length + buff.length];
				System.arraycopy(this.csvFileBytes, 0, temp, 0, this.csvFileBytes.length);
				System.arraycopy(buff, 0, temp, this.csvFileBytes.length, buff.length);
				this.csvFileBytes = temp;


				csvFile = new String(this.csvFileBytes, "UTF-8");
				String[] split = csvFile.split("\n");
				if (split.length >= DEFAULT_CSV_LENGTH) {
					break;
				}
				startPos += size;
			}
			inputStream.close();
			connection.disconnect();
			//endregion
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
		try {
			String metaFileName = getMetaFileName();
			HDFSDefine hdfsMetaFile = null;
			for (int i = 0; i < hdfsModel.getRowCount(); i++) {
				if (((HDFSDefine) hdfsModel.getRowTagAt(i)).getName().equalsIgnoreCase(metaFileName)) {
					hdfsMetaFile = (HDFSDefine) hdfsModel.getRowTagAt(i);
				}
			}
			if (hdfsMetaFile != null) {
				byte[] buff = new byte[BUFF_LENGTH];

				URL urlMeta = new URL(getMetaUrl() + "?op=open&offset=" + 0 + "&length=" + hdfsMetaFile.getSize());
				HttpURLConnection connectionMeta = (HttpURLConnection) urlMeta.openConnection();
				setConnection(connectionMeta);

				//获取文件输入流，读取文件内容
				InputStream inputStreamMeta = connectionMeta.getInputStream();
				while (inputStreamMeta == null) {
					inputStreamMeta = connectionMeta.getInputStream();
				}
				int size = -1;

				byte[] csvMetaByte = new byte[0];

				while ((size = inputStreamMeta.read(buff)) > 0) {
					// 写入文件内容，返回最后写入的长度
					byte[] temp = new byte[csvMetaByte.length + buff.length];
					System.arraycopy(csvMetaByte, 0, temp, 0, csvMetaByte.length);
					System.arraycopy(buff, 0, temp, csvMetaByte.length, buff.length);
					csvMetaByte = temp;

					metaFile = new String(csvMetaByte, "UTF-8");
//					String[] split = csvFileString.split("\\}");
//					if (split.length >= 2) {
//						// TODO: 2017/8/18
//
//						metaFile = csvFileString;
//						break;
//					}
					startPos += size;
				}
				inputStreamMeta.close();
				connectionMeta.disconnect();
			}
		} catch (Exception e) {
			// meta找不到就算了
		}


		ArrayList<RowData> csvDatas = new ArrayList<>();

		String[] split = csvFile.split("\n");
		ArrayList<String> modelColumns = new ArrayList<>();
		for (int i = 0; i < split.length - 1 && i <= DEFAULT_CSV_LENGTH; i++) {
			modelColumns.clear();
			String line = split[i];
			line = line.split("\r")[0];
			String[] columns = line.split(defaultSeparator);
			BracketMatchUtilties bracketMatchUtilties = new BracketMatchUtilties("");
			for (String column : columns) {
				if (bracketMatchUtilties.isMatch()) {
					bracketMatchUtilties = new BracketMatchUtilties(column);
				} else {
					bracketMatchUtilties.appendString(defaultSeparator + column);
				}
				if (bracketMatchUtilties.isMatch()) {
					modelColumns.add(bracketMatchUtilties.getMatchString());
					bracketMatchUtilties.clear();
				}
			}
			if (!StringUtilities.isNullOrEmpty(bracketMatchUtilties.getMatchString())) {
				modelColumns.add(bracketMatchUtilties.getMatchString());
			}
			csvDatas.add(new RowData(modelColumns.toArray(new String[modelColumns.size()])));
		}


		tableModelPreviewCSV = new PreviewCSVTableModel(csvDatas);
		tablePreviewCSV.setModel(tableModelPreviewCSV);
		tablePreviewCSV.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		int previewCSVColumnCount = tableModelPreviewCSV.getColumnCount();
		int width = Math.max(750 / (previewCSVColumnCount <= 0 ? 1 : previewCSVColumnCount), 100);
		for (int i = 0; i < previewCSVColumnCount; i++) {
			tablePreviewCSV.getColumnModel().getColumn(i).setPreferredWidth(width);
		}
		ArrayList<RowData> rowDatas = new ArrayList<>();
		if (this.metaFile != null) {
			JSONObject jsonObject = JSONObject.parseObject(metaFile);
			if (jsonObject != null) {
				String geometryType = (String) jsonObject.get("GeometryType");
				String storageType = (String) jsonObject.get("StorageType");
				boolean hasHeader = Boolean.valueOf(String.valueOf(jsonObject.get("HasHeader")));

				if (!geometryType.startsWith("GEO")) {
					geometryType = "GEO" + geometryType;
				}
				try {

					Object prjCoordsys = jsonObject.get("PrjCoordsys");
					if (null != prjCoordsys) {
						Integer integer = Integer.valueOf(String.valueOf(prjCoordsys));
						comboBoxCoorSysType.setSelectedItem(GeoCoordSysType.parse(GeoCoordSysType.class, integer));
					}
				} catch (Exception e) {
					// hehe
				}


				comboBoxGeometryType.setSelectedItem(GeometryType.parse(GeometryType.class, geometryType));
				comboBoxStorageType.setSelectedItem(storageType);
				checkBoxHasHeader.setSelected(hasHeader);

				JSONArray fieldInfos = (JSONArray) jsonObject.get("FieldInfos");
				if (fieldInfos != null) {
					for (int i = 0; i < fieldInfos.size(); i++) {
						Object fieldInfo = fieldInfos.get(i);
						String name = (String) ((JSONObject) fieldInfo).get("name");
						String type = FieldTypeUtilities.getFieldTypeName((FieldType) Enum.parse(FieldType.class, (String) ((JSONObject) fieldInfo).get("type")));
						rowDatas.add(new RowData(name, type));
						tableModelPreviewCSV.setColumnName(i, name);
					}
					for (int i = fieldInfos.size(); i < tableModelPreviewCSV.getMaxColumnCount(); i++) {
						rowDatas.add(new RowData(tableModelPreviewCSV.getColumnName(i), FieldTypeUtilities.getFieldTypeName(FieldType.WTEXT)));
					}
				}
			}
			isMetaExists = true;
		}
		if (!isMetaExists) {
			int columnCount = tableModelPreviewCSV.getMaxColumnCount();
			for (int i = 0; i < columnCount; i++) {
				rowDatas.add(new RowData(tableModelPreviewCSV.getColumnName(i), FieldTypeUtilities.getFieldTypeName(FieldType.WTEXT)));
			}
		}
		tableModelField = new CSVFiledTableModel(rowDatas);

		tableField.setModel(tableModelField);
		tableField.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JTextField()) {
			private int row;

			@Override
			public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
				this.row = row;
				return super.getTableCellEditorComponent(table, value, isSelected, row, column);
			}

			@Override
			public boolean stopCellEditing() {
				String text = ((JTextField) editorComponent).getText();
				int[] columnWidths = new int[tableModelPreviewCSV.getColumnCount()];
				for (int i = 0; i < tablePreviewCSV.getColumnCount(); i++) {
					columnWidths[i] = tablePreviewCSV.getColumnModel().getColumn(i).getWidth();
				}
				tableModelPreviewCSV.setColumnName(row, text);
				for (int i = 0; i < tablePreviewCSV.getColumnCount(); i++) {
					tablePreviewCSV.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
				}
				return super.stopCellEditing();
			}
		});

		ArrayList<String> fieldTypes = new ArrayList<>();
		fieldTypes.add(FieldTypeUtilities.getFieldTypeName(FieldType.INT16));
		fieldTypes.add(FieldTypeUtilities.getFieldTypeName(FieldType.DOUBLE));
		fieldTypes.add(FieldTypeUtilities.getFieldTypeName(FieldType.DATETIME));
		fieldTypes.add(FieldTypeUtilities.getFieldTypeName(FieldType.WTEXT));

		JComboBox<String> comboBox = new JComboBox<>(fieldTypes.toArray(new String[fieldTypes.size()]));
		tableField.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(comboBox));
	}

	private String getMetaFileName() {
		return hdfsDefine.getName().substring(0, hdfsDefine.getName().length() - 4) + ".meta";
	}

	private String getMetaUrl() {
		return webURL + getMetaFileName();
	}

	private void setConnection(HttpURLConnection connection) {
		// 设置连接超时时间为10000ms
		connection.setConnectTimeout(10000);
		// 设置读取数据超时时间为10000ms
		connection.setReadTimeout(10000);
		connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.3) Gecko/2008092510 Ubuntu/8.04 (hardy) Firefox/3.0.3");
		connection.setRequestProperty("Accept-Language", "en-us,en;q=0.7,zh-cn;q=0.3");
		connection.setRequestProperty("Accept-Encoding", "utf-8");
		connection.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		connection.setRequestProperty("Keep-Alive", "300");
		connection.setRequestProperty("connnection", "keep-alive");
		connection.setRequestProperty("If-Modified-Since", "Fri, 02 Jan 2009 17:00:05 GMT");
		connection.setRequestProperty("If-None-Match", "\"1261d8-4290-df64d224\"");
		connection.setRequestProperty("Cache-conntrol", "max-age=0");
		connection.setRequestProperty("Referer", "http://www.baidu.com");
	}

	class PreviewCSVTableModel extends DefaultTableModel {
		ArrayList<String> columnNames = new ArrayList<>();

		private ArrayList<RowData> values = new ArrayList<>();

		PreviewCSVTableModel(ArrayList<RowData> values) {
			this.values = values;
			if (values.size() > 0) {
				int maxColumnCount = getMaxColumnCount();
				for (int i = 0; i < maxColumnCount; i++) {
					String str = values.get(0).getValueAt(i);
					columnNames.add(StringUtilities.isNullOrEmpty(str) || str.length() > 20 ? "column_" + (i + 1) : values.get(0).getValueAt(i));
				}
			}
		}

		@Override
		public int getRowCount() {
			return values == null ? 0 : values.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames == null ? 0 : columnNames.size();
		}

		@Override
		public String getColumnName(int column) {
			return columnNames.get(column);
		}

		@Override
		public void setValueAt(Object aValue, int row, int column) {
			if (row < values.size()) {
				values.get(row).setValueAt(column, (String) aValue);
				fireTableCellUpdated(row, column);
			}
		}

		@Override
		public Object getValueAt(int row, int column) {
			return values.get(row).getValueAt(column);
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}

		public void setColumnName(int columnIndex, String columnName) {
			if (columnIndex >= getColumnCount()) {
				columnNames.add(columnIndex, columnName);
			} else {
				columnNames.set(columnIndex, columnName);
			}
			fireTableStructureChanged();
		}


		public int getMaxColumnCount() {
			int maxCount = -1;
			for (RowData value : values) {
				if (value.getLength() > maxCount) {
					maxCount = value.getLength();
				}
			}
			return maxCount;
		}
	}

	class CSVFiledTableModel extends DefaultTableModel {

		private ArrayList<RowData> values = new ArrayList<>();
		private String[] columnNames = new String[]{
				ControlsProperties.getString("String_FieldInfoName"),
				ControlsProperties.getString("String_FieldInfoType")
		};

		CSVFiledTableModel(ArrayList<RowData> values) {
			this.values = values;
		}

		@Override
		public int getRowCount() {
			return values == null ? 0 : values.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		@Override
		public Object getValueAt(int row, int column) {
			return values.get(row).getValueAt(column);
		}

		@Override
		public void setValueAt(Object aValue, int row, int column) {
			values.get(row).setValueAt(column, (String) aValue);
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return true;
		}
	}

	class RowData {
		private String[] datas;

		public RowData(String... datas) {
			this.datas = datas;
		}

		public int getLength() {
			return datas.length;
		}

		public String getValueAt(int index) {
			if (index >= getLength()) {
				return "";
			}
			String data = datas[index];
			if (data.startsWith("\"")) {
				data = data.substring(1);
			}
			if (data.endsWith("\"")) {
				data = data.substring(0, data.length() - 1);
			}
			return data;
		}

		public void setValueAt(int index, String value) {
			if (index < datas.length) {
				datas[index] = value;
			}
		}
	}
}
