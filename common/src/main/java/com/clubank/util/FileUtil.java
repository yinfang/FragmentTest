package com.clubank.util;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by long on 17-6-9.
 */

public class FileUtil {


//    private void initData() {
//        String filePath = "/sdcard/Test/";
//        String fileName = "log.txt";
//
//        writeTxtToFile("txt content", filePath, fileName);
//    }

    // 将字符串写入到文本文件中
    public static void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("FileCreate", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            Log.d("FileCreate", "File Has Created:" + strFilePath);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(strContent.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            Log.d("FileCreate", "writeTxtToFile: " + file.length());
//            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
//            raf.seek(0);
//            raf.write(strContent.getBytes());
//            raf.close();
        } catch (Exception e) {
            Log.e("FileCreate", "Error on write File:" + e);
        }
    }

    // 生成文件
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("FileError:", e + "");
        }
    }

    public static MyData getDirFilesContent(String rootFilePath) {
        MyData data = new MyData();
        if (TextUtils.isEmpty(rootFilePath)) {
            return data;
        }
        File file = new File(rootFilePath);
        if (file.isDirectory()) {

            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    MyRow row = new MyRow();
                    File reFile = files[i];
                    row.put(reFile.getName(),readFile(reFile));
                    data.add(row);
                }
                return data;
            } else {
                return data;
            }
        } else {
            return data;
        }
    }

    public static String readFile(File file) {
        BufferedReader reader = null;
        try {
//            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            StringBuilder builder = new StringBuilder();
//            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString =reader.readLine())!= null) {
                builder.append(tempString);
            }
            reader.close();
            return  builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
