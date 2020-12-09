package com.foreveross.atwork.modules.main.service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.model.IES;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.permissions.PermissionsResultAction;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.manager.IESInflaterManager;
import com.foreveross.atwork.modules.lite.activity.LiteBindEntryActivity;
import com.foreveross.atwork.modules.lite.manager.LiteManager;
import com.foreveross.atwork.modules.login.activity.AccountLoginActivity;
import com.foreveross.atwork.modules.login.activity.LoginActivity;
import com.foreveross.atwork.modules.login.activity.LoginWithAccountActivity;
import com.foreveross.atwork.modules.main.activity.MainActivity;
import com.foreveross.atwork.modules.qrcode.activity.QrcodeScanActivity;
import com.foreveross.atwork.modules.route.action.RouteAction;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.OutFieldPunchHelper;
import com.foreveross.atwork.utils.UserRemoveHelper;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;

/**
 * Created by dasunsy on 15/12/15.
 */
public class HandleLoginService {

    public final static String DATA_ACTION_INTENT = "DATA_ACTION_INTENT";
    public static final String DATA_FROM_LOGIN = "DATA_FROM_LOGIN";


    private static HandleLoginService mInstance;

    public Runnable mMainRunnable;
    public Runnable mLoginRunnable;
    public Handler mHandler;

    private RouteAction mRouteAction;

    private HandleLoginService (){

    }

    public static HandleLoginService getInstance() {
        if(null == mInstance) {
            mInstance = new HandleLoginService();
        }

        return mInstance;
    }

    public void toStart(final Activity activity, Handler handler, long waitTime) {
        if(UserRemoveHelper.isEncryptModeMatch(activity)) {


            if(PersonalShareInfo.getInstance().needReset(activity)) {
                //清除模式, 以最新域设置为准
                PersonalShareInfo.getInstance().setResetMode(AtworkApplicationLike.baseContext, PersonalShareInfo.ResetMode.NONE);

                LoginUserInfo.getInstance().clear(activity);
                toLoginActivity(activity, handler, waitTime, true);

            } else {
                if (LoginUserInfo.getInstance().isLogin(activity)){
                    //外勤轮询打卡
                    OutFieldPunchHelper.startAllOutFieldIntervalPunch(BaseApplicationLike.baseContext);

                    toMainActivity(activity, handler, waitTime);
                } else {
                    toLoginActivity(activity, handler, waitTime, false);
                }
            }

        } else {
            AtworkAlertDialog atworkAlertDialog = new AtworkAlertDialog(activity, AtworkAlertDialog.Type.SIMPLE)
                    .setContent(R.string.encrypt_version_change_tip)
                    .hideDeadBtn()
                    .setForbiddenBack()
                    .setClickBrightColorListener(dialog -> {

                        LoginUserInfo.getInstance().clear(activity);
                        toLoginActivity(activity, handler, waitTime, true);

                    });
            atworkAlertDialog.show();

        }

    }


    private void routeQrScan(Activity context) {

//        AndPermission
//                .with(context)
//                .runtime()
//                .permission(Permission.CAMERA)
//                .onGranted(data -> {
//                    Intent intent = QrcodeScanActivity.getIntent(context);
//                    context.startActivity(intent);
//
//                    context.finish();
//                })
//                .onDenied(data -> AtworkUtil.popAuthSettingAlert(context, data.get(0), false))
//                .start();

        Intent intent = LiteBindEntryActivity.getIntent(context);
        context.startActivity(intent);
        context.finish();

    }


    public void cancel() {
        if(null != mHandler) {
            mHandler.removeCallbacks(mMainRunnable);
            mHandler.removeCallbacks(mLoginRunnable);
        }

        mMainRunnable = null;
        mLoginRunnable = null;

    }

    private void toMainActivity(final Activity activity, Handler handler, long waitTime) {

        mMainRunnable = () -> {
            Intent intent = new Intent();
            intent.setClass(activity, MainActivity.class);
            activity.startActivity(intent);
            activity.finish();

            mMainRunnable = null;
        };
        mHandler = handler;
        mHandler.postDelayed(mMainRunnable, waitTime);

    }

    private void toLoginActivity(final Activity activity, Handler handler, long waitTime, boolean force) {

        mLoginRunnable = () -> {

            //这个处理会有问题，延迟处理，但是可能activity已经不存在了
            toLoginHandle(activity, null, force);

            mLoginRunnable = null;
        };

        mHandler = handler;
        mHandler.postDelayed(mLoginRunnable, waitTime);

    }

    public static void toLoginHandle(Activity activity, Intent actionIntent, boolean force) {
        if (null == activity) {
            return;
        }
        Intent intent = getLoginIntent(activity, force);
        intent.putExtra(DATA_ACTION_INTENT, actionIntent);
        activity.startActivity(intent);
        activity.finish();
    }

    private static Intent getLoginIntent(Context ctx, boolean force) {
        BeeWorks beeWorks = BeeWorks.getInstance();
        if (force || TextUtils.isEmpty(LoginUserInfo.getInstance().getLoginUserId(ctx)) || !UserRemoveHelper.isCurrentUserEncryptModeMatch(ctx)) {
            if (beeWorks.isBeeWorksFaceBioSettingEnable() && beeWorks.config.beeWorksSetting.getFaceBioSetting().getFaceBioAuth()) {
                return AccountLoginActivity.getLoginIntent(ctx);
            }
            return LoginActivity.getLoginIntent(ctx, false);
        }

        if (AtworkConfig.H3C_CONFIG) {
            IES ies = IESInflaterManager.getInstance().getIESAccount(ctx);
            if (ies != null) {
                if (beeWorks.isBeeWorksFaceBioSettingEnable() && beeWorks.config.beeWorksSetting.getFaceBioSetting().getFaceBioAuth()) {
                    return AccountLoginActivity.getLoginIntent(ctx);
                }
                return LoginActivity.getLoginIntent(ctx, false);
            }

        }
        if (beeWorks.isBeeWorksFaceBioSettingEnable() && beeWorks.config.beeWorksSetting.getFaceBioSetting().getFaceBioAuth()) {
            return AccountLoginActivity.getLoginIntent(ctx);
        }
        return LoginWithAccountActivity.getIntent(ctx, false);
    }

    @Nullable
    public Intent getSchemaRouteIntent(Context context) {
        RouteAction schemaRouteAction = getSchemaRouteAction();
        if(null == schemaRouteAction) {
            return null;
        }


        return schemaRouteAction.getActionIntent(context);
    }


    public RouteAction getSchemaRouteAction() {
        return mRouteAction;
    }

    public void setSchemaRouteAction(RouteAction routeAction) {
        mRouteAction = routeAction;
    }

    public void clearSchemaRouteAction() {
        setSchemaRouteAction(null);
    }
}
