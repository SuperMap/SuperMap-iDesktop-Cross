package com.supermap.desktop.ui.xmlStartMenus;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.ICtrlAction;
import com.supermap.desktop.ui.XMLCommand;
import com.supermap.desktop.utilities.CtrlActionUtilities;
import com.supermap.desktop.utilities.JOptionPaneUtilities;
import com.supermap.desktop.utilities.StringUtilities;
import com.supermap.desktop.utilities.XmlCommandUtilities;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryPrimary;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * @author XiaJT
 */
public class SmStartMenu extends RibbonApplicationMenuEntryPrimary implements IBaseItem {

	private XMLStartMenu startMenu;
	private ICtrlAction ctrlAction;

	public SmStartMenu(XMLStartMenu startMenu) {
		super(XmlCommandUtilities.getICon(XmlCommandUtilities.getXmlCommandImage(startMenu)), startMenu.getLabel(), null, JCommandButton.CommandButtonKind.POPUP_ONLY);
		this.startMenu = startMenu;
		String ctrlActionClass = startMenu.getCtrlActionClass();
		boolean isCtrlActionExist = !StringUtilities.isNullOrEmpty(ctrlActionClass);
		if (isCtrlActionExist && startMenu.getLength() <= 0) {
			entryKind = JCommandButton.CommandButtonKind.ACTION_ONLY;
		} else if (!isCtrlActionExist && startMenu.getLength() > 0) {
			entryKind = JCommandButton.CommandButtonKind.POPUP_ONLY;
		} else if (isCtrlActionExist && startMenu.getLength() > 0) {
			entryKind = JCommandButton.CommandButtonKind.ACTION_AND_POPUP_MAIN_ACTION;
		}
		ICtrlAction ctrlAction = Application.getActiveApplication().getCtrlAction(startMenu.getPluginInfo().getBundleName(),
				startMenu.getCtrlActionClass());
		if (ctrlAction == null) {
			ctrlAction = CtrlActionUtilities.getCtrlAction(startMenu, this, null);
			if (ctrlAction != null) {
				Application.getActiveApplication().setCtrlAction(startMenu.getPluginInfo().getBundleName(), startMenu.getCtrlActionClass(), ctrlAction);
			}
		}
		if (ctrlAction != null) {
			this.setCtrlAction(ctrlAction);
			this.mainActionListener=new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					item_ActionPerformed();
				}
			};
		}
		ArrayList<SmSubMenus> smSubMenus = new ArrayList<>();
		for (int i = 0; i < startMenu.getLength(); i++) {
			XMLCommand commandAtIndex = startMenu.getCommandAtIndex(i);
			if (commandAtIndex instanceof XMLSubMenus) {
				SmSubMenus smSubMenu = new SmSubMenus(((XMLSubMenus) commandAtIndex));
				this.addSecondaryMenuGroup(smSubMenu.getTitle(), smSubMenu.getMenus());
			}
		}
	}

	private void item_ActionPerformed() {
		try {
			if (this.getCtrlAction() != null) {
				this.getCtrlAction().setCaller(this);
				this.getCtrlAction().doRun();
			} else {
				Application.getActiveApplication().getOutput().output("CtrlAction Unimplemented!");
				JOptionPaneUtilities.showMessageDialog(this.startMenu.getCtrlActionClass() + " Unimplemented!");
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	@Override
	public boolean isVisible() {
		return true;
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
		return startMenu.getIndex();
	}

	@Override
	public void setIndex(int index) {
		startMenu.setIndex(index);
	}

	@Override
	public String getID() {
		return startMenu.getID();
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

}
