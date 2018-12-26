package com.clubank.util;

import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.clubank.device.BaseActivity;
/**
 * Created by Administrator on 2017/8/1.
 */

public class ToolBarUtils {
    private BaseActivity ba;
    public ToolBarUtils(BaseActivity ba){
        this.ba = ba;
    }

    public  void  changeToolbarColor(int rId){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = ba.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ba.getResources().getColor(rId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public  void  changeToolbarDrow(int rId){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = ba.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setBackgroundDrawable(ba.getResources().getDrawable(rId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setBackgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        activity.getWindow().setAttributes(lp);
    }

}
