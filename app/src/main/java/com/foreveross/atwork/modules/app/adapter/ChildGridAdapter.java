package com.foreveross.atwork.modules.app.adapter;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.modules.app.component.AppItemView;
import com.foreveross.atwork.modules.app.inter.AppRemoveListener;
import com.foreveross.atwork.modules.app.model.GroupAppItem;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.theme.manager.SkinMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Reyzhang on 2015/3/24.
 */
public class ChildGridAdapter extends BaseAdapter {
    private Activity mContext;
    private List<AppBundles> appItems = new ArrayList<>();

    private AppRemoveListener appRemoveListener;

    private GroupAppItem groupAppItem;

    private BackHandledFragment.OnK9MailClickListener mOnK9MailClickListener;

    private Boolean hasRefresh = false;

    public ChildGridAdapter(Activity context) {
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

    private boolean removeAble;

    private boolean canClickable = true;

    public void refreshAppItems(GroupAppItem groupAppItem, boolean removeAble, boolean canClickable) {
        this.groupAppItem = groupAppItem;
        this.appItems = groupAppItem.mAppBundleList;
        this.removeAble = removeAble;
        this.canClickable = canClickable;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return appItems.size();
    }

    @Override
    public AppBundles getItem(int position) {
        return appItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new AppItemView(mContext);
        }

        ((AppItemView) convertView).refreshView(groupAppItem, getItem(position), removeAble, canClickable);
        ((AppItemView) convertView).setAppRemoveListener(appRemoveListener);
        ((AppItemView) convertView).setOnK9MailClickListener(mOnK9MailClickListener);
        SkinMaster.getInstance().changeTheme((ViewGroup) convertView);
        return convertView;
    }

    public void setAppRemoveListener(AppRemoveListener appRemoveListener) {
        this.appRemoveListener = appRemoveListener;
    }

    public void setOnK9MailClickListener(BackHandledFragment.OnK9MailClickListener listener) {
        mOnK9MailClickListener = listener;
    }
}
