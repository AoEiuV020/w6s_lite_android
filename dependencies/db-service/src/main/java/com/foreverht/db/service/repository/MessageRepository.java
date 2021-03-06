package com.foreverht.db.service.repository;

import android.content.Context;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.util.LruCache;

import com.foreverht.cache.MessageCache;
import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.MessageDBHelper;
import com.foreverht.db.service.dbHelper.UnreadMessageDbHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.chat.SimpleMessageData;
import com.foreveross.atwork.infrastructure.model.user.UserHandleBasic;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.Message;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.HideEventMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.chat.BasicMsgHelper;
import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType.ANNO_FILE;
import static com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType.ANNO_IMAGE;
import static com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType.FILE;
import static com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType.IMAGE;
import static com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType.VIDEO;


public class  MessageRepository extends W6sBaseRepository {


    private static final MessageRepository sMessageRepository = new MessageRepository();




    /**
     * ???????????????????????????????????????????????????????????????, ????????? write ack ???, ??????????????????????????????????????????,
     * ???????????????????????????, ???????????????????????????????????????, ?????????????????????????????????.
     * */
    private final LruCache<String, Long> mDeliveryTimeCalibrateMap = new LruCache<>(1000);


    public static MessageRepository getInstance() {
        return sMessageRepository;
    }


    public void setDeliveryTimeCalibrating(String msgId, long deliveryTime) {
        if(0 >= deliveryTime) {
            return;
        }

        mDeliveryTimeCalibrateMap.put(msgId, deliveryTime);
    }



