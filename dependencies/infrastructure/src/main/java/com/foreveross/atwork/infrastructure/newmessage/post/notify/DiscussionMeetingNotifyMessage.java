package com.foreveross.atwork.infrastructure.newmessage.post.notify;

import java.util.Map;

/**
 * Created by dasunsy on 2017/11/13.
 */

public class DiscussionMeetingNotifyMessage extends  MeetingNotifyMessage {
    public static String FROM = "DISCUSSION_MEETING_HELPER";

    public static DiscussionMeetingNotifyMessage getDiscussionMeetingNotifyMessageFromJson(Map<String, Object> jsonMap) {
        DiscussionMeetingNotifyMessage meetingNotifyMessage = new DiscussionMeetingNotifyMessage();
        meetingNotifyMessage.pareInfo(jsonMap);
        return meetingNotifyMessage;
    }


}
