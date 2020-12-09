package com.foreveross.atwork.modules.login.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.BuildConfig;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.auth.model.LoginDeviceNeedAuthResult;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.chat.SyncMessageMode;
import com.foreveross.atwork.infrastructure.model.domain.CommonUsingSetting;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.shared.bugFix.W6sBugFixPersonShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AppUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.modules.app.manager.AppManager;
import com.foreveross.atwork.manager.EmployeeManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.device.activity.LoginDeviceAuthActivity;
import com.foreveross.atwork.modules.device.activity.LoginDeviceAuthNoMobileWarnActivity;
import com.foreveross.atwork.modules.gesturecode.activity.GestureCodeInputActivity;
import com.foreveross.atwork.modules.gesturecode.fragment.GestureCodeInputFragment;
import com.foreveross.atwork.modules.login.activity.AccountLoginActivity;
import com.foreveross.atwork.modules.login.activity.LoginWithAccountActivity;
import com.foreveross.atwork.modules.login.listener.LoginNetListener;
import com.foreveross.atwork.modules.login.model.LoginControlViewBundle;
import com.foreveross.atwork.modules.login.model.LoginHandleBundle;
import com.foreveross.atwork.modules.login.service.LoginService;
import com.foreveross.atwork.modules.route.action.MainRouteAction;
import com.foreveross.atwork.modules.route.action.RouteAction;
import com.foreveross.atwork.modules.setting.activity.ChangePasswordActivity;
import com.foreveross.atwork.support.BaseActivity;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.ImageViewUtil;
import com.foreveross.atwork.utils.OutFieldPunchHelper;

/**
 * Created by dasunsy on 2017/4/7.
 */

public class LoginHelper {
    private static boolean isDebugModifyPwd = BuildConfig.DEBUG && false;
    private static boolean isDebugModifyGestureLock = BuildConfig.DEBUG && false;

