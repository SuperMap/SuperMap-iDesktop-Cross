/*
 * Copyright (c) 2005-2016 Substance Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of Substance Kirill Grouchnikov nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.pushingpixels.substance.internal.ui;

import org.pushingpixels.lafwidget.LafWidget;
import org.pushingpixels.lafwidget.LafWidgetRepository;
import org.pushingpixels.lafwidget.utils.RenderingUtils;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.internal.animation.StateTransitionTracker;
import org.pushingpixels.substance.internal.animation.TransitionAwareUI;
import org.pushingpixels.substance.internal.utils.RolloverTextControlListener;
import org.pushingpixels.substance.internal.utils.SubstanceColorUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceSizeUtils;
import org.pushingpixels.substance.internal.utils.SubstanceTextUtilities;
import org.pushingpixels.substance.internal.utils.border.SubstanceTextComponentBorder;
import org.pushingpixels.trident.callback.TimelineCallback;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicFormattedTextFieldUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

/**
 * UI for formatted text fields in <b>Substance</b> look and feel.
 *
 * @author Kirill Grouchnikov
 */
public class SubstanceFormattedTextFieldUI extends BasicFormattedTextFieldUI
		implements TransitionAwareUI {
	protected StateTransitionTracker stateTransitionTracker;

	/**
	 * The associated formatted text field.
	 */
	protected JFormattedTextField textField;

	/**
	 * Property change listener.
	 */
	protected PropertyChangeListener substancePropertyChangeListener;

	/**
	 * Listener for transition animations.
	 */
	private RolloverTextControlListener substanceRolloverListener;

	/**
	 * Surrogate button model for tracking the state transitions.
	 */
	private ButtonModel transitionModel;

	private Set<LafWidget> lafWidgets;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
	 */
	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SubstanceFormattedTextFieldUI(comp);
	}

	/**
	 * Simple constructor.
	 *
	 * @param c Component (formatted text field).
	 */
	public SubstanceFormattedTextFieldUI(JComponent c) {
		super();
		this.textField = (JFormattedTextField) c;

		this.transitionModel = new DefaultButtonModel();
		this.transitionModel.setArmed(false);
		this.transitionModel.setSelected(false);
		this.transitionModel.setPressed(false);
		this.transitionModel.setRollover(false);
		this.transitionModel.setEnabled(this.textField.isEnabled());

		this.stateTransitionTracker = new StateTransitionTracker(
				this.textField, this.transitionModel);
		this.stateTransitionTracker.setRepaintCallback(
				new StateTransitionTracker.RepaintCallback() {
					@Override
					public TimelineCallback getRepaintCallback() {
						return SubstanceCoreUtilities.getTextComponentRepaintCallback(SubstanceFormattedTextFieldUI.this.textField);
					}
				});
	}

	@Override
	public void installUI(JComponent c) {
		this.lafWidgets = LafWidgetRepository.getRepository().getMatchingWidgets(c);

		super.installUI(c);

		for (LafWidget lafWidget : this.lafWidgets) {
			lafWidget.installUI();
		}
	}

	@Override
	public void uninstallUI(JComponent c) {
		for (LafWidget lafWidget : this.lafWidgets) {
			lafWidget.uninstallUI();
		}
		super.uninstallUI(c);
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		this.substanceRolloverListener = new RolloverTextControlListener(
				this.textField, this, this.transitionModel);
		this.substanceRolloverListener.registerListeners();

		this.stateTransitionTracker.registerModelListeners();
		this.stateTransitionTracker.registerFocusListeners();

		this.substancePropertyChangeListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("font".equals(evt.getPropertyName())) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							// remember the caret location - issue 404
							int caretPos = textField.getCaretPosition();
							textField.updateUI();
							textField.setCaretPosition(caretPos);
							Container parent = textField.getParent();
							if (parent != null) {
								parent.invalidate();
								parent.validate();
							}
						}
					});
				}

				if ("enabled".equals(evt.getPropertyName())) {
					transitionModel.setEnabled(textField.isEnabled());
				}
			}
		};
		this.textField
				.addPropertyChangeListener(this.substancePropertyChangeListener);
		for (LafWidget lafWidget : this.lafWidgets) {
			lafWidget.installListeners();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicTextUI#uninstallListeners()
	 */
	@Override
	protected void uninstallListeners() {
		this.stateTransitionTracker.unregisterModelListeners();
		this.stateTransitionTracker.unregisterFocusListeners();

		this.textField
				.removePropertyChangeListener(this.substancePropertyChangeListener);
		this.substancePropertyChangeListener = null;

		this.substanceRolloverListener.unregisterListeners();
		this.substanceRolloverListener = null;

		for (LafWidget lafWidget : this.lafWidgets) {
			lafWidget.uninstallListeners();
		}

		super.uninstallListeners();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicTextUI#installDefaults()
	 */
	@Override
	protected void installDefaults() {
		super.installDefaults();
		Border b = this.textField.getBorder();
		if (b == null || b instanceof UIResource) {
			Border newB = new BorderUIResource.CompoundBorderUIResource(
					new SubstanceTextComponentBorder(SubstanceSizeUtils
							.getTextBorderInsets(SubstanceSizeUtils
									.getComponentFontSize(this.textField))),
					new BasicBorders.MarginBorder());
			this.textField.setBorder(newB);
		}

		// support for per-window skins
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (textField == null)
					return;
				Color foregr = textField.getForeground();
				if ((foregr == null) || (foregr instanceof UIResource)) {
					textField.setForeground(SubstanceColorUtilities
							.getForegroundColor(SubstanceLookAndFeel
									.getCurrentSkin(textField)
									.getEnabledColorScheme(
											SubstanceLookAndFeel
													.getDecorationType(textField))));
				}
			}
		});
		for (LafWidget lafWidget : this.lafWidgets) {
			lafWidget.installDefaults();
		}
	}

	@Override
	protected void uninstallDefaults() {
		for (LafWidget lafWidget : this.lafWidgets) {
			lafWidget.uninstallDefaults();
		}

		super.uninstallDefaults();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicTextUI#paintBackground(java.awt.Graphics)
	 */
	@Override
	protected void paintBackground(Graphics g) {
		SubstanceTextUtilities.paintTextCompBackground(g, this.textField);
	}

	@Override
	public boolean isInside(MouseEvent me) {
		return true;
	}

	@Override
	public StateTransitionTracker getTransitionTracker() {
		return this.stateTransitionTracker;
	}

	@Override
	public void update(Graphics g, JComponent c) {
		Graphics2D g2d = (Graphics2D) g.create();
		RenderingUtils.installDesktopHints(g2d, c);
		super.update(g2d, c);
		g2d.dispose();
	}
}