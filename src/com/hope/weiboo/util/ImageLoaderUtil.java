package com.hope.weiboo.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.hope.weiboo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class ImageLoaderUtil {
	private Context mContext;
	private ImageLoaderConfiguration mConifg = null;
	private DisplayImageOptions options = null; 
	
	public ImageLoaderUtil(Context mContext) {
		super();
		this.mContext = mContext;
	}



	public  ImageLoaderConfiguration GetConfig() {
		
		mConifg = new ImageLoaderConfiguration
			.Builder(mContext)
			.memoryCacheExtraOptions(480, 800)
			.threadPoolSize(3)
			.memoryCacheSize(2 * 1024 * 1024)
			.build();
		
		return mConifg;
	}
	
	public DisplayImageOptions getOptions() {
		options = new DisplayImageOptions
			.Builder()
			.showImageOnFail(R.drawable.default_pic)
			.showImageOnLoading(R.drawable.default_pic)
			.showImageForEmptyUri(R.drawable.default_pic)
			.cacheInMemory(true)
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.resetViewBeforeLoading(true)
			.build();	
		return options;
	}
}

