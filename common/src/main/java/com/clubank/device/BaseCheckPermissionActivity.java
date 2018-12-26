package com.clubank.device;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 检查相关权限基类，适用于android 6.0 运行时权限
 * 使用方法：需要运行时权限的activity继承此类
 * 1.在Androidmanifest.xml文件声明相关权限
 * 2.通过ContextCompat.checkSelfPermission方法检查某项权限被授予情况
 * 3.申请授权
 * 4.权限回调处理
 */
public abstract class BaseCheckPermissionActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int REQUEST_CODE_PERMISSON = 2020; //权限请求码
    public static boolean isNeedCheckPermission = true; //判断是否需要检测，防止无限弹框申请权限

    /*  目前只检查和申请指定权限，需要检查全部权限时放开，子类不需要任何处理
     @Override
    protected void onResume() {
        super.onResume();
            checkAllNeedPermissions();
    }
*/

    /**
     * 检查所有权限，无权限则开始申请相关权限
     */
    protected void checkAllNeedPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//6.0以下版本回调后单独判断
            permissionGrantedSuccess();
        } else {
            /*// 检查和申请所有需要的权限
            List<String> needRequestPermissonList = getDeniedPermissions(getNeedPermissions());
            if (null != needRequestPermissonList && needRequestPermissonList.size() > 0) {
                   ActivityCompat.requestPermissions( this,needRequestPermissonList.toArray(
                        new String[needRequestPermissonList.size()]), REQUEST_CODE_PERMISSON);
            }*/

            //目前只申请指定权限
            if (getNeedPermissions().length > 0) {
                ActivityCompat.requestPermissions(this, getNeedPermissions(), REQUEST_CODE_PERMISSON);
            }
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     */
    private List<String> getDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                needRequestPermissonList.add(permission);
            }
        }
        return needRequestPermissonList;
    }

    /**
     * 所有权限是否都已授权
     *
     * @return
     */
    protected boolean isGrantedAllPermission() {
        List<String> needRequestPermissonList = getDeniedPermissions(getNeedPermissions());
        return needRequestPermissonList.size() == 0;
    }

    /**
     * 权限授权结果回调
     *
     * @param requestCode
     * @param permissions
     * @param paramArrayOfInt
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] paramArrayOfInt) {
        super.onRequestPermissionsResult(requestCode, permissions, paramArrayOfInt);
        if (requestCode == REQUEST_CODE_PERMISSON) {
            if (!verifyPermissions(paramArrayOfInt)) {
                permissionGrantedFail();
                showTipsDialog();
                isNeedCheckPermission = false;
            } else {
                permissionGrantedSuccess();
            }
        }
    }

    /**
     * 检测所有的权限是否都已授权
     *
     * @param grantResults
     * @return
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 显示提示对话框
     */
    protected void showTipsDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("提示信息").setMessage("当前应用缺少" + getDialogTipsPart()
                + "权限，请单击【确定】按钮前往设置中心进行权限授权，否则无法正常使用本应用！")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                        dialog.dismiss();
                    }
                }).show();
    }


    /**
     * 启动当前应用设置页面
     */
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    /**
     * 获取弹框提示部分内容
     *
     * @return
     */
    protected String getDialogTipsPart() {
        return "必要";
    }

    /**
     * 获取需要进行检测的权限数组，用于子类重写指定需要检查的权限
     */
    protected abstract String[] getNeedPermissions();

    /**
     * 权限授权成功，用于子类回调
     */
    protected abstract void permissionGrantedSuccess();

    /**
     * 权限授权失败，用于子类回调
     */
    protected abstract void permissionGrantedFail();
}