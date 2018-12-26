package com.clubank.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;

import com.clubank.common.R;
import com.clubank.device.BaseActivity;
import com.clubank.device.MyApplication;
import com.clubank.domain.C;
import com.clubank.domain.Point;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.security.auth.x500.X500Principal;


public class U {

    public static Object clone(Object o) {
        return getObject(getBytes(o));
    }

    public static byte[] getBytes(Object o) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(o);
            out.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getObject(byte[] b) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(b);
            ObjectInputStream in = new ObjectInputStream(bais);
            return in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T toObject(MyRow row, Class<T> clazz) {
        try {
            T t = clazz.newInstance();
            Field[] fields = clazz.getFields();
            for (Field f : fields) {
                try {
                    if (row.containsKey(f.getName())) {
                        if (f.getType().equals(Integer.TYPE)) {
                            f.set(t, row.getInt(f.getName()));
                        } else if (f.getType().equals(Boolean.TYPE)) {
                            f.set(t, row.getBoolean(f.getName()));
                        } else if (f.getType().equals(Double.TYPE)) {
                            f.set(t, row.getDouble(f.getName()));
                        } else {
                            f.set(t, row.getString(f.getName()));
                        }
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static MyRow toRow(Object o) {
        MyRow row = new MyRow();
        Field[] fields = o.getClass().getFields();
        for (Field f : fields) {
            String name = f.getName();
            try {
                row.put(name, f.get(o));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return row;
    }

    /**
     * 下载APK并运行
     *
     * @param a
     * @param apkurl
     */
    public static void downloadApk(BaseActivity a, String apkurl) {
        try {
            URL url = new URL(apkurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(3 * 1000);
            conn.setReadTimeout(60 * 1000);
            conn.setDoInput(true);

            conn.connect();
            InputStream is = conn.getInputStream();

            File cacheDir = a.getCacheDir();
            final String cachePath = cacheDir.getAbsolutePath() + "/temp.apk";

            File file = new File(cachePath);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);

            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }

            fos.close();
            is.close();

            String command = "chmod 777 " + cachePath;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + cachePath),
                    "application/vnd.android.package-archive");
            a.startActivity(intent);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }

    }

    /**
     * get formatted number
     *
     * @param row
     * @param name
     * @return
     */
    public static String getNumber(MyRow row, String name, DecimalFormat nf) {
        return nf.format(row.getDouble(name));
    }

    public static String subStr(String str, int len) {
        if (str == null) {
            return "";
        } else {
            int len1 = len;
            String ret = str.substring(0, str.length() < len ? str.length()
                    : len);
            try {
                int len2 = ret.getBytes("GBK").length;
                while (len2 > len1) {
                    int len3 = --len;
                    ret = str.substring(0, len3 > str.length() ? str.length()
                            : len3);
                    len2 = ret.getBytes("GBK").length;
                    // subStrByetsL = subStr.getBytes().length;
                }
                return ret;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static double max(double[] items) {
        double ret = 0;
        for (int i = 0; i < items.length; i++) {
            ret = Math.max(ret, items[i]);
        }
        return ret;
    }

    public static double min(double[] items) {
        double ret = Integer.MAX_VALUE;
        for (int i = 0; i < items.length; i++) {
            ret = Math.min(ret, items[i]);
        }
        return ret;
    }

    /**
     * 获得标准格式的日期字符串 如 2014-07-18
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static String getDateString(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        return C.df_yMd.format(c.getTime());
    }

    /**
     * 获得标准格式的时间字符串 如 08:51
     *
     * @param hour
     * @param minute
     * @return
     */

    public static String getTimeString(int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        return C.df_Hm.format(c.getTime());
    }

    /**
     * 格式化秒时间为分时间
     *
     * @param sDate
     * @return
     */
    public static String getMDate(String sDate) {
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
    public static String getDDate(String sDate) {
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
    public static String getSDate(String sDate) {
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

    /**
     * 格式化小时分钟
     *
     * @param sDate
     * @return
     */
    public static String getHMDate(String sDate) {
        String result = "";
        try {
            if (TextUtils.isEmpty(sDate)) {

            } else {
                Date date = C.df_Hm.parse(sDate);
                result = C.df_Hm.format(date);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 复制文件
     *
     * @param in
     * @param out
     */
    public static void copy(final InputStream in, final OutputStream out) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    /**
     * 解压zip文件
     *
     * @param zipFile
     * @param outputDir
     */
    public static void unzip(String zipFile, String outputDir) {

        byte[] buffer = new byte[1024];

        try {

            // create output directory is not exists
            File folder = new File(outputDir);
            if (!folder.exists()) {
                folder.mkdir();
            }

            // get the zip file content
            ZipInputStream zis = new ZipInputStream(
                    new FileInputStream(zipFile));
            // get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {

                String fileName = ze.getName();
                File newFile = new File(outputDir + File.separator + fileName);

                System.out.println("file unzip : " + newFile.getAbsoluteFile());

                // create all non exists folders
                // else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

            System.out.println("Done");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // 格式化时间为:分钟、月、天
    public static String getTimeDiff(Context context, String stime) {
        String str = "*";
        long t0 = System.currentTimeMillis();
        long t1 = 0;
        try {
            Date date1 = C.df_yMdHms.parse(stime);
            t1 = date1.getTime();
        } catch (Exception e) {
            return str;
        }
        long m = (long) ((t0 - t1) * 1.0000 / (60 * 1000));
        if (m < 60) {
            if (m <= 0)
                m = 0;
            str = "" + m + " " + context.getText(R.string.minute);
        } else if (m < 60 * 24) {
            long h = Math.round(m * 1.00 / 60);
            str = "" + h + " " + context.getText(R.string.hour);
        } else if (m < 60 * 24 * 30) {
            int d = (int) (m * 1.00 / (60 * 24));
            str = "" + d + " " + context.getText(R.string.day);
        } else {
            str = " " + context.getText(R.string.month1);
        }
        return str;
    }

    /**
     * 计算时间差别
     *
     * @param cxt
     * @param startTime
     * @param endTime
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String calculateTimeDiff(Context cxt, String startTime,
                                           String endTime) {
        String str = "*";
        long t1 = 0, t2 = 0;
        try {
            Date date1 = new Date(startTime);
            Date date2 = new Date(endTime);
            t1 = date1.getTime();
            t2 = date2.getTime();
        } catch (Exception e) {
            return str;
        }
        long m = (long) ((t2 - t1) * 1.0000 / (60 * 1000));
        if (m < 60) {
            if (m <= 0)
                m = 0;
            str = "" + m + " " + cxt.getText(R.string.minute);
        } else if (m < 60 * 24) {
            long h = Math.round(m * 1.00 / 60);
            str = "" + h + " " + cxt.getText(R.string.hour);

        } else if (m < 60 * 24 * 2) {// 小于48小时，显示昨天
            str = "" + cxt.getText(R.string.yestoday);
        } else if (m < 60 * 24 * 30) {
            int d = (int) (m * 1.00 / (60 * 24));
            str = "" + d + " " + cxt.getText(R.string.day);
        } else {
            str = " " + cxt.getText(R.string.month1);
        }
        return str;
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    public static String getNumber(MyRow dr, String name) {
        return C.nf_a.format(dr.getDouble(name));
    }

    public static boolean isEmpty(String name) {
        return name == null || name.trim().equals("");
    }

    public static MyRow getRow(Bundle b, String name) {
        return MyRow.fromMap((Map<?, ?>) b.getSerializable(name));
    }

    public static MyData getData(Bundle b, String name) {
        @SuppressWarnings("unchecked")
        List<Map> list = (List<Map>) b.getSerializable(name);
        MyData data = new MyData();
        for (Map<?, ?> map : list) {
            data.add(MyRow.fromMap(map));
        }
        return data;
    }



    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
    /**
     * 当前网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager m = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo gprs = m.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = m.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isConnected() || gprs.isConnected();

    }

    /**
     * 当前是否使用3G
     *
     * @param context
     * @return
     */
    public static boolean is3G(Context context) {
        ConnectivityManager m = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo gprs = m.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return gprs.isConnected();
    }

    /**
     * 判断当前网络是否是wifi网络
     *
     * @param context
     * @return boolean
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager m = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = m.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isConnected();
    }

    /**
     * 判断当前网络是否是2G网络
     *
     * @param context
     * @return boolean
     */
    public static boolean is2G(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null
                && (networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE
                || networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS || networkInfo
                .getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA)) {
            return true;
        }
        return false;
    }

    /**
     * wifi是否打开
     */
    public static boolean isWifiEnabled(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

    /**
     * 根据经纬度获取具体位置
     *
     * @param context
     * @param latitude
     * @param longitude
     * @return
     */
    public static String getAddress(Context context, double latitude, double longitude) {
        String result = "";
        Geocoder geocoder = new Geocoder(context, Locale.CHINA);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result = address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 根据城市code查名称
     *
     * @param context
     * @param code
     * @return
     */
    public static String getCityName(Context context, String code) {
        if (code != null) {
            MyDBHelper helper = new MyDBHelper(context, C.DB_NAME, null, C.DB_VERSION);

            MyRow row = new DB(helper).getByKey(
                    "select Name from t_city where code=?", code);
            if (row != null) {
                return row.getString("Name");
            }
        }
        return null;
    }

    //
    // public static String getCityCode(Context context, String name) {
    // if (name != null) {
    // MyRow row = DB.getByKey(context,
    // "select Code from t_city where Name=?", name);
    // if (row != null) {
    // return row.getString("Code");
    // }
    // }
    // return null;
    // }

    /**
     * 获得当前最近的城市，必须当前的GPS在起作用。 APK内已经内置了城市数据
     *
     * @param app
     * @return
     */
    public static MyRow getNearestCity(MyApplication app) {
        MyRow address = new MyRow();
        // TODO: 17-7-21  
//        if (C.location == null) {
//            return null;
//        }
        // TODO: 17-7-21  
//        if (!TextUtils.isEmpty(C.location.getProvince())) {
//            // 字段名称请参考数据库创建，兼容旧版本项目
//            address.put("Name", C.location.getCity());
//        }
        /*
         * Point point = new Point(C.location.getLatitude(),
         * C.location.getLongitude()); return getCityByPoint(app, point);
         */
        return address;
    }

    /**
     * 获得当前省份，必须当前的GPS在起作用。 APK内已经内置了省份数据
     *
     * @param app
     * @return
     */
    public static MyRow getProvince(MyApplication app) {
        MyRow address = new MyRow();
        // TODO: 17-7-21
//        if (C.location == null) {
//            return null;
//        }
        // TODO: 17-7-21  
//        if (!TextUtils.isEmpty(C.location.getProvince())) {
//            // 字段名称请参考数据库创建，兼容旧版本项目
//            address.put("Name", C.location.getProvince());
//			address.put("Country", C.location.getCountry());
//        }
        return address;
        /*
         * Point point = new Point(C.location.getLatitude(),
         * C.location.getLongitude()); return getProvinceByPoint(app, point);
         */
    }

    /**
     * 获得坐标最近的省份。 APK内已经内置了省份数据
     *
     * @param app
     * @param point 坐标
     * @return
     */
    public static MyRow getProvinceByPoint(MyApplication app, Point point) {
        if (point == null) {
            return null;
        }
        MyRow result = null;

        double d0 = Double.MAX_VALUE;
        MyDBHelper helper = new MyDBHelper(app, C.DB_NAME, null, C.DB_VERSION);
        MyData list = new DB(helper)
                .getData("select Code,Name,Latitude,Longitude from t_province");
        for (MyRow row : list) {
            double lat2 = row.getDouble("Latitude");
            double lon2 = row.getDouble("Longitude");
            double distance = getDistance(point.latitude, point.longitude,
                    lat2, lon2);
            if (distance < d0) {
                result = row;
                d0 = distance;
            }
        }
        return result;
    }

    /**
     * 获得坐标最近的城市。 APK内已经内置了城市数据
     *
     * @param app
     * @param point 坐标
     * @return
     */
    public static MyRow getCityByPoint(MyApplication app, Point point) {
        if (point == null) {
            return null;
        }
        MyRow result = null;
        double d0 = Double.MAX_VALUE;
        MyDBHelper helper = new MyDBHelper(app, C.DB_NAME, null, C.DB_VERSION);
        MyData list = new DB(helper)
                .getData("select Code,Name,Latitude,Longitude from t_city");
        for (MyRow row : list) {
            double lat2 = row.getDouble("Latitude");
            double lon2 = row.getDouble("Longitude");
            double distance = getDistance(point.latitude, point.longitude,
                    lat2, lon2);
            if (distance < d0) {
                result = row;
                d0 = distance;
            }
        }
        return result;
    }

    /**
     * 判断2点之间的距离
     *
     * @param lat1 维度
     * @param lon1 经度
     * @param lat2 维度
     * @param lon2 经度
     * @return
     */
    public static double getDistance(double lat1, double lon1, double lat2,
                                     double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }

    private static final X500Principal DEBUG_DN = new X500Principal(
            "CN=Android Debug,O=Android,C=US");

    /**
     * 判断当前运行的程序是否是debug包
     *
     * @param ctx
     * @return
     */
    public static boolean isDebug(Context ctx) {
        boolean debuggable = false;

        try {
            PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature signatures[] = pinfo.signatures;

            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            for (int i = 0; i < signatures.length; i++) {
                ByteArrayInputStream stream = new ByteArrayInputStream(
                        signatures[i].toByteArray());
                X509Certificate cert = (X509Certificate) cf
                        .generateCertificate(stream);
                debuggable = cert.getSubjectX500Principal().equals(DEBUG_DN);
                if (debuggable)
                    break;
            }
        } catch (NameNotFoundException e) {
            // debuggable variable will remain false
        } catch (CertificateException e) {
            // debuggable variable will remain false
        }
        return true;

    }

    /**
     * 运行 assets 目录下的apk文件。
     *
     * @param context
     * @param fileName 要运行的文件名
     */
    public static void runAssetsApk(Context context, String fileName) {
        String path = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/" + fileName;
        try {
            InputStream is = context.getAssets().open(fileName);
            File file = new File(path);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + path),
                    "application/vnd.android.package-archive");
            context.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 字节数组转为16进制字符
     *
     * @param bytes
     * @return
     */
    public static String bytes2HexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 根据表示二进制的字符串得到实际的字节数组
     *
     * @param src 表示二进制的字符串
     * @return 得到的字节数组
     */
    public static byte[] hexString2Bytes(String src) {
        if (null == src || 0 == src.length()) {
            return null;
        }
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < (tmp.length / 2); i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    private static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0}))
                .byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1}))
                .byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    /**
     * 获得指定正则表达式匹配的字符按串
     *
     * @param target 要检测的字符串
     * @param regex  正则表达式
     * @return 匹配的字符串数组
     */
    public static String[] getRegResult(String target, String regex) {
        List<String> result = new ArrayList<String>();
        try {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(target);
            while (matcher.find()) {
                result.add(matcher.group());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result.toArray(new String[]{});
    }

    /**
     * 保存临时图片
     *
     * @param bmp    图片
     * @param prefix 文件名前缀
     * @return 返回保存后的文件名
     */
    public static String saveTempBitmap(Bitmap bmp, String prefix, String suffix) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                File file = File.createTempFile(prefix, suffix,
                        Environment.getExternalStorageDirectory());
                FileOutputStream fos = new FileOutputStream(file);
                bmp.compress(CompressFormat.JPEG, 70, fos);
                fos.flush();
                fos.close();
                String filePath = file.getAbsolutePath();
                return filePath;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 判断一个字符串是否为数字型， 至少包含一个数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }

    /**
     * @param b
     * @return
     */
    public static byte[] reverse(byte[] b) {
        byte[] ret = new byte[b.length];
        for (int i = 0; i < b.length; i++) {
            ret[i] = b[b.length - i - 1];
        }
        return ret;
    }

    public static String getUdid(Context context) {
        String deviceId = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);

        return deviceId;
    }

    public static int getVersion(Context context) {
        int ret = 0;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            ret = info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static int getVCode(Context context) {
        int ret = 0;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            ret = info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static String getVName(Context context) {
        String ret = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            ret = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static Vector<String> getContactsMobile(Context context) {
        Vector<String> v = new Vector<String>();
        ContentResolver cr = context.getContentResolver();
        Cursor pCur = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                null, null, null);
        while (pCur.moveToNext()) {
            String phone = pCur
                    .getString(pCur
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (phone.length() == 11 && phone.startsWith("1"))
                System.out.println("phone" + phone);
            v.add(phone);
        }
        pCur.close();
        return v;
    }

    public static Bundle getMeta(Context context) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
        }
        if (appInfo != null) {
            return appInfo.metaData;
        }
        return null;
    }

    public static String getFormatted(String captionFormat, String captionCols,
                                      MyRow row) {
        String[] fnames = captionCols.split(",");
        Object[] fvalues = new Object[fnames.length];
        for (int j = 0; j < fnames.length; j++) {
            fvalues[j] = row.get(fnames[j]);
        }
        String value = String.format(captionFormat, fvalues);
        return value;
    }
// TODO: 17-7-21  
//    public static void initLocSDK(BaseActivity a) {
//        Bundle b = getMeta(a);
//        if (b == null || !b.containsKey("com.baidu.lbsapi.API_KEY")) {
//            return;
//        }
//        Context context = a.app.getApplicationContext();
//
//        C.lc = new LocationClient(context);
//        MyLocationListener listener = new MyLocationListener(context);
//        C.lc.registerLocationListener(listener);
//        LocationClientOption option = new LocationClientOption();
//        option.setOpenGps(true);// 可选，默认false,设置是否使用gps
//		/*
//		 * 火星坐标系gcj02 百度定位SDK可以返回三种坐标系，分别是bd09,
//		 * bd09ll和gcj02，其中bd09ll能无偏差地显示在百度地图上。 但这个地址并不是真实的经纬度，与苹果的地址不是一个体系
//		 * 数据库存储的这个地址，只能在百度地图上查看具体位置 国际经纬度坐标标准为WGS-84
//		 */
//        option.setCoorType("bd09ll");// 采用bd09ll坐标体系
//        option.setLocationMode(LocationMode.Hight_Accuracy);// 默认高精度，设置定位模式，高精度，低功耗，仅设备
//        option.setScanSpan(15000);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//        option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
//        option.setIsNeedAddress(true);// 设置是否需要地址信息，默认不需要
//        C.lc.setLocOption(option);
//        C.lc.start();
//        C.lc.requestLocation();
//    }


    public static String[] getNames(MyData data, String colName) {
        if (data == null || data.size() == 0) {
            return null;
        }
        String[] ret = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            ret[i] = data.get(i).getString(colName);
        }
        return ret;
    }

    /**
     * 对远程获得的经过Base64解码的字符串进行解码。目前是为了防止不可见字符出现。<br/>
     * 不可见字符会造成SOAP编码错误不能进行传输
     *
     * @param str 经过Base64编码的UTF8字符串
     * @return
     */
    public static String getDecoded(String str) {
        try {
            str = new String(Base64.decode(str, Base64.DEFAULT), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 将字符串进行Base64编码。目前是为了防止不可见字符出现。<br/>
     * 不可见字符会造成SOAP编码错误不能进行传输
     *
     * @param str 源字符串
     * @return 经过Base64编码的字符串
     */
    public static String toEncoded(String str) {
        try {
            str = Base64.encodeToString(str.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 转换一个字符串到一个日期时间字符串
     *
     * @param str
     * @return
     */
    public static String getDateTimeString(String str) {
        String ret = str;
        String[] arr = TextUtils.split(str, " ");
        if (arr.length > 1) {
            ret = arr[0] + " " + arr[1].substring(0, 5);
        }
        return ret;
    }

    /**
     * 转换一个字符串到一个日期字符串格式 yyyy-MM-dd
     *
     * @param str
     * @return
     */
    public static String getDateString(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        int index = TextUtils.indexOf(str, " ");
        str = TextUtils.substring(str, 0, index);
        return str;
    }

    /**
     * C#日期转换一个字符串到一个日期字符串格式 yyyy-MM-dd HH:mm:ss
     *
     * @param str
     * @return
     */
    public static String getDateSecond(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        str = str.replace("T", " ");
        if (str.indexOf(".") != -1) {
            str = str.substring(0, str.indexOf("."));
        }
        return str;
    }

    /**
     * 格式化double型字符串为int型
     *
     * @param doubleStr
     * @return
     */
    public static int formatPrice(String doubleStr) {
        if (doubleStr.indexOf(".") != -1) {
            String playernum = doubleStr.substring(0, doubleStr.indexOf("."));
            return Integer.parseInt(playernum);
        }
        if (TextUtils.isEmpty(doubleStr)) {
            return 0;
        }
        return Integer.parseInt(doubleStr);
    }

    /**
     * 获取设备型号
     *
     * @return
     */
    public static String getDevmodel() {
        return android.os.Build.MODEL + "";
    }

    /**
     * 数字每三位添加逗号
     *
     * @param data
     * @return
     */
    public static String formatTosepara(double data) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(data);
    }


    public static String checkNull(String s) {
        if (null == s || s.length() < 1 || s.equals("null") || TextUtils.isEmpty(s) || s.equals
                ("NULL")) {
            return "";
        } else {
            return s;
        }
    }

    public static float checkNullFloat(String s) {
        float a = 0.00f;
        try {
            if (TextUtils.isEmpty(checkNull(s))) {
                a = 0.00f;
            } else {
                a = Float.parseFloat(checkNull(s));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return a;
    }


}