    public static void doLogin(BaseActivity activity, LoginHandleBundle loginHandleBundle) {
        String username = loginHandleBundle.getUsername();
        String password = loginHandleBundle.getPsw();
        String secureCode = loginHandleBundle.getSecureCode();
        LoginControlViewBundle loginControlViewBundle = loginHandleBundle.getLoginControlViewBundle();
        boolean refreshCode = loginHandleBundle.isRefreshCode();


        LoginService loginNetService = new LoginService(activity);
        final Context context = BaseApplicationLike.baseContext;
        loginNetService.
                login(username, password, secureCode, AppUtil.getVersionName(context), new LoginNetListener() {
            @Override
            public void loginSuccess(String clientId, boolean needInitPwd) {
                loginControlViewBundle.mDialogHelper.dismiss();
                handleFinishLogin(activity, needInitPwd, username, password);

            }

            @Override
            public void loginDeviceNeedAuth(LoginDeviceNeedAuthResult result) {
                loginControlViewBundle.mDialogHelper.dismiss();

                if(!StringUtils.isEmpty(result.getUserPhone())) {
                    Intent intent = LoginDeviceAuthActivity.getIntent(activity, result);
                    activity.startActivity(intent);

                } else {
                    Intent intent = LoginDeviceAuthNoMobileWarnActivity.getIntent(activity);
                    activity.startActivity(intent);
                }
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                loginControlViewBundle.mDialogHelper.dismiss();

                if (errorCode == ErrorHandleUtil.MAINTENANCE_MODE || errorCode == ErrorHandleUtil.MAINTENANCE_MODE2) {
                    AtworkToast.showLongToast(ErrorHandleUtil.getMaintenanceMsg(errorMsg));
                    return;
                }
                if (errorCode == 201009) {
                    showSecureCodeView(activity, loginControlViewBundle);
                    return;
                }

                if (errorCode == 201032 || errorCode == 201033) {
                    if (loginControlViewBundle.mEtPassword != null) {
                        new android.os.Handler().postDelayed(() -> activity.runOnUiThread(() -> loginControlViewBundle.mEtPassword.setText("")), 100);
                    }
                    String url = String.format(AtworkConfig.OCT_CONFIG.getOctModifyPwdUrl(), username);
                    WebViewControlAction action = WebViewControlAction.newAction().setHideTitle(true).setNeedShare(false).setUrl(url).setNeedAuth(false);
                    activity.startActivity(WebViewActivity.getIntent(activity, action));
                    return;
                }
                if (refreshCode) {
                    getSecureCodeRemote(loginControlViewBundle.mSecureCodeView);
                }

                ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Login, errorCode, errorMsg);
            }
        });
    }

    public static void handleFinishLogin(Activity activity, boolean needInitPwd, String username, String password) {
        //切换 imageLoaderdle.
        ImageCacheHelper.refreshLoader(activity);

        //根据配置设置拉取离线时间点
        resetPullOfflineMsgTime(activity);

        //重置 app 同步状态
        AppManager.getInstance().initAppSyncStatusInLogin();

        LoginHelper.handleFirstLogin(activity, needInitPwd, CommonShareInfo.isUserLoginedBefore(activity, username), password);


        saveLoginRecord(activity, username);

        saveUserFromRemote(activity, username);
        OutFieldPunchHelper.startAllOutFieldIntervalPunch(activity);
        AtworkUtil.onEmpIncomingDataCheck(activity.getApplicationContext());
    }

    private static void saveLoginRecord(Context context, String username) {
        if(!CommonShareInfo.isUserLoginedBefore(context, username)) {
            W6sBugFixPersonShareInfo.INSTANCE.setMakeCompatibleForSessionTopAndShield(context, true);
        }

        CommonShareInfo.saveLoginUserNameRecord(context, username);
    }

    private static void resetPullOfflineMsgTime(Context context) {
        if(SyncMessageMode.MODE_ONLY_FETCH_RECENT_MSGS == CommonShareInfo.getSelectFetchMessageMode(context)) {
            int fetchInDays = CommonShareInfo.getSelectFetchMessagesDays(context);
            if(0 == fetchInDays) {
                PersonalShareInfo.getInstance().setLatestMessageTime(context, TimeUtil.getCurrentTimeInMillis(), null);

            } else {
                long lastMsgTime = PersonalShareInfo.getInstance().getLatestMessageTime(context);
                long fetchInDayTime = TimeUtil.getTimeInMillisDaysBefore(fetchInDays);
                if(fetchInDayTime > lastMsgTime) {
                    PersonalShareInfo.getInstance().setLatestMessageTime(context, fetchInDayTime, null);
                }
            }


        }
    }

    public static void getSecureCodeRemote(ImageView secureCode) {
        String secureCodeUrl = String.format(UrlConstantManager.getInstance().getLoginSecureCode(), AtworkConfig.getDeviceId(), AtworkConfig.getDeviceId(), System.currentTimeMillis());
        ImageCacheHelper.displayImage(secureCodeUrl, secureCode, ImageCacheHelper.getNotCacheOptions());
    }

    private static void showSecureCodeView(Activity activity, LoginControlViewBundle loginControlViewBundle) {
        activity.runOnUiThread(() -> {
            setFakeMaxHeightWhenHavingSecureLayout(loginControlViewBundle);
            loginControlViewBundle.mSecureCodeLayout.setVisibility(View.VISIBLE);

            getSecureCodeRemote(loginControlViewBundle.mSecureCodeView);
        });
    }

    private static void setFakeMaxHeightWhenHavingSecureLayout(LoginControlViewBundle loginControlViewBundle) {
        loginControlViewBundle.mSecureCodeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int secureLayoutHeight = loginControlViewBundle.mSecureCodeLayout.getHeight();
                int forgetPwdHeight = loginControlViewBundle.mTvForgetPwd.getHeight();

                LogUtil.e("secureLayoutHeight -> " + secureLayoutHeight);
                ImageViewUtil.setMaxHeight(loginControlViewBundle.mIvFakeHeader, (int) (ScreenUtils.getScreenHeight(BaseApplicationLike.baseContext) * 0.45) - secureLayoutHeight - forgetPwdHeight);

                loginControlViewBundle.mSecureCodeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            }
        });
    }

    public static void refreshSelfUserFromRemote() {
        saveUserFromRemote(AtworkApplicationLike.baseContext, LoginUserInfo.getInstance().getLoginUserUserName(AtworkApplicationLike.baseContext));
    }

    public static void refreshSelfEmpsFromRemote() {
        EmployeeManager.getInstance().queryOrgAndEmpListRemote(AtworkApplicationLike.baseContext, LoginUserInfo.getInstance().getLoginUserId(AtworkApplicationLike.baseContext), null);
    }

    private static void saveUserFromRemote(Context context, final String loginUsername) {
        //将当前用户存入
        UserManager.getInstance().asyncFetchLoginUserFromRemote(context, new UserAsyncNetService.OnQueryUserListener() {
            @Override
            public void onSuccess(@NonNull User loginUser) {
                LoginUserInfo.getInstance().setLoginUserBasic(context, loginUser.mUserId, loginUser.mDomainId, loginUsername.toLowerCase(), loginUser.mUsername, loginUser.getShowName(), loginUser.mAvatar, loginUser.mSignature);
                UserManager.getInstance().asyncAddUserToLocal(loginUser);

            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                ErrorHandleUtil.handleError(errorCode, errorMsg);
            }

        });
    }

    public static void handleFirstLogin(Activity activity, boolean needInitPwd, boolean isUserLoginBefore, String pwd) {
        if (CommonUsingSetting.DISABLED != DomainSettingsManager.getInstance().handleFirstLoginPasswordSetting()) {
            boolean hvResetPwdMode = PersonalShareInfo.ResetMode.MODIFY_PWD == PersonalShareInfo.getInstance().getResetMode(activity);

            if (isDebugModifyPwd || hvResetPwdMode || needInitPwd) {
                //跳转修改密码界面
                goChangePwdActivity(activity, pwd, isUserLoginBefore);
                activity.finish();

                if (CommonUsingSetting.FORCE == DomainSettingsManager.getInstance().handleFirstLoginPasswordSetting()) {
                    PersonalShareInfo.getInstance().setResetMode(activity, PersonalShareInfo.ResetMode.MODIFY_PWD);
                }
                return;

            }
        }

        activity.finish();
        new MainRouteAction().action(activity);
    }

    @Deprecated
    public static void handleNextJump_v0(BaseActivity activity, boolean isUserLoginBefore) {
        if (CommonUsingSetting.DISABLED == DomainSettingsManager.getInstance().handleFirstLoginGestureLockSetting()) {
            goMainActivity(activity);

        } else {
            boolean hvResetGestureLock = PersonalShareInfo.ResetMode.INIT_GESTURE_LOCK == PersonalShareInfo.getInstance().getResetMode(activity);

            if (isDebugModifyGestureLock || hvResetGestureLock || !isUserLoginBefore) {
                //跳转设置手势密码界面
                goGestureInputActivity(activity);

                if (CommonUsingSetting.FORCE == DomainSettingsManager.getInstance().handleFirstLoginGestureLockSetting()) {
                    PersonalShareInfo.getInstance().setResetMode(activity, PersonalShareInfo.ResetMode.INIT_GESTURE_LOCK);
                }

            } else {
                goMainActivity(activity);
            }
        }

    }

    public static void goMainActivity(BaseActivity activity) {
        new MainRouteAction().action(activity);
    }


    public static void goChangePwdActivity(Activity activity, String oldPwd, boolean isUserLoginBefore) {
        Intent intent = ChangePasswordActivity.getIntent(activity, ChangePasswordActivity.Mode.INIT_CHANGE, oldPwd, isUserLoginBefore);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    public static void goGestureInputActivity(BaseActivity activity) {
        Intent intent = GestureCodeInputActivity.getIntent(activity, GestureCodeInputFragment.Mode.INIT_ADD);
//        intent.putExtra(HandleLoginService.DATA_FROM_LOGIN, true);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }


    public static void logout(Activity activity) {
        AtworkApplicationLike.clearData();
        BeeWorks beeWorks = BeeWorks.getInstance();
        if (beeWorks.isBeeWorksFaceBioSettingEnable() && beeWorks.config.beeWorksSetting.getFaceBioSetting().getFaceBioAuth()) {
            activity.startActivity(AccountLoginActivity.getLoginIntent(activity));
        } else {
            activity.startActivity(LoginWithAccountActivity.getClearTaskIntent(activity));
        }
        activity.finish();
    }
}
