package com.clubank.device;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;

import com.clubank.common.R;

@SuppressLint({ "SetJavaScriptEnabled", "Registered" })
public class WebViewActivity extends BaseActivity {
	WebView wv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		Bundle b = getIntent().getExtras();
		String url = b.getString("url");
		wv = (WebView) findViewById(R.id.webView);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setBuiltInZoomControls(true);
		wv.getSettings().setSupportZoom(true);
		wv.setWebViewClient(new MyWebViewClient(this));
		wv.getSettings().setUseWideViewPort(true);
		wv.setInitialScale(20);
		wv.getSettings().setLoadWithOverviewMode(true);
		wv.setBackgroundColor(0);
		wv.setWebViewClient(new MyWebViewClient(this));
		if (url != null) {
			wv.loadUrl(url);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {
			wv.goBack();
			return true;
		} else {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	// private int getScale() {
	// Display display = ((WindowManager)
	// getSystemService(Context.WINDOW_SERVICE))
	// .getDefaultDisplay();
	// int width = display.getWidth();
	// Double val = new Double(width) / new Double(PIC_WIDTH);
	// val = val * 100d;
	// return val.intValue();
	// }
}
