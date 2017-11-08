/*
 * Copyright (c) 2005-2016 Laf-Widget Kirill Grouchnikov. All Rights Reserved.
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
 *  o Neither the name of Laf-Widget Kirill Grouchnikov nor the names of
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
package org.pushingpixels.lafwidget.scroll;

import org.pushingpixels.lafwidget.LafWidget;
import org.pushingpixels.lafwidget.LafWidgetAdapter;
import org.pushingpixels.lafwidget.LafWidgetRepository;
import org.pushingpixels.lafwidget.LafWidgetUtilities2;
import org.pushingpixels.lafwidget.preview.PreviewPainter;

import javax.swing.*;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Widget that decorates scroll panes with selector.
 *
 * @author Kirill Grouchnikov
 */
public class ScrollPaneSelectorWidget extends LafWidgetAdapter<JScrollPane> {
	/**
	 * The scroll pane selector for the associated scroll pane.
	 */
	protected ScrollPaneSelector scrollPaneSelector;

	/**
	 * Hierarchy listener - remove the selector in the scroll pane of a combo
	 * popup.
	 */
	protected HierarchyListener hierarchyListener;

	/**
	 * Property change listener - listens on the changes to
	 * {@link LafWidget#COMPONENT_PREVIEW_PAINTER} property.
	 */
	protected PropertyChangeListener propertyChangeListener;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidget#requiresCustomLafSupport()
	 */
	public boolean requiresCustomLafSupport() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidgetAdapter#installUI()
	 */
	@Override
	public void installUI() {
		if (LafWidgetRepository.getRepository().getLafSupport()
				.toInstallExtraElements(this.jcomp)) {

			PreviewPainter pPainter = LafWidgetUtilities2
					.getComponentPreviewPainter(this.jcomp);
			if (pPainter == null)
				return;
			this.scrollPaneSelector = new ScrollPaneSelector();
			this.scrollPaneSelector.installOnScrollPane(this.jcomp);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidgetAdapter#uninstallUI()
	 */
	@Override
	public void uninstallUI() {
		if (this.scrollPaneSelector != null) {
			this.scrollPaneSelector.uninstallFromScrollPane();
			this.scrollPaneSelector = null;
		}
	}

	@Override
	public void installListeners() {
		this.hierarchyListener = new HierarchyListener() {
			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				if (ScrollPaneSelectorWidget.this.jcomp.getParent() instanceof ComboPopup) {
					if (ScrollPaneSelectorWidget.this.scrollPaneSelector != null) {
						ScrollPaneSelectorWidget.this.scrollPaneSelector.uninstallFromScrollPane();
						ScrollPaneSelectorWidget.this.scrollPaneSelector = null;
					}
				}
			}
		};
		this.jcomp.addHierarchyListener(this.hierarchyListener);

		this.propertyChangeListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (LafWidget.COMPONENT_PREVIEW_PAINTER.equals(evt.getPropertyName())) {
					PreviewPainter pPainter = LafWidgetUtilities2
							.getComponentPreviewPainter(ScrollPaneSelectorWidget.this.jcomp);
					// Uninstall old scroll pane selector
					if (ScrollPaneSelectorWidget.this.scrollPaneSelector != null) {
						ScrollPaneSelectorWidget.this.scrollPaneSelector.uninstallFromScrollPane();
						ScrollPaneSelectorWidget.this.scrollPaneSelector = null;
					}
					// Install new scroll pane selector
					if (pPainter != null
							&& LafWidgetRepository.getRepository().getLafSupport().
							toInstallExtraElements(ScrollPaneSelectorWidget.this.jcomp)) {
						ScrollPaneSelectorWidget.this.scrollPaneSelector = new ScrollPaneSelector();
						ScrollPaneSelectorWidget.this.scrollPaneSelector.installOnScrollPane(ScrollPaneSelectorWidget.this.jcomp);
					}
				}
			}
		};
		this.jcomp.addPropertyChangeListener(this.propertyChangeListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pushingpixels.lafwidget.LafWidgetAdapter#uninstallListeners()
	 */
	@Override
	public void uninstallListeners() {
		this.jcomp.removeHierarchyListener(this.hierarchyListener);
		this.hierarchyListener = null;

		this.jcomp.removePropertyChangeListener(this.propertyChangeListener);
		this.propertyChangeListener = null;
	}
}
