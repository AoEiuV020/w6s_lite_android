package com.foreveross.atwork.utils;

import android.content.Intent;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.support.AtworkConstants;


/**
 * Atwork自定义的Toast
 */
public class AtworkToast {

    private static final int DEFAULT_DRAWABLE = -1;
    private static Toast mToast = null;
    private static boolean mShowing = false;


    public static void sendToastDependOnActivity(int tipResId) {
        sendToastDependOnActivity(AtworkApplicationLike.getResourceString(tipResId));
    }

    public static void sendToastDependOnActivity(String tip) {
        Intent intent = new Intent(AtworkConstants.ACTION_TOAST);
        intent.putExtra(AtworkConstants.DATA_TOAST_STRING, tip);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).sendBroadcast(intent);
    }

    public static void showResToast(int resId, Object... formatArgs) {
        String info = AtworkApplicationLike.getResourceString(resId, formatArgs);
        showToast(info);
    }

    public static void showToast(String info) {
        try {
            View view = View.inflate(BaseApplicationLike.baseContext, R.layout.toast,
                    null);

            //相同的toast 过滤掉
            if(null != mToast && mShowing){
                String toastingMsg = (String) mToast.getView().getTag();
                if(toastingMsg.equals(info)){
                    return;
                }
            }
            view.setTag(info);
            setToastView(info, view, DEFAULT_DRAWABLE);
            showToast(view, Toast.LENGTH_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void showLongToast(String info) {
        View view = View.inflate(BaseApplicationLike.baseContext, R.layout.toast,
                null);
        setToastView(info, view, DEFAULT_DRAWABLE);
        showToast(view, Toast.LENGTH_LONG);
    }

    private static void showToast(View view, int duration) {
        showToastCenter(view, duration);
    }

    private static void showToastCenter(View view, int duration) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = new Toast(BaseApplicationLike.baseContext);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setView(view);
        showToast(duration);
    }

    /**
     * 如果duration <= 0 则默认选择的时间是 Toast.LENGTH_SHORT = 0 ，运行的时间为2s，
     * 如果duration = 1   则默认选择的时间是 Toast.LENGTH_LONG = 1  ，运行的时间为3.5s，
     * 其他运行的时间为 duration
     */
    private static void showToast(int duration) {
        int mSleepTime = 0;

        if(Toast.LENGTH_SHORT == duration){
            mSleepTime = 2000;
        }else if(Toast.LENGTH_LONG == duration){
            mSleepTime = 3500;
        }else{
            if(0 > duration){
                duration = 2000;
            }

            mSleepTime = duration;
        }

        if (0 < mSleepTime) {
            mToast.show();
            mShowing = true;
            new Handler().postDelayed(() -> {
                if (mToast != null){
                    mToast.getView().setTag("");
                    mToast.cancel();
                    mShowing = false;
                }

            }, mSleepTime);
        }
    }

    private static void setToastView(String info, View view, int drawable) {
        TextView toast_desc = view.findViewById(R.id.toast_desc);
        toast_desc.setText(info);
    }
}
