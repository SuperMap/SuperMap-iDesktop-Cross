package com.supermap.desktop.utilities;

import com.supermap.data.*;
import com.supermap.data.conversion.ImportSteppedEvent;
import com.supermap.desktop.Application;
import com.supermap.desktop.implement.UserDefineType.ImportSettingExcel;
import com.supermap.desktop.implement.UserDefineType.UserDefineImportResult;
import com.supermap.desktop.properties.CommonProperties;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by xie on 2017/8/24.
 */
public class XlsUtilities {
	public static ImportSettingExcel importSettingExcel;

	/**
	 * 将xls数据放入二维集合中
	 *
	 * @param filePath
	 * @return
	 */
	public static ArrayList<Vector<Vector>> getXLSInfo(String filePath) {
		ArrayList<Vector<Vector>> result = new ArrayList<>();
		try {
			File xlsFile = new File(filePath);
			if (!xlsFile.exists()) {
				Application.getActiveApplication().getOutput().output(MessageFormat.format(CommonProperties.getString("String_FileNotExistsError"), filePath));
				return null;
			}
			FileInputStream stream = new FileInputStream(filePath);
			HSSFWorkbook workbook = new HSSFWorkbook(stream);
			HSSFSheet sheet;
			HSSFRow row;
			int rowCount;
			int columnCount = 0;
			Vector<Vector> vectors = new Vector<>();
			Vector vector = new Vector();
			int sheetCount = workbook.getNumberOfSheets();
			for (int i = 0; i < sheetCount; i++) {
				vectors.clear();
				sheet = workbook.getSheetAt(i);
				//获取总行数
				rowCount = sheet.getPhysicalNumberOfRows();
				//
				if (rowCount > 0) {
					//获取总列数
					columnCount = sheet.getRow(0).getPhysicalNumberOfCells();
				}
				for (int j = 0; j < rowCount; j++) {
					vector.clear();
					row = sheet.getRow(j);
					for (int k = 0; k < columnCount; k++) {
						vector.add(row.getCell(k).getStringCellValue());
					}
					vectors.add(vector);
				}
				result.add(vectors);
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
		return result;
	}

	/**
	 * @param datasource      目标数据源
	 * @param filePath        文件目录
	 * @param importFieldName 是否将首行导入为字段名称
	 */
	public static UserDefineImportResult[] importXlsFile(Datasource datasource, String filePath, boolean importFieldName) {
		UserDefineImportResult[] importResults = null;
		try {
			File xlsFile = new File(filePath);
			if (!xlsFile.exists()) {
				Application.getActiveApplication().getOutput().output(MessageFormat.format(CommonProperties.getString("String_FileNotExistsError"), filePath));
				return null;
			}
			String xlsName = xlsFile.getName();
			xlsName = xlsName.substring(0, xlsName.indexOf("."));
			FileInputStream stream = new FileInputStream(filePath);
			HSSFWorkbook workbook = new HSSFWorkbook(stream);
			HSSFSheet sheet;
			HSSFRow row;
			DatasetVector dataset;
			DatasetVectorInfo info;
			FieldInfos fieldInfos;
			FieldInfo fieldInfo = null;
			Recordset recordset;
			int columnCount = 0;
			int rowCount;
			ArrayList<String> fieldNames = new ArrayList<>();
			int sheetSize = workbook.getNumberOfSheets();
			importResults = new UserDefineImportResult[sheetSize];
			for (int i = 0; i < sheetSize; i++) {
				if (null != importSettingExcel) {
					int totalPercent = i * 100 / sheetSize;
					importSettingExcel.fireStepped(new ImportSteppedEvent(importSettingExcel, totalPercent, 0, importSettingExcel, sheetSize, false));
				}
				sheet = workbook.getSheetAt(i);
				//总行数
				rowCount = sheet.getPhysicalNumberOfRows();
				if (rowCount > 0) {
					row = sheet.getRow(0);
					//总列数
					columnCount = row.getPhysicalNumberOfCells();
				}
				info = new DatasetVectorInfo();
				Datasets datasets = datasource.getDatasets();
				info.setName(datasets.getAvailableDatasetName(xlsName + "_" + sheet.getSheetName()));
				info.setType(DatasetType.TABULAR);
				dataset = datasets.create(info);
				fieldInfos = dataset.getFieldInfos();
				if (importFieldName) {
					for (int j = 0; j < columnCount; j++) {
						fieldInfo = new FieldInfo();
						fieldInfo.setType(FieldType.TEXT);
						String name = "NewField" + "_" + sheet.getRow(0).getCell(j).getStringCellValue();
						fieldInfo.setName(name);
						fieldInfos.add(fieldInfo);
						fieldNames.add(name);
					}
				} else {
					for (int j = 0; j < columnCount; j++) {
						fieldInfo = new FieldInfo();
						fieldInfo.setType(FieldType.TEXT);
						String name = j == 0 ? "NewField" : "NewField" + "_" + String.valueOf(j);
						fieldInfo.setName(name);
						fieldInfos.add(fieldInfo);
						fieldNames.add(name);
					}
				}
				recordset = dataset.getRecordset(false, CursorType.DYNAMIC);
				Recordset.BatchEditor editor = recordset.getBatch();
				editor.begin();
				HashMap map = new HashMap();
				int j = 0;
				if (importFieldName) {
					j = 1;
				}
				for (; j < rowCount; j++) {
					row = sheet.getRow(j);
					map.clear();
					for (int k = 0; k < columnCount; k++) {
						map.put(fieldNames.get(k), row.getCell(k).getStringCellValue());
					}
					boolean importResult = recordset.addNew(null, map);
					if (importResult && null != importSettingExcel) {
						int totalPercent = i * 100 / sheetSize;
						importSettingExcel.fireStepped(new ImportSteppedEvent(importSettingExcel, totalPercent, 0, importSettingExcel, sheetSize, false));
					}
				}
				if (rowCount == dataset.getRecordCount() && null != importSettingExcel) {
					importSettingExcel.setTargetDatasetName(dataset.getName());
					importResults[i] = new UserDefineImportResult(importSettingExcel, null);
				} else {
					importResults[i] = new UserDefineImportResult(null, importSettingExcel);
				}
				editor.update();
				recordset.dispose();
				fieldInfo.dispose();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Application.getActiveApplication().getOutput().output(e);
		}
		return importResults;
	}

	/**
	 * 利用apache.poi实现将数据集导出为excel文件
	 *
	 * @param dataset
	 * @param filePath
	 * @param exportFieldName
	 */

	public static void exportXlsFile(DatasetVector dataset, String filePath, boolean exportFieldName) {
		exportXlsFile(dataset, filePath, exportFieldName, null);
	}

	/**
	 * 利用apache.poi实现将数据集导出为excel文件
	 *
	 * @param dataset         数据集
	 * @param filePath        文件目录
	 * @param exportFieldName 是否导出字段名称
	 * @param fieldNames      指定导出的字段集合，为空时全部导出
	 */
	public static void exportXlsFile(DatasetVector dataset, String filePath, boolean exportFieldName, String[]
			fieldNames) {
		try {
			HSSFWorkbook workbook = new HSSFWorkbook();//产生工作簿对象
			HSSFSheet sheet = workbook.createSheet();//产生工作表对象
			HSSFRow row;//行
			HSSFCell cell;//单元格
			DecimalFormat decimalFormat = new DecimalFormat("######0.00000000");
			FieldInfos fieldInfos = dataset.getFieldInfos();
			Recordset recordset = dataset.getRecordset(false, CursorType.STATIC);
			recordset.moveFirst();
			int rowCount = 0;
			if (true == exportFieldName) {
				//导出字段信息
				row = sheet.createRow(rowCount);
				int column = 0;
				for (int i = 0, count = fieldInfos.getCount(); i < count; i++) {
					if (null == fieldNames) {
						cell = row.createCell(i);
						cell.setCellValue(fieldInfos.get(i).getName());
					} else if (null != fieldNames && isSelectedFieldName(fieldInfos.get(i).getName(), fieldNames)) {
						cell = row.createCell(column);
						cell.setCellValue(fieldInfos.get(i).getName());
						column++;
					}
				}
				rowCount++;
			}
			while (!recordset.isEOF()) {
				row = sheet.createRow(rowCount);
				int column = 0;
				for (int i = 0, count = fieldInfos.getCount(); i < count; i++) {
					sheet.setColumnWidth(i, 14 * 256);
					String fieldName = fieldInfos.get(i).getName();
					Object cellValue = null;
					FieldType fieldType = fieldInfos.get(i).getType();
					if (null == fieldNames) {
						cell = row.createCell(i);
						cellValue = recordset.getFieldValue(i);
						setCellValue(sheet, cell, decimalFormat, i, cellValue, fieldType);
					} else if (null != fieldNames && isSelectedFieldName(fieldName, fieldNames)) {
						cell = row.createCell(column);
						cellValue = recordset.getFieldValue(fieldName);
						setCellValue(sheet, cell, decimalFormat, column, cellValue, fieldType);
						column++;
					}

				}
				recordset.moveNext();
				rowCount++;
			}
			FileOutputStream fOut = new FileOutputStream(filePath);
			workbook.write(fOut);
			fOut.flush();
			fOut.close();
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	/**
	 * 将数据转换为格式化的字符串并填充到单元格中
	 *
	 * @param sheet
	 * @param cell
	 * @param decimalFormat
	 * @param column
	 * @param cellValue
	 * @param fieldType
	 */
	private static void setCellValue(HSSFSheet sheet, HSSFCell cell, DecimalFormat decimalFormat, int column, Object
			cellValue, FieldType fieldType) {
		if (null != cellValue) {
			if (fieldType == FieldType.DATETIME) {
				//此处处理为格式化的字符串类型
				sheet.setColumnWidth(column, 20 * 256);
				SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
				cell.setCellValue(format.format((Date) cellValue));
			} else if (fieldType == FieldType.DOUBLE || fieldType == FieldType.SINGLE) {
				//浮点型格式化
				cell.setCellValue(decimalFormat.format(cellValue));
			} else {
				cell.setCellValue(cellValue.toString());
			}
		} else {
			cell.setCellValue("");
		}
	}

	private static boolean isSelectedFieldName(String fieldName, String[] fieldNames) {
		boolean result = false;
		for (String tempName : fieldNames) {
			if (fieldName.equals(tempName)) {
				result = true;
				break;
			}
		}
		return result;
	}


	public static void main(String[] args) {
		DatasourceConnectionInfo connectionInfo = new DatasourceConnectionInfo();
		connectionInfo.setServer("F:\\SampleData711\\Ci ty\\Jingjin.udb");
		Datasource datasource = new Datasource(EngineType.UDB);
		datasource.open(connectionInfo);
		DatasetVector dataset = (DatasetVector) datasource.getDatasets().get("Grids");
//		exportXlsFile(dataset, "test2.xls", true, null);
		importXlsFile(datasource, "test2.xls", false);
	}

	public static void stepped(ImportSteppedEvent importSteppedEvent) {

	}
}