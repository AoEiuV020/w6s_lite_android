package com.foreveross.atwork.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.foreveross.atwork.utils.EditTextUtil;

/**
 * Created by dasunsy on 2017/4/25.
 */

public class MaxInputEditText extends EditText {
    public MaxInputEditText(Context context) {
        super(context);
    }

    public MaxInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMaxInput(int size, boolean needToastTip) {
        EditTextUtil.setEditTextMaxStringLengthInput(this, size, needToastTip);
    }

    /**
     * 还原"最大输入"的设定
     * */
    public void clearMaxInput() {
        EditTextUtil.clearMaxInput(this);
    }

}
