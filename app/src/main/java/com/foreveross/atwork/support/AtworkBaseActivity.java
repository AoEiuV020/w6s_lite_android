package com.foreveross.atwork.support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.broadcast.NetworkBroadcastReceiver;
import com.foreveross.atwork.component.floatView.service.WorkplusFloatService;
import com.foreveross.atwork.cordova.plugin.model.OpenFileDetailRequest;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.manager.OrganizationSettingsManager;
import com.foreveross.atwork.infrastructure.model.advertisement.AdvertisementConfig;
import com.foreveross.atwork.infrastructure.model.biometricAuthentication.BiometricAuthenticationProtectItemType;
import com.foreveross.atwork.infrastructure.model.biometricAuthentication.IBiometricAuthenticationProtected;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.AtworkConstants;
import com.foreveross.atwork.infrastructure.support.VoipSdkType;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.InterceptHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptCacheDisk;
import com.foreveross.atwork.infrastructure.utils.rom.FloatWindowPermissionUtil;
import com.foreveross.atwork.infrastructure.utils.rom.RomUtil;
import com.foreveross.atwork.manager.AgreementManager;
import com.foreveross.atwork.manager.SyncManager;
import com.foreveross.atwork.manager.VoipManager;
import com.foreveross.atwork.manager.VoipNoticeManager;
import com.foreveross.atwork.manager.listener.OnDomainSettingChangeListener;
import com.foreveross.atwork.manager.listener.OnOrgSettingChangeListener;
import com.foreveross.atwork.modules.advertisement.activity.AdvertisementActivity;
import com.foreveross.atwork.modules.advertisement.manager.BootAdvertisementManager;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.chat.util.UndoMessageHelper;
import com.foreveross.atwork.modules.file.fragement.CommonFileStatusFragment;
import com.foreveross.atwork.modules.gesturecode.activity.GestureCodeLockActivity;
import com.foreveross.atwork.modules.login.activity.AccountLoginActivity;
import com.foreveross.atwork.modules.login.activity.LoginSignAgreementActivity;
import com.foreveross.atwork.modules.login.activity.LoginWithAccountActivity;
import com.foreveross.atwork.modules.main.activity.MainActivity;
import com.foreveross.atwork.modules.voip.activity.CallActivity;
import com.foreveross.atwork.modules.voip.activity.agora.AgoraCallActivity;
import com.foreveross.atwork.modules.voip.activity.qsy.QsyCallActivity;
import com.foreveross.atwork.modules.voip.service.CallService;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.services.ImSocketService;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.FloatWinHelper;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;


/**
 * Created by lingen on 15/4/18.
 * Description:
 */
public class AtworkBaseActivity extends BaseActivity implements NetworkBroadcastReceiver.NetworkChangedListener, OnDomainSettingChangeListener, OnOrgSettingChangeListener, IBiometricAuthenticationProtected {

    /**
     * 申请 system_windows_overlay 权限
     */
    public final static int REQUEST_CODE_PERMISSION_WINDOWS_OVERLAY_FOR_VOIP = 7756;

    public static final String ACTION_NEED_INTERCEPT = "com.foreveross.atwork.codelock";

    public static final String FORCE_UPDATE = "force_update";

    public static final String KICK = "KICK";

    public static final String RESET_CREDENTIALS = "RESET_CREDENTIALS";

    public static final String USER_REMOVED = "USER_REMOVED";

    public static final String TOKEN_EXPIRE = "TOKEN_EXPIRE";

    public static final String LICENSE_OVERDUE = "LICENSE_OVERDUE";

    public static final String ACCOUNT_IS_LOCKED = "ACCOUNT_IS_LOCKED";

    public static final String DEVICE_FORBIDDEN = "DEVICE_FORBIDDEN";

    public static final String DEVICE_BINDING = "DEVICE_BINDING";

    public static final String MAINTENANCE_MODE = "MAINTENANCE_MODE";

    public static final String ACTION_SHOW_COMMON_FILE_STATUS_VIEW = "ACTION_SHOW_COMMON_FILE_STATUS_VIEW";
    public static final String DATA_FILE_STATUS_INFO = "DATA_FILE_STATUS_INFO";

