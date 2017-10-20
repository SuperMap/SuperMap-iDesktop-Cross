package com.supermap.desktop.CtrlAction;

import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.implement.CtrlAction;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenu;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryPrimary;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.IconRibbonBandResizePolicy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

/**
 * @author XiaJT
 */
public class CtrlActionSkin extends CtrlAction {

	public CtrlActionSkin(IBaseItem caller, IForm formClass) {
		super(caller, formClass);
	}

	@Override
	protected void run() {
//		SkinUtilties.setSkin(new OfficeBlack2007Skin());
		JRibbonFrame jRibbonFrame = new JRibbonFrame();
		JRibbon ribbon = jRibbonFrame.getRibbon();
//		ribbon.addTaskbarComponent(new JButton("haha"));
		JRibbonBand wow = new JRibbonBand("wow", null);
		wow.setResizePolicies(((List) Arrays.asList(
				new CoreRibbonResizePolicies.None(wow.getControlPanel()),
				new IconRibbonBandResizePolicy(wow.getControlPanel()))));
		wow.addCommandButton(new JCommandButton("haha"), RibbonElementPriority.TOP);
		ribbon.addTask(new RibbonTask("hello", wow));
		RibbonApplicationMenu applicationMenu = new RibbonApplicationMenu();
		applicationMenu.addMenuEntry(new RibbonApplicationMenuEntryPrimary(null, "hahaaa", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(1);
			}
		}, JCommandButton.CommandButtonKind.POPUP_ONLY));
		ribbon.setApplicationMenu(applicationMenu);
		jRibbonFrame.setVisible(true);
	}

	@Override
	public boolean enable() {
		return super.enable();
	}
}
