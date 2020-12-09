package com.foreveross.atwork.modules.chat.component;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.foreverht.cache.DiscussionCache;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.discussion.DiscussionAsyncNetService;
import com.foreveross.atwork.component.NewMessageTipView;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionType;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.FriendNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.OrgNotifyMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.ReadAckPersonShareInfo;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.data.ChatSessionDataWrap;
import com.foreveross.atwork.modules.chat.util.SessionRefreshHelper;
import com.foreveross.atwork.modules.configSettings.manager.ConfigSettingsManager;
import com.foreveross.atwork.modules.contact.util.ContactInfoViewUtil;
import com.foreveross.atwork.modules.discussion.util.DiscussionUIHelper;
import com.foreveross.atwork.modules.discussion.util.LabelViewWrapper;
import com.foreveross.atwork.modules.file.service.FileTransferService;
import com.foreveross.atwork.utils.AtworkUtil;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.foreveross.atwork.utils.ErrorHandleUtil;
import com.foreveross.atwork.utils.TimeViewUtil;
import com.foreveross.atwork.utils.WorkplusTextSizeChangeHelper;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import kotlin.Unit;


public class SessionItemView extends RelativeLayout {

    private View mVRoot;
    private ImageView mIvChatItemAvatar;
    private RelativeLayout mRlTitleArea;
    private TextView mTvChatItemTitle;
    private TextView mTvSessionContent, mTvChatItemPersonalSignature;
    private TextView mChatItemTime;
    private NewMessageTipView mNewMessageNumber;

    private ImageView mStatusImageView;

    private TextView mStatusTextView;

    private ImageView mIvNotifySilence;
    private ImageView mIvTopStick;
    private FrameLayout mFlLabel;
    private TextView mTvLabel;
    private ImageView mIvLabel;
    private ImageView mIvBioAuthProtected;
    private ImageView mIvSessionSomeStatus;

    private View mVLine;

    private Session mSession;


    private Context mContext;

