package com.supermap.desktop.process.ui;

import com.supermap.desktop.controls.utilities.ControlsResources;
import com.supermap.desktop.process.ProcessResources;
import com.supermap.desktop.properties.CoreProperties;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by highsad on 2017/4/20.
 */
public class ButtonExecutor extends JButton implements MouseListener, MouseMotionListener {
	public final static int READY = 1;
	public final static int RUNNING = 2;
	public final static int CANCELLING = 3;
	public final static int COMPLETED = 4;

	private final static Icon ICON_READY_NORMAL = ProcessResources.getIcon("/processresources/task/image_run.png");
	private final static Icon ICON_READY_HOT = ProcessResources.getIcon("/processresources/task/image_run_hot.png");
	private final static Icon ICON_READY_UNABLE = ProcessResources.getIcon("/processresources/task/image_run_unable.png");
	private final static Icon ICON_RUNNING_NORMAL = ProcessResources.getIcon("/processresources/task/image_cancel.png");
	private final static Icon ICON_RUNNING_HOT = ProcessResources.getIcon("/processresources/task/image_cancel_hot.png");
	private final static Icon ICON_RUNNING_UNABLE = ProcessResources.getIcon("/processresources/task/image_cancel_unable.png");
	private final static Icon ICON_CANCELLING = ControlsResources.getIcon("/controlsresources/ToolBar/Image_stop_pressed.png");
	private final static Icon ICON_COMPLETED_NOMAL = ProcessResources.getIcon("/processresources/task/image_finish.png");
	private final static Icon ICON_COMPLETED_HOT = ProcessResources.getIcon("/processresources/task/image_run.png");

	private final static String TIP_READY = CoreProperties.getString(CoreProperties.Run);
	private final static String TIP_RUNNING = CoreProperties.getString(CoreProperties.Cancel);
	private final static String TIP_CANCELLING = CoreProperties.getString(CoreProperties.Cancelling);
	private final static String TIP_COMPLETED = CoreProperties.getString(CoreProperties.ReRun);

	public final static int NORMAL = 1;
	public final static int HOT = 2;
	private boolean isExecutorEnabled = true;
	private int procedure = READY;
	private int status = NORMAL;

	private Map<Integer, Icon> normalIconMap = new HashMap<>();
	private Map<Integer, Icon> hotIconMap = new HashMap<>();
	private Map<Integer, Icon> unableIconMap = new HashMap<>();
	private Map<Integer, String> tipsMap = new HashMap<>();

	private Runnable run;
	private Runnable cancel;

	public ButtonExecutor(Runnable run, Runnable cancel) {
		this.run = run;
		this.cancel = cancel;
		setContentAreaFilled(false);
		addMouseListener(this);
		addMouseMotionListener(this);
		initIconMaps();
		initTips();
		refresh();
	}

	private void initIconMaps() {
		this.normalIconMap.put(ButtonExecutor.READY, ICON_READY_NORMAL);
		this.normalIconMap.put(ButtonExecutor.RUNNING, ICON_RUNNING_NORMAL);
		this.normalIconMap.put(ButtonExecutor.CANCELLING, ICON_CANCELLING);
		this.normalIconMap.put(ButtonExecutor.COMPLETED, ICON_COMPLETED_NOMAL);

		this.hotIconMap.put(ButtonExecutor.READY, ICON_READY_HOT);
		this.hotIconMap.put(ButtonExecutor.RUNNING, ICON_RUNNING_HOT);
		this.hotIconMap.put(ButtonExecutor.CANCELLING, ICON_CANCELLING);
		this.hotIconMap.put(ButtonExecutor.COMPLETED, ICON_COMPLETED_HOT);

		this.unableIconMap.put(ButtonExecutor.READY, ICON_READY_UNABLE);
		this.unableIconMap.put(ButtonExecutor.RUNNING, ICON_RUNNING_UNABLE);
		this.unableIconMap.put(ButtonExecutor.CANCELLING, ICON_CANCELLING);
		this.unableIconMap.put(ButtonExecutor.COMPLETED, ICON_COMPLETED_NOMAL);
	}

	private void initTips() {
		this.tipsMap.put(ButtonExecutor.READY, TIP_READY);
		this.tipsMap.put(ButtonExecutor.RUNNING, TIP_RUNNING);
		this.tipsMap.put(ButtonExecutor.CANCELLING, TIP_CANCELLING);
		this.tipsMap.put(ButtonExecutor.COMPLETED, TIP_COMPLETED);
	}

	public boolean isExecutorEnabled() {
		return isExecutorEnabled;
	}

	public void setExecutorEnabled(boolean enable) {
		isExecutorEnabled = enable;
	}

	public int getProcedure() {
		return this.procedure;
	}

	public void setProcedure(int procedure) {
		if (this.procedure != procedure) {
			this.procedure = procedure;
			refresh();
		}
	}

	private void setStatus(int status) {
		if (this.status != status) {
			this.status = status;
			refresh();
		}
	}

	private void refresh() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setIcon(getAppropriateIcon());
				setToolTipText(tipsMap.get(procedure));
			}
		});
	}

	private Icon getAppropriateIcon() {
		if (!isExecutorEnabled()) {
			return this.unableIconMap.get(this.procedure);
		} else if (this.status == ButtonExecutor.NORMAL) {
			return this.normalIconMap.get(this.procedure);
		} else if (this.status == ButtonExecutor.HOT) {
			return this.hotIconMap.get(this.procedure);
		} else {
			return ICON_READY_NORMAL;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		setStatus(HOT);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (!isExecutorEnabled()) {
			return;
		}

		switch (this.procedure) {
			case READY:
			case COMPLETED: {
				setProcedure(RUNNING);
				this.run.run();
			}
			break;
			case RUNNING: {
				setProcedure(CANCELLING);
				this.cancel.run();
			}
			break;
			case CANCELLING:
			default:
				break;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		setStatus(HOT);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		setStatus(NORMAL);
	}

}
