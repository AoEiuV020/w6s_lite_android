package com.foreveross.atwork.modules.chat.component.chat;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.foreverht.workplus.ui.component.foregroundview.IForegroundView;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.chat.MeetingNoticeChatMessage;
import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.shared.ReadAckPersonShareInfo;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.modules.discussion.manager.DiscussionManager;
import com.foreveross.atwork.modules.chat.component.ChatDetailItemDataRefresh;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.inter.ChatItemClickListener;
import com.foreveross.atwork.modules.chat.inter.ChatItemLongClickListener;
import com.foreveross.atwork.modules.chat.inter.HasChatItemClickListener;
import com.foreveross.atwork.modules.chat.inter.HasChatItemLongClickListener;
import com.foreveross.atwork.modules.chat.inter.SkinModeUpdater;
import com.foreveross.atwork.modules.chat.presenter.IChatViewRefreshUIPresenter;
import com.foreveross.atwork.modules.chat.util.BurnModeHelper;
import com.foreveross.atwork.modules.file.service.FileTransferService;
import com.foreveross.atwork.utils.ChatMessageHelper;

import java.util.ArrayList;
import java.util.List;

import kotlin.collections.CollectionsKt;

/**
 * Created by dasunsy on 2017/8/29.
 */

public abstract class BasicChatItemView extends RelativeLayout implements SkinModeUpdater, HasChatItemLongClickListener, HasChatItemClickListener, ChatDetailItemDataRefresh {


    protected ChatItemLongClickListener mChatItemLongClickListener;
    protected ChatItemClickListener mChatItemClickListener;
    private List<ChatPostMessage> mMessagesContext;
    protected IChatViewRefreshUIPresenter mChatViewRefreshUIPresenter;

    public BasicChatItemView(Context context) {
        super(context);
    }

    public BasicChatItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected abstract View getMessageRootView();

    protected abstract ImageView getAvatarView();

    protected abstract MessageSourceView getMessageSourceView();

    protected abstract ChatPostMessage getMessage();

    protected void refreshAvatar(ChatPostMessage message) {

    }

    private static String sMsgIdNeedMask;
    private static List<String> sMsgIdsNeedBlink = new ArrayList();

    public static void setMsgIdNeedMask(String msgIdNeedMask) {
        BasicChatItemView.sMsgIdNeedMask = msgIdNeedMask;
    }

    public static void addMsgIdNeedBlink(String msgIdNeedBlink) {
        sMsgIdsNeedBlink.add(msgIdNeedBlink);
    }

    public static void clearMsgIdNeedMask() {
        BasicChatItemView.sMsgIdNeedMask = null;
    }

    @Override
    public String getMsgId() {
        if (null != getMessage()) {
            return getMessage().deliveryId;
        }

        return null;
    }

    @Override
    public void refreshItemView(ChatPostMessage message) {

        if (shouldHoldAvatarView(message)) {
            int width = DensityUtil.dip2px(40);
            ViewUtil.setWidth(getAvatarView(), width);

            if (shouldShowAvatarInDiscussionChat(message)) {
                getAvatarView().setVisibility(VISIBLE);
            } else {
                getAvatarView().setVisibility(INVISIBLE);

            }

        } else {

            ViewUtil.setWidth(getAvatarView(), 0);

        }

        if (shouldCompressMessageContentDivider(message)) {
            int dp5 = DensityUtil.dip2px(5);
            if (dp5 != getMessageRootView().getPaddingBottom()) {
                getMessageRootView().setPadding(getMessageRootView().getPaddingLeft(), getMessageRootView().getPaddingTop(), getMessageRootView().getPaddingRight(), dp5);
            }

        } else {
            int dp20 = DensityUtil.dip2px(20);
            if (dp20 != getMessageRootView().getPaddingBottom()) {
                getMessageRootView().setPadding(getMessageRootView().getPaddingLeft(), getMessageRootView().getPaddingTop(), getMessageRootView().getPaddingRight(), dp20);
            }
        }

        refreshAvatar(message);

        refreshMessageSource(message);

        refreshSomeStatus(message);

        handlePresenter(message);

        handleMaskForeground();
        blinkForeground();

    }

