package com.foreveross.atwork.infrastructure.newmessage.post.chat;

import android.content.Context;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.translate.VoiceTranslateStatus;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.HasMediaChatPostMessage;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.support.StringConstants;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.file.FileStreamHelper;
import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lingen on 15/4/16.
 * Description:
 */
public class VoiceChatMessage extends HasMediaChatPostMessage implements HasFileStatus, VoiceMedia {

    public static final String DURATION = "duration";
    /**
     * 语音内容
     */
    public byte[] content;

    @Expose
    public String mediaId;

    @Expose
    public boolean play;

    /**
     * 时间长度
     */
    @Expose
    public int duration;

    @Expose
    public FileStatus fileStatus;

    public boolean playing;

    public String currentPlayId = StringUtils.EMPTY;

    public boolean isFavPlay = false;

    @Expose
    public VoiceTranslateStatus mVoiceTranslateStatus;


    public VoiceChatMessage() {
        deliveryTime = TimeUtil.getCurrentTimeInMillis();
    }

    public static VoiceChatMessage getVoiceChatMessageFromJson(Map<String, Object> jsonMap) {
        VoiceChatMessage voiceChatMessage = new VoiceChatMessage();
        voiceChatMessage.initPostTypeMessageValue(jsonMap);
        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

        voiceChatMessage.initChatTypeMessageValue(bodyMap);

        if (bodyMap.containsKey(DURATION)) {
            voiceChatMessage.duration = ((Double) (bodyMap.get(DURATION))).intValue();
        }
        voiceChatMessage.mediaId = ((String) bodyMap.get(MEDIA_ID));


        if (bodyMap.containsKey(SOURCE)) {
            voiceChatMessage.source = (String)bodyMap.get(SOURCE);
        }
        if(bodyMap.containsKey(ORG_ID)) {
            voiceChatMessage.mOrgId = (String) bodyMap.get(ORG_ID);

        }

        if(!StringUtils.isEmpty(voiceChatMessage.mDeletionPolicy) && bodyMap.containsKey(BURN)) {
            voiceChatMessage.mBurnInfo = BurnInfo.parseFromMap((Map<String, Object>) bodyMap.get(BURN));
        }

        return voiceChatMessage;
    }

    public static VoiceChatMessage newSendVoiceMessage(Context context, String audioId, int playtime, ShowListItem sender, String to, ParticipantType fromType, ParticipantType toType,
                                                       String toDomain, BodyType bodyType, String orgId, ShowListItem showListItem, boolean burn, long readTime, long deletionOnTime) {
        String toAvatar = "";
        String toName = "";
        if(null != showListItem) {
            toAvatar = showListItem.getAvatar();
            toName = showListItem.getTitle();
        }
        return newSendVoiceMessage(context, audioId, playtime, sender, to, fromType, toType, toDomain, toAvatar, toName, bodyType, orgId, burn, readTime, deletionOnTime);
    }

    public static VoiceChatMessage newSendVoiceMessage(Context context, String audioId, int playtime, ShowListItem sender, String to, ParticipantType fromType, ParticipantType toType,
                                                       String toDomain, String toAvatar, String toName, BodyType bodyType, String orgId, boolean burn, long readTime, long deletionOnTime) {
        VoiceChatMessage voiceChatMessage = new VoiceChatMessage();
        voiceChatMessage.buildSenderInfo(context);
        voiceChatMessage.deliveryId = audioId;
        voiceChatMessage.content = VoiceChatMessage.loadAudioFile(context, audioId);
        voiceChatMessage.duration = playtime;
//        voiceChatMessage.from = sender.getId();
//        voiceChatMessage.mMyAvatar = sender.getAvatar();
//        voiceChatMessage.mMyName = sender.getTitle();
        voiceChatMessage.to = to;
        voiceChatMessage.chatSendType = ChatSendType.SENDER;
        voiceChatMessage.chatStatus = ChatStatus.Sending;
        voiceChatMessage.read = ReadStatus.AbsolutelyRead;
        voiceChatMessage.fileStatus = FileStatus.SENDING;
        voiceChatMessage.mBodyType = bodyType;
//        voiceChatMessage.mFromType = fromType;
        voiceChatMessage.mToType = toType;
        voiceChatMessage.mToDomain = toDomain;
        voiceChatMessage.mOrgId = orgId;
        voiceChatMessage.mDisplayAvatar = toAvatar;
        voiceChatMessage.mDisplayName = toName;
        if (burn) {
            voiceChatMessage.mBurnInfo = new BurnInfo();
            voiceChatMessage.mBurnInfo.mReadTime = readTime;
            voiceChatMessage.mDeletionPolicy = "LOGICAL";
            voiceChatMessage.mDeletionOn = deletionOnTime;
        }

        return voiceChatMessage;
    }

