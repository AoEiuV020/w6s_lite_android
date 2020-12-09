package com.foreveross.atwork.modules.friend.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;

/**
 * Created by dasunsy on 16/5/19.
 */
public class LetterTitleItemView extends LinearLayout{
    private TextView mTitleView;
    public LetterTitleItemView(Context context) {
        super(context);
        initView();
    }

    public LetterTitleItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_friend_letter, this);
        mTitleView = view.findViewById(R.id.tv_letter);
    }

    public void setText(String letter) {
        mTitleView.setText(letter);
    }
}