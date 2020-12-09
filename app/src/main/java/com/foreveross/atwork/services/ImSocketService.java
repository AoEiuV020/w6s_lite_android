package com.foreveross.atwork.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.legacy.content.WakefulBroadcastReceiver;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.sdk.android.httpdns.HttpDns;
import com.alibaba.sdk.android.httpdns.HttpDnsService;
import com.foreverht.threadGear.HighPriorityCachedTreadPoolExecutor;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.api.sdk.auth.LoginAsyncNetService;
import com.foreveross.atwork.api.sdk.auth.model.LoginEndpointPostJson;
import com.foreveross.atwork.api.sdk.net.model.UploadFileParamsMaker;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.broadcast.NetworkBroadcastReceiver;
import com.foreveross.atwork.component.floatView.service.WorkplusFloatService;
import com.foreveross.atwork.im.sdk.Client;
import com.foreveross.atwork.im.sdk.socket.ClientBuildParams;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksSetting;
import com.foreveross.atwork.infrastructure.model.user.EndPoint;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.UserTypingMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MultipartChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.UserFileDownloadNotifyMessage;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.shared.EndPointInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AppUtil;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.NetworkStatusUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.rom.RomUtil;
import com.foreveross.atwork.listener.IMNetworkChangedListener;
import com.foreveross.atwork.modules.chat.data.SendMessageDataWrap;
import com.foreveross.atwork.modules.chat.util.ChatDetailExposeBroadcastSender;
import com.foreveross.atwork.modules.chat.util.MultipartMsgHelper;
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper;
import com.foreveross.atwork.modules.main.helper.NetworkErrorViewManager;
import com.foreveross.atwork.modules.voip.activity.CallActivity;
import com.foreveross.atwork.modules.voip.service.CallService;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.services.receivers.AtworkReceiveListener;
import com.foreveross.atwork.services.support.AlarmMangerHelper;
import com.foreveross.atwork.services.support.KeepLiveSupport;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.TimeViewUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by lingen on 15/4/15.
 * Description:
 * 与IM连接的Service
 */
public class ImSocketService extends Service {

    public static final String TAG = "IM_SERVICE";

    public static final String ACTION_NEW_SEND_MESSAGE = "ACTION_NEW_SEND_MESSAGE";

    public static final String ACTION_TYPING = "ACTION_TYPING";

    public static final String DATA_NEW_MESSAGE = "DATA_NEW_MESSAGE";

    public static final String ACTION_IM_RECONNECT = "ACTION_IM_RECONNECT";

    public static final String ACTION_HEART_BEAT = "ACTION_HEART_BEAT";

    public static final String ACTION_CHECK_CONNECT = "ACTION_CHECK_CONNECT";

    public static final String WORKPLUS_WIFI_LOCK = "WORKPLUS_WIFI_LOCK";


    public static boolean sConnectionError;

    public boolean mConnecting = false;

    public static final long IM_CHECK_INTERVAL_HEART_BEAT = AtworkConfig.INTERVAL_HEART_BEAT + 10 * 1000;

    /**
     * IM Socket连接对象
     */
    private static Client sClient;

    private static ScheduledExecutorService sCheckImScheduledThreadPool = Executors.newScheduledThreadPool(1);


    private ScheduledFuture mCheckImScheduledFuture;

    private PowerManager.WakeLock mWakeLock;

    private int mConnectingContinuousEncounterTimes = 0;

    private long mLoggerHitTime = 0;

    private HttpDnsService mHttpDns;

    private Runnable mCheckImTimerRunnable = new Runnable() {
        @Override
        public void run() {

            checkConnectingStatus();


            boolean needReConnect = !mConnecting && (null == sClient || sConnectionError || isPingTimeOut() || isPongTimeOut());
            String checkIMLog = "check im timer~~~~~~~~~~~~~~~~~~~ needReConnect -> " + needReConnect +
                    " isConnecting -> " + mConnecting +
                    "\n isPingTimeOut() -> " + isPingTimeOut() +
                    "\n isPongTimeOut() -> " + isPongTimeOut() +
                    "\n sConnectionError -> " + sConnectionError;

            LogUtil.e(ImSocketService.TAG, checkIMLog);

            mLoggerHitTime++;
            //30 x 4s = 2min, 2分钟记录次check timer 状态
            if(30 == mLoggerHitTime) {
                LogUtil.e(ImSocketService.TAG, "Logger Hit Time");

                Logger.e(ImSocketService.TAG, checkIMLog);
                mLoggerHitTime = 0;
            }


            if (needReConnect) {
                reConnect();

            } else {
                boolean forcePing = TimeUtil.isTimeNeedCheck();
                if(forcePing) {
                    LogUtil.e(ImSocketService.TAG, "时间发生偏移");
                }

                if(forcePing || isNeedPing()) {
                    doStartHeartBeatSync("CheckImTimer");

                } else {
                    resendSendingMessage();
                }
            }

        }
    };

