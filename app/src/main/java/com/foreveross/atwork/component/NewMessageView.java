package com.foreveross.atwork.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;

/**
 * 超过一定的数值时，右上角显示+号的TextView
 */
public class NewMessageView extends FrameLayout {
    private TextView mTvNum;
    private TextView mTvPlus;
    private LinearLayout item_message_item_background;
    private static final int mMaxNum = 99;

    public NewMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NewMessageView(Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_with_plus_text_view, this);
        mTvNum = findViewById(R.id.item_message_item_unread_num);
        mTvPlus = findViewById(R.id.item_message_item_plus);
        item_message_item_background = findViewById(R.id.item_message_item_background);
    }

    public void setNum(int num) {
        if (num <= 0) {
            setVisibility(View.GONE);
        } else if (num > mMaxNum) {
            mTvNum.setText(mMaxNum + "");
            mTvPlus.setVisibility(View.VISIBLE);
            setVisibility(View.VISIBLE);
        } else {
            mTvNum.setText(num + "");
            mTvPlus.setVisibility(View.GONE);
            setVisibility(View.VISIBLE);
        }
        if (num < 10 && num > 0) {
            item_message_item_background.setBackgroundResource(R.drawable.shape_message_num_bg_oval);
        } else if (num >= 10) {
            item_message_item_background.setBackgroundResource(R.drawable.shape_message_num_bg);
        }
    }

}
