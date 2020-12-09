package com.foreveross.atwork.component;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import androidx.fragment.app.FragmentActivity;

import com.foreverht.workplus.ui.component.dialogFragment.W6sLoadingDialog;
import com.foreveross.atwork.infrastructure.utils.LogUtil;

import java.util.Timer;
import java.util.TimerTask;


public class ProgressDialogHelper {

    /**
     * 表示显示
     */
    public static final int SHOW = 0;
    /**
     * 表示隐藏
     */
    public static final int DISMISS = 1;


    /**
     * 表示"不可点击取消的超时时间"已到
     * */
    public static final int CANNOT_TOUCH_EXPIRED = 2;

    /**
     * 加载框最大的显示时间
     */
    public static final int LOAD_MAX_TIME = 30 * 1000;

    private Context context;

    private FragmentActivity fragmentActivity;

    private String message = "";
    // 进度对话框
    private W6sLoadingDialog progressDialog = null;

    private Timer mTimer;

    private TimerTask mTimerTask;

    private UiHandler handler = new UiHandler();

    public ProgressDialogHelper(Context context) {

        this.context = context;
        progressDialog = new W6sLoadingDialog();
        if (this.context instanceof FragmentActivity) {
            fragmentActivity = (FragmentActivity)this.context;
        }

    }


    public boolean isShowing() {
        return false;
    }


    /**
     * 显示进度框
     * @param msgId ：提示文本
     */
    public void show(int msgId) {
        show(context.getString(msgId));
    }

    public void show(String message) {
        this.message = message;
        handler.sendEmptyMessage(SHOW);
    }

    public void show() {
        show("");
    }

    public void show(boolean canTouch) {
        show(canTouch, LOAD_MAX_TIME);
    }

    public void show(boolean canTouch, long expiredTime) {
        //如果设置外层点击取消无效情况下，开启一个计时器
        if (!canTouch && 0 < expiredTime) {
            startExpiredTimer(expiredTime);
        }

        handler.obtainMessage(SHOW, canTouch).sendToTarget();

        // TODO
        show("");
    }

    /**
     * 隐藏对话框
     */
    public void dismiss() {
        handler.sendEmptyMessage(DISMISS);
    }



    private void startExpiredTimer(long expiredTime) {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {

                if (mTimer == null) {
                    return;
                }
                if (progressDialog != null && handler != null) {
                    handler.obtainMessage(CANNOT_TOUCH_EXPIRED).sendToTarget();
                }
                mTimer.cancel();
                mTimer = null;
                mTimerTask = null;
            }
        };

        mTimer.schedule(mTimerTask, expiredTime);
    }

    public class UiHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            if (null != progressDialog) {
                switch (msg.what) {
                    case SHOW:
                        progressDialog.setTip(message);
                        progressDialog.setGif(com.foreverht.workplus.ui.component.R.mipmap.icon_loading_white);
                        if (msg.obj != null && msg.obj instanceof Boolean) {
                            progressDialog.setCancelable((boolean) msg.obj);
                        }
                        LogUtil.e("dialogShow", "" + System.currentTimeMillis());
                        try {
                            progressDialog.show(fragmentActivity);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case DISMISS:
                        try {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;


                    case CANNOT_TOUCH_EXPIRED:
                        progressDialog.setCancelable(true);

                        break;
                }

            }
        }
    }
}
