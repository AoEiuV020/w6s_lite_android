package com.foreveross.atwork.modules.common.fragment;/**
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                       __           __
 .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 |________|_____|__|  |__|__|   __||__||_____|_____|
                            |__|
 */


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.app.model.LightNoticeData;
import com.foreveross.atwork.api.sdk.util.LightNoticeHelper;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.common.adapter.SelfHomeTabAdapter;
import com.foreveross.atwork.modules.common.lightapp.LightNoticeMapping;
import com.foreveross.atwork.modules.common.lightapp.SimpleLightNoticeMapping;
import com.foreveross.atwork.modules.main.data.TabNoticeManager;
import com.foreveross.atwork.modules.qrcode.activity.QrcodeScanActivity;
import com.foreveross.atwork.support.NoticeTabAndBackHandledFragment;
import com.foreveross.atwork.utils.AtworkUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by reyzhang22 on 15/11/23.
 */
public class SelfHomeTabFragment extends NoticeTabAndBackHandledFragment {

    public static final String TAB_ID = "find";

    private static final String TAG = SelfHomeTabFragment.class.getSimpleName();

    private ListView mListView;

    private SelfHomeTabAdapter mAdapter;

    private Map<String, LightNoticeMapping> mLightNoticeMap = new HashMap<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_self_define_home_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshAppLightNoticeModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLightAppNotices(mLightNoticeMap);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    protected boolean onBackPressed() {
        return false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser) {
            loadLightAppNotices(mLightNoticeMap);
        }
    }



    @Override
    protected void findViews(View view) {
        mListView = view.findViewById(R.id.self_define_home_tab_list);
        mAdapter = new SelfHomeTabAdapter(mActivity);
        mListView.setAdapter(mAdapter);
        //取消默认的分割线
        mListView.setDivider(null);

    }

    private void registerListener() {
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            String name = (String) parent.getItemAtPosition(position);
            handleItemClickEvent(name);
        });
    }

    public void refreshAppLightNoticeModel() {
        LightNoticeMapping lightNoticeModel  = SimpleLightNoticeMapping.createInstance(AtworkConfig.COLLEAGUE_URL, mId, TabNoticeManager.getInstance().getCircleAppId(getActivity()));
        mLightNoticeMap.put(lightNoticeModel.getAppId(), lightNoticeModel);
    }

    private void loadLightAppNotices(Map<String, LightNoticeMapping> lightNoticeMap) {
        for (Map.Entry<String, LightNoticeMapping> entry : lightNoticeMap.entrySet()) {
            final LightNoticeMapping lightNoticeModel = entry.getValue();

            //注册此轻应用 //TODO 放在该处不太合理
            TabNoticeManager.getInstance().registerLightNoticeMapping(lightNoticeModel);

            LightNoticeHelper.loadLightNotice(lightNoticeModel.getNoticeUrl(), mActivity, new LightNoticeHelper.LightNoticeListener() {
                @Override
                public void success(LightNoticeData lightNoticeJson) {
                    TabNoticeManager.getInstance().update(lightNoticeModel, lightNoticeJson);

                    refreshAdapter();
                }

                @Override
                public void fail() {
                    TabNoticeManager.getInstance().update(mId);
                }
            });
        }
    }

    private void refreshAdapter() {
        if(null != mAdapter) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void handleItemClickEvent(String eventName) {
        //跳转到设置页
        if (getString(R.string.employee_benefits).equals(eventName)) {
            String url = "http://112.124.103.109:8888/active.html";
            WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(url);
            Intent intent = WebViewActivity.getIntent(mActivity, webViewControlAction);
            startActivity(intent);
            //界面切换效果
            mActivity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            return;
        }

        //跳转到同事圈
        if (getString(R.string.college_circle).equals(eventName)) {
            String url = "file:///android_asset/www/colleague-circle/main/index.html";

            WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(url).setHideTitle(true);
            Intent intent = WebViewActivity.getIntent(mActivity, webViewControlAction);
            startActivity(intent);
            //界面切换效果
            mActivity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            return;
        }

        if (getString(R.string.qrcode).equals(eventName)) {
            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this, new String[]{Manifest.permission.CAMERA}, new PermissionsResultAction() {
                @Override
                public void onGranted() {
                    Intent intent = QrcodeScanActivity.getIntent(mActivity);
                    startActivity(intent);
                    //界面切换效果
                    mActivity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }

                @Override
                public void onDenied(String permission) {
                    AtworkUtil.popAuthSettingAlert(getContext(), permission);
                }
            });

            return;
        }
    }
}
