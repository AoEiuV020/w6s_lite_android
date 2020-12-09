package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;
import android.database.Cursor;

import com.foreveross.atwork.infrastructure.newmessage.ReceiptMessage;

import com.foreveross.db.SQLiteDatabase;

/**
 * Created by lingen on 15/4/3.
 * Description:
 */
public class ReceiptDBHelper implements DBHelper {

    private static final String TAG = ReceiptDBHelper.class.getSimpleName();

    public class DBColumn {

        /**
         * 主键自增ID
         */
        public static final String IDENTIFIER = "identifier_";

        /**
         * 消息ID
         */
        public static final String MSG_ID = "msg_id_";

        /**
         * 回执用户
         */
        public static final String RECEIPT_RECEIVE_FROM = "receipt_receive_from_";

        /**
         * 回执时间
         */
        public static final String RECEIPT_TIMESTAMP = "receipt_timestamp_";


        public static final String CREATE_TABLE_SQL = "create table %s (identifier_ integer primary key autoincrement," +
                "msg_id_ text, receipt_receive_from_ text, receipt_timestamp_ integer)";

    }

    public static ContentValues getContentValues(ReceiptMessage receiptMessage) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBColumn.IDENTIFIER, receiptMessage.identifier);
        contentValues.put(DBColumn.MSG_ID, receiptMessage.msgId);
        contentValues.put(DBColumn.RECEIPT_RECEIVE_FROM, receiptMessage.receiveFrom);
        contentValues.put(DBColumn.RECEIPT_TIMESTAMP, receiptMessage.timestamp);

        return contentValues;
    }

    public static ReceiptMessage fromCursor(Cursor cursor) {
        ReceiptMessage receiptMessage = new ReceiptMessage();
        int index = -1;

        if ((index = cursor.getColumnIndex(DBColumn.IDENTIFIER)) != -1) {
            receiptMessage.identifier = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.MSG_ID)) != -1) {
            receiptMessage.msgId = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.RECEIPT_RECEIVE_FROM)) != -1) {
            receiptMessage.receiveFrom = cursor.getString(index);
        }

        if ((index = cursor.getColumnIndex(DBColumn.RECEIPT_TIMESTAMP)) != -1) {
            receiptMessage.timestamp = cursor.getLong(index);
        }

        return receiptMessage;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //此表是动态创建
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
