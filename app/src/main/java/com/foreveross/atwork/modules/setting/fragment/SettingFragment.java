package com.foreveross.atwork.modules.setting.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreverht.cache.MessageCache;
import com.foreverht.db.service.repository.MessageRepository;
import com.foreverht.threadGear.DbThreadPoolExecutor;
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.BaseCallBackNetWorkListener;
import com.foreveross.atwork.api.sdk.configSetting.userSetting.model.UserConfigChatSettings;
import com.foreveross.atwork.api.sdk.configSetting.userSetting.model.UserConfigSettings;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.component.WorkplusSwitchCompat;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.manager.VoipManager;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.common.adapter.CommonAdapter;
import com.foreveross.atwork.modules.configSettings.manager.ConfigSettingsManager;
import com.foreveross.atwork.modules.login.activity.AccountLoginActivity;
import com.foreveross.atwork.modules.login.activity.LoginActivity;
import com.foreveross.atwork.modules.setting.activity.ChangePasswordActivity;
import com.foreveross.atwork.modules.setting.activity.LanguageSettingActivity;
import com.foreveross.atwork.modules.setting.activity.MessagePushSettingActivity;
import com.foreveross.atwork.modules.setting.activity.StorageSpaceSettingActivity;
import com.foreveross.atwork.modules.setting.activity.TextSizeSettingActivity;
import com.foreveross.atwork.modules.setting.activity.WebviewFloatActionSettingActivity;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.services.ImSocketService;
import com.foreveross.atwork.support.BackHandledFragment;
import com.foreveross.atwork.utils.ErrorHandleUtil;


/**
 * 设置页面
 * Created by ReyZhang on 2015/5/6.
 */
