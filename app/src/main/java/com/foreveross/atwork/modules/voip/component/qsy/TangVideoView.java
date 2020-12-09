
package com.foreveross.atwork.modules.voip.component.qsy;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.voip.utils.qsy.PromptUtil;

import org.webrtc.videoengine.ViERenderer;


/**
 * @brief 加载/显示视频的控件
 */
public class TangVideoView extends RelativeLayout {

    public static final String TAG = TangVideoView.class.getSimpleName();
    protected ImageView mProgressBar; // 进度条
    protected Animation rotateAnimation;
    protected SurfaceView mSurfaceView; // 显示视频控件
    protected ImageView mCoverView;
    protected Context mContext;
    protected String bindUserID; // 当前View绑定的用户ID
    protected boolean isSmall;

    public TangVideoView(Context ctx, String bindUserID, boolean small) {
        super(ctx);
        mContext = ctx;
        this.bindUserID = bindUserID;
        this.isSmall = small;
        init();
    }

    public TangVideoView(Context ctx, AttributeSet attrs, String bindUserID) {
        super(ctx, attrs);
        mContext = ctx;
        this.bindUserID = bindUserID;
        init();
    }

    public SurfaceView getSurfaceView() {
        return mSurfaceView;
    }

    public void setSurfaceView(SurfaceView view){
        this.mSurfaceView = view;
    }

    public ImageView getCoverView(){
        return mCoverView;
    }

    public ImageView getProgressBar(){
        return mProgressBar;
    }

    public String getBindUserID(){
        return bindUserID;
    }

    protected void init() {
        rotateAnimation = AnimationUtils.loadAnimation(mContext, R.anim.tangsdk_rotate_loading);
        LinearInterpolator li = new LinearInterpolator();
        rotateAnimation.setInterpolator(li);
        if(isSmall){
            //mSurfaceView = new SurfaceView(mContext);
            mSurfaceView= ViERenderer.CreateRenderer(mContext, true);
            LayoutParams surfaceParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            surfaceParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            this.addView(mSurfaceView, surfaceParams);
        }else{
            //mSurfaceView = new SurfaceView(mContext);
            mSurfaceView= ViERenderer.CreateRenderer(mContext, true);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mSurfaceView.setLayoutParams(params);
            this.addView(mSurfaceView);
            mSurfaceView.setVisibility(GONE);

            mCoverView = new ImageView(mContext);
            mCoverView.setBackgroundColor(mContext.getResources().getColor(R.color.tangsdk_menu_dialog_text_color));
            LayoutParams coverViewparams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mCoverView.setLayoutParams(coverViewparams);
            this.addView(mCoverView);

            mProgressBar = new ImageView(mContext);
            mProgressBar.setBackgroundResource(R.mipmap.tangsdk_video_loading);
            LayoutParams progressParams = new LayoutParams(
                    (int) PromptUtil.convertDipToPx(mContext, 80), (int) PromptUtil.convertDipToPx(mContext, 80));
            progressParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            mProgressBar.setLayoutParams(progressParams);
            this.addView(mProgressBar);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * @brief 重新new一个surfaceview
     */
    public SurfaceView renewSurfaceView() {
        mCoverView.setVisibility(VISIBLE);
        SurfaceView videoSurface = this.getSurfaceView();
        if (videoSurface != null && videoSurface.getParent() != null) {
            videoSurface.setVisibility(View.INVISIBLE);
            this.removeView(videoSurface);
        }
        //videoSurface = new SurfaceView(mContext);
        videoSurface= ViERenderer.CreateRenderer(mContext, true);
        LayoutParams surfaceParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        surfaceParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        this.addView(videoSurface, 0, surfaceParams);
        this.setSurfaceView(videoSurface);
        return videoSurface;
    }

    /**
     * @brief 开始加载
     */
    public void onLoadStart() {
        mSurfaceView.setVisibility(View.GONE);
        if(!isSmall){
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.startAnimation(rotateAnimation);
        }
    }

    /**
     * @brief 完成加载
     */
    public void onLoadComplete() {
        mSurfaceView.setVisibility(View.VISIBLE);
        if(!isSmall){
            mProgressBar.setVisibility(View.INVISIBLE);
            mProgressBar.clearAnimation();
            mCoverView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * @brief 设置是否显示进度控件
     */
    public void setProgressBarVisible(int visibility){
        mProgressBar.setVisibility(visibility);
    }

    /**
     * @brief 释放内存
     */
    public void recycle() {
        Log.d(TAG, "recycle");
        mProgressBar = null;
        mSurfaceView = null;
        mContext = null;
    }
}
