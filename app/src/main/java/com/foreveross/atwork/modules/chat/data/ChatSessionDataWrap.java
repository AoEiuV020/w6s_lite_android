package com.foreveross.atwork.modules.chat.data;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreverht.cache.MessageCache;
import com.foreverht.db.service.repository.ConfigSettingRepository;
import com.foreverht.db.service.repository.EmergencyMessageUnconfirmedRepository;
import com.foreverht.db.service.repository.MessageAppRepository;
import com.foreverht.db.service.repository.MessageRepository;
import com.foreverht.db.service.repository.OrganizationRepository;
import com.foreverht.db.service.repository.SessionRepository;
import com.foreverht.db.service.repository.UnreadSubcriptionMRepository;
import com.foreverht.db.service.repository.UserRepository;
import com.foreverht.threadGear.DbThreadPoolExecutor;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.discussion.responseJson.QueryDiscussionResponseJson;
import com.foreveross.atwork.api.sdk.upload.MediaCenterNetManager;
import com.foreveross.atwork.db.daoService.UserDaoService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionTop;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.appEnum.AppKind;
import com.foreveross.atwork.infrastructure.model.chat.MeetingNoticeChatMessage;
import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.newsSummary.NewsSummaryPostMessage;
import com.foreveross.atwork.infrastructure.model.newsSummary.UnreadNewsSummaryData;
import com.foreveross.atwork.infrastructure.model.setting.BusinessCase;
import com.foreveross.atwork.infrastructure.model.setting.ConfigSetting;
import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleBasic;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.EventPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.NotifyPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.IAtContactMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.FriendNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.OrgNotifyMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.shared.ReadAckPersonShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.AtworkConstants;
import com.foreveross.atwork.infrastructure.support.StringConstants;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.modules.app.manager.AppManager;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.manager.MessageNoticeManager;
import com.foreveross.atwork.manager.OrgApplyManager;
import com.foreveross.atwork.manager.UserManager;
import com.foreveross.atwork.manager.model.MultiResult;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.model.EntrySessionRequest;
import com.foreveross.atwork.modules.chat.service.ChatService;
import com.foreveross.atwork.modules.chat.util.ChatDetailExposeBroadcastSender;
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper;
import com.foreveross.atwork.modules.chat.util.SystemChatMessageHelper;
import com.foreveross.atwork.modules.chat.util.UndoMessageHelper;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.IntentUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import kotlin.collections.CollectionsKt;


public class ChatSessionDataWrap implements IChatSessionDataWrap {

    private static ChatSessionDataWrap sChatSessionDataWrap = new ChatSessionDataWrap();

    private CopyOnWriteArraySet<Session> mSessionList = new CopyOnWriteArraySet<>();
    public ConcurrentHashMap<String, Session> mSessionMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Session> mSendStatusMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Set<String>> mSessionUnreadCountCache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Set<String>> mSessionMessageInFileStreaming = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Set<ConfigSetting>> mSessionConfigsMap = new ConcurrentHashMap<>();
    private boolean mSessionConfigsMapLoadFromDb = false;


    private static List<String> mNoticeList = new ArrayList<>();

    private ChatSessionDataWrap() {

    }

    public static ChatSessionDataWrap getInstance() {
        return sChatSessionDataWrap;
    }


    public void asyncBatchAddMessages(final String sessionIdentifier, final ParticipantType participantType, final List<ChatPostMessage> chatPostMessages) {
        if (chatPostMessages == null || chatPostMessages.size() == 0) {
            return;
        }

        synchronized (this) {
            //user
            if (participantType == ParticipantType.User) {
                batchAddP2PMessages(sessionIdentifier, chatPostMessages);
            }
            //discussion
            else if (participantType == ParticipantType.Discussion) {
                batchAddDiscussionMessages(sessionIdentifier, chatPostMessages);

            //app
            } else if(participantType == ParticipantType.App) {
                batchAddAppMessages(sessionIdentifier, chatPostMessages);
            }
        }
    }



