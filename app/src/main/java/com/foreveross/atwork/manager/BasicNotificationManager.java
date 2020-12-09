package com.foreveross.atwork.manager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AppUtil;
import com.foreveross.atwork.modules.voip.support.qsy.utils.VibratorUtil;

/**
 * Created by dasunsy on 2017/2/20.
 */

public class BasicNotificationManager {

    protected static long mLastNotifyHavingEffectTime = -1;

    public static final int ID_VOIP_CALL_INIT = 0x776;
    public static final int ID_VOIP_CALLING = 0x777;

    private int mVoiceMode = MessageNoticeManager.VoiceMode.CUSTOM;

    public static String DEFAULT_CHANNEL_ID = "workplus_ht";

    public static final String WEAK_CHANNEL_ID = "workplus_other";

    public static String IM_SERVICE_CHANNEL_ID = "im_service";

    public static String IM_PUSH_CHANNEL_ID = "im_push";


    public static boolean shouldSetSoundOnNotificationModel() {

        if(isNotNewCreatedDefaultNotificationChannel()) {
            return true;
        }

        if(!AtworkConfig.NOTIFICATION_CONFIG.getUseChannelSoundOnAndroidO()) {
            return true;
        }

        if(Build.VERSION_CODES.O > Build.VERSION.SDK_INT) {
            return true;
        }

        return false;
    }

    private static boolean isNotNewCreatedDefaultNotificationChannel() {
        return 1 != CommonShareInfo.getNewDefaultNotificationChannelStatus(AtworkApplicationLike.baseContext);
    }

    protected void notify(NotifyParams notifyParams) {
        if (notifyParams.mIsVibrate) {
            VibratorUtil.Vibrate(notifyParams.mContext, 500);

        }

        if (shouldSetSoundOnNotificationModel()) {
            assembleNotificationSound(notifyParams);
        }

        NotificationManager notificationManager = (NotificationManager) notifyParams.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (null != notificationManager) {
            notificationManager.notify(notifyParams.mKey, notifyParams.mNotification);
        }

    }

    private void assembleNotificationSound(NotifyParams notifyParams) {
        if (notifyParams.mIsVoice) {
            if (VoiceMode.DEFAULT == mVoiceMode) {
                notifyParams.mNotification.defaults |= Notification.DEFAULT_SOUND;

            } else {
                notifyParams.mNotification.sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + AppUtil.getPackageName(notifyParams.mContext) + "/" + R.raw.notification_sound);
            }

        }
    }

    public void clear() {
        NotificationManager notificationManager = (NotificationManager) BaseApplicationLike.baseContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (null != notificationManager) {
            notificationManager.cancelAll();
        }
    }

    public void clear(int key) {
        NotificationManager notificationManager = (NotificationManager) BaseApplicationLike.baseContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (null != notificationManager) {
            notificationManager.cancel(key);
        }

    }


    public static void createNotificationChannels(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createDefaultNotificationChannels(context, notificationManager);

    }

    private static void createDefaultNotificationChannels(Context context, NotificationManager notificationManager) {
        if(Build.VERSION_CODES.O > Build.VERSION.SDK_INT) {
            return;
        }


        checkW6sCreatedNotificationChannelBefore(context, notificationManager);

        NotificationChannel defaultChannel = new NotificationChannel(DEFAULT_CHANNEL_ID,
                context.getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);


        if(shouldSetSoundOnNotificationModel()) {
            defaultChannel.setSound(null, null);

        } else {

            //8.0后 notification 上设置 sound 已经废弃了不能使用, 需要在 NotificationChannel 上设置 sound 值
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            defaultChannel.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + AppUtil.getPackageName(context) + "/" + R.raw.notification_sound), audioAttributes);
            defaultChannel.enableVibration(false);
        }


//        notificationManager.deleteNotificationChannel(DEFAULT_CHANNEL_ID);
        notificationManager.createNotificationChannel(defaultChannel);


        NotificationChannel weakChannel = new NotificationChannel(WEAK_CHANNEL_ID,
                context.getString(R.string.other_notification_channel_name), NotificationManager.IMPORTANCE_LOW);
        weakChannel.enableVibration(false);
        notificationManager.createNotificationChannel(weakChannel);

        //im service
        NotificationChannel imServiceChannel = new NotificationChannel(IM_SERVICE_CHANNEL_ID,
                context.getString(R.string.im_service), NotificationManager.IMPORTANCE_LOW);

        notificationManager.createNotificationChannel(imServiceChannel);

        NotificationChannel imPushServiceChannel = new NotificationChannel(IM_PUSH_CHANNEL_ID,
                "IM推送提醒", NotificationManager.IMPORTANCE_HIGH);
//        imPushServiceChannel.setAllowBubbles(true);
        imPushServiceChannel.enableVibration(true);
        imPushServiceChannel.setShowBadge(true);
        imPushServiceChannel.setDescription("im离线推送的channel");
        imPushServiceChannel.setBypassDnd(true);
        imPushServiceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        imPushServiceChannel.getName();
        notificationManager.createNotificationChannel(imPushServiceChannel);

    }

    private static void checkW6sCreatedNotificationChannelBefore(Context context, NotificationManager notificationManager) {
        if(-1 == CommonShareInfo.getNewDefaultNotificationChannelStatus(context)) {
            NotificationChannel channelBefore = notificationManager.getNotificationChannel(DEFAULT_CHANNEL_ID);
            if(null == channelBefore) {
                CommonShareInfo.setNewNotificationChannelStatus(context, 1);
            } else {
                CommonShareInfo.setNewNotificationChannelStatus(context, 0);

            }
        }
    }


    public static class NotifyParams {
        public Context mContext;
        public int mKey;
        public boolean mIsVoice;
        public boolean mIsVibrate;
        public Notification mNotification;

        public static NotifyParams newNotifyParams() {
            return new NotifyParams();
        }

        public NotifyParams setContext(Context context) {
            mContext = context;
            return this;
        }

        public NotifyParams setKey(int key) {
            mKey = key;
            return this;
        }

        public NotifyParams setVoice(boolean voice) {
            mIsVoice = voice;
            return this;
        }

        public NotifyParams setVibrate(boolean vibrate) {
            mIsVibrate = vibrate;
            return this;
        }

        public NotifyParams setNotification(Notification notification) {
            mNotification = notification;
            return this;
        }
    }

    public final class VoiceMode {
        /**
         * 使用系统提示语
         */
        public static final int DEFAULT = 0;

        /**
         * 使用自定义的提示音
         */
        public static final int CUSTOM = 1;
    }


}
