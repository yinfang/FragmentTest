package com.clubank.device;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.clubank.common.R;
import com.clubank.device.op.ViewVersion;
import com.clubank.domain.BRT;
import com.clubank.domain.C;
import com.clubank.domain.ContactInfo;
import com.clubank.domain.Criteria;
import com.clubank.domain.ListObject;
import com.clubank.domain.Result;
import com.clubank.util.ACache;
import com.clubank.util.CustomDialog;
import com.clubank.util.CustomDialog.Initializer;
import com.clubank.util.CustomDialog.OKProcessor;
import com.clubank.util.DownloadFile;
import com.clubank.util.GlideCacheUtil;
import com.clubank.util.GlideUtil;
import com.clubank.util.ListDialog;
import com.clubank.util.LocationUtil;
import com.clubank.util.MListDialog;
import com.clubank.util.MListDialog.OnNeutralListener;
import com.clubank.util.MyAsyncTask;
import com.clubank.util.MyData;
import com.clubank.util.MyDateDialog;
import com.clubank.util.MyRow;
import com.clubank.util.MyTimeDialog;
import com.clubank.util.MyTimeTask;
import com.clubank.util.ToolBarUtils;
import com.clubank.util.U;
import com.clubank.util.UI;
import com.clubank.util.VersionUtil;
import com.clubank.view.AlertDialog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;


@SuppressLint({"InflateParams", "Registered", "NewApi"})
public class BaseActivity extends BaseCheckPermissionActivity {
    protected final String TAG = getClass().getSimpleName();
    public SharedPreferences settings;
    public MyApplication app;
    private MyTimeTask task;
//    private MyAmapListener amapListener;

    /**
     * 检查登陆的类别以便回调时区分
     */
    protected DisplayMetrics dm = new DisplayMetrics();
    /**
     * 屏幕较短边的像素
     */
    protected int smallerScreenSize;
    protected String[] menus;
    protected int[] menuImages;

    /**
     * 触发处理图片的控件
     */
    protected View getPictureView;
    /**
     * 要填充的图片控件
     */
    public Display screen;
    protected ImageView iv;
    protected LinearLayout lin;
    private OnClickListener clickListener;

    private Calendar ca = Calendar.getInstance();
    private GlideUtil glideUtil = GlideUtil.getInstance();
    protected boolean animator = true;
    private LocationUtil locationUtil;


