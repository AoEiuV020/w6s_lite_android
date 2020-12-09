package com.foreveross.atwork.modules.chat.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.foreveross.atwork.R;

/**
 * Created by lingen on 15/5/11.
 * Description:
 */
public class SearchResultDateItemView extends LinearLayout {

    private TextView mTvTitle;
    private View mLine;

    public SearchResultDateItemView(Context context) {
        super(context);
        initView();
    }

    public SearchResultDateItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_search_result_date, this);
        mTvTitle = view.findViewById(R.id.tv_date);
        mLine = view.findViewById(R.id.view_line);
    }

    public void setTitle(String title) {
        this.mTvTitle.setText(title);
    }

    public void setTextColor(int resId) {
        this.mTvTitle.setTextColor(ContextCompat.getColor(getContext(), resId));
    }

    public void hideDiverLine() {
        mLine.setVisibility(View.GONE);
    }

    public void setTvBackground(int resId) {
        mTvTitle.setBackgroundResource(resId);
    }
}
