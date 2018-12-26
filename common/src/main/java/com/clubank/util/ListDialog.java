package com.clubank.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.clubank.common.R;
import com.clubank.device.BaseActivity;
import com.clubank.device.DialogListAdapter;
import com.clubank.domain.C;

public class ListDialog {
	private BaseActivity a;
	private OnSelectedListener onSelectedListener;

	public ListDialog(BaseActivity a) {
		this.a = a;
		if(C.dialogTheme!=-1){
			 a.setTheme(C.dialogTheme);
			}
	}

	public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
		this.onSelectedListener = onSelectedListener;
	}

	@SuppressLint("InflateParams")
	public void show(final View view, int resIdTitle, String[] captions,
                     int[] images) {
		if (images != null && images.length != captions.length) {
			throw new RuntimeException(
					"List dialog: defined images number not match captions!");
		}
		AlertDialog.Builder ad = new AlertDialog.Builder(a);
		if (resIdTitle > 0) {
			
			ad.setTitle(resIdTitle);
		}
		View v = LayoutInflater.from(a).inflate(R.layout.dialog_list, null);
		MyData data = new MyData();
		for (int i = 0; i < captions.length; i++) {
			MyRow row = new MyRow();
			if (images != null) {
				row.put("image", images[i]);
			}
			row.put("name", captions[i]);
			data.add(row);
		}
		DialogListAdapter adapter = new DialogListAdapter(a, data,
				images != null);
		ListView lv = (ListView) v.findViewById(R.id.listView1);
		lv.setAdapter(adapter);
		ad.setView(v);
		Dialog d = ad.create();
		lv.setOnItemClickListener(new MyListSelectedListener(d, view));
		d.show();
	}

	public interface OnSelectedListener {
		void onSelected(View view, int index);
	}

	class MyListSelectedListener implements OnItemClickListener {
		Dialog dialog;
		View view;

		MyListSelectedListener(Dialog dialog, View view) {
			this.dialog = dialog;
			this.view = view;
		}

		public void onItemClick(AdapterView<?> parent, View v, int pos,
                                long arg3) {
			if (onSelectedListener != null) {
				onSelectedListener.onSelected(view, pos);
			}
			dialog.dismiss();
		}
	}
}