    /**
     * 当连续遇到超过15次 {@link #mConnecting}, 也即1分钟(15 X 4秒)都为该状态, 这时认为该状态不可靠, 置为 false
     * */
    private void checkConnectingStatus() {
        if(mConnecting) {
            mConnectingContinuousEncounterTimes++;
        } else {
            mConnectingContinuousEncounterTimes = 0;
        }

        if(15 < mConnectingContinuousEncounterTimes) {
            mConnecting = false;

            LogUtil.e(TAG, "checkConnectingStatus...  mConnectingContinuousEncounterTimes 超过15次 mConnecting 为 false");
        }
    }


    private NetworkBroadcastReceiver mNetworkBroadcastReceiver;

    private BroadcastReceiver mEmailBroadcastReceiver;

    private BroadcastReceiver mSideBroadcastReceiver;

    private ExecutorService mSendMessageThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private ExecutorService mEndPonitThreadPool = Executors.newSingleThreadExecutor();


    private AtworkReceiveListener mAtworkReceiveListener = new AtworkReceiveListener(this);

    private BroadcastReceiver mHeartBeatBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(ACTION_HEART_BEAT.equals(action)) {
                doStartHeartBeat("ALARM", false);
                return;
            }
            if(ACTION_CHECK_CONNECT.equals(action)) {
                doStartHeartBeat("ACTION_CHECK_CONNECT", true);
            }
        }
    };

    private long mLastReconnectionTime;

    private BroadcastReceiver mHandleImBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(ACTION_IM_RECONNECT.equals(action)) {
                Log.e("endpointSync", "recon");
                notifyConnectionErrorInThreshold();
                return;
            }
            if(ACTION_NEW_SEND_MESSAGE.equals(action)) {
                PostTypeMessage message = (PostTypeMessage) intent.getSerializableExtra(DATA_NEW_MESSAGE);
                sendMessage(message);
            }


            if(ACTION_TYPING.equals(action)) {
                mSendMessageThreadPool.execute(() -> {
                    UserTypingMessage message = (UserTypingMessage) intent.getSerializableExtra(DATA_NEW_MESSAGE);
                    doSendTypingStatusSync(message);
                });


            }
        }
    };




    private void doStartHeartBeat(String tag, boolean forced) {


        if(!forced && !isNeedPing()) {
            return;
        }

        mSendMessageThreadPool.execute(() -> {
            if (forced || isNeedPing()) {
                doStartHeartBeatSync(tag);
            }



        });

    }

    private void doStartHeartBeatSync(String tag) {

        try {
            /**先发送心跳*/
            if (sClient != null) {
                LogUtil.e(TAG, tag + "->  心跳ing..." + TimeViewUtil.getUserCanViewTime(BaseApplicationLike.baseContext , System.currentTimeMillis()));
                sClient.ping(ImSocketService.this);
                AtworkReceiveListener.lastPingTimes = TimeUtil.getCurrentTimeInMillis();
            } else {
                LogUtil.e(TAG, "doStartHeartBeatSync  start notifyConnectionError tag : " + tag);
                notifyConnectionError();

            }

        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG, "im doStartHeartBeatSync null crash");
            notifyConnectionErrorInThreshold();
        }
    }


    private void doSendTypingStatusSync(UserTypingMessage typingMessage) {
        try {
            if (sClient != null) {
                sClient.typing(typingMessage);

            } else {
                notifyConnectionError();

            }

        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("im", "im doSendTypingStatusSync Exception crash");
            notifyConnectionErrorInThreshold();
        }
    }

    private static boolean isPongTimeOut() {
        return Math.abs(TimeUtil.getCurrentTimeInMillis() - AtworkReceiveListener.lastPongTimes) > IM_CHECK_INTERVAL_HEART_BEAT;
    }

    private static boolean isPingTimeOut() {
        long lastPingTimes = AtworkReceiveListener.lastPingTimes;
        if(AtworkReceiveListener.lastPongTimes < lastPingTimes && AtworkConfig.SOCKET_TIME_OUT < TimeUtil.getCurrentTimeInMillis() - lastPingTimes ) {
            return true;
        }

        return false;
    }

    private boolean isNeedPing() {
        return Math.abs(TimeUtil.getCurrentTimeInMillis() - AtworkReceiveListener.lastPingTimes) >= AtworkConfig.INTERVAL_HEART_BEAT;
    }


    public static void closeConnection() {
        LogUtil.d(TAG, "CLOSE SOCKET 1");

        if (sClient != null) {
            sClient.close();
            sClient = null;
        }
    }

    public static void checkConnection(Context context) {
        Logger.e("IMSOCKET", "checking connection");
        context.sendBroadcast(new Intent(ACTION_CHECK_CONNECT));
    }

    @Override
    public void onCreate() {
        LogUtil.e(ImSocketService.TAG, "ImSocketService -> onCreate");

        super.onCreate();

        registerBroadcasts();


    }


    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "ImSocketService on destroy");

        AlarmMangerHelper.initServiceGuardAlarm(BaseApplicationLike.baseContext);

        super.onDestroy();

        unregisterBroadcasts();
        cancelImLiveProtector();
    }


    private void registerBroadcasts() {
        registerNetworkBroadcast();
        registerSideBroadcast();
        registerHeartBeatBroadcast();
        registerHandleImBroadcast();
    }

    private void unregisterBroadcasts() {
        unregisterNetworkBroadcast();
        unregisterSideBroadcast();
        unregisterHeartBeatBroadcast();
        unregisterHandleImBroadcast();
    }

    public void registerHeartBeatBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_HEART_BEAT);
        intentFilter.addAction(ACTION_CHECK_CONNECT);
        //ACTION_HEART_BEAT 由AlarmManager 发出, 不能使用 LocalBroadcastManager
        registerReceiver(mHeartBeatBroadcastReceiver, intentFilter);
    }

    private void unregisterHeartBeatBroadcast() {
        unregisterReceiver(mHeartBeatBroadcastReceiver);
    }

    public void registerHandleImBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_IM_RECONNECT);
        intentFilter.addAction(ACTION_NEW_SEND_MESSAGE);
        intentFilter.addAction(ACTION_TYPING);

        LocalBroadcastManager.getInstance(this).registerReceiver(mHandleImBroadcastReceiver, intentFilter);
    }

    private void unregisterHandleImBroadcast() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandleImBroadcastReceiver);

    }

    private void registerNetworkBroadcast() {
        IntentFilter intentFilter = new IntentFilter(NetworkBroadcastReceiver.ACTION);
        mNetworkBroadcastReceiver = new NetworkBroadcastReceiver(new IMNetworkChangedListener());
        registerReceiver(mNetworkBroadcastReceiver, intentFilter);
    }

    private void unregisterNetworkBroadcast() {
        unregisterReceiver(mNetworkBroadcastReceiver);
    }

    private void registerSideBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        intentFilter.addAction(Intent.ACTION_INPUT_METHOD_CHANGED);
