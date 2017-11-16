package com.supermap.desktop.ui.xmlRibbons;

import com.supermap.desktop.Application;
import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.ICtrlAction;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.ui.XMLCommand;
import com.supermap.desktop.utilities.CtrlActionUtilities;
import com.supermap.desktop.utilities.JOptionPaneUtilities;
import com.supermap.desktop.utilities.StringUtilities;
import com.supermap.desktop.utilities.XmlCommandUtilities;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.popup.JCommandPopupMenu;
import org.pushingpixels.flamingo.api.common.popup.JPopupPanel;
import org.pushingpixels.flamingo.api.common.popup.PopupPanelCallback;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;

import java.awt.*;
import java.util.List;

public class SmXmlRibbonButton extends JCommandButton implements IBaseItem {

	private XMLRibbonButton xmlRibbonButton;
	private ICtrlAction ctrlAction = null;
	private JCommandPopupMenu menu;
	private boolean isIgnoreEvent;

	public SmXmlRibbonButton(XMLRibbonButton ribbonButton) {
		super(ribbonButton.getLabel());
		this.xmlRibbonButton = ribbonButton;
		initCommandButton();
	}

	private void initCommandButton() {
		Image image = XmlCommandUtilities.getXmlCommandImage(xmlRibbonButton);
		ImageWrapperResizableIcon iCon = XmlCommandUtilities.getICon(image);
		if (iCon != null) {
			this.setIcon(iCon);
		}
		this.setName(xmlRibbonButton.getLabel());
		ICtrlAction ctrlAction = Application.getActiveApplication().getCtrlAction(xmlRibbonButton.getPluginInfo().getBundleName(),
				xmlRibbonButton.getCtrlActionClass());
		if (ctrlAction == null) {
			ctrlAction = CtrlActionUtilities.getCtrlAction(xmlRibbonButton, this, null);
			if (ctrlAction != null) {
				Application.getActiveApplication().setCtrlAction(xmlRibbonButton.getPluginInfo().getBundleName(), xmlRibbonButton.getCtrlActionClass(), ctrlAction);
			}
		}
		if (ctrlAction != null) {
			setCtrlAction(ctrlAction);
			RichTooltip richTooltip = new RichTooltip(xmlRibbonButton.getLabel(), xmlRibbonButton.getTooltip());
			if (!StringUtilities.isNullOrEmpty(xmlRibbonButton.getTooltipImageFile())) {
				richTooltip.setMainImage(XmlCommandUtilities.getICon(XmlCommandUtilities.getXmlCommandToolTipImage(xmlRibbonButton)));
			}
			this.setActionRichTooltip(richTooltip);
		}
		this.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				item_ActionPerformed();
			}
		});
		if (xmlRibbonButton.getLength() > 0) {
			if (xmlRibbonButton.getMenuItemLength() > 0) {
				this.setCommandButtonKind(ctrlAction == null ? CommandButtonKind.POPUP_ONLY : CommandButtonKind.ACTION_AND_POPUP_MAIN_ACTION);
				menu = new JCommandPopupMenu();
				for (XMLCommand xmlCommand : xmlRibbonButton.getMenuItems()) {
					if (xmlCommand instanceof XmlRibbonCommandMenuSeparator) {
						menu.addMenuSeparator();
					} else if (xmlCommand instanceof XmlRibbonCommandMenuButton) {
						menu.addMenuButton(new SmCommandMenuButton(((XmlRibbonCommandMenuButton) xmlCommand)));
					}
				}
				this.setPopupCallback(new PopupPanelCallback() {
					@Override
					public JPopupPanel getPopupPanel(JCommandButton commandButton) {
						List<Component> menuComponents = menu.getMenuComponents();
						for (Component menuComponent : menuComponents) {
							if (menuComponent instanceof SmCommandMenuButton) {
								menuComponent.setEnabled(((SmCommandMenuButton) menuComponent).getCtrlAction().enable());
							}
						}
						return menu;
					}
				});
			} else {
				// 下拉为gallery的情况
			}
		} else if (ctrlAction == null) {
			this.setActionRichTooltip(new RichTooltip(CoreProperties.getString("String_UnDo"), this.getToolTipText() + CoreProperties.getString("String_UnDo")));
		}

	}

	private void item_ActionPerformed() {
		try {
			if (this.getCtrlAction() != null) {
				this.getCtrlAction().setCaller(this);
				this.getCtrlAction().doRun();
			} else {
				Application.getActiveApplication().getOutput().output("CtrlAction Unimplemented!");
				JOptionPaneUtilities.showMessageDialog(this.xmlRibbonButton.getCtrlActionClass() + " Unimplemented!");
			}
		} catch (Exception ex) {
			Application.getActiveApplication().getOutput().output(ex);
		}
	}

	public void putInBand(JRibbonBand ribbonBand) {
		ribbonBand.addCommandButton(this, getRibbonElementPriority());
	}

	public RibbonElementPriority getRibbonElementPriority() {
		String style = xmlRibbonButton.getStyle();
		if (style.equalsIgnoreCase("BIG")) {
			return RibbonElementPriority.TOP;
		} else if (style.equalsIgnoreCase("MEDIUM")) {
			return RibbonElementPriority.MEDIUM;
		} else if (style.equalsIgnoreCase("SMALL")) {
			return RibbonElementPriority.LOW;
		}
		return RibbonElementPriority.TOP;
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
		return this.xmlRibbonButton.getIndex();
	}

	@Override
	public void setIndex(int index) {
		this.xmlRibbonButton.setIndex(index);
	}

	@Override
	public String getID() {
		return this.xmlRibbonButton.getID();
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
		return isIgnoreEvent;
	}

	@Override
	public void setIgnoreEvent(boolean isIgnoreEvent) {
		this.isIgnoreEvent = isIgnoreEvent;
	}
}
