package com.supermap.desktop.process.util;

import com.supermap.desktop.Application;
import com.supermap.desktop.process.core.IProcess;
import com.supermap.desktop.process.parameter.interfaces.AbstractParameter;
import com.supermap.desktop.process.parameter.interfaces.IParameter;
import com.supermap.desktop.process.parameter.interfaces.IParameterPanel;
import com.supermap.desktop.process.parameter.interfaces.ParameterPanelDescribe;
import com.supermap.desktop.process.parameter.interfaces.datas.types.Type;
import com.supermap.desktop.process.parameter.ipls.ParameterClassBundleNode;
import com.supermap.desktop.process.parameter.ipls.ParameterCombine;
import com.supermap.desktop.process.parameter.ipls.ParameterSwitch;
import org.osgi.framework.Bundle;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author XiaJT
 */
public class ParameterUtil {
	public static final Dimension LABEL_DEFAULT_SIZE = new Dimension(95, 23);

	public static Class getParameterPanel(String parameterType, ArrayList<ParameterClassBundleNode> packs) {
		for (int i = packs.size() - 1; i >= 0; i--) {
			ParameterClassBundleNode pack = packs.get(i);
			List<Class<?>> classes = getClasses(pack);
			if (classes.size() > 0) {
				for (Class<?> aClass : classes) {
					Class<?>[] interfaces = aClass.getInterfaces();
					if (IParameterPanel.class.isAssignableFrom(aClass)) {
						ParameterPanelDescribe annotation = aClass.getAnnotation(ParameterPanelDescribe.class);
						if (annotation != null && annotation.parameterPanelType().equals(parameterType)) {
							return aClass;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 从包package中获取所有的Class
	 *
	 * @param pack
	 * @return
	 */
	private static List<Class<?>> getClasses(ParameterClassBundleNode pack) {

		// 第一个class类的集合
		java.util.List<Class<?>> classes = new ArrayList<>();
		// 获取包的名字 并进行替换
		String packageName = pack.getPackageName();
		String packageDirName = packageName.replace('.', '/');
		// 定义一个枚举的集合 并进行循环来处理这个目录下的things
		Enumeration<URL> dirs;
		try {
			Bundle bundle = Application.getActiveApplication().getPluginManager().getBundle(pack.getBundleName()).getBundle();
			dirs = bundle.findEntries(packageDirName, "*.class", true);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				// 得到路径信息
				String bundleOneClassName = url.getPath();
				// 将"/"替换为"."，得到类名称
				bundleOneClassName = bundleOneClassName.replace("/", ".").substring(0, bundleOneClassName.lastIndexOf("."));
				// 如果类名以"."开头，则移除这个点
				while (bundleOneClassName.startsWith(".")) {
					bundleOneClassName = bundleOneClassName.substring(1);
				}
				while (bundleOneClassName.startsWith("bin")) {
					bundleOneClassName = bundleOneClassName.substring(4);
				}

				// 让Bundle加载这个类
				classes.add(bundle.loadClass(bundleOneClassName));
			}
		} catch (Exception e) {
			Application.getActiveApplication().getOutput().output(e);
		}

		return classes;
	}

	public static ArrayList<IParameter> getSameTypeParameters(IProcess process, Type startGraphType) {
		ArrayList<IParameter> types = new ArrayList<>();
		ArrayList<IParameter> parameters = process.getParameters().getParameters();
		for (int i = 0, size = parameters.size(); i < size; i++) {
			getSameTypeParameter(types, parameters.get(i), startGraphType);
		}
		return types;
	}

	private static void getSameTypeParameter(ArrayList<IParameter> valueTypes, IParameter parameter, Type startGraphType) {
		if (parameter instanceof ParameterCombine) {
			ArrayList<IParameter> parameterList = ((ParameterCombine) parameter).getParameterList();
			for (int j = 0; j < parameterList.size(); j++) {
				if (parameterList.get(j) instanceof ParameterCombine) {
					getSameTypeParameter(valueTypes, parameterList.get(j), startGraphType);
				} else if (parameterList.get(j) instanceof ParameterSwitch) {
					int count = ((ParameterSwitch) parameterList.get(j)).getCount();
					for (int i = 0; i < count; i++) {
						getSameTypeParameter(valueTypes, ((ParameterSwitch) parameter).getParameterByIndex(i), startGraphType);
					}
				} else if (null != ((AbstractParameter) parameterList.get(j)).getValueType()
						&& ((AbstractParameter) parameterList.get(j)).getValueType().equals(startGraphType)) {
					valueTypes.add(parameterList.get(j));
				}
			}
		} else if (parameter instanceof ParameterSwitch) {
			int count = ((ParameterSwitch) parameter).getCount();
			for (int i = 0; i < count; i++) {
				getSameTypeParameter(valueTypes, ((ParameterSwitch) parameter).getParameterByIndex(i), startGraphType);
			}
		} else if (null != ((AbstractParameter) parameter).getValueType()
				&& ((AbstractParameter) parameter).getValueType().equals(startGraphType)) {
			valueTypes.add((AbstractParameter) parameter);
		}
	}
}
