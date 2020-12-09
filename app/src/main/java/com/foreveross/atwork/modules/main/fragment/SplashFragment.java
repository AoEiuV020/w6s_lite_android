package com.foreveross.atwork.modules.main.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.foreverht.threadGear.HighPriorityCachedTreadPoolExecutor;
import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksHelper;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementConfig;
import com.foreveross.atwork.infrastructure.model.settingPage.W6sGeneralSetting;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.DeviceUtil;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.manager.DomainSettingsHelper;
import com.foreveross.atwork.modules.ad.activity.AppIntroduceActivity;
import com.foreveross.atwork.modules.advertisement.activity.AdvertisementActivity;
import com.foreveross.atwork.modules.advertisement.manager.BootAdvertisementManager;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.main.service.HandleLoginService;
import com.foreveross.atwork.modules.protocol.EditAlertDialogFragment;
import com.foreveross.atwork.modules.workbench.manager.WorkbenchManager;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.IntentUtil;

import java.util.List;


/**
 * Created by lingen on 15/3/24.
 * Description:
 */
public class SplashFragment extends Fragment {

    private Activity mActivity;

    private RelativeLayout mRlRootBg;

    private ImageView mIvSplash;

    private TextView mCompanyCopyright;

    private String mOrgCode;

    private Handler mHandler = new Handler();

    public static final int RESULT_CODE_TO_WEB = -10;
    public static final String RESULT_KEY_WEB_URL = "RESULT_KEY_WEB_URL";

