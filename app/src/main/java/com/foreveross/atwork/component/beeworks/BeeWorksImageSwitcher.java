package com.foreveross.atwork.component.beeworks;/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.viewPager.WrapContentHeightViewPager;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksImages;
import com.foreveross.atwork.infrastructure.beeworks.NativeContent;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by reyzhang22 on 16/3/14.
 */
public class BeeWorksImageSwitcher extends FrameLayout {

    private WrapContentHeightViewPager mViewPager;

    private CircleIndicator mIndicator;

    private BeeWorksImagePagerAdapater mAdapter;

    private List<BeeWorksImageView> mImages;

    private TextView mCategoryTitle;

    private Context mContext;

    private AutoScrollHandler mAutoScrollHandler = new AutoScrollHandler();
    private int mCurrIndex = 0;

    private static ScheduledExecutorService sScheduledThreadPool = Executors.newScheduledThreadPool(1);
    private ScheduledFuture mScheduledFuture;

    private Runnable mTimerRunnable = new Runnable() {
        @Override
        public void run() {
            Message message = new Message();
            if (mCurrIndex == mAdapter.getCount() - 1) {
                mCurrIndex = -1;
            }
            message.arg1 = mCurrIndex + 1;
            mAutoScrollHandler.startLoop(message);
        }
    };


    public BeeWorksImageSwitcher(Context context) {
        super(context);
        mContext = context;
        initViews(context);
    }

    public BeeWorksImageSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews(context);
    }

    public BeeWorksImageSwitcher(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_beeworks_iamge_switch, this);
        mViewPager = (WrapContentHeightViewPager)view.findViewById(R.id.beeworks_image_switcher);
        mIndicator = (CircleIndicator)view.findViewById(R.id.beeworks_image_switcher_indicator);
        mCategoryTitle = (TextView)view.findViewById(R.id.app_category_name);
    }

    public void setImages(List<BeeWorksImages> images, NativeContent nativeContent) {
        mImages = null;
        mImages = new ArrayList<>();
        boolean validArgs = false;
        int dpiHeight = 0;
        if (nativeContent.mWidth > 0 || nativeContent.mHeight > 0) {
            WindowManager windowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            int displayWidth = display.getWidth();
            int layoutHeight = displayWidth * nativeContent.mHeight / nativeContent.mWidth;
            dpiHeight = DensityUtil.px2dip(layoutHeight);
            validArgs = true;
        }

        for (BeeWorksImages item : images) {
            BeeWorksImageView imageView = new BeeWorksImageView(mContext);
            if (validArgs) {
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpiHeight);
                imageView.setLayoutParams(layoutParams);
            }

            imageView.setImage(item);
            mImages.add(imageView);
        }
        mAdapter  = new BeeWorksImagePagerAdapater(mImages);
        mViewPager.setAdapter(mAdapter);

        if (mImages.size() > 1 ) {
            if ( nativeContent.mInterval > 0) {
                mScheduledFuture = sScheduledThreadPool.scheduleAtFixedRate(mTimerRunnable, 400, nativeContent.mInterval * 1000, TimeUnit.MILLISECONDS);
            }

            mIndicator.setViewPager(mViewPager);
        }

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
//                if (position <= 2 || position >= mAdapter.getCount() - 3) {
//                    // 重置页面
//                    int page = mAdapter.getItemIndexForPosition(position);
//                    int newPosition = mAdapter.getStartPageIndex() + page;
//                    mViewPager.setCurrentItem(newPosition);
//                }
                mCurrIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        mAutoScrollHandler.pause = true;
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        mAutoScrollHandler.pause = false;
                        break;
                }
            }
        });
    }

    class AutoScrollHandler extends Handler {
        boolean pause = false;

        @Override
        public void handleMessage(Message msg) {
            if (!pause) {
                if (msg.arg1 != 0) {
                    mViewPager.setCurrentItem(msg.arg1);
                } else {
                    //false 当从末页调到首页是，不显示翻页动画效果，
                    mViewPager.setCurrentItem(msg.arg1, false);
                }
            }
//            sendEmptyMessageDelayed(msg.what, 5000);
        }

        void startLoop(Message message) {
            pause = false;
            removeCallbacksAndMessages(null);
            sendMessage(message);
        }

        void stopLoop() {
            removeCallbacksAndMessages(null);
        }
    }



}
