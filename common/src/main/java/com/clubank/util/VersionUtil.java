package com.clubank.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class VersionUtil {

       /**
     * 获取版本号
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }
    /**
     * 获取包名
     * @param context
     * @return
     */
    public static String getPackageName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), 0);
            return info.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 用服务器版本号和本地版本号对比
     * @param versionCode
     * @return
     */
    public static boolean shouldUpdate(Context context,int versionCode){
        int localVersionCode = getVersionCode(context);
        if (localVersionCode == -1){
            return false;
        }
        if (versionCode > localVersionCode){
            return true;
        }else {
            return false;
        }
    }


}
