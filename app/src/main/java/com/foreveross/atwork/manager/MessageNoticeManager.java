package com.foreveross.atwork.manager;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.foreverht.cache.DiscussionCache;
import com.foreverht.cache.UserCache;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.model.app.LightApp;
import com.foreveross.atwork.infrastructure.model.chat.VoipChatMessage;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.shared.EmailSettingInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.StringConstants;
import com.foreveross.atwork.infrastructure.utils.AppUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.UrlHandleHelper;
import com.foreveross.atwork.infrastructure.utils.rom.RomUtil;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.app.manager.AppManager;
import com.foreveross.atwork.modules.chat.activity.ChatDetailActivity;
import com.foreveross.atwork.modules.main.activity.MainActivity;
import com.foreveross.atwork.modules.organization.activity.OrgApplyingActivity;
import com.foreveross.atwork.modules.route.activity.SchemaRouteActivity;
import com.foreveross.atwork.modules.route.manager.RouteActionFactory;
import com.foreveross.atwork.modules.route.model.ActivityFromSource;
import com.foreveross.atwork.modules.route.model.ActivityInfo;
import com.foreveross.atwork.modules.route.model.RouteParams;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.IntentUtil;

import java.util.List;
import java.util.Random;

import static com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage.FROM;
import static com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage.TARGET_URL;
import static com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage.TO;
import static com.foreveross.atwork.modules.chat.activity.ChatDetailActivity.TYPE;

/**
 * 消息通知管理者，管理消息栏通知
 * Created by ReyZhang on 2015/5/8.
 */
public class MessageNoticeManager extends BasicNotificationManager {

    private static final String TAG = MessageNoticeManager.class.getSimpleName();
    private static MessageNoticeManager sInstance = new MessageNoticeManager();

    //统计session列表
    private int mCount = 0;

//    private final SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
//    private final int soundID = soundPool.load(BaseApplicationLike.baseContext, R.raw.notification_sound, 1);

    public static MessageNoticeManager getInstance() {
        synchronized (MessageNoticeManager.class) {
            if (sInstance == null) {
                sInstance = new MessageNoticeManager();

            }
            return sInstance;
        }
    }


    /**
     * 通知栏通知单条消息。
     *
     * @param context
     * @param message
     * @param session
     * @param forceAtNotification
     */
    public void showChatMsgNotification(final Context context, ChatPostMessage message, Session session, boolean forceAtNotification) {

        if (message == null || session == null) {
            throw new IllegalArgumentException("invalid arguments on " + TAG);
        }

        //notice switch
        boolean isNotice = isChatMsgShouldNotice(message);
        boolean isPCOnline = PersonalShareInfo.getInstance().isPCOnline(context);
        boolean isOnlineMute = PersonalShareInfo.getInstance().isDeviceOnlineMuteMode(context);
        boolean isVoipMessage = message instanceof VoipChatMessage;

        if (!isNotice || (isPCOnline && isOnlineMute && !isVoipMessage)) {
            return;
        }

        if (!LoginUserInfo.getInstance().isLogin(context)) {
            return;
        }
        //正在聊天的SESSION不通知(紧急呼叫除外)
        // ChatListFragment.isFragmentShow()
        if (!message.isEmergency() && session.visible) {
            return;
        }

        String messageContent = getMessageContent(context, message, session, forceAtNotification);
        String mediaId = StringUtils.EMPTY;

        if (SessionType.User.equals(session.type)) {
            User user = UserCache.getInstance().getUserCache(message.from);
            if (user != null) {
                mediaId = user.mAvatar;
            }

        } else if (SessionType.Discussion.equals(session.type)) {
            Discussion discussion = DiscussionCache.getInstance().getDiscussionCache(message.to);
            if (discussion != null) {
                mediaId = discussion.mAvatar;
            }
        }

        boolean needForceFeedback;
        if (message.isEmergency()) {
            needForceFeedback = true;

        } else {
            needForceFeedback = forceAtNotification;

        }
        notifyMsg(context, mediaId, messageContent, session, needForceFeedback);
    }

