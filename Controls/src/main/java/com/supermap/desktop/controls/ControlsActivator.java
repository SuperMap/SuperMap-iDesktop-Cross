package com.supermap.desktop.controls;

import com.supermap.desktop.Application;
import com.supermap.desktop.core.CoreServiceTracker;
import com.supermap.desktop.dialog.OptionPaneImpl;
import com.supermap.desktop.process.ProcessEnv;
import com.supermap.desktop.utilities.JOptionPaneUtilities;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class ControlsActivator implements BundleActivator {
	ServiceRegistration<?> registration = null;
	CoreServiceTracker serviceTracker = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Hello SuperMap === Control!!");
		JOptionPaneUtilities.setiOptionPane(new OptionPaneImpl());
		if (Application.getActiveApplication() == null || Application.getActiveApplication().getPluginManager().getBundle("SuperMap.Desktop.Controls") == null) {
			this.serviceTracker = new CoreServiceTracker(context);
			this.serviceTracker.open();
//			this.registration = context.registerService(Application.class.getName(), new Application(), null);

			Application.getActiveApplication().getPluginManager().addPlugin("SuperMap.Desktop.Controls", context.getBundle());
			ProcessEnv.INSTANCE.addParametersUIPackage("com.supermap.desktop.process.parameters.ParameterPanels", "SuperMap.Desktop.Controls");
		}
//		JRibbonFrame frame = new JRibbonFrame();
//
//		JRibbonBand band1 = new JRibbonBand("Hello", null);
//		band1.setResizePolicies((List) Arrays.asList(
//				new CoreRibbonResizePolicies.None(band1.getControlPanel()),
//				new IconRibbonBandResizePolicy(band1.getControlPanel())));
//		JCommandButton button1 = new JCommandButton("Square", null);
//		button1.setCommandButtonKind(JCommandButton.CommandButtonKind.POPUP_ONLY);
//		button1.setPopupCallback(new PopupPanelCallback() {
//			@Override
//			public JPopupPanel getPopupPanel(JCommandButton commandButton) {
//				JCommandPopupMenu menu = new JCommandPopupMenu();
//				menu.addMenuButton(new JCommandMenuButton("你好Menu",
//						new EmptyResizableIcon(23)));
//				menu.addMenuButton(new JCommandMenuButton("HelloMenu", null));
//				menu.addMenuSeparator();
//				return menu;
//			}
//		});
//		JCommandButton button2 = new JCommandButton("Circle", null);
//		JCommandButton button3 = new JCommandButton("Triangle", null);
//		JCommandButton button4 = new JCommandButton("Star", null);
//
////				band1.addCommandButton(button3, RibbonElementPriority.MEDIUM);
////				band1.addCommandButton(button4, RibbonElementPriority.MEDIUM);
//		JRibbonBand ribbonBandChild = new JRibbonBand("child", null);
//		ribbonBandChild.setResizePolicies((List) Arrays.asList(
//				new CoreRibbonResizePolicies.Mirror(ribbonBandChild.getControlPanel()),
//				new IconRibbonBandResizePolicy(ribbonBandChild.getControlPanel())));
//		ribbonBandChild.addCommandButton(button3, RibbonElementPriority.TOP);
//		ribbonBandChild.addCommandButton(button4, RibbonElementPriority.TOP);
//		ribbonBandChild.addCommandButton(button1, RibbonElementPriority.TOP);
//		ribbonBandChild.addCommandButton(button2, RibbonElementPriority.TOP);
//
////				band1.addCommandButton(button1, RibbonElementPriority.TOP);
////				band1.addCommandButton(button2, RibbonElementPriority.MEDIUM);
//		RibbonTask task1 = new RibbonTask("One", ribbonBandChild);
//		frame.getRibbon().addTask(task1);
//		frame.pack();
//
//		frame.setVisible(true);
//		JDialog jDialog = new JDialog();
//		SubstanceSkin[] skins = new SubstanceSkin[]{
//				new AutumnSkin(),
//				new BusinessBlackSteelSkin(),
//				new BusinessBlueSteelSkin(),
//				new BusinessSkin(),
//				new CeruleanSkin(),
//				new CremeCoffeeSkin(),
//				new DustCoffeeSkin(),
//				new GeminiSkin(),
//				new GraphiteAquaSkin(),
//				new GraphiteChalkSkin(),
//				new GraphiteGlassSkin(),
//				new GraphiteGoldSkin(),
//				new GraphiteSkin(),
//				new MarinerSkin(),
//				new MistAquaSkin(),
//				new MistSilverSkin(),
//				new MistAquaSkin(),
//				new ModerateSkin(),
//				new NebulaSkin(),
//				new OfficeBlack2007Skin(),
//				new OfficeBlue2007Skin(),
//				new OfficeSilver2007Skin(),
//				new RavenSkin(),
//				new SaharaSkin(),
//		};
//
//		final JComboBox<SubstanceSkin> objectJComboBox = new JComboBox<>();
//		for (SubstanceSkin skin : skins) {
//			objectJComboBox.addItem(skin);
//		}
//		objectJComboBox.setRenderer(new ListCellRenderer<SubstanceSkin>() {
//			@Override
//			public Component getListCellRendererComponent(JList<? extends SubstanceSkin> list, SubstanceSkin value, int index, boolean isSelected, boolean cellHasFocus) {
//				JLabel jLabel = new JLabel();
//				String[] split = value.getClass().getName().split("\\.");
//				jLabel.setText(split[split.length - 1]);
//				if (isSelected) {
//					jLabel.setOpaque(true);
//					jLabel.setBackground(Color.BLUE);
//				}
//				return jLabel;
//			}
//		});
//
//		JButton jButton = new JButton("apply");
//		jButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				SkinUtilties.setSkin(((SubstanceSkin) objectJComboBox.getSelectedItem()));
//			}
//		});
//		Container contentPane = jDialog.getContentPane();
//		contentPane.setLayout(new GridBagLayout());
//		contentPane.add(objectJComboBox, new GridBagConstraintsHelper(0, 0, 1, 1).setWeight(1, 1).setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 0));
//		contentPane.add(jButton, new GridBagConstraintsHelper(1, 0, 1, 1).setWeight(1, 1).setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 5, 10, 10));
//		jDialog.setModal(false);
//		jDialog.setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("Goodbye SuperMap === Control!!");
		this.serviceTracker.close();
//		this.registration.unregister();
	}

}
