package com.supermap.desktop.ui.controls.prjcoordsys;

import com.supermap.data.*;
import com.supermap.desktop.Application;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.progress.Interface.UpdateProgressCallable;
import com.supermap.desktop.ui.controls.prjcoordsys.prjTransformPanels.TableModelBatchPrjTranslatorDatasetsList;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;

/**
 * Created by yuanR on 2017/10/11 0011.
 * 批量投影转换 进度条
 */
public class BatchPrjTranslatorCallable extends UpdateProgressCallable {
	private ArrayList<TableModelBatchPrjTranslatorDatasetsList.TableData> doDataList;
	private CoordSysTransMethod method;
	private CoordSysTransParameter parameter;

	/**
	 * 构造方法
	 */
	public BatchPrjTranslatorCallable(ArrayList<TableModelBatchPrjTranslatorDatasetsList.TableData> dataList, CoordSysTransMethod method, CoordSysTransParameter parameter) {
		this.doDataList = dataList;
		this.method = method;
		this.parameter = parameter;
	}

	@Override
	public Boolean call() throws Exception {
		boolean result = true;
		try {
			for (int i = 0; i < doDataList.size(); i++) {
				if (doDataList.get(i).getDataset().getPrjCoordSys().getType() == PrjCoordSysType.PCS_NON_EARTH) {
					String message = MessageFormat.format(ControlsProperties.getString("String_DatasetPcsNonEarth"), doDataList.get(i).getDataset().getDatasource().getAlias(),
							doDataList.get(i).getDataset().getName());
					Application.getActiveApplication().getOutput().output(message);
					continue;
				}

				DatasetSteppedListener steppedListener = new DatasetSteppedListener(i);

				doDataList.get(i).getDataset().addSteppedListener(steppedListener);

				Dataset dataset = doDataList.get(i).getDataset();
				PrjCoordSys prjCoordSys = doDataList.get(i).getTargetDatasource().getPrjCoordSys();
				Datasource targetDatasource = doDataList.get(i).getTargetDatasource();
				String resultDatasetName = targetDatasource.getDatasets().getAvailableDatasetName(doDataList.get(i).getResultDatasetName());

				Dataset targetDataset = CoordSysTranslator.convert(dataset, prjCoordSys, targetDatasource, resultDatasetName, parameter, method);
				result = targetDataset != null;
				if (result) {
					Application
							.getActiveApplication()
							.getOutput()
							.output(MessageFormat.format(ControlsProperties.getString("String_CoordSysTrans_RasterSuccess"),
									doDataList.get(i).getDataset().getName(), doDataList.get(i).getDataset().getDatasource().getAlias(), doDataList.get(i).getResultDatasetName(), doDataList.get(i).getTargetDatasource().getAlias()));
				} else {
					Application
							.getActiveApplication()
							.getOutput()
							.output(MessageFormat.format(ControlsProperties.getString("String_CoordSysTrans_Failed"),
									doDataList.get(i).getDataset().getName(), doDataList.get(i).getDataset().getDatasource().getAlias()));
				}
			}
		} catch (Exception e) {
			result = false;
			Application.getActiveApplication().getOutput().output(e);
		} finally {
			if (this.parameter != null) {
				this.parameter.dispose();
			}
		}
		return result;
	}

	private class DatasetSteppedListener implements SteppedListener {

		private int i;

		public DatasetSteppedListener(int i) {
			this.i = i;
		}

		@Override
		public void stepped(SteppedEvent arg0) {
			try {
				int totalPercent = (int) ((100 * this.i + arg0.getPercent()) / doDataList.size());
				updateProgressTotal(arg0.getPercent(),
						MessageFormat.format(ControlsProperties.getString("string_CurrentTransformingDataset"), i + 1, doDataList.size()),
						totalPercent,
						MessageFormat.format(ControlsProperties.getString("String_BeginTrans_Dataset"), doDataList.get(i).getDataset().getName()));
			} catch (CancellationException e) {
				arg0.setCancel(true);
				doDataList.get(i).getDataset().removeSteppedListener(this);
			} finally {
				if (100 == arg0.getPercent()) {
					doDataList.get(i).getDataset().removeSteppedListener(this);
				}
			}
		}
	}
}
