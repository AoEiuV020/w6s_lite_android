package com.foreveross.atwork.modules.chat.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * Created by reyzhang22 on 15/7/5.
 */
public class ListViewInPopUp extends ListView {
    public ListViewInPopUp(Context context) {
        super(context);
    }

    public ListViewInPopUp(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewInPopUp(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = measureWithByChilds() + getPaddingLeft() + getPaddingRight();
        super.onMeasure(MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
    }

    public int measureWithByChilds() {
        int maxWidth = 0;
        View view = null;
        for (int i = 0; i < getAdapter().getCount(); i++) {
            view = getAdapter().getView(i, view, this);
            view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            if (view.getMeasuredWidth() > maxWidth){
                maxWidth = view.getMeasuredWidth();
            }
        }
        return maxWidth;
    }

}
