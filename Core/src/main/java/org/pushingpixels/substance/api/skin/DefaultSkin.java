package org.pushingpixels.substance.api.skin;

import org.pushingpixels.substance.api.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.ComponentStateFacet;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceColorSchemeBundle;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.painter.border.GlassBorderPainter;
import org.pushingpixels.substance.api.painter.decoration.ArcDecorationPainter;
import org.pushingpixels.substance.api.painter.fill.ClassicFillPainter;
import org.pushingpixels.substance.api.painter.highlight.GlassHighlightPainter;
import org.pushingpixels.substance.api.shaper.ClassicButtonShaper;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;

import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;

/**
 * @author XiaJT
 */
public class DefaultSkin extends SubstanceSkin {
	public static final String NAME = "SuperMapDefault";
	// CeruleanSkin背景色

	public DefaultSkin() {
		super();
		ColorSchemes defaultColorSchemes = null;
		try {
			new Color(1, 1, 1);
			defaultColorSchemes = SubstanceColorSchemeUtilities.getColorSchemes(new File("F:/history/SuperMap-iDesktop-Cross/Core/src/main/resources/coreresources/skin/default.colorschemes").toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
//		defaultColorSchemes = SubstanceSkin.getColorSchemes("/coreresources/skin/default.colorschemes");
		SubstanceColorScheme activeScheme = defaultColorSchemes.get("Cerulean Active");// 滚动条
		SubstanceColorScheme enabledScheme = defaultColorSchemes.get("Cerulean Enabled");// 背景色
		SubstanceColorScheme rolloverSelectedScheme = defaultColorSchemes
				.get("Cerulean Rollover Selected");// tabbed panel
		SubstanceColorScheme disabledScheme = defaultColorSchemes.get("Cerulean Disabled");// gallery 上下按钮

		SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
				activeScheme, enabledScheme, disabledScheme);

		defaultSchemeBundle.registerColorScheme(defaultColorSchemes.get("Cerulean Pressed"),// 按钮按下
				ComponentState.PRESSED_SELECTED, ComponentState.PRESSED_UNSELECTED);
		defaultSchemeBundle.registerColorScheme(defaultColorSchemes.get("Cerulean Disabled Selected"),
				ComponentState.DISABLED_SELECTED);
		defaultSchemeBundle.registerColorScheme(defaultColorSchemes.get("Cerulean Selected"),
				ComponentState.SELECTED);
		defaultSchemeBundle.registerColorScheme(defaultColorSchemes.get("Cerulean Rollover Selected"),
				ComponentState.ROLLOVER_SELECTED);
		defaultSchemeBundle.registerColorScheme(defaultColorSchemes.get("Cerulean Rollover Unselected"),
				ComponentState.ROLLOVER_UNSELECTED);

		defaultSchemeBundle.registerColorScheme(defaultColorSchemes.get("Cerulean Mark"),
				ColorSchemeAssociationKind.MARK, ComponentState.getActiveStates());
		defaultSchemeBundle.registerColorScheme(defaultColorSchemes.get("Cerulean Border"),// ribbon按钮 checkbox 的边框
				ColorSchemeAssociationKind.BORDER, ComponentState.getActiveStates());

		// for progress bars
		ComponentState determinateState = new ComponentState("determinate enabled",
				new ComponentStateFacet[]{ComponentStateFacet.ENABLE,
						ComponentStateFacet.DETERMINATE, ComponentStateFacet.SELECTION},
				null);
		ComponentState determinateDisabledState = new ComponentState("determinate disabled",
				new ComponentStateFacet[]{ComponentStateFacet.DETERMINATE,
						ComponentStateFacet.SELECTION},
				new ComponentStateFacet[]{ComponentStateFacet.ENABLE});
		ComponentState indeterminateState = new ComponentState("indeterminate enabled",
				new ComponentStateFacet[]{ComponentStateFacet.ENABLE,
						ComponentStateFacet.SELECTION},
				new ComponentStateFacet[]{ComponentStateFacet.DETERMINATE});
		ComponentState indeterminateDisabledState = new ComponentState("indeterminate disabled",
				null, new ComponentStateFacet[]{ComponentStateFacet.DETERMINATE,
				ComponentStateFacet.ENABLE, ComponentStateFacet.SELECTION});
		defaultSchemeBundle.registerColorScheme(rolloverSelectedScheme, determinateState,
				indeterminateState);
		defaultSchemeBundle.registerColorScheme(rolloverSelectedScheme,
				ColorSchemeAssociationKind.BORDER, determinateState, indeterminateState);
		defaultSchemeBundle.registerColorScheme(disabledScheme, determinateDisabledState,
				indeterminateDisabledState);
		defaultSchemeBundle.registerColorScheme(disabledScheme, ColorSchemeAssociationKind.BORDER,
				determinateDisabledState, indeterminateDisabledState);

		// for uneditable fields
		ComponentState editable = new ComponentState("editable", new ComponentStateFacet[]{
				ComponentStateFacet.ENABLE, ComponentStateFacet.EDITABLE}, null);
		ComponentState uneditable = new ComponentState("uneditable", editable,
				new ComponentStateFacet[]{ComponentStateFacet.ENABLE},
				new ComponentStateFacet[]{ComponentStateFacet.EDITABLE});
		defaultSchemeBundle.registerColorScheme(defaultSchemeBundle.getColorScheme(editable),
				ColorSchemeAssociationKind.FILL, uneditable);

		// for text highlight
		SubstanceColorScheme highlightColorScheme = defaultColorSchemes.get("Moderate Highlight");
		defaultSchemeBundle.registerHighlightColorScheme(highlightColorScheme);

		registerDecorationAreaSchemeBundle(defaultSchemeBundle, DecorationAreaType.HEADER, DecorationAreaType.NONE);

		SubstanceColorScheme activeHeaderScheme = defaultColorSchemes.get("Cerulean Active Header");
		SubstanceColorScheme headerScheme = defaultColorSchemes.get("Cerulean Header");
		SubstanceColorScheme disabledHeaderScheme = defaultColorSchemes.get("Cerulean Header Disabled");
		SubstanceColorSchemeBundle headerSchemeBundle = new SubstanceColorSchemeBundle(
				activeHeaderScheme, headerScheme, disabledHeaderScheme);
		headerSchemeBundle.registerColorScheme(activeHeaderScheme, 0.6f,
				ComponentState.DISABLED_SELECTED, ComponentState.DISABLED_UNSELECTED);
		headerSchemeBundle.registerColorScheme(activeHeaderScheme, 0.6f,
				ColorSchemeAssociationKind.MARK, ComponentState.DISABLED_SELECTED,
				ComponentState.DISABLED_UNSELECTED);
		registerDecorationAreaSchemeBundle(headerSchemeBundle, headerScheme,
				DecorationAreaType.PRIMARY_TITLE_PANE, DecorationAreaType.SECONDARY_TITLE_PANE
		);

		registerAsDecorationArea(defaultColorSchemes.get("Cerulean Footer"), DecorationAreaType.FOOTER,
				DecorationAreaType.GENERAL);

		// add an overlay painter to paint a drop shadow along the top
		// edge of toolbars
//		this.addOverlayPainter(TopShadowOverlayPainter.getInstance(), DecorationAreaType.TOOLBAR);

		this.buttonShaper = new ClassicButtonShaper();
		this.fillPainter = new ClassicFillPainter();

		this.decorationPainter = new ArcDecorationPainter();

		this.highlightPainter = new GlassHighlightPainter();
		this.borderPainter = new GlassBorderPainter();
	}

	@Override
	public String getDisplayName() {
		return NAME;
	}
}
