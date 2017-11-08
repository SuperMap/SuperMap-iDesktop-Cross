package com.supermap.desktop.ui.xmlRibbons;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.ICtrlAction;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.CtrlActionUtilities;
import com.supermap.desktop.utilities.JOptionPaneUtilities;
import com.supermap.desktop.utilities.XmlCommandUtilities;
import org.pushingpixels.flamingo.api.common.JCommandMenuButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;

public class SmCommandMenuButton extends JCommandMenuButton implements IBaseItem {

	private XmlRibbonCommandMenuButton xmlCommand;
	private ICtrlAction ctrlAction;

	public SmCommandMenuButton(XmlRibbonCommandMenuButton xmlCommand) {
		super(xmlCommand.getLabel(), null);
		this.setIcon(XmlCommandUtilities.getICon(XmlCommandUtilities.getXmlCommandImage(xmlCommand)));
		this.xmlCommand = xmlCommand;
		init();
	}

	private void init() {
		this.setName(xmlCommand.getLabel());
		ICtrlAction ctrlAction = Application.getActiveApplication().getCtrlAction(xmlCommand.getPluginInfo().getBundleName(),
				xmlCommand.getCtrlActionClass());
		if (ctrlAction == null) {
			ctrlAction = CtrlActionUtilities.getCtrlAction(xmlCommand, this, null);
			if (ctrlAction != null) {
				Application.getActiveApplication().setCtrlAction(xmlCommand.getPluginInfo().getBundleName(), xmlCommand.getCtrlActionClass(), ctrlAction);
			}
		}
		if (ctrlAction != null) {
			setCtrlAction(ctrlAction);
			this.setActionRichTooltip(new RichTooltip(xmlCommand.getLabel(), xmlCommand.getTooltip()));
		} else {
			this.setActionRichTooltip(new RichTooltip("Undo", this.getToolTipText() + CoreProperties.getString("String_UnDo")));
		}
		this.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				item_ActionPerformed();
			}
		});
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
}
