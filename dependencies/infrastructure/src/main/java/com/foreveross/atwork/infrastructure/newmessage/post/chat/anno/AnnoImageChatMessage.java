package com.foreveross.atwork.infrastructure.newmessage.post.chat.anno;

import android.content.Context;

import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.HasMediaChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.BurnInfo;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.HasFileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.IAtContactMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageContentInfo;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.StringConstants;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.Base64Util;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;

public class AnnoImageChatMessage extends HasMediaChatPostMessage implements HasFileStatus, IAtContactMessage {

    @Expose
    public String comment = "";

    @Expose
    public FileStatus fileStatus;

    @Expose
    public List<ImageContentInfo> contentInfos = new ArrayList<>();


    //at人员 (at_contacts)
    public ArrayList<TextChatMessage.AtUser> mAtUserList;

    @Expose
    public boolean atAll;


    public AnnoImageChatMessage() {
        deliveryTime = TimeUtil.getCurrentTimeInMillis();
        deliveryId = UUID.randomUUID().toString();
    }

    public static AnnoImageChatMessage getAnnoImageChatMessageFromJson(Map<String, Object> jsonMap) {
        AnnoImageChatMessage annoImageChatMessage = new AnnoImageChatMessage();
        annoImageChatMessage.initPostTypeMessageValue(jsonMap);

        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

        annoImageChatMessage.initChatTypeMessageValue(bodyMap);

        annoImageChatMessage.comment = ChatPostMessage.getString(bodyMap, COMMENT);

        if (bodyMap.containsKey(CONTENTS)) {
            List<ImageContentInfo> contentInfos = new ArrayList<>();
            List<Map<String, Object>>  contentMapList = (List<Map<String, Object>>) bodyMap.get(CONTENTS);
            for(Map<String, Object> contentMap : contentMapList) {
                ImageContentInfo imageContentInfo = ImageContentInfo.getImageContentInfoFromJsonMap(annoImageChatMessage, contentMap);

                contentInfos.add(imageContentInfo);
            }


            annoImageChatMessage.contentInfos = contentInfos;

        }

        if (bodyMap.containsKey(TextChatMessage.AT_ALL)) {
            annoImageChatMessage.atAll = TextChatMessage.parseBoolean(String.valueOf(bodyMap.get(TextChatMessage.AT_ALL))) ;
        }


        if (bodyMap.containsKey(TextChatMessage.AT_CONTACTS)) {
            annoImageChatMessage.mAtUserList = (ArrayList<TextChatMessage.AtUser>) bodyMap.get(TextChatMessage.AT_CONTACTS);

        }


        return annoImageChatMessage;
    }

    public List<ImageChatMessage> getImageContentInfoMessages() {
        List<ImageChatMessage> imageChatMessageList = new ArrayList<>();
        int forcedSerial = -1;
        for(ImageContentInfo imageContentInfo: contentInfos) {
            forcedSerial++;

            ImageChatMessage transformImageChatMessage = imageContentInfo.transformImageChatMessage(this);
            transformImageChatMessage.forcedSerial = forcedSerial;
            transformImageChatMessage.deliveryTime = deliveryTime;

            imageChatMessageList.add(transformImageChatMessage);
        }
        return imageChatMessageList;
    }

    @Override
    public void reGenerate(Context context, String senderId, String receiverId, String receiverDomainId, ParticipantType fromType, ParticipantType toType, BodyType bodyType, String orgId, ShowListItem chatItem, String myName, String myAvatar) {
        super.reGenerate(context, senderId, receiverId, receiverDomainId, fromType, toType, bodyType, orgId, chatItem, myName, myAvatar);

        for(ImageContentInfo contentInfo: contentInfos) {
            contentInfo.deliveryId = UUID.randomUUID().toString();
            contentInfo.deliveryTime = this.deliveryTime;
        }
    }

