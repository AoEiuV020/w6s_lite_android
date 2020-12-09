package com.foreveross.atwork.infrastructure.newmessage.post.notify;

import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.NotifyPostMessage;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

import java.util.Map;

/**
 * Created by dasunsy on 2017/11/13.
 */

public class MeetingNotifyMessage extends NotifyPostMessage {
    public static String FROM = "MEETING_HELPER";

    public static final String TYPE = "gateway";

    public  static final String MEETING_ID = "meeting_id";

    public  static final String MEETING_TITLE = "meeting_title";

    public  static final String MEETING_HOST = "meeting_host";

    public  static final String MEETING_TIME = "meeting_time";

    public  static final String MEETING_PARTICIPANT = "meeting_participant";

    public static final String URL = "url";

    public  static final String HOST_PASSWORD = "host_password";

    public static final String HOST_JOIN_URL = "host_join_url";

    public static final String ATTENDEE_PASSWORD = "attendee_password";

    public static final String ATTENDEE_JOIN_URL = "attendee_join_url";

    public static final String OPERATOR_ID = "operator_id";

    public static final String MEETING_OPERATION_SHOW = "meeting_operation";

    public Operation mOperation;

    public String mOperationTitle;

    public String mOperatorId = StringUtils.EMPTY;

    public String mMeetingId;

    public Type mType;

    public UserHandleInfo mOperator;

    public UserHandleInfo mHost;

    public String mTitle = StringUtils.EMPTY;

    public long mMeetingTime;

    public String mMeetingParticipantsShow = StringUtils.EMPTY;

    public String mHostPassword = StringUtils.EMPTY;

    public String mHostJoinUrl = StringUtils.EMPTY;

    public String mAttendeePassword = StringUtils.EMPTY;

    public String mAttendeeJoinUrl = StringUtils.EMPTY;

    public String mUrl = StringUtils.EMPTY;


    public static MeetingNotifyMessage getMeetingNotifyMessageFromJson(Map<String, Object> jsonMap) {
        MeetingNotifyMessage meetingNotifyMessage = new MeetingNotifyMessage();
        meetingNotifyMessage.pareInfo(jsonMap);
        return meetingNotifyMessage;
    }

    protected void pareInfo(Map<String, Object> jsonMap) {
        this.initPostTypeMessageValue(jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

        this.mOperation = Operation.fromStringValue(ChatPostMessage.getString(bodyMap, OPERATION));

        this.mType = Type.fromStringValue(ChatPostMessage.getString(bodyMap, TYPE));

        this.mOperator = UserHandleInfo.getUserHandleInfo(bodyMap.get(OPERATOR));

        this.mMeetingId = ChatPostMessage.getString(bodyMap, MEETING_ID);

        this.mHost = UserHandleInfo.getUserHandleInfo(bodyMap.get(MEETING_HOST));

        this.mMeetingTime = ChatPostMessage.getLong(bodyMap, MEETING_TIME);

        this.mTitle = ChatPostMessage.getString(bodyMap, MEETING_TITLE);

        this.mMeetingParticipantsShow = ChatPostMessage.getString(bodyMap, MEETING_PARTICIPANT);

        this.mOperationTitle = ChatPostMessage.getString(bodyMap, MEETING_OPERATION_SHOW);

        this.mUrl = ChatPostMessage.getString(bodyMap, URL);

        this.mDisplayName = ChatPostMessage.getString(bodyMap, ChatPostMessage.DISPLAY_NAME);

        this.mDisplayAvatar = ChatPostMessage.getString(bodyMap, ChatPostMessage.DISPLAY_AVATAR);

        this.mOperatorId = ChatPostMessage.getString(bodyMap, OPERATOR_ID);

        this.mHostJoinUrl = ChatPostMessage.getString(bodyMap, HOST_JOIN_URL);

        this.mHostPassword = ChatPostMessage.getString(bodyMap, HOST_PASSWORD);

        this.mAttendeeJoinUrl = ChatPostMessage.getString(bodyMap, ATTENDEE_JOIN_URL);

        this.mAttendeePassword = ChatPostMessage.getString(bodyMap, ATTENDEE_PASSWORD);

    }

    public enum Type {

        UMEETING,

        QSY,

        BIZCONF,

        ZOOM,

        UNKNOWN;

        public static Type fromStringValue(String value) {
            if ("UMEETING".equalsIgnoreCase(value)) {
                return UMEETING;
            }

            if ("BIZCONF".equalsIgnoreCase(value)) {
                return BIZCONF;
            }

            if("ZOOM".equalsIgnoreCase(value)) {
                return ZOOM;
            }

            if ("QUAN_SHI".equalsIgnoreCase(value)) {
                return QSY;
            }


            return UNKNOWN;
        }

        public static String toString(Type type) {
            if (UMEETING.equals(type)) {
                return "UMEETING";
            }
            if (QSY.equals(type)) {
                return "QUAN_SHI";
            }

            if(ZOOM.equals(type)) {
                return "ZOOM";
            }

            if(BIZCONF.equals(type)) {
                return "BIZCONF";
            }

            return "UNKNOWN";
        }

    }

    public enum Operation {
        CREATED,

        UPDATE,

        CANCEL,

        UNKNOWN;

        public static Operation fromStringValue(String value) {
            if ("CREATED".equalsIgnoreCase(value)) {
                return CREATED;
            }

            if ("UPDATE".equalsIgnoreCase(value)) {
                return UPDATE;
            }

            if ("CANCEL".equalsIgnoreCase(value)) {
                return CANCEL;
            }


            return UNKNOWN;
        }

    }

}
