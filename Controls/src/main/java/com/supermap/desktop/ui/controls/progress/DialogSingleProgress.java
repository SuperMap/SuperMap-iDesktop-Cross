package com.supermap.desktop.ui.controls.progress;

import com.supermap.desktop.process.tasks.IWorkerView;
import com.supermap.desktop.process.tasks.SingleProgress;
import com.supermap.desktop.properties.CommonProperties;
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
	private JLabel labelRemaintime = null;
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
		labelRemaintime = new JLabel("...");
		buttonCancel = new SmButton(CommonProperties.getString(CommonProperties.Cancel));
		this.getRootPane().setDefaultButton(this.buttonCancel);
		GroupLayout groupLayout = new GroupLayout(this.getContentPane());
		groupLayout.setAutoCreateContainerGaps(true);
		groupLayout.setAutoCreateGaps(true);
		this.getContentPane().setLayout(groupLayout);

		// @formatter:off
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(this.labelMessage, 420, 420, 420)
				.addComponent(this.progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, 420)
				.addComponent(this.labelRemaintime, 420, 420, 420)
				.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap(10, Short.MAX_VALUE)
						.addComponent(this.buttonCancel)));

		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addComponent(this.labelMessage)
				.addComponent(this.progressBar, DEFUALT_PROGRESSBAR_HEIGHT, DEFUALT_PROGRESSBAR_HEIGHT, DEFUALT_PROGRESSBAR_HEIGHT)
				.addComponent(this.labelRemaintime)
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
					buttonCancel.setText(CommonProperties.getString(CommonProperties.BeingCanceled));
					buttonCancel.setEnabled(false);
				}
			});
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					buttonCancel.setText(CommonProperties.getString(CommonProperties.Cancel));
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

	}

	@Override
	public void cancelled() {

	}

	@Override
	public void done() {

	}

	public static void main(String[] args) {
		DialogSingleProgress d = new DialogSingleProgress();
		d.setVisible(true);
	}
}
