package com.foreveross.atwork.component;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by lingen on 15/3/26.
 * Description:
 */
public class NotScrollGridView extends WhiteClickGridView {


    public NotScrollGridView(Context context) {
        super(context);
    }

    public NotScrollGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotScrollGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置不滚动
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
