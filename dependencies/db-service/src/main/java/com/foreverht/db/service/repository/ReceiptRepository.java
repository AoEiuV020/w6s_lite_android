package com.foreverht.db.service.repository;

import android.database.Cursor;
import androidx.annotation.Nullable;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.ReceiptDBHelper;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.discussion.DiscussionMember;
import com.foreveross.atwork.infrastructure.model.discussion.DiscussionReadUnRead;
import com.foreveross.atwork.infrastructure.newmessage.ReceiptMessage;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/5/14.
 * Description:
 */
public class ReceiptRepository extends W6sBaseRepository {

    private static ReceiptRepository receiptRepository = new ReceiptRepository();


    public static ReceiptRepository getInstance() {
        return receiptRepository;
    }


    public DiscussionReadUnRead queryDiscussionReadUnRead(final String me, final String sessionIdentifier, final String msgId) {
        DiscussionReadUnRead discussionReadUnRead = new DiscussionReadUnRead();

        //获取群人员信息
        Discussion discussion = DiscussionRepository.getInstance().queryDiscussionBasicInfo(sessionIdentifier);

        if (discussion == null) {
            return discussionReadUnRead;
        }
        //获取当前消息已收到的回执信息
        List<ReceiptMessage> receiptMessages = queryGroupReceiptMessages(sessionIdentifier, msgId, null);

        //统计已读人员
        for (ReceiptMessage receiptMessage : receiptMessages) {
            if (receiptMessage.receiveFrom.equals(me)) {
                continue;
            }
            discussionReadUnRead.addReadUser(receiptMessage);
        }

        //统计未读人员
        for (DiscussionMember discussionMember : discussion.mMemberList) {
            if (discussionMember.userId.equals(me)) {
                continue;
            }
            if (!discussionReadUnRead.isRead(discussionMember.userId)) {
                discussionReadUnRead.addUnReadUser(discussionMember.userId);
            }
        }

        return discussionReadUnRead;
    }


    public boolean isP2PMessageRead(final String sessionIdentifier, final String msgId) {
        String messageTable = ReceiptMessage.getMessageTableName(sessionIdentifier);
        createReceiptMessageIfNecessary(sessionIdentifier);

        String querySQL = "select count(*) as count from " + messageTable + " where msg_id_ = ?";

        int count = 0;
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(querySQL, new String[]{msgId});
            if (cursor.moveToNext()) {
                count = cursor.getInt(cursor.getColumnIndex("count"));

            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0 < count;
    }

    /**
     * 单聊中，查询一个消息是否已读
     *
     * @param sessionIdentifier
     * @param msgId
     * @return
     */
    public ReceiptMessage queryP2PReceiptMessage(final String sessionIdentifier, final String msgId) {
        String messageTable = ReceiptMessage.getMessageTableName(sessionIdentifier);
        createReceiptMessageIfNecessary(sessionIdentifier);

        String querySQL = "select * from " + messageTable + " where msg_id_ = ?";

        ReceiptMessage receiptMessage = null;
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(querySQL, new String[]{msgId});
            if (cursor.moveToNext()) {
                receiptMessage = ReceiptDBHelper.fromCursor(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return receiptMessage;
    }

    /**
     * 群聊中，查询一个消息是否已读
     *
     * @param sessionIdentifier
     * @param msgId
     * @param from
     * @return
     */
    public List<ReceiptMessage> queryGroupReceiptMessages(final String sessionIdentifier, final String msgId, @Nullable final String from) {
        String messageTable = ReceiptMessage.getMessageTableName(sessionIdentifier);
        createReceiptMessageIfNecessary(sessionIdentifier);
        String querySQL = "select * from " + messageTable + " where msg_id_ = ?";
        if(!StringUtils.isEmpty(from)) {
            querySQL += " and receipt_receive_from_ = '" + from + "'";
        }

        List<ReceiptMessage> receiptMessageList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(querySQL, new String[]{msgId});
            while (cursor.moveToNext()) {
                receiptMessageList.add(ReceiptDBHelper.fromCursor(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return receiptMessageList;
    }


    /**
     * 批量保存已读回执
     *
     * @param receiptMessageList
     * @return
     */
    public boolean batchInsertReceipt(List<ReceiptMessage> receiptMessageList) {

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        boolean result = true;

        try {
            sqLiteDatabase.beginTransaction();

            for (ReceiptMessage message : receiptMessageList) {
                String messageTable = ReceiptMessage.getMessageTableName(message.sessionIdentifier);
                createReceiptMessageIfNecessary(message.sessionIdentifier);
                getWritableDatabase().insertWithOnConflict(
                        messageTable,
                        null,
                        ReceiptDBHelper.getContentValues(message), SQLiteDatabase.CONFLICT_REPLACE);
            }
            sqLiteDatabase.setTransactionSuccessful();

        } catch (Exception e) {
            LogUtil.e("Result", e.getLocalizedMessage());
            e.printStackTrace();
            result = false;
        } finally {
            sqLiteDatabase.endTransaction();
        }
        return result;
    }

    protected void createReceiptMessageIfNecessary(String accountName) {
        try {
            if (!tableExists(ReceiptMessage.MESSAGE_TABLE_PRE + accountName)) {
                String createTableSql = String.format(ReceiptDBHelper.DBColumn.CREATE_TABLE_SQL, ReceiptMessage.getMessageTableName(accountName));
                getWritableDatabase().execSQL(createTableSql, new String[]{});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