    private static NetworkBroadcastReceiver.NetWorkType mLastNetWorkType;

    public boolean mNeedIntercept = true;

    private AtworkAlertDialog mAlertDialog;

    protected NetworkBroadcastReceiver mNetworkBroadcastReceiver;

    private boolean mHasRequestFloatWinPermission = false;


    private BroadcastReceiver mKickBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(KICK.equals(action)) {
                showKickDialog(R.string.click_error_msg, true);

            } else if(USER_REMOVED.equals(action)) {
                showKickDialog(R.string.reset_credentials_error_msg, true);

            } else if(RESET_CREDENTIALS.equals(action)) {
                showKickDialog(R.string.reset_credentials_error_msg, true);

            } else if(TOKEN_EXPIRE.equals(action)) {
                showKickDialog(R.string.access_token_expires, true);

            } else if(ACCOUNT_IS_LOCKED.equals(action)) {
                showKickDialog(R.string.account_is_locked, true);

            } else if(LICENSE_OVERDUE.equals(action)) {
                showKickDialog(R.string.license_overdue, true);

            } else if(DEVICE_FORBIDDEN.equals(action)) {
                showKickDialog(R.string.device_forbidden, true);

            } else if(DEVICE_BINDING.equals(action)) {
                showKickDialog(R.string.device_binding, true);

            }else if (MAINTENANCE_MODE.equalsIgnoreCase(action)) {
                String maintenanceMsg = intent.getStringExtra("MAINTENANCE_MSG");
                showKickDialog(maintenanceMsg, true);
            }
        }
    };

    private BroadcastReceiver mSideBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(AtworkConstants.ACTION_TOAST.equals(action)) {
                if (isVisable()) {
                    String toastContent = intent.getStringExtra(AtworkConstants.DATA_TOAST_STRING);
                    AtworkToast.showToast(toastContent);
                }

                return;
            }



        }
    };


    private BroadcastReceiver mOneLifeBroadcaseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(ACTION_SHOW_COMMON_FILE_STATUS_VIEW.equals(action)) {
                OpenFileDetailRequest fileDetailRequest = intent.getParcelableExtra(DATA_FILE_STATUS_INFO);
                CommonFileStatusFragment commonFileStatusFragment = new CommonFileStatusFragment();
                commonFileStatusFragment.initBundle(null, fileDetailRequest);

                commonFileStatusFragment.show(getSupportFragmentManager(), "FILE_DIALOG");

                return;
            }
        }
    };

    private BroadcastReceiver mSettingsChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(OrganizationSettingsManager.ORG_SETTINGS_CHANGE.equals(action)) {
                onOrgSettingChange();

            } else if(DomainSettingsManager.DOMAIN_SETTINGS_CHANGE.equals(action)) {
                onDomainSettingChange();
            }
        }
    };

    private BroadcastReceiver mUndoMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if (UndoMessageHelper.UNDO_MESSAGE_RECEIVED.equals(intent.getAction())) {
                UndoEventMessage undoEventMessage = (UndoEventMessage) intent.getSerializableExtra(ChatDetailFragment.DATA_NEW_MESSAGE);
                onUndoMsgReceive(undoEventMessage);


            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AtworkUtil.checkBasePermissions(this);
        registerCoreBroadcast();
    }


    @Override
    protected void onStart() {
        super.onStart();

        registerOneLifeBroadcast();
        handleIntercept();

        SyncManager.getInstance().checkSyncStatus(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
//        checkUpdate();

        handleFloatView();


    }

    @Override
    protected void onPause() {
        super.onPause();

//        BingManager.getInstance().getTryHideController().tryHide();

    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterOneLifeBroadcast();

        if(AtworkConfig.OPEN_DISK_ENCRYPTION) {
            EncryptCacheDisk.getInstance().clean();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterCoreBroadcast();

        PermissionsManager.getInstance().clear();
        //手动回收对象内存
        mAlertDialog = null;
        mKickBroadcastReceiver = null;
    }


    private void handleFloatView() {
        /**
         * vivo 机型在锁屏打开时需要做特殊处理, 防止悬浮在锁屏之上
         * */
        if (CallService.isModeNeedScreenControl() && BaseApplicationLike.sIsLock) {
            return;
        }

        if (AtworkConfig.OPEN_VOIP && !AtworkConfig.VOIP_NEED_FLOATVIEW_DESKTOP_SHOW
                && VoipHelper.isSdkBasedVoipHandlingCall()
                && !(this instanceof CallActivity) && !CallActivity.sIsOpening) {




            checkPopPermissionAndCreateFloatWin();
        }


        checkWebUrlHookingFloat();
    }

    protected void checkWebUrlHookingFloat() {
        WorkplusFloatService.Companion.checkWebUrlHookingFloat();
    }



    private void handleIntercept() {
        handleSignAgreement();
    }

    private void handleSignAgreement() {

        if(!AgreementManager.SHOULD_CHECK_AGREEMENT) {
            return;
        }

        if (!shouldInterceptOnStart()) {
            return;
        }


        if(!DomainSettingsManager.getInstance().handleLoginAgreementEnable()) {
            handleCheckBioAuthSetting();
            return;
        }

        if(PersonalShareInfo.getInstance().isLoginSignedAgreementForced(this)) {
            agreementLock();
            return;
        }

        if(!shouldInterceptToAgreement()) {
            handleCheckBioAuthSetting();
            return;
        }

        AgreementManager.isUserLoginAgreementConfirmed(this, agreementStatus -> {
            if(AgreementManager.AgreementStatus.NOT_CONFIRMED == agreementStatus) {
                agreementLock();

            } else {
                handleCheckBioAuthSetting();

            }
        });

    }

    protected void handleCheckBioAuthSetting() {
        if (!shouldInterceptOnStart()) {
            return;
        }

        if(!shouldCheckBioAuthSetting()) {
            return;
        }


        boolean keyHomeBtnStatusForGesture = CommonShareInfo.getKeyHomeBtnStatusForGesture(this);
        CommonShareInfo.setKeyHomeBtnStatusForGesture(this, false);

    }

    private void handleGestureLock() {

        if (!shouldInterceptOnStart()) {
            return;
        }

        boolean keyHomeBtnStatusForGesture = CommonShareInfo.getKeyHomeBtnStatusForGesture(this);
        CommonShareInfo.setKeyHomeBtnStatusForGesture(this, false);


        if(false == PersonalShareInfo.getInstance().getLockCodeSetting(this)) {
            return;
        }

        if(InterceptHelper.sIsLocking) {
            sherlock();
            return;
        }

        if(true == keyHomeBtnStatusForGesture
                && InterceptHelper.isTimeToLock()) {
            sherlock();
            return;
        }



    }

    private void sherlock() {
        Intent intent = GestureCodeLockActivity.getLockIntent(this);
        startActivity(intent);
    }

    private void agreementLock() {
        Intent intent = LoginSignAgreementActivity.getIntent(AtworkBaseActivity.this);
        startActivity(intent);
    }

    private void registerCoreBroadcast() {
        registerKickBroadcast();
        registerSideBroadcast();
        registerSettingChangeBroadcast();
        registerUndoBroadcast();
    }

    private void unRegisterCoreBroadcast() {
        unRegisterKickBroadcast();
        unRegisterSideBroadcast();
        unRegisterSettingChangeBroadcast();
        unRegisterUndoBroadcast();
    }


    private void registerKickBroadcast() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(KICK);
        filter.addAction(RESET_CREDENTIALS);
        filter.addAction(USER_REMOVED);
        filter.addAction(TOKEN_EXPIRE);
        filter.addAction(LICENSE_OVERDUE);
        filter.addAction(ACCOUNT_IS_LOCKED);
        filter.addAction(DEVICE_FORBIDDEN);
        filter.addAction(DEVICE_BINDING);

        LocalBroadcastManager.getInstance(this).registerReceiver(mKickBroadcastReceiver, filter);

    }

    private void unRegisterKickBroadcast() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mKickBroadcastReceiver);
    }

    private void registerSideBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AtworkConstants.ACTION_TOAST);

        LocalBroadcastManager.getInstance(this).registerReceiver(mSideBroadcastReceiver, filter);

    }

    private void registerOneLifeBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SHOW_COMMON_FILE_STATUS_VIEW);

        LocalBroadcastManager.getInstance(this).registerReceiver(mOneLifeBroadcaseReceiver, filter);

    }

    private void unregisterOneLifeBroadcast() {
        if(null != mOneLifeBroadcaseReceiver) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mOneLifeBroadcaseReceiver);
        }
    }

    private void unRegisterSideBroadcast() {
        if(null != mSideBroadcastReceiver) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mSideBroadcastReceiver);
        }
    }


    private void registerSettingChangeBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(OrganizationSettingsManager.ORG_SETTINGS_CHANGE);
        filter.addAction(DomainSettingsManager.DOMAIN_SETTINGS_CHANGE);

        LocalBroadcastManager.getInstance(this).registerReceiver(mSettingsChangeReceiver, filter);
    }

    private void unRegisterSettingChangeBroadcast() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSettingsChangeReceiver);
    }

    private void registerUndoBroadcast() {
        IntentFilter undoIntentFilter = new IntentFilter();
        undoIntentFilter.addAction(UndoMessageHelper.UNDO_MESSAGE_RECEIVED);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mUndoMessageReceiver, undoIntentFilter);
    }

    private void unRegisterUndoBroadcast() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mUndoMessageReceiver);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /** check if received result code
             is equal our requested code for draw permission  */
            if (REQUEST_CODE_PERMISSION_WINDOWS_OVERLAY_FOR_VOIP == requestCode) {
                //if so check once again if we have permission
                if (Settings.canDrawOverlays(this)) {
                    // continue here - permission was granted
                    CallService.sendCreateFloatingWindow();

                } else {
                    popFloatWinForbiddenAlert();

                }


            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }


    private void toLoginActivity() {
        BeeWorks beeWorks = BeeWorks.getInstance();
        if (beeWorks.isBeeWorksFaceBioSettingEnable() && beeWorks.config.beeWorksSetting.getFaceBioSetting().getFaceBioAuth()) {
            startActivity(AccountLoginActivity.getLoginIntent(this));
        } else {
            startActivity(LoginWithAccountActivity.getClearTaskIntent(this));
        }

        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        finish();
    }


    protected void unRegisterNetworkReceiver() {
        if (mNetworkBroadcastReceiver != null) {
            unregisterReceiver(mNetworkBroadcastReceiver);
        }
    }

    protected void registerNetworkReceiver(NetworkBroadcastReceiver.NetworkChangedListener networkChangedListener) {
        IntentFilter intentFilter = new IntentFilter(NetworkBroadcastReceiver.ACTION);
        mNetworkBroadcastReceiver = new NetworkBroadcastReceiver(networkChangedListener);
        registerReceiver(mNetworkBroadcastReceiver, intentFilter);
    }

    @Override
    public void networkChanged(NetworkBroadcastReceiver.NetWorkType networkType) {
        if (mLastNetWorkType == null) {
            mLastNetWorkType = networkType;
        } else {
            //网络有变更
            if (mLastNetWorkType.equals(networkType) == false) {
                ImSocketService.checkConnection(this);
                mLastNetWorkType = networkType;
            }
        }
    }

    public void showKickDialog(int msg, final boolean toLogin) {
        showKickDialog(getString(msg), toLogin);
    }

    public void showKickDialog(String msg, final boolean toLogin) {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            return;
        }
        mAlertDialog = new AtworkAlertDialog(this, AtworkAlertDialog.Type.SIMPLE);
        mAlertDialog.hideDeadBtn().setContent(msg);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.setClickBrightColorListener(dialog -> {
            if (AtworkConfig.H3C_CONFIG) {
                AtworkApplicationLike.clearData();
                AtworkApplicationLike.exitAll(AtworkBaseActivity.this, false);
                return;
            }

            if (toLogin) {
                kickToLogin();
                return;
            }
            //去到首页会话列表
            kickToSessionList();
        });

        mAlertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            //防止两次回调 onkey
            boolean hasHandled = false;

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_BACK == keyCode && !hasHandled) {
                    if (toLogin) {
                        kickToLogin();

                    } else {
                        //去到首页会话列表
                        kickToSessionList();
                    }
                    hasHandled = true;
                    return true;
                }
                return false;
            }
        });
        try {
            if (!isFinishing()) {
                mAlertDialog.show();
                if (toLogin) {
                    //if parent activity is MainActivity, it must stop shake system
                    if (AtworkBaseActivity.this instanceof MainActivity) {
                        MainActivity activity = (MainActivity) AtworkBaseActivity.this;
                        activity.stopShakeListening();
                    }
                    //一弹出帐号异常窗口就断开 IM
                    ImSocketService.closeConnection();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        AtworkUtil.hideInput(this);

    }

    private void kickToLogin() {
//        ImSocketService.closeConnection();
        AtworkApplicationLike.clearData();
        toLoginActivity();
    }

    private void kickToSessionList() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void finish() {
        finish(false);
    }

    public void finish(boolean needAnimation) {
        super.finish();

        if (needAnimation) {
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        startActivity(intent, true);
    }

    public void startActivity(Intent intent, boolean needAnimation) {
        super.startActivity(intent);
        if (needAnimation) {
            //界面切换效果
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
    }



    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode, true);
    }

    public void startActivityForResult(Intent intent, int requestCode, boolean needAnimation) {
        super.startActivityForResult(intent, requestCode);
        //界面切换效果
        if (needAnimation) {
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
    }



    public void checkPopPermissionAndCreateFloatWin() {
        if ((RomUtil.isMeizu() || FloatWinHelper.isXiaomiNeedFloatPermissionCheck())) {
            if (FloatWindowPermissionUtil.isFloatWindowOpAllowed(this)) {
                CallService.sendCreateFloatingWindow();

            } else {
                popFloatWinForbiddenAlert();

            }

        }
        /** check if we already  have permission to draw over other apps */
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            if (!mHasRequestFloatWinPermission) {
                mHasRequestFloatWinPermission = true;

                /** if not construct intent to request permission */
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + this.getPackageName()));
                /** request permission via start activity for result */
                startActivityForResult(intent, REQUEST_CODE_PERMISSION_WINDOWS_OVERLAY_FOR_VOIP);
            }


        } else {

            CallService.sendCreateFloatingWindow();
        }
    }

    private void popFloatWinForbiddenAlert() {
        AtworkAlertDialog alertDialog = new AtworkAlertDialog(this, AtworkAlertDialog.Type.CLASSIC)
                .setTitleText(R.string.float_windows_no_permission_alert_title)
                .setContent(getString(R.string.float_windows_no_permission_voip_alert_content, getString(R.string.app_name), getString(R.string.app_name)))
                .setDeadBtnText(R.string.handup_voip)
                .setBrightBtnText(R.string.revert_voip)
                .setClickDeadColorListener((v)->{
                    VoipManager.getInstance().getVoipMeetingController().stopCall();
                })
                .setClickBrightColorListener((v) -> {

                    Intent intent;
                    if (VoipSdkType.QSY == AtworkConfig.VOIP_SDK_TYPE) {
                        intent = QsyCallActivity.getIntent(BaseApplicationLike.baseContext);
                        intent.putExtra(QsyCallActivity.EXTRA_START_FROM_OUTSIDE, true);

                    } else {
                        intent = AgoraCallActivity.getIntent(BaseApplicationLike.baseContext);
                        intent.putExtra(AgoraCallActivity.EXTRA_START_FROM_OUTSIDE, true);
                    }

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                });

        alertDialog.setCanceledOnTouchOutside(false);
        //禁止返回按钮
        alertDialog.setOnKeyListener((dialog, keyCode, event) -> {

            return true;
        });
        alertDialog.show();
    }



    @Override
    public void onBackFromHome() {
        super.onBackFromHome();


        //检查本地是否同步了login User
        SyncManager.getInstance().checkLoginUserSyncing();

        if (VoipHelper.isHandlingVoipCall()) {
            VoipNoticeManager.getInstance().callingNotificationCancel(this);
        }

        //检查播放广告
        handleAdvertisementLogic();



    }

    /**
     * 处理广告逻辑
     */
    private void handleAdvertisementLogic() {
        //如果没有基本的授权信息或者不存在某组织下广告列表，显示启动页
        String orgId = PersonalShareInfo.getInstance().getCurrentOrg(this);
        OrganizationSettingsManager organizationSettingsManager = OrganizationSettingsManager.getInstance();
        if (!organizationSettingsManager.isAdReAwakenEnabled(orgId)) {
            return;
        }
        long lastViewTime = BootAdvertisementManager.getInstance().getLastViewTimeByOrgId(this, orgId);
        int distMin = organizationSettingsManager.getAdReAwakeTime(orgId);
        if (distMin != 0) {
            if ((TimeUtil.getCurrentTimeInMillis() - lastViewTime) < (distMin * 60 * 1000)) {
                return;
            }
        }

        List<AdvertisementConfig> list = BootAdvertisementManager.getInstance().getLegalLocalAdvertisementsByOrgId(this, orgId);
        if ( ListUtil.isEmpty(list)) {
            return;
        }
        //说明存在有广告逻辑,处理广告逻辑
        String username = LoginUserInfo.getInstance().getLoginUserUserName(this);
        BootAdvertisementManager.Holder holder = BootAdvertisementManager.getInstance().getRandomAdvertisementInList(this, username, orgId);
        BootAdvertisementManager.getInstance().clearRetryTime();
        if (!holder.mAccessAble) {
            return;
        }
        toAdvertisementPage(username, orgId, holder.mAdvertisement);
    }

    private void toAdvertisementPage(String username, String orgId, AdvertisementConfig advertisement) {
        String filePath = AtWorkDirUtils.getInstance().getUserOrgAdvertisementDir(username, orgId) + advertisement.mMediaId;
        Intent intent = AdvertisementActivity.getIntent(this, orgId, advertisement.mId, advertisement.mName, advertisement.mType, filePath, advertisement.mDisplaySeconds, advertisement.mLinkUrl);
        startActivity(intent);
    }


    /**
     * 组织设置变更
     * */
    @Override
    public void onOrgSettingChange() {

    }

    /**
     * 收到撤回消息通知
     * */
    public void onUndoMsgReceive(UndoEventMessage undoEventMessage) {

    }

    /**
     * 是否应该做拦截处理
     * */
    public boolean shouldInterceptOnStart() {
       // return AtworkUtil.hasBasePermissions(this) && LoginUserInfo.getInstance().isLogin(this);
        return LoginUserInfo.getInstance().isLogin(this);
    }

    /**
     * 是否应该做保密协议页面的拦截处理
     * */
    public boolean shouldInterceptToAgreement() {
        return false;
    }

    protected boolean shouldCheckBioAuthSetting() {
        return true;
    }



    /**
     * 生物认证标签
     * @see #getBiometricAuthenticationProtectTags()
     * */
    @Nullable
    public BiometricAuthenticationProtectItemType getBiometricAuthenticationProtectItemTag() {
        return null;
    }

    /**
     * 生物认证标签
     * */
    @Nullable
    public BiometricAuthenticationProtectItemType[] getBiometricAuthenticationProtectTags() {
        return null;
    }

    @Override
    public boolean commandProtected(@NotNull Function2<? super String, ? super Boolean, Unit> getResult) {
        return false;
    }

    protected void showUndoDialog(Context context, PostTypeMessage message) {
        AtworkAlertDialog dialog = new AtworkAlertDialog(context, AtworkAlertDialog.Type.SIMPLE)
                .setContent(UndoMessageHelper.getUndoContent(context, message))
                .hideDeadBtn()
                .setClickBrightColorListener(dialog1 -> finish());
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onDomainSettingChange() {

    }
}
