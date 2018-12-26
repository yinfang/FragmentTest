package com.clubank.domain;


import com.clubank.util.MyRow;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class C {

    public static Locale loc = Locale.getDefault();
    public static String Token = "";
    public static String customerId;
    public static final String APP_ID = "APP_ID";
    public static final int SERVER_INTRANET = 1;// 内网
    public static final int SERVER_INTERNET = 2;// 外网
    public static final DecimalFormat nf_i = new DecimalFormat("#,##0");// integer
    public static final DecimalFormat nf_a = new DecimalFormat("###0.00");// amount
    public static final DecimalFormat nf_l = new DecimalFormat("###0.000000");// GPS
    public static final DecimalFormat nf_s = new DecimalFormat("#,###.#");// 1位小数
    public static final DateFormat df_y = new SimpleDateFormat("yyyy", loc);
    public static final DateFormat df_yM = new SimpleDateFormat("yyyy-MM", loc);
    public static final DateFormat df_yMd = new SimpleDateFormat("yyyy-MM-dd",
            loc);
    public static final DateFormat df_ydotMdotd = new SimpleDateFormat("yyyy.MM.dd",
            loc);
    public static final DateFormat dff_yMdHm = new SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss", loc);
    public static final DateFormat df_yMdHm = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm", loc);
    public static final DateFormat df_yMdHms = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", loc);
    public static final DateFormat dfs_yMd = new SimpleDateFormat(
            "yyyy年MM月dd日", loc);
    public static final DateFormat df_Hm = new SimpleDateFormat("HH:mm", loc);
    public static final String DB_NAME = "app.db";
    public static int DB_VERSION = 1;//数据库的版本

    public static final int cnt = 25;// 每页数据条数
    public static final int large = 500;// 头像大图
    public static final int small = 150;// 头像小图
    public static final int compress = 80;
    public static final String UNSPECIFIED_IMAGE = "image/*";

    public static final int height = 32;

    public static final String IMAGE_CACHE_DIR = "images";

    public static final int TYPE_SMS = 1;
    public static final int TYPE_EMAIL = 2;
    public static int TIMEOUT = 6000;
    public static int TYPESH = 1;//扫描打印机

    public static final int REQUEST_LOGIN = 10001;
    public static final int DLG_ILLEGAL_ACCESS = 11001;
    public static final int REQUEST_SELECT_CONTACTS = 10002;
    public static final int REQUEST_FROM_CAMERA = 10003;
    public static final int REQUEST_FROM_PICTURE = 10004;
    public static final int REQUEST_HANDLE_PICTURE = 10005;
    public static final int REQUEST_EXIT = 10006;
    public static final int REQUEST_SCAN = 10007;
    public static final int REQUEST_LOGOUT = 10008;
    public static final int DLG_DOWNLOAD_NEW_VERSION = 10102;
    public static final int DLG_CONFIRM_DELETE = 10103;
    public static final int DLG_CONFIRM_CANCEL = 10104;
    public static final int DLG_CONFIRM_LOGOUT = 10110;
    public static final int DLG_CONFIRM_EXIT = 10111;

    public static final int ENGINEER_MODE = 1;

    /**
     * 更新GPS 位置间隔
     */
    public static final int UPDATE_LOCATION_INTERVAL = 1000 * 60 * 5;

    public static String n(double d) {
        return nf_a.format(d);
    }

    public static final int OS_ANDROID = 1;

    public static String baseImageUrl;
    public static String baseUrl;
    public static String wsUrl;
    public static String namespace = "http://www.hclub.com/";

//    public static AMapLocation location;


    public static int dialogTheme = -1;//自定义dialog样式默认白色（android.R.style
    // .Theme_Holo_DialogWhenLarge 灰色）
    public static int PageIndex = 1;
    public static int PageChangeWay = 0;//界面切换动画设置（默认淡入淡出）


    public static final int HTTP_GET = 0;//get请求
    public static final int HTTP_POST = 1;//post请求
    public static final int REQUEST_MODE_SOAP = 0;//soap交互方式
    public static final int REQUEST_MODE_HTTP = 1;//http交互方式
    public static String apiDataKey = "data";//json 数据对象的key可在项目中配置
    public static String apiState = "status";
    public static String apiMsg = "msg";
    public static MyRow headerData;


}