    private void batchAddAppMessages(final String sessionIdentifier, final List<ChatPostMessage> chatPostMessages) {

        if (chatPostMessages == null || chatPostMessages.size() == 0) {
            return;
        }

        final ChatPostMessage lastChatPostMessage = chatPostMessages.get(chatPostMessages.size() - 1);
        final UserHandleBasic chatUser = ChatMessageHelper.getChatUser(lastChatPostMessage);
        final SessionType sessionType = SessionType.LightApp;

        String name = sessionIdentifier;
        App app = AppManager.getInstance().queryAppSync(BaseApplicationLike.baseContext, sessionIdentifier, lastChatPostMessage.mOrgId);
        if (app != null) {
            name = app.getTitleI18n(BaseApplicationLike.baseContext);
        }
        EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest()
                .setChatType(sessionType)
                .setName(name)
                .setIdentifier(sessionIdentifier)
                .setDomainId(chatUser.mDomainId)
                .setMessage(lastChatPostMessage)
                .setUpdateDb(false);

        Session session = entrySessionSafely(entrySessionRequest);

        session.lastMessageText = lastChatPostMessage.getSessionShowTitle();
        session.lastTimestamp = lastChatPostMessage.deliveryTime;
        session.setLastMessageStatus(AtworkApplicationLike.baseContext, lastChatPostMessage);
        session.lastMessageId = lastChatPostMessage.deliveryId;

        //????????????????????????
        MessageCache.getInstance().batchAddMessage(sessionIdentifier, chatPostMessages);


        //?????????????????????SESSION
        SessionRepository.getInstance().updateSession(session);

        MessageRepository.getInstance().batchInsertMessages(chatPostMessages);

        if(SessionType.Service.equals(session.type) && app != null) {
            List<NewsSummaryPostMessage> messageList = new ArrayList<>();
            for(ChatPostMessage chatPostMessage : chatPostMessages){
                NewsSummaryPostMessage newsSummaryRVData = new NewsSummaryPostMessage();
                newsSummaryRVData.setChatPostMessage(chatPostMessage);
                newsSummaryRVData.setChatId(session.identifier);
                newsSummaryRVData.setOrgId(app.mOrgId);
                if(!LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext).equals(chatPostMessage.from) &&
                        (chatPostMessage.mTargetScope == ChatPostMessage.TARGET_SCOPE_ALL || chatPostMessage.mTargetScope == ChatPostMessage.TARGET_SCOPE_NEWS_SUMMARY)) {
                    messageList.add(newsSummaryRVData);
                }
            }
            MessageAppRepository.getInstance().batchInsertTotleAppMessages(messageList);
        }
    }


    private void batchAddP2PMessages(final String sessionIdentifier, final List<ChatPostMessage> chatPostMessages) {

        if (chatPostMessages == null || chatPostMessages.size() == 0) {
            return;
        }
        final ChatPostMessage lastChatPostMessage = chatPostMessages.get(chatPostMessages.size() - 1);
        final UserHandleInfo chatUser = ChatMessageHelper.getChatUserInfo(lastChatPostMessage);
        final SessionType sessionType = SessionType.User;

        String name = sessionIdentifier;
        User user = UserRepository.getInstance().queryUserByUserId(sessionIdentifier);
        if (user != null) {
            name = user.getShowName();
        } else {
            name = chatUser.mShowName;
        }


        EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest()
                .setChatType(sessionType)
                .setName(name)
                .setIdentifier(sessionIdentifier)
                .setDomainId(chatUser.mDomainId)
                .setMessage(lastChatPostMessage)
                .setUpdateDb(false);

        Session session = entrySessionSafely(entrySessionRequest);

        session.lastMessageText = lastChatPostMessage.getSessionShowTitle();
        session.lastTimestamp = lastChatPostMessage.deliveryTime;
        session.setLastMessageStatus(AtworkApplicationLike.baseContext, lastChatPostMessage);
        session.lastMessageId = lastChatPostMessage.deliveryId;


        //????????????????????????
        MessageCache.getInstance().batchAddMessage(sessionIdentifier, chatPostMessages);


        //?????????????????????SESSION
        SessionRepository.getInstance().updateSession(session);

    }

    private void batchAddDiscussionMessages(String chatUser, List<ChatPostMessage> chatPostMessages) {

        final ChatPostMessage lastChatPostMessage = chatPostMessages.get(chatPostMessages.size() - 1);

        final SessionType sessionType = SessionType.Discussion;

        final UserHandleBasic lastChatUser = ChatMessageHelper.getChatUser(lastChatPostMessage);

        String name = lastChatPostMessage.mDisplayName;

        if (StringUtils.isEmpty(name)) {
            Discussion discussion = DiscussionManager.getInstance().queryDiscussionSync(BaseApplicationLike.baseContext, chatUser);
            if (null != discussion) {
                name = discussion.mName;
            }
        }

        EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest()
                .setChatType(sessionType)
                .setName(name)
                .setIdentifier(chatUser)
                .setDomainId(lastChatUser.mDomainId)
                .setMessage(lastChatPostMessage)
                .setUpdateDb(false);

        Session session = entrySessionSafely(entrySessionRequest);

//        String contact = UserManager.getInstance().getReadableName(BaseApplicationLike.baseContext, lastChatPostMessage.from, lastChatPostMessage.mFromDomain);
        session.avatar = lastChatPostMessage.mDisplayAvatar;
        session.lastMessageText = name + "???" + lastChatPostMessage.getSessionShowTitle();
        session.lastTimestamp = lastChatPostMessage.deliveryTime;
        session.lastMessageStatus = lastChatPostMessage.chatStatus;
        session.lastMessageId = lastChatPostMessage.deliveryId;

        //????????????????????????
        MessageCache.getInstance().batchAddMessage(chatUser, chatPostMessages);

        //?????????????????????SESSION
        SessionRepository.getInstance().updateSession(session);

        MessageRepository.getInstance().batchInsertMessages(chatPostMessages);
    }

    public int getUnreadCount() {

        int allUnread = 0;
        for (Session session : mSessionList) {
            if(shouldCountAllUnread(session)) {
                allUnread += session.getUnread();

            }
        }
        return allUnread;
    }

    private boolean shouldCountAllUnread(Session session) {
        return !isShield(session.identifier)
                && !Session.WORKPLUS_DISCUSSION_HELPER.equals(session.identifier)
                && !isAnnounceAppNotCheckDb(session.identifier)
                && !Session.COMPONENT_ANNOUNCE_APP.equals(session.identifier)
                && !Session.WORKPLUS_SUMMARY_HELPER.equals(session.identifier);
    }

    /**
     * ?????? Session ????????????
     *
     * @param forceUpdate ???????????? session list, ????????????????????????
     */
    public void checkSessionListUpdate(boolean forceUpdate) {
        if (forceUpdate) {
            setSessionList(ChatService.queryAllSessionsDb());

        } else {
            if (mSessionMap.size() == 0 && mSessionList.size() == 0) {
                mSessionList.addAll(ChatService.queryAllSessionsDb());
                refreshSessionMapData();
            }

        }
    }

    public void checkFilteredSessions() {
        setSessionList(ChatService.queryFilteredSessionsFromDb());

    }

    public void asyncReceiveMessageWithoutRefreshUI(ChatPostMessage chatPostMessage, boolean isCameFromOnline) {
        synchronized (this) {

            checkSessionListUpdate(false);
            //????????????
            if (isLegalMessageBuildSession(chatPostMessage)
                    ) {

                if(!isCameFromOnline && (chatPostMessage.isFromDiscussionChat()
                        || chatPostMessage.isLegalP2pUserChat(AtworkApplicationLike.baseContext))) {
                    UserDaoService.getInstance().updateUserBasicInfo(AtworkApplicationLike.baseContext, chatPostMessage.from, chatPostMessage.mFromDomain, chatPostMessage.mMyName, chatPostMessage.mMyAvatar, chatPostMessage.mMyStatus, chatPostMessage.deliveryTime);
                }

                tryUpdateTargetReadTime(chatPostMessage);


                //????????????
                if(Session.ASSET_NOTIFY_SYSTEM.equals(chatPostMessage.from)) {
                    receiveWorkplusAssetNotifyMessage(chatPostMessage, isCameFromOnline);
                    return;
                }

                //???????????????
                if (ParticipantType.App.equals(chatPostMessage.mFromType)
                        || ParticipantType.App.equals(chatPostMessage.mToType)) {
                    if(chatPostMessage.mTargetScope != ChatPostMessage.TARGET_SCOPE_NEWS_SUMMARY) {
                        receiveAppMessage(chatPostMessage, isCameFromOnline);
                    }
                    if(chatPostMessage.mTargetScope == ChatPostMessage.TARGET_SCOPE_NEWS_SUMMARY ||
                            chatPostMessage.mTargetScope == ChatPostMessage.TARGET_SCOPE_ALL) {
                        receiveServiceNoNewsSummary(chatPostMessage, isCameFromOnline);
                    }
                    return;
                }


                //??????
                if (ParticipantType.User.equals(chatPostMessage.mToType) && !ParticipantType.System.equals(chatPostMessage.mFromType)) {
                    receiveP2PMessage(chatPostMessage, isCameFromOnline);
                    return;
                }
                //??????
                if (ParticipantType.Discussion.equals(chatPostMessage.mToType)) {

                    receiveDiscussionMessage(chatPostMessage, isCameFromOnline);
                    return;
                }

                //??????
                if (ParticipantType.System.equals(chatPostMessage.mFromType) && BodyType.System.equals(chatPostMessage.mBodyType)) {
                    receiveNotifySystemMessage(chatPostMessage, isCameFromOnline);
                    return;
                }

                //????????????????????????(umeeting)
                if (ParticipantType.Meeting.equals(chatPostMessage.mToType) && BodyType.MeetingNotice.equals(chatPostMessage.mBodyType)) {
                    receiveMeetingNotifyMessage(chatPostMessage, isCameFromOnline);
                    return;
                }



            }

        }
    }

    private void tryUpdateTargetReadTime(ChatPostMessage chatPostMessage) {
        if(User.isYou(AtworkApplicationLike.baseContext, chatPostMessage.from)) {
            return;
        }

        if(chatPostMessage.isFromDiscussionChat()
                || chatPostMessage.isLegalP2pUserChat(AtworkApplicationLike.baseContext)) {

            ReadAckPersonShareInfo.INSTANCE.updateTargetReadTime(AtworkApplicationLike.baseContext, ChatMessageHelper.getChatUserSessionId(chatPostMessage), chatPostMessage.deliveryTime);
        }
    }

    public boolean isLegalMessageBuildSession(ChatPostMessage chatPostMessage) {
        return BodyType.Text.equals(chatPostMessage.mBodyType) ||
                BodyType.File.equals(chatPostMessage.mBodyType) ||
                BodyType.Image.equals(chatPostMessage.mBodyType) ||
                BodyType.Voip.equals(chatPostMessage.mBodyType) ||
                BodyType.Voice.equals(chatPostMessage.mBodyType) ||
                BodyType.Video.equals(chatPostMessage.mBodyType) ||
                BodyType.Article.equals(chatPostMessage.mBodyType) ||
                BodyType.Share.equals(chatPostMessage.mBodyType) ||
                BodyType.System.equals(chatPostMessage.mBodyType) ||
                BodyType.Multipart.equals(chatPostMessage.mBodyType) ||
                BodyType.Notice.equals(chatPostMessage.mBodyType) ||
                BodyType.MeetingNotice.equals(chatPostMessage.mBodyType) ||
                BodyType.Template.equals(chatPostMessage.mBodyType) ||
                BodyType.BingText.equals(chatPostMessage.mBodyType) ||
                BodyType.BingVoice.equals(chatPostMessage.mBodyType) ||
                BodyType.Sticker.equals(chatPostMessage.mBodyType) ||
                BodyType.Face.equals(chatPostMessage.mBodyType) ||
                BodyType.Quoted.equals(chatPostMessage.mBodyType) ||
                BodyType.AnnoFile.equals(chatPostMessage.mBodyType) ||
                BodyType.AnnoImage.equals(chatPostMessage.mBodyType) ||
                BodyType.UnKnown.equals(chatPostMessage.mBodyType);
    }

    public void asyncReceiveChatMessage(ChatPostMessage chatPostMessage) {
        asyncReceiveMessage(chatPostMessage, true);
    }

    /**
     * ??????????????????
     *
     * @param chatPostMessage
     */
    public void asyncReceiveMessage(ChatPostMessage chatPostMessage, boolean isCameFromOnline) {
        if (chatPostMessage == null) {
            return;
        }
        final UserHandleBasic chatUser = ChatMessageHelper.getChatUser(chatPostMessage);
        //?????????????????????
        if (MessageCache.getInstance().isMessageShouldNotReceive(chatUser.mUserId, chatPostMessage)) {
            return;
        }
        asyncReceiveMessageWithoutRefreshUI(chatPostMessage, isCameFromOnline);

        //??????????????????
        Session session = getSessionSafely(chatUser.mUserId, chatPostMessage);
    }

    /**
     * ??????????????????
     *
     * @param chatPostMessages
     */
    public Map<String, List<ChatPostMessage>> asyncOfflineReceiveMessages(List<ChatPostMessage> chatPostMessages) {
        for (ChatPostMessage chatPostMessage : chatPostMessages) {
            asyncReceiveMessageWithoutRefreshUI(chatPostMessage, false);
        }

        Map<String, List<ChatPostMessage>> notifyMaps = notifyOfflineMsg(chatPostMessages);

        return notifyMaps;
    }

    /**
     * ?????????????????????, ???????????? session ???????????????
     */
    @NonNull
    public Map<String, List<ChatPostMessage>> notifyOfflineMsg(List<ChatPostMessage> chatPostMessages) {
        Map<String, List<ChatPostMessage>> notifyMaps = new HashMap<>();

        //??????SESSION???????????????
        for (ChatPostMessage chatPostMessage : chatPostMessages) {
            UserHandleBasic chatUser = ChatMessageHelper.getChatUser(chatPostMessage);
            if (notifyMaps.containsKey(chatUser)) {
                List<ChatPostMessage> msgListInMap = notifyMaps.get(chatUser);
                msgListInMap.add(chatPostMessage);

            } else {
                List<ChatPostMessage> msgListInNew = new ArrayList<>();
                msgListInNew.add(chatPostMessage);
                notifyMaps.put(chatUser.mUserId, msgListInNew);

            }
        }

        for (Map.Entry<String, List<ChatPostMessage>> entry : notifyMaps.entrySet()) {
            String key = entry.getKey();
            List<ChatPostMessage> chatPostMessageList = entry.getValue();
            ChatPostMessage chatPostMessage = chatPostMessageList.get(chatPostMessageList.size() - 1);

            if (ReadStatus.Unread.equals(chatPostMessage.read)) {
                notice(key, chatPostMessage);
            }
        }
        return notifyMaps;
    }

    /**
     * ???????????????/???????????????
     *
     * @param chatPostMessage
     */
    private void receiveAppMessage(final ChatPostMessage chatPostMessage, final boolean isCameFromOnline) {
        Session session = buildAppSession(chatPostMessage);
        if (session == null) return;

        boolean needCount = !session.visible && chatPostMessage.needCount();

        //???????????????"????????????"
        checkEmergencyMsgConfirmStatus(BaseApplicationLike.baseContext, session.identifier, chatPostMessage);

        updateSession(session, chatPostMessage, needCount, isCameFromOnline);
        //????????????????????????
        MessageCache.getInstance().receiveMessage(chatPostMessage);
        //?????????????????????SESSION
        SessionRepository.getInstance().updateSession(session);
//            MessageRepository.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, chatPostMessage);

//            receiveServiceToNewsSummary(chatPostMessage,app,isCameFromOnline);
        MessageRepository.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, chatPostMessage);

    }

    private void receiveServiceNoNewsSummary(final ChatPostMessage chatPostMessage, final boolean isCameFromOnline) {
        final UserHandleBasic chatUser = ChatMessageHelper.getChatUser(chatPostMessage);
        App app = AppManager.getInstance().queryAppSync(BaseApplicationLike.baseContext, chatUser.mUserId, chatPostMessage.mOrgId);
        if (null != app) {
            EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest()
                    .setChatType(SessionType.Service)
                    .setName(BaseApplicationLike.baseContext.getString(R.string.news_summary_title))
                    .setIdentifier(Session.WORKPLUS_SUMMARY_HELPER)
                    .setDomainId(chatUser.mDomainId)
                    .setMessage(chatPostMessage)
                    .setUpdateDb(false);

            Session session = entrySessionSafely(entrySessionRequest);

            boolean needCount = !session.visible && chatPostMessage.needCount();

            updateSession(session, chatPostMessage, needCount, isCameFromOnline);
            //????????????????????????
          //  MessageCache.getInstance().receiveMessage(chatPostMessage);

            //???????????????????????????
            NewsSummaryPostMessage newsSummaryPostMessage = new NewsSummaryPostMessage();
            newsSummaryPostMessage.setChatId(app.mAppId);
            newsSummaryPostMessage.setChatPostMessage(chatPostMessage);
            newsSummaryPostMessage.setOrgId(app.mOrgId);
            if(!LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext).equals(chatPostMessage.from)) {
                MessageAppRepository.getInstance().insertOrUpdateMessageApp(newsSummaryPostMessage);
                UnreadNewsSummaryData unreadNewsSummaryData = new UnreadNewsSummaryData();
                unreadNewsSummaryData.setAppId(app.mAppId);
                unreadNewsSummaryData.setMasId(chatPostMessage.deliveryId);
                unreadNewsSummaryData.setDeliveryTime(String.valueOf(chatPostMessage.deliveryTime));
                if(!(chatPostMessage instanceof TextChatMessage)) {
                    UnreadSubcriptionMRepository.getInstance().insertOrUpdateMessageApp(unreadNewsSummaryData);
                }
            }
        }
    }

    /**
     * ????????????????????????????????????, ???????????????????????????????????????"????????????", ??????????????????????????????, ?????????????????????"????????????", ??????????????????
     * */
    private void checkEmergencyMsgConfirmStatus(Context context, String identifier, ChatPostMessage message) {
        if(!message.isEmergency()) {
            return;
        }

        if (message.mEmergencyInfo.mConfirmed) {
            return;
        }

        ChatPostMessage msgInDb = MessageRepository.getInstance().queryMessage(context, identifier, message.deliveryId);
        //???????????????????????????????????????
        if(null != msgInDb && msgInDb.isEmergencyConfirmed()) {
            message.mEmergencyInfo.mConfirmed = true;
        } else {
            //????????????????????????????????????"??????????????????"???
            EmergencyMessageUnconfirmedRepository.getInstance().insertEmergencyMessage(context, message);
        }


    }




    /**
     * ????????????????????????
     *
     * @param chatPostMessage
     */
    private void receiveDiscussionMessage(final ChatPostMessage chatPostMessage, final boolean isCameFromOnline) {
        Session session = buildDiscussionSession(chatPostMessage, isCameFromOnline);

        if(null == session) return;

        boolean needCount = !session.visible && chatPostMessage.needCount();

        updateSession(session, chatPostMessage, needCount, isCameFromOnline);
        //????????????????????????
        MessageCache.getInstance().receiveMessage(chatPostMessage);

//            checkDiscussionHelperHideStatus(session);

    }


    private boolean handleSystemChatMessageNoSession(ChatPostMessage chatPostMessage, UserHandleBasic chatUser, final boolean isCameFromOnline) {
        if(AtworkConfig.DISSCUSSION_CONFIG.isCommandCreateSessionFromNotify()) {
            return false;
        }

        if (!(chatPostMessage instanceof SystemChatMessage)) {
            return false;
        }


        SystemChatMessage systemChatMessage = (SystemChatMessage) chatPostMessage;

        if (SystemChatMessage.Type.NOTIFY_VOIP != systemChatMessage.type && !mSessionMap.containsKey(chatUser.mUserId)) {
            ChatDaoService.getInstance().insertOrUpdateMessageSync(BaseApplicationLike.baseContext, chatPostMessage, isCameFromOnline);
//            MessageRepository.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, chatPostMessage);
            return true;

        }
        return false;
    }

    private void handleDiscussionMessageSession(ChatPostMessage chatPostMessage, boolean isCameFromOnline, SessionType sessionType, UserHandleBasic chatUser) {
    }

    public void checkDiscussionHelperHideStatus(Session session) {
        if(PersonalShareInfo.getInstance().getSettingDiscussionHelper(AtworkApplicationLike.baseContext)
                && isDiscussionShieldNotCheckDb(session)) {

            PersonalShareInfo.getInstance().setHideDiscussionHelper(AtworkApplicationLike.baseContext, false);

        }
    }

    private boolean isDiscussionNotExist(MultiResult<Discussion> multiResult) {
        return null != multiResult.httpResult.resultResponse
                && (AtworkConstants.DISCUSSION_NOT_FOUND == multiResult.httpResult.resultResponse.status
                || AtworkConstants.USER_NOT_FOUND_IN_DISCUSSION == multiResult.httpResult.resultResponse.status);
    }

    /**
     * @see #updateSession(Session, ChatPostMessage, boolean, boolean, boolean)
     */
    private void updateSession(Session session, ChatPostMessage message, boolean needCount, boolean isCameFromOnline) {
        updateSession(session, message, needCount, isCameFromOnline, false);
    }

    /**
     * ?????? session ??????, ???????????? ?????? ??? ????????????
     */
    public void updateSessionForNotify(Session session, NotifyPostMessage notifyPostMessage, final boolean isCameFromOnline) {
        if (session == null) {
            return;
        }
        Context context = BaseApplicationLike.baseContext;
        if (notifyPostMessage instanceof FriendNotifyMessage) {
            FriendNotifyMessage friendNotifyMessage = (FriendNotifyMessage) notifyPostMessage;
            if (FriendNotifyMessage.Operation.APPLYING.equals(friendNotifyMessage.mOperation)) {
                String content = context.getResources().getString(R.string.tip_invite_friend, friendNotifyMessage.mAddresser.mName);

                session.lastMessageText = content;
                session.entryType = Session.EntryType.To_URL;
                session.entryValue = UrlConstantManager.getInstance().getFriendApprovalFromMain();
                session.lastMessageStatus = ChatStatus.Sended;
                session.lastTimestamp = notifyPostMessage.deliveryTime;

                updateNotifySessionCount(session, notifyPostMessage);


                String userIdSaved = "";
                String domainIdSaved = "";
                String nameSaved = "";

                //????????????????????????, ?????????????????????????????????
                if (LoginUserInfo.getInstance().getLoginUserId(context).equals(friendNotifyMessage.mOperator.mUserId)) {
                    userIdSaved = friendNotifyMessage.mAddresser.mUserId;
                    domainIdSaved = friendNotifyMessage.mAddresser.mDomainId;
                    nameSaved = friendNotifyMessage.mAddresser.mName;

                } else {
                    userIdSaved = friendNotifyMessage.mOperator.mUserId;
                    domainIdSaved = friendNotifyMessage.mOperator.mDomainId;
                    nameSaved = friendNotifyMessage.mOperator.mName;



                }

                //??????????????????
                SystemChatMessage systemChatMessage = SystemChatMessageHelper.createMessageByFriendNoticeMessage(content, userIdSaved, domainIdSaved, friendNotifyMessage);
                //??????????????????????????????
                ChatDaoService.getInstance().insertOrUpdateMessageSync(context, systemChatMessage, isCameFromOnline);
//                MessageRepository.getInstance().insertOrUpdateMessage(context, systemChatMessage);

            } else if (FriendNotifyMessage.Operation.APPROVED.equals(friendNotifyMessage.mOperation)) {
                String content;

                if (LoginUserInfo.getInstance().getLoginUserId(context).equals(friendNotifyMessage.mOperator.mUserId)) {
                    content = context.getResources().getString(R.string.me_accept_friend_tip_head) + friendNotifyMessage.mAddresser.mName + context.getString(R.string.me_accept_friend_tip_tail);

                } else {
                    content = context.getResources().getString(R.string.other_accept_friend_tip);

                }
                session.lastMessageText = content;
                session.lastMessageStatus = ChatStatus.Sended;
                session.lastTimestamp = notifyPostMessage.deliveryTime;
            }

            SessionRepository.getInstance().updateSession(session);
            SessionRefreshHelper.notifyRefreshSessionAndCount();

        } else if (notifyPostMessage instanceof OrgNotifyMessage) {
            OrgNotifyMessage orgNotifyMessage = (OrgNotifyMessage) notifyPostMessage;
            if (OrgNotifyMessage.Operation.APPLYING.equals(orgNotifyMessage.mOperation)) {

                String content = context.getResources().getString(R.string.tip_applying_org, orgNotifyMessage.mAddresser.mName, orgNotifyMessage.mOrgName);

                if (shouldUpdateApplyInfo(orgNotifyMessage)) {

                    LogUtil.e("org_test", "org name: " + orgNotifyMessage.mOrgName + "  ->  is if");

                    session.lastMessageText = content;
                    session.entryType = Session.EntryType.To_ORG_APPLYING;
                    session.lastMessageStatus = ChatStatus.Sended;
                    session.lastTimestamp = notifyPostMessage.deliveryTime;

                    updateNotifySessionCount(session, notifyPostMessage);


                    LoginUserBasic loginUserBasic = LoginUserInfo.getInstance().getLoginUserBasic(context);

                    SystemChatMessage systemChatMessage = new SystemChatMessage(SystemChatMessage.Type.NOTIFY_ORG, content, OrgNotifyMessage.FROM, loginUserBasic.mUserId, ((OrgNotifyMessage) notifyPostMessage).mDomainId, loginUserBasic.mDomainId);
                    systemChatMessage.deliveryId = notifyPostMessage.deliveryId;
                    systemChatMessage.deliveryTime = notifyPostMessage.deliveryTime;
                    systemChatMessage.mToType = ParticipantType.User;
                    systemChatMessage.mOrgId = ((OrgNotifyMessage) notifyPostMessage).mOrgCode;
                    systemChatMessage.orgLogo = ((OrgNotifyMessage) notifyPostMessage).mLogo;
                    //??????????????????????????????
                    ChatDaoService.getInstance().insertOrUpdateMessageSync(context, systemChatMessage, isCameFromOnline);
//                    MessageRepository.getInstance().insertOrUpdateMessage(context, systemChatMessage);

                } else {
                    LogUtil.e("org_test", "org name: " + orgNotifyMessage.mOrgName + "  ->  is else");
                }


                OrgApplyManager.getInstance().insertAndSendBroadcast((OrgNotifyMessage) notifyPostMessage, content);

            }

            SessionRepository.getInstance().updateSession(session);
            SessionRefreshHelper.notifyRefreshSessionAndCount();
        }
    }

    public void updateSessionForEvent(Session session, EventPostMessage eventPostMessage) {
        if (eventPostMessage instanceof UndoEventMessage) {
            UndoEventMessage undoEventMessage = (UndoEventMessage) eventPostMessage;
            synchronized (session.identifier) {
                //?????????????????????????????????
                if (undoEventMessage.isMsgUndo(session.lastMessageId)) {
                    session.lastMessageStatus = ChatStatus.Sended;
                    session.lastTimestamp = undoEventMessage.deliveryTime;
                    session.lastMessageId = undoEventMessage.deliveryId;
                    String content = UndoMessageHelper.getUndoContent(BaseApplicationLike.baseContext, eventPostMessage);
                    session.lastMessageText = content;
                }

                //???????????? At ????????? At??????????????????, ????????? Text ????????? session
                if (!Session.ShowType.At.equals(session.lastMessageShowType) || undoEventMessage.isMsgUndo(session.atMessageId)) {
                    session.lastMessageShowType = Session.ShowType.Text;
                }

                //??????????????????, ???????????????
                for (String removedMsgId : undoEventMessage.mEnvIds) {
                    session.dismissUnread(removedMsgId);
                }


                SessionRepository.getInstance().updateSession(session);

                SessionRefreshHelper.notifyRefreshSessionAndCount();
            }
        }
    }

    /**
     * ??????????????????????????????, ?????? session ??????
     *
     * @param sessionId
     */
    public void updateSessionForRemoveMsgs(String sessionId, List<String> removedMsgIds,boolean isNewsSummary) {
        Session session = getSessionSafely(sessionId, null);

        if (null == session) {
            return;
        }

        synchronized (session.identifier) {

            if (removedMsgIds.contains(session.lastMessageId)) {
                List<ChatPostMessage> chatPostMessages;
                if(isNewsSummary){
                    chatPostMessages = MessageAppRepository.getInstance().queryLatestMessage(BaseApplicationLike.baseContext, sessionId);
                }else {
                    chatPostMessages = MessageRepository.getInstance().queryLatestMessage(BaseApplicationLike.baseContext, sessionId);
                }
                if (!ListUtil.isEmpty(chatPostMessages)) {
                    ChatPostMessage lastMessage = chatPostMessages.get(0);

                    setSessionTextContent(session, lastMessage);

                    session.lastTimestamp = lastMessage.deliveryTime;
                    session.lastMessageId = lastMessage.deliveryId;

                    session.setLastMessageStatus(AtworkApplicationLike.baseContext, lastMessage);

                    setSessionShowType(session, lastMessage);

                } else {
                    //????????????????????????
                    session.lastMessageStatus = ChatStatus.Sended;
                    session.lastMessageId = StringUtils.EMPTY;
                    session.lastMessageText = StringUtils.EMPTY;
                }
            }

            //??????????????????, ???????????????
            for (String removedMsgId : removedMsgIds) {
                session.dismissUnread(removedMsgId);
            }

            SessionRepository.getInstance().updateSession(session);

            SessionRefreshHelper.notifyRefreshSessionAndCount();
        }

    }



    /**
     * ?????? session
     */
    public void emptySessionInMainChatView(Session session) {
        session.lastMessageStatus = ChatStatus.Sended;
        session.lastMessageId = StringUtils.EMPTY;
        session.lastMessageText = StringUtils.EMPTY;
        SessionRepository.getInstance().updateSession(session);
    }

    public void updateSession(Session session, ChatPostMessage message, boolean needCount, boolean isCameFromOnline, boolean forceUpdateSession) {

        synchronized (session.identifier) {
            if(message.mTargetScope != ChatPostMessage.TARGET_SCOPE_NEWS_SUMMARY) {
                ChatDaoService.getInstance().insertOrUpdateMessageSync(BaseApplicationLike.baseContext, message, isCameFromOnline);
            }
//            MessageRepository.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, message);
            if (session.identifier.equals(Session.DROPBOX_OVERDUE_REMIND)) {
                MessageRepository.getInstance().insertDropboxOverdueMessage(BaseApplicationLike.baseContext, message);
            } else if(message.mTargetScope == ChatPostMessage.TARGET_SCOPE_NEWS_SUMMARY) {
                //????????????????????????????????????????????????????????????
            } else {
                MessageRepository.getInstance().insertOrUpdateMessage(BaseApplicationLike.baseContext, message);
            }


            //?????? session ???????????????????????????(@?????????)
            if (isNeedUpdateMessageShow(session, message) || forceUpdateSession) {

                setSessionTextContent(session, message);

                session.lastTimestamp = message.deliveryTime;
                session.lastMessageId = message.deliveryId;

                session.setLastMessageStatus(AtworkApplicationLike.baseContext, message);

                setSessionShowType(session, message);
            }

            if(Session.WORKPLUS_SUMMARY_HELPER.equals(session.identifier)){
                session.lastMessageText = message.mDisplayName +":"+ message.getSessionShowTitle();
            }

            updateChatSessionCount(needCount, isCameFromOnline, session, message);

            //?????????????????????SESSION
            SessionRepository.getInstance().updateSession(session);

            if (isCameFromOnline) {
                notifyUnreadUI(message);

            }

            refreshSessionMapData();
        }

    }


    public void updateSession(Session session) {

        SessionRepository.getInstance().updateSession(session);
        refreshSessionMapData();

    }

    public void notifyUnreadUI(ChatPostMessage message) {
        //?????????????????? tab ?????????????????????????????????????????????
        final UserHandleBasic chatUser = ChatMessageHelper.getChatUser(message);
        notice(chatUser.mUserId, message);
        SessionRefreshHelper.notifyRefreshCount();
        //???????????????????????????
        IntentUtil.setBadge(BaseApplicationLike.baseContext);
    }

    private void setSessionShowType(Session session, ChatPostMessage message) {
        //????????????????????????
        if (message.isUndo()) {
            if (!Session.ShowType.At.equals(session.lastMessageShowType)) {
                session.lastMessageShowType = Session.ShowType.Text;
            }

            return;
        }


//        if(Session.ShowType.Emergency.equals(session.lastMessageShowType)) {
//            return;
//        }

        if(message.isEmergencyUnconfirmed()) {
            //???????????????????????? session ?????????[?????????]
//            session.lastMessageShowType = Session.ShowType.Emergency;
            session.lastMessageShowType = Session.ShowType.Text;
            return;
        }


        if (message instanceof IAtContactMessage) {
            IAtContactMessage atContactMessage = (IAtContactMessage) message;

            if (isNeedUpdateSessionAtType(atContactMessage)) {
                LogUtil.e("need update ~~ at status");
                session.atMessageId = ((PostTypeMessage)atContactMessage).deliveryId;
                session.lastMessageShowType = Session.ShowType.At;
                session.lastAtMessageText = atContactMessage.getContent();

            } else if (!Session.ShowType.At.equals(session.lastMessageShowType)) {
                session.lastMessageShowType = Session.ShowType.Text;
            }

            return;
        }


        //?????????@??????, ??????????????????
        if (Session.ShowType.At.equals(session.lastMessageShowType)) {
            return;
        }

        if (!message.isBurn() && message instanceof VoiceChatMessage) {
            session.lastMessageShowType = Session.ShowType.Audio;
        } else {
            //??????, ??????, ?????????????????????????????????
            session.lastMessageShowType = Session.ShowType.Text;
        }
    }

    private void setSessionTextContent(Session session, ChatPostMessage message) {
        if (message.isUndo()) {
            session.lastMessageText = UndoMessageHelper.getUndoContent(BaseApplicationLike.baseContext, message);

        } else {
            if (SessionType.Discussion.equals(session.type)) {
                String contact = ChatMessageHelper.getReadableNameShow(BaseApplicationLike.baseContext, message);

                if (message instanceof SystemChatMessage) {
                    SystemChatMessage systemChatMessage = (SystemChatMessage) message;

                    if (SystemChatMessage.Type.NOTIFY_VOIP == systemChatMessage.type) {
                        session.lastMessageText = message.getSessionShowTitle();

                    } else {
                        contact = BaseApplicationLike.baseContext.getResources().getString(R.string.system_message);
                        session.lastMessageText = contact + StringConstants.SEMICOLON + message.getSessionShowTitle();

                    }

                } else {
                    session.lastMessageText = contact + StringConstants.SEMICOLON + message.getSessionShowTitle();

                }

            } else {
                if (!StringUtils.isEmpty(message.getSessionShowTitle())) {
                    session.lastMessageText = message.getSessionShowTitle();
                }
            }

        }
    }

    private boolean isNeedUpdateMessageShow(Session session, ChatPostMessage message) {
        boolean isTextTypeContentEmpty = false;

        if (message instanceof IAtContactMessage) {

            IAtContactMessage atContactMessage = (IAtContactMessage) message;

            isTextTypeContentEmpty = StringUtils.isEmpty(session.lastMessageText)
                    && !atContactMessage.containAtMe(BaseApplicationLike.baseContext);

        }

        return isTextTypeContentEmpty
                || StringUtils.isEmpty(session.lastMessageId) //???????????????????????????????????????????????????
                || session.lastTimestamp <= message.deliveryTime
                || Session.ShowType.At.equals(session.lastMessageShowType);
    }

    private boolean isNeedUpdateSessionAtType(IAtContactMessage atContactMessage) {

        ChatPostMessage message = (ChatPostMessage) atContactMessage;

        boolean needUpdate = false;
        if (message.read.equals(ReadStatus.Unread)) {
            needUpdate = atContactMessage.isAtMe(BaseApplicationLike.baseContext);
        }

        return needUpdate;
    }


    /**
     * ????????????????????????
     *
     * @param chatPostMessage
     */
    private void receiveP2PMessage(ChatPostMessage chatPostMessage, boolean isCameFromOnline) {
        Session session = buildP2pSession(chatPostMessage, isCameFromOnline);
        if (session == null) return;

        boolean needCount = !session.visible && chatPostMessage.needCount();
        updateSession(session, chatPostMessage, needCount, isCameFromOnline);

        //????????????????????????
        MessageCache.getInstance().receiveMessage(chatPostMessage);


    }



    public void receiveNotifySystemMessage(ChatPostMessage chatPostMessage, boolean isCameFromOnline) {
        Session session = buildNotifySystemSession(chatPostMessage);
        if (session == null) return;

        boolean needCount = !session.visible && chatPostMessage.needCount();
        updateSession(session, chatPostMessage, needCount, isCameFromOnline);

        //??????
        if (isCameFromOnline) {
            MessageNoticeManager.getInstance().showNotifyNotification(BaseApplicationLike.baseContext, session);

        }

        //????????????????????????
        MessageCache.getInstance().receiveMessage(chatPostMessage);


    }


    public void receiveMeetingNotifyMessage(ChatPostMessage chatPostMessage, boolean isCameFromOnline) {
        Session session = buildMeetingNotifySession(chatPostMessage);
        if (session == null) return;

        boolean needCount = !session.visible && chatPostMessage.needCount();
        updateSession(session, chatPostMessage, needCount, isCameFromOnline);

        //??????
        if (isCameFromOnline) {
            MessageNoticeManager.getInstance().showNotifyNotification(BaseApplicationLike.baseContext, session);

        }

        //????????????????????????
        MessageCache.getInstance().receiveMessage(chatPostMessage);


    }


    public void receiveWorkplusAssetNotifyMessage(ChatPostMessage chatPostMessage, boolean isCameFromOnline) {
        Session session = buildWorkplusAssetNotifySession(chatPostMessage);
        if (session == null) return;

        boolean needCount = !session.visible && chatPostMessage.needCount();
        updateSession(session, chatPostMessage, needCount, isCameFromOnline);

        //??????
        if (isCameFromOnline) {
            MessageNoticeManager.getInstance().showNotifyNotification(BaseApplicationLike.baseContext, session);

        }

        //????????????????????????
        MessageCache.getInstance().receiveMessage(chatPostMessage);


    }


    public void updateSessionByDiscussion(Discussion discussion) {
        if (discussion == null) {
            return;
        }
        updateSessionByName(discussion.mDiscussionId, discussion.mName);
    }

    /**
     * @param identifier
     * @param name
     */
    public void updateSessionByName(String identifier, String name) {
        Session session = getSessionSafely(identifier, null);
        if (session == null) {
            return;
        }
        if (!StringUtils.isEmpty(name)) {
            session.name = name;
            SessionRefreshHelper.notifyRefreshSession();

        }
    }

    private void updateNotifySessionCount(Session session, NotifyPostMessage notifyPostMessage) {
        if (ReadStatus.Unread.equals(notifyPostMessage.read)) {
            session.addUnread(notifyPostMessage.deliveryId);

        }
    }

    /**
     * ????????????,  ?????? ???????????? session ?????????
     */
    private void updateChatSessionCount(boolean needCount, boolean isCameFromOnline, Session session, ChatPostMessage message) {
        if (needCount && ChatSendType.RECEIVER.equals(message.chatSendType)
                 && !message.isUndo()) {

            updateChatSessionCount(isCameFromOnline, session, message);

        }

    }


    public void updateChatInFileStreaming(String sessionId, String msgId) {
        Set<String> msgIdSet = mSessionMessageInFileStreaming.get(sessionId);
        if(null == msgIdSet) {
            msgIdSet = new HashSet<>();
            mSessionMessageInFileStreaming.put(sessionId, msgIdSet);
        }

        msgIdSet.add(msgId);
    }

    public boolean isChatInFileSteaming(String sessionId, String msgId) {
        Set<String> msgIdSet = mSessionMessageInFileStreaming.get(sessionId);

        if(null != msgIdSet) {
            return msgIdSet.contains(msgId);
        }

        return false;
    }


    public void removeChatInFileStreaming(String sessionId, String msgId) {
        Set<String> msgIdSet = mSessionMessageInFileStreaming.get(sessionId);

        if(null != msgIdSet) {
            msgIdSet.remove(msgId);
        }
    }

    public void clearChatInFileStreaming(List<String> sessionIdList) {
        for(String sessionId: sessionIdList) {
            mSessionMessageInFileStreaming.remove(sessionId);
        }
    }

    public void clearChatInFileStreaming() {
        mSessionMessageInFileStreaming.clear();
    }

    /**
     * ?????? ???????????? session ?????????
     */
    public void updateChatSessionCount(boolean isCameFromOnline, Session session, ChatPostMessage message) {
        if (isCameFromOnline) {
            session.addUnread(message.getMsgReadDeliveryId());

        } else {

            if (ReadStatus.Unread.equals(message.read)) {
                if (mSessionUnreadCountCache.containsKey(session.identifier)) {
                    Set<String> unreadSet = mSessionUnreadCountCache.get(session.identifier);
                    unreadSet.add(message.getMsgReadDeliveryId());

                } else {
                    Set<String> unreadSet = new HashSet<>();
                    unreadSet.add(message.getMsgReadDeliveryId());
                    mSessionUnreadCountCache.put(session.identifier, unreadSet);
                }

            }

        }
    }


    public void updateSessionCountFromCache() {
        List<Session> sessionUpdatedList = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : mSessionUnreadCountCache.entrySet()) {
            String identifier = entry.getKey();
            Set<String> unreadSet = entry.getValue();

            Session session = getSession(identifier, null);
            if (null != session) {
                session.unreadMessageIdSet.addAll(unreadSet);

                sessionUpdatedList.add(session);
            }
        }

        if (!ListUtil.isEmpty(sessionUpdatedList)) {
            SessionRepository.getInstance().batchUpdateSession(sessionUpdatedList);
        }


        mSessionUnreadCountCache.clear();
    }


    /**
     * ??????????????????????????????
     *
     * @param identifiers
     */
    public void notifyMessageSendSuccess(final List<String> identifiers) {

        List<Session> sessionList = new ArrayList<>();
        for (String identifier : identifiers) {
//            LogUtil.i("shadow", mSendStatusMap.size() + ",map"+mSendStatusMap.toString()+"???id"+identifier);
            if (mSendStatusMap.containsKey(identifier)) {
                Session session = mSendStatusMap.get(identifier);
                if (null != session) {
                    session.lastMessageStatus = ChatStatus.Self_Send;
                    sessionList.add(session);
                }


//                ChatDaoService.getInstance().sessionRefresh(session);
            }
        }

        SessionRepository.getInstance().batchUpdateSession(sessionList);

    }

    /**
     * ??????????????????????????????
     *
     * @param identifier
     */
    public void notifyMessageSendFail(final String identifier) {
        if (mSendStatusMap.containsKey(identifier)) {
            Session session = mSendStatusMap.get(identifier);
            session.lastMessageStatus = ChatStatus.Not_Send;
            ChatDaoService.getInstance().sessionRefresh(session);
            ChatDetailExposeBroadcastSender.refreshMessageListViewUI();
        }
    }



    /**
     * ????????????????????????????????????????????????????????????
     *
     * @param entrySessionRequest
     */
    @NonNull
    public Session entrySessionSafely(EntrySessionRequest entrySessionRequest) {

        synchronized (this) {
            return entrySession(entrySessionRequest);

        }

    }

    @NonNull
    public Session entrySession(EntrySessionRequest entrySessionRequest) {
        Session session = getSessionAndUpdate(entrySessionRequest);

        if (null == session) {
            session = newSession(entrySessionRequest);

        }

        //??????session????????????????????????????????????
        if (null != entrySessionRequest.mMessage && null != entrySessionRequest.mMessage.getSpecialAction()) {
            session.entryType = Session.EntryType.To_URL;
            session.entryValue = entrySessionRequest.mMessage.getSpecialAction().targetUrl;
        }

        refreshSessionMapData();

        if (entrySessionRequest.mUpdateDb) {
            ChatDaoService.getInstance().sessionRefresh(session);
        }

        return session;
    }


    @Nullable
    private Session getSessionAndUpdate(EntrySessionRequest entrySessionRequest) {
        Session session;
        if (mSessionMap.containsKey(entrySessionRequest.mIdentifier)) {
            session = mSessionMap.get(entrySessionRequest.mIdentifier);
            session.name = entrySessionRequest.mName;
            session.avatar = entrySessionRequest.mAvatar;
            session.mDomainId = entrySessionRequest.mDomainId;
            session.orgId = entrySessionRequest.mOrgId;

        } else {
            session = getSessionFromList(entrySessionRequest.mIdentifier);
            if (null != session) {
                session.orgId = entrySessionRequest.mOrgId;
            }
        }
        if (session != null) {
            session.entryType = Session.EntryType.To_Chat_Detail;
            session.entryValue = "";
        }

        return session;
    }

    @NonNull
    private Session newSession(EntrySessionRequest entrySessionRequest) {
        Session session = new Session();
        session.identifier = entrySessionRequest.mIdentifier;
        session.name = entrySessionRequest.mName;
        session.avatar = entrySessionRequest.mAvatar;
        session.type = entrySessionRequest.mChatType;
        session.lastTimestamp = TimeUtil.getCurrentTimeInMillis();
        session.mDomainId = entrySessionRequest.mDomainId;
        session.orgId = entrySessionRequest.mOrgId;
        if (entrySessionRequest.mRemoteTop) {
            session.top = SessionTop.REMOTE_TOP;
        }

        boolean needAddList = true;
        if (FriendNotifyMessage.FROM.equalsIgnoreCase(entrySessionRequest.mIdentifier) && !DomainSettingsManager.getInstance().handleFriendsRelationshipsFeature()) {
            needAddList = false;
        }
        if (OrgNotifyMessage.FROM.equalsIgnoreCase(entrySessionRequest.mIdentifier) && !DomainSettingsManager.getInstance().handleOrgApplyFeature()) {
            needAddList = false;
        }
        if (Session.EMAIL_APP_ID.equalsIgnoreCase(entrySessionRequest.mIdentifier) && !DomainSettingsManager.getInstance().handleEmailSettingsFeature()) {
            needAddList = false;
        }
        if (needAddList) {
            mSessionList.add(session);
            refreshSessionMapData();
            SessionRefreshHelper.notifyRefreshSession();

        }
        return session;
    }


    public void entryEmailSession(Session session) {

        synchronized (this) {
            if (!mSessionMap.containsKey(session.identifier)) {
                mSessionList.add(session);
            } else {
                Session cacheSession = mSessionMap.get(session.identifier);
                cacheSession.lastMessageText = session.lastMessageText;
                cacheSession.lastTimestamp = session.lastTimestamp;
                cacheSession.name = session.name;
                cacheSession.refreshUnreadSetTotally(session.unreadMessageIdSet, false);
            }
            ChatDaoService.getInstance().sessionRefresh(session);
            if (!DomainSettingsManager.getInstance().handleEmailSettingsFeature()) {
                return;
            }
            refreshSessionMapData();
            SessionRefreshHelper.notifyRefreshSessionAndCount();
        }
    }


    public Session getSessionSafely(String identifier, @Nullable ChatPostMessage message) {
        synchronized (this) {
            return getSession(identifier, message);
        }
    }

    /**
     * ??????identifier??????session, ??????????????????, UI ?????????????????????, ????????????????????????????????????
     *
     * @param identifier
     * @param message    ?????????null??????????????????????????????????????????
     * @return
     */
    public Session getSession(String identifier, @Nullable ChatPostMessage message) {

        if(StringUtils.isEmpty(identifier)) {
            return null;
        }

        if (mSessionMap.isEmpty() && mSessionList.isEmpty()) {
            mSessionList.addAll(ChatService.queryAllSessionsDb());
            refreshSessionMapData();
        }
        Session session = mSessionMap.get(identifier);

        if (session == null) {
            refreshSessionMapData();
            session = mSessionMap.get(identifier);
        }

        //??????session????????????????????????????????????
        if (null != session && null != message && null != message.getSpecialAction()) {
            session.entryType = Session.EntryType.To_URL;
            session.entryValue = message.getSpecialAction().targetUrl;
        }


        return session;
    }

    /**
     * ???????????? session ????????????, ????????? message ???,  ??????removeMessageTable ??? true
     */
    public void removeSessionSafely(String identifier) {

        DbThreadPoolExecutor.getInstance().execute(()->{
            removeSessionSyncSafely(identifier, true);
        });

    }

    public void removeSession(String identifier, boolean removeMessageTable) {

        DbThreadPoolExecutor.getInstance().execute(()->{
            removeSessionSync(identifier, removeMessageTable);
        });

    }

    public void removeSessionSyncSafely(String identifier, boolean removeMessageTable) {
        synchronized (this) {
            removeSessionSync(identifier, removeMessageTable);
        }
    }


    public void removeSessionSync(String identifier, boolean removeMessageTable) {
        removeSessionsSync(ListUtil.makeSingleList(identifier), removeMessageTable);

    }


    public void removeSessionsSync(List<String> sessionIds, boolean removeMessageTable) {

        for(String identifier: sessionIds) {
            Set<String> msgIdSet = mSessionMessageInFileStreaming.get(identifier);
            if(null != msgIdSet) {
                for(String msgIdInFileStreaming: msgIdSet) {
                    MediaCenterNetManager.brokenDownloadingOrUploading(msgIdInFileStreaming);
                }

                clearChatInFileStreaming(ListUtil.makeSingleList(identifier));
            }
        }


        ChatDaoService.getInstance().syncBatchRemoveSession(sessionIds, removeMessageTable);
        for (Session session : mSessionList) {
            if (sessionIds.contains(session.identifier)) {
                mSessionList.remove(session);
            }
        }

        refreshSessionMapData();

        SessionRefreshHelper.notifyRefreshSessionAndCount();

        for(String sessionId : sessionIds) {
            MessageNoticeManager.getInstance().clear(sessionId.hashCode());
        }
    }

    public CopyOnWriteArraySet<Session> getDisplaySessions() {

        CopyOnWriteArraySet<Session> sessions = new CopyOnWriteArraySet<>(getSessions());

        checkDiscussionHelperSession(sessions);

        checkAppAnnounceSession(sessions);

        return sessions;
    }

    private void checkAppAnnounceSession(CopyOnWriteArraySet<Session> sessions) {
        List<Session> announceAppSessions = getAnnounceAppSessions(sessions);

        if(ListUtil.isEmpty(announceAppSessions)) {
            checkRemoveAppAnnounceSession(sessions);
            return;
        }


        sessions.removeAll(announceAppSessions);

        newAppAnnounceSession(sessions, announceAppSessions);

    }

    private void newAppAnnounceSession(CopyOnWriteArraySet<Session> sessions, List<Session> announceAppSessions) {
        EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest()
                .setChatType(SessionType.Custom)
                .setName(AtworkApplicationLike.getResourceString(R.string.announce_app))
                .setIdentifier(Session.COMPONENT_ANNOUNCE_APP)
                .setDomainId(AtworkConfig.DOMAIN_ID)
                .setUpdateDb(false);


        //????????????
        Session newSession = ChatSessionDataWrap.getInstance().entrySessionSafely(entrySessionRequest);
        newSession.entryType = Session.EntryType.APP_ANNOUNCE;


//        int announceAppUnreadCount = 0;
//        for(Session session: announceAppSessions) {
//            if(session.havingUnread()) {
//                announceAppUnreadCount++;
//            }
//        }
//
//        if(0 < announceAppUnreadCount) {
//            newSession.addFakeNoticeUnread();
//        } else {
//            newSession.clearUnread();
//        }

        newSession.clearUnread();

        Session maxTimestampSession = CollectionsKt.maxBy(announceAppSessions, session -> session.lastTimestamp);
        if(null != maxTimestampSession) {
            newSession.lastTimestamp = maxTimestampSession.lastTimestamp;
            newSession.lastMessageText = maxTimestampSession.lastMessageText;

            if(PersonalShareInfo.getInstance().getLastTimeEnterAnnounceApp(AtworkApplicationLike.baseContext) < maxTimestampSession.lastTimestamp) {
                newSession.addFakeNoticeUnread();
            }
        }


        sessions.add(newSession);
    }


    private void checkDiscussionHelperSession(CopyOnWriteArraySet<Session> sessions) {
        if(!PersonalShareInfo.getInstance().getSettingDiscussionHelper(AtworkApplicationLike.baseContext)) {
            checkRemoveDiscussionHelperSession(sessions);
            return;
        }
        List<Session> shieldDiscussionSessions = getShieldDiscussionSessions(sessions);

        if(ListUtil.isEmpty(shieldDiscussionSessions)) {

            checkRemoveDiscussionHelperSession(sessions);
            return;
        }

        sessions.removeAll(shieldDiscussionSessions);


        newDiscussionHelperSession(sessions, shieldDiscussionSessions);
    }

    private void newDiscussionHelperSession(CopyOnWriteArraySet<Session> sessions, List<Session> shieldDiscussionSessions) {
        EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest()
                .setChatType(SessionType.Custom)
                .setName(AtworkApplicationLike.getResourceString(R.string.discussion_helper))
                .setIdentifier(Session.WORKPLUS_DISCUSSION_HELPER)
                .setDomainId(AtworkConfig.DOMAIN_ID)
                .setUpdateDb(false);


        //????????????
        Session newSession = ChatSessionDataWrap.getInstance().entrySessionSafely(entrySessionRequest);
        newSession.entryType = Session.EntryType.DISCUSSION_HELPER;

        int shieldUnreadDiscussionCount = 0;
        for(Session session: shieldDiscussionSessions) {
            if(session.havingUnread()) {
                shieldUnreadDiscussionCount++;
            }
        }

        if (0 < shieldUnreadDiscussionCount) {
            newSession.lastMessageText = AtworkApplicationLike.getResourceString(R.string.discussion_helper_new_messages, shieldUnreadDiscussionCount);
            newSession.addFakeNoticeUnread();


        } else {
            newSession.lastMessageText = AtworkApplicationLike.getResourceString(R.string.discussion_helper_no_new_messages);
            newSession.clearUnread();

        }

        Session maxTimestampSession = CollectionsKt.maxBy(shieldDiscussionSessions, session -> session.lastTimestamp);
        if(null != maxTimestampSession) {
            newSession.lastTimestamp = maxTimestampSession.lastTimestamp;
        }


        sessions.add(newSession);
    }


    @NonNull
    private void checkRemoveAppAnnounceSession(CopyOnWriteArraySet<Session> sessions) {
        List<Session> sessionListClone = new ArrayList<>(sessions);
        Set<Session> removedSet = new HashSet<>();
        for(Session session : sessionListClone) {

            if(Session.COMPONENT_ANNOUNCE_APP.equals(session.identifier)) {
                removedSet.add(session);
            }
        }

        sessions.removeAll(removedSet);


    }

    @NonNull
    private void checkRemoveDiscussionHelperSession(CopyOnWriteArraySet<Session> sessions) {
        List<Session> sessionListClone = new ArrayList<>(sessions);
        Set<Session> removedSet = new HashSet<>();
        for(Session session : sessionListClone) {

            if(Session.WORKPLUS_DISCUSSION_HELPER.equals(session.identifier)) {
                removedSet.add(session);
            }
        }

        sessions.removeAll(removedSet);


    }

    @NonNull
    public List<Session> getShieldDiscussionSessions() {
        return getShieldDiscussionSessions(getSessions());
    }

    @NonNull
    private List<Session> getShieldDiscussionSessions(CopyOnWriteArraySet<Session> sessions) {
        List<Session> shieldDiscussionSessions = new ArrayList<>();

        for(Session session: sessions) {
            if (!isDiscussionShieldNotCheckDb(session)) continue;

            shieldDiscussionSessions.add(session);
        }


        sortAvoidShaking(shieldDiscussionSessions);
        return shieldDiscussionSessions;
    }

    private boolean isDiscussionShieldNotCheckDb(Session session) {

        if(SessionType.Discussion != session.type) {
            return false;
        }

        if(!isShield(session.identifier)) {
            return false;
        }
        return true;
    }

    public int getAnnounceAppSessionsUnreadSum() {
        List<Session> sessionList = new ArrayList<>(getAnnounceAppSessions());
        return CollectionsKt.sumBy(sessionList, session -> session.getUnread());
    }

    @NonNull
    public List<Session> getAnnounceAppSessions() {
        return getAnnounceAppSessions(getSessions());

    }


    @NonNull
    private List<Session> getAnnounceAppSessions(CopyOnWriteArraySet<Session> sessions) {
        List<Session> announceAppSessions = new ArrayList<>();

        for(Session session: sessions) {
            if (!isAppAnnounceNotCheckDb(session)) continue;

            announceAppSessions.add(session);
        }


        sortAvoidShaking(announceAppSessions);
        return announceAppSessions;
    }


    private boolean isAppAnnounceNotCheckDb(Session session) {

        if(!session.isAppType()) {
            return false;
        }

        if(!isAnnounceAppNotCheckDb(session.identifier)) {
            return false;
        }
        return true;
    }


    /**
     * ????????????????????????????????????, ????????????
     * */
    public void sortAvoidShaking(List<Session> sessions) {
        if(ListUtil.isEmpty(sessions)) {
            return;
        }

        HashMap<String, Long> sessionWithSortTimeMap = new HashMap<>();
        for(Session session : sessions) {
            sessionWithSortTimeMap.put(session.identifier, session.lastTimestamp);
        }

//        Collections.sort(mSessionList);
        Collections.sort(sessions, (o1, o2) ->
                SessionRefreshHelper.makeCompareWith(o1, sessionWithSortTimeMap.get(o1.identifier), o2, sessionWithSortTimeMap.get(o2.identifier))
        );
    }

    public CopyOnWriteArraySet<Session> getSessions() {
        return mSessionList;
    }

    public void setSessionList(List<Session> sessionList) {
        synchronized (this) {
            mSessionList.clear();
            mSessionList.addAll(sessionList);
            refreshSessionMapData();
        }
    }


    public void refreshSessionMapData() {
        mSessionMap.clear();
        mSendStatusMap.clear();
        for (Session session : mSessionList) {
            //??????ID???SESSION
            if (session.identifier == null) {
                continue;
            }
            mSessionMap.put(session.identifier, session);

            //???????????????????????????SESSION
            if (!StringUtils.isEmpty(session.lastMessageId)) {
                mSendStatusMap.put(session.lastMessageId, session);
            }

        }
    }

    public void updateSendStatus(String lastMessageId, Session session) {
        mSendStatusMap.put(lastMessageId, session);
    }

    public boolean isSendingStatus(Session session) {
        return mSendStatusMap.containsKey(session.lastMessageId);
    }


    public void clear() {
        clearSessionData();

        clearSessionConfigMap();
        clearChatInFileStreaming();
    }

    public void clearSessionData() {
        this.mSessionList.clear();
        refreshSessionMapData();
    }

    public void clearSessionConfigMap() {
        this.mSessionConfigsMap.clear();
        mSessionConfigsMapLoadFromDb = false;
    }


    public boolean isAnnounceAppNotCheckDb(String identifier) {
        ConfigSetting shieldSetting = getSetting(identifier, BusinessCase.ANNOUNCE_APP);
        if(null == shieldSetting) {
            return false;
        }

        return 1 == shieldSetting.mValue;
    }

    public boolean isShield(String identifier) {
        ConfigSetting shieldSetting = getSetting(identifier, BusinessCase.SESSION_SHIELD);
        if(null == shieldSetting) {
            return false;
        }

        return 1 == shieldSetting.mValue;
    }

    public boolean isTop(String identifier) {
        ConfigSetting shieldSetting = getSetting(identifier, BusinessCase.SESSION_TOP);
        if(null == shieldSetting) {
            return false;
        }

        return 1 == shieldSetting.mValue;
    }

    @Nullable
    public ConfigSetting getSetting(String sessionId, BusinessCase businessCase) {
        Set<ConfigSetting> configs = mSessionConfigsMap.get(sessionId);
        if(null == configs) {
            return null;
        }

        for(ConfigSetting setting: configs) {
            if(businessCase == setting.mBusinessCase) {
                return setting;
            }
        }

        return null;
    }



    @Nullable
    public ConfigSetting getSettingCheck(String sessionId, BusinessCase businessCase) {
        checkSessionConfigData();
        return getSetting(sessionId, businessCase);

    }

    public ConcurrentHashMap<String, Set<ConfigSetting>> queryAllSessionSettingsLocalSync() {
        checkSessionConfigData();
        return mSessionConfigsMap;
    }

    public void insertSettingCheck(List<ConfigSetting> configSettings) {
        checkSessionConfigData();

        for(ConfigSetting configSetting: configSettings) {

            Set<ConfigSetting> configs = mSessionConfigsMap.get(configSetting.mSourceId);
            if(null == configs) {
                configs = new HashSet<>();
                mSessionConfigsMap.put(configSetting.mSourceId, configs);
            }

            configs.remove(configSetting);
            configs.add(configSetting);
        }

    }

    public void insertSettingCheck(ConfigSetting configSetting) {
        insertSettingCheck(ListUtil.makeSingleList(configSetting));
    }

    public void checkSessionConfigData() {
        if(!mSessionConfigsMapLoadFromDb) {

            List<ConfigSetting> configSettings = ConfigSettingRepository.getSessionConfigSettings();
            for(ConfigSetting configSetting: configSettings) {
                Set<ConfigSetting> settings = mSessionConfigsMap.get(configSetting.mSourceId);
                if(null == settings) {
                    settings = new HashSet<>();
                    mSessionConfigsMap.put(configSetting.mSourceId, settings);
                }

                settings.add(configSetting);

            }

            mSessionConfigsMapLoadFromDb = true;
        }
    }




    public void addNotices(Set<String> unreadIdSet) {
        mNoticeList.addAll(unreadIdSet);
    }

    private void notice(final String chatUser, final ChatPostMessage message) {

        if(message.mTargetScope == ChatPostMessage.TARGET_SCOPE_NEWS_SUMMARY){
            return;
        }

        if(isAnnounceAppNotCheckDb(chatUser)) {
            return;
        }

        if (!message.needNotify()) {
            return;
        }

        //????????????????????????????????????(???????????????)
        if (!message.isEmergency() && mNoticeList.contains(message.deliveryId)) {
            return;
        }

        //?????????????????????????????????????????????pc?????????????????????????????????????????????????????????from??????????????????
        if (!TextUtils.isEmpty(message.from) && message.from.equalsIgnoreCase(LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext))) {
            return;
        }
        mNoticeList.add(message.deliveryId);

        checkSessionConfigData();

        boolean forceShowNotification = false;
        if (message instanceof IAtContactMessage) {
            forceShowNotification = ((IAtContactMessage) message).isAtMe(BaseApplicationLike.baseContext);
        }

        if (message.isEmergency()
                || forceShowNotification
                || !isShield(chatUser)) {

            Session session = getSessionSafely(chatUser, message);

            if (session == null) {
                return;
            }

            MessageNoticeManager.getInstance().
                    showChatMsgNotification(BaseApplicationLike.baseContext, message, session, forceShowNotification);
        }
    }


    /**
     * ??????SESSION???????????????
     */
    public void readSpecialSession(Context context, final Session session) {

        //?????????????????????????????????????????????
        if (session.getUnread() <= 0) {
            IntentUtil.setBadge(context);
            return;
        }
        session.clearUnread();
        if (session.savedToDb) {
            updateSessionToDB(session);
        }
        IntentUtil.setBadge(context);
    }

    public void emptySessionUnread(Context context, Session session) {
        if (session.getUnread() <= 0) {
            IntentUtil.setBadge(context);
            return;
        }
        session.clearUnread();
        if (session.savedToDb) {
            SessionRepository.getInstance().updateSession(session);
        }
        setSessionList(ChatService.queryAllSessionsDb());

        IntentUtil.setBadge(context);
    }


    private void updateSessionToDB(Session session) {
        ChatDaoService.getInstance().sessionRefresh(session);
    }


    @Nullable
    private Session getSessionFromList(String sessionId) {
        for (Session session : mSessionList) {

            if (sessionId.equals(session.identifier)) {

                return session;
            }
        }

        return null;

    }


    public boolean shouldUpdateApplyInfo(OrgNotifyMessage orgNotifyMessage) {
        List<String> adminOrgCodeList = OrganizationRepository.getInstance().queryLoginAdminOrgSync(BaseApplicationLike.baseContext);

        return !(!ListUtil.isEmpty(adminOrgCodeList) && !adminOrgCodeList.contains(orgNotifyMessage.mOrgCode));

    }




    @Override
    @Nullable
    public Session buildP2pSession(ChatPostMessage chatPostMessage, boolean isCameFromOnline) {
        if(!chatPostMessage.isLegalP2pUserChat(AtworkApplicationLike.baseContext)) {
            return null;
        }

        final SessionType sessionType = SessionType.User;
        final UserHandleBasic chatUser = ChatMessageHelper.getChatUser(chatPostMessage);


        //?????????????????????
        if (MessageCache.getInstance().isMessageShouldNotReceive(chatUser.mUserId, chatPostMessage)) {
            return null;
        }

        String sessionName = null;

        //???????????????????????????, ?????????????????????, ??????????????? mDisplayName mMyName, ??????????????????????????????
        if (isCameFromOnline) {
            if (LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext).equals(chatPostMessage.from)) {
                sessionName = chatPostMessage.mDisplayName;
            } else {
                sessionName = chatPostMessage.mMyName;
            }
        }

        if (StringUtils.isEmpty(sessionName)) {
            User user = UserManager.getInstance().queryUserInSyncByUserId(BaseApplicationLike.baseContext, chatUser.mUserId, chatUser.mDomainId);
            if (null != user) {
                sessionName = user.getShowName();
            }
        }

        EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest()
                .setChatType(sessionType)
                .setName(sessionName)
                .setIdentifier(chatUser.mUserId)
                .setDomainId(chatUser.mDomainId)
                .setMessage(chatPostMessage)
                .setUpdateDb(false);

        Session session = entrySessionSafely(entrySessionRequest);
        return session;
    }


    @Override
    @Nullable
    public Session buildWorkplusAssetNotifySession(ChatPostMessage chatPostMessage) {
        final UserHandleBasic chatUser = ChatMessageHelper.getChatUser(chatPostMessage);

        //?????????????????????
        if (MessageCache.getInstance().isMessageShouldNotReceive(chatUser.mUserId, chatPostMessage)) {
            return null;
        }

        String sessionName = StringConstants.SESSION_NAME_ASSET_NOTICE;

        EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest()
                .setChatType(SessionType.Notice)
                .setName(sessionName)
                .setIdentifier(Session.ASSET_NOTIFY_SYSTEM)
                .setDomainId(chatPostMessage.mFromDomain)
                .setUpdateDb(false);


        //????????????
        Session session = ChatSessionDataWrap.getInstance().entrySessionSafely(entrySessionRequest);
        return session;
    }


    @Override
    @Nullable
    public Session buildDiscussionSession(final ChatPostMessage chatPostMessage, final boolean isCameFromOnline) {
        final SessionType sessionType = SessionType.Discussion;
        final UserHandleBasic chatUser = ChatMessageHelper.getChatUser(chatPostMessage);

        //?????????????????????
        if (MessageCache.getInstance().isMessageShouldNotReceive(chatUser.mUserId, chatPostMessage)) {
            return null;
        }

        //???????????????????????????(??? voip ??????),?????????????????????SESSION???????????????????????????,??????????????????
        if (handleSystemChatMessageNoSession(chatPostMessage, chatUser, isCameFromOnline)) {
            return null;
        }

        String name = null;
        //??????????????? displayName
        if (isCameFromOnline) {
            name = chatPostMessage.mDisplayName;
        }

        boolean shouldHandleSession = true;

        if (StringUtils.isEmpty(name)) {
            Discussion discussion = null;
            MultiResult<Discussion> multiResult = DiscussionManager.getInstance().queryDiscussionResultSync(BaseApplicationLike.baseContext, chatUser.mUserId, false, false);

            if (null != multiResult.localResult) {
                discussion = multiResult.localResult;

            } else {
                if (multiResult.httpResult.isRequestSuccess()) {
                    discussion = ((QueryDiscussionResponseJson) multiResult.httpResult.resultResponse).discussion;
                } else {
                    //???????????????, ??????????????? session ?????????
                    if (isDiscussionNotExist(multiResult)) {
                        shouldHandleSession = false;
                    }

                }

            }

            if (null != discussion) {
                name = discussion.mName;
            }
        }


        Session session = null;

        if (shouldHandleSession) {
            EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest()
                    .setChatType(sessionType)
                    .setName(name)
                    .setIdentifier(chatUser.mUserId)
                    .setDomainId(chatUser.mDomainId)
                    .setMessage(chatPostMessage)
                    .setUpdateDb(false);

            session = entrySessionSafely(entrySessionRequest);


        }

        return session;
    }


    @Override
    @Nullable
    public Session buildMeetingNotifySession(ChatPostMessage chatPostMessage) {
        MeetingNoticeChatMessage meetingNoticeChatMessage = (MeetingNoticeChatMessage) chatPostMessage;
        final UserHandleBasic chatUser = ChatMessageHelper.getChatUser(chatPostMessage);

        //?????????????????????
        if (MessageCache.getInstance().isMessageShouldNotReceive(chatUser.mUserId, chatPostMessage)) {
            return null;
        }

        String sessionName = meetingNoticeChatMessage.mDisplayName;

        EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest()
                .setChatType(SessionType.Notice)
                .setName(sessionName)
                .setMessage(chatPostMessage)
                .setIdentifier(Session.WORKPLUS_MEETING)
                .setDomainId(chatPostMessage.mFromDomain)
                .setUpdateDb(false);

        if (!StringUtils.isEmpty(meetingNoticeChatMessage.mDisplayAvatar)) {
            entrySessionRequest.setAvatar(chatPostMessage.mDisplayAvatar);
        }

        //????????????
        Session session = ChatSessionDataWrap.getInstance().entrySessionSafely(entrySessionRequest);
        return session;
    }



    @Override
    @Nullable
    public Session buildNotifySystemSession(ChatPostMessage chatPostMessage) {
        final UserHandleBasic chatUser = ChatMessageHelper.getChatUser(chatPostMessage);

        //?????????????????????
        if (MessageCache.getInstance().isMessageShouldNotReceive(chatUser.mUserId, chatPostMessage)) {
            return null;
        }
        String sessionName = StringConstants.SESSION_NAME_SYSTEM_NOTICE;

        EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest()
                .setChatType(SessionType.Notice)
                .setName(sessionName)
                .setIdentifier(Session.WORKPLUS_SYSTEM)
                .setDomainId(chatPostMessage.mFromDomain)
                .setOrgId(chatPostMessage.mOrgId)
                .setUpdateDb(false);
        //????????????
        Session session = ChatSessionDataWrap.getInstance().entrySessionSafely(entrySessionRequest);
        return session;
    }


    @Override
    @Nullable
    public Session buildAppSession(ChatPostMessage chatPostMessage) {
        final UserHandleBasic chatUser = ChatMessageHelper.getChatUser(chatPostMessage);

        App app = AppManager.getInstance().queryAppSync(BaseApplicationLike.baseContext, chatUser.mUserId, chatPostMessage.mOrgId);
        if (null == app) return null;

        Session session;
        SessionType sessionType;

        if (AppKind.LightApp.equals(app.mAppKind)) {
            sessionType = SessionType.LightApp;

        } else {
            sessionType = SessionType.Service;
        }

        EntrySessionRequest entrySessionRequest = EntrySessionRequest.newRequest(sessionType, app)
                .setOrgId(app.mOrgId)
                .setMessage(chatPostMessage)
                .setUpdateDb(false);
        session = ChatSessionDataWrap.getInstance().entrySessionSafely(entrySessionRequest);

        session.lastMessageText = chatPostMessage.getSessionShowTitle();
        session.lastTimestamp = chatPostMessage.deliveryTime;
        session.setLastMessageStatus(AtworkApplicationLike.baseContext, chatPostMessage);
        session.lastMessageId = chatPostMessage.deliveryId;
        session.orgId = chatPostMessage.mOrgId;
        return session;
    }


}
