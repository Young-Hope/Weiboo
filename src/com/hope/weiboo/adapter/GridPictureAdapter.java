package com.hope.weiboo.adapter;

import java.util.List;


import com.hope.weiboo.R;
import com.hope.weiboo.activity.CustomImageView;
import com.hope.weiboo.util.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

public class GridPictureAdapter extends BaseAdapter{
	
	private List<String> mUrls; 
	private Context mContext;
	
	public GridPictureAdapter(List<String> urls, Context context) {
		super();
		this.mUrls = urls;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return mUrls.size();
	}

	@Override
	public Object getItem(int position) {
		return mUrls.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String url = mUrls.get(position);
		ViewHolder holder = null;
		
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.view_picture, null);
			holder.imgView = (ImageView) convertView.findViewById(R.id.pic_view);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
			if (url.equals(holder.url)) {
				return convertView;
			}
		}
		holder.url = url;

		Picasso.with(mContext)
			.load(url)
			.placeholder(R.drawable.default_pic)
			.error(R.drawable.ic_launcher)
			.resize(200, 200)
			.centerCrop()
			.into(holder.imgView);
		
//		ImageLoaderUtil util = new ImageLoaderUtil(mContext);
//		ImageLoaderConfiguration config = util.GetConfig();
//		DisplayImageOptions options = util.getOptions();
//		ImageLoader loader = ImageLoader.getInstance();
//		if (!loader.isInited()) {
//			loader.init(config);
//		}
//		loader.displayImage(url, holder.imgView, options);
		
		holder.imgView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, "大图", Toast.LENGTH_SHORT).show();
			}
		});
		return convertView;
	}
	
	class ViewHolder {
		public ImageView imgView;
		String url = "";
	}

}
