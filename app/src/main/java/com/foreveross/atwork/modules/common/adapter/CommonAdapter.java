package com.foreveross.atwork.modules.common.adapter;

import android.app.Activity;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.component.WorkplusSwitchCompat;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.modules.common.component.CommonItemView;
import com.foreveross.theme.manager.SkinMaster;

/**
 * 通用列表adapter，只展现TextView的列表
 * Created by ReyZhang on 2015/5/6.
 */
public class CommonAdapter extends BaseAdapter {

    private static final String TAG = CommonAdapter.class.getSimpleName();

    private OnSwitchClickListener mSwitcherListener;

    private String[] mNames;
    private TypedArray mIconResIds;
    private Activity mActivity;

    public CommonAdapter(Activity activity, String[] names, TypedArray iconResIds) {
        if (activity == null || names == null) {
            throw new IllegalArgumentException("invalid arguments on " + TAG);
        }
        mActivity = activity;
        mNames = names;
        mIconResIds = iconResIds;
    }

    public void setSwitcherListener(OnSwitchClickListener listener) {
        mSwitcherListener = listener;
    }



    @Override
    public int getCount() {
        return mNames.length;
    }

    @Override
    public Object getItem(int position) {
        return mNames[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new CommonItemView(mActivity);
        }

        CommonItemView itemView = (CommonItemView) convertView;
        String itemName = mNames[position];

        itemView.setCommonName(itemName);

        String strDeveloperMode = mActivity.getString(R.string.developer_mode);
        String strDiscussionHelper = mActivity.getString(R.string.discussion_helper);


        if (strDeveloperMode.equals(itemName)) {
            itemView.showSwitchButton(true);
            itemView.mSwitchBtn.setChecked(true);

            itemView.mSwitchBtn.setOnClickNotPerformToggle(() -> {
                if (mSwitcherListener != null) {
                    mSwitcherListener.onSwitchClick(itemView.mSwitchBtn, position);
                }
            });

        } else if(strDiscussionHelper.equals(itemName)) {
            itemView.showSwitchButton(true);

            itemView.mSwitchBtn.setChecked(PersonalShareInfo.getInstance().getSettingDiscussionHelper(AtworkApplicationLike.baseContext));


            itemView.mSwitchBtn.setOnClickNotPerformToggle(() -> {
                if (mSwitcherListener != null) {
                    mSwitcherListener.onSwitchClick(itemView.mSwitchBtn, position);
                }
            });
        } else {

            itemView.showSwitchButton(false);

        }

        if (mIconResIds != null) {
            if (mNames.length > position) {
                itemView.setCommonImage(mIconResIds.getResourceId(position, R.mipmap.icon_set_1));
            }
        }

        itemView.setLineVisible((getCount() - 1) > position);

        SkinMaster.getInstance().changeTheme((ViewGroup) convertView);
        return convertView;
    }

    public interface OnSwitchClickListener{
        void onSwitchClick(WorkplusSwitchCompat switchCompat, int pos);
    }

}