    private boolean isChatMsgShouldNotice(ChatPostMessage message) {
        return message.isEmergency() || PersonalShareInfo.getInstance().getSettingNotice(BaseApplicationLike.baseContext);
    }

    public void showEmailNotification(final Context context, Session session, String accountUUID) {
        //TODO LITE
    }

    /**
     * 处理通知类型
     */
    public void showNotifyNotification(final Context context, Session session) {
        //notice switch
        boolean isNotice = PersonalShareInfo.getInstance().getSettingNotice(BaseApplicationLike.baseContext);

        if (!isNotice) {
            return;
        }

        String messageContent = "";
        if (PersonalShareInfo.getInstance().getSettingShowDetails(context)) {
            messageContent = session.lastMessageText;
        } else {
            messageContent = context.getString(R.string.you_received_a_message);
        }
        notifyMsg(context, null, messageContent, session, false);
    }


    /**
     * 组装单个消息通知显示体
     *
     * @param message
     * @param session
     * @return
     */
    private String getMessageContent(Context context, ChatPostMessage message, Session session, boolean forceAtNotification) {

        String content;
        if (!PersonalShareInfo.getInstance().getSettingShowDetails(context)) {
            return context.getString(R.string.you_received_a_message);
        }
        if (message.isBurn()) {
            content = context.getString(R.string.session_txt_receive_new_msg);
        } else {
            content = message.getSessionShowTitle();

        }
        return makeContent(context, content, message, session, forceAtNotification);
    }

    /**
     * 组装消息列表通知显示体，处理短时间下，同一个session会话多条消息只提醒一次，显示最后一条消息内容
     *
     * @param messages
     * @param session
     * @return
     */
    private String getMessageContent(Context context, List<ChatPostMessage> messages, Session session) {
        String content = "";
        int count = messages.size();
        if (count == 0) {
            return content;
        }
        //拿最后一条
        ChatPostMessage message = null;
        for (int i = (messages.size() - 1); i > 0; i--) {
            message = messages.get(i);
            if (message == null) {
                continue;
            }
            content = message.getChatBody().get(ChatPostMessage.CONTENT).toString();
        }
        return makeContent(context, content, message, session, false);
    }

    /**
     * 组装内容显示体
     *
     * @param content
     * @param message
     * @param session
     * @return
     */
    private String makeContent(Context context, String content, ChatPostMessage message, Session session, boolean forceAtNotification) {
        StringBuilder sb = new StringBuilder();
        if (!forceAtNotification && session.getUnread() > 1) {
            sb.append("[").append(session.getUnread()).append("条]");
        }
        if (session.type == SessionType.Discussion) {
            String contact = ChatMessageHelper.getReadableNameShow(context, message);
            sb.append(contact).append(StringConstants.SEMICOLON);
        }
        sb.append(content);

        return AtworkUtil.getMessageTypeNameI18N(context, session, sb.toString());
    }

    /**
     * 通知与消息的"通知"处理
     */
    private void notifyMsg(Context context, String mediaId, String content, Session session, boolean needForceFeedback) {
        long current = System.currentTimeMillis();

        boolean isVibrate = needForceFeedback || PersonalShareInfo.getInstance().getSettingVibrate(context);
        boolean isVoice = needForceFeedback || PersonalShareInfo.getInstance().getSettingVoice(context);
        if ((current - mLastNotifyHavingEffectTime < 2000)) {
            isVibrate = false;
            isVoice = false;

        } else {
            mLastNotifyHavingEffectTime = current;
        }

        //决定跳转的界面
        if (Session.EntryType.To_URL.equals(session.entryType)) {

            if(RouteActionFactory.INSTANCE.canRoute(RouteParams.newRouteParams().uri(session.entryValue).build())) {
                handleNotificationIntent(context, SchemaRouteActivity.getRouteIntent(context, session.entryValue), content, session, isVibrate, isVoice);
                return;
            }


            if (SessionType.LightApp.equals(session.type)) {
                notifyAppSession(context, session, content, isVibrate, isVoice);
                return;
            }

            notifySession(context, content, session, isVibrate, isVoice);

            return;
        }

        if (Session.EntryType.To_ORG_APPLYING.equals(session.entryType)) {
            Intent notificationIntent = new Intent();
            notificationIntent.setClass(context, OrgApplyingActivity.class);
            handleNotificationIntent(context, notificationIntent, content, session, isVibrate, isVoice);
            return;

        }
        notifyChatDetail(context, session, content, isVibrate, isVoice);
    }

