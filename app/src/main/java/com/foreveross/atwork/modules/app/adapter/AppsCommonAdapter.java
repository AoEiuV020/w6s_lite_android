package com.foreveross.atwork.modules.app.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.modules.app.component.AppGroupItemCommonView;
import com.foreveross.atwork.modules.app.component.AppGroupTitleView;
import com.foreveross.atwork.modules.app.inter.OnAppItemClickEventListener;
import com.foreveross.atwork.modules.app.model.GroupAppItem;
import com.foreveross.theme.manager.SkinMaster;

import java.util.ArrayList;
import java.util.List;


public class AppsCommonAdapter extends BaseAdapter {

    private Activity mContext;

    private List<GroupAppItem> mGroupAppItems;

    private OnAppItemClickEventListener mOnAppItemClickEventListener;

    private boolean mCustomMode;

    private int mCustomModeIcon;

    private int mPaddingLength;


    public AppsCommonAdapter(Activity context, List<GroupAppItem> groupAppItems) {
        assert context != null;
        assert groupAppItems != null : context.getResources().getString(R.string.error_app_list_not_null);
        this.mGroupAppItems = groupAppItems;
        mContext = context;
    }

    public AppsCommonAdapter(Activity context) {
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
                convertView = new AppGroupItemCommonView(mContext);

            } else if (GroupAppItem.TYPE_TITLE == itemViewType) {
                convertView = new AppGroupTitleView(mContext);


            }
        }

        if (null != groupAppItem) {
            if (convertView instanceof AppGroupItemCommonView) {
                AppGroupItemCommonView appGroupItemView = (AppGroupItemCommonView) convertView;
                appGroupItemView.setOnAppItemClickEventListener(mOnAppItemClickEventListener);
                appGroupItemView.setCustomModeIcon(mCustomModeIcon);

                appGroupItemView.refreshView(groupAppItem, mCustomMode, mPaddingLength);



            } else if (convertView instanceof AppGroupTitleView) {
                AppGroupTitleView appGroupTitleView = (AppGroupTitleView) convertView;
                appGroupTitleView.refreshTitle(groupAppItem.title);



            }
        }
        SkinMaster.getInstance().changeTheme((ViewGroup) convertView);
        return convertView;
    }

    public void setOnAppItemClickEventListener(OnAppItemClickEventListener onAppItemClickEventListener) {
        this.mOnAppItemClickEventListener = onAppItemClickEventListener;
    }


    public void setCustomMode(boolean customMode) {
        this.mCustomMode = customMode;
    }


    public void setCustomModeIcon(int customModeIcon) {
        mCustomModeIcon = customModeIcon;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

    }

    /**
     * 返回应用中心列表 paddingLeft 的距离(达到左右对称)
     */
    private int getPaddingLength() {
        int paddingLength = (ScreenUtils.getScreenWidth(mContext) - 4 * DensityUtil.dip2px( 82)) / 5;
        return paddingLength;
    }
}
