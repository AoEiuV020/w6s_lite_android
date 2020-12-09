package com.foreveross.atwork.utils;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.foreveross.atwork.R;

/**
 * Created by LeoLiang on 2014年5月29日下午2:07:21 This class is used to manage the
 * global toast.
 */
public class ToastHelper {
	private static Toast mToast = null;
    private static final int DEFAULT_DRAWBLE = -1;
    
	public static void showDrawbleToast(Context context,String info, int drawble) {
			View view = View.inflate(context, R.layout.toast, null);
			setToastView(context,info, view,drawble);
			showPersionalToast(context,view, Toast.LENGTH_SHORT);
	}

	public static void showToast(Context context,String info, int duration) {
		View view = View.inflate(context, R.layout.toast, null);
		setToastView(context,info, view,DEFAULT_DRAWBLE);
		showPersionalToast(context,view, duration);
	}


	public static void showShortToast(Context context,String info) {
		View view = View.inflate(context, R.layout.toast, null);
		setToastView(context,info, view,DEFAULT_DRAWBLE);
		showPersionalToast(context,view, Toast.LENGTH_SHORT);
	}

	
	public static void showLongToast(Context context,String info) {
		View view = View.inflate(context, R.layout.toast, null);
		setToastView(context,info, view,DEFAULT_DRAWBLE);
		showPersionalToast(context,view,Toast.LENGTH_LONG);
	}

	private static void showPersionalToast(Context context,View view,int duration) {
		showPersionalToast(Gravity.CENTER,context,view,duration);
	}

	private static void showPersionalToast(int position,Context context,View view,int duration) {
		if(mToast != null)
			mToast.cancel();
		mToast = new Toast(context);
		mToast.setGravity(position, 0, 0);
		mToast.setDuration(duration);
		mToast.setView(view);
		mToast.show();

	}

	
	private static void setToastView(Context context,String info, View view,int drawble) {
		TextView toast_desc = (TextView) view.findViewById(R.id.toast_desc);
		toast_desc.setText(info);

	}
}
