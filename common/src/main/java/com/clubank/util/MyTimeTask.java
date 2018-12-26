package com.clubank.util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.clubank.common.R;
import com.clubank.device.BaseActivity;
import com.clubank.device.BaseFragment;
import com.clubank.device.op.OPBase;
import com.clubank.domain.RT;
import com.clubank.domain.Result;
import com.google.gson.Gson;

import org.ksoap2.SoapFault;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.zip.CRC32;

/**
 * 定时器任务，隔一段时间执行某项操作
 *
 * @author zyf
 */
public class MyTimeTask {
    private Timer timer;
    private TimerTask task;
    private long time;

    public MyTimeTask(long time, TimerTask task) {
        this.task = task;
        this.time = time;
        if (timer == null) {
            timer = new Timer();
        }
    }

    public void start() {
        timer.schedule(task, 0, time);//每隔5分钟检测一次是否有新版本  第二个参数标识首次调用无延迟，第三个参数间隔多少时间执行一次
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            if (task != null) {
                task.cancel(); //将原任务从队列中移除 } } }
            }
        }
    }
}