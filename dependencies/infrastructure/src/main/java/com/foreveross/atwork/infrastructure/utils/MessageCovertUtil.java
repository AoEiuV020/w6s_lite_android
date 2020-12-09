package com.foreveross.atwork.infrastructure.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.HasBodyMessage;
import com.foreveross.atwork.infrastructure.newmessage.HasTimestampResponse;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.UserTypingMessage;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.CmdPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.DeviceInfoMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.EventPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.SystemPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.VoipPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.ack.AckPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoFileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MultipartChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TemplateMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.UnknownChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.BingUndoEventMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.HideEventMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.gather.CmdGatherMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.BingNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.ContactNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.DiscussionMeetingNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.DiscussionNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.EmergencyNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.FriendNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.MeetingNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.OrgNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.P2PNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.UserFileDownloadNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.conversation.ConversationNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.conversation.ConversationSettingsChangedConversationNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.conversation.ConversationSettingsResetConversationNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.user.UserSettingsChangedUserNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.user.UserNotifyMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.wallet.WalletNotifyMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


public class MessageCovertUtil {

    @NonNull
    public  static List<ChatPostMessage> coverJsonToMessageList(Context context, String json) {
        List<ChatPostMessage> resultList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for(int i = 0; i < jsonArray.length(); i++) {
                ChatPostMessage message = (ChatPostMessage) MessageCovertUtil.covertJsonToMessage(jsonArray.get(i).toString());


                if(null != message) {
                    resultList.add(message);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultList;
    }


    @NonNull
    public  static List<ChatPostMessage> coverJsonToMessageHistoryList(String json) {
        List<ChatPostMessage> resultList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for(int i = 0; i < jsonArray.length(); i++) {
                String messageJson = jsonArray.get(i).toString();
                ChatPostMessage message = (ChatPostMessage) MessageCovertUtil.covertJsonToMessage(messageJson);

                if(null != message) {
                    JSONObject jsonObject = new JSONObject(messageJson);
                    message.deliveryTime = jsonObject.optLong("refresh_time");
                    message.deliveryId = jsonObject.optString("id");

                    resultList.add(message);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    @Nullable
    public static PostTypeMessage covertPureBodyToMessage(String messageId, long messageDeliveryTime, String type, String bodyData) {
        PostTypeMessage dataMessage = null;
        try {
            JSONObject dataMessageJson = new JSONObject();
            dataMessageJson.put(PostTypeMessage.DELIVER_ID, messageId);
            dataMessageJson.put(PostTypeMessage.DELIVER_TIME, messageDeliveryTime);
            dataMessageJson.put(PostTypeMessage.FROM, StringUtils.EMPTY);
            dataMessageJson.put(PostTypeMessage.FROM_TYPE, StringUtils.EMPTY);
            dataMessageJson.put(PostTypeMessage.FROM_DOMAIN, StringUtils.EMPTY);
            dataMessageJson.put(PostTypeMessage.TO, StringUtils.EMPTY);
            dataMessageJson.put(PostTypeMessage.TO_TYPE, StringUtils.EMPTY);
            dataMessageJson.put(PostTypeMessage.TO_DOMAIN, StringUtils.EMPTY);
            dataMessageJson.put(PostTypeMessage.BODY_TYPE, type);
            dataMessageJson.put(PostTypeMessage.BODY, new JSONObject(bodyData));
            dataMessage = MessageCovertUtil.covertJsonToMessage(dataMessageJson.toString());

            if(dataMessage instanceof FileTransferChatMessage) {
                FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) dataMessage;
                fileTransferChatMessage.expiredTime = -1;
            }

            if(null != dataMessage) {
                dataMessage.chatStatus = ChatStatus.Sended;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataMessage;

    }

    /**
     * 从json获得有数据的 PostTypeMessage对象
     */
    @Nullable
    public static PostTypeMessage covertJsonToMessage(String json) {
        Gson gson = GsonHelper.getMessageMapGson();

        Map<String, Object> jsonValue = null;

        try {
            jsonValue = gson.fromJson(json, Map.class);


            LogUtil.e("IM_SOCKET:", "收到消息, 正在转换模型 -> ");
            LogUtil.e("IM_SOCKET:", json);

            Map<String, Object> bodyMap = (Map<String, Object>) jsonValue.get(PostTypeMessage.BODY);
            String bodyType = (String) jsonValue.get(PostTypeMessage.BODY_TYPE);
            if(StringUtils.isEmpty(bodyType)) {
                bodyType = (String) bodyMap.get("type");
            }

            String from = (String) jsonValue.get(PostTypeMessage.FROM);
            String toType = (String) jsonValue.get(PostTypeMessage.TO_TYPE);
            String fromType = (String) jsonValue.get(PostTypeMessage.FROM_TYPE);

            //回执确认消息
            if (BodyType.ACK.equalsIgnoreCase(bodyType)) {
                return AckPostMessage.getAckPostMessageFromJson(jsonValue);
            }

            //文本消息
            if (BodyType.TEXT.equalsIgnoreCase(bodyType) || BodyType.BURN_TEXT.equalsIgnoreCase(bodyType)) {
                return TextChatMessage.getTextChatMessageFromJson(jsonValue);
            }
            //分享消息
            if (BodyType.SHARE.equalsIgnoreCase(bodyType)) {
                return ShareChatMessage.getShareChatMessageFromJson(jsonValue);
            }
            //视频消息
            if (BodyType.VIDEO.equalsIgnoreCase(bodyType)) {
                return MicroVideoChatMessage.getMicroVideoMessageFromJson(jsonValue);
            }
            //图文消息
            if (BodyType.ARTICLE.equalsIgnoreCase(bodyType)) {
                return ArticleChatMessage.getArticleChatMessageFromJson(jsonValue);
            }
            //文件消息
            if (BodyType.FILE.equalsIgnoreCase(bodyType)) {
                return FileTransferChatMessage.getFileTransferChatMessageFromJson(jsonValue);
            }
            //语音消息
            if (BodyType.VOICE.equalsIgnoreCase(bodyType) || BodyType.BURN_VOICE.equalsIgnoreCase(bodyType)) {
                return VoiceChatMessage.getVoiceChatMessageFromJson(jsonValue);
            }
            //图片消息
            if (BodyType.IMAGE.equalsIgnoreCase(bodyType) || BodyType.BURN_IMAGE.equalsIgnoreCase(bodyType)) {
                return ImageChatMessage.getImageChatMessageFromJson(jsonValue);
            }

            //命令消息
            if (BodyType.CMD.equalsIgnoreCase(bodyType)) {
                return CmdPostMessage.getCmdPostMessageFromJson(jsonValue);
            }

            //系统消息
            if (BodyType.SYSTEM.equalsIgnoreCase(bodyType)) {
                return SystemPostMessage.getSystemPostMessageFromJson(jsonValue);
            }

            if (BodyType.STICKER.equalsIgnoreCase(bodyType) || BodyType.FACE.equalsIgnoreCase(bodyType)) {
                return StickerChatMessage.Companion.getStickerChatMessageFromJson(jsonValue);
            }

            //事件消息
            if (BodyType.EVENT.equalsIgnoreCase(bodyType)) {
                EventPostMessage.EventType eventType = EventPostMessage.EventType.fromStringValue((String) bodyMap.get(UndoEventMessage.EVENT_TYPE));
                if (EventPostMessage.EventType.Undo.equals(eventType)) {
                    return UndoEventMessage.getUndoEventMessageFromJson(jsonValue);

                }

                if(EventPostMessage.EventType.Remove.equals(eventType)) {
                    return HideEventMessage.getHideEventMessageFromJson(jsonValue);
                }

                if(EventPostMessage.EventType.BingUndo.equals(eventType)) {
                    return BingUndoEventMessage.getUndoEventMessageFromJson(jsonValue);
                }
            }

            //通知消息
            if (BodyType.NOTICE.equalsIgnoreCase(bodyType)) {
                if (DiscussionNotifyMessage.FROM.equalsIgnoreCase(from)) {
                    return DiscussionNotifyMessage.getDiscussionNotifyMessageFromJson(jsonValue);

                }
                if (FriendNotifyMessage.FROM.equalsIgnoreCase(from)) {
                    return FriendNotifyMessage.getFriendNotifyMessageFromJson(jsonValue);

                }
                if (OrgNotifyMessage.FROM.equalsIgnoreCase(from)) {
                    return OrgNotifyMessage.getOrgNotifyMessageFromJson(jsonValue);

                }
                if(ContactNotifyMessage.FROM.equalsIgnoreCase(from)) {
                    return ContactNotifyMessage.getContactNotifyMessageFromJson(jsonValue);

                }

                if(BingNotifyMessage.FROM.equalsIgnoreCase(from)) {
                    return BingNotifyMessage.getBingNotifyMessageFromJson(jsonValue);
                }

                if(MeetingNotifyMessage.FROM.equalsIgnoreCase(from)) {
                    return MeetingNotifyMessage.getMeetingNotifyMessageFromJson(jsonValue);
                }

                if(DiscussionMeetingNotifyMessage.FROM.equalsIgnoreCase(from)) {
                    return DiscussionMeetingNotifyMessage.getDiscussionMeetingNotifyMessageFromJson(jsonValue);
                }

                if(EmergencyNotifyMessage.FROM.equalsIgnoreCase(from)) {
                    return EmergencyNotifyMessage.getEmergencyNotifyMessageFromJson(jsonValue);
                }

                if(UserNotifyMessage.FROM.equalsIgnoreCase(from)) {
                    UserNotifyMessage.Operation operation = UserNotifyMessage.Operation.fromStringValue(ChatPostMessage.getString(bodyMap, UserNotifyMessage.OPERATION));

                    if(UserNotifyMessage.Operation.SETTINGS_CHANGED == operation) {
                        return UserSettingsChangedUserNotifyMessage.getUserSettingChangedUserNotifyMessageFromJson(jsonValue);
                    }

                }


                if(ConversationNotifyMessage.FROM.equalsIgnoreCase(from)) {
                    ConversationNotifyMessage.Operation operation = ConversationNotifyMessage.Operation.fromStringValue(ChatPostMessage.getString(bodyMap, ConversationNotifyMessage.OPERATION));

                    if(ConversationNotifyMessage.Operation.SETTINGS_CHANGED == operation) {
                        return ConversationSettingsChangedConversationNotifyMessage.getConversationSettingsChangedConversationNotifyMessageFromJson(jsonValue);
                    }


                    if(ConversationNotifyMessage.Operation.SETTINGS_RESET == operation)  {
                        return ConversationSettingsResetConversationNotifyMessage.getConversationSettingsResetConversationNotifyMessageFromJson(jsonValue);
                    }
                }


                if(ParticipantType.USER.equalsIgnoreCase(fromType)) {

//                    Map<String, Object> bodyMap = (Map<String, Object>) jsonValue.get(PostTypeMessage.BODY);
                    UserFileDownloadNotifyMessage.Operation operation = UserFileDownloadNotifyMessage.Operation.fromStringValue(ChatPostMessage.getString(bodyMap, WalletNotifyMessage.OPERATION));

                    if(UserFileDownloadNotifyMessage.Operation.FILE_DOWNLOAD_SUCCESS == operation) {
                        return UserFileDownloadNotifyMessage.getFileDownloadNotifyMessageFromJson(jsonValue);

                    }
                }

                if (ParticipantType.USER.equalsIgnoreCase(toType)) {
                    return P2PNotifyMessage.getP2PNotifyMessageFromJson(jsonValue);
                }

                //不认识的通知返回null, 避免在页面显示"未知消息"
                return null;

            }

            if (BodyType.VOIP.equalsIgnoreCase(bodyType)) {
                return VoipPostMessage.getVoipPostMessageFromJson(jsonValue);
            }

            if (BodyType.MULTIPART.equalsIgnoreCase(bodyType)) {
                return MultipartChatMessage.getMultipartChatMessage(jsonValue);
            }

            if (BodyType.TEMPLATE.equalsIgnoreCase(bodyType)) {
                return TemplateMessage.getTemplateMessageFromJson(jsonValue);
            }

            if(BodyType.GATHER.equalsIgnoreCase(bodyType)) {
                return CmdGatherMessage.getCmdGatherMessage(jsonValue);
            }

            if(BodyType.QUOTED.equalsIgnoreCase(bodyType)) {
                ReferenceMessage referenceMessage = ReferenceMessage.getReferenceMessage(jsonValue, json);
                if(null != referenceMessage && ReferenceMessage.supportReference(referenceMessage.mReferencingMessage)) {
                    return referenceMessage;
                }
            }


            //带 comment 文件消息
            if (BodyType.ANNO_FILE.equalsIgnoreCase(bodyType)) {
                return AnnoFileTransferChatMessage.getFileTransferChatMessageFromJson(jsonValue);
            }


            if (BodyType.ANNO_IMAGE.equalsIgnoreCase(bodyType)) {
                return AnnoImageChatMessage.getAnnoImageChatMessageFromJson(jsonValue);
            }

            return UnknownChatMessage.generaUnknownMessage(jsonValue, json);

        } catch (Exception e) {
            return null;
        }
    }

    public static HasTimestampResponse covertHasTimestampResponse(byte[] bytes, HasTimestampResponse hasTimestampResponse) {
        String json = new String(bytes, Charset.forName("UTF-8"));
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().registerTypeAdapter(byte[].class, new ByteArrayToBase64TypeAdapter()).create();

        Map<String, Object> jsonValue = null;

        try {
            jsonValue = gson.fromJson(json, Map.class);

        } catch (Exception e) {
            Log.d("IM_SOCKET", "解析IM消息错误:" + json);
            return null;
        }
        hasTimestampResponse.timestamp = ((Double) jsonValue.get("timestamp")).longValue();
        ArrayList<LinkedTreeMap<String, String>> list = (ArrayList)jsonValue.get("others");
        if (null != list) {

            ArrayList<HasTimestampResponse.OthersInfo> infos = new ArrayList<>();
            for (LinkedTreeMap<String, String> map : list)  {
                HasTimestampResponse.OthersInfo info = new HasTimestampResponse.OthersInfo();
                info.mDevicePlatform = map.get("device_platform");
                info.mDeviceId = map.get("device_id");
                info.mDeviceSystem = map.get("device_system");
                infos.add(info);
            }
            hasTimestampResponse.mOthers = infos;
        }
        return hasTimestampResponse;
    }

    public static DeviceInfoMessage parseDeviceInfoResponse(byte[] bytes, DeviceInfoMessage message) {
        String jsonString = new String(bytes, Charset.forName("UTF-8"));
        try {
            JSONObject json = new JSONObject(jsonString);
            message.mDeviceId = json.optString(DeviceInfoMessage.DEVICE_ID);
            message.mDevicePlatform = json.optString(DeviceInfoMessage.DEVICE_PLATFORM);
            message.mTimestamp = json.optString(DeviceInfoMessage.TIMESTAMP);
            message.mDeviceSystem = json.optString(DeviceInfoMessage.DEVICE_SYSTEM);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message;
    }

    public static UserTypingMessage parseUserTypingMessage(byte[] bytes) {
        String jsonString = new String(bytes, Charset.forName("UTF-8"));
        try {
            JSONObject json = new JSONObject(jsonString);

            UserTypingMessage.UserTypingMessageBuilder builder =
                    UserTypingMessage.UserTypingMessageBuilder.anUserTypingMessage()
                            .withFrom(json.getString(UserTypingMessage.FROM_ID))
                            .withFromDomainId(json.getString(PostTypeMessage.FROM_DOMAIN))
                            .withFromType(ParticipantType.toParticipantType(json.getString(PostTypeMessage.FROM_TYPE)))
                            .withTo(json.getString(UserTypingMessage.TO_ID))
                            .withToDomainId(json.getString(PostTypeMessage.TO_DOMAIN))
                            .withToType( ParticipantType.toParticipantType(json.getString(PostTypeMessage.TO_TYPE)))
                            .withConversationId(json.getString(PostTypeMessage.CONVERSATION_ID))
                            .withConversationDomainId(PostTypeMessage.CONVERSATION_DOMAIN)
                            .withConversationType(ParticipantType.toParticipantType(json.getString(PostTypeMessage.CONVERSATION_TYPE)));

            return builder.build();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    @NonNull
    public static String covertMessageListToJson(List<ChatPostMessage> msgList) {
        StringBuilder jsonStrBuilder = new StringBuilder();
        jsonStrBuilder.append("[");

        for(int i = 0; i < msgList.size(); i++) {
            jsonStrBuilder.append(MessageCovertUtil.covertMessageToJson(msgList.get(i)));

            if(i != msgList.size() - 1) {
                jsonStrBuilder.append(",");
            }

        }

        jsonStrBuilder.append("]");
        return jsonStrBuilder.toString();
    }

    @NonNull
    public static String covertMessageListToJsonWithLocalData(List<ChatPostMessage> msgList) {
        StringBuilder jsonStrBuilder = new StringBuilder();
        jsonStrBuilder.append("[");

        for(int i = 0; i < msgList.size(); i++) {
            jsonStrBuilder.append(MessageCovertUtil.coverMessageToJsonWithLocalData(msgList.get(i)));

            if(i != msgList.size() - 1) {
                jsonStrBuilder.append(",");
            }

        }

        jsonStrBuilder.append("]");
        return jsonStrBuilder.toString();
    }


    public static String covertMessageToJson(HasBodyMessage message) {
        Gson gson = GsonHelper.getMessageMapGson();
        Map<String, Object> maps = message.getMessageBody();
        return gson.toJson(maps);
    }

    public static String coverMessageToJsonWithLocalData(HasBodyMessage message) {
        Gson gson = GsonHelper.getMessageMapGson();
        Map<String, Object> maps = message.getMessageBody();
        if(message instanceof FileTransferChatMessage) {
            FileTransferChatMessage fileTransferChatMessage = (FileTransferChatMessage) message;
            maps.put(FileTransferChatMessage.LOCAL_FILE_STATUS, fileTransferChatMessage.fileStatus);
            maps.put(FileTransferChatMessage.LOCAL_FILE_PATH, fileTransferChatMessage.filePath);
        }

        return gson.toJson(maps);
    }

    @Nullable
    public static Object getObject(Map<String, Object> jsonMap, String key) {
        Object object = null;
        if(jsonMap.containsKey(key)) {
            object = jsonMap.get(key);
        }
        return object;
    }

    /**
     * 解析body 字段里 string的值
     * @param jsonMap
     * @param key
     * @return value
     * */
    public static String getString(Map<String, Object> jsonMap, String key) {
        String value = StringUtils.EMPTY;
        if(jsonMap.containsKey(key)) {
            value = (String) jsonMap.get(key);
        }
        return value;
    }

    public static long getLong(Map<String, Object> jsonMap, String key) {
        long valLong = 0;
        try {
            if (jsonMap.containsKey(key)) {
                valLong = DoubleUtil.toLong((Double) jsonMap.get(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return valLong;
    }

    public static Map transStringToMap(String mapString){
        Map map = new HashMap();
        java.util.StringTokenizer items;
        for(StringTokenizer entrys = new StringTokenizer(mapString, "^"); entrys.hasMoreTokens();
            map.put(items.nextToken(), items.hasMoreTokens() ? ((Object) (items.nextToken())) : null))
            items = new StringTokenizer(entrys.nextToken(), "'");
        return map;
    }

}
