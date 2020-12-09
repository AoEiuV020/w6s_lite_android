package com.foreveross.atwork.modules.gesturecode.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.modules.gesturecode.adapter.GestureCodeManagerAdapter;
import com.foreveross.atwork.support.BackHandledFragment;

/**
 * Created by dasunsy on 16/1/13.
 */
public class GestureCodeManagerFragment extends BackHandledFragment{
    private static final String TAG = GestureCodeManagerFragment.class.getSimpleName();

    private TextView mTvTitle;
    private ImageView mIvBack;
    private ListView mLvItems;

    private GestureCodeManagerAdapter mGestureCodeManagerAdapter;
    private String[] mItemNames;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gesture_code_manager, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerListener();

        mTvTitle.setText(R.string.gesture_password_manager);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mItemNames = getResources().getStringArray(R.array.gesture_code_manager_array);
        mGestureCodeManagerAdapter = new GestureCodeManagerAdapter(this, mItemNames);
        mLvItems.setAdapter(mGestureCodeManagerAdapter);

    }

    @Override
    protected void findViews(View view) {
        mTvTitle = view.findViewById(R.id.title_bar_common_title);
        mIvBack = view.findViewById(R.id.title_bar_common_back);
        mLvItems = view.findViewById(R.id.list);
        mLvItems.setDivider(null);

    }

    private void registerListener() {
        mIvBack.setOnClickListener(v -> onBackPressed());

    }

    @Override
    protected boolean onBackPressed() {
        mActivity.finish();
        //界面回退动画
        mActivity.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        return false;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(Activity.RESULT_OK == resultCode) {
            if(GestureCodeLockFragment.ActionFromSwitch.CLOSE == requestCode) {
                PersonalShareInfo.getInstance().setLockCodeSetting(mActivity, false);
                LoginUserInfo.getInstance().mLastCodeLockTime = -1;

            } else if(GestureCodeLockFragment.ActionFromSwitch.OPEN == requestCode) {
                PersonalShareInfo.getInstance().setLockCodeSetting(mActivity, true);

                LoginUserInfo.getInstance().mIsInitOpenCodeLock = true;
            }
        }

        mGestureCodeManagerAdapter.notifyDataSetChanged();


    }
}
