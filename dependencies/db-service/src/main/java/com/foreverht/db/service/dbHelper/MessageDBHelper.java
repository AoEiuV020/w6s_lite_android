package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.foreverht.db.service.repository.MessageRepository;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.chat.BingConfirmChatMessage;
import com.foreveross.atwork.infrastructure.model.chat.MeetingNoticeChatMessage;
import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage;
import com.foreveross.atwork.infrastructure.model.chat.VoipChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoFileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MicroVideoChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.MultipartChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ShareChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.StickerChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TemplateMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.UnknownChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.VoiceChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.anno.AnnoImageChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.reference.ReferenceMessage;
import com.foreveross.atwork.infrastructure.utils.ByteArrayToBase64TypeAdapter;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.MessageCovertUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.chat.VoiceMsgHelper;
import com.foreveross.db.SQLiteDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

/**
 * Created by lingen on 15/4/3.
 * Description:
 */
public class MessageDBHelper implements DBHelper {

    private static final String TAG = MessageDBHelper.class.getSimpleName();

    /**
     * 获得一个消息的contentValues，用于更新一个MESSAGE对象
     *
     * @param message
     * @return
     */
    public static ContentValues getContentValues(ChatPostMessage message, Long deliveryTimeCalibrating) {
        MessageHandleDbParameter messageHandleDbParameter = new MessageHandleDbParameter(message).build();
        String bodyTypeInserted = messageHandleDbParameter.getBodyTypeInserted();
        String realMessageInserted = messageHandleDbParameter.getRealMessageInserted();

        ContentValues values = new ContentValues();
        values.put(DBColumn.MSG_ID, message.deliveryId);
        if (null != message.mBodyType) {
            values.put(DBColumn.BODY_TYPE, bodyTypeInserted);
        }
        values.put(DBColumn.FROM, message.from);
        values.put(DBColumn.TO, message.to);
        values.put(DBColumn.FROM_DOMAIN, message.mFromDomain);
        values.put(DBColumn.TO_DOMAIN, message.mToDomain);
        if (null != message.mFromType) {
            values.put(DBColumn.FROM_TYPE, message.mFromType.stringValue());

        }

        if (null != message.mToType) {
            values.put(DBColumn.TO_TYPE, message.mToType.stringValue());

        }
        if (message.chatSendType != null) {
            values.put(DBColumn.CHAT_SEND_OR_RECEIVE, message.chatSendType.intValue());

        } else {
            ChatSendType chatSendType = ChatSendType.parseFrom(BaseApplicationLike.baseContext, message.from);
            values.put(DBColumn.CHAT_SEND_OR_RECEIVE, chatSendType.intValue());
        }

        long deliveryTime = message.deliveryTime;

        if(null != deliveryTimeCalibrating) {
            deliveryTime = deliveryTimeCalibrating;
        }
        values.put(DBColumn.DELIVERY_TIME, deliveryTime);

        values.put(DBColumn.READ, message.read.intValue());

        putStatus(message, values);

        byte[] data;
        if (StringUtils.isEmpty(realMessageInserted)) {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().registerTypeAdapter(byte[].class, new ByteArrayToBase64TypeAdapter()).create();
            data = gson.toJson(message).getBytes();
        } else {
            data = realMessageInserted.getBytes();
        }
        values.put(DBColumn.MSG_DATA, data);
        values.put(DBColumn.SEARCH_TEXT, message.getSearchAbleString());
        return values;
    }

    private static void putStatus(ChatPostMessage message, ContentValues values) {
        if (message.isUndo()) {
            values.put(DBColumn.STATUS, ChatStatus.UnDo.intValue());
        } else if (message.isHide()) {
            values.put(DBColumn.STATUS, ChatStatus.Hide.intValue());

        } else if(ChatStatus.At_All.equals(message.chatStatus)) {
            values.put(DBColumn.STATUS, ChatStatus.At_All.intValue());

        } else {
            //正在发送中的状态不存入数据库中，数据库中只有已发送和未发送两种状态
            if (ChatStatus.Sended.equals(message.chatStatus)) {
                values.put(DBColumn.STATUS, ChatStatus.Sended.intValue());
            } else {
                values.put(DBColumn.STATUS, ChatStatus.Not_Send.intValue());
            }
        }
    }