    @Override
    public String[] getMedias() {
        List<String> mediaIdList = CollectionsKt.map(contentInfos, new Function1<ImageContentInfo, String>() {
            @Override
            public String invoke(ImageContentInfo imageContentInfo) {
                return imageContentInfo.mediaId;
            }
        });

        return mediaIdList.toArray(new String[0]);
    }

    @Override
    public ChatType getChatType() {
        return ChatType.ANNO_IMAGE;
    }

    @Override
    public String getSessionShowTitle() {
        if(!StringUtils.isEmpty(comment)) {
            return comment;
        }

        return StringConstants.SESSION_CONTENT_IMG;
    }

    @Override
    public String getSearchAbleString() {
        return comment;
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
        chatBody.put(COMMENT, comment);

        ArrayList<Map<String, Object>> contentInfoChatMapList = new ArrayList<>();
        for(ImageContentInfo contentInfo: contentInfos) {
            contentInfoChatMapList.add(contentInfo.getChatBody());
        }

        chatBody.put(CONTENTS, contentInfoChatMapList);
        chatBody.put(TextChatMessage.AT_CONTACTS, mAtUserList);
        chatBody.put(TextChatMessage.AT_ALL, atAll);
        setBasicChatBody(chatBody);

        return chatBody;
    }

    @Override
    public void setFileStatus(FileStatus fileStatus) {
        this.fileStatus = fileStatus;
    }


    public void setAtUsers(List<UserHandleInfo> handleInfoList) {
        mAtUserList = new ArrayList<>();
        for (UserHandleInfo handleInfo : handleInfoList) {
            mAtUserList.add(new TextChatMessage.AtUser(handleInfo.mUserId, handleInfo.mShowName));

        }
    }

    public void setAtAll(boolean atAll) {
        this.atAll = atAll;
    }

    @Override
    public String getContent() {
        return comment;
    }

    @Override
    public boolean containAtMe(Context context) {
        String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        return containAtUser(meUserId);
    }

    public boolean containAtUser(String userId) {
        boolean result = false;
        if (null != mAtUserList) {
            try {
                for (int i = 0; i < mAtUserList.size(); i++) {
                    Object object = mAtUserList.get(i);
                    if (object instanceof TextChatMessage.AtUser) {
                        TextChatMessage.AtUser atUser = (TextChatMessage.AtUser) object;
                        if (userId.equals(atUser.mUserId)) {
                            result = true;
                            break;
                        }
                    }
                    if (object instanceof LinkedTreeMap) {
                        LinkedTreeMap<String, String> map = (LinkedTreeMap) object;
                        String mapUserId = map.get("user_id");
                        if (userId.equalsIgnoreCase(mapUserId)) {
                            result = true;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        return result;
    }

    @Override
    public boolean isAtMe(Context context) {
        String meUserId = LoginUserInfo.getInstance().getLoginUserId(context);
        return (atAll && !meUserId.equals(from)) || (containAtUser(meUserId));
    }

    public static AnnoImageChatMessage.Builder newBuilder() {
        return new AnnoImageChatMessage.Builder();
    }

    public static final class Builder extends ChatPostMessage.Builder<AnnoImageChatMessage.Builder>{

        private String comment;

        private FileStatus fileStatus;

        private List<ImageContentInfo> contentInfos;


        @Override
        protected BodyType getBodyType() {
            return BodyType.AnnoImage;
        }

        public Builder setComment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder setFileStatus(FileStatus fileStatus) {
            this.fileStatus = fileStatus;
            return this;
        }

        public Builder setContentInfos(List<ImageContentInfo> contentInfos) {
            this.contentInfos = contentInfos;
            return this;
        }

        public AnnoImageChatMessage build() {
            AnnoImageChatMessage annoImageChatMessage = new AnnoImageChatMessage();

            super.assemble(annoImageChatMessage);

            annoImageChatMessage.comment = comment;
            annoImageChatMessage.fileStatus = FileStatus.SENDING;
            annoImageChatMessage.contentInfos = contentInfos;

            return annoImageChatMessage;

        }
    }

}
