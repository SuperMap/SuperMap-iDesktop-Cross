package com.supermap.desktop.process.core;

import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.types.BasicTypes;
import com.supermap.desktop.process.types.CommonTypes;
import com.supermap.desktop.process.types.DatasetTypes;
import com.supermap.desktop.process.types.Type;

/**
 * Created by xie on 2017/10/25.
 */
public enum CirculationType {
	forType("ForType"),
	forFieldType("ForFieldType"),
	forObjectType("ForObjectType"),
	forDatasetType("ForDatasetType"),
	forDatasourceType("ForDatasourceType"),
	forFileType("ForFileType"),
	whileType("whileType");

	private String description;

	CirculationType(String description) {
		this.description = description;
	}

	public static CirculationType getCirculationType(String description) {
		if (description.equals(forType.toString())) {
			return forType;
		}
		if (description.equals(forFieldType.toString())) {
			return forFieldType;
		}
		if (description.equals(forObjectType.toString())) {
			return forObjectType;
		}
		if (description.equals(forDatasetType.toString())) {
			return forDatasetType;
		}
		if (description.equals(forDatasourceType.toString())) {
			return forDatasourceType;
		}
		if (description.equals(forFileType.toString())) {
			return forFileType;
		}
		if (description.equals(whileType.toString())) {
			return whileType;
		}
		return null;
	}

	@Override
	public String toString() {
		return description;
	}

	public String getName() {
		String result = null;
		switch (this) {
			case forType:
				result = ProcessProperties.getString("String_ForType");
				break;
			case forFieldType:
				result = ProcessProperties.getString("String_ForFieldType");
				break;
			case forDatasetType:
				result = ProcessProperties.getString("String_ForDatasetType");
				break;
			case forObjectType:
				result = ProcessProperties.getString("String_ForObjectType");
				break;
			case forDatasourceType:
				result = ProcessProperties.getString("String_ForDatasourceType");
				break;
			case forFileType:
				result = ProcessProperties.getString("String_ForFileType");
				break;
			case whileType:
				result = ProcessProperties.getString("String_WhileType");
				break;
			default:
				break;
		}
		return result;
	}

	//先支持一种类型
	public Type getType() {
		Type result = null;
		switch (this) {
			case forType:
				result = BasicTypes.NUMBER;
				break;
			case forFieldType:
				result = BasicTypes.STRING;
				break;
			case forDatasetType:
				result = DatasetTypes.DATASET;
				break;
			case forObjectType:
				result = BasicTypes.STRING;
				break;
			case forDatasourceType:
				result = CommonTypes.DATASOURCE;
				break;
			case forFileType:
				result = BasicTypes.STRING;
				break;
			case whileType:
				result = BasicTypes.BASICTYPES;
				break;
			default:
				break;
		}
		return result;
	}
}
