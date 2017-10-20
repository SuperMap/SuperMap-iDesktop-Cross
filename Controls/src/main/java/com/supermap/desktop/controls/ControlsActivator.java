package com.supermap.desktop.controls;

import com.supermap.desktop.Application;
import com.supermap.desktop.core.CoreServiceTracker;
import com.supermap.desktop.dialog.OptionPaneImpl;
import com.supermap.desktop.process.ProcessEnv;
import com.supermap.desktop.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.utilities.JOptionPaneUtilities;
import com.supermap.desktop.utilities.SkinUtilties;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
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
		objectJComboBox.setRenderer(new ListCellRenderer<SubstanceSkin>() {
			@Override
			public Component getListCellRendererComponent(JList<? extends SubstanceSkin> list, SubstanceSkin value, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel jLabel = new JLabel();
				String[] split = value.getClass().getName().split("\\.");
				jLabel.setText(split[split.length - 1]);
				if (isSelected) {
					jLabel.setOpaque(true);
					jLabel.setBackground(Color.BLUE);
				}
				return jLabel;
			}
		});

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
		jDialog.setModal(false);
		jDialog.setVisible(true);
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