//        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

        mSideBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();


                handleKeepAliveNotification(action);

                if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
                    Logger.e(ImSocketService.TAG, "screen on and last pong time = " + AtworkReceiveListener.lastPongTimes);

                    if (isPongTimeOut()) {
                        sConnectionError = true;
                        ImSocketService.closeConnection();

                        Logger.e(ImSocketService.TAG, "屏幕开启，发现IM在后台断线了超过5分钟");
                    }

                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏

                    BaseApplicationLike.sIsLock = true;

                    LogUtil.e("ActivityLifecycleListener", "screen lock");


                    /**
                     * vivo 机型在锁屏打开时需要做特殊处理, 防止悬浮在锁屏之上
                     * */
                    if(!CallService.isModeNeedScreenControl()) {
                        return;
                    }

                    AtworkApplicationLike.appInvisibleHandleFloatView();


                } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁

                    BaseApplicationLike.sIsLock = false;

                    handleCreateFloatWinOnModeNeedScreenControl();


                } else if (Intent.ACTION_INPUT_METHOD_CHANGED.equals(action)) {
                    CommonShareInfo.setKeyBoardHeight(context, -1);

                } else if(Intent.ACTION_TIME_TICK.equals(action)) {
                    LogUtil.e("timechange", "time tick");

                    doStartHeartBeat("ACTION_TIME_TICK", true);

                } else if(Intent.ACTION_DATE_CHANGED.equals(action) || Intent.ACTION_TIMEZONE_CHANGED.equals(action) || Intent.ACTION_TIME_CHANGED.equals(action)) {
                    LogUtil.e("timechange", "time change");

                    mSendMessageThreadPool.execute(() -> reConnect());

                }
            }
        };
        BaseApplicationLike.baseContext.registerReceiver(mSideBroadcastReceiver, intentFilter);
    }

    private void unregisterSideBroadcast() {
        BaseApplicationLike.baseContext.unregisterReceiver(mSideBroadcastReceiver);

    }


    /**
     * 9.0开始, notification hack 方法让 app 总为"前台优先级", 并且不显示通知栏的方法已经完全没效果了, 此处为
     * 了防止 9.0的机器锁屏后让 service 进入休眠状态, 在锁屏开屏做维护 notification 处理
     * */
    private void handleKeepAliveNotification(String action) {
        if(28 > Build.VERSION.SDK_INT) {
            return;
        }

        if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
            ImSocketService.this.startForeground(1, KeepLiveSupport.getNotification(ImSocketService.this));
        }

        if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
            ImSocketService.this.stopForeground(true);
            return;
        }


        if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
            ImSocketService.this.stopForeground(true);
            return;
        }



    }

    private void handleCreateFloatWinOnModeNeedScreenControl() {
        /**
         * vivo 机型在锁屏打开时需要做特殊处理, 防止悬浮在锁屏之上
         * */
        if(!CallService.isModeNeedScreenControl()) {
            return;
        }

        if(AtworkConfig.OPEN_VOIP && !AtworkConfig.VOIP_NEED_FLOATVIEW_DESKTOP_SHOW
                && VoipHelper.isHandlingVoipCall()
                && !CallActivity.sIsOpening) {
            CallService.sendCreateFloatingWindow();
        }

        WorkplusFloatService.Companion.checkWebUrlHookingFloat();

    }

    public void startHeartBeat() {

//        if (mHeartAlarmManager != null && mHeartPendingIntent != null) {
//            mHeartAlarmManager.cancel(mHeartPendingIntent);
//        }
//        Intent intent = new Intent(ACTION_HEART_BEAT);
//        mHeartPendingIntent = PendingIntent
//                .getBroadcast(this, 0, intent, 0);
//        //开始时间
//        long firstime = SystemClock.elapsedRealtime();
//        mHeartAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        mHeartAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime, AtworkConfig.INTERVAL_HEART_BEAT, mHeartPendingIntent);

        AlarmMangerHelper.setHeartBeatAlarm(this);
        Logger.e(TAG, "start heart beat alarm");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.e(ImSocketService.TAG, "ImSocketService - > onStartCommand");

        if (null != intent) {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }

        cancelImLiveProtector();

        mCheckImScheduledFuture = sCheckImScheduledThreadPool.scheduleAtFixedRate(mCheckImTimerRunnable, 100, 4 * 1000, TimeUnit.MILLISECONDS);

        KeepLiveSupport.startForeground(this, InnerService.class);

        if (Build.VERSION.SDK_INT >= 19) {
            Logger.e(ImSocketService.TAG, "ImSocketService -> reSetAlarm");

            AlarmMangerHelper.setServiceGuardAlarm(BaseApplicationLike.baseContext);
            checkConnection(ImSocketService.this);
        }

        if (AtworkConfig.HTTP_DNS_ENABLE && mHttpDns == null) {
            BeeWorks beeWorks = BeeWorks.getInstance();
            if (beeWorks.config != null && beeWorks.config.beeWorksSetting != null ) {
                BeeWorksSetting.HttpDnsSetting httpDnsSetting = beeWorks.config.beeWorksSetting.getHttpDnsSetting();
                mHttpDns = HttpDns.getService(BaseApplicationLike.sApp, httpDnsSetting.getAccountId(), httpDnsSetting.getSecret());
                mHttpDns.setPreResolveAfterNetworkChanged(true);
                mHttpDns.setHTTPSRequestEnabled(true);
                mHttpDns.setExpiredIPEnabled(true);
            }

        }

        return START_STICKY;
    }

    private void cancelImLiveProtector() {
        if (null != mCheckImScheduledFuture) {
            mCheckImScheduledFuture.cancel(false);
            mCheckImScheduledFuture = null;
        }
    }

    private void notifyConnectionErrorInThreshold() {
        if (System.currentTimeMillis() - mLastReconnectionTime < 10 * 1000) {
            Logger.e(TAG, "last reconnection time less than 10s");
            return;
        }
        mLastReconnectionTime = System.currentTimeMillis();
        Logger.e(TAG, "close connection and prepared reconnect");
        notifyConnectionError();
    }

    public void notifyConnectionError() {
        if(mConnecting) {
            return;
        }


        sConnectionError = true;
//        mConnecting = false;

        LogUtil.e(TAG, " notifyConnectionError 处理");


    }

    public void reConnect() {

        long lastTime = EndPointInfo.getInstance().getLastRetryRemoteTime(BaseApplicationLike.baseContext);
        makeConnection();

//        if (System.currentTimeMillis() - lastTime > 1 * 60 * 1000 || lastTime == -1) {
//            makeConnection();
//
////            EndPointInfo.getInstance().setLastRetryRemoteTime(BaseApplicationLike.baseContext);
//        } else {
//            connectSocket();
//        }

    }


    private long lastRequestEndpointTime = -1L;
    private long endpointRetryDurationControl = 0;

    public void resetEndpointRetryDurationControl() {
        endpointRetryDurationControl = 0;
    }

    /**
     * 0 -> (0,2)秒随机
     * 1 -> 4秒
     * 2 -> 8秒
     * 3 -> 16秒
     * */
    private long getEndpointRetryIllegalDuration() {
        if(0 == endpointRetryDurationControl) {
            return new Random().nextInt(2000);
        }

        return (long) (Math.pow(2, endpointRetryDurationControl) * 2000L);
    }

    /**
     * 重新连接IM
     */
    public void makeConnection() {

//        LogUtil.e("从启动到im开始建立连接, 耗时: " + (System.currentTimeMillis() - AtworkApplicationLike.sStartTime));

        //没有登录则不进行IM SOCKET连接
        if (!LoginUserInfo.getInstance().isLogin(this)) {

            //重连前衔关闭IM连接
            closeConnection();


            Logger.e(TAG, "Not Login..");
            mConnecting = false;
            return;
        }

        //没有网络情况下，不连接IM
        if (!NetworkStatusUtil.isNetworkAvailable(this)) {
            Logger.e(TAG, "Network is unavailable");
            mConnecting = false;
            return;
        }

        //3次endpoint 都无法成功, 认为此时可能无法连接服务器, 给予用户提醒
        if(endpointRetryDurationControl > 3) {
            NetworkErrorViewManager.notifyIMError(this);
        }

        if (!checkEndpointFrequency()) return;

        Logger.e(TAG, "IM重连");


        mConnecting = true;
        sConnectionError = false;


        //重连前衔关闭IM连接
        closeConnection();


        LoginAsyncNetService loginNetService = new LoginAsyncNetService(BaseApplicationLike.baseContext);
        LoginEndpointPostJson postJson = new LoginEndpointPostJson();
        postJson.productVersion = AppUtil.getVersionName(BaseApplicationLike.baseContext);
        postJson.locale = this.getResources().getConfiguration().locale;
        postJson.voipToken = RomUtil.getPushTokenByRom(BaseApplicationLike.baseContext);
        postJson.pushToken = RomUtil.getPushTokenByRom(BaseApplicationLike.baseContext);
        postJson.pushSound = "default";
        postJson.pushEnable = PersonalShareInfo.getInstance().getSettingNotice(BaseApplicationLike.baseContext);
        postJson.voipEnable = PersonalShareInfo.getInstance().getSettingNotice(BaseApplicationLike.baseContext);
        postJson.pushDetail = PersonalShareInfo.getInstance().getSettingShowDetails(BaseApplicationLike.baseContext);
        String romChannel = RomUtil.getRomChannel();
        if (!StringUtils.isEmpty(romChannel)) {
            postJson.channelVendor = romChannel;
            postJson.channelId = AppUtil.getPackageName(BaseApplicationLike.baseContext);
        }
        if (AtworkConfig.OPEN_IM_CONTENT_ENCRYPTION) {
            postJson.encryptType = 1;
        }


        //请求IM信息
        loginNetService.endpointSync(postJson, new LoginAsyncNetService.EndpointListener() {
            @Override
            public void endpointSuccess() {

                Logger.e(TAG, "EndPoint success");
                connectSocket();
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {


                Logger.e(TAG, "EndPoint Fail.." + errorMsg + "  code = " + errorCode);
                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
                mConnecting = false;

            }
        });
    }

    private boolean checkEndpointFrequency() {
        long currentTimeInMillis = TimeUtil.getCurrentTimeInMillis();
        long endpointRetryIllegalDuration = getEndpointRetryIllegalDuration();

        LogUtil.e(TAG, "check makeConnection -> currentTimeInMillis : " + currentTimeInMillis + " lastRequestEndpointTime : " + lastRequestEndpointTime +  "   endpointRetryIllegalDuration :  " + endpointRetryIllegalDuration);

        if(currentTimeInMillis - lastRequestEndpointTime > endpointRetryIllegalDuration) {

            endpointRetryDurationControl++;

            if(endpointRetryDurationControl > 3) {
                resetEndpointRetryDurationControl();
            }

            lastRequestEndpointTime = currentTimeInMillis;

            return true;
        }

        return false;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint("StaticFieldLeak")
    private void connectSocket() {
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                initClient();
                try {
                    if (null != sClient) {
                        Logger.e("IMSOCKET", "prepare to reconnect");

                        sClient.connect(ImSocketService.this, mHwBastetHandler);

                    } else {
                        Logger.e("IMSOCKET", "prepare to reconnect, but null, so failed");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e("IMSOCKET", "prepare to reconnect, but exception, so failed : " + e.getMessage());

                }
                return null;
            }
        }.executeOnExecutor(HighPriorityCachedTreadPoolExecutor.getInstance());

    }

    private void initClient() {
        ClientBuildParams clientBuildParams = getClientBuildParams();
        Logger.e("IMSOCKET", "CLOSING CONNECTION");
        closeConnection();

        sClient = Client.build().clientBuild(clientBuildParams).setReceiveListener(mAtworkReceiveListener);
    }

    private ClientBuildParams getClientBuildParams() {
        String clientId = LoginUserInfo.getInstance().getLoginUserId(ImSocketService.this);
        String deviceId = AtworkConfig.getDeviceId();
        EndPoint endPoint = EndPointInfo.getInstance().getCurrentEndpointInfo(AtworkApplicationLike.sApp);
        String secret = endPoint.mSecret;
        String host = endPoint.getSessionHostCheckConfig();
        if (AtworkConfig.HTTP_DNS_ENABLE) {
            String ip = mHttpDns.getIpByHostAsync(host);
            if (!TextUtils.isEmpty(ip)) {
                host = host.replaceFirst(host, ip);
            }
        }
        int port = Integer.parseInt(endPoint.getSessionPortCheckConfig());
        Logger.e("IMSOCKET", "host is " + host + "port is " + port);
        ClientBuildParams clientBuildParams = ClientBuildParams.newInstance().clientId(clientId).
                deviceId(deviceId).secret(secret).tenantId(AtworkConfig.DOMAIN_ID)
                .host(host).port(port).heartBeat(1000 * 60).sslEnabled(endPoint.mSslEnabled).sslVerify(endPoint.mSslVerify);
        return clientBuildParams;
    }



    /**
     * 发出一个broadcast,通知正在发送的消息已经成功，收到回执
     */
    public void notifySendingMessageSuccess() {
        ChatDetailExposeBroadcastSender.refreshMessageListViewUI();
        SessionRefreshHelper.notifyRefreshSession(this);
    }

    public void sendMessage(final PostTypeMessage message) {
        mSendMessageThreadPool.execute(() -> {
            sendMessageSync(message);

        });

    }

    private void sendMessageSync(PostTypeMessage message) {
        if (sClient != null) {
            try {
                sClient.send(message);
            } catch (IOException e) {
                Logger.e("IMSOCKET", "sendMessage exception " + Log.getStackTraceString(e));
                Logger.e("IMSOCKET", "prepared to do reconnection");
                notifyConnectionErrorInThreshold();
            }
        }
    }




    private boolean resending = false;

    private boolean needResendDirectly = false;

    /**
     * 重发正在发送的消息
     */
    public void resendSendingMessage() {
        synchronized (TAG) {

            if (mConnecting) {
                return;
            }

            if (resending) {
                return;
            }

            resending = true;


            long currentTimeInMillis = TimeUtil.getCurrentTimeInMillis();

            List<ChatPostMessage> msgNotSendList = new ArrayList<>();
            List<ChatPostMessage> msgCheckNeedReconnect = new ArrayList<>();
            SendMessageDataWrap.getInstance().forHandleChatMessageMap(message -> {
                if (message == null) {
                    return;
                }

                if (!(message instanceof ChatPostMessage)) {
                    return;
                }

                if(MediaCenterNetManager.isUploading(message.deliveryId)){
                    return;
                }

                if (message.isRetriesExpiredTimeLegal(currentTimeInMillis)) {
                    return;
                }

                ChatPostMessage chatPostMessage = (ChatPostMessage) message;
                if (5 <= message.mRetriesAuto) {
                    msgNotSendList.add(chatPostMessage);
                } else {

                    if (ChatStatus.Sending.equals(message.chatStatus)) {
                        msgCheckNeedReconnect.add(chatPostMessage);
                    }
                }
            });

            SendMessageDataWrap.getInstance().dealChatPostMessagesNotSend(msgNotSendList);

            //检查是否做重连操作
            boolean needReConnect = checkReConnect(msgCheckNeedReconnect);
            if (needReConnect) return;

            resendDirectly(currentTimeInMillis);

        }

    }

    private boolean checkReConnect(List<ChatPostMessage> msgCheckNeedReconnect) {
        if (!needResendDirectly && !ListUtil.isEmpty(msgCheckNeedReconnect)) {
            resending = false;
            needResendDirectly = true;

            LogUtil.e("检查到超时未发送成功, 尝试重连进行重发");

            reConnect();
            return true;
        }
        return false;
    }

    private void resendDirectly(long currentTimeInMillis) {
        needResendDirectly = false;

        SendMessageDataWrap.getInstance().forHandleChatMessageMap(message -> {
            if (message == null) {
                return;
            }

            if(MediaCenterNetManager.isUploading(message.deliveryId)){
                return;
            }

            if (message.isRetriesExpiredTimeLegal(currentTimeInMillis)) {
                return;
            }

            if (ChatStatus.Sending.equals(message.chatStatus)) {

                Logger.e("IMSOCKET", "resend sending message");

                if (sClient != null) {


                    doResendSendingMessage(message);

                }

            }


        });

        resending = false;
    }

    private void doResendSendingMessage(PostTypeMessage message) {

        message.mRetries++;
        message.mRetriesAuto = message.mRetriesAuto + 1;
        message.setRetriesTime(TimeUtil.getCurrentTimeInMillis());

        if (message instanceof TextChatMessage || message instanceof ShareChatMessage  || message instanceof UserFileDownloadNotifyMessage) {

            sendMessageSync(message);

            return;
        }

        if (message instanceof VoiceChatMessage) {
            VoiceChatMessage voiceChatMessage = (VoiceChatMessage) message;
            if(StringUtils.isEmpty(voiceChatMessage.mediaId)) {
                voiceChatMessage.fileStatus = FileStatus.SENDING;
                resendWithProgressMessage(MediaCenterNetManager.COMMON_FILE, VoiceChatMessage.getAudioPath(this, voiceChatMessage.deliveryId), voiceChatMessage);
            } else {
                voiceChatMessage.fileStatus = FileStatus.SENDED;

                sendMessageSync(voiceChatMessage);

            }

            return;

        }

        if (message instanceof ImageChatMessage) {
            ImageChatMessage imageChatMessage = (ImageChatMessage) message;
            //如果mediaId为空，则重新上传
            if (StringUtils.isEmpty(imageChatMessage.mediaId)) {
                imageChatMessage.fileStatus = FileStatus.SENDING;
                String imageFile;

                if (imageChatMessage.isFullMode()) {
                    imageFile = MediaCenterNetManager.IMAGE_FULL_FILE;
                } else {
                    imageFile = MediaCenterNetManager.IMAGE_FILE;

                }

                resendWithProgressMessage(imageFile, ImageShowHelper.getImageChatMsgPath(this, imageChatMessage), imageChatMessage);
            } else {
                imageChatMessage.fileStatus = FileStatus.SENDED;

                sendMessageSync(imageChatMessage);
            }

            return;
        }

        if (message instanceof FileTransferChatMessage) {
            FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) message;

            if (StringUtils.isEmpty(fileTransferChatMessage.mediaId)) {
                fileTransferChatMessage.fileStatus = FileStatus.SENDING;
                resendWithProgressMessage(MediaCenterNetManager.COMMON_FILE, fileTransferChatMessage.filePath, fileTransferChatMessage);
            } else {
                fileTransferChatMessage.fileStatus = FileStatus.SENDED;

                sendMessageSync(fileTransferChatMessage);                            }

            return;

        }

        if(message instanceof MultipartChatMessage) {
            MultipartChatMessage multipartChatMessage = (MultipartChatMessage) message;

            if (StringUtils.isEmpty(multipartChatMessage.mFileId)) {
                multipartChatMessage.fileStatus = FileStatus.SENDING;
                resendWithProgressMessage(MediaCenterNetManager.COMMON_FILE, MultipartMsgHelper.getMultipartPath(multipartChatMessage), multipartChatMessage);

            } else {
                multipartChatMessage.fileStatus = FileStatus.SENDED;
                sendMessageSync(multipartChatMessage);
            }

            return;
        }


        if(message instanceof StickerChatMessage) {
            StickerChatMessage stickerChatMessage = (StickerChatMessage) message;
            if(stickerChatMessage.isLocal()) {
                sendMessageSync(stickerChatMessage);
            }



            return;
        }
    }

    private void resendWithProgressMessage(final String fileType, final String filePath, final ChatPostMessage message) {

        //延时处理，因为此时监控上传下载的UI可能还没有绘制好
//        mediaCenterNetManager.uploadFile(this, fileType, message.deliveryId, filePath, false, (errorCode, errorMsg)
//                -> ErrorHandleUtil.handleError(ErrorHandleUtil.Module.Media, errorCode, errorMsg));


        UploadFileParamsMaker uploadFileParamsMaker = UploadFileParamsMaker.newRequest()
                .setType(fileType)
                .setMsgId(message.deliveryId)
                .setFilePath(filePath)
                .setNeedCheckSum(false);  //此时重发时, 因为没有上传成功, 所以也不需要检查 checksum


        MediaCenterNetManager.uploadFile(this, uploadFileParamsMaker);

    }

    /**
     * 申请设备电源锁
     */
    private void acquireWakeLock() {
        try {
            if (null == mWakeLock) {
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

                if (null != mWakeLock) {
                    mWakeLock.acquire();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放设备电源锁
     */

    private void releaseWakeLock() {
        try {
            if (null != mWakeLock) {
                mWakeLock.release();
                mWakeLock = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        AlarmMangerHelper.initServiceGuardAlarm(BaseApplicationLike.baseContext);

        super.onTaskRemoved(rootIntent);
    }

    public Handler mHwBastetHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            Logger.e(TAG, "SocketClient handleMessage");
//            switch (msg.what) {
//                case BastetParameters.BASTET_CONNECTION_CLOSED:
//                    Logger.e("HwBastet Handler", "BASTET_CONNECTION_CLOSED");
//                    break;
//                case BastetParameters.BASTET_CONNECTION_ESTABLISHED:
//                    Logger.e("HwBastet Handler", "BASTET_CONNECTION_ESTABLISHED");
//
//                    break;
//                case BastetParameters.BASTET_RECONNECTION_BEST_POINT:
//                    Logger.e("HwBastet Handler", "BASTET_RECONNECTION_BEST_POINT");
//                    onBastetReconnect();
//                    break;
//                case BastetParameters.BASTET_RECONNECTION_BREAK:
//                    Logger.e("HwBastet Handler", "BASTET_RECONNECTION_BREAK");
//
//                    break;
//                case BastetParameters.BASTET_NET_QUALITY_NOTICE:
//                    Logger.e("HwBastet Handler", "BASTET_NET_QUALITY_NOTICE");
//                    break;
//                default:
//                    break;
//            }
//            super.handleMessage(msg);

        }
    };


    public static class InnerService extends Service {

        @Override
        public void onCreate() {
            LogUtil.e(ImSocketService.TAG, "ImSocketService -> InnerService onCreate");
            super.onCreate();

        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            LogUtil.e(ImSocketService.TAG, "ImSocketService -> InnerService onStartCommand");

            KeepLiveSupport.stopForeground(this);


            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public void onDestroy() {
            Log.i(ImSocketService.TAG, "ImSocketService -> InnerService onDestroy");
            super.onDestroy();
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }


}