    private static byte[] loadAudioFile(Context context, String audioId) {
        return FileStreamHelper.readFile(VoiceChatMessage.getAudioPath(context, audioId));
    }

    @Override
    public void reGenerate(Context context, String senderId, String receiverId, String receiverDomainId, ParticipantType fromType,
                           ParticipantType toType, BodyType bodyType, String orgId, ShowListItem chatItem,String myName, String myAvatar) {

        String path = VoiceChatMessage.getAudioPath(context, deliveryId);
        byte[] content = FileStreamHelper.readFile(path);
        super.reGenerate(context, senderId, receiverId, receiverDomainId, fromType, toType, bodyType, orgId, chatItem,myName,myAvatar);
        FileStreamHelper.saveFile(path, content);

    }

    @Override
    public Map<String, Object> getChatBody() {
        Map<String, Object> chatBody = new HashMap<>();
        chatBody.put(DURATION, duration);
        chatBody.put(MEDIA_ID, mediaId);
        if (!TextUtils.isEmpty(mOrgId)) {
            chatBody.put(ORG_ID, mOrgId);
        }

        if(isBurn()) {
            chatBody.put(BURN, mBurnInfo.getChatMapBody());
        }

        setBasicChatBody(chatBody);

        return chatBody;
    }

    @Override
    public ChatType getChatType() {
        return ChatType.Voice;
    }

    @Override
    public String getSessionShowTitle() {

        if(isBurn()) {
            return StringConstants.SESSION_CONTENT_BURN_MESSAGE;
        }

        return StringConstants.SESSION_CONTENT_VOICE;
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
    public void setFileStatus(FileStatus fileStatus) {
        this.fileStatus = fileStatus;
    }

    public static String getAudioRecordingPath(Context context, String audioId) {
        if(AtworkConfig.OPEN_DISK_ENCRYPTION) {
            return AtWorkDirUtils.getInstance().getTmpFilesCachePath() + audioId + ".tmp";

        }
        return AtWorkDirUtils.getInstance().getAUDIO(context) + audioId + ".tmp";

    }

    public String getAudioPath(Context context) {
        return AtWorkDirUtils.getInstance().getAUDIO(context) + deliveryId + ".amr";
    }

    public static String getAudioPath(Context context, String audioId) {
        return AtWorkDirUtils.getInstance().getAUDIO(context) + audioId + ".amr";
    }

    public static void receiveAudio(Context context, String audioId, byte[] audioContent) {
        String path = getAudioPath(context, audioId);
        FileStreamHelper.saveFile(path, audioContent);
    }

    public void setTranslatedResult(String result, String language) {
        mVoiceTranslateStatus = new VoiceTranslateStatus();
        mVoiceTranslateStatus.mResult = result;
        mVoiceTranslateStatus.mLanguage = language;
        mVoiceTranslateStatus.mTranslating = false;
        mVoiceTranslateStatus.mVisible = true;
    }

    /**
     * 得到翻译结果
     * */
    public String getTranslatedResult() {
        if(null != mVoiceTranslateStatus) {
            return mVoiceTranslateStatus.mResult;
        }

        return StringUtils.EMPTY;
    }

    /**
     * 是否已经翻译出结果了
     * */
    public boolean hasTranslatedBefore() {
        return !StringUtils.isEmpty(getTranslatedResult());
    }

    @Override
    public String getMediaId() {
        return mediaId;
    }

    @Override
    public String getKeyId() {
        return deliveryId;
    }

    /**
     * 翻译的 UI 是否显示
     * */
    public boolean isTranslateStatusVisible() {
        return null != mVoiceTranslateStatus && mVoiceTranslateStatus.mVisible;

    }
    /**
     * 是否正在翻译中
     * */
    public boolean isTranslating() {
        return null != mVoiceTranslateStatus && mVoiceTranslateStatus.mTranslating;
    }

    @Override
    public String[] getMedias() {
        if (!StringUtils.isEmpty(mediaId)) {
            return new String[]{mediaId};
        }

        return new String[0];
    }
}