    private void notifySession(Context context, String content, Session session, boolean isVibrate, boolean isVoice) {
        if(!StringUtils.isEmpty(session.entryValue)) {
            notifyWebView(context, session, content, isVibrate, isVoice, null);
            return;
        }

        notifyChatDetail(context, session, content, isVibrate, isVoice);
    }

    private void notifyAppSession(final Context context, final Session session, final String content, boolean isVibrate, boolean isVoice) {
        AppManager.getInstance().queryApp(context, session.identifier, session.orgId, new AppManager.GetAppFromMultiListener() {
            @Override
            public void onSuccess(@NonNull App app) {
                if (app instanceof LightApp) {

                    if(ListUtil.isEmpty(app.mBundles)) {
                        notifySession(context, content, session, isVibrate, isVoice);
                        return;
                    }

                    AppBundles matchBundle = null;
                    AppBundles mainBundle = null;
                    for (AppBundles bundle : app.mBundles) {
                        if (bundle.mBundleId.equalsIgnoreCase(session.entryId)) {
                            matchBundle = bundle;
                        }
                        if (bundle.isMainBundle()) {
                            mainBundle = bundle;
                        }
                    }
                    if (matchBundle != null) {
                        notifyWebView(context, session, content, isVibrate, isVoice, matchBundle);
                        return;
                    }
                    if (mainBundle != null) {
                        notifyWebView(context, session, content, isVibrate, isVoice, mainBundle);
                        return;
                    }
                    notifyWebView(context, session, content, isVibrate, isVoice, app.mBundles.get(0));

                    return;

                }

                notifySession(context, content, session, isVibrate, isVoice);

            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {
                if (!ErrorHandleUtil.handleTokenError(errorCode, errorMsg)) {
                    notifySession(context, content, session, isVibrate, isVoice);
                }
            }
        });
    }

    private void notifyChatDetail(Context context, Session session, String content, boolean isVibrate, boolean isVoice) {
        Intent notificationIntent = new Intent();
        notificationIntent.putExtra(ChatDetailActivity.IDENTIFIER, session.identifier);
        notificationIntent.setClass(context, ChatDetailActivity.class);
        handleNotificationIntent(context, notificationIntent, content, session, isVibrate, isVoice);
    }

    public void notifyWebView(Context context, Session session, String content, boolean isVibrate, boolean isVoice, AppBundles appBundle) {
        if (appBundle != null && appBundle.useOfflinePackage() && !FileUtil.isExist(UrlHandleHelper.getReleasePath(context, appBundle))) {
            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.putExtra(MainActivity.DATA_LOAD_OFFLINE_DATA_APP, appBundle);
            handleNotificationIntent(context, notificationIntent, content, session, isVibrate, isVoice);
            return;
        }

        WebViewControlAction webViewControlAction = WebViewControlAction
                .newAction()
                .setUrl(session.entryValue)
                .setTitle(session.name)
                .setSessionId(session.identifier)
                .setFromNotice(true);
        if (appBundle != null) {
            webViewControlAction.setLightApp(appBundle);
        }
        Intent notificationIntent = WebViewActivity.getIntent(context, webViewControlAction);
        handleNotificationIntent(context, notificationIntent, content, session, isVibrate, isVoice);
    }

    @SuppressLint("WrongConstant")
    private void handleNotificationIntent(Context context, Intent notificationIntent, String content, Session session, boolean isVibrate, boolean isVoice) {
        notificationIntent.putExtra(ActivityInfo.INTENT_DATA_KEY_FROM, ActivityFromSource.NOTIFICATION);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent intent = null;
        try {
            intent = PendingIntent.getActivity(context, mCount++, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (intent == null) {
            return;
        }
        NotificationCompat.Builder builder = new NotificationBuilder(context);
        builder.setAutoCancel(true);
        builder.setContentTitle(session.name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.mipmap.icon_notice_small);

        } else {
            builder.setSmallIcon(R.mipmap.icon_logo);

        }
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_logo));
        builder.setContentText(content);
        builder.setTicker(session.name + ":" + content);
        builder.setContentIntent(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
            builder.setFullScreenIntent(null, false);
        }
        builder.setWhen(System.currentTimeMillis());

        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            builder.setChannelId(BasicNotificationManager.DEFAULT_CHANNEL_ID);
        }

