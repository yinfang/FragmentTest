package com.clubank.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clubank.common.R;
import com.clubank.device.BaseActivity;
import com.clubank.domain.C;
import com.clubank.util.CustomDialog.CancelProcessor;
import com.clubank.util.CustomDialog.Initializer;
import com.clubank.util.CustomDialog.OKProcessor;
import com.clubank.util.CustomDialog.OKProcessorDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class UI {
    public interface OkClickedListenr {
        void onClicked(Builder alertDialog, View view);
    }

    public static void showError(final Context context, CharSequence msg) {
        showError(context, msg, 0, null);
    }

    public static void showError(final Context context, final int msgId) {
        showError(context, context.getText(msgId), 0, null);
    }

    public static void showError(final Context context, final int msgId,
                                 int type, Object tag) {
        showError(context, context.getText(msgId), type, tag);
    }

    public static void showError(final Context context, final CharSequence msg,
                                 final int type, final Object tag) {

        CustomDialog cd = new CustomDialog(context);

        cd.setInitializer(new Initializer() {
            public void init(Builder alertDialog, View view) {
                alertDialog.setMessage(msg);
            }
        });
        cd.setOKProcessor(new OKProcessor() {
            public void process(Builder alertDialog, View view) {
                ((BaseActivity) context).processDialogOK(type, tag);
            }
        });
        cd.show(context.getText(R.string.error), false);
    }

    public static int getId(Context context, String resCode, String type) {
        int resId = context.getResources().getIdentifier(resCode, type,
                context.getPackageName());
        return resId;
    }

    public static View getView(Activity a, String resCode, String type) {
        return a.findViewById(getId(a, resCode, type));
    }

    public static CharSequence getText(Context context, String resCode) {
        int id = getId(context, resCode, "string");
        if (id > 0) {
            return context.getText(id);
        }
        return null;
    }

    public static void showInfo(Context context, int resId) {
        showInfo(context, context.getText(resId));
    }

    public static void showInfo(Context context, CharSequence msg) {
        showInfo(context, msg, 0);
    }

    public static void showInfo(Context context, CharSequence msg, int type) {
        showInfo(context, msg, type, null, true);
    }

    public static void showInfo(Context context, CharSequence msg, int type, boolean cancelable) {
        showInfo(context, msg, type, null, cancelable);
    }

    public static void showInfo(final Context context, final CharSequence msg,
                                final int type, final Object tag) {
        showInfo(context, msg, type, tag, true);
    }

    public static void showInfo(Context context, int resId, int type) {
        showInfo(context, context.getText(resId), type, null, true);
    }

    public static void showInfo(final Context context, final CharSequence msg,
                                final int type, final Object tag, final boolean cancelable) {
        CustomDialog cd = new CustomDialog(context);
        cd.setCancelable(cancelable);
        cd.setInitializer(new Initializer() {
            public void init(Builder alertDialog, View view) {
                alertDialog.setMessage(msg);
            }
        });
        cd.setOKProcessor(new OKProcessor() {
            public void process(Builder dialog, View view) {
                ((BaseActivity) context).processDialogOK(type, tag);
            }
        });
        cd.show(context.getText(R.string.msg_info), false);
    }

    public static void showOKCancel(final BaseActivity context, final int type,
                                    final CharSequence message, CharSequence title, final Object
                                            tag) {
        CustomDialog cd = new CustomDialog(context);
        cd.setInitializer(new Initializer() {
            public void init(Builder alertDialog, View view) {
                alertDialog.setMessage(message);
            }
        });
        cd.setOKProcessor(new OKProcessor() {
            public void process(Builder alertDialog, View view) {
                context.processDialogOK(type, tag);
            }
        });
        cd.setCancelProcessor(new CancelProcessor() {
            public void process(Builder alertDialog, View view) {
                context.processDialogCancel(type, tag);
            }
        });
        cd.show(title, true);
    }

    public static void showOKCancel(BaseActivity context, int type,
                                    int msgResId, int titleResId) {
        showOKCancel(context, type, msgResId, titleResId, null);
    }

    public static void showOKCancel(BaseActivity context, int type,
                                    int msgResId, int titleResId, Object tag) {
        showOKCancel(context, type, context.getText(msgResId),
                context.getText(titleResId), tag);
    }


    /**
     * 不是系统样式，按钮样式等均可在程序中改变
     *
     * @param type
     * @param title
     */
    public static void showDialog(final BaseActivity context, CharSequence title, CharSequence
            msg, boolean showCancel, final int type, final Object tag, int background) {

        CustomDialog cd = new CustomDialog(context);

        cd.setOKProcessorDialog(new OKProcessorDialog() {

            @Override
            public void process(Dialog dialog) {
                if (type != -1) {
                    context.processDialogOK(type, tag);
                }

                dialog.dismiss();
            }
        });

        cd.show(title, msg, showCancel, background);

    }

    /**
     * 不是系统样式，按钮文字样式等均可在程序中改变
     *
     * @param context
     * @param title
     * @param msg
     * @param showCancel
     * @param type
     * @param tag
     * @param background
     */
    public static void showDialog(final BaseActivity context, CharSequence title, CharSequence
            msg, CharSequence sure, CharSequence cancel, boolean showCancel, final int type,
                                  final Object tag, int background) {

        CustomDialog cd = new CustomDialog(context);
        context.setFinishOnTouchOutside(false);
        cd.setCancelable(false);
        cd.setOKProcessorDialog(new OKProcessorDialog() {

            @Override
            public void process(Dialog dialog) {
                if (type != -1) {
                    context.processDialogOK(type, tag);
                }

                dialog.dismiss();
            }
        });
        cd.setCancelProcessorDialog(new CustomDialog.CancelProcessorDialog() {
            @Override
            public void process(Dialog dialog) {
                if (type != -1) {
                    context.processDialogCancel(type, tag);
                }
                dialog.dismiss();
            }
        });
        cd.show(title, msg, sure, cancel, showCancel, background);

    }

    /**
     * 显示大图的dialog
     *
     * @param context
     * @param bitmap
     */
    public static void showImageDialog(BaseActivity context, Bitmap bitmap) {
        //放在custom中退出会出现两层界面
        final AlertDialog ad = new Builder(context).create();
        ad.show();
        Window window = ad.getWindow();
        window.setContentView(R.layout.image_dialog);
        ImageView image = (ImageView) ad.findViewById(R.id.image);
        image.setImageBitmap(bitmap);


    }

    public static void showOKDialog(final BaseActivity context, CharSequence msg, final int type,
                                    final Object tag) {
        showDialog(context, context.getText(R.string.confirm), msg, false, type, tag, 0);
    }

    public static void showOKDialog(final BaseActivity context, CharSequence msg, int background,
                                    final int type, final Object tag) {
        showDialog(context, context.getText(R.string.confirm), msg, false, type, tag, background);
    }

    public static void showErrorDialog(final BaseActivity context, CharSequence msg, int
            background) {
        showDialog(context, context.getText(R.string.error), msg, true, -1, null, background);
    }

    public static void showErrorDialog(final BaseActivity context, CharSequence msg) {
        showDialog(context, context.getText(R.string.error), msg, true, -1, null, 0);
    }

    public static void showInfoDialog(final BaseActivity context, CharSequence msg) {
        showDialog(context, context.getText(R.string.prompt), msg, false, -1, null, 0);
    }

    public static void showDialog(final BaseActivity context, CharSequence title, CharSequence
            msg, int buttonstyle) {
        showDialog(context, title, msg, false, -1, null, buttonstyle);
    }


    /**
     * 带取消和确认按钮的自定义对话框
     *
     * @param context
     * @param title
     * @param msg
     * @param buttonstyle
     */
    /*public static  void showDialogCancel(final BaseActivity context,CharSequence title,
    CharSequence msg,int buttonstyle){
        showDialog(context, title, msg,true,-1, null,buttonstyle);
	}*/
    public static void showDialogCancel(final BaseActivity context, CharSequence title,
                                        CharSequence msg, int type, int buttonstyle) {
        showDialog(context, title, msg, true, type, null, buttonstyle);
    }

    /**
     * 提示对话框，可设置按钮样式background=0不设置
     */
    public static void showInfoDialog(final BaseActivity context, CharSequence msg, int type, int
            background) {
        showDialog(context, context.getText(R.string.prompt), msg, false, type, null, background);
    }


    /**
     * ???????????????????????
     *
     * @param context
     * @param resId   ?????
     * @return
     */
    public static byte[] getBytes(Context context, int resId) {
        Resources resources = context.getResources();
        Bitmap bmp = BitmapFactory.decodeResource(resources, resId);
        return getBytes(bmp);
    }

    public static byte[] getBytes(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, C.compress, stream);
        byte[] b = stream.toByteArray();
        return b;
    }

    public static String getTempFile(String prefix, String suffix) {
        return Environment.getExternalStorageDirectory() + "/" + prefix
                + String.valueOf(System.currentTimeMillis()) + suffix;
    }

    public static int getPixel(Context context, int dps) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    /**
     * 获取屏幕高度（除掉顶部状态栏的高度）
     *
     * @param context
     * @return in pixels
     * @author zgh
     * @date 2014-12-24上午10:36:47
     */
    public static int getPureHeight(Context context) {
        int heightPixels = 0;
        try {
            Class<?> cl = null;
            Object obj = null;
            Field field = null;
            int x = 0, sbar = 0;
            cl = Class.forName("com.android.internal.R$dimen");
            obj = cl.newInstance();
            field = cl.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
            // 获取屏幕高度和宽度
            DisplayMetrics dm = new DisplayMetrics();
            ((BaseActivity) context).getWindow().getWindowManager()
                    .getDefaultDisplay().getMetrics(dm);
            heightPixels = dm.heightPixels - sbar;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return heightPixels;
    }

    public static String getEText(Activity a, int resId) {
        TextView tv = (TextView) a.findViewById(resId);
        if (tv != null) {
            return tv.getText().toString();
        }
        return "";
    }

    public static String getEText(View view, int resId) {
        TextView tv = (TextView) view.findViewById(resId);
        if (tv != null) {
            return tv.getText().toString();
        }
        return "";
    }

    public static void setEText(Activity a, int resId, CharSequence value) {
        if (value != null) {
            TextView tv = (TextView) a.findViewById(resId);
            if (tv != null) {
                tv.setText(value.toString().trim());
            }
        }
    }

    public static void setEText(View view, int resId, CharSequence value) {
        if (value != null) {
            TextView tv = (TextView) view.findViewById(resId);
            if (tv != null) {
                tv.setText(value.toString().trim());
            }
        }
    }

    public static void setEText(Activity a, int resId, int resValue) {
        ((TextView) a.findViewById(resId)).setText(resValue);
    }

    public static void setEText(View view, int resId, int resValue) {
        ((TextView) view.findViewById(resId)).setText(resValue);
    }

    public static int toPixel(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    /**
     * * This method converts device specific pixels to device independent
     * pixels. * * @param px A value in px (pixels) unit. Which we need to
     * convert into db * @param context Context to get resources and device
     * specific display metrics * @return A float value to represent db
     * equivalent to px value
     */
    @SuppressLint("NewApi")
    public static int toDp(Context context, float px) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return (int) dp;
    }

    public static Bitmap getBitmapFromURL(String url) {
        InputStream is = UI.getFromURL(url); // ???url?????
        if (is == null) {
            return null;
        }
        return BitmapFactory.decodeStream(is);
    }

    private static InputStream getFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3 * 1000);
            conn.setReadTimeout(60 * 1000);
            conn.setDoInput(true);
            conn.connect();
            return conn.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean validateEmail(String email) {
        return email != null
                && (!email.equals(""))
                && email.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\" +
                ".[A-Za-z0-9]+)*(\\.[A-Za-z]{2,5})$");
    }

    /*public static boolean checkMobile(String mobileNo) {
        return mobileNo != null && mobileNo.matches("^1[3578]{1}\\d{9}$");
    }*/
    /**
     * 最新手机号判断正则
     * 有效手机号集合：
     * 166，
     * 176，177，178
     * 180，181，182，183，184，185，186，187，188，189
     * 145，147
     * 130，131，132，133，134，135，136，137，138，139
     * 150，151，152，153，155，156，157，158，159
     * 198，199
     */
    public static boolean checkMobile(String mobileNo)
            throws PatternSyntaxException {
        String regExp = "^((13[0-9])|(15[^4])|(166)|(17[0-8])|(18[0-9])|(19[8-9])|(147,145))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(mobileNo);
        return m.matches();
    }

    public static boolean validateCardNo(String no) {
        String str = "(\\d{17}(\\d|X|x))|(\\d{15})";
        return no != null && (no.length() >= 15 && no.length() <= 18)
                && no.matches(str);
    }

    public static boolean validatePhoneAndFax(String in) {
        // ("^(0[0-9]{2,3}\\-)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?$");
        // 第1组：区号，以0开头，只允许输入0-9的数字，长度为2-3位，后面连接-号.
        // 第2组：固话，以2-9开头，只允许输入0-9的数字，长度为6-7位。
        // 第3组：分机号，前面连接-号,以0-9开头，长度为1-4位。
        return in != null && (!in.equals(""))
                && in.matches("^(0[0-9]{2,3}\\-)?([2-9][0-9]{6,15})?$");
        // var RegExp = ^\d{3,4}-\d{7,8}(-\d{3,4})?$;
    }

    public static Bitmap getBitmap(Context context, int resId) {
        Resources res = context.getResources();

        // return decodeSampledBitmapFromResource(res, resId, reqWidth,
        // reqHeight);
        return decodeSampledBitmapFromResource(res, resId, 270, 480);
    }

    /**
     * ???????????BMP???????????
     *
     * @param resId
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res,
                                                         int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static int getMonthInterval(String startDate, String endDate)
            throws Exception {
        int result = 0;
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(C.df_yMd.parse(startDate));
        c2.setTime(C.df_yMd.parse(endDate));
        result = c2.get(Calendar.MONDAY) - c1.get(Calendar.MONTH);
        return result == 0 ? 1 : Math.abs(result);
    }

    public static String handleString(String s) {
        String handleExpre = "</?[^<]+>\\s*|\t|\r|&nbsp;";
        return s.replaceAll(handleExpre, "");
    }

    public static void setBackground(ViewGroup vg, int color) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View v = vg.getChildAt(i);
            v.setBackgroundResource(color);
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽 3
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    public static void playBeep(final Context context, final int resSound,
                                final int duration) {
        Thread t = new Thread() {
            public void run() {
                MediaPlayer player = null;
                int countBeep = 0;
                while (countBeep < 1) {
                    player = MediaPlayer.create(context, resSound);
                    player.start();
                    countBeep += 1;
                    try {
                        Thread.sleep(player.getDuration() + duration);
                        player.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    public static void showToast(Context context, int resId) {
        showToast(context, context.getText(resId), Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, CharSequence msg) {
        showToast(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, int resId, int type) {
        showToast(context, context.getText(resId), type);
    }

    public static void showToast(Context context, CharSequence msg, int duration) {
//        Toast.makeText(context, msg, duration).show();
        if (!TextUtils.isEmpty(msg) && !msg.equals("null") && !msg.equals("NULL")) {
            ToastUtile.showText(context, msg.toString());
        }
    }

    public static void showInvalid(Context context, int resId) {
        String s0 = context.getString(R.string.invalid_input);
        String s1 = context.getString(resId);
        String s2 = String.format(s0, s1);
        showError(context, s2);
    }

    public static void showSuccess(Context context, int resId) {
        showSuccess(context, resId, 0);
    }

    public static void showSuccess(Context context, int resId, int type) {
        String s0 = context.getString(R.string.operation_success);
        String s1 = context.getString(resId);
        String s2 = String.format(s0, s1);
        showInfo(context, s2, type);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    //隐藏软键盘
    public static void hideSoftKeyboard(Activity a) {
        if (a.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams
                .SOFT_INPUT_STATE_HIDDEN) {
            if (a.getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) a.getSystemService
                        (Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(a.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    //显示软键盘
    public static void showSoftKeyboard(Activity a) {
        InputMethodManager imm = (InputMethodManager) a.getSystemService(Context
                .INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

    }

    /**
     * 护照
     *
     * @return
     */
    public static boolean checkPassport(String passport) {
        return passport != null && passport.matches("^1[45][0-9]{7}|G[0-9]{8}|P[0-9]{7}|S[0-9]{7," +
                "8}|D[0-9]+$");
    }

    /**
     * 身份证
     *
     * @return
     */
    public static boolean checkIdentityCard(String identityCard) {
        return identityCard != null && identityCard.matches("\\d{15}|\\d{18}");
    }
    /**
     * 验证输入的身份证号是否合法
     */
    public static boolean isLegalId(String id){
        if (id.toUpperCase().matches("(^\\d{15}$)|(^\\d{17}([0-9]|X)$)")){
            return true;
        }else {
            return false;
        }
    }
    /**
     * 车牌
     *
     * @return
     */
    public static boolean isCarNum(String carnumber) {
   /*
   1.常规车牌号：仅允许以汉字开头，后面可录入六个字符，由大写英文字母和阿拉伯数字组成。如：粤B12345；
   2.武警车牌：允许前两位为大写英文字母，后面可录入五个或六个字符，由大写英文字母和阿拉伯数字组成，其中第三位可录汉字也可录大写英文字母及阿拉伯数字，第三位也可空，如：WJ警00081、WJ京1234J、WJ1234X。
   3.最后一个为汉字的车牌：允许以汉字开头，后面可录入六个字符，前五位字符，由大写英文字母和阿拉伯数字组成，而最后一个字符为汉字，汉字包括“挂”、“学”、“警”、“军”、“港”、“澳”。如：粤Z1234港。
   4.新军车牌：以两位为大写英文字母开头，后面以5位阿拉伯数字组成。如：BA12345。
       */
        String carnumRegex = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[警京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼]{0,1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$";
        if (TextUtils.isEmpty(carnumber)) return false;
        else return carnumber.matches(carnumRegex);
    }

    public static void takePicture(Activity a, String file, int requestType) {
        Uri uri = Uri.fromFile(new File(file));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
        a.startActivityForResult(intent, requestType);
    }

    public static void choosePicture(Activity a, int requestType) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

            intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        }

        a.startActivityForResult(intent, requestType);
    }
}
