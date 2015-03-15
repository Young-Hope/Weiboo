package com.hope.weiboo.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class LoadImage {

	private String mUrl;
	
	public LoadImage(String url) {
		this.mUrl = url;
	}
	
	public Bitmap getImage(){
		Bitmap bm = null;

		URL url;
		HttpURLConnection conn;
		try {
			url = new URL(mUrl);
			conn = (HttpURLConnection) url.openConnection();
			if (conn.getResponseCode() == 200) {
				InputStream in = conn.getInputStream();
				bm = BitmapFactory.decodeStream(in);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		return bm;
	}
}
