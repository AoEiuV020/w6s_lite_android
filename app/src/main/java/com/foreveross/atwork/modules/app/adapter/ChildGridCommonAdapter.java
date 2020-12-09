package com.foreveross.atwork.modules.app.adapter;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.modules.app.component.AppItemCommonView;
import com.foreveross.atwork.modules.app.inter.OnAppItemClickEventListener;
import com.foreveross.atwork.modules.app.model.GroupAppItem;
import com.foreveross.theme.manager.SkinMaster;

import java.util.ArrayList;
import java.util.List;


public class ChildGridCommonAdapter extends BaseAdapter {

    private Activity mContext;
    private List<AppBundles> appBundleList = new ArrayList<>();

    private OnAppItemClickEventListener onAppItemClickEventListener;

    private int mCustomModeIcon;

    private GroupAppItem groupAppItem;

    public ChildGridCommonAdapter(Activity context) {
        mContext = context;

    }

    /**
     * 7.0 开始嵌套 listview 会有丢失DataSetObserver的问题, 该方法在 listview 离开屏幕时会调用, android
     * 以此优化性能, 但以应用中心的展现模式, 可以忽略这个优化
     * */
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (24 > Build.VERSION.SDK_INT) {
            super.unregisterDataSetObserver(observer);
        }
    }

    private boolean customMode;

    public void refreshAppItems(GroupAppItem groupAppItem, boolean custom) {
        this.groupAppItem = groupAppItem;
        this.appBundleList = groupAppItem.mAppBundleList;
        this.customMode = custom;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return appBundleList.size();
    }

    @Override
    public AppBundles getItem(int position) {
        return appBundleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new AppItemCommonView(mContext);
        }

        AppItemCommonView appItemCommonView = (AppItemCommonView) convertView;

        appItemCommonView.getCustomView().setBackgroundResource(mCustomModeIcon);
        appItemCommonView.refreshView(groupAppItem, getItem(position), customMode);
        appItemCommonView.setOnAppItemClickEventListener(onAppItemClickEventListener);
        SkinMaster.getInstance().changeTheme((ViewGroup) convertView);
        return convertView;
    }

    public void setOnAppItemClickEventListener(OnAppItemClickEventListener onAppItemClickEventListener) {
        this.onAppItemClickEventListener = onAppItemClickEventListener;
    }

    public void setCustomModeIcon(int customModeIcon) {
        mCustomModeIcon = customModeIcon;
    }
}
