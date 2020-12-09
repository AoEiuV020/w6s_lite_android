package com.foreveross.atwork.modules.gesturecode.adapter;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.core.content.ContextCompat;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.modules.common.component.CommonItemView;
import com.foreveross.atwork.modules.gesturecode.activity.GestureCodeInputActivity;
import com.foreveross.atwork.modules.gesturecode.activity.GestureCodeLockActivity;
import com.foreveross.atwork.modules.gesturecode.fragment.GestureCodeInputFragment;
import com.foreveross.atwork.modules.gesturecode.fragment.GestureCodeLockFragment;
import com.foreveross.atwork.modules.gesturecode.fragment.GestureCodeManagerFragment;
import com.foreveross.theme.manager.SkinMaster;

/**
 * Created by dasunsy on 16/1/13.
 */
public class GestureCodeManagerAdapter extends BaseAdapter {
    private GestureCodeManagerFragment mFragment;
    private String[] mNames;
    private boolean mCanClickSwitchBtn = true;


    public GestureCodeManagerAdapter(GestureCodeManagerFragment fragment, String[] names) {
        mFragment = fragment;
        mNames = names;
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
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = new CommonItemView(mFragment.mActivity);
        }
        SkinMaster.getInstance().changeTheme((ViewGroup) convertView);

        final CommonItemView commonItem = (CommonItemView)convertView;
        String itemName = (String) getItem(position);

        commonItem.setCommonName(itemName);
        if(mFragment.mActivity.getString(R.string.open_gesture_code).equals(itemName)) {
            commonItem.getCommonNameTv().setTextColor(ContextCompat.getColor(mFragment.mActivity, R.color.common_item_black));

            commonItem.showSwitchButton(true);
            commonItem.getSwitchBtn().setChecked(PersonalShareInfo.getInstance().getLockCodeSetting(mFragment.mActivity));

            setSwitchListener(commonItem);
            commonItem.setClickable(false);

        } else if(mFragment.mActivity.getString(R.string.edit_gesture_code).equals(itemName)) {
            commonItem.showSwitchButton(false);

            if(PersonalShareInfo.getInstance().getLockCodeSetting(mFragment.mActivity)) {
                commonItem.getCommonNameTv().setTextColor(ContextCompat.getColor(mFragment.mActivity, R.color.common_item_black));
                setClickListener(commonItem);

            } else {

                commonItem.getCommonNameTv().setTextColor(ContextCompat.getColor(mFragment.mActivity, R.color.common_text_gray_color_aaa));
                commonItem.setClickable(false);
            }
        }

        commonItem.setLineVisible((getCount() - 1) > position);

        return convertView;
    }

    private void setSwitchListener(final CommonItemView commonItem) {
        commonItem.getSwitchBtn().setOnClickNotPerformToggle(() -> {

            if (!mCanClickSwitchBtn) {
                return;
            }
            mCanClickSwitchBtn = false;


            if (commonItem.getSwitchBtn().isChecked()) {
                commonItem.postDelayed(() -> {
                    Intent intent = GestureCodeLockActivity.getIntent(mFragment.mActivity, GestureCodeLockFragment.ActionFromSwitch.CLOSE);

                    mFragment.startActivityForResult(intent, GestureCodeLockFragment.ActionFromSwitch.CLOSE);
                    //界面切换效果
                    mFragment.mActivity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

                    mCanClickSwitchBtn = true;
                }, 500);


            } else {

                commonItem.postDelayed(() -> {
                    Intent intent = GestureCodeInputActivity.getIntent(mFragment.mActivity, GestureCodeInputFragment.Mode.ADD);
                    mFragment.startActivityForResult(intent, GestureCodeLockFragment.ActionFromSwitch.OPEN);

                    //界面切换效果
                    mFragment.mActivity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

                    mCanClickSwitchBtn = true;
                }, 500);

            }

            commonItem.getSwitchBtn().toggle();
        });
    }

    private void setClickListener(final CommonItemView commonItem) {
        commonItem.setOnClickListener(v -> {
            Intent intent = GestureCodeLockActivity.getIntent(mFragment.mActivity, GestureCodeInputActivity.class);

            mFragment.startActivity(intent);
            //界面切换效果
            mFragment.mActivity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        });
    }
}