    private boolean mHandleInit = false;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!CommonShareInfo.isIntroduceShowedByVersion(mActivity) && AtworkConfig.SHOW_INTRODUCE) {
            Intent intent = AppIntroduceActivity.getIntent(mActivity);
            startActivity(intent);
            mActivity.finish();
            return;
        }
        if(AtworkUtil.hasBasePermissions(mActivity)) {
            handleInit();
        }



        mOrgCode = PersonalShareInfo.getInstance().getCurrentOrg(mActivity);
        String accessToken = LoginUserInfo.getInstance().getLoginUserAccessToken(mActivity);
        // 如果组织为空，说明没有组织，也就没有广告直接走启动页逻辑, 用户退出也不用播放广告
        if (TextUtils.isEmpty(mOrgCode) || TextUtils.isEmpty(accessToken)) {
            showNormalSplashPage();
            return;
        }

        //有组织，可能存在有广告的逻辑
        handleAdvertisementLogic();
    }

    /**
     * 处理广告逻辑
     */
    private void handleAdvertisementLogic() {

        //如果没有基本的授权信息或者不存在某组织下广告列表，显示启动页
        List<AdvertisementConfig> list = BootAdvertisementManager.getInstance().getLegalLocalAdvertisementsByOrgId(mActivity, mOrgCode);
        if (!AtworkUtil.hasBasePermissions(mActivity) || ListUtil.isEmpty(list)) {
            showNormalSplashPage();
            return;
        }
        //说明存在有广告逻辑,处理广告逻辑
        String username = LoginUserInfo.getInstance().getLoginUserUserName(mActivity);
        BootAdvertisementManager.Holder holder = BootAdvertisementManager.getInstance().getRandomAdvertisementInList(mActivity, username, mOrgCode);
        BootAdvertisementManager.getInstance().clearRetryTime();
        if (!holder.mAccessAble) {
            showNormalSplashPage();
            return;
        }


        toAdvertisementPage(holder.mAdvertisement);
    }

    private void showNormalSplashPage() {
        showBasicSplash();
        startPages();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在调用接下的activity的oncreate()前clear
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HandleLoginService.getInstance().cancel();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void startPages() {
        mHandler.postDelayed(mJumpRunnable, 1500);
    }

    //private Runnable mJumpRunnable = () -> askPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, () -> askPermission(Manifest.permission.READ_EXTERNAL_STORAGE, () -> requestOtherPermission()));
    private Runnable mJumpRunnable = () -> {
        if (!mHandleInit) {
            handleInit();
        }
        if(CommonShareInfo.getProtocol(mActivity) && AtworkConfig.PROTOCOL) {
            showAgreementAndPrivacyDialog();
        }else {
            jumpNext();
        }
    };
    private void requestOtherPermission() {

        askPermission(Manifest.permission.CAMERA, () ->
                askPermission(Manifest.permission.READ_CALL_LOG, () ->
                        requestPhoneStatePermission()));
    }


    public void askPermission(String permissionAsked, OnAskPermissionSuccessListener onAskPermissionSuccessListener) {



        if(Manifest.permission.READ_CALL_LOG.equals(permissionAsked)) {

            //若屏蔽了该入口, 则无需申请
            if(AtworkConfig.SETTING_PAGE_CONFIG.isInvisible(W6sGeneralSetting.EMP_INCOMING_ASSISTANT)) {
                onAskPermissionSuccessListener.onNext();
                return;
            }

        }


        if(Manifest.permission.CAMERA.equals(permissionAsked)) {
            onAskPermissionSuccessListener.onNext();
            return;
        }

        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mActivity, new String[]{permissionAsked}, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                onAskPermissionSuccessListener.onNext();
            }

            @Override
            public void onDenied(String permission) {
                AtworkUtil.popAuthSettingAlert(mActivity, permission, false);
            }
        });

    }

    interface OnAskPermissionSuccessListener {
        void onNext();
    }

    private void showAgreementAndPrivacyDialog(){
        EditAlertDialogFragment alertDialogFragment = new EditAlertDialogFragment();
        alertDialogFragment.setTitle(mActivity, R.string.user_agreement_and_privacy_policy_title);
        alertDialogFragment.setTvContent(mActivity,R.string.user_agreement_and_privacy_policy_content, true);
        alertDialogFragment.setTvLeft(mActivity,R.string.left_btn_content);
        alertDialogFragment.setTvRight(mActivity,R.string.right_btn_content);
        alertDialogFragment.setViewUnderLine(R.color.tangsdk_menu_item_pressed_color, 1);
        alertDialogFragment.setTvContentOther(mActivity, R.string.policy_protocol);
        alertDialogFragment.setTvContentThird(mActivity, R.string.policy_service);
        alertDialogFragment.isColseOutSideView(true);
        alertDialogFragment.setContentOtherOnclick(item -> {
            String url = UrlConstantManager.getInstance().getProtocolPrivacyUrl();
            WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(url).setTitle(BaseApplicationLike.baseContext.getString(R.string.policy_protocol)).setNeedShare(false);
            Intent intentJump = WebViewActivity.getIntent(getActivity(), webViewControlAction);
            startActivity(intentJump);
        });
        alertDialogFragment.setContentThirdOnclick(item -> {
            String url = UrlConstantManager.getInstance().getProtocolProtocolUrl();
            WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(url).setTitle(BaseApplicationLike.baseContext.getString(R.string.policy_service)).setNeedShare(false);
            Intent intentJump = WebViewActivity.getIntent(getActivity(), webViewControlAction);
            startActivity(intentJump);
        });

        alertDialogFragment.setLeftOnclick(item -> {
            EditAlertDialogFragment editAlertDialogFragmentChildren = new EditAlertDialogFragment();
            editAlertDialogFragmentChildren.setTitle(getActivity(), R.string.user_agreement_and_privacy_policy_children_content);
            editAlertDialogFragmentChildren.setLeftOnclick(item1 -> editAlertDialogFragmentChildren.dismiss());
            editAlertDialogFragmentChildren.setRightOnclick(item12 -> {
                editAlertDialogFragmentChildren.dismiss();
                alertDialogFragment.dismiss();
                getActivity().finish();
            });


            try {
                editAlertDialogFragmentChildren.show(getActivity().getSupportFragmentManager(), "MAIN_ACTIVITY");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        alertDialogFragment.setRightOnclick(item -> {
            alertDialogFragment.dismiss();
            CommonShareInfo.setProtocol(mActivity,false);
            jumpNext();
        });

        if (isAdded()) {
            try {
                alertDialogFragment.show(getActivity().getSupportFragmentManager(), "MAIN_ACTIVITY");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void requestPhoneStatePermission() {

        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mActivity, new String[]{Manifest.permission.READ_PHONE_STATE}, new PermissionsResultAction() {
            @Override
            public void onGranted() {


                if (!mHandleInit) {
                    handleInit();
                }


                if(CommonShareInfo.getProtocol(mActivity) && AtworkConfig.PROTOCOL) {
                    showAgreementAndPrivacyDialog();
                }else {
                    jumpNext();
                }


            }

            @Override
            public void onDenied(String permission) {
                AtworkUtil.popAuthSettingAlert(mActivity, permission, false);
            }

        });
        CommonShareInfo.setVpnPermissionHasShown(mActivity, true);
    }




    private void handleInit() {


        //初始化设备ID
        DeviceUtil.initDeviceId(BaseApplicationLike.baseContext);

        handleInitAfterGetDeviceId();

        HighPriorityCachedTreadPoolExecutor.getInstance().execute(() -> {
            FileUtil.copyAssetsToSdCard(AtworkApplicationLike.sApp, "STICKER", AtWorkDirUtils.getInstance().getStickerRootPath());
        });

        WorkbenchManager.INSTANCE.initCurrentOrgWorkbench();

        mHandleInit = true;
    }

    private void handleInitAfterGetDeviceId() {
        AtworkApplicationLike.initDomainAndOrgSettings();
        DomainSettingsHelper.getInstance().getDomainSettingsFromRemote(BaseApplicationLike.baseContext, true, null);

    }


    public void jumpNext() {
        HandleLoginService.getInstance().toStart(mActivity, mHandler, 0);
        PermissionsManager.getInstance().clear();
    }

    private void initViews(View view) {
        mIvSplash = view.findViewById(R.id.iv_splash);

        mRlRootBg = view.findViewById(R.id.rl_root_bg);
        mCompanyCopyright = mRlRootBg.findViewById(R.id.company_copyright);


        try {

            mCompanyCopyright.setText(BeeWorksHelper.getCopyright(AtworkApplicationLike.baseContext, AtworkConfig.SPLASH_CONFIG.getNeedCompanyUrl()));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public void showBasicSplash() {
        mRlRootBg.setVisibility(View.VISIBLE);
        mIvSplash.setVisibility(View.VISIBLE);
        if (AtworkConfig.SPLASH_CONFIG.isPureImgBg()) {
            mCompanyCopyright.setVisibility(View.GONE);

        } else {
            mCompanyCopyright.setVisibility(View.VISIBLE);
        }
    }

    private void toAdvertisementPage(AdvertisementConfig advertisement) {
        HandleLoginService.getInstance().cancel();
        mHandler.removeCallbacks(mJumpRunnable);
        String username = LoginUserInfo.getInstance().getLoginUserUserName(mActivity);
        String filePath = AtWorkDirUtils.getInstance().getUserOrgAdvertisementDir(username, mOrgCode) + advertisement.mMediaId;
        Intent intent = AdvertisementActivity.getIntent(mActivity, mOrgCode, advertisement.mId, advertisement.mName, advertisement.mType, filePath, advertisement.mDisplaySeconds, advertisement.mLinkUrl);
        mActivity.startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_TO_WEB) {
            String url = data.getStringExtra(RESULT_KEY_WEB_URL);
            if (TextUtils.isEmpty(url)) {
                HandleLoginService.getInstance().toStart(mActivity, mHandler, 0);
                return;
            }
            Intent intent = WebViewActivity.getIntentBeforeLogin(mActivity, url, true);
            startActivity(intent);
            return;
        }
        HandleLoginService.getInstance().toStart(mActivity, mHandler, 0);
    }
}

