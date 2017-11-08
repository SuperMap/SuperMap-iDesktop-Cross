package com.supermap.desktop.spatialanalyst.vectoranalyst.overlayAnalyst;

import com.supermap.analyst.spatialanalyst.OverlayAnalyst;
import com.supermap.analyst.spatialanalyst.OverlayAnalystParameter;
import com.supermap.data.DatasetVector;
import com.supermap.data.SteppedEvent;
import com.supermap.data.SteppedListener;
import com.supermap.desktop.Application;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.core.Time;
import com.supermap.desktop.core.TimeType;
import com.supermap.desktop.progress.Interface.UpdateProgressCallable;
import com.supermap.desktop.spatialanalyst.SpatialAnalystProperties;
import com.supermap.desktop.ui.UICommonToolkit;
import com.supermap.desktop.ui.WorkspaceComponentManager;
import com.supermap.desktop.utilities.OverlayAnalystType;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CancellationException;

/**
 * Created by xie on 2016/9/1.
 */
public class OverlayAnalystCallable extends UpdateProgressCallable implements IOverlayAnalyst {
	private OverlayAnalystType type;
	private OverlayAnalystParameter parameter;
	private DatasetVector sourceDataset;
	private DatasetVector overlayAnalystDataset;
	private DatasetVector targetDataset;
	private PercentListener percentListener;

	public OverlayAnalystCallable() {
		super();
	}

	@Override
	public Boolean call() throws Exception {
		boolean result = true;

		try {
			boolean overlayAnalystResult = false;
			this.percentListener = new PercentListener();
			OverlayAnalyst.addSteppedListener(this.percentListener);
			long startLong = System.currentTimeMillis();
			Application.getActiveApplication().getOutput().output(MessageFormat.format(SpatialAnalystProperties.getString("String_OverlayAnalyst_StartTime"), new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(new Date(startLong))));
			switch (type) {
				case CLIP:
					overlayAnalystResult = OverlayAnalyst.clip(sourceDataset, overlayAnalystDataset, targetDataset, parameter);
					break;
				case UNION:
					overlayAnalystResult = OverlayAnalyst.union(sourceDataset, overlayAnalystDataset, targetDataset, parameter);
					break;
				case ERASE:
					overlayAnalystResult = OverlayAnalyst.erase(sourceDataset, overlayAnalystDataset, targetDataset, parameter);
					break;
				case IDENTITY:
					overlayAnalystResult = OverlayAnalyst.identity(sourceDataset, overlayAnalystDataset, targetDataset, parameter);
					break;
				case INTERSECT:
					overlayAnalystResult = OverlayAnalyst.intersect(sourceDataset, overlayAnalystDataset, targetDataset, parameter);
					break;
				case UPDATE:
					overlayAnalystResult = OverlayAnalyst.update(sourceDataset, overlayAnalystDataset, targetDataset, parameter);
					break;
				case XOR:
					overlayAnalystResult = OverlayAnalyst.xOR(sourceDataset, overlayAnalystDataset, targetDataset, parameter);
					break;

			}
			long endLong = System.currentTimeMillis();
			Application.getActiveApplication().getOutput().output(MessageFormat.format(SpatialAnalystProperties.getString("String_OverlayAnalyst_EndTime"), new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(new Date(endLong))));
			if (overlayAnalystResult) {
				WorkspaceComponentManager componentManager = UICommonToolkit.getWorkspaceManager();
				componentManager.setSelectedDataProperty();
				Application.getActiveApplication().getOutput().output(MessageFormat.format(SpatialAnalystProperties.getString("String_OverlayAnalyst_SuccessTip"), type.toString()));
			} else {
				Application.getActiveApplication().getOutput().output(MessageFormat.format(ControlsProperties.getString("String_OverlayAnalyst_Failed"), sourceDataset.getName() + "@" + sourceDataset.getDatasource().getAlias(),
						overlayAnalystDataset.getName() + "@" + overlayAnalystDataset.getDatasource().getAlias(), type.toString()));
				return false;
			}
			Application.getActiveApplication().getOutput().output(MessageFormat.format(SpatialAnalystProperties.getString("String_OverlayAnalyst_Cost"), String.valueOf((endLong - startLong) / 1000.0)));
		} catch (Exception e) {
			result = false;
			Application.getActiveApplication().getOutput().output(e);
		} finally {
			OverlayAnalyst.removeSteppedListener(this.percentListener);
		}
		return result;
	}

	@Override
	public void setType(OverlayAnalystType type) {
		this.type = type;
	}

	@Override
	public void setOverlayAnalystParameter(OverlayAnalystParameter parameter) {
		this.parameter = parameter;
	}

	@Override
	public void setSourceDataset(DatasetVector sourceDataset) {
		this.sourceDataset = sourceDataset;
	}

	@Override
	public void setOverlayAnalystDataset(DatasetVector overlayAnalystDataset) {
		this.overlayAnalystDataset = overlayAnalystDataset;
	}

	@Override
	public void setTargetDataset(DatasetVector targetDataset) {
		this.targetDataset = targetDataset;
	}

	class PercentListener implements SteppedListener {

		@Override
		public void stepped(SteppedEvent arg0) {
			try {
				updateProgress(arg0.getPercent(), Time.toString(arg0.getRemainTime(), TimeType.SECOND), arg0.getMessage());
			} catch (CancellationException e) {
				arg0.setCancel(true);
			}

		}

	}
}
