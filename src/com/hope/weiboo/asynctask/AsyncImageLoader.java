package com.hope.weiboo.asynctask;

import com.hope.weiboo.net.LoadImage;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

public class AsyncImageLoader extends AsyncTask<String, Void, Bitmap>{

	private int mWeight;
	private int mHeight;
	private ImageView mImageView;
	private LruCache<String, Bitmap> lruCache;
	
	
	
	public AsyncImageLoader(int mWeight, int mHeight, ImageView mImageView,
			LruCache<String, Bitmap> lruCache) {
		super();
		this.mWeight = mWeight;
		this.mHeight = mHeight;
		this.mImageView = mImageView;
		this.lruCache = lruCache;
	}



	@Override
	protected Bitmap doInBackground(String... params) {
		Bitmap bm = null;
		String url = params[0];
		if (mHeight != 0 && mWeight != 0) {
			LoadImage loader = new LoadImage(url);
			bm = loader.getImage();
			addBitmap2MemoryCache(url, bm);
		}
		
		return bm;
	}

	private void addBitmap2MemoryCache(String url, Bitmap bm) {
		if (lruCache.get(url) == null) {
			lruCache.put(url, bm);
		}
	}



	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		if (result != null) {
			mImageView.setImageBitmap(result);
		}
	}
}
