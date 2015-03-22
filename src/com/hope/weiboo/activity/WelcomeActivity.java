package com.hope.weiboo.activity;

import com.hope.weiboo.R;
import com.hope.weiboo.constants.AuthConstant;
import com.hope.weiboo.io.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.Toast;

public class WelcomeActivity extends Activity {

	private ImageButton mLoginImageBtn = null;
	private Animation mAlphaAnimation = null;
	
	// 微博提供的接口
	private SsoHandler mSsoHandler;
	private AuthInfo mAuthInfo;
	
	/** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 取消标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // 从 SharedPreferences 中读取上次已保存好 AccessToken 等信息，
        // 第一次启动本应用，AccessToken 不可用
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        if (mAccessToken.isSessionValid()) {
        	startHomeActivity();
        	return;
        }
        setContentView(R.layout.activity_welcome);
        
        init();
        
        mLoginImageBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// SSO 授权, ALL IN ONE
				mSsoHandler.authorize(new AuthListener());
			}
		});
        
    }
    
    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     * 
     * @see {@link Activity#onActivityResult}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        
        startHomeActivity();
        
    }

	/**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    private class AuthListener implements WeiboAuthListener {
        
        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(WelcomeActivity.this, mAccessToken);
                Toast.makeText(WelcomeActivity.this, 
                        "授权成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = "授权失败";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(WelcomeActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

		@Override
		public void onCancel() {
			Toast.makeText(WelcomeActivity.this, 
                    "取消授权", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(WelcomeActivity.this, 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
    }

	private void init() {
		mLoginImageBtn = (ImageButton) findViewById(R.id.btn_login);
		mAuthInfo = new AuthInfo(getApplicationContext(), AuthConstant.APP_KEY, AuthConstant.REDIRECT_URL, AuthConstant.SCOPE);
		mSsoHandler = new SsoHandler(WelcomeActivity.this, mAuthInfo);
		
		// 设置动画
		mAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		mAlphaAnimation.setDuration(4000);
		mLoginImageBtn.startAnimation(mAlphaAnimation);
	}
	
	private void startHomeActivity() {
    	Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
	}
	
}
