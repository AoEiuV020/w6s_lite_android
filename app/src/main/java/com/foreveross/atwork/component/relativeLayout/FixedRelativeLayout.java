package com.foreveross.atwork.component.relativeLayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.WindowInsets;
import android.widget.RelativeLayout;

/**
 * Created by dasunsy on 2018/1/22.
 */

public class FixedRelativeLayout extends RelativeLayout {

    private int[] mInsets = new int[4];

    public FixedRelativeLayout(Context context) {
        super(context);
    }

    public FixedRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FixedRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected final boolean fitSystemWindows(Rect insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Intentionally do not modify the bottom inset. For some reason,
            // if the bottom inset is modified, window resizing stops working.
            // TODO: Figure out why.

            mInsets[0] = insets.left;
            mInsets[1] = insets.top;
            mInsets[2] = insets.right;

            insets.left = 0;
            insets.top = 0;
            insets.right = 0;
        }

        return super.fitSystemWindows(insets);
    }

    @Override
    public final WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            mInsets[0] = insets.getSystemWindowInsetLeft();
            Log.e("mInsets[0]",""+mInsets[0]);
            mInsets[1] = insets.getSystemWindowInsetTop();
            Log.e("mInsets[1]",""+mInsets[1]);
            mInsets[2] = insets.getSystemWindowInsetRight();
            Log.e("mInsets[2]",""+mInsets[2]);
            return super.onApplyWindowInsets(insets.replaceSystemWindowInsets(0, 0, 0,
                    insets.getSystemWindowInsetBottom()));
        } else {
            return insets;
        }
    }
}
