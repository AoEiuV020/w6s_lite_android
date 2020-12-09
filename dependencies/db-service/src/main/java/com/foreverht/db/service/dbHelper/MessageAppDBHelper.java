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
import com.foreveross.atwork.infrastructure.model.newsSummary.NewsSummaryPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ChatSendType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem;
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
import com.foreveross.atwork.infrastructure.utils.ByteArrayToBase64TypeAdapter;
import com.foreveross.atwork.infrastructure.utils.ImageShowHelper;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.MessageCovertUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.chat.VoiceMsgHelper;
import com.foreveross.db.BaseDatabaseHelper;
import com.foreveross.db.SQLiteDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

/**
 * 所有服务号汇总数据
 */
public class MessageAppDBHelper implements DBHelper {

    private static final String TAG = MessageAppDBHelper.class.getSimpleName();
    public static final String TABLE_NAME = "app_message_";

    /**
     * 消息汇总，根据服务号id建表
     * @param identifier
     * @return
     */
    public static String getMessageAppTableName(String identifier) {
        return "'" + TABLE_NAME + identifier + "_'";
    }

    /**
     * 获得一个消息的contentValues，用于更新一个MESSAGE对象
     *
     * @param message
     * @return
     */
    public static ContentValues getContentValues(ChatPostMessage message, String chatID, String orgID) {
        MessageAppDBHelper.MessageHandleDbParameter messageHandleDbParameter = new MessageAppDBHelper.MessageHandleDbParameter(message).build();
        String bodyTypeInserted = messageHandleDbParameter.getBodyTypeInserted();
        String realMessageInserted = messageHandleDbParameter.getRealMessageInserted();

        ContentValues values = new ContentValues();
        values.put(MessageAppDBHelper.DBColumn.CHAT_ID, chatID);
        values.put(MessageAppDBHelper.DBColumn.ORG_ID, orgID);
        values.put(MessageAppDBHelper.DBColumn.MSG_ID, message.deliveryId);
        if (null != message.mBodyType) {
            values.put(MessageAppDBHelper.DBColumn.BODY_TYPE, bodyTypeInserted);
        }
        values.put(MessageAppDBHelper.DBColumn.FROM, message.from);
        values.put(MessageAppDBHelper.DBColumn.TO, message.to);
        values.put(MessageAppDBHelper.DBColumn.FROM_DOMAIN, message.mFromDomain);
        values.put(MessageAppDBHelper.DBColumn.TO_DOMAIN, message.mToDomain);
        if (null != message.mFromType) {
            values.put(MessageAppDBHelper.DBColumn.FROM_TYPE, message.mFromType.stringValue());

        }

        if (null != message.mToType) {
            values.put(MessageAppDBHelper.DBColumn.TO_TYPE, message.mToType.stringValue());

        }
        if (message.chatSendType != null) {
            values.put(MessageAppDBHelper.DBColumn.CHAT_SEND_OR_RECEIVE, message.chatSendType.intValue());

        } else {
            ChatSendType chatSendType = ChatSendType.parseFrom(BaseApplicationLike.baseContext, message.from);
            values.put(MessageAppDBHelper.DBColumn.CHAT_SEND_OR_RECEIVE, chatSendType.intValue());
        }

        long deliveryTime = message.deliveryTime;

        values.put(MessageAppDBHelper.DBColumn.DELIVERY_TIME, deliveryTime);

        values.put(MessageAppDBHelper.DBColumn.READ, message.read.intValue());

        putStatus(message, values);

        byte[] data;
        if (StringUtils.isEmpty(realMessageInserted)) {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().registerTypeAdapter(byte[].class, new ByteArrayToBase64TypeAdapter()).create();
            data = gson.toJson(message).getBytes();
        } else {
            data = realMessageInserted.getBytes();
        }
        values.put(MessageAppDBHelper.DBColumn.MSG_DATA, data);
        StringBuilder searchValue = new StringBuilder(StringUtils.EMPTY);
        if (message instanceof ArticleChatMessage) {
            ArticleChatMessage articleChatMessage = (ArticleChatMessage) message;
            for(ArticleItem articleItem : articleChatMessage.articles){
                searchValue.append(articleItem.title);
            }
            values.put(MessageAppDBHelper.DBColumn.SEARCH_TEXT, searchValue.toString());
        }else {
            values.put(MessageAppDBHelper.DBColumn.SEARCH_TEXT, message.getSearchAbleString());
        }
        return values;
    }