    public SessionItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        WorkplusTextSizeChangeHelper.handleHeightEnlargedTextSizeStatus(mVRoot);
        WorkplusTextSizeChangeHelper.handleViewEnlargedTextSizeStatus(mIvChatItemAvatar, 1.1f);

    }

    public SessionItemView(Context context) {
        super(context);
        initView();
        WorkplusTextSizeChangeHelper.handleHeightEnlargedTextSizeStatus(mVRoot);
        WorkplusTextSizeChangeHelper.handleViewEnlargedTextSizeStatus(mIvChatItemAvatar, 1.1f);

    }

    public void notShowUnread() {
        mNewMessageNumber.setVisibility(GONE);
    }

    /**
     * 显示UI
     */
    private void initView() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.component_chat_list_head_v2, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mIvChatItemAvatar = view.findViewById(R.id.chat_item_avatar);
        mRlTitleArea = view.findViewById(R.id.rl_title_area);
        mTvChatItemTitle = view.findViewById(R.id.chat_item_title);
        mTvSessionContent = view.findViewById(R.id.chat_item_last_message);
        mTvChatItemPersonalSignature = view.findViewById(R.id.chat_item_personal_signature);
        mChatItemTime = view.findViewById(R.id.chat_item_time);
        mNewMessageNumber = view.findViewById(R.id.chat_item_new_messages_count);
        mStatusImageView = view.findViewById(R.id.chat_item_status_image);
        mStatusTextView = view.findViewById(R.id.chat_item_status_text);
        mIvNotifySilence = view.findViewById(R.id.iv_notify_silence);
        mIvTopStick = view.findViewById(R.id.iv_top_stick);
        mFlLabel = view.findViewById(R.id.fl_discussion_label_in_basic_info_area);
        mTvLabel = view.findViewById(R.id.tv_discussion_label_in_basic_info_area);
        mIvLabel = view.findViewById(R.id.iv_discussion_label_in_basic_info_area);
        mIvBioAuthProtected = view.findViewById(R.id.iv_bio_auth_protected);
        mIvSessionSomeStatus = view.findViewById(R.id.iv_session_some_status);
        mVLine = view.findViewById(R.id.iv_line_chat_list);

    }


    public void refresh(Session session) {
        boolean viewNeedReset = isViewNeedReset(session);

        this.mSession = session;

        setLastMessageContent(session);

        setLastMessageStatus();

        mNewMessageNumber.dotModel();

        mChatItemTime.setText(TimeViewUtil.getSimpleUserCanViewTime(getContext() , session.lastTimestamp));

        setTopView(session, viewNeedReset);

        setNewMessageNumView(session);


        setMainInfo(session, viewNeedReset);

        setViewByNotifySilence(session, viewNeedReset);


        setSessionSomeStatus(session);

        setDiscussionLabel(session);

    }

    private void setSessionSomeStatus(Session session) {
        mIvSessionSomeStatus.setTag(session.identifier);

        Boolean result = sessionReadStatusIllegal(session);
        if(null != result) {
            if (result) {
                mIvSessionSomeStatus.setVisibility(GONE);
            } else {
                setSessionTickStatusUI(session);
            }
            return;
        }



        if(session.isDiscussionType()) {

            DiscussionManager.getInstance().touchDiscussionReadFeatureThreshold(getContext(), session.identifier, isTouch -> {


                if(!session.identifier.equals(mIvSessionSomeStatus.getTag())) {
                    return;
                }

                if(isTouch) {
                    mIvSessionSomeStatus.setVisibility(GONE);
                    return;
                }

                setSessionTickStatusUI(session);
            });

            return;
        }

        mIvSessionSomeStatus.setVisibility(GONE);





    }

    private void setSessionTickStatusUI(Session session) {


        if(ChatStatus.Self_Send.equals(session.lastMessageStatus)) {

            ReadAckPersonShareInfo.ReadAckInfo readAckInfo = ChatMessageHelper.getReadAckInfoAndCheck(getContext(), session.identifier);

            if(null == readAckInfo || session.lastTimestamp <= readAckInfo.getTargetReadTimeOldDataLine()) {
                mIvSessionSomeStatus.setVisibility(GONE);
                return;
            }

            if(readAckInfo.getTargetReadTime() >= session.lastTimestamp) {
                mIvSessionSomeStatus.setImageResource(R.mipmap.icon_double_tick_blue);
                mIvSessionSomeStatus.setVisibility(VISIBLE);
                return;
            }

            mIvSessionSomeStatus.setImageResource(R.mipmap.icon_one_tick_blue);
            mIvSessionSomeStatus.setVisibility(VISIBLE);
            return;
        }


        if(ChatStatus.Peer_Read.equals(session.lastMessageStatus)) {
            mIvSessionSomeStatus.setImageResource(R.mipmap.icon_double_tick_blue);
            mIvSessionSomeStatus.setVisibility(VISIBLE);
            return;

        }

        mIvSessionSomeStatus.setVisibility(GONE);

    }

    private Boolean sessionReadStatusIllegal(Session session) {

        if(FileTransferService.INSTANCE.needVariation(session)) {
            return true;
        }

        if(null == session.lastMessageStatus) {
            return true;
        }

        if(session.isUserType()) {
            return false;
        }



        if(session.isDiscussionType()) {
            return null;
        }

        return true;
    }

    private void setMainInfo(Session session, boolean viewNeedReset) {


        if(shouldShowSignatureView(session)){
            mTvChatItemPersonalSignature.setVisibility(VISIBLE);
            ContactInfoViewUtil.dealWithSessionInitializedStatus(mIvChatItemAvatar, mTvChatItemTitle, mTvChatItemPersonalSignature, session, viewNeedReset);

        }else{
            mTvChatItemPersonalSignature.setVisibility(GONE);
            ContactInfoViewUtil.dealWithSessionInitializedStatus(mIvChatItemAvatar, mTvChatItemTitle, null, session, viewNeedReset);
        }
    }



    private boolean shouldShowSignatureView(Session session) {
        if(!DomainSettingsManager.getInstance().handlePersonalSignatureEnable()) {
            return false;
        }

        if(Session.COMPONENT_ANNOUNCE_APP.equals(session.identifier)) {
            return false;
        }


        if(!SessionType.User.equals(session.type)) {
            return false;
        }
        if(FileTransferService.INSTANCE.isClickFileTransfer(session)){
            return false;
        }


        return true;
    }





    private void setNewMessageNumView(Session session) {
        if (session.getUnread() > 0) {
            mNewMessageNumber.setVisibility(VISIBLE);
        } else {
            mNewMessageNumber.setVisibility(GONE);
        }

        if(shouldDotViewModel(session)) {
            mNewMessageNumber.dotModel();
            return;
        }

        if (Session.NewMessageType.RedDot.equals(session.newMessageType)) {
            mNewMessageNumber.dotModel();

        } else if (Session.NewMessageType.Icon.equals(session.newMessageType)) {
            mNewMessageNumber.imageModel(session.newMessageContent);
        } else {
            mNewMessageNumber.numberModel(session.getUnread());
        }

    }

    private boolean shouldDotViewModel(Session session) {

        if(Session.WORKPLUS_DISCUSSION_HELPER.equals(session.identifier)) {
            return true;
        }

        if(Session.COMPONENT_ANNOUNCE_APP.equals(session.identifier)) {
            return true;
        }

        if(Session.WORKPLUS_SUMMARY_HELPER.equals(session.identifier)) {
            return true;
        }

        boolean isShield = ChatSessionDataWrap.getInstance().isShield(session.identifier);
        if(isShield) {
            return true;
        }


        boolean isAnnounceApp = ChatSessionDataWrap.getInstance().isAnnounceAppNotCheckDb(session.identifier);
        if(isAnnounceApp) {
            return true;
        }

//        if(!PersonalShareInfo.getInstance().getSettingDiscussionHelper(AtworkApplicationLike.baseContext)) {
//            return true;
//        }
//
//        if(SessionType.Discussion == session.type) {
//            return false;
//        }

        return false;
    }

    private boolean isViewNeedReset(Session session) {
        boolean viewNeedReset = true;
        if(null != this.mSession && this.mSession.equals(session)) {
            viewNeedReset = false;
        }
        return viewNeedReset;
    }

    public void setDiscussionLabel(Session session) {

        String tag = UUID.randomUUID().toString();
        mFlLabel.setTag(tag);

        if(SessionType.Discussion.equals(session.type)) {


            Discussion discussion = DiscussionCache.getInstance().getDiscussionCache(session.identifier);
            if(null != discussion) {
                refreshDiscussionLabel(discussion);


            } else {
                setDiscussionLabelAsync(tag, session);
            }


        } else {
            hideLabelDiscussion();
        }
    }

    private void refreshDiscussionLabel(Discussion discussion) {
        DiscussionUIHelper.refreshLabel(discussion, new LabelViewWrapper(mFlLabel, mIvLabel, mTvLabel), (hasHandledIv, hasHandledTv) -> {
            setTitleViewMaxWidth(hasHandledIv, hasHandledTv);
            return Unit.INSTANCE;
        });
    }

    public void setDiscussionLabelAsync(String tag, Session session) {

        hideLabelDiscussion();

        DiscussionManager.getInstance().queryDiscussion(getContext(), session.identifier, new DiscussionAsyncNetService.OnQueryDiscussionListener() {
            @Override
            public void onSuccess(@NonNull Discussion discussion) {

                if(tag.equals(mFlLabel.getTag())) {
                    refreshDiscussionLabel(discussion);

                }
            }

            @Override
            public void networkFail(int errorCode, String errorMsg) {

                if(tag.equals(mFlLabel.getTag())) {
                    hideLabelDiscussion();

                }

                ErrorHandleUtil.handleTokenError(errorCode, errorMsg);

            }
        });
    }




    public void hideLabelDiscussion() {
        setTitleViewMaxWidth(false, false);
        mFlLabel.setVisibility(GONE);
    }

    public void setTitleViewMaxWidth(boolean showIconLabel, boolean showTextLabel) {

//        doSetTitleViewMaxWidth(showIconLabel, showTextLabel);
    }

    private void doSetTitleViewMaxWidth(boolean showIconLabel, boolean showTextLabel) {
//        int avatarWidth = mIvChatItemAvatar.getWidth() + DensityUtil.dip2px(getContext(), 10);
        int avatarWidth = DensityUtil.dip2px(48 + 10);

        int timeViewWidth = ViewUtil.getTextLength(mChatItemTime) + DensityUtil.dip2px(11);
        int labelWidth = 0;
        if (showIconLabel) {
            labelWidth = DensityUtil.dip2px(18);//"内部群label" icon 的宽度


        }

        if(showTextLabel) {
            labelWidth = ViewUtil.getTextLength(mTvLabel);
        }

        int maxWidth = ScreenUtils.getScreenWidth(getContext()) - labelWidth - avatarWidth - timeViewWidth - DensityUtil.dip2px(32 + 5); // "32"是左右总共的padding  5为微调空隙
        mTvChatItemTitle.setMaxWidth(maxWidth);
    }


    private void setTopView(Session session, boolean viewNeedReset) {

        String tag = UUID.randomUUID().toString();
        mIvTopStick.setTag(tag);


        if(session.isRemoteTop()) {
            mVRoot.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.common_bg_gray));
