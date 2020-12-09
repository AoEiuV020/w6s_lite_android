package com.foreverht.workplus.ui.component.textview;

/**
 * Created by dasunsy on 2018/1/13.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatTextView;

import com.foreverht.workplus.ui.component.R;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;

/**
 * 自定义TextView,根据宽度改变大小
 */
public class AutoAlignTextView extends AppCompatTextView {

    private float maxTextSize, minTextSize;   //最大字体和最小字体

    public AutoAlignTextView(Context context) {
        this(context, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    }

    public AutoAlignTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoAlignTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        setMaxLines(1);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.AutoAlignTextView);
        maxTextSize = mTypedArray.getDimension(R.styleable.AutoAlignTextView_maxTextSize, 48);
        minTextSize = mTypedArray.getDimension(R.styleable.AutoAlignTextView_minTextSize, 24);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        adjustTvTextSize();
    }





    //动态修改字体大小
    private void adjustTvTextSize() {
        int maxWidth = getMaxWidth();
        if(maxWidth <= 0) {
            return;
        }

        String text = getText().toString();
        int avaiWidth = (int) (maxWidth - getPaddingLeft() - getPaddingRight() - DensityUtil.dip2px(2));
        if (avaiWidth <= 0) {
            return;
        }
        TextPaint textPaintClone = new TextPaint(getPaint());
        float trySize = maxTextSize;
        textPaintClone.setTextSize(trySize);
        while (textPaintClone.measureText(text) > avaiWidth && trySize > minTextSize) {
            trySize--;
            textPaintClone.setTextSize(trySize);
        }
        setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize);
    }

    public AutoAlignTextView setMaxTextSize(float maxTextSize) {
        this.maxTextSize = maxTextSize;
        return this;
    }

    public AutoAlignTextView setMinTextSize(float minTextSize) {
        this.minTextSize = minTextSize;
        return this;
    }

    public void refresh() {
        adjustTvTextSize();
    }
}
