package com.foreveross.atwork.modules.app.component;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

/**
 * Auto Scroll View Pager
 * <ul>
 * <strong>Basic Setting and Usage</strong>
 * <li>{@link #startAutoScroll()} start auto scroll, or {@link #startAutoScroll(int)} start auto scroll delayed</li>
 * <li>{@link #stopAutoScroll()} stop auto scroll</li>
 * <li>{@link #setInterval(long)} set auto scroll time in milliseconds, default is {@link #DEFAULT_INTERVAL}</li>
 * </ul>
 * <ul>
 * <strong>Advanced Settings and Usage</strong>
 * <li>{@link #setDirection(int)} set auto scroll direction</li>
 * <li>{@link #setCycle(boolean)} set whether automatic cycle when auto scroll reaching the last or first item, default
 * is true</li>
 * <li>{@link #setSlideBorderMode(int)} set how to process when sliding at the last or first item</li>
 * <li>{@link #setStopScrollWhenTouch(boolean)} set whether stop auto scroll when touching, default is true</li>
 * </ul>
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-12-30
 */
public class AutoScrollViewPager extends ViewPager {

    public static final int        DEFAULT_INTERVAL            = 1500;

    public static final int        LEFT                        = 0;
    public static final int        RIGHT                       = 1;

    public static final int        SLIDE_BORDER_MODE_NONE      = 0;
    public static final int        SLIDE_BORDER_MODE_CYCLE     = 1;
    public static final int        SLIDE_BORDER_MODE_TO_PARENT = 2;

    private long                   interval                    = DEFAULT_INTERVAL;
    private int                    direction                   = RIGHT;
    private boolean                isCycle                     = true;
    private boolean                stopScrollWhenTouch         = true;
    private int                    slideBorderMode             = SLIDE_BORDER_MODE_NONE;
    private boolean                isBorderAnimation           = true;

    private Handler                handler;
    private boolean                isAutoScroll                = false;
    private boolean                isStopByTouch               = false;
    private float                  touchX                      = 0f, downX = 0f;
    private CustomDurationScroller scroller                    = null;

    public static final int        SCROLL_WHAT                 = 0;

    public AutoScrollViewPager(Context paramContext){
        super(paramContext);
        init();
    }

    public AutoScrollViewPager(Context paramContext, AttributeSet paramAttributeSet){
        super(paramContext, paramAttributeSet);
        init();
    }

    private void init() {
        handler = new MyHandler();
        setViewPagerScroller();
    }


    public void startAutoScroll() {
        isAutoScroll = true;
        sendScrollMessage(interval);
    }


    public void startAutoScroll(int delayTimeInMills) {
        isAutoScroll = true;
        sendScrollMessage(delayTimeInMills);
    }


    public void stopAutoScroll() {
        isAutoScroll = false;
        handler.removeMessages(SCROLL_WHAT);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
//        setCurrentItem(0);
    }

//    @Override
//    public void setCurrentItem(int item) {
//        item = getOffsetAmount() + (item % getAdapter().getCount());
//        super.setCurrentItem(item);
//
//    }

    /**
     * 得到viewpaper的偏移量
     * */
//    private int getOffsetAmount() {
//        if (getAdapter() instanceof AdvertsPagerAdapter) {
//            AdvertsPagerAdapter loopAdapter = (AdvertsPagerAdapter) getAdapter();
//            return loopAdapter.getRealCount() * 100;
//        } else {
//            return 0;
//        }
//    }


    public void setScrollDurationFactor(double scrollFactor) {
        scroller.setScrollDurationFactor(scrollFactor);
    }

    private void sendScrollMessage(long delayTimeInMills) {
        handler.removeMessages(SCROLL_WHAT);
        handler.sendEmptyMessageDelayed(SCROLL_WHAT, delayTimeInMills);
    }


    private void setViewPagerScroller() {
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            Field interpolatorField = ViewPager.class.getDeclaredField("sInterpolator");
            interpolatorField.setAccessible(true);

            scroller = new CustomDurationScroller(getContext(), (Interpolator)interpolatorField.get(null));
            scrollerField.set(this, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scrollOnce() {
        PagerAdapter adapter = getAdapter();
        int currentItem = getCurrentItem();
        int totalCount;
        if (adapter == null || (totalCount = adapter.getCount()) <= 1) {
            return;
        }

        int nextItem = (direction == LEFT) ? --currentItem : ++currentItem;
        if (nextItem < 0) {
            if (isCycle) {
                setCurrentItem(totalCount - 1, isBorderAnimation);
            }
        } else if (nextItem == totalCount) {
            if (isCycle) {
                setCurrentItem(0, isBorderAnimation);
            }
        } else {
            setCurrentItem(nextItem, true);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (stopScrollWhenTouch) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN && isAutoScroll) {
                isStopByTouch = true;
                stopAutoScroll();
            } else if (ev.getAction() == MotionEvent.ACTION_UP && isStopByTouch) {
                startAutoScroll();
            }
        }

        if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT || slideBorderMode == SLIDE_BORDER_MODE_CYCLE) {
            touchX = ev.getX();
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                downX = touchX;
            }
            int currentItem = getCurrentItem();
            PagerAdapter adapter = getAdapter();
            int pageCount = adapter == null ? 0 : adapter.getCount();

            if ((currentItem == 0 && downX <= touchX) || (currentItem == pageCount - 1 && downX >= touchX)) {
                if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    if (pageCount > 1) {
                        setCurrentItem(pageCount - currentItem - 1, isBorderAnimation);
                    }
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return super.onTouchEvent(ev);
            }
        }
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(ev);
    }

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SCROLL_WHAT:
                    scrollOnce();
                    sendScrollMessage(interval);
                default:
                    break;
            }
        }
    }


    public long getInterval() {
        return interval;
    }


    public void setInterval(long interval) {
        this.interval = interval;
    }


    public int getDirection() {
        return (direction == LEFT) ? LEFT : RIGHT;
    }


    public void setDirection(int direction) {
        this.direction = direction;
    }


    public boolean isCycle() {
        return isCycle;
    }


    public void setCycle(boolean isCycle) {
        this.isCycle = isCycle;
    }


    public boolean isStopScrollWhenTouch() {
        return stopScrollWhenTouch;
    }


    public void setStopScrollWhenTouch(boolean stopScrollWhenTouch) {
        this.stopScrollWhenTouch = stopScrollWhenTouch;
    }


    public int getSlideBorderMode() {
        return slideBorderMode;
    }


    public void setSlideBorderMode(int slideBorderMode) {
        this.slideBorderMode = slideBorderMode;
    }


    public boolean isBorderAnimation() {
        return isBorderAnimation;
    }


    public void setBorderAnimation(boolean isBorderAnimation) {
        this.isBorderAnimation = isBorderAnimation;
    }
}
