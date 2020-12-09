package com.foreveross.atwork.support;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksTab;
import com.foreveross.atwork.inter.BeeWorksInfo;
import com.foreveross.atwork.modules.main.activity.MainActivity;
import com.foreveross.theme.manager.SkinMaster;
import com.foreveross.theme.model.Theme;

/**
 * Created by lingen on 15/11/18.
 */
public abstract class NoticeTabAndBackHandledFragment extends BackHandledFragment implements BeeWorksInfo {

    public static final String ID = "ID";
    public static final String DATA_TAB_TITLE = "DATA_TAB_TITLE";
    public static final String DATA_IS_SYSTEM = "DATA_IS_SYSTEM";

    public String mId;

    public String mTabTitle;

    public boolean mIsSystemFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initBeeWorksInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCurrentTheme();
        refreshNetworkStatusUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        refreshNetworkStatusUI();

        if (isVisibleToUser) {
            handleBeeWorksTitle();
        }
    }

    private void handleBeeWorksTitle() {
        if (mActivity == null) {
            return;
        }
        BeeWorksTab beeWorksTab = BeeWorks.getInstance().getBeeWorksTabById(mActivity, mId);
        if (beeWorksTab == null) {
            return;
        }

        if (mActivity instanceof MainActivity) {
//            ((MainActivity) mActivity).onNavigationUpdate(beeWorksTab, mIsSystemFragment);
        }
    }

    @Override
    public void setBeeWorksInfo(String id, String tabId, String tabTitle, boolean isSystemFragment) {
        Bundle bundle = new Bundle();
        bundle.putString(ID, id);
        bundle.putString(DATA_TAB_TITLE, tabTitle);
        bundle.putBoolean(DATA_IS_SYSTEM, isSystemFragment);
        setArguments(bundle);
    }

    @Override
    protected void handleOverallWatermark() {

        FragmentActivity activity = getActivity();

        if(null == activity) {
            return;
        }

        //当是 MainActivity时,
        if (activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) activity;

            if(!mainActivity.isFinishSetOverallWatermark()) {
                super.handleOverallWatermark();
                mainActivity.setOverallWatermarkHandleStatus(true);
            }
            return;
        }


        super.handleOverallWatermark();
    }

    public void initBeeWorksInfo() {
        Bundle bundle = getArguments();
        if (null != bundle) {
            mId = bundle.getString(ID);
            mTabTitle = bundle.getString(DATA_TAB_TITLE);
            mIsSystemFragment = bundle.getBoolean(DATA_IS_SYSTEM);
        }
    }

    public void updateCurrentTheme() {
        Theme theme = SkinMaster.getInstance().getCurrentTheme();
        if (theme != null) {
            SkinMaster.getInstance().notifySkinChange(theme);
            return;
        }
    }

    protected void refreshNetworkStatusUI() {

    }
}
