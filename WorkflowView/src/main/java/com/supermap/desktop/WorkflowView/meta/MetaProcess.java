package com.supermap.desktop.WorkflowView.meta;

import com.supermap.data.FieldType;
import com.supermap.data.SteppedEvent;
import com.supermap.data.SteppedListener;
import com.supermap.desktop.process.ProcessResources;
import com.supermap.desktop.process.core.AbstractProcess;
import com.supermap.desktop.process.core.ReadyEvent;
import com.supermap.desktop.process.enums.RunningStatus;
import com.supermap.desktop.process.events.RunningEvent;
import com.supermap.desktop.process.parameter.events.ParameterPropertyChangedEvent;
import com.supermap.desktop.process.parameter.events.ParameterPropertyChangedListener;
import com.supermap.desktop.process.parameter.interfaces.IParameterPanel;
import com.supermap.desktop.process.parameter.interfaces.IParameters;
import com.supermap.desktop.process.parameter.ipls.DefaultParameters;
import com.supermap.desktop.properties.CoreProperties;

import javax.swing.*;

/**
 * Created by highsad on 2017/1/5.
 */
public abstract class MetaProcess extends AbstractProcess {

	protected static final String SOURCE_PANEL_DESCRIPTION = CoreProperties.getString("String_GroupBox_SourceData");
	protected static final String SETTING_PANEL_DESCRIPTION = CoreProperties.getString("String_FormEdgeCount_Text");
	protected static final String RESULT_PANEL_DESCRIPTION = CoreProperties.getString("String_GroupBox_ResultData");

	protected static FieldType[] fieldType = {FieldType.INT16, FieldType.INT32, FieldType.INT64, FieldType.SINGLE, FieldType.DOUBLE};
	private final ParameterPropertyChangedListener parameterPropertyChangedListener;

	protected IParameters parameters = new DefaultParameters(this);

	protected boolean isChangeSourceData = false;

	protected SteppedListener steppedListener = new SteppedListener() {
		@Override
		public void stepped(SteppedEvent steppedEvent) {

			RunningEvent event = new RunningEvent(MetaProcess.this, steppedEvent.getPercent(), steppedEvent.getMessage(), steppedEvent.getRemainTime());
			fireRunning(event);

			if (event.isCancel()) {
				cancel();
			}

			if (getStatus() == RunningStatus.CANCELLING) {
				steppedEvent.setCancel(true);
			}
		}
	};

	public MetaProcess() {
		parameterPropertyChangedListener = new ParameterPropertyChangedListener() {
			@Override
			public void parameterPropertyChanged(ParameterPropertyChangedEvent parameterPropertyChangedEvent) {
				checkReadyState(new ReadyEvent(MetaProcess.this, false));
			}
		};
		parameters.addParameterPropertyChangedListener(parameterPropertyChangedListener);
	}

	@Override
	public IParameters getParameters() {
		return parameters;
	}

	protected Icon getIconByPath(String path) {
		return new ImageIcon(ProcessResources.getResourceURL(path));
	}

	@Override
	public IParameterPanel getComponent() {
		return parameters.getPanel();
	}

	public boolean isFinished() {
		return this.getStatus() == RunningStatus.COMPLETED;
	}


	@Override
	public boolean isChangeSourceData() {
		return isChangeSourceData;
	}

	@Override
	public void dispose() {
		getParameters().dispose();
	}
}