@Deprecated
public class SettingFragment extends BackHandledFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = SettingFragment.class.getSimpleName();

    //----------顶部栏-------
    private ImageView mIvBack;
    private TextView mTvTitle;

    private ListView mSettingListView;

    private String[] mSettingNames;
    private TypedArray mSettingIcons;

    private CommonAdapter mCommonAdapter;

    private TextView mLogoutView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        setup();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    protected void findViews(View view) {
        mIvBack = view.findViewById(R.id.title_bar_common_back);
        mTvTitle = view.findViewById(R.id.title_bar_common_title);
        mSettingListView = view.findViewById(R.id.setting_list);
        mSettingListView.setDivider(null);
        mLogoutView = view.findViewById(R.id.setting_logout_tv);
    }

    private void setup() {
        mIvBack.setOnClickListener(this);
        mSettingListView.setOnItemClickListener(this);
        mLogoutView.setOnClickListener(this);

        mCommonAdapter.setSwitcherListener((switchCompat, pos) -> {
            String itemNameSwitch = mSettingNames[pos];

            if(getStrings(R.string.discussion_helper).equals(itemNameSwitch)) {

                handleDiscussionHelperListener(switchCompat);

            }

        });
    }

    private void handleDiscussionHelperListener(WorkplusSwitchCompat switchCompat) {
        UserConfigChatSettings userConfigChatSettings = new UserConfigChatSettings();
        userConfigChatSettings.setChatAssistantEnabled(!switchCompat.isChecked());

        UserConfigSettings userConfigSettings = new UserConfigSettings();
        userConfigSettings.setChatSetting(userConfigChatSettings);

        doHandleDiscussionHelperListener(userConfigSettings, switchCompat);
    }

    private void doHandleDiscussionHelperListener(UserConfigSettings userConfigSettings, WorkplusSwitchCompat switchCompat) {

//        if(CustomerHelper.isWorkplusV4(AtworkApplicationLike.baseContext)) {
//            PersonalShareInfo.getInstance().setSettingDiscussionHelper(AtworkApplicationLike.baseContext, !switchCompat.isChecked());
//            switchCompat.toggle();
//            return;
//        }

        ConfigSettingsManager.INSTANCE.setUserSettings(AtworkApplicationLike.baseContext, userConfigSettings, new BaseCallBackNetWorkListener() {
            @Override
            public void onSuccess() {
                PersonalShareInfo.getInstance().setSettingDiscussionHelper(AtworkApplicationLike.baseContext, !switchCompat.isChecked());
//                PersonalShareInfo.getInstance().setHideDiscussionHelper(AtworkApplication.baseContext, false);
                switchCompat.toggle();
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleError(errorCode, errorMsg);
            }
        });
    }

    private void initData() {
        mTvTitle.setText(getString(R.string.setting));
        mSettingNames = getResources().getStringArray(R.array.setting_array);
        mSettingIcons = null;
        mCommonAdapter = new CommonAdapter(mActivity, mSettingNames, mSettingIcons);
        mSettingListView.setAdapter(mCommonAdapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.title_bar_common_back:
                finish();
                break;

            case R.id.setting_logout_tv:
                handleVoipLeave();
                doLogout();

                break;
        }
    }

    private void doLogout() {
        //延迟100毫秒, 使登出前的网络请求丢出去
        new Handler().postDelayed(() -> {
            AtworkApplicationLike.clearData();
            //AtworkConfig.H3C_CONFIG
            BeeWorks beeWorks = BeeWorks.getInstance();
            if (beeWorks.isBeeWorksFaceBioSettingEnable() && beeWorks.config.beeWorksSetting.getFaceBioSetting().getFaceBioAuth()) {
                mActivity.startActivity(AccountLoginActivity.getLoginIntent(getActivity()));
            } else {
                mActivity.startActivity(LoginActivity.getLoginIntent(getActivity(), true));
            }
            mActivity.setResult(Activity.RESULT_OK);
            SettingFragment.this.finish();

            ImSocketService.closeConnection();
        }, 100);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String name = (String) parent.getItemAtPosition(position);
        //跳转到消息推送设置页面
        Intent intent;
        if (getString(R.string.push_message_setting).equalsIgnoreCase(name)) {
            intent = MessagePushSettingActivity.getIntent(mActivity);
            startActivity(intent);
            return;

        }

        if (getString(R.string.change_password).equalsIgnoreCase(name)) {

            if (AtworkConfig.hasCustomModifyPwdJumpUrl()) {
                //跳转定制的修改密码 web 页面
                WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                        .setUrl(AtworkConfig.AUTH_ROUTE_CONFIG.getCustomModifyPwdJumpUrl())
                        .setNeedShare(false);

                startActivity(WebViewActivity.getIntent(mActivity, webViewControlAction));
                return;

            }

            //跳转到修改密码页面
            intent = ChangePasswordActivity.getIntent(mActivity);
            startActivity(intent);
            return;


        }


        if (getString(R.string.language_setting).equalsIgnoreCase(name)) {
            intent = LanguageSettingActivity.getIntent(mActivity);

            startActivity(intent);
            return;
        }

        if (getString(R.string.text_size).equalsIgnoreCase(name)) {
            intent = TextSizeSettingActivity.getIntent(mActivity);
            startActivity(intent);
            return;
        }

        if (getString(R.string.webview_float_action_helper).equalsIgnoreCase(name)) {
            intent = WebviewFloatActionSettingActivity.Companion.getIntent(mActivity);
            startActivity(intent);
            return;
        }

        if(getString(R.string.storage_space).equalsIgnoreCase(name)){
            intent = StorageSpaceSettingActivity.getIntent(mActivity);
            startActivity(intent);
            return;
        }

        if(getString(R.string.clean_messages_data).equalsIgnoreCase(name)) {

            AtworkAlertDialog atworkAlertDialog = new AtworkAlertDialog(getActivity(), AtworkAlertDialog.Type.SIMPLE)
                    .setContent(getStrings(R.string.clean_messages_data_tip, AtworkConfig.CHAT_CONFIG.getCleanMessagesThreshold()))
                    .setClickBrightColorListener(dialog -> {


                        cleanMessage2Threshold();
                    });

            atworkAlertDialog.show();

            return;
        }

    }

    private void cleanMessage2Threshold() {
        ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper(getActivity());
        progressDialogHelper.show(false, 30000);

        DbThreadPoolExecutor.getInstance().execute(() -> {
            boolean result = MessageRepository.getInstance().cleanMessages2Threshold();

            if(result) {
                MessageCache.getInstance().clear();
            }

            AtworkApplicationLike.runOnMainThread(() -> {
                progressDialogHelper.dismiss();

                if(result) {
                    toastOver(R.string.clean_messages_data_successfully);
                } else {
                    toastOver(R.string.clean_messages_data_unsuccessfully);

                }
            });
        });
    }

    @Override
    protected boolean onBackPressed() {
        finish();
        return false;

    }

    /**
     * 若正在会议中, 退出登录时需要离开会议
     */
    public void handleVoipLeave() {
        if (VoipHelper.isHandlingVoipCall()) {
            VoipManager.leaveMeeting(getActivity(), null, null, VoipManager.getInstance().getVoipMeetingController().getWorkplusVoipMeetingId(), AtworkApplicationLike.getLoginUserHandleInfo(mActivity), null, new VoipManager.OnHandleVoipMeetingListener() {
                @Override
                public void onSuccess() {
                    LogUtil.e("VOIP", "leave success");
                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {

                }
            });
        }
    }

}