    private static void putStatus(ChatPostMessage message, ContentValues values) {
        if (message.isUndo()) {
            values.put(MessageAppDBHelper.DBColumn.STATUS, ChatStatus.UnDo.intValue());
        } else if (message.isHide()) {
            values.put(MessageAppDBHelper.DBColumn.STATUS, ChatStatus.Hide.intValue());

        } else if(ChatStatus.At_All.equals(message.chatStatus)) {
            values.put(MessageAppDBHelper.DBColumn.STATUS, ChatStatus.At_All.intValue());

        } else {
            //正在发送中的状态不存入数据库中，数据库中只有已发送和未发送两种状态
            if (ChatStatus.Sended.equals(message.chatStatus)) {
                values.put(MessageAppDBHelper.DBColumn.STATUS, ChatStatus.Sended.intValue());
            } else {
                values.put(MessageAppDBHelper.DBColumn.STATUS, ChatStatus.Not_Send.intValue());
            }
        }
    }

    /**
     * 从CURSOR中读取一个消息
     *
     * @param cursor
     * @return
     */
    public static NewsSummaryPostMessage fromCursor(Context context, Cursor cursor) {
        String bodyTypeRaw = cursor.getString(cursor.getColumnIndex(MessageAppDBHelper.DBColumn.BODY_TYPE));
        MessageAppDBHelper.MsgCovertParameter msgCovertParams = new MessageAppDBHelper.MsgCovertParameter(bodyTypeRaw).build();

        BodyType bodyType = msgCovertParams.getBodyType();
        boolean needConvertFromRawMsg = msgCovertParams.isNeedConvertFromRawMsg();

        ChatPostMessage chatPostMessage = getEmptyClassMessage(context, bodyType);

        if (chatPostMessage == null) {
            return null;
        }

        chatPostMessage = getContentMessageFromData(context, cursor, needConvertFromRawMsg, chatPostMessage);

        if (chatPostMessage == null) {
            return null;
        }

        processMessageFromCursor(context, cursor, chatPostMessage, bodyType);

        NewsSummaryPostMessage message = new NewsSummaryPostMessage();
        String chatIdRaw = cursor.getString(cursor.getColumnIndex(DBColumn.CHAT_ID));
        String orgIdRaw = cursor.getString(cursor.getColumnIndex(DBColumn.ORG_ID));
        message.setOrgId(orgIdRaw);
        message.setChatId(chatIdRaw);
        message.setChatPostMessage(chatPostMessage);

        return message;
    }

    private static void processMessageFromCursor(Context context, Cursor cursor, ChatPostMessage message, BodyType bodyType) {
        int index;
        if ((index = cursor.getColumnIndex(MessageAppDBHelper.DBColumn.MSG_ID)) != -1) {
            message.deliveryId = cursor.getString(index);
        }

        /*if ((index = cursor.getColumnIndex(MessageAppDBHelper.DBColumn.CHAT_ID)) != -1) {
            message.cha = cursor.getString(index);
        }*/

        if (BodyType.Voice.equals(bodyType)) {
            ((VoiceChatMessage) (message)).content = VoiceMsgHelper.readAudioContent(context, message.deliveryId);
        } else if (BodyType.Image.equals(bodyType)) {

//            ((ImageChatMessage) message).thumbnails = ImageShowHelper.getThumbnailImage(context, message.deliveryId);

        } else if (BodyType.Video.equals(bodyType)) {
            ((MicroVideoChatMessage) message).thumbnails = ImageShowHelper.getThumbnailImage(context, message.deliveryId);
        }

        if ((index = cursor.getColumnIndex(MessageAppDBHelper.DBColumn.FROM)) != -1) {
            message.from = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(MessageAppDBHelper.DBColumn.TO)) != -1) {
            message.to = cursor.getString(index);
        }


