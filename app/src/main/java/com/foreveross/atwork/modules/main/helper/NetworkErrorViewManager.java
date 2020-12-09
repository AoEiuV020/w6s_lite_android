package com.foreveross.atwork.modules.main.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.support.AtworkConstants;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil;
import com.foreveross.atwork.modules.main.model.MainTitleType;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by reyzhang22 on 17/8/16.
 */

public class NetworkErrorViewManager {

    private TextView mNetworkView;
    private RelativeLayout mNetworkLayout;
    private Set<MainTitleType> mTitleTypeList = new HashSet<>();

    public NetworkErrorViewManager(View rootView) {
        mNetworkLayout = rootView.findViewById(R.id.layout_network_error);
        mNetworkView = rootView.findViewById(R.id.main_network_error);
    }


    public void refreshNetworkStatusUI(boolean hasNetWork) {

        LogUtil.e("refreshNetworkStatusUI hasNetWork : " + hasNetWork);

        if (hasNetWork) {
            hiddenNetErrorBar();
            //为了防止碰巧没有网络时5秒的 handler 返回时出现的冲突, 碰巧会把mNetworkView最终显示出来, 导致错误
            new Handler().postDelayed(() -> {
                if (NetworkStatusUtil.isNetworkAvailable(BaseApplicationLike.baseContext)) {
                    hiddenNetErrorBar();
                }
            }, 500);
            return;
        }

        new Handler().postDelayed(() -> {
            if (NetworkStatusUtil.isNetworkAvailable(BaseApplicationLike.baseContext)) {
                hiddenNetErrorBar();
                return;
            }
            showNetErrorBar();
        }, 5 * 1000);
    }

    public static void notifyIMError(Context context) {
        Intent intent = new Intent(AtworkConstants.IM_INFO_INTENT);
        intent.putExtra(AtworkConstants.IM_STATUS, AtworkConstants.IM_ERROR);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void notifyIMSuccess(Context context) {
        Intent intent = new Intent(AtworkConstants.IM_INFO_INTENT);
        intent.putExtra(AtworkConstants.IM_STATUS, AtworkConstants.IM_OK);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


    public void registerImReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter(AtworkConstants.IM_INFO_INTENT);
        LocalBroadcastManager.getInstance(context).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra(AtworkConstants.IM_STATUS);


                if (AtworkConstants.IM_ERROR.equals(status)) {

                    //有网络的情况下才显示IM异常
                    if (null != AtworkApplicationLike.sNetWorkType && AtworkApplicationLike.sNetWorkType.hasNetwork()) {
                        addType(MainTitleType.IM_ERROR);
                        refreshMainTitleView();
                    }

                } else if (AtworkConstants.IM_OK.equals(status)) {
                    removeType(MainTitleType.IM_ERROR);
                    refreshMainTitleView();
                }
            }
        }, intentFilter);
    }

    private void showNetErrorBar() {
        addType(MainTitleType.NETWORK_ERROR);
        refreshMainTitleView();
    }

    private void hiddenNetErrorBar() {
        removeType(MainTitleType.NETWORK_ERROR);
        refreshMainTitleView();
    }

    private void showImError() {
        if (mNetworkView == null) {
            return;
        }
        mNetworkLayout.setVisibility(View.VISIBLE);
        mNetworkView.setText(R.string.network_error);
    }

    private void hiddenError() {
        if (mNetworkView == null) {
            return;
        }
        mNetworkLayout.setVisibility(View.GONE);
    }

    private void addType(MainTitleType mainTitleType) {
        mTitleTypeList.add(mainTitleType);
    }

    private void removeType(MainTitleType mainTitleType) {
        mTitleTypeList.remove(mainTitleType);
    }

    private void refreshMainTitleView() {
        boolean wasHandled = false;
        if (mTitleTypeList.contains(MainTitleType.NETWORK_ERROR)) {
            showNetworkError();
            wasHandled = true;

        } else {
            if (mTitleTypeList.contains(MainTitleType.IM_ERROR)) {
                showImError();
                wasHandled = true;
            }
        }

        if (!wasHandled) {
            hiddenError();
        }
    }

    private void showNetworkError() {
        if (mNetworkView == null) {
            return;
        }
        mNetworkLayout.setVisibility(View.VISIBLE);
        mNetworkView.setText(R.string.network_error);
    }

}
