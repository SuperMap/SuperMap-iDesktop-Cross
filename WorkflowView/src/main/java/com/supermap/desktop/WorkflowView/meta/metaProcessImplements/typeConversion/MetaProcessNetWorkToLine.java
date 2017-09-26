package com.supermap.desktop.WorkflowView.meta.metaProcessImplements.typeConversion;

import com.supermap.data.DatasetType;
import com.supermap.data.GeoLine;
import com.supermap.data.Recordset;
import com.supermap.desktop.WorkflowView.ProcessOutputResultProperties;
import com.supermap.desktop.WorkflowView.meta.MetaKeys;
import com.supermap.desktop.geometry.Abstract.IGeometry;
import com.supermap.desktop.geometry.Abstract.ILineFeature;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.parameter.interfaces.IParameters;

import java.util.Map;

/**
 * Created by yuanR on 2017/8/8  .
 * 网络数据集转换为线数据集
 */
public class MetaProcessNetWorkToLine extends MetaProcessPointLineRegion {

	public MetaProcessNetWorkToLine() {
		super(DatasetType.NETWORK, DatasetType.LINE);
		setTitle(ProcessProperties.getString("String_Title_NetworkToLine"));
	}

	@Override
	protected void initHook() {
		OUTPUT_DATA = "NetWorkToLineResult";
	}

	@Override
	protected String getOutputName() {
		return "result_networkToLine";
	}

	@Override
	protected String getOutputResultName() {
		return ProcessOutputResultProperties.getString("String_NetworkToLineResult");
	}

	@Override
	protected boolean convert(Recordset recordset, IGeometry geometry, Map<String, Object> value) {
		if (geometry instanceof ILineFeature) {
			GeoLine geoLine = null;
			try {
				geoLine = (GeoLine) geometry.getGeometry();
				for (int i = 0; i < geoLine.getPartCount(); i++) {
					if (!recordset.addNew(geoLine, value)) {
						return false;
					}
				}
				return true;
			} catch (UnsupportedOperationException e) {
				// 此时返回false-yuanR2017.9.19
				return false;
			} finally {
				if (geoLine != null) {
					geoLine.dispose();
				}
			}
		} else {
			return false;
		}
	}

	@Override
	public IParameters getParameters() {
		return parameters;
	}

	@Override
	public String getKey() {
		return MetaKeys.CONVERSION_NETWORK_TO_LINE;
	}
}