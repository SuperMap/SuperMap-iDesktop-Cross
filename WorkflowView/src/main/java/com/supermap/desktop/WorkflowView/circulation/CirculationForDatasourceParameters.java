package com.supermap.desktop.WorkflowView.circulation;

import com.supermap.data.Datasources;
import com.supermap.desktop.Application;
import com.supermap.desktop.process.core.AbstractCirculationParameters;
import com.supermap.desktop.process.parameter.interfaces.datas.OutputData;
import com.supermap.desktop.process.parameter.ipls.ParameterDatasourceConstrained;

/**
 * Created by xie on 2017/11/9.
 */
public class CirculationForDatasourceParameters extends AbstractCirculationParameters {
	private ParameterDatasourceConstrained datasource;

	public CirculationForDatasourceParameters(OutputData outputData) {
		this.outputData = outputData;
		initParameters();
	}

	private void initParameters() {
		this.datasource = new ParameterDatasourceConstrained();
		this.datasource.setReadOnlyNeeded(false);
		if (null != Application.getActiveApplication().getActiveDatasources() &&
				Application.getActiveApplication().getActiveDatasources().length > 0) {
			this.datasource.setSelectedItem(Application.getActiveApplication().getActiveDatasources()[0]);
		}
		addParameters(this.datasource);
	}

	@Override
	public void reset() {
		this.count = 0;
		if (null != this.datasource.getSelectedItem()) {
			infoList.add(this.datasource.getSelectedItem());
		}
		if (null != Application.getActiveApplication().getWorkspace().getDatasources() &&
				Application.getActiveApplication().getWorkspace().getDatasources().getCount() > 0) {
			Datasources tempDatasources = Application.getActiveApplication().getWorkspace().getDatasources();
			for (int i = 0; i < tempDatasources.getCount(); i++) {
				if (null != this.datasource.getSelectedItem()
						&& tempDatasources.get(i) != this.datasource.getSelectedItem()
						&& !tempDatasources.get(i).isReadOnly()) {
					infoList.add(tempDatasources.get(i));
				}
			}
		}
		if (infoList.size() > 0) {
			outputData.setValue(infoList.get(count));
		}

	}

}