    protected void refreshSomeStatus(ChatPostMessage message) {
        SomeStatusView someStatusView = getSomeStatusView();
        if(null == someStatusView) {
            return;
        }

        someStatusView.setContentMessage(message);

        someStatusView.handleSomeStatusInfoFloat();

        if(null != someStatusView.getTvTime()) {
            someStatusView.getTvTime().setText(TimeUtil.getStringForMillis(message.deliveryTime, TimeUtil.TIME_ONLY));
        }


        refreshIvStatus(message, someStatusView);

    }

    private void refreshIvStatus(ChatPostMessage message, SomeStatusView someStatusView) {
        ImageView ivStatus = someStatusView.getIvStatus();

        if(null != ivStatus) {

            ivStatus.setTag(message.deliveryId);

            Boolean result = messageReadStatusIllegal(message);

            if(null != result) {
                if (result) {
                    ivStatus.setVisibility(GONE);
                } else {
                    setSessionTickStatusUI(message, someStatusView);
                }

                return;
            }


            if(message.isFromDiscussionChat()) {
                String sessionChatId = ChatMessageHelper.getChatUserSessionId(message);
                DiscussionManager.getInstance().touchDiscussionReadFeatureThreshold(getContext(), sessionChatId, isTouch -> {


                    if(!message.deliveryId.equals(ivStatus.getTag())) {
                        return;
                    }

                    if(isTouch) {
                        ivStatus.setVisibility(GONE);
                        return;
                    }

                    setSessionTickStatusUI(message, someStatusView);
                });

            }
        }
    }

    private void setSessionTickStatusUI(ChatPostMessage message, SomeStatusView someStatusView) {


        String sessionChatId = ChatMessageHelper.getChatUserSessionId(message);
        ImageView ivStatus = someStatusView.getIvStatus();

        ReadAckPersonShareInfo.ReadAckInfo readAckInfo = ChatMessageHelper.getReadAckInfoAndCheck(getContext(), sessionChatId);

        if(null == readAckInfo || message.deliveryTime <= readAckInfo.getTargetReadTimeOldDataLine()) {
            ivStatus.setVisibility(GONE);
            return;
        }

        if(readAckInfo.getTargetReadTime() >= message.deliveryTime) {
            ivStatus.setImageResource(someStatusView.getIconDoubleTick());
            ivStatus.setVisibility(VISIBLE);
            return;
        }

        ivStatus.setImageResource(someStatusView.getIconOneTick());
        ivStatus.setVisibility(VISIBLE);

        someStatusView.getLlSomeStatusInfo().setVisibility(View.VISIBLE);
    }


    private Boolean messageReadStatusIllegal(ChatPostMessage message) {

        if(!User.isYou(getContext(), message.from)) {
            return true;
        }

        if(FileTransferService.INSTANCE.needVariation(message)) {
            return true;
        }


        if(message.isFromUserChat()) {
            return false;
        }



        if(message.isFromDiscussionChat()) {
            return null;
        }

        return true;
    }


    @Nullable
    protected SomeStatusView getSomeStatusView() {
        return null;
    }


    protected void handlePresenter(ChatPostMessage message) {
        if(null != mChatViewRefreshUIPresenter) {
            mChatViewRefreshUIPresenter.refreshItemView(message);
        }
    }

    protected void handleMaskForeground() {
        ChatPostMessage message = getMessage();
        if (null == message) {
            return;
        }

        View contentRootView = getContentBlinkView();

        if (!(contentRootView instanceof IForegroundView)) {
            return;
        }

        if (message.deliveryId.equals(sMsgIdNeedMask)) {

            ((IForegroundView) contentRootView).setForeground(getBlinkDrawable());

        } else {

            ((IForegroundView) contentRootView).setForeground(null);

        }
    }


