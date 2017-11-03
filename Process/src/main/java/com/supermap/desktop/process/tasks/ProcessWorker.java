package com.supermap.desktop.process.tasks;

import com.supermap.desktop.Application;
import com.supermap.desktop.core.Time;
import com.supermap.desktop.core.TimeType;
import com.supermap.desktop.process.core.IProcess;
import com.supermap.desktop.process.enums.RunningStatus;
import com.supermap.desktop.process.events.RunningEvent;
import com.supermap.desktop.process.events.RunningListener;
import com.supermap.desktop.properties.CoreProperties;

/**
 * Created by highsad on 2017/6/22.
 */
public class ProcessWorker extends Worker<SingleProgress> {
	private IProcess process;
	private RunningHandler runningHandler = new RunningHandler();

	public ProcessWorker(IProcess process) {
		if (process == null) {
			throw new NullPointerException();
		}

		this.process = process;
		setTitle(this.process.getTitle());
		this.process.addRunningListener(this.runningHandler);
	}

	public IProcess getProcess() {
		return this.process;
	}

	@Override
	protected boolean doWork() {
		return this.process.run();
	}

	@Override
	public void cancel() {
		super.cancel();
		this.process.cancel();
	}

	@Override
	public boolean isCancelled() {

		// 保证任何时候 process 的取消状态都与 worker 相同
		return super.isCancelled() && this.process.isCancelled();
	}

	private class RunningHandler implements RunningListener {
		@Override
		public void running(RunningEvent e) {
			try {
				if (process.getStatus() == RunningStatus.CANCELLING) {
					e.setCancel(true);
				} else {
					if (e.isIndeterminate()) {
						update(new SingleProgress(e.getMessage()));
					} else {
						update(new SingleProgress(e.getProgress(), e.getMessage(), CoreProperties.getString("String_Remain") + ":" + Time.toString(e.getRemainTime(), TimeType.SECOND)));
					}
				}
			} catch (Exception e1) {
				e.setCancel(true);
				Application.getActiveApplication().getOutput().output(e1);
			}
		}
	}
}
