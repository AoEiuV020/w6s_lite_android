package com.foreveross.atwork.api.sdk.message.model;

import android.content.Context;

import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.MessageCovertUtil;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by reyzhang22 on 15/10/30.
 * 离线消息json解析体
 */
public class OfflineMessageResponseJson implements Serializable {

    public static final String TAG = OfflineMessageResponseJson.class.getSimpleName();

    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;

    @SerializedName("result")
    public MessageResponseResult result;

    public static class MessageResponseResult {

        @SerializedName("messages")
        public JSONArray messagesArr;

        @SerializedName("receipts")
        public String receipts;
    }

    public static OfflineMessageResponseJson createInstance(String result) {
        synchronized (TAG) {
            OfflineMessageResponseJson offlineMessageResponseJson = new OfflineMessageResponseJson();
            try {
                JSONObject json = new JSONObject(result);
                offlineMessageResponseJson.status = json.optInt("status");
                offlineMessageResponseJson.message = json.optString("message");
                JSONObject subJson = json.optJSONObject("result");
                if (subJson != null) {
                    offlineMessageResponseJson.result = new MessageResponseResult();
                    offlineMessageResponseJson.result.messagesArr = subJson.getJSONArray("messages");

                    if (subJson.has("receipts")) {
                        offlineMessageResponseJson.result.receipts = subJson.getString("receipts");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return offlineMessageResponseJson;
        }
    }

    public static List<ChatPostMessage> toChatPostMessages(List<PostTypeMessage> postTypeMessages) {
        List<ChatPostMessage> chatPostMessageList = new ArrayList<>();

        for (PostTypeMessage postTypeMessage : postTypeMessages) {
            if (postTypeMessage instanceof ChatPostMessage) {
                chatPostMessageList.add((ChatPostMessage) postTypeMessage);
            }
        }
        return chatPostMessageList;
    }


    public List<PostTypeMessage> toPostTypeMessage(Context context) {
        List<PostTypeMessage> postTypeMessages = new ArrayList<>();
        try {
            for (int i = 0; i < result.messagesArr.length(); i++) {
                JSONObject jsonObject = result.messagesArr.getJSONObject(i);
                PostTypeMessage message = MessageCovertUtil.covertJsonToMessage(jsonObject.toString());
                if (message == null) {
                    continue;
                }

                if (LoginUserInfo.getInstance().getLoginUserId(context).equals(message.from)) {
                    message.chatSendType = ChatSendType.SENDER;
                } else {
                    message.chatSendType = ChatSendType.RECEIVER;
                }


                if(message instanceof FileTransferChatMessage){
                    FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage)message;
                    if (LoginUserInfo.getInstance().getLoginUserId(context).equals(message.from)) {
                        fileTransferChatMessage.fileStatus = FileStatus.SENDED;
                    } else {
                        fileTransferChatMessage.fileStatus = FileStatus.NOT_DOWNLOAD;
                    }

                } else if (message instanceof VoiceChatMessage){
                    VoiceChatMessage voiceChatMessage = (VoiceChatMessage) message;
                    voiceChatMessage.play = true;
                }

                message.setChatStatus(ChatStatus.Sended);
                postTypeMessages.add(message);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.sort(postTypeMessages, new Comparator<PostTypeMessage>() {
            @Override
            public int compare(PostTypeMessage lhs, PostTypeMessage rhs) {
                return TimeUtil.compareTo(lhs.deliveryTime, rhs.deliveryTime);
            }
        });
        return postTypeMessages;
    }
}
