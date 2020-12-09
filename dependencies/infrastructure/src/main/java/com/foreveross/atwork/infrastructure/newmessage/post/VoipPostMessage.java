package com.foreveross.atwork.infrastructure.newmessage.post;

import android.content.Context;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.model.voip.MeetingInfo;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.voip.GateWay;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.VoipSdkType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dasunsy on 16/7/14.
 */
public class VoipPostMessage extends PostTypeMessage {

    public static final String OPERATION = "operation";

    public static final String OPERATOR = "operator";

    public static final String CREATOR = "creator";

    public static final String GATEWAY = "gateway";

    public static final String MEMBERS = "members";

    public static final String SESSION_INFO = "conversation";

    /**
     * 后台生成的会议 id, 区别于第三方自己的会议 id
     * */
    public String mMeetingId;

    public GateWay mGateWay;

    public UserHandleInfo mCreator;

    public Operation mOperation;

    public VoipMeetingMember mOperator;

    public MeetingInfo mMeetingInfo;

    public List<VoipMeetingMember> mMemberList;

    /**
     * 消息是否已读
     */
    public ReadStatus read = ReadStatus.Unread;

    public String mDeviceId;


    public String getSessionChatId() {

        if(null != mMeetingInfo) {

            if(MeetingInfo.Type.DISCUSSION == mMeetingInfo.mType) {
                return mMeetingInfo.mId;
            }

            if(MeetingInfo.Type.USER == mMeetingInfo.mType) {
                Set<String> memberIdSet = new HashSet<>();
                if(null != mCreator) {
                    memberIdSet.add(mCreator.mUserId);
                }

                memberIdSet.add(mMeetingInfo.mId);


                for(String memberId: memberIdSet) {
                    if(!User.isYou(BaseApplicationLike.baseContext, memberId)) {
                        return memberId;
                    }
                }
            }
        }


        return from;

    }

    public boolean isLegal() {
        //未知类型的 voip, 不做任何处理
        if(null == mGateWay || VoipSdkType.UNKNOWN == mGateWay.mType) {
            return false;
        }

        return true;
    }

    public boolean isSdkTypeSupportInstant() {
        return isSdkBasedType() || isZoomProduct();
    }

    public boolean isZoomProduct() {
        return isZoomProduct(mGateWay.mType);
    }

    public static boolean isZoomProduct(VoipSdkType voipSdkType) {
        return VoipSdkType.BIZCONF == voipSdkType || VoipSdkType.ZOOM == voipSdkType;
    }



    public boolean isSdkBasedType() {
        return VoipSdkType.AGORA == mGateWay.mType || VoipSdkType.QSY == mGateWay.mType;
    }

    public static VoipPostMessage getVoipPostMessageFromJson(Map<String, Object> jsonMap) {
        VoipPostMessage voipPostMessage = new VoipPostMessage();
        voipPostMessage.initPostTypeMessageValue(jsonMap);

        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);
        voipPostMessage.mOperation = Operation.fromStringValue((String) bodyMap.get(OPERATION));
        voipPostMessage.mMeetingId = voipPostMessage.to;

        voipPostMessage.mOperator = VoipMeetingMember.getMeetingMember(bodyMap.get(OPERATOR), voipPostMessage.mMeetingId);
        voipPostMessage.mCreator = UserHandleInfo.getUserHandleInfo(bodyMap.get(CREATOR));
        voipPostMessage.mMemberList = VoipMeetingMember.getMeetingMemberList(bodyMap.get(MEMBERS), voipPostMessage.mMeetingId);
        voipPostMessage.mGateWay = GateWay.getGateWay(bodyMap.get(GATEWAY));
        voipPostMessage.mMeetingInfo = MeetingInfo.getMeetingInfo(bodyMap.get(SESSION_INFO));
        if (bodyMap.containsKey(SOURCE)) {
            voipPostMessage.source = (String)bodyMap.get(SOURCE);
        }
        voipPostMessage.mDeviceId = ChatPostMessage.getString(bodyMap, DEVICE_ID);

        return voipPostMessage;
    }

    @Override
    public Map<String, Object> getChatBody() {
        return new HashMap<>();
    }


    /**
     * 是否包含自己
     *
     * @param context
     * @return boolean
     * */
    public boolean membersContainsMe(Context context) {
        String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        for(VoipMeetingMember members : mMemberList) {
            if(members.mUserId.equals(meUserId)) {
                return true;
            }
        }

        return false;
    }

    public enum Operation {

        /**
         * 创建会议
         * */
        CREATED,

        /**
         * 离开会议
         * */
        MEMBER_LEAVED,

        /**
         * 邀请加入会议
         * */
        MEMBER_INVITED,

        /**
         * 已经加入会议
         * */
        MEMBER_JOINED,

        /**
         * 挂断电话
         * */
        MEMBER_REJECTED,

        /**
         * 会议还没通的时候结束
         * */
        ENDED,

        /**
         * 繁忙
         * */
        MEMBER_BUSY,

        /**
         * 未知类型
         * */
        UNKNOWN;


        public static Operation fromStringValue(String value) {

            if ("CREATED".equalsIgnoreCase(value)) {
                return CREATED;
            }

            if("MEMBER_LEAVED".equalsIgnoreCase(value)) {
                return MEMBER_LEAVED;
            }

            if("MEMBER_INVITED".equalsIgnoreCase(value)) {
                return MEMBER_INVITED;
            }

            if("MEMBER_REJECTED".equalsIgnoreCase(value)) {
                return MEMBER_REJECTED;
            }

            if("ENDED".equalsIgnoreCase(value)) {
                return ENDED;
            }

            if("MEMBER_BUSY".equalsIgnoreCase(value)) {
                return MEMBER_BUSY;
            }

            if("MEMBER_JOINED".equalsIgnoreCase(value)) {
                return MEMBER_JOINED;
            }

            return UNKNOWN;
        }
    }
}
