package com.foreverht.db.service.repository;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.MessageRecordDBHelper;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.record.MessageRecord;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class MessageRecordRepository extends W6sBaseRepository {

    public static boolean updateMessageRecord(PostTypeMessage postTypeMessage) {
        return updateMessageRecord(MessageRecord.transfer(postTypeMessage));
    }

    public static boolean updateMessageRecord(MessageRecord messageRecord) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        boolean result = -1 != sqLiteDatabase.insertWithOnConflict(
                MessageRecordDBHelper.TABLE_NAME,
                null,
                MessageRecordDBHelper.getContentValues(messageRecord),
                SQLiteDatabase.CONFLICT_REPLACE);

        LogUtil.e("updateMessageRecord " + messageRecord.toString() + "  结果: " + result);

        return result;
    }

    @Nullable
    public static MessageRecord queryLatestMessageRecord() {
        String sql = "select * from " + MessageRecordDBHelper.TABLE_NAME + " order by " + MessageRecordDBHelper.DBColumn.DELIVERY_TIME + " desc limit 1 offset 0";
        MessageRecord messageRecord = null;
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(sql, new String[]{});
            while (cursor.moveToNext()) {
                messageRecord = MessageRecordDBHelper.fromCursor(cursor);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return messageRecord;
    }

    public static List<MessageRecord> queryMessageRecords() {
        return queryMessageRecords(-1);
    }

    public static List<MessageRecord> queryMessageRecords(int limit) {
        String sql = "select * from " + MessageRecordDBHelper.TABLE_NAME + " order by " + MessageRecordDBHelper.DBColumn.DELIVERY_TIME + " asc";

        if(0 < limit) {
            sql += " limit " + limit + " offset 0 ";
        }


        List<MessageRecord> messageRecords = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(sql, new String[]{});
            while (cursor.moveToNext()) {
                MessageRecord messageRecord = MessageRecordDBHelper.fromCursor(cursor);
                messageRecords.add(messageRecord);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return messageRecords;
    }

    public static void removeMessageRecords(List<String> msgIds) {
        String sql = "delete from " + MessageRecordDBHelper.TABLE_NAME + " where "
                + MessageRecordDBHelper.DBColumn.MSG_ID + " in (" + getInStringParams(msgIds) + ")";

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL(sql);

    }

    public static void removeMessageRecords(long deleteOnTime){
        String sql = "delete from " + MessageRecordDBHelper.TABLE_NAME + " where " + MessageRecordDBHelper.DBColumn.DELIVERY_TIME + " < " + deleteOnTime;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL(sql);
    }


    public static void removeMessageRecordsAll() {
        String sql = "delete from " + MessageRecordDBHelper.TABLE_NAME;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL(sql);
    }




}
