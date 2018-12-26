package com.clubank.device;

import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.clubank.util.WaitingDialog;

public class MyWebViewClient extends WebViewClient {
	private Context context;
	private WaitingDialog wd;

	public MyWebViewClient(Context context) {
		this.context = context;
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		view.loadUrl(url);
		return true;
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		wd = new WaitingDialog(context);
		wd.show();
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		wd.dismiss();
	}

	@Override
	public void onReceivedError(WebView view, int erroCode, String description,
                                String failingUrl) {
		wd.dismiss();
	}

}
