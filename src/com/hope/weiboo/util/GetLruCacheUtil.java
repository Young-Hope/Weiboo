package com.hope.weiboo.util;

import android.graphics.Bitmap;
import android.util.LruCache;

public class GetLruCacheUtil {
	private static LruCache<String, Bitmap> sLruCache = null;
	private final static int sMaxMemory = (int) Runtime.getRuntime().maxMemory();
	private final static int sCacheSize = sMaxMemory / 5;
	public static LruCache<String, Bitmap> getLruCache() {
		if (sLruCache != null) {
			return sLruCache;
		}
		sLruCache = new LruCache<String, Bitmap>(sCacheSize) {
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
			}
		};
		return sLruCache;
	}
}
