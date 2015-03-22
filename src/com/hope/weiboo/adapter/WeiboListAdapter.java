package com.hope.weiboo.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.hope.weiboo.R;
import com.hope.weiboo.util.GetLruCacheUtil;
import com.hope.weiboo.util.ImageLoaderUtil;
import com.hope.weiboo.view.NoScrollGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sina.weibo.sdk.openapi.models.Status;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WeiboListAdapter extends
		RecyclerView.Adapter<WeiboListAdapter.ViewHolder> {

	private List<Status> mStatusList;
	private Context mContext;
	
	public WeiboListAdapter(ArrayList<Status> statusList, Context context) {
		super();
		this.mStatusList = statusList;
		mContext = context;
	}

	public void setStatusList(List<Status> statusList) {
		this.mStatusList = statusList;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		
		// 原创微博内容
		private TextView mUserName;
		private TextView mCreateAt;
		private TextView mSource;
		private TextView mTxtShare;
		private TextView mTxtComment;
		private TextView mContent;
		private NoScrollGridView mGridView;
		private ImageView mUserPic;
		private ImageView mContentPic;
		
		// 转发微博内容
		private RelativeLayout mRepostBody;
		private TextView mReContent;
		private ImageView mReContentPic;
		private NoScrollGridView mReGridView;
		
		public ViewHolder(View view) {
			super(view);
			
			// 原创微博
			mGridView = (NoScrollGridView) view.findViewById(R.id.picture_grid);
			mUserName = (TextView) view.findViewById(R.id.user_name);
			mCreateAt = (TextView) view.findViewById(R.id.create_at);
			mSource = (TextView) view.findViewById(R.id.source);
			mTxtComment = (TextView) view.findViewById(R.id.txt_comment);
			mTxtShare = (TextView) view.findViewById(R.id.txt_share);
			mContent = (TextView) view.findViewById(R.id.content);
			mUserPic = (ImageView) view.findViewById(R.id.user_pic);
			mContentPic = (ImageView) view.findViewById(R.id.picture_one);
			
			// 转发微博
			mRepostBody = (RelativeLayout)view.findViewById(R.id.repost_body);
			mReContent = (TextView) view.findViewById(R.id.repost_content);
			mReContentPic = (ImageView)view.findViewById(R.id.repost_picture_one);
			mReGridView = (NoScrollGridView)view.findViewById(R.id.repost_pictures_grid);
		}

	}

	@Override
	public int getItemCount() {
		return mStatusList.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Status status = mStatusList.get(position);
		
		holder.mUserName.setText(status.user.name);
		holder.mCreateAt.setText(status.created_at);
		
		holder.mContent.setText(status.text);
		holder.mTxtComment.setText(status.comments_count + "");
		holder.mTxtShare.setText(status.reposts_count + "");

		// 设置微博时间
		String from = parseTime(status);
		holder.mCreateAt.setText(from);
		
		// 设置微博来源
		String source = parseSource(status);
		holder.mSource.setText(source);
		
		// 加载头像
		loadUserHeadPic(holder.mUserPic, status.user.profile_image_url);
		
		// 加载用户微博的配图
		loadPictureIfHas(holder, status, holder.mGridView, holder.mContentPic);
		
		Status retweetedStatus = status.retweeted_status;
		
		// 如果该微博不是转发微博
		if (retweetedStatus == null) {
			holder.mRepostBody.setVisibility(View.GONE);
			return;
		}
		holder.mRepostBody.setVisibility(View.VISIBLE);
		holder.mReContent.setVisibility(View.VISIBLE);
		holder.mReContent.setText("@" + retweetedStatus.user.name + ": " + retweetedStatus.text);
		loadPictureIfHas(holder, retweetedStatus, holder.mReGridView, holder.mReContentPic);
		
	}

	private void loadPictureIfHas(ViewHolder holder, Status status, NoScrollGridView gridView, ImageView imgView) {
		ArrayList<String> pic_urls = status.pic_urls;
		int size = 0;
		if (pic_urls == null) {
			// 取消图片的可见性
			imgView.setVisibility(View.GONE);
			gridView.setVisibility(View.GONE);
			return;
		}
		size = pic_urls.size();
		if(size == 1) {
			// 一张图片的情况
			gridView.setVisibility(View.GONE);
			imgView.setVisibility(View.VISIBLE);
			String bmiddle_pic = status.bmiddle_pic;
			
//			if (null != imgView.getTag() && imgView.getTag().equals(bmiddle_pic)) {
//				return;
//			}
			// 设置标志
			//imgView.setTag(bmiddle_pic);
			
			ImageLoaderUtil util = new ImageLoaderUtil(mContext);
			ImageLoaderConfiguration config = util.GetConfig();
			DisplayImageOptions options = util.getOptions();
			ImageLoader loader = ImageLoader.getInstance();
			
			if (!loader.isInited()) {
				loader.init(config);
			}
			loader.displayImage(bmiddle_pic, imgView, options);
		} else {
			imgView.setVisibility(View.GONE);
			gridView.setVisibility(View.VISIBLE);
			
			GridPictureAdapter adapter = new GridPictureAdapter(pic_urls, mContext);
			gridView.setAdapter(adapter);
		}
	}

	private void loadUserHeadPic(ImageView imgView, String url) {
		ImageLoaderUtil util = new ImageLoaderUtil(mContext);
		ImageLoaderConfiguration config = util.GetConfig();
		DisplayImageOptions options = util.getOptions();
		ImageLoader loader = ImageLoader.getInstance();
		if (null != imgView.getTag() && imgView.getTag().equals(url)) {
			return;
		}
		imgView.setTag(url);
		if (!loader.isInited()) {
			loader.init(config);
		}
		loader.displayImage(url, imgView, options);
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