    public List<ChatPostMessage> searchMessages(final Context context, final String identifier, final String searchValue) {

        List<ChatPostMessage> chatPostMessages = new ArrayList<>();
        String messageTable = Message.getMessageTableName(identifier);
        String sql = "select * from " + messageTable + " where searchable_text_ like ? and status_ not in ( " + ChatStatus.UnDo.intValue() + ", " + ChatStatus.Hide.intValue() + ")";

        sql = tailMessagePullLatestTimeLimitPart(identifier, sql);

        Cursor cursor = null;
        try {

            cursor = getReadableDatabase().rawQuery(sql, new String[]{"%" + searchValue + "%"});

            while (cursor.moveToNext()) {
                //?????????????????????????????????
                ChatPostMessage chatPostMessage = MessageDBHelper.fromCursor(context, cursor);
                if (chatPostMessage != null) {
                    chatPostMessages.add(chatPostMessage);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return chatPostMessages;
    }

    public List<ChatPostMessage> searchMessagesByMessageType(final Context context, final String identifier, final String searchValue, final String messageType) {

        List<ChatPostMessage> chatPostMessages = new ArrayList<>();
        String messageTable = Message.getMessageTableName(identifier);
        String sql = "";
        if (messageType == FILE) {
            sql = "select * from " + messageTable + " where searchable_text_ like ? and (" +
                    MessageDBHelper.DBColumn.BODY_TYPE + " = ? or " + MessageDBHelper.DBColumn.BODY_TYPE + " = ?) and " +
                    "status_ not in ( " + ChatStatus.UnDo.intValue() + ", " + ChatStatus.Hide.intValue() + ")  order by delivery_time_ desc";
        } else {
            sql = "select * from " + messageTable + " where searchable_text_ like ? and " +
                    MessageDBHelper.DBColumn.BODY_TYPE + " = ? and " +
                    "status_ not in ( " + ChatStatus.UnDo.intValue() + ", " + ChatStatus.Hide.intValue() + ")  order by delivery_time_ desc";
        }

        sql = tailMessagePullLatestTimeLimitPart(identifier, sql);

        Cursor cursor = null;
        try {
            if (messageType == FILE) {
                cursor = getReadableDatabase().rawQuery(sql, new String[]{"%" + searchValue + "%", messageType, ANNO_FILE});
            } else {
                cursor = getReadableDatabase().rawQuery(sql, new String[]{"%" + searchValue + "%", messageType});
            }


            while (cursor.moveToNext()) {
                //?????????????????????????????????
                ChatPostMessage chatPostMessage = MessageDBHelper.fromCursor(context, cursor);
                if (chatPostMessage != null) {
                    chatPostMessages.add(chatPostMessage);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return chatPostMessages;
    }

    public List<ChatPostMessage> searchMediaMessages(final Context context, final String identifier) {

        List<ChatPostMessage> chatPostMessages = new ArrayList<>();
        String messageTable = Message.getMessageTableName(identifier);
        String sql = "select * from " + messageTable + " where ("+ MessageDBHelper.DBColumn.BODY_TYPE + " = ? or " +
                MessageDBHelper.DBColumn.BODY_TYPE + " = ? or " + MessageDBHelper.DBColumn.BODY_TYPE + " = ? ) " +
                "and status_ not in ( " + ChatStatus.UnDo.intValue() + ", " + ChatStatus.Hide.intValue() + ")  order by delivery_time_ desc";

        Cursor cursor = null;
        try {

            cursor = getReadableDatabase().rawQuery(sql, new String[]{IMAGE, VIDEO, ANNO_IMAGE});

            while (cursor.moveToNext()) {
                //?????????????????????????????????
                ChatPostMessage chatPostMessage = MessageDBHelper.fromCursor(context, cursor);
                if (chatPostMessage != null) {
                    chatPostMessages.add(chatPostMessage);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return chatPostMessages;
    }

    public List<SimpleMessageData> searchAllMessageIds(final Context context, final String identifier) {

        List<SimpleMessageData> simpleMessages = new ArrayList<>();
        String messageTable = Message.getMessageTableName(identifier);
        String sql = "SELECT " + MessageDBHelper.DBColumn.MSG_ID + ", " + MessageDBHelper.DBColumn.DELIVERY_TIME + ", " + MessageDBHelper.DBColumn.STATUS + " FROM " + messageTable +
                " WHERE status_ NOT IN ( " + ChatStatus.UnDo.intValue() + ", " + ChatStatus.Hide.intValue() + ") ORDER BY " + MessageDBHelper.DBColumn.DELIVERY_TIME + " ASC";

        Cursor cursor = null;
        try {

            cursor = getReadableDatabase().rawQuery(sql, new String[]{});
            int index = -1;
            while (cursor.moveToNext()) {
                //?????????????????????????????????
                SimpleMessageData data = new SimpleMessageData();
                if ((index = cursor.getColumnIndex(MessageDBHelper.DBColumn.MSG_ID)) != -1) {
                    data.setMessageId(cursor.getString(index));
                }
                if ((index = cursor.getColumnIndex(MessageDBHelper.DBColumn.DELIVERY_TIME)) != -1) {
                    data.setMessageTime(cursor.getLong(index));
                }
                simpleMessages.add(data);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return simpleMessages;
    }

    protected String tailMessagePullLatestTimeLimitPart(String identifier, String sql) {
        String messagePullLatestTimeLimitPart = null;
        long messagePullLatestTime = DomainSettingsManager.getInstance().getMessagePullLatestTime();
        if(-1 < messagePullLatestTime) {


//            + " or " +"( delivery_time_ >= (select min(delivery_time_) from " + messageTable + " where read_  = " + ReadStatus.Unread.intValue() + " and from_ != " + getInStringParams(loginUserId) + " and status_ not in (" + ChatStatus.UnDo.intValue() + ", " + ChatStatus.Hide.intValue()  + ")))";
            String corePart = "delivery_time_ >= " + messagePullLatestTime;


            long firstUnreadMsgTime = queryFirstUnreadMsgTime(identifier);
            if(0 < firstUnreadMsgTime) {
                corePart += " or delivery_time_ >= " +  firstUnreadMsgTime;
            }

            corePart = " ( " + corePart + " ) ";

            if(!sql.contains("where")) {
                messagePullLatestTimeLimitPart = " where " + corePart;

            } else {
                messagePullLatestTimeLimitPart = " and " + corePart;

            }
        }

        if(null != messagePullLatestTimeLimitPart) {
            sql += messagePullLatestTimeLimitPart;
        }
        return sql;
    }


    /**
     * ??????????????????????????????????????????
     *
     * @param msgId
     * @return
     */
    public boolean messageSended(String identifier, String msgId) {
        String messageTable = Message.getMessageTableName(identifier);
        createMessageTableIfNecessary(identifier);

        String sql = "update " + messageTable + " set status_ = 0  where msg_id_ = ?";
        getWritableDatabase().execSQL(sql, new String[]{msgId});
        return true;
    }

    /**
     * ????????????
     * */
    public void undoMessage(Context context, UndoEventMessage undoEventMessage) {
        UserHandleBasic chatUser = BasicMsgHelper.getChatUser(undoEventMessage);
        undoMessage(context, chatUser.mUserId, undoEventMessage);
        //????????????????????????????????????????????????
        MessageAppRepository.getInstance().undoMessage(context, chatUser.mUserId, undoEventMessage);
    }

    public void undoMessage(Context context, String identifier, UndoEventMessage undoEventMessage) {
//        String messageTable = Message.getMessageTableName(identifier);
        createMessageTableIfNecessary(identifier);

        List<ChatPostMessage> messageList = queryMessages(context, identifier, undoEventMessage.mEnvIds);
        for(ChatPostMessage message : messageList) {
            message.setChatStatus(ChatStatus.UnDo);
            message.undoSuccessTime = undoEventMessage.deliveryTime;
        }

        batchInsertMessages(messageList);
//        String sql = "update " + messageTable + " set status_ = "
//                + ChatStatus.UnDo.intValue() + "  where msg_id_ in (" + getInStringParams(undoEventMessage.mEnvIds) + ")";
//
//        getWritableDatabase().execSQL(sql, new String[]{});
    }

    /**
     * ????????????
     * */
    public void hideMessage(HideEventMessage hideEventMessage) {
        UserHandleBasic chatUser = BasicMsgHelper.getChatUser(hideEventMessage);
        String messageTable = Message.getMessageTableName(chatUser.mUserId);
        createMessageTableIfNecessary(chatUser.mUserId);

        String sql = "update " + messageTable + " set status_ = "
                + ChatStatus.Hide.intValue() + "  where msg_id_ in (" + getInStringParams(hideEventMessage.mEnvIds) + ")";
        getWritableDatabase().execSQL(sql, new String[]{});
    }



    public Set<String> queryMsgIds(String chatId, long beginTime, long endTime) {
        String messageTable = Message.getMessageTableName(chatId);
        String sql = "select " + MessageDBHelper.DBColumn.MSG_ID + " from " + messageTable
                + " where " + MessageDBHelper.DBColumn.DELIVERY_TIME +  " >= ? and " + MessageDBHelper.DBColumn.DELIVERY_TIME + " <= ?";

        Set<String> msgIdSet = new HashSet<>();

        if (tableExists(Message.MESSAGE_TABLE_PRE + chatId)) {
            Cursor cursor = null;
            try {
                cursor = getReadableDatabase().rawQuery(sql, new String[]{beginTime + "", endTime + ""});
                while (cursor.moveToNext()) {
                    msgIdSet.add(cursor.getString(0));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else {
            createMessageTableIfNecessary(chatId);
        }

        return msgIdSet;

    }


    /**
     * ???????????????????????????????????????
     * @param identifier
     * @param fixIdentifier
     * @return
     */
    public List<ChatPostMessage> queryFixMessages(Context context, String identifier, String fixIdentifier) {
        String messageTable = Message.getMessageTableName(identifier);
        String orderPart = " order by delivery_time_ desc ";
        String sql = "select * from " + messageTable + " where delivery_time_ >= (select min(delivery_time_) from " + messageTable + " where msg_id_ = ? and status_ != ?) and status_ != ?";

        sql = tailMessagePullLatestTimeLimitPart(identifier, sql);

        sql += orderPart;

        List<ChatPostMessage> messages = new ArrayList<>();
        //????????????ID???????????????
        if (tableExists(Message.MESSAGE_TABLE_PRE + identifier)) {
            Cursor cursor = null;
            try {
                cursor = getReadableDatabase().rawQuery(sql, new String[]{fixIdentifier, ChatStatus.Hide.intValue() + "", ChatStatus.Hide.intValue() + ""});
                while (cursor.moveToNext()) {
                    ChatPostMessage message = MessageDBHelper.fromCursor(context, cursor);
                    if (message != null) {
                        messages.add(message);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else {
            createMessageTableIfNecessary(identifier);
        }

        if (messages.size() < 20) {
            messages.clear();
            return queryLastTwentyMessages(context, identifier, -1);
        }
        return messages;
    }

    /**
     * ?????????????????????
     *
     * @param identifier
     * @return
     */
    public List<ChatPostMessage> queryLastMessageUnread(Context context, String identifier) {
        String messageTable = Message.getMessageTableName(identifier);
        String orderPart = " order by delivery_time_ desc ";
        String sql = "select * from " + messageTable + " where delivery_time_ >= (select min(delivery_time_) from " + messageTable + " where read_  = " + ReadStatus.Unread.intValue() + " and status_ != " + ChatStatus.Hide.intValue()  +") and status_ != " + ChatStatus.Hide.intValue();

//        sql = tailMessagePullLatestTimeLimitPart(sql);

        sql += orderPart;

        List<ChatPostMessage> messages = new ArrayList<>();

        if (tableExists(Message.MESSAGE_TABLE_PRE + identifier)) {
            Cursor cursor = null;
            try {
                cursor = getReadableDatabase().rawQuery(sql, new String[]{});
                while (cursor.moveToNext()) {
                    ChatPostMessage message = MessageDBHelper.fromCursor(context, cursor);
                    if (message != null) {
                        messages.add(message);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else {
            createMessageTableIfNecessary(identifier);
        }
        return messages;
    }

    public long queryFirstUnreadMsgTime(String identifier) {
        String messageTable = Message.getMessageTableName(identifier);
        String loginUserId = LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext);
        long firstUnreadMsgTime = -1;
        if (tableExists(Message.MESSAGE_TABLE_PRE + identifier)) {

            String firstUnreadLimitSql = "select min(delivery_time_) from " + messageTable + " where read_  = " + ReadStatus.Unread.intValue() + " and from_ != " + getInStringParams(loginUserId) + " and status_ not in (" + ChatStatus.UnDo.intValue() + ", " + ChatStatus.Hide.intValue()  + ")";

            Cursor cursor = null;
            try {
                cursor = getReadableDatabase().rawQuery(firstUnreadLimitSql, new String[]{});
                if (cursor.moveToNext()) {
                    firstUnreadMsgTime = cursor.getLong(0);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

        }

        return firstUnreadMsgTime;
    }

    /**
     * */
    public List<ChatPostMessage> queryLastTwentyMessages(Context context, String identifier, long lastTimeStamp){
        return queryLastMessagesInLimitCount(context, identifier, lastTimeStamp, 20);
    }

    /**
     * ????????????????????????
     *
     * @param identifier
     * @param lastTimeStamp
     * @param limitCount ?????????????????????
     * @return messages
     */
    public List<ChatPostMessage> queryLastMessagesInLimitCount(Context context, String identifier, long lastTimeStamp, int limitCount) {
        String messageTable = Message.getMessageTableName(identifier);
        String sql = null;
        String orderPart = " order by delivery_time_ desc limit " + limitCount + " offset 0 ";


        if (-1 == lastTimeStamp) {
            sql = "select * from " + messageTable + " where status_ != " + ChatStatus.Hide.intValue();
        } else {
            sql = "select * from " + messageTable + " where delivery_time_ < " + lastTimeStamp + " and status_  != " + ChatStatus.Hide.intValue();
        }

        sql = tailMessagePullLatestTimeLimitPart(identifier, sql);

        sql += orderPart;


        List<ChatPostMessage> messages = new ArrayList<>();
        //?????????????????? limitCount ?????????
        if (tableExists(Message.MESSAGE_TABLE_PRE + identifier)) {
            Cursor cursor = null;
            try {
                cursor = getReadableDatabase().rawQuery(sql, new String[]{});
                while (cursor.moveToNext()) {
                    ChatPostMessage message = MessageDBHelper.fromCursor(context, cursor);
                    if (message != null) {
                        messages.add(message);
                    }

                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else {
            createMessageTableIfNecessary(identifier);
        }

        return messages;
    }



    public List<ChatPostMessage> queryImageTypeMessagesInLimitCount(Context context, String identifier, int limitCount) {
        String messageTable = Message.getMessageTableName(identifier);
        String sql = "select * from " + messageTable + " where status_ != " + ChatStatus.Hide.intValue() + " and status_ != " + ChatStatus.UnDo.intValue() + " and (body_type_ = '" +  IMAGE + "' or body_type_ = '" + BodyType.ANNO_IMAGE + "' " + " or  body_type_ = '" + VIDEO  +  "' )";
        String orderPart = " order by delivery_time_ desc ";

        if (-1 != limitCount) {
            orderPart += " limit " + limitCount + " offset 0 ";
        }


//        sql = tailMessagePullLatestTimeLimitPart(identifier, sql);

        sql += orderPart;


        List<ChatPostMessage> messages = new ArrayList<>();
        //?????????????????? limitCount ?????????
        if (tableExists(Message.MESSAGE_TABLE_PRE + identifier)) {
            Cursor cursor = null;
            try {
                cursor = getReadableDatabase().rawQuery(sql, new String[]{});
                while (cursor.moveToNext()) {
                    ChatPostMessage message = MessageDBHelper.fromCursor(context, cursor);
                    if (message != null) {
                        messages.add(message);
                    }

                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else {
            createMessageTableIfNecessary(identifier);
        }

        return messages;
    }



    /**
     * ??????????????? at_all ??????
     * */
    public List<ChatPostMessage> queryUnreadAtAllMessageList(Context context, String sessionId, List<String> unreadMsgId) {
        String messageTable = Message.getMessageTableName(sessionId);
        String orderPart = " order by delivery_time_ desc";
        String sql = "select * from " + messageTable +  " where msg_id_ in (" +  getInStringParams(unreadMsgId) + ") and status_ = " + ChatStatus.At_All.intValue();

//        sql = tailMessagePullLatestTimeLimitPart(sql);

        sql += orderPart;


        List<ChatPostMessage> messages = new ArrayList<>();
        //?????????????????? limitCount ?????????
        if (tableExists(Message.MESSAGE_TABLE_PRE + sessionId)) {
            Cursor cursor = null;
            try {
                cursor = getReadableDatabase().rawQuery(sql, new String[]{});
                while (cursor.moveToNext()) {
                    ChatPostMessage message = MessageDBHelper.fromCursor(context, cursor);
                    if (message != null) {
                        messages.add(message);
                    }

                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else {
            createMessageTableIfNecessary(sessionId);
        }

        return messages;

    }



    public List<ChatPostMessage> queryMessagesBetween2Points(Context context, String sessionId, List<String> earlyMsgIdList, String farMsgId, long farMsgDeliveryTime) {
        String messageTable = Message.getMessageTableName(sessionId);
        String earlyMsgDeliveryTimeSql = "select min(delivery_time_) from " + messageTable + " where msg_id_ in (" + getInStringParams(earlyMsgIdList) + ") and status_ != " + ChatStatus.Hide.intValue();


        return queryMessagesBetween2Points(context, sessionId, earlyMsgDeliveryTimeSql, farMsgId, farMsgDeliveryTime);
    }

    /**
     * ????????????????????????????????????
     *
     * @param context
     * @param sessionId
     * @param farMsgId
     * @param earlyMsgDeliveryTimeSql
     * */
    @NonNull
    public List<ChatPostMessage> queryMessagesBetween2Points(Context context, String sessionId, String earlyMsgDeliveryTimeSql, String farMsgId, long farMsgDeliveryTime) {
        String messageTable = Message.getMessageTableName(sessionId);


        String farMsgDeliveryTimeUsing;
        if(-1 == farMsgDeliveryTime) {
            farMsgDeliveryTimeUsing = "select delivery_time_ from " + messageTable + " where msg_id_ = " + getInStringParams(farMsgId) + " and status_ != " + ChatStatus.Hide.intValue();
        } else {
            farMsgDeliveryTimeUsing = farMsgDeliveryTime + "";
        }

        String orderPart = " order by delivery_time_ desc ";
        String sql = "select * from " + messageTable + " where delivery_time_ >= (" + earlyMsgDeliveryTimeSql + ") and delivery_time_ <= (" +  farMsgDeliveryTimeUsing  + ") and status_ != " + ChatStatus.Hide.intValue();


        sql = tailMessagePullLatestTimeLimitPart(sessionId, sql);

        sql += orderPart;


        LogUtil.e("queryMessagesBetween2Points sql -> " + sql);

        List<ChatPostMessage> messages = new ArrayList<>();
        //?????????????????? limitCount ?????????
        if (tableExists(Message.MESSAGE_TABLE_PRE + sessionId)) {
            Cursor cursor = null;
            try {
                cursor = getReadableDatabase().rawQuery(sql, new String[]{});
                while (cursor.moveToNext()) {
                    ChatPostMessage message = MessageDBHelper.fromCursor(context, cursor);
                    if (message != null) {
                        messages.add(message);
                    }

                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else {
            createMessageTableIfNecessary(sessionId);
        }

        ChatPostMessage removedMsg = null;
        for(ChatPostMessage chatPostMessage : messages) {
            if(farMsgId.equals(chatPostMessage.deliveryId)) {
                removedMsg = chatPostMessage;
                break;
            }
        }

        if(null != removedMsg) {
            messages.remove(removedMsg);
        }


        return messages;
    }

    /**
     * ??????????????????
     * */
    public List<ChatPostMessage> queryLastMessagesUnreadInLimitCount(Context context, String identifier, long lastTimeStamp, int limitCount, boolean includeLocalReadMsg) {
        String messageTable = Message.getMessageTableName(identifier);
        String sql = "select * from " + messageTable + " where ";

        if(-1 != lastTimeStamp) {
            sql += "delivery_time_ < " + lastTimeStamp + " and ";
        }

        sql += "status_ != " + ChatStatus.Hide.intValue() + " and ";

        String orderPart = " order by delivery_time_ desc";
        if(includeLocalReadMsg) {
            sql += " read_  != " + ReadStatus.AbsolutelyRead.intValue();
        } else {
            sql += " read_  = " + ReadStatus.Unread.intValue();

        }


//        sql = tailMessagePullLatestTimeLimitPart(sql);

        sql += orderPart;



        if(-1 != limitCount) {
            sql += " limit " + limitCount + " offset 0 ";
        }
        List<ChatPostMessage> messages = new ArrayList<>();
        //?????????????????? limitCount ?????????
        if (tableExists(Message.MESSAGE_TABLE_PRE + identifier)) {
            Cursor cursor = null;
            try {
                cursor = getReadableDatabase().rawQuery(sql, new String[]{});
                while (cursor.moveToNext()) {
                    ChatPostMessage message = MessageDBHelper.fromCursor(context, cursor);
                    if (message != null) {
                        messages.add(message);
                    }

                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else {
            createMessageTableIfNecessary(identifier);
        }

        return messages;
    }


    public boolean existMessage(String identifier, String msgId) {
        int count = 0;
        String messageTable = Message.getMessageTableName(identifier);
        if (tableExists(Message.MESSAGE_TABLE_PRE + identifier)) {
            String querySQL = "select count(*) as count from " + messageTable + " where " + MessageDBHelper.DBColumn.MSG_ID + "='" + msgId + "'";

            Cursor cursor = null;
            try {
                cursor = getReadableDatabase().rawQuery(querySQL, new String[]{});
                if (cursor.moveToNext()) {
                    count = cursor.getInt(cursor.getColumnIndex("count"));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return 1 <= count;

    }


    /**
     * ????????????????????????
     * @param identifier
     * @return
     */
    public List<ChatPostMessage> queryLatestMessage(Context context, String identifier) {
        List<ChatPostMessage> messages = new ArrayList<>();
        String messageTable = Message.getMessageTableName(identifier);
        String orderPart = " order by delivery_time_ desc limit 1 offset 0";
        String sql = "select * from " + messageTable + " where status_ != " + ChatStatus.Hide.intValue();

        sql = tailMessagePullLatestTimeLimitPart(identifier, sql);

        sql += orderPart;

        if (tableExists(Message.MESSAGE_TABLE_PRE + identifier)) {
            Cursor cursor = null;
            try {
                cursor = getReadableDatabase().rawQuery(sql, new String[]{});
                while (cursor.moveToNext()) {
                    ChatPostMessage message = MessageDBHelper.fromCursor(context, cursor);
                    if (message != null) {
                        messages.add(message);
                    }

                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return messages;
    }

    public List<? extends ChatPostMessage> queryMsgList(Context context, String identifier, String to) {
        List<ChatPostMessage> chatPostMessageList = new ArrayList<>();

        String messageTable = Message.getMessageTableName(identifier);
        String sql = "select * from " + messageTable + " where status_ != " + ChatStatus.Hide.intValue();


        sql = tailMessagePullLatestTimeLimitPart(identifier, sql);

        if(!StringUtils.isEmpty(to)) {
            sql += " and to_ = ?";
        }

        if (tableExists(Message.MESSAGE_TABLE_PRE + identifier)) {
            Cursor cursor = null;
            try {
                String[] selectionArgs = null;

                if(!StringUtils.isEmpty(to)) {
                    selectionArgs = new String[1];
                    selectionArgs[0] = to;
                }


                cursor = getReadableDatabase().rawQuery(sql, selectionArgs);
                while (cursor.moveToNext()) {
                    ChatPostMessage message = MessageDBHelper.fromCursor(context, cursor);
                    chatPostMessageList.add(message);

                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return chatPostMessageList;


    }


    public List<ChatPostMessage> queryMsgListByFrom(Context context, String identifier, String from) {
        List<ChatPostMessage> chatPostMessageList = new ArrayList<>();

        String messageTable = Message.getMessageTableName(identifier);
        String sql = "select * from " + messageTable + " where status_ != " + ChatStatus.Hide.intValue()
                + " and status_ != " + ChatStatus.UnDo.intValue()  + " and from_ = ?"
                + " and " + MessageDBHelper.DBColumn.BODY_TYPE + " not in ('" + BodyType.Bing_Confirm  + "', '" +  BodyType.VOICE + "', '" + BodyType.FILE + "', '" + IMAGE + "')";

        sql = tailMessagePullLatestTimeLimitPart(identifier, sql);

        if (tableExists(Message.MESSAGE_TABLE_PRE + identifier)) {
            Cursor cursor = null;
            try {


                cursor = getReadableDatabase().rawQuery(sql, new String[]{from, });
                while (cursor.moveToNext()) {
                    ChatPostMessage message = MessageDBHelper.fromCursor(context, cursor);
                    chatPostMessageList.add(message);

                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return chatPostMessageList;
    }



    /**
     * ??????????????????
     * @param identifier
     * */
    public int queryMessageCount(String identifier) {
        int count = 0;
        String messageTable = Message.getMessageTableName(identifier);
        if (tableExists(Message.MESSAGE_TABLE_PRE + identifier)) {
            String querySQL = "select count(*) as count from " + messageTable ;

            Cursor cursor = null;
            try {
                cursor = getReadableDatabase().rawQuery(querySQL, new String[]{});
                if (cursor.moveToNext()) {
                    count = cursor.getInt(cursor.getColumnIndex("count"));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return count;
    }

    /**
     * ?????? to, ??????????????????
     * @param identifier
     * @param to
     * */
    public int queryMessageCount(String identifier, String to) {
        int count = 0;
        String messageTable = Message.getMessageTableName(identifier);
        if (tableExists(Message.MESSAGE_TABLE_PRE + identifier)) {
            String querySQL = "select count(*) as count from " + messageTable + " where " + MessageDBHelper.DBColumn.TO + " = ?";

            Cursor cursor = null;
            try {
                cursor = getReadableDatabase().rawQuery(querySQL, new String[]{to});
                if (cursor.moveToNext()) {
                    count = cursor.getInt(cursor.getColumnIndex("count"));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return count;
    }

    /**
     * ?????? to, ????????????????????????
     * @param identifier
     * @param to
     * */
    public int queryUnreadMessageCount(String identifier, String to) {
        int count = 0;
        String messageTable = Message.getMessageTableName(identifier);

        if (tableExists(Message.MESSAGE_TABLE_PRE + identifier)) {
            String querySQL = "select count(*) as count from " + messageTable + " where " + MessageDBHelper.DBColumn.TO + " = ? and " + MessageDBHelper.DBColumn.READ + " = " + ReadStatus.Unread.intValue();

            Cursor cursor = null;
            try {
                cursor = getReadableDatabase().rawQuery(querySQL, new String[]{to});
                if (cursor.moveToNext()) {
                    count = cursor.getInt(cursor.getColumnIndex("count"));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return count;
    }



    /**
     * ??????????????????
     * */
    public List<ChatPostMessage> queryUnreadMsgList(Context context, String sessionId, List<String> unreadIdList) {
        List<ChatPostMessage> chatPostMessageList = new ArrayList<>();

        String messageTable = Message.getMessageTableName(sessionId);
        StringBuilder sb = new StringBuilder("select * from ");
        sb.append(messageTable);
        sb.append(" where ( " + MessageDBHelper.DBColumn.MSG_ID + " in ( ").append(getInStringParams(unreadIdList)).append(" ) and status_ != ").append(ChatStatus.Hide.intValue()).append(" )");


        if (tableExists(Message.MESSAGE_TABLE_PRE + sessionId)) {
            Cursor cursor = null;
            try {
                cursor = getReadableDatabase().rawQuery(sb.toString(), new String[]{});
                while (cursor.moveToNext()) {
                    ChatPostMessage message = MessageDBHelper.fromCursor(context, cursor);
                    chatPostMessageList.add(message);

                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return chatPostMessageList;


    }

    /**
     * ??????????????????, ?????? join ??????, ????????????????????????
     * */
    public List<ChatPostMessage> queryUnreadMsgList(Context context, String sessionId) {
        List<ChatPostMessage> chatPostMessageList = new ArrayList<>();

        String messageTable = Message.getMessageTableName(sessionId);
        StringBuilder sb = new StringBuilder("select * from ");
        sb.append(messageTable);
        sb.append(" inner join ").append(UnreadMessageDbHelper.TABLE_NAME).append(" on ").append(W6sBaseRepository.getDetailDBColumn(Message.MESSAGE_TABLE_PRE + sessionId, MessageDBHelper.DBColumn.MSG_ID)).append("=").append(UnreadMessageDbHelper.getDetailDBColumn(UnreadMessageDbHelper.DBColumn.MSG_ID));
        sb.append(" where ( ").append(UnreadMessageDbHelper.getDetailDBColumn(UnreadMessageDbHelper.DBColumn.CHAT_ID)).append("=?").append(" and status_ != ").append(ChatStatus.Hide.intValue()).append(" )");

        String sql = sb.toString();



        if (tableExists(Message.MESSAGE_TABLE_PRE + sessionId)) {
            Cursor cursor = null;
            try {
                cursor = getReadableDatabase().rawQuery(sql, new String[]{sessionId});
                while (cursor.moveToNext()) {
                    ChatPostMessage message = MessageDBHelper.fromCursor(context, cursor);
                    chatPostMessageList.add(message);

                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return chatPostMessageList;


    }


    public List<ChatPostMessage> queryMessages(Context context, String identifier, List<String> msgIds) {
        List<ChatPostMessage> messageList = new ArrayList<>();

        if (ListUtil.isEmpty(msgIds)) {
            return messageList;
        }


        String messageTable = Message.getMessageTableName(identifier);
        String sql = "select * from " + messageTable + " where msg_id_ in (" + getInStringParams(msgIds) + ")";

        if (tableExists(Message.MESSAGE_TABLE_PRE + identifier)) {
            Cursor cursor = null;
            try {
                cursor = getReadableDatabase().rawQuery(sql, new String[]{});
                while (cursor.moveToNext()) {
                    ChatPostMessage message = MessageDBHelper.fromCursor(context, cursor);
                    messageList.add(message);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return messageList;
    }

    @Nullable
    public ChatPostMessage queryMessage(Context context, String identifier, String msgId) {
        if(StringUtils.isEmpty(msgId)) {
            return null;
        }


        String messageTable = Message.getMessageTableName(identifier);
        String sql = "select * from " + messageTable + " where msg_id_ = ?";
        ChatPostMessage message = null;

        if (tableExists(Message.MESSAGE_TABLE_PRE + identifier)) {
            Cursor cursor = null;
            try {
                cursor = getReadableDatabase().rawQuery(sql, new String[]{msgId});
                while (cursor.moveToNext()) {
                    message = MessageDBHelper.fromCursor(context, cursor);

                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return message;
    }


    /**
     * ??????????????????
     *
     * @param chatPostMessages
     * @return
     */
    public boolean batchRemoveMessage(List<ChatPostMessage> chatPostMessages) {

        boolean result = false;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();

            for (ChatPostMessage chatPostMessage : chatPostMessages) {
                UserHandleBasic chatUser = BasicMsgHelper.getChatUser(chatPostMessage);
                String messageTable = Message.getMessageTableName(chatUser.mUserId);
                String deleteSQL = "delete from " + messageTable + " where msg_id_ = ?";
                sqLiteDatabase.execSQL(deleteSQL, new String[]{chatPostMessage.deliveryId});

                //remove cache
                MessageCache.getInstance().removeMessage(chatUser.mUserId, chatPostMessage.deliveryId);
            }

            sqLiteDatabase.setTransactionSuccessful();
            result = true;

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            sqLiteDatabase.endTransaction();
        }
        return result;
    }


    public boolean deleteMessages(String identifier, long triggerDeleteTime) {
        String messageTable = Message.getMessageTableName(identifier);

//        String loginUserId = LoginUserInfo.getInstance().getLoginUserId(BaseApplication.baseContext);

//        String firstUnreadLimitSql = "( delivery_time_ < (select min(delivery_time_) from " + messageTable + " where read_  = " + ReadStatus.Unread.intValue() + " and from_ != " + getInStringParams(loginUserId) + " and status_ not in (" + ChatStatus.UnDo.intValue() + ", " + ChatStatus.Hide.intValue()  + ")))";


        String whereClause = "delivery_time_ < ?";

        long firstUnreadMsgTime = queryFirstUnreadMsgTime(identifier);
        if(0 < firstUnreadMsgTime) {
            whereClause += " and delivery_time_ < " + firstUnreadMsgTime;
        }


        LogUtil.e("deleteMessages  sql -> " + whereClause);

        long result = getWritableDatabase().delete(messageTable, whereClause, new String[]{triggerDeleteTime + ""});


        LogUtil.e("deleteMessages result  -> " + result);


        return 0 < result;
    }



    /**
     * ??????????????????
     */
    public boolean batchRemoveMessage(Map<String, List<String>> msgIdMap) {

        boolean result = false;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();

            for(Map.Entry<String, List<String>> entry : msgIdMap.entrySet()) {
                String sessionId = entry.getKey();
                List<String> msgIdList = entry.getValue();

                for (String msgId : msgIdList) {
                    String messageTable = Message.getMessageTableName(sessionId);
                    String deleteSQL = "delete from " + messageTable + " where msg_id_ = ?";
                    sqLiteDatabase.execSQL(deleteSQL, new String[]{msgId});
                }

            }

            sqLiteDatabase.setTransactionSuccessful();
            result = true;

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            sqLiteDatabase.endTransaction();
        }
        return result;
    }


    public boolean updateMessage(ChatPostMessage message){
        UserHandleBasic chatUser = BasicMsgHelper.getChatUser(message);

        String messageTable = Message.getMessageTableName(chatUser.mUserId);
        createMessageTableIfNecessary(chatUser.mUserId);

        String whereClause = MessageDBHelper.DBColumn.MSG_ID + "=?";
        long result = getWritableDatabase().updateWithOnConflict(messageTable,
                MessageDBHelper.getContentValues(message, mDeliveryTimeCalibrateMap.get(message.deliveryId)), whereClause, new String[]{ message.deliveryId },
                SQLiteDatabase.CONFLICT_REPLACE);

        return result != -1;
    }
    /**
     * ??????????????????
     *
     * @param message
     * @return
     */
    public boolean insertOrUpdateMessage(Context context, ChatPostMessage message) {
        UserHandleBasic chatUser = BasicMsgHelper.getChatUser(message);
        return insertOrUpdateMessage(context, chatUser.mUserId, message);
    }

    public boolean insertDropboxOverdueMessage(Context context, ChatPostMessage message) {
        return insertOrUpdateMessage(context, Session.DROPBOX_OVERDUE_REMIND, message);
    }


    public boolean insertOrUpdateMessage(Context context, String identifier, ChatPostMessage message) {
        if(message.mTargetScope == ChatPostMessage.TARGET_SCOPE_NEWS_SUMMARY){
            return false;
        }
        String messageTable = Message.getMessageTableName(identifier);
        createMessageTableIfNecessary(identifier);

        long result = getWritableDatabase().insertWithOnConflict(
                messageTable,
                null,
                MessageDBHelper.getContentValues(message, mDeliveryTimeCalibrateMap.get(message.deliveryId)), SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }


    public boolean isMessageExist(Context context, String identifier, ChatPostMessage message){
        boolean isMessageExist = false;
        String messageTable = Message.getMessageTableName(identifier);
        String sql = "select * from " + messageTable + " where msg_id_ = ?";
        Cursor cursor = null;
        try {

            cursor = getReadableDatabase().rawQuery(sql, new String[]{message.deliveryId});

            while (cursor.moveToNext()) {
                //?????????????????????????????????
                ChatPostMessage chatPostMessage = MessageDBHelper.fromCursor(context, cursor);
                if (chatPostMessage != null) {
                    isMessageExist = chatPostMessage.equals(message);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return isMessageExist;
    }


    /**
     * ??????????????????
     *
     * */
    public void batchInsertMessages(List<ChatPostMessage> messages) {
        boolean result = false;

        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();

            for (ChatPostMessage message : messages) {
                UserHandleBasic chatUser = BasicMsgHelper.getChatUser(message);
                String messageTable = Message.getMessageTableName(chatUser.mUserId);
                createMessageTableIfNecessary(chatUser.mUserId);

                db.insertWithOnConflict(
                        messageTable,
                        null,
                        MessageDBHelper.getContentValues(message, mDeliveryTimeCalibrateMap.get(message.deliveryId)), SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        }  finally {
            db.endTransaction();
            result = true;
        }
    }



    /**
     * ???????????????????????????
     * */
    public boolean updateReadStatus(String identifier, String msgId, ReadStatus readStatus) {
        String messageTable = Message.getMessageTableName(identifier);
        createMessageTableIfNecessary(identifier);

        String sql = "update " + messageTable + " set " + MessageDBHelper.DBColumn.READ + " = ? where " + MessageDBHelper.DBColumn.MSG_ID + " = ?";
        getWritableDatabase().execSQL(sql, new String[]{readStatus.intValue() + "", msgId});

        return true;
    }

    public void deleteAllMessages(String sessionIdentifier) {
        String messageTable = Message.getMessageTableName(sessionIdentifier);
        if (tableExists("message_" + sessionIdentifier)) {
            String deleteAllMessages = "delete  from " + messageTable;
            getWritableDatabase().execSQL(deleteAllMessages);
        }
    }

    public boolean cleanMessages2Threshold() {

        SQLiteDatabase db = getWritableDatabase();
        boolean result;

        try {
            db.beginTransaction();

            List<String> tableNameList = queryAllMessageTableName(db);
            for(String tableName : tableNameList) {

                long thresholdTime = queryThresholdDeliveryTime(db, tableName, AtworkConfig.CHAT_CONFIG.getCleanMessagesThreshold());

                LogUtil.e("thresholdTime  ->  " + thresholdTime + "  "+ tableName);

                String deleteSql = "delete from " + getInStringParams(tableName) + " where " + MessageDBHelper.DBColumn.DELIVERY_TIME + " < (" + thresholdTime + ")";

                db.execSQL(deleteSql);

            }

            db.setTransactionSuccessful();

            result = true;

        } catch (Exception e) {
            e.printStackTrace();

            result = false;

        } finally {
            db.endTransaction();
        }

        return result;
    }

    public long queryThresholdDeliveryTime(SQLiteDatabase db, String tableName, int threshold) {
        String selectMinInThreshold = "select min(" + MessageDBHelper.DBColumn.DELIVERY_TIME + ") from " + getInStringParams(tableName) + " where " + MessageDBHelper.DBColumn.DELIVERY_TIME +  " in ( select " + MessageDBHelper.DBColumn.DELIVERY_TIME + " from " + getInStringParams(tableName)  + " order by " + MessageDBHelper.DBColumn.DELIVERY_TIME + " desc limit " + threshold + ")";
        Cursor cursor = null;
        long deliveryTime = -1;
        try {
            cursor = db.rawQuery(selectMinInThreshold, null);
            if(cursor.moveToNext())  {
                deliveryTime = cursor.getLong(0);
            }

        } finally {
            if(null != cursor) {
                cursor.close();
            }
        }

        return deliveryTime;
    }



    public List<String> queryAllMessageTableName(@Nullable SQLiteDatabase db) {
        List<String> tableNameList = new ArrayList<>();

        String sql = "select name from sqlite_master where type = 'table' and name like 'message_%'";
        Cursor cursor = null;

        try {
            if(null == db) {
                db = getReadableDatabase();
            }
            cursor = db.rawQuery(sql, new String[]{});

            while (cursor.moveToNext()) {
                tableNameList.add(cursor.getString(0));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        tableNameList.remove("message_tags_");

        return tableNameList;
    }


    public void createMessageTableIfNecessary(String accountName) {
        try {
            if (!tableExists(Message.MESSAGE_TABLE_PRE + accountName)) {
                String createTableSql = String.format(MessageDBHelper.DBColumn.CREATE_TABLE_SQL, Message.getMessageTableName(accountName));
                SQLiteDatabase db = getWritableDatabase();
                db.execSQL(createTableSql, new String[]{});
                createIndex(db, accountName, MessageDBHelper.DBColumn.DELIVERY_TIME);


            }
        }catch (Exception e){
            Log.e("SQLite","?????????????????????"+accountName+":"+e.getLocalizedMessage());
        }

    }

    public void createIndex(@Nullable SQLiteDatabase db, String accountName, String indexDBColumn) {
        if(null == db) {
            db = getWritableDatabase();
        }
        String tableName = Message.MESSAGE_TABLE_PRE + accountName;

        String indexName = "index_" + tableName + "_" + indexDBColumn;

        try {
            db.execSQL("create index if not exists " + indexName + " on " + tableName + "(" + indexDBColumn + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
