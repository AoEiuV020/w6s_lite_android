package com.foreveross.atwork.modules.app.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.modules.app.activity.AppListCustomSortActivity;
import com.foreveross.atwork.modules.app.component.AppGroupItemView;
import com.foreveross.atwork.modules.app.component.AppGroupTitleView;
import com.foreveross.atwork.modules.app.inter.AppRemoveListener;
import com.foreveross.atwork.modules.app.model.GroupAppItem;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.theme.manager.SkinMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Reyzhang on 2015/3/24.
 */
public class AppsAdapter extends BaseAdapter {

    private Activity mContext;

    private List<GroupAppItem> mGroupAppItems;

    private AppRemoveListener appRemoveListener;

    private BackHandledFragment.OnK9MailClickListener mailClickListener;

    private boolean mRemoveAble;

    private int mPaddingLength;

    public AppsAdapter(Activity context, List<GroupAppItem> groupAppItems) {
        assert context != null;
        assert groupAppItems != null : context.getResources().getString(R.string.error_app_list_not_null);
        this.mGroupAppItems = groupAppItems;
        mContext = context;
    }

    public AppsAdapter(Activity context) {
        assert context != null;
        mContext = context;
        this.mGroupAppItems = new ArrayList<>();
        mPaddingLength = getPaddingLength();
    }

    public void refreshGroupAppList(List<GroupAppItem> groupAppItems) {
        this.mGroupAppItems = groupAppItems;
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        GroupAppItem item = getItem(position);
        if(null != item) {
            return item.type;
        }

        return GroupAppItem.TYPE_APP;
    }

    @Override
    public int getCount() {
        return mGroupAppItems.size();
    }

    @Nullable
    @Override
    public GroupAppItem getItem(int position) {
        if(position >= 0 && position < getCount()) {
            return mGroupAppItems.get(position);

        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GroupAppItem groupAppItem = getItem(position);

        if (convertView == null) {
            int itemViewType = getItemViewType(position);
            if (GroupAppItem.TYPE_APP == itemViewType) {
                convertView = new AppGroupItemView(mContext);

            } else if (GroupAppItem.TYPE_TITLE == itemViewType) {
                convertView = new AppGroupTitleView(mContext);


                convertView.setOnClickListener(v -> {
                    if (null != appRemoveListener) {
                        appRemoveListener.removeMode(false);
                    }
                });

                TextView tvEdit = ((AppGroupTitleView) convertView).getTvEdit();

                tvEdit.setOnClickListener(v -> {
                    Intent intent = AppListCustomSortActivity.Companion.getIntent(mContext);
                    mContext.startActivity(intent);

                });

            }
        }

        if (null != groupAppItem) {
            if (convertView instanceof AppGroupItemView) {
                AppGroupItemView appGroupItemView = (AppGroupItemView) convertView;
                appGroupItemView.setAppRemoveListener(appRemoveListener);
                appGroupItemView.setMailClickListener(mailClickListener);
                appGroupItemView.refreshView(groupAppItem, mRemoveAble, true, mPaddingLength);

            } else if (convertView instanceof AppGroupTitleView) {
                AppGroupTitleView appGroupTitleView = (AppGroupTitleView) convertView;
                appGroupTitleView.refreshTitle(groupAppItem.title);

                if(groupAppItem.custom) {
                    appGroupTitleView.handleTvEditVisible(View.VISIBLE);


                } else {
                    appGroupTitleView.handleTvEditVisible(View.GONE);

                }


            }
        }
        SkinMaster.getInstance().changeTheme((ViewGroup) convertView);
        return convertView;
    }

    public void setAppRemoveListener(AppRemoveListener appRemoveListener) {
        this.appRemoveListener = appRemoveListener;
    }

    public void setMailClickListener(BackHandledFragment.OnK9MailClickListener listener) {
        this.mailClickListener = listener;
    }

    public void setRemoveAble(boolean removeAble) {
        this.mRemoveAble = removeAble;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

    }

    /**
     * 返回应用中心列表 paddingLeft 的距离(达到左右对称)
     */
    public static int getPaddingLength() {
        int paddingLength = (ScreenUtils.getScreenWidth(AtworkApplicationLike.baseContext) - 4 * DensityUtil.dip2px( 82)) / 5;
        return paddingLength;
    }
}
