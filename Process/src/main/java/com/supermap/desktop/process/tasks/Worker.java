package com.supermap.desktop.process.tasks;

import com.supermap.desktop.Application;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by highsad on 2017/9/20.
 */
public abstract class Worker<V extends Object> {
	private final static int NORMAL = 1;
	private final static int CANCELLING = 2;
	private final static int CANCELLED = 3;
	private final static int RUNNING = 4;
	private final static int DONE = 5;

	private IWorkerView<V> view;
	private volatile int state = NORMAL;
	//	private volatile boolean isCancelled = false;
//	private volatile boolean isRunning = false;

	/**
	 * 是否是在进行重置。因为内置的 SwingWorker 每次执行都会构造新的实例，
	 * 所以我们在执行之前就要检查，如果已有 SwingWorker 正在工作就要中断当前工作，
	 * 但是这个过程需要悄悄的做，不能影响到当前 Worker 的状态。
	 */
	private volatile boolean isResetting = false;
	private String title;
	private SwingWorkerSub workerSub;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setView(IWorkerView<V> view) {
		if (view == null) {
			throw new NullPointerException();
		}

		this.view = view;
	}

	public boolean get() throws ExecutionException, InterruptedException {
		if (this.workerSub == null) {
			throw new IllegalArgumentException();
		}

		return this.workerSub.get() == null ? false : this.workerSub.get();
	}

	/**
	 * override 该方法需要调用 super.cancel() 以设置正确的状态。
	 */
	public void cancel() {
		this.state = CANCELLING;
		invokeUpdateView(this.state);
	}

	/**
	 * 是否正在取消，取消操作可能会进行一些耗时的回滚，只有回滚完成才算是已经取消。
	 *
	 * @return
	 */
	public boolean isCancelling() {
		return this.state == CANCELLING;
	}

	/**
	 * 是否已经取消，取消操作可能会进行一些耗时的回滚，只有回滚完成才算是已经取消。
	 *
	 * @return
	 */
	public boolean isCancelled() {
		return this.state == CANCELLED;
	}

	private void invokeUpdateView(final int state) {
		try {
			if (SwingUtilities.isEventDispatchThread()) {
				updateView(state);
			} else {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						Worker.this.updateView(state);
					}
				});
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	private void updateView(int state) {
		if (this.view == null) {
			return;
		}

		if (state == RUNNING) {
			this.view.running();
		} else if (state == CANCELLING) {
			if (!this.isResetting) {
				this.view.cancelling();
			}
		} else if (state == CANCELLED) {
			if (!this.isResetting) {
				this.view.cancelled();
			}
		} else if (state == DONE) {
			this.view.done();
		}
	}

	public boolean isRunning() {
		return this.state == RUNNING;
	}

	public final void execute() {
		if (this.workerSub != null && !this.workerSub.isDone()) {
			try {
				this.isResetting = true;
				cancel();

				// 等待取消完成
				this.workerSub.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} finally {
				this.isResetting = false;
				this.state = Worker.NORMAL;
			}
		}

		// 由于 SwingWorker 不支持重新执行，因此如果需要重复执行请构造一个新的 SwingWorker
		try {
			this.state = Worker.RUNNING;
			invokeUpdateView(this.state);
			this.workerSub = new SwingWorkerSub();
			this.workerSub.execute();
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}
	}

	protected abstract boolean doWork();

	protected void update(V chunk) {
		if (this.workerSub != null) {
			this.workerSub.update(chunk);
		}
	}

	private class SwingWorkerSub extends SwingWorker<Boolean, V> {

		@Override
		protected final Boolean doInBackground() {
			try {
				return doWork();
			} catch (Exception e) {
				return false;
			} finally {

				// SwingWorker 的原生 cancel 之后会立即调用 done 方法
				// 而很多功能取消都会有数据回滚等需要等待的操作
				// 因此自定义 cancel 的实现，并在 doWork 线程中自行处理 cancel
				// 然后再调用 SwingWorker 的原生 cancel 操作，设置 SwingWorker 的状态
				// 最后才会调用 done 方法，此时无论自定义的 cancel 状态还是 SwingWorker 的
				// cancel 状态都是正确的
				if (Worker.this.state == Worker.CANCELLING) {
					cancel(false);
				}
			}
		}

		protected void update(V chunk) {
			publish(chunk);
		}

		@Override
		protected final void process(List<V> chunks) {
			if (view == null) {
				return;
			}

			if (chunks != null && chunks.size() > 0) {
				view.update(chunks.get(chunks.size() - 1));
			}
		}

		@Override
		protected void done() {
			if (Worker.this.state == Worker.CANCELLING) {
				Worker.this.state = Worker.CANCELLED;
				invokeUpdateView(Worker.this.state);
			} else if (Worker.this.state == Worker.RUNNING) {
				Worker.this.state = Worker.DONE;
				invokeUpdateView(Worker.this.state);
			}
		}
	}
}
