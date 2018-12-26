package com.clubank.device;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import com.clubank.util.WaitingDialog;

import java.io.File;

@SuppressLint("Registered")
public class DownloadActivity extends BaseActivity {
	private DownloadManager downloadManager;
	private IntentFilter filter;
	private MyReceiver receiver;
	private WaitingDialog wd;
	private long downloadId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		receiver = new MyReceiver();
	}

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(receiver, filter);
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	// http://app.golfbaba.com/app/android/UPPayPluginEx.apk
	public class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle b = intent.getExtras();
			long downId = b.getLong(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			if (downId != downloadId) {
				return;
			}
			// String action = intent.getAction();
			DownloadManager.Query q = new DownloadManager.Query();
			q.setFilterById(downId);
			Cursor c = downloadManager.query(q);
			if (wd != null) {
				wd.dismiss();
			}
			if (c.moveToFirst()) {
				int status = c.getInt(c
						.getColumnIndex(DownloadManager.COLUMN_STATUS));
				if (status == DownloadManager.STATUS_SUCCESSFUL) {
					// process download
					String uri = c.getString(c
							.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.setDataAndType(Uri.parse(uri),
							"application/vnd.android.package-archive");
					context.startActivity(i);
				}
			}
		}
	};

	@SuppressWarnings("deprecation")
	public void downloadFile(String url) {
		wd = new WaitingDialog(this);
		wd.show();
		int i = url.lastIndexOf('/');
		String fname = url.substring(i + 1);
		File file = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		file.mkdir();
		downloadManager = (DownloadManager) getSystemService(Activity.DOWNLOAD_SERVICE);
		Uri uri = Uri.parse(url);
		Request request = new Request(uri);
		request.setAllowedNetworkTypes(Request.NETWORK_MOBILE
				| Request.NETWORK_WIFI);
		request.setShowRunningNotification(true);
		request.setVisibleInDownloadsUi(true);
		request.setDestinationInExternalPublicDir(
				Environment.DIRECTORY_DOWNLOADS, fname);
		downloadId = downloadManager.enqueue(request);
		// TODO 把id保存好，在接收者里面要用，最好保存在Preferences里面
	}
}
