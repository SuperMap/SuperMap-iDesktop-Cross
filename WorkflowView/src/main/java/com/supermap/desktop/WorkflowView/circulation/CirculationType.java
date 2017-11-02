package com.supermap.desktop.WorkflowView.circulation;

import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.parameter.interfaces.datas.types.BasicTypes;
import com.supermap.desktop.process.parameter.interfaces.datas.types.CommonTypes;
import com.supermap.desktop.process.parameter.interfaces.datas.types.Type;

/**
 * Created by xie on 2017/10/25.
 */
public enum CirculationType {
	forType("ForType"),
	forFieldType("ForFieldType"),
	forObjectType("ForObjectType"),
	forDatasetType("ForDatasetType"),
	forDatasourceType("ForDatasourceType");

	private String description;

	private CirculationType(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}

	public String getName() {
		String descriptionStr = "String_";
		return ProcessProperties.getString(descriptionStr + description);
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
				result = CommonTypes.DATASET;
				break;
			case forObjectType:
				result = BasicTypes.STRING;
				break;
			case forDatasourceType:
				result = CommonTypes.DATASOURCE;
				break;
			default:
				break;
		}
		return result;
	}
}
