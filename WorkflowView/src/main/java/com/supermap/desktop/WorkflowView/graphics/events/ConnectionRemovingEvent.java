package com.supermap.desktop.WorkflowView.graphics.events;

import com.supermap.desktop.WorkflowView.graphics.connection.IGraphConnection;
import com.supermap.desktop.WorkflowView.graphics.storage.IConnectionManager;
import com.supermap.desktop.event.CancellationEvent;

/**
 * Created by highsad on 2017/5/8.
 */
public class ConnectionRemovingEvent extends CancellationEvent {
	private IConnectionManager connectionManager;
	private IGraphConnection connection;

	/**
	 * Constructs a prototypical Event.
	 *
	 * @param connectionManager The object on which the Event initially occurred.
	 * @throws IllegalArgumentException if source is null.
	 */
	public ConnectionRemovingEvent(IConnectionManager connectionManager, IGraphConnection connection) {
		super(connectionManager, false);
		this.connectionManager = connectionManager;
		this.connection = connection;
	}

	public IConnectionManager getConnectionManager() {
		return connectionManager;
	}

	public IGraphConnection getConnection() {
		return connection;
	}
}
