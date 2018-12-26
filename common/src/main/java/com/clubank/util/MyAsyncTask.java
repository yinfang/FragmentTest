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
import java.util.concurrent.Executors;
import java.util.zip.CRC32;

/**
 * @author chenyh
 */
public class MyAsyncTask extends AsyncTask<Object, Result, Result> {
    protected Context context;
    protected Dialog wd;
    protected Class<?> op;
    BaseFragment fragment;
    public boolean showWaiting = true, updata = true;//updata是否更新最新数据
    public boolean isshowErrer = true, isCacheData;
    protected ACache mCache;//离线缓存
    protected int isCache = 0;//是否缓存 0不缓存 1缓存
    protected StringBuffer cacheName;//缓存文件名唯一标识(比如接口名称)
    // ，如果不同界面调用同一将接口参照：接口名称+不同类型type（没有就不用）+分页index


    public MyAsyncTask(Context context, Class<?> op) {
        isCache = 0;
        this.context = context;
        this.op = op;
    }

    /**
     * 带缓存的请求
     */
    public MyAsyncTask(Context context, Class<?> op, int isCache) {
        this.isCache = isCache;
        if (isCache == 1) {
            cacheName = new StringBuffer();
            this.mCache = ACache.getInstance(context);
        }

        this.context = context;
        this.op = op;
    }

    /**
     * 带缓存的请求
     */
    public MyAsyncTask(Context context, Class<?> op, int isCache, boolean updata) {
        this.isCache = isCache;
        this.updata = updata;
        if (isCache == 1) {
            cacheName = new StringBuffer();
            this.mCache = ACache.getInstance(context);
        }

        this.context = context;
        this.op = op;
    }

    public MyAsyncTask(Context context, Class<?> op, boolean showWaiting) {

        this(context, op);
        isCache = 0;
        this.showWaiting = showWaiting;
    }


    public MyAsyncTask(Context context, Class<?> op, boolean showWaiting, int isCache, boolean
            updata) {
        this(context, op);
        this.updata = updata;
        this.isCache = isCache;
        if (isCache == 1) {
            cacheName = new StringBuffer();
            this.mCache = ACache.getInstance(context);
        }

        this.showWaiting = showWaiting;
    }

    public MyAsyncTask(Context context, Class<?> op, boolean showWaiting, int isCache) {

        this(context, op);
        this.isCache = isCache;
        if (isCache == 1) {
            cacheName = new StringBuffer();
            this.mCache = ACache.getInstance(context);
        }

        this.showWaiting = showWaiting;
    }

    public MyAsyncTask(Context context, Class<?> op, boolean showWaiting, boolean isshowErrer) {
        this(context, op);
        isCache = 0;
        this.showWaiting = showWaiting;
        this.isshowErrer = isshowErrer;
    }

    public MyAsyncTask(BaseFragment fragment, Class<?> op) {
        this.fragment = fragment;
        this.context = fragment.getActivity();
        isCache = 0;
        this.op = op;
    }

    public MyAsyncTask(BaseFragment fragment, Class<?> op, boolean showWaiting) {
        this(fragment, op);
        this.showWaiting = showWaiting;
        isCache = 0;

    }

    public void onPreExecute() {
        if (showWaiting) {
            if (isCache == 1 && isCacheData()) {//如果缓存并且有缓存数据则不启动等待条
                isshowErrer = false;//缓存数据网络请求失败不提示错误
            } else {
                wd = new WaitingDialog(context);
                wd.setCanceledOnTouchOutside(false);
                wd.show();
            }
        }
    }

