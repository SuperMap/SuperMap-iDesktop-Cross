package com.supermap.desktop.process.tasks;

import com.supermap.desktop.Application;
import com.supermap.desktop.process.core.IProcess;
import com.supermap.desktop.process.core.ReadyEvent;
import com.supermap.desktop.process.core.Workflow;
import com.supermap.desktop.process.events.WorkflowChangeEvent;
import com.supermap.desktop.process.events.WorkflowChangeListener;
import com.supermap.desktop.process.tasks.events.WorkerStateChangedListener;
import com.supermap.desktop.process.tasks.events.WorkersChangedEvent;
import com.supermap.desktop.process.tasks.events.WorkersChangedListener;
import com.supermap.desktop.process.tasks.taskStates.TaskStateManager;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 第一版实现先默认把 Workflow 的所有节点都直接罗列出来，取消单个执行的按钮
 * 后续版本优化为任务管理器中的任务在构建工作流的时候就可以直接同步当前的任务状态（哪些可以执行、哪些需要等待前置条件）等
 * 并支持任务单个执行，点击则从所有前置节点开始，知道运行完成当前节点为止
 * Created by highsad on 2017/6/14.
 */
public class TasksManager {
	private final static int WORKFLOW_STATE_NORMAL = 0;
	private final static int WORKFLOW_STATE_RUNNING = 1;
	private final static int WORKFLOW_STATE_COMPLETED = 2;
	private final static int WORKFLOW_STATE_INTERRUPTED = 3;

	public final static int WORKER_STATE_RUNNING = 1;
	public final static int WORKER_STATE_READY = 2;
	public final static int WORKER_STATE_WAITING = 3;
	public final static int WORKER_STATE_COMPLETED = 4;
	public final static int WORKER_STATE_CANCELLED = 5;
	public final static int WORKER_STATE_EXCEPTION = 6;
	public final static int WORKER_STATE_WARNING = 7;

	private final Lock lock = new ReentrantLock();
	private volatile int status = WORKFLOW_STATE_NORMAL;

	private boolean isCancel = false;
	private Timer scheduler;
	private Workflow workflow;
	private TaskStateManager taskStateManager;
	private EventListenerList listenerList = new EventListenerList();
	private Map<IProcess, ProcessWorker> workersMap = new ConcurrentHashMap<>();

	private WorkflowChangeListener workflowChangeListener = new WorkflowChangeListener() {
		@Override
		public void workflowChange(WorkflowChangeEvent e) {
			if (e.getType() == WorkflowChangeEvent.ADDED) {
				processAdded(e.getProcess());
			} else if (e.getType() == WorkflowChangeEvent.REMOVED) {
				processRemoved(e.getProcess());
			}
		}
	};

	public TasksManager(Workflow workflow) {
		this.workflow = workflow;
		taskStateManager = new TaskStateManager(this, workflow);
		loadWorkflow(workflow);

		this.scheduler = new Timer(500, new SchedulerActionListener());
		this.workflow.addWorkflowChangeListener(this.workflowChangeListener);
	}

	private void loadWorkflow(Workflow workflow) {
		Vector<IProcess> processes = workflow.getProcesses();
		for (int i = 0; i < processes.size(); i++) {
			addNewProcess(processes.get(i));
		}
	}

	public int getStatus() {
		return status;
	}

	public ProcessWorker getWorkerByProcess(IProcess process) {
		return workersMap.get(process);
	}

	public Workflow getWorkflow() {
		return this.workflow;
	}

	public Vector<IProcess> getProcesses(int workerState) {
		return taskStateManager.get(workerState);
	}

	public final static int[] getWorkerStates() {
		return new int[]{WORKER_STATE_CANCELLED,
				WORKER_STATE_COMPLETED,
				WORKER_STATE_EXCEPTION,
				WORKER_STATE_READY,
				WORKER_STATE_RUNNING,
				WORKER_STATE_WAITING};
	}

	private void processAdded(IProcess process) {
		addNewProcess(process);
	}

	private void addNewProcess(IProcess process) {
		if (!this.workersMap.containsKey(process)) {
			ProcessWorker worker = new ProcessWorker(process);
			this.workersMap.put(process, worker);
			taskStateManager.addProcess(process);
			fireWorkersChanged(new WorkersChangedEvent(this, worker, WorkersChangedEvent.ADD));
		}
	}

	/**
	 * @param process
	 */
	private void processRemoved(IProcess process) {
		if (this.workersMap.containsKey(process)) {
			fireWorkersChanged(new WorkersChangedEvent(this, workersMap.get(process), WorkersChangedEvent.REMOVE));
			this.workersMap.remove(process);
			taskStateManager.removeProcess(process);
		}
	}

