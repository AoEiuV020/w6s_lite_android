package com.foreveross.atwork.infrastructure.newmessage.post.chat;/**
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                       __           __
 .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 |________|_____|__|  |__|__|   __||__||_____|_____|
                            |__|
 */


import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by reyzhang22 on 16/2/18.
 */
public class ESpaceChatMessage extends ChatPostMessage {

    private static final String TAG = ESpaceChatMessage.class.getSimpleName();

    private static final String AUDIO_MEETING_ID = "audio_meeting_id";

    private static final String AUDIO_IS_ACTIVE = "audio_is_active";

    private static final String DESCRIPTION = "description";

    @Expose
    public long mAudioMeetingId;
    @Expose
    public boolean mIsActivity;

    public String mDescription;


    public ESpaceChatMessage() {
        deliveryTime = TimeUtil.getCurrentTimeInMillis();
        deliveryId = UUID.randomUUID().toString();
    }

    public static ESpaceChatMessage getESpaceChatMessageFromJson(Map<String, Object> jsonMap) {
        ESpaceChatMessage eSpaceChatMessage = new ESpaceChatMessage();
        eSpaceChatMessage.initPostTypeMessageValue(jsonMap);

        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

        eSpaceChatMessage.mAudioMeetingId = ((Double) bodyMap.get(AUDIO_MEETING_ID)).longValue();
        eSpaceChatMessage.mIsActivity = (boolean) bodyMap.get(AUDIO_IS_ACTIVE);
        eSpaceChatMessage.mDisplayAvatar = (String) bodyMap.get(DISPLAY_AVATAR);
        eSpaceChatMessage.mDisplayName = (String) bodyMap.get(DISPLAY_NAME);
        eSpaceChatMessage.mDescription = (String)bodyMap.get(DESCRIPTION);
        return eSpaceChatMessage;
    }

//    public static ESpaceChatMessage newSendESpaceChatMessage(Contact sender, String to) {
//        ESpaceChatMessage eSpaceChatMessage = new ESpaceChatMessage();
//
//        eSpaceChatMessage.from = sender.accountName;
//        eSpaceChatMessage.mDisplayAvatar = sender.avatar;
//        eSpaceChatMessage.mDisplayName = sender.name;
//        eSpaceChatMessage.to = to;
//        eSpaceChatMessage.chatSendType = ChatSendType.SENDER;
//        eSpaceChatMessage.chatStatus = ChatStatus.Sending;
//        eSpaceChatMessage.read = ReadStatus.AbsolutelyRead;
//        return eSpaceChatMessage;
//    }

    public void setMeeting( long meetingId, boolean isAudioMeetingActive) {
        this.mAudioMeetingId = meetingId;
        this.mIsActivity = isAudioMeetingActive;
    }

    @Override
    public ChatType getChatType() {
        return ChatType.AUDIOMEETING;
    }

    @Override
    public String getSessionShowTitle() {
        return "[音频会议]";
    }

    @Override
    public String getSearchAbleString() {
        return StringUtils.EMPTY;
    }

    @Override
    public boolean needNotify() {
        return true;
    }

    @Override
    public boolean needCount() {
        return true;
    }



    @Override
    public Map<String, Object> getChatBody() {
        Map<String, Object> chatBody = new HashMap<>();
        chatBody.put(AUDIO_MEETING_ID, mAudioMeetingId);
        chatBody.put(AUDIO_IS_ACTIVE, mIsActivity);
        chatBody.put(DISPLAY_NAME, mDisplayName);
        chatBody.put(DISPLAY_AVATAR, mDisplayAvatar);
        chatBody.put(DESCRIPTION, "小视频");
        return chatBody;
    }

}
