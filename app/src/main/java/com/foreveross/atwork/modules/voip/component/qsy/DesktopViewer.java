package com.foreveross.atwork.modules.voip.component.qsy;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMemberWrapData;
import com.foreveross.atwork.modules.voip.fragment.qsy.QsyCallFragment;
import com.foreveross.atwork.modules.voip.support.qsy.TangSDKInstance;
import com.foreveross.atwork.modules.voip.support.qsy.tangsdkwapper.DesktopShareSessionController;
import com.foreveross.atwork.modules.voip.utils.qsy.PromptUtil;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Ranger Liao on 2016/4/5.
 */
public class DesktopViewer implements View.OnClickListener,
        View.OnLongClickListener,View.OnLayoutChangeListener {
    private static final String TAG = "DesktopViewer";

    private ImageView mBack;
    private ImageView mDesktopViewer;
    private Activity mActivity;
    private QsyCallFragment mCallFragment;

    private ViewGroup mDesktopRootLayout = null;
    private ViewGroup mDesktopLayout = null;
    private ViewGroup mGuideLayout = null;

    private ImageView mProgressBar; // 进度条
    private Animation rotateAnimation;
    private TextView mTitle;
    private ViewGroup mTitleBar;
    private boolean mShowGuide;

    private Timer mFullScreenTimer;
    private TimerTask mFullScreenTimerTask;

    private Timer mDalayUnloadTimer;
    private TimerTask mDalayUnloadTimerTask;

    private boolean mNeedStartView; //add for delay unload


    public ViewGroup loadView(QsyCallFragment callFragment, ViewGroup parent, boolean bShowGuide ) {
        mNeedStartView = false;
        mCallFragment = callFragment;
        mActivity = mCallFragment.getActivity();
        mShowGuide = bShowGuide;
        mDesktopRootLayout = parent;
        if(mDesktopLayout == null){
            mDesktopRootLayout.setVisibility(View.VISIBLE);
            mDesktopLayout = (ViewGroup) LayoutInflater.from(mActivity).inflate(R.layout.tangsdk_desktop_view_layout, mDesktopRootLayout, true);
            mDesktopLayout.setVisibility(View.GONE);
            initView();
            initData();
            initListener();
            mNeedStartView = true;
        }

        Animation layoutAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.tangsdk_viewgroup_enter);

        mDesktopLayout.setAnimation(layoutAnimation);
        layoutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(mNeedStartView){
                    TangSDKInstance.getInstance().desktopStartView(mDesktopViewer);
                    onLoadStart();
                } else{
                    switchToFullScreen(false);
                    startFullScreenCountDown();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mDesktopLayout.setVisibility(View.VISIBLE);
        return mDesktopLayout;
    }

    public void unloadView(boolean bCanDelayUnload){
        if(mDesktopViewer == null){
            return;
        }
        stopFullScreenCountDown();

        Animation layoutAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.tangsdk_viewgroup_exit);
        mDesktopLayout.setAnimation(layoutAnimation);
        layoutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startDelayUnloadCountDown();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mDesktopLayout.setVisibility(View.GONE);

        if(!bCanDelayUnload){
            cleanData();
        }

        mCallFragment.setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void cleanData(){
        if(mGuideLayout == null){
            return;
        }

        TangSDKInstance.getInstance().desktopStopView();

        stopFullScreenCountDown();
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressBar.clearAnimation();
        mTitleBar.clearAnimation();

        mDesktopRootLayout.removeAllViews();
        mDesktopRootLayout.setVisibility(View.GONE);
        mDesktopRootLayout = null;
        mDesktopLayout = null;

        mBack.setOnClickListener(null);
        mBack = null;
        mDesktopViewer.removeOnLayoutChangeListener(DesktopViewer.this);
        mDesktopViewer.setOnTouchListener(null);
        mDesktopViewer = null;
        mActivity = null;
        mGuideLayout = null;
        mTitle = null;
        mTitleBar = null;
    }
    public boolean isLoaded(){
        return mDesktopViewer != null;
    }

    public boolean isVisible(){
        return (mDesktopLayout != null && mDesktopLayout.getVisibility() == View.VISIBLE);
    }

    public void onDesktopViewerShowed(){
        onLoadComplete();

        if(mShowGuide){
            if(mDesktopLayout.getWidth() < mDesktopLayout.getHeight()){
                showGuide(true);
            }
        }
        startFullScreenCountDown();
    }

    private void initListener() {
        mBack.setOnClickListener(this);
    }


    private void initView() {
        mBack = (ImageView) mActivity.findViewById(R.id.desktop_back_btn);
        mBack.setVisibility(View.VISIBLE);
        mBack.setOnClickListener(this);

        mDesktopViewer = (ImageView) mActivity.findViewById(R.id.desktopView);
        mDesktopViewer.setVisibility(View.VISIBLE);
        mDesktopViewer.addOnLayoutChangeListener(this);
        mDesktopViewer.setOnTouchListener(imageViewTouchListener);

        rotateAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.tangsdk_rotate_loading);
        LinearInterpolator li = new LinearInterpolator();
        rotateAnimation.setInterpolator(li);


        mProgressBar = new ImageView(mActivity);
        mProgressBar.setBackgroundResource(R.mipmap.tangsdk_video_loading);
        RelativeLayout.LayoutParams progressParams = new RelativeLayout.LayoutParams(
                (int) PromptUtil.convertDipToPx(mActivity, 80), (int) PromptUtil.convertDipToPx(mActivity, 80));
        progressParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mProgressBar.setLayoutParams(progressParams);
        mDesktopLayout.addView(mProgressBar);
        mProgressBar.setVisibility(View.GONE);

        mGuideLayout = (ViewGroup) mActivity.findViewById(R.id.desktop_landscape_screen_guide);

        mTitleBar =  (ViewGroup) mActivity.findViewById(R.id.title_bar);
        mTitle = (TextView) mActivity.findViewById(R.id.title_desktop);
        VoipMeetingMemberWrapData userItemData= TangSDKInstance.getInstance().getDesktopSharerUserData();
        if(userItemData != null){
            String strUserName = userItemData.getUserEntity().mUserId;
            if(!TextUtils.isEmpty(strUserName)){
                String strTitle = String.format(mActivity.getString(R.string.tangsdk_desktopshare_title_format), strUserName);
                mTitle.setText(strTitle);
            }
        }
    }

    private void initData() {

    }

    private void showGuide(boolean bShow){
        if(bShow){
            if(mGuideLayout.getVisibility() == View.VISIBLE){
                return;
            }
            mGuideLayout.setVisibility(View.VISIBLE);
            mGuideLayout.setAlpha(1.0f);
            //hide it after 3 seconds
            mGuideLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mActivity == null){
                        return;
                    }
                    Animation hideAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.tangsdk_hide_view_gradually);
                    if(hideAnimation != null && mGuideLayout != null){
                        mGuideLayout.setAnimation(hideAnimation);
                        mGuideLayout.setVisibility(View.GONE);
                    }
                }
            },2000);
        } else{
            if(mGuideLayout.getVisibility()== View.GONE){
                return;
            }
            mGuideLayout.clearAnimation();
            mGuideLayout.setAlpha(0.0f);
            mGuideLayout.setVisibility(View.GONE);
        }
    }
    /**
     * @brief 全屏倒计时启动
     */
    private void startFullScreenCountDown() {
        stopFullScreenCountDown();
        mFullScreenTimerTask = new TimerTask() {
            @Override
            public void run() {

                if(mDesktopLayout!=null){
                    mDesktopLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            switchToFullScreen(true);
                            stopFullScreenCountDown();
                        }
                    });
                }
            }
        };
        mFullScreenTimer = new Timer();
        mFullScreenTimer.schedule(mFullScreenTimerTask, 5*1000);
    }

    /**
     * @brief 停止全屏计时并重置
     */
    private void stopFullScreenCountDown() {
        if (mFullScreenTimerTask != null) {
            mFullScreenTimerTask.cancel();
            mFullScreenTimerTask = null;
        }
        if (mFullScreenTimer != null) {
            mFullScreenTimer.cancel();
            mFullScreenTimer = null;
        }
    }


    private void startDelayUnloadCountDown() {
        stopDelayUnloadCountDown();
        mDalayUnloadTimerTask = new TimerTask() {
            @Override
            public void run() {
                if(mDesktopLayout!=null)
                {
                    mDesktopLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            cleanData();
                            stopDelayUnloadCountDown();
                        }
                    });
                }
            }
        };
        mDalayUnloadTimer = new Timer();
        mDalayUnloadTimer.schedule(mDalayUnloadTimerTask, 15*1000);
    }


    private void stopDelayUnloadCountDown() {
        if (mDalayUnloadTimerTask != null) {
            mDalayUnloadTimerTask.cancel();
            mDalayUnloadTimerTask = null;
        }
        if (mDalayUnloadTimer != null) {
            mDalayUnloadTimer.cancel();
            mDalayUnloadTimer = null;
        }
    }

    private void switchToFullScreen(boolean bFullScreen) {
        if (bFullScreen){
            if(mTitleBar.getVisibility() != View.GONE) {
                Animation hideAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.tangsdk_hide_view_gradually);
                mTitleBar.setAnimation(hideAnimation);
                mTitleBar.setVisibility(View.GONE);
            }
        } else {
            if(mTitleBar.getVisibility() != View.VISIBLE){
                mTitleBar.clearAnimation();
                mTitleBar.setAlpha(0.9f);
                mTitleBar.setVisibility(View.VISIBLE);
            }
        }
    }

    private  boolean isFullScreen(){
        return (mTitleBar.getVisibility() != View.VISIBLE);
    }


    /**
     * @brief 开始加载
     */
    public void onLoadStart() {
        mDesktopViewer.setVisibility(View.GONE);

        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.startAnimation(rotateAnimation);
    }

    /**
     * @brief 完成加载
     */
    public void onLoadComplete() {
        mDesktopViewer.setVisibility(View.VISIBLE);

        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressBar.clearAnimation();
    }

    /**
     * @brief 设置是否显示进度控件
     */
    public void setProgressBarVisible(int visibility){
        mProgressBar.setVisibility(visibility);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.desktop_back_btn){
            unloadView(true);
        }

    }

    @Override
    public boolean onLongClick(View v) {
        return true;
    }

    @Override
    public  void onLayoutChange(View var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9){
        if(mDesktopLayout.getWidth() > mDesktopLayout.getHeight()){
            showGuide(false);
        }
        doFitInCenter();
    }

    private boolean isZoomOutView(){
        DesktopShareSessionController desktopShareSessionController = TangSDKInstance.getInstance().getDesktopShareSession();
        if(desktopShareSessionController == null){
            return false;
        }
        long nDesktopWidth = desktopShareSessionController.getShareDesktopWidth();
        long nDesktopHeight = desktopShareSessionController.getShareDesktopHeight();
        double scale = desktopShareSessionController.getZoom();

        int nViewWidth = mDesktopViewer.getWidth();
        int nViewHeight = mDesktopViewer.getHeight();
        return !(nDesktopWidth * scale <= nViewWidth && nDesktopHeight * scale <= nViewHeight);

    }

    private void doFitInCenter(){
        DesktopShareSessionController desktopShareSessionController = TangSDKInstance.getInstance().getDesktopShareSession();
        if(desktopShareSessionController == null){
            return ;
        }
        long nDesktopWidth = desktopShareSessionController.getShareDesktopWidth();
        long nDesktopHeight = desktopShareSessionController.getShareDesktopHeight();

        int nViewWidth = mDesktopViewer.getWidth();
        int nViewHeight = mDesktopViewer.getHeight();

        double ratioDesktop = (double)nDesktopWidth/(double)nDesktopHeight;
        double ratioView = (double)nViewWidth/(double)nViewHeight;
        double scale = 1.0;
        int nScrollX = 0;
        int nScrollY = 0;
        if(ratioDesktop < ratioView){
            scale =  (double)nViewHeight/(double)nDesktopHeight;
            nScrollX = (int)(nViewWidth - nDesktopWidth*scale)/2;
            nScrollY=  0;
        } else{
            scale = (double)nViewWidth/(double)nDesktopWidth;
            nScrollX = 0;
            nScrollY= (int)(nViewHeight - nDesktopHeight*scale)/2;
        }

        desktopShareSessionController.zoomView(scale,0,0);
        desktopShareSessionController.scroll(nScrollX,nScrollY);
    }

    private void doDockInEdge(){
        DesktopShareSessionController desktopShareSessionController = TangSDKInstance.getInstance().getDesktopShareSession();
        if(desktopShareSessionController == null){
            return ;
        }
        long nDesktopWidth = desktopShareSessionController.getShareDesktopWidth();
        long nDesktopHeight = desktopShareSessionController.getShareDesktopHeight();

        int nViewWidth = mDesktopViewer.getWidth();
        int nViewHeight = mDesktopViewer.getHeight();
        double scale = desktopShareSessionController.getZoom();

        int nScrollX = desktopShareSessionController.getScrollPosX();
        int nScrollY = desktopShareSessionController.getScrollPosY();

        if(nDesktopWidth*scale < nViewWidth){
            nScrollX = (int)(nViewWidth - nDesktopWidth*scale)/2;
        } else{
            if(nScrollX > 0){
                nScrollX = 0;
            } else if(nScrollX + nDesktopWidth*scale < nViewWidth){
                nScrollX = (int)(nViewWidth - nDesktopWidth*scale);
            }
        }

        if(nDesktopHeight*scale < nViewHeight){
            nScrollY = (int)(nViewHeight - nDesktopHeight*scale)/2;
        }else{
            if(nScrollY > 0){
                nScrollY = 0;
            } else if(nScrollY + nDesktopHeight*scale < nViewHeight){
                nScrollY = (int)(nViewHeight - nDesktopHeight*scale);
            }
        }
        desktopShareSessionController.scroll(nScrollX,nScrollY);
    }


    private View.OnTouchListener imageViewTouchListener = new View.OnTouchListener() {

        double baseValue = -1;
        float startX = 0f;
        float startY = 0f;
        float originX = 0f;
        float originY = 0f;
        int zoomCenterX = 0;
        int zoomCenterY = 0;
        double originScale = 1.0f;
        boolean bZoom = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                showGuide(false);
                if(!isFullScreen()){
                    switchToFullScreen(true);
                } else{
                    switchToFullScreen(false);
                    startFullScreenCountDown();
                }

                startX = event.getX();
                startY = event.getY();
                baseValue = -1;

                originScale = 1.0f;
                originX = 0f;
                originY = 0f;
                bZoom = false;
                DesktopShareSessionController desktopShareSessionController = TangSDKInstance.getInstance().getDesktopShareSession();
                if(desktopShareSessionController !=null){
                    originScale = desktopShareSessionController.getZoom();
                    originX = desktopShareSessionController.getScrollPosX();
                    originY = desktopShareSessionController.getScrollPosY();
                }
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                if (event.getPointerCount() == 2) {
                    bZoom = true;
                    float x = event.getX(0) - event.getX(1);
                    float y = event.getY(0) - event.getY(1);
                    float value = (float) Math.sqrt(x * x + y * y);      // 计算两点的距离
                    DesktopShareSessionController desktopShareSessionController = TangSDKInstance.getInstance().getDesktopShareSession();
                    if (baseValue <=0) {
                        baseValue = value;
                        zoomCenterX =  (int)(event.getX(0) + event.getX(1))/2;
                        zoomCenterY =  (int)(event.getY(0) + event.getY(1))/2;
                    }else {
                        double scale = (value / baseValue);                // 缩放的比例=当前两点间的距离/手指落下时两点间的距离缩放的比例

                        Log.d("DesktopViewer onTouch", "Touch zoom scale value:" + scale);

                        if(desktopShareSessionController !=null){
                            desktopShareSessionController.zoomView( originScale * scale, zoomCenterX,zoomCenterY);
                        }
                    }
                }
                else if(!bZoom && event.getPointerCount() == 1){
                    int deltaX= (int)(event.getX()-startX);         //计算平移量
                    int deltaY= (int)(event.getY()-startY);
                    Log.d("DesktopViewer onTouch", "Move startX: " + startX + " startY: " + startY + "deltaX: " + deltaX + " deltaY: " + deltaY);

                    DesktopShareSessionController desktopShareSessionController = TangSDKInstance.getInstance().getDesktopShareSession();
                    if(desktopShareSessionController !=null){
                        desktopShareSessionController.scroll((int)originX+ deltaX, (int)originY + deltaY);
                    }
                }
                return true;
            } else if(event.getAction() == MotionEvent.ACTION_UP) {
                if(bZoom){
                    if(isZoomOutView()){
                        doDockInEdge();
                    } else{
                        doFitInCenter();
                    }
                }else {
                    doDockInEdge();
                }
                return true;
            }
            return false;
        }
    };




}
