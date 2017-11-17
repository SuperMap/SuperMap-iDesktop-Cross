package com.supermap.desktop.process.virtual;

import com.supermap.data.FieldType;

/**
 * Created by highsad on 2017/11/10.
 */
public class VirtualField {
	private String name;
	private String caption;
	private FieldType fieldType;

	public VirtualField(String name, FieldType fieldType) {
		this(name, name, fieldType);
	}

	public VirtualField(String name, String caption, FieldType fieldType) {
		this.name = name;
		this.caption = caption;
		this.fieldType = fieldType;
	}

	public String getName() {
		return name;
	}

	public String getCaption() {
		return caption;
	}

	public FieldType getFieldType() {
		return fieldType;
	}
}
