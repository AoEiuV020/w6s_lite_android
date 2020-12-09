package com.foreveross.atwork.infrastructure.newmessage.post.chat.reference;

import android.content.Context;
import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.HasMediaChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.IAtContactMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MultipartChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.MessageCovertUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReferenceMessage extends HasMediaChatPostMessage implements IAtContactMessage {

    public static final String QUOTE = "quote";

    public static final String REPLY = "reply";

    @Expose
    public ChatPostMessage mReferencingMessage;

    public String mSourceMsg;

    @Expose
    public String mReply;

    //at人员 (at_contacts)
    public ArrayList<TextChatMessage.AtUser> mAtUserList;

    @Expose
    public boolean atAll;

    public ReferenceMessage() {
        deliveryTime = TimeUtil.getCurrentTimeInMillis();
        deliveryId = UUID.randomUUID().toString();
    }


    public static boolean supportReference(ChatPostMessage chatPostMessage) {
        if(chatPostMessage.notSent()) {
            return false;
        }

        if(chatPostMessage instanceof ShareChatMessage) {
            ShareChatMessage shareChatMessage = (ShareChatMessage) chatPostMessage;
            if(ShareChatMessage.ShareType.BusinessCard.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                return true;
            }

            if(ShareChatMessage.ShareType.Link.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                return true;
            }

            if(ShareChatMessage.ShareType.Loc.toString().equalsIgnoreCase(shareChatMessage.getShareType())) {
                return true;
            }
        }

        return chatPostMessage instanceof TextChatMessage
                || chatPostMessage instanceof StickerChatMessage
                || chatPostMessage instanceof ImageChatMessage
                || chatPostMessage instanceof AnnoImageChatMessage
                || chatPostMessage instanceof MicroVideoChatMessage
                || chatPostMessage instanceof VoiceChatMessage
                || chatPostMessage instanceof FileTransferChatMessage
                || chatPostMessage instanceof MultipartChatMessage
                || chatPostMessage instanceof ReferenceMessage;
    }



    @Nullable
    public static ReferenceMessage getReferenceMessage(Map<String, Object> jsonMap, String sourceMsg) throws JSONException {



        Map<String, Object> bodyMap = (Map<String, Object>) jsonMap.get(BODY);

        BodyType bodyType = BodyType.toBodyType((String) jsonMap.get(BODY_TYPE));
        bodyType = BodyType.makeParseBodyCompatible(bodyMap, bodyType);

        ReferenceMessage referenceMessage = generateReferenceMessage(bodyType);

        if(null == referenceMessage) {
            return null;
        }

        referenceMessage.initPostTypeMessageValue(jsonMap);

        referenceMessage.initChatTypeMessageValue(bodyMap);
        Map<String, Object> referencingMessageMap = null;
        if (bodyMap.containsKey(QUOTE)) {
            referencingMessageMap = (Map<String, Object>) bodyMap.get(QUOTE);
        }

        if(null != referencingMessageMap) {
            PostTypeMessage postTypeMessage = MessageCovertUtil.covertJsonToMessage(new JSONObject(referencingMessageMap).toString());
            if(postTypeMessage instanceof ChatPostMessage) {
                referenceMessage.mReferencingMessage = (ChatPostMessage) postTypeMessage;

                referenceMessage.mReferencingMessage.parentReferenceMessage = referenceMessage;
            }

        }


        referenceMessage.mReply = ChatPostMessage.getString(bodyMap, REPLY);

        if (bodyMap.containsKey(TextChatMessage.AT_ALL)) {
            referenceMessage.atAll = TextChatMessage.parseBoolean(String.valueOf(bodyMap.get(TextChatMessage.AT_ALL))) ;
        }


        if (bodyMap.containsKey(TextChatMessage.AT_CONTACTS)) {
            referenceMessage.mAtUserList = (ArrayList<TextChatMessage.AtUser>) bodyMap.get(TextChatMessage.AT_CONTACTS);

        }

        referenceMessage.mSourceMsg = sourceMsg;

        return referenceMessage;
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
        return mReply;
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

    public static ReferenceMessage generateReferenceMessage(BodyType bodyType) {

        return new ReferenceMessage();
//        ReferenceMessage referenceMessage = null;
//
//        if(BodyType.QuotedText == bodyType) {
//            referenceMessage = new ReferenceTextMessage();
//        } else if(BodyType.QuotedQuoted == bodyType) {
//            referenceMessage = new ReferenceReferenceMessage();
//
//        }
//
//
//
//        return referenceMessage;
    }


    @Override
    public ChatType getChatType() {
        return ChatType.QUOTED;
    }

    @Override
    public String getSessionShowTitle() {
        return mReply;
    }

    @Override
    public String getSearchAbleString() {
        return mReply;
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
        chatBody.put(QUOTE, mReferencingMessage.getMessageBody());
        if (!StringUtils.isEmpty(mReply)) {
            chatBody.put(REPLY, mReply);
        }

        chatBody.put(TextChatMessage.AT_CONTACTS, mAtUserList);
        chatBody.put(TextChatMessage.AT_ALL, atAll);

        setBasicChatBody(chatBody);

        return chatBody;
    }


    public static ReferenceMessage.Builder newBuilder() {
        return new ReferenceMessage.Builder();
    }

    @Override
    public String[] getMedias() {
        if(mReferencingMessage instanceof HasMediaChatPostMessage) {
            return ((HasMediaChatPostMessage)mReferencingMessage).getMedias();
        } else {
            return new String[0];
        }

    }


    public static final class Builder extends ChatPostMessage.Builder<ReferenceMessage.Builder>{


        private ChatPostMessage mReferencingMessage;

        private String mReply;



        private Builder() {

        }

        @Override
        protected BodyType getBodyType() {

//            if(mReferencingMessage instanceof TextChatMessage) {
//                return BodyType.QuotedText;
//            } else if(mReferencingMessage instanceof ReferenceMessage) {
//                return BodyType.QuotedQuoted;
//            }
            return BodyType.Quoted;
        }

        public Builder setReferencingMessage(ChatPostMessage referencingMessage) {
            this.mReferencingMessage = referencingMessage;
            return this;
        }

        public Builder setReply(String reply) {
            this.mReply = reply;
            return this;
        }

        @Nullable
        public ReferenceMessage build() {
            ReferenceMessage referenceMessage = ReferenceMessage.generateReferenceMessage(getBodyType());

            if(null == referenceMessage) {
                return null;
            }

            super.assemble(referenceMessage);


            referenceMessage.mReply = mReply;
            referenceMessage.mReferencingMessage = mReferencingMessage;

            referenceMessage.mReferencingMessage.parentReferenceMessage = referenceMessage;

            return referenceMessage;
        }


    }


}