    protected boolean hasGet304Status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ToolBarUtils utils = new ToolBarUtils(this);
        app = (MyApplication) getApplication();
        settings = getSharedPreferences(C.APP_ID, MODE_PRIVATE);
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        screen = wm.getDefaultDisplay();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        smallerScreenSize = dm.widthPixels;
        if (dm.heightPixels < dm.widthPixels) {
            smallerScreenSize = dm.heightPixels;
        }
        clickListener = new MyOnClickListener();
        app.addActivity(this);
    }

    protected void onStart() {
        super.onStart();
    }

    public boolean checkNull(String s) {
        if (null == s || s.length() < 1 || s.equals("null") || TextUtils.isEmpty(s) || s.equals("NULL") || s.equals("")) {
            return true;
        }
        return false;
    }

    private void setOnClickListener(String res) {
        int resId = UI.getId(this, res, "id");
        if (resId > 0) {
            View v = findViewById(resId);
            if (v != null) {
                v.setOnClickListener(clickListener);
            }
        }
    }

    public void openIntent(Class<?> clazz, CharSequence title) {
        openIntent(clazz, title, null);
    }

    public void openIntent(Class<?> clazz, CharSequence title, Bundle extras) {
        openIntent(clazz, title, extras, 0);
    }

    public void openIntent(Class<?> clazz, CharSequence title, Bundle extras,
                           int requestCode) {
        Intent intent = new Intent(this, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (!animator) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }
        if (extras != null) {
            intent.putExtras(extras);
        }
        intent.putExtra("title", title.toString().replaceAll("\n", " "));
        if (requestCode > 0) {
            startActivityForResult(intent, requestCode);
        } else {
            startActivity(intent);
        }
        if (animator) {
            overridePendingTransition(R.anim.forward_enter, R.anim.forward_exit);
        }
    }

    public void openIntent(Class<?> clazz, int resId) {
        openIntent(clazz, getText(resId), null);
    }

    public void openIntent(Class<?> clazz, int resId, int requestCode) {
        openIntent(clazz, getText(resId), null, requestCode);
    }

    public void openIntent(Class<?> clazz, int resId, Bundle extras,
                           int requestCode) {
        openIntent(clazz, getText(resId), extras, requestCode);
    }

    /**
     * 打开新Activity
     *
     * @param clazz Activity
     */
    public void openIntent(Class<?> clazz, int resId, Bundle b) {
        openIntent(clazz, getText(resId), b);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setOnClickListener("menu");
        setOnClickListener("ic_home");
        setOnClickListener("header_back");
        setOnClickListener("basebackhome");
        Bundle bundle = getIntent().getExtras();
        String title = null;
        if (bundle != null) {
            title = bundle.getString("title");
        }

        if (title != null) {
            setHeaderTitle(title);
        }
        restoreVars();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();
        app.removeActivity(this);
        if (null != task) {
            task.stop();
        }
    }

    protected void setHeaderTitle(CharSequence title) {
        View tv = findViewById(R.id.header_title);
        if (tv != null && title != null && tv instanceof TextView) {
            ((TextView) tv).setText(title);
        }
    }

    protected void setHeaderTitle(int resId) {
        setHeaderTitle(getText(resId));
    }

    public String getEText(int resId) {
        return UI.getEText(this, resId);
    }

    public void setEColor(int resId, int color) {
        TextView tv = (TextView) findViewById(resId);
        if (tv != null)
            tv.setTextColor(getResources().getColor(color));
    }

    public void setVColor(int resId, int color) {
        View v = (View) findViewById(resId);
        if (v != null)
            v.setBackgroundColor(getResources().getColor(color));
    }

    public void setEColor(View view, int resId, int color) {
        TextView tv = (TextView) view.findViewById(resId);
        if (tv != null)
            tv.setTextColor(getResources().getColor(color));
    }

    public void setEText(int resId, CharSequence value) {
        UI.setEText(this, resId, value);
    }

    public void setEText(int resId, int resValue) {
        UI.setEText(this, resId, resValue);
    }


    public void setImage(ImageView iv, Object model) {
        glideUtil.setImage(this, iv, model);
    }

    /**
     * 加载图片的字节数组
     *
     * @param model string可以为一个文件路径、uri或者url
     *              uri类型
     *              文件
     *              资源Id,R.drawable.xxx或者R.mipmap.xxx
     *              byte[]类型
     */
    public void setImage(int ImageViewId, Object model) {
        glideUtil.setImage(this, ImageViewId, model);
    }


    public void setImage(int ImageViewId, Object model, boolean isCircle) {
        glideUtil.setImage(this, ImageViewId, model, isCircle);
    }


    public void setImage(int ImageViewId, Object model, int loadingImage) {
        glideUtil.setImage(this, ImageViewId, model, loadingImage);
    }

    /**
     * 调出打电话界面
     *
     * @param phoneNo 要打的电话
     */
    public void call(String phoneNo) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                + phoneNo));
        startActivity(intent);
    }

    /**
     * 返回上一页前检查是否确实要返回
     *
     * @return
     */
    protected boolean validate() {
        return true;
    }

    public void logout() {

        exit(false);
    }

    public void saveSetting(String name, boolean value) {
        Editor editor = getSharedPreferences(C.APP_ID, MODE_PRIVATE).edit();
        editor.putBoolean(name, value);
        editor.commit();
    }

    public void saveSetting(String name, String value) {
        Editor editor = getSharedPreferences(C.APP_ID, MODE_PRIVATE).edit();
        editor.putString(name, value);
        editor.commit();
    }

    public void saveSetting(String name, int value) {
        Editor editor = getSharedPreferences(C.APP_ID, MODE_PRIVATE).edit();
        editor.putInt(name, value);
        editor.commit();
    }

    public void removeSetting(String name) {
        Editor editor = getSharedPreferences(C.APP_ID, MODE_PRIVATE).edit();
        editor.remove(name);
        editor.commit();
    }

    /**
     * 多选对话框点击了一个通用按钮
     *
     * @param view
     * @param selected
     */

    protected void neutralSelected(View view, boolean[] selected) {
        for (int i = 0; i < selected.length; i++) {
            selected[i] = false;
        }
    }

    /**
     * 显示普通列表窗口，子类覆盖回调方法listSelected
     *
     * @param view     触发控件
     * @param captions 列表文本
     */
    public void showListDialog(View view, String[] captions) {
        showListDialog(view, 0, captions, null);
    }

    /**
     * 显示普通列表窗口，子类覆盖回调方法listSelected
     *
     * @param view     触发控件
     * @param resTitle 列表的显示标题
     * @param resArray 资源内定义的字符串数组
     */
    public void showListDialog(View view, int resTitle, int resArray) {
        showListDialog(view, resTitle, getResources().getStringArray(resArray),
                null);
    }

    /**
     * 显示普通列表窗口，子类覆盖回调方法listSelected
     *
     * @param view     触发控件
     * @param captions 列表文本
     * @param images   列表图标, 数量必须与captions保持一致
     */
    public void showListDialog(View view, String[] captions, int[] images) {
        showListDialog(view, 0, captions, images);
    }

    /**
     * 显示普通列表窗口，子类覆盖回调方法listSelected
     *
     * @param view       触发控件
     * @param resTitleId 显示标题栏文字，0 不显示标题栏
     * @param captions   列表文本
     * @param images     列表图标, 数量必须与captions保持一致
     */
    protected void showListDialog(View view, int resTitleId, String[] captions,
                                  int[] images) {
        ListDialog dialog = new ListDialog(this);
        dialog.setOnSelectedListener(new ListDialog.OnSelectedListener() {
            public void onSelected(View view, int index) {
                listSelected(view, index);
            }
        });
        dialog.show(view, resTitleId, captions, images);
    }

    /**
     * 显示普通列表窗口，子类覆盖回调方法listSelected
     *
     * @param view       触发控件
     * @param resTitleId 标题文字，0 不显示标题
     * @param captions   列表文本
     */
    protected void showListDialog(View view, int resTitleId, String[] captions) {
        showListDialog(view, resTitleId, captions, null);
    }

    /**
     * 弹出多选列表
     *
     * @param view     触发控件
     * @param captions 列表文本
     * @param selected 初始值，长度必须与captions相同
     */
    public void showMListDialog(final View view, String[] captions,
                                boolean[] selected) {
        showMListDialog(view, 0, captions, selected);
    }

    /**
     * 弹出多选列表
     *
     * @param view       触发控件
     * @param resTitleId 标题文字，0 不显示标题
     * @param captions   列表文本
     * @param selected   初始值，长度必须与captions相同
     */
    protected void showMListDialog(final View view, int resTitleId,
                                   String[] captions, boolean[] selected) {
        showMListDialog(view, resTitleId, captions, selected, 0);
    }

    /**
     * 弹出多选列表
     *
     * @param view       触发控件
     * @param resTitleId 标题文字，0 不显示标题
     * @param captions   列表文本
     * @param selected   初始值，长度必须与captions相同
     * @param thirdBtn   自定义按钮文字资源， 0 - 无自定义按钮
     */
    protected void showMListDialog(final View view, int resTitleId,
                                   String[] captions, boolean[] selected, int thirdBtn) {
        final MListDialog dialog = new MListDialog(this);
        dialog.setOnSelectedListener(new MListDialog.OnPositiveListener() {
            public void onSelected(View view, boolean[] selected) {
                mlistSelected(view, selected);
            }
        });
        if (thirdBtn > 0) {
            dialog.setOnNeutralListener(new OnNeutralListener() {
                public void onSelected(View view, boolean[] selected) {
                    neutralSelected(view, selected);
                }
            });
        }
        dialog.show(view, resTitleId, captions, selected, thirdBtn);
    }

    /**
     * 弹出Popup菜单，位置就在控件附近
     *
     * @param view  触发控件
     * @param pos   第几个菜单，1 开始
     * @param total 屏幕分为几列菜单，决定菜单宽度，3表示菜单宽度为屏幕的1/3
     * @param menus 菜单文本内容
     */
    public void showMenu(final View view, int pos, int total, String[] menus) {
        showMenu(view, pos, total, menus, new int[menus.length]);
    }

    /**
     * 弹出Popup菜单，位置就在控件附近
     *
     * @param view   触发控件
     * @param pos    第几个菜单，1 开始
     * @param total  屏幕分为几列菜单，决定菜单宽度，3表示菜单宽度为屏幕的1/3
     * @param menus  菜单文本内容
     * @param images 菜单前面显示的图标资源
     */
    @SuppressWarnings("deprecation")
    public void showMenu(final View view, int pos, int total, String[] menus,
                         int[] images) {
        if (menus == null || menus.length == 0) {
            /* 文本内容为空，忽略 */
            return;
        }
        View v = getLayoutInflater().inflate(R.layout.popwindow_light, null);
        ListView listView1 = (ListView) v.findViewById(R.id.PopWindow_lv);
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();// deprecated in API 13, ignore it.
        int height = display.getHeight() / 2;

        final PopupWindow popupWindow = new PopupWindow(v, width / total,
                height);

        /* 加上这行可以确保点击外面后让弹出窗口消失 */
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        popupWindow.setFocusable(true);

        MyData data = new MyData();
        for (int i = 0; i < menus.length; i++) {
            MyRow row = new MyRow();
            if (images != null) {
                row.put("imageResId", images[i]);
            }
            row.put("name", menus[i]);
            data.add(row);
        }
//        MenuAdapter adapter = new MenuAdapter(this, data);
//        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                menuSelected(view, arg2);
                popupWindow.dismiss();
            }
        });
        popupWindow.showAsDropDown((View) view.getParent(), (width / total)
                * (pos - 1), 0);
    }

    @SuppressWarnings("deprecation")
    public void showTipsMenu(final View view, double factor, String[] menus,
                             int[] images) {
        if (menus == null || menus.length == 0) {
            /* 文本内容为空，忽略 */
            return;
        }
        View v = getLayoutInflater().inflate(R.layout.popwindow_light, null);
        ListView listView1 = (ListView) v.findViewById(R.id.PopWindow_lv);
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();// deprecated in API 13, ignore it.
        int height = display.getHeight() / 2;

        final PopupWindow popupWindow = new PopupWindow(v,
                (int) (width * factor), height);

        /* 加上这行可以确保点击外面后让弹出窗口消失 */
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        popupWindow.setFocusable(true);

        MyData data = new MyData();
        for (int i = 0; i < menus.length; i++) {
            MyRow row = new MyRow();
            if (images != null) {
                row.put("imageResId", images[i]);
            }
            row.put("name", menus[i]);
            data.add(row);
        }
//        MenuAdapter adapter = new MenuAdapter(this, data);
//        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                menuSelected(view, arg2);
                popupWindow.dismiss();
            }
        });
        popupWindow.showAsDropDown((View) view.getParent(), 0, 0);
    }

    /**
     * 弹出日期选择框,子类需要实现回调方法 dateSet
     *
     * @param view 日期控件，必须是TextView /Button/EditText 等，格式2014-12-30
     */
    protected void showDateDialog(final View view, int year, int month, int day,
                                  boolean showday) {
        MyDateDialog d = new MyDateDialog(this);
        d.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            boolean fired;

            public void onDateSet(DatePicker v, int year, int month, int day) {
                if (!fired) {
                    if (view instanceof TextView) {
                        String date = U.getDateString(year, month, day);
                        ((TextView) view).setText(date);
                    }
                    dateSet(view, year, month, day);// 回调子类方法
                }
                fired = true;
            }
        });
        d.show(year, month, day, showday);
    }

    protected void showDateDialog(final View view, int year, int month, int day,
                                  long minDate,
                                  long maxDate, boolean showDay) {

        MyDateDialog dialog = new MyDateDialog(this);
        dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            boolean fired;

            @Override
            public void onDateSet(DatePicker v, int year, int month, int dayOfMonth) {
                if (!fired) {
                    if (view instanceof TextView) {
                        String date = U.getDateString(year, month, dayOfMonth);
                        ((TextView) view).setText(date);
                    }
                    dateSet(view, year, month, dayOfMonth);// 回调子类方法
                }
                fired = true;
            }
        });

        dialog.show(year, month, day, minDate, maxDate, showDay);
    }


    /**
     * 填出日期选择框，子类需要实现回调方法 dateSet
     *
     * @param view 日期控件，必须是TextView /Button/EditText 等，格式2014-12-30
     * @param date 显示已有日期，格式 yyyy-MM-dd
     */
    public void showDateDialog(View view, String date) {
        String[] sdate = date.toString().split("-");
        int year = Integer.parseInt(sdate[0]);
        int month = Integer.parseInt(sdate[1]) - 1;
        int day;
        if (sdate.length > 2) {
            day = Integer.parseInt(sdate[2]);
        } else {
            day = 0;
        }
        showDateDialog(view, year, month, day, true);
    }


    public void showDateDialog(View view, String date, long minDate, long maxDate) {
        String[] sdate = date.toString().split("-");
        int year = Integer.parseInt(sdate[0]);
        int month = Integer.parseInt(sdate[1]) - 1;
        int day;
        if (sdate.length > 2) {
            day = Integer.parseInt(sdate[2]);
        } else {
            day = 0;
        }
        showDateDialog(view, year, month, day, minDate, maxDate, true);
    }

    /**
     * 只展示年月的日期控件
     * <p/>
     * 填出日期选择框，子类需要实现回调方法 dateSet
     *
     * @param view 日期控件，必须是TextView /Button/EditText 等，格式2014-12-30
     * @param date 显示已有日期，格式 yyyy-MM-dd
     */
    public void showYearsDialog(View view, String date) {
        String[] sdate = date.split("-");
        int year = Integer.parseInt(sdate[0]);
        int month = Integer.parseInt(sdate[1]) - 1;
        int day;
        if (sdate.length > 2) {
            day = Integer.parseInt(sdate[2]);
        } else {
            day = ca.get(Calendar.DAY_OF_MONTH);
        }
        showDateDialog(view, year, month, day, false);
    }

    /**
     * @param view 日期控件，必须是TextView /Button/EditText 等，格式2014-12-30
     */
    public void showDateDialog(View view) {
        int day;
        String[] sdate = ((TextView) view).getText().toString().split("-");
        int year = Integer.parseInt(sdate[0]);
        int month = Integer.parseInt(sdate[1]) - 1;
        if (sdate.length > 2) {
            day = Integer.parseInt(sdate[2]);
        } else {
            day = 0;
        }
        showDateDialog(view, year, month, day, true);
    }

    /**
     * 显示年或月控件
     *
     * @param view 日期控件，必须是TextView /Button/EditText 等，格式2014-12-30
     */
    public void showYearOrMonthDialog(View view, String date) {

        if (date.length() > 2) {//显示年
            int month = ca.get(Calendar.MONTH);
            int day = ca.get(Calendar.DAY_OF_MONTH);

            int year = Integer.parseInt(date) + 1;
            showDateDialog(view, year, month, day, true, false, false);
        } else {//显示月
            int year = ca.get(Calendar.YEAR);
            int day = ca.get(Calendar.DAY_OF_MONTH);

            int month = Integer.parseInt(date) - 1;
            showDateDialog(view, year, month, day, false, true, false);
        }

    }

    /**
     * 弹出年或月选择框,子类需要实现回调方法 dateSet
     *
     * @param view  日期控件，必须是TextView /Button/EditText 等，格式2014-12
     * @param year
     * @param month
     * @param day
     */
    protected void showDateDialog(final View view, int year, int month, int day,
                                  boolean
                                          showYear, boolean showMonth,
                                  boolean showday) {
        MyDateDialog d = new MyDateDialog(this);
        d.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            boolean fired;

            public void onDateSet(DatePicker v, int year, int month, int day) {
                if (!fired) {
                    if (view instanceof TextView) {
                        String date = U.getDateString(year, month, day);
                        ((TextView) view).setText(date);
                    }
                    dateSet(view, year, month, day);// 回调子类方法
                }
                fired = true;
            }
        });
        d.show(year, month, day, showYear, showMonth, showday);
    }

    /**
     * 显示年月控件
     *
     * @param view 日期控件，必须是TextView /Button/EditText 等，格式2014-12-30
     */
    public void showYearsDialog(View view) {
        int day;
        String[] sdate = ((TextView) view).getText().toString().split("-");
        int year = Integer.parseInt(sdate[0]);
        int month = Integer.parseInt(sdate[1]);
        if (sdate.length > 2) {
            day = Integer.parseInt(sdate[2]);
        } else {
            day = 0;
        }
        showDateDialog(view, year, month, day, false);
    }


    /**
     * 弹出世间选择框，子类需要实现回调方法 timeSet
     *
     * @param view   时间控件，必须是TextView /Button/EditText 等
     * @param hour
     * @param minute
     */
    protected void showTimeDialog(final View view, int hour, int minute) {
        MyTimeDialog d = new MyTimeDialog(this);
        d.setOnTimeSetListener(new OnTimeSetListener() {
            public void onTimeSet(TimePicker v, int hour, int minute) {
                if (view instanceof TextView) {
                    String time = U.getTimeString(hour, minute);
                    ((TextView) view).setText(time);
                }
                timeSet(view, hour, minute);// 回调子类方法
            }
        });
        d.show(hour, minute);
    }

    /**
     * 弹出时间选择框，子类需要实现回调方法 timeSet
     *
     * @param view 时间控件，必须是TextView /Button/EditText 等
     * @param time 格式 19:30
     */
    public void showTimeDialog(View view, String time) {
        String[] sdate = time.split(":");
        int hour = Integer.parseInt(sdate[0]);
        int minute = Integer.parseInt(sdate[1]);
        showTimeDialog(view, hour, minute);
    }

    /**
     * 弹出时间选择框，子类需要实现回调方法 timeSet
     *
     * @param view 时间控件，必须是TextView /Button/EditText 等
     */
    public void showTimeDialog(View view) {
        String[] sdate = ((TextView) view).getText().toString().split(":");
        int hour = Integer.parseInt(sdate[0]);
        int minute = Integer.parseInt(sdate[1]);
        showTimeDialog(view, hour, minute);
    }

    /**
     * 清除参数设置，
     */
    protected void clearSetting() {
        Editor editor = getSharedPreferences(C.APP_ID, MODE_PRIVATE).edit();
        editor.remove("SessionID");
        // editor.remove("rememberPassword");// 删除记住密码
        editor.commit();
    }

    /**
     * 通用弹出窗口点OK按钮后的回调方法
     *
     * @param type 标识，如果有多个地方触发，用于区分触发的是哪次。
     * @param tag  弹出窗口附加的自定义对象
     */
    public void processDialogOK(int type, Object tag) {
        if (type == C.DLG_ILLEGAL_ACCESS) {
        } else if (type == C.DLG_DOWNLOAD_NEW_VERSION) {
            new DownloadFile(this).execute((String) tag);
        }
    }

    /**
     * 通用弹出窗口点取消按钮后的回调方法
     *
     * @param type 标识，如果有多个地方触发，用于区分触发的是哪次。
     * @param tag  弹出窗口附加的自定义对象
     */
    public void processDialogCancel(int type, Object tag) {
    }

    /**
     * 刷新数据的方法，子类可覆盖
     */
    public void refreshData() {
    }

    /**
     * 日期设置回调方法，子类中覆盖此方法
     *
     * @param view  控件标识，如果有多个日期时用这个标识区分。
     * @param year
     * @param month
     * @param day
     */
    public void dateSet(View view, int year, int month, int day) {
    }

    /**
     * 弹出菜单选择事件，子类中覆盖此方法
     *
     * @param view  弹出菜单窗口的控件
     * @param index 选择的菜单项位置
     */
    public void menuSelected(View view, int index) {
    }

    /**
     * 事件设置回调方法，子类中覆盖此方法
     *
     * @param hour
     * @param minute
     */
    public void timeSet(View view, int hour, int minute) {
    }

    /**
     * 列表选择的回调方法，子类覆盖此方法。
     *
     * @param view
     * @param index
     */
    public void listSelected(View view, int index) {
    }

    /**
     * 多选列表回调方法，子类覆盖此方法
     *
     * @param view     触发控件
     * @param selected 选择后的值，长度与文本数组长度相同
     */
    public void mlistSelected(View view, boolean[] selected) {
    }

    /**
     * 检查登陆回调方法，对于需要检查登陆的界面，必须实现此方法
     *
     * @param type
     */
    public void logined(int type) {
    }

    private Intent mIntent;

    /**
     * 系统级ActivityResult,如登陆，打开联系人，设置图片等，开发人员不用理会
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        String fname = settings.getString("fname", "");
        if (requestCode == C.REQUEST_LOGIN && resultCode == RESULT_OK) {

        } else if (requestCode == C.REQUEST_SELECT_CONTACTS
                && resultCode == Activity.RESULT_OK) {
            mIntent = data;
            getContacts();
        }
    }

    private void getContacts() {
        Uri uri = mIntent.getData();
        Cursor c = managedQuery(uri, null, null, null, null);
        c.moveToFirst();
        ContactInfo contact = getContactPhone(c);
        if (null != contact) {
            if (contact.name != null) {
                selectedContact(contact);
            }
        } else {
            UI.showToast(this, "请开启读取联系人权限");
        }
    }

    @Override
    protected String[] getNeedPermissions() {
        return new String[]{Manifest.permission.READ_CONTACTS};
    }

    @Override
    protected void permissionGrantedSuccess() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_PICK);
        i.setData(ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(i, C.REQUEST_SELECT_CONTACTS);
    }

    @Override
    protected void permissionGrantedFail() {

    }

    /**
     * 设置服务地址后的基础处理
     *
     * @param type
     */
    protected void setServerType(int type) {
        saveSetting("baseImageUrl", C.baseImageUrl);
        saveSetting("baseUrl", C.baseUrl);
        saveSetting("serverType", type);
        saveSetting("wsUrl", C.wsUrl);
    }

    /*
     * 此方法调用的前提：程序被系统销毁
     * (non-Javadoc)
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreVars();
    }

    /**
     * 恢复全局变量。因为系统会在休眠后不定时将静态变量清空， <br/>
     * 因此静态变量必须在赋值后存入硬盘。在使用时恢复。子类覆盖此方法
     */
    protected void restoreVars() {

    }

    /**
     * 初始化列表。调用此方法后，系统会按需自动显示“更多”按钮。 子类需要覆盖 refreshData()
     *
     * @param listView
     * @param adapter
     * @param criteria
     */

    private MyRow lists = new MyRow();

    /**
     * 初始化 ListView, 以便统一处理更多翻页和无数据文字显示情况
     *
     * @param listView 指定的ListView
     * @param adapter  数据适配器
     * @param criteria 查询条件，要翻页时必须有此参数
     * @param op       对应ListView查询数据的远程方法
     */
    protected void initList(ListView listView, ArrayAdapter<?> adapter,
                            Criteria criteria, Class<?> op) {
        initList(listView, adapter, criteria, op, 0);
    }

    /**
     * 初始化 ListView, 以便统一处理更多翻页和无数据文字显示情况
     *
     * @param listView 指定的ListView
     * @param adapter  数据适配器
     * @param op       对应ListView查询数据的远程方法
     */

    protected void initList(ListView listView, ArrayAdapter<?> adapter,
                            Class<?> op) {
        initList(listView, adapter, null, op, 0);
    }

    /**
     * 初始化 ListView, 以便统一处理更多翻页和无数据文字显示情况
     *
     * @param listView  指定的ListView
     * @param adapter   数据适配器
     * @param op        对应ListView查询数据的远程方法
     * @param noDataTip 无数据时显示的自定义文字，资源ID
     */
    protected void initList(ListView listView, ArrayAdapter<?> adapter,
                            Class<?> op, int noDataTip) {
        initList(listView, adapter, null, op, noDataTip);
    }

    /**
     * 初始化 ListView, 以便统一处理更多翻页和无数据文字显示情况
     *
     * @param listView  指定的ListView
     * @param adapter   数据适配器
     * @param criteria  查询条件，要翻页时必须有此参数
     * @param op        对应ListView查询数据的远程方法
     * @param noDataTip 无数据时显示的自定义文字，资源ID
     */
    protected void initList(ListView listView, ArrayAdapter<?> adapter,
                            Criteria criteria, Class<?> op, int noDataTip) {
        View footer = getLayoutInflater().inflate(R.layout.list_footer, null);
        footer.setOnClickListener(clickListener);
        ListObject lo = new ListObject();
        lo.adapter = adapter;
        lo.listView = listView;
        lo.footer = footer;
        lo.criteria = criteria;
        lo.noDataTip = noDataTip;
        lists.put(op.getName(), lo);
        if (listView.getFooterViewsCount() == 0) {
            listView.addFooterView(footer);// 防止重复添加footer
        } else {
            hide(R.id.no_data);
            hide(R.id.more_data);
        }

        listView.setAdapter(adapter);
        listView.setOnScrollListener(new MyOnScrollListener(this, lo));
    }

    protected void addEmptyView(ListView listView) {
        LayoutInflater inflater = getLayoutInflater();
        View emptyView = inflater.inflate(R.layout.list_empty_view, null);
        addContentView(emptyView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .MATCH_PARENT));
        listView.setEmptyView(emptyView);
        hide(R.id.emptyview);
    }

    private void displayFooter(final int currTotal, final ListObject lo) {
        hide(R.id.no_data);
        hide(R.id.more_data);
        lo.hasMore = false;
        if (currTotal == 0 && lo.adapter.getCount() == 0) {
            if (lo.noDataTip > 0) {
                UI.setEText(lo.footer, R.id.no_data, lo.noDataTip);
            }
            show(R.id.emptyview);
            show(R.id.no_data);
        } else if (lo.criteria != null && currTotal == lo.criteria.PageSize) {
            /* 2014-07-02 暂时用拖动刷新 */
            // show(footer,R.id.more_data);
            lo.hasMore = true;
        }
    }

    public void moreData(ListObject lo) {
        if (lo.hasMore) {
            show(lo.footer, R.id.more_data);
            lo.criteria.PageIndex += 1;
            refreshData();
        }
    }

    /**
     * 刷新数据
     *
     * @param clearData ，清除现有数据
     */
    public void refreshData(boolean clearData, Class<?> op) {
        if (clearData) {
            ListObject lo = (ListObject) lists.get(op.getName());
            if (lo != null) {
                lo.adapter.clear();// clear data since criteria changed
                if (lo.criteria != null) {
                    lo.criteria.PageIndex = C.PageIndex;
                    hide(lo.footer, R.id.no_data);
                    hide(lo.footer, R.id.more_data);
                }
            }
        }
        refreshData();
    }

    /**
     * 远程方法结束后的工作，子类可覆盖此方法
     *
     * @param op
     * @param result
     */
    public void onPostExecute(Class<?> op, Result result) {
        if (result.code == 0) {
            ListObject lo = (ListObject) lists.get(op.getName());
            if (lo != null) {
                MyData d = (MyData) result.obj;
                displayFooter(d.size(), lo);// display more data/no data
            }
        }
    }

    /**
     * 打开联系人，子类需要覆盖回调方法selectedContact
     */
    protected void openContacts() {
        checkAllNeedPermissions();
    }

    /**
     * 打开联系人的回调方法，处理选择联系人后的动作
     *
     * @param contact 选中的联系人
     */
    protected void selectedContact(ContactInfo contact) {

    }

    private ContactInfo getContactPhone(Cursor cursor) {
        ContactInfo contact = new ContactInfo();
        int column = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        if (cursor.getCount() < 1) {
            return null;
        }
        int phoneNum = cursor.getInt(column);
        if (phoneNum > 0) {
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(idColumn);
            Cursor cs = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
                            + contactId, null, null);
            // 遍历所有的电话号码
            if (cs.moveToFirst()) {
                for (; !cs.isAfterLast(); cs.moveToNext()) {
                    int index = cs
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int typeIndex = cs
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                    int phoneType = cs.getInt(typeIndex);
                    String phone = cs.getString(index);
                    String name = cs.getString(cs
                            .getColumnIndex(PhoneLookup.DISPLAY_NAME));
                    if (phoneType == 2) {
                        contact.mobilePhone = phone;
                    } else {
                        contact.phoneNumber = phone;
                    }
                    contact.name = name;
                }
                if (!cs.isClosed()) {
                    cs.close();
                }
            }
        }
        return contact;
    }

    /**
     * 虚方法， Handler 要实现的方法
     *
     * @param type 类型标识，以便区分有多个Handler的情况
     * @param msg
     */
    protected void updateUI(int type, Message msg) {

    }

    /**
     * 上传图片方法，供子类调用
     *
     * @param view 触发此按钮弹出菜单“拍照/选图片”
     * @param iv   UI上要修改的图片控件
     */
    protected void getPicture(View view, String[] pic_options, ImageView iv) {
        getPictureView = view;
        this.iv = iv;
        int[] images = new int[]{R.drawable.take_picture,
                R.drawable.choose_picture};
        showListDialog(view, pic_options, images);
    }

    /**
     * 修改线性布局北京图片方法，供子类调用
     *
     * @param view 触发此按钮弹出菜单“拍照/选图片”
     * @param lin  UI上要修改的图片控件
     */
    protected void linearLayoutgetPicture(View view, String[] pic_options, LinearLayout
            lin) {
        getPictureView = view;
        this.lin = lin;
        int[] images = new int[]{R.drawable.take_picture, R.drawable.choose_picture};
        showListDialog(view, pic_options, images);
    }

    /**
     * 用弹出窗口编辑单个文本内容
     *
     * @param title 窗口显示标题
     * @param view  要修改的主窗口文本控件
     */
    protected void editOneText(CharSequence title, View view) {
        editOneText(title, view, InputType.TYPE_CLASS_TEXT, 0);
    }

    /**
     * 用弹出窗口编辑单个文本内容
     *
     * @param title     窗口显示标题
     * @param view      要修改的主窗口文本控件
     * @param inputType 输入类型
     * @param maxLength 长度
     */
    protected void editOneText(CharSequence title, View view,
                               final int inputType, final int maxLength) {
        CustomDialog cd = new CustomDialog(this, R.layout.text);
        final TextView tv = (TextView) view;
        cd.setInitializer(new Initializer() {
            public void init(Builder alertDialog, View view) {
                String s = tv.getText().toString();
                EditText edit = (EditText) view.findViewById(R.id.input_text);
                edit.setInputType(inputType);
                if (maxLength > 0) {
                    edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                            maxLength)});
                    if (s.length() > maxLength) {
                        s = s.substring(0, maxLength);
                    }
                }
                if (inputType == InputType.TYPE_TEXT_FLAG_MULTI_LINE) {
                    edit.setSingleLine(false);
                }
                edit.setText(s);
                edit.setSelection(s.length());
            }
        });
        cd.setOKProcessor(new OKProcessor() {
            public void process(Builder alertDialog, View view) {
                tv.setText(UI.getEText(view, R.id.input_text));
            }
        });
        cd.show(title, true);
    }

    /**
     * 退出系统关闭推送消息 这个方法在应用没有用极光推送的情况下都传false.
     */
    protected void exit(boolean exitApp) {

    }

    /**
     * 处理通用的点击
     *
     * @author Administrator
     */
    class MyOnClickListener implements OnClickListener {
        public void onClick(View view) {
            int id = view.getId();
            int menu = UI.getId(BaseActivity.this, "menu", "id");
            int ic_home = UI.getId(BaseActivity.this, "ic_home", "id");
            if (id == menu) {
                if (menus != null && menus.length > 0) {
                    showMenu(view, 2, 2, menus, menuImages);
                }
            } else if (id == ic_home) {
                goHome();
            } else if (id == R.id.header_back) {
                if (validate()) {
                    back();
                }
            }
        }
    }

    private void goHome() {

    }

    public void home(View view) {
        goHome();
    }

    public void back() {
        finish();
    }

    /**
     * 隐藏对象
     *
     * @param resId
     */
    public void hide(int resId) {
        setV(findViewById(resId), View.GONE);
    }

    public void hide(View view, int resId) {
        setV(view.findViewById(resId), View.GONE);
    }

    public void show(int resId) {
        setV(findViewById(resId), View.VISIBLE);
    }

    public void show(View view, int resId) {
        setV(view.findViewById(resId), View.VISIBLE);
    }

    private void setV(View v, int value) {
        if (v != null) {
            v.setVisibility(value);
        }
    }

    private PopupWindow pop;

    /**
     * Show a pop up window
     *
     * @param resId  layout file in pop up window
     * @param anchor show pop up window below this control
     */
    @SuppressWarnings("deprecation")
    protected void showPopup(int resId, View anchor) {
        if (pop == null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View v = inflater.inflate(resId, null);
            pop = new PopupWindow(v, LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT, false);
            pop.setBackgroundDrawable(new BitmapDrawable());
            pop.setOutsideTouchable(true);
            pop.setFocusable(true);
        }
        if (pop.isShowing()) {
            pop.dismiss();
            pop = null;
        } else {
            pop.showAsDropDown(anchor);
        }
    }

    /**
     * 数据列表的滚动事件处理方法，项目子类覆盖此方法 本 Activity 使用了 initList()方法进行初始化
     *
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     */
    public void specificOnScroll(AbsListView listView,
                                 final int firstVisibleItem, final int visibleItemCount,
                                 final int totalItemCount) {
    }


    protected void copyToClipboard(CharSequence str) {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData cd = ClipData.newPlainText("label", str);
        cm.setPrimaryClip(cd);
    }

    /**
     * 纯文字分享
     *
     * @param contentString
     */
    public void shareText(String contentString) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, contentString);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(shareIntent, getTitle()));
    }

    /**
     * 分享图片列表
     *
     * @param imageList
     */
    public void shareImage(ArrayList<Uri> imageList) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageList);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(shareIntent, getTitle()));
    }

    /**
     * 隐藏软键盘
     *
     * @author fengyq
     */
    public void hideSoftKeyboard() {
        UI.hideSoftKeyboard(this);
    }

    /**
     * 检测Sdcard是否存在
     *
     * @author fengyq
     */
    protected static boolean isExitsSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /*
     * 当程序处于挂起状态被调用比如home键
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        restoreVars();
    }

    /**
     * 判断当前设备是手机还是平板
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration
                .SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 获取缓存大小
     * 图片缓存加数据缓存
     */
    public String getCacheSize() {
        ACache aCache = ACache.get(this);
        double cacheSize = GlideCacheUtil.getInstance().getCacheSizeDouble(this);
        if (null != aCache) {
            cacheSize += aCache.getCacheSizeDouble();
        }
        return GlideCacheUtil.getInstance().getFormatSize(cacheSize);
    }

    /**
     * 清除缓存
     */
    public void clearAllCache() {
        GlideCacheUtil.getInstance().clearImageAllCache(this);
        ACache aCache = ACache.get(this);
        if (null != aCache) {
            aCache.clear();
        }
    }

    public void doWork(View view) {

    }

    /**
     * 清除EditText的焦点，并隐藏软键盘
     *
     * @param parentView
     * @param a
     */
    public void clearFocusAndHideSoft(View parentView, final BaseActivity a) {
        parentView.setFocusable(true);
        parentView.setFocusableInTouchMode(true);
        parentView.requestFocus();
        parentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (null != a.getCurrentFocus()) {
                    InputMethodManager mInputMethodManager = (InputMethodManager)
                            getSystemService(INPUT_METHOD_SERVICE);
                    return mInputMethodManager.hideSoftInputFromWindow(a.getCurrentFocus()
                            .getWindowToken(), 0);
                }
                return false;
            }
        });
    }

    /**
     * 从Assets目录获取文本文件内容
     *
     * @param fileName
     * @return
     * @throws Exception
     */

    public String getFromAssets(String fileName) throws Exception {
        try {
            InputStreamReader inputReader = new InputStreamReader(getAssets().open
                    (fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            StringBuilder result = new StringBuilder();
            while ((line = bufReader.readLine()) != null) {
                result.append(line);
            }
            bufReader.close();
            inputReader.close();
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationUtil != null) {
            locationUtil.stopLocation();
        }
    }


    @Override
    public void setTheme(int resid) {
        super.setTheme(resid);
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.backward_enter, R.anim.backward_exit);
    }

    /**
     * 格式化分
     *
     * @param sDate
     * @return
     */
    protected String getMDate(String sDate) {
        String result = "";
        try {
            if (TextUtils.isEmpty(sDate) || sDate.length() < 11) {

            } else {
                Date date = C.df_yMdHm.parse(sDate);
                result = C.df_yMdHm.format(date);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 格式化天
     *
     * @param sDate
     * @return
     */
    protected String getDDate(String sDate) {
        String result = "";
        try {
            if (TextUtils.isEmpty(sDate) || sDate.length() < 11) {

            } else {
                Date date = C.df_yMd.parse(sDate);
                result = C.df_yMd.format(date);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 格式化秒
     *
     * @param sDate
     * @return
     */
    protected String getSDate(String sDate) {
        String result = "";
        try {
            if (TextUtils.isEmpty(sDate) || sDate.length() < 11) {

            } else {
                Date date = C.df_yMdHms.parse(sDate);
                result = C.df_yMdHms.format(date);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}