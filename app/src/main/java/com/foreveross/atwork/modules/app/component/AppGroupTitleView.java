package com.foreveross.atwork.modules.app.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;

/**
 * Created by dasunsy on 15/11/15.
 */
public class AppGroupTitleView extends LinearLayout{
    private TextView mTvTitle;
    private TextView mTvEdit;

    public AppGroupTitleView(Context context) {
        super(context);
        initView();
    }

    private void initView(){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_apps_group_title, this);
        mTvTitle = view.findViewById(R.id.app_category_name);
        mTvEdit = view.findViewById(R.id.tv_edit);
    }

    public void refreshTitle(String title){
        mTvTitle.setText(title);
    }

    public void handleTvEditVisible(int visibility) {
        mTvEdit.setVisibility(visibility);
    }

    public TextView getTvEdit() {
        return mTvEdit;
    }
}