    private void blinkForeground() {
        ChatPostMessage message = getMessage();
        if (null == message) {
            return;
        }

        View contentRootView = getContentBlinkView();

        if (!(contentRootView instanceof IForegroundView)) {
            return;
        }

        if (sMsgIdsNeedBlink.contains(message.deliveryId)) {
            ((IForegroundView) contentRootView).setForeground(getBlinkDrawable());
            contentRootView.animate()
                    .setDuration(300)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            ((IForegroundView) contentRootView).setForeground(null);
                            sMsgIdsNeedBlink.remove(message.deliveryId);

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            ((IForegroundView) contentRootView).setForeground(null);
                            sMsgIdsNeedBlink.remove(message.deliveryId);

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    })
                    .start();

        } else {

            contentRootView.animate().cancel();
        }

    }



    protected boolean shouldHoldAvatarView(ChatPostMessage message) {
        return message.isFromDiscussionChat();
    }

    @Override
    public void refreshMessagesContext(List<ChatPostMessage> messages) {
        mMessagesContext = messages;
    }


    public boolean shouldShowAvatarInDiscussionChat(ChatPostMessage message) {
        if (!message.isFromDiscussionChat()) {
            return false;
        }

        if (ListUtil.isEmpty(mMessagesContext)) {
            return true;
        }

        int position = mMessagesContext.indexOf(message);
        ChatPostMessage lastMessage = null;
        if (-1 != position) {
            lastMessage = CollectionsKt.getOrNull(mMessagesContext, position - 1);
        }

        if (null == lastMessage) {
            return true;
        }

        if(lastMessage.isUndo()) {
            return true;
        }

        if ((lastMessage instanceof SystemChatMessage)) {
            return true;
        }

        if (lastMessage.from.equals(message.from)) {
            return false;
        }

        return true;
    }

    protected boolean shouldCompressMessageContentDivider(ChatPostMessage message) {
        if (ListUtil.isEmpty(mMessagesContext)) {
            return false;
        }

        int position = mMessagesContext.indexOf(message);
        ChatPostMessage nextMessage = null;
        if (-1 != position) {
            nextMessage = CollectionsKt.getOrNull(mMessagesContext, position + 1);
        }

        if (null == nextMessage) {
            return false;
        }

        if ((nextMessage instanceof SystemChatMessage)) {
            return false;
        }

        if(nextMessage instanceof MeetingNoticeChatMessage) {
            return false;
        }

        if (nextMessage.from.equals(message.from)) {
            return true;
        }

        return false;
    }

    /**
     * 根据需要刷新消息来源 UI
     */
    private void refreshMessageSource(ChatPostMessage message) {
        MessageSourceView messageSourceView = getMessageSourceView();
        if (null != messageSourceView) {
            messageSourceView.refreshMsgSyncView(message);
        }
    }

    @Override
    public void refreshSkinUI() {
        if (BurnModeHelper.isBurnMode()) {
            burnSkin();
        } else {
            themeSkin();
        }
    }

    /**
     * 阅后即焚模式下的 ui
     */
    protected void burnSkin() {
        MessageSourceView messageSourceView = getMessageSourceView();
        if (null != messageSourceView) {
            messageSourceView.burnSkin();
        }
    }

    /**
     * 普通换肤模式下的 ui
     */
    protected void themeSkin() {
        MessageSourceView messageSourceView = getMessageSourceView();
        if (null != messageSourceView) {
            messageSourceView.themeSkin();
        }

    }


    @Override
    public void setChatItemLongClickListener(ChatItemLongClickListener chatItemLongClickListener) {
        this.mChatItemLongClickListener = chatItemLongClickListener;
    }

    @Override
    public void setChatItemClickListener(ChatItemClickListener chatItemClickListener) {
        this.mChatItemClickListener = chatItemClickListener;
    }

    protected abstract @NonNull View getContentRootView();

    @NonNull
    protected  View getContentBlinkView() {
        return getContentRootView();
    }


    protected Drawable getBlinkDrawable() {
        return ContextCompat.getDrawable(getContext(), R.drawable.shape_chat_message_blink_with_no_corners);
    }

    public AnchorInfo getAnchorInfo() {
        // getContentRootView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int[] location = new int[2];
        //获取mLlTextContent在屏幕中的位置
        getContentRootView().getLocationOnScreen(location);
        //View的面积高度
        int chatViewHeight;
        chatViewHeight = getContentRootView().getHeight();
        int anchorHeight = location[1];

        AnchorInfo info = new AnchorInfo();
        info.anchorHeight = anchorHeight;
        info.chatViewHeight = chatViewHeight;


        return info;
    }


}
