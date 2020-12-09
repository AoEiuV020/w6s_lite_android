
package com.foreveross.atwork.modules.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import androidx.fragment.app.Fragment;

import com.foreverht.cache.UCCalendarCache;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.infrastructure.manager.FileAlbumService;
import com.foreveross.atwork.infrastructure.permissions.PermissionsManager;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.ServiceCompat;
import com.foreveross.atwork.infrastructure.utils.rom.RomUtil;
import com.foreveross.atwork.manager.UCCalendarManager;
import com.foreveross.atwork.modules.main.fragment.SplashFragment;
import com.foreveross.atwork.modules.step.StepCounterManager;
import com.foreveross.atwork.modules.voip.manager.ZoomVoipManager;
import com.foreveross.atwork.services.ImSocketService;
import com.foreveross.atwork.support.NoFilterSingleFragmentActivity;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.push.HmsMessaging;

import java.util.concurrent.Executors;

/**
 * Created by lingen on 15/3/24.
 * Description:
 */
public class SplashActivity extends NoFilterSingleFragmentActivity {

    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (LoginUserInfo.getInstance().isLogin(AtworkApplicationLike.baseContext)) {
            ServiceCompat.startServiceCompat(this, ImSocketService.class);
        }

        handleHuaweiPush();

        FileAlbumService.getInstance().init();
        StepCounterManager.INSTANCE.init(AtworkApplicationLike.sApp);

        if (AtworkConfig.ZOOM_CONFIG.getEnabled()) {
            ZoomVoipManager.INSTANCE.init();
        }

        //初始化全时云会议客户端
        if (AtworkConfig.INTEGRATE_UCCALENDAR) {
            UCCalendarManager.getInstance().initUCCalendarClient();

            UCCalendarManager.getInstance().onAddUserStatusListener(() -> {

                Logger.e(UCCalendarManager.TAG, "QSY CALENDAR SESSION INVALID");
                UCCalendarCache.getInstance().clearUCCalendarCache();

            });
        }



    }

    private void handleHuaweiPush() {

        if(!RomUtil.isHuawei()) {
            return;
        }

        HmsMessaging.getInstance(AtworkApplicationLike.baseContext).turnOnPush().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i("HMS", "turnOnPush Complete");
            } else {
                Log.e("HMS", "turnOnPush failed: ret=" + task.getException().getMessage());
            }
        });

        Executors.newSingleThreadExecutor().submit(() -> {

            try {
                String appId = AGConnectServicesConfig.fromContext(SplashActivity.this).getString("client/app_id");
                String token = HmsInstanceId.getInstance(SplashActivity.this).getToken(appId, "HCM");
                if(!TextUtils.isEmpty(token)) {
                    Log.i("HMS", "push token = " + token);
                    CommonShareInfo.setHuaweiPushToken(SplashActivity.this, token);
                }
            } catch (ApiException e) {
                Log.i("HMS", "push token error " + Log.getStackTraceString(e));
                e.printStackTrace();
            }
        });
    }

    public static Intent getIntent(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        mFragment = new SplashFragment();
        return mFragment;
    }


    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        mFragment = fragment;
        LogUtil.e("splash", "onAttachFragment" + fragment);
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

   /*     if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            if (mFragment instanceof SplashFragment) {
                LogUtil.e("splash", "onBackPressed keydown");
                SplashFragment fragment = (SplashFragment) mFragment;
                fragment.jumpNext();
            }
            // do something on back.
            return true;
        }*/


        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
        LogUtil.e("get~ per result");
    }

    @Override
    public void changeStatusBar() {
        //do nothing
    }

    @Override
    public boolean checkDomainSettingUpdate() {
        //do nothing
        return false;
    }

    @Override
    public void registerUpdateReceiver() {
        //do nothing
    }


    @Override
    public boolean needCheckAppLegal() {
        return false;
    }

}