	public boolean run() {
		try {
			isCancel = false;
			if (this.status == WORKFLOW_STATE_RUNNING) {
				return false;
			}

			if (this.status == WORKFLOW_STATE_COMPLETED || this.status == WORKFLOW_STATE_INTERRUPTED) {
				reset();
			}

			this.status = WORKFLOW_STATE_RUNNING;

			initialize();
//			this.workflow.setEdiitable(false);

			// 正在运行的时候禁止添加、删除节点，禁止调整连接关系和状态
			if (!this.scheduler.isRunning()) {
				this.workflow.setEditable(false);
				this.scheduler.start();
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}

		return true;
	}

	public boolean isExecuting() {
		return this.status == WORKFLOW_STATE_RUNNING;
	}

	public void cancel() {
		isCancel = true;
	}

	public void pause() {
		// 待定
	}

	private void initialize() {
		Vector<IProcess> processes = this.workflow.getProcesses();
		for (IProcess process : processes) {
			if (this.workflow.isLeadingProcess(process) && process.isReady(new ReadyEvent(this, false))) {
				taskStateManager.moveProcess(process, WORKER_STATE_READY);
			}
		}
	}

	private synchronized void reset() {
		this.workflow.setEditable(true);

		taskStateManager.reset();

		if (this.scheduler.isRunning()) {
			this.scheduler.stop();
			workflow.setEditable(true);
			isCancel = false;
		}

		this.status = TasksManager.WORKFLOW_STATE_NORMAL;
	}

	public void addWorkerStateChangeListener(WorkerStateChangedListener listener) {
		taskStateManager.addWorkersChangedListener(listener);
	}

	public void removeWorkerStateChangeListener(WorkerStateChangedListener listener) {
		taskStateManager.removeWorkersChangedListener(listener);
	}

	public void addWorkersChangedListener(WorkersChangedListener listener) {
		this.listenerList.add(WorkersChangedListener.class, listener);
	}

	public void removeWorkersChangedListener(WorkersChangedListener listener) {
		this.listenerList.remove(WorkersChangedListener.class, listener);
	}

	protected void fireWorkersChanged(WorkersChangedEvent e) {
		Object[] listeners = this.listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == WorkersChangedListener.class) {
				((WorkersChangedListener) listeners[i + 1]).workersChanged(e);
			}
		}
	}

	private class SchedulerActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				lock.lock();
				Vector<IProcess> ready = taskStateManager.get(WORKER_STATE_READY);
				// TODO: 2017/8/12
				if (ready.size() > 0) {
					for (int i = ready.size() - 1; i >= 0; i--) {
						IProcess process = ready.get(i);
						if (!isCancel) {
							workersMap.get(process).execute();
						} else {
							workersMap.get(process).cancel();
						}
					}
				} else {
					// TODO: 2017/8/9 ready已经没有了，但是waitting还有需要处理下
				}

				if (isCancel) {
					Vector<IProcess> running = taskStateManager.get(WORKER_STATE_RUNNING);
					if (running != null) {
						for (int i = 0; i < running.size(); i++) {
							workersMap.get(running.get(i)).cancel();
						}
					}
				}

				// 当等待队列、就绪队列、运行队列均已经清空，则停止任务调度，并输出日志
				// 任务的执行和任务的调度是不同的线程并发执行的。调用 execute() 方法之后，任务执行线程处理任务状态改变，
				// 任务调度线程运行到这里根据任务的状态进行判断，它们的先后顺序并不一定
				// 任务调度线程进行是否停止的判断也仅仅考虑了 waiting/ready/running 三种状态
				// 任务调度的停止逻辑判断是建立在启动任务调度器的时候，任务管理处于初始状态的情况下，也就是启动前任务只有 waiting/ready 两种状态
				// 在任务调度器启动前，会有一个初始化的操作用来初始化任务管理器，然后也还会有些过程代码才会执行到 execute() 真正执行任务的地方
				// 在这个过程中，如果有某些未知的情况更改了任务的状态，则会导致一些异常结果
				// 举例说明，假设只有一个任务 taskA，调用了 cancel，导致任务从 waiting 队列移动到 cancelled 队列
				// execute 启动任务，导致 taskA 从 cancelled 队列移动到 running 队列
				// 调用 execute 之后，任务调度线程先执行到了这一句判定，此时 waiting 队列为空，ready 队列为空，running 队列为空，cancelled 队列有一个任务 taskA
				// 任务调度器就会通过这个判断，停止工作
				if (taskStateManager.get(WORKER_STATE_WAITING).size() == 0 && ready.size() == 0 && taskStateManager.get(WORKER_STATE_RUNNING).size() == 0) {
					scheduler.stop();
					workflow.setEditable(true);
					isCancel = false;
					if (workflow.getProcessCount() == taskStateManager.get(WORKER_STATE_COMPLETED).size()) {
						status = TasksManager.WORKFLOW_STATE_COMPLETED;
					} else {
						status = TasksManager.WORKFLOW_STATE_INTERRUPTED;
					}
				}
			} catch (Exception ex) {
				Application.getActiveApplication().getOutput().output(ex);
			} finally {
				lock.unlock();
			}
		}
	}

}
