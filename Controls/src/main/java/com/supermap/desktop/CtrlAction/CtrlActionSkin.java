package com.supermap.desktop.CtrlAction;

import com.supermap.desktop.Interface.IBaseItem;
import com.supermap.desktop.Interface.IForm;
import com.supermap.desktop.implement.CtrlAction;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.utilities.SkinUtilties;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandMenuButton;
import org.pushingpixels.flamingo.api.common.icon.EmptyResizableIcon;
import org.pushingpixels.flamingo.api.common.popup.JCommandPopupMenu;
import org.pushingpixels.flamingo.api.common.popup.JPopupPanel;
import org.pushingpixels.flamingo.api.common.popup.PopupPanelCallback;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenu;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryPrimary;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.IconRibbonBandResizePolicy;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.skin.AutumnSkin;
import org.pushingpixels.substance.api.skin.BusinessBlackSteelSkin;
import org.pushingpixels.substance.api.skin.BusinessBlueSteelSkin;
import org.pushingpixels.substance.api.skin.BusinessSkin;
import org.pushingpixels.substance.api.skin.CeruleanSkin;
import org.pushingpixels.substance.api.skin.CremeCoffeeSkin;
import org.pushingpixels.substance.api.skin.DustCoffeeSkin;
import org.pushingpixels.substance.api.skin.GeminiSkin;
import org.pushingpixels.substance.api.skin.GraphiteAquaSkin;
import org.pushingpixels.substance.api.skin.GraphiteChalkSkin;
import org.pushingpixels.substance.api.skin.GraphiteGlassSkin;
import org.pushingpixels.substance.api.skin.GraphiteGoldSkin;
import org.pushingpixels.substance.api.skin.GraphiteSkin;
import org.pushingpixels.substance.api.skin.MarinerSkin;
import org.pushingpixels.substance.api.skin.MistAquaSkin;
import org.pushingpixels.substance.api.skin.MistSilverSkin;
import org.pushingpixels.substance.api.skin.ModerateSkin;
import org.pushingpixels.substance.api.skin.NebulaSkin;
import org.pushingpixels.substance.api.skin.OfficeBlack2007Skin;
import org.pushingpixels.substance.api.skin.OfficeBlue2007Skin;
import org.pushingpixels.substance.api.skin.OfficeSilver2007Skin;
import org.pushingpixels.substance.api.skin.RavenSkin;
import org.pushingpixels.substance.api.skin.SaharaSkin;

import javax.swing.*;
import java.awt.*;
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
		skinTest();
//		ribbonTest();
	}

	private void skinTest() {
		JDialog jDialog = new JDialog();
		SubstanceSkin[] skins = new SubstanceSkin[]{
				new AutumnSkin(),
				new BusinessBlackSteelSkin(),
				new BusinessBlueSteelSkin(),
				new BusinessSkin(),
				new CeruleanSkin(),
				new CremeCoffeeSkin(),
				new DustCoffeeSkin(),
				new GeminiSkin(),
				new GraphiteAquaSkin(),
				new GraphiteChalkSkin(),
				new GraphiteGlassSkin(),
				new GraphiteGoldSkin(),
				new GraphiteSkin(),
				new MarinerSkin(),
				new MistAquaSkin(),
				new MistSilverSkin(),
				new MistAquaSkin(),
				new ModerateSkin(),
				new NebulaSkin(),
				new OfficeBlack2007Skin(),
				new OfficeBlue2007Skin(),
				new OfficeSilver2007Skin(),
				new RavenSkin(),
				new SaharaSkin(),
		};

		final JComboBox<SubstanceSkin> objectJComboBox = new JComboBox<>();
		for (SubstanceSkin skin : skins) {
			objectJComboBox.addItem(skin);
		}

		JButton jButton = new JButton("apply");
		jButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SkinUtilties.setSkin(((SubstanceSkin) objectJComboBox.getSelectedItem()));
			}
		});
		Container contentPane = jDialog.getContentPane();
		contentPane.setLayout(new GridBagLayout());
		contentPane.add(objectJComboBox, new GridBagConstraintsHelper(0, 0, 1, 1).setWeight(1, 1).setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 0));
		contentPane.add(jButton, new GridBagConstraintsHelper(1, 0, 1, 1).setWeight(1, 1).setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 5, 10, 10));
		jDialog.setVisible(true);
	}

	private void ribbonTest() {
		JRibbonFrame frame = new JRibbonFrame();

		JRibbonBand band1 = new JRibbonBand("Hello", null);
		band1.setResizePolicies((List) Arrays.asList(
				new CoreRibbonResizePolicies.None(band1.getControlPanel()),
				new IconRibbonBandResizePolicy(band1.getControlPanel())));
		JCommandButton button1 = new JCommandButton("Square", null);
		button1.setCommandButtonKind(JCommandButton.CommandButtonKind.POPUP_ONLY);
		button1.setPopupCallback(new PopupPanelCallback() {
			@Override
			public JPopupPanel getPopupPanel(JCommandButton commandButton) {
				JCommandPopupMenu menu = new JCommandPopupMenu();
				menu.addMenuButton(new JCommandMenuButton("你好Menu",
						new EmptyResizableIcon(23)));
				menu.addMenuButton(new JCommandMenuButton("HelloMenu", null));
				menu.addMenuSeparator();
				return menu;
			}
		});
		JCommandButton button2 = new JCommandButton("Circle", null);
		JCommandButton button3 = new JCommandButton("Triangle", null);
		JCommandButton button4 = new JCommandButton("Star", null);

//				band1.addCommandButton(button3, RibbonElementPriority.MEDIUM);
//				band1.addCommandButton(button4, RibbonElementPriority.MEDIUM);
		JRibbonBand ribbonBandChild = new JRibbonBand("child", null);
		ribbonBandChild.setResizePolicies((List) Arrays.asList(
				new CoreRibbonResizePolicies.Mirror(ribbonBandChild.getControlPanel()),
				new IconRibbonBandResizePolicy(ribbonBandChild.getControlPanel())));
		ribbonBandChild.addCommandButton(button3, RibbonElementPriority.TOP);
		ribbonBandChild.addCommandButton(button4, RibbonElementPriority.TOP);
		ribbonBandChild.addCommandButton(button1, RibbonElementPriority.TOP);
		ribbonBandChild.addCommandButton(button2, RibbonElementPriority.TOP);

//				band1.addCommandButton(button1, RibbonElementPriority.TOP);
//				band1.addCommandButton(button2, RibbonElementPriority.MEDIUM);
		RibbonTask task1 = new RibbonTask("One", ribbonBandChild);
		frame.getRibbon().addTask(task1);
		frame.pack();

		frame.setVisible(true);
	}

	public static void main(String[] args) {
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
