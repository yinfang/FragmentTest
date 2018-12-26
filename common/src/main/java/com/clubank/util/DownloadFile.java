package com.clubank.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.clubank.common.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

//usually, subclasses of AsyncTask are declared inside the activity class. 
// that way, you can easily modify the UI thread from here 
public class DownloadFile extends AsyncTask<String, Integer, String> {

    ProgressDialog pdialog;
    Context context;

    public DownloadFile(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... sUrl) {
        try {
            URL url = new URL(sUrl[0]);
            URLConnection connection = url.openConnection();
            connection.connect();
            // this will be useful so that you can show a typical 0-100%
            // progress bar
            int fileLength = connection.getContentLength();
            // download the file
            InputStream input = new BufferedInputStream(url.openStream());
            File f = File.createTempFile("temp_name", "apk", context.getCacheDir());
//            File f = new File( Environment.getExternalStorageDirectory() + "", System.currentTimeMillis() + ".apk");
            String filePath = f.getAbsolutePath();

            OutputStream output = new FileOutputStream(f);
            byte data[] = new byte[1024];
            long total = 0;
            int count;
            int last_percent = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                int percent = (int) (total * 100.00 / fileLength);
                if (percent > last_percent) {
                    publishProgress(percent);
                }
                last_percent = percent;
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();

            String command = "chmod 777 " + filePath;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判断版本大于等于7.0
                uri = FileProvider.getUriForFile(context, VersionUtil.getPackageName(context) + ".fileprovider", f);  // 参数二为apk的包名加上.fileprovider
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 给目标应用一个临时授权
            } else {
                uri = Uri.fromFile(f);
            }
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            context.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pdialog = new ProgressDialog(context);
        // dialog.setTitle("Indeterminate");
        pdialog.setMessage(context.getText(R.string.downloading_please_wait));
        pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        pdialog.setIndeterminate(false);
        pdialog.setMax(100);
        pdialog.incrementProgressBy(1);
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.setCancelable(true);
        pdialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        if (progress[0] >= 100) {
            pdialog.dismiss();
        }
        pdialog.setProgress(progress[0]);
    }
}
