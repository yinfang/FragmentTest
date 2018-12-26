package com.clubank.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by duxy on 2017/12/12.
 */

public class ToastUtile {
    // 构造方法私有化 不允许new对象
    private ToastUtile() {
    }

    // Toast对象
    private static Toast toast = null;

    /**
     * 显示Toast
     */
    public synchronized static void showText(Context context, String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        }
        toast.setText(text);
        toast.show();
    }
}
