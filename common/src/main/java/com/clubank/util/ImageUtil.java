package com.clubank.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.clubank.common.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtil {

    public static Bitmap getBitmap(byte[] b) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inJustDecodeBounds = false;
        opt.inSampleSize = 1; // width，hight设为原来的十分一
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        return BitmapFactory.decodeStream(bais, null, opt);
    }

    public static String saveTempBitmap(Bitmap bmp, String prefix, String suffix) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                File file = File.createTempFile(prefix, suffix,
                        Environment.getExternalStorageDirectory());
                FileOutputStream fos = new FileOutputStream(file);
                bmp.compress(CompressFormat.JPEG, 50, fos);
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

    public static String saveTempBitmap(String path, String prefix, String suffix) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                File file = File.createTempFile(prefix, suffix,
                        Environment.getExternalStorageDirectory());
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
                opts.inSampleSize = calculateInSampleSize(opts, 1280, 720);
                opts.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(path, opts);
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(CompressFormat.JPEG, 95, fos);
                fos.flush();
                fos.close();
                String filePath = file.getAbsolutePath();
                Log.d("ImageZoom", "ImageSize: " + getReadableFileSize(file.length()));
                return filePath;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * 计算图片的缩放值
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;//获取图片的高
        final int width = options.outWidth;//获取图片的宽
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;//求出缩放值
    }

    /**
     * 保存图片到磁盘上，类型统一为PNG
     *
     * @param context
     * @param bmp     要保存的图像
     * @param path    要保存的路径
     * @param fname   文件名。如果为空，则系统会生成一个
     */
    public static void saveImage(Context context, Bitmap bmp, String path,
                                 String fname) {
        File rootDir = Environment.getExternalStorageDirectory();
        InputStream in = bitmap2InputStream(bmp);
        // Used the File-constructor

        // Transfer bytes from in to out
        OutputStream out = null;
        byte[] buf = new byte[1024];
        int len;
        String msg = context.getString(R.string.save_picture_failed);
        if (fname == null || fname.equals("")) {
            fname = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
                    .format(new Date());
        }
        fname += ".png";
        File dir = new File(rootDir, path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            out = new FileOutputStream(new File(dir, fname));
            // A little more explicit
            while ((len = in.read(buf, 0, buf.length)) != -1) {
                out.write(buf, 0, len);
            }
            msg = context.getString(R.string.save_picture_as);
            msg = String.format(msg, dir.getAbsolutePath() + "/" + fname);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            try {
                in.close();
                out.close();
            } catch (Exception e) {
            }
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static InputStream drawable2InputStream(Drawable d) {
        Bitmap bitmap = drawable2Bitmap(d);
        return bitmap2InputStream(bitmap);
    }

    public static InputStream bitmap2InputStream(Bitmap bm, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(CompressFormat.PNG, quality, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    public static InputStream bitmap2InputStream(Bitmap bm) {
        return bitmap2InputStream(bm, 100);
    }

}
