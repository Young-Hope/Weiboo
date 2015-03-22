package com.hope.weiboo.util;

import android.content.Context;

public class DisplayUtil {
	public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    } 
}
