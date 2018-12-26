package com.clubank.util;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import com.clubank.common.R;

public class WaitingDialog extends Dialog {

	public WaitingDialog(Context context) {
		super(context, R.style.WaitingDialog);
		LayoutParams lp = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.width = UI.getPixel(context, 50);
		lp.height = UI.getPixel(context, 50);
		addContentView(new ProgressBar(context), lp);
	}
}
