package com.supermap.desktop.WorkflowView.CtrlAction;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IDockbar;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.WorkflowView.FormWorkflow;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.core.CirculationIterator;
import com.supermap.desktop.process.core.CirculationType;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;

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
								iterator.setRunning(true);
								Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_IteratorStart"));
								if (iterator.getCirculationType() != CirculationType.whileType) {
									while (iterator.hasNext() && !formWorkflow.getTasksManager().isCancel()) {
										formWorkflow.getCanvas().getCirculationGraph().getOutputData().setValue(iterator.next());
										formWorkflow.runIterator();
									}
								} else {
									try {
										boolean conditionValue = (boolean) formWorkflow.getCanvas().getCirculationGraph().getOutputData().getValue();
										while (validateAllCondition(iterator.getInfoList(), conditionValue) && !formWorkflow.getTasksManager().isCancel()) {
											formWorkflow.runIterator();
										}
									} catch (Exception e) {
										Application.getActiveApplication().getOutput().output(ProcessProperties.getString("String_whileTypeException"));
									}
								}
								iterator.setRunning(false);
								formWorkflow.getTasksManager().setCancel(false);
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

	private boolean validateAllCondition(ArrayList infoList, boolean conditionValue) throws ScriptException {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("js");
		boolean result = conditionValue;
		for (int i = 0; i < infoList.size(); i++) {
			result = result && (engine.eval((String) infoList.get(i)) == conditionValue);
		}
		return result;
	}

	@Override
	public boolean enable() {
		return Application.getActiveApplication().getMainFrame().getFormManager().getActiveForm() instanceof FormWorkflow;
	}
}
