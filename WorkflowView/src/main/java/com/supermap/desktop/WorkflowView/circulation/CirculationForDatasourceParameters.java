package com.supermap.desktop.WorkflowView.circulation;

import com.supermap.data.Datasources;
import com.supermap.desktop.Application;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.core.AbstractCirculationParameters;
import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.process.parameter.ipls.ParameterDatasourceConstrained;
import com.supermap.desktop.process.parameter.ipls.ParameterTextField;
import com.supermap.desktop.utilities.DatasourceUtilities;
import com.supermap.desktop.utilities.StringUtilities;

/**
 * Created by xie on 2017/11/9.
 */
public class CirculationForDatasourceParameters extends AbstractCirculationParameters {
	private ParameterDatasourceConstrained datasource;
	private ParameterTextField wildcard;

	public CirculationForDatasourceParameters(OutputData outputData) {
		this.outputData = outputData;
		initParameters();
	}

	private void initParameters() {
		this.datasource = new ParameterDatasourceConstrained();
		this.wildcard = new ParameterTextField(ProcessProperties.getString("String_Wildcard"));
		this.datasource.setReadOnlyNeeded(false);
		if (null != DatasourceUtilities.getDefaultDatasource()) {
			this.datasource.setSelectedItem(DatasourceUtilities.getDefaultDatasource());
		}
		addParameters(this.datasource, this.wildcard);
	}

	@Override
	public void reset() {
		this.count = 0;
		//首先添加选中的数据源
		String wildcardStr = this.wildcard.getSelectedItem();
		if (StringUtilities.isNullOrEmpty(wildcardStr) && null != this.datasource.getSelectedItem()) {
			infoList.add(this.datasource.getSelectedItem());
		} else if (null != this.datasource.getSelectedItem() && !StringUtilities.isNullOrEmpty(wildcardStr)
				&& isMatching(this.datasource.getSelectedItem().getAlias(), wildcardStr)) {
			infoList.add(this.datasource.getSelectedItem());
		}
		//添加其他的数据源
		if (null != Application.getActiveApplication().getWorkspace().getDatasources() &&
				Application.getActiveApplication().getWorkspace().getDatasources().getCount() > 0) {
			Datasources tempDatasources = Application.getActiveApplication().getWorkspace().getDatasources();
			for (int i = 0; i < tempDatasources.getCount(); i++) {
				if (null != this.datasource.getSelectedItem()
						&& tempDatasources.get(i) != this.datasource.getSelectedItem()
						&& !tempDatasources.get(i).isReadOnly()) {
					if (StringUtilities.isNullOrEmpty(wildcardStr)) {
						infoList.add(tempDatasources.get(i));
					} else if (isMatching(tempDatasources.get(i).getAlias(), wildcardStr)) {
						infoList.add(tempDatasources.get(i));
					}
				}
			}
		}
		if (infoList.size() > 0) {
			outputData.setValue(infoList.get(count));
		}

	}

}
