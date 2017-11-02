package com.supermap.desktop.process.tasks;

/**
 * Created by highsad on 2017/6/22.
 */
public interface IWorkerView<V> {
	void update(V chunk);

	void running();

	void cancelling();

	void cancelled();

	void done();
}
