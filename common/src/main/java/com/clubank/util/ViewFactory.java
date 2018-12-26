package com.clubank.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.clubank.common.R;
import com.clubank.device.BaseActivity;

/**
 * ImageView创建工厂
 */
public class ViewFactory {

	/**
	 * 获取ImageView视图的同时加载显示url
	 * 
	 * @param text
	 * @return
	 */
	public static ImageView getImageView(Context context, String url) {
		ImageView imageView = (ImageView) LayoutInflater.from(context).inflate(
				R.layout.view_banner, null);
		((BaseActivity) context).setImage(imageView, url);
		
		return imageView;
	}
}
