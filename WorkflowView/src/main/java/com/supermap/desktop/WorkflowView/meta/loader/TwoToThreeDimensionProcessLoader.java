package com.supermap.desktop.WorkflowView.meta.loader;

import com.supermap.data.DatasetType;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.WorkflowView.meta.metaProcessImplements.typeConversion.MetaProcess2DTo3D;
import com.supermap.desktop.process.core.IProcess;
import com.supermap.desktop.process.loader.AbstractProcessLoader;
import com.supermap.desktop.utilities.StringUtilities;

import java.util.Map;

/**
 * Created By Chens on 2017/8/12 0012
 */
public class TwoToThreeDimensionProcessLoader extends AbstractProcessLoader {
	private final static String TWO_TO_THREE_DIMENSION = "2DTo3D";

	public TwoToThreeDimensionProcessLoader(Map<String, String> properties, String index) {
		super(properties, index);
	}

	@Override
	public IProcess loadProcess() {
		if (getProperties() == null) {
			return null;
		}

		if (StringUtilities.isNullOrEmpty(getClassName())) {
			return null;
		}

		if (!getKey().contains(TWO_TO_THREE_DIMENSION)) {
			return null;
		}

		DatasetType type = null;
		switch (getKey()) {
			case MetaKeys.CONVERSION_POINT2D_TO_3D:
				type = DatasetType.POINT;
				break;
			case MetaKeys.CONVERSION_LINE2D_TO_3D:
				type = DatasetType.LINE;
				break;
			case MetaKeys.CONVERSION_REGION2D_TO_3D:
				type = DatasetType.REGION;
				break;
		}

		IProcess process = new MetaProcess2DTo3D(type);

		if (process != null && process.getTitle() != null && !process.getTitle().trim().isEmpty()) {
			process.setTitle(getTitle());
		}
		return process;
	}
}