        Notification notification = builder.build();


        if (RomUtil.isXiaomi()) {
            IntentUtil.setXiaoMiBadge(notification, session.getUnread());
        }

        NotifyParams notifyParams = NotifyParams.newNotifyParams()
                .setContext(context)
                .setKey(session.identifier.hashCode())
                .setVibrate(isVibrate)
                .setVoice(isVoice)
                .setNotification(notification);

        notify(notifyParams);
    }

    public void notifySimpleTextMessage(Context context, TextChatMessage message) {
        long current = System.currentTimeMillis();

        boolean isVibrate = EmailSettingInfo.getInstance().getEmailVibrateNotice(context);
        boolean isVoice = EmailSettingInfo.getInstance().getEmailVoiceNotice(context);

        if ((current - mLastNotifyHavingEffectTime < 2000)) {
            isVibrate = false;
            isVoice = false;
        } else {
            mLastNotifyHavingEffectTime = current;
        }

        Intent notificationIntent = new Intent();

        notificationIntent.putExtra(ChatDetailActivity.IDENTIFIER, message.deliveryId);
        notificationIntent.setClass(context, SchemaRouteActivity.class);
        notificationIntent.putExtra(FROM, message.from);
        notificationIntent.putExtra(TO, message.to);
        if (message.getSpecialAction() != null) {
            notificationIntent.putExtra(TARGET_URL, message.getSpecialAction().targetUrl);
        }
        notificationIntent.putExtra(TYPE, message.mFromType.stringValue());

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent intent = null;
        try {
            intent = PendingIntent.getActivity(context, mCount++, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (intent == null) {
            return;
        }
        String title = message.mDisplayName;
        if (TextUtils.isEmpty(title)) {
            title = AppUtil.getAppName(AtworkApplicationLike.baseContext);
        }
        Notification notification = buildNotification(context, title, message.text, message.text, intent);

        int key = message.deliveryId.hashCode() + new Random().nextInt();
        Logger.e("key", key + "");
        NotifyParams notifyParams = NotifyParams.newNotifyParams()
                .setContext(context)
                .setKey(key)
                .setVibrate(isVibrate)
                .setVoice(isVoice)
                .setNotification(notification);


        notify(notifyParams);
    }


    public Notification buildNotification(Context context, String title, String text, String ticker, PendingIntent intent) {
        NotificationCompat.Builder builder = new NotificationBuilder(context);
        builder.setAutoCancel(true);
        builder.setContentTitle(title);
        builder.setSmallIcon(R.mipmap.icon_logo);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_logo));
        builder.setContentText(text);
        builder.setTicker(ticker);
        builder.setContentIntent(intent);
        builder.setWhen(System.currentTimeMillis());
        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            builder.setChannelId(BasicNotificationManager.DEFAULT_CHANNEL_ID);
        }
        return builder.build();
    }


}
