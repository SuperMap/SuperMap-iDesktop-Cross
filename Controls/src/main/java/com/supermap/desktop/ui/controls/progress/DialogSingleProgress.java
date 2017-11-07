package com.supermap.desktop.ui.controls.progress;

import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.process.ProcessProperties;
import com.supermap.desktop.process.tasks.IWorkerView;
import com.supermap.desktop.process.tasks.SingleProgress;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.controls.button.SmButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by highsad on 2017/11/3.
 */
public class DialogSingleProgress extends JDialog implements IWorkerView<SingleProgress> {
	private static final long serialVersionUID = 1L;
	private static final int DEFUALT_PROGRESSBAR_HEIGHT = 30;

	private transient SwingWorker<Boolean, Object> worker = null;
	private String message = "";
	private String remainTime = "";
	private int percent = 0;
	private volatile boolean isCancel = false;

	private JProgressBar progressBar = null;
	private JLabel labelMessage = null;
	private JLabel labelRemainTime = null;
	private SmButton buttonCancel = null;

	@Override
	public void update(SingleProgress chunk) {
		setResizable(false);
		setModal(true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setSize(450, 160);

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		labelMessage = new JLabel("...");
		labelRemainTime = new JLabel("...");
		buttonCancel = new SmButton(CoreProperties.getString(CoreProperties.Cancel));
		this.getRootPane().setDefaultButton(this.buttonCancel);
		GroupLayout groupLayout = new GroupLayout(this.getContentPane());
		groupLayout.setAutoCreateContainerGaps(true);
		groupLayout.setAutoCreateGaps(true);
		this.getContentPane().setLayout(groupLayout);

		// @formatter:off
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(this.labelMessage, 420, 420, 420)
				.addComponent(this.progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, 420)
				.addComponent(this.labelRemainTime, 420, 420, 420)
				.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap(10, Short.MAX_VALUE)
						.addComponent(this.buttonCancel)));

		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addComponent(this.labelMessage)
				.addComponent(this.progressBar, DEFUALT_PROGRESSBAR_HEIGHT, DEFUALT_PROGRESSBAR_HEIGHT, DEFUALT_PROGRESSBAR_HEIGHT)
				.addComponent(this.labelRemainTime)
				.addComponent(this.buttonCancel, DEFUALT_PROGRESSBAR_HEIGHT, DEFUALT_PROGRESSBAR_HEIGHT, DEFUALT_PROGRESSBAR_HEIGHT));
		// @formatter:on

		this.buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});

		addWindowListener(new WindowAdapter() {
			/**
			 * Invoked when a window is in the process of being closed. The close operation can be overridden at this point.
			 */
			@Override
			public void windowClosing(WindowEvent e) {
				cancel();
			}
		});

		setLocationRelativeTo(null);
	}

	public boolean isCancel() {
		return this.isCancel;
	}

	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;

		if (this.isCancel) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					buttonCancel.setText(CoreProperties.getString(CoreProperties.Cancelling));
					buttonCancel.setEnabled(false);
				}
			});
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					buttonCancel.setText(CoreProperties.getString(CoreProperties.Cancel));
					buttonCancel.setEnabled(true);
				}
			});
		}
	}

	private void cancel() {
		setCancel(true);
	}


	@Override
	public void running() {

	}

	@Override
	public void cancelling() {
		this.labelMessage.setText(ControlsProperties.getString("String_Canceling"));
		this.labelRemainTime.setText("");
		this.labelRemainTime.setVisible(false);
		this.buttonCancel.setText(CoreProperties.Cancelling);
		this.buttonCancel.setEnabled(false);
	}

	@Override
	public void cancelled() {
		this.labelMessage.setText(ProcessProperties.getString("String_Cancelled"));
		this.progressBar.setValue(0);
		this.buttonCancel.setText(CoreProperties.getString(CoreProperties.Cancel));
		this.buttonCancel.setEnabled(true);
	}

	@Override
	public void done() {
		this.labelMessage.setText(CoreProperties.getString("String_Completed"));
		setVisible(false);
	}

	public static void main(String[] args) {
		DialogSingleProgress d = new DialogSingleProgress();
		d.setVisible(true);
	}
}
