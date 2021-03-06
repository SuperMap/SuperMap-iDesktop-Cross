/*
 * Copyright (c) 2005-2016 Flamingo / Substance Kirill Grouchnikov. All Rights Reserved.
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
 *  o Neither the name of Flamingo Kirill Grouchnikov nor the names of 
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
package org.pushingpixels.substance.flamingo.ribbon.ui;

import org.pushingpixels.flamingo.api.ribbon.AbstractRibbonBand;
import org.pushingpixels.flamingo.internal.ui.ribbon.BasicFlowBandControlPanelUI;
import org.pushingpixels.flamingo.internal.ui.ribbon.BasicRibbonBandUI;
import org.pushingpixels.flamingo.internal.ui.ribbon.JFlowBandControlPanel;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

/**
 * UI for {@link JFlowBandControlPanel} components in <b>Substance</b> look and
 * feel.
 *
 * @author Kirill Grouchnikov
 */
public class SubstanceFlowBandControlPanelUI extends
		BasicFlowBandControlPanelUI {
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
	 */
	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SubstanceFlowBandControlPanelUI();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.flamingo.ribbon.ui.BasicBandControlPanelUI#paintBandBackground
	 * (java.awt.Graphics, java.awt.Rectangle)
	 */
	@Override
	protected void paintBandBackground(Graphics graphics, Rectangle toFill) {
		float rolloverAmount = 0.0f;
		Component parent = this.controlPanel.getParent();
		if (parent instanceof AbstractRibbonBand) {
			// not in popup
			AbstractRibbonBand band = (AbstractRibbonBand) this.controlPanel
					.getParent();
			BasicRibbonBandUI ui = (BasicRibbonBandUI) band.getUI();
			rolloverAmount = ui.getRolloverAmount();
		}

		SubstanceRibbonBandUI.paintRibbonBandBackground(graphics,
				this.controlPanel, rolloverAmount, this.controlPanel
						.getBounds().y);
	}
}
