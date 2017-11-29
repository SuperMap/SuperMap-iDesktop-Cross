package com.supermap.desktop.layoutview.propertycontrols;

import com.supermap.desktop.Interface.IFormLayout;
import com.supermap.layout.MapLayoutDrawnEvent;
import com.supermap.layout.MapLayoutDrawnListener;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/28
 * Time: 10:39
 * Description:
 */
public abstract class AbstractPropertyControl extends JPanel {
	private static final long serialVersionUID = 1L;
	private boolean isAutoApply = true;
	private String propertyTitle = "";
	private transient IFormLayout formLayout = null;

	private MapLayoutDrawnListener mapLayoutDrawnListener=new MapLayoutDrawnListener() {
		@Override
		public void mapLayoutDrawn(MapLayoutDrawnEvent mapLayoutDrawnEvent) {

		}
	};

	/**
	 * Create the panel
	 * @param propertyTitle
	 */
	protected AbstractPropertyControl(String propertyTitle) {
		this.propertyTitle = propertyTitle;
		initializeComponents();
		initializeResources();
	}

	public String getPropertyTitle() {
		return propertyTitle;
	}

	public void setFormLayout(IFormLayout formLayout){
		unregisterEvents();
		this.formLayout = formLayout;
		if (this.formLayout != null) {
			initializePropertyValues(this.formLayout);
			fillComponents();
			setComponentsEnabled();
			registerEvents();
		}
	}

	public IFormLayout getFormLayout() {
		return this.formLayout;
	}

	public final boolean isAutoApply() {
		return this.isAutoApply;
	}

	public final void setAutoApply(boolean isAutoApply) {
		this.isAutoApply = isAutoApply;
		if (this.isAutoApply) {
			apply();
		}
	}

	public void addChangedListener(ChangedListener listener) {
		this.listenerList.add(ChangedListener.class, listener);
	}

	public void removeChangedListener(ChangedListener listener) {
		this.listenerList.remove(ChangedListener.class, listener);
	}

	public void verify() {
		if (this.isAutoApply) {
			apply();
		} else {
			if (verifyChange()) {
				fireChanged(new ChangedEvent(this, ChangedEvent.CHANGED));
			} else {
				fireChanged(new ChangedEvent(this, ChangedEvent.UNCHANGED));
			}
		}
	}

	public abstract void apply();

	protected abstract void initializeComponents();

	protected abstract void initializeResources();

	protected abstract void initializePropertyValues(IFormLayout formLayout);

	protected void registerEvents() {
		if (getFormLayout() != null) {
			this.getFormLayout().getMapLayoutControl().getMapLayout().addDrawnListener(this.mapLayoutDrawnListener);
		}
	}

	protected void unregisterEvents() {
		if (getFormLayout() != null) {
			this.getFormLayout().getMapLayoutControl().getMapLayout().removeDrawnListener(this.mapLayoutDrawnListener);
		}
	}

	protected abstract void fillComponents();

	protected abstract void setComponentsEnabled();

	protected abstract boolean verifyChange();

	protected final void fireChanged(ChangedEvent e) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangedListener.class) {
				((ChangedListener) listeners[i + 1]).changed(e);
			}
		}
	}
}
