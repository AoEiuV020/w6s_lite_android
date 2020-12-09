package com.foreveross.atwork.modules.app.component;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.NotScrollGridView;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.modules.app.adapter.ChildGridCommonAdapter;
import com.foreveross.atwork.modules.app.inter.OnAppItemClickEventListener;
import com.foreveross.atwork.modules.app.model.GroupAppItem;


public class AppGroupItemCommonView extends LinearLayout {

    private GroupAppItem groupAppItem;

    private NotScrollGridView scrollGridView;

    private ChildGridCommonAdapter childGridAdapter;

    private OnAppItemClickEventListener onAppItemClickEventListener;


    private boolean mCustomMode;
    private int mCustomModeIcon;
    private Activity mActivity;

    public AppGroupItemCommonView(Activity context) {
        super(context);
        mActivity = context;
        initView(context);
        initData();
    }

    private void initData() {
        childGridAdapter = new ChildGridCommonAdapter(mActivity);
        scrollGridView.setAdapter(childGridAdapter);
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_app_expandlist_child, this);
        scrollGridView = view.findViewById(R.id.appGridView);
    }

    public void refreshView(GroupAppItem groupAppItem, boolean customMode, int paddingLength) {
        this.mCustomMode = customMode;
        this.groupAppItem = groupAppItem;
        childGridAdapter.refreshAppItems(groupAppItem, customMode);
        childGridAdapter.setCustomModeIcon(mCustomModeIcon);
        childGridAdapter.setOnAppItemClickEventListener(onAppItemClickEventListener);
        scrollGridView.setOnTouchInvalidPositionListener(motionEvent -> {

            return false;
        });

        //12dp 转换成 px
        int vLength = (int) (DensityUtil.DP_8_TO_PX * 1.5);
        if (0 <= paddingLength) {
            scrollGridView.setPadding(paddingLength, vLength, 0, vLength);
        } else {
            scrollGridView.setPadding(vLength, vLength, vLength, vLength);
        }

    }

    public void setOnAppItemClickEventListener(OnAppItemClickEventListener onAppItemClickEventListener) {
        this.onAppItemClickEventListener = onAppItemClickEventListener;
    }

    public void setCustomModeIcon(int customModeIcon) {
        mCustomModeIcon = customModeIcon;
    }
}
