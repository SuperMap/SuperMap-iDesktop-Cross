package com.supermap.desktop.ui.xmlRibbons;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.ICtrlAction;
import com.supermap.desktop.utilities.CtrlActionUtilities;
import com.supermap.desktop.utilities.JOptionPaneUtilities;
import com.supermap.desktop.utilities.XmlCommandUtilities;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;

import java.awt.*;

public class SmXMlGalleryButton extends JCommandToggleButton implements IBaseItem {

	private XMLRibbonGalleryButton button;
	private ICtrlAction ctrlAction = null;

	public SmXMlGalleryButton(XMLRibbonGalleryButton button) {
		super(button.getLabel());
		this.button = button;
		initCommandButton();
	}

	private void initCommandButton() {
		Image image = XmlCommandUtilities.getXmlCommandImage(button);
		ImageWrapperResizableIcon iCon = XmlCommandUtilities.getICon(image);
		if (iCon != null) {
			this.setIcon(iCon);
		}
		this.setName(button.getLabel());
		ICtrlAction ctrlAction = Application.getActiveApplication().getCtrlAction(button.getPluginInfo().getBundleName(),
				button.getCtrlActionClass());
		if (ctrlAction == null) {
			ctrlAction = CtrlActionUtilities.getCtrlAction(button, this, null);
			if (ctrlAction != null) {
				Application.getActiveApplication().setCtrlAction(button.getPluginInfo().getBundleName(), button.getCtrlActionClass(), ctrlAction);
			}
		}
		if (ctrlAction != null) {
			setCtrlAction(ctrlAction);
			this.setActionRichTooltip(new RichTooltip(button.getLabel(), button.getTooltip()));
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
				JOptionPaneUtilities.showMessageDialog(this.button.getCtrlActionClass() + " Unimplemented!");
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
		return button.getIndex();
	}

	@Override
	public void setIndex(int index) {
		button.setIndex(index);
	}

	@Override
	public String getID() {
		return button.getID();
	}

	@Override
	public ICtrlAction getCtrlAction() {
		return this.ctrlAction;
	}

	@Override
	public void setCtrlAction(ICtrlAction ctrlAction) {
		this.ctrlAction = ctrlAction;
	}
}
