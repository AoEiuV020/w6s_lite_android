package com.foreveross.atwork.component;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;

import com.foreverht.workplus.ui.component.textview.MediumBoldTextView;

public class ChangeTextViewSpace extends MediumBoldTextView {
    private float spacing = Spacing.NORMAL;
    private CharSequence originalText = "";
    public ChangeTextViewSpace(Context context) {
        super(context);
    }
    
    public ChangeTextViewSpace(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    

    public float getSpacing() {
        return this.spacing;
    }
 
    public void setSpacing(float spacing) {
        this.spacing = spacing;
        applySpacing();
    }
 
    @Override
    public void setText(CharSequence text, BufferType type) {
        originalText = text;
        applySpacing();
    }
 
    @Override
    public CharSequence getText() {
        return originalText;
    }
 
    /**
     * 扩大文字空间
     */
    private void applySpacing() {
        if (this == null || this.originalText == null) return;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < originalText.length(); i++) {
            builder.append(originalText.charAt(i));
            if (i + 1 < originalText.length()) {
                builder.append("\u00A0");
            }
        }
        // 通过SpannableString类，去设置空格
        SpannableString finalText = new SpannableString(builder.toString());
        // 如果当前TextView内容长度大于1，则进行空格添加
        if (builder.toString().length() > 1) {
            for (int i = 1; i < builder.toString().length(); i += 2) {
                // ScaleXSpan 基于x轴缩放  按照x轴等比例进行缩放 通过字间距+1除以10进行等比缩放
                finalText.setSpan(new ScaleXSpan((spacing + 1) / 10), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        super.setText(finalText, BufferType.SPANNABLE);
    }
 
    public class Spacing {
        public final static float NORMAL = 0;
    }

}