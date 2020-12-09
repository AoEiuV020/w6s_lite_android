package com.foreveross.atwork.infrastructure.model.chat;

import android.content.Context;

import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.model.voip.CurrentVoipMeeting;
import com.foreveross.atwork.infrastructure.model.voip.MeetingStatus;
import com.foreveross.atwork.infrastructure.model.voip.VoipType;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.VoipPostMessage;
import com.foreveross.atwork.infrastructure.support.StringConstants;
import com.foreveross.atwork.infrastructure.support.VoipSdkType;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.UUID;

/**
 *
 * 用以单聊界面显示, 从 {@link com.foreveross.atwork.infrastructure.newmessage.post.VoipPostMessage} 转换过来<br/>
 *
 * Created by dasunsy on 16/8/10.
 */
public class VoipChatMessage extends ChatPostMessage {

    /**
     * "未接通, 点击重播", "通话时长: xxx" 等内容
     * */
    @Expose
    @SerializedName("content")
    public String mContent;

    @Expose
    @SerializedName("voip_type")
    public VoipType mVoipType;

    @Expose
    @SerializedName("status")
    public MeetingStatus mStatus;

    @Expose
    @SerializedName("sdk_type")
    public VoipSdkType mVoipSdkType = VoipSdkType.AGORA;

    public VoipChatMessage() {
        deliveryId = UUID.randomUUID().toString();

        chatStatus = ChatStatus.Sended;
    }

    public static VoipChatMessage newVoipChatMessage(Context context, String content, UserHandleInfo toUser, MeetingStatus status, CurrentVoipMeeting currentVoipMeeting) {
        VoipChatMessage voipChatMessage = new VoipChatMessage();

        voipChatMessage.deliveryId = UUID.randomUUID().toString();

        voipChatMessage.mContent = content;
        voipChatMessage.mVoipType = currentVoipMeeting.mVoipType;
        voipChatMessage.mStatus = status;

        voipChatMessage.to = toUser.mUserId;
        voipChatMessage.mToDomain = toUser.mDomainId;
        voipChatMessage.mToType = ParticipantType.User;

        if(voipChatMessage.to.equals(currentVoipMeeting.mCallPeer.mUserId)) {
            voipChatMessage.from = currentVoipMeeting.mCallSelf.mUserId;
            voipChatMessage.mFromDomain = currentVoipMeeting.mCallSelf.mDomainId;

        } else {
            voipChatMessage.from = currentVoipMeeting.mCallPeer.mUserId;
            voipChatMessage.mFromDomain = currentVoipMeeting.mCallPeer.mDomainId;
        }

        voipChatMessage.mFromType = ParticipantType.User;

        voipChatMessage.mBodyType = BodyType.Voip;

        if(User.isYou(context, voipChatMessage.from)) {
            voipChatMessage.chatSendType = ChatSendType.SENDER;
        } else {
            voipChatMessage.chatSendType = ChatSendType.RECEIVER;

        }

        voipChatMessage.deliveryTime = TimeUtil.getCurrentTimeInMillis();

        return voipChatMessage;
    }


    public static VoipChatMessage newVoipChatMessage(Context context, String content, UserHandleInfo toUser, MeetingStatus status, VoipPostMessage voipPostMessage) {
        VoipChatMessage voipChatMessage = new VoipChatMessage();

        voipChatMessage.deliveryId = voipPostMessage.deliveryId;

        voipChatMessage.mContent = content;
        voipChatMessage.mVoipType = voipPostMessage.mGateWay.mVoipType;
        voipChatMessage.mStatus = status;

        voipChatMessage.to = toUser.mUserId;
        voipChatMessage.mToDomain = toUser.mDomainId;
        voipChatMessage.mToType = ParticipantType.User;

        voipChatMessage.from = voipPostMessage.mCreator.mUserId;
        voipChatMessage.mFromDomain = voipPostMessage.mCreator.mDomainId;
        voipChatMessage.mFromType = ParticipantType.User;

        voipChatMessage.mBodyType = BodyType.Voip;

        if(User.isYou(context, voipChatMessage.from)) {
            voipChatMessage.chatSendType = ChatSendType.SENDER;
        } else {
            voipChatMessage.chatSendType = ChatSendType.RECEIVER;

        }

        voipChatMessage.deliveryTime = voipPostMessage.deliveryTime;

        return voipChatMessage;
    }

    public boolean isZoomProduct() {
        return VoipPostMessage.isZoomProduct(mVoipSdkType);
    }

    @Override
    public ChatType getChatType() {
        return ChatType.VOIP;

    }

    @Override
    public String getSessionShowTitle() {
        return StringConstants.SESSION_CONTENT_VOIP;
    }

    @Override
    public String getSearchAbleString() {
        return null;
    }

    @Override
    public boolean needNotify() {
        return false;
    }

    @Override
    public boolean needCount() {
        return ReadStatus.Unread.equals(read);
    }



    @Override
    public Map<String, Object> getChatBody() {
        return null;
    }
}
