package com.clubank.device;

import android.view.View;
import android.widget.ImageView;

import com.clubank.common.R;
import com.clubank.util.MyData;
import com.clubank.util.MyRow;

public class DialogListAdapter extends BaseAdapter {
	boolean showImage;

	public DialogListAdapter(BaseActivity a, MyData data, boolean showImage) {
		super(a, R.layout.dialog_list_item, data);
		this.showImage = showImage;
	}

	@Override
	protected void display(int position, View v, MyRow o) {
		super.display(position, v, o);
		ImageView iv = (ImageView) v.findViewById(R.id.imageView1);
		if (showImage) {
			int image = o.getInt("image");
			if (image > 0) {
				iv.setImageResource(image);
				iv.setVisibility(View.VISIBLE);
			} else {
				iv.setVisibility(View.INVISIBLE);
			}
		} else {
			iv.setVisibility(View.GONE);
		}
		setEText(v, R.id.name, o.getString("name"));
	}

}
