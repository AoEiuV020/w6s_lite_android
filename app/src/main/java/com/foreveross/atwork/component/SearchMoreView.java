package com.foreveross.atwork.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;

/**
 * Created by lingen on 15/5/11.
 * Description:
 */
public class SearchMoreView extends RelativeLayout {

    public TextView mTvTitle;

    public SearchMoreView(Context context) {
        super(context);
        initView();
    }

    public SearchMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_more_text, this);
        mTvTitle = view.findViewById(R.id.search_more_text);
    }

    public void setTitle(String title) {
        this.mTvTitle.setText(title);
    }
}
