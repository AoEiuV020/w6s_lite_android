package com.foreverht.workplus.ui.component.textview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.foreverht.workplus.ui.component.Util.TextViewHelper;


/**
 * Created by dasunsy on 2017/12/12.
 */

public class MediumBoldTextView extends AppCompatTextView {
    public MediumBoldTextView(Context context) {
        super(context);
        setMediumBold();
    }

    public MediumBoldTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setMediumBold();
    }

    private void setMediumBold() {
        TextViewHelper.mediumBold(this);
    }
}
