package com.supermap.desktop.ui.xmlStartMenus;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.ICtrlAction;
import com.supermap.desktop.ui.XMLCommand;
import com.supermap.desktop.utilities.CtrlActionUtilities;
import com.supermap.desktop.utilities.JOptionPaneUtilities;
import com.supermap.desktop.utilities.XmlCommandUtilities;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntrySecondary;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author XiaJT
 */
public class SmSubMenu extends RibbonApplicationMenuEntrySecondary  implements IBaseItem {

	private XMLCommand xmlCommand;
	private ICtrlAction ctrlAction;

	public SmSubMenu(XMLCommand xmlCommand) {
		super(XmlCommandUtilities.getICon(XmlCommandUtilities.getXmlCommandImage(xmlCommand)), xmlCommand.getLabel()
				, null, JCommandButton.CommandButtonKind.ACTION_ONLY);
		this.xmlCommand = xmlCommand;
		ICtrlAction ctrlAction = Application.getActiveApplication().getCtrlAction(this.xmlCommand.getPluginInfo().getBundleName(),
				this.xmlCommand.getCtrlActionClass());
		if (ctrlAction == null) {
			ctrlAction = CtrlActionUtilities.getCtrlAction(this.xmlCommand, this, null);
			if (ctrlAction != null) {
				Application.getActiveApplication().setCtrlAction(this.xmlCommand.getPluginInfo().getBundleName(), this.xmlCommand.getCtrlActionClass(), ctrlAction);
			}
		}
		if (ctrlAction != null) {
			this.setCtrlAction(ctrlAction);
			this.mainActionListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					item_ActionPerformed();
				}
			};
		}
	}
	private void item_ActionPerformed() {
		try {
			if (this.getCtrlAction() != null) {
				this.getCtrlAction().setCaller(this);
				this.getCtrlAction().doRun();
			} else {
				Application.getActiveApplication().getOutput().output("CtrlAction Unimplemented!");
				JOptionPaneUtilities.showMessageDialog(this.xmlCommand.getCtrlActionClass() + " Unimplemented!");
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public void setVisible(boolean visible) {

	}

	@Override
	public boolean isChecked() {
		return false;
	}

	@Override
	public void setChecked(boolean checked) {

	}

	@Override
	public int getIndex() {
		return xmlCommand.getIndex();
	}

	@Override
	public void setIndex(int index) {
xmlCommand.setIndex(index);
	}

	@Override
	public String getID() {
		return xmlCommand.getID();
	}

	@Override
	public ICtrlAction getCtrlAction() {
		return ctrlAction;
	}

	@Override
	public void setCtrlAction(ICtrlAction ctrlAction) {
		this.ctrlAction = ctrlAction;
	}

	@Override
	public boolean isIgnoreEvent() {
		return false;
	}

	@Override
	public void setIgnoreEvent(boolean isIgnoreEvent) {

	}

	@Override
	public boolean isEnabled() {
		return ctrlAction == null || ctrlAction.enable();
	}

	public String getTooltip() {
		return xmlCommand.getTooltip();
	}
}
