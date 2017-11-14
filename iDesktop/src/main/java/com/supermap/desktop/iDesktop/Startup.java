package com.supermap.desktop.iDesktop;

import com.alibaba.fastjson.JSONArray;
import org.apache.felix.main.Main;

import javax.swing.*;
import java.util.Locale;

/**
 * Created by highsad on 2016/8/3.
 */
public class Startup {
	public static void main(String[] args) {
		try {

			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//			UIManager.setLookAndFeel(new NimbusLookAndFeel());
//			UIManager.setLookAndFeel(UIManager.get);
//			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
//			UIManager.put("RootPane.setupButtonVisible", false);
//			BeautyEyeLNFHelper.translucencyAtFrameInactive = false;
//			BeautyEyeLNFHelper.setMaximizedBoundForFrame = false;
//			if(UIManager.getLookAndFeel() instanceof MetalLookAndFeel){
//				((MetalLookAndFeel)UIManager.getLookAndFeel()).setCurrentTheme(new OceanTheme());
//			}
			JSONArray jsonArray = new JSONArray();
			for (String arg : args) {
				if (arg.startsWith("-locale=")) {
					String[] split = arg.split("-locale=");
					String language = split[1];
					if (language.equals("zh_CN")) {
						Locale.setDefault(new Locale("zh", "CN"));
					} else if (language.equals("en_US")) {
						Locale.setDefault(new Locale("en", "US"));
					}
				} else {
					jsonArray.add(arg);
				}
			}
			System.setProperty("DesktopCrossStartArgs", jsonArray.toJSONString());
			Main.main(new String[0]);
			System.exit(-1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
