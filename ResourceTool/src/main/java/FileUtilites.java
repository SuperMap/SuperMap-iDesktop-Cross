package main.java;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by lixiaoyao on 2017/11/9.
 */

public class FileUtilites {

    // 输出内容数组到文件
    public static void outPutToFile(ArrayList<String> content, String pathName) throws IOException {
        File file = new File(pathName);
        if (!file.exists()) {
            file.createNewFile();
        }
        OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        BufferedWriter writer = new BufferedWriter(write);
        for (int i = 0; i < content.size(); i++) {
            writer.write(content.get(i));
            writer.write("\r\n");
        }
        writer.close();
    }

    public static void outPutToFile(ArrayList<String> content, File file) throws IOException {
        OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        BufferedWriter writer = new BufferedWriter(write);
        for (int i = 0; i < content.size(); i++) {
            writer.write(content.get(i));
            writer.write("\r\n");
        }
        writer.close();
    }

    public static void IOCopy(String sourcePath, String targetPath) {
        File file = new File(sourcePath);
        File file1 = new File(targetPath);
        if (!file.exists()) {
            System.out.println(file.getName() + ResourceToolProperties.getString("String_FileNotExists"));
        }
        byte[] b = new byte[(int) file.length()];
        if (file.isFile()) {
            try {
                FileInputStream is = new FileInputStream(file);
                FileOutputStream ps = new FileOutputStream(file1);
                is.read(b);
                ps.write(b);
                is.close();
                ps.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (file.isDirectory()) {
            if (!file.exists())
                file.mkdir();
            String[] list = file.list();
            for (int i = 0; i < list.length; i++) {
                IOCopy(sourcePath + "/" + list[i], targetPath + "/" + list[i]);
            }
        }
    }

    public static ArrayList<String> readFileContent(File file) {
        ArrayList<String> result = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));//构造一个BufferedReader类来读取文件
            String s = "";
            while ((s = br.readLine()) != null) {
                result.add(s);
                s = "";
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    // 创建目录
    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {// 判断目录是否存在
            System.out.println(ResourceToolProperties.getString("String_CreateDirectoryFailedForIsExists"));
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {// 结尾是否以"/"结束
            destDirName = destDirName + File.separator;
        }
        if (dir.mkdirs()) {// 创建目标目录
            System.out.println(ResourceToolProperties.getString("String_CreateDirectorySuccess") + destDirName);
            return true;
        } else {
            System.out.println(ResourceToolProperties.getString("String_CreateDirectoryFailed"));
            return false;
        }
    }
}
