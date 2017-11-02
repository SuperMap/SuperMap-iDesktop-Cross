package com.supermap.desktop.process.ui;

import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.utilities.ComponentUIUtilities;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.tasks.IWorkerView;
import com.supermap.desktop.process.tasks.SingleProgress;
import com.supermap.desktop.process.tasks.Worker;
import com.supermap.desktop.ui.controls.progress.RoundProgressBar;
import com.supermap.desktop.utilities.StringUtilities;

import javax.swing.*;
import java.awt.*;

/**
 * Created by highsad on 2017/6/22.
 */
public class SingleProgressPanel extends JPanel implements IWorkerView<SingleProgress> {
	private static final int WIDTH = 23;
	private static final Color DEFAULT_BACKGROUNDCOLOR = new Color(215, 215, 215);
	private static final Color DEFAULT_FOREGROUNDCOLOR = new Color(39, 162, 223);
	private static final Color CACEL_FOREGROUNDCOLOR = new Color(190, 190, 190);

	private Worker worker;
	private RoundProgressBar progressBar;
	private JLabel labelTitle;
	private JLabel labelMessage;
	private JLabel labelRemainTime;
	private ButtonExecutor buttonRun;

	private Runnable run = new Runnable() {
		@Override
		public void run() {
			if (worker != null) {
				worker.execute();
			}
		}
	};

	private Runnable cancel = new Runnable() {
		@Override
		public void run() {
			labelMessage.setText(ControlsProperties.getString("String_Canceling"));
			labelRemainTime.setText("");
			SingleProgressPanel.this.worker.cancel();
		}
	};

	public SingleProgressPanel(String title) {
		if (StringUtilities.isNullOrEmpty(title)) {
			throw new NullPointerException("worker can not be null.");
		}

		initializeComponents();
		initializeLayout();
		this.labelTitle.setText(title);
	}

	public void setWorker(Worker worker) {
		if (worker == null) {
			throw new NullPointerException("worker can not be null.");
		}

		this.worker = worker;
		this.worker.setView(this);
	}

	private void initializeComponents() {
		labelTitle = new JLabel();
		progressBar = new RoundProgressBar();
		progressBar.setBackgroundColor(DEFAULT_BACKGROUNDCOLOR);
		progressBar.setForegroundColor(DEFAULT_FOREGROUNDCOLOR);
		progressBar.setDigitalColor(labelTitle.getBackground());
		progressBar.setDrawString(true);
		labelMessage = new JLabel("...");
		labelRemainTime = new JLabel("...");
		this.buttonRun = new ButtonExecutor(this.run, this.cancel);
		ComponentUIUtilities.setName(labelTitle, "ProcessTask_labelTitle");
		ComponentUIUtilities.setName(progressBar, "ProcessTask_progressBar");
		ComponentUIUtilities.setName(labelMessage, "ProcessTask_labelMessage");
		ComponentUIUtilities.setName(labelRemainTime, "ProcessTask_labelRemaintime");
		ComponentUIUtilities.setName(buttonRun, "ProcessTask_buttonRun");
	}

	public void initializeLayout() {
		Dimension dimension = new Dimension(18, 18);
		this.buttonRun.setPreferredSize(dimension);
		this.buttonRun.setMinimumSize(dimension);
		GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(this.labelTitle)
				.addGroup(layout.createSequentialGroup()
						.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(buttonRun, WIDTH, WIDTH, WIDTH)
				)
				.addGroup(layout.createSequentialGroup()
						.addComponent(labelMessage)
						.addComponent(labelRemainTime)
				)
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.labelTitle, WIDTH, WIDTH, WIDTH)
				.addGroup(layout.createParallelGroup()
						.addComponent(progressBar, WIDTH, WIDTH, WIDTH)
						.addComponent(buttonRun, WIDTH, WIDTH, WIDTH)
				)
				.addGroup(layout.createParallelGroup()
						.addComponent(labelMessage, WIDTH, WIDTH, WIDTH)
						.addComponent(labelRemainTime, WIDTH, WIDTH, WIDTH)
				)
		);
		this.setLayout(layout);
	}

	public void setTitleVisible(boolean isVisible) {
		this.labelTitle.setVisible(isVisible);
	}

	public void reset() {
		this.progressBar.setProgress(0);
		this.labelMessage.setText("");
		this.labelRemainTime.setText("");
		this.buttonRun.setProcedure(ButtonExecutor.READY);
	}

	@Override
	public void update(SingleProgress chunk) {
		if (chunk.isIndeterminate()) {
			this.progressBar.updateProgressIndeterminate();
			this.labelMessage.setText(chunk.getMessage());
			this.labelRemainTime.setVisible(false);
			progressBar.setDrawString(false);
		} else {
			this.progressBar.stopUpdateProgressIndeterminate();
			this.progressBar.setProgress(chunk.getPercent());
			this.labelMessage.setText(chunk.getMessage());
			this.labelRemainTime.setText(chunk.getRemainTime());
			this.labelRemainTime.setVisible(true);
			progressBar.setDrawString(true);
		}
	}

	@Override
	public void running() {

	}

	@Override
	public void cancelling() {

	}

	@Override
	public void cancelled() {

	}

	@Override
	public void done() {
		this.labelRemainTime.setVisible(false);
		this.buttonRun.setProcedure(ButtonExecutor.READY);

		if (this.worker.isCancelled()) {
			this.labelMessage.setText(ProcessProperties.getString("String_Cancelled"));
			this.progressBar.setProgress(0);
		}
	}
}
