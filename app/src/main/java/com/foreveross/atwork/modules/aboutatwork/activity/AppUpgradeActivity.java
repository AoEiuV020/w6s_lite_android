package com.foreveross.atwork.modules.aboutatwork.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.net.MediaCenterHttpURLConnectionUtil;
import com.foreveross.atwork.api.sdk.setting.AppUpgradeNetService;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.broadcast.NetworkBroadcastReceiver;
import com.foreveross.atwork.infrastructure.utils.AppUtil;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.CommandUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.manager.WorkplusUpdateManager;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.IntentUtil;
import com.foreveross.atwork.utils.WorkplusTextSizeChangeHelper;

import java.io.File;
import java.util.UUID;

/**
 * 应用版本更新
 * Created by ReyZhang on 2015/5/26.
 */
public class AppUpgradeActivity extends Activity implements DialogInterface.OnKeyListener, NetworkBroadcastReceiver.NetworkChangedListener {
    public static final String INTENT_VERSION = "version";
    public static final String INTENT_FORCE_UPDATED = "intent_force_updated";
    private static final String TAG = AppUpgradeNetService.class.getSimpleName();
    private AtworkAlertDialog mUpdateDialog;
    private PowerManager.WakeLock mWakeLock;
    private NetworkBroadcastReceiver networkBroadcastReceiver;
    private NetworkBroadcastReceiver.NetWorkType lastNetWorkType;
    private boolean mForcedUpdate;
    private String mUuid;
    boolean hasExit =false;

    public static Intent getIntent(Context context, int version) {
        Intent intent = new Intent(context, AppUpgradeActivity.class);
        intent.putExtra(INTENT_VERSION, version);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WorkplusTextSizeChangeHelper.setTextSizeTheme(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new View(this));
        showDownloadDialog();
        installOrDownloadApp();
        registerNetworkReceiver(this);
    }

    public void installOrDownloadApp(){
        int version = getIntent().getIntExtra(INTENT_VERSION, 0);
        mForcedUpdate = getIntent().getBooleanExtra(INTENT_FORCE_UPDATED, false);
        final String apkFilePath = AtWorkDirUtils.getInstance().getAppUpgrade() + File.separator + "atwork-" + version + ".apk";
        mUuid = UUID.randomUUID().toString();
        //已经下载的，直接安装，不再下载
        File oldApk = new File(apkFilePath);
        long downloadPos = 0;
        if (oldApk.exists()) {
            downloadPos = oldApk.length();
        }
        AppUpgradeNetService service = AppUpgradeNetService.getInstance();
        service.onAppUpgrade(this, mUuid, AppUtil.getPackageName(this), new MediaCenterNetManager.MediaDownloadListener() {
            @Override
            public String getMsgId() {
                return null;
            }

            @Override
            public void downloadSuccess() {
                try {
                    if (mUpdateDialog != null) {
                        mUpdateDialog.dismiss();
                        mUpdateDialog = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                installApp(apkFilePath);
                //关闭自身，否则取消安装后，跳回此activity，会重新下载
                AppUpgradeActivity.this.finish();
            }

            @Override
            public void downloadFailed(int errorCode, String errorMsg, boolean doRefreshView) {
                LogUtil.e(TAG, errorMsg);
            }

            @Override
            public void downloadProgress(double progress, double value) {
                if (null != mUpdateDialog) {
                    mUpdateDialog.setProgress((int) progress);
                }
            }
        }, apkFilePath, downloadPos);
    }

    private void installApp(String apkFilePath) {
        File apkFile = new File(apkFilePath);
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if(!sdCardExist){
            CommandUtil.rwxFile(apkFile);
        }

        IntentUtil.installApk(this, apkFilePath);
    }


    public void showDownloadDialog(){
        mUpdateDialog = new AtworkAlertDialog(this)
                .setType(AtworkAlertDialog.Type.PROGRESS)
                .setTitleText(getString(R.string.app_upgrade))
                .setProgressDesc(getString(R.string.downloading))
                .setClickDeadColorListener(dialog -> {
                    if (mUpdateDialog != null) {
                        mUpdateDialog.dismiss();
                        mUpdateDialog = null;

                    }

                    WorkplusUpdateManager.INSTANCE.setTipFloatStatusAndRefresh(true);

                    if (mForcedUpdate) {
                        MediaCenterHttpURLConnectionUtil.getInstance().breakAll();
                        AtworkApplicationLike.exitAll(AppUpgradeActivity.this, false);
                        return;
                    } else {
                        MediaCenterHttpURLConnectionUtil.getInstance().brokenMedia(mUuid);
                        AtworkToast.showToast(getString(R.string.cancel_upgrade));
                        AppUpgradeActivity.this.finish();
                    }
                })
                .hideBrightBtn()
                .setMax(100);
        mUpdateDialog.show();
        mUpdateDialog.setOnKeyListener(this);
        mUpdateDialog.setProgress(0);
    }


    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_SEARCH && event.getRepeatCount() == 0) {
            // Pretend we processed it
            return true;
        }

        return KeyEvent.KEYCODE_BACK == keyCode;
    }


    @Override
    protected void onStart() {
        super.onStart();
        //下载期间，暗屏，不睡眠
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, this.getClass().getName());
        mWakeLock.acquire();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterNetworkReceiver();
    }

    protected void unRegisterNetworkReceiver() {
        if (networkBroadcastReceiver != null) {
            unregisterReceiver(networkBroadcastReceiver);
        }
    }

    protected void registerNetworkReceiver(NetworkBroadcastReceiver.NetworkChangedListener networkChangedListener) {
        IntentFilter intentFilter = new IntentFilter(NetworkBroadcastReceiver.ACTION);
        networkBroadcastReceiver = new NetworkBroadcastReceiver(networkChangedListener);
        registerReceiver(networkBroadcastReceiver, intentFilter);
    }


    @Override
    public void networkChanged(NetworkBroadcastReceiver.NetWorkType networkType) {
        if (lastNetWorkType == null) {
            lastNetWorkType = networkType;
        } else {
            if (!lastNetWorkType.equals(networkType)) {
                if (networkType == NetworkBroadcastReceiver.NetWorkType.NO_NETWORK) {
                    MediaCenterHttpURLConnectionUtil.getInstance().brokenMedia(mUuid);
                    mUpdateDialog.setType(AtworkAlertDialog.Type.CLASSIC)
                            .setTitleText(getString(R.string.app_upgrade))
                            .setContent(getString(R.string.try_update_next_time))
                            .setClickBrightColorListener(dialog -> {
                                AtworkToast.showToast(getString(R.string.cancel_upgrade));
                                AppUpgradeActivity.this.finish();
                            })
                            .hideDeadBtn()
                            .showBrightBtn();
                }
                lastNetWorkType = networkType;
            }
        }
    }

}
