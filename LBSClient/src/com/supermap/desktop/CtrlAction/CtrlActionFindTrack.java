package com.supermap.desktop.CtrlAction;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.dialog.JDialogFindTrack;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.ui.controls.DialogResult;
import javax.swing.JFrame;

public class CtrlActionFindTrack extends CtrlAction {

	String topicNameRespond = "AttributeQuery_Respond";
	
	public CtrlActionFindTrack(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	public void run() {
		try {
			JFrame parent = (JFrame)Application.getActiveApplication().getMainFrame();
			JDialogFindTrack dialog = new JDialogFindTrack(parent, true);
			DialogResult result = dialog.showDialog();
			if (result == DialogResult.OK || result == DialogResult.APPLY) {
				WorkThead thread = new WorkThead();
				thread.start();
			}		
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	@Override
	public boolean enable() {
		return true;
	}
	
	class WorkThead extends Thread {

		@Override
		public void run() {
			try {
//				lbsResultConsumer consumer = new lbsResultConsumer();
//				consumer.doWork(topicNameRespond);
			} finally {
			}
		}
	}

	
}
