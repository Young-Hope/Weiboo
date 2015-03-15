package com.hope.weiboo.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.hope.weiboo.R;
import com.hope.weiboo.asynctask.AsyncImageLoader;
import com.sina.weibo.sdk.openapi.models.Status;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class WeiboListAdapter extends
		RecyclerView.Adapter<WeiboListAdapter.ViewHolder> {

	public List<Status> statusList;

	private final int mMaxMemory = (int) Runtime.getRuntime().maxMemory();
	private final int mCacheSize = mMaxMemory / 5;
	private LruCache<String, Bitmap> mLruCache = new LruCache<String, Bitmap>(mCacheSize) {
		protected int sizeOf(String key, Bitmap bitmap) {
			return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
		};
	};
	
	public WeiboListAdapter(ArrayList<Status> statusList) {
		super();
		this.statusList = statusList;
	}

	public void setStatusList(List<Status> statusList) {
		this.statusList = statusList;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		private ImageView mUserPic;
		private ImageView mShare;
		private ImageView mComment;

		private TextView mUserName;
		private TextView mCreateAt;
		private TextView mSource;
		private TextView mTxtShare;
		private TextView mTxtComment;
		private TextView mContent;

		public ViewHolder(View view) {
			super(view);
			mUserName = (TextView) view.findViewById(R.id.user_name);
			mCreateAt = (TextView) view.findViewById(R.id.create_at);
			mSource = (TextView) view.findViewById(R.id.source);
			mTxtComment = (TextView) view.findViewById(R.id.txt_comment);
			mTxtShare = (TextView) view.findViewById(R.id.txt_share);
			mContent = (TextView) view.findViewById(R.id.content);

			mUserPic = (ImageView) view.findViewById(R.id.user_pic);
			mShare = (ImageView) view.findViewById(R.id.img_share);
			mComment = (ImageView) view.findViewById(R.id.img_comment);
		}

	}

	@Override
	public int getItemCount() {
		return statusList.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Status status = statusList.get(position);
		holder.mUserName.setText(status.user.name);
		holder.mCreateAt.setText(status.created_at);
		
		holder.mContent.setText(status.text);
		holder.mTxtComment.setText(status.comments_count + "");
		holder.mTxtShare.setText(status.reposts_count + "");

		String from = parseTime(status);
		holder.mCreateAt.setText(from);
		
		String source = parseSource(status);
		holder.mSource.setText(source);
		
		loadUserHeadPic(holder.mUserPic, status.user.profile_image_url);
	}

	private void loadUserHeadPic(ImageView imgView, String url) {
		Bitmap bm = mLruCache.get(url);
		if (bm != null) {
			imgView.setImageBitmap(bm);
		} else {
			AsyncImageLoader imageLoader = new AsyncImageLoader(50, 50, imgView, mLruCache);
			imgView.setBackgroundResource(R.drawable.kenan);
			imageLoader.execute(url);
		}
	}

	private String parseSource(Status status) {
		String url = status.source;
		int begin = url.indexOf(">");
		int end = url.indexOf("</a>");
		if (begin != -1 && end != -1) {
			return url.substring(begin + 1, end);
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	private String parseTime(Status status) {
		// 提取时间
		String format = "EEE MMM dd HH:mm:ss Z yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.ENGLISH);
		Date createdDate = null;
		try {
			createdDate = (Date) sdf.parse(status.created_at);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (createdDate != null) {
			Date now = new Date();
			int nowTime = (int)(now.getTime() / 1000);
			int createTime = (int)(createdDate.getTime() / 1000);
			
			String from = null;
			int diff = nowTime - createTime;
			if (diff < 60) {
				from = "刚刚";
			} else if (diff < 3600) {
				from = diff / 60 + "分钟前";
			} else if (diff < 3600 * 24) {
				from = diff / 3600 + "小时前";
			} else if (diff < 3600 * 24 * 365){
				int today = now.getDay();
				int createdDay = createdDate.getDay();
				from = today - createdDay + "天前";
			} else {
				int year = now.getYear();
				int createdYear = createdDate.getYear();
				from = year - createdYear + "年前";
			}
			return from;
		}
		return null;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int arg1) {
		// 给ViewHolder设置布局文件
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(
				R.layout.weibo_card, viewGroup, false);
		return new ViewHolder(v);
	}
}