        if ((index = cursor.getColumnIndex(MessageAppDBHelper.DBColumn.CHAT_SEND_OR_RECEIVE)) != -1) {
            message.chatSendType = ChatSendType.toChatSendType(cursor.getInt(index));
        }

        if ((index = cursor.getColumnIndex(MessageAppDBHelper.DBColumn.DELIVERY_TIME)) != -1) {
            message.deliveryTime = cursor.getLong(index);
        }

        if ((index = cursor.getColumnIndex(MessageAppDBHelper.DBColumn.READ)) != -1) {
            message.read = ReadStatus.fromIntValue(cursor.getInt(index));
        }
        if ((index = cursor.getColumnIndex(MessageAppDBHelper.DBColumn.FROM_TYPE)) != -1) {
            message.mFromType = ParticipantType.toParticipantType(cursor.getString(index));
        }
        if ((index = cursor.getColumnIndex(MessageAppDBHelper.DBColumn.FROM_DOMAIN)) != -1) {
            message.mFromDomain = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(MessageAppDBHelper.DBColumn.TO_TYPE)) != -1) {
            message.mToType = ParticipantType.toParticipantType(cursor.getString(index));
        }
        if ((index = cursor.getColumnIndex(MessageAppDBHelper.DBColumn.FROM_DOMAIN)) != -1) {
            message.mToDomain = cursor.getString(index);
        }

        message.mBodyType = bodyType;

        makeChatStatus(cursor, message, bodyType);
    }

    private static void makeChatStatus(Cursor cursor, ChatPostMessage message, BodyType bodyType) {
        int index;
        if ((index = cursor.getColumnIndex(MessageAppDBHelper.DBColumn.STATUS)) != -1) {

            message.chatStatus = ChatStatus.fromIntValue(cursor.getInt(index));

            if (ChatStatus.Sending == message.chatStatus) {
                message.chatStatus = ChatStatus.Not_Send;

            }


        }
    }

    private static boolean isBingMsg(BodyType bodyType) {
        return BodyType.BingText == bodyType || BodyType.BingVoice == bodyType;
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

        if (BodyType.UnKnown.equals(bodyType)) {
            return new UnknownChatMessage();
        }
        return null;
    }

    /**
     * 从msg_data_里转换出消息出来, 旧版本未知消息需要使用{@link MessageCovertUtil#(Context, String)}转换出来,
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
        if ((index = cursor.getColumnIndex(MessageAppDBHelper.DBColumn.MSG_DATA)) != -1) {
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
        String systemTable = String.format(MessageAppDBHelper.DBColumn.CREATE_TABLE_SQL, TABLE_NAME);
        db.execSQL(systemTable);
        LogUtil.d(TAG, systemTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (200 > oldVersion) {
            String systemTable = String.format(MessageAppDBHelper.DBColumn.CREATE_TABLE_SQL, TABLE_NAME);
            BaseDatabaseHelper.createTable(db, systemTable);
            oldVersion = 200;
        }

    }

    private void createMessageDeliveryTimeIndex(SQLiteDatabase db) {
        try {

            long beginTime = System.currentTimeMillis();

            db.beginTransaction();

            List<String> allMsgDbTableName = MessageRepository.getInstance().queryAllMessageTableName(db);
            for(String tableName : allMsgDbTableName) {

                MessageRepository.getInstance().createIndex(db, tableName, MessageAppDBHelper.DBColumn.DELIVERY_TIME);


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

        /**
         * 会话id
         */
        public static final String CHAT_ID = "chat_id_";

        /**
         * 服务号id
         */
        public static final String ORG_ID = "org_id_";

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
                "chat_id_ text," +
                "org_id_ text," +
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
            bodyType = BodyType.makeParseBodyCompatible(null,bodyType);
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
                if (BodyType.UnKnown == message.mBodyType) {
                    UnknownChatMessage unknownChatMessage = (UnknownChatMessage) message;
                    bodyTypeInserted = getUnknownPrefix() + unknownChatMessage.mRealBodyType;
                    realMessageInserted = unknownChatMessage.mSourceMsg;

                } else {
                    bodyTypeInserted = message.mBodyType.stringValue();
                }
            }
            return this;
        }
    }
}