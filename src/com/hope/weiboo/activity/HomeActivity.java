package com.hope.weiboo.activity;

import java.util.ArrayList;

import com.hope.weiboo.R;
import com.hope.weiboo.adapter.WeiboListAdapter;
import com.hope.weiboo.constants.AuthConstant;
import com.hope.weiboo.io.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

public class HomeActivity extends Activity{

	
	/** 用于获取微博信息流等操作的API */
	private StatusesAPI mStatusesAPI;
	/** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    
    private WeiboListAdapter mAdapter;
    private ArrayList<Status> mStatusList;
    
    private RecyclerView mRecyclerView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 取消标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		
		// 获取当前已保存过的 Token
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(this, AuthConstant.APP_KEY, mAccessToken);
		mStatusesAPI.friendsTimeline(0L, 0L, 50, 1, false, 0, false, mListener);
		
		mStatusList = new ArrayList<Status>();
		mAdapter = new WeiboListAdapter(mStatusList);
		mRecyclerView = (RecyclerView) findViewById(R.id.weibo_list);
        
        // 设置LinearLayoutManager  
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));  
        // 设置ItemAnimator  
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());  
        // 设置固定大小  
        mRecyclerView.setHasFixedSize(false);  
        // 为mRecyclerView设置适配器  
        mRecyclerView.setAdapter(mAdapter);
	}
	
	private void updateList() {
		mAdapter.setStatusList(mStatusList);
		mAdapter.notifyDataSetChanged();
	}
	/**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                if (response.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number > 0) {
                    	mStatusList = statuses.statusList;
                    	updateList();
                    	String msg = "更新了" + mStatusList.size() + "条微博";
                    	Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } else if (response.startsWith("{\"created_at\"")) {
                    // 调用 Status#parse 解析字符串成微博对象
//                    Status status = Status.parse(response);
                } else {
                    Toast.makeText(HomeActivity.this, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(HomeActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };
}
