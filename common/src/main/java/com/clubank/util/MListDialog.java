package com.clubank.util;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.view.View;

import com.clubank.common.R;

import java.util.Arrays;

public class MListDialog {
	private Context context;
	private Dialog mDialog;
	private OnPositiveListener onPositiveListener;
	private OnNeutralListener onNeutralListener;

	public interface OnPositiveListener {
		void onSelected(View view, boolean[] selected);
	}

	public interface OnNeutralListener {
		void onSelected(View view, boolean[] selected);
	}

	public MListDialog(Context context) {
		this.context = context;
	}

	public void setOnSelectedListener(OnPositiveListener onPositiveListener) {
		this.onPositiveListener = onPositiveListener;
	}

	public void setOnNeutralListener(OnNeutralListener onNeutralListener) {
		this.onNeutralListener = onNeutralListener;
	}

	public void show(final View view, int resIdTitle, String[] captions,
                     final boolean[] selected) {
		show(view, resIdTitle, captions, selected, 0);
	}

	public void show(final View view, int resIdTitle, String[] captions,
                     final boolean[] selected, int thirdBtn) {

		final Builder d = new Builder(context);
		final boolean[] oldSelected = Arrays.copyOf(selected, selected.length);
		if (resIdTitle > 0) {
			d.setTitle(resIdTitle);
		}
		d.setMultiChoiceItems(captions, selected,
				new OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
						selected[which] = isChecked;
					}
				});
		d.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int clicked) {
				if (onPositiveListener != null) {
					onPositiveListener.onSelected(view, selected);
				}
				mDialog.dismiss();
			}
		});
		d.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						for (int i = 0; i < selected.length; i++) {
							selected[i] = oldSelected[i];// recover old value;
						}
						mDialog.dismiss();
					}
				});
		if (thirdBtn > 0) {
			d.setNeutralButton(thirdBtn, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// mDialog.dismiss();
					if (onNeutralListener != null) {
						onNeutralListener.onSelected(view, selected);
					}
				}
			});
		}
		mDialog = d.create();

		d.show();
	}
}
