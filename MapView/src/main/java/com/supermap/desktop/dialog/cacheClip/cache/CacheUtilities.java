package com.supermap.desktop.dialog.cacheClip.cache;

import com.supermap.data.Datasource;
import com.supermap.data.processing.MapCacheBuilder;
import com.supermap.desktop.Application;
import com.supermap.desktop.controls.utilities.ToolbarUIUtilities;
import com.supermap.desktop.dialog.SmOptionPane;
import com.supermap.desktop.dialog.cacheClip.DialogCacheBuilder;
import com.supermap.desktop.dialog.cacheClip.DialogMapCacheClip;
import com.supermap.desktop.dialog.cacheClip.DialogMapCacheClipBuilder;
import com.supermap.desktop.mapview.MapViewProperties;
import com.supermap.desktop.ui.controls.DialogResult;
import com.supermap.desktop.utilities.PathUtilities;
import com.supermap.mapping.Map;

import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by xie on 2017/5/31.
 */
public class CacheUtilities {

	/**
	 * Replace path
	 *
	 * @param sourceStr
	 * @return
	 */
	public static String replacePath(String sourceStr) {
		if (isWindows()) {
			sourceStr = sourceStr.replaceAll("/", "\\\\");
		} else if (isLinux()) {
			sourceStr = sourceStr.replaceAll("\\\\", "/");
		}
		return sourceStr;
	}

	public static String replacePath(String sourceStr, String name) {
		sourceStr = replacePath(sourceStr);
		if (isWindows() && !sourceStr.endsWith("\\")) {
			sourceStr += "\\";
		} else if (isLinux() && !sourceStr.endsWith("/")) {
			sourceStr += "/";
		}
		return sourceStr + name;
	}

//	// 获取当前系统的所有的PidName
//	public static Set<String> getCurrOsAllPidNameSet() throws Exception {
//		Set<String> pidNameSet = new HashSet<>();
//		InputStream is = null;
//		InputStreamReader ir = null;
//		BufferedReader br = null;
//		String line = null;
//		String[] array = (String[]) null;
//		try {
//			Process p = Runtime.getRuntime().exec("TASKLIST /NH /FO CSV");
//			is = p.getInputStream();
//			ir = new InputStreamReader(is);
//			br = new BufferedReader(ir);
//			while ((line = br.readLine()) != null) {
//				array = line.split(",");
//				line = array[0].replaceAll("\"", "");
//				line = line.replaceAll(".exe", "");
//				line = line.replaceAll(".exe".toUpperCase(), "");
//				if (!StringUtilities.isNullOrEmpty(line)) {
//					pidNameSet.add(line);
//				}
//			}
//		} catch (IOException localIOException) {
//			throw new Exception("获取系统所有进程名出错！");
//		} finally {
//			if (br != null) {
//				br.close();
//			}
//			if (ir != null) {
//				ir.close();
//			}
//			if (is != null) {
//				is.close();
//			}
//		}
//		return pidNameSet;
//	}

	public static void startMapClip(Map map) {
		DialogMapCacheClip dialogMapCacheClip = new DialogMapCacheClip();
		if (dialogMapCacheClip.showDialog() == DialogResult.OK) {
			if (null != map.getLayers().get(0).getDataset()) {
				//没有提供数据源,只是拆分任务的情况
				Datasource datasource = map.getLayers().get(0).getDataset().getDatasource();
//			Datasource datasource = ((IFormMap) Application.getActiveApplication().getActiveForm()).getActiveLayers()[0].getDataset().getDatasource();
				if (!datasource.isReadOnly() && !dialogMapCacheClip.isSingleProcess()) {
					SmOptionPane pane = new SmOptionPane();
					pane.showConfirmDialog(MapViewProperties.getString("String_DatasourceOpenedNotReadOnly"));
					return;
				}
				datasource.getWorkspace().save();
				ToolbarUIUtilities.updataToolbarsState();
			}
//			Map map = ((IFormMap) Application.getActiveApplication().getActiveForm()).getMapControl().getMap();
			MapCacheBuilder mapCacheBuilder = new MapCacheBuilder();
			Map newMap = new Map(Application.getActiveApplication().getWorkspace());
			newMap.fromXML(map.toXML());
			mapCacheBuilder.setMap(newMap);
			if (dialogMapCacheClip.isBuildTask()) {
				int cmdType = dialogMapCacheClip.isSingleProcess() ? DialogMapCacheClipBuilder.SingleProcessClip : DialogMapCacheClipBuilder.MultiProcessClip;
				new DialogMapCacheClipBuilder(cmdType, mapCacheBuilder).showDialog();
			} else {
				String[] params = {"Multi", "null", "null", "null", map.getName(), "null"};
				startProcess(params, DialogCacheBuilder.class.getName(), LogWriter.BUILD_CACHE);
			}
		}
	}

	/**
	 * 获取Cross图标
	 *
	 * @return
	 */
	public static ArrayList getIconImages() {
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
		return images;
	}

	/**
	 * Start a new process
	 *
	 * @param params
	 * @param className
	 * @param cacheType
	 */
	public static void startProcess(String[] params, String className, String cacheType) {
		try {
			ArrayList<String> arguments = new ArrayList<>();
			String javaexeHome = CacheUtilities.replacePath(System.getProperty("java.home"), "bin") + File.separator + "java.exe";
			arguments.add(javaexeHome);
			arguments.add("-cp");
			String projectPath = replacePath(System.getProperty("user.dir"));
			String jarPath = "";
			if (isWindows()) {
				jarPath = ".;" + projectPath + "\\bin\\com.supermap.data.jar;" + projectPath + "\\bin\\com.supermap.mapping.jar;" + projectPath + "\\bin\\com.supermap.tilestorage.jar;" + projectPath + "\\bin\\com.supermap.data.processing.jar;" + projectPath + "\\bundles\\require_bundles\\Core.jar;" + projectPath + "\\bundles\\require_bundles\\Controls.jar;" + projectPath + "\\bundles\\idesktop_bundles\\MapView.jar";
			} else {
				jarPath = projectPath + "/bin/com.supermap.data.jar:" + projectPath + "/bin/com.supermap.mapping.jar:" + projectPath + "/bin/com.supermap.tilestorage.jar:" + projectPath + "/bin/com.supermap.data.processing.jar:" + projectPath + "/bundles/require_bundles/Core.jar:" + projectPath + "/bundles/require_bundles/Controls.jar:" + projectPath + "/bundles/idesktop_bundles/MapView.jar: ";
			}
//		String jarPath = ".;" + projectPath + "\\bundles\\require_bundles\\Core.jar;" + projectPath + "\\bundles\\idesktop_bundles\\MapView.jar";
			arguments.add(jarPath);
			arguments.add(className);
			for (int i = 0; i < params.length; i++) {
				arguments.add(params[i]);
			}
			ProcessManager manager = ProcessManager.getInstance();

			SubprocessThread thread = new SubprocessThread(arguments, cacheType);
			manager.addProcess(thread);
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isWindows() {
		boolean isWindows = false;
		String system = System.getProperties().getProperty("os.name");
		if (system.startsWith("Windows")) {
			isWindows = true;
		}
		return isWindows;
	}

	/**
	 * 判断是否是linux系统，暂时认为不是windows就是linux
	 * 避免增加平台时修改“!SystemPropertyUtilities.isWindows()”这样的调用
	 *
	 * @return
	 */
	public static boolean isLinux() {
		return !isWindows();
	}


	//List all sci file's name
	public static FilenameFilter getFilter() {
		return new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".sci");
			}
		};
	}

}
