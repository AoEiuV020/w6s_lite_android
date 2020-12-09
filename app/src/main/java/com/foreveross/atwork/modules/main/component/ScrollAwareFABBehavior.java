package com.foreveross.atwork.modules.main.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Created by dasunsy on 2017/12/11.
 */

public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private boolean mIsAnimatingOut = false;

    public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                                       final View directTargetChild, final View target, final int nestedScrollAxes) {
        // 确定是在垂直方向上滑动
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                               final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        LogUtil.e("onNestedScroll ~~~~");

        if (dyConsumed > 0 && !this.mIsAnimatingOut && child.getVisibility() == View.VISIBLE) {
            // 不显示FAB
            animateOut(child);

        }
        else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            // 显示FAB
            animateIn(child);

        }
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);

        LogUtil.e("onNestedScroll 2 ~~~~");

    }

    // 定义滑动时的属性动画效果
    private void animateOut(final FloatingActionButton button) {
        ViewCompat.animate(button).scaleX(0.0F).scaleY(0.0F).alpha(0.0F).setInterpolator(INTERPOLATOR).withLayer()
                .setListener(new ViewPropertyAnimatorListener() {
                    public void onAnimationStart(View view) {
                        ScrollAwareFABBehavior.this.mIsAnimatingOut = true;
                    }

                    public void onAnimationCancel(View view) {
                        ScrollAwareFABBehavior.this.mIsAnimatingOut = false;
                    }

                    public void onAnimationEnd(View view) {
                        ScrollAwareFABBehavior.this.mIsAnimatingOut = false;
                        view.setVisibility(View.INVISIBLE);
                    }
                }).start();

        //        else {
//            Animation anim = AnimationUtils.loadAnimation(button.getContext(), R.anim.fade_out_end);
//            anim.setInterpolator(INTERPOLATOR);
//            anim.setDuration(200L);
//            anim.setAnimationListener(new Animation.AnimationListener() {
//                public void onAnimationStart(Animation animation) {
//                    ScrollAwareFABBehavior.this.mIsAnimatingOut = true;
//                }
//
//                public void onAnimationEnd(Animation animation) {
//                    ScrollAwareFABBehavior.this.mIsAnimatingOut = false;
//                    button.setVisibility(View.INVISIBLE);
//                }
//
//                @Override
//                public void onAnimationRepeat(final Animation animation) {
//                }
//            });
//            button.startAnimation(anim);
//
//        }

    }

    private void animateIn(FloatingActionButton button) {
        button.setVisibility(View.VISIBLE);

        ViewCompat.animate(button).scaleX(1.0F).scaleY(1.0F).alpha(1.0F)
                .setInterpolator(INTERPOLATOR).withLayer().setListener(null)
                .start();

    }
}