//            mIvTopStick.setVisibility(VISIBLE);
            return;
        }


        if(viewNeedReset) {
            mVRoot.setBackgroundResource(R.drawable.bg_item_common_selector);
//            mIvTopStick.setVisibility(GONE);
        }

        ConfigSettingsManager.INSTANCE.isSessionTop(session, isTop -> {
            if(tag.equals(mIvTopStick.getTag())) {
                if(isTop) {
                    mVRoot.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.common_bg_gray));

                } else {
                    mVRoot.setBackgroundResource(R.drawable.bg_item_common_selector);


                }
//                ViewUtil.setVisible(mIvTopStick, isTop);

            }

            return null;
        });
    }


    public void setViewByNotifySilence(Session session, boolean viewNeedReset) {
        String tag = UUID.randomUUID().toString();
        mIvNotifySilence.setTag(tag);


        if(ChatSessionDataWrap.getInstance().isAnnounceAppNotCheckDb(session.identifier)) {
            mIvNotifySilence.setVisibility(VISIBLE);
            return;
        }

        if(viewNeedReset) {
            mIvNotifySilence.setVisibility(GONE);
        }

        ConfigSettingsManager.INSTANCE.isSessionShield(session, isShield -> {
            if(tag.equals(mIvNotifySilence.getTag())) {
                ViewUtil.setVisible(mIvNotifySilence, isShield);

            }

            return null;
        });

    }

    private void setLastMessageStatus() {
        if(null == mSession.lastMessageStatus) {
            mStatusImageView.setVisibility(GONE);
            return;
        }

        if (ChatStatus.Sended.equals(mSession.lastMessageStatus)) {
            mStatusImageView.setVisibility(GONE);
            return;
        }

        if (ChatStatus.Not_Send.equals(mSession.lastMessageStatus)) {
            mStatusImageView.setImageResource(R.mipmap.icon_failure);
            mStatusImageView.setVisibility(VISIBLE);
            return;
        }

        if (ChatStatus.Sending.equals(mSession.lastMessageStatus)) {
            mStatusImageView.setImageResource(R.mipmap.message_sending);
            mStatusImageView.setVisibility(VISIBLE);
            return;
        }


        mStatusImageView.setVisibility(GONE);
        return;

    }

    private void setLastMessageContent(Session session) {
        mStatusTextView.setVisibility(GONE);


        //如果有@，显示@
        if (Session.ShowType.At.equals(session.lastMessageShowType)) {
            mStatusTextView.setVisibility(VISIBLE);
            mStatusTextView.setText(getResources().getString(R.string.at));

            if (!StringUtils.isEmpty(session.lastMessageText)) {
                setSessionLastMessageContent(session);
                mTvSessionContent.setTextColor(getSecondaryTextColor());
            } else {
                mTvSessionContent.setText(StringUtils.EMPTY);
                mTvSessionContent.setTextColor(getSecondaryTextColor());
            }

            return;
        }

        if(Session.ShowType.Emergency.equals(session.lastMessageShowType)) {
            mStatusTextView.setText(getResources().getString(R.string.emergency));

            mStatusTextView.setVisibility(VISIBLE);
            mTvSessionContent.setText(StringUtils.EMPTY);
            return;
        }


        if(Session.ShowType.RedEnvelope.equals(session.lastMessageShowType)) {
            setSessionLastMessageContent(session);
            mTvSessionContent.setTextColor(getTipsColor());
            return;
        }

        //如果有草稿，优先显示草稿
        if (!StringUtils.isEmpty(session.draft)) {
            mStatusTextView.setVisibility(VISIBLE);
            mStatusTextView.setText(getResources().getString(R.string.draft));
            mTvSessionContent.setText(session.draft);
            mTvSessionContent.setTextColor(getSecondaryTextColor());
            return;
        }

        //是否为服务号订阅消息
        if(Session.WORKPLUS_SUMMARY_HELPER.equals(session.identifier) && session.name != null) {
            //若最后为语音, 而且有未读时, 则改变颜色
            if (Session.ShowType.Audio.equals(session.lastMessageShowType)) {
                setTextColor(session.unreadMessageIdSet,session.lastMessageText);
            }else {
                mTvSessionContent.setTextColor(getSecondaryTextColor());
                mTvSessionContent.setText(session.lastMessageText);
            }
            return;
        }

        //如果没有草稿，也没有@，显示最后一条消息的内容
        if (!StringUtils.isEmpty(session.lastMessageText)) {

            //若最后为语音, 而且有未读时, 则改变颜色
            if (Session.ShowType.Audio.equals(session.lastMessageShowType)) {
                handleVoiceUI(session);

            } else {

                handleShowTypeTextUI(session);

            }
        } else {
            mTvSessionContent.setText(StringUtils.EMPTY);
            mTvSessionContent.setTextColor(getSecondaryTextColor());
        }

    }

    private void setSessionLastMessageContentConsiderSessionUnreadCount(Session session) {
//        if(Session.COMPONENT_ANNOUNCE_APP.equals(session.identifier)) {
//            setAnnounceAppFoldSessionLastMessageContent(session);
//            return;
//        }

        if(Session.WORKPLUS_SUMMARY_HELPER.equals(session.identifier)) {
            setAnnounceAppFoldSessionLastMessageContent(session);
            return;
        }

        if(1 < session.getUnread() && shouldDotViewModel(session)) {
            mTvSessionContent.setText(getContext().getString(R.string.shield_session_unread_prefix_tag, session.getUnread()) + AtworkUtil.getMessageTypeNameI18N(getContext(), session, session.lastMessageText));
            return;
        }

        setSessionLastMessageContent(session);
    }

    private void setSessionLastMessageContent(Session session) {
        mTvSessionContent.setText(AtworkUtil.getMessageTypeNameI18N(getContext(), session, session.lastMessageText));
    }

    private void setAnnounceAppFoldSessionLastMessageContent(Session session) {
        int announceAppSessionsUnreadSum = ChatSessionDataWrap.getInstance().getAnnounceAppSessionsUnreadSum();
        if(1 < announceAppSessionsUnreadSum && session.havingUnread()) {
            mTvSessionContent.setText(AtworkApplicationLike.baseContext.getString(R.string.shield_session_unread_prefix_tag, announceAppSessionsUnreadSum) + AtworkUtil.getMessageTypeNameI18N(getContext(), session, session.lastMessageText));
            return;
        }


        setSessionLastMessageContent(session);
    }


    private void handleVoiceUI(Session session) {
        final String tagKey = UUID.randomUUID().toString();
        mTvSessionContent.setTag(tagKey);

        setSessionLastMessageContent(session);

        ChatDaoService.getInstance().queryLatestMessage(mContext, session.identifier, chatPostMessageList -> {
            if (null != mTvSessionContent.getTag() && tagKey.equals(mTvSessionContent.getTag())) {

                if (isLastMessageLegalAndPlay(chatPostMessageList) && !isMyself(chatPostMessageList)) {
                    mTvSessionContent.setTextColor(getTipsColor());

                } else {
                    setSessionLastMessageContentConsiderSessionUnreadCount(session);
                    mTvSessionContent.setTextColor(getSecondaryTextColor());
                }
            }

        });
    }

    private void setTextColor(Set<String> unreadMessageIdSet, String textStr){
            if(unreadMessageIdSet.size() > 0){
                mTvSessionContent.setTextColor(getTipsColor());
                mTvSessionContent.setText(textStr);
            }else {
                mTvSessionContent.setTextColor(getSecondaryTextColor());
                mTvSessionContent.setText(textStr);
            }
    }

    private int getMainColor() {
        return Color.parseColor("#1A98FF");
    }

    private int getTipsColor() {
        return Color.parseColor("#FF5858");
    }

    private int getSecondaryTextColor() {
        return Color.parseColor("#888888");
    }

    /**
     * 控制普通的会话类型视图
     */
    private void handleShowTypeTextUI(Session session) {
        setSessionLastMessageContentConsiderSessionUnreadCount(session);

        //通知类型的需要局部凸显出颜色
        if (Session.WORKPLUS_SYSTEM.equals(session.identifier)) {
            mTvSessionContent.setTextColor(getSecondaryTextColor());

            SpannableString spannableString = new SpannableString(session.lastMessageText);
            String keyApprovedOrg = getContext().getString(R.string.tip_approved_to_join_org, "");
            if (session.lastMessageText.contains(keyApprovedOrg)) {

                spannableString.setSpan(new ForegroundColorSpan(getMainColor()), keyApprovedOrg.length(), session.lastMessageText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                mTvSessionContent.setText(spannableString);

            } else if (session.lastMessageText.endsWith(getContext().getString(R.string.tip_rejected_to_join_org_end_key))) {
                String[] words = session.lastMessageText.split(" ");
                if (words.length == 4) {
                    spannableString.setSpan(new ForegroundColorSpan(getMainColor()), 0, words[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new ForegroundColorSpan(getMainColor()), session.lastMessageText.indexOf(words[2]), session.lastMessageText.indexOf(words[3]) - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                mTvSessionContent.setText(spannableString);

            } else if (session.lastMessageText.endsWith(getContext().getString(R.string.tip_org_end_key))) {
                String[] words = session.lastMessageText.split(" ");
                if (words.length == 6) {
                    spannableString.setSpan(new ForegroundColorSpan(getMainColor()), 0, words[0].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new ForegroundColorSpan(getMainColor()), session.lastMessageText.indexOf(words[2]), session.lastMessageText.indexOf(words[3]) - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new ForegroundColorSpan(getMainColor()), session.lastMessageText.indexOf(words[4]), session.lastMessageText.indexOf(words[5]) - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                mTvSessionContent.setText(spannableString);

            }

        } else if (OrgNotifyMessage.FROM.equals(session.identifier)) {
            mTvSessionContent.setTextColor(getSecondaryTextColor());
            SessionRefreshHelper.setAndHighlightOrgApplyingView(mTvSessionContent, session.lastMessageText);

        } else if (FriendNotifyMessage.FROM.equals(session.identifier)) {
            mTvSessionContent.setTextColor(getSecondaryTextColor());
            SpannableString spannableString = new SpannableString(session.lastMessageText);
            String key = getContext().getString(R.string.tip_invite_friend, "");
            String keyMeAcceptHead = getContext().getString(R.string.me_accept_friend_tip_head);
            String keyMeAcceptTail = getContext().getString(R.string.me_accept_friend_tip_tail);
            if (session.lastMessageText.contains(key)) {
                int startKey = session.lastMessageText.indexOf(key);

                spannableString.setSpan(new ForegroundColorSpan(getMainColor()), 0, startKey, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                mTvSessionContent.setText(spannableString);

            } else if (session.lastMessageText.contains(keyMeAcceptHead) && session.lastMessageText.contains(keyMeAcceptTail)) {
                int endKeyHead = keyMeAcceptHead.length();
                int startKeyTail = session.lastMessageText.indexOf(keyMeAcceptTail);

                spannableString.setSpan(new ForegroundColorSpan(getMainColor()), endKeyHead, startKeyTail, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                mTvSessionContent.setText(spannableString);
            }


        } else {
            mTvSessionContent.setTextColor(getSecondaryTextColor());

        }
    }

    public void setLineVisible(boolean visible) {
        ViewUtil.setVisible(mVLine, visible);
    }


    private boolean isLastMessageLegalAndPlay(List<ChatPostMessage> chatPostMessageList) {
        return 0 < chatPostMessageList.size()
                && chatPostMessageList.get(0) instanceof VoiceChatMessage
                && !((VoiceChatMessage) chatPostMessageList.get(0)).play;
    }

    private boolean isMyself(List<ChatPostMessage> chatPostMessageList) {
        boolean result = true;
        if (0 < chatPostMessageList.size()) {
            ChatPostMessage message = chatPostMessageList.get(0);
            result = !TextUtils.isEmpty(message.from) && message.from.equalsIgnoreCase(LoginUserInfo.getInstance().getLoginUserId(getContext()));

        }
        return result;
    }



    public Session getSession() {
        return mSession;
    }


}
