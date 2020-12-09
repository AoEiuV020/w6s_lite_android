package com.foreverht.db.service.repository;

import android.content.Context;
import android.database.Cursor;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.MessageAppDBHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.newsSummary.NewsSummaryPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.Message;
import com.foreveross.atwork.infrastructure.newmessage.ReadStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.HideEventMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MessageAppRepository extends W6sBaseRepository {
    public static final int LIMIT_DEFAULT = 20;
    public static final int OFFSET_DEFAULT = 0;
    private static final MessageAppRepository sMessageAppRepository = new MessageAppRepository();
    /**
     * 用以维护校准更新本地数据库消息表时的时间戳, 因收到 write ack 后, 消息时间戳会跟服务器时间同步,
     * 为了避免因多线程时, 因消息入库时间无法控制顺序, 时间戳会被旧数据覆盖掉.
     */
    private final LruCache<String, Long> mDeliveryTimeCalibrateMap = new LruCache<>(1000);

    public static MessageAppRepository getInstance() {
        return sMessageAppRepository;
    }

    public void setDeliveryTimeCalibrating(String msgId, long deliveryTime) {
        if (0 >= deliveryTime) {
            return;
        }

        mDeliveryTimeCalibrateMap.put(msgId, deliveryTime);
    }

    public void createMessageTableIfNecessary(String identifier) {
        try {
            if (!tableExists(MessageAppDBHelper.getMessageAppTableName(identifier))) {
                String createTableSql = String.format(MessageAppDBHelper.DBColumn.CREATE_TABLE_SQL, MessageAppDBHelper.getMessageAppTableName(identifier));
                SQLiteDatabase db = getWritableDatabase();
                db.execSQL(createTableSql, new String[]{});
                createIndex(db, identifier, MessageAppDBHelper.DBColumn.DELIVERY_TIME);
            }
        }catch (Exception e){
            Log.e("SQLite","创建消息汇总表失败"+identifier+":"+e.getLocalizedMessage());
        }
    }

    public void createIndex(@Nullable SQLiteDatabase db, String identifier, String indexDBColumn) {
        if(null == db) {
            db = getWritableDatabase();
        }
        String tableName = MessageAppDBHelper.getMessageAppTableName(identifier);
        String indexName = "index_" + tableName + "_" + indexDBColumn;
        try {
            db.execSQL("create index if not exists " + indexName + " on " + tableName + "(" + indexDBColumn + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 分页查询
     * @param context
     * @param limit
     * @param offset
     * @return
     */
    public List<NewsSummaryPostMessage> queryTotleMessages(Context context,int limit,int offset,String orgId) {
        Cursor cursor ;
        List<NewsSummaryPostMessage> newsSummaryPostMessageList = new ArrayList<>();
        if(!TextUtils.isEmpty(orgId)) {
            String sql = "select * from " + MessageAppDBHelper.TABLE_NAME + " where org_id_ = ? and status_ not in ( " + ChatStatus.UnDo.intValue() + ", " + ChatStatus.Hide.intValue() + ") order by delivery_time_ desc " + " Limit " + limit + " Offset " + offset;
            cursor = getWritableDatabase().rawQuery(sql, new String[]{orgId});
        }else{
            String sql = "select * from " + MessageAppDBHelper.TABLE_NAME + " where status_ not in ( " + ChatStatus.UnDo.intValue() + ", " + ChatStatus.Hide.intValue() + ") order by delivery_time_ desc " +  " Limit " + limit + " Offset " + offset;
            cursor = getWritableDatabase().rawQuery(sql, null);
        }
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NewsSummaryPostMessage newsSummaryPostMessage = MessageAppDBHelper.fromCursor(context, cursor);
                    newsSummaryPostMessageList.add(newsSummaryPostMessage);
                }

            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }

        Collections.sort(newsSummaryPostMessageList, new Comparator<NewsSummaryPostMessage>() {
            @Override
            public int compare(NewsSummaryPostMessage chatPostMessage, NewsSummaryPostMessage t1) {
                return Long.compare(t1.getChatPostMessage().deliveryTime, chatPostMessage.getChatPostMessage().deliveryTime);
            }
        });

        return newsSummaryPostMessageList;
    }

    public List<NewsSummaryPostMessage> queryMessagesByMsgId(Context context,String msgId) {
        String sql = "";
        Cursor cursor ;
        List<NewsSummaryPostMessage> newsSummaryPostMessageList = new ArrayList<>();
        sql = "select * from " + MessageAppDBHelper.TABLE_NAME + " where msg_id_ = ? ";
        cursor = getWritableDatabase().rawQuery(sql, new String[]{msgId});
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NewsSummaryPostMessage newsSummaryPostMessage = MessageAppDBHelper.fromCursor(context, cursor);
                    newsSummaryPostMessageList.add(newsSummaryPostMessage);
                }
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }
        return newsSummaryPostMessageList;
    }

    public List<NewsSummaryPostMessage> search(Context context,String searchValue) {
        String sql = "";
        Cursor cursor ;
        List<NewsSummaryPostMessage> newsSummaryPostMessageList = new ArrayList<>();
        sql = "select * from " + MessageAppDBHelper.TABLE_NAME + " where searchable_text_ like ? and status_ not in ( " + ChatStatus.UnDo.intValue() + ", " + ChatStatus.Hide.intValue() + ")";
        cursor = getWritableDatabase().rawQuery(sql, new String[]{"%" + searchValue + "%"});
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NewsSummaryPostMessage newsSummaryPostMessage = MessageAppDBHelper.fromCursor(context, cursor);
                    newsSummaryPostMessageList.add(newsSummaryPostMessage);
                }
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }
        return newsSummaryPostMessageList;
    }

    /**
     * 查询最新一条消息
     * @param identifier
     * @return
     */
    public List<ChatPostMessage> queryLatestMessage(Context context, String identifier) {
        List<ChatPostMessage> messages = new ArrayList<>();
        String orderPart = " order by delivery_time_ desc limit 1 offset 0";
        String sql = "select * from " + MessageAppDBHelper.TABLE_NAME + " where status_ != " + ChatStatus.Hide.intValue();

        sql = tailMessagePullLatestTimeLimitPart(identifier, sql);

        sql += orderPart;

        if (tableExists(MessageAppDBHelper.TABLE_NAME)) {
            Cursor cursor = null;
            try {
                cursor = getReadableDatabase().rawQuery(sql, new String[]{});
                while (cursor.moveToNext()) {
                    NewsSummaryPostMessage message = MessageAppDBHelper.fromCursor(context, cursor);
                    if (message != null && message.getChatPostMessage() != null) {
                        messages.add(message.getChatPostMessage());
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

    public long queryFirstUnreadMsgTime(String identifier) {
        String loginUserId = LoginUserInfo.getInstance().getLoginUserId(BaseApplicationLike.baseContext);
        long firstUnreadMsgTime = -1;
        if (tableExists(MessageAppDBHelper.TABLE_NAME)) {

            String firstUnreadLimitSql = "select min(delivery_time_) from " + MessageAppDBHelper.TABLE_NAME + " where read_  = " + ReadStatus.Unread.intValue() + " and from_ != " + getInStringParams(loginUserId) + " and status_ not in (" + ChatStatus.UnDo.intValue() + ", " + ChatStatus.Hide.intValue()  + ")";

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

    public boolean updateMessage(ChatPostMessage message){
        List<NewsSummaryPostMessage> lists = queryMessagesByMsgId(BaseApplicationLike.baseContext,message.deliveryId);
        if(lists.size() > 0) {
            String whereClause = MessageAppDBHelper.DBColumn.MSG_ID + "=?";
            long result = getWritableDatabase().updateWithOnConflict(MessageAppDBHelper.TABLE_NAME,
                    MessageAppDBHelper.getContentValues(message, lists.get(0).getChatId(),lists.get(0).getOrgId()), whereClause, new String[]{message.deliveryId},
                    SQLiteDatabase.CONFLICT_REPLACE);

            return result != -1;
        }else {
            return false;
        }
    }

    /**
     * 插入单条数据
     * @param message
     * @return
     */
    public boolean insertOrUpdateMessageApp(NewsSummaryPostMessage message) {
        boolean result = true;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();
            getWritableDatabase().insertWithOnConflict(
                    MessageAppDBHelper.TABLE_NAME,
                    null,
                    MessageAppDBHelper.getContentValues(message.getChatPostMessage(), message.getChatId(),message.getOrgId()), SQLiteDatabase.CONFLICT_REPLACE);
            sqLiteDatabase.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            sqLiteDatabase.endTransaction();
        }
        return result;
    }

    /**
     * 批量插入服务号消息(总表)
     *
     * @param newsSummaryPostMessageList
     * @return
     */
    public boolean batchInsertTotleAppMessages(List<NewsSummaryPostMessage> newsSummaryPostMessageList) {
        boolean result = true;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();

            for (NewsSummaryPostMessage newsSummaryPostMessage : newsSummaryPostMessageList) {
                getWritableDatabase().insertWithOnConflict(
                        MessageAppDBHelper.TABLE_NAME,
                        null,
                        MessageAppDBHelper.getContentValues(newsSummaryPostMessage.getChatPostMessage(), newsSummaryPostMessage.getChatId(),newsSummaryPostMessage.getOrgId()), SQLiteDatabase.CONFLICT_REPLACE);
            }
            sqLiteDatabase.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            sqLiteDatabase.endTransaction();
        }
        return result;
    }

    public void undoMessage(Context context, String identifier, UndoEventMessage undoEventMessage) {
//        String messageTable = Message.getMessageTableName(identifier);
        List<NewsSummaryPostMessage> messageList = queryMessages(context, identifier, undoEventMessage.mEnvIds);
        for(NewsSummaryPostMessage message : messageList) {
            message.getChatPostMessage().setChatStatus(ChatStatus.UnDo);
            message.getChatPostMessage().undoSuccessTime = undoEventMessage.deliveryTime;
        }

        if(messageList.size() > 0) {
            batchInsertTotleAppMessages(messageList);
        }
//        String sql = "update " + messageTable + " set status_ = "
//                + ChatStatus.UnDo.intValue() + "  where msg_id_ in (" + getInStringParams(undoEventMessage.mEnvIds) + ")";
//
//        getWritableDatabase().execSQL(sql, new String[]{});
    }

    /**
     * 隐藏消息
     * */
    public void hideMessage(HideEventMessage hideEventMessage) {
        if (tableExists(MessageAppDBHelper.TABLE_NAME)) {
            String sql = "update " + MessageAppDBHelper.TABLE_NAME + " set status_ = "
                    + ChatStatus.Hide.intValue() + "  where msg_id_ in (" + getInStringParams(hideEventMessage.mEnvIds) + ")";
            getWritableDatabase().execSQL(sql, new String[]{});
        }
    }

    public List<NewsSummaryPostMessage> queryMessages(Context context, String identifier, List<String> msgIds) {
        List<NewsSummaryPostMessage> newsSummaryPostMessageList = new ArrayList<>();

        if (ListUtil.isEmpty(msgIds)) {
            return newsSummaryPostMessageList;
        }

        Cursor cursor ;
        String sql = "select * from " + MessageAppDBHelper.TABLE_NAME + " where msg_id_ in (" + getInStringParams(msgIds) + ")";
        cursor = getReadableDatabase().rawQuery(sql, new String[]{});
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NewsSummaryPostMessage newsSummaryPostMessage = MessageAppDBHelper.fromCursor(context, cursor);
                    newsSummaryPostMessageList.add(newsSummaryPostMessage);
                }

            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }

        return newsSummaryPostMessageList;
    }

    public boolean removeAllAppMessages(String identifier) {
        String tableName = MessageAppDBHelper.getMessageAppTableName(identifier);
        return removeAll(tableName);
    }

    public boolean removeAppMessages(String chatId) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        int deletedCount = sqLiteDatabase.delete(MessageAppDBHelper.TABLE_NAME , MessageAppDBHelper.DBColumn.CHAT_ID + "=?", new String[]{chatId});
        return 0 != deletedCount;
    }

    public boolean removeAppMessagesByMsgId(String msgId,String identifier) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        int deletedCount = sqLiteDatabase.delete(MessageAppDBHelper.TABLE_NAME, MessageAppDBHelper.DBColumn.MSG_ID + "=?", new String[]{msgId});
        return 0 != deletedCount;
    }
}
