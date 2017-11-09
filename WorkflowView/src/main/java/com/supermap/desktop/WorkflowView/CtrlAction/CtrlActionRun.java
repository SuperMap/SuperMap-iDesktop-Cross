package com.supermap.desktop.WorkflowView.CtrlAction;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IDockbar;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.WorkflowView.FormWorkflow;
import com.supermap.desktop.WorkflowView.circulation.CirculationIterator;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.tasks.TasksManager;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Created by highsad on 2017/2/28.
 */
public class CtrlActionRun extends CtrlAction {
	private final static String TASKS_CONTAINER = "com.supermap.desktop.WorkflowView.tasks.TasksManagerContainer";

	public CtrlActionRun(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public void run() {
		try {
			if (Application.getActiveApplication().getActiveForm() instanceof FormWorkflow) {
				IDockbar tasksContainer = Application.getActiveApplication().getMainFrame().getDockbarManager().get(Class.forName(TASKS_CONTAINER));
				tasksContainer.setVisible(true);

				final FormWorkflow formWorkflow = (FormWorkflow) Application.getActiveApplication().getActiveForm();
				if (formWorkflow.getWorkflow().isReady()) {
					if (null != formWorkflow.iterator()) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								CirculationIterator iterator = formWorkflow.iterator();
								Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_IteratorStart"));
								while (iterator.hasNext()) {
									formWorkflow.getCanvas().getCirculationGraph().getOutputData().setValue(iterator.next());
									if (formWorkflow.getTasksManager().getStatus() == TasksManager.WORKFLOW_STATE_COMPLETED
											|| formWorkflow.getTasksManager().getStatus() == TasksManager.WORKFLOW_STATE_INTERRUPTED) {
										formWorkflow.getTasksManager().reset();
									}
									formWorkflow.getTasksManager().setStatus(TasksManager.WORKER_STATE_RUNNING);
									formWorkflow.getTasksManager().initialize();
									// 正在运行的时候禁止添加、删除节点，禁止调整连接关系和状态
									formWorkflow.getTasksManager().getWorkflow().setEditable(false);
									formWorkflow.getTasksManager().getScheduler().start();
									while (formWorkflow.getTasksManager().getScheduler().isRunning()) {
										try {
											TimeUnit.SECONDS.sleep(10);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
								}
								Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_IteratorExecuted"));
							}
						}).start();

					} else {
						formWorkflow.run();
					}
				}
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	@Override
	public boolean enable() {
		return Application.getActiveApplication().getMainFrame().getFormManager().getActiveForm() instanceof FormWorkflow;
	}
}
