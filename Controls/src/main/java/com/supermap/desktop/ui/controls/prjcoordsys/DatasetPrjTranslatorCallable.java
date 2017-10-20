package com.supermap.desktop.ui.controls.prjcoordsys;

import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.core.Time;
import com.supermap.desktop.core.TimeType;
import com.supermap.desktop.progress.Interface.UpdateProgressCallable;

import java.text.MessageFormat;
import java.util.concurrent.CancellationException;

/**
 * Created by yuanR on 2017/10/19 0019.
 * 数据集投影转换进度条类
 */
public class DatasetPrjTranslatorCallable extends UpdateProgressCallable {
	private Boolean isSaveAsResult;
	private Dataset getSourceDataset;
	private PrjCoordSys getTargetPrj;
	private CoordSysTransMethod method;
	private CoordSysTransParameter parameter;
	private Datasource getSelectedResultDatasource;
	private String getResultDatasetName;

	private PercentListener percentListener;


	/**
	 * @param isSaveAsResult
	 * @param getSourceDataset
	 * @param getTargetPrj
	 * @param method
	 * @param parameter
	 * @param getSelectedResultDatasource
	 * @param getResultDatasetName
	 */
	public DatasetPrjTranslatorCallable(Boolean isSaveAsResult,
	                                    Dataset getSourceDataset,
	                                    PrjCoordSys getTargetPrj,
	                                    CoordSysTransMethod method,
	                                    CoordSysTransParameter parameter,
	                                    Datasource getSelectedResultDatasource,
	                                    String getResultDatasetName) {

		this.isSaveAsResult = isSaveAsResult;
		this.getSourceDataset = getSourceDataset;
		this.getTargetPrj = getTargetPrj;
		this.method = method;
		this.parameter = parameter;
		this.getSelectedResultDatasource = getSelectedResultDatasource;
		this.getResultDatasetName = getResultDatasetName;
		this.method = method;
		this.parameter = parameter;
	}

	@Override
	public Boolean call() throws Exception {
		Boolean result;
		try {
			Application.getActiveApplication().getOutput().output(MessageFormat.format(ControlsProperties.getString("String_BeginTrans_Dataset"), getSourceDataset.getName()));
			this.percentListener = new PercentListener(this.getSourceDataset.getName());
			this.getSourceDataset.addSteppedListener(this.percentListener);
			if (!this.isSaveAsResult) {
				result = CoordSysTranslator.convert(this.getSourceDataset, this.getTargetPrj, this.parameter, this.method);
				if (result) {
					Application.getActiveApplication().getOutput().output(MessageFormat.format(ControlsProperties.getString("String_CoordSysTrans_VectorSuccess"),
							this.getSourceDataset.getDatasource().getAlias(), this.getSourceDataset.getName()));
				} else {
					Application.getActiveApplication().getOutput().output(MessageFormat.format(ControlsProperties.getString("String_CoordSysTrans_Failed"),
							this.getSourceDataset.getDatasource().getAlias(), this.getSourceDataset.getName()));
				}
			} else {
				Dataset targetDataset = CoordSysTranslator.convert(this.getSourceDataset, this.getTargetPrj, this.getSelectedResultDatasource, this.getResultDatasetName, this.parameter, this.method);
				result = targetDataset != null;
				if (result) {
					Application
							.getActiveApplication()
							.getOutput()
							.output(MessageFormat.format(ControlsProperties.getString("String_CoordSysTrans_RasterSuccess"),
									this.getSourceDataset.getDatasource().getAlias(), this.getSourceDataset.getName(), this.getResultDatasetName, this.getSelectedResultDatasource.getAlias()));
				} else {
					Application
							.getActiveApplication()
							.getOutput()
							.output(MessageFormat.format(ControlsProperties.getString("String_CoordSysTrans_Failed"),
									this.getSourceDataset.getDatasource().getAlias(), this.getSourceDataset.getName()));
				}
			}
		} catch (Exception e) {
			result = false;
			Application.getActiveApplication().getOutput().output(e);
		} finally {
			this.getSourceDataset.addSteppedListener(this.percentListener);
			if (this.parameter != null) {
				this.parameter.dispose();
			}
		}
		return result;
	}


	/**
	 *
	 */
	class PercentListener implements SteppedListener {
		private boolean isCancel = false;

		private String datasetName;

		PercentListener(String datasetName) {
			this.datasetName = datasetName;
		}

		public boolean isCancel() {
			return this.isCancel;
		}

		@Override
		public void stepped(SteppedEvent arg0) {
			try {
				updateProgress(arg0.getPercent(), Time.toString(arg0.getRemainTime(), TimeType.SECOND), MessageFormat.format(ControlsProperties.getString("String_BeginTrans_Dataset"), datasetName));
			} catch (CancellationException e) {
				arg0.setCancel(true);
				this.isCancel = true;
			}
		}
	}

}

