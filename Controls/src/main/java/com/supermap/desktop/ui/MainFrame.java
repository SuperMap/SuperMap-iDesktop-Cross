package com.supermap.desktop.ui;

import com.supermap.desktop.GlobalParameters;
import com.supermap.desktop.controls.utilities.SystemUIUtilities;
import com.supermap.desktop.controls.utilities.ToolbarUIUtilities;
import com.supermap.desktop.utilities.PathUtilities;
import com.supermap.desktop.utilities.XmlCommandUtilities;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * @author Administrator
 */
public class MainFrame extends FormBase implements WindowListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates new form MainFrame
	 */
	public MainFrame() {

		this.setSize(1000, 640);
		this.setExtendedState(MAXIMIZED_BOTH);
		this.setText(GlobalParameters.getDesktopTitle());
		this.setName(GlobalParameters.getDesktopTitle());
		// 换成自己的图标：
		String path = PathUtilities.getRootPathName();
		String[] paths = new String[2];
		paths[0] = path;
		paths[1] = "../Resources/Frame";
		path = PathUtilities.combinePath(paths, true);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		ArrayList<Image> images = new ArrayList<>();
		images.add(toolkit.createImage(path + "iDesktop_Cross_16.png"));
		images.add(toolkit.createImage(path + "iDesktop_Cross_24.png"));
		images.add(toolkit.createImage(path + "iDesktop_Cross_32.png"));
		images.add(toolkit.createImage(path + "iDesktop_Cross_64.png"));
		images.add(toolkit.createImage(path + "iDesktop_Cross_128.png"));
		images.add(toolkit.createImage(path + "iDesktop_Cross_256.png"));
		images.add(toolkit.createImage(path + "iDesktop Cross.ico"));
		this.setIconImages(images);
		this.setApplicationIcon(XmlCommandUtilities.getICon(toolkit.createImage(path + "iDesktop_Cross_empty.png")));
		this.addWindowListener(this);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.getRibbon().addPropertyChangeListener("selectedTask", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				ToolbarUIUtilities.updataToolbarsState();
			}
		});
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				setVisible(true);
//			}
//		});
	}


	@Override
	public void windowOpened(WindowEvent e) {
		// nothing
	}

	@Override
	public void windowClosing(WindowEvent e) {
		SystemUIUtilities.exit();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// nothing
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// nothing

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// nothing

	}

	@Override
	public void windowActivated(WindowEvent e) {
		// nothing
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// nothing

	}
}
