package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;
import android.database.Cursor;

import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.record.MessageRecord;
import com.foreveross.db.BaseDatabaseHelper;
import com.foreveross.db.SQLiteDatabase;

public class MessageRecordDBHelper implements DBHelper {

    private static final String TAG = MessageRecordDBHelper.class.getName();

    public static final String TABLE_NAME = "message_record_";


    /**
     * create table message_record_
     * ( msg_id_ text, delivery_time_ integer, body_type_ text,
     * primary key  ( msg_id_) )
     */
    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
            DBColumn.MSG_ID + TEXT + COMMA +
            DBColumn.DELIVERY_TIME + INTEGER + COMMA +
            DBColumn.BODY_TYPE + TEXT + COMMA +
            PRIMARY_KEY + LEFT_BRACKET +
            DBColumn.MSG_ID +
            RIGHT_BRACKET + RIGHT_BRACKET;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(350 > oldVersion) {
            BaseDatabaseHelper.createTable(db, SQL_EXEC);
            oldVersion = 350;
        }
    }

    public static ContentValues getContentValues(MessageRecord messageRecord) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.MSG_ID, messageRecord.msgId);
//        cv.put(DBColumn.BODY_TYPE, messageRecord.bodyType.stringValue());
        cv.put(DBColumn.DELIVERY_TIME, messageRecord.msgTime);


        return cv;
    }


    public static MessageRecord fromCursor(Cursor cursor) {
        int index = -1;
        MessageRecord messageRecord = new MessageRecord();
        if ((index = cursor.getColumnIndex(DBColumn.MSG_ID)) != -1) {
            messageRecord.msgId = cursor.getString(index);
        }
        if ((index = cursor.getColumnIndex(DBColumn.DELIVERY_TIME)) != -1) {
            messageRecord.msgTime = cursor.getLong(index);
        }
//        if ((index = cursor.getColumnIndex(DBColumn.BODY_TYPE)) != -1) {
//            messageRecord.bodyType = BodyType.toBodyType(cursor.getString(index));
//        }

        return messageRecord;
    }

    public class DBColumn {

        public static final String MSG_ID = "msg_id_";

        public static final String DELIVERY_TIME = "delivery_time_";

        public static final String BODY_TYPE = "body_type_";



    }
}