    /**
     * 从CURSOR中读取一个消息
     *
     * @param cursor
     * @return
     */
    public static ChatPostMessage fromCursor(Context context, Cursor cursor) {
        ChatPostMessage message = null;
        String bodyTypeRaw = cursor.getString(cursor.getColumnIndex(DBColumn.BODY_TYPE));
        MsgCovertParameter msgCovertParams = new MsgCovertParameter(bodyTypeRaw).build();

        BodyType bodyType = msgCovertParams.getBodyType();
        boolean needConvertFromRawMsg = msgCovertParams.isNeedConvertFromRawMsg();


        message = getEmptyClassMessage(context, bodyType);

        if (message == null) {
            return null;
        }

        message = getContentMessageFromData(context, cursor, needConvertFromRawMsg, message);

        if (message == null) {
            return null;
        }

        processMessageFromCursor(context, cursor, message, bodyType);

        return message;
    }

    private static void processMessageFromCursor(Context context, Cursor cursor, ChatPostMessage message, BodyType bodyType) {
        int index;
        if ((index = cursor.getColumnIndex(DBColumn.MSG_ID)) != -1) {
            message.deliveryId = cursor.getString(index);
        }

        if (BodyType.Voice.equals(bodyType)) {
            ((VoiceChatMessage) (message)).content = VoiceMsgHelper.readAudioContent(context, message.deliveryId);
        } else if (BodyType.Image.equals(bodyType)) {

//            ((ImageChatMessage) message).thumbnails = ImageShowHelper.getThumbnailImage(context, message.deliveryId);

        } else if (BodyType.Video.equals(bodyType)) {
            ((MicroVideoChatMessage) message).thumbnails = ImageShowHelper.getThumbnailImage(context, message.deliveryId);
        }

        if ((index = cursor.getColumnIndex(DBColumn.FROM)) != -1) {
            message.from = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.TO)) != -1) {
            message.to = cursor.getString(index);
        }


        if ((index = cursor.getColumnIndex(DBColumn.CHAT_SEND_OR_RECEIVE)) != -1) {
            message.chatSendType = ChatSendType.toChatSendType(cursor.getInt(index));
        }

        if ((index = cursor.getColumnIndex(DBColumn.DELIVERY_TIME)) != -1) {
            message.deliveryTime = cursor.getLong(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.READ)) != -1) {
            message.read = ReadStatus.fromIntValue(cursor.getInt(index));
        }
        if ((index = cursor.getColumnIndex(DBColumn.FROM_TYPE)) != -1) {
            message.mFromType = ParticipantType.toParticipantType(cursor.getString(index));
        }
        if ((index = cursor.getColumnIndex(DBColumn.FROM_DOMAIN)) != -1) {
            message.mFromDomain = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.TO_TYPE)) != -1) {
            message.mToType = ParticipantType.toParticipantType(cursor.getString(index));
        }
        if ((index = cursor.getColumnIndex(DBColumn.FROM_DOMAIN)) != -1) {
            message.mToDomain = cursor.getString(index);
        }

        message.mBodyType = bodyType;

        makeChatStatus(cursor, message, bodyType);
    }

    private static void makeChatStatus(Cursor cursor, ChatPostMessage message, BodyType bodyType) {
        int index;
        if ((index = cursor.getColumnIndex(DBColumn.STATUS)) != -1) {
            message.chatStatus = ChatStatus.fromIntValue(cursor.getInt(index));

            if (ChatStatus.Sending == message.chatStatus) {
                message.chatStatus = ChatStatus.Not_Send;

            }

        }
    }

    private static ChatPostMessage getEmptyClassMessage(Context context, BodyType bodyType) {

        if (BodyType.Text.equals(bodyType)) {
            return new TextChatMessage();

        }
        if (BodyType.Voice.equals(bodyType)) {
            VoiceChatMessage message = new VoiceChatMessage();
            message.content = VoiceMsgHelper.readAudioContent(context, message.deliveryId);
            return message;

        }
        if (BodyType.Image.equals(bodyType)) {
            ImageChatMessage message = new ImageChatMessage();
//            message.thumbnails = ImageShowHelper.getThumbnailImage(context, message.deliveryId);
            message.progress = 100;
            return message;

        }
        if (BodyType.File.equals(bodyType)) {
            FileTransferChatMessage message = new FileTransferChatMessage();
            message.thumbnail = ImageShowHelper.getThumbnailImage(context, message.deliveryId);
            return message;

        }
        if (BodyType.System.equals(bodyType)) {
            return new SystemChatMessage();

        }
        if (BodyType.Article.equals(bodyType)) {
            return new ArticleChatMessage();

        }
        if (BodyType.Share.equals(bodyType)) {
            return new ShareChatMessage();

        }
        if (BodyType.Video.equals(bodyType)) {
            return new MicroVideoChatMessage();

        }

        if (BodyType.Voip.equals(bodyType)) {
            return new VoipChatMessage();

        }
        if (BodyType.Multipart.equals(bodyType)) {
            return new MultipartChatMessage();
        }
        if (BodyType.Template.equals(bodyType)) {
            return new TemplateMessage();
        }

        if(BodyType.BingConfirm.equals(bodyType)) {
            return new BingConfirmChatMessage();
        }

        if(BodyType.MeetingNotice.equals(bodyType)) {
            return new MeetingNoticeChatMessage();
        }

        if (BodyType.Sticker.equals(bodyType) || BodyType.Face.equals(bodyType)) {
            StickerChatMessage stickerChatMessage = new StickerChatMessage();
            return stickerChatMessage;
        }


        if(BodyType.Quoted.equals(bodyType)) {
            return ReferenceMessage.generateReferenceMessage(bodyType);
        }

        if(BodyType.AnnoFile.equals(bodyType)) {
            return new AnnoFileTransferChatMessage();
        }

        if(BodyType.AnnoImage.equals(bodyType)) {
            return new AnnoImageChatMessage();
        }


        if (BodyType.UnKnown.equals(bodyType)) {
            return new UnknownChatMessage();
        }
        return null;
    }

    /**
     * 从msg_data_里转换出消息出来, 旧版本未知消息需要使用{@link MessageCovertUtil#covertJsonToMessage(Context, String)}转换出来,
     * 从而升级后获取消息成功
     *
     * @param context
     * @param cursor
     * @param needConvertFromRawMsg 是否通过 IM 元数据转换
     * @param message
     * @return 转换后的消息
     */
    private static ChatPostMessage getContentMessageFromData(Context context, Cursor cursor, boolean needConvertFromRawMsg, ChatPostMessage message) {
        int index;
        if ((index = cursor.getColumnIndex(DBColumn.MSG_DATA)) != -1) {
            byte[] data = cursor.getBlob(index);
            String dataStr = new String(data);

            if (message instanceof UnknownChatMessage || needConvertFromRawMsg) {
                message = (ChatPostMessage) MessageCovertUtil.covertJsonToMessage(dataStr);


            } else {
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().registerTypeAdapter(byte[].class, new ByteArrayToBase64TypeAdapter()).create();
                message = gson.fromJson(dataStr, message.getClass());
            }

        }
        return message;
    }

    private static String getUnknownPrefix() {
        return BodyType.UNKNOWN + "_";
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String systemTable = String.format(DBColumn.CREATE_TABLE_SQL, "system_message_");
        db.execSQL(systemTable);
        LogUtil.d(TAG, systemTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(19 < oldVersion) {

//            createMessageDeliveryTimeIndex(db);

            oldVersion = 19;
        }

    }

    private void createMessageDeliveryTimeIndex(SQLiteDatabase db) {
        try {

            long beginTime = System.currentTimeMillis();

            db.beginTransaction();

            List<String> allMsgDbTableName = MessageRepository.getInstance().queryAllMessageTableName(db);
            for(String tableName : allMsgDbTableName) {

                MessageRepository.getInstance().createIndex(db, tableName, DBColumn.DELIVERY_TIME);


                LogUtil.e("对 tableName: " + tableName + "  添加索引");
            }

            long endTime = System.currentTimeMillis();
            LogUtil.e("onUpgrade message tables -> " + (endTime - beginTime));


            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();

        }
    }


    public class DBColumn {

        /**
         * 消息ID
         */
        public static final String MSG_ID = "msg_id_";


        public static final String BODY_TYPE = "body_type_";

        public static final String FROM = "from_";

        public static final String TO = "to_";

        public static final String FROM_DOMAIN = "from_domain_";

        public static final String TO_DOMAIN = "to_domain_";

        public static final String FROM_TYPE = "from_type_";

        public static final String TO_TYPE = "to_type_";

        public static final String CHAT_SEND_OR_RECEIVE = "chat_send_or_receive_";

        public static final String DELIVERY_TIME = "delivery_time_";

        public static final String MSG_DATA = "msg_data_";

        public static final String SEARCH_TEXT = "searchable_text_";

        public static final String STATUS = "status_";

        public static final String READ = "read_";

        public static final String CREATE_TABLE_SQL = "create table %s (msg_id_ text primary key," +
                "from_ text not null," +
                "to_ text not null," +
                "from_domain_ text," +
                "to_domain_ text," +
                "from_type_ text," +
                "to_type_ text," +
                "body_type_ text," +
                "chat_send_or_receive_ integer not null," +
                "delivery_time_ integer not null," +
                "msg_data_ blob ," +
                "searchable_text_ text," +
                "read_ integer not null," +
                "status_ text)";
    }

    private static class MsgCovertParameter {
        private String bodyTypeRaw;
        private BodyType bodyType;
        private boolean needConvertFromRawMsg;

        public MsgCovertParameter(String bodyTypeRaw) {
            this.bodyTypeRaw = bodyTypeRaw;
        }

        public BodyType getBodyType() {
            return bodyType;
        }

        public boolean isNeedConvertFromRawMsg() {
            return needConvertFromRawMsg;
        }

        public MsgCovertParameter build() {
            needConvertFromRawMsg = false;

            if (bodyTypeRaw.startsWith(getUnknownPrefix())) {
                bodyTypeRaw = bodyTypeRaw.substring(getUnknownPrefix().length(), bodyTypeRaw.length());
                needConvertFromRawMsg = true;
            }

            bodyType = BodyType.toBodyType(bodyTypeRaw);


            if(BodyType.Quoted == bodyType) {
                needConvertFromRawMsg = true;
            }

            bodyType = BodyType.makeParseBodyCompatible(null, bodyType);
            return this;
        }
    }

    private static class MessageHandleDbParameter {
        private ChatPostMessage message;
        private String bodyTypeInserted;
        private String realMessageInserted;

        public MessageHandleDbParameter(ChatPostMessage message) {
            this.message = message;
        }

        public String getBodyTypeInserted() {
            return bodyTypeInserted;
        }

        public String getRealMessageInserted() {
            return realMessageInserted;
        }

        public MessageHandleDbParameter build() {
            bodyTypeInserted = StringUtils.EMPTY;
            realMessageInserted = StringUtils.EMPTY;
            if (null != message.mBodyType) {
                if (BodyType.UnKnown == message.mBodyType || message instanceof UnknownChatMessage) {
                    UnknownChatMessage unknownChatMessage = (UnknownChatMessage) message;
                    bodyTypeInserted = getUnknownPrefix() + unknownChatMessage.mRealBodyType;
                    realMessageInserted = unknownChatMessage.mSourceMsg;

                } else if(BodyType.Quoted == message.mBodyType) {
                    ReferenceMessage referenceMessage = (ReferenceMessage) message;
                    bodyTypeInserted = message.mBodyType.stringValue();

                    if(StringUtils.isEmpty(referenceMessage.mSourceMsg)) {
                        realMessageInserted = MessageCovertUtil.coverMessageToJsonWithLocalData(referenceMessage);

                    } else {
                        realMessageInserted = referenceMessage.mSourceMsg;

                    }


                } else {
                    bodyTypeInserted = message.mBodyType.stringValue();
                }
            }
            return this;
        }
    }
}
