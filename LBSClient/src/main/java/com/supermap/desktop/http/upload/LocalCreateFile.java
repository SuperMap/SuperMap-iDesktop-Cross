package com.supermap.desktop.http.upload;

import com.supermap.desktop.Application;
import com.supermap.desktop.lbs.CreateFile;
import com.supermap.desktop.lbs.WebHDFS;
import com.supermap.desktop.properties.CoreProperties;
import com.supermap.desktop.utilities.CommonUtilities;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.MessageFormat;

/**
 * Created by xie on 2017/2/25.
 */
public class LocalCreateFile extends CreateFile {
	public void createDir(String url, String name) {
		try {
			// 创建目录
			String webFile = url;
			if (!webFile.endsWith("/")) {
				webFile += "/";
			}
			webFile = String.format("%s%s?user.name=root&op=MKDIRS", webFile, name);
			HttpPut requestPut = new HttpPut(webFile);
			HttpResponse response = new DefaultHttpClient().execute(requestPut);
			if (response != null && response.getStatusLine().getStatusCode() == 200) {
				Application.getActiveApplication().getOutput().output(MessageFormat.format(CoreProperties.getString("String_MakeDirectorySuccess"), name));
				CommonUtilities.getActiveLBSControl().refresh();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
	}

	public void renameFile(String url, String name, String newName, boolean isDir) {
		try {
			// 重命名文件
			String webFile = url;
			String rootPath = webFile.replace(WebHDFS.defaultURL, "");
			if (!rootPath.endsWith("/")) {
				rootPath += "/";
			}
			if (!webFile.endsWith("/")) {
				webFile += "/";
			}
			String tempName = name;
			name = URLEncoder.encode(name, "UTF-8");
			String path = url.substring(url.lastIndexOf("v1") + 2, url.length());
			String newPath = path + newName;
//			webFile = "http://192.168.20.189:50070/webhdfs/v1/data/database.txt?user.name=root&op=RENAME&destination=/data/database1.txt";
			webFile = String.format("%s%s?user.name=root&op=RENAME&destination=%s", webFile, name, newPath);
			HttpPut requestPut = new HttpPut(webFile);
			HttpResponse response = new DefaultHttpClient().execute(requestPut);
			if (response != null && response.getStatusLine().getStatusCode() == 200) {
				if (isDir) {
					Application.getActiveApplication().getOutput()
							.output(MessageFormat.format(CoreProperties.getString("String_RenameDirSuccess"), tempName, newName));
				} else {
					Application.getActiveApplication().getOutput()


							.output(MessageFormat.format(CoreProperties.getString("String_RenameFileSuccess"), tempName, newName));
				}
				CommonUtilities.getActiveLBSControl().refresh();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
	}
}
