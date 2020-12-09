package com.foreveross.atwork.modules.contact.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.utils.StringUtils;


public class ContactTitleView extends LinearLayout {


    private LinearLayout mLlRoot;
    private TextView mTitleView;

    public ContactTitleView(Context context, int resId) {
        super(context);
        initView();
        setTitle(resId);
    }


    public ContactTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_contact_title, this);
        mLlRoot = view.findViewById(R.id.ll_root);
        mTitleView = view.findViewById(R.id.contact_title);
    }

    public LinearLayout getLlRoot() {
        return mLlRoot;
    }

    private void setTitle(int resId) {
        if (-1 == resId) {
            mTitleView.setText(StringUtils.EMPTY);
            mTitleView.setVisibility(GONE);
        } else {
            mTitleView.setText(getResources().getString(resId));
            mTitleView.setVisibility(VISIBLE);

        }
    }

}
