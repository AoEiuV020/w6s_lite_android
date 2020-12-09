package com.foreveross.atwork.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.VoipSdkType;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.main.activity.MainActivity;
import com.foreveross.atwork.modules.voip.activity.CallActivity;
import com.foreveross.atwork.modules.voip.activity.agora.AgoraCallActivity;
import com.foreveross.atwork.modules.voip.activity.qsy.QsyCallActivity;
import com.foreveross.atwork.modules.voip.support.qsy.utils.VibratorUtil;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dasunsy on 2017/2/20.
 */

public class VoipNoticeManager extends BasicNotificationManager {

    private static final Object sLock = new Object();

    public static VoipNoticeManager sInstance = null;

    public Set<Integer> mRejectNotifyIds = new HashSet<>();

    public static VoipNoticeManager getInstance() {
        /**
         * double check
         * */

        if (null == sInstance) {
            synchronized (sLock) {
                if (null == sInstance) {
                    sInstance = new VoipNoticeManager();
                }

            }
        }

        return sInstance;
    }

    public void callingNotificationShow(Context context, long time) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent();
        Class<?> cls = null;
        if(VoipSdkType.AGORA == AtworkConfig.VOIP_SDK_TYPE) {
            cls = AgoraCallActivity.class;

        } else if(VoipSdkType.QSY == AtworkConfig.VOIP_SDK_TYPE) {
            cls = QsyCallActivity.class;

        }
        if (null != cls) {
            PendingIntent intent = getPendingIntent(context, notificationIntent, cls);

            String contentText = context.getText(R.string.notification_voip_tip).toString();
            String titleText = context.getText(R.string.voip_voice_meeting).toString();

            if(-1 != time) {
                contentText += " " + VoipHelper.toCallDurationShow(time);
            }

            Notification notification = getBuilder(context, intent, contentText, titleText, true);

            notificationManager.notify(ID_VOIP_CALLING, notification);
        }



    }

    public void initCallNotificationShow(Context context, VoipType voipType, UserHandleInfo invitor) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent();
        Class<?> cls = null;
        if(VoipSdkType.AGORA == AtworkConfig.VOIP_SDK_TYPE) {
            cls = AgoraCallActivity.class;

        } else if(VoipSdkType.QSY == AtworkConfig.VOIP_SDK_TYPE) {
            cls = QsyCallActivity.class;

        }
        if (null != cls) {
            PendingIntent intent = getPendingIntent(context, notificationIntent, cls);

            String contentText;
            if (VoipType.VOICE == voipType) {
                contentText = context.getText(R.string.voip_tip_invite_join_audio_meeting).toString();
            } else {
                contentText = context.getText(R.string.voip_tip_invite_join_video_meeting).toString();

            }


            String titleText;
            if (StringUtils.isEmpty(invitor.mShowName)) {
                titleText = context.getText(R.string.voip_voice_meeting).toString();
            } else {
                titleText = invitor.mShowName;

            }

            Notification notification = getBuilder(context, intent, contentText, titleText, false);

            notificationManager.notify(ID_VOIP_CALLING, notification);
        }

    }


    public void rejectNotificationShow(Context context, int uid) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent();
        Class<?> cls = MainActivity.class;
        PendingIntent intent = getPendingIntent(context, notificationIntent, cls);

        VoipMeetingMember voipMeetingMember = VoipMeetingController.getInstance().findMember(uid);
        if(null != voipMeetingMember) {
            String contentText = context.getString(R.string.voip_not_received_tip, voipMeetingMember.getTitle());
            String titleText = context.getString(R.string.voip_voice_meeting);

            Notification notification = getBuilder(context, intent, contentText, titleText, false);

            long current = System.currentTimeMillis();

            if ((current - mLastNotifyHavingEffectTime >= 2000)) {
                mLastNotifyHavingEffectTime = current;

                VibratorUtil.Vibrate(context, 500);
            }


            notificationManager.notify(uid, notification);

            mRejectNotifyIds.add(uid);
        }

    }

    @NonNull
    public Notification getBuilder(Context context, PendingIntent intent, String contentText, String titleText, boolean isNoClear) {
        NotificationCompat.Builder builder = new NotificationBuilder(context);


        builder.setAutoCancel(false);
        builder.setContentTitle(titleText);
        builder.setContentText(contentText);
        builder.setContentIntent(intent);

        if (21 <= Build.VERSION.SDK_INT) {
            builder.setSmallIcon(R.mipmap.icon_notice_small);

        } else {
            builder.setSmallIcon(R.mipmap.icon_logo);

        }

        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_logo));


        builder.setWhen(System.currentTimeMillis());

        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {

            if(isNoClear) {
                builder.setChannelId(BasicNotificationManager.WEAK_CHANNEL_ID);

            } else {
                builder.setChannelId(BasicNotificationManager.DEFAULT_CHANNEL_ID);
            }

        }

        Notification notification = builder.build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;


        if (isNoClear) {
            notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
            notification.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用

        }
        return notification;
    }


    public PendingIntent getPendingIntent(Context context, Intent notificationIntent, Class<?> cls) {
        notificationIntent.setClass(context, cls);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra(CallActivity.EXTRA_START_FROM_OUTSIDE, true);

        return PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }



    public void callingNotificationCancel(Context context) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(ID_VOIP_CALLING);
    }


    public void rejectNotificationsCancel(Context context) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        for(Integer id : mRejectNotifyIds) {
            notificationManager.cancel(id);

        }

        mRejectNotifyIds.clear();

    }
}
