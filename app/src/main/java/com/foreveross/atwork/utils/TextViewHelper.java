package com.foreveross.atwork.utils;

import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.annotation.ColorRes;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

/**
 * Created by dasunsy on 2017/9/26.
 */

public class TextViewHelper {
    public static void highlightKey(TextView textView, String key, @ColorRes int colorId) {

        int color = BaseApplicationLike.baseContext.getResources().getColor(colorId);

        String text = textView.getText().toString();
        if (!StringUtils.isEmpty(text)) {
            int start = text.indexOf(key);
            int end = -1;
            if (start != -1) {
                end = start + key.length();
                SpannableString spannableString = new SpannableString(text);
                spannableString.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(spannableString);
            }
        }
    }


    public static void mediumBold(TextView textView) {
        TextPaint tp = textView.getPaint();
        tp.setFakeBoldText(true);

        if(Build.VERSION_CODES.LOLLIPOP > Build.VERSION.SDK_INT) {
            textView.setTypeface(Typeface.create(textView.getTypeface(), Typeface.BOLD));
            return;
        }
        textView.setTypeface(Typeface.create("sans-serif-thin", Typeface.BOLD));
//        setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

    }
}