    /**
     * 用于外部调用执行的方法，对于3.0以上设备应该使用 executeOnExecutor 否则可能会出现长时间挂起的情况。很重要。
     *
     * @param args
     */
    @SuppressLint("NewApi")
    public void run(Object... args) {
        if (isCache == 1) {//缓存唯一标识接口名称+参数
            cacheName.append(op.getSimpleName());
            if (args.length > 0) {
                CRC32 c = new CRC32();
                for (int i = 0; i < args.length; i++) {
                    c.update(new Gson().toJson(args[i]).getBytes());
                    cacheName.append(c.getValue());
                }
            }


        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                isCacheData = isCacheData();
                if (isCache == 1 && isCacheData) {//从缓存中取数据

                    final Result result = new Result();
                    if (mCache.getAsString(cacheName.toString() + "type").equals("MyData")) {
                        MyData data = JsonUtil.getMyData(mCache.getAsString(cacheName.toString()));
                        result.obj = data;//MyData直接存返回数据没值所以使用json
                    } else {
                        MyRow data = JsonUtil.getRow(mCache.getAsString(cacheName.toString()));
                        result.obj = data;//MyData直接存返回数据没值所以使用json
                    }

                    result.code = Integer.parseInt(mCache.getAsString(cacheName.toString() +
                            "code"));
                    ((BaseActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((BaseActivity) context).onPostExecute(op, result);
                        }
                    });


                }
            }
        }).start();


        if (isCache == 1 && !TextUtils.isEmpty(mCache.getAsString(cacheName.toString())) &&
                !updata) {//如果有缓存并且undatafalse则不更新数据
            return;
        }

        if (Utils.hasHoneycomb()) {
            executeOnExecutor(Executors.newCachedThreadPool(), args);
        } else {
            execute(args);
        }


    }

    public Result doInBackground(Object... args) {

        Result result = new Result();

        if (!U.isNetworkConnected(context)) {
            result.code = RT.SOCKET_ERROR;
            return result;
        }

        try {
            OPBase opbase = (OPBase) op.getConstructor(Context.class)
                    .newInstance(context);
            result = opbase.execute(args);
            // publishProgress((int) ((count / (float) length) * 100));
        } catch (Exception e) {
            Throwable t = e.getCause();
            if (t == null) {
                t = e;
            }
            // String err = t.getMessage();
            if (e instanceof UnknownHostException
                    || t instanceof SocketException
                    || t instanceof ConnectException
                    || t instanceof XmlPullParserException) {
                result.code = RT.SOCKET_ERROR;
            } else if (t instanceof SocketTimeoutException) {
                result.code = RT.SOCKET_TIMEOUT;
            } else if (t instanceof SoapFault) {
                String msg = t.getMessage();
                if (msg.indexOf("offline") > 0
                        || msg.indexOf("timeout: ClientId=") > 0) {
                    result.code = RT.CLUB_OFFLINE;
                } else if (msg.indexOf("SOAPAction") > 0) {
                    result.code = RT.INTERFACE_ERROR;
                } else {
                    result.code = RT.SERVER_ERROR;
                    result.obj = t.getMessage();
                }
                System.out.println(t.getMessage());
            } else if (t instanceof IOException) {
                result.code = RT.SERVER_ERROR;
                result.obj = t.getMessage();
            } else {
                result.code = RT.UNKNOWN_ERROR;
                result.obj = t.getMessage();
            }
            result.obj = t.toString();
            Log.e(t.getMessage(), "");
            if (U.isDebug(context)) {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                t.printStackTrace(new PrintStream(b));
                result.obj = op + "\n" + new String(b.toByteArray());
            }
            t.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Result result) {
        try {
            if (null != wd) {
                if (showWaiting && wd.isShowing()) {
                    wd.dismiss();
                }
            }
            if (isCacheData) {//如果有缓存后台请求接口不提示错误
                isshowErrer = false;
            }
            if (isshowErrer) {
                if (result.code == RT.OPERATION_FAILED) {
                    if (result.obj != null) {
                        UI.showToast(context, (String) result.obj, Toast.LENGTH_SHORT);
                    }
                } else if (result.code == RT.SOCKET_ERROR) {
                    result.msg = context.getString(R.string.network_problem);
                    UI.showToast(context, R.string.network_problem);

                } else if (result.code == RT.ILLEGAL_ACCESS) {
                    result.msg = context.getString(R.string.illegal_access);
                    UI.showToast(context, R.string.illegal_access);
                } else if (result.code == RT.OPERATION_FAILED) {
                    result.msg = context.getString(R.string.operation_failed);
                    UI.showToast(context, R.string.operation_failed);
                } else if (result.code == RT.INTERFACE_ERROR) {
                    result.msg = context.getString(R.string.interface_error);
                    UI.showToast(context, R.string.interface_error);
                } else if (result.code == RT.SOCKET_TIMEOUT) {
                    result.msg = context.getString(R.string.network_timeout);
                    UI.showToast(context, R.string.network_timeout);
                } else if (result.code == RT.SERVER_ERROR) {
                    if (isshowErrer) {
                        result.msg = context.getString(R.string.server_error);
                        UI.showToast(context, R.string.server_error);
                    }
                } else if (result.code == RT.UNKNOWN_ERROR) {
                    String msg = context.getString(R.string.unexpected_error);
                    if (result.obj != null) {
                        msg += result.obj;
                    }
                    UI.showToast(context, R.string.server_error);

                    Log.e("op=" + op.getSimpleName(), msg);
                }
            }
            if (isCache == 1 && result.code == RT.SUCCESS) {
                String dataStr = new Gson().toJson(result.obj);
                mCache.put(cacheName.toString(), dataStr);
                mCache.put(cacheName.toString() + "code", result.code + "");//10分钟

                String datatype = "MyData";
                try {
                    MyData obj = (MyData) result.obj;
                } catch (Exception e) {
                    datatype = "MyRow";
                }
                mCache.put(cacheName.toString() + "type", datatype);//数据类型
            }
            if (isCacheData) {//如果有缓存不掉onpost
                return;
            }
            if (fragment != null) {
                fragment.onPostExecute(op, result);
            } else if (context instanceof BaseActivity) {
                ((BaseActivity) context).onPostExecute(op, result);
            }
            // }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(e.getMessage(), "");
        }
    }

    @Override
    protected void onProgressUpdate(Result... values) {

    }

    /**
     * 是否有缓存数据
     *
     * @return
     */
    public boolean isCacheData() {
        boolean isCacheData = false;
        if (null != cacheName && mCache != null) {
            isCacheData = !TextUtils.isEmpty(mCache.getAsString(cacheName.toString()));
        }

        return isCacheData;
    }
}